package roboguice.additionaltests;


import Robolectric.application;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import roboguice.RoboGuice;


@RunWith(RobolectricTestRunner.class)
public class ConstructorModuleTest {
    @Test
    public void testShouldUseConstructorWithApplicationArgument() {
        // GIVEN
        Pojo instance = new Pojo();
        // WHEN
        RoboGuice.getOrCreateBaseApplicationInjector(application).injectMembers(instance);
        // THEN
        // will only work if the TestModule could be loaded properly
        Assert.assertThat(instance.getFoo(), CoreMatchers.notNullValue());
    }
}

