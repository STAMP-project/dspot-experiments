package org.kie.dmn.feel.lang.impl;


import java.util.Set;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.kie.dmn.feel.lang.CompositeType;
import org.kie.dmn.feel.lang.FEELType;
import org.kie.dmn.feel.model.Person;


public class JavaBackedTypeTest {
    @Test
    public void testPerson() {
        CompositeType personType = ((CompositeType) (JavaBackedType.of(Person.class)));
        Set<String> personProperties = personType.getFields().keySet();
        Assert.assertThat(personProperties, Matchers.hasItem("home address"));
        Assert.assertThat(personProperties, Matchers.hasItem("address"));
        // for consistency and to fix possible hierarchy resolution problem, we add the property also as per standard JavaBean specs.
        Assert.assertThat(personProperties, Matchers.hasItem("homeAddress"));
    }

    @FEELType
    public static class MyPojoNoMethodAnn {
        private String a;

        private String b;

        public MyPojoNoMethodAnn(String a, String b) {
            super();
            this.a = a;
            this.b = b;
        }

        public String getA() {
            return a;
        }

        public void setA(String a) {
            this.a = a;
        }

        public String getB() {
            return b;
        }

        public void setB(String b) {
            this.b = b;
        }

        // used for tests.
        @SuppressWarnings("unused")
        private String getBPrivate() {
            return b;
        }
    }

    @Test
    public void testMyPojoNoMethodAnn() {
        CompositeType personType = ((CompositeType) (JavaBackedType.of(JavaBackedTypeTest.MyPojoNoMethodAnn.class)));
        Set<String> personProperties = personType.getFields().keySet();
        Assert.assertThat(personProperties, Matchers.hasItem("a"));
        Assert.assertThat(personProperties, Matchers.hasItem("b"));
        // should not include private methods
        Assert.assertThat(personProperties, Matchers.not(Matchers.hasItem("bPrivate")));
        // should not include methods which are actually Object methods.
        Assert.assertThat(personProperties, Matchers.not(Matchers.hasItem("equals")));
        Assert.assertThat(personProperties, Matchers.not(Matchers.hasItem("wait")));
    }
}

