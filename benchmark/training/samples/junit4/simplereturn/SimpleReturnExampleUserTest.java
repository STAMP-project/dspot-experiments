package samples.junit4.simplereturn;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import samples.simplereturn.SimpleReturnExample;


@RunWith(PowerMockRunner.class)
@PrepareForTest(SimpleReturnExample.class)
public class SimpleReturnExampleUserTest {
    @Test
    public void testCreateMockDelegatedToEasyMock() throws Exception {
        SimpleReturnExample mock = createMock(SimpleReturnExample.class);
        expect(mock.mySimpleMethod()).andReturn(2);
        replay(mock);
        Assert.assertEquals(2, myMethod());
        verify(mock);
    }
}

