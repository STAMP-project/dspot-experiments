package pl.droidsonroids.gif;


import org.junit.Test;


public class GifDrawableBuilderTest {
    @Test
    public void testOptionsAndSampleSizeConflict() throws Exception {
        GifDrawableBuilder builder = new GifDrawableBuilder();
        GifOptions options = new GifOptions();
        builder.options(options);
        builder.sampleSize(3);
        assertThat(options.inSampleSize).isEqualTo(((char) (1)));
    }
}

