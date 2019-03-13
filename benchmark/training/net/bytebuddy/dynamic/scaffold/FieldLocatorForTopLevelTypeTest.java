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

import static net.bytebuddy.dynamic.scaffold.FieldLocator.ForTopLevelType.Factory.INSTANCE;


public class FieldLocatorForTopLevelTypeTest {
    private static final String FOO = "foo";

    private static final String QUX = "qux";

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private TypeDescription typeDescription;

    @Test
    public void testExactTypeFound() throws Exception {
        FieldLocator.Resolution resolution = new FieldLocator.ForTopLevelType(of(FieldLocatorForTopLevelTypeTest.Foo.class)).locate(FieldLocatorForTopLevelTypeTest.FOO);
        Assert.assertThat(resolution.isResolved(), CoreMatchers.is(true));
        Assert.assertThat(resolution.getField(), CoreMatchers.is(((FieldDescription) (new FieldDescription.ForLoadedField(FieldLocatorForTopLevelTypeTest.Foo.class.getDeclaredField(FieldLocatorForTopLevelTypeTest.FOO))))));
    }

    @Test
    public void testExactTypeFoundWithType() throws Exception {
        FieldLocator.Resolution resolution = new FieldLocator.ForTopLevelType(of(FieldLocatorForTopLevelTypeTest.Foo.class)).locate(FieldLocatorForTopLevelTypeTest.FOO, of(Void.class));
        Assert.assertThat(resolution.isResolved(), CoreMatchers.is(true));
        Assert.assertThat(resolution.getField(), CoreMatchers.is(((FieldDescription) (new FieldDescription.ForLoadedField(FieldLocatorForTopLevelTypeTest.Foo.class.getDeclaredField(FieldLocatorForTopLevelTypeTest.FOO))))));
    }

    @Test
    public void testExactTypeNotFoundInherited() throws Exception {
        Assert.assertThat(new FieldLocator.ForTopLevelType(of(FieldLocatorForTopLevelTypeTest.Bar.class)).locate(FieldLocatorForTopLevelTypeTest.FOO).isResolved(), CoreMatchers.is(false));
    }

    @Test
    public void testExactTypeNotFoundNotExistent() throws Exception {
        Assert.assertThat(new FieldLocator.ForTopLevelType(of(FieldLocatorForTopLevelTypeTest.Foo.class)).locate(FieldLocatorForTopLevelTypeTest.QUX).isResolved(), CoreMatchers.is(false));
    }

    @Test
    public void testExactTypeNotFoundWrongType() throws Exception {
        Assert.assertThat(new FieldLocator.ForTopLevelType(of(FieldLocatorForTopLevelTypeTest.Foo.class)).locate(FieldLocatorForTopLevelTypeTest.FOO, of(Object.class)).isResolved(), CoreMatchers.is(false));
    }

    @Test
    public void testFactory() throws Exception {
        Assert.assertThat(INSTANCE.make(typeDescription), FieldByFieldComparison.hasPrototype(((FieldLocator) (new FieldLocator.ForTopLevelType(typeDescription)))));
    }

    @SuppressWarnings("unused")
    private static class Foo {
        private Void foo;

        protected Void bar;
    }

    @SuppressWarnings("unused")
    private static class Bar extends FieldLocatorForTopLevelTypeTest.Foo {
        protected Void bar;
    }
}

