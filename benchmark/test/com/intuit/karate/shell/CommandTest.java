package com.intuit.karate.shell;


import com.intuit.karate.FileUtils;
import java.io.File;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author pthomas3
 */
public class CommandTest {
    @Test
    public void testCommand() {
        String cmd = (FileUtils.isWindows()) ? "print \"hello\"" : "ls";
        CommandThread command = new CommandThread(null, null, "target/command.log", new File("src"), cmd, "-al");
        command.start();
        int exitCode = command.waitSync();
        Assert.assertEquals(exitCode, 0);
    }
}

