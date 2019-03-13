/**
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.models.guided.template.backend;


import CompositeFieldConstraint.COMPOSITE_TYPE_AND;
import CompositeFieldConstraint.COMPOSITE_TYPE_OR;
import SingleFieldConstraint.TYPE_LITERAL;
import org.drools.workbench.models.datamodel.rule.ActionGlobalCollectionAdd;
import org.drools.workbench.models.datamodel.rule.ActionInsertFact;
import org.drools.workbench.models.datamodel.rule.CompositeFieldConstraint;
import org.drools.workbench.models.datamodel.rule.ConnectiveConstraint;
import org.drools.workbench.models.datamodel.rule.FactPattern;
import org.drools.workbench.models.datamodel.rule.FreeFormLine;
import org.drools.workbench.models.datamodel.rule.IAction;
import org.drools.workbench.models.datamodel.rule.IPattern;
import org.drools.workbench.models.datamodel.rule.RuleAttribute;
import org.drools.workbench.models.datamodel.rule.RuleModel;
import org.drools.workbench.models.datamodel.rule.SingleFieldConstraint;
import org.drools.workbench.models.guided.template.shared.TemplateModel;
import org.junit.Assert;
import org.junit.Test;


public class RuleTemplateModelXMLPersistenceTest {
    @Test
    public void testGenerateEmptyXML() {
        final RuleTemplateModelPersistence p = RuleTemplateModelXMLPersistenceImpl.getInstance();
        final String xml = p.marshal(new TemplateModel());
        Assert.assertNotNull(xml);
        Assert.assertFalse(xml.equals(""));
        Assert.assertTrue(xml.startsWith("<rule>"));
        Assert.assertTrue(xml.endsWith("</rule>"));
    }

    @Test
    public void testBasics() {
        final RuleTemplateModelPersistence p = RuleTemplateModelXMLPersistenceImpl.getInstance();
        final TemplateModel m = new TemplateModel();
        m.addLhsItem(new FactPattern("Person"));
        m.addLhsItem(new FactPattern("Accident"));
        m.addAttribute(new RuleAttribute("no-loop", "true"));
        m.addRhsItem(new ActionInsertFact("Report"));
        ActionGlobalCollectionAdd ag = new ActionGlobalCollectionAdd();
        ag.setFactName("x");
        ag.setGlobalName("g");
        m.addRhsItem(ag);
        m.name = "my rule";
        final String xml = p.marshal(m);
        System.out.println(xml);
        Assert.assertTrue(((xml.indexOf("Person")) > (-1)));
        Assert.assertTrue(((xml.indexOf("Accident")) > (-1)));
        Assert.assertTrue(((xml.indexOf("no-loop")) > (-1)));
        Assert.assertTrue(((xml.indexOf("org.kie")) == (-1)));
        Assert.assertTrue(((xml.indexOf("addToGlobal")) > (-1)));
        RuleModel rm_ = RuleTemplateModelXMLPersistenceImpl.getInstance().unmarshal(xml);
        Assert.assertEquals(2, rm_.rhs.length);
    }

    @Test
    public void testMoreComplexRendering() {
        final RuleTemplateModelPersistence p = RuleTemplateModelXMLPersistenceImpl.getInstance();
        final TemplateModel m = getComplexModel();
        final String xml = p.marshal(m);
        System.out.println(xml);
        Assert.assertTrue(((xml.indexOf("org.kie")) == (-1)));
    }

    @Test
    public void testRoundTrip() {
        final TemplateModel m = getComplexModel();
        final String xml = RuleTemplateModelXMLPersistenceImpl.getInstance().marshal(m);
        final TemplateModel m2 = RuleTemplateModelXMLPersistenceImpl.getInstance().unmarshal(xml);
        Assert.assertNotNull(m2);
        Assert.assertEquals(m.name, m2.name);
        Assert.assertEquals(m.lhs.length, m2.lhs.length);
        Assert.assertEquals(m.rhs.length, m2.rhs.length);
        Assert.assertEquals(1, m.attributes.length);
        final RuleAttribute at = m.attributes[0];
        Assert.assertEquals("no-loop", at.getAttributeName());
        Assert.assertEquals("true", at.getValue());
        final String newXML = RuleTemplateModelXMLPersistenceImpl.getInstance().marshal(m2);
        Assert.assertEquals(xml, newXML);
    }

    @Test
    public void testCompositeConstraintsRoundTrip() throws Exception {
        TemplateModel m = new TemplateModel();
        m.name = "with composite";
        FactPattern p1 = new FactPattern("Person");
        p1.setBoundName("p1");
        m.addLhsItem(p1);
        FactPattern p = new FactPattern("Goober");
        m.addLhsItem(p);
        CompositeFieldConstraint comp = new CompositeFieldConstraint();
        comp.setCompositeJunctionType(COMPOSITE_TYPE_OR);
        p.addConstraint(comp);
        final SingleFieldConstraint X = new SingleFieldConstraint();
        X.setFieldName("goo");
        X.setConstraintValueType(TYPE_LITERAL);
        X.setValue("foo");
        X.setOperator("==");
        X.setConnectives(new ConnectiveConstraint[1]);
        X.getConnectives()[0] = new ConnectiveConstraint();
        X.getConnectives()[0].setConstraintValueType(ConnectiveConstraint.TYPE_LITERAL);
        X.getConnectives()[0].setOperator("|| ==");
        X.getConnectives()[0].setValue("bar");
        comp.addConstraint(X);
        final SingleFieldConstraint Y = new SingleFieldConstraint();
        Y.setFieldName("goo2");
        Y.setConstraintValueType(TYPE_LITERAL);
        Y.setValue("foo");
        Y.setOperator("==");
        comp.addConstraint(Y);
        CompositeFieldConstraint comp2 = new CompositeFieldConstraint();
        comp2.setCompositeJunctionType(COMPOSITE_TYPE_AND);
        final SingleFieldConstraint Q1 = new SingleFieldConstraint();
        Q1.setFieldName("goo");
        Q1.setOperator("==");
        Q1.setValue("whee");
        Q1.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        comp2.addConstraint(Q1);
        final SingleFieldConstraint Q2 = new SingleFieldConstraint();
        Q2.setFieldName("gabba");
        Q2.setOperator("==");
        Q2.setValue("whee");
        Q2.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        comp2.addConstraint(Q2);
        // now nest it
        comp.addConstraint(comp2);
        final SingleFieldConstraint Z = new SingleFieldConstraint();
        Z.setFieldName("goo3");
        Z.setConstraintValueType(TYPE_LITERAL);
        Z.setValue("foo");
        Z.setOperator("==");
        p.addConstraint(Z);
        ActionInsertFact ass = new ActionInsertFact("Whee");
        m.addRhsItem(ass);
        String xml = RuleTemplateModelXMLPersistenceImpl.getInstance().marshal(m);
        // System.err.println(xml);
        RuleModel m2 = RuleTemplateModelXMLPersistenceImpl.getInstance().unmarshal(xml);
        Assert.assertNotNull(m2);
        Assert.assertEquals("with composite", m2.name);
        Assert.assertEquals(m2.lhs.length, m.lhs.length);
        Assert.assertEquals(m2.rhs.length, m.rhs.length);
    }

    @Test
    public void testFreeFormLine() {
        TemplateModel m = new TemplateModel();
        m.name = "with composite";
        m.lhs = new IPattern[1];
        m.rhs = new IAction[1];
        FreeFormLine fl = new FreeFormLine();
        fl.setText("Person()");
        m.lhs[0] = fl;
        FreeFormLine fr = new FreeFormLine();
        fr.setText("fun()");
        m.rhs[0] = fr;
        String xml = RuleTemplateModelXMLPersistenceImpl.getInstance().marshal(m);
        Assert.assertNotNull(xml);
        RuleModel m_ = RuleTemplateModelXMLPersistenceImpl.getInstance().unmarshal(xml);
        Assert.assertEquals(1, m_.lhs.length);
        Assert.assertEquals(1, m_.rhs.length);
        Assert.assertEquals("Person()", getText());
        Assert.assertEquals("fun()", getText());
    }

    /**
     * This will verify that we can load an old BRL change. If this fails, then
     * backwards compatibility is broken.
     */
    @Test
    public void testBackwardsCompatibility() throws Exception {
        RuleModel m2 = RuleTemplateModelXMLPersistenceImpl.getInstance().unmarshal(RuleTemplateModelXMLPersistenceTest.loadResource("existing_brl.xml"));
        Assert.assertNotNull(m2);
        Assert.assertEquals(3, m2.rhs.length);
    }

    @Test
    public void testLoadEmpty() {
        RuleModel m = RuleTemplateModelXMLPersistenceImpl.getInstance().unmarshal(null);
        Assert.assertNotNull(m);
        m = RuleTemplateModelXMLPersistenceImpl.getInstance().unmarshal("");
        Assert.assertNotNull(m);
    }
}

