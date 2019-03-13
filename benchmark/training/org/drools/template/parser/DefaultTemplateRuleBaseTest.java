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
package org.drools.template.parser;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.rule.GroupElement;
import org.drools.core.rule.IndexableConstraint;
import org.drools.core.rule.Pattern;
import org.drools.core.spi.Constraint;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.definition.rule.Rule;


public class DefaultTemplateRuleBaseTest {
    @SuppressWarnings("unchecked")
    @Test
    public void testSimpleTemplate() throws Exception {
        TemplateContainer tc = new TemplateContainer() {
            private Column[] columns = new Column[]{ new LongColumn("column1"), new LongColumn("column2"), new StringColumn("column3") };

            public Column[] getColumns() {
                return columns;
            }

            public String getHeader() {
                return null;
            }

            public Map<String, RuleTemplate> getTemplates() {
                Map<String, RuleTemplate> templates = new HashMap<String, RuleTemplate>();
                RuleTemplate ruleTemplate = new RuleTemplate("template1", this);
                ruleTemplate.addColumn("column1 == 10");
                ruleTemplate.addColumn("column2 < 5 || > 20");
                ruleTemplate.addColumn("column3 == \"xyz\"");
                templates.put("template1", ruleTemplate);
                return templates;
            }

            public Column getColumn(String name) {
                return columns[((Integer.parseInt(name.substring(6))) - 1)];
            }
        };
        DefaultTemplateRuleBase ruleBase = new DefaultTemplateRuleBase(tc);
        InternalKnowledgePackage[] packages = getPackages();
        Assert.assertEquals(1, packages.length);
        Map<String, String> globals = packages[0].getGlobals();
        Assert.assertEquals(DefaultGenerator.class.getName(), globals.get("generator"));
        Collection<Rule> rules = packages[0].getRules();
        Assert.assertEquals(1, rules.size());
        Assert.assertEquals("template1", rules.iterator().next().getName());
        GroupElement lhs = getLhs();
        // when
        // r : Row()
        // column1 : Column(name == "column1")
        // exists LongCell(row == r, column == column1, value == 10)
        // column2 : Column(name == "column2")
        // exists LongCell(row == r, column == column2, value < 5 | > 20)
        // column3 : Column(name == "column3")
        // exists StringCell(row == r, column == column3, value == "xyz")
        Assert.assertEquals(7, lhs.getChildren().size());
        Pattern pattern = ((Pattern) (lhs.getChildren().get(1)));
        Assert.assertEquals(1, pattern.getConstraints().size());
        Constraint constraint = pattern.getConstraints().get(0);
        GroupElement exists = ((GroupElement) (lhs.getChildren().get(2)));
        pattern = ((Pattern) (exists.getChildren().get(0)));
        Assert.assertEquals(3, pattern.getConstraints().size());
        IndexableConstraint vconstraint = ((IndexableConstraint) (pattern.getConstraints().get(1)));
        Assert.assertEquals(Column.class, vconstraint.getFieldIndex().getExtractor().getExtractToClass());
        Assert.assertEquals("column1", vconstraint.getRequiredDeclarations()[0].getIdentifier());
        pattern = ((Pattern) (lhs.getChildren().get(3)));
        Assert.assertEquals(1, pattern.getConstraints().size());
        constraint = pattern.getConstraints().get(0);
        exists = ((GroupElement) (lhs.getChildren().get(4)));
        pattern = ((Pattern) (exists.getChildren().get(0)));
        Assert.assertEquals(3, pattern.getConstraints().size());
        vconstraint = ((IndexableConstraint) (pattern.getConstraints().get(1)));
        Assert.assertEquals(Column.class, vconstraint.getFieldIndex().getExtractor().getExtractToClass());
        Assert.assertEquals("column2", vconstraint.getRequiredDeclarations()[0].getIdentifier());
        pattern = ((Pattern) (lhs.getChildren().get(5)));
        Assert.assertEquals(1, pattern.getConstraints().size());
        constraint = pattern.getConstraints().get(0);
        exists = ((GroupElement) (lhs.getChildren().get(6)));
        pattern = ((Pattern) (exists.getChildren().get(0)));
        Assert.assertEquals(3, pattern.getConstraints().size());
        vconstraint = ((IndexableConstraint) (pattern.getConstraints().get(1)));
        Assert.assertEquals(Column.class, vconstraint.getFieldIndex().getExtractor().getExtractToClass());
        Assert.assertEquals("column3", vconstraint.getRequiredDeclarations()[0].getIdentifier());
    }
}

