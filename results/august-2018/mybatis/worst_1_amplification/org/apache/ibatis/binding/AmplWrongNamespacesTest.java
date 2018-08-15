package org.apache.ibatis.binding;


import java.util.Collections;
import java.util.List;
import org.apache.ibatis.builder.BuilderException;
import org.apache.ibatis.cache.Cache;
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


public class AmplWrongNamespacesTest {
    @Test(timeout = 10000)
    public void shouldFailForWrongNamespace_failAssert1() throws Exception {
        try {
            Configuration configuration = new Configuration();
            configuration.addMapper(WrongNamespaceMapper.class);
            org.junit.Assert.fail("shouldFailForWrongNamespace should have thrown BuilderException");
        } catch (BuilderException expected) {
            Assert.assertEquals("Error parsing Mapper XML. The XML location is \'org/apache/ibatis/binding/WrongNamespaceMapper.xml\'. Cause: org.apache.ibatis.builder.BuilderException: Wrong namespace. Expected \'org.apache.ibatis.binding.WrongNamespaceMapper\' but found \'wrong.namespace\'.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldFailForWrongNamespace_mg3490_failAssert67() throws Exception {
        try {
            Integer __DSPOT_defaultStatementTimeout_1900 = -1858147503;
            Configuration configuration = new Configuration();
            configuration.addMapper(WrongNamespaceMapper.class);
            configuration.setDefaultStatementTimeout(__DSPOT_defaultStatementTimeout_1900);
            org.junit.Assert.fail("shouldFailForWrongNamespace_mg3490 should have thrown BuilderException");
        } catch (BuilderException expected) {
            Assert.assertEquals("Error parsing Mapper XML. The XML location is \'org/apache/ibatis/binding/WrongNamespaceMapper.xml\'. Cause: org.apache.ibatis.builder.BuilderException: Wrong namespace. Expected \'org.apache.ibatis.binding.WrongNamespaceMapper\' but found \'wrong.namespace\'.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldFailForWrongNamespace_mg3484_failAssert50() throws Exception {
        try {
            BoundSql __DSPOT_boundSql_1894 = new BoundSql(new Configuration(null), "]jzLWL!g&%#E-YpN8=G*", Collections.<ParameterMapping>emptyList(), new Object());
            Object __DSPOT_parameterObject_1893 = new Object();
            MappedStatement __DSPOT_mappedStatement_1892 = ExecutorTestHelper.createSelectAuthorWithIDof99MappedStatement(new Configuration(null));
            Configuration configuration = new Configuration();
            configuration.addMapper(WrongNamespaceMapper.class);
            configuration.newParameterHandler(__DSPOT_mappedStatement_1892, __DSPOT_parameterObject_1893, __DSPOT_boundSql_1894);
            org.junit.Assert.fail("shouldFailForWrongNamespace_mg3484 should have thrown BuilderException");
        } catch (BuilderException expected) {
            Assert.assertEquals("Error parsing Mapper XML. The XML location is \'org/apache/ibatis/binding/WrongNamespaceMapper.xml\'. Cause: org.apache.ibatis.builder.BuilderException: Wrong namespace. Expected \'org.apache.ibatis.binding.WrongNamespaceMapper\' but found \'wrong.namespace\'.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldFailForWrongNamespace_mg3468_failAssert51() throws Exception {
        try {
            MappedStatement __DSPOT_ms_1874 = ExecutorTestHelper.createInsertAuthorWithIDof99MappedStatement(new Configuration(null));
            Configuration configuration = new Configuration();
            configuration.addMapper(WrongNamespaceMapper.class);
            configuration.addMappedStatement(__DSPOT_ms_1874);
            org.junit.Assert.fail("shouldFailForWrongNamespace_mg3468 should have thrown BuilderException");
        } catch (BuilderException expected) {
            Assert.assertEquals("Error parsing Mapper XML. The XML location is \'org/apache/ibatis/binding/WrongNamespaceMapper.xml\'. Cause: org.apache.ibatis.builder.BuilderException: Wrong namespace. Expected \'org.apache.ibatis.binding.WrongNamespaceMapper\' but found \'wrong.namespace\'.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldFailForWrongNamespace_mg3484_failAssert50_add3791() throws Exception {
        try {
            List o_shouldFailForWrongNamespace_mg3484_failAssert50_add3791__3 = Collections.<ParameterMapping>emptyList();
            Assert.assertTrue(o_shouldFailForWrongNamespace_mg3484_failAssert50_add3791__3.isEmpty());
            BoundSql __DSPOT_boundSql_1894 = new BoundSql(new Configuration(null), "]jzLWL!g&%#E-YpN8=G*", Collections.<ParameterMapping>emptyList(), new Object());
            Object __DSPOT_parameterObject_1893 = new Object();
            MappedStatement __DSPOT_mappedStatement_1892 = ExecutorTestHelper.createSelectAuthorWithIDof99MappedStatement(new Configuration(null));
            Configuration configuration = new Configuration();
            configuration.addMapper(WrongNamespaceMapper.class);
            configuration.newParameterHandler(__DSPOT_mappedStatement_1892, __DSPOT_parameterObject_1893, __DSPOT_boundSql_1894);
            org.junit.Assert.fail("shouldFailForWrongNamespace_mg3484 should have thrown BuilderException");
        } catch (BuilderException expected) {
        }
    }

    @Test(timeout = 10000)
    public void shouldFailForWrongNamespace_mg3468_failAssert51_add3796() throws Exception {
        try {
            MappedStatement o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796__3 = ExecutorTestHelper.createInsertAuthorWithIDof99MappedStatement(new Configuration(null));
            Assert.assertNull(((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796__3)).getDatabaseId());
            Assert.assertFalse(((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796__3)).hasNestedResultMaps());
            Assert.assertFalse(((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796__3)).isUseCache());
            Assert.assertNull(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796__3)).getConfiguration())).getLogPrefix());
            Assert.assertFalse(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796__3)).getConfiguration())).isCallSettersOnNulls());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796__3)).getConfiguration())).isUseActualParamName());
            Assert.assertFalse(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796__3)).getConfiguration())).isReturnInstanceForEmptyRow());
            Assert.assertNull(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796__3)).getConfiguration())).getDatabaseId());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796__3)).getConfiguration())).isSafeResultHandlerEnabled());
            Assert.assertFalse(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796__3)).getConfiguration())).isSafeRowBoundsEnabled());
            Assert.assertFalse(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796__3)).getConfiguration())).isMapUnderscoreToCamelCase());
            Assert.assertNull(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796__3)).getConfiguration())).getEnvironment());
            Assert.assertFalse(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796__3)).getConfiguration())).isLazyLoadingEnabled());
            Assert.assertFalse(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796__3)).getConfiguration())).isAggressiveLazyLoading());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796__3)).getConfiguration())).isMultipleResultSetsEnabled());
            Assert.assertTrue(((org.apache.ibatis.session.Configuration)((org.apache.ibatis.mapping.MappedStatement)o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796__3).getConfiguration()).getLazyLoadTriggerMethods().contains("clone"));
            Assert.assertTrue(((org.apache.ibatis.session.Configuration)((org.apache.ibatis.mapping.MappedStatement)o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796__3).getConfiguration()).getLazyLoadTriggerMethods().contains("toString"));
            Assert.assertTrue(((org.apache.ibatis.session.Configuration)((org.apache.ibatis.mapping.MappedStatement)o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796__3).getConfiguration()).getLazyLoadTriggerMethods().contains("hashCode"));
            Assert.assertTrue(((org.apache.ibatis.session.Configuration)((org.apache.ibatis.mapping.MappedStatement)o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796__3).getConfiguration()).getLazyLoadTriggerMethods().contains("equals"));
            Assert.assertFalse(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796__3)).getConfiguration())).isUseGeneratedKeys());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796__3)).getConfiguration())).isCacheEnabled());
            Assert.assertNull(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796__3)).getConfiguration())).getDefaultStatementTimeout());
            Assert.assertNull(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796__3)).getConfiguration())).getDefaultFetchSize());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796__3)).getConfiguration())).isUseColumnLabel());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796__3)).getConfiguration())).getVariables().isEmpty());
            Assert.assertFalse(((TypeHandlerRegistry) (((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796__3)).getConfiguration())).getTypeHandlerRegistry())).getTypeHandlers().isEmpty());
            Assert.assertFalse(((TypeAliasRegistry) (((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796__3)).getConfiguration())).getTypeAliasRegistry())).getTypeAliases().isEmpty());
            Assert.assertTrue(((MapperRegistry) (((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796__3)).getConfiguration())).getMapperRegistry())).getMappers().isEmpty());
            Assert.assertTrue(((ReflectorFactory) (((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796__3)).getConfiguration())).getReflectorFactory())).isClassCacheEnabled());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796__3)).getConfiguration())).getInterceptors().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796__3)).getConfiguration())).getKeyGeneratorNames().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796__3)).getConfiguration())).getKeyGenerators().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796__3)).getConfiguration())).getCacheNames().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796__3)).getConfiguration())).getCaches().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796__3)).getConfiguration())).getResultMapNames().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796__3)).getConfiguration())).getParameterMapNames().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796__3)).getConfiguration())).getParameterMaps().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796__3)).getConfiguration())).getMappedStatementNames().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796__3)).getConfiguration())).getMappedStatements().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796__3)).getConfiguration())).getIncompleteStatements().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796__3)).getConfiguration())).getIncompleteCacheRefs().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796__3)).getConfiguration())).getIncompleteResultMaps().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796__3)).getConfiguration())).getIncompleteMethods().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796__3)).getConfiguration())).getSqlFragments().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796__3)).getConfiguration())).getResultMaps().isEmpty());
            Assert.assertNull(((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796__3)).getResultSetType());
            Assert.assertFalse(((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796__3)).isResultOrdered());
            Assert.assertFalse(((Log) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796__3)).getStatementLog())).isTraceEnabled());
            Assert.assertFalse(((Log) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796__3)).getStatementLog())).isDebugEnabled());
            Assert.assertNull(((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796__3)).getResultSets());
            Assert.assertNull(((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796__3)).getFetchSize());
            Assert.assertTrue(((ParameterMap) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796__3)).getParameterMap())).getParameterMappings().isEmpty());
            Assert.assertEquals("defaultParameterMap", ((ParameterMap) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796__3)).getParameterMap())).getId());
            Assert.assertNull(((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796__3)).getKeyProperties());
            Assert.assertFalse(((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796__3)).isFlushCacheRequired());
            Assert.assertNull(((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796__3)).getResulSets());
            Assert.assertTrue(((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796__3)).getResultMaps().isEmpty());
            Assert.assertNull(((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796__3)).getKeyColumns());
            Assert.assertNull(((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796__3)).getTimeout());
            Assert.assertNull(((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796__3)).getResource());
            Assert.assertEquals("insertAuthor", ((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796__3)).getId());
            Assert.assertNull(((Cache) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796__3)).getCache())).getReadWriteLock());
            Assert.assertEquals("author_cache", ((Cache) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796__3)).getCache())).getId());
            Assert.assertEquals(0, ((int) (((Cache) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796__3)).getCache())).getSize())));
            MappedStatement __DSPOT_ms_1874 = ExecutorTestHelper.createInsertAuthorWithIDof99MappedStatement(new Configuration(null));
            Configuration configuration = new Configuration();
            configuration.addMapper(WrongNamespaceMapper.class);
            configuration.addMappedStatement(__DSPOT_ms_1874);
            org.junit.Assert.fail("shouldFailForWrongNamespace_mg3468 should have thrown BuilderException");
        } catch (BuilderException expected) {
        }
    }

    @Test(timeout = 10000)
    public void shouldFailForWrongNamespace_mg3484_failAssert50_add3791_add6230() throws Exception {
        try {
            List o_shouldFailForWrongNamespace_mg3484_failAssert50_add3791__3 = Collections.<ParameterMapping>emptyList();
            List o_shouldFailForWrongNamespace_mg3484_failAssert50_add3791_add6230__6 = Collections.<ParameterMapping>emptyList();
            Assert.assertTrue(o_shouldFailForWrongNamespace_mg3484_failAssert50_add3791_add6230__6.isEmpty());
            BoundSql __DSPOT_boundSql_1894 = new BoundSql(new Configuration(null), "]jzLWL!g&%#E-YpN8=G*", Collections.<ParameterMapping>emptyList(), new Object());
            Object __DSPOT_parameterObject_1893 = new Object();
            MappedStatement __DSPOT_mappedStatement_1892 = ExecutorTestHelper.createSelectAuthorWithIDof99MappedStatement(new Configuration(null));
            Configuration configuration = new Configuration();
            configuration.addMapper(WrongNamespaceMapper.class);
            configuration.newParameterHandler(__DSPOT_mappedStatement_1892, __DSPOT_parameterObject_1893, __DSPOT_boundSql_1894);
            org.junit.Assert.fail("shouldFailForWrongNamespace_mg3484 should have thrown BuilderException");
        } catch (BuilderException expected) {
        }
    }

    @Test(timeout = 10000)
    public void shouldFailForWrongNamespace_mg3484_failAssert50_add3791_add6231() throws Exception {
        try {
            List o_shouldFailForWrongNamespace_mg3484_failAssert50_add3791__3 = Collections.<ParameterMapping>emptyList();
            BoundSql __DSPOT_boundSql_1894 = new BoundSql(new Configuration(null), "]jzLWL!g&%#E-YpN8=G*", Collections.<ParameterMapping>emptyList(), new Object());
            Object __DSPOT_parameterObject_1893 = new Object();
            MappedStatement o_shouldFailForWrongNamespace_mg3484_failAssert50_add3791_add6231__13 = ExecutorTestHelper.createSelectAuthorWithIDof99MappedStatement(new Configuration(null));
            Assert.assertFalse(((MappedStatement) (o_shouldFailForWrongNamespace_mg3484_failAssert50_add3791_add6231__13)).isResultOrdered());
            Assert.assertNull(((MappedStatement) (o_shouldFailForWrongNamespace_mg3484_failAssert50_add3791_add6231__13)).getKeyProperties());
            Assert.assertFalse(((Log) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3484_failAssert50_add3791_add6231__13)).getStatementLog())).isTraceEnabled());
            Assert.assertFalse(((Log) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3484_failAssert50_add3791_add6231__13)).getStatementLog())).isDebugEnabled());
            Assert.assertNull(((MappedStatement) (o_shouldFailForWrongNamespace_mg3484_failAssert50_add3791_add6231__13)).getResulSets());
            Assert.assertNull(((MappedStatement) (o_shouldFailForWrongNamespace_mg3484_failAssert50_add3791_add6231__13)).getFetchSize());
            Assert.assertFalse(((MappedStatement) (o_shouldFailForWrongNamespace_mg3484_failAssert50_add3791_add6231__13)).getResultMaps().isEmpty());
            Assert.assertFalse(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3484_failAssert50_add3791_add6231__13)).getConfiguration())).isSafeRowBoundsEnabled());
            Assert.assertFalse(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3484_failAssert50_add3791_add6231__13)).getConfiguration())).isMapUnderscoreToCamelCase());
            Assert.assertNull(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3484_failAssert50_add3791_add6231__13)).getConfiguration())).getEnvironment());
            Assert.assertFalse(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3484_failAssert50_add3791_add6231__13)).getConfiguration())).isLazyLoadingEnabled());
            Assert.assertFalse(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3484_failAssert50_add3791_add6231__13)).getConfiguration())).isUseGeneratedKeys());
            Assert.assertNull(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3484_failAssert50_add3791_add6231__13)).getConfiguration())).getDefaultStatementTimeout());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3484_failAssert50_add3791_add6231__13)).getConfiguration())).isUseColumnLabel());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3484_failAssert50_add3791_add6231__13)).getConfiguration())).getVariables().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3484_failAssert50_add3791_add6231__13)).getConfiguration())).getCacheNames().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3484_failAssert50_add3791_add6231__13)).getConfiguration())).getParameterMaps().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3484_failAssert50_add3791_add6231__13)).getConfiguration())).getResultMaps().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3484_failAssert50_add3791_add6231__13)).getConfiguration())).getIncompleteResultMaps().isEmpty());
            Assert.assertFalse(((TypeAliasRegistry) (((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3484_failAssert50_add3791_add6231__13)).getConfiguration())).getTypeAliasRegistry())).getTypeAliases().isEmpty());
            Assert.assertFalse(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3484_failAssert50_add3791_add6231__13)).getConfiguration())).isAggressiveLazyLoading());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3484_failAssert50_add3791_add6231__13)).getConfiguration())).isSafeResultHandlerEnabled());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3484_failAssert50_add3791_add6231__13)).getConfiguration())).isMultipleResultSetsEnabled());
            Assert.assertFalse(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3484_failAssert50_add3791_add6231__13)).getConfiguration())).isCallSettersOnNulls());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3484_failAssert50_add3791_add6231__13)).getConfiguration())).getInterceptors().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3484_failAssert50_add3791_add6231__13)).getConfiguration())).getCaches().isEmpty());
            Assert.assertNull(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3484_failAssert50_add3791_add6231__13)).getConfiguration())).getDatabaseId());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3484_failAssert50_add3791_add6231__13)).getConfiguration())).getMappedStatementNames().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3484_failAssert50_add3791_add6231__13)).getConfiguration())).getKeyGenerators().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3484_failAssert50_add3791_add6231__13)).getConfiguration())).getParameterMapNames().isEmpty());
            Assert.assertTrue(((ReflectorFactory) (((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3484_failAssert50_add3791_add6231__13)).getConfiguration())).getReflectorFactory())).isClassCacheEnabled());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3484_failAssert50_add3791_add6231__13)).getConfiguration())).getIncompleteCacheRefs().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3484_failAssert50_add3791_add6231__13)).getConfiguration())).getIncompleteMethods().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3484_failAssert50_add3791_add6231__13)).getConfiguration())).getMappedStatements().isEmpty());
            Assert.assertNull(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3484_failAssert50_add3791_add6231__13)).getConfiguration())).getDefaultFetchSize());
            Assert.assertFalse(((TypeHandlerRegistry) (((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3484_failAssert50_add3791_add6231__13)).getConfiguration())).getTypeHandlerRegistry())).getTypeHandlers().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3484_failAssert50_add3791_add6231__13)).getConfiguration())).getIncompleteStatements().isEmpty());
            Assert.assertNull(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3484_failAssert50_add3791_add6231__13)).getConfiguration())).getLogPrefix());
            Assert.assertTrue(((org.apache.ibatis.session.Configuration)((org.apache.ibatis.mapping.MappedStatement)o_shouldFailForWrongNamespace_mg3484_failAssert50_add3791_add6231__13).getConfiguration()).getLazyLoadTriggerMethods().contains("clone"));
            Assert.assertTrue(((org.apache.ibatis.session.Configuration)((org.apache.ibatis.mapping.MappedStatement)o_shouldFailForWrongNamespace_mg3484_failAssert50_add3791_add6231__13).getConfiguration()).getLazyLoadTriggerMethods().contains("toString"));
            Assert.assertTrue(((org.apache.ibatis.session.Configuration)((org.apache.ibatis.mapping.MappedStatement)o_shouldFailForWrongNamespace_mg3484_failAssert50_add3791_add6231__13).getConfiguration()).getLazyLoadTriggerMethods().contains("hashCode"));
            Assert.assertTrue(((org.apache.ibatis.session.Configuration)((org.apache.ibatis.mapping.MappedStatement)o_shouldFailForWrongNamespace_mg3484_failAssert50_add3791_add6231__13).getConfiguration()).getLazyLoadTriggerMethods().contains("equals"));
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3484_failAssert50_add3791_add6231__13)).getConfiguration())).isUseActualParamName());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3484_failAssert50_add3791_add6231__13)).getConfiguration())).getResultMapNames().isEmpty());
            Assert.assertFalse(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3484_failAssert50_add3791_add6231__13)).getConfiguration())).isReturnInstanceForEmptyRow());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3484_failAssert50_add3791_add6231__13)).getConfiguration())).getSqlFragments().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3484_failAssert50_add3791_add6231__13)).getConfiguration())).getKeyGeneratorNames().isEmpty());
            Assert.assertTrue(((MapperRegistry) (((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3484_failAssert50_add3791_add6231__13)).getConfiguration())).getMapperRegistry())).getMappers().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3484_failAssert50_add3791_add6231__13)).getConfiguration())).isCacheEnabled());
            Assert.assertNull(((MappedStatement) (o_shouldFailForWrongNamespace_mg3484_failAssert50_add3791_add6231__13)).getDatabaseId());
            Assert.assertFalse(((MappedStatement) (o_shouldFailForWrongNamespace_mg3484_failAssert50_add3791_add6231__13)).isUseCache());
            Assert.assertNull(((MappedStatement) (o_shouldFailForWrongNamespace_mg3484_failAssert50_add3791_add6231__13)).getTimeout());
            Assert.assertNull(((MappedStatement) (o_shouldFailForWrongNamespace_mg3484_failAssert50_add3791_add6231__13)).getKeyColumns());
            Assert.assertNull(((MappedStatement) (o_shouldFailForWrongNamespace_mg3484_failAssert50_add3791_add6231__13)).getResultSets());
            Assert.assertNull(((MappedStatement) (o_shouldFailForWrongNamespace_mg3484_failAssert50_add3791_add6231__13)).getResultSetType());
            Assert.assertFalse(((MappedStatement) (o_shouldFailForWrongNamespace_mg3484_failAssert50_add3791_add6231__13)).hasNestedResultMaps());
            Assert.assertFalse(((MappedStatement) (o_shouldFailForWrongNamespace_mg3484_failAssert50_add3791_add6231__13)).isFlushCacheRequired());
            Assert.assertTrue(((ParameterMap) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3484_failAssert50_add3791_add6231__13)).getParameterMap())).getParameterMappings().isEmpty());
            Assert.assertEquals("defaultParameterMap", ((ParameterMap) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3484_failAssert50_add3791_add6231__13)).getParameterMap())).getId());
            Assert.assertNull(((MappedStatement) (o_shouldFailForWrongNamespace_mg3484_failAssert50_add3791_add6231__13)).getResource());
            Assert.assertEquals("selectAuthor", ((MappedStatement) (o_shouldFailForWrongNamespace_mg3484_failAssert50_add3791_add6231__13)).getId());
            Assert.assertNull(((MappedStatement) (o_shouldFailForWrongNamespace_mg3484_failAssert50_add3791_add6231__13)).getCache());
            MappedStatement __DSPOT_mappedStatement_1892 = ExecutorTestHelper.createSelectAuthorWithIDof99MappedStatement(new Configuration(null));
            Configuration configuration = new Configuration();
            configuration.addMapper(WrongNamespaceMapper.class);
            configuration.newParameterHandler(__DSPOT_mappedStatement_1892, __DSPOT_parameterObject_1893, __DSPOT_boundSql_1894);
            org.junit.Assert.fail("shouldFailForWrongNamespace_mg3484 should have thrown BuilderException");
        } catch (BuilderException expected) {
        }
    }

    @Test(timeout = 10000)
    public void shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6234() throws Exception {
        try {
            MappedStatement o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6234__3 = ExecutorTestHelper.createInsertAuthorWithIDof99MappedStatement(new Configuration(null));
            Assert.assertFalse(((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6234__3)).isResultOrdered());
            Assert.assertNull(((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6234__3)).getKeyProperties());
            Assert.assertFalse(((Log) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6234__3)).getStatementLog())).isTraceEnabled());
            Assert.assertFalse(((Log) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6234__3)).getStatementLog())).isDebugEnabled());
            Assert.assertNull(((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6234__3)).getResulSets());
            Assert.assertNull(((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6234__3)).getFetchSize());
            Assert.assertTrue(((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6234__3)).getResultMaps().isEmpty());
            Assert.assertFalse(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6234__3)).getConfiguration())).isSafeRowBoundsEnabled());
            Assert.assertFalse(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6234__3)).getConfiguration())).isMapUnderscoreToCamelCase());
            Assert.assertNull(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6234__3)).getConfiguration())).getEnvironment());
            Assert.assertFalse(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6234__3)).getConfiguration())).isLazyLoadingEnabled());
            Assert.assertFalse(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6234__3)).getConfiguration())).isUseGeneratedKeys());
            Assert.assertNull(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6234__3)).getConfiguration())).getDefaultStatementTimeout());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6234__3)).getConfiguration())).isUseColumnLabel());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6234__3)).getConfiguration())).getVariables().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6234__3)).getConfiguration())).getCacheNames().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6234__3)).getConfiguration())).getParameterMaps().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6234__3)).getConfiguration())).getResultMaps().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6234__3)).getConfiguration())).getIncompleteResultMaps().isEmpty());
            Assert.assertFalse(((TypeAliasRegistry) (((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6234__3)).getConfiguration())).getTypeAliasRegistry())).getTypeAliases().isEmpty());
            Assert.assertFalse(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6234__3)).getConfiguration())).isAggressiveLazyLoading());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6234__3)).getConfiguration())).isSafeResultHandlerEnabled());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6234__3)).getConfiguration())).isMultipleResultSetsEnabled());
            Assert.assertFalse(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6234__3)).getConfiguration())).isCallSettersOnNulls());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6234__3)).getConfiguration())).getInterceptors().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6234__3)).getConfiguration())).getCaches().isEmpty());
            Assert.assertNull(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6234__3)).getConfiguration())).getDatabaseId());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6234__3)).getConfiguration())).getMappedStatementNames().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6234__3)).getConfiguration())).getKeyGenerators().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6234__3)).getConfiguration())).getParameterMapNames().isEmpty());
            Assert.assertTrue(((ReflectorFactory) (((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6234__3)).getConfiguration())).getReflectorFactory())).isClassCacheEnabled());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6234__3)).getConfiguration())).getIncompleteCacheRefs().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6234__3)).getConfiguration())).getIncompleteMethods().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6234__3)).getConfiguration())).getMappedStatements().isEmpty());
            Assert.assertNull(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6234__3)).getConfiguration())).getDefaultFetchSize());
            Assert.assertFalse(((TypeHandlerRegistry) (((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6234__3)).getConfiguration())).getTypeHandlerRegistry())).getTypeHandlers().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6234__3)).getConfiguration())).getIncompleteStatements().isEmpty());
            Assert.assertNull(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6234__3)).getConfiguration())).getLogPrefix());
            Assert.assertTrue(((org.apache.ibatis.session.Configuration)((org.apache.ibatis.mapping.MappedStatement)o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6234__3).getConfiguration()).getLazyLoadTriggerMethods().contains("clone"));
            Assert.assertTrue(((org.apache.ibatis.session.Configuration)((org.apache.ibatis.mapping.MappedStatement)o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6234__3).getConfiguration()).getLazyLoadTriggerMethods().contains("toString"));
            Assert.assertTrue(((org.apache.ibatis.session.Configuration)((org.apache.ibatis.mapping.MappedStatement)o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6234__3).getConfiguration()).getLazyLoadTriggerMethods().contains("hashCode"));
            Assert.assertTrue(((org.apache.ibatis.session.Configuration)((org.apache.ibatis.mapping.MappedStatement)o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6234__3).getConfiguration()).getLazyLoadTriggerMethods().contains("equals"));
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6234__3)).getConfiguration())).isUseActualParamName());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6234__3)).getConfiguration())).getResultMapNames().isEmpty());
            Assert.assertFalse(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6234__3)).getConfiguration())).isReturnInstanceForEmptyRow());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6234__3)).getConfiguration())).getSqlFragments().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6234__3)).getConfiguration())).getKeyGeneratorNames().isEmpty());
            Assert.assertTrue(((MapperRegistry) (((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6234__3)).getConfiguration())).getMapperRegistry())).getMappers().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6234__3)).getConfiguration())).isCacheEnabled());
            Assert.assertNull(((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6234__3)).getDatabaseId());
            Assert.assertFalse(((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6234__3)).isUseCache());
            Assert.assertNull(((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6234__3)).getTimeout());
            Assert.assertNull(((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6234__3)).getKeyColumns());
            Assert.assertNull(((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6234__3)).getResultSets());
            Assert.assertNull(((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6234__3)).getResultSetType());
            Assert.assertFalse(((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6234__3)).hasNestedResultMaps());
            Assert.assertFalse(((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6234__3)).isFlushCacheRequired());
            Assert.assertTrue(((ParameterMap) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6234__3)).getParameterMap())).getParameterMappings().isEmpty());
            Assert.assertEquals("defaultParameterMap", ((ParameterMap) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6234__3)).getParameterMap())).getId());
            Assert.assertNull(((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6234__3)).getResource());
            Assert.assertEquals("insertAuthor", ((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6234__3)).getId());
            Assert.assertNull(((Cache) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6234__3)).getCache())).getReadWriteLock());
            Assert.assertEquals("author_cache", ((Cache) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6234__3)).getCache())).getId());
            Assert.assertEquals(0, ((int) (((Cache) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6234__3)).getCache())).getSize())));
            MappedStatement o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796__3 = ExecutorTestHelper.createInsertAuthorWithIDof99MappedStatement(new Configuration(null));
            MappedStatement __DSPOT_ms_1874 = ExecutorTestHelper.createInsertAuthorWithIDof99MappedStatement(new Configuration(null));
            Configuration configuration = new Configuration();
            configuration.addMapper(WrongNamespaceMapper.class);
            configuration.addMappedStatement(__DSPOT_ms_1874);
            org.junit.Assert.fail("shouldFailForWrongNamespace_mg3468 should have thrown BuilderException");
        } catch (BuilderException expected) {
        }
    }

    @Test(timeout = 10000)
    public void shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6370() throws Exception {
        try {
            MappedStatement o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796__3 = ExecutorTestHelper.createInsertAuthorWithIDof99MappedStatement(new Configuration(null));
            MappedStatement o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6370__7 = ExecutorTestHelper.createInsertAuthorWithIDof99MappedStatement(new Configuration(null));
            Assert.assertFalse(((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6370__7)).isResultOrdered());
            Assert.assertNull(((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6370__7)).getKeyProperties());
            Assert.assertFalse(((Log) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6370__7)).getStatementLog())).isTraceEnabled());
            Assert.assertFalse(((Log) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6370__7)).getStatementLog())).isDebugEnabled());
            Assert.assertNull(((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6370__7)).getResulSets());
            Assert.assertNull(((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6370__7)).getFetchSize());
            Assert.assertTrue(((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6370__7)).getResultMaps().isEmpty());
            Assert.assertFalse(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6370__7)).getConfiguration())).isSafeRowBoundsEnabled());
            Assert.assertFalse(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6370__7)).getConfiguration())).isMapUnderscoreToCamelCase());
            Assert.assertNull(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6370__7)).getConfiguration())).getEnvironment());
            Assert.assertFalse(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6370__7)).getConfiguration())).isLazyLoadingEnabled());
            Assert.assertFalse(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6370__7)).getConfiguration())).isUseGeneratedKeys());
            Assert.assertNull(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6370__7)).getConfiguration())).getDefaultStatementTimeout());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6370__7)).getConfiguration())).isUseColumnLabel());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6370__7)).getConfiguration())).getVariables().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6370__7)).getConfiguration())).getCacheNames().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6370__7)).getConfiguration())).getParameterMaps().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6370__7)).getConfiguration())).getResultMaps().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6370__7)).getConfiguration())).getIncompleteResultMaps().isEmpty());
            Assert.assertFalse(((TypeAliasRegistry) (((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6370__7)).getConfiguration())).getTypeAliasRegistry())).getTypeAliases().isEmpty());
            Assert.assertFalse(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6370__7)).getConfiguration())).isAggressiveLazyLoading());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6370__7)).getConfiguration())).isSafeResultHandlerEnabled());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6370__7)).getConfiguration())).isMultipleResultSetsEnabled());
            Assert.assertFalse(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6370__7)).getConfiguration())).isCallSettersOnNulls());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6370__7)).getConfiguration())).getInterceptors().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6370__7)).getConfiguration())).getCaches().isEmpty());
            Assert.assertNull(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6370__7)).getConfiguration())).getDatabaseId());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6370__7)).getConfiguration())).getMappedStatementNames().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6370__7)).getConfiguration())).getKeyGenerators().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6370__7)).getConfiguration())).getParameterMapNames().isEmpty());
            Assert.assertTrue(((ReflectorFactory) (((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6370__7)).getConfiguration())).getReflectorFactory())).isClassCacheEnabled());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6370__7)).getConfiguration())).getIncompleteCacheRefs().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6370__7)).getConfiguration())).getIncompleteMethods().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6370__7)).getConfiguration())).getMappedStatements().isEmpty());
            Assert.assertNull(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6370__7)).getConfiguration())).getDefaultFetchSize());
            Assert.assertFalse(((TypeHandlerRegistry) (((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6370__7)).getConfiguration())).getTypeHandlerRegistry())).getTypeHandlers().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6370__7)).getConfiguration())).getIncompleteStatements().isEmpty());
            Assert.assertNull(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6370__7)).getConfiguration())).getLogPrefix());
            Assert.assertTrue(((org.apache.ibatis.session.Configuration)((org.apache.ibatis.mapping.MappedStatement)o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6370__7).getConfiguration()).getLazyLoadTriggerMethods().contains("clone"));
            Assert.assertTrue(((org.apache.ibatis.session.Configuration)((org.apache.ibatis.mapping.MappedStatement)o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6370__7).getConfiguration()).getLazyLoadTriggerMethods().contains("toString"));
            Assert.assertTrue(((org.apache.ibatis.session.Configuration)((org.apache.ibatis.mapping.MappedStatement)o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6370__7).getConfiguration()).getLazyLoadTriggerMethods().contains("hashCode"));
            Assert.assertTrue(((org.apache.ibatis.session.Configuration)((org.apache.ibatis.mapping.MappedStatement)o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6370__7).getConfiguration()).getLazyLoadTriggerMethods().contains("equals"));
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6370__7)).getConfiguration())).isUseActualParamName());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6370__7)).getConfiguration())).getResultMapNames().isEmpty());
            Assert.assertFalse(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6370__7)).getConfiguration())).isReturnInstanceForEmptyRow());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6370__7)).getConfiguration())).getSqlFragments().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6370__7)).getConfiguration())).getKeyGeneratorNames().isEmpty());
            Assert.assertTrue(((MapperRegistry) (((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6370__7)).getConfiguration())).getMapperRegistry())).getMappers().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6370__7)).getConfiguration())).isCacheEnabled());
            Assert.assertNull(((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6370__7)).getDatabaseId());
            Assert.assertFalse(((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6370__7)).isUseCache());
            Assert.assertNull(((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6370__7)).getTimeout());
            Assert.assertNull(((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6370__7)).getKeyColumns());
            Assert.assertNull(((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6370__7)).getResultSets());
            Assert.assertNull(((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6370__7)).getResultSetType());
            Assert.assertFalse(((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6370__7)).hasNestedResultMaps());
            Assert.assertFalse(((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6370__7)).isFlushCacheRequired());
            Assert.assertTrue(((ParameterMap) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6370__7)).getParameterMap())).getParameterMappings().isEmpty());
            Assert.assertEquals("defaultParameterMap", ((ParameterMap) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6370__7)).getParameterMap())).getId());
            Assert.assertNull(((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6370__7)).getResource());
            Assert.assertEquals("insertAuthor", ((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6370__7)).getId());
            Assert.assertNull(((Cache) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6370__7)).getCache())).getReadWriteLock());
            Assert.assertEquals("author_cache", ((Cache) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6370__7)).getCache())).getId());
            Assert.assertEquals(0, ((int) (((Cache) (((MappedStatement) (o_shouldFailForWrongNamespace_mg3468_failAssert51_add3796_add6370__7)).getCache())).getSize())));
            MappedStatement __DSPOT_ms_1874 = ExecutorTestHelper.createInsertAuthorWithIDof99MappedStatement(new Configuration(null));
            Configuration configuration = new Configuration();
            configuration.addMapper(WrongNamespaceMapper.class);
            configuration.addMappedStatement(__DSPOT_ms_1874);
            org.junit.Assert.fail("shouldFailForWrongNamespace_mg3468 should have thrown BuilderException");
        } catch (BuilderException expected) {
        }
    }

    @Test(timeout = 10000)
    public void shouldFailForMissingNamespace_failAssert0() throws Exception {
        try {
            Configuration configuration = new Configuration();
            configuration.addMapper(MissingNamespaceMapper.class);
            org.junit.Assert.fail("shouldFailForMissingNamespace should have thrown BuilderException");
        } catch (BuilderException expected) {
            Assert.assertEquals("Error parsing Mapper XML. The XML location is \'org/apache/ibatis/binding/MissingNamespaceMapper.xml\'. Cause: org.apache.ibatis.builder.BuilderException: Mapper\'s namespace cannot be empty", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldFailForMissingNamespace_mg9_failAssert31() throws Exception {
        try {
            String __DSPOT_id_7 = "%h1,xavU[1Rvnj|}8wu]";
            Configuration configuration = new Configuration();
            configuration.addMapper(MissingNamespaceMapper.class);
            configuration.getKeyGenerator(__DSPOT_id_7);
            org.junit.Assert.fail("shouldFailForMissingNamespace_mg9 should have thrown BuilderException");
        } catch (BuilderException expected) {
            Assert.assertEquals("Error parsing Mapper XML. The XML location is \'org/apache/ibatis/binding/MissingNamespaceMapper.xml\'. Cause: org.apache.ibatis.builder.BuilderException: Mapper\'s namespace cannot be empty", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldFailForMissingNamespace_mg22_failAssert32() throws Exception {
        try {
            BoundSql __DSPOT_boundSql_24 = new BoundSql(new Configuration(null), "JV)d4}^w[&oDAIOw? O!", Collections.<ParameterMapping>emptyList(), new Object());
            Object __DSPOT_parameterObject_23 = new Object();
            MappedStatement __DSPOT_mappedStatement_22 = ExecutorTestHelper.createSelectAuthorWithIDof99MappedStatement(new Configuration());
            Configuration configuration = new Configuration();
            configuration.addMapper(MissingNamespaceMapper.class);
            configuration.newParameterHandler(__DSPOT_mappedStatement_22, __DSPOT_parameterObject_23, __DSPOT_boundSql_24);
            org.junit.Assert.fail("shouldFailForMissingNamespace_mg22 should have thrown BuilderException");
        } catch (BuilderException expected) {
            Assert.assertEquals("Error parsing Mapper XML. The XML location is \'org/apache/ibatis/binding/MissingNamespaceMapper.xml\'. Cause: org.apache.ibatis.builder.BuilderException: Mapper\'s namespace cannot be empty", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldFailForMissingNamespace_mg6_failAssert39() throws Exception {
        try {
            MappedStatement __DSPOT_ms_4 = ExecutorTestHelper.createSelectAuthorWithIDof99MappedStatement(new Configuration(null));
            Configuration configuration = new Configuration();
            configuration.addMapper(MissingNamespaceMapper.class);
            configuration.addMappedStatement(__DSPOT_ms_4);
            org.junit.Assert.fail("shouldFailForMissingNamespace_mg6 should have thrown BuilderException");
        } catch (BuilderException expected) {
            Assert.assertEquals("Error parsing Mapper XML. The XML location is \'org/apache/ibatis/binding/MissingNamespaceMapper.xml\'. Cause: org.apache.ibatis.builder.BuilderException: Mapper\'s namespace cannot be empty", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldFailForMissingNamespace_mg6_failAssert39_add426() throws Exception {
        try {
            MappedStatement o_shouldFailForMissingNamespace_mg6_failAssert39_add426__3 = ExecutorTestHelper.createSelectAuthorWithIDof99MappedStatement(new Configuration(null));
            Assert.assertNull(((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426__3)).getDatabaseId());
            Assert.assertFalse(((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426__3)).getResultMaps().isEmpty());
            Assert.assertFalse(((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426__3)).isUseCache());
            Assert.assertNull(((Configuration) (((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426__3)).getConfiguration())).getLogPrefix());
            Assert.assertFalse(((Configuration) (((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426__3)).getConfiguration())).isCallSettersOnNulls());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426__3)).getConfiguration())).isUseActualParamName());
            Assert.assertFalse(((Configuration) (((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426__3)).getConfiguration())).isReturnInstanceForEmptyRow());
            Assert.assertNull(((Configuration) (((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426__3)).getConfiguration())).getDatabaseId());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426__3)).getConfiguration())).isSafeResultHandlerEnabled());
            Assert.assertFalse(((Configuration) (((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426__3)).getConfiguration())).isSafeRowBoundsEnabled());
            Assert.assertFalse(((Configuration) (((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426__3)).getConfiguration())).isMapUnderscoreToCamelCase());
            Assert.assertNull(((Configuration) (((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426__3)).getConfiguration())).getEnvironment());
            Assert.assertFalse(((Configuration) (((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426__3)).getConfiguration())).isLazyLoadingEnabled());
            Assert.assertFalse(((Configuration) (((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426__3)).getConfiguration())).isAggressiveLazyLoading());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426__3)).getConfiguration())).isMultipleResultSetsEnabled());
            Assert.assertTrue(((org.apache.ibatis.session.Configuration)((org.apache.ibatis.mapping.MappedStatement)o_shouldFailForMissingNamespace_mg6_failAssert39_add426__3).getConfiguration()).getLazyLoadTriggerMethods().contains("clone"));
            Assert.assertTrue(((org.apache.ibatis.session.Configuration)((org.apache.ibatis.mapping.MappedStatement)o_shouldFailForMissingNamespace_mg6_failAssert39_add426__3).getConfiguration()).getLazyLoadTriggerMethods().contains("toString"));
            Assert.assertTrue(((org.apache.ibatis.session.Configuration)((org.apache.ibatis.mapping.MappedStatement)o_shouldFailForMissingNamespace_mg6_failAssert39_add426__3).getConfiguration()).getLazyLoadTriggerMethods().contains("hashCode"));
            Assert.assertTrue(((org.apache.ibatis.session.Configuration)((org.apache.ibatis.mapping.MappedStatement)o_shouldFailForMissingNamespace_mg6_failAssert39_add426__3).getConfiguration()).getLazyLoadTriggerMethods().contains("equals"));
            Assert.assertFalse(((Configuration) (((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426__3)).getConfiguration())).isUseGeneratedKeys());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426__3)).getConfiguration())).isCacheEnabled());
            Assert.assertNull(((Configuration) (((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426__3)).getConfiguration())).getDefaultStatementTimeout());
            Assert.assertNull(((Configuration) (((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426__3)).getConfiguration())).getDefaultFetchSize());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426__3)).getConfiguration())).isUseColumnLabel());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426__3)).getConfiguration())).getVariables().isEmpty());
            Assert.assertFalse(((TypeHandlerRegistry) (((Configuration) (((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426__3)).getConfiguration())).getTypeHandlerRegistry())).getTypeHandlers().isEmpty());
            Assert.assertFalse(((TypeAliasRegistry) (((Configuration) (((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426__3)).getConfiguration())).getTypeAliasRegistry())).getTypeAliases().isEmpty());
            Assert.assertTrue(((MapperRegistry) (((Configuration) (((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426__3)).getConfiguration())).getMapperRegistry())).getMappers().isEmpty());
            Assert.assertTrue(((ReflectorFactory) (((Configuration) (((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426__3)).getConfiguration())).getReflectorFactory())).isClassCacheEnabled());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426__3)).getConfiguration())).getInterceptors().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426__3)).getConfiguration())).getKeyGeneratorNames().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426__3)).getConfiguration())).getKeyGenerators().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426__3)).getConfiguration())).getCacheNames().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426__3)).getConfiguration())).getCaches().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426__3)).getConfiguration())).getResultMapNames().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426__3)).getConfiguration())).getResultMaps().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426__3)).getConfiguration())).getParameterMapNames().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426__3)).getConfiguration())).getParameterMaps().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426__3)).getConfiguration())).getMappedStatementNames().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426__3)).getConfiguration())).getMappedStatements().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426__3)).getConfiguration())).getIncompleteStatements().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426__3)).getConfiguration())).getIncompleteCacheRefs().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426__3)).getConfiguration())).getIncompleteResultMaps().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426__3)).getConfiguration())).getIncompleteMethods().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426__3)).getConfiguration())).getSqlFragments().isEmpty());
            Assert.assertNull(((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426__3)).getResultSetType());
            Assert.assertFalse(((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426__3)).isResultOrdered());
            Assert.assertNull(((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426__3)).getKeyProperties());
            Assert.assertFalse(((Log) (((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426__3)).getStatementLog())).isTraceEnabled());
            Assert.assertFalse(((Log) (((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426__3)).getStatementLog())).isDebugEnabled());
            Assert.assertNull(((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426__3)).getFetchSize());
            Assert.assertTrue(((ParameterMap) (((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426__3)).getParameterMap())).getParameterMappings().isEmpty());
            Assert.assertEquals("defaultParameterMap", ((ParameterMap) (((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426__3)).getParameterMap())).getId());
            Assert.assertNull(((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426__3)).getResultSets());
            Assert.assertNull(((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426__3)).getTimeout());
            Assert.assertFalse(((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426__3)).isFlushCacheRequired());
            Assert.assertFalse(((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426__3)).hasNestedResultMaps());
            Assert.assertNull(((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426__3)).getKeyColumns());
            Assert.assertNull(((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426__3)).getResulSets());
            Assert.assertNull(((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426__3)).getResource());
            Assert.assertEquals("selectAuthor", ((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426__3)).getId());
            Assert.assertNull(((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426__3)).getCache());
            MappedStatement __DSPOT_ms_4 = ExecutorTestHelper.createSelectAuthorWithIDof99MappedStatement(new Configuration(null));
            Configuration configuration = new Configuration();
            configuration.addMapper(MissingNamespaceMapper.class);
            configuration.addMappedStatement(__DSPOT_ms_4);
            org.junit.Assert.fail("shouldFailForMissingNamespace_mg6 should have thrown BuilderException");
        } catch (BuilderException expected) {
        }
    }

    @Test(timeout = 10000)
    public void shouldFailForMissingNamespace_mg6_failAssert39_add426_add2902() throws Exception {
        try {
            MappedStatement o_shouldFailForMissingNamespace_mg6_failAssert39_add426__3 = ExecutorTestHelper.createSelectAuthorWithIDof99MappedStatement(new Configuration(null));
            MappedStatement o_shouldFailForMissingNamespace_mg6_failAssert39_add426_add2902__7 = ExecutorTestHelper.createSelectAuthorWithIDof99MappedStatement(new Configuration(null));
            Assert.assertFalse(((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426_add2902__7)).isFlushCacheRequired());
            Assert.assertNull(((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426_add2902__7)).getResulSets());
            Assert.assertFalse(((Configuration) (((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426_add2902__7)).getConfiguration())).isAggressiveLazyLoading());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426_add2902__7)).getConfiguration())).isCacheEnabled());
            Assert.assertTrue(((ReflectorFactory) (((Configuration) (((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426_add2902__7)).getConfiguration())).getReflectorFactory())).isClassCacheEnabled());
            Assert.assertFalse(((Configuration) (((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426_add2902__7)).getConfiguration())).isSafeRowBoundsEnabled());
            Assert.assertFalse(((Configuration) (((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426_add2902__7)).getConfiguration())).isCallSettersOnNulls());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426_add2902__7)).getConfiguration())).getIncompleteCacheRefs().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426_add2902__7)).getConfiguration())).getInterceptors().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426_add2902__7)).getConfiguration())).getIncompleteResultMaps().isEmpty());
            Assert.assertFalse(((TypeHandlerRegistry) (((Configuration) (((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426_add2902__7)).getConfiguration())).getTypeHandlerRegistry())).getTypeHandlers().isEmpty());
            Assert.assertTrue(((org.apache.ibatis.session.Configuration)((org.apache.ibatis.mapping.MappedStatement)o_shouldFailForMissingNamespace_mg6_failAssert39_add426_add2902__7).getConfiguration()).getLazyLoadTriggerMethods().contains("clone"));
            Assert.assertTrue(((org.apache.ibatis.session.Configuration)((org.apache.ibatis.mapping.MappedStatement)o_shouldFailForMissingNamespace_mg6_failAssert39_add426_add2902__7).getConfiguration()).getLazyLoadTriggerMethods().contains("toString"));
            Assert.assertTrue(((org.apache.ibatis.session.Configuration)((org.apache.ibatis.mapping.MappedStatement)o_shouldFailForMissingNamespace_mg6_failAssert39_add426_add2902__7).getConfiguration()).getLazyLoadTriggerMethods().contains("hashCode"));
            Assert.assertTrue(((org.apache.ibatis.session.Configuration)((org.apache.ibatis.mapping.MappedStatement)o_shouldFailForMissingNamespace_mg6_failAssert39_add426_add2902__7).getConfiguration()).getLazyLoadTriggerMethods().contains("equals"));
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426_add2902__7)).getConfiguration())).getSqlFragments().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426_add2902__7)).getConfiguration())).getParameterMaps().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426_add2902__7)).getConfiguration())).getIncompleteStatements().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426_add2902__7)).getConfiguration())).getVariables().isEmpty());
            Assert.assertNull(((Configuration) (((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426_add2902__7)).getConfiguration())).getDefaultStatementTimeout());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426_add2902__7)).getConfiguration())).isMultipleResultSetsEnabled());
            Assert.assertFalse(((Configuration) (((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426_add2902__7)).getConfiguration())).isUseGeneratedKeys());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426_add2902__7)).getConfiguration())).isUseActualParamName());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426_add2902__7)).getConfiguration())).getCacheNames().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426_add2902__7)).getConfiguration())).getMappedStatementNames().isEmpty());
            Assert.assertFalse(((Configuration) (((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426_add2902__7)).getConfiguration())).isLazyLoadingEnabled());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426_add2902__7)).getConfiguration())).getCaches().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426_add2902__7)).getConfiguration())).getKeyGenerators().isEmpty());
            Assert.assertNull(((Configuration) (((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426_add2902__7)).getConfiguration())).getLogPrefix());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426_add2902__7)).getConfiguration())).getResultMaps().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426_add2902__7)).getConfiguration())).getResultMapNames().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426_add2902__7)).getConfiguration())).getMappedStatements().isEmpty());
            Assert.assertFalse(((Configuration) (((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426_add2902__7)).getConfiguration())).isMapUnderscoreToCamelCase());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426_add2902__7)).getConfiguration())).isUseColumnLabel());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426_add2902__7)).getConfiguration())).getKeyGeneratorNames().isEmpty());
            Assert.assertFalse(((TypeAliasRegistry) (((Configuration) (((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426_add2902__7)).getConfiguration())).getTypeAliasRegistry())).getTypeAliases().isEmpty());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426_add2902__7)).getConfiguration())).isSafeResultHandlerEnabled());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426_add2902__7)).getConfiguration())).getIncompleteMethods().isEmpty());
            Assert.assertNull(((Configuration) (((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426_add2902__7)).getConfiguration())).getDefaultFetchSize());
            Assert.assertTrue(((Configuration) (((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426_add2902__7)).getConfiguration())).getParameterMapNames().isEmpty());
            Assert.assertFalse(((Configuration) (((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426_add2902__7)).getConfiguration())).isReturnInstanceForEmptyRow());
            Assert.assertTrue(((MapperRegistry) (((Configuration) (((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426_add2902__7)).getConfiguration())).getMapperRegistry())).getMappers().isEmpty());
            Assert.assertNull(((Configuration) (((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426_add2902__7)).getConfiguration())).getDatabaseId());
            Assert.assertNull(((Configuration) (((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426_add2902__7)).getConfiguration())).getEnvironment());
            Assert.assertTrue(((ParameterMap) (((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426_add2902__7)).getParameterMap())).getParameterMappings().isEmpty());
            Assert.assertEquals("defaultParameterMap", ((ParameterMap) (((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426_add2902__7)).getParameterMap())).getId());
            Assert.assertFalse(((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426_add2902__7)).isUseCache());
            Assert.assertNull(((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426_add2902__7)).getResultSetType());
            Assert.assertFalse(((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426_add2902__7)).hasNestedResultMaps());
            Assert.assertFalse(((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426_add2902__7)).getResultMaps().isEmpty());
            Assert.assertNull(((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426_add2902__7)).getResultSets());
            Assert.assertNull(((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426_add2902__7)).getTimeout());
            Assert.assertNull(((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426_add2902__7)).getKeyColumns());
            Assert.assertNull(((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426_add2902__7)).getKeyProperties());
            Assert.assertFalse(((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426_add2902__7)).isResultOrdered());
            Assert.assertFalse(((Log) (((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426_add2902__7)).getStatementLog())).isTraceEnabled());
            Assert.assertFalse(((Log) (((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426_add2902__7)).getStatementLog())).isDebugEnabled());
            Assert.assertNull(((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426_add2902__7)).getDatabaseId());
            Assert.assertNull(((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426_add2902__7)).getFetchSize());
            Assert.assertNull(((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426_add2902__7)).getResource());
            Assert.assertEquals("selectAuthor", ((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426_add2902__7)).getId());
            Assert.assertNull(((MappedStatement) (o_shouldFailForMissingNamespace_mg6_failAssert39_add426_add2902__7)).getCache());
            MappedStatement __DSPOT_ms_4 = ExecutorTestHelper.createSelectAuthorWithIDof99MappedStatement(new Configuration(null));
            Configuration configuration = new Configuration();
            configuration.addMapper(MissingNamespaceMapper.class);
            configuration.addMappedStatement(__DSPOT_ms_4);
            org.junit.Assert.fail("shouldFailForMissingNamespace_mg6 should have thrown BuilderException");
        } catch (BuilderException expected) {
        }
    }

    @Test(timeout = 10000)
    public void shouldFailForMissingNamespace_mg6_failAssert39_add426_add2801() throws Exception {
        try {
            MappedStatement o_shouldFailForMissingNamespace_mg6_failAssert39_add426__3 = ExecutorTestHelper.createSelectAuthorWithIDof99MappedStatement(new Configuration(null));
            boolean o_shouldFailForMissingNamespace_mg6_failAssert39_add426_add2801__7 = ((org.apache.ibatis.session.Configuration)((org.apache.ibatis.mapping.MappedStatement)o_shouldFailForMissingNamespace_mg6_failAssert39_add426__3).getConfiguration()).getLazyLoadTriggerMethods().contains("toString");
            Assert.assertTrue(o_shouldFailForMissingNamespace_mg6_failAssert39_add426_add2801__7);
            MappedStatement __DSPOT_ms_4 = ExecutorTestHelper.createSelectAuthorWithIDof99MappedStatement(new Configuration(null));
            Configuration configuration = new Configuration();
            configuration.addMapper(MissingNamespaceMapper.class);
            configuration.addMappedStatement(__DSPOT_ms_4);
            org.junit.Assert.fail("shouldFailForMissingNamespace_mg6 should have thrown BuilderException");
        } catch (BuilderException expected) {
        }
    }
}

