package hudson.slaves;


import hudson.DescriptorExtensionList;
import hudson.model.Descriptor;
import java.util.ArrayList;
import jenkins.model.Jenkins;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 *
 *
 * @author peppelan
 */
@RunWith(PowerMockRunner.class)
@PowerMockIgnore({ "com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*" })
public class DelegatingComputerLauncherTest {
    public static class DummyOne extends DelegatingComputerLauncher {
        public DummyOne() {
            super(null);
        }

        public static class DummyOneDescriptor extends DescriptorImpl {}
    }

    public static class DummyTwo extends DelegatingComputerLauncher {
        public DummyTwo() {
            super(null);
        }

        public static class DummyTwoDescriptor extends DescriptorImpl {}
    }

    // Ensure that by default a DelegatingComputerLauncher subclass doesn't advertise the option to delegate another
    // DelegatingComputerLauncher
    @Test
    @PrepareForTest(Jenkins.class)
    public void testRecursionAvoidance() {
        PowerMockito.mockStatic(Jenkins.class);
        Jenkins mockJenkins = mock(Jenkins.class);
        PowerMockito.when(Jenkins.getInstance()).thenReturn(mockJenkins);
        DescriptorExtensionList<ComputerLauncher, Descriptor<ComputerLauncher>> mockList = mock(DescriptorExtensionList.class);
        Mockito.doReturn(mockList).when(mockJenkins).getDescriptorList(ArgumentMatchers.eq(ComputerLauncher.class));
        ArrayList<Descriptor<ComputerLauncher>> returnedList = new ArrayList<>();
        returnedList.add(new DelegatingComputerLauncherTest.DummyOne.DummyOneDescriptor());
        returnedList.add(new DelegatingComputerLauncherTest.DummyTwo.DummyTwoDescriptor());
        when(mockList.iterator()).thenReturn(returnedList.iterator());
        Assert.assertTrue(("DelegatingComputerLauncher should filter out other DelegatingComputerLauncher instances " + "from its descriptor's getApplicableDescriptors() method"), new DelegatingComputerLauncherTest.DummyTwo.DummyTwoDescriptor().applicableDescriptors(null, new DumbSlave.DescriptorImpl()).isEmpty());
    }
}

