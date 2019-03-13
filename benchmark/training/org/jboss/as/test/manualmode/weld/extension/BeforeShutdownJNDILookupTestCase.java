package org.jboss.as.test.manualmode.weld.extension;


import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.jboss.arquillian.container.test.api.ContainerController;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * A test to ensure that the UserTransaction and TransactionSynchronizationRegistry can be retrieved via JNDI when
 * an extensions BeforeShutdown method is invoked.
 * <p/>
 * See WFLY-5232
 *
 * @author Ryan Emerson
 */
@RunWith(Arquillian.class)
@RunAsClient
public class BeforeShutdownJNDILookupTestCase {
    public static final String TEST_URL = ("target" + (File.separator)) + "results.txt";

    private static final String CONTAINER = "default-jbossas";

    private static final String DEPLOYMENT = "test.war";

    private static final Path TEST_PATH = Paths.get("", BeforeShutdownJNDILookupTestCase.TEST_URL);

    @ArquillianResource
    ContainerController controller;

    @Test
    public void testTransactionJNDILookupDuringShutdownEvent() throws Exception {
        controller.start(BeforeShutdownJNDILookupTestCase.CONTAINER);
        controller.kill(BeforeShutdownJNDILookupTestCase.CONTAINER);
        List<String> output = Files.readAllLines(BeforeShutdownJNDILookupTestCase.TEST_PATH);
        if (output.get(0).equals("Exception")) {
            String stacktrace = output.get(1).replaceAll(",", System.getProperty("line.separator"));
            String msg = "An exception was thrown by the deployment %s during shutdown.  The server stacktrace is shown below: %n%s";
            Assert.fail(String.format(msg, BeforeShutdownJNDILookupTestCase.DEPLOYMENT, stacktrace));
        }
        Assert.assertEquals("Contents of result.txt is not valid!", "UserTransaction", output.get(0));
    }
}

