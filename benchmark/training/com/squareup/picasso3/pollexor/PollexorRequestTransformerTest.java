package com.squareup.picasso3.pollexor;


import android.net.Uri;
import com.squareup.picasso3.Picasso;
import com.squareup.picasso3.Request;
import com.squareup.pollexor.Thumbor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(sdk = 17, manifest = NONE)
public class PollexorRequestTransformerTest {
    private static final String HOST = "http://example.com/";

    private static final String KEY = "omgsecretpassword";

    private static final String IMAGE = "http://google.com/logo.png";

    private static final Uri IMAGE_URI = Uri.parse(PollexorRequestTransformerTest.IMAGE);

    private Picasso.RequestTransformer transformer = new PollexorRequestTransformer(Thumbor.create(PollexorRequestTransformerTest.HOST));

    private Picasso.RequestTransformer secureTransformer = new PollexorRequestTransformer(Thumbor.create(PollexorRequestTransformerTest.HOST, PollexorRequestTransformerTest.KEY));

    private Picasso.RequestTransformer alwaysResizeTransformer = new PollexorRequestTransformer(Thumbor.create(PollexorRequestTransformerTest.HOST), true);

    @Test
    public void resourceIdRequestsAreNotTransformed() {
        Request input = new Request.Builder(12).build();
        Request output = transformer.transformRequest(input);
        assertThat(output).isSameAs(input);
    }

    @Test
    public void resourceIdRequestsAreNotTransformedWhenAlwaysTransformIsTrue() {
        Request input = new Request.Builder(12).build();
        Request output = alwaysResizeTransformer.transformRequest(input);
        assertThat(output).isSameAs(input);
    }

    @Test
    public void nonHttpRequestsAreNotTransformed() {
        Request input = build();
        Request output = transformer.transformRequest(input);
        assertThat(output).isSameAs(input);
    }

    @Test
    public void nonResizedRequestsAreNotTransformed() {
        Request input = build();
        Request output = transformer.transformRequest(input);
        assertThat(output).isSameAs(input);
    }

    @Test
    public void nonResizedRequestsAreTransformedWhenAlwaysTransformIsSet() {
        Request input = build();
        Request output = alwaysResizeTransformer.transformRequest(input);
        assertThat(output).isNotSameAs(input);
        assertThat(output.hasSize()).isFalse();
        String expected = Thumbor.create(PollexorRequestTransformerTest.HOST).buildImage(PollexorRequestTransformerTest.IMAGE).toUrl();
        assertThat(output.uri.toString()).isEqualTo(expected);
    }

    @Test
    public void simpleResize() {
        Request input = build();
        Request output = transformer.transformRequest(input);
        assertThat(output).isNotSameAs(input);
        assertThat(output.hasSize()).isFalse();
        String expected = Thumbor.create(PollexorRequestTransformerTest.HOST).buildImage(PollexorRequestTransformerTest.IMAGE).resize(50, 50).toUrl();
        assertThat(output.uri.toString()).isEqualTo(expected);
    }

    @Config(sdk = 18)
    @Test
    public void simpleResizeOnJbMr2UsesWebP() {
        Request input = build();
        Request output = transformer.transformRequest(input);
        assertThat(output).isNotSameAs(input);
        assertThat(output.hasSize()).isFalse();
        String expected = Thumbor.create(PollexorRequestTransformerTest.HOST).buildImage(PollexorRequestTransformerTest.IMAGE).resize(50, 50).filter(format(ImageFormat.WEBP)).toUrl();
        assertThat(output.uri.toString()).isEqualTo(expected);
    }

    @Test
    public void simpleResizeWithCenterCrop() {
        Request input = build();
        Request output = transformer.transformRequest(input);
        assertThat(output).isNotSameAs(input);
        assertThat(output.hasSize()).isFalse();
        assertThat(output.centerCrop).isFalse();
        String expected = Thumbor.create(PollexorRequestTransformerTest.HOST).buildImage(PollexorRequestTransformerTest.IMAGE).resize(50, 50).toUrl();
        assertThat(output.uri.toString()).isEqualTo(expected);
    }

    @Test
    public void simpleResizeWithCenterInside() {
        Request input = build();
        Request output = transformer.transformRequest(input);
        assertThat(output).isNotSameAs(input);
        assertThat(output.hasSize()).isFalse();
        assertThat(output.centerInside).isFalse();
        String expected = Thumbor.create(PollexorRequestTransformerTest.HOST).buildImage(PollexorRequestTransformerTest.IMAGE).resize(50, 50).fitIn().toUrl();
        assertThat(output.uri.toString()).isEqualTo(expected);
    }

    @Test
    public void simpleResizeWithEncryption() {
        Request input = build();
        Request output = secureTransformer.transformRequest(input);
        assertThat(output).isNotSameAs(input);
        assertThat(output.hasSize()).isFalse();
        String expected = Thumbor.create(PollexorRequestTransformerTest.HOST, PollexorRequestTransformerTest.KEY).buildImage(PollexorRequestTransformerTest.IMAGE).resize(50, 50).toUrl();
        assertThat(output.uri.toString()).isEqualTo(expected);
    }

    @Test
    public void simpleResizeWithCenterInsideAndEncryption() {
        Request input = build();
        Request output = secureTransformer.transformRequest(input);
        assertThat(output).isNotSameAs(input);
        assertThat(output.hasSize()).isFalse();
        assertThat(output.centerInside).isFalse();
        String expected = Thumbor.create(PollexorRequestTransformerTest.HOST, PollexorRequestTransformerTest.KEY).buildImage(PollexorRequestTransformerTest.IMAGE).resize(50, 50).fitIn().toUrl();
        assertThat(output.uri.toString()).isEqualTo(expected);
    }
}

