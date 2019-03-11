package org.robolectric.internal.dependency;


import java.net.URL;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.robolectric.internal.dependency.CachedDependencyResolver.Cache;


@RunWith(JUnit4.class)
public class CachedDependencyResolverFileCacheTest {
    private final String ID = "id";

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void shouldLoadNullWhenCacheIsEmpty() throws Exception {
        Cache cache = new CachedDependencyResolver.FileCache(temporaryFolder.getRoot(), 1000);
        String value = cache.load(ID, String.class);
        Assert.assertNull(value);
    }

    @Test
    public void shouldLoadObjectWhenCacheExists() throws Exception {
        Cache cache = new CachedDependencyResolver.FileCache(temporaryFolder.getRoot(), 1000);
        String expectedValue = "some string";
        writeToCacheFile(expectedValue);
        String value = cache.load(ID, String.class);
        Assert.assertEquals(expectedValue, value);
    }

    @Test
    public void shouldLoadNullWhenObjectInCacheHaveBadType() throws Exception {
        Cache cache = new CachedDependencyResolver.FileCache(temporaryFolder.getRoot(), 1000);
        writeToCacheFile(123L);
        Assert.assertNull(cache.load(ID, String.class));
    }

    @Test
    public void shouldWriteObjectToFile() throws Exception {
        Cache cache = new CachedDependencyResolver.FileCache(temporaryFolder.getRoot(), 1000);
        Long expectedValue = 421L;
        Assert.assertTrue(cache.write(ID, expectedValue));
        Object actual = readFromCacheFile();
        Assert.assertEquals(expectedValue, actual);
    }

    @Test
    public void shouldWriteUrlArrayToFile() throws Exception {
        Cache cache = new CachedDependencyResolver.FileCache(temporaryFolder.getRoot(), 1000);
        URL[] urls = new URL[]{ new URL("http://localhost") };
        Assert.assertTrue(cache.write(ID, urls));
        Object actual = readFromCacheFile();
        Assert.assertArrayEquals(urls, ((URL[]) (actual)));
    }
}

