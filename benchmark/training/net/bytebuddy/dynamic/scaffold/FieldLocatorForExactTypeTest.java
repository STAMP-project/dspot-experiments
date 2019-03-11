package net.bytebuddy.dynamic.scaffold;


import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.test.utility.FieldByFieldComparison;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;


public class FieldLocatorForExactTypeTest {
    private static final String FOO = "foo";

    private static final String QUX = "qux";

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private TypeDescription locatedType;

    @Mock
    private TypeDescription typeDescription;

    @Test
    public void testExactTypeFound() throws Exception {
        FieldLocator.Resolution resolution = new FieldLocator.ForExactType(of(FieldLocatorForExactTypeTest.Foo.class)).locate(FieldLocatorForExactTypeTest.FOO);
        Assert.assertThat(resolution.isResolved(), CoreMatchers.is(true));
        Assert.assertThat(resolution.getField(), CoreMatchers.is(((FieldDescription) (new FieldDescription.ForLoadedField(FieldLocatorForExactTypeTest.Foo.class.getDeclaredField(FieldLocatorForExactTypeTest.FOO))))));
    }

    @Test
    public void testExactTypeFoundWithType() throws Exception {
        FieldLocator.Resolution resolution = new FieldLocator.ForExactType(of(FieldLocatorForExactTypeTest.Foo.class)).locate(FieldLocatorForExactTypeTest.FOO, of(Void.class));
        Assert.assertThat(resolution.isResolved(), CoreMatchers.is(true));
        Assert.assertThat(resolution.getField(), CoreMatchers.is(((FieldDescription) (new FieldDescription.ForLoadedField(FieldLocatorForExactTypeTest.Foo.class.getDeclaredField(FieldLocatorForExactTypeTest.FOO))))));
    }

    @Test
    public void testExactTypeNotFoundInherited() throws Exception {
        Assert.assertThat(new FieldLocator.ForExactType(of(FieldLocatorForExactTypeTest.Bar.class)).locate(FieldLocatorForExactTypeTest.FOO).isResolved(), CoreMatchers.is(false));
    }

    @Test
    public void testExactTypeNotFoundNotExistent() throws Exception {
        Assert.assertThat(new FieldLocator.ForExactType(of(FieldLocatorForExactTypeTest.Foo.class)).locate(FieldLocatorForExactTypeTest.QUX).isResolved(), CoreMatchers.is(false));
    }

    @Test
    public void testExactTypeNotFoundInvisible() throws Exception {
        Assert.assertThat(new FieldLocator.ForExactType(of(FieldLocatorForExactTypeTest.Foo.class), of(Object.class)).locate(FieldLocatorForExactTypeTest.FOO).isResolved(), CoreMatchers.is(false));
    }

    @Test
    public void testExactTypeNotFoundWrongType() throws Exception {
        Assert.assertThat(new FieldLocator.ForExactType(of(FieldLocatorForExactTypeTest.Foo.class)).locate(FieldLocatorForExactTypeTest.FOO, of(Object.class)).isResolved(), CoreMatchers.is(false));
    }

    @Test
    public void testFactory() throws Exception {
        Assert.assertThat(new FieldLocator.ForExactType.Factory(locatedType).make(typeDescription), FieldByFieldComparison.hasPrototype(((FieldLocator) (new FieldLocator.ForExactType(locatedType, typeDescription)))));
    }

    @SuppressWarnings("unused")
    private static class Foo {
        private Void foo;

        protected Void bar;
    }

    @SuppressWarnings("unused")
    private static class Bar extends FieldLocatorForExactTypeTest.Foo {
        protected Void bar;
    }
}

