package tv.isshoni.araragi.logging.driver;

import tv.isshoni.araragi.logging.model.ILoggerDriver;
import tv.isshoni.araragi.logging.model.level.Level;
import tv.isshoni.araragi.logging.model.format.message.IMessageContext;

public class ConsoleDriver implements ILoggerDriver {

    @Override
    public void process(IMessageContext context) {
        if (context.getLevel().equals(Level.ERROR)) {
            System.err.println(context.getMessage());
            System.err.flush();
        } else {
            System.out.println(context.getMessage());
            System.out.flush();
        }
    }
}
