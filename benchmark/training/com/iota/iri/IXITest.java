package com.iota.iri;


import com.iota.iri.service.dto.AbstractResponse;
import com.iota.iri.service.dto.ErrorResponse;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Unit tests for {@link IXI}
 */
public class IXITest {
    private static TemporaryFolder ixiDir = new TemporaryFolder();

    private static IXI ixi;

    /**
     * If an command matches the command pattern, but is not valid, expect an unknown command error message.
     */
    @Test
    public void processCommandError() {
        AbstractResponse response = IXITest.ixi.processCommand("testCommand.testSuffix", null);
        Assert.assertThat("Wrong type of response", response, CoreMatchers.instanceOf(ErrorResponse.class));
        Assert.assertTrue("Wrong error message returned in response", response.toString().contains("Command [testCommand.testSuffix] is unknown"));
    }

    /**
     * If null is given as a command, expect a parameter check error message.
     */
    @Test
    public void processCommandNull() {
        AbstractResponse response = IXITest.ixi.processCommand(null, null);
        Assert.assertThat("Wrong type of response", response, CoreMatchers.instanceOf(ErrorResponse.class));
        Assert.assertTrue("Wrong error message returned in response", response.toString().contains("Command can not be null or empty"));
    }

    /**
     * If an empty string is given as a command, expect a parameter check error message.
     */
    @Test
    public void processCommandEmpty() {
        AbstractResponse response = IXITest.ixi.processCommand("", null);
        Assert.assertThat("Wrong type of response", response, CoreMatchers.instanceOf(ErrorResponse.class));
        Assert.assertTrue("Wrong error message returned in response", response.toString().contains("Command can not be null or empty"));
    }

    /**
     * If the given command does not exist, expect an unknown command error message.
     */
    @Test
    public void processCommandUnknown() {
        AbstractResponse response = IXITest.ixi.processCommand("unknown", null);
        Assert.assertThat("Wrong type of response", response, CoreMatchers.instanceOf(ErrorResponse.class));
        Assert.assertTrue("Wrong error message returned in response", response.toString().contains("Command [unknown] is unknown"));
    }

    /**
     * If an IXI module does not have the given command, expect an unknown command error message.
     */
    @Test
    public void processIXICommandUnknown() {
        AbstractResponse response = IXITest.ixi.processCommand("IXI.unknown", null);
        Assert.assertThat("Wrong type of response", response, CoreMatchers.instanceOf(ErrorResponse.class));
        Assert.assertTrue("Wrong error message returned in response", response.toString().contains("Command [IXI.unknown] is unknown"));
    }
}

