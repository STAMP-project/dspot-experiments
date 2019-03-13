package io.dropwizard.jersey.validation;


import Unwrapping.Skip;
import io.dropwizard.jersey.params.IntParam;
import io.dropwizard.jersey.params.NonEmptyStringParam;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.junit.jupiter.api.Test;


public class ParamValidatorUnwrapperTest {
    private static class Example {
        @NotNull(payload = Skip.class)
        @Min(3)
        IntParam inter = new IntParam("4");

        @NotNull(payload = Skip.class)
        @NotEmpty
        @Length(max = 3)
        public NonEmptyStringParam name = new NonEmptyStringParam("a");
    }

    private final Validator validator = Validators.newValidator();

    @Test
    public void succeedsWithAllGoodData() {
        final ParamValidatorUnwrapperTest.Example example = new ParamValidatorUnwrapperTest.Example();
        final Set<ConstraintViolation<ParamValidatorUnwrapperTest.Example>> validate = validator.validate(example);
        assertThat(validate).isEmpty();
    }

    @Test
    public void failsWithInvalidIntParam() {
        final ParamValidatorUnwrapperTest.Example example = new ParamValidatorUnwrapperTest.Example();
        example.inter = new IntParam("2");
        final Set<ConstraintViolation<ParamValidatorUnwrapperTest.Example>> validate = validator.validate(example);
        assertThat(validate).hasSize(1);
    }

    @SuppressWarnings("NullAway")
    @Test
    public void failsWithNullIntParam() {
        final ParamValidatorUnwrapperTest.Example example = new ParamValidatorUnwrapperTest.Example();
        example.inter = null;
        final Set<ConstraintViolation<ParamValidatorUnwrapperTest.Example>> validate = validator.validate(example);
        assertThat(validate).hasSize(1);
    }

    @SuppressWarnings("NullAway")
    @Test
    public void failsWithNullNonEmptyStringParam() {
        final ParamValidatorUnwrapperTest.Example example = new ParamValidatorUnwrapperTest.Example();
        example.name = null;
        final Set<ConstraintViolation<ParamValidatorUnwrapperTest.Example>> validate = validator.validate(example);
        assertThat(validate).hasSize(1);
    }

    @Test
    public void failsWithInvalidNonEmptyStringParam() {
        final ParamValidatorUnwrapperTest.Example example = new ParamValidatorUnwrapperTest.Example();
        example.name = new NonEmptyStringParam("hello");
        final Set<ConstraintViolation<ParamValidatorUnwrapperTest.Example>> validate = validator.validate(example);
        assertThat(validate).hasSize(1);
    }

    @Test
    public void failsWithEmptyNonEmptyStringParam() {
        final ParamValidatorUnwrapperTest.Example example = new ParamValidatorUnwrapperTest.Example();
        example.name = new NonEmptyStringParam("");
        final Set<ConstraintViolation<ParamValidatorUnwrapperTest.Example>> validate = validator.validate(example);
        assertThat(validate).hasSize(1);
    }
}

