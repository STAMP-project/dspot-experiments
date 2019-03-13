package org.kie.dmn.feel.marshaller;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class FEELCodeMarshallerTest {
    @Parameterized.Parameter(0)
    public Object value;

    @Parameterized.Parameter(1)
    public String result;

    @Test
    public void testExpression() {
        assertResult(value, result);
    }
}

