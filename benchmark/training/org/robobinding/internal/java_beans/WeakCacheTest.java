package org.robobinding.internal.java_beans;


import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @since 1.0
 * @version 
 * @author Cheng Wei
 */
public class WeakCacheTest {
    private WeakCache<String, Object> cache;

    @Test
    public void shouldBeCached() {
        String key = "key";
        Object value = new Object();
        cache.put(key, value);
        Assert.assertThat(cache.containsKey(key), Matchers.is(true));
        Assert.assertThat(cache.get(key), Matchers.is(value));
    }

    @Test
    public void whenStrongReferenceIsRemoved_thenValueIsNoLongInCache() {
        String key = "key";
        Object value = new Object();
        cache.put(key, value);
        value = null;
        forceGCOnKey(key);
        Assert.assertThat(cache.containsKey(key), Matchers.is(false));
    }
}

