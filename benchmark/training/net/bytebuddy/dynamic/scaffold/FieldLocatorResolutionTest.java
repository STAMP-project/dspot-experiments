package net.bytebuddy.dynamic.scaffold;


import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;

import static net.bytebuddy.dynamic.scaffold.FieldLocator.Resolution.Illegal.INSTANCE;


public class FieldLocatorResolutionTest {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private FieldDescription fieldDescription;

    @Test
    public void testSimpleResolutionResolved() throws Exception {
        MatcherAssert.assertThat(new FieldLocator.Resolution.Simple(fieldDescription).isResolved(), CoreMatchers.is(true));
    }

    @Test
    public void testSimpleResolutionFieldDescription() throws Exception {
        MatcherAssert.assertThat(new FieldLocator.Resolution.Simple(fieldDescription).getField(), CoreMatchers.is(fieldDescription));
    }

    @Test
    public void testIllegalResolutionUnresolved() throws Exception {
        MatcherAssert.assertThat(INSTANCE.isResolved(), CoreMatchers.is(false));
    }

    @Test(expected = IllegalStateException.class)
    public void testIllegalResolutionFieldDescription() throws Exception {
        INSTANCE.getField();
    }
}

