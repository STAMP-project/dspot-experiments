/**
 * import org.apache.commons.io.FileUtils;
 */
/**
 * import org.apache.commons.lang3.RandomStringUtils;
 */
package ch.qos.logback.classic.issue.logback1159;


import ch.qos.logback.core.joran.spi.JoranException;
import java.io.File;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LogbackListenerTest {
    private File logFile = new File("target/test.log");

    @Test(expected = LoggingError.class)
    public void testThatErrorIsDetectedAtLogInit() throws Exception {
        disableLogFileAccess();
        doConfigure();
    }

    @Test
    public void assertThatNonFailSafeAppendersNotAffected() throws JoranException {
        doConfigure();
        Logger logger = LoggerFactory.getLogger("NOTJOURNAL");
        logger.error("This should not fail");
    }
}

