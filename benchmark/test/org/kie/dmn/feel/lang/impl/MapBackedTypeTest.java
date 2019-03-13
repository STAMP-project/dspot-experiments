package org.kie.dmn.feel.lang.impl;


import BuiltInType.STRING;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.kie.dmn.feel.util.DynamicTypeUtils;


public class MapBackedTypeTest {
    @Test
    public void testBasic() {
        MapBackedType personType = new MapBackedType("Person", DynamicTypeUtils.mapOf(DynamicTypeUtils.entry("First Name", STRING), DynamicTypeUtils.entry("Last Name", STRING)));
        Map<?, ?> aPerson = DynamicTypeUtils.prototype(DynamicTypeUtils.entry("First Name", "John"), DynamicTypeUtils.entry("Last Name", "Doe"));
        Assert.assertTrue(personType.isAssignableValue(aPerson));
        Assert.assertTrue(personType.isInstanceOf(aPerson));
        Map<?, ?> aCompletePerson = DynamicTypeUtils.prototype(DynamicTypeUtils.entry("First Name", "John"), DynamicTypeUtils.entry("Last Name", "Doe"), DynamicTypeUtils.entry("Address", "100 East Davie Street"));
        Assert.assertTrue(personType.isAssignableValue(aCompletePerson));
        Assert.assertTrue(personType.isInstanceOf(aCompletePerson));
        Map<?, ?> notAPerson = DynamicTypeUtils.prototype(DynamicTypeUtils.entry("First Name", "John"));
        Assert.assertFalse(personType.isAssignableValue(notAPerson));
        Assert.assertFalse(personType.isInstanceOf(notAPerson));
        Map<?, ?> anonymousPerson1 = DynamicTypeUtils.prototype(DynamicTypeUtils.entry("First Name", null), DynamicTypeUtils.entry("Last Name", "Doe"));
        Assert.assertTrue(personType.isAssignableValue(anonymousPerson1));
        Assert.assertTrue(personType.isInstanceOf(anonymousPerson1));
        Map<?, ?> anonymousPerson2 = DynamicTypeUtils.prototype(DynamicTypeUtils.entry("First Name", "John"), DynamicTypeUtils.entry("Last Name", null));
        Assert.assertTrue(personType.isAssignableValue(anonymousPerson2));
        Assert.assertTrue(personType.isInstanceOf(anonymousPerson2));
        Map<?, ?> anonymousPerson3 = DynamicTypeUtils.prototype(DynamicTypeUtils.entry("First Name", null), DynamicTypeUtils.entry("Last Name", null));
        Assert.assertTrue(personType.isAssignableValue(anonymousPerson3));
        Assert.assertTrue(personType.isInstanceOf(anonymousPerson3));
        Map<?, ?> anonymousCompletePerson = DynamicTypeUtils.prototype(DynamicTypeUtils.entry("First Name", null), DynamicTypeUtils.entry("Last Name", null), DynamicTypeUtils.entry("Address", "100 East Davie Street"));
        Assert.assertTrue(personType.isAssignableValue(anonymousCompletePerson));
        Assert.assertTrue(personType.isInstanceOf(anonymousCompletePerson));
        Map<?, ?> nullPerson = null;
        Assert.assertTrue(personType.isAssignableValue(nullPerson));
        Assert.assertFalse(personType.isInstanceOf(nullPerson));
    }
}

