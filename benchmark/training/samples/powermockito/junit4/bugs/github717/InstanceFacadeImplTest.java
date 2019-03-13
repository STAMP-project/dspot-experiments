package samples.powermockito.junit4.bugs.github717;


import InstanceStatus.PENDING;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.extension.listener.AnnotationEnabler;
import org.powermock.core.classloader.annotations.PowerMockListener;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@PowerMockListener(AnnotationEnabler.class)
@PrepareForTest(InstanceFacadeImpl.class)
public class InstanceFacadeImplTest {
    private InstanceFacadeImpl instanceFacade;

    @Test
    public void should_not_throw_exception() throws Exception {
        replayAll();
        instanceFacade.instanceStatusProcessors.get(PENDING).accept(null);
        verifyAll();
    }
}

