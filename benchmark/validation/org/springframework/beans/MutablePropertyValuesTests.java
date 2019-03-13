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


import java.util.Iterator;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link MutablePropertyValues}.
 *
 * @author Rod Johnson
 * @author Chris Beams
 * @author Juergen Hoeller
 */
public class MutablePropertyValuesTests extends AbstractPropertyValuesTests {
    @Test
    public void testValid() {
        MutablePropertyValues pvs = new MutablePropertyValues();
        pvs.addPropertyValue(new PropertyValue("forname", "Tony"));
        pvs.addPropertyValue(new PropertyValue("surname", "Blair"));
        pvs.addPropertyValue(new PropertyValue("age", "50"));
        doTestTony(pvs);
        MutablePropertyValues deepCopy = new MutablePropertyValues(pvs);
        doTestTony(deepCopy);
        deepCopy.setPropertyValueAt(new PropertyValue("name", "Gordon"), 0);
        doTestTony(pvs);
        Assert.assertEquals("Gordon", deepCopy.getPropertyValue("name").getValue());
    }

    @Test
    public void testAddOrOverride() {
        MutablePropertyValues pvs = new MutablePropertyValues();
        pvs.addPropertyValue(new PropertyValue("forname", "Tony"));
        pvs.addPropertyValue(new PropertyValue("surname", "Blair"));
        pvs.addPropertyValue(new PropertyValue("age", "50"));
        doTestTony(pvs);
        PropertyValue addedPv = new PropertyValue("rod", "Rod");
        pvs.addPropertyValue(addedPv);
        Assert.assertTrue(pvs.getPropertyValue("rod").equals(addedPv));
        PropertyValue changedPv = new PropertyValue("forname", "Greg");
        pvs.addPropertyValue(changedPv);
        Assert.assertTrue(pvs.getPropertyValue("forname").equals(changedPv));
    }

    @Test
    public void testChangesOnEquals() {
        MutablePropertyValues pvs = new MutablePropertyValues();
        pvs.addPropertyValue(new PropertyValue("forname", "Tony"));
        pvs.addPropertyValue(new PropertyValue("surname", "Blair"));
        pvs.addPropertyValue(new PropertyValue("age", "50"));
        MutablePropertyValues pvs2 = pvs;
        PropertyValues changes = pvs2.changesSince(pvs);
        Assert.assertTrue("changes are empty", ((changes.getPropertyValues().length) == 0));
    }

    @Test
    public void testChangeOfOneField() {
        MutablePropertyValues pvs = new MutablePropertyValues();
        pvs.addPropertyValue(new PropertyValue("forname", "Tony"));
        pvs.addPropertyValue(new PropertyValue("surname", "Blair"));
        pvs.addPropertyValue(new PropertyValue("age", "50"));
        MutablePropertyValues pvs2 = new MutablePropertyValues(pvs);
        PropertyValues changes = pvs2.changesSince(pvs);
        Assert.assertTrue(("changes are empty, not of length " + (changes.getPropertyValues().length)), ((changes.getPropertyValues().length) == 0));
        pvs2.addPropertyValue(new PropertyValue("forname", "Gordon"));
        changes = pvs2.changesSince(pvs);
        Assert.assertEquals("1 change", 1, changes.getPropertyValues().length);
        PropertyValue fn = changes.getPropertyValue("forname");
        Assert.assertTrue("change is forname", (fn != null));
        Assert.assertTrue("new value is gordon", fn.getValue().equals("Gordon"));
        MutablePropertyValues pvs3 = new MutablePropertyValues(pvs);
        changes = pvs3.changesSince(pvs);
        Assert.assertTrue(("changes are empty, not of length " + (changes.getPropertyValues().length)), ((changes.getPropertyValues().length) == 0));
        // add new
        pvs3.addPropertyValue(new PropertyValue("foo", "bar"));
        pvs3.addPropertyValue(new PropertyValue("fi", "fum"));
        changes = pvs3.changesSince(pvs);
        Assert.assertTrue("2 change", ((changes.getPropertyValues().length) == 2));
        fn = changes.getPropertyValue("foo");
        Assert.assertTrue("change in foo", (fn != null));
        Assert.assertTrue("new value is bar", fn.getValue().equals("bar"));
    }

    @Test
    public void iteratorContainsPropertyValue() {
        MutablePropertyValues pvs = new MutablePropertyValues();
        pvs.add("foo", "bar");
        Iterator<PropertyValue> it = pvs.iterator();
        Assert.assertTrue(it.hasNext());
        PropertyValue pv = it.next();
        Assert.assertEquals("foo", pv.getName());
        Assert.assertEquals("bar", pv.getValue());
        try {
            it.remove();
            Assert.fail("Should have thrown UnsupportedOperationException");
        } catch (UnsupportedOperationException ex) {
            // expected
        }
        Assert.assertFalse(it.hasNext());
    }

    @Test
    public void iteratorIsEmptyForEmptyValues() {
        MutablePropertyValues pvs = new MutablePropertyValues();
        Iterator<PropertyValue> it = pvs.iterator();
        Assert.assertFalse(it.hasNext());
    }

    @Test
    public void streamContainsPropertyValue() {
        MutablePropertyValues pvs = new MutablePropertyValues();
        pvs.add("foo", "bar");
        Assert.assertThat(pvs.stream(), CoreMatchers.notNullValue());
        Assert.assertThat(pvs.stream().count(), CoreMatchers.is(1L));
        Assert.assertThat(pvs.stream().anyMatch(( pv) -> ("foo".equals(pv.getName())) && ("bar".equals(pv.getValue()))), CoreMatchers.is(true));
        Assert.assertThat(pvs.stream().anyMatch(( pv) -> ("bar".equals(pv.getName())) && ("foo".equals(pv.getValue()))), CoreMatchers.is(false));
    }

    @Test
    public void streamIsEmptyForEmptyValues() {
        MutablePropertyValues pvs = new MutablePropertyValues();
        Assert.assertThat(pvs.stream(), CoreMatchers.notNullValue());
        Assert.assertThat(pvs.stream().count(), CoreMatchers.is(0L));
    }
}

