package net.bytebuddy.description.type;


import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;

import static net.bytebuddy.description.type.TypeDescription.Generic.Visitor.TypeErasing.INSTANCE;


public class TypeDescriptionGenericVisitorTypeErasingTest {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private TypeDescription.Generic typeDescription;

    @Mock
    private TypeDescription.Generic rawType;

    @Test
    public void testGenericArray() throws Exception {
        MatcherAssert.assertThat(INSTANCE.onGenericArray(typeDescription), CoreMatchers.is(rawType));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWildcard() throws Exception {
        INSTANCE.onWildcard(typeDescription);
    }

    @Test
    public void testParameterized() throws Exception {
        MatcherAssert.assertThat(INSTANCE.onParameterizedType(typeDescription), CoreMatchers.is(rawType));
    }

    @Test
    public void testTypeVariable() throws Exception {
        MatcherAssert.assertThat(INSTANCE.onTypeVariable(typeDescription), CoreMatchers.is(rawType));
    }

    @Test
    public void testNonGeneric() throws Exception {
        MatcherAssert.assertThat(INSTANCE.onNonGenericType(typeDescription), CoreMatchers.is(rawType));
    }
}

