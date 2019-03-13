package org.kie.dmn.feel.marshaller;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.dmn.feel.lang.Type;


@RunWith(Parameterized.class)
public class FEELCodeMarshallerUnmarshallTest {
    @Parameterized.Parameter(0)
    public Type feelType;

    @Parameterized.Parameter(1)
    public String value;

    @Parameterized.Parameter(2)
    public Object result;

    @Test
    public void testExpression() {
        assertResult(feelType, value, result);
    }
}

