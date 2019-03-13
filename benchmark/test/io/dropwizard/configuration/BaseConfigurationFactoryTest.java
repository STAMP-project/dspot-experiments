package io.dropwizard.configuration;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.benmanes.caffeine.cache.CaffeineSpec;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.util.Maps;
import io.dropwizard.validation.BaseValidator;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.validation.Valid;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import org.assertj.core.data.MapEntry;
import org.junit.jupiter.api.Test;


public abstract class BaseConfigurationFactoryTest {
    private static final String NEWLINE = System.lineSeparator();

    @SuppressWarnings("UnusedDeclaration")
    public static class ExampleServer {
        @JsonProperty
        private int port = 8000;

        public int getPort() {
            return port;
        }

        public static BaseConfigurationFactoryTest.ExampleServer create(int port) {
            BaseConfigurationFactoryTest.ExampleServer server = new BaseConfigurationFactoryTest.ExampleServer();
            server.port = port;
            return server;
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    public static class Example {
        @NotNull
        @Pattern(regexp = "[\\w]+[\\s]+[\\w]+([\\s][\\w]+)?")
        private String name = "";

        @JsonProperty
        private int age = 1;

        List<String> type = Collections.emptyList();

        @JsonProperty
        private Map<String, String> properties = Collections.emptyMap();

        @JsonProperty
        private List<BaseConfigurationFactoryTest.ExampleServer> servers = Collections.emptyList();

        private boolean admin;

        @JsonProperty("my.logger")
        private Map<String, String> logger = Collections.emptyMap();

        public String getName() {
            return name;
        }

        public List<String> getType() {
            return type;
        }

        public Map<String, String> getProperties() {
            return properties;
        }

        public List<BaseConfigurationFactoryTest.ExampleServer> getServers() {
            return servers;
        }

        public boolean isAdmin() {
            return admin;
        }

        public void setAdmin(boolean admin) {
            this.admin = admin;
        }

        public Map<String, String> getLogger() {
            return logger;
        }
    }

    static class ExampleWithDefaults {
        @NotNull
        @Pattern(regexp = "[\\w]+[\\s]+[\\w]+([\\s][\\w]+)?")
        @JsonProperty
        String name = "Coda Hale";

        @JsonProperty
        List<String> type = Arrays.asList("coder", "wizard");

        @JsonProperty
        Map<String, String> properties = Maps.of("debug", "true", "settings.enabled", "false");

        @JsonProperty
        List<BaseConfigurationFactoryTest.ExampleServer> servers = Arrays.asList(BaseConfigurationFactoryTest.ExampleServer.create(8080), BaseConfigurationFactoryTest.ExampleServer.create(8081), BaseConfigurationFactoryTest.ExampleServer.create(8082));

        @JsonProperty
        @Valid
        CaffeineSpec cacheBuilderSpec = CaffeineSpec.parse("initialCapacity=0,maximumSize=0");
    }

    static class NonInsatiableExample {
        @JsonProperty
        String name = "Code Hale";

        NonInsatiableExample(@JsonProperty("name")
        String name) {
            this.name = name;
        }
    }

    protected final Validator validator = BaseValidator.newValidator();

    protected ConfigurationFactory<BaseConfigurationFactoryTest.Example> factory = new ConfigurationFactory<BaseConfigurationFactoryTest.Example>() {
        @Override
        public BaseConfigurationFactoryTest.Example build(ConfigurationSourceProvider provider, String path) throws ConfigurationException, IOException {
            return new BaseConfigurationFactoryTest.Example();
        }

        @Override
        public BaseConfigurationFactoryTest.Example build() throws ConfigurationException, IOException {
            return new BaseConfigurationFactoryTest.Example();
        }
    };

    protected File malformedFile = new File("/");

    protected File emptyFile = new File("/");

    protected File invalidFile = new File("/");

    protected File validFile = new File("/");

    protected File typoFile = new File("/");

    protected File wrongTypeFile = new File("/");

    protected File malformedAdvancedFile = new File("/");

    @Test
    public void usesDefaultedCacheBuilderSpec() throws Exception {
        final BaseConfigurationFactoryTest.ExampleWithDefaults example = new YamlConfigurationFactory(BaseConfigurationFactoryTest.ExampleWithDefaults.class, validator, Jackson.newObjectMapper(), "dw").build();
        assertThat(example.cacheBuilderSpec).isNotNull();
        assertThat(example.cacheBuilderSpec).isEqualTo(CaffeineSpec.parse("initialCapacity=0,maximumSize=0"));
    }

    @Test
    public void loadsValidConfigFiles() throws Exception {
        final BaseConfigurationFactoryTest.Example example = factory.build(validFile);
        assertThat(example.getName()).isEqualTo("Coda Hale");
        assertThat(example.getType().get(0)).isEqualTo("coder");
        assertThat(example.getType().get(1)).isEqualTo("wizard");
        assertThat(example.getProperties()).contains(MapEntry.entry("debug", "true"), MapEntry.entry("settings.enabled", "false"));
        assertThat(example.getServers()).hasSize(3);
        assertThat(example.getServers().get(0).getPort()).isEqualTo(8080);
    }

    @Test
    public void handlesSimpleOverride() throws Exception {
        System.setProperty("dw.name", "Coda Hale Overridden");
        final BaseConfigurationFactoryTest.Example example = factory.build(validFile);
        assertThat(example.getName()).isEqualTo("Coda Hale Overridden");
    }

    @Test
    public void handlesExistingOverrideWithPeriod() throws Exception {
        System.setProperty("dw.my\\.logger.level", "debug");
        final BaseConfigurationFactoryTest.Example example = factory.build(validFile);
        assertThat(example.getLogger().get("level")).isEqualTo("debug");
    }

    @Test
    public void handlesNewOverrideWithPeriod() throws Exception {
        System.setProperty("dw.my\\.logger.com\\.example", "error");
        final BaseConfigurationFactoryTest.Example example = factory.build(validFile);
        assertThat(example.getLogger().get("com.example")).isEqualTo("error");
    }

    @Test
    public void handlesArrayOverride() throws Exception {
        System.setProperty("dw.type", "coder,wizard,overridden");
        final BaseConfigurationFactoryTest.Example example = factory.build(validFile);
        assertThat(example.getType().get(2)).isEqualTo("overridden");
        assertThat(example.getType().size()).isEqualTo(3);
    }

    @Test
    public void handlesArrayOverrideEscaped() throws Exception {
        System.setProperty("dw.type", "coder,wizard,overr\\,idden");
        final BaseConfigurationFactoryTest.Example example = factory.build(validFile);
        assertThat(example.getType().get(2)).isEqualTo("overr,idden");
        assertThat(example.getType().size()).isEqualTo(3);
    }

    @Test
    public void handlesSingleElementArrayOverride() throws Exception {
        System.setProperty("dw.type", "overridden");
        final BaseConfigurationFactoryTest.Example example = factory.build(validFile);
        assertThat(example.getType().get(0)).isEqualTo("overridden");
        assertThat(example.getType().size()).isEqualTo(1);
    }

    @Test
    public void overridesArrayWithIndices() throws Exception {
        System.setProperty("dw.type[1]", "overridden");
        final BaseConfigurationFactoryTest.Example example = factory.build(validFile);
        assertThat(example.getType().get(0)).isEqualTo("coder");
        assertThat(example.getType().get(1)).isEqualTo("overridden");
    }

    @Test
    public void overridesArrayWithIndicesReverse() throws Exception {
        System.setProperty("dw.type[0]", "overridden");
        final BaseConfigurationFactoryTest.Example example = factory.build(validFile);
        assertThat(example.getType().get(0)).isEqualTo("overridden");
        assertThat(example.getType().get(1)).isEqualTo("wizard");
    }

    @Test
    public void overridesArrayPropertiesWithIndices() throws Exception {
        System.setProperty("dw.servers[0].port", "7000");
        System.setProperty("dw.servers[2].port", "9000");
        final BaseConfigurationFactoryTest.Example example = factory.build(validFile);
        assertThat(example.getServers()).hasSize(3);
        assertThat(example.getServers().get(0).getPort()).isEqualTo(7000);
        assertThat(example.getServers().get(2).getPort()).isEqualTo(9000);
    }

    @Test
    public void overrideMapProperty() throws Exception {
        System.setProperty("dw.properties.settings.enabled", "true");
        final BaseConfigurationFactoryTest.Example example = factory.build(validFile);
        assertThat(example.getProperties()).contains(MapEntry.entry("debug", "true"), MapEntry.entry("settings.enabled", "true"));
    }

    @Test
    public void throwsAnExceptionOnUnexpectedArrayOverride() throws Exception {
        System.setProperty("dw.servers.port", "9000");
        try {
            factory.build(validFile);
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage()).containsOnlyOnce("target is an array but no index specified");
        }
    }

    @Test
    public void throwsAnExceptionOnArrayOverrideWithInvalidType() throws Exception {
        System.setProperty("dw.servers", "one,two");
        assertThatExceptionOfType(ConfigurationParsingException.class).isThrownBy(() -> factory.build(validFile));
    }

    @Test
    public void throwsAnExceptionOnOverrideArrayIndexOutOfBounds() throws Exception {
        System.setProperty("dw.type[2]", "invalid");
        try {
            factory.build(validFile);
            failBecauseExceptionWasNotThrown(ArrayIndexOutOfBoundsException.class);
        } catch (ArrayIndexOutOfBoundsException e) {
            assertThat(e.getMessage()).containsOnlyOnce("index is greater than size of array");
        }
    }

    @Test
    public void throwsAnExceptionOnOverrideArrayPropertyIndexOutOfBounds() throws Exception {
        System.setProperty("dw.servers[4].port", "9000");
        try {
            factory.build(validFile);
            failBecauseExceptionWasNotThrown(ArrayIndexOutOfBoundsException.class);
        } catch (ArrayIndexOutOfBoundsException e) {
            assertThat(e.getMessage()).containsOnlyOnce("index is greater than size of array");
        }
    }

    @Test
    public void throwsAnExceptionOnMalformedFiles() throws Exception {
        factory.build(malformedFile);
        failBecauseExceptionWasNotThrown(ConfigurationParsingException.class);
    }

    @Test
    public void throwsAnExceptionOnEmptyFiles() throws Exception {
        try {
            factory.build(emptyFile);
            failBecauseExceptionWasNotThrown(ConfigurationParsingException.class);
        } catch (ConfigurationParsingException e) {
            assertThat(e.getMessage()).containsOnlyOnce(((" * Configuration at " + (emptyFile.toString())) + " must not be empty"));
        }
    }

    @Test
    public void throwsAnExceptionOnInvalidFiles() throws Exception {
        try {
            factory.build(invalidFile);
            failBecauseExceptionWasNotThrown(ConfigurationValidationException.class);
        } catch (ConfigurationValidationException e) {
            if ("en".equals(Locale.getDefault().getLanguage())) {
                assertThat(e.getMessage()).endsWith(String.format(("%s has an error:%n" + "  * name must match \"[\\w]+[\\s]+[\\w]+([\\s][\\w]+)?\"%n"), invalidFile.getName()));
            }
        }
    }

    @Test
    public void handleOverrideDefaultConfiguration() throws Exception {
        System.setProperty("dw.name", "Coda Hale Overridden");
        System.setProperty("dw.type", "coder,wizard,overridden");
        System.setProperty("dw.properties.settings.enabled", "true");
        System.setProperty("dw.servers[0].port", "8090");
        System.setProperty("dw.servers[2].port", "8092");
        final BaseConfigurationFactoryTest.ExampleWithDefaults example = new YamlConfigurationFactory(BaseConfigurationFactoryTest.ExampleWithDefaults.class, validator, Jackson.newObjectMapper(), "dw").build();
        assertThat(example.name).isEqualTo("Coda Hale Overridden");
        assertThat(example.type.get(2)).isEqualTo("overridden");
        assertThat(example.type.size()).isEqualTo(3);
        assertThat(example.properties).containsEntry("settings.enabled", "true");
        assertThat(example.servers.get(0).getPort()).isEqualTo(8090);
        assertThat(example.servers.get(2).getPort()).isEqualTo(8092);
    }

    @Test
    public void handleDefaultConfigurationWithoutOverriding() throws Exception {
        final BaseConfigurationFactoryTest.ExampleWithDefaults example = new YamlConfigurationFactory(BaseConfigurationFactoryTest.ExampleWithDefaults.class, validator, Jackson.newObjectMapper(), "dw").build();
        assertThat(example.name).isEqualTo("Coda Hale");
        assertThat(example.type).isEqualTo(Arrays.asList("coder", "wizard"));
        assertThat(example.properties).isEqualTo(Maps.of("debug", "true", "settings.enabled", "false"));
        assertThat(example.servers.get(0).getPort()).isEqualTo(8080);
        assertThat(example.servers.get(1).getPort()).isEqualTo(8081);
        assertThat(example.servers.get(2).getPort()).isEqualTo(8082);
    }

    @Test
    public void throwsAnExceptionIfDefaultConfigurationCantBeInstantiated() throws Exception {
        System.setProperty("dw.name", "Coda Hale Overridden");
        final YamlConfigurationFactory<BaseConfigurationFactoryTest.NonInsatiableExample> factory = new YamlConfigurationFactory(BaseConfigurationFactoryTest.NonInsatiableExample.class, validator, Jackson.newObjectMapper(), "dw");
        assertThatThrownBy(factory::build).isInstanceOf(IllegalArgumentException.class).hasMessage(("Unable create an instance of the configuration class: " + "'io.dropwizard.configuration.BaseConfigurationFactoryTest.NonInsatiableExample'"));
    }

    @Test
    public void incorrectTypeIsFound() throws Exception {
        assertThatThrownBy(() -> factory.build(wrongTypeFile)).isInstanceOf(ConfigurationParsingException.class).hasMessage(String.format(((("%s has an error:" + (BaseConfigurationFactoryTest.NEWLINE)) + "  * Incorrect type of value at: age; is of type: String, expected: int") + (BaseConfigurationFactoryTest.NEWLINE)), wrongTypeFile));
    }

    @Test
    public void printsDetailedInformationOnMalformedContent() throws Exception {
        factory.build(malformedAdvancedFile);
    }
}

