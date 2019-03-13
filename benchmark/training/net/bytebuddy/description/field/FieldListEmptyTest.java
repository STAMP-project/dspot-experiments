package net.bytebuddy.description.field;


import net.bytebuddy.matcher.ElementMatchers;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public class FieldListEmptyTest {
    @Test
    public void testTokenListWithFilter() throws Exception {
        MatcherAssert.assertThat(new FieldList.Empty<FieldDescription>().asTokenList(ElementMatchers.none()).size(), CoreMatchers.is(0));
    }

    @Test
    public void testDeclaredList() throws Exception {
        MatcherAssert.assertThat(new FieldList.Empty<FieldDescription>().asDefined().size(), CoreMatchers.is(0));
    }
}

