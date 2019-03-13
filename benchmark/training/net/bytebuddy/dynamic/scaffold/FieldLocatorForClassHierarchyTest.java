package net.bytebuddy.dynamic.scaffold;


import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.test.packaging.FieldLocatorTestHelper;
import net.bytebuddy.test.utility.FieldByFieldComparison;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;

import static net.bytebuddy.dynamic.scaffold.FieldLocator.ForClassHierarchy.Factory.INSTANCE;


public class FieldLocatorForClassHierarchyTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private static final String QUX = "qux";

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private TypeDescription typeDescription;

    @Test
    public void testClassHierarchyTypeFound() throws Exception {
        FieldLocator.Resolution resolution = new FieldLocator.ForClassHierarchy(of(FieldLocatorForClassHierarchyTest.Sample.class)).locate(FieldLocatorForClassHierarchyTest.FOO);
        Assert.assertThat(resolution.isResolved(), CoreMatchers.is(true));
        Assert.assertThat(resolution.getField(), CoreMatchers.is(((FieldDescription) (new FieldDescription.ForLoadedField(FieldLocatorForClassHierarchyTest.Sample.class.getDeclaredField(FieldLocatorForClassHierarchyTest.FOO))))));
    }

    @Test
    public void testClassHierarchyFoundWithType() throws Exception {
        FieldLocator.Resolution resolution = new FieldLocator.ForClassHierarchy(of(FieldLocatorForClassHierarchyTest.Sample.class)).locate(FieldLocatorForClassHierarchyTest.FOO, of(Void.class));
        Assert.assertThat(resolution.isResolved(), CoreMatchers.is(true));
        Assert.assertThat(resolution.getField(), CoreMatchers.is(((FieldDescription) (new FieldDescription.ForLoadedField(FieldLocatorForClassHierarchyTest.Sample.class.getDeclaredField(FieldLocatorForClassHierarchyTest.FOO))))));
    }

    @Test
    public void testClassHierarchyFoundInherited() throws Exception {
        FieldLocator.Resolution resolution = new FieldLocator.ForClassHierarchy(of(FieldLocatorForClassHierarchyTest.Qux.class)).locate(FieldLocatorForClassHierarchyTest.BAR);
        Assert.assertThat(resolution.isResolved(), CoreMatchers.is(true));
        Assert.assertThat(resolution.getField(), CoreMatchers.is(((FieldDescription) (new FieldDescription.ForLoadedField(FieldLocatorForClassHierarchyTest.Sample.class.getDeclaredField(FieldLocatorForClassHierarchyTest.BAR))))));
    }

    @Test
    public void testClassHierarchyFoundInheritedShadowed() throws Exception {
        FieldLocator.Resolution resolution = new FieldLocator.ForClassHierarchy(of(FieldLocatorForClassHierarchyTest.Bar.class)).locate(FieldLocatorForClassHierarchyTest.BAR);
        Assert.assertThat(resolution.isResolved(), CoreMatchers.is(true));
        Assert.assertThat(resolution.getField(), CoreMatchers.is(((FieldDescription) (new FieldDescription.ForLoadedField(FieldLocatorForClassHierarchyTest.Bar.class.getDeclaredField(FieldLocatorForClassHierarchyTest.BAR))))));
    }

    @Test
    public void testClassHierarchyNotFoundInherited() throws Exception {
        Assert.assertThat(new FieldLocator.ForClassHierarchy(of(FieldLocatorForClassHierarchyTest.Bar.class)).locate(FieldLocatorForClassHierarchyTest.FOO).isResolved(), CoreMatchers.is(ClassFileVersion.of(FieldLocatorForClassHierarchyTest.Bar.class).isAtLeast(JAVA_V11)));
    }

    @Test
    public void testClassHierarchyNotFoundInheritedNoNestMates() throws Exception {
        Assert.assertThat(new FieldLocator.ForClassHierarchy(of(FieldLocatorTestHelper.class)).locate(FieldLocatorForClassHierarchyTest.FOO).isResolved(), CoreMatchers.is(false));
    }

    @Test
    public void testClassHierarchyNotFoundNotExistent() throws Exception {
        Assert.assertThat(new FieldLocator.ForClassHierarchy(of(FieldLocatorForClassHierarchyTest.Sample.class)).locate(FieldLocatorForClassHierarchyTest.QUX).isResolved(), CoreMatchers.is(false));
    }

    @Test
    public void testClassHierarchyNotFoundInvisible() throws Exception {
        Assert.assertThat(new FieldLocator.ForClassHierarchy(of(FieldLocatorForClassHierarchyTest.Sample.class), of(Object.class)).locate(FieldLocatorForClassHierarchyTest.FOO).isResolved(), CoreMatchers.is(false));
    }

    @Test
    public void testClassHierarchyNotFoundWrongType() throws Exception {
        Assert.assertThat(new FieldLocator.ForClassHierarchy(of(FieldLocatorForClassHierarchyTest.Sample.class)).locate(FieldLocatorForClassHierarchyTest.FOO, of(Object.class)).isResolved(), CoreMatchers.is(false));
    }

    @Test
    public void testFactory() throws Exception {
        Assert.assertThat(INSTANCE.make(typeDescription), FieldByFieldComparison.hasPrototype(((FieldLocator) (new FieldLocator.ForClassHierarchy(typeDescription)))));
    }

    @SuppressWarnings("unused")
    public static class Sample {
        private Void foo;

        protected Void bar;
    }

    @SuppressWarnings("unused")
    private static class Bar extends FieldLocatorForClassHierarchyTest.Sample {
        protected Void bar;
    }

    @SuppressWarnings("unused")
    private static class Qux extends FieldLocatorForClassHierarchyTest.Sample {
        private Void baz;
    }
}

