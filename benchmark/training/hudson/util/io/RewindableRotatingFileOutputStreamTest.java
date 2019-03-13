package hudson.util.io;


import hudson.FilePath;
import hudson.Functions;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.jvnet.hudson.test.Issue;


public class RewindableRotatingFileOutputStreamTest {
    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    @Test
    public void rotation() throws IOException, InterruptedException {
        File base = tmp.newFile("test.log");
        RewindableRotatingFileOutputStream os = new RewindableRotatingFileOutputStream(base, 3);
        PrintWriter w = new PrintWriter(os, true);
        for (int i = 0; i <= 4; i++) {
            w.println(("Content" + i));
            os.rewind();
        }
        w.println("Content5");
        w.close();
        Assert.assertEquals("Content5", new FilePath(base).readToString().trim());
        Assert.assertEquals("Content4", new FilePath(new File(((base.getPath()) + ".1"))).readToString().trim());
        Assert.assertEquals("Content3", new FilePath(new File(((base.getPath()) + ".2"))).readToString().trim());
        Assert.assertEquals("Content2", new FilePath(new File(((base.getPath()) + ".3"))).readToString().trim());
        Assert.assertFalse(new File(((base.getPath()) + ".4")).exists());
        os.deleteAll();
    }

    @Issue("JENKINS-16634")
    @Test
    public void deletedFolder() throws Exception {
        Assume.assumeFalse(("Windows does not allow deleting a directory with a " + "file open, so this case should never occur"), Functions.isWindows());
        File dir = tmp.newFolder("dir");
        File base = new File(dir, "x.log");
        RewindableRotatingFileOutputStream os = new RewindableRotatingFileOutputStream(base, 3);
        for (int i = 0; i < 2; i++) {
            FileUtils.deleteDirectory(dir);
            os.write('.');
            FileUtils.deleteDirectory(dir);
            os.write('.');
            FileUtils.deleteDirectory(dir);
            os.rewind();
        }
    }
}

