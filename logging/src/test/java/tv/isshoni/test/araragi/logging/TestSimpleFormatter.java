package tv.isshoni.test.araragi.logging;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.araragi.logging.format.SimpleFormatter;
import tv.isshoni.araragi.logging.model.IAraragiLogger;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.ZonedDateTime;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({IAraragiLogger.class, AraragiLogger.class, ZonedDateTime.class, SimpleFormatter.class})
public class TestSimpleFormatter {

    private ZonedDateTime time;

    private IAraragiLogger logger;

    private ByteArrayOutputStream output;

    @Before
    public void setup() {
        this.logger = AraragiLogger.create("Test Logger");
        this.output = new ByteArrayOutputStream();
        this.time = ZonedDateTime.now();

        System.setOut(new PrintStream(this.output));
        System.setErr(new PrintStream(this.output));

        mockStatic(ZonedDateTime.class);
        when(ZonedDateTime.now()).thenReturn(this.time);

        assertEquals(ZonedDateTime.now(), this.time);
    }

    @Test
    public void testMessageFormatting() {
        this.logger.info("Good ${time} ${name}, how was your ${action}?", new HashMap<>() {{
            put("name", () -> "Johnathan");
            put("time", () -> "morning");
            put("action", () -> "gambling");
        }});

        assertEquals("[" + SimpleFormatter.DATE_FORMATTER.format(this.time) + "]: Test Logger INFO -] Good morning Johnathan, how was your gambling?", this.output.toString().trim());
    }

    @Test
    public void testAdvancedMessageFormatting() {
        this.logger.info("Good afternoon ${pronoun}.", new HashMap<>() {{
            put("formal_pronoun", () -> "Sir");
            put("pronoun", () -> "${formal_pronoun}");
        }});

        assertEquals("[" + SimpleFormatter.DATE_FORMATTER.format(this.time) + "]: Test Logger INFO -] Good afternoon Sir.", this.output.toString().trim());
    }

    @Test
    public void testFunctionMessageFormatting() {
        this.logger.info("Testing: ${dashes%50}");

        assertEquals("[" + SimpleFormatter.DATE_FORMATTER.format(this.time) + "]: Test Logger INFO -] Testing: --------------------------------------------------", this.output.toString().trim());
    }
}
