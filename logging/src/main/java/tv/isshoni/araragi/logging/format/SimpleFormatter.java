package tv.isshoni.araragi.logging.format;

import tv.isshoni.araragi.logging.model.IAraragiLogger;
import tv.isshoni.araragi.logging.model.IFormatter;
import tv.isshoni.araragi.logging.model.ILevel;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class SimpleFormatter implements IFormatter {

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd|HH:mm:ss.SS")
            .withLocale(Locale.US)
            .withZone(ZoneId.systemDefault());

    @Override
    public String format(String message, IAraragiLogger logger, ILevel level, ZonedDateTime time) {
        return "[" + DATE_FORMATTER.format(time) + "]: " + logger.getName() + " " + level.getName() + " - " + message;
    }
}
