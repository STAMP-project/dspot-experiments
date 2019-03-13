/**
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.internal.model.common;


import JodaTimeConstants.DATE_TIME_FQN;
import JodaTimeConstants.LOCAL_DATE_FQN;
import JodaTimeConstants.LOCAL_DATE_TIME_FQN;
import JodaTimeConstants.LOCAL_TIME_FQN;
import java.lang.annotation.Annotation;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.List;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVisitor;
import org.junit.Test;
import org.mapstruct.ap.testutil.IssueKey;


/**
 * Tests for {@link org.mapstruct.ap.internal.model.common.DateFormatValidatorFactory}.
 *
 * @author Timo Eckhardt
 */
@IssueKey("224")
public class DateFormatValidatorFactoryTest {
    private static final String JAVA_LANG_STRING = "java.lang.String";

    private TypeMirror voidTypeMirror = new TypeMirror() {
        @Override
        public List<? extends AnnotationMirror> getAnnotationMirrors() {
            return null;
        }

        @Override
        public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
            return null;
        }

        @Override
        public <A extends Annotation> A[] getAnnotationsByType(Class<A> annotationType) {
            return null;
        }

        @Override
        public TypeKind getKind() {
            return TypeKind.VOID;
        }

        @Override
        public <R, P> R accept(TypeVisitor<R, P> v, P p) {
            return null;
        }
    };

    @Test
    public void testUnsupportedTypes() {
        Type sourceType = typeWithFQN(DateFormatValidatorFactoryTest.JAVA_LANG_STRING);
        Type targetType = typeWithFQN(DateFormatValidatorFactoryTest.JAVA_LANG_STRING);
        DateFormatValidator dateFormatValidator = DateFormatValidatorFactory.forTypes(sourceType, targetType);
        assertThat(dateFormatValidator.validate("XXXX").isValid()).isTrue();
    }

    @Test
    public void testJavaUtilDateValidator() {
        Type sourceType = typeWithFQN("java.util.Date");
        Type targetType = typeWithFQN(DateFormatValidatorFactoryTest.JAVA_LANG_STRING);
        assertInvalidDateFormat(sourceType, targetType);
        assertInvalidDateFormat(targetType, sourceType);
        assertValidDateFormat(sourceType, targetType);
        assertValidDateFormat(targetType, sourceType);
    }

    @Test
    public void testJodaTimeValidator() {
        Type targetType = typeWithFQN(DateFormatValidatorFactoryTest.JAVA_LANG_STRING);
        Type sourceType = typeWithFQN(DATE_TIME_FQN);
        assertInvalidDateFormat(sourceType, targetType);
        assertInvalidDateFormat(targetType, sourceType);
        assertValidDateFormat(sourceType, targetType);
        assertValidDateFormat(targetType, sourceType);
        sourceType = typeWithFQN(LOCAL_DATE_FQN);
        assertInvalidDateFormat(sourceType, targetType);
        assertInvalidDateFormat(targetType, sourceType);
        assertValidDateFormat(sourceType, targetType);
        assertValidDateFormat(targetType, sourceType);
        sourceType = typeWithFQN(LOCAL_DATE_TIME_FQN);
        assertInvalidDateFormat(sourceType, targetType);
        assertInvalidDateFormat(targetType, sourceType);
        assertValidDateFormat(sourceType, targetType);
        assertValidDateFormat(targetType, sourceType);
        sourceType = typeWithFQN(LOCAL_TIME_FQN);
        assertInvalidDateFormat(sourceType, targetType);
        assertInvalidDateFormat(targetType, sourceType);
        assertValidDateFormat(sourceType, targetType);
        assertValidDateFormat(targetType, sourceType);
    }

    @Test
    public void testJavaTimeValidator() {
        Type targetType = typeWithFQN(DateFormatValidatorFactoryTest.JAVA_LANG_STRING);
        Type sourceType = typeWithFQN(ZonedDateTime.class.getCanonicalName());
        assertInvalidDateFormat(sourceType, targetType);
        assertInvalidDateFormat(targetType, sourceType);
        assertValidDateFormat(sourceType, targetType);
        assertValidDateFormat(targetType, sourceType);
        sourceType = typeWithFQN(LocalDate.class.getCanonicalName());
        assertInvalidDateFormat(sourceType, targetType);
        assertInvalidDateFormat(targetType, sourceType);
        assertValidDateFormat(sourceType, targetType);
        assertValidDateFormat(targetType, sourceType);
        sourceType = typeWithFQN(LocalDateTime.class.getCanonicalName());
        assertInvalidDateFormat(sourceType, targetType);
        assertInvalidDateFormat(targetType, sourceType);
        assertValidDateFormat(sourceType, targetType);
        assertValidDateFormat(targetType, sourceType);
        sourceType = typeWithFQN(LocalTime.class.getCanonicalName());
        assertInvalidDateFormat(sourceType, targetType);
        assertInvalidDateFormat(targetType, sourceType);
        assertValidDateFormat(sourceType, targetType);
        assertValidDateFormat(targetType, sourceType);
    }
}

