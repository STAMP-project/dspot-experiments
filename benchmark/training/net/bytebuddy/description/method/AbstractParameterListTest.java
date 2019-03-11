package net.bytebuddy.description.method;


import java.util.Collections;
import net.bytebuddy.matcher.AbstractFilterableListTest;
import net.bytebuddy.matcher.ElementMatchers;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static net.bytebuddy.description.type.TypeDefinition.Sort.describe;


public abstract class AbstractParameterListTest<U extends ParameterDescription, V> extends AbstractFilterableListTest<U, ParameterList<U>, V> {
    @Test
    @SuppressWarnings("unchecked")
    public void testTokenWithMatcher() throws Exception {
        MatcherAssert.assertThat(asList(getFirst()).asTokenList(ElementMatchers.none()).size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(asList(getFirst()).asTokenList(ElementMatchers.none()).getOnly().getType(), CoreMatchers.is(describe(Void.class)));
    }

    @Test
    public void testTypeList() throws Exception {
        MatcherAssert.assertThat(asList(getFirst()).asTypeList(), CoreMatchers.is(Collections.singletonList(asElement(getFirst()).getType())));
    }

    @Test
    public void testDeclared() throws Exception {
        MatcherAssert.assertThat(asList(getFirst()).asDefined(), CoreMatchers.is(Collections.singletonList(asElement(getFirst()).asDefined())));
    }

    protected static class Foo {
        public void foo(Void v) {
            /* empty */
        }

        public void bar(Void v) {
            /* empty */
        }
    }
}

