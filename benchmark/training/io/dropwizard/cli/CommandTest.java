package io.dropwizard.cli;


import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import java.io.ByteArrayOutputStream;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import org.junit.jupiter.api.Test;


public class CommandTest {
    private static class TestCommand extends Command {
        protected TestCommand() {
            super("test", "test");
        }

        @Override
        public void configure(Subparser subparser) {
            subparser.addArgument("types").choices("a", "b", "c").help("Type to use");
        }

        @Override
        public void run(Bootstrap<?> bootstrap, Namespace namespace) throws Exception {
        }
    }

    private final Application<Configuration> app = new Application<Configuration>() {
        @Override
        public void run(Configuration configuration, Environment environment) throws Exception {
        }
    };

    private final ByteArrayOutputStream stdOut = new ByteArrayOutputStream();

    private final ByteArrayOutputStream stdErr = new ByteArrayOutputStream();

    private final Command command = new CommandTest.TestCommand();

    private Cli cli;

    @Test
    public void listHelpOnceOnArgumentOmission() throws Exception {
        assertThat(cli.run("test", "-h")).isTrue();
        assertThat(stdOut.toString()).isEqualTo(String.format(("usage: java -jar dw-thing.jar test [-h] {a,b,c}%n" + ((((((("%n" + "test%n") + "%n") + "positional arguments:%n") + "  {a,b,c}                Type to use%n") + "%n") + "named arguments:%n") + "  -h, --help             show this help message and exit%n"))));
        assertThat(stdErr.toString()).isEmpty();
    }
}

