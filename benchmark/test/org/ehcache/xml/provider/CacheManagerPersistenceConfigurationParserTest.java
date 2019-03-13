/**
 * Copyright Terracotta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ehcache.xml.provider;


import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import org.ehcache.config.Configuration;
import org.ehcache.config.builders.ConfigurationBuilder;
import org.ehcache.impl.config.persistence.CacheManagerPersistenceConfiguration;
import org.ehcache.spi.service.ServiceCreationConfiguration;
import org.ehcache.xml.XmlConfiguration;
import org.ehcache.xml.model.ConfigType;
import org.junit.Test;
import org.xml.sax.SAXException;


public class CacheManagerPersistenceConfigurationParserTest {
    @Test
    public void parseServiceCreationConfiguration() throws IOException, ClassNotFoundException, JAXBException, ParserConfigurationException, SAXException {
        Configuration xmlConfig = new XmlConfiguration(getClass().getResource("/configs/disk-persistent-cache.xml"));
        List<CacheManagerPersistenceConfiguration> serviceConfig = xmlConfig.getServiceCreationConfigurations().stream().filter(( i) -> CacheManagerPersistenceConfiguration.class.equals(i.getClass())).map(CacheManagerPersistenceConfiguration.class::cast).collect(Collectors.toList());
        assertThat(serviceConfig).hasSize(1);
        CacheManagerPersistenceConfiguration providerConfiguration = serviceConfig.iterator().next();
        assertThat(providerConfiguration.getRootDirectory()).isEqualTo(new File("some/dir"));
    }

    @Test
    public void unparseServiceCreationConfiguration() {
        Configuration config = ConfigurationBuilder.newConfigurationBuilder().addService(new CacheManagerPersistenceConfiguration(new File("foo"))).build();
        ConfigType configType = new CacheManagerPersistenceConfigurationParser().unparseServiceCreationConfiguration(config, new ConfigType());
        assertThat(configType.getPersistence().getDirectory()).isEqualTo("foo");
    }
}

