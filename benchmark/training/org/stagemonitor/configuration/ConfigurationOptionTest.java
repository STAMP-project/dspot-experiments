package org.stagemonitor.configuration;


import SimpleSource.NAME;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import org.junit.Assert;
import org.junit.Test;
import org.stagemonitor.configuration.source.SimpleSource;


public class ConfigurationOptionTest {
    private final ConfigurationOption<Map<Pattern, String>> invalidPatternMap = ConfigurationOption.regexMapOption().key("invalidPatternMap").build();

    private final ConfigurationOption<Collection<Pattern>> invalidPatternSyntax = ConfigurationOption.regexListOption().key("invalidPatternSyntax").build();

    private final ConfigurationOption<Long> aLong = ConfigurationOption.longOption().key("long").build();

    private final ConfigurationOption<Long> invalidLong = ConfigurationOption.longOption().key("invalidLong").buildWithDefault(2L);

    private final ConfigurationOption<String> string = ConfigurationOption.stringOption().key("string").buildRequired();

    private final ConfigurationOption<Collection<String>> lowerStrings = ConfigurationOption.lowerStringsOption().key("lowerStrings").build();

    private final ConfigurationOption<Collection<String>> strings = ConfigurationOption.stringsOption().key("strings").build();

    private final ConfigurationOption<Boolean> booleanTrue = ConfigurationOption.booleanOption().key("boolean.true").build();

    private final ConfigurationOption<Boolean> booleanFalse = ConfigurationOption.booleanOption().key("boolean.false").build();

    private final ConfigurationOption<Boolean> booleanInvalid = ConfigurationOption.booleanOption().key("boolean.invalid").build();

    private final ConfigurationOption<String> testCaching = ConfigurationOption.stringOption().key("testCaching").build();

    private final ConfigurationOption<String> testUpdate = ConfigurationOption.stringOption().key("testUpdate").dynamic(true).build();

    private final ConfigurationOption<Optional<String>> testOptionalWithValue = ConfigurationOption.stringOption().key("testOptionalWithValue").dynamic(true).buildOptional();

    private final ConfigurationOption<Optional<String>> testOptionalWithoutValue = ConfigurationOption.stringOption().key("testOptionalWithoutValue").dynamic(true).buildOptional();

    private ConfigurationOption<String> testAlternateKeys = ConfigurationOption.stringOption().key("primaryKey").aliasKeys("alternateKey1", "alternateKey2").dynamic(true).build();

    private ConfigurationRegistry configuration;

    private SimpleSource configSource = SimpleSource.forTest("invalidLong", "two").add("stagemonitor.reporting.elasticsearch.url", "foo/").add("invalidPatternMap", "(.*).js: *.js (.*).css:  *.css").add("invalidPatternSyntax", "(.*.js").add("long", "2").add("string", "fooBar").add("lowerStrings", "fooBar").add("strings", "fooBar , barFoo").add("boolean.true", "true").add("boolean.false", "false").add("boolean.invalid", "ture").add("testCaching", "testCaching").add("testOptionalWithValue", "present");

    @Test
    public void testInvalidPatterns() {
        Assert.assertTrue(invalidPatternMap.getValue().isEmpty());
    }

    @Test
    public void testInvalidPatternSyntax() {
        Assert.assertTrue(invalidPatternSyntax.getValue().isEmpty());
    }

    @Test
    public void testGetInt() {
        Assert.assertEquals(Long.valueOf(2L), aLong.getValue());
    }

    @Test
    public void testGetInvalidLong() {
        Assert.assertEquals(Long.valueOf(2L), invalidLong.getValue());
    }

    @Test
    public void testGetString() {
        Assert.assertEquals("fooBar", string.getValue());
    }

    @Test
    public void testGetLowerStrings() {
        Assert.assertEquals(Collections.singleton("foobar"), lowerStrings.getValue());
    }

    @Test
    public void testCachingAndReload() {
        Assert.assertEquals("testCaching", testCaching.getValue());
        configSource.add("testCaching", "testCaching2");
        Assert.assertEquals("testCaching", testCaching.getValue());
        configuration.reloadDynamicConfigurationOptions();
        Assert.assertEquals("testCaching", testCaching.getValue());
        configuration.reloadAllConfigurationOptions();
        Assert.assertEquals("testCaching2", testCaching.getValue());
    }

    @Test
    public void testGetBoolean() {
        Assert.assertTrue(booleanTrue.getValue());
        Assert.assertFalse(booleanFalse.getValue());
    }

    @Test
    public void testUpdate() throws IOException {
        Assert.assertNull(testUpdate.getValue());
        testUpdate.update("updated!", "Test Configuration Source");
        Assert.assertEquals("updated!", testUpdate.getValue());
    }

    @Test
    public void testAlternateKeysPrimary() {
        final ConfigurationRegistry configuration = createConfiguration(Collections.singletonList(testAlternateKeys), SimpleSource.forTest("primaryKey", "foo"));
        Assert.assertEquals("foo", configuration.getConfigurationOptionByKey("primaryKey").getValueAsString());
        Assert.assertEquals("foo", configuration.getConfigurationOptionByKey("alternateKey1").getValueAsString());
        Assert.assertEquals("foo", configuration.getConfigurationOptionByKey("alternateKey2").getValueAsString());
    }

    @Test
    public void testAlternateKeysAlternate() {
        final ConfigurationRegistry configuration = createConfiguration(Collections.singletonList(testAlternateKeys), SimpleSource.forTest("alternateKey1", "foo"));
        Assert.assertEquals("foo", configuration.getConfigurationOptionByKey("primaryKey").getValueAsString());
        Assert.assertEquals("foo", configuration.getConfigurationOptionByKey("alternateKey1").getValueAsString());
        Assert.assertEquals("foo", configuration.getConfigurationOptionByKey("alternateKey2").getValueAsString());
    }

    @Test
    public void testAlternateKeysPrimaryAndAlternate() {
        final ConfigurationRegistry configuration = createConfiguration(Collections.singletonList(testAlternateKeys), SimpleSource.forTest("primaryKey", "foo").add("alternateKey1", "bar"));
        Assert.assertEquals("foo", configuration.getConfigurationOptionByKey("primaryKey").getValueAsString());
        Assert.assertEquals("foo", configuration.getConfigurationOptionByKey("alternateKey1").getValueAsString());
        Assert.assertEquals("foo", configuration.getConfigurationOptionByKey("alternateKey2").getValueAsString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDuplicateAlternateKeys() {
        createConfiguration(Arrays.asList(ConfigurationOption.stringOption().key("primaryKey1").aliasKeys("alternateKey1").build(), ConfigurationOption.stringOption().key("primaryKey2").aliasKeys("alternateKey1").build()), new SimpleSource());
    }

    @Test
    public void testOptional() {
        Assert.assertEquals("present", testOptionalWithValue.getValue().get());
        Assert.assertTrue(testOptionalWithValue.getValue().isPresent());
        Assert.assertFalse(testOptionalWithoutValue.getValue().isPresent());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDefaultValueNull() {
        ConfigurationOption.stringOption().key("foo").buildWithDefault(null);
    }

    @Test
    public void testWithOptions_valid() {
        final ConfigurationOption<String> option = ConfigurationOption.stringOption().key("test.options").addValidOptions("foo", "bar").buildWithDefault("foo");
        final ConfigurationRegistry configuration = createConfiguration(Collections.singletonList(option), SimpleSource.forTest("test.options", "bar"));
        assertThat(configuration.getConfigurationOptionByKey("test.options").getValueAsString()).isEqualTo("bar");
        assertThatThrownBy(() -> configuration.save("test.options", "baz", "Test Configuration Source")).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Invalid option");
    }

    @Test
    public void testWithOptions_invalidDefault() {
        assertThatThrownBy(() -> ConfigurationOption.stringOption().key("test.options").addValidOptions("foo", "bar").buildWithDefault("baz")).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Invalid option");
    }

    @Test
    public void testWithOptions_sealedOptions() {
        assertThatThrownBy(() -> ConfigurationOption.stringOption().key("test.options").addValidOptions("foo", "bar").sealValidOptions().addValidOption("baz").buildWithDefault("baz")).isInstanceOf(IllegalStateException.class).hasMessage("Options are sealed, you can't add any new ones");
    }

    interface Strategy {}

    public static class DefaultStrategyImpl implements ConfigurationOptionTest.Strategy {}

    public static class SpecialStrategyImpl implements ConfigurationOptionTest.Strategy {}

    public static class NoMetaInfServicesStrategyImpl implements ConfigurationOptionTest.Strategy {}

    @Test
    public void testServiceLoaderStrategyOption() throws Exception {
        final ConfigurationOption<ConfigurationOptionTest.Strategy> option = ConfigurationOption.serviceLoaderStrategyOption(ConfigurationOptionTest.Strategy.class).key("test.strategy").dynamic(true).buildWithDefault(new ConfigurationOptionTest.DefaultStrategyImpl());
        final ConfigurationRegistry configuration = createConfiguration(Collections.singletonList(option), new SimpleSource());
        assertThat(option.getValidOptions()).containsExactlyInAnyOrder(ConfigurationOptionTest.DefaultStrategyImpl.class.getName(), ConfigurationOptionTest.SpecialStrategyImpl.class.getName());
        assertThat(option.getValue()).isInstanceOf(ConfigurationOptionTest.DefaultStrategyImpl.class);
        configuration.save("test.strategy", ConfigurationOptionTest.SpecialStrategyImpl.class.getName(), NAME);
        assertThat(option.getValue()).isInstanceOf(ConfigurationOptionTest.SpecialStrategyImpl.class);
        assertThatThrownBy(() -> configuration.save("test.strategy", .class.getName(), SimpleSource.NAME)).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Invalid option");
    }

    enum TestEnum {

        FOO,
        BAR;
        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    @Test
    public void testEnum() throws Exception {
        final ConfigurationOption<ConfigurationOptionTest.TestEnum> option = ConfigurationOption.enumOption(ConfigurationOptionTest.TestEnum.class).key("test.enum").dynamic(true).buildWithDefault(ConfigurationOptionTest.TestEnum.FOO);
        final ConfigurationRegistry configuration = createConfiguration(Collections.singletonList(option), new SimpleSource());
        assertThat(option.getValidOptions()).containsExactlyInAnyOrder("FOO", "BAR");
        assertThat(option.getValidOptionsLabelMap()).containsEntry("FOO", "foo").containsEntry("BAR", "bar");
        assertThat(option.getValue()).isEqualTo(ConfigurationOptionTest.TestEnum.FOO);
        configuration.save("test.enum", "BAR", NAME);
        assertThat(option.getValue()).isEqualTo(ConfigurationOptionTest.TestEnum.BAR);
        assertThatThrownBy(() -> configuration.save("test.enum", "BAZ", SimpleSource.NAME)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testValidOptions_list() throws Exception {
        assertThatThrownBy(() -> ConfigurationOption.integersOption().key("test.list").dynamic(true).addValidOption(Arrays.asList(1, 2)).buildWithDefault(Collections.singleton(1))).isInstanceOf(UnsupportedOperationException.class);
    }
}

