/**
 *
 */
package hudson;


import hudson.os.WindowsUtil;
import java.io.File;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.jvnet.hudson.test.For;
import org.jvnet.hudson.test.Issue;


// https://superuser.com/q/343074
@For(Util.class)
public class RemoveWindowsDirectoryJunctionTest {
    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    @Test
    @Issue("JENKINS-2995")
    public void testJunctionIsRemovedButNotContents() throws Exception {
        File subdir1 = tmp.newFolder("notJunction");
        File f1 = new File(subdir1, "testfile1.txt");
        Assert.assertTrue("Unable to create temporary file in notJunction directory", f1.createNewFile());
        File j1 = WindowsUtil.createJunction(new File(tmp.getRoot(), "test junction"), subdir1);
        Util.deleteRecursive(j1);
        Assert.assertFalse("Windows Junction should have been removed", j1.exists());
        Assert.assertTrue("Contents of Windows Junction should not be removed", f1.exists());
    }
}

