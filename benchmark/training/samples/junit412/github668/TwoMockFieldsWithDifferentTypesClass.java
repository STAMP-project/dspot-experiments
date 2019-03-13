package samples.junit412.github668;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.annotation.Mock;
import org.powermock.api.extension.listener.AnnotationEnabler;
import org.powermock.core.classloader.annotations.PowerMockListener;
import org.powermock.modules.junit4.PowerMockRunner;
import samples.Service;


/**
 *
 */
@RunWith(PowerMockRunner.class)
@PowerMockListener(AnnotationEnabler.class)
public class TwoMockFieldsWithDifferentTypesClass {
    @Mock
    private IncidentPropertyChangeDAO incidentPropertyChangeDAO;

    @Mock
    private Service serviceMock;

    @Test
    public void mockClassShouldInjected() {
        Assert.assertNotNull(incidentPropertyChangeDAO);
        Assert.assertNotNull(serviceMock);
    }

    @Test
    public void shouldBeAbleMockMethodsOfInjected() {
        expect(incidentPropertyChangeDAO.getIncident()).andReturn("value");
        expect(serviceMock.getServiceMessage()).andReturn("value");
        replayAll(incidentPropertyChangeDAO, serviceMock);
        Assert.assertEquals("value", incidentPropertyChangeDAO.getIncident());
        Assert.assertEquals("value", serviceMock.getServiceMessage());
    }
}

