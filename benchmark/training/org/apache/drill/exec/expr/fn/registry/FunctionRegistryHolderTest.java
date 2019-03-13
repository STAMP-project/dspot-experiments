/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.exec.expr.fn.registry;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.apache.drill.categories.SqlFunctionTest;
import org.apache.drill.exec.expr.fn.DrillFuncHolder;
import org.apache.drill.shaded.guava.com.google.common.collect.ArrayListMultimap;
import org.apache.drill.shaded.guava.com.google.common.collect.ListMultimap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(SqlFunctionTest.class)
public class FunctionRegistryHolderTest {
    private static final String built_in = "built-in";

    private static final String udf_jar = "DrillUDF-1.0.jar";

    private static final String LOWER_FUNC_NAME = "lower";

    private static final String SHUFFLE_FUNC_NAME = "shuffle";

    private static Map<String, List<FunctionHolder>> newJars;

    private FunctionRegistryHolder registryHolder;

    @Test
    public void testVersion() {
        resetRegistry();
        int expectedVersion = 0;
        Assert.assertEquals("Initial version should be 0", expectedVersion, registryHolder.getVersion());
        registryHolder.addJars(new HashMap(), (++expectedVersion));
        Assert.assertEquals("Version can change if no jars were added.", expectedVersion, registryHolder.getVersion());
        fillInRegistry((++expectedVersion));
        Assert.assertEquals("Version should have incremented by 1", expectedVersion, registryHolder.getVersion());
        registryHolder.removeJar(FunctionRegistryHolderTest.built_in);
        Assert.assertEquals("Version should have incremented by 1", expectedVersion, registryHolder.getVersion());
        fillInRegistry((++expectedVersion));
        Assert.assertEquals("Version should have incremented by 1", expectedVersion, registryHolder.getVersion());
        fillInRegistry((++expectedVersion));
        Assert.assertEquals("Version should have incremented by 1", expectedVersion, registryHolder.getVersion());
    }

    @Test
    public void testAddJars() {
        resetRegistry();
        List<String> jars = new ArrayList<>();
        ListMultimap<String, DrillFuncHolder> functionsWithHolders = ArrayListMultimap.create();
        ListMultimap<String, String> functionsWithSignatures = ArrayListMultimap.create();
        Set<String> functionsSet = new HashSet<>();
        for (Map.Entry<String, List<FunctionHolder>> jar : FunctionRegistryHolderTest.newJars.entrySet()) {
            jars.add(jar.getKey());
            for (FunctionHolder functionHolder : jar.getValue()) {
                functionsWithHolders.put(functionHolder.getName(), functionHolder.getHolder());
                functionsWithSignatures.put(functionHolder.getName(), functionHolder.getSignature());
                functionsSet.add(functionHolder.getName());// Track unique function names

            }
        }
        int expectedVersion = 0;
        registryHolder.addJars(FunctionRegistryHolderTest.newJars, (++expectedVersion));
        Assert.assertEquals("Version number should match", expectedVersion, registryHolder.getVersion());
        compareTwoLists(jars, registryHolder.getAllJarNames());
        Assert.assertEquals(functionsSet.size(), registryHolder.functionsSize());
        compareListMultimaps(functionsWithHolders, registryHolder.getAllFunctionsWithHolders());
        compareListMultimaps(functionsWithSignatures, registryHolder.getAllFunctionsWithSignatures());
    }

    @Test
    public void testAddTheSameJars() {
        resetRegistry();
        Set<String> functionsSet = new HashSet<>();
        List<String> jars = new ArrayList<>();
        ListMultimap<String, DrillFuncHolder> functionsWithHolders = ArrayListMultimap.create();
        ListMultimap<String, String> functionsWithSignatures = ArrayListMultimap.create();
        for (Map.Entry<String, List<FunctionHolder>> jar : FunctionRegistryHolderTest.newJars.entrySet()) {
            jars.add(jar.getKey());
            for (FunctionHolder functionHolder : jar.getValue()) {
                functionsWithHolders.put(functionHolder.getName(), functionHolder.getHolder());
                functionsWithSignatures.put(functionHolder.getName(), functionHolder.getSignature());
                functionsSet.add(functionHolder.getName());// Track unique function names

            }
        }
        int expectedVersion = 0;
        registryHolder.addJars(FunctionRegistryHolderTest.newJars, (++expectedVersion));
        Assert.assertEquals("Version number should match", expectedVersion, registryHolder.getVersion());
        compareTwoLists(jars, registryHolder.getAllJarNames());
        Assert.assertEquals(functionsSet.size(), registryHolder.functionsSize());
        compareListMultimaps(functionsWithHolders, registryHolder.getAllFunctionsWithHolders());
        compareListMultimaps(functionsWithSignatures, registryHolder.getAllFunctionsWithSignatures());
        // adding the same jars should not cause adding duplicates, should override existing jars only
        registryHolder.addJars(FunctionRegistryHolderTest.newJars, (++expectedVersion));
        Assert.assertEquals("Version number should match", expectedVersion, registryHolder.getVersion());
        compareTwoLists(jars, registryHolder.getAllJarNames());
        Assert.assertEquals(functionsSet.size(), registryHolder.functionsSize());
        compareListMultimaps(functionsWithHolders, registryHolder.getAllFunctionsWithHolders());
        compareListMultimaps(functionsWithSignatures, registryHolder.getAllFunctionsWithSignatures());
    }

    @Test
    public void testRemoveJar() {
        registryHolder.removeJar(FunctionRegistryHolderTest.built_in);
        Assert.assertFalse("Jar should be absent", registryHolder.containsJar(FunctionRegistryHolderTest.built_in));
        Assert.assertTrue("Jar should be present", registryHolder.containsJar(FunctionRegistryHolderTest.udf_jar));
        Assert.assertEquals("Functions size should match", FunctionRegistryHolderTest.newJars.get(FunctionRegistryHolderTest.udf_jar).size(), registryHolder.functionsSize());
    }

    @Test
    public void testGetAllJarNames() {
        List<String> expectedResult = new ArrayList(FunctionRegistryHolderTest.newJars.keySet());
        compareTwoLists(expectedResult, registryHolder.getAllJarNames());
    }

    @Test
    public void testGetAllJarsWithFunctionHolders() {
        Map<String, List<FunctionHolder>> fnHoldersInRegistry = registryHolder.getAllJarsWithFunctionHolders();
        // Iterate and confirm lists are same
        for (String jarName : FunctionRegistryHolderTest.newJars.keySet()) {
            List<DrillFuncHolder> expectedHolderList = // Extract DrillFuncHolder
            FunctionRegistryHolderTest.newJars.get(jarName).stream().map(FunctionHolder::getHolder).collect(Collectors.toList());
            List<DrillFuncHolder> testHolderList = // Extract DrillFuncHolder
            fnHoldersInRegistry.get(jarName).stream().map(FunctionHolder::getHolder).collect(Collectors.toList());
            compareTwoLists(expectedHolderList, testHolderList);
        }
        Map<String, String> shuffleFunctionMap = new HashMap<>();
        // Confirm that same function spans multiple jars with different signatures
        // Init: Expected Map of items
        for (String jarName : FunctionRegistryHolderTest.newJars.keySet()) {
            for (FunctionHolder funcHolder : FunctionRegistryHolderTest.newJars.get(jarName)) {
                if (FunctionRegistryHolderTest.SHUFFLE_FUNC_NAME.equals(funcHolder.getName())) {
                    shuffleFunctionMap.put(funcHolder.getSignature(), jarName);
                }
            }
        }
        // Test: Remove items from ExpectedMap based on match from testJar's functionHolder items
        for (String testJar : registryHolder.getAllJarNames()) {
            for (FunctionHolder funcHolder : fnHoldersInRegistry.get(testJar)) {
                if (FunctionRegistryHolderTest.SHUFFLE_FUNC_NAME.equals(funcHolder.getName())) {
                    String testSignature = funcHolder.getSignature();
                    String expectedJar = shuffleFunctionMap.get(testSignature);
                    if (testJar.equals(expectedJar)) {
                        shuffleFunctionMap.remove(testSignature);
                    }
                }
            }
        }
        Assert.assertTrue(shuffleFunctionMap.isEmpty());
    }

    @Test
    public void testGetFunctionNamesByJar() {
        List<String> expectedResult = FunctionRegistryHolderTest.newJars.get(FunctionRegistryHolderTest.built_in).stream().map(FunctionHolder::getName).collect(Collectors.toList());
        compareTwoLists(expectedResult, registryHolder.getFunctionNamesByJar(FunctionRegistryHolderTest.built_in));
    }

    @Test
    public void testGetAllFunctionsWithHoldersWithVersion() {
        ListMultimap<String, DrillFuncHolder> expectedResult = ArrayListMultimap.create();
        for (List<FunctionHolder> functionHolders : FunctionRegistryHolderTest.newJars.values()) {
            for (FunctionHolder functionHolder : functionHolders) {
                expectedResult.put(functionHolder.getName(), functionHolder.getHolder());
            }
        }
        AtomicInteger version = new AtomicInteger();
        compareListMultimaps(expectedResult, registryHolder.getAllFunctionsWithHolders(version));
        Assert.assertEquals("Version number should match", version.get(), registryHolder.getVersion());
    }

    @Test
    public void testGetAllFunctionsWithHolders() {
        ListMultimap<String, DrillFuncHolder> expectedResult = ArrayListMultimap.create();
        for (List<FunctionHolder> functionHolders : FunctionRegistryHolderTest.newJars.values()) {
            for (FunctionHolder functionHolder : functionHolders) {
                expectedResult.put(functionHolder.getName(), functionHolder.getHolder());
            }
        }
        compareListMultimaps(expectedResult, registryHolder.getAllFunctionsWithHolders());
    }

    @Test
    public void testGetAllFunctionsWithSignatures() {
        ListMultimap<String, String> expectedResult = ArrayListMultimap.create();
        for (List<FunctionHolder> functionHolders : FunctionRegistryHolderTest.newJars.values()) {
            for (FunctionHolder functionHolder : functionHolders) {
                expectedResult.put(functionHolder.getName(), functionHolder.getSignature());
            }
        }
        compareListMultimaps(expectedResult, registryHolder.getAllFunctionsWithSignatures());
    }

    @Test
    public void testGetHoldersByFunctionNameWithVersion() {
        List<DrillFuncHolder> expectedResult = FunctionRegistryHolderTest.newJars.values().stream().flatMap(Collection::stream).filter(( f) -> LOWER_FUNC_NAME.equals(f.getName())).map(FunctionHolder::getHolder).collect(Collectors.toList());
        Assert.assertFalse(expectedResult.isEmpty());
        AtomicInteger version = new AtomicInteger();
        compareTwoLists(expectedResult, registryHolder.getHoldersByFunctionName(FunctionRegistryHolderTest.LOWER_FUNC_NAME, version));
        Assert.assertEquals("Version number should match", version.get(), registryHolder.getVersion());
    }

    @Test
    public void testGetHoldersByFunctionName() {
        List<DrillFuncHolder> expectedUniqueResult = new ArrayList<>();
        List<DrillFuncHolder> expectedMultipleResult = new ArrayList<>();
        for (List<FunctionHolder> functionHolders : FunctionRegistryHolderTest.newJars.values()) {
            for (FunctionHolder functionHolder : functionHolders) {
                if (FunctionRegistryHolderTest.LOWER_FUNC_NAME.equals(functionHolder.getName())) {
                    expectedUniqueResult.add(functionHolder.getHolder());
                } else
                    if (FunctionRegistryHolderTest.SHUFFLE_FUNC_NAME.equals(functionHolder.getName())) {
                        expectedMultipleResult.add(functionHolder.getHolder());
                    }

            }
        }
        // Test for function with one signature
        Assert.assertFalse(expectedUniqueResult.isEmpty());
        compareTwoLists(expectedUniqueResult, registryHolder.getHoldersByFunctionName(FunctionRegistryHolderTest.LOWER_FUNC_NAME));
        // Test for function with multiple signatures
        Assert.assertFalse(expectedMultipleResult.isEmpty());
        compareTwoLists(expectedMultipleResult, registryHolder.getHoldersByFunctionName(FunctionRegistryHolderTest.SHUFFLE_FUNC_NAME));
    }

    @Test
    public void testContainsJar() {
        Assert.assertTrue("Jar should be present in registry holder", registryHolder.containsJar(FunctionRegistryHolderTest.built_in));
        Assert.assertFalse("Jar should be absent in registry holder", registryHolder.containsJar("unknown.jar"));
    }

    @Test
    public void testFunctionsSize() {
        int fnCountInRegistryHolder = 0;
        int fnCountInNewJars = 0;
        Set<String> functionNameSet = new HashSet<>();
        for (List<FunctionHolder> functionHolders : FunctionRegistryHolderTest.newJars.values()) {
            for (FunctionHolder functionHolder : functionHolders) {
                functionNameSet.add(functionHolder.getName());// Track unique function names

                fnCountInNewJars++;// Track all functions

            }
        }
        Assert.assertEquals("Unique function name count should match", functionNameSet.size(), registryHolder.functionsSize());
        for (String jarName : registryHolder.getAllJarNames()) {
            fnCountInRegistryHolder += registryHolder.getFunctionNamesByJar(jarName).size();
        }
        Assert.assertEquals("Function count should match", fnCountInNewJars, fnCountInRegistryHolder);
    }

    @Test
    public void testJarNameByFunctionSignature() {
        FunctionHolder functionHolder = FunctionRegistryHolderTest.newJars.get(FunctionRegistryHolderTest.built_in).get(0);
        Assert.assertEquals("Jar name should match", FunctionRegistryHolderTest.built_in, registryHolder.getJarNameByFunctionSignature(functionHolder.getName(), functionHolder.getSignature()));
        Assert.assertNull("Jar name should be null", registryHolder.getJarNameByFunctionSignature("unknown_function", "unknown_function(unknown-input)"));
    }
}

