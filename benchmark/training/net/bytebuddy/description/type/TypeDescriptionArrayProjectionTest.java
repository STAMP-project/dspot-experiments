package net.bytebuddy.description.type;


import net.bytebuddy.test.utility.JavaVersionRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.mockito.Mockito;

import static net.bytebuddy.description.type.TypeDescription.ArrayProjection.of;


public class TypeDescriptionArrayProjectionTest extends AbstractTypeDescriptionTest {
    @Rule
    public MethodRule javaVersionRule = new JavaVersionRule();

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalArity() throws Exception {
        of(Mockito.mock(TypeDescription.class), (-1));
    }

    @Test
    @JavaVersionRule.Enforce(9)
    public void testTypeAnnotationOwnerType() throws Exception {
        super.testTypeAnnotationOwnerType();
    }

    @Test
    @JavaVersionRule.Enforce(9)
    public void testGenericNestedTypeAnnotationReceiverTypeOnConstructor() throws Exception {
        super.testGenericNestedTypeAnnotationReceiverTypeOnConstructor();
    }

    @Test
    @JavaVersionRule.Enforce(9)
    public void testTypeAnnotationNonGenericInnerType() throws Exception {
        super.testTypeAnnotationNonGenericInnerType();
    }
}

