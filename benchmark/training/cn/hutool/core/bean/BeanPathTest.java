package cn.hutool.core.bean;


import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/**
 * {@link BeanPath} ????
 *
 * @author looly
 */
public class BeanPathTest {
    Map<String, Object> tempMap;

    @Test
    public void beanPathTest1() {
        BeanPath pattern = new BeanPath("userInfo.examInfoDict[0].id");
        Assert.assertEquals("userInfo", pattern.patternParts.get(0));
        Assert.assertEquals("examInfoDict", pattern.patternParts.get(1));
        Assert.assertEquals("0", pattern.patternParts.get(2));
        Assert.assertEquals("id", pattern.patternParts.get(3));
    }

    @Test
    public void beanPathTest2() {
        BeanPath pattern = new BeanPath("[userInfo][examInfoDict][0][id]");
        Assert.assertEquals("userInfo", pattern.patternParts.get(0));
        Assert.assertEquals("examInfoDict", pattern.patternParts.get(1));
        Assert.assertEquals("0", pattern.patternParts.get(2));
        Assert.assertEquals("id", pattern.patternParts.get(3));
    }

    @Test
    public void beanPathTest3() {
        BeanPath pattern = new BeanPath("['userInfo']['examInfoDict'][0]['id']");
        Assert.assertEquals("userInfo", pattern.patternParts.get(0));
        Assert.assertEquals("examInfoDict", pattern.patternParts.get(1));
        Assert.assertEquals("0", pattern.patternParts.get(2));
        Assert.assertEquals("id", pattern.patternParts.get(3));
    }

    @Test
    public void getTest() {
        BeanPath pattern = BeanPath.create("userInfo.examInfoDict[0].id");
        Object result = pattern.get(tempMap);
        Assert.assertEquals(1, result);
    }

    @Test
    public void setTest() {
        BeanPath pattern = BeanPath.create("userInfo.examInfoDict[0].id");
        pattern.set(tempMap, 2);
        Object result = pattern.get(tempMap);
        Assert.assertEquals(2, result);
    }
}

