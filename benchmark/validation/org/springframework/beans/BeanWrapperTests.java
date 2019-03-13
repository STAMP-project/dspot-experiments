/**
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.beans;


import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.tests.sample.beans.TestBean;


/**
 * Specific {@link BeanWrapperImpl} tests.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Alef Arendsen
 * @author Arjen Poutsma
 * @author Chris Beams
 * @author Dave Syer
 */
public class BeanWrapperTests extends AbstractPropertyAccessorTests {
    @Test
    public void setterDoesNotCallGetter() {
        BeanWrapperTests.GetterBean target = new BeanWrapperTests.GetterBean();
        BeanWrapper accessor = createAccessor(target);
        accessor.setPropertyValue("name", "tom");
        Assert.assertEquals("tom", target.getAliasedName());
        Assert.assertEquals("tom", accessor.getPropertyValue("aliasedName"));
    }

    @Test
    public void getterSilentlyFailWithOldValueExtraction() {
        BeanWrapperTests.GetterBean target = new BeanWrapperTests.GetterBean();
        BeanWrapper accessor = createAccessor(target);
        accessor.setExtractOldValueForEditor(true);// This will call the getter

        accessor.setPropertyValue("name", "tom");
        Assert.assertEquals("tom", target.getAliasedName());
        Assert.assertEquals("tom", accessor.getPropertyValue("aliasedName"));
    }

    @Test
    public void aliasedSetterThroughDefaultMethod() {
        BeanWrapperTests.GetterBean target = new BeanWrapperTests.GetterBean();
        BeanWrapper accessor = createAccessor(target);
        accessor.setPropertyValue("aliasedName", "tom");
        Assert.assertEquals("tom", target.getAliasedName());
        Assert.assertEquals("tom", accessor.getPropertyValue("aliasedName"));
    }

    @Test
    public void setValidAndInvalidPropertyValuesShouldContainExceptionDetails() {
        TestBean target = new TestBean();
        String newName = "tony";
        String invalidTouchy = ".valid";
        try {
            BeanWrapper accessor = createAccessor(target);
            MutablePropertyValues pvs = new MutablePropertyValues();
            pvs.addPropertyValue(new PropertyValue("age", "foobar"));
            pvs.addPropertyValue(new PropertyValue("name", newName));
            pvs.addPropertyValue(new PropertyValue("touchy", invalidTouchy));
            accessor.setPropertyValues(pvs);
            Assert.fail("Should throw exception when everything is valid");
        } catch (PropertyBatchUpdateException ex) {
            Assert.assertTrue("Must contain 2 exceptions", ((ex.getExceptionCount()) == 2));
            // Test validly set property matches
            Assert.assertTrue("Valid set property must stick", target.getName().equals(newName));
            Assert.assertTrue("Invalid set property must retain old value", ((target.getAge()) == 0));
            Assert.assertTrue("New value of dodgy setter must be available through exception", ex.getPropertyAccessException("touchy").getPropertyChangeEvent().getNewValue().equals(invalidTouchy));
        }
    }

    @Test
    public void checkNotWritablePropertyHoldPossibleMatches() {
        TestBean target = new TestBean();
        try {
            BeanWrapper accessor = createAccessor(target);
            accessor.setPropertyValue("ag", "foobar");
            Assert.fail("Should throw exception on invalid property");
        } catch (NotWritablePropertyException ex) {
            // expected
            Assert.assertEquals(1, ex.getPossibleMatches().length);
            Assert.assertEquals("age", ex.getPossibleMatches()[0]);
        }
    }

    // Can't be shared; there is no such thing as a read-only field
    @Test
    public void setReadOnlyMapProperty() {
        BeanWrapperTests.TypedReadOnlyMap map = new BeanWrapperTests.TypedReadOnlyMap(Collections.singletonMap("key", new TestBean()));
        BeanWrapperTests.TypedReadOnlyMapClient target = new BeanWrapperTests.TypedReadOnlyMapClient();
        BeanWrapper accessor = createAccessor(target);
        accessor.setPropertyValue("map", map);
    }

    @Test
    public void notWritablePropertyExceptionContainsAlternativeMatch() {
        BeanWrapperTests.IntelliBean target = new BeanWrapperTests.IntelliBean();
        BeanWrapper bw = createAccessor(target);
        try {
            bw.setPropertyValue("names", "Alef");
        } catch (NotWritablePropertyException ex) {
            Assert.assertNotNull("Possible matches not determined", ex.getPossibleMatches());
            Assert.assertEquals("Invalid amount of alternatives", 1, ex.getPossibleMatches().length);
        }
    }

    @Test
    public void notWritablePropertyExceptionContainsAlternativeMatches() {
        BeanWrapperTests.IntelliBean target = new BeanWrapperTests.IntelliBean();
        BeanWrapper bw = createAccessor(target);
        try {
            bw.setPropertyValue("mystring", "Arjen");
        } catch (NotWritablePropertyException ex) {
            Assert.assertNotNull("Possible matches not determined", ex.getPossibleMatches());
            Assert.assertEquals("Invalid amount of alternatives", 3, ex.getPossibleMatches().length);
        }
    }

    // Can't be shared: no type mismatch with a field
    @Test
    public void setPropertyTypeMismatch() {
        BeanWrapperTests.PropertyTypeMismatch target = new BeanWrapperTests.PropertyTypeMismatch();
        BeanWrapper accessor = createAccessor(target);
        accessor.setPropertyValue("object", "a String");
        Assert.assertEquals("a String", target.value);
        Assert.assertTrue(((target.getObject()) == 8));
        Assert.assertEquals(8, accessor.getPropertyValue("object"));
    }

    @Test
    public void propertyDescriptors() {
        TestBean target = new TestBean();
        target.setSpouse(new TestBean());
        BeanWrapper accessor = createAccessor(target);
        accessor.setPropertyValue("name", "a");
        accessor.setPropertyValue("spouse.name", "b");
        Assert.assertEquals("a", target.getName());
        Assert.assertEquals("b", target.getSpouse().getName());
        Assert.assertEquals("a", accessor.getPropertyValue("name"));
        Assert.assertEquals("b", accessor.getPropertyValue("spouse.name"));
        Assert.assertEquals(String.class, accessor.getPropertyDescriptor("name").getPropertyType());
        Assert.assertEquals(String.class, accessor.getPropertyDescriptor("spouse.name").getPropertyType());
    }

    @Test
    public void getPropertyWithOptional() {
        BeanWrapperTests.GetterWithOptional target = new BeanWrapperTests.GetterWithOptional();
        TestBean tb = new TestBean("x");
        BeanWrapper accessor = createAccessor(target);
        accessor.setPropertyValue("object", tb);
        Assert.assertSame(tb, target.value);
        Assert.assertSame(tb, target.getObject().get());
        Assert.assertSame(tb, ((Optional<String>) (accessor.getPropertyValue("object"))).get());
        Assert.assertEquals("x", target.value.getName());
        Assert.assertEquals("x", target.getObject().get().getName());
        Assert.assertEquals("x", accessor.getPropertyValue("object.name"));
        accessor.setPropertyValue("object.name", "y");
        Assert.assertSame(tb, target.value);
        Assert.assertSame(tb, target.getObject().get());
        Assert.assertSame(tb, ((Optional<String>) (accessor.getPropertyValue("object"))).get());
        Assert.assertEquals("y", target.value.getName());
        Assert.assertEquals("y", target.getObject().get().getName());
        Assert.assertEquals("y", accessor.getPropertyValue("object.name"));
    }

    @Test
    public void getPropertyWithOptionalAndAutoGrow() {
        BeanWrapperTests.GetterWithOptional target = new BeanWrapperTests.GetterWithOptional();
        BeanWrapper accessor = createAccessor(target);
        accessor.setAutoGrowNestedPaths(true);
        accessor.setPropertyValue("object.name", "x");
        Assert.assertEquals("x", target.value.getName());
        Assert.assertEquals("x", target.getObject().get().getName());
        Assert.assertEquals("x", accessor.getPropertyValue("object.name"));
    }

    @Test
    public void incompletelyQuotedKeyLeadsToPropertyException() {
        TestBean target = new TestBean();
        try {
            BeanWrapper accessor = createAccessor(target);
            accessor.setPropertyValue("[']", "foobar");
            Assert.fail("Should throw exception on invalid property");
        } catch (NotWritablePropertyException ex) {
            Assert.assertNull(ex.getPossibleMatches());
        }
    }

    private interface BaseProperty {
        default String getAliasedName() {
            return getName();
        }

        String getName();
    }

    @SuppressWarnings("unused")
    private interface AliasedProperty extends BeanWrapperTests.BaseProperty {
        default void setAliasedName(String name) {
            setName(name);
        }

        void setName(String name);
    }

    @SuppressWarnings("unused")
    private static class GetterBean implements BeanWrapperTests.AliasedProperty {
        private String name;

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            if ((this.name) == null) {
                throw new RuntimeException("name property must be set");
            }
            return name;
        }
    }

    @SuppressWarnings("unused")
    private static class IntelliBean {
        public void setName(String name) {
        }

        public void setMyString(String string) {
        }

        public void setMyStrings(String string) {
        }

        public void setMyStriNg(String string) {
        }

        public void setMyStringss(String string) {
        }
    }

    @SuppressWarnings("serial")
    public static class TypedReadOnlyMap extends AbstractPropertyAccessorTests.ReadOnlyMap<String, TestBean> {
        public TypedReadOnlyMap() {
        }

        public TypedReadOnlyMap(Map<? extends String, ? extends TestBean> map) {
            super(map);
        }
    }

    public static class TypedReadOnlyMapClient {
        public void setMap(BeanWrapperTests.TypedReadOnlyMap map) {
        }
    }

    public static class PropertyTypeMismatch {
        public String value;

        public void setObject(String object) {
            this.value = object;
        }

        public Integer getObject() {
            return (this.value) != null ? this.value.length() : null;
        }
    }

    public static class GetterWithOptional {
        public TestBean value;

        public void setObject(TestBean object) {
            this.value = object;
        }

        public Optional<TestBean> getObject() {
            return Optional.ofNullable(this.value);
        }
    }
}

