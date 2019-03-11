package org.robolectric.shadows;


import android.os.DropBoxManager;
import android.os.DropBoxManager.Entry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Unit tests for {@see ShadowDropboxManager}.
 */
@RunWith(AndroidJUnit4.class)
public class ShadowDropBoxManagerTest {
    private static final String TAG = "TAG";

    private static final String ANOTHER_TAG = "ANOTHER_TAG";

    private static final byte[] DATA = "HELLO WORLD".getBytes(StandardCharsets.UTF_8);

    private DropBoxManager manager;

    private ShadowDropBoxManager shadowDropBoxManager;

    @Test
    public void emptyDropbox() {
        assertThat(manager.getNextEntry(null, 0)).isNull();
    }

    @Test
    public void dataExpected() throws Exception {
        shadowDropBoxManager.addData(ShadowDropBoxManagerTest.TAG, 1, ShadowDropBoxManagerTest.DATA);
        Entry entry = manager.getNextEntry(null, 0);
        assertThat(entry).isNotNull();
        assertThat(entry.getTag()).isEqualTo(ShadowDropBoxManagerTest.TAG);
        assertThat(entry.getTimeMillis()).isEqualTo(1);
        assertThat(new BufferedReader(new InputStreamReader(entry.getInputStream(), StandardCharsets.UTF_8)).readLine()).isEqualTo(new String(ShadowDropBoxManagerTest.DATA));
        assertThat(entry.getText(100)).isEqualTo(new String(ShadowDropBoxManagerTest.DATA, StandardCharsets.UTF_8));
    }

    /**
     * Checks that we retrieve the first entry <em>after</em> the specified time.
     */
    @Test
    public void dataNotExpected_timestampSameAsEntry() throws Exception {
        shadowDropBoxManager.addData(ShadowDropBoxManagerTest.TAG, 1, ShadowDropBoxManagerTest.DATA);
        assertThat(manager.getNextEntry(null, 1)).isNull();
    }

    @Test
    public void dataNotExpected_timestampAfterEntry() throws Exception {
        shadowDropBoxManager.addData(ShadowDropBoxManagerTest.TAG, 1, ShadowDropBoxManagerTest.DATA);
        assertThat(manager.getNextEntry(null, 2)).isNull();
    }

    @Test
    public void dataNotExpected_wrongTag() throws Exception {
        shadowDropBoxManager.addData(ShadowDropBoxManagerTest.TAG, 1, ShadowDropBoxManagerTest.DATA);
        assertThat(manager.getNextEntry(ShadowDropBoxManagerTest.ANOTHER_TAG, 0)).isNull();
    }

    @Test
    public void dataExpectedWithSort() throws Exception {
        shadowDropBoxManager.addData(ShadowDropBoxManagerTest.TAG, 3, ShadowDropBoxManagerTest.DATA);
        shadowDropBoxManager.addData(ShadowDropBoxManagerTest.TAG, 1, new byte[]{ ((byte) (0)) });
        Entry entry = manager.getNextEntry(null, 2);
        assertThat(entry).isNotNull();
        assertThat(entry.getText(100)).isEqualTo(new String(ShadowDropBoxManagerTest.DATA, StandardCharsets.UTF_8));
        assertThat(entry.getTimeMillis()).isEqualTo(3);
    }

    @Test
    public void resetClearsData() throws Exception {
        shadowDropBoxManager.addData(ShadowDropBoxManagerTest.TAG, 1, ShadowDropBoxManagerTest.DATA);
        shadowDropBoxManager.reset();
        assertThat(manager.getNextEntry(null, 0)).isNull();
    }
}

