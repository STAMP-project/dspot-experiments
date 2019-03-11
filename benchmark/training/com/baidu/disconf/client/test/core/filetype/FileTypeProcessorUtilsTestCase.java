package com.baidu.disconf.client.test.core.filetype;


import SupportFileTypeEnum.ANY;
import SupportFileTypeEnum.PROPERTIES;
import SupportFileTypeEnum.XML;
import com.baidu.disconf.client.core.filetype.FileTypeProcessorUtils;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author knightliao
 */
public class FileTypeProcessorUtilsTestCase {
    @Test
    public void getKvMapTest() {
        try {
            Map<String, Object> map = FileTypeProcessorUtils.getKvMap(PROPERTIES, ("testProperties" + ".properties"));
            System.out.println(map.toString());
            Assert.assertEquals(map.get("staticvar2"), "100");
            Assert.assertEquals(map.get("staticvar"), "50");
        } catch (Exception e) {
            Assert.assertTrue(false);
        }
        try {
            Map<String, Object> map = FileTypeProcessorUtils.getKvMap(XML, "testXml.xml");
            System.out.println(map.toString());
            Assert.assertEquals(0, map.keySet().size());
        } catch (Exception e) {
            Assert.assertTrue(false);
        }
        try {
            Map<String, Object> map = FileTypeProcessorUtils.getKvMap(ANY, "testJson.json");
            System.out.println(map.toString());
            Assert.assertEquals(0, map.keySet().size());
        } catch (Exception e) {
            Assert.assertTrue(false);
        }
    }
}

