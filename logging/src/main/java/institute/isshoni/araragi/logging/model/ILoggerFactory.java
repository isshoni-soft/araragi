package institute.isshoni.araragi.logging.model;

import institute.isshoni.araragi.logging.AraragiLogger;
import institute.isshoni.araragi.logging.model.level.ILevel;

public interface ILoggerFactory {

    void setDefaultLoggerLevel(ILevel level);

    AraragiLogger createLogger(String name);

    AraragiLogger createLogger(Class<?> clazz);

    AraragiLogger createLogger(String name, ILevel level);

    ILevel getLevel();
}
