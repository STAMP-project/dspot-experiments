/**
 * Copyright 2009-2016 the original author or authors.
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


package org.apache.ibatis.builder;


// @Test
// public void shouldNotLoadTheSameNamespaceFromTwoResourcesWithDifferentNames() throws Exception {
// Configuration configuration = new Configuration();
// String resource = "org/apache/ibatis/builder/AuthorMapper.xml";
// InputStream inputStream = Resources.getResourceAsStream(resource);
// XMLMapperBuilder builder = new XMLMapperBuilder(inputStream, configuration, "name1", configuration.getSqlFragments());
// builder.parse();
// InputStream inputStream2 = Resources.getResourceAsStream(resource);
// XMLMapperBuilder builder2 = new XMLMapperBuilder(inputStream2, configuration, "name2", configuration.getSqlFragments());
// builder2.parse();
// }
public class AmplXmlMapperBuilderTest {
    @org.junit.Rule
    public org.junit.rules.ExpectedException expectedException = org.junit.rules.ExpectedException.none();

    @org.junit.Test
    public void shouldSuccessfullyLoadXMLMapperFile() throws java.lang.Exception {
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        java.lang.String resource = "org/apache/ibatis/builder/AuthorMapper.xml";
        java.io.InputStream inputStream = org.apache.ibatis.io.Resources.getResourceAsStream(resource);
        org.apache.ibatis.builder.xml.XMLMapperBuilder builder = new org.apache.ibatis.builder.xml.XMLMapperBuilder(inputStream, configuration, resource, configuration.getSqlFragments());
        builder.parse();
    }

    @org.junit.Test
    public void mappedStatementWithOptions() throws java.lang.Exception {
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        java.lang.String resource = "org/apache/ibatis/builder/AuthorMapper.xml";
        java.io.InputStream inputStream = org.apache.ibatis.io.Resources.getResourceAsStream(resource);
        org.apache.ibatis.builder.xml.XMLMapperBuilder builder = new org.apache.ibatis.builder.xml.XMLMapperBuilder(inputStream, configuration, resource, configuration.getSqlFragments());
        builder.parse();
        org.apache.ibatis.mapping.MappedStatement mappedStatement = configuration.getMappedStatement("selectWithOptions");
        org.junit.Assert.assertThat(mappedStatement.getFetchSize(), org.hamcrest.CoreMatchers.is(200));
        org.junit.Assert.assertThat(mappedStatement.getTimeout(), org.hamcrest.CoreMatchers.is(10));
        org.junit.Assert.assertThat(mappedStatement.getStatementType(), org.hamcrest.CoreMatchers.is(org.apache.ibatis.mapping.StatementType.PREPARED));
        org.junit.Assert.assertThat(mappedStatement.getResultSetType(), org.hamcrest.CoreMatchers.is(org.apache.ibatis.mapping.ResultSetType.SCROLL_SENSITIVE));
        org.junit.Assert.assertThat(mappedStatement.isFlushCacheRequired(), org.hamcrest.CoreMatchers.is(false));
        org.junit.Assert.assertThat(mappedStatement.isUseCache(), org.hamcrest.CoreMatchers.is(false));
    }

    @org.junit.Test
    public void parseExpression() {
        org.apache.ibatis.builder.BaseBuilder builder = new org.apache.ibatis.builder.BaseBuilder(new org.apache.ibatis.session.Configuration()) {
            {
            }
        };
        {
            java.util.regex.Pattern pattern = builder.parseExpression("[0-9]", "[a-z]");
            org.junit.Assert.assertThat(pattern.matcher("0").find(), org.hamcrest.CoreMatchers.is(true));
            org.junit.Assert.assertThat(pattern.matcher("a").find(), org.hamcrest.CoreMatchers.is(false));
        }
        {
            java.util.regex.Pattern pattern = builder.parseExpression(null, "[a-z]");
            org.junit.Assert.assertThat(pattern.matcher("0").find(), org.hamcrest.CoreMatchers.is(false));
            org.junit.Assert.assertThat(pattern.matcher("a").find(), org.hamcrest.CoreMatchers.is(true));
        }
    }

    @org.junit.Test
    public void resolveJdbcTypeWithUndefinedValue() {
        org.apache.ibatis.builder.BaseBuilder builder = new org.apache.ibatis.builder.BaseBuilder(new org.apache.ibatis.session.Configuration()) {
            {
            }
        };
        expectedException.expect(org.apache.ibatis.builder.BuilderException.class);
        expectedException.expectMessage(org.hamcrest.CoreMatchers.startsWith("Error resolving JdbcType. Cause: java.lang.IllegalArgumentException: No enum"));
        expectedException.expectMessage(org.hamcrest.CoreMatchers.endsWith("org.apache.ibatis.type.JdbcType.aaa"));
        builder.resolveJdbcType("aaa");
    }

    @org.junit.Test
    public void resolveResultSetTypeWithUndefinedValue() {
        org.apache.ibatis.builder.BaseBuilder builder = new org.apache.ibatis.builder.BaseBuilder(new org.apache.ibatis.session.Configuration()) {
            {
            }
        };
        expectedException.expect(org.apache.ibatis.builder.BuilderException.class);
        expectedException.expectMessage(org.hamcrest.CoreMatchers.startsWith("Error resolving ResultSetType. Cause: java.lang.IllegalArgumentException: No enum"));
        expectedException.expectMessage(org.hamcrest.CoreMatchers.endsWith("org.apache.ibatis.mapping.ResultSetType.bbb"));
        builder.resolveResultSetType("bbb");
    }

    @org.junit.Test
    public void resolveParameterModeWithUndefinedValue() {
        org.apache.ibatis.builder.BaseBuilder builder = new org.apache.ibatis.builder.BaseBuilder(new org.apache.ibatis.session.Configuration()) {
            {
            }
        };
        expectedException.expect(org.apache.ibatis.builder.BuilderException.class);
        expectedException.expectMessage(org.hamcrest.CoreMatchers.startsWith("Error resolving ParameterMode. Cause: java.lang.IllegalArgumentException: No enum"));
        expectedException.expectMessage(org.hamcrest.CoreMatchers.endsWith("org.apache.ibatis.mapping.ParameterMode.ccc"));
        builder.resolveParameterMode("ccc");
    }

    @org.junit.Test
    public void createInstanceWithAbstractClass() {
        org.apache.ibatis.builder.BaseBuilder builder = new org.apache.ibatis.builder.BaseBuilder(new org.apache.ibatis.session.Configuration()) {
            {
            }
        };
        expectedException.expect(org.apache.ibatis.builder.BuilderException.class);
        expectedException.expectMessage(org.hamcrest.CoreMatchers.is("Error creating instance. Cause: java.lang.InstantiationException: org.apache.ibatis.builder.BaseBuilder"));
        builder.createInstance("org.apache.ibatis.builder.BaseBuilder");
    }

    @org.junit.Test
    public void resolveClassWithNotFound() {
        org.apache.ibatis.builder.BaseBuilder builder = new org.apache.ibatis.builder.BaseBuilder(new org.apache.ibatis.session.Configuration()) {
            {
            }
        };
        expectedException.expect(org.apache.ibatis.builder.BuilderException.class);
        expectedException.expectMessage(org.hamcrest.CoreMatchers.is("Error resolving class. Cause: org.apache.ibatis.type.TypeException: Could not resolve type alias 'ddd'.  Cause: java.lang.ClassNotFoundException: Cannot find class: ddd"));
        builder.resolveClass("ddd");
    }

    @org.junit.Test
    public void resolveTypeHandlerTypeHandlerAliasIsNull() {
        org.apache.ibatis.builder.BaseBuilder builder = new org.apache.ibatis.builder.BaseBuilder(new org.apache.ibatis.session.Configuration()) {
            {
            }
        };
        org.apache.ibatis.type.TypeHandler<?> typeHandler = builder.resolveTypeHandler(java.lang.String.class, ((java.lang.String) (null)));
        org.junit.Assert.assertThat(typeHandler, org.hamcrest.CoreMatchers.nullValue());
    }

    @org.junit.Test
    public void resolveTypeHandlerNoAssignable() {
        org.apache.ibatis.builder.BaseBuilder builder = new org.apache.ibatis.builder.BaseBuilder(new org.apache.ibatis.session.Configuration()) {
            {
            }
        };
        expectedException.expect(org.apache.ibatis.builder.BuilderException.class);
        expectedException.expectMessage(org.hamcrest.CoreMatchers.is("Type java.lang.Integer is not a valid TypeHandler because it does not implement TypeHandler interface"));
        builder.resolveTypeHandler(java.lang.String.class, "integer");
    }

    @org.junit.Test
    public void setCurrentNamespaceValueIsNull() {
        org.apache.ibatis.builder.MapperBuilderAssistant builder = new org.apache.ibatis.builder.MapperBuilderAssistant(new org.apache.ibatis.session.Configuration(), "resource");
        expectedException.expect(org.apache.ibatis.builder.BuilderException.class);
        expectedException.expectMessage(org.hamcrest.CoreMatchers.is("The mapper element requires a namespace attribute to be specified."));
        builder.setCurrentNamespace(null);
    }

    @org.junit.Test
    public void useCacheRefNamespaceIsNull() {
        org.apache.ibatis.builder.MapperBuilderAssistant builder = new org.apache.ibatis.builder.MapperBuilderAssistant(new org.apache.ibatis.session.Configuration(), "resource");
        expectedException.expect(org.apache.ibatis.builder.BuilderException.class);
        expectedException.expectMessage(org.hamcrest.CoreMatchers.is("cache-ref element requires a namespace attribute."));
        builder.useCacheRef(null);
    }

    @org.junit.Test
    public void useCacheRefNamespaceIsUndefined() {
        org.apache.ibatis.builder.MapperBuilderAssistant builder = new org.apache.ibatis.builder.MapperBuilderAssistant(new org.apache.ibatis.session.Configuration(), "resource");
        expectedException.expect(org.apache.ibatis.builder.IncompleteElementException.class);
        expectedException.expectMessage(org.hamcrest.CoreMatchers.is("No cache for namespace 'eee' could be found."));
        builder.useCacheRef("eee");
    }

    /* amplification of org.apache.ibatis.builder.XmlMapperBuilderTest#mappedStatementWithOptions */
    @org.junit.Test
    public void mappedStatementWithOptions_literalMutation5_failAssert0() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
            java.lang.String resource = "";
            java.io.InputStream inputStream = org.apache.ibatis.io.Resources.getResourceAsStream(resource);
            org.apache.ibatis.builder.xml.XMLMapperBuilder builder = new org.apache.ibatis.builder.xml.XMLMapperBuilder(inputStream, configuration, resource, configuration.getSqlFragments());
            builder.parse();
            org.apache.ibatis.mapping.MappedStatement mappedStatement = configuration.getMappedStatement("selectWithOptions");
            org.junit.Assert.assertThat(mappedStatement.getFetchSize(), org.hamcrest.CoreMatchers.is(200));
            org.junit.Assert.assertThat(mappedStatement.getTimeout(), org.hamcrest.CoreMatchers.is(10));
            org.junit.Assert.assertThat(mappedStatement.getStatementType(), org.hamcrest.CoreMatchers.is(org.apache.ibatis.mapping.StatementType.PREPARED));
            org.junit.Assert.assertThat(mappedStatement.getResultSetType(), org.hamcrest.CoreMatchers.is(org.apache.ibatis.mapping.ResultSetType.SCROLL_SENSITIVE));
            org.junit.Assert.assertThat(mappedStatement.isFlushCacheRequired(), org.hamcrest.CoreMatchers.is(false));
            org.junit.Assert.assertThat(mappedStatement.isUseCache(), org.hamcrest.CoreMatchers.is(false));
            org.junit.Assert.fail("mappedStatementWithOptions_literalMutation5 should have thrown BuilderException");
        } catch (org.apache.ibatis.builder.BuilderException eee) {
        }
    }

    /* amplification of org.apache.ibatis.builder.XmlMapperBuilderTest#mappedStatementWithOptions */
    @org.junit.Test
    public void mappedStatementWithOptions_literalMutation6_failAssert1() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
            java.lang.String resource = "org/apache/ibatis/bilder/AuthorMapper.xml";
            java.io.InputStream inputStream = org.apache.ibatis.io.Resources.getResourceAsStream(resource);
            org.apache.ibatis.builder.xml.XMLMapperBuilder builder = new org.apache.ibatis.builder.xml.XMLMapperBuilder(inputStream, configuration, resource, configuration.getSqlFragments());
            builder.parse();
            org.apache.ibatis.mapping.MappedStatement mappedStatement = configuration.getMappedStatement("selectWithOptions");
            org.junit.Assert.assertThat(mappedStatement.getFetchSize(), org.hamcrest.CoreMatchers.is(200));
            org.junit.Assert.assertThat(mappedStatement.getTimeout(), org.hamcrest.CoreMatchers.is(10));
            org.junit.Assert.assertThat(mappedStatement.getStatementType(), org.hamcrest.CoreMatchers.is(org.apache.ibatis.mapping.StatementType.PREPARED));
            org.junit.Assert.assertThat(mappedStatement.getResultSetType(), org.hamcrest.CoreMatchers.is(org.apache.ibatis.mapping.ResultSetType.SCROLL_SENSITIVE));
            org.junit.Assert.assertThat(mappedStatement.isFlushCacheRequired(), org.hamcrest.CoreMatchers.is(false));
            org.junit.Assert.assertThat(mappedStatement.isUseCache(), org.hamcrest.CoreMatchers.is(false));
            org.junit.Assert.fail("mappedStatementWithOptions_literalMutation6 should have thrown IOException");
        } catch (java.io.IOException eee) {
        }
    }

    /* amplification of org.apache.ibatis.builder.XmlMapperBuilderTest#mappedStatementWithOptions */
    @org.junit.Test(timeout = 1000)
    public void mappedStatementWithOptions_literalMutation5_failAssert0_add10() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
            java.lang.String resource = "";
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(resource, "");
            java.io.InputStream inputStream = org.apache.ibatis.io.Resources.getResourceAsStream(resource);
            org.apache.ibatis.builder.xml.XMLMapperBuilder builder = new org.apache.ibatis.builder.xml.XMLMapperBuilder(inputStream, configuration, resource, configuration.getSqlFragments());
            // MethodCallAdder
            builder.parse();
            builder.parse();
            org.apache.ibatis.mapping.MappedStatement mappedStatement = configuration.getMappedStatement("selectWithOptions");
            org.junit.Assert.assertThat(mappedStatement.getFetchSize(), org.hamcrest.CoreMatchers.is(200));
            org.junit.Assert.assertThat(mappedStatement.getTimeout(), org.hamcrest.CoreMatchers.is(10));
            org.junit.Assert.assertThat(mappedStatement.getStatementType(), org.hamcrest.CoreMatchers.is(org.apache.ibatis.mapping.StatementType.PREPARED));
            org.junit.Assert.assertThat(mappedStatement.getResultSetType(), org.hamcrest.CoreMatchers.is(org.apache.ibatis.mapping.ResultSetType.SCROLL_SENSITIVE));
            org.junit.Assert.assertThat(mappedStatement.isFlushCacheRequired(), org.hamcrest.CoreMatchers.is(false));
            org.junit.Assert.assertThat(mappedStatement.isUseCache(), org.hamcrest.CoreMatchers.is(false));
            org.junit.Assert.fail("mappedStatementWithOptions_literalMutation5 should have thrown BuilderException");
        } catch (org.apache.ibatis.builder.BuilderException eee) {
        }
    }

    /* amplification of org.apache.ibatis.builder.XmlMapperBuilderTest#mappedStatementWithOptions */
    @org.junit.Test
    public void mappedStatementWithOptions_literalMutation9_failAssert4_literalMutation34() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
            java.lang.String resource = "org}apache/ibatis/builder/AuthorMapper.xml";
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(resource, "org}apache/ibatis/builder/AuthorMapper.xml");
            java.io.InputStream inputStream = org.apache.ibatis.io.Resources.getResourceAsStream(resource);
            org.apache.ibatis.builder.xml.XMLMapperBuilder builder = new org.apache.ibatis.builder.xml.XMLMapperBuilder(inputStream, configuration, resource, configuration.getSqlFragments());
            builder.parse();
            org.apache.ibatis.mapping.MappedStatement mappedStatement = configuration.getMappedStatement("selectWithOptions");
            org.junit.Assert.assertThat(mappedStatement.getFetchSize(), org.hamcrest.CoreMatchers.is(200));
            org.junit.Assert.assertThat(mappedStatement.getTimeout(), org.hamcrest.CoreMatchers.is(10));
            org.junit.Assert.assertThat(mappedStatement.getStatementType(), org.hamcrest.CoreMatchers.is(org.apache.ibatis.mapping.StatementType.PREPARED));
            org.junit.Assert.assertThat(mappedStatement.getResultSetType(), org.hamcrest.CoreMatchers.is(org.apache.ibatis.mapping.ResultSetType.SCROLL_SENSITIVE));
            org.junit.Assert.assertThat(mappedStatement.isFlushCacheRequired(), org.hamcrest.CoreMatchers.is(false));
            org.junit.Assert.assertThat(mappedStatement.isUseCache(), org.hamcrest.CoreMatchers.is(false));
            org.junit.Assert.fail("mappedStatementWithOptions_literalMutation9 should have thrown IOException");
        } catch (java.io.IOException eee) {
        }
    }

    /* amplification of org.apache.ibatis.builder.XmlMapperBuilderTest#mappedStatementWithOptions */
    @org.junit.Test
    public void mappedStatementWithOptions_literalMutation9_failAssert4_literalMutation33_failAssert5() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
                java.lang.String resource = "";
                java.io.InputStream inputStream = org.apache.ibatis.io.Resources.getResourceAsStream(resource);
                org.apache.ibatis.builder.xml.XMLMapperBuilder builder = new org.apache.ibatis.builder.xml.XMLMapperBuilder(inputStream, configuration, resource, configuration.getSqlFragments());
                builder.parse();
                org.apache.ibatis.mapping.MappedStatement mappedStatement = configuration.getMappedStatement("selectWithOptions");
                org.junit.Assert.assertThat(mappedStatement.getFetchSize(), org.hamcrest.CoreMatchers.is(200));
                org.junit.Assert.assertThat(mappedStatement.getTimeout(), org.hamcrest.CoreMatchers.is(10));
                org.junit.Assert.assertThat(mappedStatement.getStatementType(), org.hamcrest.CoreMatchers.is(org.apache.ibatis.mapping.StatementType.PREPARED));
                org.junit.Assert.assertThat(mappedStatement.getResultSetType(), org.hamcrest.CoreMatchers.is(org.apache.ibatis.mapping.ResultSetType.SCROLL_SENSITIVE));
                org.junit.Assert.assertThat(mappedStatement.isFlushCacheRequired(), org.hamcrest.CoreMatchers.is(false));
                org.junit.Assert.assertThat(mappedStatement.isUseCache(), org.hamcrest.CoreMatchers.is(false));
                org.junit.Assert.fail("mappedStatementWithOptions_literalMutation9 should have thrown IOException");
            } catch (java.io.IOException eee) {
            }
            org.junit.Assert.fail("mappedStatementWithOptions_literalMutation9_failAssert4_literalMutation33 should have thrown BuilderException");
        } catch (org.apache.ibatis.builder.BuilderException eee) {
        }
    }

    /* amplification of org.apache.ibatis.builder.XmlMapperBuilderTest#mappedStatementWithOptions */
    @org.junit.Test(timeout = 1000)
    public void mappedStatementWithOptions_literalMutation5_failAssert0_add10_add37() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
            java.lang.String resource = "";
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(resource, "");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(resource, "");
            java.io.InputStream inputStream = org.apache.ibatis.io.Resources.getResourceAsStream(resource);
            org.apache.ibatis.builder.xml.XMLMapperBuilder builder = new org.apache.ibatis.builder.xml.XMLMapperBuilder(inputStream, configuration, resource, configuration.getSqlFragments());
            // MethodCallAdder
            builder.parse();
            // MethodCallAdder
            builder.parse();
            builder.parse();
            org.apache.ibatis.mapping.MappedStatement mappedStatement = configuration.getMappedStatement("selectWithOptions");
            org.junit.Assert.assertThat(mappedStatement.getFetchSize(), org.hamcrest.CoreMatchers.is(200));
            org.junit.Assert.assertThat(mappedStatement.getTimeout(), org.hamcrest.CoreMatchers.is(10));
            org.junit.Assert.assertThat(mappedStatement.getStatementType(), org.hamcrest.CoreMatchers.is(org.apache.ibatis.mapping.StatementType.PREPARED));
            org.junit.Assert.assertThat(mappedStatement.getResultSetType(), org.hamcrest.CoreMatchers.is(org.apache.ibatis.mapping.ResultSetType.SCROLL_SENSITIVE));
            org.junit.Assert.assertThat(mappedStatement.isFlushCacheRequired(), org.hamcrest.CoreMatchers.is(false));
            org.junit.Assert.assertThat(mappedStatement.isUseCache(), org.hamcrest.CoreMatchers.is(false));
            org.junit.Assert.fail("mappedStatementWithOptions_literalMutation5 should have thrown BuilderException");
        } catch (org.apache.ibatis.builder.BuilderException eee) {
        }
    }

    /* amplification of org.apache.ibatis.builder.XmlMapperBuilderTest#mappedStatementWithOptions */
    @org.junit.Test(timeout = 1000)
    public void mappedStatementWithOptions_literalMutation7_failAssert2_literalMutation22_failAssert3_add103() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
                java.lang.String resource = "";
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(resource, "");
                java.io.InputStream inputStream = org.apache.ibatis.io.Resources.getResourceAsStream(resource);
                org.apache.ibatis.builder.xml.XMLMapperBuilder builder = new org.apache.ibatis.builder.xml.XMLMapperBuilder(inputStream, configuration, resource, configuration.getSqlFragments());
                // MethodCallAdder
                builder.parse();
                builder.parse();
                org.apache.ibatis.mapping.MappedStatement mappedStatement = configuration.getMappedStatement("selectWithOptions");
                org.junit.Assert.assertThat(mappedStatement.getFetchSize(), org.hamcrest.CoreMatchers.is(200));
                org.junit.Assert.assertThat(mappedStatement.getTimeout(), org.hamcrest.CoreMatchers.is(10));
                org.junit.Assert.assertThat(mappedStatement.getStatementType(), org.hamcrest.CoreMatchers.is(org.apache.ibatis.mapping.StatementType.PREPARED));
                org.junit.Assert.assertThat(mappedStatement.getResultSetType(), org.hamcrest.CoreMatchers.is(org.apache.ibatis.mapping.ResultSetType.SCROLL_SENSITIVE));
                org.junit.Assert.assertThat(mappedStatement.isFlushCacheRequired(), org.hamcrest.CoreMatchers.is(false));
                org.junit.Assert.assertThat(mappedStatement.isUseCache(), org.hamcrest.CoreMatchers.is(false));
                org.junit.Assert.fail("mappedStatementWithOptions_literalMutation7 should have thrown IOException");
            } catch (java.io.IOException eee) {
            }
            org.junit.Assert.fail("mappedStatementWithOptions_literalMutation7_failAssert2_literalMutation22 should have thrown BuilderException");
        } catch (org.apache.ibatis.builder.BuilderException eee) {
        }
    }

    /* amplification of org.apache.ibatis.builder.XmlMapperBuilderTest#mappedStatementWithOptions */
    @org.junit.Test
    public void mappedStatementWithOptions_literalMutation5_failAssert0_literalMutation12_failAssert1_literalMutation45() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
                java.lang.String resource = "cc";
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(resource, "cc");
                java.io.InputStream inputStream = org.apache.ibatis.io.Resources.getResourceAsStream(resource);
                org.apache.ibatis.builder.xml.XMLMapperBuilder builder = new org.apache.ibatis.builder.xml.XMLMapperBuilder(inputStream, configuration, resource, configuration.getSqlFragments());
                builder.parse();
                org.apache.ibatis.mapping.MappedStatement mappedStatement = configuration.getMappedStatement("selectWithOptions");
                org.junit.Assert.assertThat(mappedStatement.getFetchSize(), org.hamcrest.CoreMatchers.is(200));
                org.junit.Assert.assertThat(mappedStatement.getTimeout(), org.hamcrest.CoreMatchers.is(10));
                org.junit.Assert.assertThat(mappedStatement.getStatementType(), org.hamcrest.CoreMatchers.is(org.apache.ibatis.mapping.StatementType.PREPARED));
                org.junit.Assert.assertThat(mappedStatement.getResultSetType(), org.hamcrest.CoreMatchers.is(org.apache.ibatis.mapping.ResultSetType.SCROLL_SENSITIVE));
                org.junit.Assert.assertThat(mappedStatement.isFlushCacheRequired(), org.hamcrest.CoreMatchers.is(false));
                org.junit.Assert.assertThat(mappedStatement.isUseCache(), org.hamcrest.CoreMatchers.is(false));
                org.junit.Assert.fail("mappedStatementWithOptions_literalMutation5 should have thrown BuilderException");
            } catch (org.apache.ibatis.builder.BuilderException eee) {
            }
            org.junit.Assert.fail("mappedStatementWithOptions_literalMutation5_failAssert0_literalMutation12 should have thrown IOException");
        } catch (java.io.IOException eee) {
        }
    }

    /* amplification of org.apache.ibatis.builder.XmlMapperBuilderTest#shouldSuccessfullyLoadXMLMapperFile */
    @org.junit.Test
    public void shouldSuccessfullyLoadXMLMapperFile_literalMutation384_failAssert0() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
            java.lang.String resource = "";
            java.io.InputStream inputStream = org.apache.ibatis.io.Resources.getResourceAsStream(resource);
            org.apache.ibatis.builder.xml.XMLMapperBuilder builder = new org.apache.ibatis.builder.xml.XMLMapperBuilder(inputStream, configuration, resource, configuration.getSqlFragments());
            builder.parse();
            org.junit.Assert.fail("shouldSuccessfullyLoadXMLMapperFile_literalMutation384 should have thrown BuilderException");
        } catch (org.apache.ibatis.builder.BuilderException eee) {
        }
    }

    /* amplification of org.apache.ibatis.builder.XmlMapperBuilderTest#shouldSuccessfullyLoadXMLMapperFile */
    @org.junit.Test
    public void shouldSuccessfullyLoadXMLMapperFile_literalMutation385_failAssert1() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
            java.lang.String resource = "org/apache/ibatis/builer/AuthorMapper.xml";
            java.io.InputStream inputStream = org.apache.ibatis.io.Resources.getResourceAsStream(resource);
            org.apache.ibatis.builder.xml.XMLMapperBuilder builder = new org.apache.ibatis.builder.xml.XMLMapperBuilder(inputStream, configuration, resource, configuration.getSqlFragments());
            builder.parse();
            org.junit.Assert.fail("shouldSuccessfullyLoadXMLMapperFile_literalMutation385 should have thrown IOException");
        } catch (java.io.IOException eee) {
        }
    }

    /* amplification of org.apache.ibatis.builder.XmlMapperBuilderTest#shouldSuccessfullyLoadXMLMapperFile */
    @org.junit.Test
    public void shouldSuccessfullyLoadXMLMapperFile_literalMutation384_failAssert0_literalMutation390_failAssert1() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
                java.lang.String resource = "L";
                java.io.InputStream inputStream = org.apache.ibatis.io.Resources.getResourceAsStream(resource);
                org.apache.ibatis.builder.xml.XMLMapperBuilder builder = new org.apache.ibatis.builder.xml.XMLMapperBuilder(inputStream, configuration, resource, configuration.getSqlFragments());
                builder.parse();
                org.junit.Assert.fail("shouldSuccessfullyLoadXMLMapperFile_literalMutation384 should have thrown BuilderException");
            } catch (org.apache.ibatis.builder.BuilderException eee) {
            }
            org.junit.Assert.fail("shouldSuccessfullyLoadXMLMapperFile_literalMutation384_failAssert0_literalMutation390 should have thrown IOException");
        } catch (java.io.IOException eee) {
        }
    }

    /* amplification of org.apache.ibatis.builder.XmlMapperBuilderTest#shouldSuccessfullyLoadXMLMapperFile */
    @org.junit.Test
    public void shouldSuccessfullyLoadXMLMapperFile_literalMutation385_failAssert1_literalMutation392_failAssert2() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
                java.lang.String resource = "";
                java.io.InputStream inputStream = org.apache.ibatis.io.Resources.getResourceAsStream(resource);
                org.apache.ibatis.builder.xml.XMLMapperBuilder builder = new org.apache.ibatis.builder.xml.XMLMapperBuilder(inputStream, configuration, resource, configuration.getSqlFragments());
                builder.parse();
                org.junit.Assert.fail("shouldSuccessfullyLoadXMLMapperFile_literalMutation385 should have thrown IOException");
            } catch (java.io.IOException eee) {
            }
            org.junit.Assert.fail("shouldSuccessfullyLoadXMLMapperFile_literalMutation385_failAssert1_literalMutation392 should have thrown BuilderException");
        } catch (org.apache.ibatis.builder.BuilderException eee) {
        }
    }

    /* amplification of org.apache.ibatis.builder.XmlMapperBuilderTest#shouldSuccessfullyLoadXMLMapperFile */
    @org.junit.Test(timeout = 1000)
    public void shouldSuccessfullyLoadXMLMapperFile_literalMutation384_failAssert0_add388() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
            java.lang.String resource = "";
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(resource, "");
            java.io.InputStream inputStream = org.apache.ibatis.io.Resources.getResourceAsStream(resource);
            org.apache.ibatis.builder.xml.XMLMapperBuilder builder = new org.apache.ibatis.builder.xml.XMLMapperBuilder(inputStream, configuration, resource, configuration.getSqlFragments());
            // MethodCallAdder
            builder.parse();
            builder.parse();
            org.junit.Assert.fail("shouldSuccessfullyLoadXMLMapperFile_literalMutation384 should have thrown BuilderException");
        } catch (org.apache.ibatis.builder.BuilderException eee) {
        }
    }

    /* amplification of org.apache.ibatis.builder.XmlMapperBuilderTest#shouldSuccessfullyLoadXMLMapperFile */
    @org.junit.Test
    public void shouldSuccessfullyLoadXMLMapperFile_literalMutation384_failAssert0_literalMutation390_failAssert1_literalMutation421() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
                java.lang.String resource = "j";
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(resource, "j");
                java.io.InputStream inputStream = org.apache.ibatis.io.Resources.getResourceAsStream(resource);
                org.apache.ibatis.builder.xml.XMLMapperBuilder builder = new org.apache.ibatis.builder.xml.XMLMapperBuilder(inputStream, configuration, resource, configuration.getSqlFragments());
                builder.parse();
                org.junit.Assert.fail("shouldSuccessfullyLoadXMLMapperFile_literalMutation384 should have thrown BuilderException");
            } catch (org.apache.ibatis.builder.BuilderException eee) {
            }
            org.junit.Assert.fail("shouldSuccessfullyLoadXMLMapperFile_literalMutation384_failAssert0_literalMutation390 should have thrown IOException");
        } catch (java.io.IOException eee) {
        }
    }

    /* amplification of org.apache.ibatis.builder.XmlMapperBuilderTest#shouldSuccessfullyLoadXMLMapperFile */
    @org.junit.Test(timeout = 1000)
    public void shouldSuccessfullyLoadXMLMapperFile_literalMutation384_failAssert0_add388_add409() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
            java.lang.String resource = "";
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(resource, "");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(resource, "");
            java.io.InputStream inputStream = org.apache.ibatis.io.Resources.getResourceAsStream(resource);
            org.apache.ibatis.builder.xml.XMLMapperBuilder builder = new org.apache.ibatis.builder.xml.XMLMapperBuilder(inputStream, configuration, resource, configuration.getSqlFragments());
            // MethodCallAdder
            builder.parse();
            // MethodCallAdder
            builder.parse();
            builder.parse();
            org.junit.Assert.fail("shouldSuccessfullyLoadXMLMapperFile_literalMutation384 should have thrown BuilderException");
        } catch (org.apache.ibatis.builder.BuilderException eee) {
        }
    }

    /* amplification of org.apache.ibatis.builder.XmlMapperBuilderTest#shouldSuccessfullyLoadXMLMapperFile */
    @org.junit.Test(timeout = 1000)
    public void shouldSuccessfullyLoadXMLMapperFile_literalMutation386_failAssert2_literalMutation399_failAssert3_add456() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
                java.lang.String resource = "";
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(resource, "");
                java.io.InputStream inputStream = org.apache.ibatis.io.Resources.getResourceAsStream(resource);
                org.apache.ibatis.builder.xml.XMLMapperBuilder builder = new org.apache.ibatis.builder.xml.XMLMapperBuilder(inputStream, configuration, resource, configuration.getSqlFragments());
                // MethodCallAdder
                builder.parse();
                builder.parse();
                org.junit.Assert.fail("shouldSuccessfullyLoadXMLMapperFile_literalMutation386 should have thrown IOException");
            } catch (java.io.IOException eee) {
            }
            org.junit.Assert.fail("shouldSuccessfullyLoadXMLMapperFile_literalMutation386_failAssert2_literalMutation399 should have thrown BuilderException");
        } catch (org.apache.ibatis.builder.BuilderException eee) {
        }
    }
}

