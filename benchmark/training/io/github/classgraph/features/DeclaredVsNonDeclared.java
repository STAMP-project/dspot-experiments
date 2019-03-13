package io.github.classgraph.features;


import io.github.classgraph.AnnotationInfo;
import io.github.classgraph.AnnotationInfoList;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import org.assertj.core.api.iterable.Extractor;
import org.junit.Test;


/**
 * The Class DeclaredVsNonDeclared.
 */
public class DeclaredVsNonDeclared {
    /**
     * The Class A.
     */
    @NormalAnnotation
    @InheritedAnnotation
    public abstract static class A {
        /**
         * The x.
         */
        float x;

        /**
         * The z.
         */
        String z;

        /**
         * Y.
         *
         * @param x
         * 		the x
         * @param y
         * 		the y
         */
        abstract void y(int x, int y);

        /**
         * Y.
         *
         * @param x
         * 		the x
         */
        abstract void y(String x);

        /**
         * Y.
         *
         * @param x
         * 		the x
         */
        abstract void y(Integer x);

        /**
         * W.
         */
        @InheritedAnnotation
        abstract void w();
    }

    /**
     * The Class B.
     */
    public abstract static class B extends DeclaredVsNonDeclared.A {
        /**
         * The x.
         */
        int x;

        /* (non-Javadoc)
        @see io.github.classgraph.features.DeclaredVsNonDeclared.A#y(int, int)
         */
        @Override
        void y(final int x, final int y) {
        }

        /* (non-Javadoc)
        @see io.github.classgraph.features.DeclaredVsNonDeclared.A#w()
         */
        @Override
        void w() {
        }
    }

    /**
     * The Class C.
     */
    @NormalAnnotation
    public abstract static class C extends DeclaredVsNonDeclared.A {}

    /**
     * Declared vs non declared methods.
     */
    @Test
    public void declaredVsNonDeclaredMethods() {
        try (ScanResult scanResult = new ClassGraph().enableAllInfo().whitelistPackages(DeclaredVsNonDeclared.class.getPackage().getName()).scan()) {
            final ClassInfo A = scanResult.getClassInfo(DeclaredVsNonDeclared.A.class.getName());
            final ClassInfo B = scanResult.getClassInfo(DeclaredVsNonDeclared.B.class.getName());
            assertThat(B.getFieldInfo("x").getClassInfo().getName()).isEqualTo(DeclaredVsNonDeclared.B.class.getName());
            assertThat(B.getFieldInfo("z").getClassInfo().getName()).isEqualTo(DeclaredVsNonDeclared.A.class.getName());
            assertThat(A.getFieldInfo().get(0).getTypeDescriptor().toString()).isEqualTo("float");
            assertThat(B.getFieldInfo().get(0).getTypeDescriptor().toString()).isEqualTo("int");
            assertThat(B.getMethodInfo().toString()).isEqualTo("[void y(int, int), void w(), abstract void y(java.lang.String), abstract void y(java.lang.Integer)]");
            assertThat(B.getDeclaredMethodInfo().toString()).isEqualTo("[void y(int, int), void w()]");
        }
    }

    /**
     * Annotation infos should be able to differentiate between direct and reachable.
     */
    @Test
    public void annotationInfosShouldBeAbleToDifferentiateBetweenDirectAndReachable() {
        final Extractor<AnnotationInfo, Object> annotationNameExtractor = new Extractor<AnnotationInfo, Object>() {
            @Override
            public Object extract(final AnnotationInfo input) {
                return input.getName();
            }
        };
        try (ScanResult scanResult = new ClassGraph().enableAllInfo().whitelistPackages(DeclaredVsNonDeclared.class.getPackage().getName()).scan()) {
            final ClassInfo A = scanResult.getClassInfo(DeclaredVsNonDeclared.A.class.getName());
            final ClassInfo B = scanResult.getClassInfo(DeclaredVsNonDeclared.B.class.getName());
            final ClassInfo C = scanResult.getClassInfo(DeclaredVsNonDeclared.C.class.getName());
            final AnnotationInfoList annotationInfossOnA = A.getAnnotationInfo();
            final AnnotationInfoList annotationsInfosOnB = B.getAnnotationInfo();
            assertThat(annotationInfossOnA).extracting(annotationNameExtractor).containsExactlyInAnyOrder(InheritedAnnotation.class.getName(), NormalAnnotation.class.getName(), InheritedMetaAnnotation.class.getName(), NonInheritedMetaAnnotation.class.getName());
            assertThat(annotationsInfosOnB).extracting(annotationNameExtractor).containsExactlyInAnyOrder(InheritedAnnotation.class.getName(), InheritedMetaAnnotation.class.getName());
            assertThat(annotationInfossOnA.directOnly()).extracting(annotationNameExtractor).containsExactlyInAnyOrder(NormalAnnotation.class.getName(), InheritedAnnotation.class.getName());
            assertThat(annotationsInfosOnB.directOnly()).isEmpty();
            assertThat(C.getAnnotationInfo().directOnly()).extracting(annotationNameExtractor).containsExactlyInAnyOrder(NormalAnnotation.class.getName());
            final AnnotationInfoList annotationsOnAw = A.getMethodInfo().getSingleMethod("w").getAnnotationInfo();
            assertThat(annotationsOnAw).extracting(annotationNameExtractor).containsExactlyInAnyOrder(InheritedAnnotation.class.getName(), InheritedMetaAnnotation.class.getName(), NonInheritedMetaAnnotation.class.getName());
            final AnnotationInfoList annotationsOnBw = B.getMethodInfo().getSingleMethod("w").getAnnotationInfo();
            assertThat(annotationsOnBw).extracting(annotationNameExtractor).isEmpty();
            // See note on inherited annotations on methods
            // https://docs.oracle.com/javase/8/docs/api/java/lang/annotation/Inherited.html
            // "Note that this (@Inherited) meta-annotation type has no effect if the annotated type is used to
            // annotate anything other than a class. Note also that this meta-annotation only causes annotations
            // to be inherited from superclasses; annotations on implemented interfaces have no effect."
            assertThat(annotationsOnBw.directOnly()).extracting(annotationNameExtractor).isEmpty();
        }
    }

    /**
     * Annotations should be able to differentiate between direct and reachable.
     */
    @Test
    public void annotationsShouldBeAbleToDifferentiateBetweenDirectAndReachable() {
        try (ScanResult scanResult = new ClassGraph().enableAllInfo().whitelistPackages(DeclaredVsNonDeclared.class.getPackage().getName()).scan()) {
            final ClassInfo A = scanResult.getClassInfo(DeclaredVsNonDeclared.A.class.getName());
            final ClassInfo B = scanResult.getClassInfo(DeclaredVsNonDeclared.B.class.getName());
            final ClassInfoList annotationsOnA = A.getAnnotations();
            final ClassInfoList annotationsOnB = B.getAnnotations();
            assertThat(annotationsOnA.loadClasses()).containsExactlyInAnyOrder(NormalAnnotation.class, InheritedAnnotation.class, InheritedMetaAnnotation.class, NonInheritedMetaAnnotation.class);
            assertThat(annotationsOnB.loadClasses()).containsExactlyInAnyOrder(InheritedAnnotation.class, InheritedMetaAnnotation.class);
            assertThat(annotationsOnA.directOnly().loadClasses()).containsExactlyInAnyOrder(NormalAnnotation.class, InheritedAnnotation.class);
            assertThat(annotationsOnB.directOnly()).isEmpty();
        }
    }

    /**
     * Load field and method.
     */
    @Test
    public void loadFieldAndMethod() {
        try (ScanResult scanResult = new ClassGraph().enableAllInfo().whitelistPackages(DeclaredVsNonDeclared.class.getPackage().getName()).scan()) {
            final ClassInfo B = scanResult.getClassInfo(DeclaredVsNonDeclared.B.class.getName());
            assertThat(B.getFieldInfo("x").loadClassAndGetField().getName()).isEqualTo("x");
            assertThat(B.getMethodInfo("y").get(0).loadClassAndGetMethod().getName()).isEqualTo("y");
        }
    }
}

