package com.alibaba.json.bvt.serializer;


import com.alibaba.fastjson.JSON;
import java.io.File;
import junit.framework.TestCase;
import org.junit.Assert;


public class FileTest extends TestCase {
    public void test_file() throws Exception {
        File file = new File("abc.txt");
        String text = JSON.toJSONString(file);
        Assert.assertEquals(JSON.toJSONString(file.getPath()), text);
        File file2 = JSON.parseObject(text, File.class);
        Assert.assertEquals(file, file2);
    }
}

