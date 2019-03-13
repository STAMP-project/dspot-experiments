package cn.hutool.poi.excel.test;


import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/**
 * Excel??????
 *
 * @author Looly
 */
public class ExcelReadTest {
    @Test
    public void aliasTest() {
        ExcelReader reader = ExcelUtil.getReader(ResourceUtil.getStream("alias.xlsx"));
        // ???????????
        Object value = reader.readCellValue(1, 2);
        Assert.assertEquals("??", value);
        Map<String, String> headerAlias = MapUtil.newHashMap();
        headerAlias.put("????", "userName");
        headerAlias.put("??", "storageName");
        headerAlias.put("????", "checkPerm");
        headerAlias.put("??????", "allotAuditPerm");
        reader.setHeaderAlias(headerAlias);
        // ??list???????????
        List<List<Object>> read = reader.read();
        Assert.assertEquals("userName", read.get(0).get(0));
        Assert.assertEquals("storageName", read.get(0).get(1));
        Assert.assertEquals("checkPerm", read.get(0).get(2));
        Assert.assertEquals("allotAuditPerm", read.get(0).get(3));
        List<Map<String, Object>> readAll = reader.readAll();
        for (Map<String, Object> map : readAll) {
            Assert.assertTrue(map.containsKey("userName"));
            Assert.assertTrue(map.containsKey("storageName"));
            Assert.assertTrue(map.containsKey("checkPerm"));
            Assert.assertTrue(map.containsKey("allotAuditPerm"));
        }
    }

    @Test
    public void excelReadTestOfEmptyLine() {
        ExcelReader reader = ExcelUtil.getReader(ResourceUtil.getStream("priceIndex.xls"));
        List<Map<String, Object>> readAll = reader.readAll();
        Assert.assertEquals(4, readAll.size());
    }

    @Test
    public void excelReadTest() {
        ExcelReader reader = ExcelUtil.getReader(ResourceUtil.getStream("aaa.xlsx"));
        List<List<Object>> readAll = reader.read();
        // ??
        Assert.assertEquals("??", readAll.get(0).get(0));
        Assert.assertEquals("??", readAll.get(0).get(1));
        Assert.assertEquals("??", readAll.get(0).get(2));
        Assert.assertEquals("??", readAll.get(0).get(3));
        // ???
        Assert.assertEquals("??", readAll.get(1).get(0));
        Assert.assertEquals("?", readAll.get(1).get(1));
        Assert.assertEquals(11L, readAll.get(1).get(2));
        Assert.assertEquals(41.5, readAll.get(1).get(3));
    }

    @Test
    public void excelReadAsTextTest() {
        ExcelReader reader = ExcelUtil.getReader(ResourceUtil.getStream("aaa.xlsx"));
        Assert.assertNotNull(reader.readAsText(false));
    }

    @Test
    public void excel03ReadTest() {
        ExcelReader reader = ExcelUtil.getReader(ResourceUtil.getStream("aaa.xls"));
        List<List<Object>> readAll = reader.read();
        // for (List<Object> list : readAll) {
        // Console.log(list);
        // }
        // ??
        Assert.assertEquals("??", readAll.get(0).get(0));
        Assert.assertEquals("??", readAll.get(0).get(1));
        Assert.assertEquals("??", readAll.get(0).get(2));
        Assert.assertEquals("??", readAll.get(0).get(3));
        // ???
        Assert.assertEquals("??", readAll.get(1).get(0));
        Assert.assertEquals("?", readAll.get(1).get(1));
        Assert.assertEquals(11L, readAll.get(1).get(2));
        Assert.assertEquals(33.2, readAll.get(1).get(3));
    }

    @Test
    public void excel03ReadTest2() {
        ExcelReader reader = ExcelUtil.getReader(ResourceUtil.getStream("aaa.xls"), "????");
        List<List<Object>> readAll = reader.read();
        // ??
        Assert.assertEquals("??", readAll.get(0).get(0));
        Assert.assertEquals("??", readAll.get(0).get(1));
        Assert.assertEquals("??", readAll.get(0).get(2));
        Assert.assertEquals("????", readAll.get(0).get(3));
        Assert.assertEquals("????", readAll.get(0).get(4));
    }

    @Test
    public void excelReadToMapListTest() {
        ExcelReader reader = ExcelUtil.getReader(ResourceUtil.getStream("aaa.xlsx"));
        List<Map<String, Object>> readAll = reader.readAll();
        Assert.assertEquals("??", readAll.get(0).get("??"));
        Assert.assertEquals("?", readAll.get(0).get("??"));
        Assert.assertEquals(11L, readAll.get(0).get("??"));
    }

    @Test
    public void excelReadToBeanListTest() {
        ExcelReader reader = ExcelUtil.getReader(ResourceUtil.getStream("aaa.xlsx"));
        reader.addHeaderAlias("??", "name");
        reader.addHeaderAlias("??", "age");
        reader.addHeaderAlias("??", "gender");
        List<ExcelReadTest.Person> all = reader.readAll(ExcelReadTest.Person.class);
        Assert.assertEquals("??", all.get(0).getName());
        Assert.assertEquals("?", all.get(0).getGender());
        Assert.assertEquals(Integer.valueOf(11), all.get(0).getAge());
    }

    public static class Person {
        private String name;

        private String gender;

        private Integer age;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }

        @Override
        public String toString() {
            return ((((("Person [name=" + (name)) + ", gender=") + (gender)) + ", age=") + (age)) + "]";
        }
    }
}

