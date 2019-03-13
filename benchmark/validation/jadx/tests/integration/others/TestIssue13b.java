package jadx.tests.integration.others;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import org.junit.Assert;
import org.junit.Test;


public class TestIssue13b extends IntegrationTest {
    public static class TestCls {
        private static final String PROPERTIES_FILE = "";

        private static final String TAG = "";

        private final CountDownLatch mInitializedLatch = new CountDownLatch(1);

        private int mC2KServerPort = 0;

        private String mSuplServerHost = "";

        private int mSuplServerPort = 0;

        private String mC2KServerHost = "";

        public TestCls() {
            Properties mProperties = new Properties();
            try {
                File file = new File(TestIssue13b.TestCls.PROPERTIES_FILE);
                FileInputStream stream = new FileInputStream(file);
                mProperties.load(stream);
                stream.close();
                mSuplServerHost = mProperties.getProperty("SUPL_HOST");
                String portString = mProperties.getProperty("SUPL_PORT");
                if (((mSuplServerHost) != null) && (portString != null)) {
                    try {
                        mSuplServerPort = Integer.parseInt(portString);
                    } catch (NumberFormatException e) {
                        TestIssue13b.TestCls.Log.e(TestIssue13b.TestCls.TAG, ("unable to parse SUPL_PORT: " + portString));
                    }
                }
                mC2KServerHost = mProperties.getProperty("C2K_HOST");
                portString = mProperties.getProperty("C2K_PORT");
                if (((mC2KServerHost) != null) && (portString != null)) {
                    try {
                        mC2KServerPort = Integer.parseInt(portString);
                    } catch (NumberFormatException e) {
                        TestIssue13b.TestCls.Log.e(TestIssue13b.TestCls.TAG, ("unable to parse C2K_PORT: " + portString));
                    }
                }
            } catch (IOException e) {
                TestIssue13b.TestCls.Log.e(TestIssue13b.TestCls.TAG, ("Could not open GPS configuration file " + (TestIssue13b.TestCls.PROPERTIES_FILE)));
            }
            Thread mThread = new Thread();
            mThread.start();
            while (true) {
                try {
                    mInitializedLatch.await();
                    break;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            } 
        }

        private static class Log {
            public static void e(String tag, String s) {
            }
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestIssue13b.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.countString(4, "} catch ("));
        Assert.assertThat(code, JadxMatchers.countString(3, "Log.e("));
        Assert.assertThat(code, JadxMatchers.containsOne("Thread.currentThread().interrupt();"));
    }
}

