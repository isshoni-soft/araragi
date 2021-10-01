package tv.isshoni.araragi.runtime;

public class AraragiRuntime {

    public static void registerShutdownHook(Runnable runnable) {
        Runtime.getRuntime().addShutdownHook(new Thread(runnable));
    }
}
