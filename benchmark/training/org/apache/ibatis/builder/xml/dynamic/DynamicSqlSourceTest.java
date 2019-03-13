/**
 * Copyright 2009-2011 the original author or authors.
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


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.scripting.xmltags.DynamicSqlSource;
import org.apache.ibatis.scripting.xmltags.MixedSqlNode;
import org.apache.ibatis.scripting.xmltags.SqlNode;
import org.apache.ibatis.scripting.xmltags.TextSqlNode;
import org.apache.ibatis.session.Configuration;
import org.junit.Assert;
import org.junit.Test;


public class DynamicSqlSourceTest extends BaseDataTest {
    @Test
    public void shouldDemonstrateSimpleExpectedTextWithNoLoopsOrConditionals() throws Exception {
        final String expected = "SELECT * FROM BLOG";
        final MixedSqlNode sqlNode = mixedContents(new TextSqlNode(expected));
        DynamicSqlSource source = createDynamicSqlSource(sqlNode);
        BoundSql boundSql = source.getBoundSql(null);
        Assert.assertEquals(expected, boundSql.getSql());
    }

    @Test
    public void shouldDemonstrateMultipartExpectedTextWithNoLoopsOrConditionals() throws Exception {
        final String expected = "SELECT * FROM BLOG WHERE ID = ?";
        DynamicSqlSource source = createDynamicSqlSource(new TextSqlNode("SELECT * FROM BLOG"), new TextSqlNode("WHERE ID = ?"));
        BoundSql boundSql = source.getBoundSql(null);
        Assert.assertEquals(expected, boundSql.getSql());
    }

    @Test
    public void shouldConditionallyIncludeWhere() throws Exception {
        final String expected = "SELECT * FROM BLOG WHERE ID = ?";
        DynamicSqlSource source = createDynamicSqlSource(new TextSqlNode("SELECT * FROM BLOG"), new org.apache.ibatis.scripting.xmltags.IfSqlNode(mixedContents(new TextSqlNode("WHERE ID = ?")), "true"));
        BoundSql boundSql = source.getBoundSql(null);
        Assert.assertEquals(expected, boundSql.getSql());
    }

    @Test
    public void shouldConditionallyExcludeWhere() throws Exception {
        final String expected = "SELECT * FROM BLOG";
        DynamicSqlSource source = createDynamicSqlSource(new TextSqlNode("SELECT * FROM BLOG"), new org.apache.ibatis.scripting.xmltags.IfSqlNode(mixedContents(new TextSqlNode("WHERE ID = ?")), "false"));
        BoundSql boundSql = source.getBoundSql(null);
        Assert.assertEquals(expected, boundSql.getSql());
    }

    @Test
    public void shouldConditionallyDefault() throws Exception {
        final String expected = "SELECT * FROM BLOG WHERE CATEGORY = 'DEFAULT'";
        DynamicSqlSource source = createDynamicSqlSource(new TextSqlNode("SELECT * FROM BLOG"), new org.apache.ibatis.scripting.xmltags.ChooseSqlNode(new ArrayList<SqlNode>() {
            {
                add(new org.apache.ibatis.scripting.xmltags.IfSqlNode(mixedContents(new TextSqlNode("WHERE CATEGORY = ?")), "false"));
                add(new org.apache.ibatis.scripting.xmltags.IfSqlNode(mixedContents(new TextSqlNode("WHERE CATEGORY = 'NONE'")), "false"));
            }
        }, mixedContents(new TextSqlNode("WHERE CATEGORY = 'DEFAULT'"))));
        BoundSql boundSql = source.getBoundSql(null);
        Assert.assertEquals(expected, boundSql.getSql());
    }

    @Test
    public void shouldConditionallyChooseFirst() throws Exception {
        final String expected = "SELECT * FROM BLOG WHERE CATEGORY = ?";
        DynamicSqlSource source = createDynamicSqlSource(new TextSqlNode("SELECT * FROM BLOG"), new org.apache.ibatis.scripting.xmltags.ChooseSqlNode(new ArrayList<SqlNode>() {
            {
                add(new org.apache.ibatis.scripting.xmltags.IfSqlNode(mixedContents(new TextSqlNode("WHERE CATEGORY = ?")), "true"));
                add(new org.apache.ibatis.scripting.xmltags.IfSqlNode(mixedContents(new TextSqlNode("WHERE CATEGORY = 'NONE'")), "false"));
            }
        }, mixedContents(new TextSqlNode("WHERE CATEGORY = 'DEFAULT'"))));
        BoundSql boundSql = source.getBoundSql(null);
        Assert.assertEquals(expected, boundSql.getSql());
    }

    @Test
    public void shouldConditionallyChooseSecond() throws Exception {
        final String expected = "SELECT * FROM BLOG WHERE CATEGORY = 'NONE'";
        DynamicSqlSource source = createDynamicSqlSource(new TextSqlNode("SELECT * FROM BLOG"), new org.apache.ibatis.scripting.xmltags.ChooseSqlNode(new ArrayList<SqlNode>() {
            {
                add(new org.apache.ibatis.scripting.xmltags.IfSqlNode(mixedContents(new TextSqlNode("WHERE CATEGORY = ?")), "false"));
                add(new org.apache.ibatis.scripting.xmltags.IfSqlNode(mixedContents(new TextSqlNode("WHERE CATEGORY = 'NONE'")), "true"));
            }
        }, mixedContents(new TextSqlNode("WHERE CATEGORY = 'DEFAULT'"))));
        BoundSql boundSql = source.getBoundSql(null);
        Assert.assertEquals(expected, boundSql.getSql());
    }

    @Test
    public void shouldTrimWHEREInsteadOfANDForFirstCondition() throws Exception {
        final String expected = "SELECT * FROM BLOG WHERE  ID = ?";
        DynamicSqlSource source = createDynamicSqlSource(new TextSqlNode("SELECT * FROM BLOG"), new org.apache.ibatis.scripting.xmltags.WhereSqlNode(new Configuration(), mixedContents(new org.apache.ibatis.scripting.xmltags.IfSqlNode(mixedContents(new TextSqlNode("   and ID = ?  ")), "true"), new org.apache.ibatis.scripting.xmltags.IfSqlNode(mixedContents(new TextSqlNode("   or NAME = ?  ")), "false"))));
        BoundSql boundSql = source.getBoundSql(null);
        Assert.assertEquals(expected, boundSql.getSql());
    }

    @Test
    public void shouldTrimWHEREANDWithLFForFirstCondition() throws Exception {
        final String expected = "SELECT * FROM BLOG WHERE \n ID = ?";
        DynamicSqlSource source = createDynamicSqlSource(new TextSqlNode("SELECT * FROM BLOG"), new org.apache.ibatis.scripting.xmltags.WhereSqlNode(new Configuration(), mixedContents(new org.apache.ibatis.scripting.xmltags.IfSqlNode(mixedContents(new TextSqlNode("   and\n ID = ?  ")), "true"))));
        BoundSql boundSql = source.getBoundSql(null);
        Assert.assertEquals(expected, boundSql.getSql());
    }

    @Test
    public void shouldTrimWHEREANDWithCRLFForFirstCondition() throws Exception {
        final String expected = "SELECT * FROM BLOG WHERE \r\n ID = ?";
        DynamicSqlSource source = createDynamicSqlSource(new TextSqlNode("SELECT * FROM BLOG"), new org.apache.ibatis.scripting.xmltags.WhereSqlNode(new Configuration(), mixedContents(new org.apache.ibatis.scripting.xmltags.IfSqlNode(mixedContents(new TextSqlNode("   and\r\n ID = ?  ")), "true"))));
        BoundSql boundSql = source.getBoundSql(null);
        Assert.assertEquals(expected, boundSql.getSql());
    }

    @Test
    public void shouldTrimWHEREANDWithTABForFirstCondition() throws Exception {
        final String expected = "SELECT * FROM BLOG WHERE \t ID = ?";
        DynamicSqlSource source = createDynamicSqlSource(new TextSqlNode("SELECT * FROM BLOG"), new org.apache.ibatis.scripting.xmltags.WhereSqlNode(new Configuration(), mixedContents(new org.apache.ibatis.scripting.xmltags.IfSqlNode(mixedContents(new TextSqlNode("   and\t ID = ?  ")), "true"))));
        BoundSql boundSql = source.getBoundSql(null);
        Assert.assertEquals(expected, boundSql.getSql());
    }

    @Test
    public void shouldTrimWHEREORWithLFForFirstCondition() throws Exception {
        final String expected = "SELECT * FROM BLOG WHERE \n ID = ?";
        DynamicSqlSource source = createDynamicSqlSource(new TextSqlNode("SELECT * FROM BLOG"), new org.apache.ibatis.scripting.xmltags.WhereSqlNode(new Configuration(), mixedContents(new org.apache.ibatis.scripting.xmltags.IfSqlNode(mixedContents(new TextSqlNode("   or\n ID = ?  ")), "true"))));
        BoundSql boundSql = source.getBoundSql(null);
        Assert.assertEquals(expected, boundSql.getSql());
    }

    @Test
    public void shouldTrimWHEREORWithCRLFForFirstCondition() throws Exception {
        final String expected = "SELECT * FROM BLOG WHERE \r\n ID = ?";
        DynamicSqlSource source = createDynamicSqlSource(new TextSqlNode("SELECT * FROM BLOG"), new org.apache.ibatis.scripting.xmltags.WhereSqlNode(new Configuration(), mixedContents(new org.apache.ibatis.scripting.xmltags.IfSqlNode(mixedContents(new TextSqlNode("   or\r\n ID = ?  ")), "true"))));
        BoundSql boundSql = source.getBoundSql(null);
        Assert.assertEquals(expected, boundSql.getSql());
    }

    @Test
    public void shouldTrimWHEREORWithTABForFirstCondition() throws Exception {
        final String expected = "SELECT * FROM BLOG WHERE \t ID = ?";
        DynamicSqlSource source = createDynamicSqlSource(new TextSqlNode("SELECT * FROM BLOG"), new org.apache.ibatis.scripting.xmltags.WhereSqlNode(new Configuration(), mixedContents(new org.apache.ibatis.scripting.xmltags.IfSqlNode(mixedContents(new TextSqlNode("   or\t ID = ?  ")), "true"))));
        BoundSql boundSql = source.getBoundSql(null);
        Assert.assertEquals(expected, boundSql.getSql());
    }

    @Test
    public void shouldTrimWHEREInsteadOfORForSecondCondition() throws Exception {
        final String expected = "SELECT * FROM BLOG WHERE  NAME = ?";
        DynamicSqlSource source = createDynamicSqlSource(new TextSqlNode("SELECT * FROM BLOG"), new org.apache.ibatis.scripting.xmltags.WhereSqlNode(new Configuration(), mixedContents(new org.apache.ibatis.scripting.xmltags.IfSqlNode(mixedContents(new TextSqlNode("   and ID = ?  ")), "false"), new org.apache.ibatis.scripting.xmltags.IfSqlNode(mixedContents(new TextSqlNode("   or NAME = ?  ")), "true"))));
        BoundSql boundSql = source.getBoundSql(null);
        Assert.assertEquals(expected, boundSql.getSql());
    }

    @Test
    public void shouldTrimWHEREInsteadOfANDForBothConditions() throws Exception {
        final String expected = "SELECT * FROM BLOG WHERE  ID = ?   OR NAME = ?";
        DynamicSqlSource source = createDynamicSqlSource(new TextSqlNode("SELECT * FROM BLOG"), new org.apache.ibatis.scripting.xmltags.WhereSqlNode(new Configuration(), mixedContents(new org.apache.ibatis.scripting.xmltags.IfSqlNode(mixedContents(new TextSqlNode("   and ID = ?   ")), "true"), new org.apache.ibatis.scripting.xmltags.IfSqlNode(mixedContents(new TextSqlNode("OR NAME = ?  ")), "true"))));
        BoundSql boundSql = source.getBoundSql(null);
        Assert.assertEquals(expected, boundSql.getSql());
    }

    @Test
    public void shouldTrimNoWhereClause() throws Exception {
        final String expected = "SELECT * FROM BLOG";
        DynamicSqlSource source = createDynamicSqlSource(new TextSqlNode("SELECT * FROM BLOG"), new org.apache.ibatis.scripting.xmltags.WhereSqlNode(new Configuration(), mixedContents(new org.apache.ibatis.scripting.xmltags.IfSqlNode(mixedContents(new TextSqlNode("   and ID = ?   ")), "false"), new org.apache.ibatis.scripting.xmltags.IfSqlNode(mixedContents(new TextSqlNode("OR NAME = ?  ")), "false"))));
        BoundSql boundSql = source.getBoundSql(null);
        Assert.assertEquals(expected, boundSql.getSql());
    }

    @Test
    public void shouldTrimSETInsteadOfCOMMAForBothConditions() throws Exception {
        final String expected = "UPDATE BLOG SET ID = ?,  NAME = ?";
        DynamicSqlSource source = createDynamicSqlSource(new TextSqlNode("UPDATE BLOG"), new org.apache.ibatis.scripting.xmltags.SetSqlNode(new Configuration(), mixedContents(new org.apache.ibatis.scripting.xmltags.IfSqlNode(mixedContents(new TextSqlNode(" ID = ?, ")), "true"), new org.apache.ibatis.scripting.xmltags.IfSqlNode(mixedContents(new TextSqlNode(" NAME = ?, ")), "true"))));
        BoundSql boundSql = source.getBoundSql(null);
        Assert.assertEquals(expected, boundSql.getSql());
    }

    @Test
    public void shouldTrimNoSetClause() throws Exception {
        final String expected = "UPDATE BLOG";
        DynamicSqlSource source = createDynamicSqlSource(new TextSqlNode("UPDATE BLOG"), new org.apache.ibatis.scripting.xmltags.SetSqlNode(new Configuration(), mixedContents(new org.apache.ibatis.scripting.xmltags.IfSqlNode(mixedContents(new TextSqlNode("   , ID = ?   ")), "false"), new org.apache.ibatis.scripting.xmltags.IfSqlNode(mixedContents(new TextSqlNode(", NAME = ?  ")), "false"))));
        BoundSql boundSql = source.getBoundSql(null);
        Assert.assertEquals(expected, boundSql.getSql());
    }

    @Test
    public void shouldIterateOnceForEachItemInCollection() throws Exception {
        final HashMap<String, String[]> parameterObject = new HashMap<String, String[]>() {
            {
                put("array", new String[]{ "one", "two", "three" });
            }
        };
        final String expected = "SELECT * FROM BLOG WHERE ID in (  one = ? AND two = ? AND three = ? )";
        DynamicSqlSource source = createDynamicSqlSource(new TextSqlNode("SELECT * FROM BLOG WHERE ID in"), new org.apache.ibatis.scripting.xmltags.ForEachSqlNode(new Configuration(), mixedContents(new TextSqlNode("${item} = #{item}")), "array", "index", "item", "(", ")", "AND"));
        BoundSql boundSql = source.getBoundSql(parameterObject);
        Assert.assertEquals(expected, boundSql.getSql());
        Assert.assertEquals(3, boundSql.getParameterMappings().size());
        Assert.assertEquals("__frch_item_0", boundSql.getParameterMappings().get(0).getProperty());
        Assert.assertEquals("__frch_item_1", boundSql.getParameterMappings().get(1).getProperty());
        Assert.assertEquals("__frch_item_2", boundSql.getParameterMappings().get(2).getProperty());
    }

    @Test
    public void shouldSkipForEachWhenCollectionIsEmpty() throws Exception {
        final HashMap<String, Integer[]> parameterObject = new HashMap<String, Integer[]>() {
            {
                put("array", new Integer[]{  });
            }
        };
        final String expected = "SELECT * FROM BLOG";
        DynamicSqlSource source = createDynamicSqlSource(new TextSqlNode("SELECT * FROM BLOG"), new org.apache.ibatis.scripting.xmltags.ForEachSqlNode(new Configuration(), mixedContents(new TextSqlNode("#{item}")), "array", null, "item", "WHERE id in (", ")", ","));
        BoundSql boundSql = source.getBoundSql(parameterObject);
        Assert.assertEquals(expected, boundSql.getSql());
        Assert.assertEquals(0, boundSql.getParameterMappings().size());
    }

    @Test
    public void shouldPerformStrictMatchOnForEachVariableSubstitution() throws Exception {
        final Map<String, Object> param = new HashMap<String, Object>();
        final Map<String, String> uuu = new HashMap<String, String>();
        uuu.put("u", "xyz");
        List<DynamicSqlSourceTest.Bean> uuuu = new ArrayList<DynamicSqlSourceTest.Bean>();
        uuuu.add(new DynamicSqlSourceTest.Bean("bean id"));
        param.put("uuu", uuu);
        param.put("uuuu", uuuu);
        DynamicSqlSource source = createDynamicSqlSource(new TextSqlNode("INSERT INTO BLOG (ID, NAME, NOTE, COMMENT) VALUES"), new org.apache.ibatis.scripting.xmltags.ForEachSqlNode(new Configuration(), mixedContents(new TextSqlNode(("#{uuu.u}, #{u.id}, #{ u,typeHandler=org.apache.ibatis.type.StringTypeHandler}," + " #{u:VARCHAR,typeHandler=org.apache.ibatis.type.StringTypeHandler}"))), "uuuu", "uu", "u", "(", ")", ","));
        BoundSql boundSql = source.getBoundSql(param);
        Assert.assertEquals(4, boundSql.getParameterMappings().size());
        Assert.assertEquals("uuu.u", boundSql.getParameterMappings().get(0).getProperty());
        Assert.assertEquals("__frch_u_0.id", boundSql.getParameterMappings().get(1).getProperty());
        Assert.assertEquals("__frch_u_0", boundSql.getParameterMappings().get(2).getProperty());
        Assert.assertEquals("__frch_u_0", boundSql.getParameterMappings().get(3).getProperty());
    }

    @Test
    public void shouldMapNullStringsToEmptyStrings() {
        final String expected = "id=${id}";
        final MixedSqlNode sqlNode = mixedContents(new TextSqlNode(expected));
        final DynamicSqlSource source = new DynamicSqlSource(new Configuration(), sqlNode);
        String sql = source.getBoundSql(new DynamicSqlSourceTest.Bean(null)).getSql();
        Assert.assertEquals("id=", sql);
    }

    public static class Bean {
        public String id;

        public Bean(String property) {
            this.id = property;
        }

        public String getId() {
            return id;
        }

        public void setId(String property) {
            this.id = property;
        }
    }
}

