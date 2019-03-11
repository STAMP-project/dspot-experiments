package com.zendesk.maxwell;


import com.zendesk.maxwell.producer.AbstractProducer;
import com.zendesk.maxwell.producer.ProducerFactory;
import java.nio.file.Paths;
import joptsimple.OptionException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;


public class MaxwellConfigTest {
    private MaxwellConfig config;

    @Rule
    public final EnvironmentVariables environmentVariables = new EnvironmentVariables();

    @Test
    public void testFetchProducerFactoryFromArgs() {
        config = new MaxwellConfig(new String[]{ "--custom_producer.factory=" + (MaxwellConfigTest.TestProducerFactory.class.getName()) });
        Assert.assertNotNull(config.producerFactory);
        Assert.assertTrue(((config.producerFactory) instanceof MaxwellConfigTest.TestProducerFactory));
    }

    @Test
    public void testFetchProducerFactoryFromConfigFile() {
        String configPath = (getTestConfigDir()) + "producer-factory-config.properties";
        Assert.assertNotNull(("Config file not found at: " + configPath), Paths.get(configPath));
        config = new MaxwellConfig(new String[]{ "--config=" + configPath });
        Assert.assertNotNull(config.producerFactory);
        Assert.assertTrue(((config.producerFactory) instanceof MaxwellConfigTest.TestProducerFactory));
    }

    @Test(expected = OptionException.class)
    public void testCustomProperties() {
        // custom properties are not supported on the command line just like 'kafka.*' properties
        new MaxwellConfig(new String[]{ "--custom.foo=bar" });
    }

    @Test
    public void testCustomPropertiesFromConfigFile() {
        String configPath = (getTestConfigDir()) + "producer-factory-config.properties";
        Assert.assertNotNull(("Config file not found at: " + configPath), Paths.get(configPath));
        config = new MaxwellConfig(new String[]{ "--config=" + configPath });
        Assert.assertEquals("bar", config.customProducerProperties.getProperty("foo"));
    }

    @Test
    public void testEnvVarConfigViaOption() {
        environmentVariables.set("MAXWELL_USER", "foo");
        environmentVariables.set("maxwell_password", "bar");
        environmentVariables.set("maxwell_host", "remotehost");
        environmentVariables.set("MAXWELL_KAFKA.RETRIES", "100");
        environmentVariables.set("USER", "mysql");
        config = new MaxwellConfig(new String[]{ "--env_config_prefix=MAXWELL_", "--host=localhost" });
        Assert.assertEquals("foo", config.maxwellMysql.user);
        Assert.assertEquals("bar", config.maxwellMysql.password);
        Assert.assertEquals("localhost", config.maxwellMysql.host);
        Assert.assertEquals("100", config.kafkaProperties.getProperty("retries"));
    }

    @Test
    public void testEnvVarConfigViaConfigFile() {
        environmentVariables.set("FOO_USER", "foo");
        environmentVariables.set("foo_password", "bar");
        environmentVariables.set("foo_host", "remotehost");
        environmentVariables.set("FOO_KAFKA.RETRIES", "100");
        environmentVariables.set("USER", "mysql");
        String configPath = (getTestConfigDir()) + "env-var-config.properties";
        Assert.assertNotNull(("Config file not found at: " + configPath), Paths.get(configPath));
        config = new MaxwellConfig(new String[]{ "--config=" + configPath, "--host=localhost" });
        Assert.assertEquals("foo", config.maxwellMysql.user);
        Assert.assertEquals("bar", config.maxwellMysql.password);
        Assert.assertEquals("localhost", config.maxwellMysql.host);
        Assert.assertEquals("100", config.kafkaProperties.getProperty("retries"));
    }

    public static class TestProducerFactory implements ProducerFactory {
        public AbstractProducer createProducer(MaxwellContext context) {
            return new com.zendesk.maxwell.producer.StdoutProducer(context);
        }
    }
}

