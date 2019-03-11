package json.chao.com.wanandroid.testStudyExample.PowerMockTestExample;


import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.internal.verification.VerificationModeFactory;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.api.support.membermodification.MemberModifier;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.powermock.reflect.Whitebox;


/**
 *
 *
 * @author quchao
 * @unknown 2018/6/5
 */
// @RunWith(PowerMockRunner.class)
@PrepareForTest({ Banana.class })
@PowerMockIgnore({ "org.mockito.*", "org.robolectric.*", "android.*" })
public class PowerMockTest {
    @Rule
    public PowerMockRule mPowerMockRule = new PowerMockRule();

    @Test
    public void testStaticMethod() {
        PowerMockito.mockStatic(Banana.class);
        PowerMockito.when(Banana.getColor()).thenReturn("??");
        System.out.println(Banana.getColor());
    }

    @Test
    public void testStaticField() {
        Whitebox.setInternalState(Banana.class, "COLOR", "??");
        Assert.assertEquals("??", Banana.getColor());
    }

    @Test
    public void testPrivateMethod() throws Exception {
        Banana banana = PowerMockito.mock(Banana.class);
        PowerMockito.when(banana.getBananaInfo()).thenCallRealMethod();
        PowerMockito.when(banana, "flavor").thenReturn("???");
        Assert.assertEquals(banana.getBananaInfo(), "??????");
        PowerMockito.verifyPrivate(banana, VerificationModeFactory.times(1)).invoke("flavor");
        System.out.println(banana.getBananaInfo());
    }

    @Test
    public void skipPrivateMethod() {
        Banana banana = new Banana();
        PowerMockito.suppress(PowerMockito.method(Banana.class, "flavor"));
        System.out.println(banana.getBananaInfo());
    }

    @Test
    public void changePrivateField() throws IllegalAccessException {
        Banana banana = new Banana();
        MemberModifier.field(Banana.class, "fruit").set(banana, "??");
        Assert.assertEquals("??", banana.getFruit());
    }

    @Test
    public void changeFinalMethod() {
        Banana banana = PowerMockito.mock(Banana.class);
        PowerMockito.when(banana.isLike()).thenReturn(false);
        Assert.assertEquals(false, banana.isLike());
    }

    @Test
    public void whenNewClass() throws Exception {
        Banana banana = PowerMockito.mock(Banana.class);
        PowerMockito.when(banana.getBananaInfo()).thenReturn("???");
        PowerMockito.whenNew(Banana.class).withNoArguments().thenReturn(banana);
        Banana banana1 = new Banana();
        Assert.assertEquals("???", banana1.getBananaInfo());
    }
}

