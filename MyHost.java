import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

public class MyHost extends Host {

    private final PriorityQueue<Task> taskQueue = new PriorityQueue<>(
            Comparator.comparing(Task::getPriority).reversed()
                    .thenComparingInt(Task::getStart)
    );
    private volatile boolean running = true;
    private final Object lock = new Object();
    private Task currentTask = null;
    private long currentTaskStartTime;
    private Dispatcher dispatcher;

    @Override
    public void addTask(Task task) {
        synchronized (lock) {
            taskQueue.add(task);
            lock.notify();
        }
    }

    @Override
    public void run() {
        try {
            while (running) {
                synchronized (lock) {
                    // extrage taskul venit de la dispatcher din coada
                    if (currentTask == null && !taskQueue.isEmpty()) {
                        currentTask = taskQueue.poll();
                        currentTaskStartTime = System.currentTimeMillis();
                    }
                }

                if (currentTask != null && !handlePreemptionCheck()) {
                    // daca intervine preemptarea, pune taskul curent din nou in coada
                    long elapsedTime = System.currentTimeMillis() - currentTaskStartTime;
                    long remainingTime = currentTask.getDuration() - elapsedTime;
                    currentTask.setLeft(remainingTime);
                    taskQueue.add(currentTask);
                    synchronized (lock) {
                        currentTask = null;
                    }
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /*  se verifica constant daca exista un task cu prioritate mai
         mare gata sa preempteze taskul curent */
    private boolean handlePreemptionCheck() throws InterruptedException {

        long lastCheckTime = System.currentTimeMillis();

        while (currentTask.getLeft() > 0) {
            
            // creaza o pauza scurta in timpul executiei unui task
            Thread.sleep(100);
            
            long currentTime = System.currentTimeMillis();
            long timeElapsed = currentTime - lastCheckTime;
            lastCheckTime = currentTime;

            currentTask.setLeft(Math.max(0, currentTask.getLeft() - timeElapsed));
            synchronized (lock) {
                // se opreste executia taskului si este repus in coada
                if (!taskQueue.isEmpty() && taskQueue.peek().getPriority() > currentTask.getPriority() &&
                        currentTask.isPreemptible()) {
                    return false;
                }

            }

        }
        // termin executia taskului curent
            currentTask.finish();
            synchronized (lock) {
                currentTask = null;
                if (dispatcher != null) {
                    ((MyDispatcher) dispatcher).notifyTaskCompletion();
                }
            }

        return true;
    }


    @Override
    public int getQueueSize() {
        synchronized (lock) {
            return taskQueue.size();
        }
    }

    @Override
    public long getWorkLeft() {
        synchronized (lock) {
            long totalWorkLeft = 0;
            if (currentTask != null) {
                // adauga timpul ramas pentru taskul curent
                totalWorkLeft += currentTask.getLeft();
            }

            // timpul pentru toate taskurile din coada
            for (Task task : taskQueue) {
                totalWorkLeft += task.getDuration();
            }

            return totalWorkLeft;
        }
    }


    @Override
    public void shutdown() {
        running = false;
        synchronized (lock) {
            lock.notifyAll();
        }
    }
}
