/**
 * Copyright 2009-2015 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */


package org.apache.ibatis.parsing;


public class AmplXPathParserTest {
    @org.junit.Test
    public void shouldTestXPathParserMethods() throws java.lang.Exception {
        java.lang.String resource = "resources/nodelet_test.xml";
        java.io.InputStream inputStream = org.apache.ibatis.io.Resources.getResourceAsStream(resource);
        org.apache.ibatis.parsing.XPathParser parser = new org.apache.ibatis.parsing.XPathParser(inputStream, false, null, null);
        org.junit.Assert.assertEquals(((java.lang.Long) (1970L)), parser.evalLong("/employee/birth_date/year"));
        org.junit.Assert.assertEquals(((short) (6)), ((short) (parser.evalShort("/employee/birth_date/month"))));
        org.junit.Assert.assertEquals(((java.lang.Integer) (15)), parser.evalInteger("/employee/birth_date/day"));
        org.junit.Assert.assertEquals(((java.lang.Float) (5.8F)), parser.evalFloat("/employee/height"));
        org.junit.Assert.assertEquals(((java.lang.Double) (5.8)), parser.evalDouble("/employee/height"));
        org.junit.Assert.assertEquals("${id_var}", parser.evalString("/employee/@id"));
        org.junit.Assert.assertEquals(java.lang.Boolean.TRUE, parser.evalBoolean("/employee/active"));
        org.junit.Assert.assertEquals("<id>${id_var}</id>", parser.evalNode("/employee/@id").toString().trim());
        org.junit.Assert.assertEquals(7, parser.evalNodes("/employee/*").size());
        org.apache.ibatis.parsing.XNode node = parser.evalNode("/employee/height");
        org.junit.Assert.assertEquals("employee/height", node.getPath());
        org.junit.Assert.assertEquals("employee[${id_var}]_height", node.getValueBasedIdentifier());
    }

    /* amplification of org.apache.ibatis.parsing.XPathParserTest#shouldTestXPathParserMethods */
    @org.junit.Test(timeout = 1000)
    public void shouldTestXPathParserMethods_cf275_failAssert12() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String resource = "resources/nodelet_test.xml";
            java.io.InputStream inputStream = org.apache.ibatis.io.Resources.getResourceAsStream(resource);
            org.apache.ibatis.parsing.XPathParser parser = new org.apache.ibatis.parsing.XPathParser(inputStream, false, null, null);
            // MethodAssertGenerator build local variable
            Object o_6_0 = parser.evalLong("/employee/birth_date/year");
            // MethodAssertGenerator build local variable
            Object o_8_0 = ((short) (parser.evalShort("/employee/birth_date/month")));
            // MethodAssertGenerator build local variable
            Object o_10_0 = parser.evalInteger("/employee/birth_date/day");
            // MethodAssertGenerator build local variable
            Object o_12_0 = parser.evalFloat("/employee/height");
            // MethodAssertGenerator build local variable
            Object o_14_0 = parser.evalDouble("/employee/height");
            // MethodAssertGenerator build local variable
            Object o_16_0 = parser.evalString("/employee/@id");
            // MethodAssertGenerator build local variable
            Object o_18_0 = parser.evalBoolean("/employee/active");
            // MethodAssertGenerator build local variable
            Object o_20_0 = parser.evalNode("/employee/@id").toString().trim();
            // MethodAssertGenerator build local variable
            Object o_24_0 = parser.evalNodes("/employee/*").size();
            org.apache.ibatis.parsing.XNode node = parser.evalNode("/employee/height");
            // MethodAssertGenerator build local variable
            Object o_29_0 = node.getPath();
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_49 = new java.lang.String();
            // StatementAdderMethod cloned existing statement
            parser.evalLong(vc_49);
            // MethodAssertGenerator build local variable
            Object o_35_0 = node.getValueBasedIdentifier();
            org.junit.Assert.fail("shouldTestXPathParserMethods_cf275 should have thrown BuilderException");
        } catch (org.apache.ibatis.builder.BuilderException eee) {
        }
    }

    /* amplification of org.apache.ibatis.parsing.XPathParserTest#shouldTestXPathParserMethods */
    @org.junit.Test
    public void shouldTestXPathParserMethods_literalMutation6_failAssert16() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String resource = "resources/nodelet_test.xml";
            java.io.InputStream inputStream = org.apache.ibatis.io.Resources.getResourceAsStream(resource);
            org.apache.ibatis.parsing.XPathParser parser = new org.apache.ibatis.parsing.XPathParser(inputStream, true, null, null);
            // MethodAssertGenerator build local variable
            Object o_7_0 = parser.evalLong("/employee/birth_date/year");
            // MethodAssertGenerator build local variable
            Object o_9_0 = ((short) (parser.evalShort("/employee/birth_date/month")));
            // MethodAssertGenerator build local variable
            Object o_11_0 = parser.evalInteger("/employee/birth_date/day");
            // MethodAssertGenerator build local variable
            Object o_13_0 = parser.evalFloat("/employee/height");
            // MethodAssertGenerator build local variable
            Object o_15_0 = parser.evalDouble("/employee/height");
            // MethodAssertGenerator build local variable
            Object o_17_0 = parser.evalString("/employee/@id");
            // MethodAssertGenerator build local variable
            Object o_19_0 = parser.evalBoolean("/employee/active");
            // MethodAssertGenerator build local variable
            Object o_21_0 = parser.evalNode("/employee/@id").toString().trim();
            // MethodAssertGenerator build local variable
            Object o_25_0 = parser.evalNodes("/employee/*").size();
            org.apache.ibatis.parsing.XNode node = parser.evalNode("/employee/height");
            // MethodAssertGenerator build local variable
            Object o_30_0 = node.getPath();
            // MethodAssertGenerator build local variable
            Object o_32_0 = node.getValueBasedIdentifier();
            org.junit.Assert.fail("shouldTestXPathParserMethods_literalMutation6 should have thrown BuilderException");
        } catch (org.apache.ibatis.builder.BuilderException eee) {
        }
    }

    /* amplification of org.apache.ibatis.parsing.XPathParserTest#shouldTestXPathParserMethods */
    @org.junit.Test
    public void shouldTestXPathParserMethods_literalMutation4_failAssert27() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String resource = "resources/nodelet_t*st.xml";
            java.io.InputStream inputStream = org.apache.ibatis.io.Resources.getResourceAsStream(resource);
            org.apache.ibatis.parsing.XPathParser parser = new org.apache.ibatis.parsing.XPathParser(inputStream, false, null, null);
            // MethodAssertGenerator build local variable
            Object o_6_0 = parser.evalLong("/employee/birth_date/year");
            // MethodAssertGenerator build local variable
            Object o_8_0 = ((short) (parser.evalShort("/employee/birth_date/month")));
            // MethodAssertGenerator build local variable
            Object o_10_0 = parser.evalInteger("/employee/birth_date/day");
            // MethodAssertGenerator build local variable
            Object o_12_0 = parser.evalFloat("/employee/height");
            // MethodAssertGenerator build local variable
            Object o_14_0 = parser.evalDouble("/employee/height");
            // MethodAssertGenerator build local variable
            Object o_16_0 = parser.evalString("/employee/@id");
            // MethodAssertGenerator build local variable
            Object o_18_0 = parser.evalBoolean("/employee/active");
            // MethodAssertGenerator build local variable
            Object o_20_0 = parser.evalNode("/employee/@id").toString().trim();
            // MethodAssertGenerator build local variable
            Object o_24_0 = parser.evalNodes("/employee/*").size();
            org.apache.ibatis.parsing.XNode node = parser.evalNode("/employee/height");
            // MethodAssertGenerator build local variable
            Object o_29_0 = node.getPath();
            // MethodAssertGenerator build local variable
            Object o_31_0 = node.getValueBasedIdentifier();
            org.junit.Assert.fail("shouldTestXPathParserMethods_literalMutation4 should have thrown IOException");
        } catch (java.io.IOException eee) {
        }
    }

    /* amplification of org.apache.ibatis.parsing.XPathParserTest#shouldTestXPathParserMethods */
    @org.junit.Test(timeout = 1000)
    public void shouldTestXPathParserMethods_cf371_failAssert18_literalMutation609_failAssert34() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                java.lang.String resource = "resources/nodelet_test.xml";
                java.io.InputStream inputStream = org.apache.ibatis.io.Resources.getResourceAsStream(resource);
                org.apache.ibatis.parsing.XPathParser parser = new org.apache.ibatis.parsing.XPathParser(inputStream, true, null, null);
                // MethodAssertGenerator build local variable
                Object o_6_0 = parser.evalLong("/employee/birth_date/year");
                // MethodAssertGenerator build local variable
                Object o_8_0 = ((short) (parser.evalShort("/employee/birth_date/month")));
                // MethodAssertGenerator build local variable
                Object o_10_0 = parser.evalInteger("/employee/birth_date/day");
                // MethodAssertGenerator build local variable
                Object o_12_0 = parser.evalFloat("/employee/height");
                // MethodAssertGenerator build local variable
                Object o_14_0 = parser.evalDouble("/employee/height");
                // MethodAssertGenerator build local variable
                Object o_16_0 = parser.evalString("/employee/@id");
                // MethodAssertGenerator build local variable
                Object o_18_0 = parser.evalBoolean("/employee/active");
                // MethodAssertGenerator build local variable
                Object o_20_0 = parser.evalNode("/employee/@id").toString().trim();
                // MethodAssertGenerator build local variable
                Object o_24_0 = parser.evalNodes("/employee/*").size();
                org.apache.ibatis.parsing.XNode node = parser.evalNode("/employee/height");
                // MethodAssertGenerator build local variable
                Object o_29_0 = node.getPath();
                // StatementAdderOnAssert create null value
                java.lang.String vc_74 = (java.lang.String)null;
                // StatementAdderOnAssert create null value
                java.lang.Object vc_72 = (java.lang.Object)null;
                // StatementAdderOnAssert create null value
                org.apache.ibatis.parsing.XPathParser vc_70 = (org.apache.ibatis.parsing.XPathParser)null;
                // StatementAdderMethod cloned existing statement
                vc_70.evalNodes(vc_72, vc_74);
                // MethodAssertGenerator build local variable
                Object o_39_0 = node.getValueBasedIdentifier();
                org.junit.Assert.fail("shouldTestXPathParserMethods_cf371 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("shouldTestXPathParserMethods_cf371_failAssert18_literalMutation609 should have thrown BuilderException");
        } catch (org.apache.ibatis.builder.BuilderException eee) {
        }
    }

    /* amplification of org.apache.ibatis.parsing.XPathParserTest#shouldTestXPathParserMethods */
    @org.junit.Test(timeout = 1000)
    public void shouldTestXPathParserMethods_cf452_literalMutation706_failAssert27() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String resource = "resources/nodelet_test.xml";
            java.io.InputStream inputStream = org.apache.ibatis.io.Resources.getResourceAsStream(resource);
            org.apache.ibatis.parsing.XPathParser parser = new org.apache.ibatis.parsing.XPathParser(inputStream, false, null, null);
            // MethodAssertGenerator build local variable
            Object o_6_0 = parser.evalLong("/employee/birth_date/year");
            // MethodAssertGenerator build local variable
            Object o_8_0 = ((short) (parser.evalShort("/employee/birth_date/month")));
            // MethodAssertGenerator build local variable
            Object o_10_0 = parser.evalInteger("/employee/birth_date/day");
            // MethodAssertGenerator build local variable
            Object o_12_0 = parser.evalFloat("/employee/height");
            // MethodAssertGenerator build local variable
            Object o_14_0 = parser.evalDouble("${id)var}");
            // MethodAssertGenerator build local variable
            Object o_16_0 = parser.evalString("/employee/@id");
            // MethodAssertGenerator build local variable
            Object o_18_0 = parser.evalBoolean("/employee/active");
            // MethodAssertGenerator build local variable
            Object o_20_0 = parser.evalNode("/employee/@id").toString().trim();
            // MethodAssertGenerator build local variable
            Object o_24_0 = parser.evalNodes("/employee/*").size();
            org.apache.ibatis.parsing.XNode node = parser.evalNode("/employee/height");
            // MethodAssertGenerator build local variable
            Object o_29_0 = node.getPath();
            // AssertGenerator replace invocation
            org.apache.ibatis.parsing.XNode o_shouldTestXPathParserMethods_cf452__31 = // StatementAdderMethod cloned existing statement
parser.evalNode(resource);
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(o_shouldTestXPathParserMethods_cf452__31);
            // MethodAssertGenerator build local variable
            Object o_35_0 = node.getValueBasedIdentifier();
            org.junit.Assert.fail("shouldTestXPathParserMethods_cf452_literalMutation706 should have thrown BuilderException");
        } catch (org.apache.ibatis.builder.BuilderException eee) {
        }
    }

    /* amplification of org.apache.ibatis.parsing.XPathParserTest#shouldTestXPathParserMethods */
    @org.junit.Test
    public void shouldTestXPathParserMethods_literalMutation1_failAssert61_literalMutation1422_failAssert49() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                java.lang.String resource = "${first_name}${initial}${last_name}";
                java.io.InputStream inputStream = org.apache.ibatis.io.Resources.getResourceAsStream(resource);
                org.apache.ibatis.parsing.XPathParser parser = new org.apache.ibatis.parsing.XPathParser(inputStream, false, null, null);
                // MethodAssertGenerator build local variable
                Object o_6_0 = parser.evalLong("/employee/birth_date/year");
                // MethodAssertGenerator build local variable
                Object o_8_0 = ((short) (parser.evalShort("/employee/birth_date/month")));
                // MethodAssertGenerator build local variable
                Object o_10_0 = parser.evalInteger("/employee/birth_date/day");
                // MethodAssertGenerator build local variable
                Object o_12_0 = parser.evalFloat("/employee/height");
                // MethodAssertGenerator build local variable
                Object o_14_0 = parser.evalDouble("/employee/height");
                // MethodAssertGenerator build local variable
                Object o_16_0 = parser.evalString("/employee/@id");
                // MethodAssertGenerator build local variable
                Object o_18_0 = parser.evalBoolean("/employee/active");
                // MethodAssertGenerator build local variable
                Object o_20_0 = parser.evalNode("/employee/@id").toString().trim();
                // MethodAssertGenerator build local variable
                Object o_24_0 = parser.evalNodes("/employee/*").size();
                org.apache.ibatis.parsing.XNode node = parser.evalNode("/employee/height");
                // MethodAssertGenerator build local variable
                Object o_29_0 = node.getPath();
                // MethodAssertGenerator build local variable
                Object o_31_0 = node.getValueBasedIdentifier();
                org.junit.Assert.fail("shouldTestXPathParserMethods_literalMutation1 should have thrown BuilderException");
            } catch (org.apache.ibatis.builder.BuilderException eee) {
            }
            org.junit.Assert.fail("shouldTestXPathParserMethods_literalMutation1_failAssert61_literalMutation1422 should have thrown IOException");
        } catch (java.io.IOException eee) {
        }
    }

    /* amplification of org.apache.ibatis.parsing.XPathParserTest#shouldTestXPathParserMethods */
    @org.junit.Test(timeout = 1000)
    public void shouldTestXPathParserMethods_cf452_cf1081_literalMutation4244_failAssert67() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String resource = "resources/nodelet_test.xml";
            java.io.InputStream inputStream = org.apache.ibatis.io.Resources.getResourceAsStream(resource);
            org.apache.ibatis.parsing.XPathParser parser = new org.apache.ibatis.parsing.XPathParser(inputStream, true, null, null);
            // MethodAssertGenerator build local variable
            Object o_7_0 = parser.evalLong("/employee/birth_date/year");
            // MethodAssertGenerator build local variable
            Object o_9_0 = ((short) (parser.evalShort("/employee/birth_date/month")));
            // MethodAssertGenerator build local variable
            Object o_11_0 = parser.evalInteger("/employee/birth_date/day");
            // MethodAssertGenerator build local variable
            Object o_13_0 = parser.evalFloat("/employee/height");
            // MethodAssertGenerator build local variable
            Object o_15_0 = parser.evalDouble("/employee/height");
            // MethodAssertGenerator build local variable
            Object o_17_0 = parser.evalString("/employee/@id");
            // MethodAssertGenerator build local variable
            Object o_19_0 = parser.evalBoolean("/employee/active");
            // MethodAssertGenerator build local variable
            Object o_21_0 = parser.evalNode("/employee/@id").toString().trim();
            // MethodAssertGenerator build local variable
            Object o_25_0 = parser.evalNodes("/employee/*").size();
            org.apache.ibatis.parsing.XNode node = parser.evalNode("/employee/height");
            // MethodAssertGenerator build local variable
            Object o_30_0 = node.getPath();
            // AssertGenerator replace invocation
            org.apache.ibatis.parsing.XNode o_shouldTestXPathParserMethods_cf452__31 = // StatementAdderMethod cloned existing statement
parser.evalNode(resource);
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(o_shouldTestXPathParserMethods_cf452__31);
            // AssertGenerator replace invocation
            java.util.List<org.apache.ibatis.parsing.XNode> o_shouldTestXPathParserMethods_cf452_cf1081__35 = // StatementAdderMethod cloned existing statement
parser.evalNodes(resource);
            // AssertGenerator add assertion
            java.util.ArrayList collection_1375842180 = new java.util.ArrayList<Object>();
	junit.framework.Assert.assertEquals(collection_1375842180, o_shouldTestXPathParserMethods_cf452_cf1081__35);;
            // MethodAssertGenerator build local variable
            Object o_40_0 = node.getValueBasedIdentifier();
            org.junit.Assert.fail("shouldTestXPathParserMethods_cf452_cf1081_literalMutation4244 should have thrown BuilderException");
        } catch (org.apache.ibatis.builder.BuilderException eee) {
        }
    }

    /* amplification of org.apache.ibatis.parsing.XPathParserTest#shouldTestXPathParserMethods */
    @org.junit.Test(timeout = 1000)
    public void shouldTestXPathParserMethods_cf452_cf1081_literalMutation4273_failAssert6() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String resource = "resources/nodelet_test.xml";
            java.io.InputStream inputStream = org.apache.ibatis.io.Resources.getResourceAsStream(resource);
            org.apache.ibatis.parsing.XPathParser parser = new org.apache.ibatis.parsing.XPathParser(inputStream, false, null, null);
            // MethodAssertGenerator build local variable
            Object o_6_0 = parser.evalLong("/employee/birth_date/year");
            // MethodAssertGenerator build local variable
            Object o_8_0 = ((short) (parser.evalShort("/employee/birth_date/month")));
            // MethodAssertGenerator build local variable
            Object o_10_0 = parser.evalInteger("/employee/birth_date/day");
            // MethodAssertGenerator build local variable
            Object o_12_0 = parser.evalFloat("/employee/height");
            // MethodAssertGenerator build local variable
            Object o_14_0 = parser.evalDouble("${id_v?ar}");
            // MethodAssertGenerator build local variable
            Object o_16_0 = parser.evalString("/employee/@id");
            // MethodAssertGenerator build local variable
            Object o_18_0 = parser.evalBoolean("/employee/active");
            // MethodAssertGenerator build local variable
            Object o_20_0 = parser.evalNode("/employee/@id").toString().trim();
            // MethodAssertGenerator build local variable
            Object o_24_0 = parser.evalNodes("/employee/*").size();
            org.apache.ibatis.parsing.XNode node = parser.evalNode("/employee/height");
            // MethodAssertGenerator build local variable
            Object o_29_0 = node.getPath();
            // AssertGenerator replace invocation
            org.apache.ibatis.parsing.XNode o_shouldTestXPathParserMethods_cf452__31 = // StatementAdderMethod cloned existing statement
parser.evalNode(resource);
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(o_shouldTestXPathParserMethods_cf452__31);
            // AssertGenerator replace invocation
            java.util.List<org.apache.ibatis.parsing.XNode> o_shouldTestXPathParserMethods_cf452_cf1081__35 = // StatementAdderMethod cloned existing statement
parser.evalNodes(resource);
            // AssertGenerator add assertion
            java.util.ArrayList collection_1375842180 = new java.util.ArrayList<Object>();
	junit.framework.Assert.assertEquals(collection_1375842180, o_shouldTestXPathParserMethods_cf452_cf1081__35);;
            // MethodAssertGenerator build local variable
            Object o_39_0 = node.getValueBasedIdentifier();
            org.junit.Assert.fail("shouldTestXPathParserMethods_cf452_cf1081_literalMutation4273 should have thrown BuilderException");
        } catch (org.apache.ibatis.builder.BuilderException eee) {
        }
    }
}

