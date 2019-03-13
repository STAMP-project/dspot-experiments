package com.bumptech.glide.load.resource.bitmap;


import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import com.bumptech.glide.load.engine.Initializable;
import com.bumptech.glide.load.engine.Resource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class LazyBitmapDrawableResourceTest {
    @Mock
    private Resource<Bitmap> bitmapResource;

    private LazyBitmapDrawableResource resource;

    private Resources resources;

    private Bitmap bitmap;

    @Test
    public void obtain_withNullBitmapResource_returnsNull() {
        assertThat(LazyBitmapDrawableResource.obtain(resources, null)).isNull();
    }

    @Test
    public void getSize_returnsSizeOfWrappedResource() {
        Mockito.when(bitmapResource.getSize()).thenReturn(100);
        assertThat(resource.getSize()).isEqualTo(100);
    }

    @Test
    public void recycle_callsRecycleOnWrappedResource() {
        resource.recycle();
        Mockito.verify(bitmapResource).recycle();
    }

    @Test
    public void recycle_doesNotRecycleWrappedBitmap() {
        resource.recycle();
        assertThat(bitmap.isRecycled()).isFalse();
    }

    @Test
    public void get_returnsDrawableContainingWrappedBitmap() {
        BitmapDrawable drawable = resource.get();
        assertThat(drawable.getBitmap()).isSameAs(bitmap);
    }

    @Test
    public void initialize_withNonInitializableResource_doesNothing() {
        resource.initialize();
    }

    @Test
    public void initialize_withWrappedInitializableResource_callsInitializeOnWrapped() {
        LazyBitmapDrawableResourceTest.InitializableBitmapResource bitmapResource = Mockito.mock(LazyBitmapDrawableResourceTest.InitializableBitmapResource.class);
        resource = ((LazyBitmapDrawableResource) (LazyBitmapDrawableResource.obtain(resources, bitmapResource)));
        resource.initialize();
        initialize();
    }

    // Intentionally empty.
    private interface InitializableBitmapResource extends Initializable , Resource<Bitmap> {}
}

