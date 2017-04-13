

package org.apache.ibatis.builder;


public class AmplXmlConfigBuilderTest {
    @org.junit.Rule
    public org.junit.rules.ExpectedException expectedException = org.junit.rules.ExpectedException.none();

    @org.junit.Test
    public void shouldSuccessfullyLoadMinimalXMLConfigFile() throws java.lang.Exception {
        java.lang.String resource = "org/apache/ibatis/builder/MinimalMapperConfig.xml";
        java.io.InputStream inputStream = org.apache.ibatis.io.Resources.getResourceAsStream(resource);
        org.apache.ibatis.builder.xml.XMLConfigBuilder builder = new org.apache.ibatis.builder.xml.XMLConfigBuilder(inputStream);
        org.apache.ibatis.session.Configuration config = builder.parse();
        org.junit.Assert.assertNotNull(config);
        org.junit.Assert.assertThat(config.getAutoMappingBehavior(), org.hamcrest.core.Is.is(org.apache.ibatis.session.AutoMappingBehavior.PARTIAL));
        org.junit.Assert.assertThat(config.getAutoMappingUnknownColumnBehavior(), org.hamcrest.core.Is.is(org.apache.ibatis.session.AutoMappingUnknownColumnBehavior.NONE));
        org.junit.Assert.assertThat(config.isCacheEnabled(), org.hamcrest.core.Is.is(true));
        org.junit.Assert.assertThat(config.getProxyFactory(), org.hamcrest.core.Is.is(org.hamcrest.core.IsInstanceOf.instanceOf(org.apache.ibatis.executor.loader.javassist.JavassistProxyFactory.class)));
        org.junit.Assert.assertThat(config.isLazyLoadingEnabled(), org.hamcrest.core.Is.is(false));
        org.junit.Assert.assertThat(config.isAggressiveLazyLoading(), org.hamcrest.core.Is.is(false));
        org.junit.Assert.assertThat(config.isMultipleResultSetsEnabled(), org.hamcrest.core.Is.is(true));
        org.junit.Assert.assertThat(config.isUseColumnLabel(), org.hamcrest.core.Is.is(true));
        org.junit.Assert.assertThat(config.isUseGeneratedKeys(), org.hamcrest.core.Is.is(false));
        org.junit.Assert.assertThat(config.getDefaultExecutorType(), org.hamcrest.core.Is.is(org.apache.ibatis.session.ExecutorType.SIMPLE));
        org.junit.Assert.assertNull(config.getDefaultStatementTimeout());
        org.junit.Assert.assertNull(config.getDefaultFetchSize());
        org.junit.Assert.assertThat(config.isMapUnderscoreToCamelCase(), org.hamcrest.core.Is.is(false));
        org.junit.Assert.assertThat(config.isSafeRowBoundsEnabled(), org.hamcrest.core.Is.is(false));
        org.junit.Assert.assertThat(config.getLocalCacheScope(), org.hamcrest.core.Is.is(org.apache.ibatis.session.LocalCacheScope.SESSION));
        org.junit.Assert.assertThat(config.getJdbcTypeForNull(), org.hamcrest.core.Is.is(org.apache.ibatis.type.JdbcType));
        org.junit.Assert.assertThat(config.getLazyLoadTriggerMethods(), org.hamcrest.core.Is.is(((java.util.Set<java.lang.String>) (new java.util.HashSet<java.lang.String>(java.util.Arrays.asList("equals", "clone", "hashCode", "toString"))))));
        org.junit.Assert.assertThat(config.isSafeResultHandlerEnabled(), org.hamcrest.core.Is.is(true));
        org.junit.Assert.assertThat(config.getDefaultScriptingLanguageInstance(), org.hamcrest.core.Is.is(org.hamcrest.core.IsInstanceOf.instanceOf(org.apache.ibatis.scripting.xmltags.XMLLanguageDriver.class)));
        org.junit.Assert.assertThat(config.isCallSettersOnNulls(), org.hamcrest.core.Is.is(false));
        org.junit.Assert.assertNull(config.getLogPrefix());
        org.junit.Assert.assertNull(config.getLogImpl());
        org.junit.Assert.assertNull(config.getConfigurationFactory());
    }

    enum MyEnum {
ONE, TWO;    }

    public static class EnumOrderTypeHandler<E extends java.lang.Enum<E>> extends org.apache.ibatis.type.BaseTypeHandler<E> {
        private E[] constants;

        public EnumOrderTypeHandler(java.lang.Class<E> javaType) {
            constants = javaType.getEnumConstants();
        }

        @java.lang.Override
        public void setNonNullParameter(java.sql.PreparedStatement ps, int i, E parameter, org.apache.ibatis.type.JdbcType jdbcType) throws java.sql.SQLException {
            ps.setInt(i, ((parameter.ordinal()) + 1));
        }

        @java.lang.Override
        public E getNullableResult(java.sql.ResultSet rs, java.lang.String columnName) throws java.sql.SQLException {
            int index = (rs.getInt(columnName)) - 1;
            return index < 0 ? null : constants[index];
        }

        @java.lang.Override
        public E getNullableResult(java.sql.ResultSet rs, int columnIndex) throws java.sql.SQLException {
            int index = (rs.getInt(rs.getInt(columnIndex))) - 1;
            return index < 0 ? null : constants[index];
        }

        @java.lang.Override
        public E getNullableResult(java.sql.CallableStatement cs, int columnIndex) throws java.sql.SQLException {
            int index = (cs.getInt(columnIndex)) - 1;
            return index < 0 ? null : constants[index];
        }
    }

    @org.junit.Test
    public void registerJavaTypeInitializingTypeHandler() {
        final java.lang.String MAPPER_CONFIG = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" + (((((("<!DOCTYPE configuration PUBLIC \"-//mybatis.org//DTD Config 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-config.dtd\">\n" + "<configuration>\n") + "  <typeHandlers>\n") + "    <typeHandler javaType=\"org.apache.ibatis.builder.XmlConfigBuilderTest$MyEnum\"\n") + "      handler=\"org.apache.ibatis.builder.XmlConfigBuilderTest$EnumOrderTypeHandler\"/>\n") + "  </typeHandlers>\n") + "</configuration>\n");
        org.apache.ibatis.builder.xml.XMLConfigBuilder builder = new org.apache.ibatis.builder.xml.XMLConfigBuilder(new java.io.StringReader(MAPPER_CONFIG));
        builder.parse();
        org.apache.ibatis.type.TypeHandlerRegistry typeHandlerRegistry = builder.getConfiguration().getTypeHandlerRegistry();
        org.apache.ibatis.type.TypeHandler<org.apache.ibatis.builder.AmplXmlConfigBuilderTest.MyEnum> typeHandler = typeHandlerRegistry.getTypeHandler(org.apache.ibatis.builder.AmplXmlConfigBuilderTest.MyEnum.class);
        org.junit.Assert.assertTrue((typeHandler instanceof org.apache.ibatis.builder.AmplXmlConfigBuilderTest.EnumOrderTypeHandler));
        org.junit.Assert.assertArrayEquals(org.apache.ibatis.builder.AmplXmlConfigBuilderTest.MyEnum.values(), ((org.apache.ibatis.builder.AmplXmlConfigBuilderTest.EnumOrderTypeHandler) (typeHandler)).constants);
    }

    @org.junit.Test
    public void shouldSuccessfullyLoadXMLConfigFile() throws java.lang.Exception {
        java.lang.String resource = "org/apache/ibatis/builder/CustomizedSettingsMapperConfig.xml";
        java.io.InputStream inputStream = org.apache.ibatis.io.Resources.getResourceAsStream(resource);
        java.util.Properties props = new java.util.Properties();
        props.put("prop2", "cccc");
        org.apache.ibatis.builder.xml.XMLConfigBuilder builder = new org.apache.ibatis.builder.xml.XMLConfigBuilder(inputStream, null, props);
        org.apache.ibatis.session.Configuration config = builder.parse();
        org.junit.Assert.assertThat(config.getAutoMappingBehavior(), org.hamcrest.core.Is.is(org.apache.ibatis.session.AutoMappingBehavior.NONE));
        org.junit.Assert.assertThat(config.getAutoMappingUnknownColumnBehavior(), org.hamcrest.core.Is.is(org.apache.ibatis.session.AutoMappingUnknownColumnBehavior.WARNING));
        org.junit.Assert.assertThat(config.isCacheEnabled(), org.hamcrest.core.Is.is(false));
        org.junit.Assert.assertThat(config.getProxyFactory(), org.hamcrest.core.Is.is(org.hamcrest.core.IsInstanceOf.instanceOf(org.apache.ibatis.executor.loader.cglib.CglibProxyFactory.class)));
        org.junit.Assert.assertThat(config.isLazyLoadingEnabled(), org.hamcrest.core.Is.is(true));
        org.junit.Assert.assertThat(config.isAggressiveLazyLoading(), org.hamcrest.core.Is.is(true));
        org.junit.Assert.assertThat(config.isMultipleResultSetsEnabled(), org.hamcrest.core.Is.is(false));
        org.junit.Assert.assertThat(config.isUseColumnLabel(), org.hamcrest.core.Is.is(false));
        org.junit.Assert.assertThat(config.isUseGeneratedKeys(), org.hamcrest.core.Is.is(true));
        org.junit.Assert.assertThat(config.getDefaultExecutorType(), org.hamcrest.core.Is.is(org.apache.ibatis.session.ExecutorType.BATCH));
        org.junit.Assert.assertThat(config.getDefaultStatementTimeout(), org.hamcrest.core.Is.is(10));
        org.junit.Assert.assertThat(config.getDefaultFetchSize(), org.hamcrest.core.Is.is(100));
        org.junit.Assert.assertThat(config.isMapUnderscoreToCamelCase(), org.hamcrest.core.Is.is(true));
        org.junit.Assert.assertThat(config.isSafeRowBoundsEnabled(), org.hamcrest.core.Is.is(true));
        org.junit.Assert.assertThat(config.getLocalCacheScope(), org.hamcrest.core.Is.is(org.apache.ibatis.session.LocalCacheScope.STATEMENT));
        org.junit.Assert.assertThat(config.getJdbcTypeForNull(), org.hamcrest.core.Is.is(org.apache.ibatis.type.JdbcType));
        org.junit.Assert.assertThat(config.getLazyLoadTriggerMethods(), org.hamcrest.core.Is.is(((java.util.Set<java.lang.String>) (new java.util.HashSet<java.lang.String>(java.util.Arrays.asList("equals", "clone", "hashCode", "toString", "xxx"))))));
        org.junit.Assert.assertThat(config.isSafeResultHandlerEnabled(), org.hamcrest.core.Is.is(false));
        org.junit.Assert.assertThat(config.getDefaultScriptingLanguageInstance(), org.hamcrest.core.Is.is(org.hamcrest.core.IsInstanceOf.instanceOf(org.apache.ibatis.scripting.defaults.RawLanguageDriver.class)));
        org.junit.Assert.assertThat(config.isCallSettersOnNulls(), org.hamcrest.core.Is.is(true));
        org.junit.Assert.assertThat(config.getLogPrefix(), org.hamcrest.core.Is.is("mybatis_"));
        org.junit.Assert.assertThat(config.getLogImpl().getName(), org.hamcrest.core.Is.is(org.apache.ibatis.logging.slf4j.Slf4jImpl.class.getName()));
        org.junit.Assert.assertThat(config.getVfsImpl().getName(), org.hamcrest.core.Is.is(org.apache.ibatis.io.JBoss6VFS.class.getName()));
        org.junit.Assert.assertThat(config.getConfigurationFactory().getName(), org.hamcrest.core.Is.is(java.lang.String.class.getName()));
        org.junit.Assert.assertTrue(config.getTypeAliasRegistry().getTypeAliases().get("blogauthor").equals(org.apache.ibatis.domain.blog.Author.class));
        org.junit.Assert.assertTrue(config.getTypeAliasRegistry().getTypeAliases().get("blog").equals(org.apache.ibatis.domain.blog.Blog.class));
        org.junit.Assert.assertTrue(config.getTypeAliasRegistry().getTypeAliases().get("cart").equals(org.apache.ibatis.domain.jpetstore.Cart.class));
        org.junit.Assert.assertThat(config.getTypeHandlerRegistry().getTypeHandler(java.lang.Integer.class), org.hamcrest.core.Is.is(org.hamcrest.core.IsInstanceOf.instanceOf(org.apache.ibatis.builder.typehandler.CustomIntegerTypeHandler.class)));
        org.junit.Assert.assertThat(config.getTypeHandlerRegistry().getTypeHandler(java.lang.Long.class), org.hamcrest.core.Is.is(org.hamcrest.core.IsInstanceOf.instanceOf(org.apache.ibatis.builder.CustomLongTypeHandler.class)));
        org.junit.Assert.assertThat(config.getTypeHandlerRegistry().getTypeHandler(java.lang.String.class), org.hamcrest.core.Is.is(org.hamcrest.core.IsInstanceOf.instanceOf(org.apache.ibatis.builder.CustomStringTypeHandler.class)));
        org.junit.Assert.assertThat(config.getTypeHandlerRegistry().getTypeHandler(java.lang.String.class, org.apache.ibatis.type.JdbcType), org.hamcrest.core.Is.is(org.hamcrest.core.IsInstanceOf.instanceOf(org.apache.ibatis.builder.CustomStringTypeHandler.class)));
        org.apache.ibatis.builder.ExampleObjectFactory objectFactory = ((org.apache.ibatis.builder.ExampleObjectFactory) (config.getObjectFactory()));
        org.junit.Assert.assertThat(objectFactory.getProperties().size(), org.hamcrest.core.Is.is(1));
        org.junit.Assert.assertThat(objectFactory.getProperties().getProperty("objectFactoryProperty"), org.hamcrest.core.Is.is("100"));
        org.junit.Assert.assertThat(config.getObjectWrapperFactory(), org.hamcrest.core.Is.is(org.hamcrest.core.IsInstanceOf.instanceOf(org.apache.ibatis.builder.CustomObjectWrapperFactory.class)));
        org.junit.Assert.assertThat(config.getReflectorFactory(), org.hamcrest.core.Is.is(org.hamcrest.core.IsInstanceOf.instanceOf(org.apache.ibatis.builder.CustomReflectorFactory.class)));
        org.apache.ibatis.builder.ExamplePlugin plugin = ((org.apache.ibatis.builder.ExamplePlugin) (config.getInterceptors().get(0)));
        org.junit.Assert.assertThat(plugin.getProperties().size(), org.hamcrest.core.Is.is(1));
        org.junit.Assert.assertThat(plugin.getProperties().getProperty("pluginProperty"), org.hamcrest.core.Is.is("100"));
        org.apache.ibatis.mapping.Environment environment = config.getEnvironment();
        org.junit.Assert.assertThat(environment.getId(), org.hamcrest.core.Is.is("development"));
        org.junit.Assert.assertThat(environment.getDataSource(), org.hamcrest.core.Is.is(org.hamcrest.core.IsInstanceOf.instanceOf(org.apache.ibatis.datasource.unpooled.UnpooledDataSource.class)));
        org.junit.Assert.assertThat(environment.getTransactionFactory(), org.hamcrest.core.Is.is(org.hamcrest.core.IsInstanceOf.instanceOf(org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory.class)));
        org.junit.Assert.assertThat(config.getDatabaseId(), org.hamcrest.core.Is.is("derby"));
        org.junit.Assert.assertThat(config.getMapperRegistry().getMappers().size(), org.hamcrest.core.Is.is(4));
        org.junit.Assert.assertThat(config.getMapperRegistry().hasMapper(org.apache.ibatis.builder.CachedAuthorMapper.class), org.hamcrest.core.Is.is(true));
        org.junit.Assert.assertThat(config.getMapperRegistry().hasMapper(org.apache.ibatis.builder.mapper.CustomMapper.class), org.hamcrest.core.Is.is(true));
        org.junit.Assert.assertThat(config.getMapperRegistry().hasMapper(org.apache.ibatis.domain.blog.mappers.BlogMapper.class), org.hamcrest.core.Is.is(true));
        org.junit.Assert.assertThat(config.getMapperRegistry().hasMapper(org.apache.ibatis.domain.blog.mappers.NestedBlogMapper.class), org.hamcrest.core.Is.is(true));
    }

    @org.junit.Test
    public void shouldSuccessfullyLoadXMLConfigFileWithPropertiesUrl() throws java.lang.Exception {
        java.lang.String resource = "org/apache/ibatis/builder/PropertiesUrlMapperConfig.xml";
        java.io.InputStream inputStream = org.apache.ibatis.io.Resources.getResourceAsStream(resource);
        org.apache.ibatis.builder.xml.XMLConfigBuilder builder = new org.apache.ibatis.builder.xml.XMLConfigBuilder(inputStream);
        org.apache.ibatis.session.Configuration config = builder.parse();
        org.junit.Assert.assertThat(config.getVariables().get("driver").toString(), org.hamcrest.core.Is.is("org.apache.derby.jdbc.EmbeddedDriver"));
        org.junit.Assert.assertThat(config.getVariables().get("prop1").toString(), org.hamcrest.core.Is.is("bbbb"));
    }

    @org.junit.Test
    public void parseIsTwice() throws java.lang.Exception {
        java.lang.String resource = "org/apache/ibatis/builder/MinimalMapperConfig.xml";
        java.io.InputStream inputStream = org.apache.ibatis.io.Resources.getResourceAsStream(resource);
        org.apache.ibatis.builder.xml.XMLConfigBuilder builder = new org.apache.ibatis.builder.xml.XMLConfigBuilder(inputStream);
        builder.parse();
        expectedException.expect(org.apache.ibatis.builder.BuilderException.class);
        expectedException.expectMessage("Each XMLConfigBuilder can only be used once.");
        builder.parse();
    }

    @org.junit.Test
    public void unknownSettings() {
        final java.lang.String MAPPER_CONFIG = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" + ((((("<!DOCTYPE configuration PUBLIC \"-//mybatis.org//DTD Config 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-config.dtd\">\n" + "<configuration>\n") + "  <settings>\n") + "    <setting name=\"foo\" value=\"bar\"/>\n") + "  </settings>\n") + "</configuration>\n");
        expectedException.expect(org.apache.ibatis.builder.BuilderException.class);
        expectedException.expectMessage("The setting foo is not known.  Make sure you spelled it correctly (case sensitive).");
        org.apache.ibatis.builder.xml.XMLConfigBuilder builder = new org.apache.ibatis.builder.xml.XMLConfigBuilder(new java.io.StringReader(MAPPER_CONFIG));
        builder.parse();
    }

    @org.junit.Test
    public void unknownJavaTypeOnTypeHandler() {
        final java.lang.String MAPPER_CONFIG = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" + ((((("<!DOCTYPE configuration PUBLIC \"-//mybatis.org//DTD Config 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-config.dtd\">\n" + "<configuration>\n") + "  <typeAliases>\n") + "    <typeAlias type=\"a.b.c.Foo\"/>\n") + "  </typeAliases>\n") + "</configuration>\n");
        expectedException.expect(org.apache.ibatis.builder.BuilderException.class);
        expectedException.expectMessage("Error registering typeAlias for 'null'. Cause: ");
        org.apache.ibatis.builder.xml.XMLConfigBuilder builder = new org.apache.ibatis.builder.xml.XMLConfigBuilder(new java.io.StringReader(MAPPER_CONFIG));
        builder.parse();
    }

    @org.junit.Test
    public void propertiesSpecifyResourceAndUrlAtSameTime() {
        final java.lang.String MAPPER_CONFIG = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" + ((("<!DOCTYPE configuration PUBLIC \"-//mybatis.org//DTD Config 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-config.dtd\">\n" + "<configuration>\n") + "  <properties resource=\"a/b/c/foo.properties\" url=\"file:./a/b/c/jdbc.properties\"/>\n") + "</configuration>\n");
        expectedException.expect(org.apache.ibatis.builder.BuilderException.class);
        expectedException.expectMessage("The properties element cannot specify both a URL and a resource based property file reference.  Please specify one or the other.");
        org.apache.ibatis.builder.xml.XMLConfigBuilder builder = new org.apache.ibatis.builder.xml.XMLConfigBuilder(new java.io.StringReader(MAPPER_CONFIG));
        builder.parse();
    }

    @org.junit.Test
    public void parseIsTwice_literalMutation9_literalMutation88_literalMutation839_failAssert156() throws java.lang.Exception {
        try {
            java.lang.String resource = "";
            java.io.InputStream inputStream = org.apache.ibatis.io.Resources.getResourceAsStream(resource);
            org.apache.ibatis.builder.xml.XMLConfigBuilder builder = new org.apache.ibatis.builder.xml.XMLConfigBuilder(inputStream);
            builder.parse();
            expectedException.expect(org.apache.ibatis.builder.BuilderException.class);
            expectedException.expectMessage("Each XMLConfigBuilder can only be used once.");
            builder.parse();
            org.junit.Assert.fail("parseIsTwice_literalMutation9_literalMutation88_literalMutation839 should have thrown BuilderException");
        } catch (org.apache.ibatis.builder.BuilderException eee) {
        }
    }

    @org.junit.Test
    public void registerJavaTypeInitializingTypeHandler_literalMutation1903_literalMutation2265_failAssert114() {
        try {
            final java.lang.String MAPPER_CONFIG = "(Qz(!&?km!082eSn^!A3tZE(WS@f95|gIcTt!Cw#" + (((((("" + "<configuration>\n") + "  <typeHandlers>\n") + "    <typeHandler javaType=\"org.apache.ibatis.builder.XmlConfigBuilderTest$MyEnum\"\n") + "      handler=\"org.apache.ibatis.builder.XmlConfigBuilderTest$EnumOrderTypeHandler\"/>\n") + "  </typeHandlers>\n") + "</configuration>\n");
            org.apache.ibatis.builder.xml.XMLConfigBuilder builder = new org.apache.ibatis.builder.xml.XMLConfigBuilder(new java.io.StringReader(MAPPER_CONFIG));
            builder.parse();
            org.apache.ibatis.type.TypeHandlerRegistry typeHandlerRegistry = builder.getConfiguration().getTypeHandlerRegistry();
            org.apache.ibatis.type.TypeHandler<org.apache.ibatis.builder.AmplXmlConfigBuilderTest.MyEnum> typeHandler = typeHandlerRegistry.getTypeHandler(org.apache.ibatis.builder.AmplXmlConfigBuilderTest.MyEnum.class);
            java.lang.Object o_11_0 = typeHandler instanceof org.apache.ibatis.builder.AmplXmlConfigBuilderTest.EnumOrderTypeHandler;
            java.lang.Object o_12_0 = org.apache.ibatis.builder.AmplXmlConfigBuilderTest.MyEnum.values();
            org.junit.Assert.fail("registerJavaTypeInitializingTypeHandler_literalMutation1903_literalMutation2265 should have thrown BuilderException");
        } catch (org.apache.ibatis.builder.BuilderException eee) {
        }
    }

    @org.junit.Test
    public void registerJavaTypeInitializingTypeHandler_literalMutation1914_literalMutation2735_failAssert54_literalMutation5821() {
        try {
            final java.lang.String MAPPER_CONFIG = "VOA>`kM;fCG*<GJ![ySpS4ms3&2B._BcddZjM^$[" + (((((("<!DOCTYPE configuration PUBLIC \"-//mybatis.org//DTD Config 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-config.dtd\">\n" + "<configuration>\n") + "  <typeHanders>\n") + "    <typeHandler javaType=\"org.apache.ibatis.builder.XmlConfigBuilderTest$MyEnum\"\n") + "      handler=\"org.apache.ibatis.builder.XmlConfigBuilderTest$EnumOrderTypeHandler\"/>\n") + "") + "</configuration>\n");
            org.junit.Assert.assertEquals(MAPPER_CONFIG, "VOA>`kM;fCG*<GJ![ySpS4ms3&2B._BcddZjM^$[<!DOCTYPE configuration PUBLIC \"-//mybatis.org//DTD Config 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-config.dtd\">\n<configuration>\n  <typeHanders>\n    <typeHandler javaType=\"org.apache.ibatis.builder.XmlConfigBuilderTest$MyEnum\"\n      handler=\"org.apache.ibatis.builder.XmlConfigBuilderTest$EnumOrderTypeHandler\"/>\n</configuration>\n");
            org.apache.ibatis.builder.xml.XMLConfigBuilder builder = new org.apache.ibatis.builder.xml.XMLConfigBuilder(new java.io.StringReader(MAPPER_CONFIG));
            builder.parse();
            org.apache.ibatis.type.TypeHandlerRegistry typeHandlerRegistry = builder.getConfiguration().getTypeHandlerRegistry();
            org.apache.ibatis.type.TypeHandler<org.apache.ibatis.builder.AmplXmlConfigBuilderTest.MyEnum> typeHandler = typeHandlerRegistry.getTypeHandler(org.apache.ibatis.builder.AmplXmlConfigBuilderTest.MyEnum.class);
            java.lang.Object o_11_0 = typeHandler instanceof org.apache.ibatis.builder.AmplXmlConfigBuilderTest.EnumOrderTypeHandler;
            java.lang.Object o_12_0 = org.apache.ibatis.builder.AmplXmlConfigBuilderTest.MyEnum.values();
            org.junit.Assert.fail("registerJavaTypeInitializingTypeHandler_literalMutation1914_literalMutation2735 should have thrown BuilderException");
        } catch (org.apache.ibatis.builder.BuilderException eee) {
        }
    }

    @org.junit.Test
    public void shouldSuccessfullyLoadMinimalXMLConfigFile_literalMutation11585_failAssert0() throws java.lang.Exception {
        try {
            java.lang.Object o_68_1 = org.hamcrest.core.Is.is(false);
            java.lang.Object o_64_1 = org.hamcrest.core.Is.is(org.hamcrest.core.IsInstanceOf.instanceOf(org.apache.ibatis.scripting.xmltags.XMLLanguageDriver.class));
            java.lang.Object o_61_1 = org.hamcrest.core.Is.is(true);
            java.lang.Object o_56_1 = org.hamcrest.core.Is.is(((java.util.Set<java.lang.String>) (new java.util.HashSet<java.lang.String>(java.util.Arrays.asList("equals", "clone", "hashCode", "toString")))));
            java.lang.Object o_53_1 = org.hamcrest.core.Is.is(org.apache.ibatis.type.JdbcType);
            java.lang.Object o_50_1 = org.hamcrest.core.Is.is(org.apache.ibatis.session.LocalCacheScope.SESSION);
            java.lang.Object o_47_1 = org.hamcrest.core.Is.is(false);
            java.lang.Object o_44_1 = org.hamcrest.core.Is.is(false);
            java.lang.Object o_37_1 = org.hamcrest.core.Is.is(org.apache.ibatis.session.ExecutorType.SIMPLE);
            java.lang.Object o_34_1 = org.hamcrest.core.Is.is(false);
            java.lang.Object o_31_1 = org.hamcrest.core.Is.is(true);
            java.lang.Object o_28_1 = org.hamcrest.core.Is.is(true);
            java.lang.Object o_25_1 = org.hamcrest.core.Is.is(false);
            java.lang.Object o_22_1 = org.hamcrest.core.Is.is(false);
            java.lang.Object o_18_1 = org.hamcrest.core.Is.is(org.hamcrest.core.IsInstanceOf.instanceOf(org.apache.ibatis.executor.loader.javassist.JavassistProxyFactory.class));
            java.lang.Object o_15_1 = org.hamcrest.core.Is.is(true);
            java.lang.Object o_12_1 = org.hamcrest.core.Is.is(org.apache.ibatis.session.AutoMappingUnknownColumnBehavior.NONE);
            java.lang.Object o_9_1 = org.hamcrest.core.Is.is(org.apache.ibatis.session.AutoMappingBehavior.PARTIAL);
            java.lang.String resource = "";
            java.io.InputStream inputStream = org.apache.ibatis.io.Resources.getResourceAsStream(resource);
            org.apache.ibatis.builder.xml.XMLConfigBuilder builder = new org.apache.ibatis.builder.xml.XMLConfigBuilder(inputStream);
            org.apache.ibatis.session.Configuration config = builder.parse();
            java.lang.Object o_9_0 = config.getAutoMappingBehavior();
            java.lang.Object o_12_0 = config.getAutoMappingUnknownColumnBehavior();
            java.lang.Object o_15_0 = config.isCacheEnabled();
            java.lang.Object o_18_0 = config.getProxyFactory();
            java.lang.Object o_22_0 = config.isLazyLoadingEnabled();
            java.lang.Object o_25_0 = config.isAggressiveLazyLoading();
            java.lang.Object o_28_0 = config.isMultipleResultSetsEnabled();
            java.lang.Object o_31_0 = config.isUseColumnLabel();
            java.lang.Object o_34_0 = config.isUseGeneratedKeys();
            java.lang.Object o_37_0 = config.getDefaultExecutorType();
            java.lang.Object o_40_0 = config.getDefaultStatementTimeout();
            java.lang.Object o_42_0 = config.getDefaultFetchSize();
            java.lang.Object o_44_0 = config.isMapUnderscoreToCamelCase();
            java.lang.Object o_47_0 = config.isSafeRowBoundsEnabled();
            java.lang.Object o_50_0 = config.getLocalCacheScope();
            java.lang.Object o_53_0 = config.getJdbcTypeForNull();
            java.lang.Object o_56_0 = config.getLazyLoadTriggerMethods();
            java.lang.Object o_61_0 = config.isSafeResultHandlerEnabled();
            java.lang.Object o_64_0 = config.getDefaultScriptingLanguageInstance();
            java.lang.Object o_68_0 = config.isCallSettersOnNulls();
            java.lang.Object o_71_0 = config.getLogPrefix();
            java.lang.Object o_73_0 = config.getLogImpl();
            java.lang.Object o_75_0 = config.getConfigurationFactory();
            org.junit.Assert.fail("shouldSuccessfullyLoadMinimalXMLConfigFile_literalMutation11585 should have thrown BuilderException");
        } catch (org.apache.ibatis.builder.BuilderException eee) {
        }
    }

    @org.junit.Test
    public void shouldSuccessfullyLoadMinimalXMLConfigFile_literalMutation11589_failAssert4_literalMutation11608_failAssert5() throws java.lang.Exception {
        try {
            try {
                java.lang.Object o_68_1 = org.hamcrest.core.Is.is(false);
                java.lang.Object o_64_1 = org.hamcrest.core.Is.is(org.hamcrest.core.IsInstanceOf.instanceOf(org.apache.ibatis.scripting.xmltags.XMLLanguageDriver.class));
                java.lang.Object o_61_1 = org.hamcrest.core.Is.is(true);
                java.lang.Object o_56_1 = org.hamcrest.core.Is.is(((java.util.Set<java.lang.String>) (new java.util.HashSet<java.lang.String>(java.util.Arrays.asList("equals", "clone", "hashCode", "toString")))));
                java.lang.Object o_53_1 = org.hamcrest.core.Is.is(org.apache.ibatis.type.JdbcType);
                java.lang.Object o_50_1 = org.hamcrest.core.Is.is(org.apache.ibatis.session.LocalCacheScope.SESSION);
                java.lang.Object o_47_1 = org.hamcrest.core.Is.is(false);
                java.lang.Object o_44_1 = org.hamcrest.core.Is.is(false);
                java.lang.Object o_37_1 = org.hamcrest.core.Is.is(org.apache.ibatis.session.ExecutorType.SIMPLE);
                java.lang.Object o_34_1 = org.hamcrest.core.Is.is(false);
                java.lang.Object o_31_1 = org.hamcrest.core.Is.is(true);
                java.lang.Object o_28_1 = org.hamcrest.core.Is.is(true);
                java.lang.Object o_25_1 = org.hamcrest.core.Is.is(false);
                java.lang.Object o_22_1 = org.hamcrest.core.Is.is(false);
                java.lang.Object o_18_1 = org.hamcrest.core.Is.is(org.hamcrest.core.IsInstanceOf.instanceOf(org.apache.ibatis.executor.loader.javassist.JavassistProxyFactory.class));
                java.lang.Object o_15_1 = org.hamcrest.core.Is.is(true);
                java.lang.Object o_12_1 = org.hamcrest.core.Is.is(org.apache.ibatis.session.AutoMappingUnknownColumnBehavior.NONE);
                java.lang.Object o_9_1 = org.hamcrest.core.Is.is(org.apache.ibatis.session.AutoMappingBehavior.PARTIAL);
                java.lang.String resource = "";
                java.io.InputStream inputStream = org.apache.ibatis.io.Resources.getResourceAsStream(resource);
                org.apache.ibatis.builder.xml.XMLConfigBuilder builder = new org.apache.ibatis.builder.xml.XMLConfigBuilder(inputStream);
                org.apache.ibatis.session.Configuration config = builder.parse();
                java.lang.Object o_9_0 = config.getAutoMappingBehavior();
                java.lang.Object o_12_0 = config.getAutoMappingUnknownColumnBehavior();
                java.lang.Object o_15_0 = config.isCacheEnabled();
                java.lang.Object o_18_0 = config.getProxyFactory();
                java.lang.Object o_22_0 = config.isLazyLoadingEnabled();
                java.lang.Object o_25_0 = config.isAggressiveLazyLoading();
                java.lang.Object o_28_0 = config.isMultipleResultSetsEnabled();
                java.lang.Object o_31_0 = config.isUseColumnLabel();
                java.lang.Object o_34_0 = config.isUseGeneratedKeys();
                java.lang.Object o_37_0 = config.getDefaultExecutorType();
                java.lang.Object o_40_0 = config.getDefaultStatementTimeout();
                java.lang.Object o_42_0 = config.getDefaultFetchSize();
                java.lang.Object o_44_0 = config.isMapUnderscoreToCamelCase();
                java.lang.Object o_47_0 = config.isSafeRowBoundsEnabled();
                java.lang.Object o_50_0 = config.getLocalCacheScope();
                java.lang.Object o_53_0 = config.getJdbcTypeForNull();
                java.lang.Object o_56_0 = config.getLazyLoadTriggerMethods();
                java.lang.Object o_61_0 = config.isSafeResultHandlerEnabled();
                java.lang.Object o_64_0 = config.getDefaultScriptingLanguageInstance();
                java.lang.Object o_68_0 = config.isCallSettersOnNulls();
                java.lang.Object o_71_0 = config.getLogPrefix();
                java.lang.Object o_73_0 = config.getLogImpl();
                java.lang.Object o_75_0 = config.getConfigurationFactory();
                org.junit.Assert.fail("shouldSuccessfullyLoadMinimalXMLConfigFile_literalMutation11589 should have thrown IOException");
            } catch (java.io.IOException eee) {
            }
            org.junit.Assert.fail("shouldSuccessfullyLoadMinimalXMLConfigFile_literalMutation11589_failAssert4_literalMutation11608 should have thrown BuilderException");
        } catch (org.apache.ibatis.builder.BuilderException eee) {
        }
    }

    /* amplification of org.apache.ibatis.builder.XmlConfigBuilderTest#parseIsTwice */
    /* amplification of org.apache.ibatis.builder.XmlConfigBuilderTest#parseIsTwice_literalMutation5 */
    /* amplification of org.apache.ibatis.builder.XmlConfigBuilderTest#parseIsTwice_literalMutation5_literalMutation57 */
    @org.junit.Test
    public void parseIsTwice_literalMutation5_literalMutation57_literalMutation568_failAssert125() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String resource = "";
            java.io.InputStream inputStream = org.apache.ibatis.io.Resources.getResourceAsStream(resource);
            org.apache.ibatis.builder.xml.XMLConfigBuilder builder = new org.apache.ibatis.builder.xml.XMLConfigBuilder(inputStream);
            builder.parse();
            expectedException.expect(org.apache.ibatis.builder.BuilderException.class);
            expectedException.expectMessage("Each XMLConfigBuilder can only be used once.");
            builder.parse();
            org.junit.Assert.fail("parseIsTwice_literalMutation5_literalMutation57_literalMutation568 should have thrown BuilderException");
        } catch (org.apache.ibatis.builder.BuilderException eee) {
        }
    }

    /* amplification of org.apache.ibatis.builder.XmlConfigBuilderTest#registerJavaTypeInitializingTypeHandler */
    /* amplification of org.apache.ibatis.builder.XmlConfigBuilderTest#registerJavaTypeInitializingTypeHandler_literalMutation1908 */
    @org.junit.Test
    public void registerJavaTypeInitializingTypeHandler_literalMutation1908_literalMutation2467_failAssert129() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            final java.lang.String MAPPER_CONFIG = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" + (((((("" + "<coniguration>\n") + "  <typeHandlers>\n") + "    <typeHandler javaType=\"org.apache.ibatis.builder.XmlConfigBuilderTest$MyEnum\"\n") + "      handler=\"org.apache.ibatis.builder.XmlConfigBuilderTest$EnumOrderTypeHandler\"/>\n") + "  </typeHandlers>\n") + "</configuration>\n");
            org.apache.ibatis.builder.xml.XMLConfigBuilder builder = new org.apache.ibatis.builder.xml.XMLConfigBuilder(new java.io.StringReader(MAPPER_CONFIG));
            builder.parse();
            org.apache.ibatis.type.TypeHandlerRegistry typeHandlerRegistry = builder.getConfiguration().getTypeHandlerRegistry();
            org.apache.ibatis.type.TypeHandler<org.apache.ibatis.builder.AmplXmlConfigBuilderTest.MyEnum> typeHandler = typeHandlerRegistry.getTypeHandler(org.apache.ibatis.builder.AmplXmlConfigBuilderTest.MyEnum.class);
            // MethodAssertGenerator build local variable
            Object o_11_0 = typeHandler instanceof org.apache.ibatis.builder.AmplXmlConfigBuilderTest.EnumOrderTypeHandler;
            // MethodAssertGenerator build local variable
            Object o_12_0 = org.apache.ibatis.builder.AmplXmlConfigBuilderTest.MyEnum.values();
            org.junit.Assert.fail("registerJavaTypeInitializingTypeHandler_literalMutation1908_literalMutation2467 should have thrown BuilderException");
        } catch (org.apache.ibatis.builder.BuilderException eee) {
        }
    }

    /* amplification of org.apache.ibatis.builder.XmlConfigBuilderTest#registerJavaTypeInitializingTypeHandler */
    /* amplification of org.apache.ibatis.builder.XmlConfigBuilderTest#registerJavaTypeInitializingTypeHandler_literalMutation1915 */
    @org.junit.Test
    public void registerJavaTypeInitializingTypeHandler_literalMutation1915_literalMutation2762_failAssert145_literalMutation9440() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            final java.lang.String MAPPER_CONFIG = "Sk@T=(^GEi:_1Ph]Wuv4I`gbWgjFfpcZ::EJU?30" + (((((("<!DOCTYPE configuration PUBLIC \"-//mybatis.org//DTD Config 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-config.dtd\">\n" + "<configuration>\n") + "  R<tPypeHandlers>\n") + "    <typeHandler javaType=\"org.apache.ibatis.builder.XmlConfigBuilderTest$MyEnum\"\n") + "      handler=\"org.apache.ibatis.builder.XmlConfigBuilderTest$EnumOrderTypeHandler\"/>\n") + "  </typeHandlers>\n") + "</configuration>\n");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(MAPPER_CONFIG, "Sk@T=(^GEi:_1Ph]Wuv4I`gbWgjFfpcZ::EJU?30<!DOCTYPE configuration PUBLIC \"-//mybatis.org//DTD Config 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-config.dtd\">\n<configuration>\n  R<tPypeHandlers>\n    <typeHandler javaType=\"org.apache.ibatis.builder.XmlConfigBuilderTest$MyEnum\"\n      handler=\"org.apache.ibatis.builder.XmlConfigBuilderTest$EnumOrderTypeHandler\"/>\n  </typeHandlers>\n</configuration>\n");
            org.apache.ibatis.builder.xml.XMLConfigBuilder builder = new org.apache.ibatis.builder.xml.XMLConfigBuilder(new java.io.StringReader(MAPPER_CONFIG));
            builder.parse();
            org.apache.ibatis.type.TypeHandlerRegistry typeHandlerRegistry = builder.getConfiguration().getTypeHandlerRegistry();
            org.apache.ibatis.type.TypeHandler<org.apache.ibatis.builder.AmplXmlConfigBuilderTest.MyEnum> typeHandler = typeHandlerRegistry.getTypeHandler(org.apache.ibatis.builder.AmplXmlConfigBuilderTest.MyEnum.class);
            // MethodAssertGenerator build local variable
            Object o_11_0 = typeHandler instanceof org.apache.ibatis.builder.AmplXmlConfigBuilderTest.EnumOrderTypeHandler;
            // MethodAssertGenerator build local variable
            Object o_12_0 = org.apache.ibatis.builder.AmplXmlConfigBuilderTest.MyEnum.values();
            org.junit.Assert.fail("registerJavaTypeInitializingTypeHandler_literalMutation1915_literalMutation2762 should have thrown BuilderException");
        } catch (org.apache.ibatis.builder.BuilderException eee) {
        }
    }
}

