package tv.isshoni.araragi.logging.model;

import java.time.ZonedDateTime;

public interface ILoggerDriver {

    void process(String message, IAraragiLogger logger, ILevel level, ZonedDateTime now);
}
