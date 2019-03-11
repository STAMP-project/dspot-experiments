package io.dropwizard.configuration;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.util.Resources;
import io.dropwizard.validation.BaseValidator;
import java.io.File;
import javax.validation.Validator;
import org.junit.jupiter.api.Test;


public class ConfigurationFactoryFactoryTest {
    private final ConfigurationFactoryFactory<BaseConfigurationFactoryTest.Example> factoryFactory = new DefaultConfigurationFactoryFactory();

    private final Validator validator = BaseValidator.newValidator();

    @Test
    public void createDefaultFactory() throws Exception {
        File validFile = new File(Resources.getResource("factory-test-valid.yml").toURI());
        ConfigurationFactory<BaseConfigurationFactoryTest.Example> factory = factoryFactory.create(BaseConfigurationFactoryTest.Example.class, validator, Jackson.newObjectMapper(), "dw");
        final BaseConfigurationFactoryTest.Example example = factory.build(validFile);
        assertThat(example.getName()).isEqualTo("Coda Hale");
    }

    @Test
    public void createDefaultFactoryFailsUnknownProperty() throws Exception {
        File validFileWithUnknownProp = new File(Resources.getResource("factory-test-unknown-property.yml").toURI());
        ConfigurationFactory<BaseConfigurationFactoryTest.Example> factory = factoryFactory.create(BaseConfigurationFactoryTest.Example.class, validator, Jackson.newObjectMapper(), "dw");
        assertThatExceptionOfType(ConfigurationException.class).isThrownBy(() -> factory.build(validFileWithUnknownProp)).withMessageContaining("Unrecognized field at: trait");
    }

    @Test
    public void createFactoryAllowingUnknownProperties() throws Exception {
        ConfigurationFactoryFactory<BaseConfigurationFactoryTest.Example> customFactory = new ConfigurationFactoryFactoryTest.PassThroughConfigurationFactoryFactory();
        File validFileWithUnknownProp = new File(Resources.getResource("factory-test-unknown-property.yml").toURI());
        ConfigurationFactory<BaseConfigurationFactoryTest.Example> factory = customFactory.create(BaseConfigurationFactoryTest.Example.class, validator, Jackson.newObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES), "dw");
        BaseConfigurationFactoryTest.Example example = factory.build(validFileWithUnknownProp);
        assertThat(example.getName()).isEqualTo("Mighty Wizard");
    }

    private static final class PassThroughConfigurationFactoryFactory extends DefaultConfigurationFactoryFactory<BaseConfigurationFactoryTest.Example> {
        @Override
        protected ObjectMapper configureObjectMapper(ObjectMapper objectMapper) {
            return objectMapper;
        }
    }
}

