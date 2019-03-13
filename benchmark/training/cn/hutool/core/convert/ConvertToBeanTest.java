package cn.hutool.core.convert;


import cn.hutool.core.bean.BeanUtilTest;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/**
 * ??????????<br>
 * ?????
 *
 * @author Looly
 */
public class ConvertToBeanTest {
    @Test
    public void beanToMapTest() {
        BeanUtilTest.SubPerson person = new BeanUtilTest.SubPerson();
        person.setAge(14);
        person.setOpenid("11213232");
        person.setName("??A11");
        person.setSubName("sub??");
        Map<?, ?> map = Convert.convert(Map.class, person);
        Assert.assertEquals(map.get("name"), "??A11");
        Assert.assertEquals(map.get("age"), 14);
        Assert.assertEquals("11213232", map.get("openid"));
    }

    @Test
    public void mapToBeanTest() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", "88dc4b28-91b1-4a1a-bab5-444b795c7ecd");
        map.put("age", 14);
        map.put("openid", "11213232");
        map.put("name", "??A11");
        map.put("subName", "sub??");
        BeanUtilTest.SubPerson subPerson = Convert.convert(BeanUtilTest.SubPerson.class, map);
        Assert.assertEquals("88dc4b28-91b1-4a1a-bab5-444b795c7ecd", subPerson.getId().toString());
        Assert.assertEquals(14, subPerson.getAge());
        Assert.assertEquals("11213232", subPerson.getOpenid());
        Assert.assertEquals("??A11", subPerson.getName());
        Assert.assertEquals("11213232", subPerson.getOpenid());
    }
}

