/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.drools.testcoverage.functional;


import KieServices.Factory;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import org.drools.decisiontable.ExternalSpreadsheetCompiler;
import org.drools.template.DataProviderCompiler;
import org.drools.template.ObjectDataCompiler;
import org.drools.template.objects.ArrayDataProvider;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.TestConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests templates - providers, generating rules, performance.
 */
@RunWith(Parameterized.class)
public class TemplatesTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(TemplatesTest.class);

    private static final StringBuffer EXPECTED_RULES = new StringBuffer();

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public TemplatesTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    static {
        final String head = (((((((("package " + (TestConstants.PACKAGE_FUNCTIONAL)) + ";\n") + "import  ") + (TemplatesTest.class.getCanonicalName())) + ".Vegetable;\n") + "import  ") + (TemplatesTest.class.getCanonicalName())) + ".Taste;\n") + "global java.util.List list;\n\n";
        final String rule2_when = "rule \"is appropriate 2\"\n" + (("\twhen\n" + "\t\tVegetable( $name : name == \"carrot\", $field : weight >= 0 && <= 1000, $price : price <= 2, ") + "$taste : taste  == Taste.HORRIBLE )\n");
        final String rule2_then = "\tthen\n\t\tlist.add( $name );\nend\n\n";
        final String rule1_when = "rule \"is appropriate 1\"\n" + (("\twhen\n" + "\t\tVegetable( $name : name == \"cucumber\", $field : length >= 20 && <= 40, $price : price <= 15, ") + "$taste : taste  == Taste.EXCELENT )\n");
        final String rule1_then = "\tthen\n\t\tlist.add( $name );\nend\n\n";
        final String rule0_when = "rule \"is appropriate 0\"\n" + (("\twhen\n" + "\t\tVegetable( $name : name == \"tomato\", $field : weight >= 200 && <= 1000, $price : price <= 6, ") + "$taste : taste  == Taste.GOOD || == Taste.EXCELENT )\n");
        final String rule0_then = "\tthen\n\t\tlist.add( $name );\nend\n\n";
        TemplatesTest.EXPECTED_RULES.append(head);
        TemplatesTest.EXPECTED_RULES.append(rule2_when).append(rule2_then);
        TemplatesTest.EXPECTED_RULES.append(rule1_when).append(rule1_then);
        TemplatesTest.EXPECTED_RULES.append(rule0_when).append(rule0_then);
    }

    @Test
    public void loadingFromDLRObjsCorrectnessCheck() throws IOException {
        final KieServices kieServices = Factory.get();
        final Collection<TemplatesTest.ParamSet> cfl = new ArrayList<TemplatesTest.ParamSet>();
        cfl.add(new TemplatesTest.ParamSet("tomato", "weight", 200, 1000, 6, EnumSet.of(TemplatesTest.Taste.GOOD, TemplatesTest.Taste.EXCELENT)));
        cfl.add(new TemplatesTest.ParamSet("cucumber", "length", 20, 40, 15, EnumSet.of(TemplatesTest.Taste.EXCELENT)));
        cfl.add(new TemplatesTest.ParamSet("carrot", "weight", 0, 1000, 2, EnumSet.of(TemplatesTest.Taste.HORRIBLE)));
        final ObjectDataCompiler converter = new ObjectDataCompiler();
        try (InputStream resourceStream = kieServices.getResources().newClassPathResource("template_1.drl", getClass()).getInputStream()) {
            final String drl = converter.compile(cfl, resourceStream);
            // prints rules generated from template
            TemplatesTest.LOGGER.debug(drl);
            TemplatesTest.assertEqualsIgnoreWhitespace(TemplatesTest.EXPECTED_RULES.toString(), drl);
            testCorrectnessCheck(drl);
        }
    }

    @Test
    public void loadingFromDLRMapsCorrectnessCheck() throws IOException {
        final KieServices kieServices = Factory.get();
        final ObjectDataCompiler converter = new ObjectDataCompiler();
        try (InputStream resourceStream = kieServices.getResources().newClassPathResource("template_1.drl", getClass()).getInputStream()) {
            final String drl = converter.compile(getMapsParam(), resourceStream);
            // prints rules generated from template
            TemplatesTest.LOGGER.debug(drl);
            TemplatesTest.assertEqualsIgnoreWhitespace(TemplatesTest.EXPECTED_RULES.toString(), drl);
            testCorrectnessCheck(drl);
        }
    }

    @Test
    public void loadingFromDLRArrayCorrectnessCheck() throws Exception {
        final String[][] rows = new String[3][6];
        rows[0] = new String[]{ "tomato", "weight", "200", "1000", "6", "== Taste.GOOD || == Taste.EXCELENT" };
        rows[1] = new String[]{ "cucumber", "length", "20", "40", "15", "== Taste.EXCELENT" };
        rows[2] = new String[]{ "carrot", "weight", "0", "1000", "2", "== Taste.HORRIBLE" };
        final ArrayDataProvider adp = new ArrayDataProvider(rows);
        final DataProviderCompiler converter = new DataProviderCompiler();
        try (InputStream resourceStream = Factory.get().getResources().newClassPathResource("template_1.drl", getClass()).getInputStream()) {
            final String drl = converter.compile(adp, resourceStream);
            // prints rules generated from template
            TemplatesTest.LOGGER.debug(drl);
            TemplatesTest.assertEqualsIgnoreWhitespace(TemplatesTest.EXPECTED_RULES.toString(), drl);
            testCorrectnessCheck(drl);
        }
    }

    @Test
    public void loadingFromDLRSpreadsheetCorrectnessCheck() throws Exception {
        final ExternalSpreadsheetCompiler converter = new ExternalSpreadsheetCompiler();
        final KieServices kieServices = Factory.get();
        // the data we are interested in starts at row 1, column 1 (e.g. A1)
        try (InputStream spreadSheetStream = kieServices.getResources().newClassPathResource("template1_spreadsheet.xls", getClass()).getInputStream();InputStream templateStream = kieServices.getResources().newClassPathResource("template_1.drl", getClass()).getInputStream()) {
            final String drl = converter.compile(spreadSheetStream, templateStream, 1, 1);
            // prints rules generated from template
            TemplatesTest.LOGGER.debug(drl);
            TemplatesTest.assertEqualsIgnoreWhitespace(TemplatesTest.EXPECTED_RULES.toString(), drl);
            testCorrectnessCheck(drl);
        }
    }

    @Test(timeout = 30000L)
    public void OneRuleManyRows() throws IOException {
        final KieServices kieServices = Factory.get();
        final Collection<TemplatesTest.ParamSet> cfl = new ArrayList<TemplatesTest.ParamSet>();
        cfl.add(new TemplatesTest.ParamSet("tomato", "weight", 200, 1000, 6, EnumSet.of(TemplatesTest.Taste.GOOD, TemplatesTest.Taste.EXCELENT)));
        final ObjectDataCompiler converter = new ObjectDataCompiler();
        try (InputStream resourceStream = kieServices.getResources().newClassPathResource("template_1.drl", getClass()).getInputStream()) {
            final String drl = converter.compile(cfl, resourceStream);
            // prints rules generated from template
            TemplatesTest.LOGGER.debug(drl);
            testManyRows(drl, 0, 1);
        }
    }

    @Test(timeout = 30000L)
    public void TenRulesManyRows() throws IOException {
        final KieServices kieServices = Factory.get();
        final ObjectDataCompiler converter = new ObjectDataCompiler();
        try (InputStream resourceStream = kieServices.getResources().newClassPathResource("template_1.drl", getClass()).getInputStream()) {
            final String drl = converter.compile(generateParamSetCollection(1), resourceStream);
            // prints rules generated from template
            TemplatesTest.LOGGER.debug(drl);
            testManyRows(drl, 500, 10);
        }
    }

    @Test(timeout = 30000L)
    public void OneTemplateManyRules() throws IOException {
        final KieServices kieServices = Factory.get();
        final ObjectDataCompiler converter = new ObjectDataCompiler();
        try (InputStream resourceStream = kieServices.getResources().newClassPathResource("template_1.drl", getClass()).getInputStream()) {
            final String drl = converter.compile(generateParamSetCollection(5), resourceStream);
            // prints rules generated from template
            TemplatesTest.LOGGER.debug(drl);
            testManyRules(drl, 50);
        }
    }

    @Test(timeout = 30000L)
    public void TenTemplatesManyRules() throws IOException {
        final KieServices kieServices = Factory.get();
        final ObjectDataCompiler converter = new ObjectDataCompiler();
        try (InputStream resourceStream = kieServices.getResources().newClassPathResource("template_2.drl", getClass()).getInputStream()) {
            final String drl = converter.compile(generateParamSetCollection(5), resourceStream);
            // prints rules generated from template
            TemplatesTest.LOGGER.debug(drl);
            testManyRules(drl, 500);
        }
    }

    public static class ParamSet {
        private String name;

        private String field;

        private int fieldLower;

        private int fieldUpper;

        private int price;

        private EnumSet<TemplatesTest.Taste> tasteSet;

        public ParamSet(String name, String field, int fieldLower, int fieldUpper, int price, EnumSet<TemplatesTest.Taste> tasteSet) {
            this.name = name;
            this.field = field;
            this.fieldLower = fieldLower;
            this.fieldUpper = fieldUpper;
            this.tasteSet = tasteSet;
            this.price = price;
        }

        public String getName() {
            return name;
        }

        public String getField() {
            return field;
        }

        public int getFieldLower() {
            return fieldLower;
        }

        public int getFieldUpper() {
            return fieldUpper;
        }

        public int getPrice() {
            return price;
        }

        public String getTastes() {
            StringBuilder sb = new StringBuilder();
            String conn = "";
            for (TemplatesTest.Taste t : tasteSet) {
                sb.append(conn).append(" == Taste.").append(t);
                conn = " ||";
            }
            return sb.toString();
        }
    }

    public class Vegetable {
        private String name;

        private int weight;

        private int length;

        private int price;

        private TemplatesTest.Taste taste;

        public Vegetable(String name, int weight, int length, int price, TemplatesTest.Taste taste) {
            this.name = name;
            this.weight = weight;
            this.length = length;
            this.taste = taste;
            this.price = price;
        }

        public String getName() {
            return this.name;
        }

        public int getWeight() {
            return this.weight;
        }

        public int getPrice() {
            return this.price;
        }

        public int getLength() {
            return this.length;
        }

        public TemplatesTest.Taste getTaste() {
            return this.taste;
        }
    }

    public enum Taste {

        HORRIBLE,
        BAD,
        AVERAGE,
        GOOD,
        EXCELENT;}
}

