package io.dropwizard.validation;


import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.jersey.validation.MutableValidatorFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import java.util.Optional;
import java.util.Set;
import javax.validation.ConstraintValidatorFactory;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.Min;
import org.hibernate.validator.internal.constraintvalidators.bv.number.bound.MinValidatorForNumber;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorFactoryImpl;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class InjectValidatorFeatureTest {
    private final Application<Configuration> application = new Application<Configuration>() {
        @Override
        public void initialize(Bootstrap<Configuration> bootstrap) {
        }

        @Override
        public void run(Configuration configuration, Environment environment) {
        }
    };

    private ValidatorFactory validatorFactory;

    @Test
    public void shouldReplaceValidatorFactory() {
        ConstraintValidatorFactory factory = validatorFactory.getConstraintValidatorFactory();
        assertThat(factory).isInstanceOf(MutableValidatorFactory.class);
    }

    @Test
    public void shouldValidateNormally() {
        Validator validator = validatorFactory.getValidator();
        // Run validation manually
        Set<ConstraintViolation<InjectValidatorFeatureTest.Bean>> constraintViolations = validator.validate(new InjectValidatorFeatureTest.Bean(1));
        assertThat(constraintViolations.size()).isEqualTo(1);
        Optional<String> message = constraintViolations.stream().findFirst().map(ConstraintViolation::getMessage);
        assertThat(message).hasValue("must be greater than or equal to 10");
    }

    @Test
    public void shouldInvokeUpdatedFactory() {
        MutableValidatorFactory mutableFactory = ((MutableValidatorFactory) (validatorFactory.getConstraintValidatorFactory()));
        ConstraintValidatorFactory mockedFactory = Mockito.mock(ConstraintValidatorFactory.class, AdditionalAnswers.delegatesTo(new ConstraintValidatorFactoryImpl()));
        // Swap validator factory at runtime
        mutableFactory.setValidatorFactory(mockedFactory);
        // Run validation manually
        Validator validator = validatorFactory.getValidator();
        validator.validate(new InjectValidatorFeatureTest.Bean(1));
        Mockito.verify(mockedFactory).getInstance(ArgumentMatchers.eq(MinValidatorForNumber.class));
    }

    static class Bean {
        @Min(10)
        final int value;

        Bean(int value) {
            this.value = value;
        }
    }
}

