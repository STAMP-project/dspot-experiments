package samples.junit4.misc;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ PrivateInnerInterfacesInTestClassTest.class })
public class PrivateInnerInterfacesInTestClassTest {
    @Test
    public void privateInterfacesCanBeLoadedAndBytcodeManipulatedByPowerMock() throws Exception {
        PrivateInnerInterfacesInTestClassTest.InnerInterface innerInterface = new PrivateInnerInterfacesInTestClassTest.InnerInterface() {
            public String aMethod() {
                return "ok";
            }
        };
        Assert.assertEquals("ok", innerInterface.aMethod());
    }

    private interface InnerInterface {
        String aMethod();
    }
}

