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
package org.ehcache.xml.service;


import com.pany.ehcache.copier.AnotherPersonCopier;
import com.pany.ehcache.copier.Description;
import com.pany.ehcache.copier.DescriptionCopier;
import com.pany.ehcache.copier.Person;
import com.pany.ehcache.copier.PersonCopier;
import java.util.Collection;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.core.spi.service.ServiceUtils;
import org.ehcache.impl.config.copy.DefaultCopierConfiguration;
import org.ehcache.impl.copy.SerializingCopier;
import org.ehcache.xml.XmlConfiguration;
import org.ehcache.xml.exceptions.XmlConfigurationException;
import org.ehcache.xml.model.CacheEntryType;
import org.ehcache.xml.model.CacheType;
import org.junit.Test;

import static org.ehcache.impl.config.copy.DefaultCopierConfiguration.Type.KEY;
import static org.ehcache.impl.config.copy.DefaultCopierConfiguration.Type.VALUE;


public class DefaultCopierConfigurationParserTest {
    @Test
    public void parseServiceConfiguration() throws Exception {
        CacheConfiguration<?, ?> cacheConfiguration = new XmlConfiguration(getClass().getResource("/configs/cache-copiers.xml")).getCacheConfigurations().get("baz");
        @SuppressWarnings("rawtypes")
        Collection<DefaultCopierConfiguration> copierConfigs = ServiceUtils.findAmongst(DefaultCopierConfiguration.class, cacheConfiguration.getServiceConfigurations());
        assertThat(copierConfigs).hasSize(2);
        for (DefaultCopierConfiguration<?> copierConfig : copierConfigs) {
            if ((copierConfig.getType()) == (KEY)) {
                assertThat(copierConfig.getClazz()).isEqualTo(SerializingCopier.class);
            } else {
                assertThat(copierConfig.getClazz()).isEqualTo(AnotherPersonCopier.class);
            }
        }
    }

    @Test
    public void unparseServiceConfiguration() {
        @SuppressWarnings({ "unchecked", "rawtypes" })
        CacheConfiguration<?, ?> cacheConfig = CacheConfigurationBuilder.newCacheConfigurationBuilder(Description.class, Person.class, ResourcePoolsBuilder.heap(10)).add(new DefaultCopierConfiguration(DescriptionCopier.class, KEY)).add(new DefaultCopierConfiguration(PersonCopier.class, VALUE)).build();
        CacheType cacheType = new CacheType();
        CacheEntryType keyType = new CacheEntryType();
        keyType.setValue("foo");
        cacheType.setKeyType(keyType);
        CacheEntryType valueType = new CacheEntryType();
        valueType.setValue("bar");
        cacheType.setValueType(valueType);
        cacheType = new DefaultCopierConfigurationParser().unparseServiceConfiguration(cacheConfig, cacheType);
        assertThat(cacheType.getKeyType().getCopier()).isEqualTo(DescriptionCopier.class.getName());
        assertThat(cacheType.getValueType().getCopier()).isEqualTo(PersonCopier.class.getName());
    }

    @Test
    public void unparseServiceConfigurationWithInstance() {
        DescriptionCopier descriptionCopier = new DescriptionCopier();
        PersonCopier personCopier = new PersonCopier();
        DefaultCopierConfiguration<Description> config1 = new DefaultCopierConfiguration<>(descriptionCopier, KEY);
        DefaultCopierConfiguration<Person> config2 = new DefaultCopierConfiguration<>(personCopier, VALUE);
        CacheConfiguration<?, ?> cacheConfig = CacheConfigurationBuilder.newCacheConfigurationBuilder(Description.class, Person.class, ResourcePoolsBuilder.heap(10)).add(config1).add(config2).build();
        CacheType cacheType = new CacheType();
        CacheEntryType keyType = new CacheEntryType();
        keyType.setValue("foo");
        cacheType.setKeyType(keyType);
        CacheEntryType valueType = new CacheEntryType();
        valueType.setValue("bar");
        cacheType.setValueType(valueType);
        assertThatExceptionOfType(XmlConfigurationException.class).isThrownBy(() -> new DefaultCopierConfigurationParser().unparseServiceConfiguration(cacheConfig, cacheType)).withMessage("%s", ("XML translation for instance based initialization for " + "DefaultCopierConfiguration is not supported"));
    }
}

