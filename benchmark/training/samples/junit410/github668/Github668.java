package samples.junit410.github668;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.annotation.Mock;
import org.powermock.api.extension.listener.AnnotationEnabler;
import org.powermock.core.classloader.annotations.PowerMockListener;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 *
 */
@RunWith(PowerMockRunner.class)
@PowerMockListener(AnnotationEnabler.class)
public class Github668 {
    @Mock
    private IncidentPropertyChangeDAO incidentPropertyChangeDAO;

    @Test
    public void mockClassShouldInjected() {
        Assert.assertNotNull(incidentPropertyChangeDAO);
    }

    @Test
    public void shouldBeAbleMockMethodsOfInjected() {
        expect(incidentPropertyChangeDAO.getIncident()).andReturn("value");
        replayAll(incidentPropertyChangeDAO);
        Assert.assertEquals("value", incidentPropertyChangeDAO.getIncident());
    }
}

