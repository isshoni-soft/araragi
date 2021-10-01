package tv.isshoni.test.araragi.logging.model;

import tv.isshoni.araragi.logging.format.SimpleFormatter;
import tv.isshoni.araragi.logging.model.format.message.IMessageContext;

public class TestFormatter extends SimpleFormatter {

    @Override
    public void formatMessage(IMessageContext context) {
        context.setMessage((m, lg, lv, t) -> "[" + lv.getName() + "]: " + lg.getName() + " - " + m);
    }
}
