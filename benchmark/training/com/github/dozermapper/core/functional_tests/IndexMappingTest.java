/**
 * Copyright 2005-2019 Dozer Project
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
package com.github.dozermapper.core.functional_tests;


import com.github.dozermapper.core.functional_tests.runner.Proxied;
import com.github.dozermapper.core.vo.A;
import com.github.dozermapper.core.vo.Aliases;
import com.github.dozermapper.core.vo.B;
import com.github.dozermapper.core.vo.C;
import com.github.dozermapper.core.vo.D;
import com.github.dozermapper.core.vo.FieldValue;
import com.github.dozermapper.core.vo.FlatIndividual;
import com.github.dozermapper.core.vo.Individual;
import com.github.dozermapper.core.vo.Individuals;
import com.github.dozermapper.core.vo.index.Mccoy;
import com.github.dozermapper.core.vo.index.MccoyPrime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(Proxied.class)
public class IndexMappingTest extends AbstractFunctionalTest {
    @Test
    public void testMap1() {
        List<String> userNames = newInstance(ArrayList.class);
        userNames.add("username1");
        userNames.add("username2");
        String[] secondNames = new String[3];
        secondNames[0] = "secondName1";
        secondNames[1] = "secondName2";
        secondNames[2] = "secondName3";
        Individuals source = newInstance(Individuals.class);
        source.setUsernames(userNames);
        source.setSimpleField("a very simple field");
        source.setSecondNames(secondNames);
        Set<String> mySet = newInstance(HashSet.class);
        mySet.add("myString");
        source.setAddressSet(mySet);
        FlatIndividual dest = mapper.map(source, FlatIndividual.class);
        Assert.assertEquals(source.getUsernames().get(0), dest.getUsername1());
        Assert.assertEquals(source.getSimpleField(), dest.getSimpleField());
        Assert.assertEquals(source.getSecondNames()[1], dest.getSecondName1());
        Assert.assertEquals(source.getSecondNames()[2], dest.getSecondName2());
        Assert.assertEquals("myString", dest.getAddress());
    }

    @Test
    public void testMap1Inv() {
        FlatIndividual source = newInstance(FlatIndividual.class);
        source.setUsername1("username1");
        source.setUsername2("username2");
        source.setSimpleField("a simple field");
        source.setSecondName1("secondName1");
        source.setSecondName2("secondName2");
        source.setPrimaryAlias("aqqq");
        source.setThirdName("thirdName");
        Individuals dest = mapper.map(source, Individuals.class);
        Assert.assertEquals(source.getUsername1(), dest.getUsernames().get(0));
        Assert.assertEquals(dest.getIndividual().getUsername(), source.getUsername2());
        Assert.assertEquals(dest.getAliases().getOtherAliases()[0], "aqqq");
        Assert.assertEquals(source.getUsername2(), dest.getUsernames().get(1));
        Assert.assertEquals(source.getSimpleField(), dest.getSimpleField());
        Assert.assertEquals(source.getSecondName1(), dest.getSecondNames()[1]);
        Assert.assertEquals(source.getSecondName2(), dest.getSecondNames()[2]);
        Assert.assertEquals(source.getThirdName(), dest.getThirdNameElement1());
    }

    @Test
    public void testMap3() {
        List<String> userNames = newInstance(ArrayList.class);
        userNames.add("username1");
        userNames.add("username2");
        Individual nestedIndividual = newInstance(Individual.class);
        nestedIndividual.setUsername("nestedusername");
        String[] secondNames = new String[3];
        secondNames[0] = "secondName1";
        secondNames[1] = "secondName2";
        secondNames[2] = "secondName3";
        Individuals source = newInstance(Individuals.class);
        source.setUsernames(userNames);
        source.setIndividual(nestedIndividual);
        source.setSecondNames(secondNames);
        FlatIndividual dest = mapper.map(source, FlatIndividual.class);
        Assert.assertEquals(source.getUsernames().get(0), dest.getUsername1());
        Assert.assertEquals(source.getIndividual().getUsername(), dest.getUsername2());
        Assert.assertEquals(source.getSecondNames()[1], dest.getSecondName1());
        Assert.assertEquals(source.getSecondNames()[2], dest.getSecondName2());
    }

    @Test
    public void testNulls() {
        FlatIndividual source = newInstance(FlatIndividual.class);
        source.setSimpleField("a simplefield");
        Individuals dest = mapper.map(source, Individuals.class);
        Assert.assertEquals(source.getSimpleField(), dest.getSimpleField());
    }

    @Test
    public void testNullsInv() {
        Individuals source = newInstance(Individuals.class);
        source.setSimpleField("a simplefield");
        FlatIndividual dest = mapper.map(source, FlatIndividual.class);
        Assert.assertEquals(source.getSimpleField(), dest.getSimpleField());
    }

    @Test
    public void testNestedArray() {
        Individuals source = newInstance(Individuals.class);
        Aliases aliases = newInstance(Aliases.class);
        aliases.setOtherAliases(new String[]{ "other alias 1", "other alias 2" });
        source.setAliases(aliases);
        FlatIndividual dest = mapper.map(source, FlatIndividual.class);
        Assert.assertEquals("other alias 1", dest.getPrimaryAlias());
    }

    @Test
    public void testNotNullNestedIndexAtoD() {
        C c = newInstance(C.class);
        c.setValue("value1");
        B b = newInstance(B.class);
        b.setCs(new C[]{ c });
        A a = newInstance(A.class);
        a.setB(b);
        D d = mapper.map(a, D.class);
        Assert.assertEquals("value not translated", "value1", d.getValue());
    }

    @Test
    public void testNullNestedIndexAtoD() {
        A a = newInstance(A.class);
        D d = mapper.map(a, D.class);
        Assert.assertNull("value should not be translated", d.getValue());
    }

    @Test
    public void testNotNullNestedIndexDtoA() {
        D d = newInstance(D.class);
        d.setValue("value1");
        A a = mapper.map(d, A.class);
        Assert.assertEquals("value not translated", d.getValue(), a.getB().getCs()[0].getValue());
    }

    @Test
    public void testNullNestedIndexDtoA() {
        D d = newInstance(D.class);
        A a = mapper.map(d, A.class);
        Assert.assertNotNull(a);
    }

    @Test
    public void testStringToIndexedSet_UsingMapSetMethod() {
        mapper = getMapper("mappings/indexMapping.xml");
        Mccoy src = newInstance(Mccoy.class);
        src.setStringProperty(String.valueOf(System.currentTimeMillis()));
        MccoyPrime dest = mapper.map(src, MccoyPrime.class);
        Set<?> destSet = dest.getFieldValueObjects();
        Assert.assertNotNull("dest set should not be null", destSet);
        Assert.assertEquals("dest set should contain 1 entry", 1, destSet.size());
        Object entry = destSet.iterator().next();
        Assert.assertTrue("dest set entry should be instance of FieldValue", (entry instanceof FieldValue));
        Assert.assertEquals("invalid value for dest object", src.getStringProperty(), ((FieldValue) (entry)).getValue("stringProperty"));
    }
}

