package org.baeldung.hamcrest;


import java.io.File;
import org.junit.Assert;
import org.junit.Test;


/* @Test
public final void whenVerifyingFilePath_thenCorrect() {
File file = new File("src/test/resources/test1.in");

assertThat(file, aFileWithCanonicalPath(containsString("src/test/resources")));
assertThat(file, aFileWithAbsolutePath(containsString("src/test/resources")));
}
 */
public class HamcrestFileUnitTest {
    @Test
    public final void whenVerifyingFileName_thenCorrect() {
        File file = new File("src/test/resources/test1.in");
        Assert.assertThat(file, aFileNamed(equalToIgnoringCase("test1.in")));
    }

    @Test
    public final void whenVerifyingFileOrDirExist_thenCorrect() {
        File file = new File("src/test/resources/test1.in");
        File dir = new File("src/test/resources");
        Assert.assertThat(file, anExistingFile());
        Assert.assertThat(dir, anExistingDirectory());
        Assert.assertThat(file, anExistingFileOrDirectory());
        Assert.assertThat(dir, anExistingFileOrDirectory());
    }

    @Test
    public final void whenVerifyingFileIsReadableAndWritable_thenCorrect() {
        File file = new File("src/test/resources/test1.in");
        Assert.assertThat(file, aReadableFile());
        Assert.assertThat(file, aWritableFile());
    }

    @Test
    public final void whenVerifyingFileSize_thenCorrect() {
        File file = new File("src/test/resources/test1.in");
        Assert.assertThat(file, aFileWithSize(11));
        Assert.assertThat(file, aFileWithSize(greaterThan(1L)));
    }
}

