package cn.hutool.core.io;


import java.io.File;
import org.junit.Assert;
import org.junit.Test;


/**
 * ??????????
 *
 * @author Looly
 */
public class FileTypeUtilTest {
    @Test
    public void fileTypeUtilTest() {
        File file = FileUtil.file("hutool.jpg");
        String type = FileTypeUtil.getType(file);
        Assert.assertEquals("jpg", type);
        FileTypeUtil.putFileType("ffd8ffe000104a464946", "new_jpg");
        String newType = FileTypeUtil.getType(file);
        Assert.assertEquals("new_jpg", newType);
    }
}

