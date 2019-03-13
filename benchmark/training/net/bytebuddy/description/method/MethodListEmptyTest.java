package net.bytebuddy.description.method;


import net.bytebuddy.matcher.ElementMatchers;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public class MethodListEmptyTest {
    @Test
    public void testTokenListWithFilter() throws Exception {
        MatcherAssert.assertThat(new MethodList.Empty<MethodDescription>().asTokenList(ElementMatchers.none()).size(), CoreMatchers.is(0));
    }

    @Test
    public void testDeclaredList() throws Exception {
        MatcherAssert.assertThat(new MethodList.Empty<MethodDescription>().asDefined().size(), CoreMatchers.is(0));
    }
}

