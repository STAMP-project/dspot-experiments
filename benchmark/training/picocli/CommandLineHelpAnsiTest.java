package picocli;


import Ansi.AUTO;
import Ansi.IStyle;
import Ansi.OFF;
import Ansi.ON;
import Ansi.Style;
import Ansi.Style.bg_cyan;
import Ansi.Style.bg_green;
import Ansi.Style.bg_magenta;
import Ansi.Style.bg_red;
import Ansi.Style.bg_white;
import Ansi.Style.bg_yellow;
import Ansi.Style.bold;
import Ansi.Style.faint;
import Ansi.Style.fg_black;
import Ansi.Style.fg_red;
import Ansi.Style.reverse;
import CommandLine.Command;
import CommandLine.Help.ColorScheme;
import CommandLine.Option;
import CommandLine.Parameters;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import org.fusesource.jansi.AnsiConsole;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.rules.TestRule;
import picocli.CommandLine.Help.Ansi;


public class CommandLineHelpAnsiTest {
    private static final String LINESEP = System.getProperty("line.separator");

    private static final String[] ANSI_ENVIRONMENT_VARIABLES = new String[]{ "TERM", "OSTYPE", "NO_COLOR", "ANSICON", "CLICOLOR", "ConEmuANSI", "CLICOLOR_FORCE" };

    // allows tests to set any kind of properties they like, without having to individually roll them back
    @Rule
    public final TestRule restoreSystemProperties = new RestoreSystemProperties();

    @Rule
    public final SystemOutRule systemOutRule = new SystemOutRule().enableLog().muteForSuccessfulTests();

    @Rule
    public final EnvironmentVariables environmentVariables = new EnvironmentVariables();

    @Test
    public void testTextWithMultipleStyledSections() {
        Assert.assertEquals("\u001b[1m<main class>\u001b[21m\u001b[0m [\u001b[33m-v\u001b[39m\u001b[0m] [\u001b[33m-c\u001b[39m\u001b[0m [\u001b[3m<count>\u001b[23m\u001b[0m]]", Ansi.ON.new Text("@|bold <main class>|@ [@|yellow -v|@] [@|yellow -c|@ [@|italic <count>|@]]").toString());
    }

    @Test
    public void testTextAdjacentStyles() {
        Assert.assertEquals("\u001b[3m<commit\u001b[23m\u001b[0m\u001b[3m>\u001b[23m\u001b[0m%n", Ansi.ON.new Text("@|italic <commit|@@|italic >|@%n").toString());
    }

    @Test
    public void testTextNoConversionWithoutClosingTag() {
        Assert.assertEquals("\u001b[3mabc\u001b[23m\u001b[0m", Ansi.ON.new Text("@|italic abc|@").toString());
        Assert.assertEquals("@|italic abc", Ansi.ON.new Text("@|italic abc").toString());
    }

    @Test
    public void testTextNoConversionWithoutSpaceSeparator() {
        Assert.assertEquals("\u001b[3ma\u001b[23m\u001b[0m", Ansi.ON.new Text("@|italic a|@").toString());
        Assert.assertEquals("@|italic|@", Ansi.ON.new Text("@|italic|@").toString());
        Assert.assertEquals("", Ansi.ON.new Text("@|italic |@").toString());
    }

    @Test
    public void testPalette236ColorForegroundIndex() {
        Assert.assertEquals("\u001b[38;5;45mabc\u001b[39m\u001b[0m", Ansi.ON.new Text("@|fg(45) abc|@").toString());
    }

    @Test
    public void testPalette236ColorForegroundRgb() {
        int num = ((16 + (36 * 5)) + (6 * 5)) + 5;
        Assert.assertEquals((("\u001b[38;5;" + num) + "mabc\u001b[39m\u001b[0m"), Ansi.ON.new Text("@|fg(5;5;5) abc|@").toString());
    }

    @Test
    public void testPalette236ColorBackgroundIndex() {
        Assert.assertEquals("\u001b[48;5;77mabc\u001b[49m\u001b[0m", Ansi.ON.new Text("@|bg(77) abc|@").toString());
    }

    @Test
    public void testPalette236ColorBackgroundRgb() {
        int num = ((16 + (36 * 3)) + (6 * 3)) + 3;
        Assert.assertEquals((("\u001b[48;5;" + num) + "mabc\u001b[49m\u001b[0m"), Ansi.ON.new Text("@|bg(3;3;3) abc|@").toString());
    }

    @Test
    public void testAnsiEnabled() {
        Assert.assertTrue(ON.enabled());
        Assert.assertFalse(OFF.enabled());
        System.setProperty("picocli.ansi", "true");
        Assert.assertEquals(true, AUTO.enabled());
        System.setProperty("picocli.ansi", "false");
        Assert.assertEquals(false, AUTO.enabled());
        System.clearProperty("picocli.ansi");
        boolean isWindows = System.getProperty("os.name").startsWith("Windows");
        boolean isXterm = ((System.getenv("TERM")) != null) && (System.getenv("TERM").startsWith("xterm"));
        boolean hasOsType = (System.getenv("OSTYPE")) != null;// null on Windows unless on Cygwin or MSYS

        boolean isAtty = (isWindows && (isXterm || hasOsType))// cygwin pseudo-tty
         || (hasConsole());
        Assert.assertEquals(((isAtty && (((!isWindows) || isXterm) || hasOsType)) || (CommandLineHelpAnsiTest.isJansiConsoleInstalled())), AUTO.enabled());
        if (isWindows && (!(AUTO.enabled()))) {
            AnsiConsole.systemInstall();
            try {
                Assert.assertTrue(AUTO.enabled());
            } finally {
                AnsiConsole.systemUninstall();
            }
        }
    }

    @Test
    public void testSystemPropertiesOverrideDefaultColorScheme() {
        @Command(separator = "=")
        class App {
            @Option(names = { "--verbose", "-v" })
            boolean verbose;

            @Option(names = { "--count", "-c" })
            int count;

            @Option(names = { "--help", "-h" }, hidden = true)
            boolean helpRequested;

            @Parameters(paramLabel = "FILE", arity = "1..*")
            File[] files;
        }
        Ansi ansi = Ansi.ON;
        // default color scheme
        Assert.assertEquals(ansi.new Text(("@|bold <main class>|@ [@|yellow -v|@] [@|yellow -c|@=@|italic <count>|@] @|yellow FILE|@..." + (CommandLineHelpAnsiTest.LINESEP))), synopsis(0));
        System.setProperty("picocli.color.commands", "blue");
        System.setProperty("picocli.color.options", "green");
        System.setProperty("picocli.color.parameters", "cyan");
        System.setProperty("picocli.color.optionParams", "magenta");
        Assert.assertEquals(ansi.new Text(("@|blue <main class>|@ [@|green -v|@] [@|green -c|@=@|magenta <count>|@] @|cyan FILE|@..." + (CommandLineHelpAnsiTest.LINESEP))), synopsis(0));
    }

    @Test
    public void testSystemPropertiesOverrideExplicitColorScheme() {
        @Command(separator = "=")
        class App {
            @Option(names = { "--verbose", "-v" })
            boolean verbose;

            @Option(names = { "--count", "-c" })
            int count;

            @Option(names = { "--help", "-h" }, hidden = true)
            boolean helpRequested;

            @Parameters(paramLabel = "FILE", arity = "1..*")
            File[] files;
        }
        Ansi ansi = Ansi.ON;
        CommandLine.Help.ColorScheme explicit = new CommandLine.Help.ColorScheme(ansi).commands(faint, bg_magenta).options(bg_red).parameters(reverse).optionParams(bg_green);
        // default color scheme
        Assert.assertEquals(ansi.new Text(("@|faint,bg(magenta) <main class>|@ [@|bg(red) -v|@] [@|bg(red) -c|@=@|bg(green) <count>|@] @|reverse FILE|@..." + (CommandLineHelpAnsiTest.LINESEP))), synopsis(0));
        System.setProperty("picocli.color.commands", "blue");
        System.setProperty("picocli.color.options", "blink");
        System.setProperty("picocli.color.parameters", "red");
        System.setProperty("picocli.color.optionParams", "magenta");
        Assert.assertEquals(ansi.new Text(("@|blue <main class>|@ [@|blink -v|@] [@|blink -c|@=@|magenta <count>|@] @|red FILE|@..." + (CommandLineHelpAnsiTest.LINESEP))), synopsis(0));
    }

    @Test
    public void testUsageWithCustomColorScheme() throws UnsupportedEncodingException {
        CommandLine.Help.ColorScheme scheme = new CommandLine.Help.ColorScheme(Ansi.ON).options(bg_magenta).parameters(bg_cyan).optionParams(bg_yellow).commands(reverse);
        class Args {
            @Parameters(description = "param desc")
            String[] params;

            @Option(names = "-x", description = "option desc")
            String[] options;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CommandLine.usage(new Args(), new PrintStream(baos, true, "UTF8"), scheme);
        String actual = baos.toString("UTF8");
        String expected = String.format(("" + (("Usage: @|reverse <main class>|@ [@|bg_magenta -x|@=@|bg_yellow <options>|@]... [@|bg_cyan <params>|@...]%n" + "      [@|bg_cyan <params>|@...]   param desc%n") + "  @|bg_magenta -x|@=@|bg_yellow <|@@|bg_yellow options>|@        option desc%n")));
        Assert.assertEquals(Ansi.ON.new Text(expected).toString(), actual);
    }

    @Test
    public void testAbreviatedSynopsis_withParameters() {
        @Command(abbreviateSynopsis = true)
        class App {
            @Option(names = { "--verbose", "-v" })
            boolean verbose;

            @Option(names = { "--count", "-c" })
            int count;

            @Option(names = { "--help", "-h" }, hidden = true)
            boolean helpRequested;

            @CommandLine.Parameters
            File[] files;
        }
        CommandLine.Help help = new CommandLine.Help(new App(), Ansi.OFF);
        Assert.assertEquals(("<main class> [OPTIONS] [<files>...]" + (CommandLineHelpAnsiTest.LINESEP)), help.synopsis(0));
    }

    @Test
    public void testAbreviatedSynopsis_withParameters_ANSI() {
        @Command(abbreviateSynopsis = true)
        class App {
            @Option(names = { "--verbose", "-v" })
            boolean verbose;

            @Option(names = { "--count", "-c" })
            int count;

            @Option(names = { "--help", "-h" }, hidden = true)
            boolean helpRequested;

            @CommandLine.Parameters
            File[] files;
        }
        CommandLine.Help help = new CommandLine.Help(new App(), Ansi.ON);
        Assert.assertEquals(Ansi.ON.new Text(("@|bold <main class>|@ [OPTIONS] [@|yellow <files>|@...]" + (CommandLineHelpAnsiTest.LINESEP))).toString(), help.synopsis(0));
    }

    @Test
    public void testSynopsis_withSeparator_withParameters() {
        @Command(separator = ":")
        class App {
            @Option(names = { "--verbose", "-v" })
            boolean verbose;

            @Option(names = { "--count", "-c" })
            int count;

            @Option(names = { "--help", "-h" }, hidden = true)
            boolean helpRequested;

            @CommandLine.Parameters
            File[] files;
        }
        CommandLine.Help help = new CommandLine.Help(new App(), Ansi.OFF);
        Assert.assertEquals(("<main class> [-v] [-c:<count>] [<files>...]" + (CommandLineHelpAnsiTest.LINESEP)), help.synopsis(0));
    }

    @Test
    public void testSynopsis_withSeparator_withParameters_ANSI() {
        @Command(separator = ":")
        class App {
            @Option(names = { "--verbose", "-v" })
            boolean verbose;

            @Option(names = { "--count", "-c" })
            int count;

            @Option(names = { "--help", "-h" }, hidden = true)
            boolean helpRequested;

            @CommandLine.Parameters
            File[] files;
        }
        CommandLine.Help help = new CommandLine.Help(new App(), Ansi.ON);
        Assert.assertEquals(Ansi.ON.new Text(("@|bold <main class>|@ [@|yellow -v|@] [@|yellow -c|@:@|italic <count>|@] [@|yellow <files>|@...]" + (CommandLineHelpAnsiTest.LINESEP))), help.synopsis(0));
    }

    @Test
    public void testSynopsis_withSeparator_withLabeledParameters() {
        @Command(separator = "=")
        class App {
            @Option(names = { "--verbose", "-v" })
            boolean verbose;

            @Option(names = { "--count", "-c" })
            int count;

            @Option(names = { "--help", "-h" }, hidden = true)
            boolean helpRequested;

            @Parameters(paramLabel = "FILE")
            File[] files;
        }
        CommandLine.Help help = new CommandLine.Help(new App(), Ansi.OFF);
        Assert.assertEquals(("<main class> [-v] [-c=<count>] [FILE...]" + (CommandLineHelpAnsiTest.LINESEP)), help.synopsis(0));
    }

    @Test
    public void testSynopsis_withSeparator_withLabeledParameters_ANSI() {
        @Command(separator = "=")
        class App {
            @Option(names = { "--verbose", "-v" })
            boolean verbose;

            @Option(names = { "--count", "-c" })
            int count;

            @Option(names = { "--help", "-h" }, hidden = true)
            boolean helpRequested;

            @Parameters(paramLabel = "FILE")
            File[] files;
        }
        CommandLine.Help help = new CommandLine.Help(new App(), Ansi.ON);
        Assert.assertEquals(Ansi.ON.new Text(("@|bold <main class>|@ [@|yellow -v|@] [@|yellow -c|@=@|italic <count>|@] [@|yellow FILE|@...]" + (CommandLineHelpAnsiTest.LINESEP))), help.synopsis(0));
    }

    @Test
    public void testSynopsis_withSeparator_withLabeledRequiredParameters() {
        @Command(separator = "=")
        class App {
            @Option(names = { "--verbose", "-v" })
            boolean verbose;

            @Option(names = { "--count", "-c" })
            int count;

            @Option(names = { "--help", "-h" }, hidden = true)
            boolean helpRequested;

            @Parameters(paramLabel = "FILE", arity = "1..*")
            File[] files;
        }
        CommandLine.Help help = new CommandLine.Help(new App(), Ansi.OFF);
        Assert.assertEquals(("<main class> [-v] [-c=<count>] FILE..." + (CommandLineHelpAnsiTest.LINESEP)), help.synopsis(0));
    }

    @Test
    public void testSynopsis_withSeparator_withLabeledRequiredParameters_ANSI() {
        @Command(separator = "=")
        class App {
            @Option(names = { "--verbose", "-v" })
            boolean verbose;

            @Option(names = { "--count", "-c" })
            int count;

            @Option(names = { "--help", "-h" }, hidden = true)
            boolean helpRequested;

            @Parameters(paramLabel = "FILE", arity = "1..*")
            File[] files;
        }
        CommandLine.Help help = new CommandLine.Help(new App(), Ansi.ON);
        Assert.assertEquals(Ansi.ON.new Text(("@|bold <main class>|@ [@|yellow -v|@] [@|yellow -c|@=@|italic <count>|@] @|yellow FILE|@..." + (CommandLineHelpAnsiTest.LINESEP))), help.synopsis(0));
    }

    @Test
    public void testSynopsis_clustersRequiredBooleanOptionsSeparately() {
        @Command(separator = "=")
        class App {
            @Option(names = { "--verbose", "-v" })
            boolean verbose;

            @Option(names = { "--aaaa", "-a" })
            boolean aBoolean;

            @Option(names = { "--xxxx", "-x" })
            Boolean xBoolean;

            @Option(names = { "--Verbose", "-V" }, required = true)
            boolean requiredVerbose;

            @Option(names = { "--Aaaa", "-A" }, required = true)
            boolean requiredABoolean;

            @Option(names = { "--Xxxx", "-X" }, required = true)
            Boolean requiredXBoolean;

            @Option(names = { "--count", "-c" }, paramLabel = "COUNT")
            int count;
        }
        CommandLine.Help help = new CommandLine.Help(new App(), Ansi.OFF);
        Assert.assertEquals(("<main class> -AVX [-avx] [-c=COUNT]" + (CommandLineHelpAnsiTest.LINESEP)), help.synopsis(0));
    }

    @Test
    public void testSynopsis_clustersRequiredBooleanOptionsSeparately_ANSI() {
        @Command(separator = "=")
        class App {
            @Option(names = { "--verbose", "-v" })
            boolean verbose;

            @Option(names = { "--aaaa", "-a" })
            boolean aBoolean;

            @Option(names = { "--xxxx", "-x" })
            Boolean xBoolean;

            @Option(names = { "--Verbose", "-V" }, required = true)
            boolean requiredVerbose;

            @Option(names = { "--Aaaa", "-A" }, required = true)
            boolean requiredABoolean;

            @Option(names = { "--Xxxx", "-X" }, required = true)
            Boolean requiredXBoolean;

            @Option(names = { "--count", "-c" }, paramLabel = "COUNT")
            int count;
        }
        CommandLine.Help help = new CommandLine.Help(new App(), Ansi.ON);
        Assert.assertEquals(Ansi.ON.new Text(("@|bold <main class>|@ @|yellow -AVX|@ [@|yellow -avx|@] [@|yellow -c|@=@|italic COUNT|@]" + (CommandLineHelpAnsiTest.LINESEP))), help.synopsis(0));
    }

    @Test
    public void testSynopsis_firstLineLengthAdjustedForSynopsisHeading() {
        // Usage: small-test-program [-acorv!?] [--version] [-h <number>] [-p <file>|<folder>] [-d
        // <folder> [<folder>]] [-i <includePattern>
        // [<includePattern>...]]
        @Command(name = "small-test-program", sortOptions = false, separator = " ")
        class App {
            @Option(names = "-a")
            boolean a;

            @Option(names = "-c")
            boolean c;

            @Option(names = "-o")
            boolean o;

            @Option(names = "-r")
            boolean r;

            @Option(names = "-v")
            boolean v;

            @Option(names = "-!")
            boolean exclamation;

            @Option(names = "-?")
            boolean question;

            @Option(names = { "--version" })
            boolean version;

            @Option(names = { "--handle", "-h" })
            int number;

            @Option(names = { "--ppp", "-p" }, paramLabel = "<file>|<folder>")
            File f;

            @Option(names = { "--ddd", "-d" }, paramLabel = "<folder>", arity = "1..2")
            File[] d;

            @Option(names = { "--include", "-i" }, paramLabel = "<includePattern>")
            String pattern;
        }
        CommandLine.Help help = new CommandLine.Help(new App(), Ansi.OFF);
        String expected = ((((("" + "Usage: small-test-program [-!?acorv] [--version] [-h <number>] [-i") + (CommandLineHelpAnsiTest.LINESEP)) + "                          <includePattern>] [-p <file>|<folder>] [-d <folder>") + (CommandLineHelpAnsiTest.LINESEP)) + "                          [<folder>]]...") + (CommandLineHelpAnsiTest.LINESEP);
        Assert.assertEquals(expected, ((help.synopsisHeading()) + (help.synopsis(help.synopsisHeadingLength()))));
        help.commandSpec().usageMessage().synopsisHeading("Usage:%n");
        expected = ((((("" + "Usage:") + (CommandLineHelpAnsiTest.LINESEP)) + "small-test-program [-!?acorv] [--version] [-h <number>] [-i <includePattern>]") + (CommandLineHelpAnsiTest.LINESEP)) + "                   [-p <file>|<folder>] [-d <folder> [<folder>]]...") + (CommandLineHelpAnsiTest.LINESEP);
        Assert.assertEquals(expected, ((help.synopsisHeading()) + (help.synopsis(help.synopsisHeadingLength()))));
    }

    @Test
    public void testLongMultiLineSynopsisIndented() {
        @Command(name = "<best-app-ever>")
        class App {
            @Option(names = "--long-option-name", paramLabel = "<long-option-value>")
            int a;

            @Option(names = "--another-long-option-name", paramLabel = "<another-long-option-value>")
            int b;

            @Option(names = "--third-long-option-name", paramLabel = "<third-long-option-value>")
            int c;

            @Option(names = "--fourth-long-option-name", paramLabel = "<fourth-long-option-value>")
            int d;
        }
        CommandLine.Help help = new CommandLine.Help(new App(), Ansi.OFF);
        Assert.assertEquals(String.format(("<best-app-ever> [--another-long-option-name=<another-long-option-value>]%n" + (("                [--fourth-long-option-name=<fourth-long-option-value>]%n" + "                [--long-option-name=<long-option-value>]%n") + "                [--third-long-option-name=<third-long-option-value>]%n"))), help.synopsis(0));
    }

    @Test
    public void testLongMultiLineSynopsisWithAtMarkIndented() {
        @Command(name = "<best-app-ever>")
        class App {
            @Option(names = "--long-option@-name", paramLabel = "<long-option-valu@@e>")
            int a;

            @Option(names = "--another-long-option-name", paramLabel = "^[<another-long-option-value>]")
            int b;

            @Option(names = "--third-long-option-name", paramLabel = "<third-long-option-value>")
            int c;

            @Option(names = "--fourth-long-option-name", paramLabel = "<fourth-long-option-value>")
            int d;
        }
        CommandLine.Help help = new CommandLine.Help(new App(), Ansi.OFF);
        Assert.assertEquals(String.format(("<best-app-ever> [--another-long-option-name=^[<another-long-option-value>]]%n" + (("                [--fourth-long-option-name=<fourth-long-option-value>]%n" + "                [--long-option@-name=<long-option-valu@@e>]%n") + "                [--third-long-option-name=<third-long-option-value>]%n"))), help.synopsis(0));
    }

    @Test
    public void testLongMultiLineSynopsisWithAtMarkIndented_ANSI() {
        @Command(name = "<best-app-ever>")
        class App {
            @Option(names = "--long-option@-name", paramLabel = "<long-option-valu@@e>")
            int a;

            @Option(names = "--another-long-option-name", paramLabel = "^[<another-long-option-value>]")
            int b;

            @Option(names = "--third-long-option-name", paramLabel = "<third-long-option-value>")
            int c;

            @Option(names = "--fourth-long-option-name", paramLabel = "<fourth-long-option-value>")
            int d;
        }
        CommandLine.Help help = new CommandLine.Help(new App(), Ansi.ON);
        Assert.assertEquals(Ansi.ON.new Text(String.format(("@|bold <best-app-ever>|@ [@|yellow --another-long-option-name|@=@|italic ^[<another-long-option-value>]|@]%n" + (("                [@|yellow --fourth-long-option-name|@=@|italic <fourth-long-option-value>|@]%n" + "                [@|yellow --long-option@-name|@=@|italic <long-option-valu@@e>|@]%n") + "                [@|yellow --third-long-option-name|@=@|italic <third-long-option-value>|@]%n")))), help.synopsis(0));
    }

    @Test
    public void testUsageMainCommand_NoAnsi() {
        String actual = HelpTestUtil.usageString(Demo.mainCommand(), OFF);
        Assert.assertEquals(String.format(Demo.EXPECTED_USAGE_MAIN), actual);
    }

    @Test
    public void testUsageMainCommand_ANSI() {
        String actual = HelpTestUtil.usageString(Demo.mainCommand(), ON);
        Assert.assertEquals(Ansi.ON.new Text(String.format(Demo.EXPECTED_USAGE_MAIN_ANSI)), actual);
    }

    @Test
    public void testUsageSubcommandGitStatus_NoAnsi() {
        String actual = HelpTestUtil.usageString(new Demo.GitStatus(), OFF);
        Assert.assertEquals(String.format(Demo.EXPECTED_USAGE_GITSTATUS), actual);
    }

    @Test
    public void testUsageSubcommandGitStatus_ANSI() {
        String actual = HelpTestUtil.usageString(new Demo.GitStatus(), ON);
        Assert.assertEquals(Ansi.ON.new Text(String.format(Demo.EXPECTED_USAGE_GITSTATUS_ANSI)), actual);
    }

    @Test
    public void testUsageSubcommandGitCommit_NoAnsi() {
        String actual = HelpTestUtil.usageString(new Demo.GitCommit(), OFF);
        Assert.assertEquals(String.format(Demo.EXPECTED_USAGE_GITCOMMIT), actual);
    }

    @Test
    public void testUsageSubcommandGitCommit_ANSI() {
        String actual = HelpTestUtil.usageString(new Demo.GitCommit(), ON);
        Assert.assertEquals(Ansi.ON.new Text(String.format(Demo.EXPECTED_USAGE_GITCOMMIT_ANSI)), actual);
    }

    @Test
    public void testTextConstructorPlain() {
        Assert.assertEquals("--NoAnsiFormat", Ansi.ON.new Text("--NoAnsiFormat").toString());
    }

    @Test
    public void testTextConstructorWithStyle() {
        Assert.assertEquals("\u001b[1m--NoAnsiFormat\u001b[21m\u001b[0m", Ansi.ON.new Text("@|bold --NoAnsiFormat|@").toString());
    }

    @Test
    public void testTextApply() {
        Ansi.Text txt = ON.apply("--p", Arrays.<Ansi.IStyle>asList(fg_red, bold));
        Assert.assertEquals(Ansi.ON.new Text("@|fg(red),bold --p|@"), txt);
    }

    @Test
    public void testTextDefaultColorScheme() {
        Ansi ansi = Ansi.ON;
        CommandLine.Help.ColorScheme scheme = CommandLine.Help.defaultColorScheme(ansi);
        Assert.assertEquals(scheme.ansi().new Text("@|yellow -p|@"), scheme.optionText("-p"));
        Assert.assertEquals(scheme.ansi().new Text("@|bold command|@"), scheme.commandText("command"));
        Assert.assertEquals(scheme.ansi().new Text("@|yellow FILE|@"), scheme.parameterText("FILE"));
        Assert.assertEquals(scheme.ansi().new Text("@|italic NUMBER|@"), scheme.optionParamText("NUMBER"));
    }

    @Test
    public void testTextSubString() {
        Ansi ansi = Ansi.ON;
        Ansi.Text txt = ansi.new Text("@|bold 01234|@").concat("56").concat("@|underline 7890|@");
        Assert.assertEquals(ansi.new Text("@|bold 01234|@56@|underline 7890|@"), txt.substring(0));
        Assert.assertEquals(ansi.new Text("@|bold 1234|@56@|underline 7890|@"), txt.substring(1));
        Assert.assertEquals(ansi.new Text("@|bold 234|@56@|underline 7890|@"), txt.substring(2));
        Assert.assertEquals(ansi.new Text("@|bold 34|@56@|underline 7890|@"), txt.substring(3));
        Assert.assertEquals(ansi.new Text("@|bold 4|@56@|underline 7890|@"), txt.substring(4));
        Assert.assertEquals(ansi.new Text("56@|underline 7890|@"), txt.substring(5));
        Assert.assertEquals(ansi.new Text("6@|underline 7890|@"), txt.substring(6));
        Assert.assertEquals(ansi.new Text("@|underline 7890|@"), txt.substring(7));
        Assert.assertEquals(ansi.new Text("@|underline 890|@"), txt.substring(8));
        Assert.assertEquals(ansi.new Text("@|underline 90|@"), txt.substring(9));
        Assert.assertEquals(ansi.new Text("@|underline 0|@"), txt.substring(10));
        Assert.assertEquals(ansi.new Text(""), txt.substring(11));
        Assert.assertEquals(ansi.new Text("@|bold 01234|@56@|underline 7890|@"), txt.substring(0, 11));
        Assert.assertEquals(ansi.new Text("@|bold 01234|@56@|underline 789|@"), txt.substring(0, 10));
        Assert.assertEquals(ansi.new Text("@|bold 01234|@56@|underline 78|@"), txt.substring(0, 9));
        Assert.assertEquals(ansi.new Text("@|bold 01234|@56@|underline 7|@"), txt.substring(0, 8));
        Assert.assertEquals(ansi.new Text("@|bold 01234|@56"), txt.substring(0, 7));
        Assert.assertEquals(ansi.new Text("@|bold 01234|@5"), txt.substring(0, 6));
        Assert.assertEquals(ansi.new Text("@|bold 01234|@"), txt.substring(0, 5));
        Assert.assertEquals(ansi.new Text("@|bold 0123|@"), txt.substring(0, 4));
        Assert.assertEquals(ansi.new Text("@|bold 012|@"), txt.substring(0, 3));
        Assert.assertEquals(ansi.new Text("@|bold 01|@"), txt.substring(0, 2));
        Assert.assertEquals(ansi.new Text("@|bold 0|@"), txt.substring(0, 1));
        Assert.assertEquals(ansi.new Text(""), txt.substring(0, 0));
        Assert.assertEquals(ansi.new Text("@|bold 1234|@56@|underline 789|@"), txt.substring(1, 10));
        Assert.assertEquals(ansi.new Text("@|bold 234|@56@|underline 78|@"), txt.substring(2, 9));
        Assert.assertEquals(ansi.new Text("@|bold 34|@56@|underline 7|@"), txt.substring(3, 8));
        Assert.assertEquals(ansi.new Text("@|bold 4|@56"), txt.substring(4, 7));
        Assert.assertEquals(ansi.new Text("5"), txt.substring(5, 6));
        Assert.assertEquals(ansi.new Text("@|bold 2|@"), txt.substring(2, 3));
        Assert.assertEquals(ansi.new Text("@|underline 8|@"), txt.substring(8, 9));
        Ansi.Text txt2 = ansi.new Text("@|bold abc|@@|underline DEF|@");
        Assert.assertEquals(ansi.new Text("@|bold abc|@@|underline DEF|@"), txt2.substring(0));
        Assert.assertEquals(ansi.new Text("@|bold bc|@@|underline DEF|@"), txt2.substring(1));
        Assert.assertEquals(ansi.new Text("@|bold abc|@@|underline DE|@"), txt2.substring(0, 5));
        Assert.assertEquals(ansi.new Text("@|bold bc|@@|underline DE|@"), txt2.substring(1, 5));
    }

    @Test
    public void testTextSplitLines() {
        Ansi ansi = Ansi.ON;
        Ansi[] all = new Ansi.Text[]{ ansi.new Text("@|bold 012\n34|@").concat("5\nAA\n6").concat("@|underline 78\n90|@"), ansi.new Text("@|bold 012\r34|@").concat("5\rAA\r6").concat("@|underline 78\r90|@"), ansi.new Text("@|bold 012\r\n34|@").concat("5\r\nAA\r\n6").concat("@|underline 78\r\n90|@") };
        for (Ansi.Text text : all) {
            Ansi[] lines = text.splitLines();
            int i = 0;
            Assert.assertEquals(ansi.new Text("@|bold 012|@"), lines[(i++)]);
            Assert.assertEquals(ansi.new Text("@|bold 34|@5"), lines[(i++)]);
            Assert.assertEquals(ansi.new Text("AA"), lines[(i++)]);
            Assert.assertEquals(ansi.new Text("6@|underline 78|@"), lines[(i++)]);
            Assert.assertEquals(ansi.new Text("@|underline 90|@"), lines[(i++)]);
        }
    }

    @Test
    public void testTextSplitLinesEmpty() {
        Ansi ansi = Ansi.ON;
        Ansi.Text text = ansi.new Text("abc\n\n\n");
        Ansi[] lines = text.splitLines();
        Assert.assertEquals(4, lines.length);
        Assert.assertEquals(ansi.new Text("abc"), lines[0]);
        Assert.assertEquals(ansi.new Text(""), lines[1]);
        Assert.assertEquals(ansi.new Text(""), lines[2]);
        Assert.assertEquals(ansi.new Text(""), lines[3]);
    }

    @Test
    public void testTextSplitLinesStartEnd() {
        Ansi ansi = Ansi.ON;
        Ansi[] all = new Ansi.Text[]{ ansi.new Text("\n@|bold 012\n34|@").concat("5\nAA\n6").concat("@|underline 78\n90|@\n"), ansi.new Text("\r@|bold 012\r34|@").concat("5\rAA\r6").concat("@|underline 78\r90|@\r"), ansi.new Text("\r\n@|bold 012\r\n34|@").concat("5\r\nAA\r\n6").concat("@|underline 78\r\n90|@\r\n") };
        for (Ansi.Text text : all) {
            Ansi[] lines = text.splitLines();
            int i = 0;
            Assert.assertEquals(ansi.new Text(""), lines[(i++)]);
            Assert.assertEquals(ansi.new Text("@|bold 012|@"), lines[(i++)]);
            Assert.assertEquals(ansi.new Text("@|bold 34|@5"), lines[(i++)]);
            Assert.assertEquals(ansi.new Text("AA"), lines[(i++)]);
            Assert.assertEquals(ansi.new Text("6@|underline 78|@"), lines[(i++)]);
            Assert.assertEquals(ansi.new Text("@|underline 90|@"), lines[(i++)]);
            Assert.assertEquals(ansi.new Text(""), lines[(i++)]);
        }
    }

    @Test
    public void testTextSplitLinesStartEndIntermediate() {
        Ansi ansi = Ansi.ON;
        Ansi[] all = new Ansi.Text[]{ ansi.new Text("\n@|bold 012\n\n\n34|@").concat("5\n\n\nAA\n\n\n6").concat("@|underline 78\n90|@\n"), ansi.new Text("\r@|bold 012\r\r\r34|@").concat("5\r\r\rAA\r\r\r6").concat("@|underline 78\r90|@\r"), ansi.new Text("\r\n@|bold 012\r\n\r\n\r\n34|@").concat("5\r\n\r\n\r\nAA\r\n\r\n\r\n6").concat("@|underline 78\r\n90|@\r\n") };
        for (Ansi.Text text : all) {
            Ansi[] lines = text.splitLines();
            int i = 0;
            Assert.assertEquals(ansi.new Text(""), lines[(i++)]);
            Assert.assertEquals(ansi.new Text("@|bold 012|@"), lines[(i++)]);
            Assert.assertEquals(ansi.new Text(""), lines[(i++)]);
            Assert.assertEquals(ansi.new Text(""), lines[(i++)]);
            Assert.assertEquals(ansi.new Text("@|bold 34|@5"), lines[(i++)]);
            Assert.assertEquals(ansi.new Text(""), lines[(i++)]);
            Assert.assertEquals(ansi.new Text(""), lines[(i++)]);
            Assert.assertEquals(ansi.new Text("AA"), lines[(i++)]);
            Assert.assertEquals(ansi.new Text(""), lines[(i++)]);
            Assert.assertEquals(ansi.new Text(""), lines[(i++)]);
            Assert.assertEquals(ansi.new Text("6@|underline 78|@"), lines[(i++)]);
            Assert.assertEquals(ansi.new Text("@|underline 90|@"), lines[(i++)]);
            Assert.assertEquals(ansi.new Text(""), lines[(i++)]);
        }
    }

    @Test
    public void testTextHashCode() {
        Ansi ansi = Ansi.ON;
        Assert.assertEquals(ansi.new Text("a").hashCode(), ansi.new Text("a").hashCode());
        Assert.assertNotEquals(ansi.new Text("a").hashCode(), ansi.new Text("b").hashCode());
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testTextAppendString() {
        Ansi ansi = Ansi.ON;
        Assert.assertEquals(ansi.new Text("a").append("xyz"), ansi.new Text("a").concat("xyz"));
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testTextAppendText() {
        Ansi ansi = Ansi.ON;
        Ansi.Text xyz = ansi.new Text("xyz");
        Assert.assertEquals(ansi.new Text("a").append(xyz), ansi.new Text("a").concat(xyz));
    }

    @Test
    public void testStyleParseAllowsMissingClosingBrackets() {
        Ansi.IStyle whiteBg = Style.parse("bg(white")[0];
        Assert.assertEquals(bg_white.on(), whiteBg.on());
        Ansi.IStyle blackFg = Style.parse("fg(black")[0];
        Assert.assertEquals(fg_black.on(), blackFg.on());
    }

    @Test
    public void testColorSchemeDefaultConstructorHasAnsiAuto() {
        CommandLine.Help.ColorScheme colorScheme = new CommandLine.Help.ColorScheme();
        Assert.assertEquals(AUTO, colorScheme.ansi());
    }

    @Test
    public void testCommandLine_printVersionInfo_printsArrayOfPlainTextStrings() {
        @Command(version = { "Versioned Command 1.0", "512-bit superdeluxe", "(c) 2017" })
        class Versioned {}
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new CommandLine(new Versioned()).printVersionHelp(new PrintStream(baos, true), OFF);
        String result = baos.toString();
        Assert.assertEquals(String.format("Versioned Command 1.0%n512-bit superdeluxe%n(c) 2017%n"), result);
    }

    @Test
    public void testCommandLine_printVersionInfo_printsSingleStringWithMarkup() {
        @Command(version = "@|red 1.0|@")
        class Versioned {}
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new CommandLine(new Versioned()).printVersionHelp(new PrintStream(baos, true), ON);
        String result = baos.toString();
        Assert.assertEquals(String.format("\u001b[31m1.0\u001b[39m\u001b[0m%n"), result);
    }

    @Test
    public void testCommandLine_printVersionInfo_printsArrayOfStringsWithMarkup() {
        @Command(version = { "@|yellow Versioned Command 1.0|@", "@|blue Build 12345|@", "@|red,bg(white) (c) 2017|@" })
        class Versioned {}
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new CommandLine(new Versioned()).printVersionHelp(new PrintStream(baos, true), ON);
        String result = baos.toString();
        Assert.assertEquals(String.format(("" + (("\u001b[33mVersioned Command 1.0\u001b[39m\u001b[0m%n" + "\u001b[34mBuild 12345\u001b[39m\u001b[0m%n") + "\u001b[31m\u001b[47m(c) 2017\u001b[49m\u001b[39m\u001b[0m%n"))), result);
    }

    @Test
    public void testCommandLine_printVersionInfo_formatsArguments() {
        @Command(version = { "First line %1$s", "Second line %2$s", "Third line %s %s" })
        class Versioned {}
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos, true);
        new CommandLine(new Versioned()).printVersionHelp(ps, OFF, "VALUE1", "VALUE2", "VALUE3");
        String result = baos.toString();
        Assert.assertEquals(String.format("First line VALUE1%nSecond line VALUE2%nThird line VALUE1 VALUE2%n"), result);
    }

    @Test
    public void testCommandLine_printVersionInfo_withMarkupAndParameterContainingMarkup() {
        @Command(version = { "@|yellow Versioned Command 1.0|@", "@|blue Build 12345|@%1$s", "@|red,bg(white) (c) 2017|@%2$s" })
        class Versioned {}
        CommandLine commandLine = new CommandLine(new Versioned());
        verifyVersionWithMarkup(commandLine);
    }

    static class MarkupVersionProvider implements CommandLine.IVersionProvider {
        public String[] getVersion() {
            return new String[]{ "@|yellow Versioned Command 1.0|@", "@|blue Build 12345|@%1$s", "@|red,bg(white) (c) 2017|@%2$s" };
        }
    }

    @Test
    public void testCommandLine_printVersionInfo_fromAnnotation_withMarkupAndParameterContainingMarkup() {
        @Command(versionProvider = CommandLineHelpAnsiTest.MarkupVersionProvider.class)
        class Versioned {}
        CommandLine commandLine = new CommandLine(new Versioned());
        verifyVersionWithMarkup(commandLine);
    }

    @Test
    public void testCommandLine_printVersionInfo_usesProviderIfBothProviderAndStaticVersionInfoExist() {
        @Command(versionProvider = CommandLineHelpAnsiTest.MarkupVersionProvider.class, version = "static version is ignored")
        class Versioned {}
        CommandLine commandLine = new CommandLine(new Versioned());
        verifyVersionWithMarkup(commandLine);
    }

    @Test
    public void testAnsiIsWindowsDependsOnSystemProperty() {
        System.setProperty("os.name", "MMIX");
        Assert.assertFalse(Ansi.isWindows());
        System.setProperty("os.name", "Windows");
        Assert.assertTrue(Ansi.isWindows());
        System.setProperty("os.name", "Windows 10 build 12345");
        Assert.assertTrue(Ansi.isWindows());
    }

    @Test
    public void testAnsiIsXtermDependsOnEnvironmentVariable() {
        environmentVariables.clear(CommandLineHelpAnsiTest.ANSI_ENVIRONMENT_VARIABLES);
        Assert.assertFalse(Ansi.isXterm());
        environmentVariables.set("TERM", "random value");
        Assert.assertFalse(Ansi.isXterm());
        environmentVariables.set("TERM", "xterm");
        Assert.assertTrue(Ansi.isXterm());
        environmentVariables.set("TERM", "xterm asfasfasf");
        Assert.assertTrue(Ansi.isXterm());
    }

    @Test
    public void testAnsiHasOstypeDependsOnEnvironmentVariable() {
        environmentVariables.clear(CommandLineHelpAnsiTest.ANSI_ENVIRONMENT_VARIABLES);
        Assert.assertFalse(Ansi.hasOsType());
        environmentVariables.set("OSTYPE", "");
        Assert.assertTrue(Ansi.hasOsType());
        environmentVariables.set("OSTYPE", "42");
        Assert.assertTrue(Ansi.hasOsType());
    }

    @Test
    public void testAnsiIsPseudoTtyDependsOnWindowsXtermOrOsType() {
        System.setProperty("os.name", "MMIX");
        environmentVariables.clear(CommandLineHelpAnsiTest.ANSI_ENVIRONMENT_VARIABLES);
        Assert.assertFalse("OSTYPE and XTERM are not set", Ansi.isPseudoTTY());
        System.setProperty("os.name", "Windows 10 build 12345");
        environmentVariables.set("OSTYPE", "222");
        environmentVariables.set("TERM", "xterm");
        Assert.assertTrue(Ansi.isPseudoTTY());
        System.setProperty("os.name", "MMIX");
        Assert.assertFalse("Not Windows", Ansi.isPseudoTTY());
        System.setProperty("os.name", "Windows 10 build 12345");// restore

        Assert.assertTrue("restored", Ansi.isPseudoTTY());
        environmentVariables.clear("OSTYPE");
        Assert.assertTrue("Missing OSTYPE, but TERM=xterm", Ansi.isPseudoTTY());
        environmentVariables.set("OSTYPE", "anything");
        Assert.assertTrue("restored", Ansi.isPseudoTTY());
        environmentVariables.clear("XTERM");
        Assert.assertTrue("Missing XTERM, but OSTYPE defined", Ansi.isPseudoTTY());
    }

    @Test
    public void testAnsiHintDisabledTrueIfCLICOLORZero() {
        environmentVariables.clear(CommandLineHelpAnsiTest.ANSI_ENVIRONMENT_VARIABLES);
        Assert.assertFalse("no env vars set", Ansi.hintDisabled());
        environmentVariables.set("CLICOLOR", "");
        Assert.assertFalse("Just defining CLICOLOR is not enough", Ansi.hintDisabled());
        environmentVariables.set("CLICOLOR", "1");
        Assert.assertFalse("CLICOLOR=1 is not enough", Ansi.hintDisabled());
        environmentVariables.set("CLICOLOR", "false");
        Assert.assertFalse("CLICOLOR=false is not enough", Ansi.hintDisabled());
        environmentVariables.set("CLICOLOR", "0");
        Assert.assertTrue("CLICOLOR=0 disables", Ansi.hintDisabled());
    }

    @Test
    public void testAnsiHintDisabledTrueIfConEmuANSIisOFF() {
        environmentVariables.clear(CommandLineHelpAnsiTest.ANSI_ENVIRONMENT_VARIABLES);
        Assert.assertFalse("no env vars set", Ansi.hintDisabled());
        environmentVariables.set("ConEmuANSI", "");
        Assert.assertFalse("Just defining ConEmuANSI is not enough", Ansi.hintDisabled());
        environmentVariables.set("ConEmuANSI", "0");
        Assert.assertFalse("ConEmuANSI=0 is not enough", Ansi.hintDisabled());
        environmentVariables.set("ConEmuANSI", "false");
        Assert.assertFalse("ConEmuANSI=false is not enough", Ansi.hintDisabled());
        environmentVariables.set("ConEmuANSI", "off");
        Assert.assertFalse("ConEmuANSI=off does not disable", Ansi.hintDisabled());
        environmentVariables.set("ConEmuANSI", "Off");
        Assert.assertFalse("ConEmuANSI=Off does not disable", Ansi.hintDisabled());
        environmentVariables.set("ConEmuANSI", "OFF");
        Assert.assertTrue("ConEmuANSI=OFF disables", Ansi.hintDisabled());
    }

    @Test
    public void testAnsiHintEnbledTrueIfANSICONDefined() {
        environmentVariables.clear(CommandLineHelpAnsiTest.ANSI_ENVIRONMENT_VARIABLES);
        Assert.assertFalse("no env vars set", Ansi.hintEnabled());
        environmentVariables.set("ANSICON", "");
        Assert.assertTrue("ANSICON defined without value", Ansi.hintEnabled());
        environmentVariables.set("ANSICON", "abc");
        Assert.assertTrue("ANSICON defined any value", Ansi.hintEnabled());
    }

    @Test
    public void testAnsiHintEnbledTrueIfCLICOLOROne() {
        environmentVariables.clear(CommandLineHelpAnsiTest.ANSI_ENVIRONMENT_VARIABLES);
        Assert.assertFalse("no env vars set", Ansi.hintEnabled());
        environmentVariables.set("CLICOLOR", "");
        Assert.assertFalse("Just defining CLICOLOR is not enough", Ansi.hintEnabled());
        environmentVariables.set("CLICOLOR", "0");
        Assert.assertFalse("CLICOLOR=0 is not enough", Ansi.hintEnabled());
        environmentVariables.set("CLICOLOR", "true");
        Assert.assertFalse("CLICOLOR=true is not enough", Ansi.hintEnabled());
        environmentVariables.set("CLICOLOR", "1");
        Assert.assertTrue("CLICOLOR=1 enables", Ansi.hintEnabled());
    }

    @Test
    public void testAnsiHintEnabledTrueIfConEmuANSIisON() {
        environmentVariables.clear(CommandLineHelpAnsiTest.ANSI_ENVIRONMENT_VARIABLES);
        Assert.assertFalse("no env vars set", Ansi.hintEnabled());
        environmentVariables.set("ConEmuANSI", "");
        Assert.assertFalse("Just defining ConEmuANSI is not enough", Ansi.hintEnabled());
        environmentVariables.set("ConEmuANSI", "1");
        Assert.assertFalse("ConEmuANSI=1 is not enough", Ansi.hintEnabled());
        environmentVariables.set("ConEmuANSI", "true");
        Assert.assertFalse("ConEmuANSI=true is not enough", Ansi.hintEnabled());
        environmentVariables.set("ConEmuANSI", "on");
        Assert.assertFalse("ConEmuANSI=on does not enables", Ansi.hintEnabled());
        environmentVariables.set("ConEmuANSI", "On");
        Assert.assertFalse("ConEmuANSI=On does not enables", Ansi.hintEnabled());
        environmentVariables.set("ConEmuANSI", "ON");
        Assert.assertTrue("ConEmuANSI=ON enables", Ansi.hintEnabled());
    }

    @Test
    public void testAnsiForceEnabledTrueIfCLICOLOR_FORCEisDefinedAndNonZero() {
        environmentVariables.clear(CommandLineHelpAnsiTest.ANSI_ENVIRONMENT_VARIABLES);
        Assert.assertFalse("no env vars set", Ansi.forceEnabled());
        environmentVariables.set("CLICOLOR_FORCE", "");
        Assert.assertTrue("Just defining CLICOLOR_FORCE is enough", Ansi.forceEnabled());
        environmentVariables.set("CLICOLOR_FORCE", "1");
        Assert.assertTrue("CLICOLOR_FORCE=1 is enough", Ansi.forceEnabled());
        environmentVariables.set("CLICOLOR_FORCE", "0");
        Assert.assertFalse("CLICOLOR_FORCE=0 is not forced", Ansi.forceEnabled());
    }

    @Test
    public void testAnsiForceDisabledTrueIfNO_COLORDefined() {
        environmentVariables.clear(CommandLineHelpAnsiTest.ANSI_ENVIRONMENT_VARIABLES);
        Assert.assertFalse("no env vars set", Ansi.forceDisabled());
        environmentVariables.set("NO_COLOR", "");
        Assert.assertTrue("NO_COLOR defined without value", Ansi.forceDisabled());
        environmentVariables.set("NO_COLOR", "abc");
        Assert.assertTrue("NO_COLOR defined any value", Ansi.forceDisabled());
    }

    @Test
    public void testAnsiOnEnabled() {
        Assert.assertTrue(ON.enabled());
    }

    @Test
    public void testAnsiOffDisabled() {
        Assert.assertFalse(OFF.enabled());
    }

    @Test
    public void testAnsiAutoIfSystemPropertyPicocliAnsiCleared() {
        environmentVariables.set("CLICOLOR_FORCE", "1");
        System.clearProperty("picocli.ansi");
        Assert.assertTrue(AUTO.enabled());
    }

    @Test
    public void testAnsiAutoIfSystemPropertyPicocliAnsiIsAuto() {
        environmentVariables.set("CLICOLOR_FORCE", "1");
        System.setProperty("picocli.ansi", "auto");
        Assert.assertTrue(AUTO.enabled());
        System.setProperty("picocli.ansi", "Auto");
        Assert.assertTrue(AUTO.enabled());
        System.setProperty("picocli.ansi", "AUTO");
        Assert.assertTrue(AUTO.enabled());
    }

    @Test
    public void testAnsiOffIfSystemPropertyPicocliAnsiIsNotAuto() {
        System.setProperty("picocli.ansi", "auto1");
        environmentVariables.set("CLICOLOR_FORCE", "1");
        Assert.assertFalse(AUTO.enabled());
    }

    @Test
    public void testAnsiAutoForceDisabledOverridesForceEnabled() {
        environmentVariables.clear(CommandLineHelpAnsiTest.ANSI_ENVIRONMENT_VARIABLES);
        environmentVariables.set("NO_COLOR", "");
        environmentVariables.set("CLICOLOR_FORCE", "1");
        Assert.assertTrue(Ansi.forceDisabled());
        Assert.assertTrue(Ansi.forceEnabled());
        Assert.assertFalse(Ansi.hintDisabled());
        Assert.assertFalse(Ansi.hintEnabled());
        Assert.assertFalse("forceDisabled overrides forceEnabled", AUTO.enabled());
    }

    @Test
    public void testAnsiAutoForceDisabledOverridesHintEnabled() {
        environmentVariables.clear(CommandLineHelpAnsiTest.ANSI_ENVIRONMENT_VARIABLES);
        environmentVariables.set("NO_COLOR", "");
        environmentVariables.set("CLICOLOR", "1");
        Assert.assertTrue(Ansi.forceDisabled());
        Assert.assertFalse(Ansi.forceEnabled());
        Assert.assertFalse(Ansi.hintDisabled());
        Assert.assertTrue(Ansi.hintEnabled());
        Assert.assertFalse("forceDisabled overrides hintEnabled", AUTO.enabled());
    }

    @Test
    public void testAnsiAutoForcedEnabledOverridesHintDisabled() {
        environmentVariables.clear(CommandLineHelpAnsiTest.ANSI_ENVIRONMENT_VARIABLES);
        environmentVariables.set("CLICOLOR", "0");
        environmentVariables.set("CLICOLOR_FORCE", "1");
        Assert.assertFalse(Ansi.forceDisabled());
        Assert.assertTrue(Ansi.hintDisabled());
        Assert.assertTrue(Ansi.forceEnabled());
        Assert.assertFalse(Ansi.hintEnabled());
        Assert.assertTrue("forceEnabled overrides hintDisabled", AUTO.enabled());
        environmentVariables.clear(CommandLineHelpAnsiTest.ANSI_ENVIRONMENT_VARIABLES);
        environmentVariables.set("ConEmuANSI", "OFF");
        environmentVariables.set("CLICOLOR_FORCE", "1");
        Assert.assertFalse(Ansi.forceDisabled());
        Assert.assertTrue(Ansi.hintDisabled());
        Assert.assertTrue(Ansi.forceEnabled());
        Assert.assertFalse(Ansi.hintEnabled());
        Assert.assertTrue("forceEnabled overrides hintDisabled 2", AUTO.enabled());
    }

    @Test
    public void testAnsiAutoJansiConsoleInstalledOverridesHintDisabled() {
        environmentVariables.clear(CommandLineHelpAnsiTest.ANSI_ENVIRONMENT_VARIABLES);
        environmentVariables.set("CLICOLOR", "0");// hint disabled

        System.setProperty("os.name", "Windows");
        Assert.assertTrue(Ansi.isWindows());
        Assert.assertFalse(Ansi.isPseudoTTY());
        Assert.assertFalse(Ansi.forceDisabled());
        Assert.assertFalse(Ansi.forceEnabled());
        Assert.assertTrue(Ansi.hintDisabled());
        Assert.assertFalse(Ansi.hintEnabled());
        Assert.assertFalse(Ansi.isJansiConsoleInstalled());
        AnsiConsole.systemInstall();
        try {
            Assert.assertTrue(Ansi.isJansiConsoleInstalled());
            Assert.assertTrue(AUTO.enabled());
        } finally {
            AnsiConsole.systemUninstall();
        }
    }

    @Test
    public void testAnsiAutoHintDisabledOverridesHintEnabled() {
        environmentVariables.clear(CommandLineHelpAnsiTest.ANSI_ENVIRONMENT_VARIABLES);
        environmentVariables.set("CLICOLOR", "0");// hint disabled

        environmentVariables.set("ANSICON", "1");// hint enabled

        System.setProperty("os.name", "Windows");
        Assert.assertTrue(Ansi.isWindows());
        environmentVariables.set("TERM", "xterm");// fake Cygwin

        Assert.assertTrue(Ansi.isPseudoTTY());
        Assert.assertFalse(Ansi.isJansiConsoleInstalled());
        Assert.assertFalse(Ansi.forceDisabled());
        Assert.assertFalse(Ansi.forceEnabled());
        Assert.assertTrue(Ansi.hintDisabled());
        Assert.assertTrue(Ansi.hintEnabled());
        Assert.assertFalse("Disabled overrides enabled", AUTO.enabled());
    }

    @Test
    public void testAnsiAutoDisabledIfNoTty() {
        if (Ansi.isTTY()) {
            return;
        }// 

        environmentVariables.clear(CommandLineHelpAnsiTest.ANSI_ENVIRONMENT_VARIABLES);
        System.setProperty("os.name", "Windows");
        Assert.assertTrue(Ansi.isWindows());
        Assert.assertFalse(Ansi.isPseudoTTY());
        Assert.assertFalse(Ansi.isJansiConsoleInstalled());
        Assert.assertFalse(Ansi.forceDisabled());
        Assert.assertFalse(Ansi.forceEnabled());
        Assert.assertFalse(Ansi.hintDisabled());
        Assert.assertFalse(Ansi.hintEnabled());
        Assert.assertFalse("Must have TTY if no JAnsi", AUTO.enabled());
    }

    @Test
    public void testAnsiAutoEnabledIfNotWindows() {
        if (!(Ansi.isTTY())) {
            return;
        }// needs TTY for this test

        environmentVariables.clear(CommandLineHelpAnsiTest.ANSI_ENVIRONMENT_VARIABLES);
        System.setProperty("os.name", "MMIX");
        Assert.assertFalse(Ansi.isWindows());
        Assert.assertFalse(Ansi.isPseudoTTY());// TODO Mock this?

        Assert.assertFalse(Ansi.isJansiConsoleInstalled());
        Assert.assertFalse(Ansi.forceDisabled());
        Assert.assertFalse(Ansi.forceEnabled());
        Assert.assertFalse(Ansi.hintDisabled());
        Assert.assertFalse(Ansi.hintEnabled());
        Assert.assertTrue("If have TTY, enabled on non-Windows", AUTO.enabled());
    }

    @Test
    public void testAnsiAutoEnabledIfWindowsPseudoTTY() {
        environmentVariables.clear(CommandLineHelpAnsiTest.ANSI_ENVIRONMENT_VARIABLES);
        System.setProperty("os.name", "Windows");
        Assert.assertTrue(Ansi.isWindows());
        Assert.assertFalse(Ansi.isJansiConsoleInstalled());
        Assert.assertFalse(Ansi.forceDisabled());
        Assert.assertFalse(Ansi.forceEnabled());
        Assert.assertFalse(Ansi.hintDisabled());
        Assert.assertFalse(Ansi.hintEnabled());
        environmentVariables.set("TERM", "xterm");
        Assert.assertTrue(Ansi.isPseudoTTY());
        Assert.assertTrue("If have Cygwin pseudo-TTY, enabled on Windows", AUTO.enabled());
        environmentVariables.clear(CommandLineHelpAnsiTest.ANSI_ENVIRONMENT_VARIABLES);
        environmentVariables.set("OSTYPE", "Windows");
        Assert.assertTrue(Ansi.isPseudoTTY());
        Assert.assertTrue("If have MSYS pseudo-TTY, enabled on Windows", AUTO.enabled());
    }
}

