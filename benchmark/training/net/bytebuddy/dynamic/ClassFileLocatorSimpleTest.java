package net.bytebuddy.dynamic;


import java.util.Collections;
import net.bytebuddy.description.type.TypeDescription;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mockito;

import static net.bytebuddy.dynamic.ClassFileLocator.Simple.of;


public class ClassFileLocatorSimpleTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private static final byte[] QUX = new byte[]{ 1, 2, 3 };

    @Test
    public void testSuccessfulLocation() throws Exception {
        ClassFileLocator.Resolution resolution = ClassFileLocator.Simple.of(ClassFileLocatorSimpleTest.FOO, ClassFileLocatorSimpleTest.QUX).locate(ClassFileLocatorSimpleTest.FOO);
        MatcherAssert.assertThat(resolution.isResolved(), CoreMatchers.is(true));
        MatcherAssert.assertThat(resolution.resolve(), CoreMatchers.is(ClassFileLocatorSimpleTest.QUX));
    }

    @Test
    public void testInSuccessfulLocation() throws Exception {
        ClassFileLocator.Resolution resolution = ClassFileLocator.Simple.of(ClassFileLocatorSimpleTest.FOO, ClassFileLocatorSimpleTest.QUX).locate(ClassFileLocatorSimpleTest.BAR);
        MatcherAssert.assertThat(resolution.isResolved(), CoreMatchers.is(false));
    }

    @Test
    public void testClose() throws Exception {
        ClassFileLocator.Simple.of(ClassFileLocatorSimpleTest.FOO, ClassFileLocatorSimpleTest.QUX).close();
    }

    @Test
    public void testDynamicType() throws Exception {
        DynamicType dynamicType = Mockito.mock(DynamicType.class);
        TypeDescription typeDescription = Mockito.mock(TypeDescription.class);
        Mockito.when(typeDescription.getName()).thenReturn(ClassFileLocatorSimpleTest.FOO);
        Mockito.when(dynamicType.getAllTypes()).thenReturn(Collections.singletonMap(typeDescription, ClassFileLocatorSimpleTest.QUX));
        ClassFileLocator classFileLocator = of(dynamicType);
        MatcherAssert.assertThat(classFileLocator.locate(ClassFileLocatorSimpleTest.FOO).isResolved(), CoreMatchers.is(true));
        MatcherAssert.assertThat(classFileLocator.locate(ClassFileLocatorSimpleTest.FOO).resolve(), CoreMatchers.is(ClassFileLocatorSimpleTest.QUX));
        MatcherAssert.assertThat(classFileLocator.locate(ClassFileLocatorSimpleTest.BAR).isResolved(), CoreMatchers.is(false));
    }

    @Test
    public void testOfResources() throws Exception {
        ClassFileLocator.Resolution resolution = ofResources(Collections.singletonMap(((((ClassFileLocatorSimpleTest.FOO) + "/") + (ClassFileLocatorSimpleTest.BAR)) + ".class"), ClassFileLocatorSimpleTest.QUX)).locate((((ClassFileLocatorSimpleTest.FOO) + ".") + (ClassFileLocatorSimpleTest.BAR)));
        MatcherAssert.assertThat(resolution.isResolved(), CoreMatchers.is(true));
        MatcherAssert.assertThat(resolution.resolve(), CoreMatchers.is(ClassFileLocatorSimpleTest.QUX));
    }

    @Test
    public void testOfResourcesNoClassFile() throws Exception {
        ClassFileLocator.Resolution resolution = ofResources(Collections.singletonMap((((ClassFileLocatorSimpleTest.FOO) + "/") + (ClassFileLocatorSimpleTest.BAR)), ClassFileLocatorSimpleTest.QUX)).locate((((ClassFileLocatorSimpleTest.FOO) + ".") + (ClassFileLocatorSimpleTest.BAR)));
        MatcherAssert.assertThat(resolution.isResolved(), CoreMatchers.is(false));
    }
}

