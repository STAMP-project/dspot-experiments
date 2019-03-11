package org.robolectric.shadows;


import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.R;
import org.robolectric.Shadows;

import static org.robolectric.R.drawable.an_image;


@RunWith(AndroidJUnit4.class)
public class ShadowImageViewTest {
    @Test
    public void getDrawableResourceId_shouldWorkWhenTheDrawableWasCreatedFromAResource() throws Exception {
        Resources resources = ApplicationProvider.getApplicationContext().getResources();
        Bitmap bitmap = BitmapFactory.decodeResource(resources, an_image);
        ImageView imageView = new ImageView(ApplicationProvider.getApplicationContext());
        imageView.setImageBitmap(bitmap);
        imageView.setImageResource(an_image);
        assertThat(Shadows.shadowOf(imageView.getDrawable()).getCreatedFromResId()).isEqualTo(an_image);
    }
}

