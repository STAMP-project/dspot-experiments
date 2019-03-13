package com.bumptech.glide;


import Bitmap.Config.ARGB_8888;
import Color.RED;
import DiskCache.Factory;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.load.engine.cache.MemoryCache;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.manager.Lifecycle;
import com.bumptech.glide.manager.RequestManagerTreeNode;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.tests.GlideShadowLooper;
import com.bumptech.glide.tests.TearDownGlide;
import com.bumptech.glide.util.Preconditions;
import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.annotation.Resetter;
import org.robolectric.shadows.ShadowBitmap;

import static MemoryCategory.HIGH;
import static MemoryCategory.LOW;
import static MemoryCategory.NORMAL;


/**
 * Tests for the {@link Glide} interface and singleton.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 18, shadows = { GlideTest.ShadowFileDescriptorContentResolver.class, GlideTest.ShadowMediaMetadataRetriever.class, GlideShadowLooper.class, GlideTest.MutableShadowBitmap.class })
@SuppressWarnings("unchecked")
public class GlideTest {
    // Fixes method overload confusion.
    private static final Object NULL = null;

    @Rule
    public TearDownGlide tearDownGlide = new TearDownGlide();

    @SuppressWarnings("rawtypes")
    @Mock
    private Target target;

    @Mock
    private Factory diskCacheFactory;

    @Mock
    private DiskCache diskCache;

    @Mock
    private MemoryCache memoryCache;

    @Mock
    private Handler bgHandler;

    @Mock
    private Lifecycle lifecycle;

    @Mock
    private RequestManagerTreeNode treeNode;

    @Mock
    private BitmapPool bitmapPool;

    private ImageView imageView;

    private RequestManager requestManager;

    private Context context;

    @Test
    public void testCanSetMemoryCategory() {
        MemoryCategory memoryCategory = NORMAL;
        Glide glide = new GlideBuilder().setBitmapPool(bitmapPool).setMemoryCache(memoryCache).build(context);
        glide.setMemoryCategory(memoryCategory);
        Mockito.verify(memoryCache).setSizeMultiplier(ArgumentMatchers.eq(memoryCategory.getMultiplier()));
        Mockito.verify(bitmapPool).setSizeMultiplier(ArgumentMatchers.eq(memoryCategory.getMultiplier()));
    }

    @Test
    public void testCanIncreaseMemoryCategory() {
        MemoryCategory memoryCategory = NORMAL;
        Glide glide = new GlideBuilder().setBitmapPool(bitmapPool).setMemoryCache(memoryCache).build(context);
        glide.setMemoryCategory(memoryCategory);
        Mockito.verify(memoryCache).setSizeMultiplier(ArgumentMatchers.eq(memoryCategory.getMultiplier()));
        Mockito.verify(bitmapPool).setSizeMultiplier(ArgumentMatchers.eq(memoryCategory.getMultiplier()));
        MemoryCategory newMemoryCategory = HIGH;
        MemoryCategory oldMemoryCategory = glide.setMemoryCategory(newMemoryCategory);
        Assert.assertEquals(memoryCategory, oldMemoryCategory);
        Mockito.verify(memoryCache).setSizeMultiplier(ArgumentMatchers.eq(newMemoryCategory.getMultiplier()));
        Mockito.verify(bitmapPool).setSizeMultiplier(ArgumentMatchers.eq(newMemoryCategory.getMultiplier()));
    }

    @Test
    public void testCanDecreaseMemoryCategory() {
        MemoryCategory memoryCategory = NORMAL;
        Glide glide = new GlideBuilder().setBitmapPool(bitmapPool).setMemoryCache(memoryCache).build(context);
        glide.setMemoryCategory(memoryCategory);
        Mockito.verify(memoryCache).setSizeMultiplier(ArgumentMatchers.eq(memoryCategory.getMultiplier()));
        Mockito.verify(bitmapPool).setSizeMultiplier(ArgumentMatchers.eq(memoryCategory.getMultiplier()));
        MemoryCategory newMemoryCategory = LOW;
        MemoryCategory oldMemoryCategory = glide.setMemoryCategory(newMemoryCategory);
        Assert.assertEquals(memoryCategory, oldMemoryCategory);
        Mockito.verify(memoryCache).setSizeMultiplier(ArgumentMatchers.eq(newMemoryCategory.getMultiplier()));
        Mockito.verify(bitmapPool).setSizeMultiplier(ArgumentMatchers.eq(newMemoryCategory.getMultiplier()));
    }

    @Test
    public void testClearMemory() {
        Glide glide = new GlideBuilder().setBitmapPool(bitmapPool).setMemoryCache(memoryCache).build(context);
        glide.clearMemory();
        Mockito.verify(bitmapPool).clearMemory();
        Mockito.verify(memoryCache).clearMemory();
    }

    @Test
    public void testTrimMemory() {
        Glide glide = new GlideBuilder().setBitmapPool(bitmapPool).setMemoryCache(memoryCache).build(context);
        final int level = 123;
        glide.trimMemory(level);
        Mockito.verify(bitmapPool).trimMemory(ArgumentMatchers.eq(level));
        Mockito.verify(memoryCache).trimMemory(ArgumentMatchers.eq(level));
    }

    @Test
    public void testFileDefaultLoaderWithInputStream() {
        registerFailFactory(File.class, ParcelFileDescriptor.class);
        runTestFileDefaultLoader();
    }

    @Test
    public void testFileDefaultLoaderWithFileDescriptor() {
        registerFailFactory(File.class, InputStream.class);
        runTestFileDefaultLoader();
    }

    @Test
    public void testFileDefaultLoader() {
        runTestFileDefaultLoader();
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testUrlDefaultLoader() throws MalformedURLException {
        URL url = new URL("http://www.google.com");
        requestManager.load(url).into(target);
        requestManager.load(url).into(imageView);
        Mockito.verify(target).onResourceReady(ArgumentMatchers.isA(BitmapDrawable.class), ArgumentMatchers.isA(Transition.class));
        Mockito.verify(target).setRequest(((Request) (ArgumentMatchers.notNull())));
        Assert.assertNotNull(imageView.getDrawable());
    }

    @Test
    public void testAsBitmapOption() {
        Uri uri = Uri.parse("content://something/else");
        mockUri(uri);
        requestManager.asBitmap().load(uri).into(target);
        Mockito.verify(target).onResourceReady(ArgumentMatchers.isA(Bitmap.class), ArgumentMatchers.isA(Transition.class));
    }

    @Test
    public void testToBytesOption() {
        Uri uri = Uri.parse("content://something/else");
        mockUri(uri);
        requestManager.as(byte[].class).apply(RequestOptions.decodeTypeOf(Bitmap.class)).load(uri).into(target);
        Mockito.verify(target).onResourceReady(ArgumentMatchers.isA(byte[].class), ArgumentMatchers.isA(Transition.class));
    }

    @Test
    public void testLoadColorDrawable_withUnitBitmapTransformation_returnsColorDrawable() {
        ColorDrawable colorDrawable = new ColorDrawable(Color.RED);
        requestManager.load(colorDrawable).apply(new RequestOptions().override(100, 100).centerCrop()).into(target);
        ArgumentCaptor<Object> argumentCaptor = ArgumentCaptor.forClass(Object.class);
        Mockito.verify(target).onResourceReady(argumentCaptor.capture(), ArgumentMatchers.isA(Transition.class));
        Object result = argumentCaptor.getValue();
        assertThat(result).isInstanceOf(ColorDrawable.class);
        assertThat(getColor()).isEqualTo(RED);
    }

    @Test
    public void testLoadColorDrawable_withNonUnitBitmapTransformation_returnsBitmapDrawable() {
        ColorDrawable colorDrawable = new ColorDrawable(Color.RED);
        requestManager.load(colorDrawable).apply(new RequestOptions().override(100, 100).circleCrop()).into(target);
        ArgumentCaptor<Object> argumentCaptor = ArgumentCaptor.forClass(Object.class);
        Mockito.verify(target).onResourceReady(argumentCaptor.capture(), ArgumentMatchers.isA(Transition.class));
        Object result = argumentCaptor.getValue();
        assertThat(result).isInstanceOf(BitmapDrawable.class);
        Bitmap bitmap = getBitmap();
        assertThat(bitmap.getWidth()).isEqualTo(100);
        assertThat(bitmap.getHeight()).isEqualTo(100);
    }

    @Test
    public void testUriDefaultLoaderWithInputStream() {
        registerFailFactory(Uri.class, ParcelFileDescriptor.class);
        runTestUriDefaultLoader();
    }

    @Test
    public void testUriDefaultLoaderWithFileDescriptor() {
        registerFailFactory(Uri.class, InputStream.class);
        runTestUriDefaultLoader();
    }

    @Test
    public void testUriDefaultLoader() {
        runTestUriDefaultLoader();
    }

    @Test
    public void testStringDefaultLoaderWithUrl() {
        runTestStringDefaultLoader("http://www.google.com");
    }

    @Test
    public void testFileStringDefaultLoaderWithInputStream() {
        registerFailFactory(String.class, ParcelFileDescriptor.class);
        runTestFileStringDefaultLoader();
    }

    @Test
    public void testFileStringDefaultLoaderWithFileDescriptor() {
        registerFailFactory(String.class, ParcelFileDescriptor.class);
        runTestFileStringDefaultLoader();
    }

    @Test
    public void testFileStringDefaultLoader() {
        runTestFileStringDefaultLoader();
    }

    @Test
    public void testUriStringDefaultLoaderWithInputStream() {
        registerFailFactory(String.class, ParcelFileDescriptor.class);
        runTestUriStringDefaultLoader();
    }

    @Test
    public void testUriStringDefaultLoaderWithFileDescriptor() {
        registerFailFactory(String.class, InputStream.class);
        runTestUriStringDefaultLoader();
    }

    @Test
    public void testUriStringDefaultLoader() {
        runTestUriStringDefaultLoader();
    }

    @Test
    public void testIntegerDefaultLoaderWithInputStream() {
        registerFailFactory(Integer.class, ParcelFileDescriptor.class);
        runTestIntegerDefaultLoader();
    }

    @Test
    public void testIntegerDefaultLoaderWithFileDescriptor() {
        registerFailFactory(Integer.class, InputStream.class);
        runTestIntegerDefaultLoader();
    }

    @Test
    public void testIntegerDefaultLoader() {
        runTestIntegerDefaultLoader();
    }

    @Test
    public void testByteArrayDefaultLoader() {
        byte[] bytes = new byte[10];
        requestManager.load(bytes).into(target);
        requestManager.load(bytes).into(imageView);
        Mockito.verify(target).onResourceReady(ArgumentMatchers.isA(BitmapDrawable.class), ArgumentMatchers.isA(Transition.class));
        Mockito.verify(target).setRequest(((Request) (ArgumentMatchers.notNull())));
        Assert.assertNotNull(imageView.getDrawable());
    }

    @Test(expected = Exception.class)
    public void testUnregisteredModelThrowsException() {
        Float unregistered = 0.5F;
        requestManager.load(unregistered).into(target);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testNonDefaultModelWithRegisteredFactoryDoesNotThrow() {
        registerMockStreamModelLoader(Float.class);
        requestManager.load(0.5F).into(target);
    }

    @Test
    public void testReceivesGif() {
        String fakeUri = "content://fake";
        InputStream testGifData = openGif();
        mockUri(Uri.parse(fakeUri), testGifData);
        requestManager.asGif().load(fakeUri).into(target);
        Mockito.verify(target).onResourceReady(ArgumentMatchers.isA(GifDrawable.class), ArgumentMatchers.isA(Transition.class));
    }

    @Test
    public void testReceivesGifBytes() {
        String fakeUri = "content://fake";
        InputStream testGifData = openGif();
        mockUri(Uri.parse(fakeUri), testGifData);
        requestManager.as(byte[].class).apply(RequestOptions.decodeTypeOf(GifDrawable.class)).load(fakeUri).into(target);
        Mockito.verify(target).onResourceReady(ArgumentMatchers.isA(byte[].class), ArgumentMatchers.isA(Transition.class));
    }

    @Test
    public void testReceivesBitmapBytes() {
        String fakeUri = "content://fake";
        mockUri(fakeUri);
        requestManager.as(byte[].class).apply(RequestOptions.decodeTypeOf(Bitmap.class)).load(fakeUri).into(target);
        Mockito.verify(target).onResourceReady(ArgumentMatchers.isA(byte[].class), ArgumentMatchers.isA(Transition.class));
    }

    @Test
    public void testReceivesThumbnails() {
        String full = mockUri("content://full");
        String thumb = mockUri("content://thumb");
        requestManager.load(full).thumbnail(requestManager.load(thumb)).into(target);
        Mockito.verify(target, Mockito.times(2)).onResourceReady(ArgumentMatchers.isA(Drawable.class), ArgumentMatchers.isA(Transition.class));
    }

    @Test
    public void testReceivesRecursiveThumbnails() {
        requestManager.load(mockUri("content://first")).thumbnail(requestManager.load(mockUri("content://second")).thumbnail(requestManager.load(mockUri("content://third")).thumbnail(requestManager.load(mockUri("content://fourth"))))).into(target);
        Mockito.verify(target, Mockito.times(4)).onResourceReady(ArgumentMatchers.isA(Drawable.class), ArgumentMatchers.isA(Transition.class));
    }

    @Test
    public void testReceivesRecursiveThumbnailWithPercentage() {
        requestManager.load(mockUri("content://first")).thumbnail(requestManager.load(mockUri("content://second")).thumbnail(0.5F)).into(target);
        Mockito.verify(target, Mockito.times(3)).onResourceReady(ArgumentMatchers.isA(Drawable.class), ArgumentMatchers.isA(Transition.class));
    }

    @Test
    public void testNullModelInGenericImageLoadDoesNotThrow() {
        requestManager.load(GlideTest.NULL).into(target);
    }

    @Test
    public void testNullModelInGenericVideoLoadDoesNotThrow() {
        requestManager.load(GlideTest.NULL).into(target);
    }

    @Test
    public void testNullModelInGenericLoadDoesNotThrow() {
        requestManager.load(GlideTest.NULL).into(target);
    }

    @Test
    public void testNullModelDoesNotThrow() {
        Drawable drawable = new ColorDrawable(Color.RED);
        requestManager.load(GlideTest.NULL).apply(RequestOptions.errorOf(drawable)).into(target);
        Mockito.verify(target).onLoadFailed(ArgumentMatchers.eq(drawable));
    }

    @Test
    public void testNullModelPrefersErrorDrawable() {
        Drawable placeholder = new ColorDrawable(Color.GREEN);
        Drawable error = new ColorDrawable(Color.RED);
        requestManager.load(GlideTest.NULL).apply(RequestOptions.placeholderOf(placeholder).error(error)).into(target);
        Mockito.verify(target).onLoadFailed(ArgumentMatchers.eq(error));
    }

    @Test
    public void testLoadBitmap_asBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(100, 100, ARGB_8888);
        requestManager.asBitmap().load(bitmap).into(target);
        Mockito.verify(target).onResourceReady(ArgumentMatchers.eq(bitmap), ArgumentMatchers.any(Transition.class));
    }

    @Test
    public void testLoadBitmap_asDrawable() {
        Bitmap bitmap = Bitmap.createBitmap(100, 100, ARGB_8888);
        requestManager.load(bitmap).into(target);
        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        Mockito.verify(target).onResourceReady(captor.capture(), ArgumentMatchers.any(Transition.class));
        BitmapDrawable drawable = ((BitmapDrawable) (captor.getValue()));
        assertThat(drawable.getBitmap()).isEqualTo(bitmap);
    }

    @Test
    public void testLoadDrawable() {
        Drawable drawable = new ColorDrawable(Color.RED);
        requestManager.load(drawable).into(target);
        ArgumentCaptor<Drawable> drawableCaptor = ArgumentCaptor.forClass(Drawable.class);
        Mockito.verify(target).onResourceReady(drawableCaptor.capture(), ArgumentMatchers.any(Transition.class));
        assertThat(getColor()).isEqualTo(RED);
    }

    @Test
    public void testNullModelPrefersFallbackDrawable() {
        Drawable placeholder = new ColorDrawable(Color.GREEN);
        Drawable error = new ColorDrawable(Color.RED);
        Drawable fallback = new ColorDrawable(Color.BLUE);
        requestManager.load(GlideTest.NULL).apply(RequestOptions.placeholderOf(placeholder).error(error).fallback(fallback)).into(target);
        Mockito.verify(target).onLoadFailed(ArgumentMatchers.eq(fallback));
    }

    @Test
    public void testNullModelResolvesToUsePlaceholder() {
        Drawable placeholder = new ColorDrawable(Color.GREEN);
        requestManager.load(GlideTest.NULL).apply(RequestOptions.placeholderOf(placeholder)).into(target);
        Mockito.verify(target).onLoadFailed(ArgumentMatchers.eq(placeholder));
    }

    @Test
    public void testByteData() {
        byte[] data = new byte[]{ 1, 2, 3, 4, 5, 6 };
        requestManager.load(data).into(target);
    }

    @Test
    public void removeFromManagers_afterRequestManagerRemoved_clearsRequest() {
        target = requestManager.load(mockUri("content://uri")).into(new com.bumptech.glide.request.target.SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull
            Drawable resource, @Nullable
            Transition<? super Drawable> transition) {
                // Do nothing.
            }
        });
        Request request = Preconditions.checkNotNull(target.getRequest());
        requestManager.onDestroy();
        requestManager.clear(target);
        assertThat(target.getRequest()).isNull();
        assertThat(request.isCleared()).isTrue();
    }

    @Test
    public void testClone() {
        Target<Drawable> firstTarget = Mockito.mock(Target.class);
        Mockito.doAnswer(new GlideTest.CallSizeReady(100, 100)).when(firstTarget).getSize(ArgumentMatchers.isA(SizeReadyCallback.class));
        Target<Drawable> secondTarget = Mockito.mock(Target.class);
        Mockito.doAnswer(new GlideTest.CallSizeReady(100, 100)).when(secondTarget).getSize(ArgumentMatchers.isA(SizeReadyCallback.class));
        RequestBuilder<Drawable> firstRequest = requestManager.load(mockUri("content://first"));
        firstRequest.into(firstTarget);
        firstRequest.clone().apply(RequestOptions.placeholderOf(new ColorDrawable(Color.RED))).into(secondTarget);
        Mockito.verify(firstTarget).onResourceReady(ArgumentMatchers.isA(Drawable.class), ArgumentMatchers.isA(Transition.class));
        Mockito.verify(secondTarget).onResourceReady(ArgumentMatchers.notNull(Drawable.class), ArgumentMatchers.isA(Transition.class));
    }

    private static class CallSizeReady implements Answer<Void> {
        private final int width;

        private final int height;

        CallSizeReady() {
            this(100, 100);
        }

        CallSizeReady(int width, int height) {
            this.width = width;
            this.height = height;
        }

        @Override
        public Void answer(InvocationOnMock invocation) throws Throwable {
            SizeReadyCallback cb = ((SizeReadyCallback) (invocation.getArguments()[0]));
            cb.onSizeReady(width, height);
            return null;
        }
    }

    // TODO: Extending ShadowContentResolver results in exceptions because of some state issues
    // where we seem to get one content resolver shadow in one part of the test and a different one in
    // a different part of the test. Each one ends up with different registered uris, which causes
    // tests to fail. We shouldn't need to do this, but using static maps seems to fix the issue.
    @Implements(ContentResolver.class)
    @SuppressWarnings("unused")
    public static class ShadowFileDescriptorContentResolver {
        private static final Map<Uri, AssetFileDescriptor> URI_TO_FILE_DESCRIPTOR = new HashMap<>();

        private static final Map<Uri, InputStream> URI_TO_INPUT_STREAMS = new HashMap<>();

        @Resetter
        public static void reset() {
            GlideTest.ShadowFileDescriptorContentResolver.URI_TO_INPUT_STREAMS.clear();
            GlideTest.ShadowFileDescriptorContentResolver.URI_TO_FILE_DESCRIPTOR.clear();
        }

        void registerInputStream(Uri uri, InputStream inputStream) {
            GlideTest.ShadowFileDescriptorContentResolver.URI_TO_INPUT_STREAMS.put(uri, inputStream);
        }

        void registerAssetFileDescriptor(Uri uri, AssetFileDescriptor assetFileDescriptor) {
            GlideTest.ShadowFileDescriptorContentResolver.URI_TO_FILE_DESCRIPTOR.put(uri, assetFileDescriptor);
        }

        @Implementation
        public InputStream openInputStream(Uri uri) {
            if (!(GlideTest.ShadowFileDescriptorContentResolver.URI_TO_INPUT_STREAMS.containsKey(uri))) {
                throw new IllegalArgumentException(("You must first register an InputStream for uri: " + uri));
            }
            return GlideTest.ShadowFileDescriptorContentResolver.URI_TO_INPUT_STREAMS.get(uri);
        }

        @Implementation
        public AssetFileDescriptor openAssetFileDescriptor(Uri uri, String type) {
            if (!(GlideTest.ShadowFileDescriptorContentResolver.URI_TO_FILE_DESCRIPTOR.containsKey(uri))) {
                throw new IllegalArgumentException((("You must first register an AssetFileDescriptor for " + "uri: ") + uri));
            }
            return GlideTest.ShadowFileDescriptorContentResolver.URI_TO_FILE_DESCRIPTOR.get(uri);
        }
    }

    @Implements(Bitmap.class)
    public static class MutableShadowBitmap extends ShadowBitmap {
        @Implementation
        public static Bitmap createBitmap(int width, int height, Bitmap.Config config) {
            Bitmap bitmap = ShadowBitmap.createBitmap(width, height, config);
            Shadows.shadowOf(bitmap).setMutable(true);
            return bitmap;
        }
    }

    @Implements(MediaMetadataRetriever.class)
    public static class ShadowMediaMetadataRetriever {
        @Implementation
        @SuppressWarnings("unused")
        public Bitmap getFrameAtTime() {
            Bitmap bitmap = Bitmap.createBitmap(100, 100, ARGB_8888);
            Shadows.shadowOf(bitmap).appendDescription(" from MediaMetadataRetriever");
            return bitmap;
        }
    }
}

