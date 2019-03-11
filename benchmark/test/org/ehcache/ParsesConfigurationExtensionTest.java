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
package org.ehcache;


import com.pany.domain.Customer;
import com.pany.domain.Product;
import com.pany.ehcache.MyEvictionAdvisor;
import com.pany.ehcache.integration.ProductCacheLoaderWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import org.ehcache.config.CacheRuntimeConfiguration;
import org.ehcache.config.ResourceType;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.core.EhcacheManager;
import org.ehcache.core.spi.service.ServiceUtils;
import org.ehcache.expiry.ExpiryPolicy;
import org.ehcache.jsr107.config.Jsr107Configuration;
import org.ehcache.jsr107.internal.DefaultJsr107Service;
import org.ehcache.spi.service.Service;
import org.ehcache.xml.XmlConfiguration;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.xml.sax.SAXException;

import static org.ehcache.config.ResourceType.Core.HEAP;


/**
 *
 *
 * @author Alex Snaps
 */
public class ParsesConfigurationExtensionTest {
    @Test
    public void testConfigParse() throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException, SAXException {
        final XmlConfiguration configuration = new XmlConfiguration(this.getClass().getResource("/ehcache-107.xml"));
        final DefaultJsr107Service jsr107Service = new DefaultJsr107Service(ServiceUtils.findSingletonAmongst(Jsr107Configuration.class, configuration.getServiceCreationConfigurations()));
        final CacheManager cacheManager = new EhcacheManager(configuration, Collections.<Service>singletonList(jsr107Service));
        cacheManager.init();
        MatcherAssert.assertThat(jsr107Service.getTemplateNameForCache("foos"), CoreMatchers.equalTo("stringCache"));
        MatcherAssert.assertThat(jsr107Service.getTemplateNameForCache("bars"), CoreMatchers.equalTo("tinyCache"));
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void testXmlExample() throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException, SAXException {
        XmlConfiguration config = new XmlConfiguration(ParsesConfigurationExtensionTest.class.getResource("/ehcache-example.xml"));
        final DefaultJsr107Service jsr107Service = new DefaultJsr107Service(ServiceUtils.findSingletonAmongst(Jsr107Configuration.class, config.getServiceCreationConfigurations()));
        final CacheManager cacheManager = new EhcacheManager(config, Collections.<Service>singletonList(jsr107Service));
        cacheManager.init();
        // test productCache
        {
            final Cache<Long, Product> productCache = cacheManager.getCache("productCache", Long.class, Product.class);
            MatcherAssert.assertThat(productCache, CoreMatchers.notNullValue());
            // Test the config
            {
                final CacheRuntimeConfiguration<Long, Product> runtimeConfiguration = productCache.getRuntimeConfiguration();
                MatcherAssert.assertThat(runtimeConfiguration.getResourcePools().getPoolForResource(HEAP).getSize(), CoreMatchers.equalTo(200L));
                final ExpiryPolicy<? super Long, ? super Product> expiry = runtimeConfiguration.getExpiryPolicy();
                MatcherAssert.assertThat(expiry.getClass().getName(), CoreMatchers.equalTo("org.ehcache.config.builders.ExpiryPolicyBuilder$TimeToIdleExpiryPolicy"));
                MatcherAssert.assertThat(expiry.getExpiryForAccess(42L, null), CoreMatchers.equalTo(Duration.ofMinutes(2)));
                MatcherAssert.assertThat(runtimeConfiguration.getEvictionAdvisor(), CoreMatchers.instanceOf(MyEvictionAdvisor.class));
            }
            // test copies
            {
                final Product value = new Product(1L);
                productCache.put(value.getId(), value);
                value.setMutable("fool!");
                MatcherAssert.assertThat(productCache.get(value.getId()).getMutable(), CoreMatchers.nullValue());
                MatcherAssert.assertThat(productCache.get(value.getId()), CoreMatchers.not(CoreMatchers.sameInstance(productCache.get(value.getId()))));
            }
            // test loader
            {
                final long key = 123L;
                final Product product = productCache.get(key);
                MatcherAssert.assertThat(product, CoreMatchers.notNullValue());
                MatcherAssert.assertThat(product.getId(), CoreMatchers.equalTo(key));
            }
            // test writer
            {
                final Product value = new Product(42L);
                productCache.put(42L, value);
                final List<Product> products = ProductCacheLoaderWriter.written.get(value.getId());
                MatcherAssert.assertThat(products, CoreMatchers.notNullValue());
                MatcherAssert.assertThat(products.get(0), CoreMatchers.sameInstance(value));
            }
        }
        // Test template
        {
            final CacheConfigurationBuilder<Object, Object> myDefaultTemplate = config.newCacheConfigurationBuilderFromTemplate("myDefaultTemplate", Object.class, Object.class, ResourcePoolsBuilder.heap(10));
            MatcherAssert.assertThat(myDefaultTemplate, CoreMatchers.notNullValue());
        }
        // Test customerCache templated cache
        {
            final Cache<Long, Customer> customerCache = cacheManager.getCache("customerCache", Long.class, Customer.class);
            final CacheRuntimeConfiguration<Long, Customer> runtimeConfiguration = customerCache.getRuntimeConfiguration();
            MatcherAssert.assertThat(runtimeConfiguration.getResourcePools().getPoolForResource(HEAP).getSize(), CoreMatchers.equalTo(200L));
        }
    }
}

