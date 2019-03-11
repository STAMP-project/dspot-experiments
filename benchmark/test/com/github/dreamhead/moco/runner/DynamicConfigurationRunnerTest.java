package com.github.dreamhead.moco.runner;


import java.io.File;
import java.io.IOException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class DynamicConfigurationRunnerTest extends AbstractRunnerTest {
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void should_reload_configuration() throws IOException, InterruptedException {
        final File config = tempFolder.newFile();
        changeFileContent(config, ("[{\"response\" :{" + ("\"text\" : \"foo\"" + "}}]")));
        RunnerFactory factory = new RunnerFactory("SHUTDOWN");
        runner = factory.createRunner(httpArgs().withPort(port()).withShutdownPort(9090).withConfigurationFile(config.getAbsolutePath()).build());
        runner.run();
        Assert.assertThat(helper.get(root()), CoreMatchers.is("foo"));
        changeFileContent(config, ("[{\"response\" :{" + ("\"text\" : \"foobar\"" + "}}]")));
        waitChangeHappens();
        Assert.assertThat(helper.get(root()), CoreMatchers.is("foobar"));
        runner.stop();
    }
}

