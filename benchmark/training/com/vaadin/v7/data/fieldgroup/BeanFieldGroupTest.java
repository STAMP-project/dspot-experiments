package com.vaadin.v7.data.fieldgroup;


import org.junit.Assert;
import org.junit.Test;


public class BeanFieldGroupTest {
    class Main {
        private String mainField;

        public String getMainField() {
            return mainField;
        }

        public void setMainField(String mainField) {
            this.mainField = mainField;
        }
    }

    class Sub1 extends BeanFieldGroupTest.Main {
        private Integer sub1Field;

        public Integer getSub1Field() {
            return sub1Field;
        }

        public void setSub1Field(Integer sub1Field) {
            this.sub1Field = sub1Field;
        }
    }

    class Sub2 extends BeanFieldGroupTest.Sub1 {
        private boolean sub2field;

        public boolean isSub2field() {
            return sub2field;
        }

        public void setSub2field(boolean sub2field) {
            this.sub2field = sub2field;
        }
    }

    @Test
    public void propertyTypeWithoutItem() {
        BeanFieldGroup<BeanFieldGroupTest.Sub2> s = new BeanFieldGroup<BeanFieldGroupTest.Sub2>(BeanFieldGroupTest.Sub2.class);
        Assert.assertEquals(boolean.class, s.getPropertyType("sub2field"));
        Assert.assertEquals(Integer.class, s.getPropertyType("sub1Field"));
        Assert.assertEquals(String.class, s.getPropertyType("mainField"));
    }
}

