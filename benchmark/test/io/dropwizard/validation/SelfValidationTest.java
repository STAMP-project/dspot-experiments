package io.dropwizard.validation;


import io.dropwizard.validation.selfvalidating.SelfValidating;
import io.dropwizard.validation.selfvalidating.SelfValidation;
import io.dropwizard.validation.selfvalidating.ViolationCollector;
import javax.annotation.concurrent.NotThreadSafe;
import javax.validation.Validator;
import org.junit.jupiter.api.Test;
import uk.org.lidalia.slf4jext.Level;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;


@NotThreadSafe
public class SelfValidationTest {
    private static final String FAILED = "failed";

    private static final String FAILED_RESULT = " " + (SelfValidationTest.FAILED);

    @SelfValidating
    public static class FailingExample {
        @SelfValidation
        public void validateFail(ViolationCollector col) {
            col.addViolation(SelfValidationTest.FAILED);
        }
    }

    public static class SubclassExample extends SelfValidationTest.FailingExample {
        @SelfValidation
        public void subValidateFail(ViolationCollector col) {
            col.addViolation(((SelfValidationTest.FAILED) + "subclass"));
        }
    }

    @SelfValidating
    public static class AnnotatedSubclassExample extends SelfValidationTest.FailingExample {
        @SelfValidation
        public void subValidateFail(ViolationCollector col) {
            col.addViolation(((SelfValidationTest.FAILED) + "subclass"));
        }
    }

    public static class OverridingExample extends SelfValidationTest.FailingExample {
        @Override
        public void validateFail(ViolationCollector col) {
        }
    }

    @SelfValidating
    public static class DirectContextExample {
        @SelfValidation
        public void validateFail(ViolationCollector col) {
            col.getContext().buildConstraintViolationWithTemplate(SelfValidationTest.FAILED).addConstraintViolation();
            col.setViolationOccurred(true);
        }
    }

    @SelfValidating
    public static class CorrectExample {
        @SuppressWarnings("unused")
        @SelfValidation
        public void validateCorrect(ViolationCollector col) {
        }
    }

    @SelfValidating
    public static class InvalidExample {
        @SuppressWarnings("unused")
        @SelfValidation
        public void validateCorrect(ViolationCollector col) {
        }

        @SuppressWarnings("unused")
        @SelfValidation
        public void validateFailAdditionalParameters(ViolationCollector col, int a) {
            col.addViolation(SelfValidationTest.FAILED);
        }

        @SelfValidation
        public boolean validateFailReturn(ViolationCollector col) {
            col.addViolation(SelfValidationTest.FAILED);
            return true;
        }

        @SelfValidation
        private void validateFailPrivate(ViolationCollector col) {
            col.addViolation(SelfValidationTest.FAILED);
        }
    }

    @SelfValidating
    public static class ComplexExample {
        @SelfValidation
        public void validateFail1(ViolationCollector col) {
            col.addViolation(((SelfValidationTest.FAILED) + "1"));
        }

        @SelfValidation
        public void validateFail2(ViolationCollector col) {
            col.addViolation(((SelfValidationTest.FAILED) + "2"));
        }

        @SelfValidation
        public void validateFail3(ViolationCollector col) {
            col.addViolation(((SelfValidationTest.FAILED) + "3"));
        }

        @SuppressWarnings("unused")
        @SelfValidation
        public void validateCorrect(ViolationCollector col) {
        }
    }

    @SelfValidating
    public static class NoValidations {}

    private final Validator validator = BaseValidator.newValidator();

    @Test
    public void failingExample() throws Exception {
        assertThat(ConstraintViolations.format(validator.validate(new SelfValidationTest.FailingExample()))).containsExactlyInAnyOrder(SelfValidationTest.FAILED_RESULT);
        assertThat(TestLoggerFactory.getAllLoggingEvents()).isEmpty();
    }

    @Test
    public void subClassExample() throws Exception {
        assertThat(ConstraintViolations.format(validator.validate(new SelfValidationTest.SubclassExample()))).containsExactlyInAnyOrder(SelfValidationTest.FAILED_RESULT, ((SelfValidationTest.FAILED_RESULT) + "subclass"));
        assertThat(TestLoggerFactory.getAllLoggingEvents()).isEmpty();
    }

    @Test
    public void annotatedSubClassExample() throws Exception {
        assertThat(ConstraintViolations.format(validator.validate(new SelfValidationTest.AnnotatedSubclassExample()))).containsExactlyInAnyOrder(SelfValidationTest.FAILED_RESULT, ((SelfValidationTest.FAILED_RESULT) + "subclass"));
        assertThat(TestLoggerFactory.getAllLoggingEvents()).isEmpty();
    }

    @Test
    public void overridingSubClassExample() throws Exception {
        assertThat(ConstraintViolations.format(validator.validate(new SelfValidationTest.OverridingExample()))).isEmpty();
        assertThat(TestLoggerFactory.getAllLoggingEvents()).isEmpty();
    }

    @Test
    public void correctExample() throws Exception {
        assertThat(ConstraintViolations.format(validator.validate(new SelfValidationTest.CorrectExample()))).isEmpty();
        assertThat(TestLoggerFactory.getAllLoggingEvents()).isEmpty();
    }

    @Test
    public void multipleTestingOfSameClass() throws Exception {
        assertThat(ConstraintViolations.format(validator.validate(new SelfValidationTest.CorrectExample()))).isEmpty();
        assertThat(ConstraintViolations.format(validator.validate(new SelfValidationTest.CorrectExample()))).isEmpty();
        assertThat(TestLoggerFactory.getAllLoggingEvents()).isEmpty();
    }

    @Test
    public void testDirectContextUsage() throws Exception {
        assertThat(ConstraintViolations.format(validator.validate(new SelfValidationTest.DirectContextExample()))).containsExactlyInAnyOrder(SelfValidationTest.FAILED_RESULT);
        assertThat(TestLoggerFactory.getAllLoggingEvents()).isEmpty();
    }

    @Test
    public void complexExample() throws Exception {
        assertThat(ConstraintViolations.format(validator.validate(new SelfValidationTest.ComplexExample()))).containsExactlyInAnyOrder(((SelfValidationTest.FAILED_RESULT) + "1"), ((SelfValidationTest.FAILED_RESULT) + "2"), ((SelfValidationTest.FAILED_RESULT) + "3"));
        assertThat(TestLoggerFactory.getAllLoggingEvents()).isEmpty();
    }

    @Test
    public void invalidExample() throws Exception {
        assertThat(ConstraintViolations.format(validator.validate(new SelfValidationTest.InvalidExample()))).isEmpty();
        assertThat(TestLoggerFactory.getAllLoggingEvents()).containsExactlyInAnyOrder(new uk.org.lidalia.slf4jtest.LoggingEvent(Level.ERROR, "The method {} is annotated with @SelfValidation but does not have a single parameter of type {}", SelfValidationTest.InvalidExample.class.getMethod("validateFailAdditionalParameters", ViolationCollector.class, int.class), ViolationCollector.class), new uk.org.lidalia.slf4jtest.LoggingEvent(Level.ERROR, "The method {} is annotated with @SelfValidation but does not return void. It is ignored", SelfValidationTest.InvalidExample.class.getMethod("validateFailReturn", ViolationCollector.class)), new uk.org.lidalia.slf4jtest.LoggingEvent(Level.ERROR, "The method {} is annotated with @SelfValidation but is not public", SelfValidationTest.InvalidExample.class.getDeclaredMethod("validateFailPrivate", ViolationCollector.class)));
    }

    @Test
    public void giveWarningIfNoValidationMethods() throws Exception {
        assertThat(ConstraintViolations.format(validator.validate(new SelfValidationTest.NoValidations()))).isEmpty();
        assertThat(TestLoggerFactory.getAllLoggingEvents()).containsExactlyInAnyOrder(new uk.org.lidalia.slf4jtest.LoggingEvent(Level.WARN, "The class {} is annotated with @SelfValidating but contains no valid methods that are annotated with @SelfValidation", SelfValidationTest.NoValidations.class));
    }
}

