///* Implement this class. *///
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class MyDispatcher extends Dispatcher {
    private int nextHostIndex = 0;
    private final AtomicInteger tasksProcessed = new AtomicInteger(0);
    private final PriorityQueue<Task> waitingTasks = new PriorityQueue<>(
            Comparator.comparingInt(Task::getStart).thenComparingInt(Task::getPriority)
    );

    public MyDispatcher(SchedulingAlgorithm algorithm, List<Host> hosts) {
        super(algorithm, hosts);
    }

    @Override
    public synchronized void addTask(Task task) {
        waitingTasks.add(task);

        dispatchTasks();
    }

    private void dispatchTasks() {
        long currentTime = System.currentTimeMillis();
        // selectez algoritmul aplicat de catre Dispatcher
        while (!waitingTasks.isEmpty() && waitingTasks.peek().getStart() * 1000L <= currentTime) {
            Task task = waitingTasks.poll();
            int hostIndex;

            if (algorithm == SchedulingAlgorithm.LEAST_WORK_LEFT) {
                hostIndex = getLeastWorkLeftHost();
                hosts.get(hostIndex).addTask(task);
            } else if (algorithm == SchedulingAlgorithm.SIZE_INTERVAL_TASK_ASSIGNMENT) {
                assert task != null;
                hostIndex = getHostIndexForSITA(task);
                hosts.get(hostIndex).addTask(task);
            } else if (algorithm == SchedulingAlgorithm.ROUND_ROBIN) {
                hostIndex = getNextHostRR();
                hosts.get(hostIndex).addTask(task);
            } else if (algorithm == SchedulingAlgorithm.SHORTEST_QUEUE) {
                hostIndex = getShortestQueueHost();
                hosts.get(hostIndex).addTask(task);
            }

        }
    }
    private int getNextHostRR() {
        int index = nextHostIndex;
        nextHostIndex = (nextHostIndex + 1) % hosts.size();
        return index;
    }

    private int getShortestQueueHost() {
        int minIndex = 0;
        int minSize = Integer.MAX_VALUE;
        for (int i = 0; i < hosts.size(); i++) {
            int size = hosts.get(i).getQueueSize() + (hosts.get(i).getWorkLeft() > 0 ? 1 : 0);
            if (size < minSize) {
                minSize = size;
                minIndex = i;
            }
        }
        return minIndex;
    }

    private int getHostIndexForSITA(Task task) {
        // asignez in functie de tip
        return switch (task.getType()) {
            case SHORT -> 0; // nod task scurt
            case MEDIUM -> 1; // nod taskuri medii
            case LONG -> 2; // nod taskuri lungi
            default -> 0; // default
        };
    }

    private int getLeastWorkLeftHost() {
        int minIndex = 0;
        long minWorkLeft = Long.MAX_VALUE;
        for (int i = 0; i < hosts.size(); i++) {
            long workLeft = hosts.get(i).getWorkLeft();
            if (workLeft < minWorkLeft) {
                minWorkLeft = workLeft;
                minIndex = i;
            // daca avem aceeasi cantitate de "munca", alege nodul cu id ul cel mai mic
            } else if (workLeft == minWorkLeft && i < minIndex) {
                
                minIndex = i;
            }
        }
        return minIndex;
    }

    public synchronized void notifyTaskCompletion() {
        tasksProcessed.incrementAndGet();
        dispatchTasks();
    }
}
