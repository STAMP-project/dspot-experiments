package android.graphics;


import R.drawable.an_image;
import android.content.res.Resources;
import android.graphics.BitmapFactory.Options;
import androidx.test.runner.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.internal.DoNotInstrument;


/**
 * Compatibility test for {@link BitmapFactory}
 */
@DoNotInstrument
@RunWith(AndroidJUnit4.class)
public class BitmapFactoryTest {
    // height and width of start.jpg
    private static final int START_HEIGHT = 53;

    private static final int START_WIDTH = 64;

    private Resources resources;

    @Test
    public void decodeResource() {
        Bitmap bitmap = BitmapFactory.decodeResource(resources, an_image);
        assertThat(bitmap.getHeight()).isEqualTo(BitmapFactoryTest.START_HEIGHT);
        assertThat(bitmap.getWidth()).isEqualTo(BitmapFactoryTest.START_WIDTH);
    }

    @Test
    public void testDecodeByteArray1() {
        byte[] array = obtainArray();
        Options options1 = new Options();
        options1.inScaled = false;
        Bitmap b = BitmapFactory.decodeByteArray(array, 0, array.length, options1);
        assertThat(b).isNotNull();
        // Test the bitmap size
        assertThat(b.getHeight()).isEqualTo(BitmapFactoryTest.START_HEIGHT);
        assertThat(b.getWidth()).isEqualTo(BitmapFactoryTest.START_WIDTH);
    }

    @Test
    public void testDecodeByteArray2() {
        byte[] array = obtainArray();
        Bitmap b = BitmapFactory.decodeByteArray(array, 0, array.length);
        assertThat(b).isNotNull();
        // Test the bitmap size
        assertThat(b.getHeight()).isEqualTo(BitmapFactoryTest.START_HEIGHT);
        assertThat(b.getWidth()).isEqualTo(BitmapFactoryTest.START_WIDTH);
    }
}

