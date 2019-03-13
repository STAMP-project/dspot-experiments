package com.baeldung.cglib.proxy;


import java.lang.reflect.Method;
import junit.framework.TestCase;
import net.sf.cglib.beans.BeanGenerator;
import org.junit.Test;


public class BeanGeneratorIntegrationTest {
    @Test
    public void givenBeanCreator_whenAddProperty_thenClassShouldHaveFieldValue() throws Exception {
        // given
        BeanGenerator beanGenerator = new BeanGenerator();
        // when
        beanGenerator.addProperty("name", String.class);
        Object myBean = beanGenerator.create();
        Method setter = myBean.getClass().getMethod("setName", String.class);
        setter.invoke(myBean, "some string value set by a cglib");
        // then
        Method getter = myBean.getClass().getMethod("getName");
        TestCase.assertEquals("some string value set by a cglib", getter.invoke(myBean));
    }
}

