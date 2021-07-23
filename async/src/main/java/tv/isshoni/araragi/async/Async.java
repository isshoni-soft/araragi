package tv.isshoni.araragi.async;

import tv.isshoni.araragi.logging.AraragiLogger;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Async {

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();

    private static final AraragiLogger LOGGER = AraragiLogger.create("Asynchronous");

    // TODO: Move this to allow customization of executor services, maybe add the ability to create one instead
    // TODO: Of just using the winry use-case.
    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOGGER.info("Waiting on async executor...");
            LOGGER.info("If it is apparent that a thread is deadlocked, please force kill...");

            EXECUTOR_SERVICE.shutdown();
            try {
                EXECUTOR_SERVICE.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));
    }

    public static <T> Future<T> submit(Callable<T> callable) {
        return EXECUTOR_SERVICE.submit(callable);
    }

    public static Future<?> submit(Runnable runnable) {
        return EXECUTOR_SERVICE.submit(runnable);
    }
}
