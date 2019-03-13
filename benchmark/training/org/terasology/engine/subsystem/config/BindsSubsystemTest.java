package org.terasology.engine.subsystem.config;


import InputType.KEY;
import KeyId.T;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.terasology.config.facade.BindsConfiguration;
import org.terasology.context.Context;
import org.terasology.engine.SimpleUri;
import org.terasology.input.BindButtonEvent;
import org.terasology.input.BindableButton;
import org.terasology.input.DefaultBinding;
import org.terasology.input.Input;
import org.terasology.input.InputType;
import org.terasology.input.Keyboard.KeyId;
import org.terasology.input.RegisterBindButton;
import org.terasology.module.ModuleEnvironment;


public class BindsSubsystemTest {
    private static final String TEST_MODULE = "TestModule";

    private BindsSubsystem bindsSubsystem;

    private BindsConfiguration bindsConfiguration;

    private Context context;

    private List<Class<?>> registerBindButtonClasses;

    private List<Class<?>> registerRealBindAxisClasses;

    @Test
    public void testSelfRegisterOnContext() {
        Assert.assertThat(context.get(BindsManager.class), CoreMatchers.is(bindsSubsystem));
    }

    @Test
    public void testUpdateBinds() {
        registerBindButtonClasses.add(BindsSubsystemTest.TestEventButton.class);
        bindsSubsystem.updateConfigWithDefaultBinds();
        List<Input> defaultBinds = bindsSubsystem.getDefaultBindsConfig().getBinds(new SimpleUri(BindsSubsystemTest.TEST_MODULE, "testEvent"));
        Assert.assertThat(defaultBinds.size(), CoreMatchers.is(1));
        Assert.assertThat(defaultBinds.get(0).getType(), CoreMatchers.is(KEY));
        Assert.assertThat(defaultBinds.get(0).getId(), CoreMatchers.is(T));
        Assert.assertThat(defaultBinds.get(0).getName(), CoreMatchers.is(Key.T.getName()));
        Assert.assertThat(defaultBinds.get(0).getDisplayName(), CoreMatchers.is(Key.T.getDisplayName()));
        List<Input> binds = bindsSubsystem.getBindsConfig().getBinds(new SimpleUri(BindsSubsystemTest.TEST_MODULE, "testEvent"));
        Assert.assertThat(binds.size(), CoreMatchers.is(1));
        Assert.assertThat(binds.get(0).getType(), CoreMatchers.is(KEY));
        Assert.assertThat(binds.get(0).getId(), CoreMatchers.is(T));
        Assert.assertThat(binds.get(0).getName(), CoreMatchers.is(Key.T.getName()));
        Assert.assertThat(binds.get(0).getDisplayName(), CoreMatchers.is(Key.T.getDisplayName()));
    }

    @Test
    public void test() {
        ModuleEnvironment environment = Mockito.mock(ModuleEnvironment.class);
        Mockito.when(environment.getTypesAnnotatedWith(ArgumentMatchers.eq(RegisterBindButton.class), ArgumentMatchers.any())).thenReturn(registerBindButtonClasses);
        registerBindButtonClasses.add(BindsSubsystemTest.TestEventButton.class);
    }

    @Test
    public void testRegisterBinds() {
        registerBindButtonClasses.add(BindsSubsystemTest.TestEventButton.class);
        bindsSubsystem.updateConfigWithDefaultBinds();
        bindsSubsystem.registerBinds();
        BindableButton button = bindsSubsystem.getKeyBinds().get(T);
        Assert.assertThat(button, CoreMatchers.is(CoreMatchers.not(CoreMatchers.nullValue())));
        Assert.assertThat(button.getId(), CoreMatchers.is(new SimpleUri(BindsSubsystemTest.TEST_MODULE, "testEvent")));
        Assert.assertThat(button.getDisplayName(), CoreMatchers.is("${engine-tests:menu#theTestEvent}"));
    }

    // test classes, registered during tests
    @RegisterBindButton(id = "testEvent", description = "${engine-tests:menu#theTestEvent}", repeating = false, category = "tests")
    @DefaultBinding(type = InputType.KEY, id = KeyId.T)
    public static class TestEventButton extends BindButtonEvent {}
}

