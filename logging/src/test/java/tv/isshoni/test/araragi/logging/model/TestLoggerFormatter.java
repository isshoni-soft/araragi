package tv.isshoni.test.araragi.logging.model;

import tv.isshoni.araragi.logging.format.SimpleLoggerFormatter;
import tv.isshoni.araragi.logging.model.format.message.IMessageContext;

public class TestLoggerFormatter extends SimpleLoggerFormatter {

    @Override
    public void format(IMessageContext context) {
        super.format(context);

        context.setPrefix((lg, lv, t) -> "[" + lv.getName() + "]: " + lg.getName() + " - ");
    }
}
