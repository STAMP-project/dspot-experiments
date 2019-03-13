package com.amaze.filemanager.activities;


import Intent.CATEGORY_DEFAULT;
import RuntimeEnvironment.application;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.widget.TextView;
import com.amaze.filemanager.BuildConfig;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowContentResolver;
import org.robolectric.shadows.multidex.ShadowMultiDex;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, shadows = { ShadowMultiDex.class })
public class TextEditorActivityTest {
    private final String fileContents = "fsdfsdfs";

    private TextView text;

    @Test
    public void testOpenFileUri() throws IOException {
        File file = simulateFile();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.fromFile(file));
        generateActivity(intent);
        Assert.assertThat(text.getText().toString(), Matchers.is(((fileContents) + "\n")));
    }

    @Test
    public void testOpenContentUri() throws Exception {
        Uri uri = Uri.parse("content://foo.bar.test.streamprovider/temp/thisisatest.txt");
        ContentResolver contentResolver = application.getContentResolver();
        ShadowContentResolver shadowContentResolver = Shadows.shadowOf(contentResolver);
        shadowContentResolver.registerInputStream(uri, new ByteArrayInputStream(fileContents.getBytes("UTF-8")));
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(CATEGORY_DEFAULT);
        intent.setType("text/plain");
        intent.setData(uri);
        generateActivity(intent);
        Assert.assertEquals(fileContents, text.getText().toString().trim());
    }
}

