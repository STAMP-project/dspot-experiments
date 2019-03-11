package snapshoot;


import fastdex.build.lib.snapshoot.file.FileNode;
import fastdex.build.lib.snapshoot.sourceset.JavaDirectorySnapshoot;
import fastdex.build.lib.snapshoot.sourceset.SourceSetDiffResultSet;
import fastdex.build.lib.snapshoot.sourceset.SourceSetSnapshoot;
import java.io.File;
import java.io.FileOutputStream;
import junit.framework.TestCase;
import org.junit.Test;


/**
 * Created by tong on 17/3/31.
 */
public class SourceSetSnapshootTest extends TestCase {
    String workDir;

    String source_set1;

    String source_set2;

    String source_set11;

    String source_set22;

    @Test
    public void testCreate() throws Throwable {
        if ((((!(isDir(source_set1))) || (!(isDir(source_set2)))) || (!(isDir(source_set11)))) || (!(isDir(source_set22)))) {
            System.err.println("Test-env not init!!");
            return;
        }
        SourceSetSnapshoot snapshoot = new SourceSetSnapshoot(new File(workDir), source_set1, source_set2);
        TestCase.assertEquals(snapshoot.directorySnapshootSet.size(), 2);
        SourceSetSnapshoot snapshoot2 = new SourceSetSnapshoot(new File(workDir), source_set1, source_set1);
        TestCase.assertEquals(snapshoot2.directorySnapshootSet.size(), 1);
    }

    @Test
    public void testDiffAddOneSourceSet() throws Throwable {
        if ((((!(isDir(source_set1))) || (!(isDir(source_set2)))) || (!(isDir(source_set11)))) || (!(isDir(source_set22)))) {
            System.err.println("Test-env not init!!");
            return;
        }
        SourceSetSnapshoot now = new SourceSetSnapshoot(new File(workDir), source_set1, source_set2);
        SourceSetSnapshoot old = new SourceSetSnapshoot(new File(workDir), source_set1);
        SourceSetDiffResultSet sourceSetResultSet = ((SourceSetDiffResultSet) (now.diff(old)));
        TestCase.assertTrue(sourceSetResultSet.isJavaFileChanged());
        System.out.println(sourceSetResultSet);
    }

    @Test
    public void testSave() throws Throwable {
        if ((((!(isDir(source_set1))) || (!(isDir(source_set2)))) || (!(isDir(source_set11)))) || (!(isDir(source_set22)))) {
            System.err.println("Test-env not init!!");
            return;
        }
        SourceSetSnapshoot now = new SourceSetSnapshoot(new File(workDir), source_set1, source_set2);
        now.serializeTo(new FileOutputStream(new File(workDir, "now.json")));
    }

    @Test
    public void testDiff1() throws Throwable {
        if ((((!(isDir(source_set1))) || (!(isDir(source_set2)))) || (!(isDir(source_set11)))) || (!(isDir(source_set22)))) {
            System.err.println("Test-env not init!!");
            return;
        }
        SourceSetSnapshoot now = new SourceSetSnapshoot(new File(workDir), source_set1);
        SourceSetSnapshoot old = new SourceSetSnapshoot(new File(workDir), source_set11);
        SourceSetDiffResultSet sourceSetResultSet = ((SourceSetDiffResultSet) (now.diff(old)));
        System.out.println(sourceSetResultSet.toString());
        sourceSetResultSet.serializeTo(new FileOutputStream(new File(workDir, "diff.json")));
    }

    @Test
    public void testDiff2() throws Throwable {
        if ((((!(isDir(source_set1))) || (!(isDir(source_set2)))) || (!(isDir(source_set11)))) || (!(isDir(source_set22)))) {
            System.err.println("Test-env not init!!");
            return;
        }
        SourceSetSnapshoot now = new SourceSetSnapshoot(new File(workDir), source_set1);
        now.serializeTo(new FileOutputStream(new File(workDir, "snapshoot.json")));
        SourceSetSnapshoot old = ((SourceSetSnapshoot) (SourceSetSnapshoot.load(new File(workDir, "snapshoot.json"), SourceSetSnapshoot.class)));
        JavaDirectorySnapshoot javaDirectorySnapshoot = new java.util.ArrayList(old.directorySnapshootSet).get(0);
        FileNode fileNode = new java.util.ArrayList(javaDirectorySnapshoot.nodes).get(0);
        fileNode.lastModified = System.currentTimeMillis();
        SourceSetDiffResultSet resultSet = ((SourceSetDiffResultSet) (now.diff(old)));
        TestCase.assertEquals(resultSet.changedJavaFileDiffInfos.size(), 1);
        System.out.println(resultSet);
    }

    @Test
    public void testDiff3() throws Throwable {
        if ((((!(isDir(source_set1))) || (!(isDir(source_set2)))) || (!(isDir(source_set11)))) || (!(isDir(source_set22)))) {
            System.err.println("Test-env not init!!");
            return;
        }
        SourceSetSnapshoot now = new SourceSetSnapshoot(new File("/Users/tong/Projects/fastdex/DevSample/app"), "");
    }
}

