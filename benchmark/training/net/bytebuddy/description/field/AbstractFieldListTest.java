package net.bytebuddy.description.field;


import java.util.Collections;
import net.bytebuddy.description.ByteCodeElement;
import net.bytebuddy.matcher.AbstractFilterableListTest;
import net.bytebuddy.matcher.ElementMatchers;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public abstract class AbstractFieldListTest<U, V extends FieldDescription> extends AbstractFilterableListTest<V, FieldList<V>, U> {
    @Test
    @SuppressWarnings("unchecked")
    public void testTokenWithMatcher() throws Exception {
        MatcherAssert.assertThat(asList(getFirst()).asTokenList(ElementMatchers.none()), CoreMatchers.is(new ByteCodeElement.Token.TokenList<FieldDescription.Token>(asElement(getFirst()).asToken(ElementMatchers.none()))));
    }

    @Test
    public void testAsDefined() throws Exception {
        MatcherAssert.assertThat(asList(getFirst()).asDefined(), CoreMatchers.is(Collections.singletonList(asElement(getFirst()).asDefined())));
    }

    protected static class Foo {
        Void foo;

        Void bar;
    }
}

