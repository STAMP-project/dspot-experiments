package com.vaadin.v7.data.util;


import com.vaadin.v7.data.Property;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test BeanItem specific features.
 *
 * Only public API is tested, not the methods with package visibility.
 *
 * See also {@link PropertySetItemTest}, which tests the base class.
 */
public class BeanItemTest {
    @SuppressWarnings("unused")
    protected static class MySuperClass {
        private int superPrivate = 1;

        private int superPrivate2 = 2;

        protected double superProtected = 3.0;

        private double superProtected2 = 4.0;

        public boolean superPublic = true;

        private boolean superPublic2 = true;

        public int getSuperPrivate() {
            return superPrivate;
        }

        public void setSuperPrivate(int superPrivate) {
            this.superPrivate = superPrivate;
        }

        public double getSuperProtected() {
            return superProtected;
        }

        public void setSuperProtected(double superProtected) {
            this.superProtected = superProtected;
        }

        public boolean isSuperPublic() {
            return superPublic;
        }

        public void setSuperPublic(boolean superPublic) {
            this.superPublic = superPublic;
        }
    }

    protected static class MyClass extends BeanItemTest.MySuperClass {
        private String name;

        public int value = 123;

        public MyClass(String name) {
            this.name = name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setNoField(String name) {
        }

        public String getNoField() {
            return "no field backing this setter";
        }

        public String getName2() {
            return name;
        }
    }

    protected static class MyClass2 extends BeanItemTest.MyClass {
        public MyClass2(String name) {
            super(name);
        }

        @Override
        public void setName(String name) {
            super.setName((name + "2"));
        }

        @Override
        public String getName() {
            return (super.getName()) + "2";
        }

        @Override
        public String getName2() {
            return super.getName();
        }

        public void setName2(String name) {
            super.setName(name);
        }
    }

    protected static interface MySuperInterface {
        public int getSuper1();

        public void setSuper1(int i);

        public int getOverride();
    }

    protected static interface MySuperInterface2 {
        public int getSuper2();
    }

    protected static class Generic<T> {
        public T getProperty() {
            return null;
        }

        public void setProperty(T t) {
            throw new UnsupportedOperationException();
        }
    }

    protected static class SubClass extends BeanItemTest.Generic<String> {
        // Has a bridged method
        @Override
        public String getProperty() {
            return "";
        }

        // Has a bridged method
        @Override
        public void setProperty(String t) {
        }
    }

    protected static interface MySubInterface extends BeanItemTest.MySuperInterface , BeanItemTest.MySuperInterface2 {
        public int getSub();

        public void setSub(int i);

        @Override
        public int getOverride();

        public void setOverride(int i);
    }

    @Test
    public void testGetProperties() {
        BeanItem<BeanItemTest.MySuperClass> item = new BeanItem<BeanItemTest.MySuperClass>(new BeanItemTest.MySuperClass());
        Collection<?> itemPropertyIds = item.getItemPropertyIds();
        Assert.assertEquals(3, itemPropertyIds.size());
        Assert.assertTrue(itemPropertyIds.contains("superPrivate"));
        Assert.assertTrue(itemPropertyIds.contains("superProtected"));
        Assert.assertTrue(itemPropertyIds.contains("superPublic"));
    }

    @Test
    public void testGetSuperClassProperties() {
        BeanItem<BeanItemTest.MyClass> item = new BeanItem<BeanItemTest.MyClass>(new BeanItemTest.MyClass("bean1"));
        Collection<?> itemPropertyIds = item.getItemPropertyIds();
        Assert.assertEquals(6, itemPropertyIds.size());
        Assert.assertTrue(itemPropertyIds.contains("superPrivate"));
        Assert.assertTrue(itemPropertyIds.contains("superProtected"));
        Assert.assertTrue(itemPropertyIds.contains("superPublic"));
        Assert.assertTrue(itemPropertyIds.contains("name"));
        Assert.assertTrue(itemPropertyIds.contains("noField"));
        Assert.assertTrue(itemPropertyIds.contains("name2"));
    }

    @Test
    public void testOverridingProperties() {
        BeanItem<BeanItemTest.MyClass2> item = new BeanItem<BeanItemTest.MyClass2>(new BeanItemTest.MyClass2("bean2"));
        Collection<?> itemPropertyIds = item.getItemPropertyIds();
        Assert.assertEquals(6, itemPropertyIds.size());
        Assert.assertTrue(BeanItemTest.MyClass2.class.equals(item.getBean().getClass()));
        // check that name2 accessed via MyClass2, not MyClass
        Assert.assertFalse(item.getItemProperty("name2").isReadOnly());
    }

    @Test
    public void testGetInterfaceProperties() throws IllegalAccessException, IllegalArgumentException, NoSuchMethodException, SecurityException, InvocationTargetException {
        Method method = BeanItem.class.getDeclaredMethod("getPropertyDescriptors", Class.class);
        method.setAccessible(true);
        LinkedHashMap<String, VaadinPropertyDescriptor<Class>> propertyDescriptors = ((LinkedHashMap<String, VaadinPropertyDescriptor<Class>>) (method.invoke(null, BeanItemTest.MySuperInterface.class)));
        Assert.assertEquals(2, propertyDescriptors.size());
        Assert.assertTrue(propertyDescriptors.containsKey("super1"));
        Assert.assertTrue(propertyDescriptors.containsKey("override"));
        MethodProperty<?> property = ((MethodProperty<?>) (propertyDescriptors.get("override").createProperty(getClass())));
        Assert.assertTrue(property.isReadOnly());
    }

    @Test
    public void testGetSuperInterfaceProperties() throws IllegalAccessException, IllegalArgumentException, NoSuchMethodException, SecurityException, InvocationTargetException {
        Method method = BeanItem.class.getDeclaredMethod("getPropertyDescriptors", Class.class);
        method.setAccessible(true);
        LinkedHashMap<String, VaadinPropertyDescriptor<Class>> propertyDescriptors = ((LinkedHashMap<String, VaadinPropertyDescriptor<Class>>) (method.invoke(null, BeanItemTest.MySubInterface.class)));
        Assert.assertEquals(4, propertyDescriptors.size());
        Assert.assertTrue(propertyDescriptors.containsKey("sub"));
        Assert.assertTrue(propertyDescriptors.containsKey("super1"));
        Assert.assertTrue(propertyDescriptors.containsKey("super2"));
        Assert.assertTrue(propertyDescriptors.containsKey("override"));
        MethodProperty<?> property = ((MethodProperty<?>) (propertyDescriptors.get("override").createProperty(getClass())));
        Assert.assertFalse(property.isReadOnly());
    }

    @Test
    public void testPropertyExplicitOrder() {
        Collection<String> ids = new ArrayList<String>();
        ids.add("name");
        ids.add("superPublic");
        ids.add("name2");
        ids.add("noField");
        BeanItem<BeanItemTest.MyClass> item = new BeanItem<BeanItemTest.MyClass>(new BeanItemTest.MyClass("bean1"), ids);
        Iterator<?> it = item.getItemPropertyIds().iterator();
        Assert.assertEquals("name", it.next());
        Assert.assertEquals("superPublic", it.next());
        Assert.assertEquals("name2", it.next());
        Assert.assertEquals("noField", it.next());
        Assert.assertFalse(it.hasNext());
    }

    @Test
    public void testPropertyExplicitOrder2() {
        BeanItem<BeanItemTest.MyClass> item = new BeanItem<BeanItemTest.MyClass>(new BeanItemTest.MyClass("bean1"), new String[]{ "name", "superPublic", "name2", "noField" });
        Iterator<?> it = item.getItemPropertyIds().iterator();
        Assert.assertEquals("name", it.next());
        Assert.assertEquals("superPublic", it.next());
        Assert.assertEquals("name2", it.next());
        Assert.assertEquals("noField", it.next());
        Assert.assertFalse(it.hasNext());
    }

    @Test
    public void testPropertyBadPropertyName() {
        Collection<String> ids = new ArrayList<String>();
        ids.add("name3");
        ids.add("name");
        // currently silently ignores non-existent properties
        BeanItem<BeanItemTest.MyClass> item = new BeanItem<BeanItemTest.MyClass>(new BeanItemTest.MyClass("bean1"), ids);
        Iterator<?> it = item.getItemPropertyIds().iterator();
        Assert.assertEquals("name", it.next());
        Assert.assertFalse(it.hasNext());
    }

    @Test
    public void testRemoveProperty() {
        BeanItem<BeanItemTest.MyClass> item = new BeanItem<BeanItemTest.MyClass>(new BeanItemTest.MyClass("bean1"));
        Collection<?> itemPropertyIds = item.getItemPropertyIds();
        Assert.assertEquals(6, itemPropertyIds.size());
        item.removeItemProperty("name2");
        Assert.assertEquals(5, itemPropertyIds.size());
        Assert.assertFalse(itemPropertyIds.contains("name2"));
    }

    @Test
    public void testRemoveSuperProperty() {
        BeanItem<BeanItemTest.MyClass> item = new BeanItem<BeanItemTest.MyClass>(new BeanItemTest.MyClass("bean1"));
        Collection<?> itemPropertyIds = item.getItemPropertyIds();
        Assert.assertEquals(6, itemPropertyIds.size());
        item.removeItemProperty("superPrivate");
        Assert.assertEquals(5, itemPropertyIds.size());
        Assert.assertFalse(itemPropertyIds.contains("superPrivate"));
    }

    @Test
    public void testPropertyTypes() {
        BeanItem<BeanItemTest.MyClass> item = new BeanItem<BeanItemTest.MyClass>(new BeanItemTest.MyClass("bean1"));
        Assert.assertTrue(Integer.class.equals(item.getItemProperty("superPrivate").getType()));
        Assert.assertTrue(Double.class.equals(item.getItemProperty("superProtected").getType()));
        Assert.assertTrue(Boolean.class.equals(item.getItemProperty("superPublic").getType()));
        Assert.assertTrue(String.class.equals(item.getItemProperty("name").getType()));
    }

    @Test
    public void testPropertyReadOnly() {
        BeanItem<BeanItemTest.MyClass> item = new BeanItem<BeanItemTest.MyClass>(new BeanItemTest.MyClass("bean1"));
        Assert.assertFalse(item.getItemProperty("name").isReadOnly());
        Assert.assertTrue(item.getItemProperty("name2").isReadOnly());
    }

    @Test
    public void testCustomProperties() throws Exception {
        LinkedHashMap<String, VaadinPropertyDescriptor<BeanItemTest.MyClass>> propertyDescriptors = new LinkedHashMap<String, VaadinPropertyDescriptor<BeanItemTest.MyClass>>();
        propertyDescriptors.put("myname", new MethodPropertyDescriptor<BeanItemTest.MyClass>("myname", BeanItemTest.MyClass.class, BeanItemTest.MyClass.class.getDeclaredMethod("getName"), BeanItemTest.MyClass.class.getDeclaredMethod("setName", String.class)));
        BeanItemTest.MyClass instance = new BeanItemTest.MyClass("bean1");
        Constructor<BeanItem> constructor = BeanItem.class.getDeclaredConstructor(Object.class, Map.class);
        constructor.setAccessible(true);
        BeanItem<BeanItemTest.MyClass> item = constructor.newInstance(instance, propertyDescriptors);
        Assert.assertEquals(1, item.getItemPropertyIds().size());
        Assert.assertEquals("bean1", item.getItemProperty("myname").getValue());
    }

    @Test
    public void testAddRemoveProperty() throws Exception {
        MethodPropertyDescriptor<BeanItemTest.MyClass> pd = new MethodPropertyDescriptor<BeanItemTest.MyClass>("myname", BeanItemTest.MyClass.class, BeanItemTest.MyClass.class.getDeclaredMethod("getName"), BeanItemTest.MyClass.class.getDeclaredMethod("setName", String.class));
        BeanItem<BeanItemTest.MyClass> item = new BeanItem<BeanItemTest.MyClass>(new BeanItemTest.MyClass("bean1"));
        Assert.assertEquals(6, item.getItemPropertyIds().size());
        Assert.assertEquals(null, item.getItemProperty("myname"));
        item.addItemProperty("myname", pd.createProperty(item.getBean()));
        Assert.assertEquals(7, item.getItemPropertyIds().size());
        Assert.assertEquals("bean1", item.getItemProperty("myname").getValue());
        item.removeItemProperty("myname");
        Assert.assertEquals(6, item.getItemPropertyIds().size());
        Assert.assertEquals(null, item.getItemProperty("myname"));
    }

    @Test
    public void testOverriddenGenericMethods() {
        BeanItem<BeanItemTest.SubClass> item = new BeanItem<BeanItemTest.SubClass>(new BeanItemTest.SubClass());
        Property<?> property = item.getItemProperty("property");
        Assert.assertEquals("Unexpected class for property type", String.class, property.getType());
        Assert.assertEquals("Unexpected property value", "", property.getValue());
        // Should not be exception
        property.setValue(null);
    }

    @Test
    public void testChangeBean() {
        BeanItem<BeanItemTest.MyClass> beanItem = new BeanItem<BeanItemTest.MyClass>(new BeanItemTest.MyClass("Foo"));
        beanItem.setBean(new BeanItemTest.MyClass("Bar"));
        Assert.assertEquals("Bar", beanItem.getItemProperty("name").getValue());
    }

    @Test
    public void testChangeBeanNestedProperty() {
        BeanItem<BeanItemTest.MyClass> beanItem = new BeanItem<BeanItemTest.MyClass>(new BeanItemTest.MyClass("Foo"));
        beanItem.setBean(new BeanItemTest.MyClass("Bar"));
        Assert.assertEquals("Bar", beanItem.getItemProperty("name").getValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testChangeBeanToIncompatibleOne() {
        BeanItem<Object> beanItem = new BeanItem<Object>(new BeanItemTest.MyClass("Foo"));
        beanItem.setBean(new BeanItemTest.Generic<String>());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testChangeBeanToSubclass() {
        BeanItem<BeanItemTest.MyClass> beanItem = new BeanItem<BeanItemTest.MyClass>(new BeanItemTest.MyClass("Foo"));
        beanItem.setBean(new BeanItemTest.MyClass("Bar"));
        beanItem.setBean(new BeanItemTest.MyClass2("foo"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testChangeBeanToNull() {
        BeanItem<Object> beanItem = new BeanItem<Object>(new BeanItemTest.MyClass("Foo"));
        beanItem.setBean(null);
    }
}

