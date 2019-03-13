/**
 * Copyright 2018 Confluent Inc.
 *
 * Licensed under the Confluent Community License (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 *
 * http://www.confluent.io/confluent-community-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package io.confluent.ksql.config;


import KsqlConfig.CURRENT_DEF;
import KsqlConfig.KSQL_SERVICE_ID_CONFIG;
import SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG;
import StreamsConfig.APPLICATION_ID_CONFIG;
import StreamsConfig.SEND_BUFFER_CONFIG;
import org.apache.kafka.common.config.ConfigDef.ConfigKey;
import org.apache.kafka.common.config.ConfigException;
import org.apache.kafka.common.config.types.Password;
import org.apache.kafka.streams.StreamsConfig;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class ConfigItemTest {
    private static final ConfigKey KEY_NO_VALIDATOR = CURRENT_DEF.configKeys().get(KSQL_SERVICE_ID_CONFIG);

    private static final ConfigKey KEY_WITH_VALIDATOR = StreamsConfig.configDef().configKeys().get(SEND_BUFFER_CONFIG);

    private static final ConfigKey KEY_NO_DEFAULT = StreamsConfig.configDef().configKeys().get(APPLICATION_ID_CONFIG);

    private static final ConfigKey PASSWORD_KEY = CURRENT_DEF.configKeys().get(SSL_KEYSTORE_PASSWORD_CONFIG);

    private static final ConfigItem RESOLVED_NO_VALIDATOR = ConfigItem.resolved(ConfigItemTest.KEY_NO_VALIDATOR);

    private static final ConfigItem RESOLVED_WITH_VALIDATOR = ConfigItem.resolved(ConfigItemTest.KEY_WITH_VALIDATOR);

    private static final ConfigItem RESOLVED_NO_DEFAULT = ConfigItem.resolved(ConfigItemTest.KEY_NO_DEFAULT);

    private static final ConfigItem RESOLVED_PASSWORD = ConfigItem.resolved(ConfigItemTest.PASSWORD_KEY);

    private static final ConfigItem UNRESOLVED = ConfigItem.unresolved("some.unresolved.prop");

    @Test
    public void shouldReturnResolved() {
        Assert.assertThat(ConfigItemTest.RESOLVED_NO_VALIDATOR.isResolved(), Matchers.is(true));
        Assert.assertThat(ConfigItemTest.UNRESOLVED.isResolved(), Matchers.is(false));
    }

    @Test
    public void shouldReturnPropertyName() {
        Assert.assertThat(ConfigItemTest.RESOLVED_NO_VALIDATOR.getPropertyName(), Matchers.is(KSQL_SERVICE_ID_CONFIG));
        Assert.assertThat(ConfigItemTest.RESOLVED_WITH_VALIDATOR.getPropertyName(), Matchers.is(SEND_BUFFER_CONFIG));
        Assert.assertThat(ConfigItemTest.UNRESOLVED.getPropertyName(), Matchers.is("some.unresolved.prop"));
    }

    @Test
    public void shouldPassThroughUnresolvedValueOnParse() {
        Assert.assertThat(ConfigItemTest.UNRESOLVED.parseValue("anything"), Matchers.is("anything"));
        Assert.assertThat(ConfigItemTest.UNRESOLVED.parseValue(12345L), Matchers.is(12345L));
    }

    @Test
    public void shouldParseResolvedValue() {
        Assert.assertThat(ConfigItemTest.RESOLVED_NO_VALIDATOR.parseValue("ksql_default"), Matchers.is("ksql_default"));
    }

    @Test
    public void shouldCoerceParsedValue() {
        Assert.assertThat(ConfigItemTest.RESOLVED_NO_VALIDATOR.parseValue("ksql_default"), Matchers.is("ksql_default"));
    }

    @Test
    public void shouldResolvePasswordToPassword() {
        Assert.assertThat(ConfigItemTest.RESOLVED_PASSWORD.parseValue("Sensitive"), Matchers.is(new Password("Sensitive")));
    }

    @Test
    public void shouldValidateParsedValue() {
        Assert.assertThat(ConfigItemTest.RESOLVED_WITH_VALIDATOR.parseValue(101), Matchers.is(101));
    }

    @Test(expected = ConfigException.class)
    public void shouldThrowIfCanNotCoerceParsedValue() {
        ConfigItemTest.RESOLVED_WITH_VALIDATOR.parseValue("not a number");
    }

    @Test
    public void shouldPassThroughUnresolvedOnConvertToString() {
        Assert.assertThat(ConfigItemTest.UNRESOLVED.convertToString(12345L), Matchers.is("12345"));
        Assert.assertThat(ConfigItemTest.UNRESOLVED.convertToString(null), Matchers.is("NULL"));
    }

    @Test
    public void shouldConvertResolvedToString() {
        Assert.assertThat(ConfigItemTest.RESOLVED_NO_VALIDATOR.convertToString("101"), Matchers.is("101"));
    }

    @Test
    public void shouldObfuscatePasswordsOnResolveToString() {
        Assert.assertThat(ConfigItemTest.RESOLVED_PASSWORD.convertToString("Sensitive"), Matchers.is("[hidden]"));
    }

    @Test
    public void shouldNotBeDefaultValueIfNotResolved() {
        Assert.assertThat(ConfigItemTest.UNRESOLVED.isDefaultValue("anything"), Matchers.is(false));
        Assert.assertThat(ConfigItemTest.UNRESOLVED.isDefaultValue(null), Matchers.is(false));
    }

    @Test
    public void shouldBeDefaultValue() {
        Assert.assertThat(ConfigItemTest.RESOLVED_NO_VALIDATOR.isDefaultValue("default_"), Matchers.is(true));
    }

    @Test
    public void shouldCoerceBeforeCheckingIfDefaultValue() {
        Assert.assertThat(ConfigItemTest.RESOLVED_NO_VALIDATOR.isDefaultValue("default_"), Matchers.is(true));
    }

    @Test
    public void shouldHandleNoDefaultValue() {
        Assert.assertThat(ConfigItemTest.RESOLVED_NO_DEFAULT.isDefaultValue("anything"), Matchers.is(false));
    }

    @Test
    public void shouldHandlePasswordDefaultValue() {
        Assert.assertThat(ConfigItemTest.RESOLVED_PASSWORD.isDefaultValue("anything"), Matchers.is(false));
    }
}

