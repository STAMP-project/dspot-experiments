/**
 * Copyright 2005 JBoss Inc
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
package org.drools.modelcompiler;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.drools.modelcompiler.domain.ChildFactWithObject;
import org.drools.modelcompiler.domain.Person;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.runtime.KieSession;


public class TypeCoercionTest extends BaseModelTest {
    public TypeCoercionTest(BaseModelTest.RUN_TYPE testRunType) {
        super(testRunType);
    }

    @Test
    public void testEqualCoercion() {
        String str = ((((((("import " + (Person.class.getCanonicalName())) + "\n") + "global java.util.List list\n") + "rule R when\n") + "    Person( $name : name == 40 )") + "then\n") + "    list.add($name);") + "end ";
        KieSession ksession = getKieSession(str);
        List<String> list = new ArrayList<>();
        ksession.setGlobal("list", list);
        Person person1 = new Person("40");
        ksession.insert(person1);
        ksession.fireAllRules();
        Assert.assertEquals(1, list.size());
        Assert.assertEquals("40", list.get(0));
    }

    @Test
    public void testComparisonCoercion() {
        String str = ((((((("import " + (Person.class.getCanonicalName())) + "\n") + "global java.util.List list\n") + "rule R when\n") + "    Person( $name : name < \"50\" )") + "then\n") + "    list.add($name);") + "end ";
        KieSession ksession = getKieSession(str);
        List<String> list = new ArrayList<>();
        ksession.setGlobal("list", list);
        Person person1 = new Person("40");
        ksession.insert(person1);
        ksession.fireAllRules();
        Assert.assertEquals(1, list.size());
        Assert.assertEquals("40", list.get(0));
    }

    @Test
    public void testComparisonCoercion2() {
        String str = ((((((("import " + (Person.class.getCanonicalName())) + "\n") + "global java.util.List list\n") + "rule R when\n") + "    Person( $name : name, age < \"50\" )") + "then\n") + "    list.add($name);") + "end ";
        KieSession ksession = getKieSession(str);
        List<String> list = new ArrayList<>();
        ksession.setGlobal("list", list);
        Person person1 = new Person("Mario", 40);
        ksession.insert(person1);
        ksession.fireAllRules();
        Assert.assertEquals(1, list.size());
        Assert.assertEquals("Mario", list.get(0));
    }

    @Test
    public void testPrimitiveCoercion() {
        String str = ((((((("import " + (Person.class.getCanonicalName())) + "\n") + "global java.util.List list\n") + "rule R when\n") + "    $n : Number( doubleValue == 0 )") + "then\n") + "    list.add(\"\" + $n);") + "end ";
        KieSession ksession = getKieSession(str);
        List<String> list = new ArrayList<>();
        ksession.setGlobal("list", list);
        ksession.insert(0);
        ksession.fireAllRules();
        Assert.assertEquals(1, list.size());
        Assert.assertEquals("0", list.get(0));
    }

    public static class DoubleHolder {
        public Double getValue() {
            return 80.0;
        }
    }

    public static class LongHolder {
        public Long getValue() {
            return 80L;
        }
    }

    @Test
    public void testDoubleToInt() {
        final String drl1 = ((((("import " + (TypeCoercionTest.DoubleHolder.class.getCanonicalName())) + ";\n") + "rule R when\n") + "    DoubleHolder( value == 80 )\n") + "then\n") + "end\n";
        KieSession ksession = getKieSession(drl1);
        ksession.insert(new TypeCoercionTest.DoubleHolder());
        Assert.assertEquals(1, ksession.fireAllRules());
    }

    @Test
    public void testLongToInt() {
        final String drl1 = ((((("import " + (TypeCoercionTest.LongHolder.class.getCanonicalName())) + ";\n") + "rule R when\n") + "    LongHolder( value == 80 )\n") + "then\n") + "end\n";
        KieSession ksession = getKieSession(drl1);
        ksession.insert(new TypeCoercionTest.LongHolder());
        Assert.assertEquals(1, ksession.fireAllRules());
    }

    @Test
    public void testJoinLongToDouble() {
        final String drl1 = ((((((((("import " + (TypeCoercionTest.DoubleHolder.class.getCanonicalName())) + ";\n") + "import ") + (TypeCoercionTest.LongHolder.class.getCanonicalName())) + ";\n") + "rule R when\n") + "    LongHolder( $l : value )\n") + "    DoubleHolder( value >= $l )\n") + "then\n") + "end\n";
        KieSession ksession = getKieSession(drl1);
        ksession.insert(new TypeCoercionTest.LongHolder());
        ksession.insert(new TypeCoercionTest.DoubleHolder());
        Assert.assertEquals(1, ksession.fireAllRules());
    }

    @Test
    public void testJoinDoubleToLong() {
        final String drl1 = ((((((((("import " + (TypeCoercionTest.DoubleHolder.class.getCanonicalName())) + ";\n") + "import ") + (TypeCoercionTest.LongHolder.class.getCanonicalName())) + ";\n") + "rule R when\n") + "    DoubleHolder( $d : value )\n") + "    LongHolder( value >= $d )\n") + "then\n") + "end\n";
        KieSession ksession = getKieSession(drl1);
        ksession.insert(new TypeCoercionTest.LongHolder());
        ksession.insert(new TypeCoercionTest.DoubleHolder());
        Assert.assertEquals(1, ksession.fireAllRules());
    }

    @Test
    public void testStringToDateComparison() {
        String str = ((((((("import " + (Date.class.getCanonicalName())) + ";\n") + "declare Flight departuretime : java.util.Date end\n") + "rule Init when then insert(new Flight(new Date(365L * 24 * 60 * 60 * 1000))); end\n") + "rule R when\n") + "    Flight( departuretime >= \"01-Jan-1970\" && departuretime <= \"01-Jan-2018\" )\n") + "then\n") + "end\n";
        KieSession ksession = getKieSession(str);
        ksession.insert(new ChildFactWithObject(5, 1, new Object[0]));
        Assert.assertEquals(2, ksession.fireAllRules());
    }
}

