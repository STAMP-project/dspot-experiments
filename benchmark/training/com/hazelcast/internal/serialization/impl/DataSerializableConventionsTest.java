/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.internal.serialization.impl;


import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.hazelcast.internal.serialization.DataSerializerHook;
import com.hazelcast.nio.serialization.BinaryInterface;
import com.hazelcast.nio.serialization.DataSerializable;
import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.nio.serialization.SerializableByConvention;
import com.hazelcast.spi.AbstractLocalOperation;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.ReflectionsHelper;
import com.hazelcast.test.annotation.QuickTest;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


/**
 * Tests to verify serializable classes conventions are observed. Each conventions test scans the classpath (excluding
 * test classes) and tests <b>concrete</b> classes which implement (directly or transitively) {@code Serializable} or
 * {@code DataSerializable} interface, then verifies that it's either annotated with {@link BinaryInterface},
 * is excluded from conventions tests by being annotated with {@link SerializableByConvention} or
 * they also implement {@code IdentifiedDataSerializable}.
 * Additionally, tests whether IDS instanced obtained from DS factories
 * have the same ID as the one reported by their `getId` method and that F_ID/ID combinations are unique.
 */
@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class })
public class DataSerializableConventionsTest {
    // subclasses of classes in the white list are not taken into account for
    // conventions tests. Reasons:
    // - they inherit Serializable from a parent class and cannot implement
    // IdentifiedDataSerializable due to unavailability of default constructor.
    // - they purposefully break conventions to fix a known issue
    private final Set<Class> classWhiteList;

    public DataSerializableConventionsTest() {
        classWhiteList = Collections.unmodifiableSet(getWhitelistedClasses());
    }

    /**
     * Verifies that any class which is {@link DataSerializable} and is not annotated with {@link BinaryInterface}
     * is also an {@link IdentifiedDataSerializable}.
     */
    @Test
    public void test_dataSerializableClasses_areIdentifiedDataSerializable() {
        Set<Class<? extends DataSerializable>> dataSerializableClasses = ReflectionsHelper.REFLECTIONS.getSubTypesOf(DataSerializable.class);
        Set<Class<? extends IdentifiedDataSerializable>> allIdDataSerializableClasses = ReflectionsHelper.REFLECTIONS.getSubTypesOf(IdentifiedDataSerializable.class);
        dataSerializableClasses.removeAll(allIdDataSerializableClasses);
        // also remove IdentifiedDataSerializable itself
        dataSerializableClasses.remove(IdentifiedDataSerializable.class);
        // do not check abstract classes & interfaces
        ReflectionsHelper.filterNonConcreteClasses(dataSerializableClasses);
        // locate all classes annotated with BinaryInterface and remove those as well
        Set<?> allAnnotatedClasses = ReflectionsHelper.REFLECTIONS.getTypesAnnotatedWith(BinaryInterface.class, true);
        dataSerializableClasses.removeAll(allAnnotatedClasses);
        // exclude @SerializableByConvention classes
        Set<?> serializableByConventions = ReflectionsHelper.REFLECTIONS.getTypesAnnotatedWith(SerializableByConvention.class, true);
        dataSerializableClasses.removeAll(serializableByConventions);
        if ((dataSerializableClasses.size()) > 0) {
            SortedSet<String> nonCompliantClassNames = new TreeSet<String>();
            for (Object o : dataSerializableClasses) {
                nonCompliantClassNames.add(o.toString());
            }
            System.out.println("The following classes are DataSerializable while they should be IdentifiedDataSerializable:");
            // failure - output non-compliant classes to standard output and fail the test
            for (String s : nonCompliantClassNames) {
                System.out.println(s);
            }
            Assert.fail(((("There are " + (dataSerializableClasses.size())) + " classes which are DataSerializable, not @BinaryInterface-") + "annotated and are not IdentifiedDataSerializable."));
        }
    }

    /**
     * Verifies that any class which is {@link Serializable} and is not annotated with {@link BinaryInterface}
     * is also an {@link IdentifiedDataSerializable}.
     */
    @Test
    public void test_serializableClasses_areIdentifiedDataSerializable() {
        Set<Class<? extends Serializable>> serializableClasses = ReflectionsHelper.REFLECTIONS.getSubTypesOf(Serializable.class);
        Set<Class<? extends IdentifiedDataSerializable>> allIdDataSerializableClasses = ReflectionsHelper.REFLECTIONS.getSubTypesOf(IdentifiedDataSerializable.class);
        serializableClasses.removeAll(allIdDataSerializableClasses);
        // do not check abstract classes & interfaces
        ReflectionsHelper.filterNonConcreteClasses(serializableClasses);
        // locate all classes annotated with BinaryInterface and remove those as well
        Set<?> allAnnotatedClasses = ReflectionsHelper.REFLECTIONS.getTypesAnnotatedWith(BinaryInterface.class, true);
        serializableClasses.removeAll(allAnnotatedClasses);
        // exclude @SerializableByConvention classes
        Set<?> serializableByConventions = ReflectionsHelper.REFLECTIONS.getTypesAnnotatedWith(SerializableByConvention.class, true);
        serializableClasses.removeAll(serializableByConventions);
        if ((serializableClasses.size()) > 0) {
            SortedSet<String> nonCompliantClassNames = new TreeSet<String>();
            for (Object o : serializableClasses) {
                if (!(inheritsFromWhiteListedClass(((Class) (o))))) {
                    nonCompliantClassNames.add(o.toString());
                }
            }
            if (!(nonCompliantClassNames.isEmpty())) {
                System.out.println("The following classes are Serializable and should be IdentifiedDataSerializable:");
                // failure - output non-compliant classes to standard output and fail the test
                for (String s : nonCompliantClassNames) {
                    System.out.println(s);
                }
                Assert.fail(((("There are " + (nonCompliantClassNames.size())) + " classes which are Serializable, not @BinaryInterface-") + "annotated and are not IdentifiedDataSerializable."));
            }
        }
    }

    /**
     * Fails when {@link IdentifiedDataSerializable} classes:
     * - do not have a default no-args constructor
     * - factoryId/id pairs are not unique per class
     */
    @Test
    public void test_identifiedDataSerializables_haveUniqueFactoryAndTypeId() throws Exception {
        Set<String> classesWithInstantiationProblems = new TreeSet<String>();
        Set<String> classesThrowingUnsupportedOperationException = new TreeSet<String>();
        Multimap<Integer, Integer> factoryToTypeId = HashMultimap.create();
        Set<Class<? extends IdentifiedDataSerializable>> identifiedDataSerializables = getIDSConcreteClasses();
        for (Class<? extends IdentifiedDataSerializable> klass : identifiedDataSerializables) {
            // exclude classes which are known to be meant for local use only
            if ((!(AbstractLocalOperation.class.isAssignableFrom(klass))) && (!(isReadOnlyConfig(klass)))) {
                // wrap all of this in try-catch, as it is legitimate for some classes to throw UnsupportedOperationException
                try {
                    Constructor<? extends IdentifiedDataSerializable> ctor = klass.getDeclaredConstructor();
                    ctor.setAccessible(true);
                    IdentifiedDataSerializable instance = ctor.newInstance();
                    int factoryId = instance.getFactoryId();
                    int typeId = instance.getId();
                    if (factoryToTypeId.containsEntry(factoryId, typeId)) {
                        Assert.fail(((((((("Factory-Type ID pair {" + factoryId) + ", ") + typeId) + "} from ") + (klass.toString())) + " is already") + " registered in another type."));
                    } else {
                        factoryToTypeId.put(factoryId, typeId);
                    }
                } catch (UnsupportedOperationException e) {
                    // expected from local operation classes not meant for serialization
                    // gather those and print them to system.out for information at end of test
                    classesThrowingUnsupportedOperationException.add(klass.getName());
                } catch (InstantiationException e) {
                    classesWithInstantiationProblems.add((((klass.getName()) + " failed with ") + (e.getMessage())));
                } catch (NoSuchMethodException e) {
                    classesWithInstantiationProblems.add((((klass.getName()) + " failed with ") + (e.getMessage())));
                }
            }
        }
        if (!(classesThrowingUnsupportedOperationException.isEmpty())) {
            System.out.println(((("INFO: " + (classesThrowingUnsupportedOperationException.size())) + " classes threw") + " UnsupportedOperationException in getFactoryId/getId invocation:"));
            for (String className : classesThrowingUnsupportedOperationException) {
                System.out.println(className);
            }
        }
        if (!(classesWithInstantiationProblems.isEmpty())) {
            System.out.println((((("There are " + (classesWithInstantiationProblems.size())) + " classes which threw an exception while") + " attempting to invoke a default no-args constructor. See console output for exception details.") + " List of problematic classes:"));
            for (String className : classesWithInstantiationProblems) {
                System.out.println(className);
            }
            Assert.fail(((("There are " + (classesWithInstantiationProblems.size())) + " classes which threw an exception while") + " attempting to invoke a default no-args constructor. See test output for exception details."));
        }
    }

    /**
     * Locates {@link IdentifiedDataSerializable} classes via reflection, iterates over them and asserts an instance created by
     * a factory is of the same classes as an instance created via reflection.
     */
    @Test
    public void test_identifiedDataSerializables_areInstancesOfSameClass_whenConstructedFromFactory() throws Exception {
        Set<Class<? extends DataSerializerHook>> dsHooks = ReflectionsHelper.REFLECTIONS.getSubTypesOf(DataSerializerHook.class);
        Map<Integer, DataSerializableFactory> factories = new HashMap<Integer, DataSerializableFactory>();
        for (Class<? extends DataSerializerHook> hookClass : dsHooks) {
            DataSerializerHook dsHook = hookClass.newInstance();
            DataSerializableFactory factory = dsHook.createFactory();
            factories.put(dsHook.getFactoryId(), factory);
        }
        Set<Class<? extends IdentifiedDataSerializable>> identifiedDataSerializables = getIDSConcreteClasses();
        for (Class<? extends IdentifiedDataSerializable> klass : identifiedDataSerializables) {
            if (AbstractLocalOperation.class.isAssignableFrom(klass)) {
                continue;
            }
            if (isReadOnlyConfig(klass)) {
                continue;
            }
            // wrap all of this in try-catch, as it is legitimate for some classes to throw UnsupportedOperationException
            try {
                Constructor<? extends IdentifiedDataSerializable> ctor = klass.getDeclaredConstructor();
                ctor.setAccessible(true);
                IdentifiedDataSerializable instance = ctor.newInstance();
                int factoryId = instance.getFactoryId();
                int typeId = instance.getId();
                if (!(factories.containsKey(factoryId))) {
                    Assert.fail(((((("Factory with ID " + factoryId) + " declared in ") + klass) + " not found.") + " Is such a factory ID registered?"));
                }
                IdentifiedDataSerializable instanceFromFactory = factories.get(factoryId).create(typeId);
                Assert.assertNotNull(((("Factory with ID " + factoryId) + " returned null for type with ID ") + typeId), instanceFromFactory);
                Assert.assertTrue(((((("Factory with ID " + factoryId) + " instantiated an object of ") + (instanceFromFactory.getClass())) + " while expected type was ") + (instance.getClass())), instanceFromFactory.getClass().equals(instance.getClass()));
            } catch (UnsupportedOperationException ignored) {
                // expected from local operation classes not meant for serialization
            }
        }
    }
}

