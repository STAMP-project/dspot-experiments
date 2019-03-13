/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.support.component;


import ApiMethodHelper.MatchType.EXACT;
import ApiMethodHelper.MatchType.SUBSET;
import ApiMethodHelper.MatchType.SUPER_SET;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class ApiMethodHelperTest {
    private static ApiMethodHelperTest.TestMethod[] sayHis = new ApiMethodHelperTest.TestMethod[]{ ApiMethodHelperTest.TestMethod.SAYHI, ApiMethodHelperTest.TestMethod.SAYHI_1 };

    private static ApiMethodHelper<ApiMethodHelperTest.TestMethod> apiMethodHelper;

    static {
        final HashMap<String, String> aliases = new HashMap<>();
        aliases.put("say(.*)", "$1");
        ApiMethodHelperTest.apiMethodHelper = new ApiMethodHelper(ApiMethodHelperTest.TestMethod.class, aliases, Arrays.asList("names"));
    }

    @Test
    public void testGetCandidateMethods() {
        List<ApiMethod> methods = ApiMethodHelperTest.apiMethodHelper.getCandidateMethods("sayHi");
        Assert.assertEquals("Can't find sayHi(*)", 2, methods.size());
        methods = ApiMethodHelperTest.apiMethodHelper.getCandidateMethods("hi");
        Assert.assertEquals("Can't find sayHi(name)", 2, methods.size());
        methods = ApiMethodHelperTest.apiMethodHelper.getCandidateMethods("hi", Arrays.asList("name"));
        Assert.assertEquals("Can't find sayHi(name)", 1, methods.size());
        methods = ApiMethodHelperTest.apiMethodHelper.getCandidateMethods("greetMe");
        Assert.assertEquals("Can't find greetMe(name)", 1, methods.size());
        methods = ApiMethodHelperTest.apiMethodHelper.getCandidateMethods("greetUs", Arrays.asList("name1"));
        Assert.assertEquals("Can't find greetUs(name1, name2)", 1, methods.size());
        methods = ApiMethodHelperTest.apiMethodHelper.getCandidateMethods("greetAll", Arrays.asList("nameMap"));
        Assert.assertEquals("Can't find greetAll(nameMap)", 1, methods.size());
        methods = ApiMethodHelperTest.apiMethodHelper.getCandidateMethods("greetInnerChild", Arrays.asList("child"));
        Assert.assertEquals("Can't find greetInnerChild(child)", 1, methods.size());
    }

    @Test
    public void testFilterMethods() {
        List<ApiMethod> methods = ApiMethodHelperTest.apiMethodHelper.filterMethods(Arrays.asList(ApiMethodHelperTest.sayHis), EXACT);
        Assert.assertEquals("Exact match failed for sayHi()", 1, methods.size());
        Assert.assertEquals("Exact match failed for sayHi()", ApiMethodHelperTest.TestMethod.SAYHI, methods.get(0));
        methods = ApiMethodHelperTest.apiMethodHelper.filterMethods(Arrays.asList(ApiMethodHelperTest.sayHis), SUBSET);
        Assert.assertEquals("Subset match failed for sayHi(*)", 2, methods.size());
        methods = ApiMethodHelperTest.apiMethodHelper.filterMethods(Arrays.asList(ApiMethodHelperTest.sayHis), SUBSET, Arrays.asList("name"));
        Assert.assertEquals("Subset match failed for sayHi(name)", 1, methods.size());
        Assert.assertEquals("Exact match failed for sayHi()", ApiMethodHelperTest.TestMethod.SAYHI_1, methods.get(0));
        methods = ApiMethodHelperTest.apiMethodHelper.filterMethods(Arrays.asList(ApiMethodHelperTest.sayHis), SUPER_SET, Arrays.asList("name"));
        Assert.assertEquals("Super set match failed for sayHi(name)", 1, methods.size());
        Assert.assertEquals("Exact match failed for sayHi()", ApiMethodHelperTest.TestMethod.SAYHI_1, methods.get(0));
        methods = ApiMethodHelperTest.apiMethodHelper.filterMethods(Arrays.asList(ApiMethodHelperTest.TestMethod.values()), SUPER_SET, Arrays.asList("name"));
        Assert.assertEquals("Super set match failed for sayHi(name)", 2, methods.size());
        // test nullable names
        methods = ApiMethodHelperTest.apiMethodHelper.filterMethods(Arrays.asList(ApiMethodHelperTest.TestMethod.GREETALL, ApiMethodHelperTest.TestMethod.GREETALL_1, ApiMethodHelperTest.TestMethod.GREETALL_2), SUPER_SET);
        Assert.assertEquals("Super set match with null args failed for greetAll(names)", 1, methods.size());
    }

    @Test
    public void testGetArguments() {
        Assert.assertEquals("GetArguments failed for hi", 2, ApiMethodHelperTest.apiMethodHelper.getArguments("hi").size());
        Assert.assertEquals("GetArguments failed for greetMe", 2, ApiMethodHelperTest.apiMethodHelper.getArguments("greetMe").size());
        Assert.assertEquals("GetArguments failed for greetUs", 4, ApiMethodHelperTest.apiMethodHelper.getArguments("greetUs").size());
        Assert.assertEquals("GetArguments failed for greetAll", 6, ApiMethodHelperTest.apiMethodHelper.getArguments("greetAll").size());
        Assert.assertEquals("GetArguments failed for greetInnerChild", 2, ApiMethodHelperTest.apiMethodHelper.getArguments("greetInnerChild").size());
    }

    @Test
    public void testGetMissingProperties() throws Exception {
        Assert.assertEquals("Missing properties for hi", 1, ApiMethodHelperTest.apiMethodHelper.getMissingProperties("hi", new HashSet<String>()).size());
        final HashSet<String> argNames = new HashSet<>();
        argNames.add("name");
        Assert.assertEquals("Missing properties for greetMe", 0, ApiMethodHelperTest.apiMethodHelper.getMissingProperties("greetMe", argNames).size());
        argNames.clear();
        argNames.add("name1");
        Assert.assertEquals("Missing properties for greetMe", 1, ApiMethodHelperTest.apiMethodHelper.getMissingProperties("greetUs", argNames).size());
    }

    @Test
    public void testAllArguments() throws Exception {
        Assert.assertEquals("Get all arguments", 8, ApiMethodHelperTest.apiMethodHelper.allArguments().size());
    }

    @Test
    public void testGetType() throws Exception {
        Assert.assertEquals("Get type name", String.class, ApiMethodHelperTest.apiMethodHelper.getType("name"));
        Assert.assertEquals("Get type name1", String.class, ApiMethodHelperTest.apiMethodHelper.getType("name1"));
        Assert.assertEquals("Get type name2", String.class, ApiMethodHelperTest.apiMethodHelper.getType("name2"));
        Assert.assertEquals("Get type nameMap", Map.class, ApiMethodHelperTest.apiMethodHelper.getType("nameMap"));
        Assert.assertEquals("Get type child", TestProxy.InnerChild.class, ApiMethodHelperTest.apiMethodHelper.getType("child"));
    }

    @Test
    public void testGetHighestPriorityMethod() throws Exception {
        Assert.assertEquals("Get highest priority method", ApiMethodHelperTest.TestMethod.SAYHI_1, ApiMethodHelper.getHighestPriorityMethod(Arrays.asList(ApiMethodHelperTest.sayHis)));
    }

    @Test
    public void testInvokeMethod() throws Exception {
        TestProxy proxy = new TestProxy();
        Assert.assertEquals("sayHi()", "Hello!", ApiMethodHelper.invokeMethod(proxy, ApiMethodHelperTest.TestMethod.SAYHI, Collections.<String, Object>emptyMap()));
        final HashMap<String, Object> properties = new HashMap<>();
        properties.put("name", "Dave");
        Assert.assertEquals("sayHi(name)", "Hello Dave", ApiMethodHelper.invokeMethod(proxy, ApiMethodHelperTest.TestMethod.SAYHI_1, properties));
        Assert.assertEquals("greetMe(name)", "Greetings Dave", ApiMethodHelper.invokeMethod(proxy, ApiMethodHelperTest.TestMethod.GREETME, properties));
        properties.clear();
        properties.put("name1", "Dave");
        properties.put("name2", "Frank");
        Assert.assertEquals("greetUs(name1, name2)", "Greetings Dave, Frank", ApiMethodHelper.invokeMethod(proxy, ApiMethodHelperTest.TestMethod.GREETUS, properties));
        properties.clear();
        properties.put("names", new String[]{ "Dave", "Frank" });
        Assert.assertEquals("greetAll(names)", "Greetings Dave, Frank", ApiMethodHelper.invokeMethod(proxy, ApiMethodHelperTest.TestMethod.GREETALL, properties));
        properties.clear();
        Map<String, String> nameMap = new HashMap<>();
        nameMap.put("Dave", "Hello");
        nameMap.put("Frank", "Goodbye");
        properties.put("nameMap", nameMap);
        final Map<String, String> result = ((Map<String, String>) (ApiMethodHelper.invokeMethod(proxy, ApiMethodHelperTest.TestMethod.GREETALL_2, properties)));
        Assert.assertNotNull("greetAll(nameMap)", result);
        for (Map.Entry<String, String> entry : result.entrySet()) {
            Assert.assertTrue("greetAll(nameMap)", entry.getValue().endsWith(entry.getKey()));
        }
        // test with a derived proxy
        proxy = new TestProxy() {
            @Override
            public String sayHi(String name) {
                return "Howdy " + name;
            }
        };
        properties.clear();
        properties.put("name", "Dave");
        Assert.assertEquals("Derived sayHi(name)", "Howdy Dave", ApiMethodHelper.invokeMethod(proxy, ApiMethodHelperTest.TestMethod.SAYHI_1, properties));
    }

    enum TestMethod implements ApiMethod {

        SAYHI(String.class, "sayHi"),
        SAYHI_1(String.class, "sayHi", ApiMethodArg.arg("name", String.class)),
        GREETME(String.class, "greetMe", ApiMethodArg.arg("name", String.class)),
        GREETUS(String.class, "greetUs", ApiMethodArg.arg("name1", String.class), ApiMethodArg.arg("name2", String.class)),
        GREETALL(String.class, "greetAll", ApiMethodArg.arg("names", new String[0].getClass())),
        GREETALL_1(String.class, "greetAll", ApiMethodArg.arg("nameList", List.class)),
        GREETALL_2(Map.class, "greetAll", ApiMethodArg.arg("nameMap", Map.class)),
        GREETTIMES(new String[0].getClass(), "greetTimes", ApiMethodArg.arg("name", String.class), ApiMethodArg.arg("times", int.class)),
        GREETINNERCHILD(new String[0].getClass(), "greetInnerChild", ApiMethodArg.arg("child", TestProxy.InnerChild.class));
        private final ApiMethod apiMethod;

        TestMethod(Class<?> resultType, String name, ApiMethodArg... args) {
            this.apiMethod = new ApiMethodImpl(TestProxy.class, resultType, name, args);
        }

        @Override
        public String getName() {
            return apiMethod.getName();
        }

        @Override
        public Class<?> getResultType() {
            return apiMethod.getResultType();
        }

        @Override
        public List<String> getArgNames() {
            return apiMethod.getArgNames();
        }

        @Override
        public List<Class<?>> getArgTypes() {
            return apiMethod.getArgTypes();
        }

        @Override
        public Method getMethod() {
            return apiMethod.getMethod();
        }
    }
}

