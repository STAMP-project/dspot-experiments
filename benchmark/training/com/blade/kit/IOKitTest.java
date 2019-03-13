package com.blade.kit;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author biezhi
 * @unknown 2017/9/21
 */
public class IOKitTest {
    @Test
    public void testCloseQuietly() throws FileNotFoundException {
        InputStream ins = IOKitTest.class.getResourceAsStream("/application.properties");
        IOKit.closeQuietly(ins);
        IOKit.closeQuietly(null);
    }

    @Test
    public void testReadToString() throws IOException, URISyntaxException {
        String content = IOKit.readToString(IOKitTest.class.getResourceAsStream("/application.properties"));
        Assert.assertEquals(true, StringKit.isNotBlank(content));
        content = IOKit.readToString(Paths.get(IOKitTest.class.getResource("/application.properties").toURI()).toString());
        Assert.assertEquals(true, StringKit.isNotBlank(content));
    }

    @Test
    public void testCopyFile() throws IOException {
        IOKit.copyFile(new File(IOKitTest.class.getResource("/application.properties").getPath()), new File("./tmp.properties"));
        File tmp = new File("./tmp.properties");
        Assert.assertEquals(true, ((tmp.exists()) && (tmp.isFile())));
        tmp.delete();
    }
}

