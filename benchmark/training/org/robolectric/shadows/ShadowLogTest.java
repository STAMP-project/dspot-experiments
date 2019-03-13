package org.robolectric.shadows;


import Log.ASSERT;
import Log.DEBUG;
import Log.ERROR;
import Log.INFO;
import Log.VERBOSE;
import Log.WARN;
import android.util.Log;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.shadows.ShadowLog.LogItem;

import static ShadowLog.stream;


@RunWith(AndroidJUnit4.class)
public class ShadowLogTest {
    @Test
    public void d_shouldLogAppropriately() {
        Log.d("tag", "msg");
        assertLogged(DEBUG, "tag", "msg", null);
    }

    @Test
    public void d_shouldLogAppropriately_withThrowable() {
        Throwable throwable = new Throwable();
        Log.d("tag", "msg", throwable);
        assertLogged(DEBUG, "tag", "msg", throwable);
    }

    @Test
    public void e_shouldLogAppropriately() {
        Log.e("tag", "msg");
        assertLogged(ERROR, "tag", "msg", null);
    }

    @Test
    public void e_shouldLogAppropriately_withThrowable() {
        Throwable throwable = new Throwable();
        Log.e("tag", "msg", throwable);
        assertLogged(ERROR, "tag", "msg", throwable);
    }

    @Test
    public void i_shouldLogAppropriately() {
        Log.i("tag", "msg");
        assertLogged(INFO, "tag", "msg", null);
    }

    @Test
    public void i_shouldLogAppropriately_withThrowable() {
        Throwable throwable = new Throwable();
        Log.i("tag", "msg", throwable);
        assertLogged(INFO, "tag", "msg", throwable);
    }

    @Test
    public void v_shouldLogAppropriately() {
        Log.v("tag", "msg");
        assertLogged(VERBOSE, "tag", "msg", null);
    }

    @Test
    public void v_shouldLogAppropriately_withThrowable() {
        Throwable throwable = new Throwable();
        Log.v("tag", "msg", throwable);
        assertLogged(VERBOSE, "tag", "msg", throwable);
    }

    @Test
    public void w_shouldLogAppropriately() {
        Log.w("tag", "msg");
        assertLogged(WARN, "tag", "msg", null);
    }

    @Test
    public void w_shouldLogAppropriately_withThrowable() {
        Throwable throwable = new Throwable();
        Log.w("tag", "msg", throwable);
        assertLogged(WARN, "tag", "msg", throwable);
    }

    @Test
    public void w_shouldLogAppropriately_withJustThrowable() {
        Throwable throwable = new Throwable();
        Log.w("tag", throwable);
        assertLogged(WARN, "tag", null, throwable);
    }

    @Test
    public void wtf_shouldLogAppropriately() {
        Log.wtf("tag", "msg");
        assertLogged(ASSERT, "tag", "msg", null);
    }

    @Test
    public void wtf_shouldLogAppropriately_withThrowable() {
        Throwable throwable = new Throwable();
        Log.wtf("tag", "msg", throwable);
        assertLogged(ASSERT, "tag", "msg", throwable);
    }

    @Test
    public void wtf_wtfIsFatalIsSet_shouldThrowTerribleFailure() {
        ShadowLog.setWtfIsFatal(true);
        Throwable throwable = new Throwable();
        try {
            Log.wtf("tag", "msg", throwable);
            Assert.fail("TerribleFailure should be thrown");
        } catch (ShadowLog e) {
            // pass
        }
        assertLogged(ASSERT, "tag", "msg", throwable);
    }

    @Test
    public void println_shouldLogAppropriately() {
        int len = Log.println(ASSERT, "tag", "msg");
        assertLogged(ASSERT, "tag", "msg", null);
        assertThat(len).isEqualTo(11);
    }

    @Test
    public void println_shouldLogNullTagAppropriately() {
        int len = Log.println(ASSERT, null, "msg");
        assertLogged(ASSERT, null, "msg", null);
        assertThat(len).isEqualTo(8);
    }

    @Test
    public void println_shouldLogNullMessageAppropriately() {
        int len = Log.println(ASSERT, "tag", null);
        assertLogged(ASSERT, "tag", null, null);
        assertThat(len).isEqualTo(8);
    }

    @Test
    public void println_shouldLogNullTagAndNullMessageAppropriately() {
        int len = Log.println(ASSERT, null, null);
        assertLogged(ASSERT, null, null, null);
        assertThat(len).isEqualTo(5);
    }

    @Test
    public void shouldLogToProvidedStream() throws Exception {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PrintStream old = stream;
        try {
            stream = new PrintStream(bos);
            Log.d("tag", "msg");
            assertThat(new String(bos.toByteArray(), StandardCharsets.UTF_8)).isEqualTo(("D/tag: msg" + (System.getProperty("line.separator"))));
            Log.w("tag", new RuntimeException());
            Assert.assertTrue(new String(bos.toByteArray(), StandardCharsets.UTF_8).contains("RuntimeException"));
        } finally {
            stream = old;
        }
    }

    @Test
    public void shouldLogAccordingToTag() throws Exception {
        ShadowLog.reset();
        Log.d("tag1", "1");
        Log.i("tag2", "2");
        Log.e("tag3", "3");
        Log.w("tag1", "4");
        Log.i("tag1", "5");
        Log.d("tag2", "6");
        List<LogItem> allItems = ShadowLog.getLogs();
        assertThat(allItems.size()).isEqualTo(6);
        int i = 1;
        for (LogItem item : allItems) {
            assertThat(item.msg).isEqualTo(Integer.toString(i));
            i++;
        }
        assertUniformLogsForTag("tag1", 3);
        assertUniformLogsForTag("tag2", 2);
        assertUniformLogsForTag("tag3", 1);
    }

    @Test
    public void infoIsDefaultLoggableLevel() throws Exception {
        PrintStream old = stream;
        stream = null;
        Assert.assertFalse(Log.isLoggable("FOO", VERBOSE));
        Assert.assertFalse(Log.isLoggable("FOO", DEBUG));
        Assert.assertTrue(Log.isLoggable("FOO", INFO));
        Assert.assertTrue(Log.isLoggable("FOO", WARN));
        Assert.assertTrue(Log.isLoggable("FOO", ERROR));
        Assert.assertTrue(Log.isLoggable("FOO", ASSERT));
        stream = old;
    }

    @Test
    public void shouldAlwaysBeLoggableIfStreamIsSpecified() throws Exception {
        PrintStream old = stream;
        stream = new PrintStream(new ByteArrayOutputStream());
        Assert.assertTrue(Log.isLoggable("FOO", VERBOSE));
        Assert.assertTrue(Log.isLoggable("FOO", DEBUG));
        Assert.assertTrue(Log.isLoggable("FOO", INFO));
        Assert.assertTrue(Log.isLoggable("FOO", WARN));
        Assert.assertTrue(Log.isLoggable("FOO", ERROR));
        Assert.assertTrue(Log.isLoggable("FOO", ASSERT));
        stream = old;
    }

    @Test
    public void identicalLogItemInstancesAreEqual() {
        LogItem item1 = new LogItem(Log.VERBOSE, "Foo", "Bar", null);
        LogItem item2 = new LogItem(Log.VERBOSE, "Foo", "Bar", null);
        assertThat(item1).isEqualTo(item2);
        assertThat(item2).isEqualTo(item1);
    }

    @Test
    public void logsAfterSetLoggable() {
        ShadowLog.setLoggable("Foo", VERBOSE);
        Assert.assertTrue(Log.isLoggable("Foo", DEBUG));
    }

    @Test
    public void noLogAfterSetLoggable() {
        PrintStream old = stream;
        stream = new PrintStream(new ByteArrayOutputStream());
        ShadowLog.setLoggable("Foo", DEBUG);
        Assert.assertFalse(Log.isLoggable("Foo", VERBOSE));
        stream = old;
    }

    @Test
    public void getLogs_shouldReturnCopy() {
        assertThat(ShadowLog.getLogs()).isNotSameAs(ShadowLog.getLogs());
        assertThat(ShadowLog.getLogs()).isEqualTo(ShadowLog.getLogs());
    }

    @Test
    public void getLogsForTag_empty() {
        assertThat(ShadowLog.getLogsForTag("non_existent")).isEmpty();
    }

    @Test
    public void clear() {
        assertThat(ShadowLog.getLogsForTag("tag1")).isEmpty();
        Log.d("tag1", "1");
        assertThat(ShadowLog.getLogsForTag("tag1")).isNotEmpty();
        ShadowLog.clear();
        assertThat(ShadowLog.getLogsForTag("tag1")).isEmpty();
        assertThat(ShadowLog.getLogs()).isEmpty();
    }
}

