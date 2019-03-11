package org.robolectric.annotation.processing.validator;


import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.robolectric.annotation.processing.RobolectricProcessor;
import org.robolectric.annotation.processing.Utils;


/**
 * Tests for {@link RealObjectValidator}
 */
@RunWith(JUnit4.class)
public class RealObjectValidatorTest {
    @Test
    public void realObjectWithoutImplements_shouldNotCompile() {
        final String testClass = "org.robolectric.annotation.processing.shadows.ShadowRealObjectWithoutImplements";
        assertAbout(SingleClassSubject.singleClass()).that(testClass).failsToCompile().withErrorContaining("@RealObject without @Implements").onLine(7);
    }

    @Test
    public void realObjectParameterizedMissingParameters_shouldNotCompile() {
        final String testClass = "org.robolectric.annotation.processing.shadows.ShadowRealObjectParameterizedMissingParameters";
        assertAbout(SingleClassSubject.singleClass()).that(testClass).failsToCompile().withErrorContaining("@RealObject is missing type parameters").onLine(11);
    }

    @Test
    public void realObjectParameterizedMismatch_shouldNotCompile() {
        final String testClass = "org.robolectric.annotation.processing.shadows.ShadowRealObjectParameterizedMismatch";
        assertAbout(SingleClassSubject.singleClass()).that(testClass).failsToCompile().withErrorContaining("Parameter type mismatch: expecting <T,S>, was <S,T>").onLine(11);
    }

    @Test
    public void realObjectWithEmptyImplements_shouldNotRaiseOwnError() {
        final String testClass = "org.robolectric.annotation.processing.shadows.ShadowRealObjectWithEmptyImplements";
        assertAbout(SingleClassSubject.singleClass()).that(testClass).failsToCompile().withNoErrorContaining("@RealObject");
    }

    @Test
    public void realObjectWithEmptyClassName_shouldNotRaiseOwnError() {
        final String testClass = "org.robolectric.annotation.processing.shadows.ShadowRealObjectWithEmptyClassName";
        assertAbout(SingleClassSubject.singleClass()).that(testClass).failsToCompile().withNoErrorContaining("@RealObject");
    }

    @Test
    public void realObjectWithTypeMismatch_shouldNotCompile() {
        final String testClass = "org.robolectric.annotation.processing.shadows.ShadowRealObjectWithWrongType";
        assertAbout(SingleClassSubject.singleClass()).that(testClass).failsToCompile().withErrorContaining("@RealObject with type <com.example.objects.UniqueDummy>; expected <com.example.objects.Dummy>").onLine(11);
    }

    @Test
    public void realObjectWithClassName_typeMismatch_shouldNotCompile() {
        final String testClass = "org.robolectric.annotation.processing.shadows.ShadowRealObjectWithIncorrectClassName";
        assertAbout(SingleClassSubject.singleClass()).that(testClass).failsToCompile().withErrorContaining("@RealObject with type <com.example.objects.UniqueDummy>; expected <com.example.objects.Dummy>").onLine(10);
    }

    @Test
    public void realObjectWithCorrectType_shouldCompile() {
        final String testClass = "org.robolectric.annotation.processing.shadows.ShadowRealObjectWithCorrectType";
        assertAbout(SingleClassSubject.singleClass()).that(testClass).compilesWithoutError();
    }

    @Test
    public void realObjectWithCorrectClassName_shouldCompile() {
        assertAbout(javaSources()).that(ImmutableList.of(Utils.SHADOW_EXTRACTOR_SOURCE, forResource("org/robolectric/annotation/processing/shadows/ShadowRealObjectWithCorrectClassName.java"))).processedWith(new RobolectricProcessor(Utils.DEFAULT_OPTS)).compilesWithoutError();
    }

    @Test
    public void realObjectWithNestedClassName_shouldCompile() {
        final String testClass = "org.robolectric.annotation.processing.shadows.ShadowRealObjectWithNestedClassName";
        assertAbout(SingleClassSubject.singleClass()).that(testClass).compilesWithoutError();
    }
}

