package io.github.classgraph.issues;


import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassRefTypeSignature;
import io.github.classgraph.FieldInfoList;
import io.github.classgraph.ScanResult;
import org.junit.Test;


/**
 * The Class GenericInnerClassTypedField.
 */
public class GenericInnerClassTypedField {
    /**
     * The Class A.
     *
     * @param <X>
     * 		the generic type
     * @param <Y>
     * 		the generic type
     */
    private static class A<X, Y> {
        /**
         * The Class B.
         */
        private class B {}
    }

    /**
     * The field.
     */
    GenericInnerClassTypedField.A<Integer, String>.B field;

    /**
     * Test generic inner class typed field.
     */
    @Test
    public void testGenericInnerClassTypedField() {
        try (ScanResult scanResult = new ClassGraph().whitelistPackages(GenericInnerClassTypedField.class.getPackage().getName()).enableAllInfo().scan()) {
            final FieldInfoList fields = scanResult.getClassInfo(GenericInnerClassTypedField.class.getName()).getFieldInfo();
            final ClassRefTypeSignature classRefTypeSignature = ((ClassRefTypeSignature) (fields.get(0).getTypeSignature()));
            assertThat(classRefTypeSignature.toString()).isEqualTo(((((((GenericInnerClassTypedField.A.class.getName()) + "<") + (Integer.class.getName())) + ", ") + (String.class.getName())) + ">.B"));
            assertThat(classRefTypeSignature.getFullyQualifiedClassName()).isEqualTo(((GenericInnerClassTypedField.A.class.getName()) + "$B"));
        }
    }
}

