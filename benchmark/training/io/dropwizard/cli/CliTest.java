package io.dropwizard.cli;


import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.util.JarLocation;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.Locale;
import java.util.Optional;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;


public class CliTest {
    private static final Locale DEFAULT_LOCALE = Locale.getDefault();

    private final JarLocation location = Mockito.mock(JarLocation.class);

    private final Application<Configuration> app = new Application<Configuration>() {
        @Override
        public void run(Configuration configuration, Environment environment) throws Exception {
        }
    };

    private static final class BadAppException extends Exception {
        private static final long serialVersionUID = 1L;

        public static final String BAD_APP_EXCEPTION_STACK_TRACE = "BadAppException stack trace";

        public BadAppException() {
        }

        @Override
        public void printStackTrace(PrintWriter writer) {
            writer.println(CliTest.BadAppException.BAD_APP_EXCEPTION_STACK_TRACE);
        }

        @Override
        public String getMessage() {
            return "I'm a bad exception";
        }
    }

    public static final class CustomCommand extends Command {
        protected CustomCommand() {
            super("custom", "I'm custom");
        }

        @Override
        public void configure(Subparser subparser) {
            subparser.addArgument("--debug").action(Arguments.storeTrue());
        }

        @Override
        public void run(Bootstrap<?> bootstrap, Namespace namespace) throws Exception {
            throw new RuntimeException("I did not expect this!");
        }

        @Override
        public void onError(Cli cli, Namespace namespace, Throwable e) {
            if (namespace.getBoolean("debug")) {
                super.onError(cli, namespace, e);
            } else {
                cli.getStdOut().println(e.getMessage());
            }
        }
    }

    private final Bootstrap<Configuration> bootstrap = new Bootstrap(app);

    private final ByteArrayOutputStream stdOut = new ByteArrayOutputStream();

    private final ByteArrayOutputStream stdErr = new ByteArrayOutputStream();

    private final CheckCommand<Configuration> command = Mockito.spy(new CheckCommand(app));

    private Cli cli;

    @Test
    public void handlesShortVersionCommands() throws Exception {
        assertThat(cli.run("-v")).isTrue();
        assertThat(stdOut.toString()).isEqualTo(String.format("1.0.0%n"));
        assertThat(stdErr.toString()).isEmpty();
    }

    @Test
    public void handlesLongVersionCommands() throws Exception {
        assertThat(cli.run("--version")).isTrue();
        assertThat(stdOut.toString()).isEqualTo(String.format("1.0.0%n"));
        assertThat(stdErr.toString()).isEmpty();
    }

    @Test
    public void handlesMissingVersions() throws Exception {
        Mockito.when(location.getVersion()).thenReturn(Optional.empty());
        final Cli newCli = new Cli(location, bootstrap, stdOut, stdErr);
        assertThat(newCli.run("--version")).isTrue();
        assertThat(stdOut.toString()).isEqualTo(String.format("No application version detected. Add a Implementation-Version entry to your JAR's manifest to enable this.%n"));
        assertThat(stdErr.toString()).isEmpty();
    }

    @Test
    public void handlesZeroArgumentsAsHelpCommand() throws Exception {
        assertThat(cli.run()).isTrue();
        assertThat(stdOut.toString()).isEqualTo(String.format(("usage: java -jar dw-thing.jar [-h] [-v] {check,custom} ...%n" + (((((("%n" + "positional arguments:%n") + "  {check,custom}         available commands%n") + "%n") + "named arguments:%n") + "  -h, --help             show this help message and exit%n") + "  -v, --version          show the application version and exit%n"))));
        assertThat(stdErr.toString()).isEmpty();
    }

    @Test
    public void handlesShortHelpCommands() throws Exception {
        assertThat(cli.run("-h")).isTrue();
        assertThat(stdOut.toString()).isEqualTo(String.format(("usage: java -jar dw-thing.jar [-h] [-v] {check,custom} ...%n" + (((((("%n" + "positional arguments:%n") + "  {check,custom}         available commands%n") + "%n") + "named arguments:%n") + "  -h, --help             show this help message and exit%n") + "  -v, --version          show the application version and exit%n"))));
        assertThat(stdErr.toString()).isEmpty();
    }

    @Test
    public void handlesLongHelpCommands() throws Exception {
        assertThat(cli.run("--help")).isTrue();
        assertThat(stdOut.toString()).isEqualTo(String.format(("usage: java -jar dw-thing.jar [-h] [-v] {check,custom} ...%n" + (((((("%n" + "positional arguments:%n") + "  {check,custom}         available commands%n") + "%n") + "named arguments:%n") + "  -h, --help             show this help message and exit%n") + "  -v, --version          show the application version and exit%n"))));
        assertThat(stdErr.toString()).isEmpty();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void handlesShortHelpSubcommands() throws Exception {
        assertThat(cli.run("check", "-h")).isTrue();
        assertThat(stdOut.toString()).isEqualTo(String.format(("usage: java -jar dw-thing.jar check [-h] [file]%n" + ((((((("%n" + "Parses and validates the configuration file%n") + "%n") + "positional arguments:%n") + "  file                   application configuration file%n") + "%n") + "named arguments:%n") + "  -h, --help             show this help message and exit%n"))));
        assertThat(stdErr.toString()).isEmpty();
        Mockito.verify(command, Mockito.never()).run(any(Bootstrap.class), org.mockito.ArgumentMatchers.any(Namespace.class), org.mockito.ArgumentMatchers.any(Configuration.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void handlesLongHelpSubcommands() throws Exception {
        assertThat(cli.run("check", "--help")).isTrue();
        assertThat(stdOut.toString()).isEqualTo(String.format(("usage: java -jar dw-thing.jar check [-h] [file]%n" + ((((((("%n" + "Parses and validates the configuration file%n") + "%n") + "positional arguments:%n") + "  file                   application configuration file%n") + "%n") + "named arguments:%n") + "  -h, --help             show this help message and exit%n"))));
        assertThat(stdErr.toString()).isEmpty();
        Mockito.verify(command, Mockito.never()).run(any(Bootstrap.class), org.mockito.ArgumentMatchers.any(Namespace.class), org.mockito.ArgumentMatchers.any(Configuration.class));
    }

    @Test
    public void rejectsBadCommandFlags() throws Exception {
        assertThat(cli.run("--yes")).isFalse();
        assertThat(stdOut.toString()).isEmpty();
        assertThat(stdErr.toString()).isEqualTo(String.format(("unrecognized arguments: '--yes'%n" + ((((((("usage: java -jar dw-thing.jar [-h] [-v] {check,custom} ...%n" + "%n") + "positional arguments:%n") + "  {check,custom}         available commands%n") + "%n") + "named arguments:%n") + "  -h, --help             show this help message and exit%n") + "  -v, --version          show the application version and exit%n"))));
    }

    @Test
    public void rejectsBadSubcommandFlags() throws Exception {
        assertThat(cli.run("check", "--yes")).isFalse();
        assertThat(stdOut.toString()).isEmpty();
        assertThat(stdErr.toString()).isEqualTo(String.format(("unrecognized arguments: '--yes'%n" + (((((((("usage: java -jar dw-thing.jar check [-h] [file]%n" + "%n") + "Parses and validates the configuration file%n") + "%n") + "positional arguments:%n") + "  file                   application configuration file%n") + "%n") + "named arguments:%n") + "  -h, --help             show this help message and exit%n"))));
    }

    @Test
    public void rejectsBadSubcommands() throws Exception {
        assertThat(cli.run("plop")).isFalse();
        assertThat(stdOut.toString()).isEmpty();
        assertThat(stdErr.toString()).isEqualTo(String.format(("invalid choice: 'plop' (choose from 'check', 'custom')%n" + ((((((("usage: java -jar dw-thing.jar [-h] [-v] {check,custom} ...%n" + "%n") + "positional arguments:%n") + "  {check,custom}         available commands%n") + "%n") + "named arguments:%n") + "  -h, --help             show this help message and exit%n") + "  -v, --version          show the application version and exit%n"))));
    }

    @Test
    public void runsCommands() throws Exception {
        assertThat(cli.run("check")).isTrue();
        assertThat(stdOut.toString()).isEmpty();
        assertThat(stdErr.toString()).isEmpty();
        Mockito.verify(command).run(eq(bootstrap), org.mockito.ArgumentMatchers.any(Namespace.class), org.mockito.ArgumentMatchers.any(Configuration.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void unhandledExceptionsMessagesArePrintedForCheck() throws Exception {
        Mockito.doThrow(new CliTest.BadAppException()).when(command).run(any(Bootstrap.class), org.mockito.ArgumentMatchers.any(Namespace.class), org.mockito.ArgumentMatchers.any(Configuration.class));
        assertThat(cli.run("check")).isFalse();
        assertThat(stdOut.toString()).isEmpty();
        assertThat(stdErr.toString()).isEqualTo(String.format("I'm a bad exception%n"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void unhandledExceptionsCustomCommand() throws Exception {
        Mockito.doThrow(new CliTest.BadAppException()).when(command).run(any(Bootstrap.class), org.mockito.ArgumentMatchers.any(Namespace.class), org.mockito.ArgumentMatchers.any(Configuration.class));
        assertThat(cli.run("custom")).isFalse();
        assertThat(stdOut.toString()).isEqualTo(String.format("I did not expect this!%n"));
        assertThat(stdErr.toString()).isEmpty();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void unhandledExceptionsCustomCommandDebug() throws Exception {
        Mockito.doThrow(new CliTest.BadAppException()).when(command).run(any(Bootstrap.class), org.mockito.ArgumentMatchers.any(Namespace.class), org.mockito.ArgumentMatchers.any(Configuration.class));
        assertThat(cli.run("custom", "--debug")).isFalse();
        assertThat(stdOut.toString()).isEmpty();
        assertThat(stdErr.toString()).startsWith(String.format(("java.lang.RuntimeException: I did not expect this!%n" + "\tat io.dropwizard.cli.CliTest$CustomCommand.run(CliTest.java")));
    }
}

