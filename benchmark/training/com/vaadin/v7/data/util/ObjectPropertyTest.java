package com.vaadin.v7.data.util;


import org.junit.Assert;
import org.junit.Test;


public class ObjectPropertyTest {
    public static class TestSuperClass {
        private String name;

        public TestSuperClass(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return getName();
        }
    }

    public static class TestSubClass extends ObjectPropertyTest.TestSuperClass {
        public TestSubClass(String name) {
            super(("Subclass: " + name));
        }
    }

    private ObjectPropertyTest.TestSuperClass super1 = new ObjectPropertyTest.TestSuperClass("super1");

    private ObjectPropertyTest.TestSubClass sub1 = new ObjectPropertyTest.TestSubClass("sub1");

    @Test
    public void testSimple() {
        ObjectProperty<ObjectPropertyTest.TestSuperClass> prop1 = new ObjectProperty<ObjectPropertyTest.TestSuperClass>(super1, ObjectPropertyTest.TestSuperClass.class);
        Assert.assertEquals("super1", prop1.getValue().getName());
        prop1 = new ObjectProperty<ObjectPropertyTest.TestSuperClass>(super1);
        Assert.assertEquals("super1", prop1.getValue().getName());
        ObjectProperty<ObjectPropertyTest.TestSubClass> prop2 = new ObjectProperty<ObjectPropertyTest.TestSubClass>(sub1, ObjectPropertyTest.TestSubClass.class);
        Assert.assertEquals("Subclass: sub1", prop2.getValue().getName());
        prop2 = new ObjectProperty<ObjectPropertyTest.TestSubClass>(sub1);
        Assert.assertEquals("Subclass: sub1", prop2.getValue().getName());
    }

    @Test
    public void testSetValueObjectSuper() {
        ObjectProperty<ObjectPropertyTest.TestSuperClass> prop = new ObjectProperty<ObjectPropertyTest.TestSuperClass>(super1, ObjectPropertyTest.TestSuperClass.class);
        Assert.assertEquals("super1", prop.getValue().getName());
        prop.setValue(new ObjectPropertyTest.TestSuperClass("super2"));
        Assert.assertEquals("super1", super1.getName());
        Assert.assertEquals("super2", prop.getValue().getName());
    }

    @Test
    public void testSetValueObjectSub() {
        ObjectProperty<ObjectPropertyTest.TestSubClass> prop = new ObjectProperty<ObjectPropertyTest.TestSubClass>(sub1, ObjectPropertyTest.TestSubClass.class);
        Assert.assertEquals("Subclass: sub1", prop.getValue().getName());
        prop.setValue(new ObjectPropertyTest.TestSubClass("sub2"));
        Assert.assertEquals("Subclass: sub1", sub1.getName());
        Assert.assertEquals("Subclass: sub2", prop.getValue().getName());
    }

    @Test
    public void testSetValueStringSuper() {
        ObjectProperty<ObjectPropertyTest.TestSuperClass> prop = new ObjectProperty<ObjectPropertyTest.TestSuperClass>(super1, ObjectPropertyTest.TestSuperClass.class);
        Assert.assertEquals("super1", prop.getValue().getName());
        prop.setValue(new ObjectPropertyTest.TestSuperClass("super2"));
        Assert.assertEquals("super1", super1.getName());
        Assert.assertEquals("super2", prop.getValue().getName());
    }

    @Test
    public void testSetValueStringSub() {
        ObjectProperty<ObjectPropertyTest.TestSubClass> prop = new ObjectProperty<ObjectPropertyTest.TestSubClass>(sub1, ObjectPropertyTest.TestSubClass.class);
        Assert.assertEquals("Subclass: sub1", prop.getValue().getName());
        prop.setValue(new ObjectPropertyTest.TestSubClass("sub2"));
        Assert.assertEquals("Subclass: sub1", sub1.getName());
        Assert.assertEquals("Subclass: sub2", prop.getValue().getName());
    }

    @Test
    public void testMixedGenerics() {
        ObjectProperty<ObjectPropertyTest.TestSuperClass> prop = new ObjectProperty<ObjectPropertyTest.TestSuperClass>(sub1);
        Assert.assertEquals("Subclass: sub1", prop.getValue().getName());
        Assert.assertEquals(prop.getType(), ObjectPropertyTest.TestSubClass.class);
        // create correct subclass based on the runtime type of the instance
        // given to ObjectProperty constructor, which is a subclass of the type
        // parameter
        prop.setValue(new ObjectPropertyTest.TestSubClass("sub2"));
        Assert.assertEquals("Subclass: sub2", prop.getValue().getName());
    }
}

