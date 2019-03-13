package net.bytebuddy.dynamic.scaffold;


import net.bytebuddy.description.type.TypeDescription;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static net.bytebuddy.dynamic.scaffold.FieldLocator.NoOp.INSTANCE;


public class FieldLocatorNoOpTest {
    private static final String FOO = "foo";

    @Test
    public void testCannotLocateWithoutType() throws Exception {
        MatcherAssert.assertThat(INSTANCE.locate(FieldLocatorNoOpTest.FOO).isResolved(), CoreMatchers.is(false));
    }

    @Test
    public void testCannotLocateWithType() throws Exception {
        MatcherAssert.assertThat(INSTANCE.locate(FieldLocatorNoOpTest.FOO, TypeDescription.OBJECT).isResolved(), CoreMatchers.is(false));
    }

    @Test
    public void testFactory() throws Exception {
        MatcherAssert.assertThat(INSTANCE.make(TypeDescription.OBJECT), CoreMatchers.is(((FieldLocator) (INSTANCE))));
    }
}

