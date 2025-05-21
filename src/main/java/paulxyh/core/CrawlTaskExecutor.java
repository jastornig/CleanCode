package paulxyh.core;

import paulxyh.util.logger.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class CrawlTaskExecutor {
    private ExecutorService executorService;
    private AtomicInteger runningTasks = new AtomicInteger(0);

    public CrawlTaskExecutor(int numThreads){
        this.executorService = Executors.newFixedThreadPool(numThreads);
    }

    public void submitTask(Runnable task) {
        runningTasks.incrementAndGet();
        executorService.execute(() -> {
            try {
                task.run();
            } finally {
                runningTasks.decrementAndGet();
                synchronized (this) {
                    notifyAll();
                }
            }
        });
    }

    public void waitForAllTasksToFinish(){
        synchronized (this) {
            while (runningTasks.get() > 0) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    Logger.error("Task interrupted: " + e.getMessage());
                    break;
                }
            }
        }
    }

    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, java.util.concurrent.TimeUnit.SECONDS)) {
                executorService.shutdownNow();
                Logger.error("Executor did not terminate");
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }finally {
            Logger.info("CrawlTaskExecutor shut down");
        }
    }
}
