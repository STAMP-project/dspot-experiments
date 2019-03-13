package cn.hutool.core.bean;


import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;


/**
 * Bean??????
 *
 * @author Looly
 */
public class BeanUtilTest {
    @Test
    public void isBeanTest() {
        // HashMap???setXXX?????bean
        boolean isBean = BeanUtil.isBean(HashMap.class);
        Assert.assertFalse(isBean);
    }

    @Test
    public void fillBeanTest() {
        BeanUtilTest.Person person = BeanUtil.fillBean(new BeanUtilTest.Person(), new cn.hutool.core.bean.copier.ValueProvider<String>() {
            @Override
            public Object value(String key, Type valueType) {
                switch (key) {
                    case "name" :
                        return "??";
                    case "age" :
                        return 18;
                }
                return null;
            }

            @Override
            public boolean containsKey(String key) {
                // ????key
                return true;
            }
        }, CopyOptions.create());
        Assert.assertEquals(person.getName(), "??");
        Assert.assertEquals(person.getAge(), 18);
    }

    @Test
    public void fillBeanWithMapIgnoreCaseTest() {
        HashMap<String, Object> map = CollectionUtil.newHashMap();
        map.put("Name", "Joe");
        map.put("aGe", 12);
        map.put("openId", "DFDFSDFWERWER");
        BeanUtilTest.SubPerson person = BeanUtil.fillBeanWithMapIgnoreCase(map, new BeanUtilTest.SubPerson(), false);
        Assert.assertEquals(person.getName(), "Joe");
        Assert.assertEquals(person.getAge(), 12);
        Assert.assertEquals(person.getOpenid(), "DFDFSDFWERWER");
    }

    @Test
    public void mapToBeanIgnoreCaseTest() {
        HashMap<String, Object> map = CollectionUtil.newHashMap();
        map.put("Name", "Joe");
        map.put("aGe", 12);
        BeanUtilTest.Person person = BeanUtil.mapToBeanIgnoreCase(map, BeanUtilTest.Person.class, false);
        Assert.assertEquals("Joe", person.getName());
        Assert.assertEquals(12, person.getAge());
    }

    @Test
    public void mapToBeanTest() {
        HashMap<String, Object> map = CollectionUtil.newHashMap();
        map.put("a_name", "Joe");
        map.put("b_age", 12);
        // ??
        HashMap<String, String> mapping = CollUtil.newHashMap();
        mapping.put("a_name", "name");
        mapping.put("b_age", "age");
        BeanUtilTest.Person person = BeanUtil.mapToBean(map, BeanUtilTest.Person.class, CopyOptions.create().setFieldMapping(mapping));
        Assert.assertEquals("Joe", person.getName());
        Assert.assertEquals(12, person.getAge());
    }

    @Test
    public void beanToMapTest() {
        BeanUtilTest.SubPerson person = new BeanUtilTest.SubPerson();
        person.setAge(14);
        person.setOpenid("11213232");
        person.setName("??A11");
        person.setSubName("sub??");
        Map<String, Object> map = BeanUtil.beanToMap(person);
        Assert.assertEquals("??A11", map.get("name"));
        Assert.assertEquals(14, map.get("age"));
        Assert.assertEquals("11213232", map.get("openid"));
        // static??????
        Assert.assertFalse(map.containsKey("SUBNAME"));
    }

    @Test
    public void beanToMapTest2() {
        BeanUtilTest.SubPerson person = new BeanUtilTest.SubPerson();
        person.setAge(14);
        person.setOpenid("11213232");
        person.setName("??A11");
        person.setSubName("sub??");
        Map<String, Object> map = BeanUtil.beanToMap(person, true, true);
        Assert.assertEquals("sub??", map.get("sub_name"));
    }

    @Test
    public void getPropertyTest() {
        BeanUtilTest.SubPerson person = new BeanUtilTest.SubPerson();
        person.setAge(14);
        person.setOpenid("11213232");
        person.setName("??A11");
        person.setSubName("sub??");
        Object name = BeanUtil.getProperty(person, "name");
        Assert.assertEquals("??A11", name);
        Object subName = BeanUtil.getProperty(person, "subName");
        Assert.assertEquals("sub??", subName);
    }

    @Test
    public void getPropertyDescriptorsTest() {
        HashSet<Object> set = CollUtil.newHashSet();
        PropertyDescriptor[] propertyDescriptors = BeanUtil.getPropertyDescriptors(BeanUtilTest.SubPerson.class);
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            set.add(propertyDescriptor.getName());
        }
        Assert.assertTrue(set.contains("age"));
        Assert.assertTrue(set.contains("id"));
        Assert.assertTrue(set.contains("name"));
        Assert.assertTrue(set.contains("openid"));
        Assert.assertTrue(set.contains("slow"));
        Assert.assertTrue(set.contains("subName"));
    }

    @Test
    public void copyPropertiesHasBooleanTest() {
        BeanUtilTest.SubPerson p1 = new BeanUtilTest.SubPerson();
        p1.setSlow(true);
        // ??boolean???isXXX??
        BeanUtilTest.SubPerson p2 = new BeanUtilTest.SubPerson();
        BeanUtil.copyProperties(p1, p2);
        Assert.assertTrue(p2.isSlow());
        // ??boolean????isXXX??
        BeanUtilTest.SubPerson2 p3 = new BeanUtilTest.SubPerson2();
        BeanUtil.copyProperties(p1, p3);
        Assert.assertTrue(p3.isSlow());
    }

    @Test
    public void copyPropertiesBeanToMapTest() {
        // ??BeanToMap
        BeanUtilTest.SubPerson p1 = new BeanUtilTest.SubPerson();
        p1.setSlow(true);
        p1.setName("??");
        p1.setSubName("sub??");
        Map<String, Object> map = MapUtil.newHashMap();
        BeanUtil.copyProperties(p1, map);
        Assert.assertTrue(((Boolean) (map.get("isSlow"))));
        Assert.assertEquals("??", map.get("name"));
        Assert.assertEquals("sub??", map.get("subName"));
    }

    @Test
    public void copyPropertiesMapToMapTest() {
        // ??MapToMap
        Map<String, Object> p1 = new HashMap<>();
        p1.put("isSlow", true);
        p1.put("name", "??");
        p1.put("subName", "sub??");
        Map<String, Object> map = MapUtil.newHashMap();
        BeanUtil.copyProperties(p1, map);
        Assert.assertTrue(((Boolean) (map.get("isSlow"))));
        Assert.assertEquals("??", map.get("name"));
        Assert.assertEquals("sub??", map.get("subName"));
    }

    @Test
    public void trimBeanStrFieldsTest() {
        BeanUtilTest.Person person = new BeanUtilTest.Person();
        person.setAge(1);
        person.setName("  ?? ");
        person.setOpenid(null);
        BeanUtilTest.Person person2 = BeanUtil.trimStrFields(person);
        // ???????
        Assert.assertEquals("??", person.getName());
        Assert.assertEquals("??", person2.getName());
    }

    // -----------------------------------------------------------------------------------------------------------------
    public static class SubPerson extends BeanUtilTest.Person {
        public static final String SUBNAME = "TEST";

        private UUID id;

        private String subName;

        private Boolean isSlow;

        public UUID getId() {
            return id;
        }

        public void setId(UUID id) {
            this.id = id;
        }

        public String getSubName() {
            return subName;
        }

        public void setSubName(String subName) {
            this.subName = subName;
        }

        public Boolean isSlow() {
            return isSlow;
        }

        public void setSlow(Boolean isSlow) {
            this.isSlow = isSlow;
        }
    }

    public static class SubPerson2 extends BeanUtilTest.Person {
        private String subName;

        // boolean????isXXX??
        private Boolean slow;

        public String getSubName() {
            return subName;
        }

        public void setSubName(String subName) {
            this.subName = subName;
        }

        public Boolean isSlow() {
            return slow;
        }

        public void setSlow(Boolean isSlow) {
            this.slow = isSlow;
        }
    }

    public static class Person {
        private String name;

        private int age;

        private String openid;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getOpenid() {
            return openid;
        }

        public void setOpenid(String openid) {
            this.openid = openid;
        }
    }
}

