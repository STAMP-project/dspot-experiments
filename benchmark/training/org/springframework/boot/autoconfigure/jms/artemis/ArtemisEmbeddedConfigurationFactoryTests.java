/**
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.boot.autoconfigure.jms.artemis;


import JournalType.NIO;
import java.util.List;
import java.util.Map;
import org.apache.activemq.artemis.api.core.SimpleString;
import org.apache.activemq.artemis.core.config.Configuration;
import org.apache.activemq.artemis.core.config.CoreAddressConfiguration;
import org.apache.activemq.artemis.core.settings.impl.AddressSettings;
import org.junit.Test;


/**
 * Tests for {@link ArtemisEmbeddedConfigurationFactory}
 *
 * @author Edd? Mel?ndez
 * @author Stephane Nicoll
 * @author Phillip Webb
 */
public class ArtemisEmbeddedConfigurationFactoryTests {
    @Test
    public void defaultDataDir() {
        ArtemisProperties properties = new ArtemisProperties();
        properties.getEmbedded().setPersistent(true);
        Configuration configuration = createConfiguration();
        assertThat(configuration.getJournalDirectory()).startsWith(System.getProperty("java.io.tmpdir")).endsWith("/journal");
    }

    @Test
    public void persistenceSetup() {
        ArtemisProperties properties = new ArtemisProperties();
        properties.getEmbedded().setPersistent(true);
        Configuration configuration = createConfiguration();
        assertThat(configuration.isPersistenceEnabled()).isTrue();
        assertThat(configuration.getJournalType()).isEqualTo(NIO);
    }

    @Test
    public void generatedClusterPassword() {
        ArtemisProperties properties = new ArtemisProperties();
        Configuration configuration = createConfiguration();
        assertThat(configuration.getClusterPassword().length()).isEqualTo(36);
    }

    @Test
    public void specificClusterPassword() {
        ArtemisProperties properties = new ArtemisProperties();
        properties.getEmbedded().setClusterPassword("password");
        Configuration configuration = createConfiguration();
        assertThat(configuration.getClusterPassword()).isEqualTo("password");
    }

    @Test
    public void hasDlqExpiryQueueAddressSettingsConfigured() {
        ArtemisProperties properties = new ArtemisProperties();
        Configuration configuration = createConfiguration();
        Map<String, AddressSettings> addressesSettings = configuration.getAddressesSettings();
        assertThat(((Object) (addressesSettings.get("#").getDeadLetterAddress()))).isEqualTo(SimpleString.toSimpleString("DLQ"));
        assertThat(((Object) (addressesSettings.get("#").getExpiryAddress()))).isEqualTo(SimpleString.toSimpleString("ExpiryQueue"));
    }

    @Test
    public void hasDlqExpiryQueueConfigured() {
        ArtemisProperties properties = new ArtemisProperties();
        Configuration configuration = createConfiguration();
        List<CoreAddressConfiguration> addressConfigurations = configuration.getAddressConfigurations();
        assertThat(addressConfigurations).hasSize(2);
    }
}

