package io.dropwizard.validation.valuehandling;


import Unwrapping.Unwrap;
import java.util.Optional;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.HibernateValidator;
import org.junit.jupiter.api.Test;


// Dropwizard used to supply its own Java 8 optional validator but since
// Hibernate Validator 5.2, it's built in, so the class was removed but
// the test class stays to ensure behavior remains
public class OptionalValidatedValueUnwrapperTest {
    public static class Example {
        @Min(value = 3, payload = Unwrap.class)
        Optional<Integer> three = Optional.empty();

        @NotNull(payload = Unwrap.class)
        Optional<Integer> notNull = Optional.of(123);
    }

    private final Validator validator = Validation.byProvider(HibernateValidator.class).configure().buildValidatorFactory().getValidator();

    @Test
    public void succeedsWhenAbsent() {
        OptionalValidatedValueUnwrapperTest.Example example = new OptionalValidatedValueUnwrapperTest.Example();
        Set<ConstraintViolation<OptionalValidatedValueUnwrapperTest.Example>> violations = validator.validate(example);
        assertThat(violations).isEmpty();
    }

    @Test
    public void failsWhenFailingConstraint() {
        OptionalValidatedValueUnwrapperTest.Example example = new OptionalValidatedValueUnwrapperTest.Example();
        example.three = Optional.of(2);
        Set<ConstraintViolation<OptionalValidatedValueUnwrapperTest.Example>> violations = validator.validate(example);
        assertThat(violations).hasSize(1);
    }

    @Test
    public void succeedsWhenConstraintsMet() {
        OptionalValidatedValueUnwrapperTest.Example example = new OptionalValidatedValueUnwrapperTest.Example();
        example.three = Optional.of(10);
        Set<ConstraintViolation<OptionalValidatedValueUnwrapperTest.Example>> violations = validator.validate(example);
        assertThat(violations).isEmpty();
    }

    @Test
    public void notNullFailsWhenAbsent() {
        OptionalValidatedValueUnwrapperTest.Example example = new OptionalValidatedValueUnwrapperTest.Example();
        example.notNull = Optional.empty();
        Set<ConstraintViolation<OptionalValidatedValueUnwrapperTest.Example>> violations = validator.validate(example);
        assertThat(violations).hasSize(1);
    }
}

