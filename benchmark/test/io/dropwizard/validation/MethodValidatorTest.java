package io.dropwizard.validation;


import java.util.Collection;
import javax.validation.Valid;
import javax.validation.Validator;
import org.junit.jupiter.api.Test;


@SuppressWarnings({ "FieldMayBeFinal", "MethodMayBeStatic", "UnusedDeclaration" })
public class MethodValidatorTest {
    public static class SubExample {
        @ValidationMethod(message = "also needs something special")
        public boolean isOK() {
            return false;
        }
    }

    public static class Example {
        @Valid
        private MethodValidatorTest.SubExample subExample = new MethodValidatorTest.SubExample();

        @ValidationMethod(message = "must have a false thing")
        public boolean isFalse() {
            return false;
        }

        @ValidationMethod(message = "must have a true thing")
        public boolean isTrue() {
            return true;
        }
    }

    private final Validator validator = BaseValidator.newValidator();

    @Test
    public void complainsAboutMethodsWhichReturnFalse() throws Exception {
        final Collection<String> errors = ConstraintViolations.format(validator.validate(new MethodValidatorTest.Example()));
        assertThat(errors).containsOnly("must have a false thing", "also needs something special");
    }
}

