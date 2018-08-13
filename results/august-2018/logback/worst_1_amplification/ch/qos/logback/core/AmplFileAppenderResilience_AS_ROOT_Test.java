package ch.qos.logback.core;


import ch.qos.logback.core.encoder.EchoEncoder;
import ch.qos.logback.core.testUtil.EnvUtilForTests;
import ch.qos.logback.core.testUtil.RandomUtil;
import ch.qos.logback.core.util.StatusPrinter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.junit.After;
import org.junit.Before;


public class AmplFileAppenderResilience_AS_ROOT_Test {
    static String MOUNT_POINT = "/mnt/loop/";

    static String LONG_STR = " xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";

    static String PATH_LOOPFS_SCRIPT = "/home/ceki/java/logback/logback-core/src/test/loopfs.sh";

    enum LoopFSCommand {

        setup,
        shake,
        teardown;}

    Context context = new ContextBase();

    int diff = RandomUtil.getPositiveInt();

    String outputDirStr = (((AmplFileAppenderResilience_AS_ROOT_Test.MOUNT_POINT) + "resilience-") + (diff)) + "/";

    String logfileStr = (outputDirStr) + "output.log";

    FileAppender<Object> fa = new FileAppender<Object>();

    static boolean isConformingHost() {
        return EnvUtilForTests.isLocalHostNameInList(new String[]{ "haro" });
    }

    @Before
    public void setUp() throws IOException, InterruptedException {
        if (!(AmplFileAppenderResilience_AS_ROOT_Test.isConformingHost())) {
            return;
        }
        Process p = runLoopFSScript(AmplFileAppenderResilience_AS_ROOT_Test.LoopFSCommand.setup);
        p.waitFor();
        dump("/tmp/loopfs.log");
        fa.setContext(context);
        File outputDir = new File(outputDirStr);
        outputDir.mkdirs();
        System.out.println((("FileAppenderResilienceTest output dir [" + (outputDirStr)) + "]"));
        fa.setName("FILE");
        fa.setEncoder(new EchoEncoder<Object>());
        fa.setFile(logfileStr);
        fa.start();
    }

    void dump(String file) throws IOException {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            int r;
            while ((r = fis.read()) != (-1)) {
                char c = ((char) (r));
                System.out.print(c);
            } 
        } finally {
            if (fis != null) {
                fis.close();
            }
        }
    }

    @After
    public void tearDown() throws IOException, InterruptedException {
        if (!(AmplFileAppenderResilience_AS_ROOT_Test.isConformingHost())) {
            return;
        }
        StatusPrinter.print(context);
        fa.stop();
        Process p = runLoopFSScript(AmplFileAppenderResilience_AS_ROOT_Test.LoopFSCommand.teardown);
        p.waitFor();
        System.out.println("Tearing down");
    }

    static int TOTAL_DURATION = 5000;

    static int NUM_STEPS = 500;

    static int DELAY = (AmplFileAppenderResilience_AS_ROOT_Test.TOTAL_DURATION) / (AmplFileAppenderResilience_AS_ROOT_Test.NUM_STEPS);

    Process runLoopFSScript(AmplFileAppenderResilience_AS_ROOT_Test.LoopFSCommand cmd) throws IOException, InterruptedException {
        if (!(AmplFileAppenderResilience_AS_ROOT_Test.isConformingHost())) {
            return null;
        }
        ProcessBuilder pb = new ProcessBuilder();
        pb.command("/usr/bin/sudo", AmplFileAppenderResilience_AS_ROOT_Test.PATH_LOOPFS_SCRIPT, cmd.toString());
        return pb.start();
    }
}

