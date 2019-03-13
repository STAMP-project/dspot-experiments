package cn.hutool.core.io;


import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 * {@link FileUtil} ?????
 *
 * @author Looly
 */
public class FileUtilTest {
    @Test(expected = IllegalArgumentException.class)
    public void fileTest() {
        File file = FileUtil.file("d:/aaa", "bbb");
        Assert.assertNotNull(file);
        // ???????????????
        FileUtil.file(file, "../ccc");
    }

    @Test
    public void getAbsolutePathTest() {
        String absolutePath = FileUtil.getAbsolutePath("LICENSE-junit.txt");
        Assert.assertNotNull(absolutePath);
        String absolutePath2 = FileUtil.getAbsolutePath(absolutePath);
        Assert.assertNotNull(absolutePath2);
        Assert.assertEquals(absolutePath, absolutePath2);
    }

    @Test
    public void getAbsolutePathTest2() {
        String path = FileUtil.getAbsolutePath("??.xml");
        Assert.assertTrue(path.contains("??.xml"));
    }

    @Test
    public void copyTest() throws Exception {
        File srcFile = FileUtil.file("hutool.jpg");
        File destFile = FileUtil.file("hutool.copy.jpg");
        FileUtil.copy(srcFile, destFile, true);
        Assert.assertTrue(destFile.exists());
        Assert.assertEquals(srcFile.length(), destFile.length());
    }

    @Test
    public void equlasTest() {
        // ????????????
        File srcFile = FileUtil.file("d:/hutool.jpg");
        File destFile = FileUtil.file("d:/hutool.jpg");
        boolean equals = FileUtil.equals(srcFile, destFile);
        Assert.assertTrue(equals);
        // ?????????????
        File srcFile1 = FileUtil.file("hutool.jpg");
        File destFile1 = FileUtil.file("d:/hutool.jpg");
        boolean notEquals = FileUtil.equals(srcFile1, destFile1);
        Assert.assertFalse(notEquals);
    }

    @Test
    public void normalizeTest() {
        Assert.assertEquals("/foo/", FileUtil.normalize("/foo//"));
        Assert.assertEquals("/foo/", FileUtil.normalize("/foo/./"));
        Assert.assertEquals("/bar", FileUtil.normalize("/foo/../bar"));
        Assert.assertEquals("/bar/", FileUtil.normalize("/foo/../bar/"));
        Assert.assertEquals("/baz", FileUtil.normalize("/foo/../bar/../baz"));
        Assert.assertEquals("/", FileUtil.normalize("/../"));
        Assert.assertEquals("foo", FileUtil.normalize("foo/bar/.."));
        Assert.assertEquals("bar", FileUtil.normalize("foo/../../bar"));
        Assert.assertEquals("bar", FileUtil.normalize("foo/../bar"));
        Assert.assertEquals("/server/bar", FileUtil.normalize("//server/foo/../bar"));
        Assert.assertEquals("/bar", FileUtil.normalize("//server/../bar"));
        Assert.assertEquals("C:/bar", FileUtil.normalize("C:\\foo\\..\\bar"));
        Assert.assertEquals("C:/bar", FileUtil.normalize("C:\\..\\bar"));
        Assert.assertEquals("~/bar/", FileUtil.normalize("~/foo/../bar/"));
        Assert.assertEquals("bar", FileUtil.normalize("~/../bar"));
        Assert.assertEquals("bar", FileUtil.normalize("../../bar"));
        Assert.assertEquals("C:/bar", FileUtil.normalize("/C:/bar"));
    }

    @Test
    public void normalizeClassPathTest() {
        Assert.assertEquals("", FileUtil.normalize("classpath:"));
    }

    @Test
    public void doubleNormalizeTest() {
        String normalize = FileUtil.normalize("/aa/b:/c");
        String normalize2 = FileUtil.normalize(normalize);
        Assert.assertEquals("/aa/b:/c", normalize);
        Assert.assertEquals(normalize, normalize2);
    }

    @Test
    public void subPathTest() {
        Path path = Paths.get("/aaa/bbb/ccc/ddd/eee/fff");
        Path subPath = FileUtil.subPath(path, 5, 4);
        Assert.assertEquals("eee", subPath.toString());
        subPath = FileUtil.subPath(path, 0, 1);
        Assert.assertEquals("aaa", subPath.toString());
        subPath = FileUtil.subPath(path, 1, 0);
        Assert.assertEquals("aaa", subPath.toString());
        // ??
        subPath = FileUtil.subPath(path, (-1), 0);
        Assert.assertEquals("aaa/bbb/ccc/ddd/eee", subPath.toString().replace('\\', '/'));
        subPath = FileUtil.subPath(path, (-1), Integer.MAX_VALUE);
        Assert.assertEquals("fff", subPath.toString());
        subPath = FileUtil.subPath(path, (-1), path.getNameCount());
        Assert.assertEquals("fff", subPath.toString());
        subPath = FileUtil.subPath(path, (-2), (-3));
        Assert.assertEquals("ddd", subPath.toString());
    }

    @Test
    public void subPathTest2() {
        String subPath = FileUtil.subPath("d:/aaa/bbb/", "d:/aaa/bbb/ccc/");
        Assert.assertEquals("ccc/", subPath);
        subPath = FileUtil.subPath("d:/aaa/bbb", "d:/aaa/bbb/ccc/");
        Assert.assertEquals("ccc/", subPath);
        subPath = FileUtil.subPath("d:/aaa/bbb", "d:/aaa/bbb/ccc/test.txt");
        Assert.assertEquals("ccc/test.txt", subPath);
        subPath = FileUtil.subPath("d:/aaa/bbb/", "d:/aaa/bbb/ccc");
        Assert.assertEquals("ccc", subPath);
        subPath = FileUtil.subPath("d:/aaa/bbb", "d:/aaa/bbb/ccc");
        Assert.assertEquals("ccc", subPath);
        subPath = FileUtil.subPath("d:/aaa/bbb", "d:/aaa/bbb");
        Assert.assertEquals("", subPath);
        subPath = FileUtil.subPath("d:/aaa/bbb/", "d:/aaa/bbb");
        Assert.assertEquals("", subPath);
    }

    @Test
    public void getPathEle() {
        Path path = Paths.get("/aaa/bbb/ccc/ddd/eee/fff");
        Path ele = FileUtil.getPathEle(path, (-1));
        Assert.assertEquals("fff", ele.toString());
        ele = FileUtil.getPathEle(path, 0);
        Assert.assertEquals("aaa", ele.toString());
        ele = FileUtil.getPathEle(path, (-5));
        Assert.assertEquals("bbb", ele.toString());
        ele = FileUtil.getPathEle(path, (-6));
        Assert.assertEquals("aaa", ele.toString());
    }

    @Test
    public void listFileNamesTest() {
        List<String> names = FileUtil.listFileNames("classpath:");
        Assert.assertTrue(names.contains("hutool.jpg"));
        names = FileUtil.listFileNames("");
        Assert.assertTrue(names.contains("hutool.jpg"));
        names = FileUtil.listFileNames(".");
        Assert.assertTrue(names.contains("hutool.jpg"));
    }

    @Test
    public void getParentTest() {
        // ??Windows???
        if (FileUtil.isWindows()) {
            File parent = FileUtil.getParent(FileUtil.file("d:/aaa/bbb/cc/ddd"), 0);
            Assert.assertEquals(FileUtil.file("d:\\aaa\\bbb\\cc\\ddd"), parent);
            parent = FileUtil.getParent(FileUtil.file("d:/aaa/bbb/cc/ddd"), 1);
            Assert.assertEquals(FileUtil.file("d:\\aaa\\bbb\\cc"), parent);
            parent = FileUtil.getParent(FileUtil.file("d:/aaa/bbb/cc/ddd"), 2);
            Assert.assertEquals(FileUtil.file("d:\\aaa\\bbb"), parent);
            parent = FileUtil.getParent(FileUtil.file("d:/aaa/bbb/cc/ddd"), 4);
            Assert.assertEquals(FileUtil.file("d:\\"), parent);
            parent = FileUtil.getParent(FileUtil.file("d:/aaa/bbb/cc/ddd"), 5);
            Assert.assertNull(parent);
            parent = FileUtil.getParent(FileUtil.file("d:/aaa/bbb/cc/ddd"), 10);
            Assert.assertNull(parent);
        }
    }

    @Test
    public void lastIndexOfSeparatorTest() {
        String dir = "d:\\aaa\\bbb\\cc\\ddd";
        int index = FileUtil.lastIndexOfSeparator(dir);
        Assert.assertEquals(13, index);
        String file = "ddd.jpg";
        int index2 = FileUtil.lastIndexOfSeparator(file);
        Assert.assertEquals((-1), index2);
    }

    @Test
    public void getNameTest() {
        String path = "d:\\aaa\\bbb\\cc\\ddd\\";
        String name = FileUtil.getName(path);
        Assert.assertEquals("ddd", name);
        path = "d:\\aaa\\bbb\\cc\\ddd.jpg";
        name = FileUtil.getName(path);
        Assert.assertEquals("ddd.jpg", name);
    }

    @Test
    public void mainNameTest() {
        String path = "d:\\aaa\\bbb\\cc\\ddd\\";
        String mainName = FileUtil.mainName(path);
        Assert.assertEquals("ddd", mainName);
        path = "d:\\aaa\\bbb\\cc\\ddd";
        mainName = FileUtil.mainName(path);
        Assert.assertEquals("ddd", mainName);
        path = "d:\\aaa\\bbb\\cc\\ddd.jpg";
        mainName = FileUtil.mainName(path);
        Assert.assertEquals("ddd", mainName);
    }

    @Test
    public void extNameTest() {
        String path = "d:\\aaa\\bbb\\cc\\ddd\\";
        String mainName = FileUtil.extName(path);
        Assert.assertEquals("", mainName);
        path = "d:\\aaa\\bbb\\cc\\ddd";
        mainName = FileUtil.extName(path);
        Assert.assertEquals("", mainName);
        path = "d:\\aaa\\bbb\\cc\\ddd.jpg";
        mainName = FileUtil.extName(path);
        Assert.assertEquals("jpg", mainName);
    }

    @Test
    public void getWebRootTest() {
        File webRoot = FileUtil.getWebRoot();
        Assert.assertNotNull(webRoot);
        Assert.assertEquals("hutool-core", webRoot.getName());
    }

    @Test
    public void getMimeTypeTest() {
        String mimeType = FileUtil.getMimeType("test2Write.jpg");
        Assert.assertEquals("image/jpeg", mimeType);
    }
}

