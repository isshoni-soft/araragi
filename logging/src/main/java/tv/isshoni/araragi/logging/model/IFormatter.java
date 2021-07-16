package tv.isshoni.araragi.logging.model;

import java.time.ZonedDateTime;

public interface IFormatter {

    String format(String message, IAraragiLogger logger, ILevel level, ZonedDateTime time);

}
