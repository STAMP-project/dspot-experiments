package com.danikula.videocache;


import RuntimeEnvironment.application;
import com.danikula.android.garden.io.IoUtils;
import com.danikula.videocache.file.FileCache;
import com.danikula.videocache.sourcestorage.SourceInfoStorage;
import com.danikula.videocache.sourcestorage.SourceInfoStorageFactory;
import com.danikula.videocache.support.ProxyCacheTestUtils;
import com.danikula.videocache.support.Response;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.Socket;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Test {@link HttpProxyCache}.
 *
 * @author Alexey Danilov (danikula@gmail.com).
 */
public class HttpProxyCacheTest extends BaseTest {
    @Test
    public void testProcessRequestNoCache() throws Exception {
        Response response = processRequest(ProxyCacheTestUtils.HTTP_DATA_URL, (("GET /" + (ProxyCacheTestUtils.HTTP_DATA_URL)) + " HTTP/1.1"));
        assertThat(response.data).isEqualTo(ProxyCacheTestUtils.loadTestData());
        assertThat(response.code).isEqualTo(200);
        assertThat(response.contentLength).isEqualTo(ProxyCacheTestUtils.HTTP_DATA_SIZE);
        assertThat(response.contentType).isEqualTo("image/jpeg");
    }

    @Test
    public void testProcessPartialRequestWithoutCache() throws Exception {
        FileCache fileCache = new FileCache(ProxyCacheTestUtils.newCacheFile());
        FileCache spyFileCache = Mockito.spy(fileCache);
        Mockito.doThrow(new RuntimeException()).when(spyFileCache).read(ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt());
        String httpRequest = ("GET /" + (ProxyCacheTestUtils.HTTP_DATA_URL)) + " HTTP/1.1\nRange: bytes=2000-";
        Response response = processRequest(ProxyCacheTestUtils.HTTP_DATA_URL, httpRequest, spyFileCache);
        byte[] fullData = ProxyCacheTestUtils.loadTestData();
        byte[] partialData = new byte[(fullData.length) - 2000];
        System.arraycopy(fullData, 2000, partialData, 0, partialData.length);
        assertThat(response.data).isEqualTo(partialData);
        assertThat(response.code).isEqualTo(206);
    }

    // https://github.com/danikula/AndroidVideoCache/issues/43
    @Test
    public void testPreventClosingOriginalSourceForNewPartialRequestWithoutCache() throws Exception {
        HttpUrlSource source = new HttpUrlSource(ProxyCacheTestUtils.HTTP_DATA_BIG_URL);
        FileCache fileCache = new FileCache(ProxyCacheTestUtils.newCacheFile());
        HttpProxyCache proxyCache = new HttpProxyCache(source, fileCache);
        ExecutorService executor = Executors.newFixedThreadPool(5);
        Future<Response> firstRequestFeature = processAsync(executor, proxyCache, (("GET /" + (ProxyCacheTestUtils.HTTP_DATA_URL)) + " HTTP/1.1"));
        Thread.sleep(100);// wait for first request started to process

        int offset = 30000;
        String partialRequest = ((("GET /" + (ProxyCacheTestUtils.HTTP_DATA_URL)) + " HTTP/1.1\nRange: bytes=") + offset) + "-";
        Future<Response> secondRequestFeature = processAsync(executor, proxyCache, partialRequest);
        Response secondResponse = secondRequestFeature.get();
        Response firstResponse = firstRequestFeature.get();
        byte[] responseData = ProxyCacheTestUtils.loadAssetFile(ProxyCacheTestUtils.ASSETS_DATA_BIG_NAME);
        assertThat(firstResponse.data).isEqualTo(responseData);
        byte[] partialData = new byte[(responseData.length) - offset];
        System.arraycopy(responseData, offset, partialData, 0, partialData.length);
        assertThat(secondResponse.data).isEqualTo(partialData);
    }

    @Test
    public void testProcessManyThreads() throws Exception {
        final String url = "https://raw.githubusercontent.com/danikula/AndroidVideoCache/master/files/space.jpg";
        HttpUrlSource source = new HttpUrlSource(url);
        FileCache fileCache = new FileCache(ProxyCacheTestUtils.newCacheFile());
        final HttpProxyCache proxyCache = new HttpProxyCache(source, fileCache);
        final byte[] loadedData = ProxyCacheTestUtils.loadAssetFile("space.jpg");
        final Random random = new Random(System.currentTimeMillis());
        int concurrentRequests = 10;
        ExecutorService executor = Executors.newFixedThreadPool(concurrentRequests);
        Future[] results = new Future[concurrentRequests];
        int[] offsets = new int[concurrentRequests];
        final CountDownLatch finishLatch = new CountDownLatch(concurrentRequests);
        final CountDownLatch startLatch = new CountDownLatch(1);
        for (int i = 0; i < concurrentRequests; i++) {
            final int offset = random.nextInt(loadedData.length);
            offsets[i] = offset;
            results[i] = executor.submit(new Callable<Response>() {
                @Override
                public Response call() throws Exception {
                    try {
                        startLatch.await();
                        String partialRequest = ((("GET /" + url) + " HTTP/1.1\nRange: bytes=") + offset) + "-";
                        return processRequest(proxyCache, partialRequest);
                    } finally {
                        finishLatch.countDown();
                    }
                }
            });
        }
        startLatch.countDown();
        finishLatch.await();
        for (int i = 0; i < (results.length); i++) {
            Response response = ((Response) (results[i].get()));
            int offset = offsets[i];
            byte[] partialData = new byte[(loadedData.length) - offset];
            System.arraycopy(loadedData, offset, partialData, 0, partialData.length);
            assertThat(response.data).isEqualTo(partialData);
        }
    }

    @Test
    public void testLoadEmptyFile() throws Exception {
        String zeroSizeUrl = "https://raw.githubusercontent.com/danikula/AndroidVideoCache/master/files/empty.txt";
        HttpUrlSource source = new HttpUrlSource(zeroSizeUrl);
        HttpProxyCache proxyCache = new HttpProxyCache(source, new FileCache(ProxyCacheTestUtils.newCacheFile()));
        GetRequest request = new GetRequest((("GET /" + (ProxyCacheTestUtils.HTTP_DATA_URL)) + " HTTP/1.1"));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Socket socket = Mockito.mock(Socket.class);
        Mockito.when(socket.getOutputStream()).thenReturn(out);
        CacheListener listener = Mockito.mock(CacheListener.class);
        proxyCache.registerCacheListener(listener);
        proxyCache.processRequest(request, socket);
        proxyCache.registerCacheListener(null);
        Response response = new Response(out.toByteArray());
        Mockito.verify(listener).onCacheAvailable(Mockito.<File>any(), ArgumentMatchers.eq(zeroSizeUrl), ArgumentMatchers.eq(100));
        assertThat(response.data).isEmpty();
    }

    @Test
    public void testCacheListenerCalledAtTheEnd() throws Exception {
        File file = ProxyCacheTestUtils.newCacheFile();
        File tempFile = ProxyCacheTestUtils.getTempFile(file);
        HttpProxyCache proxyCache = new HttpProxyCache(new HttpUrlSource(ProxyCacheTestUtils.HTTP_DATA_URL), new FileCache(file));
        CacheListener listener = Mockito.mock(CacheListener.class);
        proxyCache.registerCacheListener(listener);
        processRequest(proxyCache, (("GET /" + (ProxyCacheTestUtils.HTTP_DATA_URL)) + " HTTP/1.1"));
        Mockito.verify(listener).onCacheAvailable(tempFile, ProxyCacheTestUtils.HTTP_DATA_URL, 100);// must be called for temp file ...

        Mockito.verify(listener).onCacheAvailable(file, ProxyCacheTestUtils.HTTP_DATA_URL, 100);// .. and for original file too

    }

    @Test(expected = ProxyCacheException.class)
    public void testTouchSourceForAbsentSourceInfoAndCache() throws Exception {
        SourceInfoStorage sourceInfoStorage = SourceInfoStorageFactory.newEmptySourceInfoStorage();
        HttpUrlSource source = ProxyCacheTestUtils.newNotOpenableHttpUrlSource(ProxyCacheTestUtils.HTTP_DATA_URL, sourceInfoStorage);
        HttpProxyCache proxyCache = new HttpProxyCache(source, new FileCache(ProxyCacheTestUtils.newCacheFile()));
        processRequest(proxyCache, (("GET /" + (ProxyCacheTestUtils.HTTP_DATA_URL)) + " HTTP/1.1"));
        proxyCache.shutdown();
        fail("Angry source should throw error! There is no file and caches source info");
    }

    @Test(expected = ProxyCacheException.class)
    public void testTouchSourceForExistedSourceInfoAndAbsentCache() throws Exception {
        SourceInfoStorage sourceInfoStorage = SourceInfoStorageFactory.newSourceInfoStorage(application);
        sourceInfoStorage.put(ProxyCacheTestUtils.HTTP_DATA_URL, new SourceInfo(ProxyCacheTestUtils.HTTP_DATA_URL, ProxyCacheTestUtils.HTTP_DATA_SIZE, "image/jpg"));
        HttpUrlSource source = ProxyCacheTestUtils.newNotOpenableHttpUrlSource(ProxyCacheTestUtils.HTTP_DATA_URL, sourceInfoStorage);
        HttpProxyCache proxyCache = new HttpProxyCache(source, new FileCache(ProxyCacheTestUtils.newCacheFile()));
        processRequest(proxyCache, (("GET /" + (ProxyCacheTestUtils.HTTP_DATA_URL)) + " HTTP/1.1"));
        proxyCache.shutdown();
        fail("Angry source should throw error! There is no cache file");
    }

    @Test
    public void testTouchSourceForExistedSourceInfoAndCache() throws Exception {
        SourceInfoStorage sourceInfoStorage = SourceInfoStorageFactory.newSourceInfoStorage(application);
        sourceInfoStorage.put(ProxyCacheTestUtils.HTTP_DATA_URL, new SourceInfo(ProxyCacheTestUtils.HTTP_DATA_URL, ProxyCacheTestUtils.HTTP_DATA_SIZE, "cached/mime"));
        HttpUrlSource source = ProxyCacheTestUtils.newNotOpenableHttpUrlSource(ProxyCacheTestUtils.HTTP_DATA_URL, sourceInfoStorage);
        File file = ProxyCacheTestUtils.newCacheFile();
        IoUtils.saveToFile(ProxyCacheTestUtils.loadAssetFile(ProxyCacheTestUtils.ASSETS_DATA_NAME), file);
        HttpProxyCache proxyCache = new HttpProxyCache(source, new FileCache(file));
        Response response = processRequest(proxyCache, (("GET /" + (ProxyCacheTestUtils.HTTP_DATA_URL)) + " HTTP/1.1"));
        proxyCache.shutdown();
        assertThat(response.data).isEqualTo(ProxyCacheTestUtils.loadAssetFile(ProxyCacheTestUtils.ASSETS_DATA_NAME));
        assertThat(response.contentLength).isEqualTo(ProxyCacheTestUtils.HTTP_DATA_SIZE);
        assertThat(response.contentType).isEqualTo("cached/mime");
    }

    @Test
    public void testReuseSourceInfo() throws Exception {
        SourceInfoStorage sourceInfoStorage = SourceInfoStorageFactory.newSourceInfoStorage(application);
        HttpUrlSource source = new HttpUrlSource(ProxyCacheTestUtils.HTTP_DATA_URL, sourceInfoStorage);
        File cacheFile = ProxyCacheTestUtils.newCacheFile();
        HttpProxyCache proxyCache = new HttpProxyCache(source, new FileCache(cacheFile));
        processRequest(proxyCache, (("GET /" + (ProxyCacheTestUtils.HTTP_DATA_URL)) + " HTTP/1.1"));
        HttpUrlSource notOpenableSource = ProxyCacheTestUtils.newNotOpenableHttpUrlSource(ProxyCacheTestUtils.HTTP_DATA_URL, sourceInfoStorage);
        HttpProxyCache proxyCache2 = new HttpProxyCache(notOpenableSource, new FileCache(cacheFile));
        Response response = processRequest(proxyCache2, (("GET /" + (ProxyCacheTestUtils.HTTP_DATA_URL)) + " HTTP/1.1"));
        proxyCache.shutdown();
        assertThat(response.data).isEqualTo(ProxyCacheTestUtils.loadAssetFile(ProxyCacheTestUtils.ASSETS_DATA_NAME));
        assertThat(response.contentLength).isEqualTo(ProxyCacheTestUtils.HTTP_DATA_SIZE);
        assertThat(response.contentType).isEqualTo("image/jpeg");
    }
}

