package cn.hutool.core.io;


import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.util.StrUtil;
import java.io.IOException;
import java.util.Properties;
import org.junit.Assert;
import org.junit.Test;


/**
 * ClassPath??????
 *
 * @author Looly
 */
public class ClassPathResourceTest {
    @Test
    public void readStringTest() throws IOException {
        ClassPathResource resource = new ClassPathResource("test.properties");
        String content = resource.readUtf8Str();
        Assert.assertTrue(StrUtil.isNotEmpty(content));
    }

    @Test
    public void readStringTest2() throws IOException {
        ClassPathResource resource = new ClassPathResource("/");
        String content = resource.readUtf8Str();
        Assert.assertTrue(StrUtil.isNotEmpty(content));
    }

    @Test
    public void readTest() throws IOException {
        ClassPathResource resource = new ClassPathResource("test.properties");
        Properties properties = new Properties();
        properties.load(resource.getStream());
        Assert.assertEquals("1", properties.get("a"));
        Assert.assertEquals("2", properties.get("b"));
    }

    @Test
    public void readFromJarTest() throws IOException {
        // ????junit?jar???LICENSE-junit.txt??
        final ClassPathResource resource = new ClassPathResource("LICENSE-junit.txt");
        String result = resource.readUtf8Str();
        Assert.assertNotNull(result);
        // ??????????????????????
        result = resource.readUtf8Str();
        Assert.assertNotNull(result);
    }

    @Test
    public void getAbsTest() {
        final ClassPathResource resource = new ClassPathResource("LICENSE-junit.txt");
        String absPath = resource.getAbsolutePath();
        Assert.assertTrue(absPath.contains("LICENSE-junit.txt"));
    }
}

