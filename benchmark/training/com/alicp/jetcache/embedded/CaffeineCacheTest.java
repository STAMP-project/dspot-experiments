package com.alicp.jetcache.embedded;


import org.junit.Test;


/**
 * Created on 2016/10/25.
 *
 * @author <a href="mailto:areyouok@gmail.com">huangli</a>
 */
public class CaffeineCacheTest extends AbstractEmbeddedCacheTest {
    @Test
    public void test() throws Exception {
        super.test(500, false);
        // cache = EmbeddedCacheBuilder.createEmbeddedCacheBuilder()
        // .buildFunc(getBuildFunc()).expireAfterWrite(100, TimeUnit.MILLISECONDS).limit(2).build();
        // cache.put("K1", "V1");
        // cache.put("K2", "V2");
        // cache.put("K3", "V3");
        // System.out.println(cache.get("K1"));
        // System.out.println(cache.get("K2"));
        // System.out.println(cache.get("K3"));
        // System.out.println(cache.get("K1"));
        // System.out.println(cache.get("K2"));
        // System.out.println(cache.get("K3"));
    }
}

