package net.bytebuddy.dynamic;


import net.bytebuddy.description.type.TypeDescription;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mockito;

import static net.bytebuddy.dynamic.Transformer.NoOp.INSTANCE;


public class TransformerNoOpTest {
    @Test
    public void testTransformation() throws Exception {
        Object target = Mockito.mock(Object.class);
        MatcherAssert.assertThat(INSTANCE.transform(Mockito.mock(TypeDescription.class), target), CoreMatchers.is(target));
    }
}

