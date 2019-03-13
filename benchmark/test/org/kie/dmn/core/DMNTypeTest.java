/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.dmn.core;


import KieDMNModelInstrumentedBase.URI_FEEL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.core.compiler.DMNTypeRegistry;
import org.kie.dmn.core.compiler.DMNTypeRegistryV11;
import org.kie.dmn.core.util.DynamicTypeUtils;
import org.kie.dmn.feel.FEEL;
import org.kie.dmn.feel.lang.types.BuiltInType;


public class DMNTypeTest {
    private static final DMNTypeRegistry typeRegistry = new DMNTypeRegistryV11();

    private static final DMNType FEEL_STRING = DMNTypeTest.typeRegistry.resolveType(URI_FEEL, "string");

    private static final DMNType FEEL_NUMBER = DMNTypeTest.typeRegistry.resolveType(URI_FEEL, "number");

    @Test
    public void testDROOLS2147() {
        // DROOLS-2147
        final String testNS = "testDROOLS2147";
        final Map<String, DMNType> personPrototype = DynamicTypeUtils.prototype(DynamicTypeUtils.entry("name", DMNTypeTest.FEEL_STRING), DynamicTypeUtils.entry("age", DMNTypeTest.FEEL_NUMBER));
        final DMNType dmnPerson = DMNTypeTest.typeRegistry.registerType(new org.kie.dmn.core.impl.CompositeTypeImpl(testNS, "person", null, false, personPrototype, null, null));
        final DMNType dmnPersonList = DMNTypeTest.typeRegistry.registerType(new org.kie.dmn.core.impl.CompositeTypeImpl(testNS, "personList", null, true, null, dmnPerson, null));
        final DMNType dmnListOfPersonsGrouped = DMNTypeTest.typeRegistry.registerType(new org.kie.dmn.core.impl.CompositeTypeImpl(testNS, "groups", null, true, null, dmnPersonList, null));
        final Map<String, Object> instanceBob = DynamicTypeUtils.prototype(DynamicTypeUtils.entry("name", "Bob"), DynamicTypeUtils.entry("age", 42));
        final Map<String, Object> instanceJohn = DynamicTypeUtils.prototype(DynamicTypeUtils.entry("name", "John"), DynamicTypeUtils.entry("age", 47));
        final Map<String, Object> instanceNOTaPerson = DynamicTypeUtils.prototype(DynamicTypeUtils.entry("name", "NOTAPERSON"));
        Assert.assertTrue(dmnPerson.isAssignableValue(instanceBob));
        Assert.assertTrue(dmnPerson.isAssignableValue(instanceJohn));
        Assert.assertFalse(dmnPerson.isAssignableValue(instanceNOTaPerson));
        final List<Map<String, Object>> onlyBob = Collections.singletonList(instanceBob);
        final List<Map<String, Object>> bobANDjohn = Arrays.asList(instanceBob, instanceJohn);
        final List<Map<String, Object>> johnANDnotAPerson = Arrays.asList(instanceJohn, instanceNOTaPerson);
        Assert.assertTrue(dmnPersonList.isAssignableValue(onlyBob));
        Assert.assertTrue(dmnPersonList.isAssignableValue(bobANDjohn));
        Assert.assertFalse(dmnPersonList.isAssignableValue(johnANDnotAPerson));
        Assert.assertTrue(dmnPersonList.isAssignableValue(instanceBob));// because accordingly to FEEL spec, bob=[bob]

        final List<List<Map<String, Object>>> the2ListsThatContainBob = Arrays.asList(onlyBob, bobANDjohn);
        Assert.assertTrue(dmnListOfPersonsGrouped.isAssignableValue(the2ListsThatContainBob));
        final List<List<Map<String, Object>>> the3Lists = Arrays.asList(onlyBob, bobANDjohn, johnANDnotAPerson);
        Assert.assertFalse(dmnListOfPersonsGrouped.isAssignableValue(the3Lists));
        final List<Object> groupsOfBobAndBobHimself = Arrays.asList(instanceBob, onlyBob, bobANDjohn);
        Assert.assertTrue(dmnListOfPersonsGrouped.isAssignableValue(groupsOfBobAndBobHimself));// [bob, [bob], [bob, john]] because for the property of FEEL spec a=[a] is equivalent to [[bob], [bob], [bob, john]]

        final DMNType listOfGroups = DMNTypeTest.typeRegistry.registerType(new org.kie.dmn.core.impl.CompositeTypeImpl(testNS, "listOfGroups", null, true, null, dmnListOfPersonsGrouped, null));
        final List<Object> groupsContainingBobPartitionedBySize = Arrays.asList(the2ListsThatContainBob, Collections.singletonList(bobANDjohn));
        Assert.assertTrue(listOfGroups.isAssignableValue(groupsContainingBobPartitionedBySize));// [ [[B], [B, J]], [[B, J]] ]

    }

    @Test
    public void testAllowedValuesForASimpleTypeCollection() {
        // DROOLS-2357
        final String testNS = "testDROOLS2357";
        final FEEL feel = FEEL.newInstance();
        final DMNType tDecision1 = DMNTypeTest.typeRegistry.registerType(new org.kie.dmn.core.impl.SimpleTypeImpl(testNS, "tListOfVowels", null, true, feel.evaluateUnaryTests("\"a\",\"e\",\"i\",\"o\",\"u\""), DMNTypeTest.FEEL_STRING, BuiltInType.STRING));
        Assert.assertTrue(tDecision1.isAssignableValue("a"));
        Assert.assertTrue(tDecision1.isAssignableValue(Collections.singletonList("a")));
        Assert.assertFalse(tDecision1.isAssignableValue("z"));
        Assert.assertTrue(tDecision1.isAssignableValue(Arrays.asList("a", "e")));
        Assert.assertFalse(tDecision1.isAssignableValue(Arrays.asList("a", "e", "zzz")));
    }
}

