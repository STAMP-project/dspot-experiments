/**
 * The MIT License
 *
 * Copyright (c) 2004-2010, Sun Microsystems, Inc., Kohsuke Kawaguchi, Alan Harder
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package hudson;


import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.jvnet.hudson.test.Issue;


// TODO to be merged back in FilePathTest after security release
public class FilePathSEC904Test {
    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Test
    @Issue("SECURITY-904")
    public void isDescendant_regularFiles() throws IOException, InterruptedException {
        // root
        // /workspace
        // /sub
        // sub-regular.txt
        // regular.txt
        // /protected
        // secret.txt
        FilePath rootFolder = new FilePath(temp.newFolder("root"));
        FilePath workspaceFolder = rootFolder.child("workspace");
        FilePath subFolder = workspaceFolder.child("sub");
        FilePath protectedFolder = rootFolder.child("protected");
        FilePath regularFile = workspaceFolder.child("regular.txt");
        regularFile.write("regular-file", StandardCharsets.UTF_8.name());
        FilePath subRegularFile = subFolder.child("sub-regular.txt");
        subRegularFile.write("sub-regular-file", StandardCharsets.UTF_8.name());
        FilePath secretFile = protectedFolder.child("secret.txt");
        secretFile.write("secrets", StandardCharsets.UTF_8.name());
        Assert.assertTrue(workspaceFolder.isDescendant("."));
        Assert.assertTrue(workspaceFolder.isDescendant("regular.txt"));
        Assert.assertTrue(workspaceFolder.isDescendant("./regular.txt"));
        Assert.assertTrue(workspaceFolder.isDescendant("sub/sub-regular.txt"));
        Assert.assertTrue(workspaceFolder.isDescendant("sub//sub-regular.txt"));
        Assert.assertTrue(workspaceFolder.isDescendant("sub/../sub/sub-regular.txt"));
        Assert.assertTrue(workspaceFolder.isDescendant("./sub/../sub/sub-regular.txt"));
        // nonexistent files
        Assert.assertTrue(workspaceFolder.isDescendant("nonexistent.txt"));
        Assert.assertTrue(workspaceFolder.isDescendant("sub/nonexistent.txt"));
        Assert.assertTrue(workspaceFolder.isDescendant("nonexistent/nonexistent.txt"));
        Assert.assertFalse(workspaceFolder.isDescendant("../protected/nonexistent.txt"));
        Assert.assertFalse(workspaceFolder.isDescendant("../nonexistent/nonexistent.txt"));
        // the intermediate path "./.." goes out of workspace and so is refused
        Assert.assertFalse(workspaceFolder.isDescendant("./../workspace"));
        Assert.assertFalse(workspaceFolder.isDescendant("./../workspace/"));
        Assert.assertFalse(workspaceFolder.isDescendant("./../workspace/regular.txt"));
        Assert.assertFalse(workspaceFolder.isDescendant("../workspace/regular.txt"));
        Assert.assertFalse(workspaceFolder.isDescendant("./../../root/workspace/regular.txt"));
        // attempt to reach other folder
        Assert.assertFalse(workspaceFolder.isDescendant("../protected/secret.txt"));
        Assert.assertFalse(workspaceFolder.isDescendant("./../protected/secret.txt"));
    }

    @Test
    @Issue("SECURITY-904")
    public void isDescendant_regularSymlinks() throws IOException, InterruptedException {
        // root
        // /workspace
        // /a
        // a.txt
        // /b
        // _a => symlink to ../a
        // _atxt => symlink to ../a/a.txt
        // regular.txt
        // _nonexistent => symlink to nonexistent (nonexistent folder)
        // _nonexistentUp => symlink to ../nonexistent (nonexistent folder + illegal)
        // _protected => symlink to ../protected (illegal)
        // _secrettxt => symlink to ../protected/secret.txt (illegal)
        // /protected
        // secret.txt
        FilePath rootFolder = new FilePath(temp.newFolder("root"));
        FilePath workspaceFolder = rootFolder.child("workspace");
        FilePath aFolder = workspaceFolder.child("a");
        FilePath bFolder = workspaceFolder.child("b");
        FilePath protectedFolder = rootFolder.child("protected");
        FilePath regularFile = workspaceFolder.child("regular.txt");
        regularFile.write("regular-file", StandardCharsets.UTF_8.name());
        FilePath aFile = aFolder.child("a.txt");
        aFile.write("a-file", StandardCharsets.UTF_8.name());
        FilePath bFile = bFolder.child("a.txt");
        bFile.write("b-file", StandardCharsets.UTF_8.name());
        bFolder.child("_a").symlinkTo("../a", null);
        bFolder.child("_atxt").symlinkTo("../a/a.txt", null);
        // illegal symlinks
        workspaceFolder.child("_protected").symlinkTo("../protected", null);
        workspaceFolder.child("_nonexistent").symlinkTo("nonexistent", null);
        workspaceFolder.child("_nonexistentUp").symlinkTo("../nonexistent", null);
        workspaceFolder.child("_secrettxt").symlinkTo("../protected/secret.txt", null);
        FilePath secretFile = protectedFolder.child("secret.txt");
        secretFile.write("secrets", StandardCharsets.UTF_8.name());
        Assert.assertTrue(workspaceFolder.isDescendant("regular.txt"));
        Assert.assertTrue(workspaceFolder.isDescendant("_nonexistent"));
        Assert.assertTrue(workspaceFolder.isDescendant("a"));
        Assert.assertTrue(workspaceFolder.isDescendant("a/a.txt"));
        Assert.assertTrue(workspaceFolder.isDescendant("a/../a/a.txt"));
        Assert.assertTrue(workspaceFolder.isDescendant("b/../a/a.txt"));
        Assert.assertTrue(workspaceFolder.isDescendant("b"));
        Assert.assertTrue(workspaceFolder.isDescendant("./b"));
        Assert.assertTrue(workspaceFolder.isDescendant("b/_a/a.txt"));
        Assert.assertTrue(workspaceFolder.isDescendant("b/_a/../a/a.txt"));
        Assert.assertTrue(workspaceFolder.isDescendant("b/_atxt"));
        // nonexistent but illegal
        Assert.assertFalse(workspaceFolder.isDescendant("_nonexistentUp"));
        // illegal symlinks
        Assert.assertFalse(workspaceFolder.isDescendant("_protected"));
        Assert.assertFalse(workspaceFolder.isDescendant("_protected/"));
        Assert.assertFalse(workspaceFolder.isDescendant("_protected/secret.txt"));
        Assert.assertFalse(workspaceFolder.isDescendant("./_protected/secret.txt"));
        Assert.assertFalse(workspaceFolder.isDescendant("_secrettxt"));
        Assert.assertFalse(workspaceFolder.isDescendant("./_secrettxt"));
    }

    @Test
    @Issue("SECURITY-904")
    public void isDescendant_windowsSpecificSymlinks() throws Exception {
        Assume.assumeTrue(Functions.isWindows());
        // root
        // /workspace
        // /a
        // a.txt
        // /b
        // b.txt
        // _a => junction to ../a
        // regular.txt
        // _nonexistent => junction to nonexistent (nonexistent folder)
        // _nonexistentUp => junction to ../nonexistent (nonexistent and illegal)
        // _protected => junction to ../protected (illegal)
        // /protected
        // secret.txt
        File root = temp.newFolder("root");
        FilePath rootFolder = new FilePath(root);
        FilePath workspaceFolder = rootFolder.child("workspace");
        FilePath aFolder = workspaceFolder.child("a");
        FilePath bFolder = workspaceFolder.child("b");
        FilePath protectedFolder = rootFolder.child("protected");
        FilePath regularFile = workspaceFolder.child("regular.txt");
        regularFile.write("regular-file", StandardCharsets.UTF_8.name());
        FilePath aFile = aFolder.child("a.txt");
        aFile.write("a-file", StandardCharsets.UTF_8.name());
        FilePath bFile = bFolder.child("a.txt");
        bFile.write("b-file", StandardCharsets.UTF_8.name());
        createJunction(new File(root, "/workspace/b/_a"), new File(root, "/workspace/a"));
        createJunction(new File(root, "/workspace/_nonexistent"), new File(root, "/workspace/nonexistent"));
        createJunction(new File(root, "/workspace/_nonexistentUp"), new File(root, "/nonexistent"));
        createJunction(new File(root, "/workspace/_protected"), new File(root, "/protected"));
        FilePath secretFile = protectedFolder.child("secret.txt");
        secretFile.write("secrets", StandardCharsets.UTF_8.name());
        Assert.assertTrue(workspaceFolder.isDescendant("b"));
        Assert.assertTrue(workspaceFolder.isDescendant("b/_a/a.txt"));
        Assert.assertTrue(workspaceFolder.isDescendant("b\\_a\\a.txt"));
        Assert.assertTrue(workspaceFolder.isDescendant("b\\_a\\../a/a.txt"));
        Assert.assertTrue(workspaceFolder.isDescendant("b\\_a\\..\\a\\a.txt"));
        Assert.assertTrue(workspaceFolder.isDescendant(".\\b\\_a\\..\\a\\a.txt"));
        Assert.assertTrue(workspaceFolder.isDescendant("b/_a/../a/a.txt"));
        Assert.assertTrue(workspaceFolder.isDescendant("./b/_a/../a/a.txt"));
        // nonexistent and not proven illegal, the junction links are not resolved
        // by Util.resolveSymlinkToFile / neither Path.toRealPath under Windows
        Assert.assertTrue(workspaceFolder.isDescendant("_nonexistent"));
        Assert.assertTrue(workspaceFolder.isDescendant("_nonexistent/"));
        Assert.assertTrue(workspaceFolder.isDescendant("_nonexistent/.."));
        Assert.assertTrue(workspaceFolder.isDescendant("_nonexistentUp"));
        // illegal symlinks
        Assert.assertFalse(workspaceFolder.isDescendant("_protected"));
        Assert.assertFalse(workspaceFolder.isDescendant("_protected/../a"));
    }

    @Test(expected = IllegalArgumentException.class)
    @Issue("SECURITY-904")
    public void isDescendant_throwIfAbsolutePathGiven() throws Exception {
        FilePath rootFolder = new FilePath(temp.newFolder("root"));
        rootFolder.mkdirs();
        rootFolder.isDescendant(temp.newFile().getAbsolutePath());
    }

    @Test
    @Issue("SECURITY-904")
    public void isDescendant_worksEvenInSymbolicWorkspace() throws Exception {
        // root
        // /w
        // /_workspace => symlink to ../workspace
        // /workspace
        // /a
        // a.txt
        // /b
        // _a => symlink to ../a
        // _atxt => symlink to ../a/a.txt
        // regular.txt
        // _nonexistent => symlink to nonexistent (nonexistent folder)
        // _nonexistentUp => symlink to ../nonexistent (nonexistent folder + illegal)
        // _protected => symlink to ../protected (illegal)
        // _secrettxt => symlink to ../protected/secret.txt (illegal)
        // /protected
        // secret.txt
        FilePath rootFolder = new FilePath(temp.newFolder("root"));
        FilePath wFolder = rootFolder.child("w");
        FilePath workspaceFolder = rootFolder.child("workspace");
        FilePath aFolder = workspaceFolder.child("a");
        FilePath bFolder = workspaceFolder.child("b");
        FilePath protectedFolder = rootFolder.child("protected");
        FilePath regularFile = workspaceFolder.child("regular.txt");
        regularFile.write("regular-file", StandardCharsets.UTF_8.name());
        FilePath aFile = aFolder.child("a.txt");
        aFile.write("a-file", StandardCharsets.UTF_8.name());
        FilePath bFile = bFolder.child("a.txt");
        bFile.write("b-file", StandardCharsets.UTF_8.name());
        bFolder.child("_a").symlinkTo("../a", null);
        bFolder.child("_atxt").symlinkTo("../a/a.txt", null);
        // illegal symlinks
        workspaceFolder.child("_protected").symlinkTo("../protected", null);
        workspaceFolder.child("_protected2").symlinkTo("../../protected", null);
        workspaceFolder.child("_nonexistent").symlinkTo("nonexistent", null);
        workspaceFolder.child("_nonexistentUp").symlinkTo("../nonexistent", null);
        workspaceFolder.child("_secrettxt").symlinkTo("../protected/secret.txt", null);
        workspaceFolder.child("_secrettxt2").symlinkTo("../../protected/secret.txt", null);
        wFolder.mkdirs();
        FilePath symbolicWorkspace = wFolder.child("_w");
        symbolicWorkspace.symlinkTo("../workspace", null);
        FilePath secretFile = protectedFolder.child("secret.txt");
        secretFile.write("secrets", StandardCharsets.UTF_8.name());
        Assert.assertTrue(symbolicWorkspace.isDescendant("regular.txt"));
        Assert.assertTrue(symbolicWorkspace.isDescendant("_nonexistent"));
        Assert.assertTrue(symbolicWorkspace.isDescendant("a"));
        Assert.assertTrue(symbolicWorkspace.isDescendant("a/a.txt"));
        Assert.assertTrue(symbolicWorkspace.isDescendant("b"));
        Assert.assertTrue(symbolicWorkspace.isDescendant("b/_a/a.txt"));
        Assert.assertTrue(symbolicWorkspace.isDescendant("b/_atxt"));
        // nonexistent but illegal
        Assert.assertFalse(symbolicWorkspace.isDescendant("_nonexistentUp"));
        // illegal symlinks
        Assert.assertFalse(symbolicWorkspace.isDescendant("_protected"));
        Assert.assertFalse(symbolicWorkspace.isDescendant("_protected/"));
        Assert.assertFalse(symbolicWorkspace.isDescendant("_protected/secret.txt"));
        Assert.assertFalse(symbolicWorkspace.isDescendant("./_protected/secret.txt"));
        Assert.assertFalse(symbolicWorkspace.isDescendant("_protected2"));
        Assert.assertFalse(symbolicWorkspace.isDescendant("_protected2/secret.txt"));
        Assert.assertFalse(symbolicWorkspace.isDescendant("_secrettxt"));
        Assert.assertFalse(symbolicWorkspace.isDescendant("./_secrettxt"));
        Assert.assertFalse(symbolicWorkspace.isDescendant("_secrettxt2"));
    }
}

