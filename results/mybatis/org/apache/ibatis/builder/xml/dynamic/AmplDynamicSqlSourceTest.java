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


package org.apache.ibatis.builder.xml.dynamic;


public class AmplDynamicSqlSourceTest extends org.apache.ibatis.BaseDataTest {
    @org.junit.Test
    public void shouldDemonstrateSimpleExpectedTextWithNoLoopsOrConditionals() throws java.lang.Exception {
        final java.lang.String expected = "SELECT * FROM BLOG";
        final org.apache.ibatis.scripting.xmltags.MixedSqlNode sqlNode = mixedContents(new org.apache.ibatis.scripting.xmltags.TextSqlNode(expected));
        org.apache.ibatis.scripting.xmltags.DynamicSqlSource source = createDynamicSqlSource(sqlNode);
        org.apache.ibatis.mapping.BoundSql boundSql = source.getBoundSql(null);
        org.junit.Assert.assertEquals(expected, boundSql.getSql());
    }

    @org.junit.Test
    public void shouldDemonstrateMultipartExpectedTextWithNoLoopsOrConditionals() throws java.lang.Exception {
        final java.lang.String expected = "SELECT * FROM BLOG WHERE ID = ?";
        org.apache.ibatis.scripting.xmltags.DynamicSqlSource source = createDynamicSqlSource(new org.apache.ibatis.scripting.xmltags.TextSqlNode("SELECT * FROM BLOG"), new org.apache.ibatis.scripting.xmltags.TextSqlNode("WHERE ID = ?"));
        org.apache.ibatis.mapping.BoundSql boundSql = source.getBoundSql(null);
        org.junit.Assert.assertEquals(expected, boundSql.getSql());
    }

    @org.junit.Test
    public void shouldConditionallyIncludeWhere() throws java.lang.Exception {
        final java.lang.String expected = "SELECT * FROM BLOG WHERE ID = ?";
        org.apache.ibatis.scripting.xmltags.DynamicSqlSource source = createDynamicSqlSource(new org.apache.ibatis.scripting.xmltags.TextSqlNode("SELECT * FROM BLOG"), new org.apache.ibatis.scripting.xmltags.IfSqlNode(mixedContents(new org.apache.ibatis.scripting.xmltags.TextSqlNode("WHERE ID = ?")), "true"));
        org.apache.ibatis.mapping.BoundSql boundSql = source.getBoundSql(null);
        org.junit.Assert.assertEquals(expected, boundSql.getSql());
    }

    @org.junit.Test
    public void shouldConditionallyExcludeWhere() throws java.lang.Exception {
        final java.lang.String expected = "SELECT * FROM BLOG";
        org.apache.ibatis.scripting.xmltags.DynamicSqlSource source = createDynamicSqlSource(new org.apache.ibatis.scripting.xmltags.TextSqlNode("SELECT * FROM BLOG"), new org.apache.ibatis.scripting.xmltags.IfSqlNode(mixedContents(new org.apache.ibatis.scripting.xmltags.TextSqlNode("WHERE ID = ?")), "false"));
        org.apache.ibatis.mapping.BoundSql boundSql = source.getBoundSql(null);
        org.junit.Assert.assertEquals(expected, boundSql.getSql());
    }

    @org.junit.Test
    public void shouldConditionallyDefault() throws java.lang.Exception {
        final java.lang.String expected = "SELECT * FROM BLOG WHERE CATEGORY = 'DEFAULT'";
        org.apache.ibatis.scripting.xmltags.DynamicSqlSource source = createDynamicSqlSource(new org.apache.ibatis.scripting.xmltags.TextSqlNode("SELECT * FROM BLOG"), new org.apache.ibatis.scripting.xmltags.ChooseSqlNode(new java.util.ArrayList<org.apache.ibatis.scripting.xmltags.SqlNode>() {
            {
                add(new org.apache.ibatis.scripting.xmltags.IfSqlNode(mixedContents(new org.apache.ibatis.scripting.xmltags.TextSqlNode("WHERE CATEGORY = ?")), "false"));
                add(new org.apache.ibatis.scripting.xmltags.IfSqlNode(mixedContents(new org.apache.ibatis.scripting.xmltags.TextSqlNode("WHERE CATEGORY = 'NONE'")), "false"));
            }
        }, mixedContents(new org.apache.ibatis.scripting.xmltags.TextSqlNode("WHERE CATEGORY = 'DEFAULT'"))));
        org.apache.ibatis.mapping.BoundSql boundSql = source.getBoundSql(null);
        org.junit.Assert.assertEquals(expected, boundSql.getSql());
    }

    @org.junit.Test
    public void shouldConditionallyChooseFirst() throws java.lang.Exception {
        final java.lang.String expected = "SELECT * FROM BLOG WHERE CATEGORY = ?";
        org.apache.ibatis.scripting.xmltags.DynamicSqlSource source = createDynamicSqlSource(new org.apache.ibatis.scripting.xmltags.TextSqlNode("SELECT * FROM BLOG"), new org.apache.ibatis.scripting.xmltags.ChooseSqlNode(new java.util.ArrayList<org.apache.ibatis.scripting.xmltags.SqlNode>() {
            {
                add(new org.apache.ibatis.scripting.xmltags.IfSqlNode(mixedContents(new org.apache.ibatis.scripting.xmltags.TextSqlNode("WHERE CATEGORY = ?")), "true"));
                add(new org.apache.ibatis.scripting.xmltags.IfSqlNode(mixedContents(new org.apache.ibatis.scripting.xmltags.TextSqlNode("WHERE CATEGORY = 'NONE'")), "false"));
            }
        }, mixedContents(new org.apache.ibatis.scripting.xmltags.TextSqlNode("WHERE CATEGORY = 'DEFAULT'"))));
        org.apache.ibatis.mapping.BoundSql boundSql = source.getBoundSql(null);
        org.junit.Assert.assertEquals(expected, boundSql.getSql());
    }

    @org.junit.Test
    public void shouldConditionallyChooseSecond() throws java.lang.Exception {
        final java.lang.String expected = "SELECT * FROM BLOG WHERE CATEGORY = 'NONE'";
        org.apache.ibatis.scripting.xmltags.DynamicSqlSource source = createDynamicSqlSource(new org.apache.ibatis.scripting.xmltags.TextSqlNode("SELECT * FROM BLOG"), new org.apache.ibatis.scripting.xmltags.ChooseSqlNode(new java.util.ArrayList<org.apache.ibatis.scripting.xmltags.SqlNode>() {
            {
                add(new org.apache.ibatis.scripting.xmltags.IfSqlNode(mixedContents(new org.apache.ibatis.scripting.xmltags.TextSqlNode("WHERE CATEGORY = ?")), "false"));
                add(new org.apache.ibatis.scripting.xmltags.IfSqlNode(mixedContents(new org.apache.ibatis.scripting.xmltags.TextSqlNode("WHERE CATEGORY = 'NONE'")), "true"));
            }
        }, mixedContents(new org.apache.ibatis.scripting.xmltags.TextSqlNode("WHERE CATEGORY = 'DEFAULT'"))));
        org.apache.ibatis.mapping.BoundSql boundSql = source.getBoundSql(null);
        org.junit.Assert.assertEquals(expected, boundSql.getSql());
    }

    @org.junit.Test
    public void shouldTrimWHEREInsteadOfANDForFirstCondition() throws java.lang.Exception {
        final java.lang.String expected = "SELECT * FROM BLOG WHERE  ID = ?";
        org.apache.ibatis.scripting.xmltags.DynamicSqlSource source = createDynamicSqlSource(new org.apache.ibatis.scripting.xmltags.TextSqlNode("SELECT * FROM BLOG"), new org.apache.ibatis.scripting.xmltags.WhereSqlNode(new org.apache.ibatis.session.Configuration(), mixedContents(new org.apache.ibatis.scripting.xmltags.IfSqlNode(mixedContents(new org.apache.ibatis.scripting.xmltags.TextSqlNode("   and ID = ?  ")), "true"), new org.apache.ibatis.scripting.xmltags.IfSqlNode(mixedContents(new org.apache.ibatis.scripting.xmltags.TextSqlNode("   or NAME = ?  ")), "false"))));
        org.apache.ibatis.mapping.BoundSql boundSql = source.getBoundSql(null);
        org.junit.Assert.assertEquals(expected, boundSql.getSql());
    }

    @org.junit.Test
    public void shouldTrimWHEREANDWithLFForFirstCondition() throws java.lang.Exception {
        final java.lang.String expected = "SELECT * FROM BLOG WHERE \n ID = ?";
        org.apache.ibatis.scripting.xmltags.DynamicSqlSource source = createDynamicSqlSource(new org.apache.ibatis.scripting.xmltags.TextSqlNode("SELECT * FROM BLOG"), new org.apache.ibatis.scripting.xmltags.WhereSqlNode(new org.apache.ibatis.session.Configuration(), mixedContents(new org.apache.ibatis.scripting.xmltags.IfSqlNode(mixedContents(new org.apache.ibatis.scripting.xmltags.TextSqlNode("   and\n ID = ?  ")), "true"))));
        org.apache.ibatis.mapping.BoundSql boundSql = source.getBoundSql(null);
        org.junit.Assert.assertEquals(expected, boundSql.getSql());
    }

    @org.junit.Test
    public void shouldTrimWHEREANDWithCRLFForFirstCondition() throws java.lang.Exception {
        final java.lang.String expected = "SELECT * FROM BLOG WHERE \r\n ID = ?";
        org.apache.ibatis.scripting.xmltags.DynamicSqlSource source = createDynamicSqlSource(new org.apache.ibatis.scripting.xmltags.TextSqlNode("SELECT * FROM BLOG"), new org.apache.ibatis.scripting.xmltags.WhereSqlNode(new org.apache.ibatis.session.Configuration(), mixedContents(new org.apache.ibatis.scripting.xmltags.IfSqlNode(mixedContents(new org.apache.ibatis.scripting.xmltags.TextSqlNode("   and\r\n ID = ?  ")), "true"))));
        org.apache.ibatis.mapping.BoundSql boundSql = source.getBoundSql(null);
        org.junit.Assert.assertEquals(expected, boundSql.getSql());
    }

    @org.junit.Test
    public void shouldTrimWHEREANDWithTABForFirstCondition() throws java.lang.Exception {
        final java.lang.String expected = "SELECT * FROM BLOG WHERE \t ID = ?";
        org.apache.ibatis.scripting.xmltags.DynamicSqlSource source = createDynamicSqlSource(new org.apache.ibatis.scripting.xmltags.TextSqlNode("SELECT * FROM BLOG"), new org.apache.ibatis.scripting.xmltags.WhereSqlNode(new org.apache.ibatis.session.Configuration(), mixedContents(new org.apache.ibatis.scripting.xmltags.IfSqlNode(mixedContents(new org.apache.ibatis.scripting.xmltags.TextSqlNode("   and\t ID = ?  ")), "true"))));
        org.apache.ibatis.mapping.BoundSql boundSql = source.getBoundSql(null);
        org.junit.Assert.assertEquals(expected, boundSql.getSql());
    }

    @org.junit.Test
    public void shouldTrimWHEREORWithLFForFirstCondition() throws java.lang.Exception {
        final java.lang.String expected = "SELECT * FROM BLOG WHERE \n ID = ?";
        org.apache.ibatis.scripting.xmltags.DynamicSqlSource source = createDynamicSqlSource(new org.apache.ibatis.scripting.xmltags.TextSqlNode("SELECT * FROM BLOG"), new org.apache.ibatis.scripting.xmltags.WhereSqlNode(new org.apache.ibatis.session.Configuration(), mixedContents(new org.apache.ibatis.scripting.xmltags.IfSqlNode(mixedContents(new org.apache.ibatis.scripting.xmltags.TextSqlNode("   or\n ID = ?  ")), "true"))));
        org.apache.ibatis.mapping.BoundSql boundSql = source.getBoundSql(null);
        org.junit.Assert.assertEquals(expected, boundSql.getSql());
    }

    @org.junit.Test
    public void shouldTrimWHEREORWithCRLFForFirstCondition() throws java.lang.Exception {
        final java.lang.String expected = "SELECT * FROM BLOG WHERE \r\n ID = ?";
        org.apache.ibatis.scripting.xmltags.DynamicSqlSource source = createDynamicSqlSource(new org.apache.ibatis.scripting.xmltags.TextSqlNode("SELECT * FROM BLOG"), new org.apache.ibatis.scripting.xmltags.WhereSqlNode(new org.apache.ibatis.session.Configuration(), mixedContents(new org.apache.ibatis.scripting.xmltags.IfSqlNode(mixedContents(new org.apache.ibatis.scripting.xmltags.TextSqlNode("   or\r\n ID = ?  ")), "true"))));
        org.apache.ibatis.mapping.BoundSql boundSql = source.getBoundSql(null);
        org.junit.Assert.assertEquals(expected, boundSql.getSql());
    }

    @org.junit.Test
    public void shouldTrimWHEREORWithTABForFirstCondition() throws java.lang.Exception {
        final java.lang.String expected = "SELECT * FROM BLOG WHERE \t ID = ?";
        org.apache.ibatis.scripting.xmltags.DynamicSqlSource source = createDynamicSqlSource(new org.apache.ibatis.scripting.xmltags.TextSqlNode("SELECT * FROM BLOG"), new org.apache.ibatis.scripting.xmltags.WhereSqlNode(new org.apache.ibatis.session.Configuration(), mixedContents(new org.apache.ibatis.scripting.xmltags.IfSqlNode(mixedContents(new org.apache.ibatis.scripting.xmltags.TextSqlNode("   or\t ID = ?  ")), "true"))));
        org.apache.ibatis.mapping.BoundSql boundSql = source.getBoundSql(null);
        org.junit.Assert.assertEquals(expected, boundSql.getSql());
    }

    @org.junit.Test
    public void shouldTrimWHEREInsteadOfORForSecondCondition() throws java.lang.Exception {
        final java.lang.String expected = "SELECT * FROM BLOG WHERE  NAME = ?";
        org.apache.ibatis.scripting.xmltags.DynamicSqlSource source = createDynamicSqlSource(new org.apache.ibatis.scripting.xmltags.TextSqlNode("SELECT * FROM BLOG"), new org.apache.ibatis.scripting.xmltags.WhereSqlNode(new org.apache.ibatis.session.Configuration(), mixedContents(new org.apache.ibatis.scripting.xmltags.IfSqlNode(mixedContents(new org.apache.ibatis.scripting.xmltags.TextSqlNode("   and ID = ?  ")), "false"), new org.apache.ibatis.scripting.xmltags.IfSqlNode(mixedContents(new org.apache.ibatis.scripting.xmltags.TextSqlNode("   or NAME = ?  ")), "true"))));
        org.apache.ibatis.mapping.BoundSql boundSql = source.getBoundSql(null);
        org.junit.Assert.assertEquals(expected, boundSql.getSql());
    }

    @org.junit.Test
    public void shouldTrimWHEREInsteadOfANDForBothConditions() throws java.lang.Exception {
        final java.lang.String expected = "SELECT * FROM BLOG WHERE  ID = ?   OR NAME = ?";
        org.apache.ibatis.scripting.xmltags.DynamicSqlSource source = createDynamicSqlSource(new org.apache.ibatis.scripting.xmltags.TextSqlNode("SELECT * FROM BLOG"), new org.apache.ibatis.scripting.xmltags.WhereSqlNode(new org.apache.ibatis.session.Configuration(), mixedContents(new org.apache.ibatis.scripting.xmltags.IfSqlNode(mixedContents(new org.apache.ibatis.scripting.xmltags.TextSqlNode("   and ID = ?   ")), "true"), new org.apache.ibatis.scripting.xmltags.IfSqlNode(mixedContents(new org.apache.ibatis.scripting.xmltags.TextSqlNode("OR NAME = ?  ")), "true"))));
        org.apache.ibatis.mapping.BoundSql boundSql = source.getBoundSql(null);
        org.junit.Assert.assertEquals(expected, boundSql.getSql());
    }

    @org.junit.Test
    public void shouldTrimNoWhereClause() throws java.lang.Exception {
        final java.lang.String expected = "SELECT * FROM BLOG";
        org.apache.ibatis.scripting.xmltags.DynamicSqlSource source = createDynamicSqlSource(new org.apache.ibatis.scripting.xmltags.TextSqlNode("SELECT * FROM BLOG"), new org.apache.ibatis.scripting.xmltags.WhereSqlNode(new org.apache.ibatis.session.Configuration(), mixedContents(new org.apache.ibatis.scripting.xmltags.IfSqlNode(mixedContents(new org.apache.ibatis.scripting.xmltags.TextSqlNode("   and ID = ?   ")), "false"), new org.apache.ibatis.scripting.xmltags.IfSqlNode(mixedContents(new org.apache.ibatis.scripting.xmltags.TextSqlNode("OR NAME = ?  ")), "false"))));
        org.apache.ibatis.mapping.BoundSql boundSql = source.getBoundSql(null);
        org.junit.Assert.assertEquals(expected, boundSql.getSql());
    }

    @org.junit.Test
    public void shouldTrimSETInsteadOfCOMMAForBothConditions() throws java.lang.Exception {
        final java.lang.String expected = "UPDATE BLOG SET ID = ?,  NAME = ?";
        org.apache.ibatis.scripting.xmltags.DynamicSqlSource source = createDynamicSqlSource(new org.apache.ibatis.scripting.xmltags.TextSqlNode("UPDATE BLOG"), new org.apache.ibatis.scripting.xmltags.SetSqlNode(new org.apache.ibatis.session.Configuration(), mixedContents(new org.apache.ibatis.scripting.xmltags.IfSqlNode(mixedContents(new org.apache.ibatis.scripting.xmltags.TextSqlNode(" ID = ?, ")), "true"), new org.apache.ibatis.scripting.xmltags.IfSqlNode(mixedContents(new org.apache.ibatis.scripting.xmltags.TextSqlNode(" NAME = ?, ")), "true"))));
        org.apache.ibatis.mapping.BoundSql boundSql = source.getBoundSql(null);
        org.junit.Assert.assertEquals(expected, boundSql.getSql());
    }

    @org.junit.Test
    public void shouldTrimNoSetClause() throws java.lang.Exception {
        final java.lang.String expected = "UPDATE BLOG";
        org.apache.ibatis.scripting.xmltags.DynamicSqlSource source = createDynamicSqlSource(new org.apache.ibatis.scripting.xmltags.TextSqlNode("UPDATE BLOG"), new org.apache.ibatis.scripting.xmltags.SetSqlNode(new org.apache.ibatis.session.Configuration(), mixedContents(new org.apache.ibatis.scripting.xmltags.IfSqlNode(mixedContents(new org.apache.ibatis.scripting.xmltags.TextSqlNode("   , ID = ?   ")), "false"), new org.apache.ibatis.scripting.xmltags.IfSqlNode(mixedContents(new org.apache.ibatis.scripting.xmltags.TextSqlNode(", NAME = ?  ")), "false"))));
        org.apache.ibatis.mapping.BoundSql boundSql = source.getBoundSql(null);
        org.junit.Assert.assertEquals(expected, boundSql.getSql());
    }

    @org.junit.Test
    public void shouldIterateOnceForEachItemInCollection() throws java.lang.Exception {
        final java.util.HashMap<java.lang.String, java.lang.String[]> parameterObject = new java.util.HashMap<java.lang.String, java.lang.String[]>() {
            {
                put("array", new java.lang.String[]{ "one" , "two" , "three" });
            }
        };
        final java.lang.String expected = "SELECT * FROM BLOG WHERE ID in (  one = ? AND two = ? AND three = ? )";
        org.apache.ibatis.scripting.xmltags.DynamicSqlSource source = createDynamicSqlSource(new org.apache.ibatis.scripting.xmltags.TextSqlNode("SELECT * FROM BLOG WHERE ID in"), new org.apache.ibatis.scripting.xmltags.ForEachSqlNode(new org.apache.ibatis.session.Configuration(), mixedContents(new org.apache.ibatis.scripting.xmltags.TextSqlNode("${item} = #{item}")), "array", "index", "item", "(", ")", "AND"));
        org.apache.ibatis.mapping.BoundSql boundSql = source.getBoundSql(parameterObject);
        org.junit.Assert.assertEquals(expected, boundSql.getSql());
        org.junit.Assert.assertEquals(3, boundSql.getParameterMappings().size());
        org.junit.Assert.assertEquals("__frch_item_0", boundSql.getParameterMappings().get(0).getProperty());
        org.junit.Assert.assertEquals("__frch_item_1", boundSql.getParameterMappings().get(1).getProperty());
        org.junit.Assert.assertEquals("__frch_item_2", boundSql.getParameterMappings().get(2).getProperty());
    }

    @org.junit.Test
    public void shouldHandleOgnlExpression() throws java.lang.Exception {
        final java.util.HashMap<java.lang.String, java.lang.String> parameterObject = new java.util.HashMap<java.lang.String, java.lang.String>() {
            {
                put("name", "Steve");
            }
        };
        final java.lang.String expected = "Expression test: 3 / yes.";
        org.apache.ibatis.scripting.xmltags.DynamicSqlSource source = createDynamicSqlSource(new org.apache.ibatis.scripting.xmltags.TextSqlNode("Expression test: ${name.indexOf(\'v\')} / ${name in {\'Bob\', \'Steve\'\\} ? \'yes\' : \'no\'}."));
        org.apache.ibatis.mapping.BoundSql boundSql = source.getBoundSql(parameterObject);
        org.junit.Assert.assertEquals(expected, boundSql.getSql());
    }

    @org.junit.Test
    public void shouldSkipForEachWhenCollectionIsEmpty() throws java.lang.Exception {
        final java.util.HashMap<java.lang.String, java.lang.Integer[]> parameterObject = new java.util.HashMap<java.lang.String, java.lang.Integer[]>() {
            {
                put("array", new java.lang.Integer[]{  });
            }
        };
        final java.lang.String expected = "SELECT * FROM BLOG";
        org.apache.ibatis.scripting.xmltags.DynamicSqlSource source = createDynamicSqlSource(new org.apache.ibatis.scripting.xmltags.TextSqlNode("SELECT * FROM BLOG"), new org.apache.ibatis.scripting.xmltags.ForEachSqlNode(new org.apache.ibatis.session.Configuration(), mixedContents(new org.apache.ibatis.scripting.xmltags.TextSqlNode("#{item}")), "array", null, "item", "WHERE id in (", ")", ","));
        org.apache.ibatis.mapping.BoundSql boundSql = source.getBoundSql(parameterObject);
        org.junit.Assert.assertEquals(expected, boundSql.getSql());
        org.junit.Assert.assertEquals(0, boundSql.getParameterMappings().size());
    }

    @org.junit.Test
    public void shouldPerformStrictMatchOnForEachVariableSubstitution() throws java.lang.Exception {
        final java.util.Map<java.lang.String, java.lang.Object> param = new java.util.HashMap<java.lang.String, java.lang.Object>();
        final java.util.Map<java.lang.String, java.lang.String> uuu = new java.util.HashMap<java.lang.String, java.lang.String>();
        uuu.put("u", "xyz");
        java.util.List<org.apache.ibatis.builder.xml.dynamic.AmplDynamicSqlSourceTest.Bean> uuuu = new java.util.ArrayList<org.apache.ibatis.builder.xml.dynamic.AmplDynamicSqlSourceTest.Bean>();
        uuuu.add(new org.apache.ibatis.builder.xml.dynamic.AmplDynamicSqlSourceTest.Bean("bean id"));
        param.put("uuu", uuu);
        param.put("uuuu", uuuu);
        org.apache.ibatis.scripting.xmltags.DynamicSqlSource source = createDynamicSqlSource(new org.apache.ibatis.scripting.xmltags.TextSqlNode("INSERT INTO BLOG (ID, NAME, NOTE, COMMENT) VALUES"), new org.apache.ibatis.scripting.xmltags.ForEachSqlNode(new org.apache.ibatis.session.Configuration(), mixedContents(new org.apache.ibatis.scripting.xmltags.TextSqlNode(("#{uuu.u}, #{u.id}, #{ u,typeHandler=org.apache.ibatis.type.StringTypeHandler}," + " #{u:VARCHAR,typeHandler=org.apache.ibatis.type.StringTypeHandler}"))), "uuuu", "uu", "u", "(", ")", ","));
        org.apache.ibatis.mapping.BoundSql boundSql = source.getBoundSql(param);
        org.junit.Assert.assertEquals(4, boundSql.getParameterMappings().size());
        org.junit.Assert.assertEquals("uuu.u", boundSql.getParameterMappings().get(0).getProperty());
        org.junit.Assert.assertEquals("__frch_u_0.id", boundSql.getParameterMappings().get(1).getProperty());
        org.junit.Assert.assertEquals("__frch_u_0", boundSql.getParameterMappings().get(2).getProperty());
        org.junit.Assert.assertEquals("__frch_u_0", boundSql.getParameterMappings().get(3).getProperty());
    }

    private org.apache.ibatis.scripting.xmltags.DynamicSqlSource createDynamicSqlSource(org.apache.ibatis.scripting.xmltags.SqlNode... contents) throws java.io.IOException, java.sql.SQLException {
        org.apache.ibatis.BaseDataTest.createBlogDataSource();
        final java.lang.String resource = "org/apache/ibatis/builder/MapperConfig.xml";
        final java.io.Reader reader = org.apache.ibatis.io.Resources.getResourceAsReader(resource);
        org.apache.ibatis.session.SqlSessionFactory sqlMapper = new org.apache.ibatis.session.SqlSessionFactoryBuilder().build(reader);
        org.apache.ibatis.session.Configuration configuration = sqlMapper.getConfiguration();
        org.apache.ibatis.scripting.xmltags.MixedSqlNode sqlNode = mixedContents(contents);
        return new org.apache.ibatis.scripting.xmltags.DynamicSqlSource(configuration, sqlNode);
    }

    private org.apache.ibatis.scripting.xmltags.MixedSqlNode mixedContents(org.apache.ibatis.scripting.xmltags.SqlNode... contents) {
        return new org.apache.ibatis.scripting.xmltags.MixedSqlNode(java.util.Arrays.asList(contents));
    }

    @org.junit.Test
    public void shouldMapNullStringsToEmptyStrings() {
        final java.lang.String expected = "id=${id}";
        final org.apache.ibatis.scripting.xmltags.MixedSqlNode sqlNode = mixedContents(new org.apache.ibatis.scripting.xmltags.TextSqlNode(expected));
        final org.apache.ibatis.scripting.xmltags.DynamicSqlSource source = new org.apache.ibatis.scripting.xmltags.DynamicSqlSource(new org.apache.ibatis.session.Configuration(), sqlNode);
        java.lang.String sql = source.getBoundSql(new org.apache.ibatis.builder.xml.dynamic.AmplDynamicSqlSourceTest.Bean(null)).getSql();
        org.junit.Assert.assertEquals("id=", sql);
    }

    public static class Bean {
        public java.lang.String id;

        public Bean(java.lang.String property) {
            this.id = property;
        }

        public java.lang.String getId() {
            return id;
        }

        public void setId(java.lang.String property) {
            this.id = property;
        }
    }
}

