package samples.junit4.swing;


import javax.swing.JOptionPane;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import samples.swing.ReallySimpleSwingDemo;


/**
 * Unit test that makes sure that PowerMock works with Swing components.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(JOptionPane.class)
public class ReallySimpleSwingDemoTest {
    @Test
    public void assertThatPowerMockWorksWithSwingComponents() throws Exception {
        // Currently this tests fails on Java 8, see issue 504.
        Assume.assumeTrue(((Float.valueOf(System.getProperty("java.specification.version"))) < 1.8F));
        final String message = "powermock";
        mockStatic(JOptionPane.class);
        JOptionPane.showMessageDialog(null, message);
        expectLastCall().once();
        replayAll();
        new ReallySimpleSwingDemo().displayMessage(message);
        verifyAll();
    }
}

