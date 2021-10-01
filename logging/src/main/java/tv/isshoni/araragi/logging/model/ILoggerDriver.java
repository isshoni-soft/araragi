package tv.isshoni.araragi.logging.model;

import tv.isshoni.araragi.logging.model.format.message.IMessageContext;

public interface ILoggerDriver {

    void process(IMessageContext context);
}
