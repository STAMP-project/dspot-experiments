package com.vaadin.data;


import com.vaadin.ui.TextField;
import javax.validation.Validation;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Vaadin Ltd
 */
public class BeanValidationTest {
    public static class Bean {
        private String property;

        public String getProperty() {
            return property;
        }

        public void setProperty(String property) {
            this.property = property;
        }
    }

    @Test
    public void binderWorksWithoutBeanValidationImpl() {
        // Just to make sure that it's available at the compilation time and in
        // runtime
        Assert.assertNotNull(Validation.class);
        Binder<BeanValidationTest.Bean> binder = new Binder(BeanValidationTest.Bean.class);
        TextField field = new TextField();
        binder.forField(field).bind("property");
        BeanValidationTest.Bean bean = new BeanValidationTest.Bean();
        binder.setBean(bean);
        field.setValue("foo");
        Assert.assertEquals(field.getValue(), bean.getProperty());
    }
}

