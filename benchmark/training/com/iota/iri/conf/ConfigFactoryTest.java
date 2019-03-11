package com.iota.iri.conf;


import HashFactory.ADDRESS;
import com.iota.iri.model.Hash;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Tests for the {@link ConfigFactory}
 */
public class ConfigFactoryTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    /**
     * Creates and validates a Testnet {@link IotaConfig}.
     */
    @Test
    public void createIotaConfigTestnet() {
        IotaConfig iotaConfig = ConfigFactory.createIotaConfig(true);
        TestCase.assertTrue("Expected iotaConfig as instance of TestnetConfig.", (iotaConfig instanceof TestnetConfig));
        TestCase.assertTrue("Expected iotaConfig as Testnet.", iotaConfig.isTestnet());
    }

    /**
     * Creates and validates a Mainnet {@link IotaConfig}.
     */
    @Test
    public void createIotaConfigMainnet() {
        IotaConfig iotaConfig = ConfigFactory.createIotaConfig(false);
        TestCase.assertTrue("Expected iotaConfig as instance of MainnetConfig.", (iotaConfig instanceof MainnetConfig));
        Assert.assertFalse("Expected iotaConfig as Mainnet.", iotaConfig.isTestnet());
    }

    /**
     * Creates and validates a Testnet {@link IotaConfig} with <code>TESTNET=true</code> in config file and
     * <code>testnet: false</code> as method parameter for {@link ConfigFactory#createFromFile(File, boolean)}.
     *
     * @throws IOException
     * 		when config file not found.
     */
    @Test
    public void createFromFileTestnetWithTestnetTrueAndFalse() throws IOException {
        // lets assume in our configFile is TESTNET=true
        File configFile = createTestnetConfigFile("true");
        // but the parameter is set to testnet=false
        IotaConfig iotaConfig = ConfigFactory.createFromFile(configFile, false);
        TestCase.assertTrue("Expected iotaConfig as instance of TestnetConfig.", (iotaConfig instanceof TestnetConfig));
        TestCase.assertTrue("Expected iotaConfig as Testnet.", iotaConfig.isTestnet());
    }

    /**
     * Creates and validates a Testnet {@link IotaConfig} with <code>TESTNET=true</code> in config file and
     * <code>testnet: true</code> as method parameter for {@link ConfigFactory#createFromFile(File, boolean)}.
     *
     * @throws IOException
     * 		when config file not found.
     */
    @Test
    public void createFromFileTestnetWithTestnetTrueAndTrue() throws IOException {
        // lets assume in our configFile is TESTNET=true
        File configFile = createTestnetConfigFile("true");
        // but the parameter is set to testnet=true
        IotaConfig iotaConfig = ConfigFactory.createFromFile(configFile, true);
        TestCase.assertTrue("Expected iotaConfig as instance of TestnetConfig.", (iotaConfig instanceof TestnetConfig));
        TestCase.assertTrue("Expected iotaConfig as Testnet.", iotaConfig.isTestnet());
    }

    /**
     * Creates and validates a Testnet {@link IotaConfig} with <code>TESTNET=false</code> in config file and
     * <code>testnet: true</code> as method parameter for {@link ConfigFactory#createFromFile(File, boolean)}.
     *
     * @throws IOException
     * 		when config file not found.
     */
    @Test
    public void createFromFileTestnetWithTestnetFalseAndTrue() throws IOException {
        // lets assume in our configFile is TESTNET=false
        File configFile = createTestnetConfigFile("false");
        // but the parameter is set to testnet=true
        IotaConfig iotaConfig = ConfigFactory.createFromFile(configFile, true);
        TestCase.assertTrue("Expected iotaConfig as instance of TestnetConfig.", (iotaConfig instanceof TestnetConfig));
        TestCase.assertTrue("Expected iotaConfig as Testnet.", iotaConfig.isTestnet());
    }

    /**
     * Creates and validates a Mainnet {@link IotaConfig} with <code>TESTNET=false</code> in config file and
     * <code>testnet: false</code> as method parameter for {@link ConfigFactory#createFromFile(File, boolean)}.
     *
     * @throws IOException
     * 		when config file not found.
     */
    @Test
    public void createFromFileTestnetWithTestnetFalseAndFalse() throws IOException {
        // lets assume in our configFile is TESTNET=false
        File configFile = createTestnetConfigFile("false");
        // but the parameter is set to testnet=true
        IotaConfig iotaConfig = ConfigFactory.createFromFile(configFile, false);
        TestCase.assertTrue("Expected iotaConfig as instance of MainnetConfig.", (iotaConfig instanceof MainnetConfig));
        Assert.assertFalse("Expected iotaConfig as Mainnet.", iotaConfig.isTestnet());
    }

    /**
     * Test if leading and trailing spaces are trimmed from string in properties file.
     *
     * @throws IOException
     * 		when config file not found.
     */
    @Test
    public void createFromFileTestnetWithTrailingSpaces() throws IOException {
        File configFile = createTestnetConfigFile("true");
        IotaConfig iotaConfig = ConfigFactory.createFromFile(configFile, true);
        Hash expected = ADDRESS.create("NPCRMHDOMU9QHFFBKFCWFHFJNNQDRNDOGVPEVDVGWKHFUFEXLWJBHXDJFKQGYFRDZBQIFDSJMUCCQVICI");
        Assert.assertEquals("Expected that leading and trailing spaces were trimmed.", expected, iotaConfig.getCoordinator());
    }

    /**
     * Test if trailing spaces are correctly trimmed from integer.
     *
     * @throws IOException
     * 		when config file not found.
     */
    @Test
    public void createFromFileTestnetWithInteger() throws IOException {
        File configFile = createTestnetConfigFile("true");
        IotaConfig iotaConfig = ConfigFactory.createFromFile(configFile, true);
        Assert.assertEquals("Expected that trailing spaces are trimmed.", 2, iotaConfig.getMilestoneStartIndex());
    }

    /**
     * Test if trailing spaces are correctly trimmed from boolean.
     *
     * @throws IOException
     * 		when config file not found.
     */
    @Test
    public void createFromFileTestnetWithBoolean() throws IOException {
        File configFile = createTestnetConfigFile("true");
        IotaConfig iotaConfig = ConfigFactory.createFromFile(configFile, true);
        TestCase.assertTrue("Expected that ZMQ is enabled.", iotaConfig.isZmqEnabled());
    }

    /**
     * Try to create an {@link IotaConfig} from a not existing configFile.
     *
     * @throws IOException
     * 		when config file not found.
     */
    @Test(expected = FileNotFoundException.class)
    public void createFromFileTestnetWithFileNotFound() throws IOException {
        File configFile = new File("doesNotExist.ini");
        ConfigFactory.createFromFile(configFile, false);
    }
}

