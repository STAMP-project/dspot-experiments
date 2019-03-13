package samples.junit412.github668;


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
public class TwoMockFieldsWithSameTypeCase {
    @Mock(fieldName = "incidentPropertyChangeDAO")
    private IncidentPropertyChangeDAO incidentPropertyChangeDAO;

    @Mock(fieldName = "propertyChangeDAO")
    private IncidentPropertyChangeDAO propertyChangeDAO;

    @Test
    public void mockClassShouldInjected() {
        Assert.assertNotNull(incidentPropertyChangeDAO);
        Assert.assertNotNull(propertyChangeDAO);
    }

    @Test
    public void shouldBeAbleMockMethodsOfInjected() {
        expect(incidentPropertyChangeDAO.getIncident()).andReturn("value");
        expect(propertyChangeDAO.getIncident()).andReturn("value1");
        replayAll(incidentPropertyChangeDAO, propertyChangeDAO);
        Assert.assertEquals("value", incidentPropertyChangeDAO.getIncident());
        Assert.assertEquals("value1", propertyChangeDAO.getIncident());
    }
}

