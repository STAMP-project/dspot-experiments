/**
 * Copyright 2002-2014 the original author or authors.
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
package org.springframework.validation;


import java.beans.PropertyEditorSupport;
import java.util.Map;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.NotWritablePropertyException;
import org.springframework.beans.NullValueInNestedPathException;
import org.springframework.beans.PropertyValue;
import org.springframework.tests.sample.beans.FieldAccessBean;
import org.springframework.tests.sample.beans.TestBean;

import static BindingResult.MODEL_KEY_PREFIX;


/**
 *
 *
 * @author Juergen Hoeller
 * @author Stephane Nicoll
 * @since 07.03.2006
 */
public class DataBinderFieldAccessTests {
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void bindingNoErrors() throws Exception {
        FieldAccessBean rod = new FieldAccessBean();
        DataBinder binder = new DataBinder(rod, "person");
        Assert.assertTrue(binder.isIgnoreUnknownFields());
        binder.initDirectFieldAccess();
        MutablePropertyValues pvs = new MutablePropertyValues();
        pvs.addPropertyValue(new PropertyValue("name", "Rod"));
        pvs.addPropertyValue(new PropertyValue("age", new Integer(32)));
        pvs.addPropertyValue(new PropertyValue("nonExisting", "someValue"));
        binder.bind(pvs);
        binder.close();
        Assert.assertTrue("changed name correctly", rod.getName().equals("Rod"));
        Assert.assertTrue("changed age correctly", ((rod.getAge()) == 32));
        Map<?, ?> m = binder.getBindingResult().getModel();
        Assert.assertTrue("There is one element in map", ((m.size()) == 2));
        FieldAccessBean tb = ((FieldAccessBean) (m.get("person")));
        Assert.assertTrue("Same object", tb.equals(rod));
    }

    @Test
    public void bindingNoErrorsNotIgnoreUnknown() throws Exception {
        FieldAccessBean rod = new FieldAccessBean();
        DataBinder binder = new DataBinder(rod, "person");
        binder.initDirectFieldAccess();
        binder.setIgnoreUnknownFields(false);
        MutablePropertyValues pvs = new MutablePropertyValues();
        pvs.addPropertyValue(new PropertyValue("name", "Rod"));
        pvs.addPropertyValue(new PropertyValue("age", new Integer(32)));
        pvs.addPropertyValue(new PropertyValue("nonExisting", "someValue"));
        try {
            binder.bind(pvs);
            Assert.fail("Should have thrown NotWritablePropertyException");
        } catch (NotWritablePropertyException ex) {
            // expected
        }
    }

    @Test
    public void bindingWithErrors() throws Exception {
        FieldAccessBean rod = new FieldAccessBean();
        DataBinder binder = new DataBinder(rod, "person");
        binder.initDirectFieldAccess();
        MutablePropertyValues pvs = new MutablePropertyValues();
        pvs.addPropertyValue(new PropertyValue("name", "Rod"));
        pvs.addPropertyValue(new PropertyValue("age", "32x"));
        binder.bind(pvs);
        try {
            binder.close();
            Assert.fail("Should have thrown BindException");
        } catch (BindException ex) {
            Assert.assertTrue("changed name correctly", rod.getName().equals("Rod"));
            // assertTrue("changed age correctly", rod.getAge() == 32);
            Map<?, ?> map = binder.getBindingResult().getModel();
            // assertTrue("There are 3 element in map", m.size() == 1);
            FieldAccessBean tb = ((FieldAccessBean) (map.get("person")));
            Assert.assertTrue("Same object", tb.equals(rod));
            BindingResult br = ((BindingResult) (map.get(((MODEL_KEY_PREFIX) + "person"))));
            Assert.assertTrue("Added itself to map", (br == (binder.getBindingResult())));
            Assert.assertTrue(br.hasErrors());
            Assert.assertTrue("Correct number of errors", ((br.getErrorCount()) == 1));
            Assert.assertTrue("Has age errors", br.hasFieldErrors("age"));
            Assert.assertTrue("Correct number of age errors", ((br.getFieldErrorCount("age")) == 1));
            Assert.assertEquals("32x", binder.getBindingResult().getFieldValue("age"));
            Assert.assertEquals("32x", binder.getBindingResult().getFieldError("age").getRejectedValue());
            Assert.assertEquals(0, tb.getAge());
        }
    }

    @Test
    public void nestedBindingWithDefaultConversionNoErrors() throws Exception {
        FieldAccessBean rod = new FieldAccessBean();
        DataBinder binder = new DataBinder(rod, "person");
        Assert.assertTrue(binder.isIgnoreUnknownFields());
        binder.initDirectFieldAccess();
        MutablePropertyValues pvs = new MutablePropertyValues();
        pvs.addPropertyValue(new PropertyValue("spouse.name", "Kerry"));
        pvs.addPropertyValue(new PropertyValue("spouse.jedi", "on"));
        binder.bind(pvs);
        binder.close();
        Assert.assertEquals("Kerry", rod.getSpouse().getName());
        Assert.assertTrue(rod.getSpouse().isJedi());
    }

    @Test
    public void nestedBindingWithDisabledAutoGrow() throws Exception {
        FieldAccessBean rod = new FieldAccessBean();
        DataBinder binder = new DataBinder(rod, "person");
        binder.setAutoGrowNestedPaths(false);
        binder.initDirectFieldAccess();
        MutablePropertyValues pvs = new MutablePropertyValues();
        pvs.addPropertyValue(new PropertyValue("spouse.name", "Kerry"));
        thrown.expect(NullValueInNestedPathException.class);
        binder.bind(pvs);
    }

    @Test
    public void bindingWithErrorsAndCustomEditors() throws Exception {
        FieldAccessBean rod = new FieldAccessBean();
        DataBinder binder = new DataBinder(rod, "person");
        binder.initDirectFieldAccess();
        binder.registerCustomEditor(TestBean.class, "spouse", new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                setValue(new TestBean(text, 0));
            }

            @Override
            public String getAsText() {
                return getName();
            }
        });
        MutablePropertyValues pvs = new MutablePropertyValues();
        pvs.addPropertyValue(new PropertyValue("name", "Rod"));
        pvs.addPropertyValue(new PropertyValue("age", "32x"));
        pvs.addPropertyValue(new PropertyValue("spouse", "Kerry"));
        binder.bind(pvs);
        try {
            binder.close();
            Assert.fail("Should have thrown BindException");
        } catch (BindException ex) {
            Assert.assertTrue("changed name correctly", rod.getName().equals("Rod"));
            // assertTrue("changed age correctly", rod.getAge() == 32);
            Map<?, ?> model = binder.getBindingResult().getModel();
            // assertTrue("There are 3 element in map", m.size() == 1);
            FieldAccessBean tb = ((FieldAccessBean) (model.get("person")));
            Assert.assertTrue("Same object", tb.equals(rod));
            BindingResult br = ((BindingResult) (model.get(((MODEL_KEY_PREFIX) + "person"))));
            Assert.assertTrue("Added itself to map", (br == (binder.getBindingResult())));
            Assert.assertTrue(br.hasErrors());
            Assert.assertTrue("Correct number of errors", ((br.getErrorCount()) == 1));
            Assert.assertTrue("Has age errors", br.hasFieldErrors("age"));
            Assert.assertTrue("Correct number of age errors", ((br.getFieldErrorCount("age")) == 1));
            Assert.assertEquals("32x", binder.getBindingResult().getFieldValue("age"));
            Assert.assertEquals("32x", binder.getBindingResult().getFieldError("age").getRejectedValue());
            Assert.assertEquals(0, tb.getAge());
            Assert.assertTrue("Does not have spouse errors", (!(br.hasFieldErrors("spouse"))));
            Assert.assertEquals("Kerry", binder.getBindingResult().getFieldValue("spouse"));
            Assert.assertNotNull(tb.getSpouse());
        }
    }
}

