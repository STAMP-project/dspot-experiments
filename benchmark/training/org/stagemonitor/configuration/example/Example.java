package org.stagemonitor.configuration.example;


import SimpleSource.NAME;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Test;
import org.mockito.Mockito;
import org.stagemonitor.configuration.ConfigurationOption;
import org.stagemonitor.configuration.ConfigurationOptionProvider;
import org.stagemonitor.configuration.ConfigurationRegistry;


public class Example {
    private Example.ExampleConfiguration exampleConfiguration;

    private ConfigurationRegistry configurationRegistry;

    /* You can group you configuration values into configuration classes

    This is especially useful if you are following a modular approach  to application architecture
    - each module can have it's own configuration class
     */
    public static class ExampleConfiguration extends ConfigurationOptionProvider {
        private static final String EXAMPLE_CATEGORY = "Example category";

        private final ConfigurationOption<Boolean> booleanExample = // configuration options can never return null values
        // as we have to either set a default value,
        // explicitly mark it as optional with buildOptional(),
        // transforming the configuration option into a java.util.Optional
        // or require that a value has to be present in any
        // configuration source (buildRequired())
        // categorize your config options. This is especially useful when
        // generating a configuration UI
        // "forces" you to document the purpose of this configuration option
        // You can even use this data to automatically generate a configuration UI
        // explicitly flag this configuration option as dynamic which means we
        // can change the value at runtime
        // Non dynamic options can't be saved to a transient configuration source
        // see ConfigurationSource.isSavingPersistent()
        ConfigurationOption.booleanOption().key("example.boolean").dynamic(true).label("Example boolean config").description("More detailed description of the configuration option").configurationCategory(Example.ExampleConfiguration.EXAMPLE_CATEGORY).tags("fancy", "wow").buildWithDefault(true);

        private final ConfigurationOption<Optional<String>> optionalExample = ConfigurationOption.stringOption().key("example.optional").dynamic(true).label("Example optional config").configurationCategory(Example.ExampleConfiguration.EXAMPLE_CATEGORY).buildOptional();

        private final ConfigurationOption<Optional<String>> valitatorExample = ConfigurationOption.stringOption().key("example.validator").dynamic(false).label("Example config with validator").addValidator(( value) -> {
            if ((value != null) && (!(value.equals(value.toLowerCase())))) {
                throw new IllegalArgumentException("Must be in lower case");
            }
        }).configurationCategory(Example.ExampleConfiguration.EXAMPLE_CATEGORY).buildOptional();

        public boolean getBooleanExample() {
            return booleanExample.get();
        }

        public ConfigurationOption<Optional<String>> getOptionalExample() {
            return optionalExample;
        }
    }

    @Test
    public void getConfigurationValueTypeSafe() throws Exception {
        assertThat(exampleConfiguration.getBooleanExample()).isTrue();
    }

    @Test
    public void testChangeListener() throws Exception {
        AtomicBoolean changeListenerInvoked = new AtomicBoolean(false);
        exampleConfiguration.getOptionalExample().addChangeListener(( configurationOption, oldValue, newValue) -> changeListenerInvoked.set(true));
        // saves a value into a specific configuration source
        exampleConfiguration.getOptionalExample().update(Optional.of("foo"), NAME);
        assertThat(changeListenerInvoked).isTrue();
    }

    @Test
    public void testValidation() throws Exception {
        assertThatThrownBy(() -> configurationRegistry.save("example.validator", "FOO", SimpleSource.NAME)).isInstanceOf(IllegalArgumentException.class).hasMessage("Must be in lower case");
    }

    @Test
    public void testMocking() throws Exception {
        class SomeClassRequiringConfiguration {
            private final Example.ExampleConfiguration exampleConfiguration;

            private SomeClassRequiringConfiguration(Example.ExampleConfiguration exampleConfiguration) {
                this.exampleConfiguration = exampleConfiguration;
            }

            public String getFooOrBar() {
                if (exampleConfiguration.getBooleanExample()) {
                    return "foo";
                } else {
                    return "bar";
                }
            }
        }
        Example.ExampleConfiguration exampleConfigurationMock = Mockito.mock(Example.ExampleConfiguration.class);
        Mockito.when(exampleConfigurationMock.getBooleanExample()).thenReturn(true);
        final SomeClassRequiringConfiguration someClass = new SomeClassRequiringConfiguration(exampleConfigurationMock);
        assertThat(someClass.getFooOrBar()).isEqualTo("foo");
    }
}

