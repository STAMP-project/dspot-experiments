package com.github.dreamhead.moco.runner;


import com.github.dreamhead.moco.util.Files;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class DynamicSettingRunnerTest extends AbstractRunnerTest {
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void should_load_configuration() throws IOException, InterruptedException {
        final File config = tempFolder.newFile("config.json");
        changeFileContent(config, ("[{\"response\" :{" + ("\"text\" : \"foo\"" + "}}]")));
        RunnerFactory factory = new RunnerFactory("SHUTDOWN");
        runner = factory.createRunner(httpArgs().withPort(port()).withShutdownPort(9090).withConfigurationFile(config.getAbsolutePath()).build());
        runner.run();
        Assert.assertThat(helper.get(root()), CoreMatchers.is("foo"));
    }

    @Test
    public void should_load_glob_configuration() throws IOException, InterruptedException {
        final File config = tempFolder.newFile("config.json");
        changeFileContent(config, ("[{\"response\" :{" + ("\"text\" : \"foo\"" + "}}]")));
        RunnerFactory factory = new RunnerFactory("SHUTDOWN");
        String absolutePath = config.getParent();
        String result = Files.join(absolutePath, "*.json");
        runner = factory.createRunner(httpArgs().withPort(port()).withShutdownPort(9090).withConfigurationFile(result).build());
        runner.run();
        Assert.assertThat(helper.get(root()), CoreMatchers.is("foo"));
    }

    @Test
    public void should_reload_configuration() throws IOException, InterruptedException {
        final File config = tempFolder.newFile("config.json");
        changeFileContent(config, ("[{\"response\" :{" + ("\"text\" : \"foo\"" + "}}]")));
        final File setting = tempFolder.newFile("settings.json");
        String path = config.getAbsolutePath();
        changeFileContent(setting, (((("[{" + "\"include\" : \"") + path) + "\"") + "}]"));
        RunnerFactory factory = new RunnerFactory("SHUTDOWN");
        runner = factory.createRunner(httpArgs().withPort(port()).withShutdownPort(9090).withSettings(setting.getAbsolutePath()).build());
        runner.run();
        Assert.assertThat(helper.get(root()), CoreMatchers.is("foo"));
        changeFileContent(config, ("[{\"response\" :{" + ("\"text\" : \"foobar\"" + "}}]")));
        waitChangeHappens();
        Assert.assertThat(helper.get(root()), CoreMatchers.is("foobar"));
    }

    @Test
    public void should_reload_configuration_with_multiple_modification() throws IOException, InterruptedException {
        final File config1 = tempFolder.newFile("config1.json");
        changeFileContent(config1, ("[{" + (((((("        \"request\": {" + "            \"uri\": \"/foo\"") + "        },") + "        \"response\": {") + "            \"text\": \"foo\"") + "        }") + "}]")));
        final File config2 = tempFolder.newFile("config2.json");
        changeFileContent(config2, ("[{" + (((((("        \"request\": {" + "            \"uri\": \"/bar\"") + "        },") + "        \"response\": {") + "            \"text\": \"bar\"") + "        }") + "}]")));
        final File setting = tempFolder.newFile("settings.json");
        changeFileContent(setting, ((((((("[" + "{\"include\" : \"") + (config1.getAbsolutePath())) + "\"},") + "{\"include\" : \"") + (config2.getAbsolutePath())) + "\"}") + "]"));
        RunnerFactory factory = new RunnerFactory("SHUTDOWN");
        runner = factory.createRunner(httpArgs().withPort(port()).withShutdownPort(9090).withSettings(setting.getAbsolutePath()).build());
        runner.run();
        Assert.assertThat(helper.get(remoteUrl("/foo")), CoreMatchers.is("foo"));
        Assert.assertThat(helper.get(remoteUrl("/bar")), CoreMatchers.is("bar"));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(out);
        PrintStream oldOut = System.out;
        PrintStream oldErr = System.err;
        System.setErr(printStream);
        System.setOut(printStream);
        changeFileContent(config1, ("[{" + (((((("        \"request\": {" + "            \"uri\": \"/foo\"") + "        },") + "        \"response\": {") + "            \"text\": \"foo1\"") + "        }") + "}]")));
        changeFileContent(config2, ("[{" + (((((("        \"request\": {" + "            \"uri\": \"/bar\"") + "        },") + "        \"response\": {") + "            \"text\": \"bar1\"") + "        }") + "}]")));
        waitChangeHappens();
        System.setOut(oldOut);
        System.setErr(oldErr);
        Assert.assertThat(helper.get(remoteUrl("/foo")), CoreMatchers.is("foo1"));
        String result = new String(out.toByteArray());
        Assert.assertThat(result.contains("Fail"), CoreMatchers.is(false));
    }
}

