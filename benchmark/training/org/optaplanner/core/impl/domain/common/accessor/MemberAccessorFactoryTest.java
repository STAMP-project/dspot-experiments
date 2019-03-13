/**
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.optaplanner.core.impl.domain.common.accessor;


import MemberAccessorFactory.MemberAccessorType.FIELD_OR_GETTER_METHOD_WITH_SETTER;
import org.junit.Assert;
import org.junit.Test;
import org.optaplanner.core.api.domain.solution.drools.ProblemFactProperty;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.optaplanner.core.impl.testdata.domain.reflect.accessmodifier.TestdataVisibilityModifierSolution;
import org.optaplanner.core.impl.testdata.domain.reflect.field.TestdataFieldAnnotatedEntity;
import org.optaplanner.core.impl.testdata.util.PlannerAssert;


public class MemberAccessorFactoryTest {
    @Test
    public void fieldAnnotatedEntity() throws NoSuchFieldException {
        MemberAccessor memberAccessor = MemberAccessorFactory.buildMemberAccessor(TestdataFieldAnnotatedEntity.class.getDeclaredField("value"), FIELD_OR_GETTER_METHOD_WITH_SETTER, PlanningVariable.class);
        PlannerAssert.assertInstanceOf(ReflectionFieldMemberAccessor.class, memberAccessor);
        Assert.assertEquals("value", memberAccessor.getName());
        Assert.assertEquals(TestdataValue.class, memberAccessor.getType());
        TestdataValue v1 = new TestdataValue("v1");
        TestdataValue v2 = new TestdataValue("v2");
        TestdataFieldAnnotatedEntity e1 = new TestdataFieldAnnotatedEntity("e1", v1);
        Assert.assertSame(v1, memberAccessor.executeGetter(e1));
        memberAccessor.executeSetter(e1, v2);
        Assert.assertSame(v2, e1.getValue());
    }

    @Test
    public void privateField() throws NoSuchFieldException {
        MemberAccessor memberAccessor = MemberAccessorFactory.buildMemberAccessor(TestdataVisibilityModifierSolution.class.getDeclaredField("privateField"), FIELD_OR_GETTER_METHOD_WITH_SETTER, ProblemFactProperty.class);
        PlannerAssert.assertInstanceOf(ReflectionFieldMemberAccessor.class, memberAccessor);
        Assert.assertEquals("privateField", memberAccessor.getName());
        Assert.assertEquals(String.class, memberAccessor.getType());
        TestdataVisibilityModifierSolution s1 = new TestdataVisibilityModifierSolution("s1", "firstValue", "n/a", "n/a", "n/a", "n/a", "n/a");
        Assert.assertEquals("firstValue", memberAccessor.executeGetter(s1));
        memberAccessor.executeSetter(s1, "secondValue");
        Assert.assertEquals("secondValue", memberAccessor.executeGetter(s1));
    }

    @Test
    public void publicField() throws NoSuchFieldException {
        MemberAccessor memberAccessor = MemberAccessorFactory.buildMemberAccessor(TestdataVisibilityModifierSolution.class.getDeclaredField("publicField"), FIELD_OR_GETTER_METHOD_WITH_SETTER, ProblemFactProperty.class);
        PlannerAssert.assertInstanceOf(ReflectionFieldMemberAccessor.class, memberAccessor);
        Assert.assertEquals("publicField", memberAccessor.getName());
        Assert.assertEquals(String.class, memberAccessor.getType());
        TestdataVisibilityModifierSolution s1 = new TestdataVisibilityModifierSolution("s1", "n/a", "firstValue", "n/a", "n/a", "n/a", "n/a");
        Assert.assertEquals("firstValue", memberAccessor.executeGetter(s1));
        memberAccessor.executeSetter(s1, "secondValue");
        Assert.assertEquals("secondValue", memberAccessor.executeGetter(s1));
    }

    @Test
    public void publicProperty() throws NoSuchMethodException {
        MemberAccessor memberAccessor = MemberAccessorFactory.buildMemberAccessor(TestdataVisibilityModifierSolution.class.getDeclaredMethod("getPublicProperty"), FIELD_OR_GETTER_METHOD_WITH_SETTER, ProblemFactProperty.class);
        PlannerAssert.assertInstanceOf(LambdaBeanPropertyMemberAccessor.class, memberAccessor);
        Assert.assertEquals("publicProperty", memberAccessor.getName());
        Assert.assertEquals(String.class, memberAccessor.getType());
        TestdataVisibilityModifierSolution s1 = new TestdataVisibilityModifierSolution("s1", "n/a", "n/a", "n/a", "n/a", "n/a", "firstValue");
        Assert.assertEquals("firstValue", memberAccessor.executeGetter(s1));
        memberAccessor.executeSetter(s1, "secondValue");
        Assert.assertEquals("secondValue", memberAccessor.executeGetter(s1));
    }
}

