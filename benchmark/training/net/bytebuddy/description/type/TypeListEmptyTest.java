package net.bytebuddy.description.type;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public class TypeListEmptyTest {
    @Test
    public void testInternalName() throws Exception {
        MatcherAssert.assertThat(new TypeList.Empty().toInternalNames(), CoreMatchers.nullValue(String[].class));
    }

    @Test
    public void testSize() throws Exception {
        MatcherAssert.assertThat(new TypeList.Empty().getStackSize(), CoreMatchers.is(0));
    }
}

