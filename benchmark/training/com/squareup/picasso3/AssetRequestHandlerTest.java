package com.squareup.picasso3;


import android.content.Context;
import android.net.Uri;
import java.io.IOException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class AssetRequestHandlerTest {
    @Mock
    Context context;

    @Test
    public void truncatesFilePrefix() throws IOException {
        Uri uri = Uri.parse("file:///android_asset/foo/bar.png");
        Request request = build();
        String actual = AssetRequestHandler.getFilePath(request);
        assertThat(actual).isEqualTo("foo/bar.png");
    }
}

