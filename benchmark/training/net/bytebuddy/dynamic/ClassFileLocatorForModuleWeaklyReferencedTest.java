package net.bytebuddy.dynamic;


import net.bytebuddy.test.utility.FieldByFieldComparison;
import net.bytebuddy.test.utility.JavaVersionRule;
import net.bytebuddy.test.utility.MockitoRule;
import net.bytebuddy.utility.JavaModule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;

import static net.bytebuddy.dynamic.ClassFileLocator.ForModule.of;


public class ClassFileLocatorForModuleWeaklyReferencedTest {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Rule
    public MethodRule javaVersionRule = new JavaVersionRule();

    @Mock
    private JavaModule module;

    @Mock
    private ClassLoader classLoader;

    @Mock
    private Object unwrapped;

    @Test
    public void testCreationNamed() throws Exception {
        Mockito.when(module.isNamed()).thenReturn(true);
        MatcherAssert.assertThat(ClassFileLocator.ForModule.WeaklyReferenced.of(module), FieldByFieldComparison.hasPrototype(((ClassFileLocator) (new ClassFileLocator.ForModule.WeaklyReferenced(unwrapped)))));
    }

    @Test
    public void testCreationUnnamed() throws Exception {
        Mockito.when(module.isNamed()).thenReturn(false);
        MatcherAssert.assertThat(ClassFileLocator.ForModule.WeaklyReferenced.of(module), FieldByFieldComparison.hasPrototype(((ClassFileLocator) (new ClassFileLocator.ForClassLoader.WeaklyReferenced(classLoader)))));
    }

    @Test
    public void testCreationNamedSystem() throws Exception {
        Mockito.when(module.isNamed()).thenReturn(true);
        Mockito.when(module.getClassLoader()).thenReturn(ClassLoader.getSystemClassLoader());
        MatcherAssert.assertThat(ClassFileLocator.ForModule.WeaklyReferenced.of(module), FieldByFieldComparison.hasPrototype(((ClassFileLocator) (new ClassFileLocator.ForModule(module)))));
    }

    @Test
    public void testCreationUnnamedSystem() throws Exception {
        Mockito.when(module.isNamed()).thenReturn(false);
        Mockito.when(module.getClassLoader()).thenReturn(ClassLoader.getSystemClassLoader());
        MatcherAssert.assertThat(of(module), FieldByFieldComparison.hasPrototype(ofSystemLoader()));
    }

    @Test
    public void testCreationNamedPlatform() throws Exception {
        Mockito.when(module.isNamed()).thenReturn(true);
        Mockito.when(module.getClassLoader()).thenReturn(ClassLoader.getSystemClassLoader().getParent());
        MatcherAssert.assertThat(ClassFileLocator.ForModule.WeaklyReferenced.of(module), FieldByFieldComparison.hasPrototype(((ClassFileLocator) (new ClassFileLocator.ForModule(module)))));
    }

    @Test
    public void testCreationUnnamedPlatform() throws Exception {
        Mockito.when(module.isNamed()).thenReturn(false);
        Mockito.when(module.getClassLoader()).thenReturn(ClassLoader.getSystemClassLoader().getParent());
        MatcherAssert.assertThat(of(module), FieldByFieldComparison.hasPrototype(((ClassFileLocator) (new ClassFileLocator.ForClassLoader(ClassLoader.getSystemClassLoader().getParent())))));
    }

    @Test
    public void testCreationNamedBoot() throws Exception {
        Mockito.when(module.isNamed()).thenReturn(true);
        Mockito.when(module.getClassLoader()).thenReturn(null);
        MatcherAssert.assertThat(ClassFileLocator.ForModule.WeaklyReferenced.of(module), FieldByFieldComparison.hasPrototype(((ClassFileLocator) (new ClassFileLocator.ForModule(module)))));
    }

    @Test
    public void testCreationUnnamedBoot() throws Exception {
        Mockito.when(module.isNamed()).thenReturn(false);
        Mockito.when(module.getClassLoader()).thenReturn(null);
        MatcherAssert.assertThat(of(module), FieldByFieldComparison.hasPrototype(ofBootLoader()));
    }

    @Test
    @JavaVersionRule.Enforce(9)
    public void testLocateModules() throws Exception {
        ClassFileLocator classFileLocator = new ClassFileLocator.ForModule.WeaklyReferenced(JavaModule.ofType(Object.class).unwrap());
        MatcherAssert.assertThat(classFileLocator.locate(Object.class.getName()).isResolved(), CoreMatchers.is(true));
        MatcherAssert.assertThat(classFileLocator.locate(getClass().getName()).isResolved(), CoreMatchers.is(false));
    }

    @Test
    public void testClose() throws Exception {
        new ClassFileLocator.ForModule.WeaklyReferenced(module).close();
        Mockito.verifyZeroInteractions(module);
    }
}

