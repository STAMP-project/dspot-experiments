package com.blade.ioc;


import com.blade.ioc.bean.BeanDefine;
import com.blade.types.BladeBeanDefineType;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author biezhi
 * @unknown 2017/9/19
 */
public class BeanDefineTest {
    @Test
    public void testBeanDefine() {
        BeanDefine beanDefine = new BeanDefine(new BladeBeanDefineType());
        Class<?> type = beanDefine.getType();
        Assert.assertEquals(BladeBeanDefineType.class, type);
        Object bean = beanDefine.getBean();
        Assert.assertNotNull(bean);
        Assert.assertEquals(true, beanDefine.isSingleton());
        beanDefine.setSingleton(true);
        beanDefine.setType(BladeBeanDefineType.class);
        beanDefine.setBean(new BladeBeanDefineType());
        Assert.assertEquals(BladeBeanDefineType.class, type);
        Assert.assertNotNull(bean);
        Assert.assertEquals(true, beanDefine.isSingleton());
    }

    @Test
    public void testBeanDefine2() {
        BeanDefine beanDefine = new BeanDefine(new BladeBeanDefineType(), BladeBeanDefineType.class);
        Assert.assertEquals(BladeBeanDefineType.class, beanDefine.getType());
        beanDefine = new BeanDefine(new BladeBeanDefineType(), BladeBeanDefineType.class, true);
        Assert.assertEquals(BladeBeanDefineType.class, beanDefine.getType());
        Assert.assertEquals(true, beanDefine.isSingleton());
    }
}

