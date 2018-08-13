package org.apache.ibatis.builder;


import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class AmplParameterExpressionTest {
    @Test(timeout = 10000)
    public void simpleProperty_add42961_add43347litString46452_failAssert363() throws Exception {
        try {
            Map<String, String> result = new ParameterExpression("(");
            int o_simpleProperty_add42961_add43347__3 = result.size();
            int o_simpleProperty_add42961__3 = result.size();
            String o_simpleProperty_add42961__4 = result.get("property");
            String o_simpleProperty_add42961__5 = result.get("property");
            org.junit.Assert.fail("simpleProperty_add42961_add43347litString46452 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: 2", expected.getMessage());
        }
    }
}

