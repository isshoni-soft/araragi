package tv.isshoni.araragi.logging.driver;

import tv.isshoni.araragi.logging.model.IAraragiLogger;
import tv.isshoni.araragi.logging.model.ILevel;
import tv.isshoni.araragi.logging.model.ILoggerDriver;

import java.time.ZonedDateTime;

public class ConsoleDriver implements ILoggerDriver {

    @Override
    public void process(String message, IAraragiLogger logger, ILevel level, ZonedDateTime now) {
        System.out.println();
    }
}
