package org.robolectric.shadows;


import android.webkit.JsResult;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Shadows;
import org.robolectric.shadow.api.Shadow;


@RunWith(AndroidJUnit4.class)
public class ShadowJsResultTest {
    @Test
    public void shouldRecordCanceled() throws Exception {
        JsResult jsResult = Shadow.newInstanceOf(JsResult.class);
        Assert.assertFalse(Shadows.shadowOf(jsResult).wasCancelled());
        jsResult.cancel();
        Assert.assertTrue(Shadows.shadowOf(jsResult).wasCancelled());
    }
}

