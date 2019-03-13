package com.nytimes.android.external.fs3.impl;


import RecordState.FRESH;
import RecordState.MISSING;
import RecordState.STALE;
import com.nytimes.android.external.fs3.filesystem.FileSystem;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okio.BufferedSource;
import org.junit.Test;


public class SimpleTest {
    private static final String testString1 = "aszfbW#$%#$^&*5 r7ytjdfbv!@#R$\n@!#$%2354 wtyebfsdv\n";

    private static final String testString2 = "#%^sdfvb#W%EtsdfbSER@#$%dsfb\nASRG \n #dsfvb \n";

    private static FileSystem fileSystem;

    @Test(expected = FileNotFoundException.class)
    public void loadFileNotFound() throws IOException {
        SimpleTest.fileSystem.read("/loadFileNotFound.txt").readUtf8();
    }

    @Test
    public void saveNload() throws IOException {
        diffMe("/flibber.txt", "/flibber.txt");
        diffMe("/blarg/flibber.txt", "/blarg/flibber.txt");
        diffMe("/blubber.txt", "blubber.txt");
        diffMe("/blarg/blubber.txt", "blarg/blubber.txt");
    }

    @Test
    public void delete() throws IOException {
        SimpleTest.fileSystem.write("/boo", SimpleTest.source(SimpleTest.testString1));
        assertThat(SimpleTest.fileSystem.read("/boo").readUtf8()).isEqualTo(SimpleTest.testString1);
        SimpleTest.fileSystem.delete("/boo");
        assertThat(SimpleTest.fileSystem.exists("/boo")).isFalse();
    }

    @Test
    public void testIsRecordStale() throws IOException {
        SimpleTest.fileSystem.write("/boo", SimpleTest.source(SimpleTest.testString1));
        assertThat(SimpleTest.fileSystem.read("/boo").readUtf8()).isEqualTo(SimpleTest.testString1);
        assertThat(SimpleTest.fileSystem.getRecordState(TimeUnit.MINUTES, 1, "/boo")).isEqualTo(FRESH);
        assertThat(SimpleTest.fileSystem.getRecordState(TimeUnit.MICROSECONDS, 1, "/boo")).isEqualTo(STALE);
        assertThat(SimpleTest.fileSystem.getRecordState(TimeUnit.DAYS, 1, "/notfound")).isEqualTo(MISSING);
    }

    @Test
    public void testDeleteWhileReading() throws IOException {
        SimpleTest.fileSystem.write("/boo", SimpleTest.source(SimpleTest.testString1));
        BufferedSource source = SimpleTest.fileSystem.read("/boo");
        SimpleTest.fileSystem.delete("/boo");
        assertThat(SimpleTest.fileSystem.exists("/boo")).isFalse();
        assertThat(source.readUtf8()).isEqualTo(SimpleTest.testString1);
        assertThat(SimpleTest.fileSystem.exists("/boo")).isFalse();
    }

    @Test
    public void deleteWhileReadingThenWrite() throws IOException {
        SimpleTest.fileSystem.write("/boo", SimpleTest.source(SimpleTest.testString1));
        BufferedSource source1 = SimpleTest.fileSystem.read("/boo");// open a source and hang onto it

        SimpleTest.fileSystem.delete("/boo");// now delete the file

        assertThat(SimpleTest.fileSystem.exists("/boo")).isFalse();// exists() should say it's gone even though

        // we still have a source to it
        SimpleTest.fileSystem.write("/boo", SimpleTest.source(SimpleTest.testString2));// and now un-delete it by writing a new version

        assertThat(SimpleTest.fileSystem.exists("/boo")).isTrue();// exists() should say it's back

        BufferedSource source2 = SimpleTest.fileSystem.read("/boo");// open another source and hang onto it

        SimpleTest.fileSystem.delete("/boo");// now delete the file *again*

        // the sources should have the correct data even though the file was deleted/re-written/deleted
        assertThat(source1.readUtf8()).isEqualTo(SimpleTest.testString1);
        assertThat(source2.readUtf8()).isEqualTo(SimpleTest.testString2);
        // now that the 2 sources have been fully read, you shouldn't be able to read it
        assertThat(SimpleTest.fileSystem.exists("/boo")).isFalse();
    }
}

