package net.bytebuddy.dynamic;


import java.io.ByteArrayInputStream;
import net.bytebuddy.test.utility.FieldByFieldComparison;
import net.bytebuddy.test.utility.JavaVersionRule;
import net.bytebuddy.test.utility.MockitoRule;
import net.bytebuddy.utility.JavaModule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;

import static net.bytebuddy.dynamic.ClassFileLocator.ForModule.of;
import static net.bytebuddy.dynamic.ClassFileLocator.ForModule.ofBootLayer;


public class ClassFileLocatorForModuleTest {
    private static final String FOOBAR = "foo/bar";

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Rule
    public MethodRule javaVersionRule = new JavaVersionRule();

    @Mock
    private JavaModule module;

    @Mock
    private ClassLoader classLoader;

    @Test
    public void testCreationNamed() throws Exception {
        Mockito.when(module.isNamed()).thenReturn(true);
        MatcherAssert.assertThat(of(module), FieldByFieldComparison.hasPrototype(((ClassFileLocator) (new ClassFileLocator.ForModule(module)))));
    }

    @Test
    public void testCreationUnnamed() throws Exception {
        Mockito.when(module.isNamed()).thenReturn(false);
        MatcherAssert.assertThat(of(module), FieldByFieldComparison.hasPrototype(((ClassFileLocator) (new ClassFileLocator.ForClassLoader(classLoader)))));
    }

    @Test
    public void testLocatable() throws Exception {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(new byte[]{ 1, 2, 3 });
        Mockito.when(module.getResourceAsStream(((ClassFileLocatorForModuleTest.FOOBAR) + ".class"))).thenReturn(inputStream);
        ClassFileLocator.Resolution resolution = new ClassFileLocator.ForModule(module).locate(ClassFileLocatorForModuleTest.FOOBAR);
        MatcherAssert.assertThat(resolution.isResolved(), CoreMatchers.is(true));
        MatcherAssert.assertThat(resolution.resolve(), CoreMatchers.is(new byte[]{ 1, 2, 3 }));
        Mockito.verify(module).getResourceAsStream(((ClassFileLocatorForModuleTest.FOOBAR) + ".class"));
        Mockito.verifyNoMoreInteractions(module);
    }

    @Test(expected = IllegalStateException.class)
    public void testNonLocatable() throws Exception {
        ClassFileLocator.Resolution resolution = new ClassFileLocator.ForModule(module).locate(ClassFileLocatorForModuleTest.FOOBAR);
        MatcherAssert.assertThat(resolution.isResolved(), CoreMatchers.is(false));
        Mockito.verify(module).getResourceAsStream(((ClassFileLocatorForModuleTest.FOOBAR) + ".class"));
        Mockito.verifyNoMoreInteractions(module);
        resolution.resolve();
        Assert.fail();
    }

    @Test
    @JavaVersionRule.Enforce(9)
    public void testBootPath() throws Exception {
        ClassFileLocator classFileLocator = ofBootLayer();
        MatcherAssert.assertThat(classFileLocator.locate(Object.class.getName()).isResolved(), CoreMatchers.is(true));
        MatcherAssert.assertThat(classFileLocator.locate(getClass().getName()).isResolved(), CoreMatchers.is(false));
    }

    @Test
    public void testClose() throws Exception {
        new ClassFileLocator.ForModule(module).close();
        Mockito.verifyZeroInteractions(module);
    }
}

