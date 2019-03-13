/**
 * Copyright ? 2010-2014 Nokia
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
package org.jsonschema2pojo.integration;


import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNot;
import org.jsonschema2pojo.integration.util.Jsonschema2PojoRule;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;


public class ExcludedFromEqualsAndHashCodeIT {
    @ClassRule
    public static Jsonschema2PojoRule classSchemaRule = new Jsonschema2PojoRule();

    private static Class<?> clazz;

    @Test
    public void hashCodeTest() throws IntrospectionException, IllegalAccessException, InstantiationException, InvocationTargetException {
        Object instance = ExcludedFromEqualsAndHashCodeIT.clazz.newInstance();
        ExcludedFromEqualsAndHashCodeIT.setProperty(instance, "excludedByProperty", "one");
        ExcludedFromEqualsAndHashCodeIT.setProperty(instance, "excludedByArray", "two");
        ExcludedFromEqualsAndHashCodeIT.setProperty(instance, "notExcluded", "three");
        ExcludedFromEqualsAndHashCodeIT.setProperty(instance, "notExcludedByProperty", "four");
        int hashCodeBefore;
        int hashCodeAfter;
        hashCodeBefore = instance.hashCode();
        ExcludedFromEqualsAndHashCodeIT.setProperty(instance, "excludedByProperty", "five");
        hashCodeAfter = instance.hashCode();
        Assert.assertThat(hashCodeBefore, Is.is(CoreMatchers.equalTo(hashCodeAfter)));
        hashCodeBefore = hashCodeAfter;
        ExcludedFromEqualsAndHashCodeIT.setProperty(instance, "excludedByArray", "six");
        hashCodeAfter = instance.hashCode();
        Assert.assertThat(hashCodeBefore, Is.is(CoreMatchers.equalTo(hashCodeAfter)));
        hashCodeBefore = hashCodeAfter;
        ExcludedFromEqualsAndHashCodeIT.setProperty(instance, "notExcluded", "seven");
        hashCodeAfter = instance.hashCode();
        Assert.assertThat(hashCodeBefore, Is.is(IsNot.not(CoreMatchers.equalTo(hashCodeAfter))));
        hashCodeBefore = hashCodeAfter;
        ExcludedFromEqualsAndHashCodeIT.setProperty(instance, "notExcludedByProperty", "eight");
        hashCodeAfter = instance.hashCode();
        Assert.assertThat(hashCodeBefore, Is.is(IsNot.not(CoreMatchers.equalTo(hashCodeAfter))));
    }

    @Test
    public void equalsSelf() throws IllegalAccessException, InstantiationException {
        Object instance = ExcludedFromEqualsAndHashCodeIT.clazz.newInstance();
        Assert.assertThat(instance.equals(instance), Is.is(true));
    }

    @Test
    public void exludedByPropertyTest() throws IntrospectionException, IllegalAccessException, InstantiationException, InvocationTargetException {
        Object instanceOne = ExcludedFromEqualsAndHashCodeIT.clazz.newInstance();
        Object instanceTwo = ExcludedFromEqualsAndHashCodeIT.clazz.newInstance();
        ExcludedFromEqualsAndHashCodeIT.setProperty(instanceOne, "excludedByProperty", "one");
        ExcludedFromEqualsAndHashCodeIT.setProperty(instanceOne, "excludedByArray", "two");
        ExcludedFromEqualsAndHashCodeIT.setProperty(instanceOne, "notExcluded", "three");
        ExcludedFromEqualsAndHashCodeIT.setProperty(instanceOne, "notExcludedByProperty", "four");
        ExcludedFromEqualsAndHashCodeIT.setProperty(instanceTwo, "excludedByProperty", "differentValue");
        ExcludedFromEqualsAndHashCodeIT.setProperty(instanceTwo, "excludedByArray", "two");
        ExcludedFromEqualsAndHashCodeIT.setProperty(instanceTwo, "notExcluded", "three");
        ExcludedFromEqualsAndHashCodeIT.setProperty(instanceTwo, "notExcludedByProperty", "four");
        Assert.assertThat(instanceOne.equals(instanceTwo), Is.is(true));
    }

    @Test
    public void exludedByArrayTest() throws IntrospectionException, IllegalAccessException, InstantiationException, InvocationTargetException {
        Object instanceOne = ExcludedFromEqualsAndHashCodeIT.clazz.newInstance();
        Object instanceTwo = ExcludedFromEqualsAndHashCodeIT.clazz.newInstance();
        ExcludedFromEqualsAndHashCodeIT.setProperty(instanceOne, "excludedByProperty", "one");
        ExcludedFromEqualsAndHashCodeIT.setProperty(instanceOne, "excludedByArray", "two");
        ExcludedFromEqualsAndHashCodeIT.setProperty(instanceOne, "notExcluded", "three");
        ExcludedFromEqualsAndHashCodeIT.setProperty(instanceOne, "notExcludedByProperty", "four");
        ExcludedFromEqualsAndHashCodeIT.setProperty(instanceTwo, "excludedByProperty", "one");
        ExcludedFromEqualsAndHashCodeIT.setProperty(instanceTwo, "excludedByArray", "differentValue");
        ExcludedFromEqualsAndHashCodeIT.setProperty(instanceTwo, "notExcluded", "three");
        ExcludedFromEqualsAndHashCodeIT.setProperty(instanceTwo, "notExcludedByProperty", "four");
        Assert.assertThat(instanceOne.equals(instanceTwo), Is.is(true));
    }

    @Test
    public void notExcludedTest() throws IntrospectionException, IllegalAccessException, InstantiationException, InvocationTargetException {
        Object instanceOne = ExcludedFromEqualsAndHashCodeIT.clazz.newInstance();
        Object instanceTwo = ExcludedFromEqualsAndHashCodeIT.clazz.newInstance();
        ExcludedFromEqualsAndHashCodeIT.setProperty(instanceOne, "excludedByProperty", "one");
        ExcludedFromEqualsAndHashCodeIT.setProperty(instanceOne, "excludedByArray", "two");
        ExcludedFromEqualsAndHashCodeIT.setProperty(instanceOne, "notExcluded", "three");
        ExcludedFromEqualsAndHashCodeIT.setProperty(instanceOne, "notExcludedByProperty", "four");
        ExcludedFromEqualsAndHashCodeIT.setProperty(instanceTwo, "excludedByProperty", "one");
        ExcludedFromEqualsAndHashCodeIT.setProperty(instanceTwo, "excludedByArray", "two");
        ExcludedFromEqualsAndHashCodeIT.setProperty(instanceTwo, "notExcluded", "differentValue");
        ExcludedFromEqualsAndHashCodeIT.setProperty(instanceTwo, "notExcludedByProperty", "four");
        Assert.assertThat(instanceOne.equals(instanceTwo), Is.is(false));
    }

    @Test
    public void notExludedByPropertyTest() throws IntrospectionException, IllegalAccessException, InstantiationException, InvocationTargetException {
        Object instanceOne = ExcludedFromEqualsAndHashCodeIT.clazz.newInstance();
        Object instanceTwo = ExcludedFromEqualsAndHashCodeIT.clazz.newInstance();
        ExcludedFromEqualsAndHashCodeIT.setProperty(instanceOne, "excludedByProperty", "one");
        ExcludedFromEqualsAndHashCodeIT.setProperty(instanceOne, "excludedByArray", "two");
        ExcludedFromEqualsAndHashCodeIT.setProperty(instanceOne, "notExcluded", "three");
        ExcludedFromEqualsAndHashCodeIT.setProperty(instanceOne, "notExcludedByProperty", "four");
        ExcludedFromEqualsAndHashCodeIT.setProperty(instanceTwo, "excludedByProperty", "one");
        ExcludedFromEqualsAndHashCodeIT.setProperty(instanceTwo, "excludedByArray", "two");
        ExcludedFromEqualsAndHashCodeIT.setProperty(instanceTwo, "notExcluded", "three");
        ExcludedFromEqualsAndHashCodeIT.setProperty(instanceTwo, "notExcludedByProperty", "differentValue");
        Assert.assertThat(instanceOne.equals(instanceTwo), Is.is(false));
    }
}

