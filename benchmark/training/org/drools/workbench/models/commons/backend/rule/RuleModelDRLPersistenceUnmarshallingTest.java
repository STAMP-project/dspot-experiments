/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.models.commons.backend.rule;


import BaseSingleFieldConstraint.TYPE_ENUM;
import BaseSingleFieldConstraint.TYPE_EXPR_BUILDER_VALUE;
import BaseSingleFieldConstraint.TYPE_LITERAL;
import BaseSingleFieldConstraint.TYPE_PREDICATE;
import BaseSingleFieldConstraint.TYPE_RET_VALUE;
import BaseSingleFieldConstraint.TYPE_UNDEFINED;
import BaseSingleFieldConstraint.TYPE_VARIABLE;
import CompositeFactPattern.COMPOSITE_TYPE_OR;
import DataType.TYPE_BOOLEAN;
import DataType.TYPE_COLLECTION;
import DataType.TYPE_COMPARABLE;
import DataType.TYPE_DATE;
import DataType.TYPE_NUMERIC;
import DataType.TYPE_NUMERIC_BIGDECIMAL;
import DataType.TYPE_NUMERIC_DOUBLE;
import DataType.TYPE_NUMERIC_FLOAT;
import DataType.TYPE_NUMERIC_INTEGER;
import DataType.TYPE_NUMERIC_LONG;
import DataType.TYPE_STRING;
import DataType.TYPE_THIS;
import FieldNatureType.TYPE_FORMULA;
import SingleFieldConstraint.TYPE_TEMPLATE;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.drools.compiler.lang.Expander;
import org.drools.compiler.lang.dsl.DSLMappingFile;
import org.drools.compiler.lang.dsl.DSLTokenizedMappingFile;
import org.drools.compiler.lang.dsl.DefaultExpander;
import org.drools.workbench.models.datamodel.rule.ActionCallMethod;
import org.drools.workbench.models.datamodel.rule.ActionFieldValue;
import org.drools.workbench.models.datamodel.rule.ActionGlobalCollectionAdd;
import org.drools.workbench.models.datamodel.rule.ActionInsertFact;
import org.drools.workbench.models.datamodel.rule.ActionInsertLogicalFact;
import org.drools.workbench.models.datamodel.rule.ActionSetField;
import org.drools.workbench.models.datamodel.rule.ActionUpdateField;
import org.drools.workbench.models.datamodel.rule.CEPWindow;
import org.drools.workbench.models.datamodel.rule.CompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.CompositeFieldConstraint;
import org.drools.workbench.models.datamodel.rule.ConnectiveConstraint;
import org.drools.workbench.models.datamodel.rule.DSLComplexVariableValue;
import org.drools.workbench.models.datamodel.rule.DSLSentence;
import org.drools.workbench.models.datamodel.rule.DSLVariableValue;
import org.drools.workbench.models.datamodel.rule.ExpressionCollection;
import org.drools.workbench.models.datamodel.rule.ExpressionField;
import org.drools.workbench.models.datamodel.rule.ExpressionFormLine;
import org.drools.workbench.models.datamodel.rule.ExpressionMethod;
import org.drools.workbench.models.datamodel.rule.ExpressionMethodParameterDefinition;
import org.drools.workbench.models.datamodel.rule.ExpressionPart;
import org.drools.workbench.models.datamodel.rule.ExpressionText;
import org.drools.workbench.models.datamodel.rule.ExpressionUnboundFact;
import org.drools.workbench.models.datamodel.rule.ExpressionVariable;
import org.drools.workbench.models.datamodel.rule.FactPattern;
import org.drools.workbench.models.datamodel.rule.FieldConstraint;
import org.drools.workbench.models.datamodel.rule.FreeFormLine;
import org.drools.workbench.models.datamodel.rule.FromAccumulateCompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.FromCollectCompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.FromCompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.IAction;
import org.drools.workbench.models.datamodel.rule.IPattern;
import org.drools.workbench.models.datamodel.rule.RuleAttribute;
import org.drools.workbench.models.datamodel.rule.RuleModel;
import org.drools.workbench.models.datamodel.rule.SingleFieldConstraint;
import org.drools.workbench.models.datamodel.rule.SingleFieldConstraintEBLeftSide;
import org.junit.Assert;
import org.junit.Test;
import org.kie.soup.project.datamodel.oracle.DataType;
import org.kie.soup.project.datamodel.oracle.MethodInfo;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RuleModelDRLPersistenceUnmarshallingTest extends BaseRuleModelTest {
    private static final Logger logger = LoggerFactory.getLogger(RuleModelDRLPersistenceUnmarshallingTest.class);

    @Test
    public void testFactPattern() {
        String drl = "rule \"rule1\"\n" + ((("when\n" + "Applicant()\n") + "then\n") + "end");
        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals("rule1", m.name);
        Assert.assertEquals(1, m.lhs.length);
        IPattern p = m.lhs[0];
        Assert.assertTrue((p instanceof FactPattern));
        FactPattern fp = ((FactPattern) (p));
        Assert.assertEquals("Applicant", fp.getFactType());
        Assert.assertEquals(0, fp.getNumberOfConstraints());
    }

    @Test
    public void testFactPatternWithBinding() {
        String drl = "rule \"rule1\"\n" + ((("when\n" + "$a : Applicant()\n") + "then\n") + "end");
        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals("rule1", m.name);
        Assert.assertEquals(1, m.lhs.length);
        IPattern p = m.lhs[0];
        Assert.assertTrue((p instanceof FactPattern));
        FactPattern fp = ((FactPattern) (p));
        Assert.assertEquals("Applicant", fp.getFactType());
        Assert.assertEquals("$a", fp.getBoundName());
        Assert.assertEquals(0, fp.getNumberOfConstraints());
    }

    @Test
    public void testSingleFieldConstraint() {
        String drl = "rule \"rule1\"\n" + ((("when\n" + "Applicant( age < 55 )\n") + "then\n") + "end");
        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals("rule1", m.name);
        Assert.assertEquals(1, m.lhs.length);
        IPattern p = m.lhs[0];
        Assert.assertTrue((p instanceof FactPattern));
        FactPattern fp = ((FactPattern) (p));
        Assert.assertEquals("Applicant", fp.getFactType());
        Assert.assertEquals(1, fp.getConstraintList().getConstraints().length);
        Assert.assertTrue(((fp.getConstraint(0)) instanceof SingleFieldConstraint));
        SingleFieldConstraint sfp = ((SingleFieldConstraint) (fp.getConstraint(0)));
        Assert.assertEquals("Applicant", sfp.getFactType());
        Assert.assertEquals("age", sfp.getFieldName());
        Assert.assertEquals("<", sfp.getOperator());
        Assert.assertEquals("55", sfp.getValue());
        Assert.assertEquals(TYPE_LITERAL, sfp.getConstraintValueType());
    }

    @Test
    public void testSingleFieldConstraintWithBinding() {
        String drl = "rule \"rule1\"\n" + ((("when\n" + "Applicant( $a : age < 55 )\n") + "then\n") + "end");
        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals("rule1", m.name);
        Assert.assertEquals(1, m.lhs.length);
        IPattern p = m.lhs[0];
        Assert.assertTrue((p instanceof FactPattern));
        FactPattern fp = ((FactPattern) (p));
        Assert.assertEquals("Applicant", fp.getFactType());
        Assert.assertEquals(1, fp.getConstraintList().getConstraints().length);
        Assert.assertTrue(((fp.getConstraint(0)) instanceof SingleFieldConstraint));
        SingleFieldConstraint sfp = ((SingleFieldConstraint) (fp.getConstraint(0)));
        Assert.assertEquals("Applicant", sfp.getFactType());
        Assert.assertEquals("age", sfp.getFieldName());
        Assert.assertEquals("<", sfp.getOperator());
        Assert.assertEquals("55", sfp.getValue());
        Assert.assertEquals(TYPE_LITERAL, sfp.getConstraintValueType());
        Assert.assertEquals("$a", sfp.getFieldBinding());
    }

    @Test
    public void testSingleFieldConstraintWithTwoFieldsBinding() {
        String drl = "rule \"rule1\"\n" + ((("when\n" + "Applicant( $a : age, $n : name )\n") + "then\n") + "end");
        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals("rule1", m.name);
        Assert.assertEquals(1, m.lhs.length);
        IPattern p = m.lhs[0];
        Assert.assertTrue((p instanceof FactPattern));
        FactPattern fp = ((FactPattern) (p));
        Assert.assertEquals("Applicant", fp.getFactType());
        Assert.assertEquals(2, fp.getConstraintList().getConstraints().length);
        Assert.assertTrue(((fp.getConstraint(0)) instanceof SingleFieldConstraint));
        SingleFieldConstraint sfp0 = ((SingleFieldConstraint) (fp.getConstraint(0)));
        Assert.assertEquals("Applicant", sfp0.getFactType());
        Assert.assertEquals("age", sfp0.getFieldName());
        Assert.assertEquals(TYPE_UNDEFINED, sfp0.getConstraintValueType());
        Assert.assertEquals("$a", sfp0.getFieldBinding());
        Assert.assertTrue(((fp.getConstraint(1)) instanceof SingleFieldConstraint));
        SingleFieldConstraint sfp1 = ((SingleFieldConstraint) (fp.getConstraint(1)));
        Assert.assertEquals("Applicant", sfp1.getFactType());
        Assert.assertEquals("name", sfp1.getFieldName());
        Assert.assertEquals(TYPE_UNDEFINED, sfp1.getConstraintValueType());
        Assert.assertEquals("$n", sfp1.getFieldBinding());
    }

    @Test
    public void testCompositeFieldConstraint() {
        String drl = "rule \"rule1\"\n" + ((("when\n" + "Applicant( age < 55 || age > 70 )\n") + "then\n") + "end");
        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals("rule1", m.name);
        Assert.assertEquals(1, m.lhs.length);
        IPattern p = m.lhs[0];
        Assert.assertTrue((p instanceof FactPattern));
        FactPattern fp = ((FactPattern) (p));
        Assert.assertEquals("Applicant", fp.getFactType());
        Assert.assertEquals(1, fp.getConstraintList().getConstraints().length);
        Assert.assertTrue(((fp.getConstraint(0)) instanceof CompositeFieldConstraint));
        CompositeFieldConstraint cfp = ((CompositeFieldConstraint) (fp.getConstraint(0)));
        Assert.assertEquals("||", cfp.getCompositeJunctionType());
        Assert.assertEquals(2, cfp.getNumberOfConstraints());
        Assert.assertTrue(((cfp.getConstraint(0)) instanceof SingleFieldConstraint));
        Assert.assertTrue(((cfp.getConstraint(1)) instanceof SingleFieldConstraint));
        SingleFieldConstraint sfp1 = ((SingleFieldConstraint) (cfp.getConstraint(0)));
        Assert.assertEquals("Applicant", sfp1.getFactType());
        Assert.assertEquals("age", sfp1.getFieldName());
        Assert.assertEquals("<", sfp1.getOperator());
        Assert.assertEquals("55", sfp1.getValue());
        Assert.assertEquals(TYPE_LITERAL, sfp1.getConstraintValueType());
        SingleFieldConstraint sfp2 = ((SingleFieldConstraint) (cfp.getConstraint(1)));
        Assert.assertEquals("Applicant", sfp2.getFactType());
        Assert.assertEquals("age", sfp2.getFieldName());
        Assert.assertEquals(">", sfp2.getOperator());
        Assert.assertEquals("70", sfp2.getValue());
        Assert.assertEquals(TYPE_LITERAL, sfp2.getConstraintValueType());
    }

    @Test
    public void testSingleFieldConstraintIsNullOperator() {
        String drl = "rule \"rule1\"\n" + ((("when\n" + "Applicant( age == null )\n") + "then\n") + "end");
        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals("rule1", m.name);
        Assert.assertEquals(1, m.lhs.length);
        IPattern p = m.lhs[0];
        Assert.assertTrue((p instanceof FactPattern));
        FactPattern fp = ((FactPattern) (p));
        Assert.assertEquals("Applicant", fp.getFactType());
        Assert.assertEquals(1, fp.getConstraintList().getConstraints().length);
        Assert.assertTrue(((fp.getConstraint(0)) instanceof SingleFieldConstraint));
        SingleFieldConstraint sfp = ((SingleFieldConstraint) (fp.getConstraint(0)));
        Assert.assertEquals("Applicant", sfp.getFactType());
        Assert.assertEquals("age", sfp.getFieldName());
        Assert.assertEquals("== null", sfp.getOperator());
        Assert.assertNull(sfp.getValue());
        Assert.assertEquals(TYPE_UNDEFINED, sfp.getConstraintValueType());
    }

    @Test
    public void testSingleFieldConstraintIsNotNullOperator() {
        String drl = "rule \"rule1\"\n" + ((("when\n" + "Applicant( age != null )\n") + "then\n") + "end");
        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals("rule1", m.name);
        Assert.assertEquals(1, m.lhs.length);
        IPattern p = m.lhs[0];
        Assert.assertTrue((p instanceof FactPattern));
        FactPattern fp = ((FactPattern) (p));
        Assert.assertEquals("Applicant", fp.getFactType());
        Assert.assertEquals(1, fp.getConstraintList().getConstraints().length);
        Assert.assertTrue(((fp.getConstraint(0)) instanceof SingleFieldConstraint));
        SingleFieldConstraint sfp = ((SingleFieldConstraint) (fp.getConstraint(0)));
        Assert.assertEquals("Applicant", sfp.getFactType());
        Assert.assertEquals("age", sfp.getFieldName());
        Assert.assertEquals("!= null", sfp.getOperator());
        Assert.assertNull(sfp.getValue());
        Assert.assertEquals(TYPE_UNDEFINED, sfp.getConstraintValueType());
    }

    @Test
    public void testCompositeFieldConstraintWithNotNullOperator() {
        String drl = "rule \"rule1\"\n" + ((("when\n" + "Applicant( age != null && age > 70 )\n") + "then\n") + "end");
        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals("rule1", m.name);
        Assert.assertEquals(1, m.lhs.length);
        IPattern p = m.lhs[0];
        Assert.assertTrue((p instanceof FactPattern));
        FactPattern fp = ((FactPattern) (p));
        Assert.assertEquals("Applicant", fp.getFactType());
        Assert.assertEquals(1, fp.getConstraintList().getConstraints().length);
        Assert.assertTrue(((fp.getConstraint(0)) instanceof CompositeFieldConstraint));
        CompositeFieldConstraint cfp = ((CompositeFieldConstraint) (fp.getConstraint(0)));
        Assert.assertEquals("&&", cfp.getCompositeJunctionType());
        Assert.assertEquals(2, cfp.getNumberOfConstraints());
        Assert.assertTrue(((cfp.getConstraint(0)) instanceof SingleFieldConstraint));
        Assert.assertTrue(((cfp.getConstraint(1)) instanceof SingleFieldConstraint));
        SingleFieldConstraint sfp1 = ((SingleFieldConstraint) (cfp.getConstraint(0)));
        Assert.assertEquals("Applicant", sfp1.getFactType());
        Assert.assertEquals("age", sfp1.getFieldName());
        Assert.assertEquals("!= null", sfp1.getOperator());
        Assert.assertNull(sfp1.getValue());
        Assert.assertEquals(TYPE_UNDEFINED, sfp1.getConstraintValueType());
        SingleFieldConstraint sfp2 = ((SingleFieldConstraint) (cfp.getConstraint(1)));
        Assert.assertEquals("Applicant", sfp2.getFactType());
        Assert.assertEquals("age", sfp2.getFieldName());
        Assert.assertEquals(">", sfp2.getOperator());
        Assert.assertEquals("70", sfp2.getValue());
        Assert.assertEquals(TYPE_LITERAL, sfp2.getConstraintValueType());
    }

    @Test
    public void testSingleFieldConstraintCEPOperator() {
        String drl = "rule \"rule1\"\n" + ((("when\n" + "Applicant( dob after \"26-Jun-2013\" )\n") + "then\n") + "end");
        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals("rule1", m.name);
        Assert.assertEquals(1, m.lhs.length);
        IPattern p = m.lhs[0];
        Assert.assertTrue((p instanceof FactPattern));
        FactPattern fp = ((FactPattern) (p));
        Assert.assertEquals("Applicant", fp.getFactType());
        Assert.assertEquals(1, fp.getNumberOfConstraints());
        Assert.assertTrue(((fp.getConstraint(0)) instanceof SingleFieldConstraint));
        SingleFieldConstraint sfp = ((SingleFieldConstraint) (fp.getConstraint(0)));
        Assert.assertEquals("Applicant", sfp.getFactType());
        Assert.assertEquals("dob", sfp.getFieldName());
        Assert.assertEquals("after", sfp.getOperator());
        Assert.assertEquals("26-Jun-2013", sfp.getValue());
        Assert.assertEquals(TYPE_LITERAL, sfp.getConstraintValueType());
    }

    @Test
    public void testSingleFieldConstraintCEPOperator1Parameter() {
        String drl = "rule \"rule1\"\n" + (((("when\n" + "$e : Event()\n") + "Event( this after[1d] $e )\n") + "then\n") + "end");
        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals("rule1", m.name);
        Assert.assertEquals(2, m.lhs.length);
        IPattern p1 = m.lhs[0];
        Assert.assertTrue((p1 instanceof FactPattern));
        FactPattern fp1 = ((FactPattern) (p1));
        Assert.assertEquals("Event", fp1.getFactType());
        Assert.assertEquals("$e", fp1.getBoundName());
        Assert.assertEquals(0, fp1.getNumberOfConstraints());
        IPattern p2 = m.lhs[1];
        Assert.assertTrue((p2 instanceof FactPattern));
        FactPattern fp2 = ((FactPattern) (p2));
        Assert.assertEquals("Event", fp2.getFactType());
        Assert.assertNull(fp2.getBoundName());
        Assert.assertEquals(1, fp2.getNumberOfConstraints());
        SingleFieldConstraint sfp = ((SingleFieldConstraint) (fp2.getConstraint(0)));
        Assert.assertEquals("Event", sfp.getFactType());
        Assert.assertEquals("this", sfp.getFieldName());
        Assert.assertEquals("after", sfp.getOperator());
        Assert.assertEquals("$e", sfp.getValue());
        Assert.assertEquals(TYPE_VARIABLE, sfp.getConstraintValueType());
        Assert.assertEquals(3, sfp.getParameters().size());
        Assert.assertEquals("1d", sfp.getParameter("0"));
        Assert.assertEquals("1", sfp.getParameter("org.drools.workbench.models.commons.backend.rule.visibleParameterSet"));
        Assert.assertEquals("org.drools.workbench.models.commons.backend.rule.CEPOperatorParameterDRLBuilder", sfp.getParameter("org.drools.workbench.models.commons.backend.rule.operatorParameterGenerator"));
    }

    @Test
    public void testReciprocal_SingleFieldConstraintCEPOperator1Parameter() {
        // This is the inverse of "SingleFieldConstraintCEPOperator1Parameter"
        String drl = "rule \"rule1\"\n" + ((((("dialect \"mvel\"\n" + "when\n") + "$e : Event()\n") + "Event( this after[1d] $e )\n") + "then\n") + "end");
        RuleModel m = new RuleModel();
        m.name = "rule1";
        FactPattern fp1 = new FactPattern();
        fp1.setFactType("Event");
        fp1.setBoundName("$e");
        FactPattern fp2 = new FactPattern();
        fp2.setFactType("Event");
        SingleFieldConstraint sfp = new SingleFieldConstraint();
        sfp.setFactType("Event");
        sfp.setFieldName("this");
        sfp.setOperator("after");
        sfp.setValue("$e");
        sfp.setConstraintValueType(TYPE_VARIABLE);
        sfp.getParameters().put("0", "1d");
        sfp.getParameters().put("org.drools.workbench.models.commons.backend.rule.visibleParameterSet", "1");
        sfp.getParameters().put("org.drools.workbench.models.commons.backend.rule.operatorParameterGenerator", "org.drools.workbench.models.commons.backend.rule.CEPOperatorParameterDRLBuilder");
        fp2.addConstraint(sfp);
        m.addLhsItem(fp1);
        m.addLhsItem(fp2);
        String actualDrl = RuleModelDRLPersistenceImpl.getInstance().marshal(m);
        assertEqualsIgnoreWhitespace(drl, actualDrl);
    }

    @Test
    public void testSingleFieldConstraintCEPOperator2Parameters() {
        String drl = "rule \"rule1\"\n" + (((("when\n" + "$e : Event()\n") + "Event( this after[1d, 2d] $e )\n") + "then\n") + "end");
        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals("rule1", m.name);
        Assert.assertEquals(2, m.lhs.length);
        IPattern p1 = m.lhs[0];
        Assert.assertTrue((p1 instanceof FactPattern));
        FactPattern fp1 = ((FactPattern) (p1));
        Assert.assertEquals("Event", fp1.getFactType());
        Assert.assertEquals("$e", fp1.getBoundName());
        Assert.assertEquals(0, fp1.getNumberOfConstraints());
        IPattern p2 = m.lhs[1];
        Assert.assertTrue((p2 instanceof FactPattern));
        FactPattern fp2 = ((FactPattern) (p2));
        Assert.assertEquals("Event", fp2.getFactType());
        Assert.assertNull(fp2.getBoundName());
        Assert.assertEquals(1, fp2.getNumberOfConstraints());
        SingleFieldConstraint sfp = ((SingleFieldConstraint) (fp2.getConstraint(0)));
        Assert.assertEquals("Event", sfp.getFactType());
        Assert.assertEquals("this", sfp.getFieldName());
        Assert.assertEquals("after", sfp.getOperator());
        Assert.assertEquals("$e", sfp.getValue());
        Assert.assertEquals(TYPE_VARIABLE, sfp.getConstraintValueType());
        Assert.assertEquals(4, sfp.getParameters().size());
        Assert.assertEquals("1d", sfp.getParameter("0"));
        Assert.assertEquals("2d", sfp.getParameter("1"));
        Assert.assertEquals("2", sfp.getParameter("org.drools.workbench.models.commons.backend.rule.visibleParameterSet"));
        Assert.assertEquals("org.drools.workbench.models.commons.backend.rule.CEPOperatorParameterDRLBuilder", sfp.getParameter("org.drools.workbench.models.commons.backend.rule.operatorParameterGenerator"));
    }

    @Test
    public void testReciprocal_SingleFieldConstraintCEPOperator2Parameters() {
        // This is the inverse of "SingleFieldConstraintCEPOperator2Parameters"
        String drl = "rule \"rule1\"\n" + ((((("dialect \"mvel\"\n" + "when\n") + "$e : Event()\n") + "Event( this after[1d, 2d] $e )\n") + "then\n") + "end");
        RuleModel m = new RuleModel();
        m.name = "rule1";
        FactPattern fp1 = new FactPattern();
        fp1.setFactType("Event");
        fp1.setBoundName("$e");
        FactPattern fp2 = new FactPattern();
        fp2.setFactType("Event");
        SingleFieldConstraint sfp = new SingleFieldConstraint();
        sfp.setFactType("Event");
        sfp.setFieldName("this");
        sfp.setOperator("after");
        sfp.setValue("$e");
        sfp.setConstraintValueType(TYPE_VARIABLE);
        sfp.getParameters().put("0", "1d");
        sfp.getParameters().put("1", "2d");
        sfp.getParameters().put("org.drools.workbench.models.commons.backend.rule.visibleParameterSet", "2");
        sfp.getParameters().put("org.drools.workbench.models.commons.backend.rule.operatorParameterGenerator", "org.drools.workbench.models.commons.backend.rule.CEPOperatorParameterDRLBuilder");
        fp2.addConstraint(sfp);
        m.addLhsItem(fp1);
        m.addLhsItem(fp2);
        String actualDrl = RuleModelDRLPersistenceImpl.getInstance().marshal(m);
        assertEqualsIgnoreWhitespace(drl, actualDrl);
    }

    @Test
    public void testSingleFieldConstraintCEPOperatorTimeWindow() {
        String drl = "rule \"rule1\"\n" + ((("when\n" + "Event() over window:time (1d)\n") + "then\n") + "end");
        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals("rule1", m.name);
        Assert.assertEquals(1, m.lhs.length);
        IPattern p1 = m.lhs[0];
        Assert.assertTrue((p1 instanceof FactPattern));
        FactPattern fp1 = ((FactPattern) (p1));
        Assert.assertEquals("Event", fp1.getFactType());
        Assert.assertNull(fp1.getBoundName());
        Assert.assertEquals(0, fp1.getNumberOfConstraints());
        Assert.assertNotNull(fp1.getWindow());
        CEPWindow window = fp1.getWindow();
        Assert.assertEquals("over window:time", window.getOperator());
        Assert.assertEquals(2, window.getParameters().size());
        Assert.assertEquals("1d", window.getParameter("1"));
        Assert.assertEquals("org.drools.workbench.models.commons.backend.rule.CEPWindowOperatorParameterDRLBuilder", window.getParameter("org.drools.workbench.models.commons.backend.rule.operatorParameterGenerator"));
    }

    @Test
    public void testReciprocal_SingleFieldConstraintCEPOperatorTimeWindow() {
        // This is the inverse of "SingleFieldConstraintCEPOperatorTimeWindow"
        String drl = "rule \"rule1\"\n" + (((("dialect \"mvel\"\n" + "when\n") + "Event() over window:time (1d)\n") + "then\n") + "end");
        RuleModel m = new RuleModel();
        m.name = "rule1";
        FactPattern fp1 = new FactPattern();
        fp1.setFactType("Event");
        CEPWindow window = new CEPWindow();
        window.setOperator("over window:time");
        window.getParameters().put("1", "1d");
        window.getParameters().put("org.drools.workbench.models.commons.backend.rule.operatorParameterGenerator", "org.drools.workbench.models.commons.backend.rule.CEPWindowOperatorParameterDRLBuilder");
        fp1.setWindow(window);
        m.addLhsItem(fp1);
        String actualDrl = RuleModelDRLPersistenceImpl.getInstance().marshal(m);
        assertEqualsIgnoreWhitespace(drl, actualDrl);
    }

    @Test
    public void testSingleFieldConstraintCEPOperatorTimeLength() {
        String drl = "rule \"rule1\"\n" + ((("when\n" + "Event() over window:length (10)\n") + "then\n") + "end");
        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals("rule1", m.name);
        Assert.assertEquals(1, m.lhs.length);
        IPattern p1 = m.lhs[0];
        Assert.assertTrue((p1 instanceof FactPattern));
        FactPattern fp1 = ((FactPattern) (p1));
        Assert.assertEquals("Event", fp1.getFactType());
        Assert.assertNull(fp1.getBoundName());
        Assert.assertEquals(0, fp1.getNumberOfConstraints());
        Assert.assertNotNull(fp1.getWindow());
        CEPWindow window = fp1.getWindow();
        Assert.assertEquals("over window:length", window.getOperator());
        Assert.assertEquals(2, window.getParameters().size());
        Assert.assertEquals("10", window.getParameter("1"));
        Assert.assertEquals("org.drools.workbench.models.commons.backend.rule.CEPWindowOperatorParameterDRLBuilder", window.getParameter("org.drools.workbench.models.commons.backend.rule.operatorParameterGenerator"));
    }

    @Test
    public void testReciprocal_SingleFieldConstraintCEPOperatorTimeLength() {
        // This is the inverse of "SingleFieldConstraintCEPOperatorTimeLength"
        String drl = "rule \"rule1\"\n" + (((("dialect \"mvel\"\n" + "when\n") + "Event() over window:length (10)\n") + "then\n") + "end");
        RuleModel m = new RuleModel();
        m.name = "rule1";
        FactPattern fp1 = new FactPattern();
        fp1.setFactType("Event");
        CEPWindow window = new CEPWindow();
        window.setOperator("over window:length");
        window.getParameters().put("1", "10");
        window.getParameters().put("org.drools.workbench.models.commons.backend.rule.operatorParameterGenerator", "org.drools.workbench.models.commons.backend.rule.CEPWindowOperatorParameterDRLBuilder");
        fp1.setWindow(window);
        m.addLhsItem(fp1);
        String actualDrl = RuleModelDRLPersistenceImpl.getInstance().marshal(m);
        assertEqualsIgnoreWhitespace(drl, actualDrl);
    }

    @Test
    public void testExtends() {
        String drl = "rule \"rule1\" extends \"rule2\" \n" + (("when\n" + "then\n") + "end");
        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals("rule1", m.name);
        Assert.assertEquals("rule2", m.parentName);
    }

    @Test
    public void testRuleNameWithoutTheQuotes() {
        String drl = "rule rule1\n" + (("when\n" + "then\n") + "end");
        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals("rule1", m.name);
    }

    @Test
    public void testMetaData() {
        String drl = "rule rule1\n" + ((("@author( Bob )\n" + "when\n") + "then\n") + "end");
        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals(1, m.metadataList.length);
        Assert.assertEquals("author", m.metadataList[0].getAttributeName());
        Assert.assertEquals("Bob", getValue());
    }

    @Test
    public void testAttributes() {
        String drl = "rule rule1\n" + ((("salience 42\n" + "when\n") + "then\n") + "end");
        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals(1, m.attributes.length);
        Assert.assertEquals("salience", m.attributes[0].getAttributeName());
        Assert.assertEquals("42", getValue());
    }

    @Test
    public void testEval() {
        String drl = "rule rule1\n" + ((("when\n" + "eval( true )\n") + "then\n") + "end");
        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals(1, m.lhs.length);
        Assert.assertTrue(((m.lhs[0]) instanceof FreeFormLine));
        Assert.assertEquals("eval( true )", getText());
    }

    @Test
    public void testEval2() {
        String drl = "rule rule1\n" + ((("when\n" + "Double( eval( functionTrue() && functionFalse()  ) )\n") + "then\n") + "end");
        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals(1, m.lhs.length);
        Assert.assertTrue(((m.lhs[0]) instanceof FactPattern));
        SingleFieldConstraint constraint = ((SingleFieldConstraint) (getConstraint(0)));
        Assert.assertEquals("functionTrue() && functionFalse()", constraint.getValue());
        Assert.assertEquals(TYPE_PREDICATE, constraint.getConstraintValueType());
    }

    @Test
    public void testLHSFreeFormLine() {
        String drl = "rule rule1\n" + ((("when\n" + "//A comment\n") + "then\n") + "end");
        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals(1, m.lhs.length);
        Assert.assertTrue(((m.lhs[0]) instanceof FreeFormLine));
        Assert.assertEquals("//A comment", getText());
    }

    @Test
    public void testRHSFreeFormLine() {
        String drl = "rule rule1\n" + (((("when\n" + "then\n") + "int test = (int)(1-0.8);\n") + "System.out.println( \"Hello Mario!\" );\n") + "end");
        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals(2, m.rhs.length);
        Assert.assertTrue(((m.rhs[0]) instanceof FreeFormLine));
        Assert.assertEquals("int test = (int)(1-0.8);", getText());
        Assert.assertTrue(((m.rhs[1]) instanceof FreeFormLine));
        Assert.assertEquals("System.out.println( \"Hello Mario!\" );", getText());
    }

    @Test
    public void testLHSFreeFormLineWithDsl() {
        String drl = "rule rule1\n" + ((("when\n" + ">//A comment\n") + "then\n") + "end");
        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshalUsingDSL(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals(1, m.lhs.length);
        Assert.assertTrue(((m.lhs[0]) instanceof FreeFormLine));
        Assert.assertEquals("//A comment", getText());
    }

    @Test
    public void testRHSFreeFormLineWithDsl() {
        String drl = "rule rule1\n" + (((("when\n" + "then\n") + ">int test = (int)(1-0.8);\n") + ">System.out.println( \"Hello Mario!\" );\n") + "end");
        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshalUsingDSL(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals(2, m.rhs.length);
        Assert.assertTrue(((m.rhs[0]) instanceof FreeFormLine));
        Assert.assertEquals("int test = (int)(1-0.8);", getText());
        Assert.assertTrue(((m.rhs[1]) instanceof FreeFormLine));
        Assert.assertEquals("System.out.println( \"Hello Mario!\" );", getText());
    }

    @Test
    public void testVarAssignment() {
        String drl = "rule rule1\n" + (((("when\n" + " d : Double()\n") + "then\n") + "double test = d.doubleValue();\n") + "end");
        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals(1, m.rhs.length);
        Assert.assertTrue(((m.rhs[0]) instanceof FreeFormLine));
        Assert.assertEquals("double test = d.doubleValue();", getText());
    }

    @Test
    public void testRHSOrder() {
        String drl = "rule \"Low Down Payment based on Appraisal\"\n" + ((((((((((((((("  dialect \"mvel\"\n" + "  ruleflow-group \"apr-calculation\"\n") + "  salience -3\n") + "  no-loop true\n") + " when\n") + "    appraised : Appraisal( )\n") + "    application : Application( mortgageAmount > ( appraised.value * 8 / 10 ) )\n") + " then\n") + "    double ratio = application.getMortgageAmount().doubleValue() / appraised.getValue().doubleValue();\n") + "    int brackets = (int)((ratio - 0.8) / 0.05);\n") + "    brackets++;\n") + "    double aprSurcharge = 0.75 * brackets;\n") + "    System.out.println( \"aprSurcharge added is \" + aprSurcharge );\n") + "    application.setApr(  application.getApr() + aprSurcharge );\n") + "    System.out.println(\"Executed Rule: \" + drools.getRule().getName() );\n") + "end");
        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals(7, m.rhs.length);
        Assert.assertTrue(((m.rhs[0]) instanceof FreeFormLine));
        Assert.assertTrue(((m.rhs[1]) instanceof FreeFormLine));
        Assert.assertTrue(((m.rhs[2]) instanceof FreeFormLine));
        Assert.assertTrue(((m.rhs[3]) instanceof FreeFormLine));
        Assert.assertTrue(((m.rhs[4]) instanceof FreeFormLine));
        Assert.assertTrue(((m.rhs[5]) instanceof ActionSetField));
        Assert.assertTrue(((m.rhs[6]) instanceof FreeFormLine));
    }

    @Test
    public void testNestedFieldExpressions() {
        String drl = "rule rule1\n" + ((("when\n" + "Person( address.postalCode == 12345 )\n") + "then\n") + "end");
        addModelField("org.test.Person", "address", "org.test.Address", "Address");
        addModelField("org.test.Address", "postalCode", "java.lang.Integer", "Integer");
        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals(1, m.lhs.length);
        Assert.assertTrue(((m.lhs[0]) instanceof FactPattern));
        Assert.assertTrue(((getFieldConstraints()[0]) instanceof SingleFieldConstraintEBLeftSide));
        SingleFieldConstraintEBLeftSide ebLeftSide = ((SingleFieldConstraintEBLeftSide) (getFieldConstraints()[0]));
        Assert.assertEquals("postalCode", ebLeftSide.getFieldName());
        Assert.assertEquals("java.lang.Integer", ebLeftSide.getFieldType());
        Assert.assertEquals("==", ebLeftSide.getOperator());
        Assert.assertEquals("12345", ebLeftSide.getValue());
        Assert.assertEquals(3, ebLeftSide.getExpressionLeftSide().getParts().size());
        Assert.assertTrue(((ebLeftSide.getExpressionLeftSide().getParts().get(0)) instanceof ExpressionUnboundFact));
        ExpressionUnboundFact expressionUnboundFact = ((ExpressionUnboundFact) (ebLeftSide.getExpressionLeftSide().getParts().get(0)));
        Assert.assertEquals("Person", expressionUnboundFact.getName());
        Assert.assertEquals("Person", expressionUnboundFact.getClassType());
        Assert.assertEquals("Person", expressionUnboundFact.getGenericType());
        Assert.assertEquals(getFactType(), expressionUnboundFact.getFactType());
        Assert.assertNull(expressionUnboundFact.getPrevious());
        Assert.assertEquals(ebLeftSide.getExpressionLeftSide().getParts().get(1), expressionUnboundFact.getNext());
        Assert.assertTrue(((ebLeftSide.getExpressionLeftSide().getParts().get(1)) instanceof ExpressionField));
        ExpressionField expressionField1 = ((ExpressionField) (ebLeftSide.getExpressionLeftSide().getParts().get(1)));
        Assert.assertEquals("address", expressionField1.getName());
        Assert.assertEquals("org.test.Address", expressionField1.getClassType());
        Assert.assertEquals("Address", expressionField1.getGenericType());
        Assert.assertEquals(ebLeftSide.getExpressionLeftSide().getParts().get(0), expressionField1.getPrevious());
        Assert.assertEquals(ebLeftSide.getExpressionLeftSide().getParts().get(2), expressionField1.getNext());
        Assert.assertTrue(((ebLeftSide.getExpressionLeftSide().getParts().get(2)) instanceof ExpressionField));
        ExpressionField expressionField2 = ((ExpressionField) (ebLeftSide.getExpressionLeftSide().getParts().get(2)));
        Assert.assertEquals("postalCode", expressionField2.getName());
        Assert.assertEquals("java.lang.Integer", expressionField2.getClassType());
        Assert.assertEquals(TYPE_NUMERIC_INTEGER, expressionField2.getGenericType());
        Assert.assertEquals(ebLeftSide.getExpressionLeftSide().getParts().get(1), expressionField2.getPrevious());
        Assert.assertNull(expressionField2.getNext());
    }

    @Test
    public void testNestedFieldExpressionsWithAFunction() {
        String drl = "rule rule1\n" + ((("when\n" + "Person( address.postalCode == myFunction() )\n") + "then\n") + "end");
        addModelField("org.test.Person", "address", "org.test.Address", "Address");
        addModelField("org.test.Address", "postalCode", "java.lang.Integer", "Integer");
        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals(1, m.lhs.length);
        Assert.assertTrue(((m.lhs[0]) instanceof FactPattern));
        Assert.assertTrue(((getFieldConstraints()[0]) instanceof SingleFieldConstraintEBLeftSide));
        SingleFieldConstraintEBLeftSide ebLeftSide = ((SingleFieldConstraintEBLeftSide) (getFieldConstraints()[0]));
        Assert.assertEquals("postalCode", ebLeftSide.getFieldName());
        Assert.assertEquals("java.lang.Integer", ebLeftSide.getFieldType());
        Assert.assertEquals("==", ebLeftSide.getOperator());
        Assert.assertEquals("myFunction()", ebLeftSide.getValue());
        Assert.assertEquals(3, ebLeftSide.getExpressionLeftSide().getParts().size());
        Assert.assertTrue(((ebLeftSide.getExpressionLeftSide().getParts().get(0)) instanceof ExpressionUnboundFact));
        ExpressionUnboundFact expressionUnboundFact = ((ExpressionUnboundFact) (ebLeftSide.getExpressionLeftSide().getParts().get(0)));
        Assert.assertEquals("Person", expressionUnboundFact.getName());
        Assert.assertEquals("Person", expressionUnboundFact.getClassType());
        Assert.assertEquals("Person", expressionUnboundFact.getGenericType());
        Assert.assertEquals(getFactType(), expressionUnboundFact.getFactType());
        Assert.assertNull(expressionUnboundFact.getPrevious());
        Assert.assertEquals(ebLeftSide.getExpressionLeftSide().getParts().get(1), expressionUnboundFact.getNext());
        Assert.assertTrue(((ebLeftSide.getExpressionLeftSide().getParts().get(1)) instanceof ExpressionField));
        ExpressionField expressionField1 = ((ExpressionField) (ebLeftSide.getExpressionLeftSide().getParts().get(1)));
        Assert.assertEquals("address", expressionField1.getName());
        Assert.assertEquals("org.test.Address", expressionField1.getClassType());
        Assert.assertEquals("Address", expressionField1.getGenericType());
        Assert.assertEquals(ebLeftSide.getExpressionLeftSide().getParts().get(0), expressionField1.getPrevious());
        Assert.assertEquals(ebLeftSide.getExpressionLeftSide().getParts().get(2), expressionField1.getNext());
        Assert.assertTrue(((ebLeftSide.getExpressionLeftSide().getParts().get(2)) instanceof ExpressionField));
        ExpressionField expressionField2 = ((ExpressionField) (ebLeftSide.getExpressionLeftSide().getParts().get(2)));
        Assert.assertEquals("postalCode", expressionField2.getName());
        Assert.assertEquals("java.lang.Integer", expressionField2.getClassType());
        Assert.assertEquals("Integer", expressionField2.getGenericType());
        Assert.assertEquals(ebLeftSide.getExpressionLeftSide().getParts().get(1), expressionField2.getPrevious());
        Assert.assertNull(expressionField2.getNext());
    }

    @Test
    public void testNestedFieldExpressionsWithAnotherExpression() {
        String drl = "rule rule1\n" + ((("when\n" + "p : Person( address.postalCode == p.address.postalCode )\n") + "then\n") + "end");
        addModelField("org.test.Person", "address", "org.test.Address", "Address");
        addModelField("org.test.Person", "this", "org.test.Person", TYPE_THIS);
        addModelField("org.test.Address", "postalCode", "java.lang.Integer", "Integer");
        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals(1, m.lhs.length);
        Assert.assertTrue(((m.lhs[0]) instanceof FactPattern));
        Assert.assertTrue(((getFieldConstraints()[0]) instanceof SingleFieldConstraintEBLeftSide));
        SingleFieldConstraintEBLeftSide ebLeftSide = ((SingleFieldConstraintEBLeftSide) (getFieldConstraints()[0]));
        Assert.assertEquals("postalCode", ebLeftSide.getFieldName());
        Assert.assertEquals("java.lang.Integer", ebLeftSide.getFieldType());
        Assert.assertEquals("==", ebLeftSide.getOperator());
        Assert.assertEquals("", ebLeftSide.getValue());
        Assert.assertEquals(3, ebLeftSide.getExpressionValue().getParts().size());
        Assert.assertTrue(((ebLeftSide.getExpressionValue().getParts().get(0)) instanceof ExpressionVariable));
        ExpressionVariable expressionVariable = ((ExpressionVariable) (ebLeftSide.getExpressionValue().getParts().get(0)));
        Assert.assertEquals("p", expressionVariable.getName());
        Assert.assertEquals("org.test.Person", expressionVariable.getClassType());
        Assert.assertEquals(TYPE_THIS, expressionVariable.getGenericType());
        Assert.assertTrue(((ebLeftSide.getExpressionValue().getParts().get(1)) instanceof ExpressionField));
        ExpressionField ef1 = ((ExpressionField) (ebLeftSide.getExpressionValue().getParts().get(1)));
        Assert.assertEquals("address", ef1.getName());
        Assert.assertEquals("org.test.Address", ef1.getClassType());
        Assert.assertEquals("Address", ef1.getGenericType());
        Assert.assertTrue(((ebLeftSide.getExpressionValue().getParts().get(2)) instanceof ExpressionField));
        ExpressionField ef2 = ((ExpressionField) (ebLeftSide.getExpressionValue().getParts().get(2)));
        Assert.assertEquals("postalCode", ef2.getName());
        Assert.assertEquals("java.lang.Integer", ef2.getClassType());
        Assert.assertEquals(TYPE_NUMERIC_INTEGER, ef2.getGenericType());
        Assert.assertEquals(3, ebLeftSide.getExpressionLeftSide().getParts().size());
        Assert.assertTrue(((ebLeftSide.getExpressionLeftSide().getParts().get(0)) instanceof ExpressionUnboundFact));
        ExpressionUnboundFact expressionUnboundFact = ((ExpressionUnboundFact) (ebLeftSide.getExpressionLeftSide().getParts().get(0)));
        Assert.assertEquals("Person", expressionUnboundFact.getName());
        Assert.assertEquals("Person", expressionUnboundFact.getClassType());
        Assert.assertEquals("Person", expressionUnboundFact.getGenericType());
        Assert.assertEquals(getFactType(), expressionUnboundFact.getFactType());
        Assert.assertNull(expressionUnboundFact.getPrevious());
        Assert.assertEquals(ebLeftSide.getExpressionLeftSide().getParts().get(1), expressionUnboundFact.getNext());
        Assert.assertTrue(((ebLeftSide.getExpressionLeftSide().getParts().get(1)) instanceof ExpressionField));
        ExpressionField expressionField1 = ((ExpressionField) (ebLeftSide.getExpressionLeftSide().getParts().get(1)));
        Assert.assertEquals("address", expressionField1.getName());
        Assert.assertEquals("org.test.Address", expressionField1.getClassType());
        Assert.assertEquals("Address", expressionField1.getGenericType());
        Assert.assertEquals(ebLeftSide.getExpressionLeftSide().getParts().get(0), expressionField1.getPrevious());
        Assert.assertEquals(ebLeftSide.getExpressionLeftSide().getParts().get(2), expressionField1.getNext());
        Assert.assertTrue(((ebLeftSide.getExpressionLeftSide().getParts().get(2)) instanceof ExpressionField));
        ExpressionField expressionField2 = ((ExpressionField) (ebLeftSide.getExpressionLeftSide().getParts().get(2)));
        Assert.assertEquals("postalCode", expressionField2.getName());
        Assert.assertEquals("java.lang.Integer", expressionField2.getClassType());
        Assert.assertEquals(TYPE_NUMERIC_INTEGER, expressionField2.getGenericType());
        Assert.assertEquals(ebLeftSide.getExpressionLeftSide().getParts().get(1), expressionField2.getPrevious());
        Assert.assertNull(expressionField2.getNext());
    }

    @Test
    public void testSingleFieldConstraintContainsOperator() {
        String drl = "rule \"rule1\"\n" + (((("when\n" + "$is : IncomeSource( )\n") + "Applicant( incomes contains $is )\n") + "then\n") + "end");
        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals("rule1", m.name);
        Assert.assertEquals(2, m.lhs.length);
        IPattern p0 = m.lhs[0];
        Assert.assertTrue((p0 instanceof FactPattern));
        FactPattern fp0 = ((FactPattern) (p0));
        Assert.assertEquals("IncomeSource", fp0.getFactType());
        Assert.assertEquals("$is", fp0.getBoundName());
        IPattern p1 = m.lhs[1];
        Assert.assertTrue((p1 instanceof FactPattern));
        FactPattern fp1 = ((FactPattern) (p1));
        Assert.assertEquals("Applicant", fp1.getFactType());
        Assert.assertEquals(0, fp0.getNumberOfConstraints());
        Assert.assertEquals(1, fp1.getNumberOfConstraints());
        Assert.assertTrue(((fp1.getConstraint(0)) instanceof SingleFieldConstraint));
        SingleFieldConstraint sfp = ((SingleFieldConstraint) (fp1.getConstraint(0)));
        Assert.assertEquals("Applicant", sfp.getFactType());
        Assert.assertEquals("incomes", sfp.getFieldName());
        Assert.assertEquals("contains", sfp.getOperator());
        Assert.assertEquals("$is", sfp.getValue());
        Assert.assertEquals(TYPE_VARIABLE, sfp.getConstraintValueType());
    }

    @Test
    public void testCompositeFactPatternWithOr() {
        String drl = "rule \"rule1\"\n" + ((("when\n" + "( Person( age == 42 ) or Person( age == 43 ) )\n") + "then\n") + "end");
        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals("rule1", m.name);
        // LHS Pattern
        Assert.assertEquals(1, m.lhs.length);
        IPattern p = m.lhs[0];
        Assert.assertTrue((p instanceof CompositeFactPattern));
        CompositeFactPattern cfp = ((CompositeFactPattern) (p));
        Assert.assertEquals(COMPOSITE_TYPE_OR, cfp.getType());
        // LHS sub-patterns
        Assert.assertEquals(2, cfp.getPatterns().length);
        IPattern cfp_p1 = cfp.getPatterns()[0];
        Assert.assertTrue((cfp_p1 instanceof FactPattern));
        FactPattern fp1 = ((FactPattern) (cfp_p1));
        Assert.assertEquals("Person", fp1.getFactType());
        Assert.assertEquals(1, fp1.getConstraintList().getConstraints().length);
        Assert.assertTrue(((fp1.getConstraint(0)) instanceof SingleFieldConstraint));
        SingleFieldConstraint cfp_sfp1 = ((SingleFieldConstraint) (fp1.getConstraint(0)));
        Assert.assertEquals("Person", cfp_sfp1.getFactType());
        Assert.assertEquals("age", cfp_sfp1.getFieldName());
        Assert.assertEquals("==", cfp_sfp1.getOperator());
        Assert.assertEquals("42", cfp_sfp1.getValue());
        Assert.assertEquals(TYPE_LITERAL, cfp_sfp1.getConstraintValueType());
        IPattern cfp_p2 = cfp.getPatterns()[1];
        Assert.assertTrue((cfp_p2 instanceof FactPattern));
        FactPattern fp2 = ((FactPattern) (cfp_p2));
        Assert.assertEquals("Person", fp2.getFactType());
        Assert.assertTrue(((fp2.getConstraint(0)) instanceof SingleFieldConstraint));
        SingleFieldConstraint cfp_sfp2 = ((SingleFieldConstraint) (fp2.getConstraint(0)));
        Assert.assertEquals("Person", cfp_sfp2.getFactType());
        Assert.assertEquals("age", cfp_sfp2.getFieldName());
        Assert.assertEquals("==", cfp_sfp2.getOperator());
        Assert.assertEquals("43", cfp_sfp2.getValue());
        Assert.assertEquals(TYPE_LITERAL, cfp_sfp2.getConstraintValueType());
    }

    @Test
    public void testReciprocal_CompositeFactPatternWithOr() {
        // This is the inverse of "CompositeFactPatternWithOr"
        String drl = "rule \"rule1\"\n" + (((("dialect \"mvel\"\n" + "when\n") + "( Person( age == 42 ) or Person( age == 43 ) )\n") + "then\n") + "end");
        RuleModel m = new RuleModel();
        m.name = "rule1";
        // LHS Patterns
        CompositeFactPattern cfp = new CompositeFactPattern();
        cfp.setType(COMPOSITE_TYPE_OR);
        // LHS sub-patterns
        FactPattern fp1 = new FactPattern();
        fp1.setFactType("Person");
        SingleFieldConstraint cfp_sfp1 = new SingleFieldConstraint();
        cfp_sfp1.setFactType("Person");
        cfp_sfp1.setFieldName("age");
        cfp_sfp1.setOperator("==");
        cfp_sfp1.setValue("42");
        cfp_sfp1.setFieldType(TYPE_NUMERIC_INTEGER);
        cfp_sfp1.setConstraintValueType(TYPE_LITERAL);
        fp1.addConstraint(cfp_sfp1);
        cfp.addFactPattern(fp1);
        FactPattern fp2 = new FactPattern();
        fp2.setFactType("Person");
        SingleFieldConstraint cfp_sfp2 = new SingleFieldConstraint();
        cfp_sfp2.setFactType("Person");
        cfp_sfp2.setFieldName("age");
        cfp_sfp2.setOperator("==");
        cfp_sfp2.setValue("43");
        cfp_sfp2.setFieldType(TYPE_NUMERIC_INTEGER);
        cfp_sfp2.setConstraintValueType(TYPE_LITERAL);
        fp2.addConstraint(cfp_sfp2);
        cfp.addFactPattern(fp2);
        m.addLhsItem(cfp);
        String actualDrl = RuleModelDRLPersistenceImpl.getInstance().marshal(m);
        assertEqualsIgnoreWhitespace(drl, actualDrl);
    }

    @Test
    public void testCompositeFactPatternWithOrAndCompositeFieldConstraint() {
        String drl = "rule \"rule1\"\n" + ((("when\n" + "( Person( age == 42 ) or Person( age == 43 || age == 44) )\n") + "then\n") + "end");
        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals("rule1", m.name);
        // LHS Pattern
        Assert.assertEquals(1, m.lhs.length);
        IPattern p = m.lhs[0];
        Assert.assertTrue((p instanceof CompositeFactPattern));
        CompositeFactPattern cfp = ((CompositeFactPattern) (p));
        Assert.assertEquals(COMPOSITE_TYPE_OR, cfp.getType());
        // LHS sub-patterns
        Assert.assertEquals(2, cfp.getPatterns().length);
        IPattern cfp_p1 = cfp.getPatterns()[0];
        Assert.assertTrue((cfp_p1 instanceof FactPattern));
        FactPattern fp1 = ((FactPattern) (cfp_p1));
        Assert.assertEquals("Person", fp1.getFactType());
        Assert.assertEquals(1, fp1.getConstraintList().getConstraints().length);
        Assert.assertTrue(((fp1.getConstraint(0)) instanceof SingleFieldConstraint));
        SingleFieldConstraint cfp_sfp1 = ((SingleFieldConstraint) (fp1.getConstraint(0)));
        Assert.assertEquals("Person", cfp_sfp1.getFactType());
        Assert.assertEquals("age", cfp_sfp1.getFieldName());
        Assert.assertEquals("==", cfp_sfp1.getOperator());
        Assert.assertEquals("42", cfp_sfp1.getValue());
        Assert.assertEquals(TYPE_LITERAL, cfp_sfp1.getConstraintValueType());
        IPattern cfp_p2 = cfp.getPatterns()[1];
        Assert.assertTrue((cfp_p2 instanceof FactPattern));
        FactPattern fp2 = ((FactPattern) (cfp_p2));
        Assert.assertEquals(1, fp2.getConstraintList().getConstraints().length);
        Assert.assertTrue(((fp2.getConstraint(0)) instanceof CompositeFieldConstraint));
        CompositeFieldConstraint cfp_p2_cfp = ((CompositeFieldConstraint) (fp2.getConstraint(0)));
        Assert.assertEquals("||", cfp_p2_cfp.getCompositeJunctionType());
        Assert.assertEquals(2, cfp_p2_cfp.getNumberOfConstraints());
        Assert.assertTrue(((cfp_p2_cfp.getConstraint(0)) instanceof SingleFieldConstraint));
        Assert.assertTrue(((cfp_p2_cfp.getConstraint(1)) instanceof SingleFieldConstraint));
        SingleFieldConstraint cfp_p2_sfp1 = ((SingleFieldConstraint) (cfp_p2_cfp.getConstraint(0)));
        Assert.assertEquals("Person", cfp_p2_sfp1.getFactType());
        Assert.assertEquals("age", cfp_p2_sfp1.getFieldName());
        Assert.assertEquals("==", cfp_p2_sfp1.getOperator());
        Assert.assertEquals("43", cfp_p2_sfp1.getValue());
        Assert.assertEquals(TYPE_LITERAL, cfp_p2_sfp1.getConstraintValueType());
        SingleFieldConstraint cfp_p2_sfp2 = ((SingleFieldConstraint) (cfp_p2_cfp.getConstraint(1)));
        Assert.assertEquals("Person", cfp_p2_sfp2.getFactType());
        Assert.assertEquals("age", cfp_p2_sfp2.getFieldName());
        Assert.assertEquals("==", cfp_p2_sfp2.getOperator());
        Assert.assertEquals("44", cfp_p2_sfp2.getValue());
        Assert.assertEquals(TYPE_LITERAL, cfp_p2_sfp2.getConstraintValueType());
    }

    @Test
    public void testReciprocal_CompositeFactPatternWithOrAndCompositeFieldConstraint() {
        // This is the inverse of "CompositeFactPatternWithOrAndCompositeFieldConstraint"
        String drl = "rule \"rule1\"\n" + (((("dialect \"mvel\"\n" + "when\n") + "( Person( age == 42 ) or Person( age == 43 || age == 44) )\n") + "then\n") + "end");
        RuleModel m = new RuleModel();
        m.name = "rule1";
        // LHS Pattern
        CompositeFactPattern cfp = new CompositeFactPattern();
        cfp.setType(COMPOSITE_TYPE_OR);
        // LHS sub-patterns
        FactPattern fp1 = new FactPattern();
        fp1.setFactType("Person");
        SingleFieldConstraint fp1_sfp1 = new SingleFieldConstraint();
        fp1_sfp1.setFactType("Person");
        fp1_sfp1.setFieldName("age");
        fp1_sfp1.setOperator("==");
        fp1_sfp1.setValue("42");
        fp1_sfp1.setFieldType(TYPE_NUMERIC_INTEGER);
        fp1_sfp1.setConstraintValueType(TYPE_LITERAL);
        fp1.addConstraint(fp1_sfp1);
        FactPattern fp2 = new FactPattern();
        fp2.setFactType("Person");
        CompositeFieldConstraint fp2_cfp = new CompositeFieldConstraint();
        fp2_cfp.setCompositeJunctionType(CompositeFieldConstraint.COMPOSITE_TYPE_OR);
        fp2.addConstraint(fp2_cfp);
        SingleFieldConstraint fp2_sfp1 = new SingleFieldConstraint();
        fp2_sfp1.setFactType("Person");
        fp2_sfp1.setFieldName("age");
        fp2_sfp1.setOperator("==");
        fp2_sfp1.setValue("43");
        fp2_sfp1.setFieldType(TYPE_NUMERIC_INTEGER);
        fp2_sfp1.setConstraintValueType(TYPE_LITERAL);
        fp2_cfp.addConstraint(fp2_sfp1);
        SingleFieldConstraint fp2_sfp2 = new SingleFieldConstraint();
        fp2_sfp2.setFactType("Person");
        fp2_sfp2.setFieldName("age");
        fp2_sfp2.setOperator("==");
        fp2_sfp2.setValue("44");
        fp2_sfp2.setFieldType(TYPE_NUMERIC_INTEGER);
        fp2_sfp2.setConstraintValueType(TYPE_LITERAL);
        fp2_cfp.addConstraint(fp2_sfp2);
        cfp.addFactPattern(fp1);
        cfp.addFactPattern(fp2);
        m.addLhsItem(cfp);
        String actualDrl = RuleModelDRLPersistenceImpl.getInstance().marshal(m);
        assertEqualsIgnoreWhitespace(drl, actualDrl);
    }

    @Test
    public void testNestedFieldConstraints() {
        String drl = "rule \"rule1\"\n" + ((("when\n" + "ParentType( this != null, this.parentChildField != null, this.parentChildField.childField == \"hello\" )\n") + "then\n") + "end");
        addModelField("org.test.ParentType", "parentChildField", "org.test.ChildType", "ChildType");
        addModelField("org.test.ChildType", "childField", "java.lang.String", "String");
        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals("rule1", m.name);
        Assert.assertEquals(1, m.lhs.length);
        IPattern p = m.lhs[0];
        Assert.assertTrue((p instanceof FactPattern));
        FactPattern fp = ((FactPattern) (p));
        Assert.assertEquals("ParentType", fp.getFactType());
        Assert.assertEquals(3, fp.getConstraintList().getConstraints().length);
        Assert.assertTrue(((fp.getConstraint(0)) instanceof SingleFieldConstraint));
        SingleFieldConstraint sfp0 = ((SingleFieldConstraint) (fp.getConstraint(0)));
        Assert.assertEquals("ParentType", sfp0.getFactType());
        Assert.assertEquals("this", sfp0.getFieldName());
        Assert.assertEquals(TYPE_THIS, sfp0.getFieldType());
        Assert.assertEquals("!= null", sfp0.getOperator());
        Assert.assertNull(sfp0.getValue());
        Assert.assertEquals(TYPE_UNDEFINED, sfp0.getConstraintValueType());
        Assert.assertNull(sfp0.getParent());
        Assert.assertTrue(((fp.getConstraint(1)) instanceof SingleFieldConstraintEBLeftSide));
        SingleFieldConstraintEBLeftSide sfp1 = ((SingleFieldConstraintEBLeftSide) (fp.getConstraint(1)));
        Assert.assertEquals("ParentType", sfp1.getFactType());
        Assert.assertEquals("parentChildField", sfp1.getFieldName());
        Assert.assertEquals("org.test.ChildType", sfp1.getFieldType());
        Assert.assertEquals("!= null", sfp1.getOperator());
        Assert.assertNull(sfp1.getValue());
        Assert.assertEquals(TYPE_UNDEFINED, sfp1.getConstraintValueType());
        Assert.assertNull(sfp1.getParent());
        Assert.assertTrue(((fp.getConstraint(2)) instanceof SingleFieldConstraintEBLeftSide));
        SingleFieldConstraintEBLeftSide sfp2 = ((SingleFieldConstraintEBLeftSide) (fp.getConstraint(2)));
        Assert.assertEquals("childField", sfp2.getFieldName());
        Assert.assertEquals("java.lang.String", sfp2.getFieldType());
        Assert.assertEquals("==", sfp2.getOperator());
        Assert.assertEquals("hello", sfp2.getValue());
        Assert.assertEquals(TYPE_LITERAL, sfp2.getConstraintValueType());
        Assert.assertNull(sfp2.getParent());
    }

    @Test
    public void testReciprocal_NestedFieldConstraints() {
        // This is the inverse of "NestedFieldConstraints"
        String drl = "rule \"rule1\"\n" + (((("dialect \"mvel\"\n" + "when\n") + "ParentType( this != null, this.parentChildField != null, this.parentChildField.childField == \"hello\" )\n") + "then\n") + "end");
        RuleModel m = new RuleModel();
        m.name = "rule1";
        // LHS Pattern
        FactPattern fp1 = new FactPattern();
        fp1.setFactType("ParentType");
        SingleFieldConstraint fp1_sfp1 = new SingleFieldConstraint();
        fp1_sfp1.setFactType("ParentType");
        fp1_sfp1.setFieldName("this");
        fp1_sfp1.setFieldType(TYPE_THIS);
        fp1_sfp1.setOperator("!= null");
        fp1_sfp1.setConstraintValueType(TYPE_UNDEFINED);
        fp1.addConstraint(fp1_sfp1);
        SingleFieldConstraint fp1_sfp2 = new SingleFieldConstraint();
        fp1_sfp2.setFactType("ParentType");
        fp1_sfp2.setFieldName("parentChildField");
        fp1_sfp2.setFieldType("ChildType");
        fp1_sfp2.setOperator("!= null");
        fp1_sfp2.setConstraintValueType(TYPE_UNDEFINED);
        fp1.addConstraint(fp1_sfp2);
        fp1_sfp2.setParent(fp1_sfp1);
        SingleFieldConstraint fp1_sfp3 = new SingleFieldConstraint();
        fp1_sfp3.setFactType("ChildType");
        fp1_sfp3.setFieldName("childField");
        fp1_sfp3.setFieldType(TYPE_STRING);
        fp1_sfp3.setOperator("==");
        fp1_sfp3.setValue("hello");
        fp1_sfp3.setConstraintValueType(TYPE_LITERAL);
        fp1.addConstraint(fp1_sfp3);
        fp1_sfp3.setParent(fp1_sfp2);
        m.addLhsItem(fp1);
        String actualDrl = RuleModelDRLPersistenceImpl.getInstance().marshal(m);
        assertEqualsIgnoreWhitespace(drl, actualDrl);
    }

    @Test
    public void testNestedFieldConstraintsAsExpression() {
        String drl = "rule \"rule1\"\n" + ((("when\n" + "Person( contact.telephone > 12345 )\n") + "then\n") + "end");
        addModelField("org.test.Person", "contact", "org.test.Contact", "Contact");
        addModelField("org.test.Contact", "telephone", "java.lang.Integer", TYPE_NUMERIC_INTEGER);
        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals("rule1", m.name);
        Assert.assertEquals(1, m.lhs.length);
        IPattern p = m.lhs[0];
        Assert.assertTrue((p instanceof FactPattern));
        FactPattern fp = ((FactPattern) (p));
        Assert.assertEquals("Person", fp.getFactType());
        Assert.assertEquals(1, fp.getConstraintList().getConstraints().length);
        Assert.assertTrue(((getFieldConstraints()[0]) instanceof SingleFieldConstraintEBLeftSide));
        SingleFieldConstraintEBLeftSide ebLeftSide = ((SingleFieldConstraintEBLeftSide) (getFieldConstraints()[0]));
        Assert.assertEquals("telephone", ebLeftSide.getFieldName());
        Assert.assertEquals("java.lang.Integer", ebLeftSide.getFieldType());
        Assert.assertEquals(">", ebLeftSide.getOperator());
        Assert.assertEquals("12345", ebLeftSide.getValue());
        Assert.assertEquals(3, ebLeftSide.getExpressionLeftSide().getParts().size());
        Assert.assertTrue(((ebLeftSide.getExpressionLeftSide().getParts().get(0)) instanceof ExpressionUnboundFact));
        ExpressionUnboundFact expressionUnboundFact = ((ExpressionUnboundFact) (ebLeftSide.getExpressionLeftSide().getParts().get(0)));
        Assert.assertEquals("Person", expressionUnboundFact.getName());
        Assert.assertEquals("Person", expressionUnboundFact.getClassType());
        Assert.assertEquals("Person", expressionUnboundFact.getGenericType());
        Assert.assertEquals(getFactType(), expressionUnboundFact.getFactType());
        Assert.assertEquals(null, expressionUnboundFact.getPrevious());
        Assert.assertEquals(ebLeftSide.getExpressionLeftSide().getParts().get(1), expressionUnboundFact.getNext());
        Assert.assertTrue(((ebLeftSide.getExpressionLeftSide().getParts().get(1)) instanceof ExpressionField));
        ExpressionField expressionField1 = ((ExpressionField) (ebLeftSide.getExpressionLeftSide().getParts().get(1)));
        Assert.assertEquals("contact", expressionField1.getName());
        Assert.assertEquals("org.test.Contact", expressionField1.getClassType());
        Assert.assertEquals("Contact", expressionField1.getGenericType());
        Assert.assertEquals(ebLeftSide.getExpressionLeftSide().getParts().get(0), expressionField1.getPrevious());
        Assert.assertEquals(ebLeftSide.getExpressionLeftSide().getParts().get(2), expressionField1.getNext());
        Assert.assertTrue(((ebLeftSide.getExpressionLeftSide().getParts().get(2)) instanceof ExpressionField));
        ExpressionField expressionField2 = ((ExpressionField) (ebLeftSide.getExpressionLeftSide().getParts().get(2)));
        Assert.assertEquals("telephone", expressionField2.getName());
        Assert.assertEquals("java.lang.Integer", expressionField2.getClassType());
        Assert.assertEquals(TYPE_NUMERIC_INTEGER, expressionField2.getGenericType());
        Assert.assertEquals(ebLeftSide.getExpressionLeftSide().getParts().get(1), expressionField2.getPrevious());
        Assert.assertNull(expressionField2.getNext());
    }

    @Test
    public void testNestedFieldConstraintsOnlyLeafOperator() {
        String drl = "rule \"rule1\"\n" + ((("when\n" + "ParentType( parentChildField.childField == \"hello\" )\n") + "then\n") + "end");
        addModelField("org.test.ParentType", "parentChildField", "org.test.ChildType", "ChildType");
        addModelField("org.test.ChildType", "childField", "java.lang.String", "String");
        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals(1, m.lhs.length);
        Assert.assertTrue(((m.lhs[0]) instanceof FactPattern));
        Assert.assertTrue(((getFieldConstraints()[0]) instanceof SingleFieldConstraintEBLeftSide));
        SingleFieldConstraintEBLeftSide ebLeftSide = ((SingleFieldConstraintEBLeftSide) (getFieldConstraints()[0]));
        Assert.assertEquals("childField", ebLeftSide.getFieldName());
        Assert.assertEquals("java.lang.String", ebLeftSide.getFieldType());
        Assert.assertEquals("==", ebLeftSide.getOperator());
        Assert.assertEquals("hello", ebLeftSide.getValue());
        Assert.assertEquals(3, ebLeftSide.getExpressionLeftSide().getParts().size());
        Assert.assertTrue(((ebLeftSide.getExpressionLeftSide().getParts().get(0)) instanceof ExpressionUnboundFact));
        ExpressionUnboundFact expressionUnboundFact = ((ExpressionUnboundFact) (ebLeftSide.getExpressionLeftSide().getParts().get(0)));
        Assert.assertEquals("ParentType", expressionUnboundFact.getName());
        Assert.assertEquals("ParentType", expressionUnboundFact.getClassType());
        Assert.assertEquals("ParentType", expressionUnboundFact.getGenericType());
        Assert.assertEquals(getFactType(), expressionUnboundFact.getFactType());
        Assert.assertNull(expressionUnboundFact.getPrevious());
        Assert.assertEquals(ebLeftSide.getExpressionLeftSide().getParts().get(1), expressionUnboundFact.getNext());
        Assert.assertTrue(((ebLeftSide.getExpressionLeftSide().getParts().get(1)) instanceof ExpressionField));
        ExpressionField expressionField1 = ((ExpressionField) (ebLeftSide.getExpressionLeftSide().getParts().get(1)));
        Assert.assertEquals("parentChildField", expressionField1.getName());
        Assert.assertEquals("org.test.ChildType", expressionField1.getClassType());
        Assert.assertEquals("ChildType", expressionField1.getGenericType());
        Assert.assertEquals(ebLeftSide.getExpressionLeftSide().getParts().get(0), expressionField1.getPrevious());
        Assert.assertEquals(ebLeftSide.getExpressionLeftSide().getParts().get(2), expressionField1.getNext());
        Assert.assertTrue(((ebLeftSide.getExpressionLeftSide().getParts().get(2)) instanceof ExpressionField));
        ExpressionField expressionField2 = ((ExpressionField) (ebLeftSide.getExpressionLeftSide().getParts().get(2)));
        Assert.assertEquals("childField", expressionField2.getName());
        Assert.assertEquals("java.lang.String", expressionField2.getClassType());
        Assert.assertEquals("String", expressionField2.getGenericType());
        Assert.assertEquals(ebLeftSide.getExpressionLeftSide().getParts().get(1), expressionField2.getPrevious());
        Assert.assertNull(expressionField2.getNext());
    }

    @Test
    public void testReciprocal_NestedFieldConstraintsOnlyLeafOperator() {
        // This is the inverse of "NestedFieldConstraintsOnlyLeafOperator"
        String drl = "rule \"rule1\"\n" + (((("dialect \"mvel\"\n" + "when\n") + "ParentType( this.parentChildField.childField == \"hello\" )\n") + "then\n") + "end");
        RuleModel m = new RuleModel();
        m.name = "rule1";
        // LHS Pattern
        FactPattern fp1 = new FactPattern();
        fp1.setFactType("ParentType");
        SingleFieldConstraint fp1_sfp1 = new SingleFieldConstraint();
        fp1_sfp1.setFactType("ParentType");
        fp1_sfp1.setFieldName("this");
        fp1_sfp1.setFieldType(TYPE_THIS);
        fp1_sfp1.setConstraintValueType(TYPE_UNDEFINED);
        fp1.addConstraint(fp1_sfp1);
        SingleFieldConstraint fp1_sfp2 = new SingleFieldConstraint();
        fp1_sfp2.setFactType("ParentType");
        fp1_sfp2.setFieldName("parentChildField");
        fp1_sfp2.setFieldType("ChildType");
        fp1_sfp2.setConstraintValueType(TYPE_UNDEFINED);
        fp1.addConstraint(fp1_sfp2);
        fp1_sfp2.setParent(fp1_sfp1);
        SingleFieldConstraint fp1_sfp3 = new SingleFieldConstraint();
        fp1_sfp3.setFactType("ChildType");
        fp1_sfp3.setFieldName("childField");
        fp1_sfp3.setFieldType(TYPE_STRING);
        fp1_sfp3.setOperator("==");
        fp1_sfp3.setValue("hello");
        fp1_sfp3.setConstraintValueType(TYPE_LITERAL);
        fp1.addConstraint(fp1_sfp3);
        fp1_sfp3.setParent(fp1_sfp2);
        m.addLhsItem(fp1);
        String actualDrl = RuleModelDRLPersistenceImpl.getInstance().marshal(m);
        assertEqualsIgnoreWhitespace(drl, actualDrl);
    }

    @Test
    public void testCalendarsAttribute() {
        String drl = "rule \"rule1\"\n" + ((("calendars \"myCalendar\", \"Yet Another Calendar\"\n" + "when\n") + "then\n") + "end");
        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertEquals(1, m.attributes.length);
        Assert.assertEquals("calendars", m.attributes[0].getAttributeName());
        Assert.assertEquals("myCalendar, Yet Another Calendar", getValue());
    }

    @Test
    public void testFunctionCall() {
        // BZ-1013682
        String drl = "" + ((((((((("package org.mortgages;\n" + "import org.mortgages.LoanApplication;\n") + "\n") + "rule \"my rule\"\n") + "  dialect \"mvel\"\n") + "  when\n") + "    a : LoanApplication( )\n") + "  then\n") + "    keke.clear(  );\n") + "end\n");
        HashMap<String, String> globals = new HashMap<>();
        globals.put("keke", "java.util.ArrayList");
        Mockito.when(dmo.getPackageGlobals()).thenReturn(globals);
        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertTrue(((m.rhs[0]) instanceof ActionCallMethod));
        ActionCallMethod actionGlobalCollectionAdd = ((ActionCallMethod) (m.rhs[0]));
        Assert.assertEquals("clear", actionGlobalCollectionAdd.getMethodName());
        Assert.assertEquals("keke", actionGlobalCollectionAdd.getVariable());
        Assert.assertEquals(1, actionGlobalCollectionAdd.getState());
        Assert.assertEquals(0, actionGlobalCollectionAdd.getFieldValues().length);
        assertEqualsIgnoreWhitespace(drl, RuleModelDRLPersistenceImpl.getInstance().marshal(m));
    }

    @Test
    public void testMethodCall() {
        // BZ-1042511
        String drl = "" + (((((((((("package org.mortgages;\n" + "import org.mortgages.LoanApplication;\n") + "import java.util.Map;\n") + "rule \"my rule\"\n") + "  dialect \"mvel\"\n") + "  when\n") + "    a : LoanApplication( )\n") + "    m : Map()\n") + "  then\n") + "    m.put(\"key\", a );\n") + "end\n");
        HashMap<String, String> globals = new HashMap<>();
        Mockito.when(dmo.getPackageGlobals()).thenReturn(globals);
        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertEquals(2, m.getImports().getImports().size());
        Assert.assertTrue(((m.rhs[0]) instanceof ActionCallMethod));
        ActionCallMethod mc = ((ActionCallMethod) (m.rhs[0]));
        Assert.assertEquals("put", mc.getMethodName());
        Assert.assertEquals("m", mc.getVariable());
        Assert.assertEquals(1, mc.getState());
        Assert.assertEquals(2, mc.getFieldValues().length);
        ActionFieldValue f1 = mc.getFieldValue(0);
        Assert.assertEquals("key", f1.getValue());
        ActionFieldValue f2 = mc.getFieldValue(1);
        Assert.assertEquals("a", f2.getValue());
        String marshalled = RuleModelDRLPersistenceImpl.getInstance().marshal(m);
        RuleModelDRLPersistenceUnmarshallingTest.logger.debug(marshalled);
        assertEqualsIgnoreWhitespace(drl, marshalled);
    }

    @Test
    public void testMethodCallCheckParameterDataTypes1() {
        // BZ-1045423
        String drl = "" + (((((((((("package org.mortgages;\n" + "import org.mortgages.LoanApplication;\n") + "import java.util.Map;\n") + "rule \"my rule\"\n") + "  dialect \"mvel\"\n") + "  when\n") + "    a : LoanApplication( )\n") + "    m : Map()\n") + "  then\n") + "    m.put(\"key\", a );\n") + "end\n");
        Map<String, List<MethodInfo>> methodInformation = new HashMap<>();
        List<MethodInfo> mapMethodInformation = new ArrayList<>();
        mapMethodInformation.add(new MethodInfo("put", Arrays.asList("java.lang.Object", "java.lang.Object"), "void", "void", "java.util.Map"));
        methodInformation.put("java.util.Map", mapMethodInformation);
        Mockito.when(dmo.getModuleMethodInformation()).thenReturn(methodInformation);
        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertTrue(((m.rhs[0]) instanceof ActionCallMethod));
        ActionCallMethod mc = ((ActionCallMethod) (m.rhs[0]));
        Assert.assertEquals("put", mc.getMethodName());
        Assert.assertEquals("m", mc.getVariable());
        Assert.assertEquals(1, mc.getState());
        Assert.assertEquals(2, mc.getFieldValues().length);
        ActionFieldValue f1 = mc.getFieldValue(0);
        Assert.assertEquals("\"key\"", f1.getValue());
        Assert.assertEquals("java.lang.Object", f1.getType());
        Assert.assertEquals(FieldNatureType.TYPE_LITERAL, f1.getNature());
        ActionFieldValue f2 = mc.getFieldValue(1);
        Assert.assertEquals("a", f2.getValue());
        Assert.assertEquals("java.lang.Object", f2.getType());
        Assert.assertEquals(FieldNatureType.TYPE_VARIABLE, f2.getNature());
        String marshalled = RuleModelDRLPersistenceImpl.getInstance().marshal(m);
        RuleModelDRLPersistenceUnmarshallingTest.logger.debug(marshalled);
        assertEqualsIgnoreWhitespace(drl, marshalled);
    }

    @Test
    public void testMethodCallCheckParameterDataTypes2() {
        // BZ-1045423
        String drl = "" + (((((((("package org.mortgages;\n" + "import org.mortgages.MyType;\n") + "rule \"my rule\"\n") + "  dialect \"mvel\"\n") + "  when\n") + "    t : MyType( )\n") + "  then\n") + "    t.doSomething( 1 * 2 );\n") + "end\n");
        Map<String, List<MethodInfo>> methodInformation = new HashMap<>();
        List<MethodInfo> mapMethodInformation = new ArrayList<>();
        mapMethodInformation.add(new MethodInfo("doSomething", Collections.singletonList(TYPE_NUMERIC_INTEGER), "void", "void", "org.mortgages.MyType"));
        methodInformation.put("org.mortgages.MyType", mapMethodInformation);
        Mockito.when(dmo.getModuleMethodInformation()).thenReturn(methodInformation);
        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertTrue(((m.rhs[0]) instanceof ActionCallMethod));
        ActionCallMethod mc = ((ActionCallMethod) (m.rhs[0]));
        Assert.assertEquals("doSomething", mc.getMethodName());
        Assert.assertEquals("t", mc.getVariable());
        Assert.assertEquals(1, mc.getState());
        Assert.assertEquals(1, mc.getFieldValues().length);
        ActionFieldValue f1 = mc.getFieldValue(0);
        Assert.assertEquals("1 * 2", f1.getValue());
        Assert.assertEquals(TYPE_NUMERIC_INTEGER, f1.getType());
        Assert.assertEquals(TYPE_FORMULA, f1.getNature());
        String marshalled = RuleModelDRLPersistenceImpl.getInstance().marshal(m);
        RuleModelDRLPersistenceUnmarshallingTest.logger.debug(marshalled);
        assertEqualsIgnoreWhitespace(drl, marshalled);
    }

    @Test
    public void testMethodCallCheckParameterDataTypes3() {
        // BZ-1045423
        String drl = "" + ((((((((("package org.mortgages;\n" + "import org.mortgages.MyType;\n") + "rule \"my rule\"\n") + "  dialect \"mvel\"\n") + "  when\n") + "    i : Integer( )\n") + "    t : MyType( )\n") + "  then\n") + "    t.doSomething( i );\n") + "end\n");
        Map<String, List<MethodInfo>> methodInformation = new HashMap<>();
        List<MethodInfo> mapMethodInformation = new ArrayList<>();
        mapMethodInformation.add(new MethodInfo("doSomething", Collections.singletonList(TYPE_NUMERIC_INTEGER), "void", "void", "org.mortgages.MyType"));
        methodInformation.put("org.mortgages.MyType", mapMethodInformation);
        Mockito.when(dmo.getModuleMethodInformation()).thenReturn(methodInformation);
        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertTrue(((m.rhs[0]) instanceof ActionCallMethod));
        ActionCallMethod mc = ((ActionCallMethod) (m.rhs[0]));
        Assert.assertEquals("doSomething", mc.getMethodName());
        Assert.assertEquals("t", mc.getVariable());
        Assert.assertEquals(1, mc.getState());
        Assert.assertEquals(1, mc.getFieldValues().length);
        ActionFieldValue f1 = mc.getFieldValue(0);
        Assert.assertEquals("i", f1.getValue());
        Assert.assertEquals(TYPE_NUMERIC_INTEGER, f1.getType());
        Assert.assertEquals(FieldNatureType.TYPE_VARIABLE, f1.getNature());
        String marshalled = RuleModelDRLPersistenceImpl.getInstance().marshal(m);
        RuleModelDRLPersistenceUnmarshallingTest.logger.debug(marshalled);
        assertEqualsIgnoreWhitespace(drl, marshalled);
    }

    @Test
    public void testGlobalCollectionAdd() {
        // BZ-1013682
        String drl = "package org.mortgages;\n" + ((((((((("\n" + "import org.mortgages.LoanApplication;\n") + "\n") + "rule \"Bankruptcy history\"\n") + "  dialect \"mvel\"\n") + "  when\n") + "    a : LoanApplication( )\n") + "  then\n") + "    keke.add( a );\n") + "end");
        HashMap<String, String> globals = new HashMap<>();
        globals.put("keke", "java.util.ArrayList");
        Mockito.when(dmo.getPackageGlobals()).thenReturn(globals);
        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertTrue(((m.rhs[0]) instanceof ActionGlobalCollectionAdd));
        ActionGlobalCollectionAdd actionGlobalCollectionAdd = ((ActionGlobalCollectionAdd) (m.rhs[0]));
        Assert.assertEquals("keke", actionGlobalCollectionAdd.getGlobalName());
        Assert.assertEquals("a", actionGlobalCollectionAdd.getFactName());
    }

    @Test
    public void testFieldConstraintLessThanOrEqualTo() {
        String drl = "rule \"rule1\"\n" + ((("when\n" + "Applicant( age <= 22 )\n") + "then\n") + "end");
        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals("rule1", m.name);
        Assert.assertEquals(1, m.lhs.length);
        IPattern p = m.lhs[0];
        Assert.assertTrue((p instanceof FactPattern));
        FactPattern fp = ((FactPattern) (p));
        Assert.assertEquals("Applicant", fp.getFactType());
        Assert.assertEquals(1, fp.getNumberOfConstraints());
        FieldConstraint fc = fp.getConstraint(0);
        Assert.assertNotNull(fc);
        Assert.assertTrue((fc instanceof SingleFieldConstraint));
        SingleFieldConstraint sfc = ((SingleFieldConstraint) (fc));
        Assert.assertEquals("<=", sfc.getOperator());
        Assert.assertEquals("22", sfc.getValue());
    }

    @Test
    public void testExpressionWithListSize() throws Exception {
        String drl = "" + ((((("rule \"Borked\"\n" + "  dialect \"mvel\"\n") + "  when\n") + "    Company( emps.size() == 0 )\n") + "  then\n") + "end");
        addModelField("Company", "emps", "java.util.List", "List");
        addMethodInformation("java.util.List", "size", Collections.emptyList(), "int", null, TYPE_NUMERIC_INTEGER);
        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertEquals(1, m.lhs.length);
        Assert.assertTrue(((m.lhs[0]) instanceof FactPattern));
        FactPattern factPattern = ((FactPattern) (m.lhs[0]));
        Assert.assertEquals(1, factPattern.getConstraintList().getConstraints().length);
        Assert.assertTrue(((factPattern.getConstraintList().getConstraints()[0]) instanceof SingleFieldConstraintEBLeftSide));
        SingleFieldConstraintEBLeftSide constraint = ((SingleFieldConstraintEBLeftSide) (factPattern.getConstraintList().getConstraints()[0]));
        Assert.assertEquals("size", constraint.getFieldName());
        Assert.assertEquals("int", constraint.getFieldType());
        Assert.assertEquals("0", constraint.getValue());
        Assert.assertEquals("==", constraint.getOperator());
        Assert.assertEquals(1, constraint.getConstraintValueType());
    }

    @Test
    public void testMVELInlineList() throws Exception {
        String drl = "" + (((((("rule \"Borked\"\n" + "  dialect \"mvel\"\n") + "  when\n") + "    c : Company( )\n") + "  then\n") + "    c.setEmps( [\"item1\", \"item2\"] );\n") + "end");
        addModelField("Company", "emps", "java.util.List", TYPE_COLLECTION);
        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertEquals(1, m.rhs.length);
        Assert.assertTrue(((m.rhs[0]) instanceof ActionSetField));
        ActionSetField actionSetField = ((ActionSetField) (m.rhs[0]));
        Assert.assertEquals("c", actionSetField.getVariable());
        Assert.assertEquals(1, actionSetField.getFieldValues().length);
        ActionFieldValue actionFieldValue = actionSetField.getFieldValues()[0];
        Assert.assertEquals("[\"item1\", \"item2\"]", actionFieldValue.getValue());
        Assert.assertEquals("emps", actionFieldValue.getField());
        Assert.assertEquals(TYPE_FORMULA, actionFieldValue.getNature());
        Assert.assertEquals(TYPE_COLLECTION, actionFieldValue.getType());
    }

    @Test
    public void testFunctionInRHS() throws Exception {
        String drl = "" + ((((((("rule \"Borked\"\n" + "  dialect \"mvel\"\n") + "  when\n") + "    application : Application( )\n") + "  then\n") + "    application.setApr( application.getApr() + 5 );\n") + "    update( application )") + "end");
        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertTrue(((m.rhs[0]) instanceof ActionUpdateField));
        ActionUpdateField field = ((ActionUpdateField) (m.rhs[0]));
        Assert.assertNotNull(field.getFieldValues()[0]);
        ActionFieldValue value = field.getFieldValues()[0];
        Assert.assertEquals("apr", value.getField());
        Assert.assertEquals("application.getApr() + 5", value.getValue());
        Assert.assertEquals(TYPE_FORMULA, value.getNature());
        Assert.assertEquals(TYPE_NUMERIC, value.getType());
    }

    @Test
    public void testFieldVars() throws Exception {
        String drl = "" + ((((("rule \"Borked\"\n" + "  dialect \"mvel\"\n") + "  when\n") + "    Customer( var:contact )\n") + "  then\n") + "end");
        addModelField("Customer", "contact", "Contact", "Contact");
        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        FactPattern pattern = ((FactPattern) (m.lhs[0]));
        SingleFieldConstraint constraint = ((SingleFieldConstraint) (pattern.getFieldConstraints()[0]));
        Assert.assertEquals("var", constraint.getFieldBinding());
        Assert.assertEquals("Customer", constraint.getFactType());
        Assert.assertEquals("contact", constraint.getFieldName());
        Assert.assertEquals("Contact", constraint.getFieldType());
    }

    @Test
    public void testFieldVarsWithImports() throws Exception {
        String drl = "" + (((((("import org.test.Customer\n" + "rule \"Borked\"\n") + "  dialect \"mvel\"\n") + "  when\n") + "    Customer( var:contact )\n") + "  then\n") + "end");
        addModelField("org.test.Customer", "contact", "org.test.Contact", "org.test.Contact");
        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        FactPattern pattern = ((FactPattern) (m.lhs[0]));
        SingleFieldConstraint constraint = ((SingleFieldConstraint) (pattern.getFieldConstraints()[0]));
        Assert.assertEquals("var", constraint.getFieldBinding());
        Assert.assertEquals("Customer", constraint.getFactType());
        Assert.assertEquals("contact", constraint.getFieldName());
        Assert.assertEquals("org.test.Contact", constraint.getFieldType());
    }

    @Test
    public void testFieldVarsFactTypeInTheSamePackage() throws Exception {
        String drl = "" + (((((("package org.test\n" + "rule \"Borked\"\n") + "  dialect \"mvel\"\n") + "  when\n") + "    Customer( var:contact )\n") + "  then\n") + "end");
        addModelField("org.test.Customer", "contact", "org.test.Contact", "org.test.Contact");
        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        FactPattern pattern = ((FactPattern) (m.lhs[0]));
        SingleFieldConstraint constraint = ((SingleFieldConstraint) (pattern.getFieldConstraints()[0]));
        Assert.assertEquals("var", constraint.getFieldBinding());
        Assert.assertEquals("Customer", constraint.getFactType());
        Assert.assertEquals("contact", constraint.getFieldName());
        Assert.assertEquals("org.test.Contact", constraint.getFieldType());
    }

    @Test
    public void testSingleFieldConstraintEBLeftSide() throws Exception {
        String drl = "" + ((((("rule \" broken \"\n" + "dialect \"mvel\"\n") + "  when\n") + "    Customer( contact != null , contact.tel1 > \"15\" )\n") + "  then\n") + "end");
        addModelField("Customer", "contact", "Contact", "Contact");
        addModelField("Contact", "tel1", "String", "String");
        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        FactPattern pattern = ((FactPattern) (m.lhs[0]));
        SingleFieldConstraint constraint = ((SingleFieldConstraint) (pattern.getFieldConstraints()[0]));
        Assert.assertEquals("Customer", constraint.getFactType());
        Assert.assertEquals("contact", constraint.getFieldName());
        Assert.assertEquals("Contact", constraint.getFieldType());
        SingleFieldConstraintEBLeftSide constraint2 = ((SingleFieldConstraintEBLeftSide) (pattern.getFieldConstraints()[1]));
        Assert.assertEquals("tel1", constraint2.getFieldName());
        Assert.assertEquals("String", constraint2.getFieldType());
        Assert.assertEquals("15", constraint2.getValue());
        Assert.assertEquals(">", constraint2.getOperator());
        Assert.assertEquals(3, constraint2.getExpressionLeftSide().getParts().size());
        ExpressionPart part1 = constraint2.getExpressionLeftSide().getParts().get(0);
        Assert.assertEquals("Customer", part1.getName());
        Assert.assertEquals("Customer", part1.getClassType());
        Assert.assertEquals("Customer", part1.getGenericType());
        ExpressionPart part2 = constraint2.getExpressionLeftSide().getParts().get(1);
        Assert.assertEquals("contact", part2.getName());
        Assert.assertEquals("Contact", part2.getClassType());
        Assert.assertEquals("Contact", part2.getGenericType());
        ExpressionPart part3 = constraint2.getExpressionLeftSide().getParts().get(2);
        Assert.assertEquals("tel1", part3.getName());
        Assert.assertEquals("String", part3.getClassType());
        Assert.assertEquals("String", part3.getGenericType());
    }

    @Test
    public void testExpressionEditorLeftToOperator() throws Exception {
        String drl = "" + (((((("rule \"r1\"\n" + "dialect \"mvel\"\n") + "when\n") + " a: Applicant()\n") + " Applicant( age == a.age )\n") + "then\n") + "end");
        addModelField("Applicant", "this", "Applicant", TYPE_THIS);
        addModelField("Applicant", "age", "java.lang.Integer", "Integer");
        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        FactPattern pattern1 = ((FactPattern) (m.lhs[0]));
        Assert.assertEquals("a", pattern1.getBoundName());
        FactPattern pattern2 = ((FactPattern) (m.lhs[1]));
        Assert.assertEquals(1, pattern2.getConstraintList().getNumberOfConstraints());
        Assert.assertTrue(((getConstraint(0)) instanceof SingleFieldConstraint));
        SingleFieldConstraint constraint = ((SingleFieldConstraint) (getConstraint(0)));
        Assert.assertEquals("age", constraint.getFieldName());
        Assert.assertEquals("==", constraint.getOperator());
        Assert.assertEquals(TYPE_EXPR_BUILDER_VALUE, constraint.getConstraintValueType());
        Assert.assertEquals("", constraint.getValue());
        Assert.assertEquals(2, constraint.getExpressionValue().getParts().size());
        Assert.assertTrue(((constraint.getExpressionValue().getParts().get(0)) instanceof ExpressionVariable));
        ExpressionVariable expressionVariable = ((ExpressionVariable) (constraint.getExpressionValue().getParts().get(0)));
        Assert.assertEquals("a", expressionVariable.getName());
        Assert.assertEquals("Applicant", expressionVariable.getClassType());
        Assert.assertEquals("this", expressionVariable.getGenericType());
        Assert.assertEquals(constraint.getExpressionValue().getParts().get(1), expressionVariable.getNext());
        Assert.assertTrue(((constraint.getExpressionValue().getParts().get(1)) instanceof ExpressionField));
        ExpressionField expressionField = ((ExpressionField) (constraint.getExpressionValue().getParts().get(1)));
        Assert.assertEquals("age", expressionField.getName());
        Assert.assertEquals("java.lang.Integer", expressionField.getClassType());
        Assert.assertEquals("Integer", expressionField.getGenericType());
    }

    @Test
    public void testEnumeration() throws Exception {
        // https://bugzilla.redhat.com/show_bug.cgi?id=1047879
        String drl = "import org.drools.workbench.models.commons.backend.rule.TestEnum;\n" + ((((("rule \"r1\"\n" + "dialect \"mvel\"\n") + "when\n") + "OuterClassWithEnums( outerField == TestEnum.VALUE1 )\n") + "then\n") + "end");
        addModelField("OuterClassWithEnums", "outerField", TestEnum.class.getSimpleName(), TYPE_COMPARABLE);
        addJavaEnumDefinition("OuterClassWithEnums", "outerField", new String[]{ "TestEnum.VALUE1=TestEnum.VALUE1", "TestEnum.VALUE2=TestEnum.VALUE2" });
        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        FactPattern pattern = ((FactPattern) (m.lhs[0]));
        Assert.assertEquals(1, pattern.getNumberOfConstraints());
        Assert.assertTrue(((pattern.getConstraint(0)) instanceof SingleFieldConstraint));
        SingleFieldConstraint constraint = ((SingleFieldConstraint) (pattern.getConstraint(0)));
        Assert.assertEquals("OuterClassWithEnums", constraint.getFactType());
        Assert.assertEquals("outerField", constraint.getFieldName());
        Assert.assertEquals(TYPE_COMPARABLE, constraint.getFieldType());
        Assert.assertEquals("==", constraint.getOperator());
        Assert.assertEquals("TestEnum.VALUE1", constraint.getValue());
        Assert.assertEquals(TYPE_ENUM, constraint.getConstraintValueType());
        final String drl2 = RuleModelDRLPersistenceImpl.getInstance().marshal(m);
        assertEqualsIgnoreWhitespace(drl, drl2);
    }

    @Test
    public void testFullyQualifiedClassNameEnumeration() throws Exception {
        // https://bugzilla.redhat.com/show_bug.cgi?id=1047879
        String drl = "import org.drools.workbench.models.commons.backend.rule.TestEnum;\n" + (((((("import org.drools.workbench.models.commons.backend.rule.OuterClassWithEnums;\n" + "rule \"r1\"\n") + "dialect \"mvel\"\n") + "when\n") + "OuterClassWithEnums( outerField == TestEnum.VALUE1 )\n") + "then\n") + "end");
        addModelField("org.drools.workbench.models.commons.backend.rule.OuterClassWithEnums", "outerField", TestEnum.class.getSimpleName(), TYPE_COMPARABLE);
        addJavaEnumDefinition("org.drools.workbench.models.commons.backend.rule.OuterClassWithEnums", "outerField", new String[]{ "TestEnum.VALUE1=TestEnum.VALUE1", "TestEnum.VALUE2=TestEnum.VALUE2" });
        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        FactPattern pattern = ((FactPattern) (m.lhs[0]));
        Assert.assertEquals(1, pattern.getNumberOfConstraints());
        Assert.assertTrue(((pattern.getConstraint(0)) instanceof SingleFieldConstraint));
        SingleFieldConstraint constraint = ((SingleFieldConstraint) (pattern.getConstraint(0)));
        Assert.assertEquals("OuterClassWithEnums", constraint.getFactType());
        Assert.assertEquals("outerField", constraint.getFieldName());
        Assert.assertEquals(TYPE_COMPARABLE, constraint.getFieldType());
        Assert.assertEquals("==", constraint.getOperator());
        Assert.assertEquals("TestEnum.VALUE1", constraint.getValue());
        Assert.assertEquals(TYPE_ENUM, constraint.getConstraintValueType());
        final String drl2 = RuleModelDRLPersistenceImpl.getInstance().marshal(m);
        assertEqualsIgnoreWhitespace(drl, drl2);
    }

    @Test
    public void testEnumerationNestedClasses() throws Exception {
        // https://bugzilla.redhat.com/show_bug.cgi?id=1047879
        String drl = "import org.drools.workbench.models.commons.backend.rule.TestEnum;\n" + ((((("rule \"r1\"\n" + "dialect \"mvel\"\n") + "when\n") + "OuterClassWithEnums( innerClass.innerField == TestEnum.VALUE1 )\n") + "then\n") + "end");
        addModelField("OuterClassWithEnums", "innerClass", "InnerClassWithEnums", "InnerClassWithEnums");
        addModelField("InnerClassWithEnums", "innerField", TestEnum.class.getSimpleName(), TYPE_COMPARABLE);
        addJavaEnumDefinition("InnerClassWithEnums", "innerField", new String[]{ "TestEnum.VALUE1=TestEnum.VALUE1", "TestEnum.VALUE2=TestEnum.VALUE2" });
        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        FactPattern pattern = ((FactPattern) (m.lhs[0]));
        Assert.assertEquals(1, pattern.getNumberOfConstraints());
        Assert.assertTrue(((pattern.getConstraint(0)) instanceof SingleFieldConstraintEBLeftSide));
        final SingleFieldConstraintEBLeftSide constraint = ((SingleFieldConstraintEBLeftSide) (pattern.getConstraint(0)));
        Assert.assertEquals(3, constraint.getExpressionLeftSide().getParts().size());
        Assert.assertTrue(((constraint.getExpressionLeftSide().getParts().get(0)) instanceof ExpressionUnboundFact));
        final ExpressionUnboundFact eubf = ((ExpressionUnboundFact) (constraint.getExpressionLeftSide().getParts().get(0)));
        Assert.assertEquals("OuterClassWithEnums", eubf.getName());
        Assert.assertEquals("OuterClassWithEnums", eubf.getClassType());
        Assert.assertEquals("OuterClassWithEnums", eubf.getGenericType());
        Assert.assertTrue(((constraint.getExpressionLeftSide().getParts().get(1)) instanceof ExpressionField));
        final ExpressionField ef1 = ((ExpressionField) (constraint.getExpressionLeftSide().getParts().get(1)));
        Assert.assertEquals("innerClass", ef1.getName());
        Assert.assertEquals("InnerClassWithEnums", ef1.getClassType());
        Assert.assertEquals("InnerClassWithEnums", ef1.getGenericType());
        Assert.assertTrue(((constraint.getExpressionLeftSide().getParts().get(2)) instanceof ExpressionField));
        final ExpressionField ef2 = ((ExpressionField) (constraint.getExpressionLeftSide().getParts().get(2)));
        Assert.assertEquals("innerField", ef2.getName());
        Assert.assertEquals("TestEnum", ef2.getClassType());
        Assert.assertEquals(TYPE_COMPARABLE, ef2.getGenericType());
        Assert.assertEquals("OuterClassWithEnums", constraint.getFactType());
        Assert.assertEquals("innerField", constraint.getFieldName());
        Assert.assertEquals("TestEnum", constraint.getFieldType());
        Assert.assertEquals("==", constraint.getOperator());
        Assert.assertEquals("TestEnum.VALUE1", constraint.getValue());
        Assert.assertEquals(TYPE_ENUM, constraint.getConstraintValueType());
        final String drl2 = RuleModelDRLPersistenceImpl.getInstance().marshal(m);
        assertEqualsIgnoreWhitespace(drl, drl2);
    }

    @Test
    public void testFullyQualifiedClassNameEnumerationNestedClasses() throws Exception {
        // https://bugzilla.redhat.com/show_bug.cgi?id=1047879
        String drl = "import org.drools.workbench.models.commons.backend.rule.TestEnum;\n" + ((((((("import org.drools.workbench.models.commons.backend.rule.OuterClassWithEnums;\n" + "import org.drools.workbench.models.commons.backend.rule.OuterClassWithEnums.InnerClassWithEnums;\n") + "rule \"r1\"\n") + "dialect \"mvel\"\n") + "when\n") + "OuterClassWithEnums( innerClass.innerField == TestEnum.VALUE1 )\n") + "then\n") + "end");
        addModelField("org.drools.workbench.models.commons.backend.rule.OuterClassWithEnums", "innerClass", "org.drools.workbench.models.commons.backend.rule.OuterClassWithEnums$InnerClassWithEnums", "OuterClassWithEnums$InnerClassWithEnums");
        addModelField("org.drools.workbench.models.commons.backend.rule.OuterClassWithEnums$InnerClassWithEnums", "innerField", TestEnum.class.getSimpleName(), TYPE_COMPARABLE);
        addJavaEnumDefinition("org.drools.workbench.models.commons.backend.rule.OuterClassWithEnums$InnerClassWithEnums", "innerField", new String[]{ "TestEnum.VALUE1=TestEnum.VALUE1", "TestEnum.VALUE2=TestEnum.VALUE2" });
        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        FactPattern pattern = ((FactPattern) (m.lhs[0]));
        Assert.assertEquals(1, pattern.getNumberOfConstraints());
        Assert.assertTrue(((pattern.getConstraint(0)) instanceof SingleFieldConstraintEBLeftSide));
        final SingleFieldConstraintEBLeftSide constraint = ((SingleFieldConstraintEBLeftSide) (pattern.getConstraint(0)));
        Assert.assertEquals(3, constraint.getExpressionLeftSide().getParts().size());
        Assert.assertTrue(((constraint.getExpressionLeftSide().getParts().get(0)) instanceof ExpressionUnboundFact));
        final ExpressionUnboundFact eubf = ((ExpressionUnboundFact) (constraint.getExpressionLeftSide().getParts().get(0)));
        Assert.assertEquals("OuterClassWithEnums", eubf.getName());
        Assert.assertEquals("OuterClassWithEnums", eubf.getClassType());
        Assert.assertEquals("OuterClassWithEnums", eubf.getGenericType());
        Assert.assertTrue(((constraint.getExpressionLeftSide().getParts().get(1)) instanceof ExpressionField));
        final ExpressionField ef1 = ((ExpressionField) (constraint.getExpressionLeftSide().getParts().get(1)));
        Assert.assertEquals("innerClass", ef1.getName());
        Assert.assertEquals("org.drools.workbench.models.commons.backend.rule.OuterClassWithEnums$InnerClassWithEnums", ef1.getClassType());
        Assert.assertEquals("OuterClassWithEnums$InnerClassWithEnums", ef1.getGenericType());
        Assert.assertTrue(((constraint.getExpressionLeftSide().getParts().get(2)) instanceof ExpressionField));
        final ExpressionField ef2 = ((ExpressionField) (constraint.getExpressionLeftSide().getParts().get(2)));
        Assert.assertEquals("innerField", ef2.getName());
        Assert.assertEquals("TestEnum", ef2.getClassType());
        Assert.assertEquals(TYPE_COMPARABLE, ef2.getGenericType());
        Assert.assertEquals("OuterClassWithEnums", constraint.getFactType());
        Assert.assertEquals("innerField", constraint.getFieldName());
        Assert.assertEquals("TestEnum", constraint.getFieldType());
        Assert.assertEquals("==", constraint.getOperator());
        Assert.assertEquals("TestEnum.VALUE1", constraint.getValue());
        Assert.assertEquals(TYPE_ENUM, constraint.getConstraintValueType());
        final String drl2 = RuleModelDRLPersistenceImpl.getInstance().marshal(m);
        assertEqualsIgnoreWhitespace(drl, drl2);
    }

    @Test
    public void testCalendars() {
        // BZ1059232 - Guided rule editor: calendars attribute is broken when a list of calendars is used
        String drl = "package org.mortgages;\n" + ((((((("\n" + "import java.lang.Number;\n") + "rule \"Test\"\n") + "  calendars \"a\" ,\"b\"\n") + "  dialect \"mvel\"\n") + "  when\n") + "  then\n") + "end\n");
        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m.attributes[0]);
        RuleAttribute attribute = m.attributes[0];
        Assert.assertEquals("calendars", attribute.getAttributeName());
        Assert.assertEquals("a, b", attribute.getValue());
    }

    @Test
    public void testFromRestrictions() {
        String drl = "package org.mortgages;\n" + (((((((("\n" + "import java.lang.Number;\n") + "rule \"Test\"\n") + "  dialect \"mvel\"\n") + "  when\n") + "    reserva : Reserva( )\n") + "    itinerario : Itinerario( destino == \"USA\" ) from reserva.itinerarios\n") + "  then\n") + "end\n");
        addModelField("org.mortgages.Reserva", "itinerarios", "java.lang.List", TYPE_COLLECTION);
        addModelField("org.mortgages.Itinerario", "destino", "String", TYPE_STRING);
        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertTrue(((m.lhs[1]) instanceof FromCompositeFactPattern));
        FromCompositeFactPattern factPattern = ((FromCompositeFactPattern) (m.lhs[1]));
        Assert.assertNotNull(factPattern.getFactPattern().getConstraintList());
        Assert.assertEquals(1, factPattern.getFactPattern().getConstraintList().getNumberOfConstraints());
        SingleFieldConstraint constraint = ((SingleFieldConstraint) (getFieldConstraints()[0]));
        Assert.assertEquals("Itinerario", constraint.getFactType());
        Assert.assertEquals("destino", constraint.getFieldName());
        Assert.assertEquals(TYPE_STRING, constraint.getFieldType());
        Assert.assertEquals("USA", constraint.getValue());
        Assert.assertEquals("==", constraint.getOperator());
    }

    @Test
    public void testFactsWithSameName() {
        String drl = "package org.pkg1;\n" + ((((((("\n" + "import java.lang.Number;\n") + "rule \"Test\"\n") + "  dialect \"mvel\"\n") + "  when\n") + "    Fact( field.field )\n") + "  then\n") + "end\n");
        addModelField("org.pkg1.Fact", "field", "org.pkg1.SubFact", "SubFact");
        addModelField("org.pkg1.SubFact", "field", "String", TYPE_STRING);
        addModelField("org.pkg2.Fact", "someOtherField", "org.pkg2.SubFact", "SubFact");
        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertEquals(1, m.lhs.length);
    }

    @Test
    public void testFactsWithSameNameImports() {
        String drl = "package org.test;\n" + ((((((("\n" + "import org.pkg1.Fact;\n") + "rule \"Test\"\n") + "  dialect \"mvel\"\n") + "  when\n") + "    Fact( field.field )\n") + "  then\n") + "end\n");
        addModelField("org.pkg1.Fact", "field", "org.pkg1.SubFact", "SubFact");
        addModelField("org.pkg1.SubFact", "field", "String", TYPE_STRING);
        addModelField("org.pkg2.Fact", "someOtherField", "org.pkg2.SubFact", "SubFact");
        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertEquals(1, m.lhs.length);
    }

    @Test
    public void testFromBoundVariable() {
        String drl = "import java.lang.Number;\n" + (((((("import org.drools.workbench.models.commons.backend.rule.Counter;\n" + "rule \"rule1\"\n") + "when\n") + "cc : Counter()\n") + "Number() from cc.number\n") + "then\n") + "end");
        addModelField("org.drools.workbench.models.commons.backend.rule.Counter", "number", "java.lang.Number", TYPE_NUMERIC);
        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals("rule1", m.name);
        Assert.assertEquals(2, m.lhs.length);
        IPattern p0 = m.lhs[0];
        Assert.assertTrue((p0 instanceof FactPattern));
        FactPattern fp0 = ((FactPattern) (p0));
        Assert.assertEquals("Counter", fp0.getFactType());
        Assert.assertEquals("cc", fp0.getBoundName());
        Assert.assertEquals(0, fp0.getNumberOfConstraints());
        IPattern p1 = m.lhs[1];
        Assert.assertTrue((p1 instanceof FromCompositeFactPattern));
        FromCompositeFactPattern fcfp1 = ((FromCompositeFactPattern) (p1));
        FactPattern fp1 = fcfp1.getFactPattern();
        ExpressionFormLine efl1 = fcfp1.getExpression();
        Assert.assertNotNull(fp1);
        Assert.assertNotNull(efl1);
        Assert.assertEquals("Number", fp1.getFactType());
        Assert.assertEquals(0, fp1.getNumberOfConstraints());
        Assert.assertEquals(2, efl1.getParts().size());
        Assert.assertTrue(((efl1.getParts().get(0)) instanceof ExpressionVariable));
        Assert.assertTrue(((efl1.getParts().get(1)) instanceof ExpressionField));
        ExpressionVariable eflv1 = ((ExpressionVariable) (efl1.getParts().get(0)));
        Assert.assertEquals("cc", eflv1.getName());
        Assert.assertEquals("Counter", eflv1.getClassType());
        Assert.assertEquals(TYPE_NUMERIC, eflv1.getGenericType());
        ExpressionField eflf1 = ((ExpressionField) (efl1.getParts().get(1)));
        Assert.assertEquals("number", eflf1.getName());
        Assert.assertEquals("java.lang.Number", eflf1.getClassType());
        Assert.assertEquals(TYPE_NUMERIC, eflf1.getGenericType());
    }

    @Test
    public void testFromAccumulate() {
        String drl = "package org.test;\n" + ((((("import java.lang.Number;\n" + "rule \"rule1\"\n") + "when\n") + "  total : Number( intValue > 0 ) from accumulate ( Applicant( age < 30 ), count() )\n") + "then\n") + "end");
        addModelField("java.lang.Number", "intValue", "java.lang.Integer", TYPE_NUMERIC_INTEGER);
        addModelField("org.test.Applicant", "this", "org.test.Applicant", TYPE_THIS);
        addModelField("org.test.Applicant", "age", Integer.class.getName(), TYPE_NUMERIC_INTEGER);
        Mockito.when(dmo.getPackageName()).thenReturn("org.test");
        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertTrue(((m.lhs[0]) instanceof FromAccumulateCompositeFactPattern));
        FromAccumulateCompositeFactPattern pattern = ((FromAccumulateCompositeFactPattern) (m.lhs[0]));
        Assert.assertNotNull(pattern.getFactPattern());
        FactPattern factPattern = pattern.getFactPattern();
        Assert.assertEquals("total", factPattern.getBoundName());
        Assert.assertNotNull(factPattern.getConstraintList());
        Assert.assertEquals(1, factPattern.getConstraintList().getNumberOfConstraints());
        FieldConstraint constraint = factPattern.getConstraintList().getConstraint(0);
        Assert.assertTrue((constraint instanceof SingleFieldConstraint));
        SingleFieldConstraint fieldConstraint = ((SingleFieldConstraint) (constraint));
        Assert.assertEquals("Number", fieldConstraint.getFactType());
        Assert.assertEquals("intValue", fieldConstraint.getFieldName());
        Assert.assertEquals("Integer", fieldConstraint.getFieldType());
        Assert.assertEquals(">", fieldConstraint.getOperator());
        Assert.assertEquals("0", fieldConstraint.getValue());
    }

    @Test
    public void testSimpleDSLExpansionLHS() {
        String drl = "rule \"rule1\"\n" + (((("when\n" + "Es gibt einen Vertrag\n") + "- Rabatt nicht mehr als 123\n") + "then\n") + "end\n");
        final String dslDefinition = "Es gibt einen Vertrag";
        final String dslFile = ("[condition][vertrag]" + dslDefinition) + "=vertrag : Vertrag()";
        final String dslDefinition2 = "- Rabatt nicht mehr als {rabatt}";
        final String dslFile2 = ("[condition][vertrag]" + dslDefinition2) + "=rabatt < {rabatt}";
        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshalUsingDSL(drl, Collections.emptyList(), dmo, dslFile, dslFile2);
        Assert.assertNotNull(m);
        Assert.assertTrue(((m.lhs[0]) instanceof DSLSentence));
        DSLSentence dslSentence = ((DSLSentence) (m.lhs[0]));
        Assert.assertEquals("vertrag : Vertrag()", dslSentence.getDrl());
        Assert.assertEquals(dslDefinition, dslSentence.getDefinition());
        Assert.assertEquals(0, dslSentence.getValues().size());
        DSLSentence dslSentence2 = ((DSLSentence) (m.lhs[1]));
        Assert.assertEquals("rabatt < {rabatt}", dslSentence2.getDrl());
        Assert.assertEquals(dslDefinition2, dslSentence2.getDefinition());
        Assert.assertEquals(1, dslSentence2.getValues().size());
        Assert.assertNotNull(dslSentence2.getValues().get(0));
        DSLVariableValue dslComplexVariableValue = dslSentence2.getValues().get(0);
        Assert.assertEquals("123", dslComplexVariableValue.getValue());
    }

    @Test
    public void testDSL() {
        String drl = "package org.mortgages;\n" + ((((("rule \"testdsl\"\n" + "  dialect \"mvel\"\n") + "  when\n") + "    There is a test rated applicant older than 111 years\n") + "  then\n") + "end");
        String dslDefinition = "There is a {rating} rated applicant older than {age} years";
        String dslFile = ("[when]" + dslDefinition) + "= Applicant( creditRating == \"{rating}\", age > {age} )";
        Mockito.when(dmo.getPackageName()).thenReturn("org.mortgages");
        final RuleModel model = RuleModelDRLPersistenceImpl.getInstance().unmarshalUsingDSL(drl, Collections.emptyList(), dmo, dslFile);
        Assert.assertEquals(1, model.lhs.length);
        DSLSentence dslSentence = ((DSLSentence) (model.lhs[0]));
        Assert.assertEquals("Applicant( creditRating == \"{rating}\", age > {age} )", dslSentence.getDrl());
        Assert.assertEquals("test", getValue());
        Assert.assertEquals("111", getValue());
    }

    @Test
    public void testDSLDollar() {
        String drl = "package org.mortgages;\n" + ((((("rule \"testdsl\"\n" + "  dialect \"mvel\"\n") + "  when\n") + "    Price is $111\n") + "  then\n") + "end");
        String dslDefinition = "Price is ${p}";
        String dslFile = ("[when]" + dslDefinition) + "= Item( price == \"{p}\" )";
        Mockito.when(dmo.getPackageName()).thenReturn("org.mortgages");
        final RuleModel model = RuleModelDRLPersistenceImpl.getInstance().unmarshalUsingDSL(drl, Collections.emptyList(), dmo, dslFile);
        Assert.assertEquals(1, model.lhs.length);
        DSLSentence dslSentence = ((DSLSentence) (model.lhs[0]));
        Assert.assertEquals("Price is ${p}", dslSentence.getDefinition());
        Assert.assertEquals("111", getValue());
    }

    @Test
    public void testDSLExpansionLHS() {
        String drl = "rule \"rule1\"\n" + ((("when\n" + "The credit rating is AA\n") + "then\n") + "end\n");
        final String dslDefinition = "The credit rating is {rating:ENUM:Applicant.creditRating}";
        final String dslFile = ("[when]" + dslDefinition) + "=Applicant( creditRating == {rating} )";
        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshalUsingDSL(drl, Collections.emptyList(), dmo, dslFile);
        Assert.assertNotNull(m);
        Assert.assertTrue(((m.lhs[0]) instanceof DSLSentence));
        DSLSentence dslSentence = ((DSLSentence) (m.lhs[0]));
        Assert.assertEquals(dslDefinition, dslSentence.getDefinition());
        Assert.assertEquals(1, dslSentence.getValues().size());
        Assert.assertTrue(((dslSentence.getValues().get(0)) instanceof DSLComplexVariableValue));
        DSLComplexVariableValue dslComplexVariableValue = ((DSLComplexVariableValue) (dslSentence.getValues().get(0)));
        Assert.assertEquals("AA", dslComplexVariableValue.getValue());
        Assert.assertEquals("ENUM:Applicant.creditRating", dslComplexVariableValue.getId());
    }

    @Test
    public void testDSLExpansionLHS2() {
        String drl = "rule \"rule1\"\n" + ((((("dialect \"mvel\"\n" + "when\n") + "There is an Applicant\n") + "- credit rating is AA\n") + "then\n") + "end\n");
        final String dslDefinition1 = "There is an Applicant";
        final String dslFile1 = ("[when]" + dslDefinition1) + "=Applicant( )";
        final String dslDefinition2 = "- credit rating is {rating:ENUM:Applicant.creditRating}";
        final String dslFile2 = ("[when]" + dslDefinition2) + "=creditRating == {rating}";
        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshalUsingDSL(drl, Collections.emptyList(), dmo, dslFile1, dslFile2);
        Assert.assertNotNull(m);
        Assert.assertEquals(2, m.lhs.length);
        Assert.assertTrue(((m.lhs[0]) instanceof DSLSentence));
        Assert.assertTrue(((m.lhs[1]) instanceof DSLSentence));
        DSLSentence dslSentence1 = ((DSLSentence) (m.lhs[0]));
        Assert.assertEquals(dslDefinition1, dslSentence1.getDefinition());
        Assert.assertEquals(0, dslSentence1.getValues().size());
        DSLSentence dslSentence2 = ((DSLSentence) (m.lhs[1]));
        Assert.assertEquals(dslDefinition2, dslSentence2.getDefinition());
        Assert.assertEquals(1, dslSentence2.getValues().size());
        Assert.assertTrue(((dslSentence2.getValues().get(0)) instanceof DSLComplexVariableValue));
        DSLComplexVariableValue dslComplexVariableValue = ((DSLComplexVariableValue) (dslSentence2.getValues().get(0)));
        Assert.assertEquals("AA", dslComplexVariableValue.getValue());
        Assert.assertEquals("ENUM:Applicant.creditRating", dslComplexVariableValue.getId());
        assertEqualsIgnoreWhitespace(drl, RuleModelDRLPersistenceImpl.getInstance().marshal(m));
    }

    // https://bugzilla.redhat.com/show_bug.cgi?id=1173842
    @Test
    public void testDSLExpansionLHS_WithKeyword_then() {
        String expected_dslr = "rule \"rule1\"\n" + ((((("dialect \"mvel\"\n" + "when\n") + "There is an Applicant\n") + "- age more then 55\n") + "then\n") + "end\n");
        String expected_drl = "rule \"rule1\"\n" + (((("dialect \"mvel\"\n" + "when\n") + "Applicant( age > 55 )\n") + "then\n") + "end\n");
        final String dslDefinition1 = "There is an Applicant";
        final String dslFile1 = ("[when]" + dslDefinition1) + "=Applicant( )";
        final String dslDefinition2 = "- age more then {age}";
        final String dslFile2 = ("[when]" + dslDefinition2) + "=age > {age}";
        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshalUsingDSL(expected_dslr, Collections.emptyList(), dmo, dslFile1, dslFile2);
        Assert.assertNotNull(m);
        Assert.assertEquals(2, m.lhs.length);
        Assert.assertTrue(((m.lhs[0]) instanceof DSLSentence));
        Assert.assertTrue(((m.lhs[1]) instanceof DSLSentence));
        DSLSentence dslSentence1 = ((DSLSentence) (m.lhs[0]));
        Assert.assertEquals(dslDefinition1, dslSentence1.getDefinition());
        Assert.assertEquals(0, dslSentence1.getValues().size());
        DSLSentence dslSentence2 = ((DSLSentence) (m.lhs[1]));
        Assert.assertEquals(dslDefinition2, dslSentence2.getDefinition());
        Assert.assertEquals(1, dslSentence2.getValues().size());
        DSLVariableValue dslVariableValue = dslSentence2.getValues().get(0);
        Assert.assertEquals("55", dslVariableValue.getValue());
        // Check round-trip
        assertEqualsIgnoreWhitespace(expected_dslr, RuleModelDRLPersistenceImpl.getInstance().marshal(m));
        // Check DSL expansion (as BZ stated runtime was flawed as well)
        final Expander expander = new DefaultExpander();
        final List<DSLMappingFile> dsls = new ArrayList<>();
        try {
            final DSLTokenizedMappingFile dslTokenizer1 = new DSLTokenizedMappingFile();
            if (dslTokenizer1.parseAndLoad(new StringReader(dslFile1))) {
                dsls.add(dslTokenizer1);
            } else {
                Assert.fail();
            }
            final DSLTokenizedMappingFile dslTokenizer2 = new DSLTokenizedMappingFile();
            if (dslTokenizer2.parseAndLoad(new StringReader(dslFile2))) {
                dsls.add(dslTokenizer2);
            } else {
                Assert.fail();
            }
        } catch (IOException e) {
            Assert.fail();
        }
        for (DSLMappingFile dsl : dsls) {
            expander.addDSLMapping(dsl.getMapping());
        }
        final String actual_drl = expander.expand(expected_dslr);
        assertEqualsIgnoreWhitespace(expected_drl, actual_drl);
    }

    @Test
    public void testDSLExpansionRHS() {
        String drl = "rule \"rule1\"\n" + (((("when\n" + "> $a : Applicant()\n") + "then\n") + "Set applicant name to Bob\n") + "end\n");
        final String dslDefinition = "Set applicant name to {name:\\w+ \\w+}";
        final String dslFile = ("[then]" + dslDefinition) + "=$a.setName( \"{name}\" )";
        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshalUsingDSL(drl, Collections.emptyList(), dmo, dslFile);
        Assert.assertNotNull(m);
        Assert.assertTrue(((m.lhs[0]) instanceof FactPattern));
        FactPattern pattern = ((FactPattern) (m.lhs[0]));
        Assert.assertEquals("Applicant", pattern.getFactType());
        Assert.assertEquals("$a", pattern.getBoundName());
        Assert.assertTrue(((m.rhs[0]) instanceof DSLSentence));
        DSLSentence dslSentence = ((DSLSentence) (m.rhs[0]));
        Assert.assertEquals(dslDefinition, dslSentence.getDefinition());
        Assert.assertEquals(1, dslSentence.getValues().size());
        Assert.assertTrue(((dslSentence.getValues().get(0)) instanceof DSLComplexVariableValue));
        DSLComplexVariableValue dslComplexVariableValue = ((DSLComplexVariableValue) (dslSentence.getValues().get(0)));
        Assert.assertEquals("Bob", dslComplexVariableValue.getValue());
        Assert.assertEquals("\\w+ \\w+", dslComplexVariableValue.getId());
    }

    /* https://issues.jboss.org/browse/GUVNOR-3451 */
    @Test
    public void testDSLWithMultilineBrackets() {
        String drl = "rule \"rule1\"\n" + (((("when\n" + "> Applicant(\n") + "> )\n") + "then\n") + "end\n");
        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshalUsingDSL(drl, Collections.emptyList(), dmo, "");
        Assert.assertNotNull(m);
        Assert.assertTrue(((m.lhs[0]) instanceof FactPattern));
        FactPattern pattern = ((FactPattern) (m.lhs[0]));
        Assert.assertEquals("Applicant", pattern.getFactType());
    }

    @Test
    public void testDSLWithMultilineBracketsOneField() {
        String drl = "rule \"rule1\"\n" + (((("when\n" + "> Applicant( name == \n") + "> \"John\")\n") + "then\n") + "end\n");
        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshalUsingDSL(drl, Collections.emptyList(), dmo, "");
        Assert.assertNotNull(m);
        Assert.assertTrue(((m.lhs[0]) instanceof FactPattern));
        FactPattern pattern = ((FactPattern) (m.lhs[0]));
        Assert.assertEquals("Applicant", pattern.getFactType());
        Assert.assertEquals(1, pattern.getNumberOfConstraints());
        Assert.assertEquals("name", getFieldName());
        Assert.assertEquals("==", getOperator());
        Assert.assertEquals("John", getValue());
    }

    @Test
    public void testDSLWithMultilineBracketsTwoFields() {
        String drl = "rule \"rule1\"\n" + (((((("when\n" + "> Applicant( name == \n") + "> \"John\"\n") + "> , \n") + "> age > 18 )\n") + "then\n") + "end\n");
        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshalUsingDSL(drl, Collections.emptyList(), dmo, "");
        Assert.assertNotNull(m);
        Assert.assertTrue(((m.lhs[0]) instanceof FactPattern));
        FactPattern pattern = ((FactPattern) (m.lhs[0]));
        Assert.assertEquals("Applicant", pattern.getFactType());
        Assert.assertEquals(2, pattern.getNumberOfConstraints());
        Assert.assertEquals("name", getFieldName());
        Assert.assertEquals("==", getOperator());
        Assert.assertEquals("John", getValue());
        Assert.assertEquals("age", getFieldName());
        Assert.assertEquals(">", getOperator());
        Assert.assertEquals("18", getValue());
    }

    @Test
    public void testFunctionCalls() {
        String drl = "package org.mortgages;\n" + ((((((((("import java.lang.Number;\n" + "import java.lang.String;\n") + "rule \"rule1\"\n") + "dialect \"mvel\"\n") + "when\n") + "  s : String()\n") + "then\n") + "  s.indexOf( s );\n") + "  s.indexOf( 0 );\n") + "end\n");
        Map<String, List<MethodInfo>> methodInformation = new HashMap<>();
        List<MethodInfo> mapMethodInformation = new ArrayList<>();
        mapMethodInformation.add(new MethodInfo("indexOf", Collections.singletonList(TYPE_STRING), "int", null, DataType.TYPE_STRING));
        mapMethodInformation.add(new MethodInfo("indexOf", Collections.singletonList(TYPE_NUMERIC_INTEGER), "int", null, DataType.TYPE_STRING));
        methodInformation.put("java.lang.String", mapMethodInformation);
        Mockito.when(dmo.getModuleMethodInformation()).thenReturn(methodInformation);
        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals(2, m.rhs.length);
        Assert.assertTrue(((m.rhs[0]) instanceof ActionCallMethod));
        Assert.assertTrue(((m.rhs[1]) instanceof ActionCallMethod));
        ActionCallMethod actionCallMethod1 = ((ActionCallMethod) (m.rhs[0]));
        Assert.assertEquals(1, actionCallMethod1.getState());
        Assert.assertEquals("indexOf", actionCallMethod1.getMethodName());
        Assert.assertEquals("s", actionCallMethod1.getVariable());
        Assert.assertEquals(1, actionCallMethod1.getFieldValues().length);
        Assert.assertEquals("indexOf", actionCallMethod1.getFieldValues()[0].getField());
        Assert.assertEquals("s", getValue());
        Assert.assertEquals(FieldNatureType.TYPE_VARIABLE, actionCallMethod1.getFieldValues()[0].getNature());
        Assert.assertEquals("String", actionCallMethod1.getFieldValues()[0].getType());
        ActionCallMethod actionCallMethod2 = ((ActionCallMethod) (m.rhs[1]));
        Assert.assertEquals(1, actionCallMethod2.getState());
        Assert.assertEquals("indexOf", actionCallMethod2.getMethodName());
        Assert.assertEquals("s", actionCallMethod2.getVariable());
        Assert.assertEquals(1, actionCallMethod2.getFieldValues().length);
        Assert.assertEquals("indexOf", actionCallMethod2.getFieldValues()[0].getField());
        Assert.assertEquals("0", getValue());
        Assert.assertEquals(FieldNatureType.TYPE_LITERAL, actionCallMethod2.getFieldValues()[0].getNature());
        Assert.assertEquals(TYPE_NUMERIC_INTEGER, actionCallMethod2.getFieldValues()[0].getType());
        assertEqualsIgnoreWhitespace(drl, RuleModelDRLPersistenceImpl.getInstance().marshal(m));
    }

    @Test
    public void testFunctionCalls2() {
        String drl = "package org.mortgages;\n" + (((((((("import java.lang.Number;\n" + "import java.lang.String;\n") + "rule \"rule1\"\n") + "dialect \"mvel\"\n") + "when\n") + "  s : String()\n") + "then\n") + "  s.indexOf( 0 );\n") + "end\n");
        Map<String, List<MethodInfo>> methodInformation = new HashMap<>();
        List<MethodInfo> mapMethodInformation = new ArrayList<>();
        mapMethodInformation.add(new MethodInfo("indexOf", Collections.singletonList(TYPE_STRING), "int", null, DataType.TYPE_STRING));
        mapMethodInformation.add(new MethodInfo("indexOf", Collections.singletonList(TYPE_NUMERIC_INTEGER), "int", null, DataType.TYPE_STRING));
        methodInformation.put("java.lang.String", mapMethodInformation);
        Mockito.when(dmo.getModuleMethodInformation()).thenReturn(methodInformation);
        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals(1, m.rhs.length);
        Assert.assertTrue(((m.rhs[0]) instanceof ActionCallMethod));
        ActionCallMethod actionCallMethod1 = ((ActionCallMethod) (m.rhs[0]));
        Assert.assertEquals(1, actionCallMethod1.getState());
        Assert.assertEquals("indexOf", actionCallMethod1.getMethodName());
        Assert.assertEquals("s", actionCallMethod1.getVariable());
        Assert.assertEquals(1, actionCallMethod1.getFieldValues().length);
        Assert.assertEquals("indexOf", actionCallMethod1.getFieldValues()[0].getField());
        Assert.assertEquals("0", getValue());
        Assert.assertEquals(FieldNatureType.TYPE_LITERAL, actionCallMethod1.getFieldValues()[0].getNature());
        Assert.assertEquals(TYPE_NUMERIC_INTEGER, actionCallMethod1.getFieldValues()[0].getType());
        assertEqualsIgnoreWhitespace(drl, RuleModelDRLPersistenceImpl.getInstance().marshal(m));
    }

    @Test
    public void testFunctionCalls3() {
        String drl = "package org.mortgages;\n" + (((((((((("import java.lang.Number;\n" + "import java.lang.String;\n") + "rule \"rule1\"\n") + "dialect \"mvel\"\n") + "when\n") + "  $var : String()\n") + "then\n") + "  $var.indexOf( $var );\n") + "  $var.endsWith( \".\" );\n") + "  $var.substring( 0, 1 );\n") + "end\n");
        Map<String, List<MethodInfo>> methodInformation = new HashMap<>();
        List<MethodInfo> mapMethodInformation = new ArrayList<>();
        mapMethodInformation.add(new MethodInfo("indexOf", Collections.singletonList(TYPE_STRING), "int", null, DataType.TYPE_STRING));
        mapMethodInformation.add(new MethodInfo("indexOf", Collections.singletonList(TYPE_NUMERIC_INTEGER), "int", null, DataType.TYPE_STRING));
        mapMethodInformation.add(new MethodInfo("endsWith", Collections.singletonList(TYPE_STRING), "boolean", null, DataType.TYPE_BOOLEAN));
        mapMethodInformation.add(new MethodInfo("substring", Collections.singletonList(TYPE_NUMERIC_INTEGER), "String", null, DataType.TYPE_STRING));
        mapMethodInformation.add(new MethodInfo("substring", Arrays.asList(TYPE_NUMERIC_INTEGER, TYPE_NUMERIC_INTEGER), "String", null, DataType.TYPE_STRING));
        methodInformation.put("java.lang.String", mapMethodInformation);
        Mockito.when(dmo.getModuleMethodInformation()).thenReturn(methodInformation);
        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals(3, m.rhs.length);
        Assert.assertTrue(((m.rhs[0]) instanceof ActionCallMethod));
        Assert.assertTrue(((m.rhs[1]) instanceof ActionCallMethod));
        Assert.assertTrue(((m.rhs[2]) instanceof ActionCallMethod));
        ActionCallMethod actionCallMethod1 = ((ActionCallMethod) (m.rhs[0]));
        Assert.assertEquals(1, actionCallMethod1.getState());
        Assert.assertEquals("indexOf", actionCallMethod1.getMethodName());
        Assert.assertEquals("$var", actionCallMethod1.getVariable());
        Assert.assertEquals(1, actionCallMethod1.getFieldValues().length);
        Assert.assertEquals("indexOf", actionCallMethod1.getFieldValues()[0].getField());
        Assert.assertEquals("$var", getValue());
        Assert.assertEquals(FieldNatureType.TYPE_VARIABLE, actionCallMethod1.getFieldValues()[0].getNature());
        Assert.assertEquals(TYPE_STRING, actionCallMethod1.getFieldValues()[0].getType());
        ActionCallMethod actionCallMethod2 = ((ActionCallMethod) (m.rhs[1]));
        Assert.assertEquals(1, actionCallMethod2.getState());
        Assert.assertEquals("endsWith", actionCallMethod2.getMethodName());
        Assert.assertEquals("$var", actionCallMethod2.getVariable());
        Assert.assertEquals(1, actionCallMethod2.getFieldValues().length);
        Assert.assertEquals("endsWith", actionCallMethod2.getFieldValues()[0].getField());
        Assert.assertEquals(".", getValue());
        Assert.assertEquals(FieldNatureType.TYPE_LITERAL, actionCallMethod2.getFieldValues()[0].getNature());
        Assert.assertEquals(TYPE_STRING, actionCallMethod2.getFieldValues()[0].getType());
        ActionCallMethod actionCallMethod3 = ((ActionCallMethod) (m.rhs[2]));
        Assert.assertEquals(1, actionCallMethod3.getState());
        Assert.assertEquals("substring", actionCallMethod3.getMethodName());
        Assert.assertEquals("$var", actionCallMethod3.getVariable());
        Assert.assertEquals(2, actionCallMethod3.getFieldValues().length);
        Assert.assertEquals("substring", actionCallMethod3.getFieldValues()[0].getField());
        Assert.assertEquals("0", getValue());
        Assert.assertEquals(FieldNatureType.TYPE_LITERAL, actionCallMethod3.getFieldValues()[0].getNature());
        Assert.assertEquals(TYPE_NUMERIC_INTEGER, actionCallMethod3.getFieldValues()[0].getType());
        Assert.assertEquals("substring", actionCallMethod3.getFieldValues()[1].getField());
        Assert.assertEquals("1", getValue());
        Assert.assertEquals(FieldNatureType.TYPE_LITERAL, actionCallMethod3.getFieldValues()[1].getNature());
        Assert.assertEquals(TYPE_NUMERIC_INTEGER, actionCallMethod3.getFieldValues()[1].getType());
        assertEqualsIgnoreWhitespace(drl, RuleModelDRLPersistenceImpl.getInstance().marshal(m));
    }

    @Test
    public void testFunctionCalls4_MultiParameterSetter() {
        String drl = "package org.mortgages;\n" + ((((((("import org.mortgages.classes.MyClass;\n" + "rule \"rule1\"\n") + "dialect \"mvel\"\n") + "when\n") + "  $c : MyClass()\n") + "then\n") + "  $c.setSomething(0, 1);\n") + "end\n");
        Map<String, List<MethodInfo>> methodInformation = new HashMap<>();
        List<MethodInfo> mapMethodInformation = new ArrayList<>();
        mapMethodInformation.add(new MethodInfo("setSomething", Arrays.asList(TYPE_NUMERIC_INTEGER, TYPE_NUMERIC_INTEGER), "void", null, DataType.TYPE_STRING));
        methodInformation.put("org.mortgages.classes.MyClass", mapMethodInformation);
        Mockito.when(dmo.getModuleMethodInformation()).thenReturn(methodInformation);
        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals(1, m.rhs.length);
        Assert.assertTrue(((m.rhs[0]) instanceof ActionCallMethod));
        ActionCallMethod actionCallMethod1 = ((ActionCallMethod) (m.rhs[0]));
        Assert.assertEquals(1, actionCallMethod1.getState());
        Assert.assertEquals("setSomething", actionCallMethod1.getMethodName());
        Assert.assertEquals("$c", actionCallMethod1.getVariable());
        Assert.assertEquals(2, actionCallMethod1.getFieldValues().length);
        Assert.assertEquals("setSomething", actionCallMethod1.getFieldValues()[0].getField());
        Assert.assertEquals("0", getValue());
        Assert.assertEquals(FieldNatureType.TYPE_LITERAL, actionCallMethod1.getFieldValues()[0].getNature());
        Assert.assertEquals(TYPE_NUMERIC_INTEGER, actionCallMethod1.getFieldValues()[0].getType());
        Assert.assertEquals("setSomething", actionCallMethod1.getFieldValues()[1].getField());
        Assert.assertEquals("1", getValue());
        Assert.assertEquals(FieldNatureType.TYPE_LITERAL, actionCallMethod1.getFieldValues()[1].getNature());
        Assert.assertEquals(TYPE_NUMERIC_INTEGER, actionCallMethod1.getFieldValues()[1].getType());
        assertEqualsIgnoreWhitespace(drl, RuleModelDRLPersistenceImpl.getInstance().marshal(m));
    }

    @Test
    public void testRHSInsertFactWithFieldAsVariable() {
        // https://bugzilla.redhat.com/show_bug.cgi?id=1077212
        String drl = "package org.mortgages\n" + ((((((((("import org.test.Person\n" + "rule \"variable\"\n") + "dialect \"mvel\"\n") + "when\n") + "Person( $f : field1 == 44 )\n") + "then\n") + "Person fact0 = new Person();\n") + "fact0.setField1( $f );\n") + "insert( fact0 );\n") + "end");
        addModelField("org.test.Person", "field1", "java.lang.Integer", TYPE_NUMERIC_INTEGER);
        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals(1, m.lhs.length);
        IPattern p = m.lhs[0];
        Assert.assertTrue((p instanceof FactPattern));
        FactPattern fp = ((FactPattern) (p));
        Assert.assertEquals("Person", fp.getFactType());
        Assert.assertEquals(1, fp.getConstraintList().getConstraints().length);
        Assert.assertTrue(((fp.getConstraint(0)) instanceof SingleFieldConstraint));
        SingleFieldConstraint sfp = ((SingleFieldConstraint) (fp.getConstraint(0)));
        Assert.assertEquals("Person", sfp.getFactType());
        Assert.assertEquals("field1", sfp.getFieldName());
        Assert.assertEquals("==", sfp.getOperator());
        Assert.assertEquals("44", sfp.getValue());
        Assert.assertEquals(TYPE_LITERAL, sfp.getConstraintValueType());
        Assert.assertEquals("$f", sfp.getFieldBinding());
        Assert.assertEquals(1, m.rhs.length);
        IAction a = m.rhs[0];
        Assert.assertTrue((a instanceof ActionInsertFact));
        ActionInsertFact ap = ((ActionInsertFact) (a));
        Assert.assertEquals("Person", ap.getFactType());
        Assert.assertEquals("fact0", ap.getBoundName());
        Assert.assertEquals(1, ap.getFieldValues().length);
        ActionFieldValue afv = ap.getFieldValues()[0];
        Assert.assertEquals("field1", afv.getField());
        Assert.assertEquals(FieldNatureType.TYPE_VARIABLE, afv.getNature());
        Assert.assertEquals("=$f", afv.getValue());
        Assert.assertEquals(TYPE_NUMERIC_INTEGER, afv.getType());
    }

    @Test
    public void testRHSInsertFactWithFieldAsLiteral() {
        String drl = "package org.mortgages\n" + ((((((((("import org.test.Person\n" + "rule \"variable\"\n") + "dialect \"mvel\"\n") + "when\n") + "Person( field1 == 44 )\n") + "then\n") + "Person fact0 = new Person();\n") + "fact0.setField1( 55 );\n") + "insert( fact0 );\n") + "end");
        addModelField("org.test.Person", "field1", "java.lang.Integer", TYPE_NUMERIC_INTEGER);
        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals(1, m.lhs.length);
        IPattern p = m.lhs[0];
        Assert.assertTrue((p instanceof FactPattern));
        FactPattern fp = ((FactPattern) (p));
        Assert.assertEquals("Person", fp.getFactType());
        Assert.assertEquals(1, fp.getConstraintList().getConstraints().length);
        Assert.assertTrue(((fp.getConstraint(0)) instanceof SingleFieldConstraint));
        SingleFieldConstraint sfp = ((SingleFieldConstraint) (fp.getConstraint(0)));
        Assert.assertEquals("Person", sfp.getFactType());
        Assert.assertEquals("field1", sfp.getFieldName());
        Assert.assertEquals("==", sfp.getOperator());
        Assert.assertEquals("44", sfp.getValue());
        Assert.assertEquals(TYPE_LITERAL, sfp.getConstraintValueType());
        Assert.assertEquals(1, m.rhs.length);
        IAction a = m.rhs[0];
        Assert.assertTrue((a instanceof ActionInsertFact));
        ActionInsertFact ap = ((ActionInsertFact) (a));
        Assert.assertEquals("Person", ap.getFactType());
        Assert.assertEquals("fact0", ap.getBoundName());
        Assert.assertEquals(1, ap.getFieldValues().length);
        ActionFieldValue afv = ap.getFieldValues()[0];
        Assert.assertEquals("field1", afv.getField());
        Assert.assertEquals(FieldNatureType.TYPE_LITERAL, afv.getNature());
        Assert.assertEquals("55", afv.getValue());
        Assert.assertEquals(TYPE_NUMERIC_INTEGER, afv.getType());
    }

    @Test
    public void testRHSUpdateFactWithFieldAsVariable() {
        // https://bugzilla.redhat.com/show_bug.cgi?id=1077212
        String drl = "package org.mortgages\n" + (((((((("import org.test.Person\n" + "rule \"variable\"\n") + "dialect \"mvel\"\n") + "when\n") + "$p : Person( $f : field1 == 44 )\n") + "then\n") + "$p.setField1( $f );\n") + "update( $p );\n") + "end");
        addModelField("org.test.Person", "field1", "java.lang.Integer", TYPE_NUMERIC_INTEGER);
        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals(1, m.lhs.length);
        IPattern p = m.lhs[0];
        Assert.assertTrue((p instanceof FactPattern));
        FactPattern fp = ((FactPattern) (p));
        Assert.assertEquals("Person", fp.getFactType());
        Assert.assertEquals("$p", fp.getBoundName());
        Assert.assertEquals(1, fp.getConstraintList().getConstraints().length);
        Assert.assertTrue(((fp.getConstraint(0)) instanceof SingleFieldConstraint));
        SingleFieldConstraint sfp = ((SingleFieldConstraint) (fp.getConstraint(0)));
        Assert.assertEquals("Person", sfp.getFactType());
        Assert.assertEquals("field1", sfp.getFieldName());
        Assert.assertEquals("==", sfp.getOperator());
        Assert.assertEquals("44", sfp.getValue());
        Assert.assertEquals(TYPE_LITERAL, sfp.getConstraintValueType());
        Assert.assertEquals("$f", sfp.getFieldBinding());
        Assert.assertEquals(1, m.rhs.length);
        IAction a = m.rhs[0];
        Assert.assertTrue((a instanceof ActionUpdateField));
        ActionUpdateField ap = ((ActionUpdateField) (a));
        Assert.assertEquals("$p", ap.getVariable());
        Assert.assertEquals(1, ap.getFieldValues().length);
        ActionFieldValue afv = ap.getFieldValues()[0];
        Assert.assertEquals("field1", afv.getField());
        Assert.assertEquals(FieldNatureType.TYPE_VARIABLE, afv.getNature());
        Assert.assertEquals("=$f", afv.getValue());
        Assert.assertEquals(TYPE_NUMERIC_INTEGER, afv.getType());
    }

    @Test
    public void testRHSUpdateFactWithFieldAsLiteral() {
        String drl = "package org.mortgages\n" + (((((((("import org.test.Person\n" + "rule \"variable\"\n") + "dialect \"mvel\"\n") + "when\n") + "$p : Person()\n") + "then\n") + "$p.setField1( 55 );\n") + "update( $p );\n") + "end");
        addModelField("org.test.Person", "field1", "java.lang.Integer", TYPE_NUMERIC_INTEGER);
        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals(1, m.lhs.length);
        IPattern p = m.lhs[0];
        Assert.assertTrue((p instanceof FactPattern));
        FactPattern fp = ((FactPattern) (p));
        Assert.assertEquals("Person", fp.getFactType());
        Assert.assertEquals(0, fp.getNumberOfConstraints());
        Assert.assertEquals(1, m.rhs.length);
        IAction a = m.rhs[0];
        Assert.assertTrue((a instanceof ActionUpdateField));
        ActionUpdateField ap = ((ActionUpdateField) (a));
        Assert.assertEquals("$p", ap.getVariable());
        Assert.assertEquals(1, ap.getFieldValues().length);
        ActionFieldValue afv = ap.getFieldValues()[0];
        Assert.assertEquals("field1", afv.getField());
        Assert.assertEquals(FieldNatureType.TYPE_LITERAL, afv.getNature());
        Assert.assertEquals("55", afv.getValue());
        Assert.assertEquals(TYPE_NUMERIC_INTEGER, afv.getType());
    }

    @Test
    public void testRHSInsertFactWithFieldAsVariableSamePackage() {
        // https://bugzilla.redhat.com/show_bug.cgi?id=1077212
        String drl = "package org.test\n" + (((((((("rule \"variable\"\n" + "dialect \"mvel\"\n") + "when\n") + "Person( $f : field1 == 44 )\n") + "then\n") + "Person fact0 = new Person();\n") + "fact0.setField1( $f );\n") + "insert( fact0 );\n") + "end");
        addModelField("org.test.Person", "field1", "java.lang.Integer", TYPE_NUMERIC_INTEGER);
        Mockito.when(dmo.getPackageName()).thenReturn("org.test");
        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals(1, m.lhs.length);
        IPattern p = m.lhs[0];
        Assert.assertTrue((p instanceof FactPattern));
        FactPattern fp = ((FactPattern) (p));
        Assert.assertEquals("Person", fp.getFactType());
        Assert.assertEquals(1, fp.getConstraintList().getConstraints().length);
        Assert.assertTrue(((fp.getConstraint(0)) instanceof SingleFieldConstraint));
        SingleFieldConstraint sfp = ((SingleFieldConstraint) (fp.getConstraint(0)));
        Assert.assertEquals("Person", sfp.getFactType());
        Assert.assertEquals("field1", sfp.getFieldName());
        Assert.assertEquals("==", sfp.getOperator());
        Assert.assertEquals("44", sfp.getValue());
        Assert.assertEquals(TYPE_LITERAL, sfp.getConstraintValueType());
        Assert.assertEquals("$f", sfp.getFieldBinding());
        Assert.assertEquals(1, m.rhs.length);
        IAction a = m.rhs[0];
        Assert.assertTrue((a instanceof ActionInsertFact));
        ActionInsertFact ap = ((ActionInsertFact) (a));
        Assert.assertEquals("Person", ap.getFactType());
        Assert.assertEquals("fact0", ap.getBoundName());
        Assert.assertEquals(1, ap.getFieldValues().length);
        ActionFieldValue afv = ap.getFieldValues()[0];
        Assert.assertEquals("field1", afv.getField());
        Assert.assertEquals(FieldNatureType.TYPE_VARIABLE, afv.getNature());
        Assert.assertEquals("=$f", afv.getValue());
        Assert.assertEquals(TYPE_NUMERIC_INTEGER, afv.getType());
    }

    @Test
    public void testRHSInsertFactWithFieldAsLiteralSamePackage() {
        String drl = "package org.test\n" + (((((((("rule \"variable\"\n" + "dialect \"mvel\"\n") + "when\n") + "Person( field1 == 44 )\n") + "then\n") + "Person fact0 = new Person();\n") + "fact0.setField1( 55 );\n") + "insert( fact0 );\n") + "end");
        addModelField("org.test.Person", "field1", "java.lang.Integer", TYPE_NUMERIC_INTEGER);
        Mockito.when(dmo.getPackageName()).thenReturn("org.test");
        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals(1, m.lhs.length);
        IPattern p = m.lhs[0];
        Assert.assertTrue((p instanceof FactPattern));
        FactPattern fp = ((FactPattern) (p));
        Assert.assertEquals("Person", fp.getFactType());
        Assert.assertEquals(1, fp.getConstraintList().getConstraints().length);
        Assert.assertTrue(((fp.getConstraint(0)) instanceof SingleFieldConstraint));
        SingleFieldConstraint sfp = ((SingleFieldConstraint) (fp.getConstraint(0)));
        Assert.assertEquals("Person", sfp.getFactType());
        Assert.assertEquals("field1", sfp.getFieldName());
        Assert.assertEquals("==", sfp.getOperator());
        Assert.assertEquals("44", sfp.getValue());
        Assert.assertEquals(TYPE_LITERAL, sfp.getConstraintValueType());
        Assert.assertEquals(1, m.rhs.length);
        IAction a = m.rhs[0];
        Assert.assertTrue((a instanceof ActionInsertFact));
        ActionInsertFact ap = ((ActionInsertFact) (a));
        Assert.assertEquals("Person", ap.getFactType());
        Assert.assertEquals("fact0", ap.getBoundName());
        Assert.assertEquals(1, ap.getFieldValues().length);
        ActionFieldValue afv = ap.getFieldValues()[0];
        Assert.assertEquals("field1", afv.getField());
        Assert.assertEquals(FieldNatureType.TYPE_LITERAL, afv.getNature());
        Assert.assertEquals("55", afv.getValue());
        Assert.assertEquals(TYPE_NUMERIC_INTEGER, afv.getType());
    }

    @Test
    public void testRHSUpdateFactWithFieldAsVariableSamePackage() {
        // https://bugzilla.redhat.com/show_bug.cgi?id=1077212
        String drl = "package org.test\n" + ((((((("rule \"variable\"\n" + "dialect \"mvel\"\n") + "when\n") + "$p : Person( $f : field1 == 44 )\n") + "then\n") + "$p.setField1( $f );\n") + "update( $p );\n") + "end");
        addModelField("org.test.Person", "field1", "java.lang.Integer", TYPE_NUMERIC_INTEGER);
        Mockito.when(dmo.getPackageName()).thenReturn("org.test");
        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals(1, m.lhs.length);
        IPattern p = m.lhs[0];
        Assert.assertTrue((p instanceof FactPattern));
        FactPattern fp = ((FactPattern) (p));
        Assert.assertEquals("Person", fp.getFactType());
        Assert.assertEquals("$p", fp.getBoundName());
        Assert.assertEquals(1, fp.getConstraintList().getConstraints().length);
        Assert.assertTrue(((fp.getConstraint(0)) instanceof SingleFieldConstraint));
        SingleFieldConstraint sfp = ((SingleFieldConstraint) (fp.getConstraint(0)));
        Assert.assertEquals("Person", sfp.getFactType());
        Assert.assertEquals("field1", sfp.getFieldName());
        Assert.assertEquals("==", sfp.getOperator());
        Assert.assertEquals("44", sfp.getValue());
        Assert.assertEquals(TYPE_LITERAL, sfp.getConstraintValueType());
        Assert.assertEquals("$f", sfp.getFieldBinding());
        Assert.assertEquals(1, m.rhs.length);
        IAction a = m.rhs[0];
        Assert.assertTrue((a instanceof ActionUpdateField));
        ActionUpdateField ap = ((ActionUpdateField) (a));
        Assert.assertEquals("$p", ap.getVariable());
        Assert.assertEquals(1, ap.getFieldValues().length);
        ActionFieldValue afv = ap.getFieldValues()[0];
        Assert.assertEquals("field1", afv.getField());
        Assert.assertEquals(FieldNatureType.TYPE_VARIABLE, afv.getNature());
        Assert.assertEquals("=$f", afv.getValue());
        Assert.assertEquals(TYPE_NUMERIC_INTEGER, afv.getType());
    }

    @Test
    public void testRHSUpdateFactWithFieldAsLiteralSamePackage() {
        String drl = "package org.test\n" + ((((((("rule \"variable\"\n" + "dialect \"mvel\"\n") + "when\n") + "$p : Person()\n") + "then\n") + "$p.setField1( 55 );\n") + "update( $p );\n") + "end");
        addModelField("org.test.Person", "field1", "java.lang.Integer", TYPE_NUMERIC_INTEGER);
        Mockito.when(dmo.getPackageName()).thenReturn("org.test");
        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals(1, m.lhs.length);
        IPattern p = m.lhs[0];
        Assert.assertTrue((p instanceof FactPattern));
        FactPattern fp = ((FactPattern) (p));
        Assert.assertEquals("Person", fp.getFactType());
        Assert.assertEquals(0, fp.getNumberOfConstraints());
        Assert.assertEquals(1, m.rhs.length);
        IAction a = m.rhs[0];
        Assert.assertTrue((a instanceof ActionUpdateField));
        ActionUpdateField ap = ((ActionUpdateField) (a));
        Assert.assertEquals("$p", ap.getVariable());
        Assert.assertEquals(1, ap.getFieldValues().length);
        ActionFieldValue afv = ap.getFieldValues()[0];
        Assert.assertEquals("field1", afv.getField());
        Assert.assertEquals(FieldNatureType.TYPE_LITERAL, afv.getNature());
        Assert.assertEquals("55", afv.getValue());
        Assert.assertEquals(TYPE_NUMERIC_INTEGER, afv.getType());
    }

    @Test
    public void testRHSUpdateFactWithFormula() {
        // https://bugzilla.redhat.com/show_bug.cgi?id=1079253
        String drl = "package org.mortgages;\n" + (((((((("import org.test.ShoppingCart\n" + "rule \"r1\"\n") + "dialect \"mvel\"\n") + "when\n") + "$sc : ShoppingCart( )\n") + "then\n") + "$sc.setCartItemPromoSavings( ($sc.cartItemPromoSavings == 0.0) ? 0.0 : $sc.cartItemPromoSavings * -1 );\n") + "update( $sc );\n") + "end\n");
        addModelField("org.test.ShoppingCart", "cartItemPromoSavings", "java.lang.Double", TYPE_NUMERIC_DOUBLE);
        Mockito.when(dmo.getPackageName()).thenReturn("org.test");
        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals(1, m.lhs.length);
        IPattern p = m.lhs[0];
        Assert.assertTrue((p instanceof FactPattern));
        FactPattern fp = ((FactPattern) (p));
        Assert.assertEquals("ShoppingCart", fp.getFactType());
        Assert.assertEquals("$sc", fp.getBoundName());
        Assert.assertEquals(0, fp.getNumberOfConstraints());
        Assert.assertEquals(1, m.rhs.length);
        IAction a = m.rhs[0];
        Assert.assertTrue((a instanceof ActionUpdateField));
        ActionUpdateField ap = ((ActionUpdateField) (a));
        Assert.assertEquals("$sc", ap.getVariable());
        Assert.assertEquals(1, ap.getFieldValues().length);
        ActionFieldValue afv = ap.getFieldValues()[0];
        Assert.assertEquals("cartItemPromoSavings", afv.getField());
        Assert.assertEquals("($sc.cartItemPromoSavings == 0.0) ? 0.0 : $sc.cartItemPromoSavings * -1", afv.getValue());
        Assert.assertEquals(TYPE_FORMULA, afv.getNature());
    }

    @Test
    public void testRHSUpdateFactWithFormulaSamePackage() {
        // https://bugzilla.redhat.com/show_bug.cgi?id=1079253
        String drl = "package org.test;\n" + ((((((("rule \"r1\"\n" + "dialect \"mvel\"\n") + "when\n") + "$sc : ShoppingCart( )\n") + "then\n") + "$sc.setCartItemPromoSavings( ($sc.cartItemPromoSavings == 0.0) ? 0.0 : $sc.cartItemPromoSavings * -1 );\n") + "update( $sc );\n") + "end\n");
        addModelField("org.test.ShoppingCart", "cartItemPromoSavings", "java.lang.Double", TYPE_NUMERIC_DOUBLE);
        Mockito.when(dmo.getPackageName()).thenReturn("org.test");
        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals(1, m.lhs.length);
        IPattern p = m.lhs[0];
        Assert.assertTrue((p instanceof FactPattern));
        FactPattern fp = ((FactPattern) (p));
        Assert.assertEquals("ShoppingCart", fp.getFactType());
        Assert.assertEquals("$sc", fp.getBoundName());
        Assert.assertEquals(0, fp.getNumberOfConstraints());
        Assert.assertEquals(1, m.rhs.length);
        IAction a = m.rhs[0];
        Assert.assertTrue((a instanceof ActionUpdateField));
        ActionUpdateField ap = ((ActionUpdateField) (a));
        Assert.assertEquals("$sc", ap.getVariable());
        Assert.assertEquals(1, ap.getFieldValues().length);
        ActionFieldValue afv = ap.getFieldValues()[0];
        Assert.assertEquals("cartItemPromoSavings", afv.getField());
        Assert.assertEquals("($sc.cartItemPromoSavings == 0.0) ? 0.0 : $sc.cartItemPromoSavings * -1", afv.getValue());
        Assert.assertEquals(TYPE_FORMULA, afv.getNature());
    }

    @Test
    public void testFunctionsWithVariableParameters() throws Exception {
        String drl = "package org.mortgages;\n" + ((((((("rule \"test\"\n" + " dialect \"mvel\"\n") + " when\n") + "  Calculator( s : summer)\n") + "  Applicant( $age : age)\n") + " then\n") + "  s.sum( $age, $age );\n") + "end");
        addModelField("Calculator", "summer", "Summer", "Summer");
        addModelField("Applicant", "age", "java.lang.Integer", TYPE_NUMERIC_INTEGER);
        HashMap<String, List<MethodInfo>> map = new HashMap<>();
        ArrayList<MethodInfo> methodInfos = new ArrayList<>();
        ArrayList<String> params = new ArrayList<>();
        params.add("Integer");
        params.add("Integer");
        methodInfos.add(new MethodInfo("sum", params, "java.lang.Integer", null, "Summer"));
        map.put("Calculator", methodInfos);
        Mockito.when(dmo.getModuleMethodInformation()).thenReturn(map);
        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals(2, m.lhs.length);
        Assert.assertEquals(1, m.rhs.length);
        ActionCallMethod actionCallMethod = ((ActionCallMethod) (m.rhs[0]));
        Assert.assertEquals("sum", actionCallMethod.getMethodName());
        Assert.assertEquals("s", actionCallMethod.getVariable());
        Assert.assertEquals(2, actionCallMethod.getFieldValues().length);
        Assert.assertEquals("sum", actionCallMethod.getFieldValue(0).getField());
        Assert.assertEquals("$age", getValue());
        Assert.assertEquals(2, actionCallMethod.getFieldValue(0).getNature());
        Assert.assertEquals("java.lang.Integer", actionCallMethod.getFieldValue(0).getType());
        Assert.assertEquals("sum", actionCallMethod.getFieldValue(1).getField());
        Assert.assertEquals("$age", getValue());
        Assert.assertEquals(2, actionCallMethod.getFieldValue(1).getNature());
        Assert.assertEquals("java.lang.Integer", actionCallMethod.getFieldValue(1).getType());
        assertEqualsIgnoreWhitespace(drl, RuleModelDRLPersistenceImpl.getInstance().marshal(m));
    }

    @Test
    public void testListSize() throws Exception {
        String drl = "" + ((((((("package org.mortgages;\n" + "import java.lang.Number;\n") + "rule \"Test\"\n") + "  dialect \"mvel\"\n") + "  when\n") + "    Person( addresses.size() == 0 )\n") + "  then\n") + "end\n");
        addModelField("Person", "addresses", "java.util.List", TYPE_COLLECTION);
        addMethodInformation("java.util.List", "size", Collections.emptyList(), "int", null, TYPE_NUMERIC_INTEGER);
        HashMap<String, String> map = new HashMap<>();
        map.put("Person#addresses", "Address");
        Mockito.when(dmo.getModuleFieldParametersType()).thenReturn(map);
        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertEquals(1, m.lhs.length);
        FactPattern pattern = ((FactPattern) (m.lhs[0]));
        Assert.assertEquals(1, pattern.getConstraintList().getConstraints().length);
        SingleFieldConstraintEBLeftSide constraint = ((SingleFieldConstraintEBLeftSide) (pattern.getConstraint(0)));
        Assert.assertEquals("size", constraint.getFieldName());
        Assert.assertEquals("int", constraint.getFieldType());
        Assert.assertEquals("0", constraint.getValue());
        Assert.assertEquals("==", constraint.getOperator());
        Assert.assertEquals(1, constraint.getConstraintValueType());
        Assert.assertTrue(((constraint.getExpressionLeftSide().getParts().get(0)) instanceof ExpressionUnboundFact));
        Assert.assertTrue(((constraint.getExpressionLeftSide().getParts().get(1)) instanceof ExpressionCollection));
        Assert.assertTrue(((constraint.getExpressionLeftSide().getParts().get(2)) instanceof ExpressionMethod));
        assertEqualsIgnoreWhitespace(drl, RuleModelDRLPersistenceImpl.getInstance().marshal(m));
    }

    @Test
    public void testCollectWithFreeFormDRL_MethodsDefined() throws Exception {
        // https://bugzilla.redhat.com/show_bug.cgi?id=1060816
        String drl = "package org.sample.resourceassignment;\n" + (((((("rule \"r1\"\n" + "dialect \"mvel\"\n") + "when\n") + "$trans : Transactions()\n") + "$transactions : java.util.List( eval( size > 0 ) ) from collect ( Transaction() from $trans.getRecCategorization().get(\"APES-01\") )\n") + "then\n") + "end");
        addModelField("Transactions", "recCategorization", "java.util.Map", TYPE_COLLECTION);
        addMethodInformation("Transactions", "getRecCategorization()", Collections.emptyList(), "java.util.Map", null, TYPE_COLLECTION);
        addMethodInformation("java.util.Map", "get", new ArrayList<String>() {
            {
                add("p0");
            }
        }, "java.lang.String", null, TYPE_STRING);
        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals(2, m.lhs.length);
        IPattern p0 = m.lhs[0];
        Assert.assertTrue((p0 instanceof FactPattern));
        FactPattern fp0 = ((FactPattern) (p0));
        Assert.assertEquals("Transactions", fp0.getFactType());
        Assert.assertEquals("$trans", fp0.getBoundName());
        Assert.assertEquals(0, fp0.getNumberOfConstraints());
        IPattern p1 = m.lhs[1];
        Assert.assertTrue((p1 instanceof FromCollectCompositeFactPattern));
        FromCollectCompositeFactPattern fp1 = ((FromCollectCompositeFactPattern) (p1));
        Assert.assertEquals("java.util.List", getFactType());
        Assert.assertEquals("$transactions", fp1.getFactPattern().getBoundName());
        Assert.assertEquals(1, fp1.getFactPattern().getNumberOfConstraints());
        Assert.assertTrue(((getConstraint(0)) instanceof SingleFieldConstraint));
        SingleFieldConstraint fp1sfc = ((SingleFieldConstraint) (getConstraint(0)));
        Assert.assertEquals("size > 0", fp1sfc.getValue());
        Assert.assertEquals(TYPE_PREDICATE, fp1sfc.getConstraintValueType());
        Assert.assertTrue(((fp1.getRightPattern()) instanceof FromCompositeFactPattern));
        FromCompositeFactPattern fp2 = ((FromCompositeFactPattern) (fp1.getRightPattern()));
        Assert.assertNotNull(fp2.getFactPattern());
        FactPattern fp3 = fp2.getFactPattern();
        Assert.assertEquals("Transaction", fp3.getFactType());
        Assert.assertEquals(0, fp3.getNumberOfConstraints());
        Assert.assertNotNull(fp2.getExpression());
        ExpressionFormLine efl = fp2.getExpression();
        Assert.assertEquals(3, efl.getParts().size());
        Assert.assertTrue(((efl.getParts().get(0)) instanceof ExpressionVariable));
        ExpressionVariable ev = ((ExpressionVariable) (efl.getParts().get(0)));
        Assert.assertEquals("$trans", ev.getName());
        Assert.assertEquals("Transactions", ev.getClassType());
        Assert.assertTrue(((efl.getParts().get(1)) instanceof ExpressionMethod));
        ExpressionMethod em = ((ExpressionMethod) (efl.getParts().get(1)));
        Assert.assertEquals("getRecCategorization()", em.getName());
        Assert.assertEquals("java.util.Map", em.getClassType());
        Assert.assertEquals(TYPE_COLLECTION, em.getGenericType());
        Assert.assertTrue(((efl.getParts().get(2)) instanceof ExpressionText));
        ExpressionText et = ((ExpressionText) (efl.getParts().get(2)));
        Assert.assertEquals("get(\"APES-01\")", et.getName());
        Assert.assertEquals("java.lang.String", et.getClassType());
        Assert.assertEquals(TYPE_STRING, et.getGenericType());
        Assert.assertEquals(0, m.rhs.length);
    }

    @Test
    public void testCollectWithFreeFormDRL_MethodsUndefined() throws Exception {
        // https://bugzilla.redhat.com/show_bug.cgi?id=1060816
        String drl = "package org.sample.resourceassignment;\n" + (((((("rule \"r1\"\n" + "dialect \"mvel\"\n") + "when\n") + "$trans : Transactions()\n") + "$transactions : java.util.List( eval( size > 0 ) ) from collect ( Transaction() from $trans.getRecCategorization().get(\"APES-01\") )\n") + "then\n") + "end");
        addModelField("Transactions", "recCategorization", "java.util.Map", TYPE_COLLECTION);
        addMethodInformation("java.util.Map", "get", new ArrayList<String>() {
            {
                add("p0");
            }
        }, "java.lang.String", null, TYPE_STRING);
        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals(2, m.lhs.length);
        IPattern p0 = m.lhs[0];
        Assert.assertTrue((p0 instanceof FactPattern));
        FactPattern fp0 = ((FactPattern) (p0));
        Assert.assertEquals("Transactions", fp0.getFactType());
        Assert.assertEquals("$trans", fp0.getBoundName());
        Assert.assertEquals(0, fp0.getNumberOfConstraints());
        IPattern p1 = m.lhs[1];
        Assert.assertTrue((p1 instanceof FromCollectCompositeFactPattern));
        FromCollectCompositeFactPattern fp1 = ((FromCollectCompositeFactPattern) (p1));
        Assert.assertEquals("java.util.List", getFactType());
        Assert.assertEquals("$transactions", fp1.getFactPattern().getBoundName());
        Assert.assertEquals(1, fp1.getFactPattern().getNumberOfConstraints());
        Assert.assertTrue(((getConstraint(0)) instanceof SingleFieldConstraint));
        SingleFieldConstraint fp1sfc = ((SingleFieldConstraint) (getConstraint(0)));
        Assert.assertEquals("size > 0", fp1sfc.getValue());
        Assert.assertEquals(TYPE_PREDICATE, fp1sfc.getConstraintValueType());
        Assert.assertTrue(((fp1.getRightPattern()) instanceof FromCompositeFactPattern));
        FromCompositeFactPattern fp2 = ((FromCompositeFactPattern) (fp1.getRightPattern()));
        Assert.assertNotNull(fp2.getFactPattern());
        FactPattern fp3 = fp2.getFactPattern();
        Assert.assertEquals("Transaction", fp3.getFactType());
        Assert.assertEquals(0, fp3.getNumberOfConstraints());
        Assert.assertNotNull(fp2.getExpression());
        ExpressionFormLine efl = fp2.getExpression();
        Assert.assertEquals(3, efl.getParts().size());
        Assert.assertTrue(((efl.getParts().get(0)) instanceof ExpressionVariable));
        ExpressionVariable ev = ((ExpressionVariable) (efl.getParts().get(0)));
        Assert.assertEquals("$trans", ev.getName());
        Assert.assertEquals("Transactions", ev.getClassType());
        Assert.assertTrue(((efl.getParts().get(1)) instanceof ExpressionText));
        ExpressionText et1 = ((ExpressionText) (efl.getParts().get(1)));
        Assert.assertEquals("getRecCategorization()", et1.getName());
        Assert.assertEquals("java.lang.String", et1.getClassType());
        Assert.assertEquals(TYPE_STRING, et1.getGenericType());
        Assert.assertTrue(((efl.getParts().get(2)) instanceof ExpressionText));
        ExpressionText et2 = ((ExpressionText) (efl.getParts().get(2)));
        Assert.assertEquals("get(\"APES-01\")", et2.getName());
        Assert.assertEquals("java.lang.String", et2.getClassType());
        Assert.assertEquals(TYPE_STRING, et2.getGenericType());
        Assert.assertEquals(0, m.rhs.length);
    }

    @Test
    public void testLHSInOperatorFieldNameNotContainingInLiteral() {
        String drl = "package org.test\n" + ((((("rule \"in\"\n" + "dialect \"mvel\"\n") + "when\n") + "Person( field1 in (1, 2) )\n") + "then\n") + "end");
        addModelField("org.test.Person", "field1", "java.lang.Integer", TYPE_NUMERIC_INTEGER);
        Mockito.when(dmo.getPackageName()).thenReturn("org.test");
        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals(1, m.lhs.length);
        IPattern p = m.lhs[0];
        Assert.assertTrue((p instanceof FactPattern));
        FactPattern fp = ((FactPattern) (p));
        Assert.assertEquals("Person", fp.getFactType());
        Assert.assertEquals(1, fp.getConstraintList().getConstraints().length);
        Assert.assertTrue(((fp.getConstraint(0)) instanceof SingleFieldConstraint));
        SingleFieldConstraint sfp = ((SingleFieldConstraint) (fp.getConstraint(0)));
        Assert.assertEquals("Person", sfp.getFactType());
        Assert.assertEquals("field1", sfp.getFieldName());
        Assert.assertEquals("in", sfp.getOperator());
        Assert.assertEquals("1, 2", sfp.getValue());
        Assert.assertEquals(TYPE_LITERAL, sfp.getConstraintValueType());
    }

    @Test
    public void testLHSInOperatorFieldNameContainingInLiteral() {
        String drl = "package org.test\n" + ((((("rule \"in\"\n" + "dialect \"mvel\"\n") + "when\n") + "Person( rating in (1, 2) )\n") + "then\n") + "end");
        addModelField("org.test.Person", "rating", "java.lang.Integer", TYPE_NUMERIC_INTEGER);
        Mockito.when(dmo.getPackageName()).thenReturn("org.test");
        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals(1, m.lhs.length);
        IPattern p = m.lhs[0];
        Assert.assertTrue((p instanceof FactPattern));
        FactPattern fp = ((FactPattern) (p));
        Assert.assertEquals("Person", fp.getFactType());
        Assert.assertEquals(1, fp.getConstraintList().getConstraints().length);
        Assert.assertTrue(((fp.getConstraint(0)) instanceof SingleFieldConstraint));
        SingleFieldConstraint sfp = ((SingleFieldConstraint) (fp.getConstraint(0)));
        Assert.assertEquals("Person", sfp.getFactType());
        Assert.assertEquals("rating", sfp.getFieldName());
        Assert.assertEquals("in", sfp.getOperator());
        Assert.assertEquals("1, 2", sfp.getValue());
        Assert.assertEquals(TYPE_LITERAL, sfp.getConstraintValueType());
    }

    @Test
    public void testLHSInOperatorFieldNameNotContainingNotInLiteral() {
        String drl = "package org.test\n" + ((((("rule \"in\"\n" + "dialect \"mvel\"\n") + "when\n") + "Person( field1 not in (1, 2) )\n") + "then\n") + "end");
        addModelField("org.test.Person", "field1", "java.lang.Integer", TYPE_NUMERIC_INTEGER);
        Mockito.when(dmo.getPackageName()).thenReturn("org.test");
        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals(1, m.lhs.length);
        IPattern p = m.lhs[0];
        Assert.assertTrue((p instanceof FactPattern));
        FactPattern fp = ((FactPattern) (p));
        Assert.assertEquals("Person", fp.getFactType());
        Assert.assertEquals(1, fp.getConstraintList().getConstraints().length);
        Assert.assertTrue(((fp.getConstraint(0)) instanceof SingleFieldConstraint));
        SingleFieldConstraint sfp = ((SingleFieldConstraint) (fp.getConstraint(0)));
        Assert.assertEquals("Person", sfp.getFactType());
        Assert.assertEquals("field1", sfp.getFieldName());
        Assert.assertEquals("not in", sfp.getOperator());
        Assert.assertEquals("1, 2", sfp.getValue());
        Assert.assertEquals(TYPE_LITERAL, sfp.getConstraintValueType());
    }

    @Test
    public void testLHSInOperatorFieldNameContainingNotInLiteral() {
        String drl = "package org.test\n" + ((((("rule \"in\"\n" + "dialect \"mvel\"\n") + "when\n") + "Person( rating not in (1, 2) )\n") + "then\n") + "end");
        addModelField("org.test.Person", "rating", "java.lang.Integer", TYPE_NUMERIC_INTEGER);
        Mockito.when(dmo.getPackageName()).thenReturn("org.test");
        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals(1, m.lhs.length);
        IPattern p = m.lhs[0];
        Assert.assertTrue((p instanceof FactPattern));
        FactPattern fp = ((FactPattern) (p));
        Assert.assertEquals("Person", fp.getFactType());
        Assert.assertEquals(1, fp.getConstraintList().getConstraints().length);
        Assert.assertTrue(((fp.getConstraint(0)) instanceof SingleFieldConstraint));
        SingleFieldConstraint sfp = ((SingleFieldConstraint) (fp.getConstraint(0)));
        Assert.assertEquals("Person", sfp.getFactType());
        Assert.assertEquals("rating", sfp.getFieldName());
        Assert.assertEquals("not in", sfp.getOperator());
        Assert.assertEquals("1, 2", sfp.getValue());
        Assert.assertEquals(TYPE_LITERAL, sfp.getConstraintValueType());
    }

    @Test
    public void testRHSModifyBlockSingleFieldSingleLine() throws Exception {
        // The value used in the "set" is intentionally yucky to catch extraction of the field's value errors!
        String drl = "rule \"modify1\"\n" + ((((("  dialect \"mvel\"\n" + "  when\n") + "    $p : Person( )\n") + "  then\n") + "  modify( $p ) { setFirstName( \",)\" ) }\n") + "end");
        addModelField("Person", "firstName", "java.lang.String", TYPE_STRING);
        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertEquals(1, m.rhs.length);
        Assert.assertTrue(((m.rhs[0]) instanceof ActionUpdateField));
        ActionUpdateField field = ((ActionUpdateField) (m.rhs[0]));
        Assert.assertEquals("$p", field.getVariable());
        Assert.assertNotNull(field.getFieldValues()[0]);
        Assert.assertEquals(1, field.getFieldValues().length);
        ActionFieldValue value = field.getFieldValues()[0];
        Assert.assertEquals("firstName", value.getField());
        Assert.assertEquals(",)", value.getValue());
        Assert.assertEquals(FieldNatureType.TYPE_LITERAL, value.getNature());
        Assert.assertEquals(TYPE_STRING, value.getType());
    }

    @Test
    public void testRHSModifyBlockSingleFieldMultipleLines() throws Exception {
        // The value used in the "set" is intentionally yucky to catch extraction of the field's value errors!
        String drl = "rule \"modify1\"\n" + ((((((("  dialect \"mvel\"\n" + "  when\n") + "    $p : Person( )\n") + "  then\n") + "  modify( $p ) {\n") + "    setFirstName( \",)\" )\n") + "  }\n") + "end");
        addModelField("Person", "firstName", "java.lang.String", TYPE_STRING);
        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertEquals(1, m.rhs.length);
        Assert.assertTrue(((m.rhs[0]) instanceof ActionUpdateField));
        ActionUpdateField field = ((ActionUpdateField) (m.rhs[0]));
        Assert.assertEquals("$p", field.getVariable());
        Assert.assertNotNull(field.getFieldValues()[0]);
        Assert.assertEquals(1, field.getFieldValues().length);
        ActionFieldValue value = field.getFieldValues()[0];
        Assert.assertEquals("firstName", value.getField());
        Assert.assertEquals(",)", value.getValue());
        Assert.assertEquals(FieldNatureType.TYPE_LITERAL, value.getNature());
        Assert.assertEquals(TYPE_STRING, value.getType());
    }

    @Test
    public void testRHSModifyBlockMultipleFieldsSingleLine() throws Exception {
        // The value used in the "set" is intentionally yucky to catch extraction of the field's value errors!
        String drl = "rule \"modify1\"\n" + ((((("  dialect \"mvel\"\n" + "  when\n") + "    $p : Person( )\n") + "  then\n") + "  modify( $p ) { setFirstName( \",)\" ), setLastName( \",)\" ) }\n") + "end");
        addModelField("Person", "firstName", "java.lang.String", TYPE_STRING);
        addModelField("Person", "lastName", "java.lang.String", TYPE_STRING);
        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertEquals(1, m.rhs.length);
        Assert.assertTrue(((m.rhs[0]) instanceof ActionUpdateField));
        ActionUpdateField field = ((ActionUpdateField) (m.rhs[0]));
        Assert.assertEquals("$p", field.getVariable());
        Assert.assertNotNull(field.getFieldValues()[0]);
        Assert.assertEquals(2, field.getFieldValues().length);
        ActionFieldValue value1 = field.getFieldValues()[0];
        Assert.assertEquals("firstName", value1.getField());
        Assert.assertEquals(",)", value1.getValue());
        Assert.assertEquals(FieldNatureType.TYPE_LITERAL, value1.getNature());
        Assert.assertEquals(TYPE_STRING, value1.getType());
        ActionFieldValue value2 = field.getFieldValues()[1];
        Assert.assertEquals("lastName", value2.getField());
        Assert.assertEquals(",)", value2.getValue());
        Assert.assertEquals(FieldNatureType.TYPE_LITERAL, value2.getNature());
        Assert.assertEquals(TYPE_STRING, value2.getType());
    }

    @Test
    public void testRHSModifyBlockMultipleFieldsMultipleLines() throws Exception {
        // The value used in the "set" is intentionally yucky to catch extraction of the field's value errors!
        String drl = "rule \"modify1\"\n" + (((((((("  dialect \"mvel\"\n" + "  when\n") + "    $p : Person( )\n") + "  then\n") + "  modify( $p ) { \n") + "    setFirstName( \",)\" ), \n") + "    setLastName( \",)\" )\n") + "  }\n") + "end");
        addModelField("Person", "firstName", "java.lang.String", TYPE_STRING);
        addModelField("Person", "lastName", "java.lang.String", TYPE_STRING);
        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertEquals(1, m.rhs.length);
        Assert.assertTrue(((m.rhs[0]) instanceof ActionUpdateField));
        ActionUpdateField field = ((ActionUpdateField) (m.rhs[0]));
        Assert.assertEquals("$p", field.getVariable());
        Assert.assertNotNull(field.getFieldValues()[0]);
        Assert.assertEquals(2, field.getFieldValues().length);
        ActionFieldValue value1 = field.getFieldValues()[0];
        Assert.assertEquals("firstName", value1.getField());
        Assert.assertEquals(",)", value1.getValue());
        Assert.assertEquals(FieldNatureType.TYPE_LITERAL, value1.getNature());
        Assert.assertEquals(TYPE_STRING, value1.getType());
        ActionFieldValue value2 = field.getFieldValues()[1];
        Assert.assertEquals("lastName", value2.getField());
        Assert.assertEquals(",)", value2.getValue());
        Assert.assertEquals(FieldNatureType.TYPE_LITERAL, value2.getNature());
        Assert.assertEquals(TYPE_STRING, value2.getType());
    }

    @Test
    public void testLiteralStrFieldNames() throws Exception {
        // The issue is fields that contain the "str" operator literal value
        String drl = "rule \"rule1\"\n" + (((("  dialect \"mvel\"\n" + "  when\n") + "    Room( decoration == \"tapestry\" , strangeField == 11 )\n") + "  then\n") + "end");
        addModelField("Room", "decoration", "java.lang.String", TYPE_STRING);
        addModelField("Room", "strangeField", "java.lang.Integer", TYPE_NUMERIC_INTEGER);
        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals("rule1", m.name);
        Assert.assertEquals(1, m.lhs.length);
        IPattern p = m.lhs[0];
        Assert.assertTrue((p instanceof FactPattern));
        FactPattern fp = ((FactPattern) (p));
        Assert.assertEquals("Room", fp.getFactType());
        Assert.assertEquals(2, fp.getNumberOfConstraints());
        Assert.assertTrue(((fp.getConstraint(0)) instanceof SingleFieldConstraint));
        SingleFieldConstraint sfp0 = ((SingleFieldConstraint) (fp.getConstraint(0)));
        Assert.assertEquals("Room", sfp0.getFactType());
        Assert.assertEquals("decoration", sfp0.getFieldName());
        Assert.assertEquals("==", sfp0.getOperator());
        Assert.assertEquals("tapestry", sfp0.getValue());
        Assert.assertEquals(TYPE_LITERAL, sfp0.getConstraintValueType());
        Assert.assertEquals(TYPE_STRING, sfp0.getFieldType());
        SingleFieldConstraint sfp1 = ((SingleFieldConstraint) (fp.getConstraint(1)));
        Assert.assertEquals("Room", sfp1.getFactType());
        Assert.assertEquals("strangeField", sfp1.getFieldName());
        Assert.assertEquals("==", sfp1.getOperator());
        Assert.assertEquals("11", sfp1.getValue());
        Assert.assertEquals(TYPE_LITERAL, sfp1.getConstraintValueType());
        Assert.assertEquals(TYPE_NUMERIC_INTEGER, sfp1.getFieldType());
    }

    @Test
    public void testMethodCallWithTwoParametersIntegerAndString() throws Exception {
        String drl = "package org.mortgages;\n" + (((((("rule \"test\"\n" + " dialect \"mvel\"\n") + " when\n") + "  $t : TestClass()\n") + " then\n") + "  $t.testFunction( 123, \"hello\" );\n") + "end");
        final HashMap<String, List<MethodInfo>> map = new HashMap<>();
        final ArrayList<MethodInfo> methodInfos = new ArrayList<>();
        final ArrayList<String> params = new ArrayList<>();
        params.add("Integer");
        params.add("String");
        methodInfos.add(new MethodInfo("testFunction", params, "java.lang.Void", null, "TestClass"));
        map.put("TestClass", methodInfos);
        Mockito.when(dmo.getModuleMethodInformation()).thenReturn(map);
        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals(1, m.lhs.length);
        Assert.assertEquals(1, m.rhs.length);
        ActionCallMethod actionCallMethod = ((ActionCallMethod) (m.rhs[0]));
        Assert.assertEquals("testFunction", actionCallMethod.getMethodName());
        Assert.assertEquals("$t", actionCallMethod.getVariable());
        Assert.assertEquals(2, actionCallMethod.getFieldValues().length);
        Assert.assertEquals("testFunction", actionCallMethod.getFieldValue(0).getField());
        Assert.assertEquals("123", getValue());
        Assert.assertEquals(FieldNatureType.TYPE_LITERAL, actionCallMethod.getFieldValue(0).getNature());
        Assert.assertEquals("Integer", actionCallMethod.getFieldValue(0).getType());
        Assert.assertEquals("testFunction", actionCallMethod.getFieldValue(1).getField());
        Assert.assertEquals("hello", getValue());
        Assert.assertEquals(FieldNatureType.TYPE_LITERAL, actionCallMethod.getFieldValue(1).getNature());
        Assert.assertEquals("String", actionCallMethod.getFieldValue(1).getType());
        assertEqualsIgnoreWhitespace(drl, RuleModelDRLPersistenceImpl.getInstance().marshal(m));
    }

    @Test
    public void testLHSNumberExpressionWithoutThisPrefix() throws Exception {
        String drl = "package org.mortgages;\n" + (((((("import java.lang.Number\n" + "rule \"test\"\n") + " dialect \"mvel\"\n") + " when\n") + "  Number( intValue() > 5 )\n") + " then\n") + "end");
        final HashMap<String, List<MethodInfo>> map = new HashMap<>();
        final ArrayList<MethodInfo> methodInfos = new ArrayList<>();
        methodInfos.add(new MethodInfo("intValue", Collections.emptyList(), "int", null, DataType.TYPE_NUMERIC_INTEGER));
        map.put("java.lang.Number", methodInfos);
        Mockito.when(dmo.getModuleMethodInformation()).thenReturn(map);
        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals(1, m.lhs.length);
        Assert.assertTrue(((m.lhs[0]) instanceof FactPattern));
        final FactPattern fp = ((FactPattern) (m.lhs[0]));
        Assert.assertEquals("Number", fp.getFactType());
        Assert.assertEquals(1, fp.getNumberOfConstraints());
        Assert.assertTrue(((fp.getConstraint(0)) instanceof SingleFieldConstraintEBLeftSide));
        final SingleFieldConstraintEBLeftSide exp = ((SingleFieldConstraintEBLeftSide) (fp.getConstraint(0)));
        Assert.assertEquals("int", exp.getFieldType());
        Assert.assertEquals(">", exp.getOperator());
        Assert.assertEquals("5", exp.getValue());
        Assert.assertEquals(2, exp.getExpressionLeftSide().getParts().size());
        Assert.assertTrue(((exp.getExpressionLeftSide().getParts().get(0)) instanceof ExpressionUnboundFact));
        final ExpressionUnboundFact expPart0 = ((ExpressionUnboundFact) (exp.getExpressionLeftSide().getParts().get(0)));
        Assert.assertEquals("Number", expPart0.getFactType());
        Assert.assertTrue(((exp.getExpressionLeftSide().getParts().get(1)) instanceof ExpressionMethod));
        final ExpressionMethod expPart1 = ((ExpressionMethod) (exp.getExpressionLeftSide().getParts().get(1)));
        Assert.assertEquals("intValue", expPart1.getName());
    }

    @Test
    public void testLHSNumberExpressionWithThisPrefix() throws Exception {
        String drl = "package org.mortgages;\n" + (((((("import java.lang.Number\n" + "rule \"test\"\n") + " dialect \"mvel\"\n") + " when\n") + "  Number( this.intValue() > 5 )\n") + " then\n") + "end");
        final HashMap<String, List<MethodInfo>> map = new HashMap<>();
        final ArrayList<MethodInfo> methodInfos = new ArrayList<>();
        methodInfos.add(new MethodInfo("intValue", Collections.emptyList(), "int", null, DataType.TYPE_NUMERIC_INTEGER));
        map.put("java.lang.Number", methodInfos);
        Mockito.when(dmo.getModuleMethodInformation()).thenReturn(map);
        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals(1, m.lhs.length);
        Assert.assertTrue(((m.lhs[0]) instanceof FactPattern));
        final FactPattern fp = ((FactPattern) (m.lhs[0]));
        Assert.assertEquals("Number", fp.getFactType());
        Assert.assertEquals(1, fp.getNumberOfConstraints());
        Assert.assertTrue(((fp.getConstraint(0)) instanceof SingleFieldConstraintEBLeftSide));
        final SingleFieldConstraintEBLeftSide exp = ((SingleFieldConstraintEBLeftSide) (fp.getConstraint(0)));
        Assert.assertEquals("int", exp.getFieldType());
        Assert.assertEquals(">", exp.getOperator());
        Assert.assertEquals("5", exp.getValue());
        Assert.assertEquals(3, exp.getExpressionLeftSide().getParts().size());
        Assert.assertTrue(((exp.getExpressionLeftSide().getParts().get(0)) instanceof ExpressionUnboundFact));
        final ExpressionUnboundFact expPart0 = ((ExpressionUnboundFact) (exp.getExpressionLeftSide().getParts().get(0)));
        Assert.assertEquals("Number", expPart0.getFactType());
        Assert.assertTrue(((exp.getExpressionLeftSide().getParts().get(1)) instanceof ExpressionField));
        final ExpressionField expPart1 = ((ExpressionField) (exp.getExpressionLeftSide().getParts().get(1)));
        Assert.assertEquals("this", expPart1.getName());
        Assert.assertTrue(((exp.getExpressionLeftSide().getParts().get(2)) instanceof ExpressionMethod));
        final ExpressionMethod expPart2 = ((ExpressionMethod) (exp.getExpressionLeftSide().getParts().get(2)));
        Assert.assertEquals("intValue", expPart2.getName());
    }

    @Test
    public void testLHSNestedMethodCalls() throws Exception {
        String drl = "package org.mortgages;\n" + ((((("rule \"test\"\n" + " dialect \"mvel\"\n") + " when\n") + "  Parent( methodToGetChild1().methodToGetChild2().field1 > 5 )\n") + " then\n") + "end");
        addMethodInformation("Parent", "methodToGetChild1", Collections.emptyList(), "Child1", null, "Child1");
        addMethodInformation("Child1", "methodToGetChild2", Collections.emptyList(), "Child2", null, "Child2");
        addModelField("Child2", "field1", "int", TYPE_NUMERIC_INTEGER);
        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals(1, m.lhs.length);
        Assert.assertTrue(((m.lhs[0]) instanceof FactPattern));
        final FactPattern fp = ((FactPattern) (m.lhs[0]));
        Assert.assertEquals("Parent", fp.getFactType());
        Assert.assertEquals(1, fp.getNumberOfConstraints());
        Assert.assertTrue(((fp.getConstraint(0)) instanceof SingleFieldConstraintEBLeftSide));
        final SingleFieldConstraintEBLeftSide exp = ((SingleFieldConstraintEBLeftSide) (fp.getConstraint(0)));
        Assert.assertEquals("int", exp.getFieldType());
        Assert.assertEquals(">", exp.getOperator());
        Assert.assertEquals("5", exp.getValue());
        Assert.assertEquals(4, exp.getExpressionLeftSide().getParts().size());
        Assert.assertTrue(((exp.getExpressionLeftSide().getParts().get(0)) instanceof ExpressionUnboundFact));
        final ExpressionUnboundFact expPart0 = ((ExpressionUnboundFact) (exp.getExpressionLeftSide().getParts().get(0)));
        Assert.assertEquals("Parent", expPart0.getFactType());
        Assert.assertTrue(((exp.getExpressionLeftSide().getParts().get(1)) instanceof ExpressionMethod));
        final ExpressionMethod expPart1 = ((ExpressionMethod) (exp.getExpressionLeftSide().getParts().get(1)));
        Assert.assertEquals("methodToGetChild1", expPart1.getName());
        Assert.assertTrue(((exp.getExpressionLeftSide().getParts().get(2)) instanceof ExpressionMethod));
        final ExpressionMethod expPart2 = ((ExpressionMethod) (exp.getExpressionLeftSide().getParts().get(2)));
        Assert.assertEquals("methodToGetChild2", expPart2.getName());
        Assert.assertTrue(((exp.getExpressionLeftSide().getParts().get(3)) instanceof ExpressionField));
        final ExpressionField expPart3 = ((ExpressionField) (exp.getExpressionLeftSide().getParts().get(3)));
        Assert.assertEquals("field1", expPart3.getName());
    }

    @Test
    public void testLHSMissingConstraints() {
        String drl = "package org.mortgages;\n" + (((((((((((((("import java.lang.Number;\n" + "import org.drools.workbench.models.commons.backend.rule.classes.SearchContext;\n") + "import org.drools.workbench.models.commons.backend.rule.classes.ProducerMasterForRules;\n") + "import org.drools.workbench.models.commons.backend.rule.classes.RuleFactor;\n") + "rule \"SecondaryCuisineRepeatUsage\"\n") + "dialect \"mvel\"\n") + "when\n") + "  $searchContext : SearchContext( lastThreeCuisines != null )\n") + "  ProducerMasterForRules( primaryCuisine != null, primaryCuisine != $searchContext.lastThreeCuisines, secondaryCuisine != null, secondaryCuisine == $searchContext.lastThreeCuisines )\n") + "  $secondaryCuisineRepeatUsageFactor : RuleFactor( )\n") + "then\n") + "  modify( $secondaryCuisineRepeatUsageFactor ) {\n") + "    setWeightageImpact(-30)\n") + "  }\n") + "end\n");
        addModelField("org.drools.workbench.models.commons.backend.rule.classes.SearchContext", "this", "org.drools.workbench.models.commons.backend.rule.classes.SearchContext", TYPE_THIS);
        addModelField("org.drools.workbench.models.commons.backend.rule.classes.SearchContext", "lastThreeCuisines", "java.lang.String", TYPE_STRING);
        addModelField("org.drools.workbench.models.commons.backend.rule.classes.ProducerMasterForRules", "primaryCuisine", "java.lang.String", TYPE_STRING);
        addModelField("org.drools.workbench.models.commons.backend.rule.classes.ProducerMasterForRules", "secondaryCuisine", "java.lang.String", TYPE_STRING);
        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        assertEqualsIgnoreWhitespace(drl, RuleModelDRLPersistenceImpl.getInstance().marshal(m));
    }

    @Test
    public void testLHSNestedMethodOneParameter() {
        String drl = "package org.mortgages;\n" + (((((((((("import java.lang.Number;\n" + "import java.util.List;\n") + "import org.drools.workbench.models.commons.backend.rule.classes.MyListContainerClass;\n") + "import org.drools.workbench.models.commons.backend.rule.classes.MyStringContainerClass;\n") + "rule \"r1\"\n") + "dialect \"mvel\"\n") + "when\n") + "  $foo : MyListContainerClass()\n") + "  $bar : MyStringContainerClass( myString == $foo.myList.get(1))\n") + "then\n") + "end\n");
        addModelField("org.drools.workbench.models.commons.backend.rule.classes.MyListContainerClass", "this", "org.drools.workbench.models.commons.backend.rule.classes.MyListContainerClass", TYPE_THIS);
        addModelField("org.drools.workbench.models.commons.backend.rule.classes.MyListContainerClass", "myList", "java.util.List", "java.util.List");
        addModelField("org.drools.workbench.models.commons.backend.rule.classes.MyStringContainerClass", "this", "org.drools.workbench.models.commons.backend.rule.classes.MyStringContainerClass", TYPE_THIS);
        addModelField("org.drools.workbench.models.commons.backend.rule.classes.MyStringContainerClass", "myString", "java.lang.String", TYPE_STRING);
        addMethodInformation("java.util.List", "get", new ArrayList<String>() {
            {
                add("Integer");
            }
        }, "java.lang.Object", null, "java.lang.Object");
        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        assertEqualsIgnoreWhitespace(drl, RuleModelDRLPersistenceImpl.getInstance().marshal(m));
    }

    @Test
    public void testLHSNestedMethodTwoParameters() {
        String drl = "package org.mortgages;\n" + (((((((((("import java.lang.Number;\n" + "import java.util.List;\n") + "import org.drools.workbench.models.commons.backend.rule.classes.MyListContainerClass;\n") + "import org.drools.workbench.models.commons.backend.rule.classes.MyStringContainerClass;\n") + "rule \"r1\"\n") + "dialect \"mvel\"\n") + "when\n") + "  $foo : MyListContainerClass()\n") + "  $bar : MyStringContainerClass( $foo.myList.set(1, \"hello\" ) == true )\n") + "then\n") + "end\n");
        addModelField("org.drools.workbench.models.commons.backend.rule.classes.MyListContainerClass", "this", "org.drools.workbench.models.commons.backend.rule.classes.MyListContainerClass", TYPE_THIS);
        addModelField("org.drools.workbench.models.commons.backend.rule.classes.MyListContainerClass", "myList", "java.util.List", "java.util.List");
        addModelField("org.drools.workbench.models.commons.backend.rule.classes.MyStringContainerClass", "this", "org.drools.workbench.models.commons.backend.rule.classes.MyStringContainerClass", TYPE_THIS);
        addModelField("org.drools.workbench.models.commons.backend.rule.classes.MyStringContainerClass", "myString", "java.lang.String", TYPE_STRING);
        addModelField("java.util.List", "this", "java.util.List", TYPE_THIS);
        addMethodInformation("java.util.List", "set", new ArrayList<String>() {
            {
                add("Integer");
                add("String");
            }
        }, TYPE_BOOLEAN, null, TYPE_BOOLEAN);
        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        assertEqualsIgnoreWhitespace(drl, RuleModelDRLPersistenceImpl.getInstance().marshal(m));
    }

    // https://bugzilla.redhat.com/show_bug.cgi?id=1160658
    @Test
    public void testRHSFormulaTruncation() throws Exception {
        String drl = "package org.test;\n" + (((((("rule \"rule1\"\n" + "dialect \"mvel\"\n") + "when\n") + "  $p : Person()\n") + "then\n") + "  $p.setTextOut( $p.getType() + \"\" );\n") + "end");
        addModelField("org.test.Person", "this", "org.test.Person", TYPE_THIS);
        addModelField("org.test.Person", "textOut", "java.lang.String", TYPE_STRING);
        addModelField("org.test.Person", "type", "java.lang.String", TYPE_STRING);
        Mockito.when(dmo.getPackageName()).thenReturn("org.test");
        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals(1, m.lhs.length);
        Assert.assertEquals(1, m.rhs.length);
        assertEqualsIgnoreWhitespace(drl, RuleModelDRLPersistenceImpl.getInstance().marshal(m));
    }

    // https://bugzilla.redhat.com/show_bug.cgi?id=1158813
    @Test
    public void testLHSFromCollectWithoutListDeclared() throws Exception {
        String drl = "package org.test;\n" + ((((((("rule \"rule1\"\n" + "dialect \"mvel\"\n") + "when\n") + "  c : Customer( )\n") + "  items : java.util.List( eval( size == c.items.size )) \n") + "  from collect ( var : Item( price > 10 )) \n") + "then \n") + "end");
        addModelField("org.test.Customer", "this", "org.test.Customer", TYPE_THIS);
        addModelField("org.test.Item", "this", "org.test.Item", TYPE_THIS);
        addModelField("org.test.Item", "price", "java.lang.Double", TYPE_NUMERIC_DOUBLE);
        addMethodInformation("org.test.Customer", "items", new ArrayList<String>() {
            {
            }
        }, TYPE_NUMERIC_INTEGER, null, TYPE_NUMERIC_INTEGER);
        Mockito.when(dmo.getPackageName()).thenReturn("org.test");
        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals(2, m.lhs.length);
        Assert.assertEquals(0, m.rhs.length);
        Assert.assertTrue(((m.lhs[0]) instanceof FactPattern));
        final FactPattern fp = ((FactPattern) (m.lhs[0]));
        Assert.assertEquals("Customer", fp.getFactType());
        Assert.assertEquals("c", fp.getBoundName());
        Assert.assertEquals(0, fp.getNumberOfConstraints());
        Assert.assertTrue(((m.lhs[1]) instanceof FromCollectCompositeFactPattern));
        final FromCollectCompositeFactPattern fcfp = ((FromCollectCompositeFactPattern) (m.lhs[1]));
        Assert.assertNotNull(fcfp.getFactPattern());
        Assert.assertEquals("java.util.List", getFactType());
        Assert.assertEquals("items", fcfp.getFactPattern().getBoundName());
        Assert.assertEquals(1, fcfp.getFactPattern().getNumberOfConstraints());
        Assert.assertTrue(((getConstraint(0)) instanceof SingleFieldConstraint));
        final SingleFieldConstraint sfc0 = ((SingleFieldConstraint) (getConstraint(0)));
        Assert.assertNull(sfc0.getFactType());
        Assert.assertNull(sfc0.getFieldName());
        Assert.assertEquals("size == c.items.size", sfc0.getValue());
        Assert.assertEquals(TYPE_PREDICATE, sfc0.getConstraintValueType());
        Assert.assertNotNull(fcfp.getRightPattern());
        Assert.assertTrue(((fcfp.getRightPattern()) instanceof FactPattern));
        final FactPattern rfp = ((FactPattern) (fcfp.getRightPattern()));
        Assert.assertEquals("Item", rfp.getFactType());
        Assert.assertEquals("var", rfp.getBoundName());
        Assert.assertEquals(1, rfp.getNumberOfConstraints());
        Assert.assertTrue(((rfp.getConstraint(0)) instanceof SingleFieldConstraint));
        final SingleFieldConstraint sfc1 = ((SingleFieldConstraint) (rfp.getConstraint(0)));
        Assert.assertEquals("Item", sfc1.getFactType());
        Assert.assertEquals("price", sfc1.getFieldName());
        Assert.assertEquals("10", sfc1.getValue());
        Assert.assertEquals(">", sfc1.getOperator());
        Assert.assertEquals(TYPE_LITERAL, sfc1.getConstraintValueType());
        assertEqualsIgnoreWhitespace(drl, RuleModelDRLPersistenceImpl.getInstance().marshal(m));
    }

    // https://bugzilla.redhat.com/show_bug.cgi?id=1158813
    @Test
    public void testLHSFromCollectWithListDeclared() throws Exception {
        String drl = "package org.test;\n" + ((((((("rule \"rule1\"\n" + "dialect \"mvel\"\n") + "when\n") + "  c : Customer( )\n") + "  items : List( eval( size == c.items.size )) \n") + "  from collect ( var : Item( price > 10 )) \n") + "then \n") + "end");
        addModelField("org.test.Customer", "this", "org.test.Customer", TYPE_THIS);
        addModelField("org.test.Item", "this", "org.test.Item", TYPE_THIS);
        addModelField("org.test.Item", "price", "java.lang.Double", TYPE_NUMERIC_DOUBLE);
        addModelField("java.util.List", "this", "java.util.List", TYPE_THIS);
        addMethodInformation("org.test.Customer", "items", new ArrayList<String>() {
            {
            }
        }, TYPE_NUMERIC_INTEGER, null, TYPE_NUMERIC_INTEGER);
        Mockito.when(dmo.getPackageName()).thenReturn("org.test");
        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals(2, m.lhs.length);
        Assert.assertEquals(0, m.rhs.length);
        Assert.assertTrue(((m.lhs[0]) instanceof FactPattern));
        final FactPattern fp = ((FactPattern) (m.lhs[0]));
        Assert.assertEquals("Customer", fp.getFactType());
        Assert.assertEquals("c", fp.getBoundName());
        Assert.assertEquals(0, fp.getNumberOfConstraints());
        Assert.assertTrue(((m.lhs[1]) instanceof FromCollectCompositeFactPattern));
        final FromCollectCompositeFactPattern fcfp = ((FromCollectCompositeFactPattern) (m.lhs[1]));
        Assert.assertNotNull(fcfp.getFactPattern());
        Assert.assertEquals("List", getFactType());
        Assert.assertEquals("items", fcfp.getFactPattern().getBoundName());
        Assert.assertEquals(1, fcfp.getFactPattern().getNumberOfConstraints());
        Assert.assertTrue(((getConstraint(0)) instanceof SingleFieldConstraint));
        final SingleFieldConstraint sfc0 = ((SingleFieldConstraint) (getConstraint(0)));
        Assert.assertNull(sfc0.getFactType());
        Assert.assertNull(sfc0.getFieldName());
        Assert.assertEquals("size == c.items.size", sfc0.getValue());
        Assert.assertEquals(TYPE_PREDICATE, sfc0.getConstraintValueType());
        Assert.assertNotNull(fcfp.getRightPattern());
        Assert.assertTrue(((fcfp.getRightPattern()) instanceof FactPattern));
        final FactPattern rfp = ((FactPattern) (fcfp.getRightPattern()));
        Assert.assertEquals("Item", rfp.getFactType());
        Assert.assertEquals("var", rfp.getBoundName());
        Assert.assertEquals(1, rfp.getNumberOfConstraints());
        Assert.assertTrue(((rfp.getConstraint(0)) instanceof SingleFieldConstraint));
        final SingleFieldConstraint sfc1 = ((SingleFieldConstraint) (rfp.getConstraint(0)));
        Assert.assertEquals("Item", sfc1.getFactType());
        Assert.assertEquals("price", sfc1.getFieldName());
        Assert.assertEquals("10", sfc1.getValue());
        Assert.assertEquals(">", sfc1.getOperator());
        Assert.assertEquals(TYPE_LITERAL, sfc1.getConstraintValueType());
        assertEqualsIgnoreWhitespace(drl, RuleModelDRLPersistenceImpl.getInstance().marshal(m));
    }

    // https://bugzilla.redhat.com/show_bug.cgi?id=1134067
    @Test
    public void testRHSFormulaTruncationInsertFact() throws Exception {
        String drl = "package org.test;\n" + (((((((("rule \"rule1\"\n" + "dialect \"mvel\"\n") + "when\n") + "  Person( $t : name )\n") + "then \n") + "  Person fact0 = new Person();\n") + "  fact0.setName( $t );\n") + "  insert( fact0 );\n") + "end");
        addModelField("org.test.Person", "this", "org.test.Person", TYPE_THIS);
        addModelField("org.test.Person", "name", "java.lang.String", TYPE_STRING);
        addModelField("org.test.Person", "type", "java.lang.String", TYPE_STRING);
        Mockito.when(dmo.getPackageName()).thenReturn("org.test");
        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals(1, m.lhs.length);
        Assert.assertEquals(1, m.rhs.length);
        assertEqualsIgnoreWhitespace(drl, RuleModelDRLPersistenceImpl.getInstance().marshal(m));
    }

    // https://issues.jboss.org/browse/GUVNOR-2144
    @Test
    public void testRuleModelPersistenceHelperUnwrapParenthesis() throws Exception {
        String drl = "package org.test;\n" + ((((((((("rule \"Load all schedules\"\n" + "dialect \"mvel\"\n") + "agenda-group \"LoadSchedules\"\n") + "salience 900\n") + "  when\n") + "    $bundle : Bundle( evaluated == false )\n") + "  then\n") + "    $bundle.setEvaluated( true );\n") + "    update( $bundle );\n") + "end");
        // Expected is different as we convert "update" to "modify" blocks
        String expected = "package org.test;\n" + (((((((((("rule \"Load all schedules\"\n" + "dialect \"mvel\"\n") + "agenda-group \"LoadSchedules\"\n") + "salience 900\n") + "  when\n") + "    $bundle : Bundle( evaluated == false )\n") + "  then\n") + "    modify( $bundle ) {\n") + "      setEvaluated( true )\n") + "    }\n") + "end");
        addModelField("org.test.Bundle", "this", "org.test.Bundle", TYPE_THIS);
        addModelField("org.test.Person", "evaluated", Boolean.class.getName(), TYPE_BOOLEAN);
        Mockito.when(dmo.getPackageName()).thenReturn("org.test");
        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals(1, m.lhs.length);
        Assert.assertEquals(1, m.rhs.length);
        Assert.assertEquals(3, m.attributes.length);
        assertEqualsIgnoreWhitespace(expected, RuleModelDRLPersistenceImpl.getInstance().marshal(m));
    }

    // https://issues.jboss.org/browse/GUVNOR-2141
    @Test
    public void testSingleFieldConstraintConnectives1() {
        String drl = "package org.test;\n" + ((((("rule \"rule1\"\n" + "dialect \"mvel\"\n") + "  when\n") + "    Applicant( age < 55 || > 75 )\n") + "  then\n") + "end");
        addModelField("org.test.Applicant", "this", "org.test.Applicant", TYPE_THIS);
        addModelField("org.test.Applicant", "age", Integer.class.getName(), TYPE_NUMERIC_INTEGER);
        Mockito.when(dmo.getPackageName()).thenReturn("org.test");
        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals("rule1", m.name);
        Assert.assertEquals(1, m.lhs.length);
        IPattern p = m.lhs[0];
        Assert.assertTrue((p instanceof FactPattern));
        FactPattern fp = ((FactPattern) (p));
        Assert.assertEquals("Applicant", fp.getFactType());
        Assert.assertEquals(1, fp.getConstraintList().getConstraints().length);
        Assert.assertTrue(((fp.getConstraint(0)) instanceof SingleFieldConstraint));
        SingleFieldConstraint sfp = ((SingleFieldConstraint) (fp.getConstraint(0)));
        Assert.assertEquals("Applicant", sfp.getFactType());
        Assert.assertEquals("age", sfp.getFieldName());
        Assert.assertEquals("<", sfp.getOperator());
        Assert.assertEquals("55", sfp.getValue());
        Assert.assertEquals(TYPE_LITERAL, sfp.getConstraintValueType());
        Assert.assertEquals(1, sfp.getConnectives().length);
        ConnectiveConstraint cc = sfp.getConnectives()[0];
        Assert.assertEquals("Applicant", cc.getFactType());
        Assert.assertEquals("age", cc.getFieldName());
        Assert.assertEquals("|| >", cc.getOperator());
        Assert.assertEquals("75", cc.getValue());
        Assert.assertEquals(TYPE_LITERAL, cc.getConstraintValueType());
        assertEqualsIgnoreWhitespace(drl, RuleModelDRLPersistenceImpl.getInstance().marshal(m));
    }

    // https://issues.jboss.org/browse/GUVNOR-2141
    @Test
    public void testSingleFieldConstraintConnectives2() {
        String drl = "package org.test;\n" + ((((("rule \"rule1\"\n" + "dialect \"mvel\"\n") + "  when\n") + "    Applicant( age == 55 || == 75 )\n") + "  then\n") + "end");
        addModelField("org.test.Applicant", "this", "org.test.Applicant", TYPE_THIS);
        addModelField("org.test.Applicant", "age", Integer.class.getName(), TYPE_NUMERIC_INTEGER);
        Mockito.when(dmo.getPackageName()).thenReturn("org.test");
        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals("rule1", m.name);
        Assert.assertEquals(1, m.lhs.length);
        IPattern p = m.lhs[0];
        Assert.assertTrue((p instanceof FactPattern));
        FactPattern fp = ((FactPattern) (p));
        Assert.assertEquals("Applicant", fp.getFactType());
        Assert.assertEquals(1, fp.getConstraintList().getConstraints().length);
        Assert.assertTrue(((fp.getConstraint(0)) instanceof SingleFieldConstraint));
        SingleFieldConstraint sfp = ((SingleFieldConstraint) (fp.getConstraint(0)));
        Assert.assertEquals("Applicant", sfp.getFactType());
        Assert.assertEquals("age", sfp.getFieldName());
        Assert.assertEquals("==", sfp.getOperator());
        Assert.assertEquals("55", sfp.getValue());
        Assert.assertEquals(TYPE_LITERAL, sfp.getConstraintValueType());
        Assert.assertEquals(1, sfp.getConnectives().length);
        ConnectiveConstraint cc = sfp.getConnectives()[0];
        Assert.assertEquals("Applicant", cc.getFactType());
        Assert.assertEquals("age", cc.getFieldName());
        Assert.assertEquals("|| ==", cc.getOperator());
        Assert.assertEquals("75", cc.getValue());
        Assert.assertEquals(TYPE_LITERAL, cc.getConstraintValueType());
        assertEqualsIgnoreWhitespace(drl, RuleModelDRLPersistenceImpl.getInstance().marshal(m));
    }

    // https://issues.jboss.org/browse/RHBRMS-2854
    @Test
    public void testSingleFieldConstraintConnectives3() {
        String drl = "package org.test;\n" + ((((("rule \"rule1\"\n" + "dialect \"mvel\"\n") + "when\n") + "    Applicant(age != 1 && < 10 && > 5)\n") + "then\n") + "end");
        addModelField("org.test.Applicant", "this", "org.test.Applicant", TYPE_THIS);
        addModelField("org.test.Applicant", "age", Integer.class.getName(), TYPE_NUMERIC_INTEGER);
        Mockito.when(dmo.getPackageName()).thenReturn("org.test");
        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals("rule1", m.name);
        Assert.assertEquals(1, m.lhs.length);
        IPattern p = m.lhs[0];
        Assert.assertTrue((p instanceof FactPattern));
        FactPattern fp = ((FactPattern) (p));
        Assert.assertEquals("Applicant", fp.getFactType());
        Assert.assertEquals(1, fp.getConstraintList().getConstraints().length);
        Assert.assertTrue(((fp.getConstraint(0)) instanceof SingleFieldConstraint));
        SingleFieldConstraint sfp = ((SingleFieldConstraint) (fp.getConstraint(0)));
        Assert.assertEquals("Applicant", sfp.getFactType());
        Assert.assertEquals("age", sfp.getFieldName());
        Assert.assertEquals("!=", sfp.getOperator());
        Assert.assertEquals("1", sfp.getValue());
        Assert.assertEquals(TYPE_LITERAL, sfp.getConstraintValueType());
        Assert.assertEquals(2, sfp.getConnectives().length);
        ConnectiveConstraint cc1 = sfp.getConnectives()[0];
        Assert.assertEquals("Applicant", cc1.getFactType());
        Assert.assertEquals("age", cc1.getFieldName());
        Assert.assertEquals("&& <", cc1.getOperator());
        Assert.assertEquals("10", cc1.getValue());
        Assert.assertEquals(TYPE_LITERAL, cc1.getConstraintValueType());
        ConnectiveConstraint cc2 = sfp.getConnectives()[1];
        Assert.assertEquals("Applicant", cc2.getFactType());
        Assert.assertEquals("age", cc2.getFieldName());
        Assert.assertEquals("&& >", cc2.getOperator());
        Assert.assertEquals("5", cc2.getValue());
        Assert.assertEquals(TYPE_LITERAL, cc2.getConstraintValueType());
        assertEqualsIgnoreWhitespace(drl, RuleModelDRLPersistenceImpl.getInstance().marshal(m));
    }

    // https://issues.jboss.org/browse/RHBRMS-2854
    @Test
    public void testSingleFieldConstraintConnectives4() {
        String drl = "package org.test;\n" + ((((("rule \"rule1\"\n" + "dialect \"mvel\"\n") + "when\n") + "    Applicant(age != null && != 1 && < 10 && > 5)\n") + "then\n") + "end");
        addModelField("org.test.Applicant", "this", "org.test.Applicant", TYPE_THIS);
        addModelField("org.test.Applicant", "age", Integer.class.getName(), TYPE_NUMERIC_INTEGER);
        Mockito.when(dmo.getPackageName()).thenReturn("org.test");
        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals("rule1", m.name);
        Assert.assertEquals(1, m.lhs.length);
        IPattern p = m.lhs[0];
        Assert.assertTrue((p instanceof FactPattern));
        FactPattern fp = ((FactPattern) (p));
        Assert.assertEquals("Applicant", fp.getFactType());
        Assert.assertEquals(1, fp.getConstraintList().getConstraints().length);
        Assert.assertTrue(((fp.getConstraint(0)) instanceof SingleFieldConstraint));
        SingleFieldConstraint sfp = ((SingleFieldConstraint) (fp.getConstraint(0)));
        Assert.assertEquals("Applicant", sfp.getFactType());
        Assert.assertEquals("age", sfp.getFieldName());
        Assert.assertEquals("!= null", sfp.getOperator());
        Assert.assertNull(sfp.getValue());
        Assert.assertEquals(TYPE_UNDEFINED, sfp.getConstraintValueType());
        Assert.assertEquals(3, sfp.getConnectives().length);
        ConnectiveConstraint cc1 = sfp.getConnectives()[0];
        Assert.assertEquals("Applicant", cc1.getFactType());
        Assert.assertEquals("age", cc1.getFieldName());
        Assert.assertEquals("&& !=", cc1.getOperator());
        Assert.assertEquals("1", cc1.getValue());
        Assert.assertEquals(TYPE_LITERAL, cc1.getConstraintValueType());
        ConnectiveConstraint cc2 = sfp.getConnectives()[1];
        Assert.assertEquals("Applicant", cc2.getFactType());
        Assert.assertEquals("age", cc2.getFieldName());
        Assert.assertEquals("&& <", cc2.getOperator());
        Assert.assertEquals("10", cc2.getValue());
        Assert.assertEquals(TYPE_LITERAL, cc2.getConstraintValueType());
        ConnectiveConstraint cc3 = sfp.getConnectives()[2];
        Assert.assertEquals("Applicant", cc3.getFactType());
        Assert.assertEquals("age", cc3.getFieldName());
        Assert.assertEquals("&& >", cc3.getOperator());
        Assert.assertEquals("5", cc3.getValue());
        Assert.assertEquals(TYPE_LITERAL, cc3.getConstraintValueType());
        assertEqualsIgnoreWhitespace(drl, RuleModelDRLPersistenceImpl.getInstance().marshal(m));
    }

    // https://issues.jboss.org/browse/GUVNOR-2143
    @Test
    public void testNewKeywordVariableNamePrefix1() {
        String oldValue = System.getProperty("drools.dateformat");
        try {
            System.setProperty("drools.dateformat", "dd-MMM-yyyy");
            String drl = "package org.test;\n" + (((((((((("rule \"rule1\"\n" + "  dialect \"java\"\n") + "  when\n") + "    $bundle : Bundle( $treatmentEffectiveDt : treatmentEffectiveDt )\n") + "  then\n") + "    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(\"dd-MMM-yyyy\");\n") + "    DateTime newStartDate = new DateTime();\n") + "    modify( $bundle ) {\n") + "      setTreatmentEffectiveDt( newStartDate.toDate() )") + "    }\n") + "end\n");
            addModelField("org.test.Bundle", "this", "org.test.Bundle", TYPE_THIS);
            addModelField("org.test.Bundle", "treatmentEffectiveDt", Date.class.getName(), TYPE_DATE);
            Mockito.when(dmo.getPackageName()).thenReturn("org.test");
            RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
            Assert.assertNotNull(m);
            Assert.assertEquals("rule1", m.name);
            Assert.assertEquals(1, m.lhs.length);
            IPattern p = m.lhs[0];
            Assert.assertTrue((p instanceof FactPattern));
            FactPattern fp = ((FactPattern) (p));
            Assert.assertEquals("Bundle", fp.getFactType());
            Assert.assertEquals("$bundle", fp.getBoundName());
            Assert.assertEquals(1, fp.getConstraintList().getConstraints().length);
            Assert.assertTrue(((fp.getConstraint(0)) instanceof SingleFieldConstraint));
            SingleFieldConstraint sfp = ((SingleFieldConstraint) (fp.getConstraint(0)));
            Assert.assertEquals("Bundle", sfp.getFactType());
            Assert.assertEquals("treatmentEffectiveDt", sfp.getFieldName());
            Assert.assertEquals("$treatmentEffectiveDt", sfp.getFieldBinding());
            Assert.assertNull(sfp.getOperator());
            Assert.assertNull(sfp.getValue());
            Assert.assertEquals(TYPE_UNDEFINED, sfp.getConstraintValueType());
            Assert.assertEquals(2, m.rhs.length);
            Assert.assertTrue(((m.rhs[0]) instanceof FreeFormLine));
            FreeFormLine ffl = ((FreeFormLine) (m.rhs[0]));
            Assert.assertEquals("DateTime newStartDate = new DateTime();", ffl.getText());
            Assert.assertTrue(((m.rhs[1]) instanceof ActionUpdateField));
            ActionUpdateField auf = ((ActionUpdateField) (m.rhs[1]));
            Assert.assertEquals("$bundle", auf.getVariable());
            Assert.assertEquals(1, auf.getFieldValues().length);
            ActionFieldValue afv = auf.getFieldValues()[0];
            Assert.assertEquals("treatmentEffectiveDt", afv.getField());
            Assert.assertEquals("newStartDate.toDate()", afv.getValue());
            Assert.assertEquals(TYPE_FORMULA, afv.getNature());
            assertEqualsIgnoreWhitespace(drl, RuleModelDRLPersistenceImpl.getInstance().marshal(m));
        } finally {
            if (oldValue == null) {
                System.clearProperty("drools.dateformat");
            } else {
                System.setProperty("drools.dateformat", oldValue);
            }
        }
    }

    // https://issues.jboss.org/browse/GUVNOR-2143
    @Test
    public void testNewKeywordVariableNamePrefix2() {
        String oldValue = System.getProperty("drools.dateformat");
        try {
            System.setProperty("drools.dateformat", "dd-MMM-yyyy");
            String drl = "package org.test;\n" + (((((((((("rule \"rule1\"\n" + "  dialect \"java\"\n") + "  when\n") + "    $a : Applicant()\n") + "  then\n") + "    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(\"dd-MMM-yyyy\");\n") + "    java.util.Date newStartDate = new java.util.Date();\n") + "    modify( $a ) {\n") + "      setApplicantDate( newStartDate )") + "    }\n") + "end\n");
            addModelField("org.test.Applicant", "this", "org.test.Applicant", TYPE_THIS);
            addModelField("org.test.Applicant", "applicantDate", Date.class.getName(), TYPE_DATE);
            Mockito.when(dmo.getPackageName()).thenReturn("org.test");
            RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
            Assert.assertNotNull(m);
            Assert.assertEquals("rule1", m.name);
            Assert.assertEquals(1, m.lhs.length);
            IPattern p = m.lhs[0];
            Assert.assertTrue((p instanceof FactPattern));
            FactPattern fp = ((FactPattern) (p));
            Assert.assertEquals("Applicant", fp.getFactType());
            Assert.assertEquals("$a", fp.getBoundName());
            Assert.assertNull(fp.getConstraintList());
            Assert.assertEquals(2, m.rhs.length);
            Assert.assertTrue(((m.rhs[0]) instanceof FreeFormLine));
            FreeFormLine ffl = ((FreeFormLine) (m.rhs[0]));
            Assert.assertEquals("java.util.Date newStartDate = new java.util.Date();", ffl.getText());
            Assert.assertTrue(((m.rhs[1]) instanceof ActionUpdateField));
            ActionUpdateField auf = ((ActionUpdateField) (m.rhs[1]));
            Assert.assertEquals("$a", auf.getVariable());
            Assert.assertEquals(1, auf.getFieldValues().length);
            ActionFieldValue afv = auf.getFieldValues()[0];
            Assert.assertEquals("applicantDate", afv.getField());
            Assert.assertEquals("newStartDate", afv.getValue());
            Assert.assertEquals(TYPE_FORMULA, afv.getNature());
            assertEqualsIgnoreWhitespace(drl, RuleModelDRLPersistenceImpl.getInstance().marshal(m));
        } finally {
            if (oldValue == null) {
                System.clearProperty("drools.dateformat");
            } else {
                System.setProperty("drools.dateformat", oldValue);
            }
        }
    }

    // https://issues.jboss.org/browse/GUVNOR-2143
    @Test
    public void testNewKeywordVariableNamePrefix3() {
        String oldValue = System.getProperty("drools.dateformat");
        try {
            System.setProperty("drools.dateformat", "dd-MMM-yyyy");
            String drl = "package org.test;\n" + ((((((((("rule \"rule1\"\n" + "  dialect \"java\"\n") + "  when\n") + "    $a : Applicant()\n") + "  then\n") + "    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(\"dd-MMM-yyyy\");\n") + "    java.util.Date newStartDate = new java.util.Date();\n") + "    modify( $a ) \n") + "    { setApplicantDate( newStartDate ) }\n") + "end\n");
            addModelField("org.test.Applicant", "this", "org.test.Applicant", TYPE_THIS);
            addModelField("org.test.Applicant", "applicantDate", Date.class.getName(), TYPE_DATE);
            Mockito.when(dmo.getPackageName()).thenReturn("org.test");
            RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
            Assert.assertNotNull(m);
            Assert.assertEquals("rule1", m.name);
            Assert.assertEquals(1, m.lhs.length);
            IPattern p = m.lhs[0];
            Assert.assertTrue((p instanceof FactPattern));
            FactPattern fp = ((FactPattern) (p));
            Assert.assertEquals("Applicant", fp.getFactType());
            Assert.assertEquals("$a", fp.getBoundName());
            Assert.assertNull(fp.getConstraintList());
            Assert.assertEquals(2, m.rhs.length);
            Assert.assertTrue(((m.rhs[0]) instanceof FreeFormLine));
            FreeFormLine ffl = ((FreeFormLine) (m.rhs[0]));
            Assert.assertEquals("java.util.Date newStartDate = new java.util.Date();", ffl.getText());
            Assert.assertTrue(((m.rhs[1]) instanceof ActionUpdateField));
            ActionUpdateField auf = ((ActionUpdateField) (m.rhs[1]));
            Assert.assertEquals("$a", auf.getVariable());
            Assert.assertEquals(1, auf.getFieldValues().length);
            ActionFieldValue afv = auf.getFieldValues()[0];
            Assert.assertEquals("applicantDate", afv.getField());
            Assert.assertEquals("newStartDate", afv.getValue());
            Assert.assertEquals(TYPE_FORMULA, afv.getNature());
            assertEqualsIgnoreWhitespace(drl, RuleModelDRLPersistenceImpl.getInstance().marshal(m));
        } finally {
            if (oldValue == null) {
                System.clearProperty("drools.dateformat");
            } else {
                System.setProperty("drools.dateformat", oldValue);
            }
        }
    }

    // https://bugzilla.redhat.com/show_bug.cgi?id=1158176
    @Test
    public void testRHSEntryPointInsertion() throws Exception {
        String drl = "package org.test;\n" + ((((((((((((("rule \"AgentBooking Rule\"\n" + "dialect \"mvel\"\n") + "  when\n") + "    agent : Agent( )\n") + "    currentDate : TransactionDate( )\n") + "    Number( eval( intValue >=2 )) from accumulate (\n") + "      resEvent : ReservationEvent( agentId == agent.agentId , resdate == currentDate.eventDate ) from entry-point \"reservationEvent\", \n") + "      count(resEvent))\n") + "  then\n") + "    ReservationThresoldEvent thresoldevent = new ReservationThresoldEvent();\n") + "    thresoldevent.setAgentId( agent.getAgentId() );\n") + "    thresoldevent.setTxEventDate( currentDate.getEventDate() );\n") + "    kcontext.getKnowledgeRuntime().getEntryPoint(\"reservationTraceEvent\").insert( thresoldevent );\n") + "end");
        addModelField("org.test.Agent", "this", "org.test.Agent", TYPE_THIS);
        addModelField("org.test.Agent", "agentId", Integer.class.getName(), TYPE_NUMERIC_INTEGER);
        addModelField("org.test.TransactionDate", "this", "org.test.TransactionDate", TYPE_THIS);
        addModelField("org.test.TransactionDate", "eventDate", Date.class.getName(), TYPE_DATE);
        addModelField("java.lang.Number", "intValue", Integer.class.getName(), TYPE_NUMERIC_INTEGER);
        addModelField("org.test.ReservationEvent", "this", "org.test.ReservationEvent", TYPE_THIS);
        addModelField("org.test.ReservationEvent", "agentId", Integer.class.getName(), TYPE_NUMERIC_INTEGER);
        addModelField("org.test.ReservationEvent", "resdate", Date.class.getName(), TYPE_DATE);
        Mockito.when(dmo.getPackageName()).thenReturn("org.test");
        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals(3, m.lhs.length);
        Assert.assertEquals(3, m.rhs.length);
        Assert.assertTrue(((m.rhs[0]) instanceof FreeFormLine));
        Assert.assertTrue(((m.rhs[1]) instanceof FreeFormLine));
        Assert.assertTrue(((m.rhs[2]) instanceof FreeFormLine));
        assertEqualsIgnoreWhitespace(drl, RuleModelDRLPersistenceImpl.getInstance().marshal(m));
    }

    // https://bugzilla.redhat.com/show_bug.cgi?id=1136100
    @Test
    public void testLHSCustomAccumulateFunctions() throws Exception {
        String drl = "package org.test;\n" + ((((((((("rule \"test\"\n" + "dialect \"mvel\"\n") + "when\n") + "Double( ) from accumulate ( Applicant( $a : age != null ),\n") + "init( double total = 0; ),\n") + "action( total += $a; ),\n") + "reverse( total -= $a; ),\n") + "result( new Double( total ) ) )\n") + "then\n") + "end");
        addModelField(Double.class.getName(), "this", Double.class.getName(), TYPE_THIS);
        addModelField("org.test.Applicant", "this", "org.test.Applicant", TYPE_THIS);
        addModelField("org.test.Applicant", "age", Integer.class.getName(), TYPE_NUMERIC_INTEGER);
        Mockito.when(dmo.getPackageName()).thenReturn("org.test");
        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals(1, m.lhs.length);
        Assert.assertEquals(0, m.rhs.length);
        Assert.assertTrue(((m.lhs[0]) instanceof FromAccumulateCompositeFactPattern));
        final FromAccumulateCompositeFactPattern facfp = ((FromAccumulateCompositeFactPattern) (m.lhs[0]));
        Assert.assertEquals("double total = 0;", facfp.getInitCode());
        Assert.assertEquals("total += $a;", facfp.getActionCode());
        Assert.assertEquals("total -= $a;", facfp.getReverseCode());
        Assert.assertEquals("new Double( total )", facfp.getResultCode());
        Assert.assertNotNull(facfp.getFactPattern());
        final FactPattern fp = facfp.getFactPattern();
        Assert.assertEquals("Double", fp.getFactType());
        Assert.assertEquals(0, fp.getNumberOfConstraints());
        Assert.assertNotNull(facfp.getSourcePattern());
        Assert.assertTrue(((facfp.getSourcePattern()) instanceof FactPattern));
        final FactPattern fsp = ((FactPattern) (facfp.getSourcePattern()));
        Assert.assertEquals("Applicant", fsp.getFactType());
        Assert.assertEquals(1, fsp.getNumberOfConstraints());
        Assert.assertTrue(((fsp.getConstraint(0)) instanceof SingleFieldConstraint));
        final SingleFieldConstraint sfc = ((SingleFieldConstraint) (fsp.getConstraint(0)));
        Assert.assertEquals("$a", sfc.getFieldBinding());
        Assert.assertEquals("Applicant", sfc.getFactType());
        Assert.assertEquals("!= null", sfc.getOperator());
        assertEqualsIgnoreWhitespace(drl, RuleModelDRLPersistenceImpl.getInstance().marshal(m));
    }

    // https://bugzilla.redhat.com/show_bug.cgi?id=1174360
    @Test
    public void testLHSMultipleAllOfTheFollowing() throws Exception {
        String drl = "package org.test;\n" + ((((((((("rule \"test\"\n" + "dialect \"mvel\"\n") + "when\n") + "PhoneNumber(\n") + "( homePhone != null && homePhone matches \"\\\"+9199\\\"\" ) || \n") + "( personalPhone != null && personalPhone matches \"\\\"+9188\\\"\" ) || \n") + "( workPhone != null && workPhone matches \"\\\"+9177\\\"\") \n") + ")\n") + "then\n") + "end\n");
        addModelField("org.test.PhoneNumber", "this", "org.test.PhoneNumber", TYPE_THIS);
        addModelField("org.test.PhoneNumber", "homePhone", String.class.getName(), TYPE_STRING);
        addModelField("org.test.PhoneNumber", "personalPhone", String.class.getName(), TYPE_STRING);
        addModelField("org.test.PhoneNumber", "workPhone", String.class.getName(), TYPE_STRING);
        Mockito.when(dmo.getPackageName()).thenReturn("org.test");
        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals(1, m.lhs.length);
        Assert.assertEquals(0, m.rhs.length);
        // Check Pattern
        Assert.assertTrue(((m.lhs[0]) instanceof FactPattern));
        final FactPattern fp = ((FactPattern) (m.lhs[0]));
        Assert.assertEquals("PhoneNumber", fp.getFactType());
        Assert.assertEquals(1, fp.getNumberOfConstraints());
        Assert.assertTrue(((fp.getConstraint(0)) instanceof CompositeFieldConstraint));
        final CompositeFieldConstraint cfc = ((CompositeFieldConstraint) (fp.getConstraint(0)));
        Assert.assertEquals("||", cfc.getCompositeJunctionType());
        Assert.assertEquals(3, cfc.getNumberOfConstraints());
        Assert.assertTrue(((cfc.getConstraint(0)) instanceof CompositeFieldConstraint));
        Assert.assertTrue(((cfc.getConstraint(1)) instanceof CompositeFieldConstraint));
        Assert.assertTrue(((cfc.getConstraint(2)) instanceof CompositeFieldConstraint));
        // Check first composite field constraint
        final CompositeFieldConstraint cfc_0 = ((CompositeFieldConstraint) (cfc.getConstraint(0)));
        Assert.assertEquals("&&", cfc_0.getCompositeJunctionType());
        Assert.assertEquals(2, cfc_0.getNumberOfConstraints());
        Assert.assertTrue(((cfc_0.getConstraint(0)) instanceof SingleFieldConstraint));
        Assert.assertTrue(((cfc_0.getConstraint(1)) instanceof SingleFieldConstraint));
        final SingleFieldConstraint sfc_0_0 = ((SingleFieldConstraint) (cfc_0.getConstraint(0)));
        Assert.assertEquals("PhoneNumber", sfc_0_0.getFactType());
        Assert.assertEquals("homePhone", sfc_0_0.getFieldName());
        Assert.assertEquals(TYPE_STRING, sfc_0_0.getFieldType());
        Assert.assertEquals("!= null", sfc_0_0.getOperator());
        Assert.assertNull(sfc_0_0.getValue());
        final SingleFieldConstraint sfc_0_1 = ((SingleFieldConstraint) (cfc_0.getConstraint(1)));
        Assert.assertEquals("PhoneNumber", sfc_0_1.getFactType());
        Assert.assertEquals("homePhone", sfc_0_1.getFieldName());
        Assert.assertEquals(TYPE_STRING, sfc_0_1.getFieldType());
        Assert.assertEquals("matches", sfc_0_1.getOperator());
        Assert.assertEquals("\\\"+9199\\\"", sfc_0_1.getValue());
        // Check second composite field constraint
        final CompositeFieldConstraint cfc_1 = ((CompositeFieldConstraint) (cfc.getConstraint(1)));
        Assert.assertEquals("&&", cfc_1.getCompositeJunctionType());
        Assert.assertEquals(2, cfc_1.getNumberOfConstraints());
        Assert.assertTrue(((cfc_1.getConstraint(0)) instanceof SingleFieldConstraint));
        Assert.assertTrue(((cfc_1.getConstraint(1)) instanceof SingleFieldConstraint));
        final SingleFieldConstraint sfc_1_0 = ((SingleFieldConstraint) (cfc_1.getConstraint(0)));
        Assert.assertEquals("PhoneNumber", sfc_1_0.getFactType());
        Assert.assertEquals("personalPhone", sfc_1_0.getFieldName());
        Assert.assertEquals(TYPE_STRING, sfc_1_0.getFieldType());
        Assert.assertEquals("!= null", sfc_1_0.getOperator());
        Assert.assertNull(sfc_1_0.getValue());
        final SingleFieldConstraint sfc_1_1 = ((SingleFieldConstraint) (cfc_1.getConstraint(1)));
        Assert.assertEquals("PhoneNumber", sfc_1_1.getFactType());
        Assert.assertEquals("personalPhone", sfc_1_1.getFieldName());
        Assert.assertEquals(TYPE_STRING, sfc_1_1.getFieldType());
        Assert.assertEquals("matches", sfc_1_1.getOperator());
        Assert.assertEquals("\\\"+9188\\\"", sfc_1_1.getValue());
        // Check third composite field constraint
        final CompositeFieldConstraint cfc_2 = ((CompositeFieldConstraint) (cfc.getConstraint(2)));
        Assert.assertEquals("&&", cfc_2.getCompositeJunctionType());
        Assert.assertEquals(2, cfc_2.getNumberOfConstraints());
        Assert.assertTrue(((cfc_2.getConstraint(0)) instanceof SingleFieldConstraint));
        Assert.assertTrue(((cfc_2.getConstraint(1)) instanceof SingleFieldConstraint));
        final SingleFieldConstraint sfc_2_0 = ((SingleFieldConstraint) (cfc_2.getConstraint(0)));
        Assert.assertEquals("PhoneNumber", sfc_2_0.getFactType());
        Assert.assertEquals("workPhone", sfc_2_0.getFieldName());
        Assert.assertEquals(TYPE_STRING, sfc_2_0.getFieldType());
        Assert.assertEquals("!= null", sfc_2_0.getOperator());
        Assert.assertNull(sfc_2_0.getValue());
        final SingleFieldConstraint sfc_2_1 = ((SingleFieldConstraint) (cfc_2.getConstraint(1)));
        Assert.assertEquals("PhoneNumber", sfc_2_1.getFactType());
        Assert.assertEquals("workPhone", sfc_2_1.getFieldName());
        Assert.assertEquals(TYPE_STRING, sfc_2_1.getFieldType());
        Assert.assertEquals("matches", sfc_2_1.getOperator());
        Assert.assertEquals("\\\"+9177\\\"", sfc_2_1.getValue());
        // Check round-trip
        assertEqualsIgnoreWhitespace(drl, RuleModelDRLPersistenceImpl.getInstance().marshal(m));
    }

    // https://bugzilla.redhat.com/show_bug.cgi?id=1175333
    @Test
    public void testRHSFreeFormDRL() throws Exception {
        String drl = "package org.test;\n" + ((((((((((("rule \"Validate Down Payment\"\n" + "dialect \"mvel\"\n") + "ruleflow-group \"validation\"\n") + "when\n") + "  property : Property( )\n") + "  Application( downPayment < 0 || downPayment > property.price )\n") + "then\n") + "  ValidationError fact0 = new ValidationError();\n") + "  fact0.setCause( \"Down payment can\'t be negative or larger than property value\" );\n") + "  insert( fact0 );\n") + "  System.out.println(\"Executed Rule: \" + drools.getRule().getName() );\n") + "end\n");
        addModelField("org.test.Property", "this", "org.test.Property", TYPE_THIS);
        addModelField("org.test.Property", "price", Integer.class.getName(), TYPE_NUMERIC_INTEGER);
        addModelField("org.test.Application", "this", "org.test.Application", TYPE_THIS);
        addModelField("org.test.Application", "downPayment", Integer.class.getName(), TYPE_NUMERIC_INTEGER);
        addModelField("org.test.ValidationError", "this", "org.test.ValidationError", TYPE_THIS);
        Mockito.when(dmo.getPackageName()).thenReturn("org.test");
        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        // Check round-trip
        assertEqualsIgnoreWhitespace(drl, RuleModelDRLPersistenceImpl.getInstance().marshal(m));
    }

    // https://bugzilla.redhat.com/show_bug.cgi?id=1189930
    // Formatted as generated from a Guided Rule in the Workbench
    @Test
    public void testBigDecimal1() throws Exception {
        String drl = "package org.test;\n" + ((((((((((("import java.lang.Number;\n" + "import java.math.BigDecimal;\n") + "import java.util.Calendar;\n") + "rule \"BigDecimalRule\"\n") + "  dialect \"java\"\n") + "when\n") + "  $bd : BigDecimal( )\n") + "then\n") + "  LastRunInformation lastRun = new LastRunInformation();\n") + "  lastRun.setLastNumber( $bd );\n") + "  insert( lastRun );\n") + "end");
        addModelField("org.test.LastRunInformation", "this", "org.test.LastRunInformation", TYPE_THIS);
        addModelField("org.test.LastRunInformation", "lastNumber", BigDecimal.class.getName(), TYPE_NUMERIC_BIGDECIMAL);
        Mockito.when(dmo.getPackageName()).thenReturn("org.test");
        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals(1, m.lhs.length);
        final IPattern p = m.lhs[0];
        Assert.assertTrue((p instanceof FactPattern));
        final FactPattern fp = ((FactPattern) (p));
        Assert.assertEquals("$bd", fp.getBoundName());
        Assert.assertEquals("BigDecimal", fp.getFactType());
        Assert.assertEquals(1, m.rhs.length);
        final IAction a = m.rhs[0];
        Assert.assertTrue((a instanceof ActionInsertFact));
        final ActionInsertFact aif = ((ActionInsertFact) (a));
        Assert.assertEquals("lastRun", aif.getBoundName());
        Assert.assertEquals("LastRunInformation", aif.getFactType());
        Assert.assertEquals(1, aif.getFieldValues().length);
        final ActionFieldValue afv0 = aif.getFieldValues()[0];
        Assert.assertEquals("lastNumber", afv0.getField());
        Assert.assertEquals(FieldNatureType.TYPE_VARIABLE, afv0.getNature());
        Assert.assertEquals("=$bd", afv0.getValue());
        // Check round-trip
        assertEqualsIgnoreWhitespace(drl, RuleModelDRLPersistenceImpl.getInstance().marshal(m));
    }

    // https://bugzilla.redhat.com/show_bug.cgi?id=1189930
    @Test
    public void testBigDecimal2() throws Exception {
        String drl = "package org.test;\n" + ((((((((((("import java.lang.Number;\n" + "import java.math.BigDecimal;\n") + "import java.util.Calendar;\n") + "rule \"BigDecimalRule\"\n") + "  dialect \"java\"\n") + "when\n") + "  $bd : BigDecimal( )\n") + "then\n") + "  LastRunInformation lastRun = new LastRunInformation();\n") + "  lastRun.setLastNumber($bd);\n") + "  insert(lastRun);\n") + "end");
        addModelField("org.test.LastRunInformation", "this", "org.test.LastRunInformation", TYPE_THIS);
        addModelField("org.test.LastRunInformation", "lastNumber", BigDecimal.class.getName(), TYPE_NUMERIC_BIGDECIMAL);
        Mockito.when(dmo.getPackageName()).thenReturn("org.test");
        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals(1, m.lhs.length);
        final IPattern p = m.lhs[0];
        Assert.assertTrue((p instanceof FactPattern));
        final FactPattern fp = ((FactPattern) (p));
        Assert.assertEquals("$bd", fp.getBoundName());
        Assert.assertEquals("BigDecimal", fp.getFactType());
        Assert.assertEquals(1, m.rhs.length);
        final IAction a = m.rhs[0];
        Assert.assertTrue((a instanceof ActionInsertFact));
        final ActionInsertFact aif = ((ActionInsertFact) (a));
        Assert.assertEquals("lastRun", aif.getBoundName());
        Assert.assertEquals("LastRunInformation", aif.getFactType());
        Assert.assertEquals(1, aif.getFieldValues().length);
        final ActionFieldValue afv0 = aif.getFieldValues()[0];
        Assert.assertEquals("lastNumber", afv0.getField());
        Assert.assertEquals(FieldNatureType.TYPE_VARIABLE, afv0.getNature());
        Assert.assertEquals("=$bd", afv0.getValue());
        // Check round-trip
        assertEqualsIgnoreWhitespace(drl, RuleModelDRLPersistenceImpl.getInstance().marshal(m));
    }

    // https://bugzilla.redhat.com/show_bug.cgi?id=1189930
    @Test
    public void testBigDecimal3() throws Exception {
        String drl = "package org.test;\n" + (((((((((((("import java.lang.Number;\n" + "import java.math.BigDecimal;\n") + "import java.util.Calendar;\n") + "rule \"BigDecimalRule\"\n") + "  dialect \"java\"\n") + "when\n") + "  $bd : BigDecimal( )\n") + "then\n") + "  LastRunInformation lastRun = new LastRunInformation();\n") + "  lastRun.setLastNumber($bd);\n") + "\n") + "  insert(lastRun);\n") + "end");
        addModelField("org.test.LastRunInformation", "this", "org.test.LastRunInformation", TYPE_THIS);
        addModelField("org.test.LastRunInformation", "lastNumber", BigDecimal.class.getName(), TYPE_NUMERIC_BIGDECIMAL);
        Mockito.when(dmo.getPackageName()).thenReturn("org.test");
        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals(1, m.lhs.length);
        final IPattern p = m.lhs[0];
        Assert.assertTrue((p instanceof FactPattern));
        final FactPattern fp = ((FactPattern) (p));
        Assert.assertEquals("$bd", fp.getBoundName());
        Assert.assertEquals("BigDecimal", fp.getFactType());
        Assert.assertEquals(1, m.rhs.length);
        final IAction a = m.rhs[0];
        Assert.assertTrue((a instanceof ActionInsertFact));
        final ActionInsertFact aif = ((ActionInsertFact) (a));
        Assert.assertEquals("lastRun", aif.getBoundName());
        Assert.assertEquals("LastRunInformation", aif.getFactType());
        Assert.assertEquals(1, aif.getFieldValues().length);
        final ActionFieldValue afv0 = aif.getFieldValues()[0];
        Assert.assertEquals("lastNumber", afv0.getField());
        Assert.assertEquals(FieldNatureType.TYPE_VARIABLE, afv0.getNature());
        Assert.assertEquals("=$bd", afv0.getValue());
        // Check round-trip
        assertEqualsIgnoreWhitespace(drl, RuleModelDRLPersistenceImpl.getInstance().marshal(m));
    }

    // https://bugzilla.redhat.com/show_bug.cgi?id=1191737
    @Test
    public void testMultipleFromKeywords() throws Exception {
        String drl = "package org.test;\n" + (((((((("rule \"ToyWithoutName \"\n" + "dialect \"java\"\n") + "when\n") + "  $father: Father()\n") + "  $kid: Kid() from $father.kids\n") + "  $toy: Toy(name == null) from $kid.toys\n") + "then\n") + "  System.out.println(\"blabla\");\n") + "end");
        addModelField("org.test.Father", "this", "org.test.Father", TYPE_THIS);
        addModelField("org.test.Father", "kids", "org.test.Kid", TYPE_COLLECTION);
        addModelField("org.test.Kid", "this", "org.test.Kid", TYPE_THIS);
        addModelField("org.test.Kid", "toys", "org.test.Toy", TYPE_COLLECTION);
        addModelField("org.test.Toy", "this", "org.test.Toy", TYPE_THIS);
        addModelField("org.test.Toy", "name", "java.lang.String", TYPE_STRING);
        Mockito.when(dmo.getPackageName()).thenReturn("org.test");
        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals(3, m.lhs.length);
        final IPattern p0 = m.lhs[0];
        Assert.assertTrue((p0 instanceof FactPattern));
        final FactPattern fp0 = ((FactPattern) (p0));
        Assert.assertEquals("$father", fp0.getBoundName());
        Assert.assertEquals("Father", fp0.getFactType());
        final IPattern p1 = m.lhs[1];
        Assert.assertTrue((p1 instanceof FromCompositeFactPattern));
        final FromCompositeFactPattern fp1 = ((FromCompositeFactPattern) (p1));
        Assert.assertEquals("$kid", fp1.getFactPattern().getBoundName());
        Assert.assertEquals("Kid", fp1.getFactType());
        final IPattern p2 = m.lhs[2];
        Assert.assertTrue((p2 instanceof FromCompositeFactPattern));
        final FromCompositeFactPattern fp2 = ((FromCompositeFactPattern) (p2));
        Assert.assertEquals("$toy", fp2.getFactPattern().getBoundName());
        Assert.assertEquals("Toy", fp2.getFactType());
        Assert.assertEquals(1, m.rhs.length);
        final IAction a = m.rhs[0];
        Assert.assertTrue((a instanceof FreeFormLine));
        final FreeFormLine affl = ((FreeFormLine) (a));
        Assert.assertEquals("System.out.println(\"blabla\");", affl.getText());
        // Check round-trip
        assertEqualsIgnoreWhitespace(drl, RuleModelDRLPersistenceImpl.getInstance().marshal(m));
    }

    // https://issues.jboss.org/browse/DROOLS-713
    @Test
    public void testLHSFormula() throws Exception {
        String drl = "package org.test;\n" + ((((((((("rule \"MyRule\"\n" + "dialect \"java\"\n") + "agenda-group \"MyGroup\"\n") + "salience 900\n") + "when\n") + "  $bundle : MyClass( $protocolSequence : protocolSequence )\n") + "  eval( $protocolSequence != null )\n") + "  $followupBundle : MyClass( protocolSequence == ( $protocolSequence + 1 ) )\n") + "then\n") + "end");
        addModelField("org.test.MyClass", "this", "org.test.MyClass", TYPE_THIS);
        addModelField("org.test.MyClass", "protocolSequence", Integer.class.getName(), TYPE_NUMERIC_INTEGER);
        Mockito.when(dmo.getPackageName()).thenReturn("org.test");
        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals(3, m.lhs.length);
        final IPattern p0 = m.lhs[0];
        Assert.assertTrue((p0 instanceof FactPattern));
        final FactPattern fp0 = ((FactPattern) (p0));
        Assert.assertEquals("$bundle", fp0.getBoundName());
        Assert.assertEquals("MyClass", fp0.getFactType());
        final IPattern p1 = m.lhs[1];
        Assert.assertTrue((p1 instanceof FreeFormLine));
        final FreeFormLine ffl1 = ((FreeFormLine) (p1));
        Assert.assertEquals("eval( $protocolSequence != null )", ffl1.getText());
        final IPattern p2 = m.lhs[2];
        Assert.assertTrue((p2 instanceof FactPattern));
        final FactPattern fp2 = ((FactPattern) (p2));
        Assert.assertEquals("$followupBundle", fp2.getBoundName());
        Assert.assertEquals("MyClass", fp2.getFactType());
        Assert.assertEquals(1, fp2.getNumberOfConstraints());
        Assert.assertTrue(((fp2.getConstraint(0)) instanceof SingleFieldConstraint));
        final SingleFieldConstraint sfc1 = ((SingleFieldConstraint) (fp2.getConstraint(0)));
        Assert.assertEquals("MyClass", sfc1.getFactType());
        Assert.assertEquals("protocolSequence", sfc1.getFieldName());
        Assert.assertEquals(TYPE_NUMERIC_INTEGER, sfc1.getFieldType());
        Assert.assertEquals("==", sfc1.getOperator());
        Assert.assertEquals("$protocolSequence + 1", sfc1.getValue());
        Assert.assertEquals(TYPE_RET_VALUE, sfc1.getConstraintValueType());
        // Check round-trip
        assertEqualsIgnoreWhitespace(drl, RuleModelDRLPersistenceImpl.getInstance().marshal(m));
    }

    // https://bugzilla.redhat.com/show_bug.cgi?id=1127303
    @Test
    public void testRHSChainedMethodCalls1() throws Exception {
        String drl = "package org.test;\n" + (((((("rule \"MyRule\"\n" + "dialect \"mvel\"\n") + "when\n") + "  Person( $n : name )\n") + "then\n") + "  $n.toUpperCase().indexOf(\"S\", 1);\n") + "end");
        addModelField("org.test.Person", "this", "org.test.Person", TYPE_THIS);
        addModelField("org.test.Person", "name", String.class.getName(), TYPE_STRING);
        Mockito.when(dmo.getPackageName()).thenReturn("org.test");
        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals(1, m.lhs.length);
        final IPattern p0 = m.lhs[0];
        Assert.assertTrue((p0 instanceof FactPattern));
        final FactPattern fp0 = ((FactPattern) (p0));
        Assert.assertEquals("Person", fp0.getFactType());
        Assert.assertEquals(1, fp0.getNumberOfConstraints());
        Assert.assertTrue(((fp0.getConstraint(0)) instanceof SingleFieldConstraint));
        final SingleFieldConstraint sfc1 = ((SingleFieldConstraint) (fp0.getConstraint(0)));
        Assert.assertEquals("Person", sfc1.getFactType());
        Assert.assertEquals("name", sfc1.getFieldName());
        Assert.assertEquals(TYPE_STRING, sfc1.getFieldType());
        Assert.assertEquals(1, m.rhs.length);
        final IAction a0 = m.rhs[0];
        Assert.assertTrue((a0 instanceof FreeFormLine));
        final FreeFormLine ffl1 = ((FreeFormLine) (a0));
        Assert.assertEquals("$n.toUpperCase().indexOf(\"S\", 1);", ffl1.getText());
        // Check round-trip
        assertEqualsIgnoreWhitespace(drl, RuleModelDRLPersistenceImpl.getInstance().marshal(m));
    }

    // https://bugzilla.redhat.com/show_bug.cgi?id=1127303
    @Test
    public void testRHSChainedMethodCalls2() throws Exception {
        String drl = "package org.test;\n" + (((((("rule \"MyRule\"\n" + "dialect \"mvel\"\n") + "when\n") + "  Person( $n : name )\n") + "then\n") + "  $n.toUpperCase().indexOf(\".\", 1);\n") + "end");
        addModelField("org.test.Person", "this", "org.test.Person", TYPE_THIS);
        addModelField("org.test.Person", "name", String.class.getName(), TYPE_STRING);
        Mockito.when(dmo.getPackageName()).thenReturn("org.test");
        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals(1, m.lhs.length);
        final IPattern p0 = m.lhs[0];
        Assert.assertTrue((p0 instanceof FactPattern));
        final FactPattern fp0 = ((FactPattern) (p0));
        Assert.assertEquals("Person", fp0.getFactType());
        Assert.assertEquals(1, fp0.getNumberOfConstraints());
        Assert.assertTrue(((fp0.getConstraint(0)) instanceof SingleFieldConstraint));
        final SingleFieldConstraint sfc1 = ((SingleFieldConstraint) (fp0.getConstraint(0)));
        Assert.assertEquals("Person", sfc1.getFactType());
        Assert.assertEquals("name", sfc1.getFieldName());
        Assert.assertEquals(TYPE_STRING, sfc1.getFieldType());
        Assert.assertEquals(1, m.rhs.length);
        final IAction a0 = m.rhs[0];
        Assert.assertTrue((a0 instanceof FreeFormLine));
        final FreeFormLine ffl1 = ((FreeFormLine) (a0));
        Assert.assertEquals("$n.toUpperCase().indexOf(\".\", 1);", ffl1.getText());
        // Check round-trip
        assertEqualsIgnoreWhitespace(drl, RuleModelDRLPersistenceImpl.getInstance().marshal(m));
    }

    // https://bugzilla.redhat.com/show_bug.cgi?id=1127303
    @Test
    public void testRHSChainedMethodCalls3() throws Exception {
        String drl = "package org.test;\n" + (((((("rule \"MyRule\"\n" + "dialect \"mvel\"\n") + "when\n") + "  Person( $n : name )\n") + "then\n") + "  $n.toUpperCase().indexOf(\"(\", 1);\n") + "end");
        addModelField("org.test.Person", "this", "org.test.Person", TYPE_THIS);
        addModelField("org.test.Person", "name", String.class.getName(), TYPE_STRING);
        Mockito.when(dmo.getPackageName()).thenReturn("org.test");
        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals(1, m.lhs.length);
        final IPattern p0 = m.lhs[0];
        Assert.assertTrue((p0 instanceof FactPattern));
        final FactPattern fp0 = ((FactPattern) (p0));
        Assert.assertEquals("Person", fp0.getFactType());
        Assert.assertEquals(1, fp0.getNumberOfConstraints());
        Assert.assertTrue(((fp0.getConstraint(0)) instanceof SingleFieldConstraint));
        final SingleFieldConstraint sfc1 = ((SingleFieldConstraint) (fp0.getConstraint(0)));
        Assert.assertEquals("Person", sfc1.getFactType());
        Assert.assertEquals("name", sfc1.getFieldName());
        Assert.assertEquals(TYPE_STRING, sfc1.getFieldType());
        Assert.assertEquals(1, m.rhs.length);
        final IAction a0 = m.rhs[0];
        Assert.assertTrue((a0 instanceof FreeFormLine));
        final FreeFormLine ffl1 = ((FreeFormLine) (a0));
        Assert.assertEquals("$n.toUpperCase().indexOf(\"(\", 1);", ffl1.getText());
        // Check round-trip
        assertEqualsIgnoreWhitespace(drl, RuleModelDRLPersistenceImpl.getInstance().marshal(m));
    }

    // https://bugzilla.redhat.com/show_bug.cgi?id=1127303
    @Test
    public void testRHSChainedMethodCalls4() throws Exception {
        String drl = "package org.test;\n" + (((((("rule \"MyRule\"\n" + "dialect \"mvel\"\n") + "when\n") + "  Person( $n : name )\n") + "then\n") + "  $n.toUpperCase().indexOf(\"\\\").\", 1);\n") + "end");
        addModelField("org.test.Person", "this", "org.test.Person", TYPE_THIS);
        addModelField("org.test.Person", "name", String.class.getName(), TYPE_STRING);
        Mockito.when(dmo.getPackageName()).thenReturn("org.test");
        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals(1, m.lhs.length);
        final IPattern p0 = m.lhs[0];
        Assert.assertTrue((p0 instanceof FactPattern));
        final FactPattern fp0 = ((FactPattern) (p0));
        Assert.assertEquals("Person", fp0.getFactType());
        Assert.assertEquals(1, fp0.getNumberOfConstraints());
        Assert.assertTrue(((fp0.getConstraint(0)) instanceof SingleFieldConstraint));
        final SingleFieldConstraint sfc1 = ((SingleFieldConstraint) (fp0.getConstraint(0)));
        Assert.assertEquals("Person", sfc1.getFactType());
        Assert.assertEquals("name", sfc1.getFieldName());
        Assert.assertEquals(TYPE_STRING, sfc1.getFieldType());
        Assert.assertEquals(1, m.rhs.length);
        final IAction a0 = m.rhs[0];
        Assert.assertTrue((a0 instanceof FreeFormLine));
        final FreeFormLine ffl1 = ((FreeFormLine) (a0));
        Assert.assertEquals("$n.toUpperCase().indexOf(\"\\\").\", 1);", ffl1.getText());
        // Check round-trip
        assertEqualsIgnoreWhitespace(drl, RuleModelDRLPersistenceImpl.getInstance().marshal(m));
    }

    // https://issues.jboss.org/browse/DROOLS-715
    @Test
    public void testLHSValidLiteralFieldName() throws Exception {
        String drl = "package org.test;\n" + ((((("rule \"MyRule\"\n" + "dialect \"mvel\"\n") + "when\n") + "  MyClass( valid == true )\n") + "then\n") + "end");
        addModelField("org.test.MyClass", "this", "org.test.MyClass", TYPE_THIS);
        addModelField("org.test.MyClass", "valid", Boolean.class.getName(), TYPE_BOOLEAN);
        Mockito.when(dmo.getPackageName()).thenReturn("org.test");
        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals(1, m.lhs.length);
        final IPattern p0 = m.lhs[0];
        Assert.assertTrue((p0 instanceof FactPattern));
        final FactPattern fp0 = ((FactPattern) (p0));
        Assert.assertEquals("MyClass", fp0.getFactType());
        Assert.assertEquals(1, fp0.getNumberOfConstraints());
        Assert.assertTrue(((fp0.getConstraint(0)) instanceof SingleFieldConstraint));
        final SingleFieldConstraint sfc1 = ((SingleFieldConstraint) (fp0.getConstraint(0)));
        Assert.assertEquals("MyClass", sfc1.getFactType());
        Assert.assertEquals("valid", sfc1.getFieldName());
        Assert.assertEquals(TYPE_BOOLEAN, sfc1.getFieldType());
        Assert.assertEquals("==", sfc1.getOperator());
        Assert.assertEquals("true", sfc1.getValue());
        Assert.assertEquals(TYPE_ENUM, sfc1.getConstraintValueType());
        // Check round-trip
        assertEqualsIgnoreWhitespace(drl, RuleModelDRLPersistenceImpl.getInstance().marshal(m));
    }

    // https://bugzilla.redhat.com/show_bug.cgi?id=1218308
    @Test
    public void testInvalidFromSyntax() throws Exception {
        String drl = "rule \"test\"\n" + ((((("    dialect \"mvel\"\n" + "    when\n") + "    (obj : MyClass( ) from my.package)\n") + "    then\n") + "    System.out.println(\"Test\")\n") + "    end");
        Mockito.when(dmo.getPackageName()).thenReturn("org.test");
        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals(1, m.lhs.length);
        final IPattern p0 = m.lhs[0];
        Assert.assertTrue((p0 instanceof FromCompositeFactPattern));
        final FromCompositeFactPattern fp0 = ((FromCompositeFactPattern) (p0));
        Assert.assertEquals("MyClass", fp0.getFactType());
        final FactPattern fp1 = fp0.getFactPattern();
        Assert.assertEquals("MyClass", fp1.getFactType());
        Assert.assertEquals(0, fp1.getNumberOfConstraints());
        final ExpressionFormLine efl = fp0.getExpression();
        Assert.assertNotNull(efl);
        Assert.assertEquals(2, efl.getParts().size());
        Assert.assertTrue(((efl.getParts().get(0)) instanceof ExpressionVariable));
        final ExpressionVariable ev = ((ExpressionVariable) (efl.getParts().get(0)));
        Assert.assertEquals("my", ev.getName());
        Assert.assertTrue(((efl.getParts().get(1)) instanceof ExpressionText));
        final ExpressionText et = ((ExpressionText) (efl.getParts().get(1)));
        Assert.assertEquals("package", et.getName());
        // Check round-trip
        assertEqualsIgnoreWhitespace(drl, RuleModelDRLPersistenceImpl.getInstance().marshal(m));
    }

    // https://bugzilla.redhat.com/show_bug.cgi?id=1218308
    @Test
    public void testValidSyntaxNonImportedType() throws Exception {
        String drl = "rule \"x\"\n" + (((((("dialect \"mvel\"\n" + "when\n") + "  var : NotImported( )\n") + "  OtherType( field != var.field )\n") + "  MyType( ) from var.collectionField\n") + "then\n") + "end");
        addModelField("org.test.OtherType", "this", "org.test.OtherType", TYPE_THIS);
        addModelField("org.test.OtherType", "field", String.class.getName(), TYPE_STRING);
        addModelField("org.test.MyType", "this", "org.test.MyType", TYPE_THIS);
        Mockito.when(dmo.getPackageName()).thenReturn("org.test");
        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals(3, m.lhs.length);
        final IPattern p0 = m.lhs[0];
        Assert.assertTrue((p0 instanceof FactPattern));
        final FactPattern fp0 = ((FactPattern) (p0));
        Assert.assertEquals("NotImported", fp0.getFactType());
        Assert.assertEquals("var", fp0.getBoundName());
        Assert.assertEquals(0, fp0.getNumberOfConstraints());
        final IPattern p1 = m.lhs[1];
        Assert.assertTrue((p1 instanceof FactPattern));
        final FactPattern fp1 = ((FactPattern) (p1));
        Assert.assertEquals("OtherType", fp1.getFactType());
        Assert.assertEquals(1, fp1.getNumberOfConstraints());
        Assert.assertTrue(((fp1.getConstraint(0)) instanceof SingleFieldConstraint));
        final SingleFieldConstraint fp1sfc0 = ((SingleFieldConstraint) (fp1.getConstraint(0)));
        Assert.assertEquals("OtherType", fp1sfc0.getFactType());
        Assert.assertEquals("field", fp1sfc0.getFieldName());
        Assert.assertEquals("!=", fp1sfc0.getOperator());
        Assert.assertEquals(2, fp1sfc0.getExpressionValue().getParts().size());
        Assert.assertTrue(((fp1sfc0.getExpressionValue().getParts().get(0)) instanceof ExpressionText));
        final ExpressionText fp1sfc0e0 = ((ExpressionText) (fp1sfc0.getExpressionValue().getParts().get(0)));
        Assert.assertEquals("var", fp1sfc0e0.getName());
        Assert.assertTrue(((fp1sfc0.getExpressionValue().getParts().get(1)) instanceof ExpressionText));
        final ExpressionText fp1sfc0e1 = ((ExpressionText) (fp1sfc0.getExpressionValue().getParts().get(1)));
        Assert.assertEquals("field", fp1sfc0e1.getName());
        final IPattern p2 = m.lhs[2];
        Assert.assertTrue((p2 instanceof FromCompositeFactPattern));
        final FromCompositeFactPattern fp2 = ((FromCompositeFactPattern) (p2));
        Assert.assertEquals("MyType", fp2.getFactType());
        Assert.assertEquals(2, fp2.getExpression().getParts().size());
        Assert.assertTrue(((fp2.getExpression().getParts().get(0)) instanceof ExpressionVariable));
        final ExpressionVariable fp2e0 = ((ExpressionVariable) (fp2.getExpression().getParts().get(0)));
        Assert.assertEquals("var", fp2e0.getName());
        Assert.assertTrue(((fp2.getExpression().getParts().get(1)) instanceof ExpressionText));
        final ExpressionText fp2e1 = ((ExpressionText) (fp2.getExpression().getParts().get(1)));
        Assert.assertEquals("collectionField", fp2e1.getName());
        Assert.assertEquals(0, m.rhs.length);
        // Check round-trip
        assertEqualsIgnoreWhitespace(drl, RuleModelDRLPersistenceImpl.getInstance().marshal(m));
    }

    // https://bugzilla.redhat.com/show_bug.cgi?id=1218308
    @Test
    public void testInvalidSyntax1() throws Exception {
        String drl = "rule \"test\"\n" + (((("dialect \"mvel\"\n" + "when\n") + "  Smurf( flange \n") + "then\n") + "end");
        Mockito.when(dmo.getPackageName()).thenReturn("org.test");
        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals(1, m.lhs.length);
        final IPattern p0 = m.lhs[0];
        Assert.assertTrue((p0 instanceof FreeFormLine));
        final FreeFormLine ffl = ((FreeFormLine) (p0));
        Assert.assertEquals("Smurf( flange", ffl.getText());
        Assert.assertEquals(0, m.rhs.length);
        // Check round-trip
        assertEqualsIgnoreWhitespace(drl, RuleModelDRLPersistenceImpl.getInstance().marshal(m));
    }

    // https://bugzilla.redhat.com/show_bug.cgi?id=1234640
    @Test
    public void testStringFieldsWithDoubleForwardSlashes() throws Exception {
        String drl = "rule \"test\"\n" + (((("dialect \"mvel\"\n" + "when\n") + "  MyType( url == \"http://www.redhat.com\" )\n") + "then\n") + "end");
        addModelField("org.test.MyType", "this", "org.test.MyType", TYPE_THIS);
        addModelField("org.test.MyType", "url", String.class.getName(), TYPE_STRING);
        Mockito.when(dmo.getPackageName()).thenReturn("org.test");
        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals(1, m.lhs.length);
        final IPattern p0 = m.lhs[0];
        Assert.assertTrue((p0 instanceof FactPattern));
        final FactPattern fp0 = ((FactPattern) (p0));
        Assert.assertEquals("MyType", fp0.getFactType());
        Assert.assertEquals(1, fp0.getNumberOfConstraints());
        Assert.assertTrue(((fp0.getConstraint(0)) instanceof SingleFieldConstraint));
        final SingleFieldConstraint fp0sfc0 = ((SingleFieldConstraint) (fp0.getConstraint(0)));
        Assert.assertEquals("MyType", fp0sfc0.getFactType());
        Assert.assertEquals("url", fp0sfc0.getFieldName());
        Assert.assertEquals("==", fp0sfc0.getOperator());
        Assert.assertEquals(TYPE_STRING, fp0sfc0.getFieldType());
        Assert.assertEquals("http://www.redhat.com", fp0sfc0.getValue());
        Assert.assertEquals(0, m.rhs.length);
        // Check round-trip
        assertEqualsIgnoreWhitespace(drl, RuleModelDRLPersistenceImpl.getInstance().marshal(m));
    }

    @Test
    public void testStringReplaceExpression() throws Exception {
        // https://bugzilla.redhat.com/show_bug.cgi?id=1264321
        String drl = "rule \"Replace_condition_Issue\"\n" + (((("dialect \"mvel\"\n" + "when\n") + "  MyType( myString.replace(\"a\",\"b\"))\n") + "then\n") + "end");
        addModelField("org.test.MyType", "this", "org.test.MyType", TYPE_THIS);
        addModelField("org.test.MyType", "myString", String.class.getName(), TYPE_STRING);
        addMethodInformation("java.lang.String", "replace", new ArrayList<String>() {
            {
                add("String");
                add("String");
            }
        }, "java.lang.String", null, TYPE_STRING);
        Mockito.when(dmo.getPackageName()).thenReturn("org.test");
        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals(1, m.lhs.length);
        final IPattern p0 = m.lhs[0];
        Assert.assertTrue((p0 instanceof FactPattern));
        final FactPattern fp0 = ((FactPattern) (p0));
        Assert.assertEquals("MyType", fp0.getFactType());
        Assert.assertEquals(1, fp0.getNumberOfConstraints());
        Assert.assertTrue(((fp0.getConstraint(0)) instanceof SingleFieldConstraintEBLeftSide));
        final SingleFieldConstraintEBLeftSide fp0sfc0 = ((SingleFieldConstraintEBLeftSide) (fp0.getConstraint(0)));
        Assert.assertEquals(3, fp0sfc0.getExpressionLeftSide().getParts().size());
        Assert.assertTrue(((fp0sfc0.getExpressionLeftSide().getParts().get(0)) instanceof ExpressionUnboundFact));
        final ExpressionUnboundFact ep0 = ((ExpressionUnboundFact) (fp0sfc0.getExpressionLeftSide().getParts().get(0)));
        Assert.assertEquals("MyType", ep0.getFactType());
        Assert.assertTrue(((fp0sfc0.getExpressionLeftSide().getParts().get(1)) instanceof ExpressionField));
        final ExpressionField ep1 = ((ExpressionField) (fp0sfc0.getExpressionLeftSide().getParts().get(1)));
        Assert.assertEquals("myString", ep1.getName());
        Assert.assertTrue(((fp0sfc0.getExpressionLeftSide().getParts().get(2)) instanceof ExpressionMethod));
        final ExpressionMethod ep2 = ((ExpressionMethod) (fp0sfc0.getExpressionLeftSide().getParts().get(2)));
        Assert.assertEquals("replace", ep2.getName());
        Assert.assertEquals(2, ep2.getParams().size());
        final ExpressionFormLine param0 = ep2.getParams().get(new ExpressionMethodParameterDefinition(0, "String"));
        Assert.assertNotNull(param0);
        Assert.assertEquals(1, param0.getParts().size());
        Assert.assertNotNull(param0.getParts().get(0));
        Assert.assertEquals("a", param0.getParts().get(0).getName());
        Assert.assertEquals("String", param0.getParts().get(0).getClassType());
        final ExpressionFormLine param1 = ep2.getParams().get(new ExpressionMethodParameterDefinition(1, "String"));
        Assert.assertNotNull(param1);
        Assert.assertEquals(1, param1.getParts().size());
        Assert.assertNotNull(param1.getParts().get(0));
        Assert.assertEquals("b", param1.getParts().get(0).getName());
        Assert.assertEquals("String", param1.getParts().get(0).getClassType());
        Assert.assertEquals(0, m.rhs.length);
        // Check round-trip
        assertEqualsIgnoreWhitespace(drl, RuleModelDRLPersistenceImpl.getInstance().marshal(m));
    }

    @Test
    public void testBoundListConstraint() throws Exception {
        // https://bugzilla.redhat.com/show_bug.cgi?id=1264339
        String drl = "package org.test;\n" + (((((("rule \"List_Of_Values_Issue\"\n" + "dialect \"mvel\"\n") + "when\n") + "  MyTransactionVO( $myData : myData )\n") + "  MyDataList( myDataList contains $myData )\n") + "     then\n") + "end");
        addModelField("org.test.MyTransactionVO", "this", "org.test.MyTransactionVO", TYPE_THIS);
        addModelField("org.test.MyTransactionVO", "myData", "org.test.MyDataList", "org.test.MyDataList");
        addModelField("org.test.MyDataList", "this", "org.test.MyDataList", TYPE_THIS);
        addModelField("org.test.MyDataList", "myDataList", List.class.getName(), TYPE_COLLECTION);
        Mockito.when(dmo.getPackageName()).thenReturn("org.test");
        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals(2, m.lhs.length);
        final IPattern p0 = m.lhs[0];
        Assert.assertTrue((p0 instanceof FactPattern));
        final FactPattern fp0 = ((FactPattern) (p0));
        Assert.assertEquals("MyTransactionVO", fp0.getFactType());
        Assert.assertEquals(1, fp0.getNumberOfConstraints());
        final IPattern p1 = m.lhs[1];
        Assert.assertTrue((p1 instanceof FactPattern));
        final FactPattern fp1 = ((FactPattern) (p1));
        Assert.assertEquals("MyDataList", fp1.getFactType());
        Assert.assertEquals(1, fp1.getNumberOfConstraints());
        Assert.assertTrue(((fp0.getConstraint(0)) instanceof SingleFieldConstraint));
        final SingleFieldConstraint fp0sfc0 = ((SingleFieldConstraint) (fp0.getConstraint(0)));
        Assert.assertEquals("MyTransactionVO", fp0sfc0.getFactType());
        Assert.assertEquals("myData", fp0sfc0.getFieldName());
        Assert.assertEquals("$myData", fp0sfc0.getFieldBinding());
        Assert.assertEquals("MyDataList", fp0sfc0.getFieldType());
        Assert.assertNull(fp0sfc0.getOperator());
        Assert.assertNull(fp0sfc0.getValue());
        Assert.assertTrue(((fp1.getConstraint(0)) instanceof SingleFieldConstraint));
        final SingleFieldConstraint fp1sfc0 = ((SingleFieldConstraint) (fp1.getConstraint(0)));
        Assert.assertEquals("MyDataList", fp1sfc0.getFactType());
        Assert.assertEquals("myDataList", fp1sfc0.getFieldName());
        Assert.assertEquals("contains", fp1sfc0.getOperator());
        Assert.assertEquals("$myData", fp1sfc0.getValue());
        Assert.assertEquals(SingleFieldConstraint.TYPE_VARIABLE, fp1sfc0.getConstraintValueType());
        Assert.assertEquals(0, m.rhs.length);
        // Check round-trip
        assertEqualsIgnoreWhitespace(drl, RuleModelDRLPersistenceImpl.getInstance().marshal(m));
    }

    @Test
    public void testRHSAppendToList() throws Exception {
        // https://issues.jboss.org/browse/GUVNOR-2286
        String drl = "package org.test;\n" + (((((((((("import java.lang.Number;\n" + "rule \"RuleCheckEmail\"\n") + "dialect \"mvel\"\n") + "when\n") + "incomeData : IncomeData( email == \"myemail\" , $list : list != null )\n") + "then\n") + "Element element = new Element();\n") + "element.setId( 2 );\n") + "insertLogical( element );\n") + "$list.add( element );\n") + "end");
        addModelField("org.test.IncomeData", "this", "org.test.IncomeData", TYPE_THIS);
        addModelField("org.test.IncomeData", "email", String.class.getName(), TYPE_STRING);
        addModelField("org.test.IncomeData", "list", List.class.getName(), TYPE_COLLECTION);
        addModelField("java.util.List", "this", "java.util.List", TYPE_THIS);
        addMethodInformation("java.util.List", "add", new ArrayList<String>() {
            {
                add("java.lang.Object");
            }
        }, TYPE_BOOLEAN, null, TYPE_BOOLEAN);
        addMethodInformation("java.util.List", "add", new ArrayList<String>() {
            {
                add("java.lang.Integer");
                add("java.lang.Object");
            }
        }, TYPE_BOOLEAN, null, TYPE_BOOLEAN);
        Mockito.when(dmo.getPackageName()).thenReturn("org.test");
        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals(1, m.lhs.length);
        final IPattern p0 = m.lhs[0];
        Assert.assertTrue((p0 instanceof FactPattern));
        final FactPattern fp0 = ((FactPattern) (p0));
        Assert.assertEquals("IncomeData", fp0.getFactType());
        Assert.assertEquals(2, fp0.getNumberOfConstraints());
        Assert.assertTrue(((fp0.getConstraint(0)) instanceof SingleFieldConstraint));
        final SingleFieldConstraint fp0sfc0 = ((SingleFieldConstraint) (fp0.getConstraint(0)));
        Assert.assertEquals("IncomeData", fp0sfc0.getFactType());
        Assert.assertEquals("email", fp0sfc0.getFieldName());
        Assert.assertEquals(TYPE_STRING, fp0sfc0.getFieldType());
        Assert.assertEquals("==", fp0sfc0.getOperator());
        Assert.assertEquals("myemail", fp0sfc0.getValue());
        Assert.assertTrue(((fp0.getConstraint(1)) instanceof SingleFieldConstraint));
        final SingleFieldConstraint fp0sfc1 = ((SingleFieldConstraint) (fp0.getConstraint(1)));
        Assert.assertEquals("IncomeData", fp0sfc1.getFactType());
        Assert.assertEquals("list", fp0sfc1.getFieldName());
        Assert.assertEquals(TYPE_COLLECTION, fp0sfc1.getFieldType());
        Assert.assertEquals("!= null", fp0sfc1.getOperator());
        Assert.assertNull(fp0sfc1.getValue());
        Assert.assertEquals(2, m.rhs.length);
        Assert.assertTrue(((m.rhs[0]) instanceof ActionInsertLogicalFact));
        final ActionInsertLogicalFact a0 = ((ActionInsertLogicalFact) (m.rhs[0]));
        Assert.assertEquals("Element", a0.getFactType());
        Assert.assertEquals("element", a0.getBoundName());
        Assert.assertEquals(1, a0.getFieldValues().length);
        final ActionFieldValue a0f0 = a0.getFieldValues()[0];
        Assert.assertEquals("id", a0f0.getField());
        Assert.assertEquals("2", a0f0.getValue());
        Assert.assertEquals(TYPE_NUMERIC, a0f0.getType());
        Assert.assertEquals(FieldNatureType.TYPE_LITERAL, a0f0.getNature());
        Assert.assertTrue(((m.rhs[1]) instanceof ActionCallMethod));
        final ActionCallMethod a1 = ((ActionCallMethod) (m.rhs[1]));
        Assert.assertEquals("add", a1.getMethodName());
        Assert.assertEquals("$list", a1.getVariable());
        Assert.assertEquals(1, a1.getFieldValues().length);
        final ActionFieldValue a1f0 = a1.getFieldValues()[0];
        Assert.assertEquals("add", a1f0.getField());
        Assert.assertEquals("element", a1f0.getValue());
        Assert.assertEquals(FieldNatureType.TYPE_VARIABLE, a1f0.getNature());
        Assert.assertEquals("java.lang.Object", a1f0.getType());
        // Check round-trip
        assertEqualsIgnoreWhitespace(drl, RuleModelDRLPersistenceImpl.getInstance().marshal(m));
    }

    // https://issues.jboss.org/browse/GUVNOR-2030
    @Test
    public void testLHSTemplateKeys() throws Exception {
        String drl = "package org.test;\n" + ((((("rule \"MyRule\"\n" + "dialect \"mvel\"\n") + "when\n") + "  Person( name == \"@{k1}\" )\n") + "then\n") + "end");
        addModelField("org.test.Person", "this", "org.test.Person", TYPE_THIS);
        addModelField("org.test.Person", "name", String.class.getName(), TYPE_STRING);
        Mockito.when(dmo.getPackageName()).thenReturn("org.test");
        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals(1, m.lhs.length);
        final IPattern p0 = m.lhs[0];
        Assert.assertTrue((p0 instanceof FactPattern));
        final FactPattern fp0 = ((FactPattern) (p0));
        Assert.assertEquals("Person", fp0.getFactType());
        Assert.assertEquals(1, fp0.getNumberOfConstraints());
        Assert.assertTrue(((fp0.getConstraint(0)) instanceof SingleFieldConstraint));
        final SingleFieldConstraint sfc1 = ((SingleFieldConstraint) (fp0.getConstraint(0)));
        Assert.assertEquals("Person", sfc1.getFactType());
        Assert.assertEquals("name", sfc1.getFieldName());
        Assert.assertEquals(TYPE_STRING, sfc1.getFieldType());
        Assert.assertEquals(TYPE_TEMPLATE, sfc1.getConstraintValueType());
        Assert.assertEquals("k1", sfc1.getValue());
        Assert.assertEquals(0, m.rhs.length);
    }

    // https://issues.jboss.org/browse/GUVNOR-2030
    @Test
    public void testRHSTemplateKeys() throws Exception {
        String drl = "package org.test;\n" + (((((("rule \"MyRule\"\n" + "dialect \"mvel\"\n") + "when\n") + "  $p : Person( name == \"Fred\" )\n") + "then\n") + "  modify( $p ) { setName( \"@{k1}\" ) }\n") + "end");
        addModelField("org.test.Person", "this", "org.test.Person", TYPE_THIS);
        addModelField("org.test.Person", "name", String.class.getName(), TYPE_STRING);
        Mockito.when(dmo.getPackageName()).thenReturn("org.test");
        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals(1, m.lhs.length);
        final IPattern p0 = m.lhs[0];
        Assert.assertTrue((p0 instanceof FactPattern));
        final FactPattern fp0 = ((FactPattern) (p0));
        Assert.assertEquals("Person", fp0.getFactType());
        Assert.assertEquals(1, fp0.getNumberOfConstraints());
        Assert.assertTrue(((fp0.getConstraint(0)) instanceof SingleFieldConstraint));
        final SingleFieldConstraint sfc1 = ((SingleFieldConstraint) (fp0.getConstraint(0)));
        Assert.assertEquals("Person", sfc1.getFactType());
        Assert.assertEquals("name", sfc1.getFieldName());
        Assert.assertEquals(TYPE_STRING, sfc1.getFieldType());
        Assert.assertEquals(SingleFieldConstraint.TYPE_LITERAL, sfc1.getConstraintValueType());
        Assert.assertEquals("Fred", sfc1.getValue());
        Assert.assertEquals(1, m.rhs.length);
        Assert.assertTrue(((m.rhs[0]) instanceof ActionUpdateField));
        ActionUpdateField auf = ((ActionUpdateField) (m.rhs[0]));
        Assert.assertEquals("$p", auf.getVariable());
        Assert.assertEquals(1, auf.getFieldValues().length);
        ActionFieldValue afv = auf.getFieldValues()[0];
        Assert.assertEquals("name", afv.getField());
        Assert.assertEquals("k1", afv.getValue());
        Assert.assertEquals(FieldNatureType.TYPE_TEMPLATE, afv.getNature());
    }

    // See https://issues.jboss.org/browse/GUVNOR-2455
    @Test
    public void testRHS_DataTypeSuffixes() throws Exception {
        String drl = "package org.test;\n" + (((((("rule \"MyRule\"\n" + "dialect \"mvel\"\n") + "when\n") + "  $p : Person( )\n") + "then\n") + "  modify( $p ) { setDouble( 25.0d ), setFloat( 25.0f ), setLong( 25L ) }\n") + "end");
        addModelField("org.test.Person", "this", "org.test.Person", TYPE_THIS);
        addModelField("org.test.Person", "double", Double.class.getName(), TYPE_NUMERIC_DOUBLE);
        addModelField("org.test.Person", "float", Float.class.getName(), TYPE_NUMERIC_FLOAT);
        addModelField("org.test.Person", "long", Long.class.getName(), TYPE_NUMERIC_LONG);
        Mockito.when(dmo.getPackageName()).thenReturn("org.test");
        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals(1, m.lhs.length);
        final IPattern p0 = m.lhs[0];
        Assert.assertTrue((p0 instanceof FactPattern));
        final FactPattern fp0 = ((FactPattern) (p0));
        Assert.assertEquals("Person", fp0.getFactType());
        Assert.assertEquals(0, fp0.getNumberOfConstraints());
        Assert.assertEquals(1, m.rhs.length);
        Assert.assertTrue(((m.rhs[0]) instanceof ActionUpdateField));
        ActionUpdateField auf = ((ActionUpdateField) (m.rhs[0]));
        Assert.assertEquals("$p", auf.getVariable());
        Assert.assertEquals(3, auf.getFieldValues().length);
        ActionFieldValue afv0 = auf.getFieldValues()[0];
        Assert.assertEquals("double", afv0.getField());
        Assert.assertEquals("25.0", afv0.getValue());
        Assert.assertEquals(FieldNatureType.TYPE_LITERAL, afv0.getNature());
        ActionFieldValue afv1 = auf.getFieldValues()[1];
        Assert.assertEquals("float", afv1.getField());
        Assert.assertEquals("25.0", afv1.getValue());
        Assert.assertEquals(FieldNatureType.TYPE_LITERAL, afv1.getNature());
        ActionFieldValue afv2 = auf.getFieldValues()[2];
        Assert.assertEquals("long", afv2.getField());
        Assert.assertEquals("25", afv2.getValue());
        Assert.assertEquals(FieldNatureType.TYPE_LITERAL, afv2.getNature());
    }

    @Test
    public void compositeFieldConstraintWithTwoPredicates() {
        String drl = "rule \"rule1\"\n" + ((("when\n" + "Person( eval( age > 18 ) && eval(age < 45) )\n") + "then\n") + "end");
        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals("rule1", m.name);
        // LHS Pattern
        Assert.assertEquals(1, m.lhs.length);
        IPattern p = m.lhs[0];
        Assert.assertTrue((p instanceof FactPattern));
        FactPattern fp = ((FactPattern) (p));
        // LHS sub-patterns
        Assert.assertEquals(1, fp.getNumberOfConstraints());
        Assert.assertTrue(((fp.getConstraint(0)) instanceof CompositeFieldConstraint));
        CompositeFieldConstraint fp_cfp = ((CompositeFieldConstraint) (fp.getConstraint(0)));
        Assert.assertEquals("&&", fp_cfp.getCompositeJunctionType());
        Assert.assertEquals(2, fp_cfp.getNumberOfConstraints());
        Assert.assertTrue(((fp_cfp.getConstraint(0)) instanceof SingleFieldConstraint));
        Assert.assertTrue(((fp_cfp.getConstraint(1)) instanceof SingleFieldConstraint));
        SingleFieldConstraint fp_cfp_sfp1 = ((SingleFieldConstraint) (fp_cfp.getConstraint(0)));
        Assert.assertEquals("age > 18", fp_cfp_sfp1.getValue());
        Assert.assertEquals(TYPE_PREDICATE, fp_cfp_sfp1.getConstraintValueType());
        SingleFieldConstraint fp_cfp_sfp2 = ((SingleFieldConstraint) (fp_cfp.getConstraint(1)));
        Assert.assertEquals("age < 45", fp_cfp_sfp2.getValue());
        Assert.assertEquals(TYPE_PREDICATE, fp_cfp_sfp2.getConstraintValueType());
    }

    @Test
    public void compositeFieldConstraintWithOnePredicateAndOneLiteral() {
        String drl = "rule \"rule1\"\n" + ((("when\n" + "Person( eval( age > 18 ) && age < 45 )\n") + "then\n") + "end");
        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals("rule1", m.name);
        // LHS Pattern
        Assert.assertEquals(1, m.lhs.length);
        IPattern p = m.lhs[0];
        Assert.assertTrue((p instanceof FactPattern));
        FactPattern fp = ((FactPattern) (p));
        // LHS sub-patterns
        Assert.assertEquals(1, fp.getNumberOfConstraints());
        Assert.assertTrue(((fp.getConstraint(0)) instanceof CompositeFieldConstraint));
        CompositeFieldConstraint fp_cfp = ((CompositeFieldConstraint) (fp.getConstraint(0)));
        Assert.assertEquals("&&", fp_cfp.getCompositeJunctionType());
        Assert.assertEquals(2, fp_cfp.getNumberOfConstraints());
        Assert.assertTrue(((fp_cfp.getConstraint(0)) instanceof SingleFieldConstraint));
        Assert.assertTrue(((fp_cfp.getConstraint(1)) instanceof SingleFieldConstraint));
        SingleFieldConstraint fp_cfp_sfp1 = ((SingleFieldConstraint) (fp_cfp.getConstraint(0)));
        Assert.assertEquals("age > 18", fp_cfp_sfp1.getValue());
        Assert.assertEquals(TYPE_PREDICATE, fp_cfp_sfp1.getConstraintValueType());
        SingleFieldConstraint fp_cfp_sfp2 = ((SingleFieldConstraint) (fp_cfp.getConstraint(1)));
        Assert.assertEquals("Person", fp_cfp_sfp2.getFactType());
        Assert.assertEquals("age", fp_cfp_sfp2.getFieldName());
        Assert.assertEquals("<", fp_cfp_sfp2.getOperator());
        Assert.assertEquals("45", fp_cfp_sfp2.getValue());
        Assert.assertEquals(TYPE_LITERAL, fp_cfp_sfp2.getConstraintValueType());
    }

    @Test
    public void newRHSFactsCanBeUsedInRHSBinding() throws Exception {
        String drl = "package org.mortgages;\n" + (((((((((("rule \"r1\"\n" + "dialect \"mvel\"\n") + "when\n") + "$l : LoanApplication()\n") + "then\n") + "Applicant $a = new Applicant();\n") + "insert( $a );\n") + "modify( $l ) {\n") + "  setApplicant( $a )") + "}\n") + "end");
        addModelField("org.mortgages.Applicant", "this", "org.mortgages.Applicant", TYPE_THIS);
        addModelField("org.mortgages.LoanApplication", "this", "org.mortgages.LoanApplication", TYPE_THIS);
        addModelField("org.mortgages.LoanApplication", "applicant", "org.mortgages.Applicant", "org.mortgages.Applicant");
        Mockito.when(dmo.getPackageName()).thenReturn("org.mortgages");
        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals("r1", m.name);
        // LHS Pattern
        Assert.assertEquals(1, m.lhs.length);
        IPattern p = m.lhs[0];
        Assert.assertTrue((p instanceof FactPattern));
        FactPattern fp = ((FactPattern) (p));
        Assert.assertEquals("LoanApplication", fp.getFactType());
        Assert.assertEquals(0, fp.getNumberOfConstraints());
        Assert.assertEquals(2, m.rhs.length);
        Assert.assertTrue(((m.rhs[0]) instanceof ActionInsertFact));
        ActionInsertFact aif = ((ActionInsertFact) (m.rhs[0]));
        Assert.assertEquals("$a", aif.getBoundName());
        Assert.assertEquals("Applicant", aif.getFactType());
        Assert.assertEquals(0, aif.getFieldValues().length);
        Assert.assertTrue(((m.rhs[1]) instanceof ActionUpdateField));
        ActionUpdateField auf = ((ActionUpdateField) (m.rhs[1]));
        Assert.assertEquals("$l", auf.getVariable());
        Assert.assertEquals(1, auf.getFieldValues().length);
        ActionFieldValue afv0 = auf.getFieldValues()[0];
        Assert.assertEquals("applicant", afv0.getField());
        Assert.assertEquals("=$a", afv0.getValue());
        Assert.assertEquals(FieldNatureType.TYPE_VARIABLE, afv0.getNature());
        Assert.assertEquals("Applicant", afv0.getType());
    }

    @Test
    public void actionUpdateFieldWithFormula() throws Exception {
        String drl = "package org.mortgages;\n" + (((((((("rule \"r1\"\n" + "dialect \"mvel\"\n") + "when\n") + "$a : Application()\n") + "then\n") + "modify( $a ) {\n") + "  setName( \"Pupa\" + 20 + \"Smurf\" )") + "}\n") + "end");
        addModelField("org.mortgages.Applicant", "this", "org.mortgages.Applicant", TYPE_THIS);
        addModelField("org.mortgages.Applicant", "name", String.class.getName(), TYPE_STRING);
        Mockito.when(dmo.getPackageName()).thenReturn("org.mortgages");
        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals("r1", m.name);
        // LHS Pattern
        Assert.assertEquals(1, m.lhs.length);
        IPattern p = m.lhs[0];
        Assert.assertTrue((p instanceof FactPattern));
        FactPattern fp = ((FactPattern) (p));
        Assert.assertEquals("Application", fp.getFactType());
        Assert.assertEquals(0, fp.getNumberOfConstraints());
        Assert.assertEquals(1, m.rhs.length);
        Assert.assertTrue(((m.rhs[0]) instanceof ActionUpdateField));
        ActionUpdateField auf = ((ActionUpdateField) (m.rhs[0]));
        Assert.assertEquals("$a", auf.getVariable());
        Assert.assertEquals(1, auf.getFieldValues().length);
        ActionFieldValue afv0 = auf.getFieldValues()[0];
        Assert.assertEquals("name", afv0.getField());
        Assert.assertEquals("\"Pupa\" + 20 + \"Smurf\"", afv0.getValue());
        Assert.assertEquals(TYPE_FORMULA, afv0.getNature());
    }

    @Test
    public void actionUpdateFieldWithFormulaNotEndWithString() throws Exception {
        String drl = "package org.mortgages;\n" + (((((((("rule \"r1\"\n" + "dialect \"mvel\"\n") + "when\n") + "$a : Application()\n") + "then\n") + "modify( $a ) {\n") + "  setName( \"Pupa\" + 20 )") + "}\n") + "end");
        addModelField("org.mortgages.Applicant", "this", "org.mortgages.Applicant", TYPE_THIS);
        addModelField("org.mortgages.Applicant", "name", String.class.getName(), TYPE_STRING);
        Mockito.when(dmo.getPackageName()).thenReturn("org.mortgages");
        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals("r1", m.name);
        // LHS Pattern
        Assert.assertEquals(1, m.lhs.length);
        IPattern p = m.lhs[0];
        Assert.assertTrue((p instanceof FactPattern));
        FactPattern fp = ((FactPattern) (p));
        Assert.assertEquals("Application", fp.getFactType());
        Assert.assertEquals(0, fp.getNumberOfConstraints());
        Assert.assertEquals(1, m.rhs.length);
        Assert.assertTrue(((m.rhs[0]) instanceof ActionUpdateField));
        ActionUpdateField auf = ((ActionUpdateField) (m.rhs[0]));
        Assert.assertEquals("$a", auf.getVariable());
        Assert.assertEquals(1, auf.getFieldValues().length);
        ActionFieldValue afv0 = auf.getFieldValues()[0];
        Assert.assertEquals("name", afv0.getField());
        Assert.assertEquals("\"Pupa\" + 20", afv0.getValue());
        Assert.assertEquals(TYPE_FORMULA, afv0.getNature());
    }

    @Test
    public void actionUpdateFieldWithFormulaWithEscapedQuote() throws Exception {
        String drl = "package org.mortgages;\n" + (((((((("rule \"r1\"\n" + "dialect \"mvel\"\n") + "when\n") + "$a : Application()\n") + "then\n") + "modify( $a ) {\n") + "  setName( \"Pupa \\\"\" + 20 + \"\\\" Smurf\" )") + "}\n") + "end");
        addModelField("org.mortgages.Applicant", "this", "org.mortgages.Applicant", TYPE_THIS);
        addModelField("org.mortgages.Applicant", "name", String.class.getName(), TYPE_STRING);
        Mockito.when(dmo.getPackageName()).thenReturn("org.mortgages");
        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals("r1", m.name);
        // LHS Pattern
        Assert.assertEquals(1, m.lhs.length);
        IPattern p = m.lhs[0];
        Assert.assertTrue((p instanceof FactPattern));
        FactPattern fp = ((FactPattern) (p));
        Assert.assertEquals("Application", fp.getFactType());
        Assert.assertEquals(0, fp.getNumberOfConstraints());
        Assert.assertEquals(1, m.rhs.length);
        Assert.assertTrue(((m.rhs[0]) instanceof ActionUpdateField));
        ActionUpdateField auf = ((ActionUpdateField) (m.rhs[0]));
        Assert.assertEquals("$a", auf.getVariable());
        Assert.assertEquals(1, auf.getFieldValues().length);
        ActionFieldValue afv0 = auf.getFieldValues()[0];
        Assert.assertEquals("name", afv0.getField());
        Assert.assertEquals("\"Pupa \\\"\" + 20 + \"\\\" Smurf\"", afv0.getValue());
        Assert.assertEquals(TYPE_FORMULA, afv0.getNature());
    }

    @Test
    public void actionSetFieldWithFormula() throws Exception {
        String drl = "package org.mortgages;\n" + (((((("rule \"r1\"\n" + "dialect \"mvel\"\n") + "when\n") + "$a : Application()\n") + "then\n") + "$a.setName( \"Pupa\" + 20 + \"Smurf\" );") + "end");
        addModelField("org.mortgages.Applicant", "this", "org.mortgages.Applicant", TYPE_THIS);
        addModelField("org.mortgages.Applicant", "name", String.class.getName(), TYPE_STRING);
        Mockito.when(dmo.getPackageName()).thenReturn("org.mortgages");
        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals("r1", m.name);
        // LHS Pattern
        Assert.assertEquals(1, m.lhs.length);
        IPattern p = m.lhs[0];
        Assert.assertTrue((p instanceof FactPattern));
        FactPattern fp = ((FactPattern) (p));
        Assert.assertEquals("Application", fp.getFactType());
        Assert.assertEquals(0, fp.getNumberOfConstraints());
        Assert.assertEquals(1, m.rhs.length);
        Assert.assertTrue(((m.rhs[0]) instanceof ActionSetField));
        ActionSetField asf = ((ActionSetField) (m.rhs[0]));
        Assert.assertEquals("$a", asf.getVariable());
        Assert.assertEquals(1, asf.getFieldValues().length);
        ActionFieldValue afv0 = asf.getFieldValues()[0];
        Assert.assertEquals("name", afv0.getField());
        Assert.assertEquals("\"Pupa\" + 20 + \"Smurf\"", afv0.getValue());
        Assert.assertEquals(TYPE_FORMULA, afv0.getNature());
    }

    @Test
    public void actionInsertFactWithFormula() throws Exception {
        String drl = "package org.mortgages;\n" + (((((((("rule \"r1\"\n" + "dialect \"mvel\"\n") + "when\n") + "$a : Application()\n") + "then\n") + "Applicant $a = new Applicant();\n") + "$a.setName( \"Pupa\" + 20 + \"Smurf\" );\n") + "insert( $a );\n") + "end");
        addModelField("org.mortgages.Applicant", "this", "org.mortgages.Applicant", TYPE_THIS);
        addModelField("org.mortgages.Applicant", "name", String.class.getName(), TYPE_STRING);
        Mockito.when(dmo.getPackageName()).thenReturn("org.mortgages");
        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals("r1", m.name);
        // LHS Pattern
        Assert.assertEquals(1, m.lhs.length);
        IPattern p = m.lhs[0];
        Assert.assertTrue((p instanceof FactPattern));
        FactPattern fp = ((FactPattern) (p));
        Assert.assertEquals("Application", fp.getFactType());
        Assert.assertEquals(0, fp.getNumberOfConstraints());
        Assert.assertEquals(1, m.rhs.length);
        Assert.assertTrue(((m.rhs[0]) instanceof ActionInsertFact));
        ActionInsertFact aif = ((ActionInsertFact) (m.rhs[0]));
        Assert.assertEquals("$a", aif.getBoundName());
        Assert.assertEquals(1, aif.getFieldValues().length);
        ActionFieldValue afv0 = aif.getFieldValues()[0];
        Assert.assertEquals("name", afv0.getField());
        Assert.assertEquals("\"Pupa\" + 20 + \"Smurf\"", afv0.getValue());
        Assert.assertEquals(TYPE_FORMULA, afv0.getNature());
    }

    @Test
    public void testForAll() throws Exception {
        // RHBPMS-4666
        String drl = "package org.test;\n" + ((((("import java.util.List;\n" + "rule \"MyRule\" dialect \"mvel\" when\n") + "  $myList : List( empty == false )\n") + "  forall( String( this.startsWith( \"n\" ) ) from $myList )\n") + "then\n") + "end");
        Mockito.when(dmo.getPackageName()).thenReturn("org.test");
        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        // Check round-trip
        assertEqualsIgnoreWhitespace(drl, RuleModelDRLPersistenceImpl.getInstance().marshal(m));
    }

    // https://issues.jboss.org/browse/RHBPMS-4695
    @Test
    public void testNestedEvalWithIdenticalCompositeOperator() {
        String drl = "rule \"rule1\"\n" + (((("dialect \"mvel\"\n" + "when\n") + "MyTestDateObject(firstName != null && firstName != \"\" && eval($td.getFirstName().toUpperCase().equals(\"NONAME\") || $td.getFirstName().toUpperCase().equals(\"NAMENOTPROVIDED\")))\n") + "then\n") + "end");
        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        // Check round-trip
        assertEqualsIgnoreWhitespace(drl, RuleModelDRLPersistenceImpl.getInstance().marshal(m));
    }

    // https://issues.jboss.org/browse/RHBPMS-4695
    @Test
    public void testNestedEvalWithDifferentCompositeOperators() {
        String drl = "rule \"rule1\"\n" + (((("dialect \"mvel\"\n" + "when\n") + "MyTestDateObject(firstName != null && firstName != \"\" || eval($td.getFirstName().toUpperCase().equals(\"NONAME\") || $td.getFirstName().toUpperCase().equals(\"NAMENOTPROVIDED\")))\n") + "then\n") + "end");
        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        // Check round-trip
        assertEqualsIgnoreWhitespace(drl, RuleModelDRLPersistenceImpl.getInstance().marshal(m));
    }

    // https://issues.jboss.org/browse/RHBPMS-4695
    @Test
    public void testNestedEvalWithIdenticalCompositeOperatorsAndTrailingConstraint() {
        String drl = "rule \"rule1\"\n" + (((("dialect \"mvel\"\n" + "when\n") + "MyTestDateObject(firstName != null && eval($td.getFirstName().toUpperCase().equals(\"NONAME\") || $td.getFirstName().toUpperCase().equals(\"NAMENOTPROVIDED\")) && firstName != \"\" )\n") + "then\n") + "end");
        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        // Check round-trip
        assertEqualsIgnoreWhitespace(drl, RuleModelDRLPersistenceImpl.getInstance().marshal(m));
    }

    // https://issues.jboss.org/browse/RHBPMS-4695
    @Test
    public void testNestedEvalWithDifferentCompositeOperatorsAndTrailingConstraint() {
        String drl = "rule \"rule1\"\n" + (((("dialect \"mvel\"\n" + "when\n") + "MyTestDateObject(firstName != null || eval($td.getFirstName().toUpperCase().equals(\"NONAME\") || $td.getFirstName().toUpperCase().equals(\"NAMENOTPROVIDED\")) && firstName != \"\" )\n") + "then\n") + "end");
        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        // Check round-trip
        assertEqualsIgnoreWhitespace(drl, RuleModelDRLPersistenceImpl.getInstance().marshal(m));
    }

    @Test
    public void testNotMatchesOrNotSoundsLike() {
        final String drl = "rule \"rule1\"\n" + (((("dialect \"mvel\"\n" + "when\n") + "MyTestObject(firstName not matches \"John\" || firstName not soundslike \"Peter\" )\n") + "then\n") + "end");
        final RuleModel model = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, Collections.emptyList(), dmo);
        assertEqualsIgnoreWhitespace(drl, RuleModelDRLPersistenceImpl.getInstance().marshal(model));
    }

    @Test
    public void testNotMatchesAndNotSoundsLikeDeclaredInDsl() {
        final String drl = "package org.mortgages;\n" + ((((("rule \"testdsl\"\n" + "  dialect \"mvel\"\n") + "  when\n") + "    Applicant\'s name does not matches or does not sounds like John\n") + "  then\n") + "end");
        final String dslDefinition = "Applicant's name does not matches or does not sounds like {name}";
        final String dslFile = ("[when]" + dslDefinition) + "= Applicant( name not matches \"{name}\" || name not soundslike \"{name}\" )";
        Mockito.when(dmo.getPackageName()).thenReturn("org.mortgages");
        final RuleModel model = RuleModelDRLPersistenceImpl.getInstance().unmarshalUsingDSL(drl, Collections.emptyList(), dmo, dslFile);
        assertEqualsIgnoreWhitespace(drl, RuleModelDRLPersistenceImpl.getInstance().marshal(model));
    }
}

