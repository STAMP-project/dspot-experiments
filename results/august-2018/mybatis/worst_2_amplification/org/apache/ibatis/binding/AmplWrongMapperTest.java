package org.apache.ibatis.binding;


import java.util.Collections;
import org.apache.ibatis.builder.BuilderException;
import org.apache.ibatis.executor.ExecutorTestHelper;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMap;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.reflection.ReflectorFactory;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeAliasRegistry;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.junit.Assert;
import org.junit.Test;


public class AmplWrongMapperTest {
    @Test(timeout = 10000)
    public void shouldFailForBothOneAndMany_failAssert0() throws Exception {
        try {
            Configuration configuration = new Configuration();
            configuration.addMapper(MapperWithOneAndMany.class);
            org.junit.Assert.fail("shouldFailForBothOneAndMany should have thrown BuilderException");
        } catch (BuilderException expected) {
            Assert.assertEquals("Cannot use both @One and @Many annotations in the same @Result", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldFailForBothOneAndMany_mg6_failAssert38() throws Exception {
        try {
            MappedStatement __DSPOT_ms_4 = ExecutorTestHelper.createSelectAuthorWithIDof99MappedStatement(new Configuration(null));
            Configuration configuration = new Configuration();
            configuration.addMapper(MapperWithOneAndMany.class);
            configuration.addMappedStatement(__DSPOT_ms_4);
            org.junit.Assert.fail("shouldFailForBothOneAndMany_mg6 should have thrown BuilderException");
        } catch (BuilderException expected) {
            Assert.assertEquals("Cannot use both @One and @Many annotations in the same @Result", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldFailForBothOneAndMany_mg28_failAssert14() throws Exception {
        try {
            Integer __DSPOT_defaultStatementTimeout_30 = -864755754;
            Configuration configuration = new Configuration();
            configuration.addMapper(MapperWithOneAndMany.class);
            configuration.setDefaultStatementTimeout(__DSPOT_defaultStatementTimeout_30);
            org.junit.Assert.fail("shouldFailForBothOneAndMany_mg28 should have thrown BuilderException");
        } catch (BuilderException expected) {
            Assert.assertEquals("Cannot use both @One and @Many annotations in the same @Result", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldFailForBothOneAndMany_mg22_failAssert31_add404() throws Exception {
        try {
            BoundSql __DSPOT_boundSql_24 = new BoundSql(new Configuration(null), "JV)d4}^w[&oDAIOw? O!", Collections.<ParameterMapping>emptyList(), new Object());
            Object __DSPOT_parameterObject_23 = new Object();
            MappedStatement o_shouldFailForBothOneAndMany_mg22_failAssert31_add404__10 = ExecutorTestHelper.createSelectAuthorWithIDof99MappedStatement(new Configuration());
            Assert.assertFalse(((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404__10)).getResultMaps().isEmpty());
            Assert.assertFalse(((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404__10)).isUseCache());
            Assert.assertNull(((Configuration) (((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404__10)).getConfiguration())).getLogPrefix());
            Assert.assertFalse(((Configuration) (((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404__10)).getConfiguration())).isCallSettersOnNulls());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404__10)).getConfiguration())).isUseActualParamName());
            Assert.assertFalse(((Configuration) (((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404__10)).getConfiguration())).isReturnInstanceForEmptyRow());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404__10)).getConfiguration())).isSafeResultHandlerEnabled());
            Assert.assertFalse(((Configuration) (((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404__10)).getConfiguration())).isSafeRowBoundsEnabled());
            Assert.assertFalse(((Configuration) (((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404__10)).getConfiguration())).isMapUnderscoreToCamelCase());
            Assert.assertNull(((Configuration) (((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404__10)).getConfiguration())).getEnvironment());
            Assert.assertFalse(((Configuration) (((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404__10)).getConfiguration())).isLazyLoadingEnabled());
            Assert.assertFalse(((Configuration) (((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404__10)).getConfiguration())).isAggressiveLazyLoading());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404__10)).getConfiguration())).isMultipleResultSetsEnabled());
            Assert.assertTrue(((org.apache.ibatis.session.Configuration)((org.apache.ibatis.mapping.MappedStatement)o_shouldFailForBothOneAndMany_mg22_failAssert31_add404__10).getConfiguration()).getLazyLoadTriggerMethods().contains("clone"));
            Assert.assertTrue(((org.apache.ibatis.session.Configuration)((org.apache.ibatis.mapping.MappedStatement)o_shouldFailForBothOneAndMany_mg22_failAssert31_add404__10).getConfiguration()).getLazyLoadTriggerMethods().contains("toString"));
            Assert.assertTrue(((org.apache.ibatis.session.Configuration)((org.apache.ibatis.mapping.MappedStatement)o_shouldFailForBothOneAndMany_mg22_failAssert31_add404__10).getConfiguration()).getLazyLoadTriggerMethods().contains("hashCode"));
            Assert.assertTrue(((org.apache.ibatis.session.Configuration)((org.apache.ibatis.mapping.MappedStatement)o_shouldFailForBothOneAndMany_mg22_failAssert31_add404__10).getConfiguration()).getLazyLoadTriggerMethods().contains("equals"));
            Assert.assertFalse(((Configuration) (((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404__10)).getConfiguration())).isUseGeneratedKeys());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404__10)).getConfiguration())).isCacheEnabled());
            Assert.assertNull(((Configuration) (((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404__10)).getConfiguration())).getDefaultStatementTimeout());
            Assert.assertNull(((Configuration) (((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404__10)).getConfiguration())).getDefaultFetchSize());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404__10)).getConfiguration())).isUseColumnLabel());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404__10)).getConfiguration())).getVariables().isEmpty());
            Assert.assertFalse(((TypeHandlerRegistry) (((Configuration) (((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404__10)).getConfiguration())).getTypeHandlerRegistry())).getTypeHandlers().isEmpty());
            Assert.assertFalse(((TypeAliasRegistry) (((Configuration) (((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404__10)).getConfiguration())).getTypeAliasRegistry())).getTypeAliases().isEmpty());
            Assert.assertTrue(((MapperRegistry) (((Configuration) (((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404__10)).getConfiguration())).getMapperRegistry())).getMappers().isEmpty());
            Assert.assertTrue(((ReflectorFactory) (((Configuration) (((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404__10)).getConfiguration())).getReflectorFactory())).isClassCacheEnabled());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404__10)).getConfiguration())).getInterceptors().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404__10)).getConfiguration())).getKeyGeneratorNames().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404__10)).getConfiguration())).getKeyGenerators().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404__10)).getConfiguration())).getCacheNames().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404__10)).getConfiguration())).getCaches().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404__10)).getConfiguration())).getResultMapNames().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404__10)).getConfiguration())).getResultMaps().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404__10)).getConfiguration())).getParameterMapNames().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404__10)).getConfiguration())).getParameterMaps().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404__10)).getConfiguration())).getMappedStatementNames().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404__10)).getConfiguration())).getMappedStatements().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404__10)).getConfiguration())).getIncompleteStatements().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404__10)).getConfiguration())).getIncompleteCacheRefs().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404__10)).getConfiguration())).getSqlFragments().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404__10)).getConfiguration())).getIncompleteResultMaps().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404__10)).getConfiguration())).getIncompleteMethods().isEmpty());
            Assert.assertNull(((Configuration) (((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404__10)).getConfiguration())).getDatabaseId());
            Assert.assertNull(((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404__10)).getResultSetType());
            Assert.assertFalse(((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404__10)).isFlushCacheRequired());
            Assert.assertNull(((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404__10)).getKeyProperties());
            Assert.assertNull(((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404__10)).getKeyColumns());
            Assert.assertNull(((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404__10)).getResultSets());
            Assert.assertNull(((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404__10)).getResulSets());
            Assert.assertNull(((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404__10)).getResource());
            Assert.assertEquals("selectAuthor", ((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404__10)).getId());
            Assert.assertNull(((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404__10)).getCache());
            Assert.assertEquals("defaultParameterMap", ((ParameterMap) (((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404__10)).getParameterMap())).getId());
            Assert.assertTrue(((ParameterMap) (((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404__10)).getParameterMap())).getParameterMappings().isEmpty());
            Assert.assertNull(((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404__10)).getDatabaseId());
            Assert.assertNull(((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404__10)).getTimeout());
            Assert.assertFalse(((Log) (((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404__10)).getStatementLog())).isDebugEnabled());
            Assert.assertFalse(((Log) (((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404__10)).getStatementLog())).isTraceEnabled());
            Assert.assertFalse(((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404__10)).hasNestedResultMaps());
            Assert.assertFalse(((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404__10)).isResultOrdered());
            Assert.assertNull(((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404__10)).getFetchSize());
            MappedStatement __DSPOT_mappedStatement_22 = ExecutorTestHelper.createSelectAuthorWithIDof99MappedStatement(new Configuration());
            Configuration configuration = new Configuration();
            configuration.addMapper(MapperWithOneAndMany.class);
            configuration.newParameterHandler(__DSPOT_mappedStatement_22, __DSPOT_parameterObject_23, __DSPOT_boundSql_24);
            org.junit.Assert.fail("shouldFailForBothOneAndMany_mg22 should have thrown BuilderException");
        } catch (BuilderException expected) {
        }
    }

    @Test(timeout = 10000)
    public void shouldFailForBothOneAndMany_mg22_failAssert31_add404_add2806() throws Exception {
        try {
            BoundSql __DSPOT_boundSql_24 = new BoundSql(new Configuration(null), "JV)d4}^w[&oDAIOw? O!", Collections.<ParameterMapping>emptyList(), new Object());
            Object __DSPOT_parameterObject_23 = new Object();
            MappedStatement o_shouldFailForBothOneAndMany_mg22_failAssert31_add404__10 = ExecutorTestHelper.createSelectAuthorWithIDof99MappedStatement(new Configuration());
            boolean o_shouldFailForBothOneAndMany_mg22_failAssert31_add404_add2806__14 = ((org.apache.ibatis.session.Configuration)((org.apache.ibatis.mapping.MappedStatement)o_shouldFailForBothOneAndMany_mg22_failAssert31_add404__10).getConfiguration()).getLazyLoadTriggerMethods().contains("equals");
            Assert.assertTrue(o_shouldFailForBothOneAndMany_mg22_failAssert31_add404_add2806__14);
            MappedStatement __DSPOT_mappedStatement_22 = ExecutorTestHelper.createSelectAuthorWithIDof99MappedStatement(new Configuration());
            Configuration configuration = new Configuration();
            configuration.addMapper(MapperWithOneAndMany.class);
            configuration.newParameterHandler(__DSPOT_mappedStatement_22, __DSPOT_parameterObject_23, __DSPOT_boundSql_24);
            org.junit.Assert.fail("shouldFailForBothOneAndMany_mg22 should have thrown BuilderException");
        } catch (BuilderException expected) {
        }
    }

    @Test(timeout = 10000)
    public void shouldFailForBothOneAndMany_mg22_failAssert31_add404_add2908() throws Exception {
        try {
            BoundSql __DSPOT_boundSql_24 = new BoundSql(new Configuration(null), "JV)d4}^w[&oDAIOw? O!", Collections.<ParameterMapping>emptyList(), new Object());
            Object __DSPOT_parameterObject_23 = new Object();
            MappedStatement o_shouldFailForBothOneAndMany_mg22_failAssert31_add404__10 = ExecutorTestHelper.createSelectAuthorWithIDof99MappedStatement(new Configuration());
            MappedStatement o_shouldFailForBothOneAndMany_mg22_failAssert31_add404_add2908__14 = ExecutorTestHelper.createSelectAuthorWithIDof99MappedStatement(new Configuration());
            Assert.assertNull(((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404_add2908__14)).getKeyProperties());
            Assert.assertNull(((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404_add2908__14)).getResulSets());
            Assert.assertNull(((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404_add2908__14)).getFetchSize());
            Assert.assertTrue(((ParameterMap) (((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404_add2908__14)).getParameterMap())).getParameterMappings().isEmpty());
            Assert.assertEquals("defaultParameterMap", ((ParameterMap) (((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404_add2908__14)).getParameterMap())).getId());
            Assert.assertFalse(((Configuration) (((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404_add2908__14)).getConfiguration())).isMapUnderscoreToCamelCase());
            Assert.assertFalse(((Configuration) (((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404_add2908__14)).getConfiguration())).isLazyLoadingEnabled());
            Assert.assertNull(((Configuration) (((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404_add2908__14)).getConfiguration())).getLogPrefix());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404_add2908__14)).getConfiguration())).isUseActualParamName());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404_add2908__14)).getConfiguration())).getKeyGeneratorNames().isEmpty());
            Assert.assertFalse(((Configuration) (((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404_add2908__14)).getConfiguration())).isReturnInstanceForEmptyRow());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404_add2908__14)).getConfiguration())).getKeyGenerators().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404_add2908__14)).getConfiguration())).isMultipleResultSetsEnabled());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404_add2908__14)).getConfiguration())).getInterceptors().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404_add2908__14)).getConfiguration())).getIncompleteStatements().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404_add2908__14)).getConfiguration())).getParameterMaps().isEmpty());
            Assert.assertNull(((Configuration) (((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404_add2908__14)).getConfiguration())).getDefaultFetchSize());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404_add2908__14)).getConfiguration())).getCacheNames().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404_add2908__14)).getConfiguration())).getIncompleteCacheRefs().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404_add2908__14)).getConfiguration())).getIncompleteResultMaps().isEmpty());
            Assert.assertFalse(((TypeAliasRegistry) (((Configuration) (((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404_add2908__14)).getConfiguration())).getTypeAliasRegistry())).getTypeAliases().isEmpty());
            Assert.assertTrue(((MapperRegistry) (((Configuration) (((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404_add2908__14)).getConfiguration())).getMapperRegistry())).getMappers().isEmpty());
            Assert.assertTrue(((org.apache.ibatis.session.Configuration)((org.apache.ibatis.mapping.MappedStatement)o_shouldFailForBothOneAndMany_mg22_failAssert31_add404_add2908__14).getConfiguration()).getLazyLoadTriggerMethods().contains("clone"));
            Assert.assertTrue(((org.apache.ibatis.session.Configuration)((org.apache.ibatis.mapping.MappedStatement)o_shouldFailForBothOneAndMany_mg22_failAssert31_add404_add2908__14).getConfiguration()).getLazyLoadTriggerMethods().contains("toString"));
            Assert.assertTrue(((org.apache.ibatis.session.Configuration)((org.apache.ibatis.mapping.MappedStatement)o_shouldFailForBothOneAndMany_mg22_failAssert31_add404_add2908__14).getConfiguration()).getLazyLoadTriggerMethods().contains("hashCode"));
            Assert.assertTrue(((org.apache.ibatis.session.Configuration)((org.apache.ibatis.mapping.MappedStatement)o_shouldFailForBothOneAndMany_mg22_failAssert31_add404_add2908__14).getConfiguration()).getLazyLoadTriggerMethods().contains("equals"));
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404_add2908__14)).getConfiguration())).getMappedStatementNames().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404_add2908__14)).getConfiguration())).getVariables().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404_add2908__14)).getConfiguration())).isUseColumnLabel());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404_add2908__14)).getConfiguration())).getParameterMapNames().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404_add2908__14)).getConfiguration())).getResultMaps().isEmpty());
            Assert.assertTrue(((ReflectorFactory) (((Configuration) (((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404_add2908__14)).getConfiguration())).getReflectorFactory())).isClassCacheEnabled());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404_add2908__14)).getConfiguration())).getResultMapNames().isEmpty());
            Assert.assertFalse(((Configuration) (((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404_add2908__14)).getConfiguration())).isUseGeneratedKeys());
            Assert.assertNull(((Configuration) (((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404_add2908__14)).getConfiguration())).getDatabaseId());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404_add2908__14)).getConfiguration())).isCacheEnabled());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404_add2908__14)).getConfiguration())).getSqlFragments().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404_add2908__14)).getConfiguration())).getCaches().isEmpty());
            Assert.assertFalse(((TypeHandlerRegistry) (((Configuration) (((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404_add2908__14)).getConfiguration())).getTypeHandlerRegistry())).getTypeHandlers().isEmpty());
            Assert.assertNull(((Configuration) (((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404_add2908__14)).getConfiguration())).getEnvironment());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404_add2908__14)).getConfiguration())).getMappedStatements().isEmpty());
            Assert.assertFalse(((Configuration) (((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404_add2908__14)).getConfiguration())).isCallSettersOnNulls());
            Assert.assertFalse(((Configuration) (((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404_add2908__14)).getConfiguration())).isSafeRowBoundsEnabled());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404_add2908__14)).getConfiguration())).isSafeResultHandlerEnabled());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404_add2908__14)).getConfiguration())).getIncompleteMethods().isEmpty());
            Assert.assertNull(((Configuration) (((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404_add2908__14)).getConfiguration())).getDefaultStatementTimeout());
            Assert.assertFalse(((Configuration) (((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404_add2908__14)).getConfiguration())).isAggressiveLazyLoading());
            Assert.assertNull(((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404_add2908__14)).getResultSets());
            Assert.assertFalse(((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404_add2908__14)).getResultMaps().isEmpty());
            Assert.assertNull(((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404_add2908__14)).getResultSetType());
            Assert.assertNull(((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404_add2908__14)).getDatabaseId());
            Assert.assertFalse(((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404_add2908__14)).hasNestedResultMaps());
            Assert.assertFalse(((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404_add2908__14)).isUseCache());
            Assert.assertFalse(((Log) (((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404_add2908__14)).getStatementLog())).isDebugEnabled());
            Assert.assertFalse(((Log) (((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404_add2908__14)).getStatementLog())).isTraceEnabled());
            Assert.assertFalse(((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404_add2908__14)).isFlushCacheRequired());
            Assert.assertFalse(((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404_add2908__14)).isResultOrdered());
            Assert.assertNull(((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404_add2908__14)).getKeyColumns());
            Assert.assertNull(((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404_add2908__14)).getTimeout());
            Assert.assertNull(((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404_add2908__14)).getResource());
            Assert.assertEquals("selectAuthor", ((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404_add2908__14)).getId());
            Assert.assertNull(((MappedStatement) (o_shouldFailForBothOneAndMany_mg22_failAssert31_add404_add2908__14)).getCache());
            MappedStatement __DSPOT_mappedStatement_22 = ExecutorTestHelper.createSelectAuthorWithIDof99MappedStatement(new Configuration());
            Configuration configuration = new Configuration();
            configuration.addMapper(MapperWithOneAndMany.class);
            configuration.newParameterHandler(__DSPOT_mappedStatement_22, __DSPOT_parameterObject_23, __DSPOT_boundSql_24);
            org.junit.Assert.fail("shouldFailForBothOneAndMany_mg22 should have thrown BuilderException");
        } catch (BuilderException expected) {
        }
    }
}

