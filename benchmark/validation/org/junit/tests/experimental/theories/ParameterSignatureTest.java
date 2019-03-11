package org.junit.tests.experimental.theories;


import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.ParameterSignature;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.suppliers.TestedOn;
import org.junit.runner.RunWith;


@RunWith(Theories.class)
public class ParameterSignatureTest {
    @DataPoint
    public static int ZERO = 0;

    @DataPoint
    public static int ONE = 1;

    @Test
    public void getAnnotations() throws NoSuchMethodException, SecurityException {
        Method method = getClass().getMethod("foo", int.class);
        List<Annotation> annotations = ParameterSignature.signatures(method).get(0).getAnnotations();
        Assert.assertThat(annotations, CoreMatchers.<TestedOn>hasItem(CoreMatchers.isA(TestedOn.class)));
    }

    @Test
    public void primitiveTypesShouldBeAcceptedAsWrapperTypes() throws Exception {
        List<ParameterSignature> signatures = ParameterSignature.signatures(getClass().getMethod("integerMethod", Integer.class));
        ParameterSignature integerSignature = signatures.get(0);
        Assert.assertTrue(integerSignature.canAcceptType(int.class));
    }

    @Test
    public void primitiveTypesShouldBeAcceptedAsWrapperTypeAssignables() throws Exception {
        List<ParameterSignature> signatures = ParameterSignature.signatures(getClass().getMethod("numberMethod", Number.class));
        ParameterSignature numberSignature = signatures.get(0);
        Assert.assertTrue(numberSignature.canAcceptType(int.class));
    }

    @Test
    public void wrapperTypesShouldBeAcceptedAsPrimitiveTypes() throws Exception {
        List<ParameterSignature> signatures = ParameterSignature.signatures(getClass().getMethod("intMethod", int.class));
        ParameterSignature intSignature = signatures.get(0);
        Assert.assertTrue(intSignature.canAcceptType(Integer.class));
    }
}

