package com.vaadin.v7.tests.server.component.fieldgroup;


import com.vaadin.annotations.PropertyId;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.fieldgroup.BeanFieldGroup;
import com.vaadin.v7.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.v7.data.util.BeanItem;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.RichTextArea;
import com.vaadin.v7.ui.TextField;
import org.junit.Assert;
import org.junit.Test;


public class BeanFieldGroupTest {
    private static final String DEFAULT_FOR_BASIC_FIELD = "default";

    public static class MyBean {
        private String basicField = BeanFieldGroupTest.DEFAULT_FOR_BASIC_FIELD;

        private String anotherField;

        private BeanFieldGroupTest.MyNestedBean nestedBean = new BeanFieldGroupTest.MyNestedBean();

        public BeanFieldGroupTest.MyNestedBean getNestedBean() {
            return nestedBean;
        }

        /**
         *
         *
         * @return the basicField
         */
        public String getBasicField() {
            return basicField;
        }

        /**
         *
         *
         * @param basicField
         * 		the basicField to set
         */
        public void setBasicField(String basicField) {
            this.basicField = basicField;
        }

        /**
         *
         *
         * @return the anotherField
         */
        public String getAnotherField() {
            return anotherField;
        }

        /**
         *
         *
         * @param anotherField
         * 		the anotherField to set
         */
        public void setAnotherField(String anotherField) {
            this.anotherField = anotherField;
        }
    }

    public static class MyNestedBean {
        private String hello = "Hello world";

        public String getHello() {
            return hello;
        }
    }

    public static class ViewStub {
        TextField basicField = new TextField();

        @PropertyId("anotherField")
        TextField boundWithAnnotation = new TextField();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testStaticBindingHelper() {
        BeanFieldGroupTest.MyBean myBean = new BeanFieldGroupTest.MyBean();
        BeanFieldGroupTest.ViewStub viewStub = new BeanFieldGroupTest.ViewStub();
        BeanFieldGroup<BeanFieldGroupTest.MyBean> bindFields = BeanFieldGroup.bindFieldsUnbuffered(myBean, viewStub);
        Field<String> field = ((Field<String>) (bindFields.getField("basicField")));
        Assert.assertEquals(BeanFieldGroupTest.DEFAULT_FOR_BASIC_FIELD, myBean.basicField);
        field.setValue("Foo");
        Assert.assertEquals("Foo", myBean.basicField);
        field = ((Field<String>) (bindFields.getField("anotherField")));
        field.setValue("Foo");
        Assert.assertEquals("Foo", myBean.anotherField);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testStaticBufferedBindingHelper() throws CommitException {
        BeanFieldGroupTest.MyBean myBean = new BeanFieldGroupTest.MyBean();
        BeanFieldGroupTest.ViewStub viewStub = new BeanFieldGroupTest.ViewStub();
        BeanFieldGroup<BeanFieldGroupTest.MyBean> bindFields = BeanFieldGroup.bindFieldsBuffered(myBean, viewStub);
        Field<String> basicField = ((Field<String>) (bindFields.getField("basicField")));
        basicField.setValue("Foo");
        Assert.assertEquals(BeanFieldGroupTest.DEFAULT_FOR_BASIC_FIELD, myBean.basicField);
        Field<String> anotherField = ((Field<String>) (bindFields.getField("anotherField")));
        anotherField.setValue("Foo");
        Assert.assertNull(myBean.anotherField);
        bindFields.commit();
        Assert.assertEquals("Foo", myBean.basicField);
        Assert.assertEquals("Foo", myBean.anotherField);
    }

    @Test
    public void buildAndBindNestedProperty() {
        BeanFieldGroupTest.MyBean bean = new BeanFieldGroupTest.MyBean();
        BeanFieldGroup<BeanFieldGroupTest.MyBean> bfg = new BeanFieldGroup<BeanFieldGroupTest.MyBean>(BeanFieldGroupTest.MyBean.class);
        bfg.setItemDataSource(bean);
        Field<?> helloField = bfg.buildAndBind("Hello string", "nestedBean.hello");
        Assert.assertEquals(bean.nestedBean.hello, helloField.getValue().toString());
    }

    @Test
    public void buildAndBindNestedRichTextAreaProperty() {
        BeanFieldGroupTest.MyBean bean = new BeanFieldGroupTest.MyBean();
        BeanFieldGroup<BeanFieldGroupTest.MyBean> bfg = new BeanFieldGroup<BeanFieldGroupTest.MyBean>(BeanFieldGroupTest.MyBean.class);
        bfg.setItemDataSource(bean);
        RichTextArea helloField = bfg.buildAndBind("Hello string", "nestedBean.hello", RichTextArea.class);
        Assert.assertEquals(bean.nestedBean.hello, helloField.getValue().toString());
    }

    @Test
    public void setDataSource_nullBean_nullBeanIsSetInDataSource() {
        BeanFieldGroup<BeanFieldGroupTest.MyBean> group = new BeanFieldGroup<BeanFieldGroupTest.MyBean>(BeanFieldGroupTest.MyBean.class);
        group.setItemDataSource(((BeanFieldGroupTest.MyBean) (null)));
        BeanItem<BeanFieldGroupTest.MyBean> dataSource = group.getItemDataSource();
        Assert.assertNull("Data source is null for null bean", dataSource);
    }

    @Test
    public void setDataSource_nullItem_nullDataSourceIsSet() {
        BeanFieldGroup<BeanFieldGroupTest.MyBean> group = new BeanFieldGroup<BeanFieldGroupTest.MyBean>(BeanFieldGroupTest.MyBean.class);
        group.setItemDataSource(((Item) (null)));
        BeanItem<BeanFieldGroupTest.MyBean> dataSource = group.getItemDataSource();
        Assert.assertNull("Group returns not null data source", dataSource);
    }
}

