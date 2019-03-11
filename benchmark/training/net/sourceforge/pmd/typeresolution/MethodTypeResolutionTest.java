/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.typeresolution;


import net.sourceforge.pmd.lang.java.typeresolution.MethodTypeResolution;
import net.sourceforge.pmd.lang.java.typeresolution.typedefinition.JavaTypeDefinition;
import org.junit.Assert;
import org.junit.Test;


public class MethodTypeResolutionTest {
    @Test
    public void testBoxingRules() {
        Assert.assertSame(Boolean.class, MethodTypeResolution.boxPrimitive(JavaTypeDefinition.forClass(boolean.class)).getType());
        Assert.assertSame(Double.class, MethodTypeResolution.boxPrimitive(JavaTypeDefinition.forClass(double.class)).getType());
        Assert.assertSame(Float.class, MethodTypeResolution.boxPrimitive(JavaTypeDefinition.forClass(float.class)).getType());
        Assert.assertSame(Long.class, MethodTypeResolution.boxPrimitive(JavaTypeDefinition.forClass(long.class)).getType());
        Assert.assertSame(Integer.class, MethodTypeResolution.boxPrimitive(JavaTypeDefinition.forClass(int.class)).getType());
        Assert.assertSame(Character.class, MethodTypeResolution.boxPrimitive(JavaTypeDefinition.forClass(char.class)).getType());
        Assert.assertSame(Short.class, MethodTypeResolution.boxPrimitive(JavaTypeDefinition.forClass(short.class)).getType());
        Assert.assertSame(Byte.class, MethodTypeResolution.boxPrimitive(JavaTypeDefinition.forClass(byte.class)).getType());
        Assert.assertSame(Void.class, MethodTypeResolution.boxPrimitive(JavaTypeDefinition.forClass(void.class)).getType());
    }
}

