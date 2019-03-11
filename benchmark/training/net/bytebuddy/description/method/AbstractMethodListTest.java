package net.bytebuddy.description.method;


import java.util.Collections;
import net.bytebuddy.description.ByteCodeElement;
import net.bytebuddy.matcher.AbstractFilterableListTest;
import net.bytebuddy.matcher.ElementMatchers;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public abstract class AbstractMethodListTest<U, V extends MethodDescription> extends AbstractFilterableListTest<V, MethodList<V>, U> {
    @Test
    @SuppressWarnings("unchecked")
    public void testTokenWithMatcher() throws Exception {
        MatcherAssert.assertThat(asList(getFirst()).asTokenList(ElementMatchers.none()), CoreMatchers.is(new ByteCodeElement.Token.TokenList<MethodDescription.Token>(asElement(getFirst()).asToken(ElementMatchers.none()))));
    }

    @Test
    public void testAsDefined() throws Exception {
        MatcherAssert.assertThat(asList(getFirst()).asDefined(), CoreMatchers.is(Collections.singletonList(asElement(getFirst()).asDefined())));
    }

    public abstract static class Foo {
        abstract void foo();

        abstract void bar();
    }
}

