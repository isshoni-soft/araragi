package institute.isshoni.test.araragi.logging;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import institute.isshoni.araragi.logging.AraragiLogger;
import institute.isshoni.araragi.logging.format.StringFormatterLoggerFormatter;
import institute.isshoni.araragi.logging.model.IAraragiLogger;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({IAraragiLogger.class, AraragiLogger.class, ZonedDateTime.class, Instant.class, StringFormatterLoggerFormatter.class})
public class TestStringFormatterLoggerFormatter {

    private ZonedDateTime time;

    private Instant instant;

    private IAraragiLogger logger;

    private ByteArrayOutputStream output;

    // this abomination forces ZonedDateTime.now() and Instant.now() to return fixed values
    // allows for "now" calls to be strictly tested.
    @Before
    public void setup() {
        this.logger = AraragiLogger.create("Test Logger");
        this.output = new ByteArrayOutputStream();
        this.time = ZonedDateTime.now();

        // intentionally stagger times so that an instant != a ZonedDateTime when comparing.
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        this.instant = Instant.now();

        System.setOut(new PrintStream(this.output));
        System.setErr(new PrintStream(this.output));

        mockStatic(ZonedDateTime.class);
        when(ZonedDateTime.now()).thenReturn(this.time);

        mockStatic(Instant.class);
        when(Instant.now()).thenReturn(this.instant);

        assertEquals(ZonedDateTime.now(), this.time);
        assertEquals(Instant.now(), this.instant);
    }

    @Test
    public void testMessageFormatting() {
        this.logger.info("Good ${time} ${name}, how was your ${action}?", new HashMap<>() {{
            put("name", () -> "Johnathan");
            put("time", () -> "morning");
            put("action", () -> "gambling");
        }});

        assertEquals("[" + StringFormatterLoggerFormatter.DATE_FORMATTER.format(this.time) + "]: Test Logger INFO -] Good morning Johnathan, how was your gambling?", this.output.toString().trim());
    }

    @Test
    public void testAdvancedMessageFormatting() {
        this.logger.info("Good afternoon ${pronoun}.", new HashMap<>() {{
            put("formal_pronoun", () -> "Sir");
            put("pronoun", () -> "${formal_pronoun}");
        }});

        assertEquals("[" + StringFormatterLoggerFormatter.DATE_FORMATTER.format(this.time) + "]: Test Logger INFO -] Good afternoon Sir.", this.output.toString().trim());
    }

    @Test
    public void testFunctionMessageFormatting() {
        this.logger.info("Testing: ${a:dashes%50}");

        assertEquals("[" + StringFormatterLoggerFormatter.DATE_FORMATTER.format(this.time) + "]: Test Logger INFO -] Testing: --------------------------------------------------", this.output.toString().trim());
    }

    @Test
    public void testNowSupplier() {
        this.logger.info("Testing: ${a:now}");

        assertEquals("[" + StringFormatterLoggerFormatter.DATE_FORMATTER.format(this.time) + "]: Test Logger INFO -] Testing: " + StringFormatterLoggerFormatter.DATE_FORMATTER.format(this.instant), this.output.toString().trim());
    }

    @Test
    public void testThreadSupplier() {
        this.logger.info("Thread: ${a:thread}");

        assertEquals("[" + StringFormatterLoggerFormatter.DATE_FORMATTER.format(this.time) + "]: Test Logger INFO -] Thread: " + Thread.currentThread().getName(), this.output.toString().trim());
    }

    @Test
    public void testMethodSupplier() {
        this.logger.info("Method: ${a:method}");
        // expected: "Method: testMethodSupplier"

        assertEquals("[" + StringFormatterLoggerFormatter.DATE_FORMATTER.format(this.time) + "]: Test Logger INFO -] Method: testMethodSupplier", this.output.toString().trim());
    }
}
