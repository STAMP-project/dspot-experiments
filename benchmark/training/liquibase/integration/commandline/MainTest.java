package liquibase.integration.commandline;


import SnapshotCommand.SnapshotCommandResult;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.StringReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import liquibase.command.CommandFactory;
import liquibase.command.core.SnapshotCommand;
import liquibase.configuration.GlobalConfiguration;
import liquibase.configuration.LiquibaseConfiguration;
import liquibase.exception.CommandLineParsingException;
import liquibase.util.StringUtil;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 * Tests for {@link Main}
 */
// PowerMockito tends to choke on these, and we do not really need to mock them anyway:
@RunWith(PowerMockRunner.class)
@PowerMockIgnore({ "javax.xml.*", "org.xml.sax.*", "org.w3c.dom.*", "org.springframework.context.*", "org.apache.log4j" + ".*" })
@PrepareForTest({ Main.class, CommandFactory.class })
public class MainTest {
    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();

    @Mock
    private CommandFactory commandFactory;

    @Mock
    private SnapshotCommand snapshotCommand;

    @Mock
    private SnapshotCommandResult snapshotCommandResult;

    public MainTest() throws Exception {
        PowerMockito.mockStatic(CommandFactory.class);
        commandFactory = PowerMockito.mock(CommandFactory.class);
        snapshotCommand = PowerMockito.mock(SnapshotCommand.class);
        snapshotCommandResult = PowerMockito.mock(SnapshotCommandResult.class);
        // Do not do actual database snapshots.
        when(CommandFactory.getInstance()).thenReturn(commandFactory);
        when(commandFactory.getCommand("snapshot")).thenReturn(snapshotCommand);
        when(snapshotCommand.execute()).thenReturn(snapshotCommandResult);
        when(snapshotCommandResult.print()).thenReturn("<?xml version=\"1.0\" encoding=\"UTF-8\"?>...");
        // This one is not so much for JUnit, but for people working with IntelliJ. It seems that IntelliJ's
        // test runner can get confused badly if tests open an OutputStreamWriter in STDOUT.
        PowerMockito.stub(method(Main.class, "getOutputWriter")).toReturn(new OutputStreamWriter(System.err));
    }

    @Test
    public void testLocalProperties() throws Exception {
        String[] args = new String[]{ "--driver=DRIVER", "--username=USERNAME", "--password=PASSWORD", "--url=offline:mock?version=1.20&productName=SuperDuperDatabase&catalog=startCatalog" + ("&caseSensitive=true&changeLogFile=liquibase/database/simpleChangeLog.xml" + "&sendsStringParametersAsUnicode=true"), "--changeLogFile=dummy.log", "--changeExecListenerClass=MockChangeExecListener", "--defaultsFile=target/test-classes/liquibase.properties", "snapshot" };
        Main cli = new Main();
        cli.parseOptions(args);
        Assert.assertTrue("Read context from liquibase.local.properties", (((cli.contexts) != null) && (cli.contexts.contains("local-context-for-liquibase-unit-tests"))));
        Assert.assertTrue("Read context from liquibase.properties", (((cli.logFile) != null) && (("target" + "/logfile_set_from_liquibase_properties.log").equals(cli.logFile))));
    }

    @Test
    public void startWithoutParameters() {
        exit.expectSystemExitWithStatus(1);
        Main.main(new String[0]);
        Assert.assertTrue("We just want to survive until this point", true);
    }

    @Test
    public void globalConfigurationSaysDoNotRun() throws Exception {
        LiquibaseConfiguration.getInstance().getConfiguration(GlobalConfiguration.class).setValue("shouldRun", false);
        int errorLevel = Main.run(new String[0]);
        LiquibaseConfiguration.getInstance().getConfiguration(GlobalConfiguration.class).setValue("shouldRun", true);
        Assert.assertEquals(errorLevel, 0);// If it SHOULD run, and we would call without parameters, we would get -1

    }

    @Test
    public void mockedSnapshotRun() throws Exception {
        String[] args = new String[]{ "--driver=DRIVER", "--username=USERNAME", "--password=PASSWORD", "--url=offline:mock?version=1.20&productName=SuperDuperDatabase&catalog=startCatalog" + ("&caseSensitive=true&changeLogFile=liquibase/database/simpleChangeLog.xml" + "&sendsStringParametersAsUnicode=true"), "--changeLogFile=dummy.log", "--changeExecListenerClass=MockChangeExecListener", "snapshot" };
        int errorLevel = Main.run(args);
        Assert.assertEquals(0, errorLevel);
    }

    @Test
    public void localPropertyFiles() throws Exception {
        String[] args = new String[]{ "--driver=DRIVER", "--username=USERNAME", "--password=PASSWORD", "--url=offline:mock?version=1.20&productName=SuperDuperDatabase&catalog=startCatalog" + ("&caseSensitive=true&changeLogFile=liquibase/database/simpleChangeLog.xml" + "&sendsStringParametersAsUnicode=true"), "--changeLogFile=dummy.log", "--changeExecListenerClass=MockChangeExecListener", "snapshot" };
        int errorLevel = Main.run(args);
        Assert.assertEquals(0, errorLevel);
    }

    @Test
    public void migrateWithAllParameters() throws Exception {
        String[] args = new String[]{ "--driver=DRIVER", "--username=USERNAME", "--password=PASSWORD", "--url=URL", "--changeLogFile=FILE", "--classpath=CLASSPATH;CLASSPATH2", "--contexts=CONTEXT1,CONTEXT2", "--promptForNonLocalDatabase=true", "--changeExecListenerClass=MockChangeExecListener", "--changeExecListenerPropertiesFile=PROPS", "update" };
        Main cli = new Main();
        cli.parseOptions(args);
        Assert.assertEquals("Option --driver was parsed correctly", "DRIVER", cli.driver);
        Assert.assertEquals("Option --username was parsed correctly", "USERNAME", cli.username);
        Assert.assertEquals("Option --password was parsed correctly", "PASSWORD", cli.password);
        Assert.assertEquals("Option --url was parsed correctly", "URL", cli.url);
        Assert.assertEquals("Option --changeLogFile was parsed correctly", "FILE", cli.changeLogFile);
        Assert.assertEquals("Option --classpath was parsed correctly", "CLASSPATH;CLASSPATH2", cli.classpath);
        Assert.assertEquals("Option --contexts was parsed correctly", "CONTEXT1,CONTEXT2", cli.contexts);
        Assert.assertEquals("Option --promptForNonLocalDatabase was parsed correctly", Boolean.TRUE, cli.promptForNonLocalDatabase);
        Assert.assertEquals("Main command 'update' was parsed correctly", "update", cli.command);
        Assert.assertEquals("Option --changeExecListenerClass was parsed correctly", "MockChangeExecListener", cli.changeExecListenerClass);
        Assert.assertEquals("Option --changeExecListenerPropertiesFile was parsed correctly", "PROPS", cli.changeExecListenerPropertiesFile);
    }

    @Test
    public void falseBooleanParameters() throws Exception {
        String[] args = new String[]{ "--promptForNonLocalDatabase=false", "update" };
        Main cli = new Main();
        cli.parseOptions(args);
        Assert.assertEquals("Option --promptForNonLocalDatabase=false was parsed correctly", Boolean.FALSE, cli.promptForNonLocalDatabase);
        Assert.assertEquals("Main command 'update' was parsed correctly", "update", cli.command);
    }

    @Test
    public void convertMigrateToUpdate() throws Exception {
        String[] args = new String[]{ "--promptForNonLocalDatabase=false", "migrate" };
        Main cli = new Main();
        cli.parseOptions(args);
        Assert.assertEquals("Option --promptForNonLocalDatabase was parsed correctly", Boolean.FALSE, cli.promptForNonLocalDatabase);
        Assert.assertEquals("Main command 'migrate' was parsed correctly as 'update'", "update", cli.command);
    }

    @Test
    public void trueBooleanParameters() throws Exception {
        String[] args = new String[]{ "--promptForNonLocalDatabase=true", "update" };
        Main cli = new Main();
        cli.parseOptions(args);
        Assert.assertEquals("Option --promptForNonLocalDatabase=true was parsed correctly", Boolean.TRUE, cli.promptForNonLocalDatabase);
        Assert.assertEquals("Main command 'update' was parsed correctly", "update", cli.command);
    }

    @Test(expected = CommandLineParsingException.class)
    public void parameterWithoutDash() throws Exception {
        String[] args = new String[]{ "promptForNonLocalDatabase=true", "update" };
        Main cli = new Main();
        cli.parseOptions(args);
    }

    @Test
    public void emptyUrlParameter() throws Exception {
        String[] args = new String[]{ "--changeLogFile=FILE", "--url=", "update" };
        Main cli = new Main();
        cli.parseOptions(args);
        List<String> errMsgs = cli.checkSetup();
        Assert.assertEquals("specifying an empty URL should return 1 error message.", 1, errMsgs.size());
    }

    @Test
    public void misplacedDiffTypesDataOption() throws Exception {
        String[] args = new String[]{ "--changeLogFile=FILE", "--url=TESTFILE", "diffChangeLog", "--diffTypes=data" };
        Main cli = new Main();
        cli.parseOptions(args);
        List<String> errMsgs = cli.checkSetup();
        Assert.assertEquals("the combination of --diffTypes=data and diffChangeLog must not be accepted.", 1, errMsgs.size());
    }

    @Test(expected = CommandLineParsingException.class)
    public void unknownParameter() throws Exception {
        String[] args = new String[]{ "--promptForNonLocalDatabase=true", "--badParam=here", "migrate" };
        Main cli = new Main();
        cli.parseOptions(args);
    }

    @Test
    public void statusVerbose() throws Exception {
        String[] args = new String[]{ "--url=URL", "--changeLogFile=FILE", "status", "--verbose" };
        Main cli = new Main();
        cli.parseOptions(args);
        Assert.assertEquals("Main command 'status' was not correctly parsed", "status", cli.command);
        List<String> errMsgs = cli.checkSetup();
        Assert.assertEquals(0, errMsgs.size());// verbose option parsed correctly

    }

    @Test
    public void statusVerboseWithValue() throws Exception {
        String[] args = new String[]{ "--url=URL", "--changeLogFile=FILE", "status", "--verbose=true" };
        Main cli = new Main();
        cli.parseOptions(args);
        Assert.assertEquals("Main command 'status' was not correctly parsed", "status", cli.command);
        List<String> errMsgs = cli.checkSetup();
        Assert.assertEquals(1, errMsgs.size());// value is not expected and will raise an error message

    }

    @Test
    public void statusWithoutVerbose() throws Exception {
        String[] args = new String[]{ "--url=URL", "--changeLogFile=FILE", "status" };
        Main cli = new Main();
        cli.parseOptions(args);
        Assert.assertEquals("Main command 'status' was not correctly parsed", "status", cli.command);
        List<String> errMsgs = cli.checkSetup();
        Assert.assertEquals(0, errMsgs.size());
    }

    @Test(expected = CommandLineParsingException.class)
    public void configureNonExistantClassloaderLocation() throws Exception {
        Main cli = new Main();
        cli.classpath = "badClasspathLocation";
        cli.configureClassLoader();
    }

    @Test
    public void windowsConfigureClassLoaderLocation() throws Exception {
        Main cli = new Main();
        if (cli.isWindows()) {
            System.setProperty("os.name", "Windows XP");
            cli.classpath = "c:\\;c:\\windows\\";
            cli.applyDefaults();
            cli.configureClassLoader();
            URL[] classloaderURLs = ((URLClassLoader) (cli.classLoader)).getURLs();
            Assert.assertEquals("Parsing example Windows classpath returns 2 entries", 2, classloaderURLs.length);
            Assert.assertEquals("Windows path C:\\ is correctly parsed", "file:/c:/", classloaderURLs[0].toExternalForm());
            Assert.assertEquals("Windows path C:\\windows\\ is correctly parsed", "file:/c:/windows/", classloaderURLs[1].toExternalForm());
        }
    }

    @Test
    public void unixConfigureClassLoaderLocation() throws Exception {
        Main cli = new Main();
        if (!(cli.isWindows())) {
            System.setProperty("os.name", "Linux");
            cli.classpath = "/tmp:/";
            cli.applyDefaults();
            cli.configureClassLoader();
            URL[] classloaderURLs = ((URLClassLoader) (cli.classLoader)).getURLs();
            Assert.assertEquals(2, classloaderURLs.length);
            Assert.assertEquals("file:/tmp/", classloaderURLs[0].toExternalForm());
            Assert.assertEquals("file:/", classloaderURLs[1].toExternalForm());
        }
    }

    @Test
    public void propertiesFileWithNoOtherArgs() throws Exception {
        Main cli = new Main();
        Properties props = new Properties();
        props.setProperty("driver", "DRIVER");
        props.setProperty("username", "USERNAME");
        props.setProperty("password", "PASSWD");
        props.setProperty("url", "URL");
        props.setProperty("changeLogFile", "FILE");
        props.setProperty("classpath", "CLASSPAHT");
        props.setProperty("contexts", "CONTEXTS");
        props.setProperty("promptForNonLocalDatabase", "TRUE");
        ByteArrayOutputStream propFile = new ByteArrayOutputStream();
        props.store(propFile, "");
        cli.parsePropertiesFile(new ByteArrayInputStream(propFile.toByteArray()));
        Assert.assertEquals("DRIVER", cli.driver);
        Assert.assertEquals("USERNAME", cli.username);
        Assert.assertEquals("PASSWD", cli.password);
        Assert.assertEquals("URL", cli.url);
        Assert.assertEquals("FILE", cli.changeLogFile);
        Assert.assertEquals("CLASSPAHT", cli.classpath);
        Assert.assertEquals("CONTEXTS", cli.contexts);
        Assert.assertEquals(Boolean.TRUE, cli.promptForNonLocalDatabase);
    }

    @Test
    public void propertiesFileWithOtherArgs() throws Exception {
        Main cli = new Main();
        cli.username = "PASSED USERNAME";
        cli.password = "PASSED PASSWD";
        Properties props = new Properties();
        props.setProperty("driver", "DRIVER");
        props.setProperty("username", "USERNAME");
        props.setProperty("password", "PASSWD");
        props.setProperty("url", "URL");
        props.setProperty("changeLogFile", "FILE");
        props.setProperty("classpath", "CLASSPAHT");
        props.setProperty("contexts", "CONTEXTS");
        props.setProperty("promptForNonLocalDatabase", "TRUE");
        ByteArrayOutputStream propFile = new ByteArrayOutputStream();
        props.store(propFile, "");
        cli.parsePropertiesFile(new ByteArrayInputStream(propFile.toByteArray()));
        Assert.assertEquals("DRIVER", cli.driver);
        Assert.assertEquals("PASSED USERNAME", cli.username);
        Assert.assertEquals("PASSED PASSWD", cli.password);
        Assert.assertEquals("URL", cli.url);
        Assert.assertEquals("FILE", cli.changeLogFile);
        Assert.assertEquals("CLASSPAHT", cli.classpath);
        Assert.assertEquals("CONTEXTS", cli.contexts);
        Assert.assertEquals(Boolean.TRUE, cli.promptForNonLocalDatabase);
    }

    @Test
    public void propertiesFileParsingShouldIgnoreUnknownArgumentsIfStrictFalseIsInFile() throws Exception {
        Main cli = new Main();
        Properties props = new Properties();
        props.setProperty("driver", "DRIVER");
        props.setProperty("unknown.property", "UnknownValue");
        props.setProperty("strict", "false");
        ByteArrayOutputStream propFile = new ByteArrayOutputStream();
        props.store(propFile, "");
        cli.parsePropertiesFile(new ByteArrayInputStream(propFile.toByteArray()));
        Assert.assertEquals("DRIVER", cli.driver);
    }

    @Test
    public void propertiesFileChangeLogParameters() throws Exception {
        Main cli = new Main();
        Properties props = new Properties();
        props.setProperty("driver", "DRIVER");
        props.setProperty("parameter.some_changelog_parameter", "parameterValue");
        ByteArrayOutputStream propFile = new ByteArrayOutputStream();
        props.store(propFile, "");
        cli.parsePropertiesFile(new ByteArrayInputStream(propFile.toByteArray()));
        Assert.assertEquals("Changelog parameter in properties file is recognized", "parameterValue", cli.changeLogParameters.get("some_changelog_parameter"));
    }

    @Test
    public void propertiesFileParsingShouldIgnoreUnknownArgumentsIfStrictModeIsFalse() throws Exception {
        Main cli = new Main();
        String[] args = new String[]{ "--strict=false" };
        cli.parseOptions(args);
        Properties props = new Properties();
        props.setProperty("driver", "DRIVER");
        props.setProperty("unknown.property", "UnknownValue");
        ByteArrayOutputStream propFile = new ByteArrayOutputStream();
        props.store(propFile, "");
        cli.parsePropertiesFile(new ByteArrayInputStream(propFile.toByteArray()));
        Assert.assertEquals("DRIVER", cli.driver);
    }

    @Test(expected = CommandLineParsingException.class)
    public void propertiesFileParsingShouldFailOnUnknownArgumentsIfStrictMode() throws Exception {
        Main cli = new Main();
        Properties props = new Properties();
        props.setProperty("driver", "DRIVER");
        props.setProperty("unknown.property", "UnknownValue");
        props.setProperty("strict", "true");
        ByteArrayOutputStream propFile = new ByteArrayOutputStream();
        props.store(propFile, "");
        cli.parsePropertiesFile(new ByteArrayInputStream(propFile.toByteArray()));
    }

    @Test
    public void applyDefaults() {
        Main cli = new Main();
        cli.promptForNonLocalDatabase = Boolean.TRUE;
        cli.applyDefaults();
        Assert.assertEquals("Correct default value for --promptForNonLocalDatabase", Boolean.TRUE, cli.promptForNonLocalDatabase);
        cli.promptForNonLocalDatabase = Boolean.FALSE;
        cli.applyDefaults();
        Assert.assertEquals("Correct default value for --promptForNonLocalDatabase", Boolean.FALSE, cli.promptForNonLocalDatabase);
        cli.promptForNonLocalDatabase = null;
        cli.applyDefaults();
        Assert.assertEquals("Correct default value for --promptForNonLocalDatabase", Boolean.FALSE, cli.promptForNonLocalDatabase);
    }

    @Test(expected = CommandLineParsingException.class)
    public void propertiesFileWithBadArgs() throws Exception {
        Main cli = new Main();
        Properties props = new Properties();
        props.setProperty("driver", "DRIVER");
        props.setProperty("username", "USERNAME");
        props.setProperty("badArg", "ARG");
        ByteArrayOutputStream propFile = new ByteArrayOutputStream();
        props.store(propFile, "");
        cli.parsePropertiesFile(new ByteArrayInputStream(propFile.toByteArray()));
    }

    @Test
    public void checkSetup() {
        Main cli = new Main();
        Assert.assertTrue((!(cli.checkSetup().isEmpty())));
        cli.driver = "driver";
        cli.username = "username";
        cli.password = "pwd";
        cli.url = "url";
        cli.changeLogFile = "file";
        cli.classpath = "classpath";
        Assert.assertTrue((!(cli.checkSetup().isEmpty())));
        cli.command = "BadCommand";
        Assert.assertTrue((!(cli.checkSetup().isEmpty())));
        cli.command = "migrate";
        Assert.assertEquals(0, cli.checkSetup().size());
        String[] noArgCommand = new String[]{ "migrate", "migrateSQL", "update", "updateSQL", "futureRollbackSQL", "updateTestingRollback", "listLocks", "dropAll", "releaseLocks", "validate", "help", "clearCheckSums", "changelogSync", "changelogSyncSQL", "markNextChangeSetRan", "markNextChangeSetRanSQL" };
        cli.commandParams.clear();
        cli.commandParams.add("--logLevel=debug");
        // verify unexpected parameter
        for (int i = 0; i < (noArgCommand.length); i++) {
            cli.command = noArgCommand[i];
            Assert.assertEquals(1, cli.checkSetup().size());
        }
        // test update cmd with -D parameter
        cli.command = "update";
        cli.commandParams.clear();
        cli.changeLogParameters.clear();
        cli.changeLogParameters.put("engine", "myisam");
        Assert.assertEquals(0, cli.checkSetup().size());
        // verify normal case - comand w/o command parameters
        cli.commandParams.clear();
        for (int i = 0; i < (noArgCommand.length); i++) {
            cli.command = noArgCommand[i];
            Assert.assertEquals(0, cli.checkSetup().size());
        }
        String[] singleArgCommand = new String[]{ "updateCount", "updateCountSQL", "tag", "dbDoc" };
        // verify unexpected parameter for single arg commands
        cli.commandParams.add("--logLevel=debug");
        for (int i = 0; i < (singleArgCommand.length); i++) {
            cli.command = singleArgCommand[i];
            Assert.assertEquals(1, cli.checkSetup().size());
        }
        // verify normal case - comand with string command parameter
        cli.commandParams.clear();
        cli.commandParams.add("someCommandValue");
        for (int i = 0; i < (singleArgCommand.length); i++) {
            cli.command = singleArgCommand[i];
            Assert.assertEquals(0, cli.checkSetup().size());
        }
        // status w/o parameter
        cli.command = "status";
        cli.commandParams.clear();
        Assert.assertEquals(0, cli.checkSetup().size());
        // status w/--verbose
        cli.commandParams.add("--verbose");
        Assert.assertEquals(0, cli.checkSetup().size());
        cli.commandParams.clear();
        cli.commandParams.add("--logLevel=debug");
        Assert.assertEquals(1, cli.checkSetup().size());
        String[] multiArgCommand = new String[]{ "diff", "diffChangeLog" };
        // first verify diff cmds w/o args
        cli.commandParams.clear();
        for (int i = 0; i < (multiArgCommand.length); i++) {
            cli.command = multiArgCommand[i];
            Assert.assertEquals(0, cli.checkSetup().size());
        }
        // next verify with all parms
        String[] cmdParms = new String[]{ "--referenceUsername=USERNAME", "--referencePassword=PASSWORD", "--referenceUrl=URL", "--referenceDriver=DRIVER" };
        // load all parms
        for (String param : cmdParms) {
            cli.commandParams.add(param);
        }
        Assert.assertEquals(0, cli.checkSetup().size());
        // now add an unexpected parm
        cli.commandParams.add("--logLevel=debug");
        Assert.assertEquals(1, cli.checkSetup().size());
    }

    @Test
    public void printHelp() throws Exception {
        final int MAXIMUM_LENGTH = 80;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Main cli = new Main();
        cli.printHelp(new PrintStream(stream));
        BufferedReader reader = new BufferedReader(new StringReader(new String(stream.toByteArray())));
        String line;
        while ((line = reader.readLine()) != null) {
            if ((line.length()) > MAXIMUM_LENGTH) {
                Assert.fail((("'" + line) + (String.format("' is longer than %d chars", MAXIMUM_LENGTH))));
            }
        } 
    }

    @Test
    public void tag() throws Exception {
        String[] args = new String[]{ "--driver=DRIVER", "--username=USERNAME", "--password=PASSWORD", "--url=URL", "--changeLogFile=FILE", "--classpath=CLASSPATH;CLASSPATH2", "--contexts=CONTEXT1,CONTEXT2", "--databaseChangeLogTablespaceName=MYTABLES", "tag", "TagHere" };
        Main cli = new Main();
        cli.parseOptions(args);
        Assert.assertEquals("Command line option --driver is parsed correctly", "DRIVER", cli.driver);
        Assert.assertEquals("Command line option --username is parsed correctly", "USERNAME", cli.username);
        Assert.assertEquals("Command line option --password is parsed correctly", "PASSWORD", cli.password);
        Assert.assertEquals("Command line option --url is parsed correctly", "URL", cli.url);
        Assert.assertEquals("Command line option --changeLogFile is parsed correctly", "FILE", cli.changeLogFile);
        Assert.assertEquals("Command line option --classpath is parsed correctly", "CLASSPATH;CLASSPATH2", cli.classpath);
        Assert.assertEquals("Command line option --contexts is parsed correctly", "CONTEXT1,CONTEXT2", cli.contexts);
        Assert.assertEquals("Command line option --databaseChangeLogTablespaceName is parsed correctly", "MYTABLES", cli.databaseChangeLogTablespaceName);
        Assert.assertEquals("Main command 'tag' is parsed correctly", "tag", cli.command);
        Assert.assertEquals("Command parameter 'TagHere' is parsed correctly", "TagHere", cli.commandParams.iterator().next());
    }

    @Test
    public void migrateWithEqualsInParams() throws Exception {
        String url = "dbc:sqlserver://127.0.0.1;DatabaseName=dev_nn;user=ffdatabase;password=p!88worD";
        String[] args = new String[]{ "--url=" + url, "migrate" };
        Main cli = new Main();
        cli.parseOptions(args);
        Assert.assertEquals(url, cli.url);
    }

    @Test
    public void fixArgs() {
        Main liquibase = new Main();
        String[] fixedArgs = liquibase.fixupArgs(new String[]{ "--defaultsFile", "liquibase.properties", "migrate" });
        Assert.assertEquals("--defaultsFile=liquibase.properties migrate", StringUtil.join(Arrays.asList(fixedArgs), " "));
        fixedArgs = liquibase.fixupArgs(new String[]{ "--defaultsFile=liquibase.properties", "migrate" });
        Assert.assertEquals("--defaultsFile=liquibase.properties migrate", StringUtil.join(Arrays.asList(fixedArgs), " "));
        fixedArgs = liquibase.fixupArgs(new String[]{ "--driver=DRIVER", "--username=USERNAME", "--password=PASSWORD", "--url=URL", "--changeLogFile=FILE", "--classpath=CLASSPATH;CLASSPATH2", "--contexts=CONTEXT1,CONTEXT2", "--promptForNonLocalDatabase=true", "migrate" });
        Assert.assertEquals(("--driver=DRIVER --username=USERNAME --password=PASSWORD --url=URL --changeLogFile=FILE " + ("--classpath=CLASSPATH;CLASSPATH2 --contexts=CONTEXT1,CONTEXT2 " + "--promptForNonLocalDatabase=true migrate")), StringUtil.join(Arrays.asList(fixedArgs), " "));
    }

    @Test
    public void testVersionArg() throws Exception {
        Main.run(new String[]{ "--version" });
        Assert.assertTrue(true);// Just want to test if the call goes through

    }

    @Test
    public void testSplitArgWithValueEndingByEqualSing() throws CommandLineParsingException {
        final String argName = "password";
        final String argValue = "s3-cr3t=";
        Main tested = new Main();
        tested.parseOptions(new String[]{ (("--" + argName) + "=") + argValue });
        Assert.assertEquals("Password containing an equal sign (=) is parsed correctly", argValue, tested.password);
    }

    @Test
    public void testDatabaseChangeLogTableName_Properties() throws IOException, CommandLineParsingException {
        Main main = new Main();
        Properties props = new Properties();
        props.setProperty("databaseChangeLogTableName", "PROPSCHANGELOG");
        props.setProperty("databaseChangeLogLockTableName", "PROPSCHANGELOGLOCK");
        ByteArrayOutputStream propFile = new ByteArrayOutputStream();
        props.store(propFile, "");
        main.parsePropertiesFile(new ByteArrayInputStream(propFile.toByteArray()));
        Assert.assertEquals("Custom database change log table gets parsed correctly (as a property)", "PROPSCHANGELOG", main.databaseChangeLogTableName);
        Assert.assertEquals("Custom database change log LOCK table gets parsed correctly (as a property)", "PROPSCHANGELOGLOCK", main.databaseChangeLogLockTableName);
    }

    @Test
    public void testDatabaseChangeLogTableName_Options() throws CommandLineParsingException {
        Main main = new Main();
        String[] opts = new String[]{ "--databaseChangeLogTableName=OPTSCHANGELOG", "--databaseChangeLogLockTableName=OPTSCHANGELOGLOCK" };
        main.parseOptions(opts);
        Assert.assertEquals("Custom database change log table gets parsed correctly (as an option argument)", "OPTSCHANGELOG", main.databaseChangeLogTableName);
        Assert.assertEquals("Custom database change log LOCK table gets parsed correctly (as an option argument)", "OPTSCHANGELOGLOCK", main.databaseChangeLogLockTableName);
    }
}

