/**
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.workbench.models.guided.template.backend;


import org.drools.workbench.models.datamodel.rule.DSLSentence;
import org.drools.workbench.models.datamodel.rule.DSLVariableValue;
import org.drools.workbench.models.datamodel.rule.RuleModel;
import org.junit.Assert;
import org.junit.Test;


public class RuleTemplateModelXMLLegacyPersistenceTest {
    @Test
    public void testUnmarshalDSLVariableValuesLegacy() {
        // See https://issues.jboss.org/browse/GUVNOR-1872
        final String xml = "<rule>" + (((((((((((((((((((("<name>BugReportRule</name>" + "<modelVersion>1.0</modelVersion>") + "<attributes/>") + "<metadataList/>") + "<lhs>") + "<dslSentence>") + "<definition>If processInstance</definition>") + "<values/>") + "</dslSentence>") + "</lhs>") + "<rhs>") + "<dslSentence>") + "<definition>MyLog {myout}</definition>") + "<values>") + "<string>sample out rule 1</string>") + "<string>myout</string>") + "</values>") + "</dslSentence>") + "</rhs>") + "<isNegated>false</isNegated>") + "</rule>");
        RuleModel rm = RuleTemplateModelXMLPersistenceImpl.getInstance().unmarshal(xml);
        Assert.assertNotNull(rm);
        Assert.assertEquals(1, rm.lhs.length);
        Assert.assertTrue(((rm.lhs[0]) instanceof DSLSentence));
        DSLSentence dslPattern = ((DSLSentence) (rm.lhs[0]));
        Assert.assertEquals("If processInstance", dslPattern.getDefinition());
        Assert.assertEquals(0, dslPattern.getValues().size());
        Assert.assertEquals(1, rm.rhs.length);
        Assert.assertTrue(((rm.rhs[0]) instanceof DSLSentence));
        DSLSentence dslAction = ((DSLSentence) (rm.rhs[0]));
        Assert.assertEquals("MyLog {myout}", dslAction.getDefinition());
        Assert.assertEquals(2, dslAction.getValues().size());
        Assert.assertTrue(((dslAction.getValues().get(0)) instanceof DSLVariableValue));
        Assert.assertTrue(((dslAction.getValues().get(1)) instanceof DSLVariableValue));
        Assert.assertEquals("sample out rule 1", dslAction.getValues().get(0).getValue());
        Assert.assertEquals("myout", dslAction.getValues().get(1).getValue());
    }

    @Test
    public void testUnmarshalDSLVariableValues() {
        // See https://issues.jboss.org/browse/GUVNOR-1872
        final String xml = "<rule>" + (((((((((((((((((((((((("<name>BugReportRule</name>" + "<modelVersion>1.0</modelVersion>") + "<attributes/>") + "<metadataList/>") + "<lhs>") + "<dslSentence>") + "<definition>If processInstance</definition>") + "<values/>") + "</dslSentence>") + "</lhs>") + "<rhs>") + "<dslSentence>") + "<definition>MyLog {myout}</definition>") + "<values>") + "<org.drools.workbench.models.datamodel.rule.DSLVariableValue>") + "<value>5-4 sample out</value>") + "</org.drools.workbench.models.datamodel.rule.DSLVariableValue>") + "<org.drools.workbench.models.datamodel.rule.DSLVariableValue>") + "<value>myout</value>") + "</org.drools.workbench.models.datamodel.rule.DSLVariableValue>") + "</values>") + "</dslSentence>") + "</rhs>") + "<isNegated>false</isNegated>") + "</rule>");
        RuleModel rm = RuleTemplateModelXMLPersistenceImpl.getInstance().unmarshal(xml);
        Assert.assertNotNull(rm);
        Assert.assertEquals(1, rm.lhs.length);
        Assert.assertTrue(((rm.lhs[0]) instanceof DSLSentence));
        DSLSentence dslPattern = ((DSLSentence) (rm.lhs[0]));
        Assert.assertEquals("If processInstance", dslPattern.getDefinition());
        Assert.assertEquals(0, dslPattern.getValues().size());
        Assert.assertEquals(1, rm.rhs.length);
        Assert.assertTrue(((rm.rhs[0]) instanceof DSLSentence));
        DSLSentence dslAction = ((DSLSentence) (rm.rhs[0]));
        Assert.assertEquals("MyLog {myout}", dslAction.getDefinition());
        Assert.assertEquals(2, dslAction.getValues().size());
        Assert.assertTrue(((dslAction.getValues().get(0)) instanceof DSLVariableValue));
        Assert.assertTrue(((dslAction.getValues().get(1)) instanceof DSLVariableValue));
        Assert.assertEquals("5-4 sample out", dslAction.getValues().get(0).getValue());
        Assert.assertEquals("myout", dslAction.getValues().get(1).getValue());
    }

    /**
     * This will verify that we can load an old BRL change. If this fails, then
     * backwards compatibility is broken.
     */
    @Test
    public void testBackwardsCompat() throws Exception {
        RuleModel m2 = RuleTemplateModelXMLPersistenceImpl.getInstance().unmarshal(RuleTemplateModelXMLLegacyPersistenceTest.loadResource("existing_brl.xml"));
        Assert.assertNotNull(m2);
        Assert.assertEquals(3, m2.rhs.length);
    }
}

