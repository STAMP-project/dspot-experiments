package samples.powermockito.junit4.whennew;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import samples.expectnew.ExpectNewDemo;
import samples.newmocking.MyClass;


@PrepareForTest(ExpectNewDemo.class)
@RunWith(PowerMockRunner.class)
public class VerifyNewWithoutWhenNewTest {
    @Test
    public void verifyingNewWithoutExpectationWhenNoArgumentsThrowsISE() throws Exception {
        ExpectNewDemo tested = new ExpectNewDemo();
        Assert.assertEquals("Hello world", tested.getMessage());
        try {
            verifyNew(MyClass.class).withNoArguments();
            Assert.fail("IllegalStateException expected");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals(("No instantiation of class samples.newmocking.MyClass was recorded " + ("during the test. Note that only expected object creations " + "(e.g. those using whenNew(..)) can be verified.")), e.getMessage());
        }
    }

    @Test
    public void verifyingNewWithoutExpectationButWithArgumentsThrowsISE() throws Exception {
        ExpectNewDemo tested = new ExpectNewDemo();
        Assert.assertEquals("Hello world", tested.getMessage());
        try {
            verifyNew(MyClass.class, Mockito.atLeastOnce()).withNoArguments();
            Assert.fail("IllegalStateException expected");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals(("No instantiation of class samples.newmocking.MyClass was recorded " + ("during the test. Note that only expected object creations " + "(e.g. those using whenNew(..)) can be verified.")), e.getMessage());
        }
    }
}

