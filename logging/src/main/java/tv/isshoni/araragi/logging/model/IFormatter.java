package tv.isshoni.araragi.logging.model;

import tv.isshoni.araragi.logging.model.level.ILevel;

import java.time.ZonedDateTime;

public interface IFormatter {

    String format(String message, IAraragiLogger logger, ILevel level, ZonedDateTime time);

}
