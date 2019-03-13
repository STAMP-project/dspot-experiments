/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.compiler.integrationtests;


import ConsequenceMetaData.Field;
import ConsequenceMetaData.Statement;
import ConsequenceMetaData.Statement.Type.INSERT;
import ConsequenceMetaData.Statement.Type.MODIFY;
import ConsequenceMetaData.Statement.Type.RETRACT;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.rule.ConsequenceMetaData;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.KieBase;


public class RuleMetadataTest extends CommonTestMethodBase {
    @Test
    public void testModify() {
        String rule1 = "modify( $a ) { setA( 20 ), setB( $bb ) }";
        KieBase kbase = getKnowledgeBase(rule1);
        RuleImpl rule = getRule(kbase, "R0");
        ConsequenceMetaData consequenceMetaData = rule.getConsequenceMetaData();
        Assert.assertEquals(1, consequenceMetaData.getStatements().size());
        ConsequenceMetaData.Statement statment = consequenceMetaData.getStatements().get(0);
        Assert.assertEquals(MODIFY, statment.getType());
        Assert.assertEquals("org.drools.A", statment.getFactClassName());
        Assert.assertEquals(2, statment.getFields().size());
        ConsequenceMetaData.Field field1 = statment.getFields().get(0);
        Assert.assertEquals("a", field1.getName());
        Assert.assertEquals("20", field1.getValue());
        Assert.assertTrue(field1.isLiteral());
        ConsequenceMetaData.Field field2 = statment.getFields().get(1);
        Assert.assertEquals("b", field2.getName());
        Assert.assertEquals("$bb", field2.getValue());
        Assert.assertFalse(field2.isLiteral());
    }

    @Test
    public void testModify2() {
        String rule1 = "modify( $a ) { setC( $bc ) };\n modify( $b ) { c = \"Hello\" };";
        KieBase kbase = getKnowledgeBase(rule1);
        RuleImpl rule = getRule(kbase, "R0");
        ConsequenceMetaData consequenceMetaData = rule.getConsequenceMetaData();
        Assert.assertEquals(2, consequenceMetaData.getStatements().size());
        ConsequenceMetaData.Statement statment1 = consequenceMetaData.getStatements().get(0);
        Assert.assertEquals(MODIFY, statment1.getType());
        Assert.assertEquals("org.drools.A", statment1.getFactClassName());
        Assert.assertEquals(1, statment1.getFields().size());
        ConsequenceMetaData.Field field1 = statment1.getFields().get(0);
        Assert.assertEquals("c", field1.getName());
        Assert.assertEquals("$bc", field1.getValue());
        Assert.assertFalse(field1.isLiteral());
        ConsequenceMetaData.Statement statment2 = consequenceMetaData.getStatements().get(1);
        Assert.assertEquals(MODIFY, statment2.getType());
        Assert.assertEquals(RuleMetadataTest.B.class.getName(), statment2.getFactClassName());
        Assert.assertEquals(1, statment2.getFields().size());
        ConsequenceMetaData.Field field2 = statment2.getFields().get(0);
        Assert.assertEquals("c", field2.getName());
        Assert.assertEquals("\"Hello\"", field2.getValue());
        Assert.assertTrue(field2.isLiteral());
    }

    @Test
    public void testRetract() {
        String rule1 = "retract( $b );";
        KieBase kbase = getKnowledgeBase(rule1);
        RuleImpl rule = getRule(kbase, "R0");
        ConsequenceMetaData consequenceMetaData = rule.getConsequenceMetaData();
        Assert.assertEquals(1, consequenceMetaData.getStatements().size());
        ConsequenceMetaData.Statement statment = consequenceMetaData.getStatements().get(0);
        Assert.assertEquals(RETRACT, statment.getType());
        Assert.assertEquals(RuleMetadataTest.B.class.getName(), statment.getFactClassName());
    }

    @Test
    public void testRetractWithFunction() {
        String rule1 = "retract( getA($a) );";
        KieBase kbase = getKnowledgeBase(rule1);
        RuleImpl rule = getRule(kbase, "R0");
        ConsequenceMetaData consequenceMetaData = rule.getConsequenceMetaData();
        Assert.assertEquals(1, consequenceMetaData.getStatements().size());
        ConsequenceMetaData.Statement statment = consequenceMetaData.getStatements().get(0);
        Assert.assertEquals(RETRACT, statment.getType());
        Assert.assertEquals("org.drools.A", statment.getFactClassName());
    }

    @Test
    public void testUpdate() {
        String rule1 = "$a.setA( 20 );\n $a.setB( $bb );\n update( $a );";
        KieBase kbase = getKnowledgeBase(rule1);
        RuleImpl rule = getRule(kbase, "R0");
        ConsequenceMetaData consequenceMetaData = rule.getConsequenceMetaData();
        Assert.assertEquals(1, consequenceMetaData.getStatements().size());
        ConsequenceMetaData.Statement statment = consequenceMetaData.getStatements().get(0);
        Assert.assertEquals(MODIFY, statment.getType());
        Assert.assertEquals("org.drools.A", statment.getFactClassName());
        Assert.assertEquals(2, statment.getFields().size());
        ConsequenceMetaData.Field field1 = statment.getFields().get(0);
        Assert.assertEquals("a", field1.getName());
        Assert.assertEquals("20", field1.getValue());
        Assert.assertTrue(field1.isLiteral());
        ConsequenceMetaData.Field field2 = statment.getFields().get(1);
        Assert.assertEquals("b", field2.getName());
        Assert.assertEquals("$bb", field2.getValue());
        Assert.assertFalse(field2.isLiteral());
    }

    @Test
    public void testUpdate2() {
        String rule1 = "$a.setC( $bc );\n $b.c = \"Hello\";\n update( $a );\n update( $b );";
        KieBase kbase = getKnowledgeBase(rule1);
        RuleImpl rule = getRule(kbase, "R0");
        ConsequenceMetaData consequenceMetaData = rule.getConsequenceMetaData();
        Assert.assertEquals(2, consequenceMetaData.getStatements().size());
        ConsequenceMetaData.Statement statment1 = consequenceMetaData.getStatements().get(0);
        Assert.assertEquals(MODIFY, statment1.getType());
        Assert.assertEquals("org.drools.A", statment1.getFactClassName());
        Assert.assertEquals(1, statment1.getFields().size());
        ConsequenceMetaData.Field field1 = statment1.getFields().get(0);
        Assert.assertEquals("c", field1.getName());
        Assert.assertEquals("$bc", field1.getValue());
        Assert.assertFalse(field1.isLiteral());
        ConsequenceMetaData.Statement statment2 = consequenceMetaData.getStatements().get(1);
        Assert.assertEquals(MODIFY, statment2.getType());
        Assert.assertEquals(RuleMetadataTest.B.class.getName(), statment2.getFactClassName());
        Assert.assertEquals(1, statment2.getFields().size());
        ConsequenceMetaData.Field field2 = statment2.getFields().get(0);
        Assert.assertEquals("c", field2.getName());
        Assert.assertEquals("\"Hello\"", field2.getValue());
        Assert.assertTrue(field2.isLiteral());
    }

    @Test
    public void testInsert() {
        String rule1 = "insert( new A(1, $bb, \"3\") );";
        KieBase kbase = getKnowledgeBase(rule1);
        RuleImpl rule = getRule(kbase, "R0");
        ConsequenceMetaData consequenceMetaData = rule.getConsequenceMetaData();
        Assert.assertEquals(1, consequenceMetaData.getStatements().size());
        ConsequenceMetaData.Statement statment = consequenceMetaData.getStatements().get(0);
        Assert.assertEquals(INSERT, statment.getType());
        Assert.assertEquals("org.drools.A", statment.getFactClassName());
        Assert.assertEquals(3, statment.getFields().size());
        ConsequenceMetaData.Field field1 = statment.getFields().get(0);
        Assert.assertEquals("a", field1.getName());
        Assert.assertEquals("1", field1.getValue());
        Assert.assertTrue(field1.isLiteral());
        ConsequenceMetaData.Field field2 = statment.getFields().get(1);
        Assert.assertEquals("b", field2.getName());
        Assert.assertEquals("$bb", field2.getValue());
        Assert.assertFalse(field2.isLiteral());
        ConsequenceMetaData.Field field3 = statment.getFields().get(2);
        Assert.assertEquals("c", field3.getName());
        Assert.assertEquals("\"3\"", field3.getValue());
        Assert.assertTrue(field3.isLiteral());
    }

    @Test
    public void testInsert2() {
        String rule1 = "insert( new B(1, $ab) );";
        KieBase kbase = getKnowledgeBase(rule1);
        RuleImpl rule = getRule(kbase, "R0");
        ConsequenceMetaData consequenceMetaData = rule.getConsequenceMetaData();
        Assert.assertEquals(1, consequenceMetaData.getStatements().size());
        ConsequenceMetaData.Statement statment = consequenceMetaData.getStatements().get(0);
        Assert.assertEquals(INSERT, statment.getType());
        Assert.assertEquals(RuleMetadataTest.B.class.getName(), statment.getFactClassName());
    }

    public static class B {
        public int a;

        public int b;

        public String c;

        public B() {
        }

        public B(int a, int b) {
            this.a = a;
            this.b = b;
        }

        public int getA() {
            return a;
        }

        public void setA(int a) {
            this.a = a;
        }

        public int getB() {
            return b;
        }

        public void setB(int b) {
            this.b = b;
        }

        public String getC() {
            return c;
        }

        public void setC(String c) {
            this.c = c;
        }
    }
}

