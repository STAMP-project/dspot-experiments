package io.requery.test;


import AddressType.HOME;
import io.requery.EntityCache;
import io.requery.test.model.Address;
import io.requery.test.model.Person;
import java.util.UUID;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;
import org.junit.Assert;
import org.junit.Test;


public class EntityCacheTest {
    @Test
    public void testSerializeGetPut() {
        CachingProvider provider = Caching.getCachingProvider();
        CacheManager cacheManager = provider.getCacheManager();
        EntityCache cache = useReferenceCache(false).useSerializableCache(true).useCacheManager(cacheManager).build();
        Person p = new Person();
        p.setName("Alice");
        p.setUUID(UUID.randomUUID());
        p.setAddress(new Address());
        p.getAddress().setType(HOME);
        int id = 100;
        cache.put(Person.class, id, p);
        Person d = cache.get(Person.class, id);
        Assert.assertNotNull(d);
        Assert.assertNotSame(p, d);
        Assert.assertEquals(p.getName(), d.getName());
        Assert.assertEquals(p.getUUID(), d.getUUID());
    }
}

