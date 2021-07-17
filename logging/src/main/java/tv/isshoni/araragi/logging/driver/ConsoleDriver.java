package tv.isshoni.araragi.logging.driver;

import tv.isshoni.araragi.logging.model.ILoggerDriver;

public class ConsoleDriver implements ILoggerDriver {

    @Override
    public void process(String message) {
        System.out.println(message);
    }
}
