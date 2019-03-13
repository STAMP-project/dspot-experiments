package com.danikula.videocache;


import com.danikula.android.garden.io.IoUtils;
import com.danikula.videocache.file.FileCache;
import com.danikula.videocache.support.ProxyCacheTestUtils;
import java.io.File;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.junit.Test;


/**
 *
 *
 * @author Alexey Danilov (danikula@gmail.com).
 */
public class ProxyCacheTest extends BaseTest {
    @Test
    public void testNoCache() throws Exception {
        byte[] sourceData = ProxyCacheTestUtils.generate(345);
        ProxyCache proxyCache = new ProxyCache(new ByteArraySource(sourceData), new ByteArrayCache());
        byte[] buffer = new byte[sourceData.length];
        int read = proxyCache.read(buffer, 0, sourceData.length);
        assertThat(read).isEqualTo(sourceData.length);
        assertThat(buffer).isEqualTo(sourceData);
    }

    @Test
    public void testAllFromCacheNoSource() throws Exception {
        byte[] cacheData = ProxyCacheTestUtils.generate(34564);
        ProxyCache proxyCache = new ProxyCache(new ByteArraySource(new byte[0]), new ByteArrayCache(cacheData));
        byte[] buffer = new byte[(cacheData.length) + 100];
        int read = proxyCache.read(buffer, 0, cacheData.length);
        byte[] readData = Arrays.copyOfRange(buffer, 0, cacheData.length);
        assertThat(read).isEqualTo(cacheData.length);
        assertThat(readData).isEqualTo(cacheData);
    }

    @Test
    public void testMergeSourceAndCache() throws Exception {
        byte[] sourceData = ProxyCacheTestUtils.generate(2345);
        byte[] cacheData = ProxyCacheTestUtils.generate(1048);
        ProxyCache proxyCache = new ProxyCache(new ByteArraySource(sourceData), new ByteArrayCache(cacheData));
        byte[] buffer = new byte[sourceData.length];
        int read = proxyCache.read(buffer, 0, sourceData.length);
        byte[] expected = new byte[sourceData.length];
        System.arraycopy(cacheData, 0, expected, 0, cacheData.length);
        System.arraycopy(sourceData, cacheData.length, expected, cacheData.length, ((sourceData.length) - (cacheData.length)));
        assertThat(read).isEqualTo(sourceData.length);
        assertThat(buffer).isEqualTo(expected);
    }

    @Test
    public void testReuseCache() throws Exception {
        int size = 20000;
        byte[] sourceData = ProxyCacheTestUtils.generate(size);
        Cache cache = new ByteArrayCache();
        ProxyCache proxyCache = new ProxyCache(new ByteArraySource(sourceData), cache);
        byte[] fetchedData = new byte[size];
        proxyCache.read(fetchedData, 0, size);
        assertThat(fetchedData).isEqualTo(sourceData);
        byte[] sourceCopy = Arrays.copyOf(sourceData, size);
        byte[] newSource = ProxyCacheTestUtils.generate(size);
        proxyCache = new ProxyCache(new ByteArraySource(newSource), cache);
        Arrays.fill(fetchedData, ((byte) (0)));
        proxyCache.read(fetchedData, 0, size);
        assertThat(fetchedData).isEqualTo(sourceCopy);
    }

    @Test
    public void testProxyWithPhlegmaticSource() throws Exception {
        int dataSize = 100000;
        byte[] sourceData = ProxyCacheTestUtils.generate(dataSize);
        Source source = ProxyCacheTestUtils.newPhlegmaticSource(sourceData, 200);
        ProxyCache proxyCache = new ProxyCache(source, new FileCache(ProxyCacheTestUtils.newCacheFile()));
        byte[] readData = new byte[dataSize];
        proxyCache.read(readData, 0, dataSize);
        assertThat(readData).isEqualTo(sourceData);
    }

    @Test
    public void testReadEnd() throws Exception {
        int capacity = 5323;
        Source source = ProxyCacheTestUtils.newPhlegmaticSource(ProxyCacheTestUtils.generate(capacity), 200);
        Cache cache = new FileCache(ProxyCacheTestUtils.newCacheFile());
        ProxyCache proxyCache = new ProxyCache(source, cache);
        proxyCache.read(new byte[1], (capacity - 1), 1);
        TimeUnit.MILLISECONDS.sleep(200);// wait for completion

        assertThat(cache.isCompleted()).isTrue();
    }

    @Test
    public void testReadRandomParts() throws Exception {
        int dataSize = 123456;
        byte[] sourceData = ProxyCacheTestUtils.generate(dataSize);
        Source source = ProxyCacheTestUtils.newPhlegmaticSource(sourceData, 300);
        File file = ProxyCacheTestUtils.newCacheFile();
        Cache cache = new FileCache(file);
        ProxyCache proxyCache = new ProxyCache(source, cache);
        Random random = new Random(System.currentTimeMillis());
        for (int i = 0; i < 100; i++) {
            int offset = random.nextInt(dataSize);
            int bufferSize = random.nextInt((dataSize / 4));
            bufferSize = Math.min(bufferSize, (dataSize - offset));
            byte[] buffer = new byte[bufferSize];
            proxyCache.read(buffer, offset, bufferSize);
            byte[] dataPortion = Arrays.copyOfRange(sourceData, offset, (offset + bufferSize));
            assertThat(buffer).isEqualTo(dataPortion);
        }
        proxyCache.read(new byte[1], (dataSize - 1), 1);
        TimeUnit.MILLISECONDS.sleep(200);// wait for completion

        assertThat(cache.isCompleted()).isTrue();
        assertThat(sourceData).isEqualTo(ProxyCacheTestUtils.getFileContent(file));
    }

    @Test
    public void testLoadingHttpData() throws Exception {
        Source source = new HttpUrlSource(ProxyCacheTestUtils.HTTP_DATA_URL);
        ProxyCache proxyCache = new ProxyCache(source, new FileCache(ProxyCacheTestUtils.newCacheFile()));
        byte[] remoteData = new byte[ProxyCacheTestUtils.HTTP_DATA_SIZE];
        proxyCache.read(remoteData, 0, ProxyCacheTestUtils.HTTP_DATA_SIZE);
        proxyCache.shutdown();
        assertThat(remoteData).isEqualTo(ProxyCacheTestUtils.loadAssetFile(ProxyCacheTestUtils.ASSETS_DATA_NAME));
    }

    @Test
    public void testReadMoreThanAvailable() throws Exception {
        byte[] data = ProxyCacheTestUtils.generate(20000);
        Cache fileCache = new FileCache(ProxyCacheTestUtils.newCacheFile());
        ProxyCache proxyCache = new ProxyCache(new ByteArraySource(data), fileCache);
        byte[] buffer = new byte[15000];
        proxyCache.read(buffer, 18000, buffer.length);
        byte[] expectedData = new byte[15000];
        System.arraycopy(data, 18000, expectedData, 0, 2000);
        assertThat(buffer).isEqualTo(expectedData);
    }

    @Test
    public void testCompletion() throws Exception {
        Cache cache = new FileCache(ProxyCacheTestUtils.newCacheFile());
        ProxyCache proxyCache = new ProxyCache(new ByteArraySource(ProxyCacheTestUtils.generate(20000)), cache);
        proxyCache.read(new byte[5], 19999, 5);
        assertThat(cache.isCompleted()).isTrue();
    }

    @Test
    public void testNoTouchSource() throws Exception {
        int dataSize = 2000;
        byte[] data = ProxyCacheTestUtils.generate(dataSize);
        File file = ProxyCacheTestUtils.newCacheFile();
        IoUtils.saveToFile(data, file);
        Source source = ProxyCacheTestUtils.newAngryHttpUrlSource();
        ProxyCache proxyCache = new ProxyCache(source, new FileCache(file));
        byte[] readData = new byte[dataSize];
        proxyCache.read(readData, 0, dataSize);
        assertThat(readData).isEqualTo(data);
    }
}

