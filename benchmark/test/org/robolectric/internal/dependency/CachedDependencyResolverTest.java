package org.robolectric.internal.dependency;


import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.internal.dependency.CachedDependencyResolver.Cache;
import org.robolectric.internal.dependency.CachedDependencyResolver.CacheNamingStrategy;
import org.robolectric.internal.dependency.CachedDependencyResolver.CacheValidationStrategy;


@RunWith(JUnit4.class)
public class CachedDependencyResolverTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private static final String CACHE_NAME = "someName";

    private DependencyResolver internalResolver = Mockito.mock(DependencyResolver.class);

    private CacheNamingStrategy cacheNamingStrategy = new CacheNamingStrategy() {
        @Override
        public String getName(String prefix, DependencyJar... dependencies) {
            return CachedDependencyResolverTest.CACHE_NAME;
        }
    };

    private CacheValidationStrategy cacheValidationStrategy = new CacheValidationStrategy() {
        @Override
        public boolean isValid(URL url) {
            return true;
        }

        @Override
        public boolean isValid(URL[] urls) {
            return true;
        }
    };

    private URL url;

    private Cache cache = new CachedDependencyResolverTest.CacheStub();

    private DependencyJar[] dependencies = new DependencyJar[]{ createDependency("group1", "artifact1"), createDependency("group2", "artifact2") };

    private DependencyJar dependency = dependencies[0];

    @Test
    public void getLocalArtifactUrl_shouldWriteLocalArtifactUrlWhenCacheMiss() throws Exception {
        DependencyResolver res = createResolver();
        Mockito.when(internalResolver.getLocalArtifactUrl(dependency)).thenReturn(url);
        URL url = res.getLocalArtifactUrl(dependency);
        Assert.assertEquals(this.url, url);
        assertCacheContents(url);
    }

    @Test
    public void getLocalArtifactUrl_shouldReadLocalArtifactUrlFromCacheIfExists() throws Exception {
        DependencyResolver res = createResolver();
        cache.write(CachedDependencyResolverTest.CACHE_NAME, url);
        URL url = res.getLocalArtifactUrl(dependency);
        Mockito.verify(internalResolver, Mockito.never()).getLocalArtifactUrl(dependency);
        Assert.assertEquals(this.url, url);
    }

    @Test
    public void getLocalArtifactUrl_whenCacheInvalid_shouldFetchDependencyInformation() {
        CacheValidationStrategy failStrategy = Mockito.mock(CacheValidationStrategy.class);
        Mockito.when(failStrategy.isValid(ArgumentMatchers.any(URL.class))).thenReturn(false);
        DependencyResolver res = new CachedDependencyResolver(internalResolver, cache, cacheNamingStrategy, failStrategy);
        cache.write(CachedDependencyResolverTest.CACHE_NAME, this.url);
        res.getLocalArtifactUrl(dependency);
        Mockito.verify(internalResolver).getLocalArtifactUrl(dependency);
    }

    private static class CacheStub implements CachedDependencyResolver.Cache {
        private Map<String, Serializable> map = new HashMap<>();

        @SuppressWarnings("unchecked")
        @Override
        public <T extends Serializable> T load(String id, Class<T> type) {
            Serializable o = map.get(id);
            return (o != null) && ((o.getClass()) == type) ? ((T) (o)) : null;
        }

        @Override
        public <T extends Serializable> boolean write(String id, T object) {
            map.put(id, object);
            return true;
        }
    }
}

