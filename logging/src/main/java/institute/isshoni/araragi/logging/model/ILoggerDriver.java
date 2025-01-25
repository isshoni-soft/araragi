package institute.isshoni.araragi.logging.model;

import institute.isshoni.araragi.logging.model.format.message.IMessageContext;

public interface ILoggerDriver {

    void process(IMessageContext context);
}
