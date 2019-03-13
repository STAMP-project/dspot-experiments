package net.bytebuddy.description.type;


import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;

import static net.bytebuddy.description.type.TypeDescription.Generic.Visitor.NoOp.INSTANCE;


public class TypeDescriptionGenericVisitorNoOpTest {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private TypeDescription.Generic typeDescription;

    @Test
    public void testVisitGenericArray() throws Exception {
        MatcherAssert.assertThat(INSTANCE.onGenericArray(typeDescription), CoreMatchers.is(typeDescription));
    }

    @Test
    public void testVisitWildcard() throws Exception {
        MatcherAssert.assertThat(INSTANCE.onWildcard(typeDescription), CoreMatchers.is(typeDescription));
    }

    @Test
    public void testVisitParameterized() throws Exception {
        MatcherAssert.assertThat(INSTANCE.onParameterizedType(typeDescription), CoreMatchers.is(typeDescription));
    }

    @Test
    public void testVisitTypeVariable() throws Exception {
        MatcherAssert.assertThat(INSTANCE.onTypeVariable(typeDescription), CoreMatchers.is(typeDescription));
    }

    @Test
    public void testVisitNonGenericType() throws Exception {
        MatcherAssert.assertThat(INSTANCE.onNonGenericType(typeDescription), CoreMatchers.is(typeDescription));
    }
}

