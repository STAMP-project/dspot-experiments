package com.vaadin.data;


import com.vaadin.ui.Grid;
import com.vaadin.ui.TextField;
import org.junit.Assert;
import org.junit.Test;


public class PropertyRetrospectionTest {
    @SuppressWarnings("unused")
    public static class InnerBean {
        private String innerString;

        public String getInnerString() {
            return innerString;
        }

        public void setInnerString(String innerString) {
            this.innerString = innerString;
        }
    }

    @SuppressWarnings("unused")
    public static class BeanOne {
        private String someString;

        private PropertyRetrospectionTest.InnerBean innerBean;

        public String getSomeString() {
            return someString;
        }

        public void setSomeString(String someString) {
            this.someString = someString;
        }

        public PropertyRetrospectionTest.InnerBean getInnerBean() {
            return innerBean;
        }

        public void setInnerBean(PropertyRetrospectionTest.InnerBean innerBean) {
            this.innerBean = innerBean;
        }
    }

    @SuppressWarnings("unused")
    public static class BeanTwo {
        private String someString;

        private PropertyRetrospectionTest.InnerBean innerBean;

        public String getSomeString() {
            return someString;
        }

        public void setSomeString(String someString) {
            this.someString = someString;
        }

        public PropertyRetrospectionTest.InnerBean getInnerBean() {
            return innerBean;
        }

        public void setInnerBean(PropertyRetrospectionTest.InnerBean innerBean) {
            this.innerBean = innerBean;
        }
    }

    @Test
    public void testGridBeanProperties() {
        Grid<PropertyRetrospectionTest.BeanOne> grid1 = new Grid(PropertyRetrospectionTest.BeanOne.class);
        Assert.assertEquals(2, BeanPropertySet.get(PropertyRetrospectionTest.BeanOne.class).getProperties().count());
        Assert.assertEquals(2, grid1.getColumns().size());
        grid1.addColumn("innerBean.innerString");
        Assert.assertEquals(3, grid1.getColumns().size());
        Assert.assertEquals(2, BeanPropertySet.get(PropertyRetrospectionTest.BeanOne.class).getProperties().count());
        Grid<PropertyRetrospectionTest.BeanOne> grid2 = new Grid(PropertyRetrospectionTest.BeanOne.class);
        Assert.assertEquals(2, BeanPropertySet.get(PropertyRetrospectionTest.BeanOne.class).getProperties().count());
        Assert.assertEquals(2, grid2.getColumns().size());
        grid2.addColumn("innerBean.innerString");
        Assert.assertEquals(3, grid2.getColumns().size());
        Assert.assertEquals(2, BeanPropertySet.get(PropertyRetrospectionTest.BeanOne.class).getProperties().count());
    }

    @Test
    public void testBinder() {
        Binder<PropertyRetrospectionTest.BeanTwo> binder1 = new Binder(PropertyRetrospectionTest.BeanTwo.class);
        Assert.assertEquals(2, BeanPropertySet.get(PropertyRetrospectionTest.BeanTwo.class).getProperties().count());
        binder1.forField(new TextField()).bind("innerBean.innerString");
        Assert.assertEquals(2, BeanPropertySet.get(PropertyRetrospectionTest.BeanTwo.class).getProperties().count());
        Binder<PropertyRetrospectionTest.BeanTwo> binder2 = new Binder(PropertyRetrospectionTest.BeanTwo.class);
        Assert.assertEquals(2, BeanPropertySet.get(PropertyRetrospectionTest.BeanTwo.class).getProperties().count());
        binder2.forField(new TextField()).bind("innerBean.innerString");
        Assert.assertEquals(2, BeanPropertySet.get(PropertyRetrospectionTest.BeanTwo.class).getProperties().count());
    }
}

