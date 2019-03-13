package io.dropwizard.validation.valuehandling;


import GuavaOptionalValueExtractor.DESCRIPTOR;
import Unwrapping.Unwrap;
import com.google.common.base.Optional;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.HibernateValidator;
import org.junit.jupiter.api.Test;


public class GuavaOptionalValueExtractorTest {
    public static class Example {
        @Min(value = 3, payload = Unwrap.class)
        Optional<Integer> three = Optional.absent();

        @NotNull(payload = Unwrap.class)
        Optional<Integer> notNull = Optional.of(123);
    }

    private final Validator validator = Validation.byProvider(HibernateValidator.class).configure().addValueExtractor(DESCRIPTOR.getValueExtractor()).buildValidatorFactory().getValidator();

    @Test
    public void succeedsWhenAbsent() {
        GuavaOptionalValueExtractorTest.Example example = new GuavaOptionalValueExtractorTest.Example();
        Set<ConstraintViolation<GuavaOptionalValueExtractorTest.Example>> violations = validator.validate(example);
        assertThat(violations).isEmpty();
    }

    @Test
    public void failsWhenFailingConstraint() {
        GuavaOptionalValueExtractorTest.Example example = new GuavaOptionalValueExtractorTest.Example();
        example.three = Optional.of(2);
        Set<ConstraintViolation<GuavaOptionalValueExtractorTest.Example>> violations = validator.validate(example);
        assertThat(violations).hasSize(1);
    }

    @Test
    public void succeedsWhenConstraintsMet() {
        GuavaOptionalValueExtractorTest.Example example = new GuavaOptionalValueExtractorTest.Example();
        example.three = Optional.of(10);
        Set<ConstraintViolation<GuavaOptionalValueExtractorTest.Example>> violations = validator.validate(example);
        assertThat(violations).isEmpty();
    }

    @Test
    public void notNullFailsWhenAbsent() {
        GuavaOptionalValueExtractorTest.Example example = new GuavaOptionalValueExtractorTest.Example();
        example.notNull = Optional.absent();
        Set<ConstraintViolation<GuavaOptionalValueExtractorTest.Example>> violations = validator.validate(example);
        assertThat(violations).hasSize(1);
    }
}

