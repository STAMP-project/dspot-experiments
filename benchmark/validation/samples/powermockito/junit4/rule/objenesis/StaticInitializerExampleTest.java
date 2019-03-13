package samples.powermockito.junit4.rule.objenesis;


import java.util.HashSet;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.powermock.reflect.Whitebox;
import samples.staticinitializer.StaticInitializerExample;


@SuppressStaticInitializationFor("samples.staticinitializer.StaticInitializerExample")
public class StaticInitializerExampleTest {
    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Test
    public void testSupressStaticInitializerAndSetFinalField() throws Exception {
        Assert.assertNull("Should be null because the static initializer should be suppressed", StaticInitializerExample.getMySet());
        final HashSet<String> hashSet = new HashSet<String>();
        Whitebox.setInternalState(StaticInitializerExample.class, "mySet", hashSet);
        Assert.assertSame(hashSet, Whitebox.getInternalState(StaticInitializerExample.class, "mySet"));
    }
}

