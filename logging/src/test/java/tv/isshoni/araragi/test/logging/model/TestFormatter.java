package tv.isshoni.araragi.test.logging.model;

import tv.isshoni.araragi.logging.model.IAraragiLogger;
import tv.isshoni.araragi.logging.model.IFormatter;
import tv.isshoni.araragi.logging.model.level.ILevel;

import java.time.ZonedDateTime;

public class TestFormatter implements IFormatter {

    @Override
    public String format(String message, IAraragiLogger logger, ILevel level, ZonedDateTime time) {
        return "[" + level.getName() + "]: " + logger.getName() + " - " + message;
    }
}
