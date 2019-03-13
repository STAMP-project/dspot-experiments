package android.content.res;


import AssetManager.ACCESS_BUFFER;
import ParcelFileDescriptor.MODE_READ_ONLY;
import android.os.ParcelFileDescriptor;
import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;
import com.google.common.io.CharStreams;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.robolectric.annotation.internal.DoNotInstrument;


/**
 * Compatibility test for {@link AssetManager}
 */
@DoNotInstrument
@RunWith(AndroidJUnit4.class)
public class AssetManagerTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private AssetManager assetManager;

    private static final Charset UTF_8 = Charset.forName("UTF-8");

    @Test
    public void assetsPathListing() throws IOException {
        assertThat(assetManager.list("")).asList().containsAllOf("assetsHome.txt", "robolectric.png", "myFont.ttf");
        assertThat(assetManager.list("testing")).asList().contains("hello.txt");
        assertThat(assetManager.list("bogus-dir")).isEmpty();
    }

    @Test
    public void open_shouldOpenFile() throws IOException {
        final String contents = CharStreams.toString(new InputStreamReader(assetManager.open("assetsHome.txt"), AssetManagerTest.UTF_8));
        assertThat(contents).isEqualTo("assetsHome!");
    }

    @Test
    public void open_withAccessMode_shouldOpenFile() throws IOException {
        final String contents = CharStreams.toString(new InputStreamReader(assetManager.open("assetsHome.txt", ACCESS_BUFFER), AssetManagerTest.UTF_8));
        assertThat(contents).isEqualTo("assetsHome!");
    }

    @Test
    public void open_shouldProvideFileDescriptor() throws Exception {
        File file = new File((((InstrumentationRegistry.getTargetContext().getFilesDir()) + (File.separator)) + "open_shouldProvideFileDescriptor.txt"));
        FileOutputStream output = new FileOutputStream(file);
        output.write("hi".getBytes());
        ParcelFileDescriptor parcelFileDescriptor = ParcelFileDescriptor.open(file, MODE_READ_ONLY);
        AssetFileDescriptor assetFileDescriptor = new AssetFileDescriptor(parcelFileDescriptor, 0, "hi".getBytes().length);
        assertThat(CharStreams.toString(new InputStreamReader(assetFileDescriptor.createInputStream(), AssetManagerTest.UTF_8))).isEqualTo("hi");
        assertThat(assetFileDescriptor.getLength()).isEqualTo(2);
        assetFileDescriptor.close();
    }
}

