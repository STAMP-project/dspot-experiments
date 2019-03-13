package org.baeldung.shell.simple;


import java.io.File;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.shell.core.CommandResult;
import org.springframework.shell.core.JLineShellComponent;


public class SimpleCLIIntegrationTest {
    static JLineShellComponent shell;

    @Test
    public void givenCommandConfig_whenExecutingWebGetCommand_thenCorrectResult() {
        final CommandResult resultWebSave = SimpleCLIIntegrationTest.shell.executeCommand("web-get --url https://www.google.com");
        Assert.assertTrue(resultWebSave.isSuccess());
    }

    @Test
    public void givenCommandConfig_whenExecutingWebSaveCommand_thenCorrectResult() {
        SimpleCLIIntegrationTest.shell.executeCommand("admin-enable");
        final CommandResult result = SimpleCLIIntegrationTest.shell.executeCommand("web-save --url https://www.google.com --out contents.txt");
        Assert.assertArrayEquals(new boolean[]{ result.isSuccess(), new File("contents.txt").exists() }, new boolean[]{ true, true });
    }

    @Test
    public void givenCommandConfig_whenAdminEnableCommandExecuted_thenCorrectAvailability() {
        final CommandResult resultAdminDisable = SimpleCLIIntegrationTest.shell.executeCommand("admin-disable");
        final CommandResult resultWebSaveUnavailable = SimpleCLIIntegrationTest.shell.executeCommand("web-save --url https://www.google.com --out contents.txt");
        final CommandResult resultAdminEnable = SimpleCLIIntegrationTest.shell.executeCommand("admin-enable");
        final CommandResult resultWebSaveAvailable = SimpleCLIIntegrationTest.shell.executeCommand("web-save --url https://www.google.com --out contents.txt");
        Assert.assertArrayEquals(new boolean[]{ resultAdminDisable.isSuccess(), resultWebSaveUnavailable.isSuccess(), resultAdminEnable.isSuccess(), resultWebSaveAvailable.isSuccess() }, new boolean[]{ true, false, true, true });
    }

    @Test
    public void givenCommandConfig_whenWebSaveCommandExecutedNoOutArgument_thenError() {
        SimpleCLIIntegrationTest.shell.executeCommand("admin-enable");
        final CommandResult resultWebSave = SimpleCLIIntegrationTest.shell.executeCommand("web-save --url https://www.google.com");
        Assert.assertEquals(resultWebSave.isSuccess(), false);
    }

    @Test
    public void givenCommandConfig_whenExecutingWebGetCommandWithDefaultArgument_thenCorrectResult() {
        final CommandResult result = SimpleCLIIntegrationTest.shell.executeCommand("web-get https://www.google.com");
        Assert.assertEquals(result.isSuccess(), true);
    }
}

