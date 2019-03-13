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


import ApiMethodParser.ApiMethodModel;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.apache.camel.support.component.ArgumentSubstitutionParser.Substitution;
import org.junit.Assert;
import org.junit.Test;


public class ArgumentSubstitutionParserTest {
    private static final String PERSON = "person";

    @Test
    public void testParse() throws Exception {
        final Substitution[] adapters = new Substitution[4];
        adapters[0] = new Substitution(".+", "name", ArgumentSubstitutionParserTest.PERSON);
        adapters[1] = new Substitution("greet.+", "person([0-9]+)", "astronaut$1");
        adapters[2] = new Substitution(".+", "(.+)", "java.util.List", "$1List");
        adapters[3] = new Substitution(".+", "(.+)", ".*?(\\w++)\\[\\]", "$1Array", true);
        final ApiMethodParser<TestProxy> parser = new ArgumentSubstitutionParser(TestProxy.class, adapters);
        final ArrayList<String> signatures = new ArrayList<>();
        signatures.add("public String sayHi();");
        signatures.add("public String sayHi(final String name);");
        signatures.add("public final String greetMe(final String name);");
        signatures.add("public final String greetUs(final String name1, String name2);");
        signatures.add("public final String greetAll(String[] names);");
        signatures.add("public final String greetAll(java.util.List<String> names);");
        signatures.add("public final java.util.Map<String, String> greetAll(java.util.Map<String> nameMap);");
        signatures.add("public final String[] greetTimes(String name, int times);");
        signatures.add("public final String greetInnerChild(org.apache.camel.support.component.TestProxy.InnerChild child);");
        signatures.add("public final <T extends java.util.Date> T sayHiResource(java.util.Set<T> resourceType, String resourceId);");
        signatures.add("public final <T extends java.util.Date> T with(T theDate);");
        signatures.add("public final <T extends java.util.Date> String withDate(T theDate, Class<? extends java.util.Date> dateClass, Class<T> parameter, T parameters);");
        parser.setSignatures(signatures);
        final List<ApiMethodParser.ApiMethodModel> methodModels = parser.parse();
        Assert.assertEquals(12, methodModels.size());
        final ApiMethodParser.ApiMethodModel withDate = methodModels.get(11);
        Assert.assertEquals(String.class, withDate.getResultType());
        Assert.assertEquals(Date.class, withDate.getArguments().get(0).getType());
        final ApiMethodParser.ApiMethodModel sayHi1 = methodModels.get(8);
        Assert.assertEquals(ArgumentSubstitutionParserTest.PERSON, sayHi1.getArguments().get(0).getName());
        Assert.assertEquals("SAYHI_1", sayHi1.getUniqueName());
        ApiMethodParser.ApiMethodModel sayHiResource = methodModels.get(9);
        Assert.assertEquals(Date.class, sayHiResource.getResultType());
        Assert.assertEquals(Set.class, sayHiResource.getArguments().get(0).getType());
        Assert.assertEquals("resourceType", sayHiResource.getArguments().get(0).getName());
        Assert.assertEquals("resourceId", sayHiResource.getArguments().get(1).getName());
        Assert.assertEquals(String.class, sayHiResource.getArguments().get(1).getType());
        ApiMethodParser.ApiMethodModel with = methodModels.get(10);
        Assert.assertEquals(Date.class, with.getResultType());
        Assert.assertEquals(Date.class, with.getArguments().get(0).getType());
        final ApiMethodParser.ApiMethodModel greetMe = methodModels.get(4);
        Assert.assertEquals(ArgumentSubstitutionParserTest.PERSON, greetMe.getArguments().get(0).getName());
        final ApiMethodParser.ApiMethodModel greetUs = methodModels.get(6);
        Assert.assertEquals("astronaut1", greetUs.getArguments().get(0).getName());
        Assert.assertEquals("astronaut2", greetUs.getArguments().get(1).getName());
        final ApiMethodParser.ApiMethodModel greetAll = methodModels.get(0);
        Assert.assertEquals("personMap", greetAll.getArguments().get(0).getName());
        final ApiMethodParser.ApiMethodModel greetAll1 = methodModels.get(1);
        Assert.assertEquals("personsList", greetAll1.getArguments().get(0).getName());
        final ApiMethodParser.ApiMethodModel greetAll2 = methodModels.get(2);
        Assert.assertEquals("stringArray", greetAll2.getArguments().get(0).getName());
        final ApiMethodParser.ApiMethodModel greetInnerChild = methodModels.get(3);
        Assert.assertEquals("child", greetInnerChild.getArguments().get(0).getName());
    }
}

