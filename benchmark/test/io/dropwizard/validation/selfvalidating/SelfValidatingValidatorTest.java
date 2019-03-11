package io.dropwizard.validation.selfvalidating;


import io.dropwizard.validation.BaseValidator;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import org.junit.jupiter.api.Test;


public class SelfValidatingValidatorTest {
    private SelfValidatingValidator selfValidatingValidator = new SelfValidatingValidator();

    @Test
    public void validObjectHasNoViolations() throws Exception {
        final Validator validator = BaseValidator.newValidator();
        final Set<ConstraintViolation<SelfValidatingValidatorTest.ValidExample>> violations = validator.validate(new SelfValidatingValidatorTest.ValidExample(1));
        assertThat(violations).isEmpty();
    }

    @Test
    public void invalidObjectHasViolations() throws Exception {
        final Validator validator = BaseValidator.newValidator();
        final Set<ConstraintViolation<SelfValidatingValidatorTest.ValidExample>> violations = validator.validate(new SelfValidatingValidatorTest.ValidExample((-1)));
        assertThat(violations).isNotEmpty().allSatisfy(( violation) -> assertThat(violation.getMessage()).isEqualTo("n must be positive!"));
    }

    @Test
    public void correctMethod() throws Exception {
        assertThat(selfValidatingValidator.isMethodCorrect(getMethod("validateCorrect", ViolationCollector.class))).isTrue();
    }

    @Test
    public void voidIsNotAccepted() throws Exception {
        assertThat(selfValidatingValidator.isMethodCorrect(getMethod("validateFailReturn", ViolationCollector.class))).isFalse();
    }

    @Test
    public void privateIsNotAccepted() throws Exception {
        assertThat(selfValidatingValidator.isMethodCorrect(getMethod("validateFailPrivate", ViolationCollector.class))).isFalse();
    }

    @Test
    public void additionalParametersAreNotAccepted() throws Exception {
        assertThat(selfValidatingValidator.isMethodCorrect(getMethod("validateFailAdditionalParameters", ViolationCollector.class, int.class))).isFalse();
    }

    @SelfValidating
    static class InvalidExample {
        @SelfValidation
        public void validateCorrect(ViolationCollector col) {
        }

        @SelfValidation
        public void validateFailAdditionalParameters(ViolationCollector col, int a) {
        }

        @SelfValidation
        public boolean validateFailReturn(ViolationCollector col) {
            return true;
        }

        @SelfValidation
        private void validateFailPrivate(ViolationCollector col) {
        }
    }

    @SelfValidating
    static class ValidExample {
        final int n;

        ValidExample(int n) {
            this.n = n;
        }

        @SelfValidation
        public void validate(ViolationCollector col) {
            if ((n) < 0) {
                col.addViolation("n must be positive!");
            }
        }
    }
}

