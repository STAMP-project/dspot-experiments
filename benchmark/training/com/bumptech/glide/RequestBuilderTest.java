package com.bumptech.glide;


import DataSource.LOCAL;
import android.app.Application;
import android.widget.ImageView;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.SingleRequest;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.tests.BackgroundUtil;
import com.bumptech.glide.tests.TearDownGlide;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;


@SuppressWarnings("unchecked")
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 18)
public class RequestBuilderTest {
    @Rule
    public TearDownGlide tearDownGlide = new TearDownGlide();

    @Mock
    private RequestListener<Object> listener1;

    @Mock
    private RequestListener<Object> listener2;

    @Mock
    private Target<Object> target;

    @Mock
    private GlideContext glideContext;

    @Mock
    private RequestManager requestManager;

    @Captor
    private ArgumentCaptor<SingleRequest<Object>> requestCaptor;

    private Glide glide;

    private Application context;

    @Test(expected = NullPointerException.class)
    public void testThrowsIfContextIsNull() {
        /* context */
        new RequestBuilder(null, requestManager, Object.class, context);
    }

    @Test(expected = NullPointerException.class)
    public void testThrowsWhenTransitionsOptionsIsNull() {
        // noinspection ConstantConditions testing if @NonNull is enforced
        getNullModelRequest().transition(null);
    }

    @Test
    public void testDoesNotThrowWithNullModelWhenRequestIsBuilt() {
        getNullModelRequest().into(target);
    }

    @Test
    public void testAddsNewRequestToRequestTracker() {
        getNullModelRequest().into(target);
        Mockito.verify(requestManager).track(ArgumentMatchers.eq(target), ArgumentMatchers.isA(Request.class));
    }

    @Test
    public void testRemovesPreviousRequestFromRequestTracker() {
        Request previous = Mockito.mock(Request.class);
        Mockito.when(target.getRequest()).thenReturn(previous);
        getNullModelRequest().into(target);
        Mockito.verify(requestManager).clear(ArgumentMatchers.eq(target));
    }

    @Test(expected = NullPointerException.class)
    public void testThrowsIfGivenNullTarget() {
        // noinspection ConstantConditions testing if @NonNull is enforced
        getNullModelRequest().into(((Target<Object>) (null)));
    }

    @Test(expected = NullPointerException.class)
    public void testThrowsIfGivenNullView() {
        getNullModelRequest().into(((ImageView) (null)));
    }

    @Test(expected = RuntimeException.class)
    public void testThrowsIfIntoViewCalledOnBackgroundThread() throws InterruptedException {
        final ImageView imageView = new ImageView(RuntimeEnvironment.application);
        BackgroundUtil.testInBackground(new BackgroundUtil.BackgroundTester() {
            @Override
            public void runTest() {
                getNullModelRequest().into(imageView);
            }
        });
    }

    @Test
    public void doesNotThrowIfIntoTargetCalledOnBackgroundThread() throws InterruptedException {
        final Target<Object> target = Mockito.mock(Target.class);
        BackgroundUtil.testInBackground(new BackgroundUtil.BackgroundTester() {
            @Override
            public void runTest() {
                getNullModelRequest().into(target);
            }
        });
    }

    @Test
    public void testMultipleRequestListeners() {
        getNullModelRequest().addListener(listener1).addListener(listener2).into(target);
        Mockito.verify(requestManager).track(ArgumentMatchers.any(Target.class), requestCaptor.capture());
        requestCaptor.getValue().onResourceReady(new com.bumptech.glide.load.resource.SimpleResource(new Object()), LOCAL);
        Mockito.verify(listener1).onResourceReady(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.isA(Target.class), ArgumentMatchers.isA(DataSource.class), ArgumentMatchers.anyBoolean());
        Mockito.verify(listener2).onResourceReady(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.isA(Target.class), ArgumentMatchers.isA(DataSource.class), ArgumentMatchers.anyBoolean());
    }

    @Test
    public void testListenerApiOverridesListeners() {
        getNullModelRequest().addListener(listener1).listener(listener2).into(target);
        Mockito.verify(requestManager).track(ArgumentMatchers.any(Target.class), requestCaptor.capture());
        requestCaptor.getValue().onResourceReady(new com.bumptech.glide.load.resource.SimpleResource(new Object()), LOCAL);
        // The #listener API removes any previous listeners, so the first listener should not be called.
        Mockito.verify(listener1, Mockito.never()).onResourceReady(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.isA(Target.class), ArgumentMatchers.isA(DataSource.class), ArgumentMatchers.anyBoolean());
        Mockito.verify(listener2).onResourceReady(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.isA(Target.class), ArgumentMatchers.isA(DataSource.class), ArgumentMatchers.anyBoolean());
    }
}

