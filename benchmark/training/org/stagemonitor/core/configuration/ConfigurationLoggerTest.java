package org.stagemonitor.core.configuration;


import java.net.URL;
import java.util.List;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.stagemonitor.configuration.ConfigurationOption;
import org.stagemonitor.configuration.ConfigurationOptionProvider;
import org.stagemonitor.configuration.ConfigurationRegistry;
import org.stagemonitor.configuration.source.SimpleSource;


public class ConfigurationLoggerTest {
    private ConfigurationLogger configurationLogger;

    private Logger logger;

    @Test
    public void testDeprecatedOption() throws Exception {
        class Provider extends ConfigurationOptionProvider {
            private ConfigurationOption<String> deprecatedOption = ConfigurationOption.stringOption().key("foo").tags("deprecated").buildRequired();
        }
        final ConfigurationRegistry configurationRegistry = ConfigurationRegistry.builder().addOptionProvider(new Provider()).addConfigSource(new SimpleSource().add("foo", "bar")).build();
        configurationLogger.logConfiguration(configurationRegistry);
        Mockito.verify(logger).warn(ArgumentMatchers.contains("Detected usage of deprecated configuration option '{}'"), ArgumentMatchers.eq("foo"));
    }

    @Test
    public void testAliasKey() throws Exception {
        class Provider extends ConfigurationOptionProvider {
            private ConfigurationOption<String> aliasOption = ConfigurationOption.stringOption().key("foo").aliasKeys("foo.old").buildRequired();
        }
        final ConfigurationRegistry configurationRegistry = ConfigurationRegistry.builder().addOptionProvider(new Provider()).addConfigSource(new SimpleSource().add("foo.old", "bar")).build();
        configurationLogger.logConfiguration(configurationRegistry);
        Mockito.verify(logger).warn(ArgumentMatchers.eq(("Detected usage of an old configuration key: '{}'. " + "Please use '{}' instead.")), ArgumentMatchers.eq("foo.old"), ArgumentMatchers.eq("foo"));
    }

    @Test
    public void testLogSensitive() throws Exception {
        class Provider extends ConfigurationOptionProvider {
            private ConfigurationOption<String> sensitiveOption = ConfigurationOption.stringOption().key("foo").sensitive().buildRequired();
        }
        final ConfigurationRegistry configurationRegistry = ConfigurationRegistry.builder().addOptionProvider(new Provider()).addConfigSource(new SimpleSource("source").add("foo", "secret")).build();
        configurationLogger.logConfiguration(configurationRegistry);
        Mockito.verify(logger).info(ArgumentMatchers.startsWith("# stagemonitor configuration"));
        Mockito.verify(logger).info(ArgumentMatchers.eq("{}: {} (source: {})"), ArgumentMatchers.eq("foo"), ArgumentMatchers.eq("XXXX"), ArgumentMatchers.eq("source"));
    }

    @Test
    public void testLogUrlWithBasicAuth() throws Exception {
        class Provider extends ConfigurationOptionProvider {
            private ConfigurationOption<List<URL>> urlBasicAuthOption = ConfigurationOption.urlsOption().key("foo").buildRequired();
        }
        final ConfigurationRegistry configurationRegistry = ConfigurationRegistry.builder().addOptionProvider(new Provider()).addConfigSource(new SimpleSource("source").add("foo", "http://user:pwd@example.com")).build();
        configurationLogger.logConfiguration(configurationRegistry);
        Mockito.verify(logger).info(ArgumentMatchers.startsWith("# stagemonitor configuration"));
        Mockito.verify(logger).info(ArgumentMatchers.eq("{}: {} (source: {})"), ArgumentMatchers.eq("foo"), ArgumentMatchers.eq("http://user:XXX@example.com"), ArgumentMatchers.eq("source"));
    }
}

