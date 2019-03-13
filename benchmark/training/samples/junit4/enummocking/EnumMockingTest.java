package samples.junit4.enummocking;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import samples.enummocking.MyEnum;


@RunWith(PowerMockRunner.class)
@PrepareForTest(MyEnum.class)
public class EnumMockingTest {
    @Test
    public void assertMockingOfStaticMethodInEnumWorks() throws Exception {
        final String expected = "something else";
        mockStatic(MyEnum.class);
        expect(MyEnum.getString()).andReturn(expected);
        replayAll();
        final String actual = MyEnum.getString();
        verifyAll();
        Assert.assertEquals(expected, actual);
    }
}

