package tv.isshoni.araragi.logging.model;

import tv.isshoni.araragi.logging.model.level.ILevel;

public interface ILoggerDriver {

    void process(String message, ILevel level);
}
