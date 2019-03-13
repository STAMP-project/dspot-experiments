/**
 * SonarQube
 * Copyright (C) 2009-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.application.command;


import com.google.common.collect.ImmutableMap;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.sonar.process.MessageException;
import org.sonar.process.Props;


@RunWith(DataProviderRunner.class)
public class JvmOptionsTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private final Random random = new Random();

    private final String randomPropertyName = randomAlphanumeric(3);

    private final String randomPrefix = "-" + (randomAlphabetic(5).toLowerCase(Locale.ENGLISH));

    private final String randomValue = randomAlphanumeric(4).toLowerCase(Locale.ENGLISH);

    private final Properties properties = new Properties();

    private final JvmOptions underTest = new JvmOptions();

    @Test
    public void constructor_without_arguments_creates_empty_JvmOptions() {
        JvmOptions<JvmOptions> testJvmOptions = new JvmOptions();
        assertThat(testJvmOptions.getAll()).isEmpty();
    }

    @Test
    public void constructor_throws_NPE_if_argument_is_null() {
        expectJvmOptionNotNullNPE();
        new JvmOptions(null);
    }

    @Test
    public void constructor_throws_NPE_if_any_option_prefix_is_null() {
        Map<String, String> mandatoryJvmOptions = JvmOptionsTest.shuffleThenToMap(Stream.of(IntStream.range(0, random.nextInt(10)).mapToObj(( i) -> new JvmOptionsTest.Option("-B", String.valueOf(i))), Stream.of(new JvmOptionsTest.Option(null, "value"))).flatMap(( s) -> s));
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("JVM option prefix can't be null");
        new JvmOptions(mandatoryJvmOptions);
    }

    @Test
    public void constructor_throws_IAE_if_any_option_prefix_does_not_start_with_dash() {
        String invalidPrefix = randomAlphanumeric(3);
        Map<String, String> mandatoryJvmOptions = JvmOptionsTest.shuffleThenToMap(Stream.of(IntStream.range(0, random.nextInt(10)).mapToObj(( i) -> new JvmOptionsTest.Option("-B", String.valueOf(i))), Stream.of(new JvmOptionsTest.Option(invalidPrefix, "value"))).flatMap(( s) -> s));
        expectJvmOptionNotEmptyAndStartByDashIAE();
        new JvmOptions(mandatoryJvmOptions);
    }

    @Test
    public void constructor_throws_NPE_if_any_option_value_is_null() {
        Map<String, String> mandatoryJvmOptions = JvmOptionsTest.shuffleThenToMap(Stream.of(IntStream.range(0, random.nextInt(10)).mapToObj(( i) -> new JvmOptionsTest.Option("-B", String.valueOf(i))), Stream.of(new JvmOptionsTest.Option("-prefix", null))).flatMap(( s) -> s));
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("JVM option value can't be null");
        new JvmOptions(mandatoryJvmOptions);
    }

    @Test
    public void add_throws_NPE_if_argument_is_null() {
        expectJvmOptionNotNullNPE();
        underTest.add(null);
    }

    @Test
    public void add_throws_IAE_if_argument_does_not_start_with_dash() {
        expectJvmOptionNotEmptyAndStartByDashIAE();
        underTest.add(randomAlphanumeric(3));
    }

    @Test
    public void add_throws_MessageException_if_option_starts_with_prefix_of_mandatory_option_but_has_different_value() {
        String[] optionOverrides = new String[]{ randomPrefix, (randomPrefix) + (randomAlphanumeric(1)), (randomPrefix) + (randomAlphanumeric(2)), (randomPrefix) + (randomAlphanumeric(3)), (randomPrefix) + (randomAlphanumeric(4)), (randomPrefix) + (randomValue.substring(1)), (randomPrefix) + (randomValue.substring(2)), (randomPrefix) + (randomValue.substring(3)) };
        JvmOptions underTest = new JvmOptions(ImmutableMap.of(randomPrefix, randomValue));
        for (String optionOverride : optionOverrides) {
            try {
                underTest.add(optionOverride);
                fail("an MessageException should have been thrown");
            } catch (MessageException e) {
                assertThat(e.getMessage()).isEqualTo((((("a JVM option can't overwrite mandatory JVM options. " + optionOverride) + " overwrites ") + (randomPrefix)) + (randomValue)));
            }
        }
    }

    @Test
    public void add_checks_against_mandatory_options_is_case_sensitive() {
        String[] optionOverrides = new String[]{ randomPrefix, (randomPrefix) + (randomAlphanumeric(1)), (randomPrefix) + (randomAlphanumeric(2)), (randomPrefix) + (randomAlphanumeric(3)), (randomPrefix) + (randomAlphanumeric(4)), (randomPrefix) + (randomValue.substring(1)), (randomPrefix) + (randomValue.substring(2)), (randomPrefix) + (randomValue.substring(3)) };
        JvmOptions underTest = new JvmOptions(ImmutableMap.of(randomPrefix, randomValue));
        for (String optionOverride : optionOverrides) {
            underTest.add(optionOverride.toUpperCase(Locale.ENGLISH));
        }
    }

    @Test
    public void add_accepts_property_equal_to_mandatory_option_and_does_not_add_it_twice() {
        JvmOptions underTest = new JvmOptions(ImmutableMap.of(randomPrefix, randomValue));
        underTest.add(((randomPrefix) + (randomValue)));
        assertThat(underTest.getAll()).containsOnly(((randomPrefix) + (randomValue)));
    }

    @Test
    public void addFromMandatoryProperty_fails_with_IAE_if_property_does_not_exist() {
        expectMissingPropertyIAE(this.randomPropertyName);
        underTest.addFromMandatoryProperty(new Props(properties), this.randomPropertyName);
    }

    @Test
    public void addFromMandatoryProperty_fails_with_IAE_if_property_contains_an_empty_value() {
        expectMissingPropertyIAE(this.randomPropertyName);
        underTest.addFromMandatoryProperty(new Props(properties), randomPropertyName);
    }

    @Test
    public void addFromMandatoryProperty_supports_spaces_inside_options() {
        properties.put(randomPropertyName, "-foo bar -duck");
        underTest.addFromMandatoryProperty(new Props(properties), randomPropertyName);
        assertThat(underTest.getAll()).containsOnly("-foo bar", "-duck");
    }

    @Test
    public void addFromMandatoryProperty_throws_IAE_if_option_starts_with_prefix_of_mandatory_option_but_has_different_value() {
        String[] optionOverrides = new String[]{ randomPrefix, (randomPrefix) + (randomValue.substring(1)), (randomPrefix) + (randomValue.substring(1)), (randomPrefix) + (randomValue.substring(2)), (randomPrefix) + (randomValue.substring(3)), ((randomPrefix) + (randomValue.substring(3))) + (randomAlphanumeric(1)), ((randomPrefix) + (randomValue.substring(3))) + (randomAlphanumeric(2)), ((randomPrefix) + (randomValue.substring(3))) + (randomAlphanumeric(3)), ((randomPrefix) + (randomValue)) + (randomAlphanumeric(1)) };
        JvmOptions underTest = new JvmOptions(ImmutableMap.of(randomPrefix, randomValue));
        for (String optionOverride : optionOverrides) {
            try {
                properties.put(randomPropertyName, optionOverride);
                underTest.addFromMandatoryProperty(new Props(properties), randomPropertyName);
                fail("an MessageException should have been thrown");
            } catch (MessageException e) {
                assertThat(e.getMessage()).isEqualTo(((((((("a JVM option can't overwrite mandatory JVM options. " + "The following JVM options defined by property '") + (randomPropertyName)) + "' are invalid: ") + optionOverride) + " overwrites ") + (randomPrefix)) + (randomValue)));
            }
        }
    }

    @Test
    public void addFromMandatoryProperty_checks_against_mandatory_options_is_case_sensitive() {
        String[] optionOverrides = new String[]{ randomPrefix, (randomPrefix) + (randomValue.substring(1)), (randomPrefix) + (randomValue.substring(1)), (randomPrefix) + (randomValue.substring(2)), (randomPrefix) + (randomValue.substring(3)), ((randomPrefix) + (randomValue.substring(3))) + (randomAlphanumeric(1)), ((randomPrefix) + (randomValue.substring(3))) + (randomAlphanumeric(2)), ((randomPrefix) + (randomValue.substring(3))) + (randomAlphanumeric(3)), ((randomPrefix) + (randomValue)) + (randomAlphanumeric(1)) };
        JvmOptions underTest = new JvmOptions(ImmutableMap.of(randomPrefix, randomValue));
        for (String optionOverride : optionOverrides) {
            properties.setProperty(randomPropertyName, optionOverride.toUpperCase(Locale.ENGLISH));
            underTest.addFromMandatoryProperty(new Props(properties), randomPropertyName);
        }
    }

    @Test
    public void addFromMandatoryProperty_reports_all_overriding_options_in_single_exception() {
        String overriding1 = randomPrefix;
        String overriding2 = ((randomPrefix) + (randomValue)) + (randomAlphanumeric(1));
        properties.setProperty(randomPropertyName, ((("-foo " + overriding1) + " -bar ") + overriding2));
        JvmOptions underTest = new JvmOptions(ImmutableMap.of(randomPrefix, randomValue));
        expectedException.expect(MessageException.class);
        expectedException.expectMessage((((((((((((("a JVM option can't overwrite mandatory JVM options. " + "The following JVM options defined by property '") + (randomPropertyName)) + "' are invalid: ") + overriding1) + " overwrites ") + (randomPrefix)) + (randomValue)) + ", ") + overriding2) + " overwrites ") + (randomPrefix)) + (randomValue)));
        underTest.addFromMandatoryProperty(new Props(properties), randomPropertyName);
    }

    @Test
    public void addFromMandatoryProperty_accepts_property_equal_to_mandatory_option_and_does_not_add_it_twice() {
        JvmOptions underTest = new JvmOptions(ImmutableMap.of(randomPrefix, randomValue));
        properties.put(randomPropertyName, ((randomPrefix) + (randomValue)));
        underTest.addFromMandatoryProperty(new Props(properties), randomPropertyName);
        assertThat(underTest.getAll()).containsOnly(((randomPrefix) + (randomValue)));
    }

    @Test
    public void toString_prints_all_jvm_options() {
        underTest.add("-foo").add("-bar");
        assertThat(underTest.toString()).isEqualTo("[-foo, -bar]");
    }

    private static final class Option {
        private final String prefix;

        private final String value;

        private Option(String prefix, String value) {
            this.prefix = prefix;
            this.value = value;
        }

        public String getPrefix() {
            return prefix;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return ((("[" + (prefix)) + "-") + (value)) + ']';
        }
    }
}

