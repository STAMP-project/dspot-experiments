/**
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.sql.oracle.demo;


import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLNumericLiteralExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.expr.SQLVariantRefExpr;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitorAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;


public class Demo3 extends TestCase {
    public void test_0() throws Exception {
        String sql = "select * from user u where u.uid = 2 and uname = ?";
        List<Object> parameters = new ArrayList<Object>();
        parameters.add(1);
        parameters.add("wenshao");
        String realSql = convert(sql, parameters);
        System.out.println(realSql);
    }

    public void test_1() throws Exception {
        String sql = "select * from user where uid = ? and uname = ?";
        List<Object> parameters = new ArrayList<Object>();
        parameters.add(1);
        parameters.add("wenshao");
        String realSql = convert(sql, parameters);
        System.out.println(realSql);
    }

    public void test_2() throws Exception {
        String sql = "select * from (select * from user where uid = ? and uname = ?) t";
        List<Object> parameters = new ArrayList<Object>();
        parameters.add(1);
        parameters.add("wenshao");
        String realSql = convert(sql, parameters);
        System.out.println(realSql);
    }

    public void test_3() throws Exception {
        String sql = "select * from groups where uid = ? and uname = ?";
        List<Object> parameters = new ArrayList<Object>();
        parameters.add(1);
        parameters.add("wenshao");
        String realSql = convert(sql, parameters);
        System.out.println(realSql);
    }

    private static class MyVisitor extends MySqlASTVisitorAdapter {
        private int varIndex = 0;

        private List<SQLExpr> variantList = new ArrayList<SQLExpr>();

        private List<SQLExprTableSource> tableSourceList = new ArrayList<SQLExprTableSource>();

        private Map<String, String> tableAlias = new HashMap<String, String>();

        private String defaultTableName;

        public boolean visit(SQLVariantRefExpr x) {
            x.getAttributes().put("varIndex", ((varIndex)++));
            return true;
        }

        public boolean visit(SQLBinaryOpExpr x) {
            if (isUserId(x.getLeft())) {
                if ((x.getRight()) instanceof SQLVariantRefExpr) {
                    SQLIdentifierExpr identExpr = ((SQLIdentifierExpr) (x.getLeft()));
                    String ident = identExpr.getName();
                    if (ident.equals("uid")) {
                        variantList.add(x.getRight());
                    }
                } else
                    if ((x.getRight()) instanceof SQLNumericLiteralExpr) {
                        variantList.add(x.getRight());
                    }

            }
            return true;
        }

        private boolean isUserId(SQLExpr x) {
            if (x instanceof SQLIdentifierExpr) {
                if (("user".equals(defaultTableName)) && ("uid".equals(getName()))) {
                    return true;
                }
                return false;
            }
            if (x instanceof SQLPropertyExpr) {
                SQLPropertyExpr propExpr = ((SQLPropertyExpr) (x));
                String columnName = propExpr.getName();
                if (!("uid".equals(columnName))) {
                    return false;
                }
                if ((propExpr.getOwner()) instanceof SQLIdentifierExpr) {
                    String ownerName = ((SQLIdentifierExpr) (propExpr.getOwner())).getName();
                    if (("user".equals(ownerName)) || ("user".equals(tableAlias.get(ownerName)))) {
                        return true;
                    }
                }
            }
            return false;
        }

        public boolean visit(SQLExprTableSource x) {
            recordTableSource(x);
            return true;
        }

        private String recordTableSource(SQLExprTableSource x) {
            if ((x.getExpr()) instanceof SQLIdentifierExpr) {
                String tableName = ((SQLIdentifierExpr) (x.getExpr())).getName();
                if ((x.getAlias()) != null) {
                    tableAlias.put(x.getAlias(), tableName);
                }
                if ("user".equals(tableName)) {
                    if (!(tableSourceList.contains(x))) {
                        tableSourceList.add(x);
                    }
                }
                return tableName;
            }
            return null;
        }

        public boolean visit(SQLSelectQueryBlock queryBlock) {
            if ((queryBlock.getFrom()) instanceof SQLExprTableSource) {
                defaultTableName = recordTableSource(((SQLExprTableSource) (queryBlock.getFrom())));
            }
            return true;
        }

        public boolean visit(MySqlSelectQueryBlock queryBlock) {
            if ((queryBlock.getFrom()) instanceof SQLExprTableSource) {
                defaultTableName = recordTableSource(((SQLExprTableSource) (queryBlock.getFrom())));
            }
            return true;
        }

        public List<SQLExpr> getVariantList() {
            return variantList;
        }

        public List<SQLExprTableSource> getTableSourceList() {
            return tableSourceList;
        }
    }
}

