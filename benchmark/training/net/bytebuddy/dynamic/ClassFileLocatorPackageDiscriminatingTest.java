package net.bytebuddy.dynamic;


import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;


public class ClassFileLocatorPackageDiscriminatingTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    private ClassFileLocator classFileLocator;

    @Mock
    private ClassFileLocator foo;

    @Mock
    private ClassFileLocator bar;

    @Mock
    private ClassFileLocator.Resolution fooResulution;

    @Mock
    private ClassFileLocator.Resolution barResolution;

    @Test
    public void testValidLocation() throws Exception {
        MatcherAssert.assertThat(classFileLocator.locate((((ClassFileLocatorPackageDiscriminatingTest.FOO) + ".") + (ClassFileLocatorPackageDiscriminatingTest.BAR))), CoreMatchers.is(fooResulution));
    }

    @Test
    public void testValidLocationDefaultPackage() throws Exception {
        MatcherAssert.assertThat(classFileLocator.locate(ClassFileLocatorPackageDiscriminatingTest.BAR), CoreMatchers.is(barResolution));
    }

    @Test
    public void testInvalidLocation() throws Exception {
        MatcherAssert.assertThat(classFileLocator.locate((((ClassFileLocatorPackageDiscriminatingTest.BAR) + ".") + (ClassFileLocatorPackageDiscriminatingTest.FOO))).isResolved(), CoreMatchers.is(false));
    }

    @Test
    public void testClose() throws Exception {
        classFileLocator.close();
        Mockito.verify(foo).close();
        Mockito.verifyNoMoreInteractions(foo);
        Mockito.verify(bar).close();
        Mockito.verifyNoMoreInteractions(bar);
    }
}

