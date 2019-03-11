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
package org.ehcache.jsr107;


import com.pany.domain.Client;
import com.pany.domain.Customer;
import com.pany.domain.Product;
import com.pany.ehcache.Test107CacheEntryListener;
import com.pany.ehcache.TestCacheEventListener;
import com.pany.ehcache.integration.ProductCacheLoaderWriter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.configuration.CompleteConfiguration;
import javax.cache.configuration.MutableCacheEntryListenerConfiguration;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.EternalExpiryPolicy;
import javax.cache.integration.CacheLoader;
import javax.cache.integration.CacheLoaderException;
import javax.cache.integration.CompletionListenerFuture;
import javax.cache.javax.cache.Cache;
import javax.cache.spi.CachingProvider;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;


public class Eh107XmlIntegrationTest {
    private CacheManager cacheManager;

    private CachingProvider cachingProvider;

    @Test
    public void test107CacheCanReturnCompleteConfigurationWhenNonePassedIn() {
        CacheManager cacheManager = cachingProvider.getCacheManager();
        Cache<Long, String> cache = cacheManager.createCache("cacheWithoutCompleteConfig", new javax.cache.configuration.Configuration<Long, String>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Class<Long> getKeyType() {
                return Long.class;
            }

            @Override
            public Class<String> getValueType() {
                return String.class;
            }

            @Override
            public boolean isStoreByValue() {
                return true;
            }
        });
        @SuppressWarnings("unchecked")
        CompleteConfiguration<Long, String> configuration = cache.getConfiguration(CompleteConfiguration.class);
        MatcherAssert.assertThat(configuration, CoreMatchers.notNullValue());
        MatcherAssert.assertThat(configuration.isStoreByValue(), CoreMatchers.is(true));
        // Respects defaults
        MatcherAssert.assertThat(configuration.getExpiryPolicyFactory(), CoreMatchers.equalTo(EternalExpiryPolicy.factoryOf()));
    }

    @Test
    public void testTemplateAddsListeners() throws Exception {
        CacheManager cacheManager = cachingProvider.getCacheManager(getClass().getResource("/ehcache-107-listeners.xml").toURI(), getClass().getClassLoader());
        MutableConfiguration<String, String> configuration = new MutableConfiguration();
        configuration.setTypes(String.class, String.class);
        MutableCacheEntryListenerConfiguration<String, String> listenerConfiguration = new MutableCacheEntryListenerConfiguration(Test107CacheEntryListener::new, null, false, true);
        configuration.addCacheEntryListenerConfiguration(listenerConfiguration);
        Cache<String, String> cache = cacheManager.createCache("foos", configuration);
        cache.put("Hello", "Bonjour");
        MatcherAssert.assertThat(Test107CacheEntryListener.seen.size(), Matchers.is(1));
        MatcherAssert.assertThat(TestCacheEventListener.seen.size(), Matchers.is(1));
    }

    @Test
    public void test107LoaderOverriddenByEhcacheTemplateLoaderWriter() throws Exception {
        final AtomicBoolean loaderFactoryInvoked = new AtomicBoolean(false);
        final Eh107XmlIntegrationTest.DumbCacheLoader product2CacheLoader = new Eh107XmlIntegrationTest.DumbCacheLoader();
        MutableConfiguration<Long, Product> product2Configuration = new MutableConfiguration();
        product2Configuration.setTypes(Long.class, Product.class).setReadThrough(true);
        product2Configuration.setCacheLoaderFactory(() -> {
            loaderFactoryInvoked.set(true);
            return product2CacheLoader;
        });
        Cache<Long, Product> productCache2 = cacheManager.createCache("productCache2", product2Configuration);
        MatcherAssert.assertThat(loaderFactoryInvoked.get(), CoreMatchers.is(false));
        Product product = productCache2.get(124L);
        MatcherAssert.assertThat(product.getId(), CoreMatchers.is(124L));
        MatcherAssert.assertThat(ProductCacheLoaderWriter.seen, Matchers.hasItem(124L));
        MatcherAssert.assertThat(product2CacheLoader.seen, CoreMatchers.is(Matchers.empty()));
        CompletionListenerFuture future = new CompletionListenerFuture();
        productCache2.loadAll(Collections.singleton(42L), false, future);
        future.get();
        MatcherAssert.assertThat(ProductCacheLoaderWriter.seen, Matchers.hasItem(42L));
        MatcherAssert.assertThat(product2CacheLoader.seen, CoreMatchers.is(Matchers.empty()));
    }

    @Test
    public void test107ExpiryOverriddenByEhcacheTemplateExpiry() {
        final AtomicBoolean expiryFactoryInvoked = new AtomicBoolean(false);
        MutableConfiguration<Long, Product> configuration = new MutableConfiguration();
        configuration.setTypes(Long.class, Product.class);
        configuration.setExpiryPolicyFactory(() -> {
            expiryFactoryInvoked.set(true);
            return new CreatedExpiryPolicy(Duration.FIVE_MINUTES);
        });
        cacheManager.createCache("productCache3", configuration);
        MatcherAssert.assertThat(expiryFactoryInvoked.get(), CoreMatchers.is(false));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testXmlExampleIn107() throws Exception {
        javax.cache.Cache<Long, Product> productCache = cacheManager.getCache("productCache", Long.class, Product.class);
        MatcherAssert.assertThat(productCache, CoreMatchers.is(CoreMatchers.notNullValue()));
        javax.cache.configuration.Configuration<Long, Product> configuration = productCache.getConfiguration(javax.cache.configuration.Configuration.class);
        MatcherAssert.assertThat(configuration.getKeyType(), CoreMatchers.is(CoreMatchers.equalTo(Long.class)));
        MatcherAssert.assertThat(configuration.getValueType(), CoreMatchers.is(CoreMatchers.equalTo(Product.class)));
        Eh107ReverseConfiguration<Long, Product> eh107ReverseConfiguration = productCache.getConfiguration(Eh107ReverseConfiguration.class);
        MatcherAssert.assertThat(eh107ReverseConfiguration.isReadThrough(), CoreMatchers.is(true));
        MatcherAssert.assertThat(eh107ReverseConfiguration.isWriteThrough(), CoreMatchers.is(true));
        MatcherAssert.assertThat(eh107ReverseConfiguration.isStoreByValue(), CoreMatchers.is(true));
        Product product = new Product(1L);
        productCache.put(1L, product);
        MatcherAssert.assertThat(productCache.get(1L).getId(), CoreMatchers.equalTo(product.getId()));
        product.setMutable("foo");
        MatcherAssert.assertThat(productCache.get(1L).getMutable(), CoreMatchers.nullValue());
        MatcherAssert.assertThat(productCache.get(1L), CoreMatchers.not(CoreMatchers.sameInstance(product)));
        javax.cache.Cache<Long, Customer> customerCache = cacheManager.getCache("customerCache", Long.class, Customer.class);
        MatcherAssert.assertThat(customerCache, CoreMatchers.is(CoreMatchers.notNullValue()));
        javax.cache.configuration.Configuration<Long, Customer> customerConfiguration = customerCache.getConfiguration(javax.cache.configuration.Configuration.class);
        MatcherAssert.assertThat(customerConfiguration.getKeyType(), CoreMatchers.is(CoreMatchers.equalTo(Long.class)));
        MatcherAssert.assertThat(customerConfiguration.getValueType(), CoreMatchers.is(CoreMatchers.equalTo(Customer.class)));
        Customer customer = new Customer(1L);
        customerCache.put(1L, customer);
        MatcherAssert.assertThat(customerCache.get(1L).getId(), CoreMatchers.equalTo(customer.getId()));
    }

    @Test
    public void testCopierAtServiceLevel() throws Exception {
        CacheManager cacheManager = cachingProvider.getCacheManager(getClass().getResource("/ehcache-107-default-copiers.xml").toURI(), getClass().getClassLoader());
        MutableConfiguration<Long, Client> config = new MutableConfiguration();
        config.setTypes(Long.class, Client.class);
        Cache<Long, Client> bar = cacheManager.createCache("bar", config);
        Client client = new Client("tc", 1000000L);
        long key = 42L;
        bar.put(key, client);
        MatcherAssert.assertThat(bar.get(key), CoreMatchers.not(CoreMatchers.sameInstance(client)));
    }

    static class DumbCacheLoader implements CacheLoader<Long, Product> {
        Set<Long> seen = new HashSet<>();

        @Override
        public Product load(Long aLong) throws CacheLoaderException {
            seen.add(aLong);
            return new Product(aLong);
        }

        @Override
        public Map<Long, Product> loadAll(Iterable<? extends Long> iterable) throws CacheLoaderException {
            for (Long aLong : iterable) {
                seen.add(aLong);
            }
            return Collections.emptyMap();
        }
    }
}

