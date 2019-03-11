package com.squareup.picasso3;


import android.content.Context;
import android.graphics.Bitmap;
import androidx.annotation.NonNull;
import com.squareup.picasso3.RequestHandler.Result;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(shadows = { Shadows.ShadowVideoThumbnails.class, Shadows.ShadowImageThumbnails.class })
public class MediaStoreRequestHandlerTest {
    @Mock
    Context context;

    @Test
    public void decodesVideoThumbnailWithVideoMimeType() {
        final Bitmap bitmap = TestUtils.makeBitmap();
        Request request = stableKey(TestUtils.MEDIA_STORE_CONTENT_KEY_1).resize(100, 100).build();
        Action action = TestUtils.mockAction(request);
        MediaStoreRequestHandler requestHandler = create("video/");
        requestHandler.load(null, action.request, new RequestHandler.Callback() {
            @Override
            public void onSuccess(Result result) {
                MediaStoreRequestHandlerTest.assertBitmapsEqual(result.getBitmap(), bitmap);
            }

            @Override
            public void onError(@NonNull
            Throwable t) {
                Assert.fail(t.getMessage());
            }
        });
    }

    @Test
    public void decodesImageThumbnailWithImageMimeType() {
        final Bitmap bitmap = TestUtils.makeBitmap(20, 20);
        Request request = stableKey(TestUtils.MEDIA_STORE_CONTENT_KEY_1).resize(100, 100).build();
        Action action = TestUtils.mockAction(request);
        MediaStoreRequestHandler requestHandler = create("image/png");
        requestHandler.load(null, action.request, new RequestHandler.Callback() {
            @Override
            public void onSuccess(Result result) {
                MediaStoreRequestHandlerTest.assertBitmapsEqual(result.getBitmap(), bitmap);
            }

            @Override
            public void onError(@NonNull
            Throwable t) {
                Assert.fail(t.getMessage());
            }
        });
    }

    @Test
    public void getPicassoKindMicro() {
        assertThat(MediaStoreRequestHandler.getPicassoKind(96, 96)).isEqualTo(PicassoKind.MICRO);
        assertThat(MediaStoreRequestHandler.getPicassoKind(95, 95)).isEqualTo(PicassoKind.MICRO);
    }

    @Test
    public void getPicassoKindMini() {
        assertThat(MediaStoreRequestHandler.getPicassoKind(512, 384)).isEqualTo(PicassoKind.MINI);
        assertThat(MediaStoreRequestHandler.getPicassoKind(100, 100)).isEqualTo(PicassoKind.MINI);
    }

    @Test
    public void getPicassoKindFull() {
        assertThat(MediaStoreRequestHandler.getPicassoKind(513, 385)).isEqualTo(PicassoKind.FULL);
        assertThat(MediaStoreRequestHandler.getPicassoKind(1000, 1000)).isEqualTo(PicassoKind.FULL);
        assertThat(MediaStoreRequestHandler.getPicassoKind(1000, 384)).isEqualTo(PicassoKind.FULL);
        assertThat(MediaStoreRequestHandler.getPicassoKind(1000, 96)).isEqualTo(PicassoKind.FULL);
        assertThat(MediaStoreRequestHandler.getPicassoKind(96, 1000)).isEqualTo(PicassoKind.FULL);
    }
}

