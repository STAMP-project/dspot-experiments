package cn.hutool.core.util;


import cn.hutool.core.collection.CollUtil;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/**
 * EnumUtil????
 *
 * @author looly
 */
public class EnumUtilTest {
    @Test
    public void getNamesTest() {
        List<String> names = EnumUtil.getNames(EnumUtilTest.TestEnum.class);
        Assert.assertEquals(CollUtil.newArrayList("TEST1", "TEST2", "TEST3"), names);
    }

    @Test
    public void getFieldValuesTest() {
        List<Object> types = EnumUtil.getFieldValues(EnumUtilTest.TestEnum.class, "type");
        Assert.assertEquals(CollUtil.newArrayList("type1", "type2", "type3"), types);
    }

    @Test
    public void getFieldNamesTest() {
        List<String> names = EnumUtil.getFieldNames(EnumUtilTest.TestEnum.class);
        Assert.assertEquals(CollUtil.newArrayList("type", "name"), names);
    }

    @Test
    public void likeValueOfTest() {
        EnumUtilTest.TestEnum value = EnumUtil.likeValueOf(EnumUtilTest.TestEnum.class, "type2");
        Assert.assertEquals(EnumUtilTest.TestEnum.TEST2, value);
    }

    @Test
    public void getEnumMapTest() {
        Map<String, EnumUtilTest.TestEnum> enumMap = EnumUtil.getEnumMap(EnumUtilTest.TestEnum.class);
        Assert.assertEquals(EnumUtilTest.TestEnum.TEST1, enumMap.get("TEST1"));
    }

    @Test
    public void getNameFieldMapTest() {
        Map<String, Object> enumMap = EnumUtil.getNameFieldMap(EnumUtilTest.TestEnum.class, "type");
        Assert.assertEquals("type1", enumMap.get("TEST1"));
    }

    public static enum TestEnum {

        TEST1("type1"),
        TEST2("type2"),
        TEST3("type3");
        private TestEnum(String type) {
            this.type = type;
        }

        private String type;

        private String name;

        public String getType() {
            return this.type;
        }

        public String getName() {
            return this.name;
        }
    }
}

