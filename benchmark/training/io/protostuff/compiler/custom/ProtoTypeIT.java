package io.protostuff.compiler.custom;


import io.protostuff.compiler.it.custom.JavaBeanWithProtoDescriptor;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Kostiantyn Shchepanovskyi
 */
public class ProtoTypeIT {
    private static final String EXPECTED = "message JavaBeanWithProtoDescriptor {\n" + ((("    optional int32 a = 1;\n" + "    optional int64 b = 2;\n") + "    optional E e = 3;\n") + "}\n");

    @Test
    public void testProtoDescriptor() throws Exception {
        Assert.assertEquals(ProtoTypeIT.EXPECTED, JavaBeanWithProtoDescriptor.getProtoDefinition());
    }
}

