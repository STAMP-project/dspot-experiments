package com.ctrip.framework.apollo.internals;


import ConfigSourceType.LOCAL;
import com.ctrip.framework.apollo.enums.ConfigSourceType;
import com.ctrip.framework.apollo.util.ConfigUtil;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import java.io.File;
import java.util.Properties;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Created by Jason on 4/9/16.
 */
public class LocalFileConfigRepositoryTest {
    private File someBaseDir;

    private String someNamespace;

    private ConfigRepository upstreamRepo;

    private Properties someProperties;

    private static String someAppId = "someApp";

    private static String someCluster = "someCluster";

    private String defaultKey;

    private String defaultValue;

    private ConfigSourceType someSourceType;

    @Test
    public void testLoadConfigWithLocalFile() throws Exception {
        String someKey = "someKey";
        String someValue = "someValue\nxxx\nyyy";
        Properties someProperties = new Properties();
        someProperties.setProperty(someKey, someValue);
        createLocalCachePropertyFile(someProperties);
        LocalFileConfigRepository localRepo = new LocalFileConfigRepository(someNamespace);
        localRepo.setLocalCacheDir(someBaseDir, true);
        Properties properties = localRepo.getConfig();
        Assert.assertEquals(someValue, properties.getProperty(someKey));
        Assert.assertEquals(LOCAL, localRepo.getSourceType());
    }

    @Test
    public void testLoadConfigWithLocalFileAndFallbackRepo() throws Exception {
        File file = new File(someBaseDir, assembleLocalCacheFileName());
        String someValue = "someValue";
        Files.write((((defaultKey) + "=") + someValue), file, Charsets.UTF_8);
        LocalFileConfigRepository localRepo = new LocalFileConfigRepository(someNamespace, upstreamRepo);
        localRepo.setLocalCacheDir(someBaseDir, true);
        Properties properties = localRepo.getConfig();
        Assert.assertEquals(defaultValue, properties.getProperty(defaultKey));
        Assert.assertEquals(someSourceType, localRepo.getSourceType());
    }

    @Test
    public void testLoadConfigWithNoLocalFile() throws Exception {
        LocalFileConfigRepository localFileConfigRepository = new LocalFileConfigRepository(someNamespace, upstreamRepo);
        localFileConfigRepository.setLocalCacheDir(someBaseDir, true);
        Properties result = localFileConfigRepository.getConfig();
        Assert.assertThat("LocalFileConfigRepository's properties should be the same as fallback repo's when there is no local cache", result.entrySet(), IsEqual.equalTo(someProperties.entrySet()));
        Assert.assertEquals(someSourceType, localFileConfigRepository.getSourceType());
    }

    @Test
    public void testLoadConfigWithNoLocalFileMultipleTimes() throws Exception {
        LocalFileConfigRepository localRepo = new LocalFileConfigRepository(someNamespace, upstreamRepo);
        localRepo.setLocalCacheDir(someBaseDir, true);
        Properties someProperties = localRepo.getConfig();
        LocalFileConfigRepository anotherLocalRepoWithNoFallback = new LocalFileConfigRepository(someNamespace);
        anotherLocalRepoWithNoFallback.setLocalCacheDir(someBaseDir, true);
        Properties anotherProperties = anotherLocalRepoWithNoFallback.getConfig();
        Assert.assertThat("LocalFileConfigRepository should persist local cache files and return that afterwards", someProperties.entrySet(), IsEqual.equalTo(anotherProperties.entrySet()));
        Assert.assertEquals(someSourceType, localRepo.getSourceType());
    }

    @Test
    public void testOnRepositoryChange() throws Exception {
        RepositoryChangeListener someListener = Mockito.mock(RepositoryChangeListener.class);
        LocalFileConfigRepository localFileConfigRepository = new LocalFileConfigRepository(someNamespace, upstreamRepo);
        Assert.assertEquals(someSourceType, localFileConfigRepository.getSourceType());
        localFileConfigRepository.setLocalCacheDir(someBaseDir, true);
        localFileConfigRepository.addChangeListener(someListener);
        localFileConfigRepository.getConfig();
        Properties anotherProperties = new Properties();
        anotherProperties.put("anotherKey", "anotherValue");
        ConfigSourceType anotherSourceType = ConfigSourceType.NONE;
        Mockito.when(upstreamRepo.getSourceType()).thenReturn(anotherSourceType);
        localFileConfigRepository.onRepositoryChange(someNamespace, anotherProperties);
        final ArgumentCaptor<Properties> captor = ArgumentCaptor.forClass(Properties.class);
        Mockito.verify(someListener, Mockito.times(1)).onRepositoryChange(ArgumentMatchers.eq(someNamespace), captor.capture());
        Assert.assertEquals(anotherProperties, captor.getValue());
        Assert.assertEquals(anotherSourceType, localFileConfigRepository.getSourceType());
    }

    public static class MockConfigUtil extends ConfigUtil {
        @Override
        public String getAppId() {
            return LocalFileConfigRepositoryTest.someAppId;
        }

        @Override
        public String getCluster() {
            return LocalFileConfigRepositoryTest.someCluster;
        }
    }
}

