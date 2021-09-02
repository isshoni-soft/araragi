package tv.isshoni.araragi.logging.driver;

import tv.isshoni.araragi.logging.model.ILoggerDriver;
import tv.isshoni.araragi.logging.model.level.ILevel;
import tv.isshoni.araragi.logging.model.level.Level;

public class ConsoleDriver implements ILoggerDriver {

    @Override
    public void process(String message, ILevel level) {
        if (level.equals(Level.ERROR)) {
            System.err.println(message);
            System.err.flush();
        } else {
            System.out.println(message);
            System.out.flush();
        }
    }
}
