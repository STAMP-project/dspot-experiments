/**
 * Copyright (c) 2017 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.assertj.core.api.Assertions;
import org.junit.Assume;
import org.junit.Test;


public class AnnotationsAreCopiedFromMockedTypeTest {
    @Test
    public void mock_should_have_annotations_copied_from_mocked_type_at_class_level() {
        AnnotationsAreCopiedFromMockedTypeTest.AnnotationWithDefaultValue onClassDefaultValue = Mockito.mock(AnnotationsAreCopiedFromMockedTypeTest.OnClass.class).getClass().getAnnotation(AnnotationsAreCopiedFromMockedTypeTest.AnnotationWithDefaultValue.class);
        AnnotationsAreCopiedFromMockedTypeTest.AnnotationWithCustomValue onClassCustomValue = Mockito.mock(AnnotationsAreCopiedFromMockedTypeTest.OnClass.class).getClass().getAnnotation(AnnotationsAreCopiedFromMockedTypeTest.AnnotationWithCustomValue.class);
        Assume.assumeTrue("Annotation copying does not apply for inline mocks", ((Mockito.mock(AnnotationsAreCopiedFromMockedTypeTest.OnClass.class).getClass()) != (AnnotationsAreCopiedFromMockedTypeTest.OnClass.class)));
        Assertions.assertThat(onClassDefaultValue.value()).isEqualTo("yup");
        Assertions.assertThat(onClassCustomValue.value()).isEqualTo("yay");
    }

    @Test
    public void mock_should_have_annotations_copied_from_mocked_type_on_methods() {
        AnnotationsAreCopiedFromMockedTypeTest.AnnotationWithDefaultValue onClassDefaultValue = method("method", Mockito.mock(AnnotationsAreCopiedFromMockedTypeTest.OnMethod.class)).getAnnotation(AnnotationsAreCopiedFromMockedTypeTest.AnnotationWithDefaultValue.class);
        AnnotationsAreCopiedFromMockedTypeTest.AnnotationWithCustomValue onClassCustomValue = method("method", Mockito.mock(AnnotationsAreCopiedFromMockedTypeTest.OnMethod.class)).getAnnotation(AnnotationsAreCopiedFromMockedTypeTest.AnnotationWithCustomValue.class);
        Assertions.assertThat(onClassDefaultValue.value()).isEqualTo("yup");
        Assertions.assertThat(onClassCustomValue.value()).isEqualTo("yay");
    }

    @Test
    public void mock_should_have_annotations_copied_from_mocked_type_on_method_parameters() {
        AnnotationsAreCopiedFromMockedTypeTest.AnnotationWithDefaultValue onClassDefaultValue = firstParamOf(method("method", Mockito.mock(AnnotationsAreCopiedFromMockedTypeTest.OnMethod.class))).getAnnotation(AnnotationsAreCopiedFromMockedTypeTest.AnnotationWithDefaultValue.class);
        AnnotationsAreCopiedFromMockedTypeTest.AnnotationWithCustomValue onClassCustomValue = firstParamOf(method("method", Mockito.mock(AnnotationsAreCopiedFromMockedTypeTest.OnMethod.class))).getAnnotation(AnnotationsAreCopiedFromMockedTypeTest.AnnotationWithCustomValue.class);
        Assertions.assertThat(onClassDefaultValue.value()).isEqualTo("yup");
        Assertions.assertThat(onClassCustomValue.value()).isEqualTo("yay");
    }

    @AnnotationsAreCopiedFromMockedTypeTest.AnnotationWithDefaultValue
    @AnnotationsAreCopiedFromMockedTypeTest.AnnotationWithCustomValue("yay")
    public class OnClass {}

    public class OnMethod {
        @AnnotationsAreCopiedFromMockedTypeTest.AnnotationWithDefaultValue
        @AnnotationsAreCopiedFromMockedTypeTest.AnnotationWithCustomValue("yay")
        public String method(@AnnotationsAreCopiedFromMockedTypeTest.AnnotationWithDefaultValue
        @AnnotationsAreCopiedFromMockedTypeTest.AnnotationWithCustomValue("yay")
        String ignored) {
            return "";
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD })
    public @interface AnnotationWithDefaultValue {
        String value() default "yup";
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD })
    public @interface AnnotationWithCustomValue {
        String value() default "";
    }
}

