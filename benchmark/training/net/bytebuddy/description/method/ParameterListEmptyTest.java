package net.bytebuddy.description.method;


import net.bytebuddy.matcher.ElementMatchers;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public class ParameterListEmptyTest {
    @Test
    public void testTokenListWithFilter() throws Exception {
        MatcherAssert.assertThat(new ParameterList.Empty<ParameterDescription>().asTokenList(ElementMatchers.none()).size(), CoreMatchers.is(0));
    }

    @Test
    public void testTokenListMetaData() throws Exception {
        MatcherAssert.assertThat(new ParameterList.Empty<ParameterDescription>().hasExplicitMetaData(), CoreMatchers.is(true));
    }

    @Test
    public void testTypeList() throws Exception {
        MatcherAssert.assertThat(new ParameterList.Empty<ParameterDescription>().asTypeList().size(), CoreMatchers.is(0));
    }

    @Test
    public void testDeclaredList() throws Exception {
        MatcherAssert.assertThat(new ParameterList.Empty<ParameterDescription>().asDefined().size(), CoreMatchers.is(0));
    }
}

