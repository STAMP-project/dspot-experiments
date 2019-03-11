package com.querydsl.core.util;


import com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.Test;


public class BeanMapTest {
    private BeanMap beanMap;

    @Test
    public void size() {
        Assert.assertEquals(4, beanMap.size());
    }

    @Test
    public void clear() {
        beanMap.clear();
        Assert.assertEquals(4, beanMap.size());
    }

    @Test
    public void primitives() {
        beanMap.put("id", 5);
        Assert.assertEquals(5, ((Entity) (beanMap.getBean())).getId());
    }

    @Test
    public void beanMap() {
        Assert.assertEquals(0, new BeanMap().size());
    }

    @Test
    public void beanMapObject() {
        Assert.assertEquals(4, new BeanMap(new Entity()).size());
    }

    @Test
    public void toString_() {
        Assert.assertEquals("BeanMap<null>", new BeanMap().toString());
    }

    @Test
    public void clone_() throws CloneNotSupportedException {
        Assert.assertEquals(beanMap, beanMap.clone());
    }

    @Test
    public void containsKeyString() {
        Assert.assertTrue(beanMap.containsKey("id"));
    }

    @Test
    public void getString() {
        beanMap.put("firstName", "John");
        Assert.assertEquals("John", beanMap.get("firstName"));
    }

    @Test
    public void keySet() {
        Assert.assertEquals(Sets.newHashSet("id", "class", "firstName", "lastName"), beanMap.keySet());
    }

    @Test
    public void entrySet() {
        beanMap.put("firstName", "John");
        Assert.assertFalse(beanMap.entrySet().isEmpty());
    }

    @Test
    public void getBean() {
        Assert.assertEquals(Entity.class, beanMap.getBean().getClass());
    }

    @Test
    public void setBean() {
        Entity entity = new Entity();
        beanMap.setBean(entity);
        Assert.assertTrue((entity == (beanMap.getBean())));
    }
}

