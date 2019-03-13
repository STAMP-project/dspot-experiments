package samples.junit4.easymock;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;
import samples.Service;
import samples.privatefield.SimplePrivateFieldServiceClass;


/**
 * This test verifies that you can mix EasyMock and PowerMock.
 */
@RunWith(PowerMockRunner.class)
@SuppressStaticInitializationFor("samples.privatefield.SimplePrivateFieldServiceClass")
public class EasyMockAndPowerMockMixTest {
    @Test
    public void testSimplePrivateFieldServiceClass() throws Exception {
        SimplePrivateFieldServiceClass tested = new SimplePrivateFieldServiceClass();
        Service serviceMock = createMock(Service.class);
        setInternalState(tested, "service", serviceMock, SimplePrivateFieldServiceClass.class);
        final String expected = "Hello world!";
        expect(serviceMock.getServiceMessage()).andReturn(expected);
        replay(serviceMock);
        final String actual = tested.useService();
        verify(serviceMock);
        Assert.assertEquals(expected, actual);
    }
}

