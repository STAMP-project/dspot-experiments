package com.reactnativenavigation.utils;


import Typeface.BOLD;
import Typeface.ITALIC;
import Typeface.NORMAL;
import com.reactnativenavigation.BaseTest;
import org.junit.Test;
import org.mockito.Mockito;


public class TypefaceLoaderTest extends BaseTest {
    @Test
    public void loadTypefaceNoAssets() {
        Context context = new MockContext();
        TypefaceLoader mockedLoader = Mockito.spy(new TypefaceLoader(context));
        Mockito.doReturn(null).when(mockedLoader).getTypefaceFromAssets("Helvetica-Bold");
        Typeface typeface = mockedLoader.getTypeFace("Helvetica-Bold");
        assertThat(typeface).isNotNull();
        assertThat(typeface.getStyle()).isEqualTo(BOLD);
    }

    @Test
    public void loadTypefaceWithAssets() {
        Context context = new MockContext();
        TypefaceLoader mockedLoader = Mockito.spy(new TypefaceLoader(context));
        Mockito.doReturn(Typeface.create("Helvetica-Italic", ITALIC)).when(mockedLoader).getTypefaceFromAssets("Helvetica-Italic");
        Typeface typeface = mockedLoader.getTypeFace("Helvetica-Italic");
        assertThat(typeface).isNotNull();
        assertThat(typeface.getStyle()).isEqualTo(ITALIC);
    }

    @Test
    public void loadTypefaceWrongName() {
        Context context = new MockContext();
        TypefaceLoader mockedLoader = Mockito.spy(new TypefaceLoader(context));
        Mockito.doReturn(null).when(mockedLoader).getTypefaceFromAssets("Some-name");
        Typeface typeface = mockedLoader.getTypeFace("Some-name");
        assertThat(typeface).isNotNull();
        assertThat(typeface.getStyle()).isEqualTo(NORMAL);
    }

    @Test
    public void loadTypefaceNull() {
        Context context = new MockContext();
        TypefaceLoader mockedLoader = Mockito.spy(new TypefaceLoader(context));
        Mockito.doReturn(null).when(mockedLoader).getTypefaceFromAssets(null);
        Typeface typeface = mockedLoader.getTypeFace(null);
        assertThat(typeface).isNull();
    }
}

