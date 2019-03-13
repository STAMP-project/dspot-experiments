package org.robolectric.shadows;


import Typeface.BOLD;
import Typeface.ITALIC;
import Typeface.NORMAL;
import android.graphics.Typeface;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.io.File;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Shadows;


@RunWith(AndroidJUnit4.class)
public class ShadowTypefaceTest {
    private File fontFile;

    @Test
    public void create_withFamilyName_shouldCreateTypeface() {
        Typeface typeface = Typeface.create("roboto", BOLD);
        assertThat(typeface.getStyle()).isEqualTo(BOLD);
        assertThat(Shadows.shadowOf(typeface).getFontDescription().getFamilyName()).isEqualTo("roboto");
        assertThat(Shadows.shadowOf(typeface).getFontDescription().getStyle()).isEqualTo(BOLD);
    }

    @Test
    public void create_withFamily_shouldCreateTypeface() {
        Typeface typeface = Typeface.create(Typeface.create("roboto", BOLD), ITALIC);
        assertThat(typeface.getStyle()).isEqualTo(ITALIC);
        assertThat(Shadows.shadowOf(typeface).getFontDescription().getFamilyName()).isEqualTo("roboto");
        assertThat(Shadows.shadowOf(typeface).getFontDescription().getStyle()).isEqualTo(ITALIC);
    }

    @Test
    public void create_withoutFamily_shouldCreateTypeface() {
        Typeface typeface = Typeface.create(((Typeface) (null)), ITALIC);
        assertThat(typeface.getStyle()).isEqualTo(ITALIC);
        assertThat(Shadows.shadowOf(typeface).getFontDescription().getFamilyName()).isEqualTo(null);
        assertThat(Shadows.shadowOf(typeface).getFontDescription().getStyle()).isEqualTo(ITALIC);
    }

    @Test
    public void createFromFile_withFile_shouldCreateTypeface() {
        Typeface typeface = Typeface.createFromFile(fontFile);
        assertThat(typeface.getStyle()).isEqualTo(NORMAL);
        assertThat(Shadows.shadowOf(typeface).getFontDescription().getFamilyName()).isEqualTo("myFont.ttf");
    }

    @Test
    public void createFromFile_withPath_shouldCreateTypeface() {
        Typeface typeface = Typeface.createFromFile(fontFile.getPath());
        assertThat(typeface.getStyle()).isEqualTo(NORMAL);
        assertThat(Shadows.shadowOf(typeface).getFontDescription().getFamilyName()).isEqualTo("myFont.ttf");
        assertThat(Shadows.shadowOf(typeface).getFontDescription().getStyle()).isEqualTo(NORMAL);
    }

    @Test
    public void createFromAsset_shouldCreateTypeface() {
        Typeface typeface = Typeface.createFromAsset(ApplicationProvider.getApplicationContext().getAssets(), "myFont.ttf");
        assertThat(typeface.getStyle()).isEqualTo(NORMAL);
        assertThat(Shadows.shadowOf(typeface).getFontDescription().getFamilyName()).isEqualTo("myFont.ttf");
        assertThat(Shadows.shadowOf(typeface).getFontDescription().getStyle()).isEqualTo(NORMAL);
    }

    @Test
    public void createFromAsset_throwsExceptionWhenFontNotFound() throws Exception {
        try {
            Typeface.createFromAsset(ApplicationProvider.getApplicationContext().getAssets(), "nonexistent.ttf");
            Assert.fail("Expected exception");
        } catch (RuntimeException expected) {
            // Expected
        }
    }
}

