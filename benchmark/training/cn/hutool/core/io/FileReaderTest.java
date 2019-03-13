package cn.hutool.core.io;


import cn.hutool.core.io.file.FileReader;
import org.junit.Assert;
import org.junit.Test;


/**
 * ??????
 *
 * @author Looly
 */
public class FileReaderTest {
    @Test
    public void fileReaderTest() {
        FileReader fileReader = new FileReader("test.properties");
        String result = fileReader.readString();
        Assert.assertNotNull(result);
    }
}

