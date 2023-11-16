package tv.isshoni.test.araragi.logging;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.araragi.logging.format.StringFormatterLoggerFormatter;
import tv.isshoni.araragi.logging.model.IAraragiLogger;
import tv.isshoni.araragi.logging.model.level.Level;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.ZonedDateTime;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({IAraragiLogger.class, AraragiLogger.class, ZonedDateTime.class, StringFormatterLoggerFormatter.class})
public class TestAraragiLogger {

    private ZonedDateTime time;

    private IAraragiLogger logger;

    private ByteArrayOutputStream output;

    @Before
    public void setup() {
        this.logger = AraragiLogger.create("Test Logger");
        this.output = new ByteArrayOutputStream();
        this.time = ZonedDateTime.now();

        StringFormatterLoggerFormatter loggerFormatter = new StringFormatterLoggerFormatter();
        loggerFormatter.setPrefixFormat("[${al:level}]: ${al:name} - ");

        this.logger.setFormatter(loggerFormatter);

        System.setOut(new PrintStream(this.output));
        System.setErr(new PrintStream(this.output));

        mockStatic(ZonedDateTime.class);
        when(ZonedDateTime.now()).thenReturn(this.time);

        assertEquals(ZonedDateTime.now(), this.time);
    }

    @Test
    public void testInfoLogging() {
        this.logger.info("print!");
        this.logger.debug("THIS IS A TEST");
        this.logger.info("asd");

        assertEquals("[INFO]: Test Logger - print!\n[INFO]: Test Logger - asd", this.output.toString().trim());
    }

    @Test
    public void testWarnLogging() {
        this.logger.setLevel(Level.WARNING);
        this.logger.info("print!");
        this.logger.warn("THIS IS A TEST");
        this.logger.info("asd");

        assertEquals("[WARNING]: Test Logger - THIS IS A TEST", this.output.toString().trim());
    }

    @Test
    public void testErrorLogging() {
        this.logger.setLevel(Level.ERROR);
        this.logger.info("print!");
        this.logger.error("blah");

        assertEquals("[ERROR]: Test Logger - blah", this.output.toString().trim());
    }

    @Test
    public void testSingleCharacterToken() {
        char c = '$';
        this.logger.info("test ${0}", c);
    }
}
