package net.bytebuddy.description.type;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static net.bytebuddy.description.type.TypeDescription.Generic.Visitor.NoOp.INSTANCE;


public class TypeListGenericEmptyTest {
    @Test
    public void testRawTypes() throws Exception {
        MatcherAssert.assertThat(new TypeList.Generic.Empty().asErasures().size(), CoreMatchers.is(0));
    }

    @Test
    public void testVisitor() throws Exception {
        MatcherAssert.assertThat(new TypeList.Generic.Empty().accept(INSTANCE).size(), CoreMatchers.is(0));
    }

    @Test
    public void testSize() throws Exception {
        MatcherAssert.assertThat(new TypeList.Generic.Empty().getStackSize(), CoreMatchers.is(0));
    }
}

