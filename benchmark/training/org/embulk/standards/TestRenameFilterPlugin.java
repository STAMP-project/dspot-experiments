package org.embulk.standards;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import org.embulk.EmbulkTestRuntime;
import org.embulk.config.ConfigException;
import org.embulk.config.ConfigSource;
import org.embulk.config.TaskSource;
import org.embulk.config.TaskValidationException;
import org.embulk.spi.Column;
import org.embulk.spi.Exec;
import org.embulk.spi.FilterPlugin;
import org.embulk.spi.Schema;
import org.embulk.spi.SchemaConfigException;
import org.embulk.standards.RenameFilterPlugin.PluginTask;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * Testing |RenameFilterPlugin|.
 *
 * NOTE: DO NOT CHANGE THE EXISTING TESTS.
 *
 * As written in the comment of |RenameFilterPlugin|, the existing behaviors
 * should not change so that users are not confused.
 */
@SuppressWarnings("checkstyle:ParameterName")
public class TestRenameFilterPlugin {
    @Rule
    public EmbulkTestRuntime runtime = new EmbulkTestRuntime();

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private static final Schema SCHEMA = Schema.builder().add("_c0", STRING).add("_c1", TIMESTAMP).build();

    private static final String DEFAULT = "__use_default__";

    private RenameFilterPlugin filter;

    @Test
    public void checkDefaultValues() {
        PluginTask task = Exec.newConfigSource().loadConfig(PluginTask.class);
        Assert.assertTrue(task.getRenameMap().isEmpty());
    }

    @Test
    public void throwSchemaConfigExceptionIfColumnNotFound() {
        ConfigSource pluginConfig = Exec.newConfigSource().set("columns", ImmutableMap.of("not_found", "any_name"));
        try {
            filter.transaction(pluginConfig, TestRenameFilterPlugin.SCHEMA, new FilterPlugin.Control() {
                public void run(TaskSource task, Schema schema) {
                }
            });
            Assert.fail();
        } catch (Throwable t) {
            Assert.assertTrue((t instanceof SchemaConfigException));
        }
    }

    @Test
    public void checkRenaming() {
        ConfigSource pluginConfig = Exec.newConfigSource().set("columns", ImmutableMap.of("_c0", "_c0_new"));
        filter.transaction(pluginConfig, TestRenameFilterPlugin.SCHEMA, new FilterPlugin.Control() {
            @Override
            public void run(TaskSource task, Schema newSchema) {
                // _c0 -> _c0_new
                Column old0 = TestRenameFilterPlugin.SCHEMA.getColumn(0);
                Column new0 = newSchema.getColumn(0);
                Assert.assertEquals("_c0_new", new0.getName());
                Assert.assertEquals(old0.getType(), new0.getType());
                // _c1 is not changed
                Column old1 = TestRenameFilterPlugin.SCHEMA.getColumn(1);
                Column new1 = newSchema.getColumn(1);
                Assert.assertEquals("_c1", new1.getName());
                Assert.assertEquals(old1.getType(), new1.getType());
            }
        });
    }

    @Test
    public void checkConfigExceptionIfUnknownStringTypeOfRenamingOperator() {
        // A simple string shouldn't come as a renaming rule.
        ConfigSource pluginConfig = Exec.newConfigSource().set("rules", ImmutableList.of("string_rule"));
        try {
            filter.transaction(pluginConfig, TestRenameFilterPlugin.SCHEMA, new FilterPlugin.Control() {
                public void run(TaskSource task, Schema schema) {
                }
            });
            Assert.fail();
        } catch (Throwable t) {
            Assert.assertTrue((t instanceof ConfigException));
        }
    }

    @Test
    public void checkConfigExceptionIfUnknownListTypeOfRenamingOperator() {
        // A list [] shouldn't come as a renaming rule.
        ConfigSource pluginConfig = Exec.newConfigSource().set("rules", ImmutableList.of(ImmutableList.of("listed_operator1", "listed_operator2")));
        try {
            filter.transaction(pluginConfig, TestRenameFilterPlugin.SCHEMA, new FilterPlugin.Control() {
                public void run(TaskSource task, Schema schema) {
                }
            });
            Assert.fail();
        } catch (Throwable t) {
            Assert.assertTrue((t instanceof ConfigException));
        }
    }

    @Test
    public void checkConfigExceptionIfUnknownRenamingOperatorName() {
        ConfigSource pluginConfig = Exec.newConfigSource().set("rules", ImmutableList.of(ImmutableMap.of("rule", "some_unknown_renaming_operator")));
        try {
            filter.transaction(pluginConfig, TestRenameFilterPlugin.SCHEMA, new FilterPlugin.Control() {
                public void run(TaskSource task, Schema schema) {
                }
            });
            Assert.fail();
        } catch (Throwable t) {
            Assert.assertTrue((t instanceof ConfigException));
        }
    }

    @Test
    public void checkRuleLowerToUpperRule() {
        final String[] original = new String[]{ "_C0", "_C1", "_c2" };
        final String[] expected = new String[]{ "_C0", "_C1", "_C2" };
        ConfigSource config = Exec.newConfigSource().set("rules", ImmutableList.of(ImmutableMap.of("rule", "lower_to_upper")));
        renameAndCheckSchema(config, original, expected);
    }

    @Test
    public void checkTruncateRule() {
        final String[] original = new String[]{ "foo", "bar", "gj", "foobar", "foobarbaz" };
        final String[] expected = new String[]{ "foo", "bar", "gj", "foo", "foo" };
        ConfigSource config = Exec.newConfigSource().set("rules", ImmutableList.of(ImmutableMap.of("rule", "truncate", "max_length", "3")));
        renameAndCheckSchema(config, original, expected);
    }

    @Test
    public void checkTruncateRuleDefault() {
        final String[] original = new String[]{ "123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890" };
        final String[] expected = new String[]{ "12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678" };
        ConfigSource config = Exec.newConfigSource().set("rules", ImmutableList.of(ImmutableMap.of("rule", "truncate")));
        renameAndCheckSchema(config, original, expected);
    }

    @Test
    public void checkTruncateRuleNegative() {
        final String[] original = new String[]{ "foo" };
        ConfigSource config = Exec.newConfigSource().set("rules", ImmutableList.of(ImmutableMap.of("rule", "truncate", "max_length", (-1))));
        exception.expect(TaskValidationException.class);
        // TODO(dmikurube): Except "Caused by": exception.expectCause(instanceOf(JsonMappingException.class));
        // Needs to import org.hamcrest.Matchers... in addition to org.junit...
        renameAndCheckSchema(config, original, original);
    }

    @Test
    public void checkRuleUpperToLowerRule() {
        final String[] original = new String[]{ "_C0", "_C1", "_c2" };
        final String[] expected = new String[]{ "_c0", "_c1", "_c2" };
        ConfigSource config = Exec.newConfigSource().set("rules", ImmutableList.of(ImmutableMap.of("rule", "upper_to_lower")));
        renameAndCheckSchema(config, original, expected);
    }

    @Test
    public void checkCharacterTypesRulePassAlphabet() {
        final String[] original = new String[]{ "Internal$Foo0123--Bar" };
        final String[] expected = new String[]{ "Internal_Foo______Bar" };
        final String[] pass_types = new String[]{ "a-z", "A-Z" };
        checkCharacterTypesRuleInternal(original, expected, pass_types, "");
    }

    @Test
    public void checkCharacterTypesRulePassAlphanumeric() {
        final String[] original = new String[]{ "Internal$Foo0123--Bar" };
        final String[] expected = new String[]{ "Internal_Foo0123__Bar" };
        final String[] pass_types = new String[]{ "a-z", "A-Z", "0-9" };
        checkCharacterTypesRuleInternal(original, expected, pass_types, "");
    }

    @Test
    public void checkCharacterTypesRulePassLowercase() {
        final String[] original = new String[]{ "Internal$Foo0123--Bar" };
        final String[] expected = new String[]{ "_nternal__oo_______ar" };
        final String[] pass_types = new String[]{ "a-z" };
        checkCharacterTypesRuleInternal(original, expected, pass_types, "");
    }

    @Test
    public void checkCharacterTypesRulePassLowerwording() {
        final String[] original = new String[]{ "Internal$Foo_0123--Bar" };
        final String[] expected = new String[]{ "-nternal--oo_0123---ar" };
        final String[] pass_types = new String[]{ "a-z", "0-9" };
        checkCharacterTypesRuleInternal(original, expected, pass_types, "_", "-");
    }

    @Test
    public void checkCharacterTypesRulePassNumeric() {
        final String[] original = new String[]{ "Internal$Foo_0123--Bar" };
        final String[] expected = new String[]{ "_____________0123_____" };
        final String[] pass_types = new String[]{ "0-9" };
        checkCharacterTypesRuleInternal(original, expected, pass_types, "");
    }

    @Test
    public void checkCharacterTypesRulePassUppercase() {
        final String[] original = new String[]{ "Internal$Foo_0123--Bar" };
        final String[] expected = new String[]{ "I________F_________B__" };
        final String[] pass_types = new String[]{ "A-Z" };
        checkCharacterTypesRuleInternal(original, expected, pass_types, "");
    }

    @Test
    public void checkCharacterTypesRulePassUpperwording() {
        final String[] original = new String[]{ "Internal$Foo_0123--Bar" };
        final String[] expected = new String[]{ "I--------F--_0123--B--" };
        final String[] pass_types = new String[]{ "A-Z", "0-9" };
        checkCharacterTypesRuleInternal(original, expected, pass_types, "_", "-");
    }

    @Test
    public void checkCharacterTypesRulePassWording() {
        final String[] original = new String[]{ "Internal$Foo_0123--Bar" };
        final String[] expected = new String[]{ "Internal-Foo_0123--Bar" };
        final String[] pass_types = new String[]{ "a-z", "A-Z", "0-9" };
        checkCharacterTypesRuleInternal(original, expected, pass_types, "_", "-");
    }

    @Test
    public void checkCharacterTypesRulePassCombination() {
        final String[] original = new String[]{ "@Foobar0123_$" };
        final String[] expected = new String[]{ "__oobar0123__" };
        final String[] pass_types = new String[]{ "0-9", "a-z" };
        checkCharacterTypesRuleInternal(original, expected, pass_types, "");
    }

    @Test
    public void checkCharacterTypesRuleLongReplace() {
        final String[] original = new String[]{ "fooBAR" };
        final String[] pass_types = new String[]{ "a-z" };
        exception.expect(TaskValidationException.class);
        // TODO(dmikurube): Except "Caused by": exception.expectCause(instanceOf(JsonMappingException.class));
        // Needs to import org.hamcrest.Matchers... in addition to org.junit...
        checkCharacterTypesRuleInternal(original, original, pass_types, "", "___");
    }

    @Test
    public void checkCharacterTypesRuleEmptyReplace() {
        final String[] original = new String[]{ "fooBAR" };
        final String[] pass_types = new String[]{ "a-z" };
        exception.expect(TaskValidationException.class);
        // TODO(dmikurube): Except "Caused by": exception.expectCause(instanceOf(JsonMappingException.class));
        // Needs to import org.hamcrest.Matchers... in addition to org.junit...
        checkCharacterTypesRuleInternal(original, original, pass_types, "", "");
    }

    // TODO(dmikurube): Test a nil/null replace.
    // - rule: character_types
    // delimiter:
    @Test
    public void checkCharacterTypesRuleUnknownType() {
        final String[] original = new String[]{ "fooBAR" };
        final String[] pass_types = new String[]{ "some_unknown_keyword" };
        exception.expect(ConfigException.class);
        // TODO(dmikurube): Except "Caused by": exception.expectCause(instanceOf(JsonMappingException.class));
        // Needs to import org.hamcrest.Matchers... in addition to org.junit...
        checkCharacterTypesRuleInternal(original, original, pass_types, "");
    }

    @Test
    public void checkCharacterTypesRuleForbiddenCharSequence() {
        final String[] original = new String[]{ "fooBAR" };
        final String[] pass_types = new String[]{  };
        exception.expect(ConfigException.class);
        // TODO(dmikurube): Except "Caused by": exception.expectCause(instanceOf(JsonMappingException.class));
        // Needs to import org.hamcrest.Matchers... in addition to org.junit...
        checkCharacterTypesRuleInternal(original, original, pass_types, "\\E");
    }

    @Test
    public void checkRegexReplaceRule1() {
        final String[] original = new String[]{ "foobarbaz" };
        final String[] expected = new String[]{ "hogebarbaz" };
        checkRegexReplaceRuleInternal(original, expected, "foo", "hoge");
    }

    @Test
    public void checkRegexReplaceRule2() {
        final String[] original = new String[]{ "200_dollars" };
        final String[] expected = new String[]{ "USD200" };
        checkRegexReplaceRuleInternal(original, expected, "([0-9]+)_dollars", "USD$1");
    }

    @Test
    public void checkFirstCharacterTypesRuleReplaceSingleHyphen() {
        final String[] original = new String[]{ "foo", "012foo", "@bar", "BAZ", "&ban", "_jar", "*zip", "-zap" };
        final String[] expected = new String[]{ "_oo", "_12foo", "_bar", "_AZ", "_ban", "_jar", "_zip", "-zap" };
        final String[] pass_types = new String[]{  };
        checkFirstCharacterTypesRuleReplaceInternal(original, expected, "_", pass_types, "-");
    }

    @Test
    public void checkFirstCharacterTypesRuleReplaceMultipleSingles() {
        final String[] original = new String[]{ "foo", "012foo", "@bar", "BAZ", "&ban", "_jar", "*zip", "-zap" };
        final String[] expected = new String[]{ "_oo", "_12foo", "@bar", "_AZ", "_ban", "_jar", "*zip", "-zap" };
        final String[] pass_types = new String[]{  };
        checkFirstCharacterTypesRuleReplaceInternal(original, expected, "_", pass_types, "-@*");
    }

    @Test
    public void checkFirstCharacterTypesRuleReplaceAlphabet() {
        final String[] original = new String[]{ "foo", "012foo", "@bar", "BAZ", "&ban", "_jar", "*zip", "-zap" };
        final String[] expected = new String[]{ "foo", "_12foo", "_bar", "BAZ", "_ban", "_jar", "_zip", "_zap" };
        final String[] pass_types = new String[]{ "a-z", "A-Z" };
        checkFirstCharacterTypesRuleReplaceInternal(original, expected, "_", pass_types);
    }

    @Test
    public void checkFirstCharacterTypesRuleReplaceAlphanumeric() {
        final String[] original = new String[]{ "foo", "012foo", "@bar", "BAZ", "&ban", "_jar", "*zip", "-zap" };
        final String[] expected = new String[]{ "foo", "012foo", "_bar", "BAZ", "_ban", "_jar", "_zip", "_zap" };
        final String[] pass_types = new String[]{ "a-z", "A-Z", "0-9" };
        checkFirstCharacterTypesRuleReplaceInternal(original, expected, "_", pass_types);
    }

    @Test
    public void checkFirstCharacterTypesRuleReplaceLowercase() {
        final String[] original = new String[]{ "foo", "012foo", "@bar", "BAZ", "&ban", "_jar", "*zip", "-zap" };
        final String[] expected = new String[]{ "foo", "_12foo", "_bar", "_AZ", "_ban", "_jar", "_zip", "_zap" };
        final String[] pass_types = new String[]{ "a-z" };
        checkFirstCharacterTypesRuleReplaceInternal(original, expected, "_", pass_types);
    }

    @Test
    public void checkFirstCharacterTypesRuleReplaceLowerwording() {
        final String[] original = new String[]{ "foo", "012foo", "@bar", "BAZ", "&ban", "_jar", "*zip", "-zap" };
        final String[] expected = new String[]{ "foo", "012foo", "-bar", "-AZ", "-ban", "_jar", "-zip", "-zap" };
        final String[] pass_types = new String[]{ "a-z", "0-9" };
        checkFirstCharacterTypesRuleReplaceInternal(original, expected, "-", pass_types, "_");
    }

    @Test
    public void checkFirstCharacterTypesRuleReplaceNumeric() {
        final String[] original = new String[]{ "foo", "012foo", "@bar", "BAZ", "&ban", "_jar", "*zip", "-zap" };
        final String[] expected = new String[]{ "_oo", "012foo", "_bar", "_AZ", "_ban", "_jar", "_zip", "_zap" };
        final String[] pass_types = new String[]{ "0-9" };
        checkFirstCharacterTypesRuleReplaceInternal(original, expected, "_", pass_types);
    }

    @Test
    public void checkFirstCharacterTypesRuleReplaceUppercase() {
        final String[] original = new String[]{ "foo", "012foo", "@bar", "BAZ", "&ban", "_jar", "*zip", "-zap" };
        final String[] expected = new String[]{ "_oo", "_12foo", "_bar", "BAZ", "_ban", "_jar", "_zip", "_zap" };
        final String[] pass_types = new String[]{ "A-Z" };
        checkFirstCharacterTypesRuleReplaceInternal(original, expected, "_", pass_types);
    }

    @Test
    public void checkFirstCharacterTypesRuleReplaceUpperwording() {
        final String[] original = new String[]{ "foo", "012foo", "@bar", "BAZ", "&ban", "_jar", "*zip", "-zap" };
        final String[] expected = new String[]{ "-oo", "012foo", "-bar", "BAZ", "-ban", "_jar", "-zip", "-zap" };
        final String[] pass_types = new String[]{ "A-Z", "0-9" };
        checkFirstCharacterTypesRuleReplaceInternal(original, expected, "-", pass_types, "_");
    }

    @Test
    public void checkFirstCharacterTypesRuleReplaceWording() {
        final String[] original = new String[]{ "foo", "012foo", "@bar", "BAZ", "&ban", "_jar", "*zip", "-zap" };
        final String[] expected = new String[]{ "foo", "012foo", "$bar", "BAZ", "$ban", "_jar", "$zip", "$zap" };
        final String[] pass_types = new String[]{ "a-z", "A-Z", "0-9" };
        checkFirstCharacterTypesRuleReplaceInternal(original, expected, "$", pass_types, "_");
    }

    @Test
    public void checkFirstCharacterTypesRuleReplaceUnknownFirst() {
        final String[] original = new String[]{ "foo" };
        final String[] pass_types = new String[]{ "some_unknown_type" };
        exception.expect(ConfigException.class);
        // TODO(dmikurube): Except "Caused by": exception.expectCause(instanceOf(JsonMappingException.class));
        // Needs to import org.hamcrest.Matchers... in addition to org.junit...
        checkFirstCharacterTypesRuleReplaceInternal(original, original, "_", pass_types);
    }

    @Test
    public void checkFirstCharacterTypesRuleReplaceForbiddenCharSequence() {
        final String[] original = new String[]{ "foo" };
        final String[] pass_types = new String[]{  };
        exception.expect(ConfigException.class);
        // TODO(dmikurube): Except "Caused by": exception.expectCause(instanceOf(JsonMappingException.class));
        // Needs to import org.hamcrest.Matchers... in addition to org.junit...
        checkFirstCharacterTypesRuleReplaceInternal(original, original, "_", pass_types, "\\E");
    }

    @Test
    public void checkFirstCharacterTypesRulePrefixSingleHyphen() {
        final String[] original = new String[]{ "foo", "012foo", "@bar", "BAZ", "&ban", "_jar", "*zip", "-zap" };
        final String[] expected = new String[]{ "_foo", "_012foo", "_@bar", "_BAZ", "_&ban", "__jar", "_*zip", "-zap" };
        final String[] pass_types = new String[]{  };
        checkFirstCharacterTypesRulePrefixInternal(original, expected, "_", pass_types, "-");
    }

    @Test
    public void checkFirstCharacterTypesRulePrefixMultipleSingles() {
        final String[] original = new String[]{ "foo", "012foo", "@bar", "BAZ", "&ban", "_jar", "*zip", "-zap" };
        final String[] expected = new String[]{ "_foo", "_012foo", "@bar", "_BAZ", "_&ban", "__jar", "*zip", "-zap" };
        final String[] pass_types = new String[]{  };
        checkFirstCharacterTypesRulePrefixInternal(original, expected, "_", pass_types, "-@*");
    }

    @Test
    public void checkFirstCharacterTypesRulePrefixAlphabet() {
        final String[] original = new String[]{ "foo", "012foo", "@bar", "BAZ", "&ban", "_jar", "*zip", "-zap" };
        final String[] expected = new String[]{ "foo", "_012foo", "_@bar", "BAZ", "_&ban", "__jar", "_*zip", "_-zap" };
        final String[] pass_types = new String[]{ "a-z", "A-Z" };
        checkFirstCharacterTypesRulePrefixInternal(original, expected, "_", pass_types);
    }

    @Test
    public void checkFirstCharacterTypesRulePrefixAlphanumeric() {
        final String[] original = new String[]{ "foo", "012foo", "@bar", "BAZ", "&ban", "_jar", "*zip", "-zap" };
        final String[] expected = new String[]{ "foo", "012foo", "_@bar", "BAZ", "_&ban", "__jar", "_*zip", "_-zap" };
        final String[] pass_types = new String[]{ "a-z", "A-Z", "0-9" };
        checkFirstCharacterTypesRulePrefixInternal(original, expected, "_", pass_types);
    }

    @Test
    public void checkFirstCharacterTypesRulePrefixLowercase() {
        final String[] original = new String[]{ "foo", "012foo", "@bar", "BAZ", "&ban", "_jar", "*zip", "-zap" };
        final String[] expected = new String[]{ "foo", "_012foo", "_@bar", "_BAZ", "_&ban", "__jar", "_*zip", "_-zap" };
        final String[] pass_types = new String[]{ "a-z" };
        checkFirstCharacterTypesRulePrefixInternal(original, expected, "_", pass_types);
    }

    @Test
    public void checkFirstCharacterTypesRulePrefixLowerwording() {
        final String[] original = new String[]{ "foo", "012foo", "@bar", "BAZ", "&ban", "_jar", "*zip", "-zap" };
        final String[] expected = new String[]{ "foo", "012foo", "-@bar", "-BAZ", "-&ban", "_jar", "-*zip", "--zap" };
        final String[] pass_types = new String[]{ "a-z", "0-9" };
        checkFirstCharacterTypesRulePrefixInternal(original, expected, "-", pass_types, "_");
    }

    @Test
    public void checkFirstCharacterTypesRulePrefixNumeric() {
        final String[] original = new String[]{ "foo", "012foo", "@bar", "BAZ", "&ban", "_jar", "*zip", "-zap" };
        final String[] expected = new String[]{ "_foo", "012foo", "_@bar", "_BAZ", "_&ban", "__jar", "_*zip", "_-zap" };
        final String[] pass_types = new String[]{ "0-9" };
        checkFirstCharacterTypesRulePrefixInternal(original, expected, "_", pass_types);
    }

    @Test
    public void checkFirstCharacterTypesRulePrefixUppercase() {
        final String[] original = new String[]{ "foo", "012foo", "@bar", "BAZ", "&ban", "_jar", "*zip", "-zap" };
        final String[] expected = new String[]{ "_foo", "_012foo", "_@bar", "BAZ", "_&ban", "__jar", "_*zip", "_-zap" };
        final String[] pass_types = new String[]{ "A-Z" };
        checkFirstCharacterTypesRulePrefixInternal(original, expected, "_", pass_types);
    }

    @Test
    public void checkFirstCharacterTypesRulePrefixUpperwording() {
        final String[] original = new String[]{ "foo", "012foo", "@bar", "BAZ", "&ban", "_jar", "*zip", "-zap" };
        final String[] expected = new String[]{ "-foo", "012foo", "-@bar", "BAZ", "-&ban", "_jar", "-*zip", "--zap" };
        final String[] pass_types = new String[]{ "A-Z", "0-9" };
        checkFirstCharacterTypesRulePrefixInternal(original, expected, "-", pass_types, "_");
    }

    @Test
    public void checkFirstCharacterTypesRulePrefixWording() {
        final String[] original = new String[]{ "foo", "012foo", "@bar", "BAZ", "&ban", "_jar", "*zip", "-zap" };
        final String[] expected = new String[]{ "foo", "012foo", "$@bar", "BAZ", "$&ban", "_jar", "$*zip", "$-zap" };
        final String[] pass_types = new String[]{ "a-z", "A-Z", "0-9" };
        checkFirstCharacterTypesRulePrefixInternal(original, expected, "$", pass_types, "_");
    }

    @Test
    public void checkFirstCharacterTypesRuleEmptyPrefix() {
        final String[] original = new String[]{ "foo" };
        final String[] pass_types = new String[]{  };
        exception.expect(ConfigException.class);
        // TODO(dmikurube): Except "Caused by": exception.expectCause(instanceOf(JsonMappingException.class));
        // Needs to import org.hamcrest.Matchers... in addition to org.junit...
        checkFirstCharacterTypesRulePrefixInternal(original, original, "", pass_types);
    }

    @Test
    public void checkFirstCharacterTypesRuleLongPrefix() {
        final String[] original = new String[]{ "foo" };
        final String[] pass_types = new String[]{  };
        exception.expect(ConfigException.class);
        // TODO(dmikurube): Except "Caused by": exception.expectCause(instanceOf(JsonMappingException.class));
        // Needs to import org.hamcrest.Matchers... in addition to org.junit...
        checkFirstCharacterTypesRulePrefixInternal(original, original, "__", pass_types);
    }

    @Test
    public void checkFirstCharacterTypesRuleEmptyReplace() {
        final String[] original = new String[]{ "foo" };
        final String[] pass_types = new String[]{  };
        exception.expect(ConfigException.class);
        // TODO(dmikurube): Except "Caused by": exception.expectCause(instanceOf(JsonMappingException.class));
        // Needs to import org.hamcrest.Matchers... in addition to org.junit...
        checkFirstCharacterTypesRuleReplaceInternal(original, original, "", pass_types);
    }

    @Test
    public void checkFirstCharacterTypesRuleLongReplace() {
        final String[] original = new String[]{ "foo" };
        final String[] pass_types = new String[]{  };
        exception.expect(ConfigException.class);
        // TODO(dmikurube): Except "Caused by": exception.expectCause(instanceOf(JsonMappingException.class));
        // Needs to import org.hamcrest.Matchers... in addition to org.junit...
        checkFirstCharacterTypesRuleReplaceInternal(original, original, "__", pass_types);
    }

    @Test
    public void checkFirstCharacterTypesRulePrefixUnknownFirst() {
        final String[] original = new String[]{ "foo" };
        final String[] pass_types = new String[]{ "some_unknown_type" };
        exception.expect(ConfigException.class);
        // TODO(dmikurube): Except "Caused by": exception.expectCause(instanceOf(JsonMappingException.class));
        // Needs to import org.hamcrest.Matchers... in addition to org.junit...
        checkFirstCharacterTypesRulePrefixInternal(original, original, "_", pass_types);
    }

    @Test
    public void checkFirstCharacterTypesRulePrefixForbiddenCharSequence() {
        final String[] original = new String[]{ "foo" };
        final String[] pass_types = new String[]{  };
        exception.expect(ConfigException.class);
        // TODO(dmikurube): Except "Caused by": exception.expectCause(instanceOf(JsonMappingException.class));
        // Needs to import org.hamcrest.Matchers... in addition to org.junit...
        checkFirstCharacterTypesRulePrefixInternal(original, original, "\\E", pass_types);
    }

    @Test
    public void checkFirstCharacterTypesRuleBothReplacePrefix() {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("rule", "first_character_types");
        parameters.put("replace", "_");
        parameters.put("prefix", "_");
        ConfigSource config = Exec.newConfigSource().set("rules", ImmutableList.of(ImmutableMap.copyOf(parameters)));
        exception.expect(ConfigException.class);
        // TODO(dmikurube): Except "Caused by": exception.expectCause(instanceOf(JsonMappingException.class));
        // Needs to import org.hamcrest.Matchers... in addition to org.junit...
        renameAndCheckSchema(config, new String[0], new String[0]);
    }

    @Test
    public void checkFirstCharacterTypesRuleNeitherReplacePrefix() {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("rule", "first_character_types");
        ConfigSource config = Exec.newConfigSource().set("rules", ImmutableList.of(ImmutableMap.copyOf(parameters)));
        exception.expect(ConfigException.class);
        // TODO(dmikurube): Except "Caused by": exception.expectCause(instanceOf(JsonMappingException.class));
        // Needs to import org.hamcrest.Matchers... in addition to org.junit...
        renameAndCheckSchema(config, new String[0], new String[0]);
    }

    @Test
    public void checkUniqueNumberSuffixRuleLongDelimiter() {
        final String[] columnNames = new String[]{ "c" };
        try {
            checkUniqueNumberSuffixRuleInternal(columnNames, columnNames, "__");
        } catch (Throwable t) {
            Assert.assertTrue((t instanceof ConfigException));
        }
    }

    @Test
    public void checkUniqueNumberSuffixRuleDigitDelimiter() {
        final String[] columnNames = new String[]{ "c" };
        try {
            checkUniqueNumberSuffixRuleInternal(columnNames, columnNames, "2");
        } catch (Throwable t) {
            Assert.assertTrue((t instanceof ConfigException));
        }
    }

    @Test
    public void checkUniqueNumberSuffixRuleShortMaxLength() {
        final String[] columnNames = new String[]{ "c" };
        try {
            checkUniqueNumberSuffixRuleInternal(columnNames, columnNames, TestRenameFilterPlugin.DEFAULT, (-1), 7);
        } catch (Throwable t) {
            Assert.assertTrue((t instanceof ConfigException));
        }
    }

    // TODO(dmikurube): Test a nil/null delimiter in "unique".
    // - rule: unique
    // delimiter:
    @Test
    public void checkUniqueNumberSuffixRule0() {
        final String[] originalColumnNames = new String[]{ "a", "b", "c", "d", "e" };
        final String[] expectedColumnNames = new String[]{ "a", "b", "c", "d", "e" };
        checkUniqueNumberSuffixRuleInternal(originalColumnNames, expectedColumnNames);
    }

    @Test
    public void checkUniqueNumberSuffixRule1() {
        final String[] originalColumnNames = new String[]{ "c", "c", "c1", "c2", "c2" };
        final String[] expectedColumnNames = new String[]{ "c", "c_2", "c1", "c2", "c2_2" };
        checkUniqueNumberSuffixRuleInternal(originalColumnNames, expectedColumnNames);
    }

    @Test
    public void checkUniqueNumberSuffixRule2() {
        final String[] originalColumnNames = new String[]{ "c", "c", "c_1", "c_3", "c" };
        final String[] expectedColumnNames = new String[]{ "c", "c_2", "c_1", "c_3", "c_4" };
        checkUniqueNumberSuffixRuleInternal(originalColumnNames, expectedColumnNames);
    }

    @Test
    public void checkUniqueNumberSuffixRule3() {
        final String[] originalColumnNames = new String[]{ "c", "c", "c", "c", "c", "c", "c", "c", "c", "c", "c_1", "c_1" };
        final String[] expectedColumnNames = new String[]{ "c", "c_2", "c_3", "c_4", "c_5", "c_6", "c_7", "c_8", "c_9", "c_10", "c_1", "c_1_2" };
        checkUniqueNumberSuffixRuleInternal(originalColumnNames, expectedColumnNames);
    }

    @Test
    public void checkUniqueNumberSuffixRule4DifferentDelimiter() {
        final String[] originalColumnNames = new String[]{ "c", "c", "c1", "c2", "c2" };
        final String[] expectedColumnNames = new String[]{ "c", "c-2", "c1", "c2", "c2-2" };
        checkUniqueNumberSuffixRuleInternal(originalColumnNames, expectedColumnNames, "-");
    }

    @Test
    public void checkUniqueNumberSuffixRule5Digits() {
        final String[] originalColumnNames = new String[]{ "c", "c", "c1", "c2", "c2" };
        final String[] expectedColumnNames = new String[]{ "c", "c_0002", "c1", "c2", "c2_0002" };
        checkUniqueNumberSuffixRuleInternal(originalColumnNames, expectedColumnNames, TestRenameFilterPlugin.DEFAULT, 4, (-1));
    }

    @Test
    public void checkUniqueNumberSuffixRule6MaxLength1() {
        final String[] originalColumnNames = new String[]{ "column", "column", "column_1", "column_2", "column_2" };
        final String[] expectedColumnNames = new String[]{ "column", "column_3", "column_1", "column_2", "column_4" };
        checkUniqueNumberSuffixRuleInternal(originalColumnNames, expectedColumnNames, TestRenameFilterPlugin.DEFAULT, (-1), 8);
    }

    @Test
    public void checkUniqueNumberSuffixRule7() {
        final String[] originalColumnNames = new String[]{ "column", "column", "column_2", "column_3" };
        final String[] expectedColumnNames = new String[]{ "column", "column_4", "column_2", "column_3" };
        checkUniqueNumberSuffixRuleInternal(originalColumnNames, expectedColumnNames, TestRenameFilterPlugin.DEFAULT, (-1), 8);
    }

    @Test
    public void checkUniqueNumberSuffixRule8MaxLength2() {
        final String[] originalColumnNames = new String[]{ "column", "colum", "column", "colum", "column", "colum", "column", "colum", "column", "colum", "column", "colum", "column", "colum", "column", "colum", "column", "colum", "column", "colum", "column", "colum" };
        final String[] expectedColumnNames = new String[]{ "column", "colum", "column_2", "colum_2", "column_3", "colum_3", "column_4", "colum_4", "column_5", "colum_5", "column_6", "colum_6", "column_7", "colum_7", "column_8", "colum_8", "column_9", "colum_9", "colum_10", "colum_11", "colum_12", "colum_13" };
        checkUniqueNumberSuffixRuleInternal(originalColumnNames, expectedColumnNames, TestRenameFilterPlugin.DEFAULT, (-1), 8);
    }

    @Test
    public void checkUniqueNumberSuffixRule9MaxLength3() {
        final String[] originalColumnNames = new String[]{ "column", "column", "column", "column", "column", "column", "column", "column", "column", "colum", "colum", "colum", "colum", "colum", "colum", "colum", "colum", "column", "colum", "column", "colum", "column" };
        final String[] expectedColumnNames = new String[]{ "column", "column_2", "column_3", "column_4", "column_5", "column_6", "column_7", "column_8", "column_9", "colum", "colum_2", "colum_3", "colum_4", "colum_5", "colum_6", "colum_7", "colum_8", "colum_10", "colum_9", "colum_11", "colum_12", "colum_13" };
        checkUniqueNumberSuffixRuleInternal(originalColumnNames, expectedColumnNames, TestRenameFilterPlugin.DEFAULT, (-1), 8);
    }

    @Test
    public void checkUniqueNumberSuffixRule10EsteemOriginalNames() {
        final String[] originalColumnNames = new String[]{ "c", "c", "c_2" };
        final String[] expectedColumnNames = new String[]{ "c", "c_3", "c_2" };
        checkUniqueNumberSuffixRuleInternal(originalColumnNames, expectedColumnNames, TestRenameFilterPlugin.DEFAULT, (-1), (-1));
    }

    @Test
    public void checkUniqueNumberSuffixRuleNegativeLength() {
        final String[] originalColumnNames = new String[]{ "column" };
        exception.expect(ConfigException.class);
        // TODO(dmikurube): Except "Caused by": exception.expectCause(instanceOf(JsonMappingException.class));
        // Needs to import org.hamcrest.Matchers... in addition to org.junit...
        checkUniqueNumberSuffixRuleInternal(originalColumnNames, originalColumnNames, TestRenameFilterPlugin.DEFAULT, (-1), (-2));
    }
}

