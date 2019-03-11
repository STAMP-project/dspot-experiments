/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.calcite.test;


import Schema.TableType;
import SqlTypeName.BIGINT;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import net.hydromatic.quidem.Quidem;
import org.apache.calcite.jdbc.CalciteConnection;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.schema.impl.AbstractTable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.apache.calcite.test.CalciteAssert.Config.FOODMART_CLONE;
import static org.apache.calcite.test.CalciteAssert.Config.GEO;
import static org.apache.calcite.test.CalciteAssert.Config.JDBC_SCOTT;
import static org.apache.calcite.test.CalciteAssert.Config.REGULAR;
import static org.apache.calcite.test.CalciteAssert.Config.SCOTT;
import static org.apache.calcite.test.CalciteAssert.DatabaseInstance.HSQLDB;
import static org.apache.calcite.test.CalciteAssert.SchemaSpec.BLANK;
import static org.apache.calcite.test.CalciteAssert.SchemaSpec.ORINOCO;
import static org.apache.calcite.test.CalciteAssert.SchemaSpec.POST;


/**
 * Test that runs every Quidem file as a test.
 */
@RunWith(Parameterized.class)
public abstract class QuidemTest {
    protected final String path;

    protected final Method method;

    /**
     * Creates a QuidemTest.
     */
    protected QuidemTest(String path) {
        this.path = path;
        this.method = findMethod(path);
    }

    @Test
    public void test() throws Exception {
        if ((method) != null) {
            try {
                method.invoke(this);
            } catch (InvocationTargetException e) {
                Throwable cause = e.getCause();
                if (cause instanceof Exception) {
                    throw ((Exception) (cause));
                }
                if (cause instanceof Error) {
                    throw ((Error) (cause));
                }
                throw e;
            }
        } else {
            checkRun(path);
        }
    }

    /**
     * Quidem connection factory for Calcite's built-in test schemas.
     */
    protected static class QuidemConnectionFactory implements Quidem.ConnectionFactory {
        public Connection connect(String name) throws Exception {
            return connect(name, false);
        }

        public Connection connect(String name, boolean reference) throws Exception {
            if (reference) {
                if (name.equals("foodmart")) {
                    final ConnectionSpec db = HSQLDB.foodmart;
                    final Connection connection = DriverManager.getConnection(db.url, db.username, db.password);
                    connection.setSchema("foodmart");
                    return connection;
                }
                return null;
            }
            switch (name) {
                case "hr" :
                    return CalciteAssert.hr().connect();
                case "foodmart" :
                    return CalciteAssert.that().with(FOODMART_CLONE).connect();
                case "geo" :
                    return CalciteAssert.that().with(GEO).connect();
                case "scott" :
                    return CalciteAssert.that().with(SCOTT).connect();
                case "jdbc_scott" :
                    return CalciteAssert.that().with(JDBC_SCOTT).connect();
                case "post" :
                    return CalciteAssert.that().with(REGULAR).with(POST).connect();
                case "catchall" :
                    return CalciteAssert.that().withSchema("s", new org.apache.calcite.adapter.java.ReflectiveSchema(new ReflectiveSchemaTest.CatchallSchema())).connect();
                case "orinoco" :
                    return CalciteAssert.that().with(ORINOCO).connect();
                case "blank" :
                    return CalciteAssert.that().with(CalciteConnectionProperty.PARSER_FACTORY, ("org.apache.calcite.sql.parser.parserextensiontesting" + ".ExtensionSqlParserImpl#FACTORY")).with(BLANK).connect();
                case "seq" :
                    final Connection connection = CalciteAssert.that().withSchema("s", new org.apache.calcite.schema.impl.AbstractSchema()).connect();
                    connection.unwrap(CalciteConnection.class).getRootSchema().getSubSchema("s").add("my_seq", new AbstractTable() {
                        public RelDataType getRowType(RelDataTypeFactory typeFactory) {
                            return typeFactory.builder().add("$seq", BIGINT).build();
                        }

                        @Override
                        public TableType getJdbcTableType() {
                            return TableType.SEQUENCE;
                        }
                    });
                    return connection;
                default :
                    throw new RuntimeException((("unknown connection '" + name) + "'"));
            }
        }
    }
}

/**
 * End QuidemTest.java
 */
