package net.bytebuddy.dynamic;


import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;


public class ClassFileLocatorCompoundTest {
    private static final String FOO = "foo";

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private ClassFileLocator classFileLocator;

    @Mock
    private ClassFileLocator otherClassFileLocator;

    @Mock
    private ClassFileLocator.Resolution legal;

    @Mock
    private ClassFileLocator.Resolution illegal;

    @Test
    public void testApplicationOrderCallsSecond() throws Exception {
        Mockito.when(classFileLocator.locate(ClassFileLocatorCompoundTest.FOO)).thenReturn(illegal);
        Mockito.when(otherClassFileLocator.locate(ClassFileLocatorCompoundTest.FOO)).thenReturn(legal);
        MatcherAssert.assertThat(new ClassFileLocator.Compound(classFileLocator, otherClassFileLocator).locate(ClassFileLocatorCompoundTest.FOO), CoreMatchers.is(legal));
        Mockito.verify(classFileLocator).locate(ClassFileLocatorCompoundTest.FOO);
        Mockito.verifyNoMoreInteractions(classFileLocator);
        Mockito.verify(otherClassFileLocator).locate(ClassFileLocatorCompoundTest.FOO);
        Mockito.verifyNoMoreInteractions(otherClassFileLocator);
    }

    @Test
    public void testApplicationOrderDoesNotCallSecond() throws Exception {
        Mockito.when(classFileLocator.locate(ClassFileLocatorCompoundTest.FOO)).thenReturn(legal);
        MatcherAssert.assertThat(new ClassFileLocator.Compound(classFileLocator, otherClassFileLocator).locate(ClassFileLocatorCompoundTest.FOO), CoreMatchers.is(legal));
        Mockito.verify(classFileLocator).locate(ClassFileLocatorCompoundTest.FOO);
        Mockito.verifyNoMoreInteractions(classFileLocator);
        Mockito.verifyZeroInteractions(otherClassFileLocator);
    }

    @Test
    public void testClosable() throws Exception {
        Mockito.when(classFileLocator.locate(ClassFileLocatorCompoundTest.FOO)).thenReturn(legal);
        new ClassFileLocator.Compound(classFileLocator, otherClassFileLocator).close();
        Mockito.verify(classFileLocator).close();
        Mockito.verifyNoMoreInteractions(classFileLocator);
        Mockito.verify(otherClassFileLocator).close();
        Mockito.verifyNoMoreInteractions(otherClassFileLocator);
    }
}

