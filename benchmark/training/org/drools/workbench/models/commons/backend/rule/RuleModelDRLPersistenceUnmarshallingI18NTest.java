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


import DataType.TYPE_NUMERIC_INTEGER;
import FieldNatureType.TYPE_LITERAL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.drools.workbench.models.datamodel.rule.ActionFieldValue;
import org.drools.workbench.models.datamodel.rule.ActionInsertFact;
import org.drools.workbench.models.datamodel.rule.FactPattern;
import org.drools.workbench.models.datamodel.rule.RuleModel;
import org.drools.workbench.models.datamodel.rule.SingleFieldConstraint;
import org.junit.Assert;
import org.junit.Test;
import org.kie.soup.project.datamodel.oracle.MethodInfo;
import org.kie.soup.project.datamodel.oracle.ModelField;
import org.kie.soup.project.datamodel.oracle.PackageDataModelOracle;


public class RuleModelDRLPersistenceUnmarshallingI18NTest {
    private PackageDataModelOracle dmo;

    private Map<String, ModelField[]> packageModelFields = new HashMap<String, ModelField[]>();

    private Map<String, String[]> projectJavaEnumDefinitions = new HashMap<String, String[]>();

    private Map<String, List<MethodInfo>> projectMethodInformation = new HashMap<String, List<MethodInfo>>();

    @Test
    public void testI18N_US_InsertFact() {
        final String drl = "package org.test;\n" + ((((((("rule \"r1\"\n" + "dialect \"mvel\"\n") + "when\n") + "then\n") + "Applicant fact0 = new Applicant();\n") + "fact0.setAge( 55 );\n") + "insert( fact0 );\n") + "end");
        addModelField("Applicant", "age", "java.lang.Integer", TYPE_NUMERIC_INTEGER);
        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, new ArrayList<String>(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals(1, m.rhs.length);
        Assert.assertTrue(((m.rhs[0]) instanceof ActionInsertFact));
        final ActionInsertFact aif = ((ActionInsertFact) (m.rhs[0]));
        Assert.assertEquals("Applicant", aif.getFactType());
        Assert.assertEquals("fact0", aif.getBoundName());
        Assert.assertEquals(1, aif.getFieldValues().length);
        final ActionFieldValue afv = aif.getFieldValues()[0];
        Assert.assertEquals("age", afv.getField());
        Assert.assertEquals("55", afv.getValue());
        Assert.assertEquals(TYPE_NUMERIC_INTEGER, afv.getType());
        Assert.assertEquals(TYPE_LITERAL, afv.getNature());
    }

    @Test
    public void testI18N_JP_InsertFact() {
        final String drl = "package org.test;\n" + ((((((("rule \"r1\"\n" + "dialect \"mvel\"\n") + "when\n") + "then\n") + "\u5e3d\u5b50 fact0 = new \u5e3d\u5b50();\n") + "fact0.set\u30b5\u30a4\u30ba( 55 );\n") + "insert( fact0 );\n") + "end");
        addModelField("??", "???", "java.lang.Integer", TYPE_NUMERIC_INTEGER);
        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, new ArrayList<String>(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals(1, m.rhs.length);
        Assert.assertTrue(((m.rhs[0]) instanceof ActionInsertFact));
        final ActionInsertFact aif = ((ActionInsertFact) (m.rhs[0]));
        Assert.assertEquals("??", aif.getFactType());
        Assert.assertEquals("fact0", aif.getBoundName());
        Assert.assertEquals(1, aif.getFieldValues().length);
        final ActionFieldValue afv = aif.getFieldValues()[0];
        Assert.assertEquals("???", afv.getField());
        Assert.assertEquals("55", afv.getValue());
        Assert.assertEquals(TYPE_NUMERIC_INTEGER, afv.getType());
        Assert.assertEquals(TYPE_LITERAL, afv.getNature());
    }

    @Test
    public void testI18N_US_BoundField() {
        final String drl = "package org.test;\n" + ((((("rule \"r1\"\n" + "dialect \"mvel\"\n") + "when\n") + "Applicant( $a : age )\n") + "then\n") + "end");
        addModelField("Applicant", "age", "java.lang.Integer", TYPE_NUMERIC_INTEGER);
        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, new ArrayList<String>(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals(1, m.lhs.length);
        Assert.assertTrue(((m.lhs[0]) instanceof FactPattern));
        final FactPattern fp = ((FactPattern) (m.lhs[0]));
        Assert.assertEquals("Applicant", fp.getFactType());
        Assert.assertEquals(1, fp.getNumberOfConstraints());
        Assert.assertTrue(((fp.getConstraint(0)) instanceof SingleFieldConstraint));
        final SingleFieldConstraint sfc = ((SingleFieldConstraint) (fp.getConstraint(0)));
        Assert.assertEquals("age", sfc.getFieldName());
        Assert.assertEquals(TYPE_NUMERIC_INTEGER, sfc.getFieldType());
        Assert.assertEquals("$a", sfc.getFieldBinding());
    }

    @Test
    public void testI18N_JP_BoundField() {
        final String drl = "package org.test;\n" + ((((("rule \"r1\"\n" + "dialect \"mvel\"\n") + "when\n") + "Applicant( \u88fd\u54c1\u756a\u53f7 : age )\n") + "then\n") + "end");
        addModelField("Applicant", "age", "java.lang.Integer", TYPE_NUMERIC_INTEGER);
        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl, new ArrayList<String>(), dmo);
        Assert.assertNotNull(m);
        Assert.assertEquals(1, m.lhs.length);
        Assert.assertTrue(((m.lhs[0]) instanceof FactPattern));
        final FactPattern fp = ((FactPattern) (m.lhs[0]));
        Assert.assertEquals("Applicant", fp.getFactType());
        Assert.assertEquals(1, fp.getNumberOfConstraints());
        Assert.assertTrue(((fp.getConstraint(0)) instanceof SingleFieldConstraint));
        final SingleFieldConstraint sfc = ((SingleFieldConstraint) (fp.getConstraint(0)));
        Assert.assertEquals("age", sfc.getFieldName());
        Assert.assertEquals(TYPE_NUMERIC_INTEGER, sfc.getFieldType());
        Assert.assertEquals("????", sfc.getFieldBinding());
    }
}

