/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.jdbc.test;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.apache.drill.categories.JdbcTest;
import org.apache.drill.jdbc.AlreadyClosedSqlException;
import org.apache.drill.jdbc.JdbcTestBase;
import org.apache.drill.test.TestTools;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test that non-SQLException exceptions used by Drill's current version of
 * Avatica to indicate unsupported features are wrapped in or mapped to
 * SQLException exceptions.
 *
 * <p>
 *   As of 2015-08-24, Drill's version of Avatica used non-SQLException exception
 *   class to report that methods/features were not implemented.
 * </p>
 * <pre>
 *   5 UnsupportedOperationException in ArrayImpl
 *  29 UnsupportedOperationException in AvaticaConnection
 *  10 Helper.todo() (RuntimeException) in AvaticaDatabaseMetaData
 *  21 UnsupportedOperationException in AvaticaStatement
 *   4 UnsupportedOperationException in AvaticaPreparedStatement
 * 103 UnsupportedOperationException in AvaticaResultSet
 * </pre>
 */
@Category(JdbcTest.class)
public class Drill2769UnsupportedReportsUseSqlExceptionTest extends JdbcTestBase {
    private static final Logger logger = LoggerFactory.getLogger(Drill2769UnsupportedReportsUseSqlExceptionTest.class);

    @Rule
    public TestRule TIMEOUT = /* ms */
    TestTools.getTimeoutRule(180000);

    private static Connection connection;

    private static Statement plainStatement;

    private static PreparedStatement preparedStatement;

    // No CallableStatement.
    private static ResultSet resultSet;

    private static ResultSetMetaData resultSetMetaData;

    private static DatabaseMetaData databaseMetaData;

    /**
     * Reflection-based checker that exceptions thrown by JDBC interfaces'
     * implementation methods for unsupported-operation cases are SQLExceptions
     * (not UnsupportedOperationExceptions).
     *
     * @param <INTF>
     * 		JDBC interface type
     */
    private static class NoNonSqlExceptionsChecker<INTF> {
        private final Class<INTF> jdbcIntf;

        private final INTF jdbcObject;

        private final StringBuilder failureLinesBuf = new StringBuilder();

        private final StringBuilder successLinesBuf = new StringBuilder();

        NoNonSqlExceptionsChecker(final Class<INTF> jdbcIntf, final INTF jdbcObject) {
            this.jdbcIntf = jdbcIntf;
            this.jdbcObject = jdbcObject;
        }

        /**
         * Hook/factory method to allow context to provide fresh object for each
         * method.  Needed for Statement and PrepareStatement, whose execute...
         * methods can close the statement (at least given our minimal dummy
         * argument values).
         */
        protected INTF getJdbcObject() throws SQLException {
            return jdbcObject;
        }

        /**
         * Gets minimal value suitable for use as actual parameter value for given
         * formal parameter type.
         */
        private static Object getDummyValueForType(Class<?> type) {
            final Object result;
            if (type.equals(String.class)) {
                result = "";
            } else
                if (!(type.isPrimitive())) {
                    result = null;
                } else {
                    if (type == (boolean.class)) {
                        result = false;
                    } else
                        if (type == (byte.class)) {
                            result = ((byte) (0));
                        } else
                            if (type == (short.class)) {
                                result = ((short) (0));
                            } else
                                if (type == (int.class)) {
                                    result = 0;
                                } else
                                    if (type == (long.class)) {
                                        result = ((long) (0L));
                                    } else
                                        if (type == (float.class)) {
                                            result = 0.0F;
                                        } else
                                            if (type == (double.class)) {
                                                result = 0.0;
                                            } else {
                                                Assert.fail(("Test needs to be updated to handle type " + type));
                                                result = null;// Not executed; for "final".

                                            }






                }

            return result;
        }

        /**
         * Assembles method signature text for given method.
         */
        private String makeLabel(Method method) {
            String methodLabel;
            methodLabel = (((jdbcIntf.getSimpleName()) + ".") + (method.getName())) + "(";
            boolean first = true;
            for (Class<?> paramType : method.getParameterTypes()) {
                if (!first) {
                    methodLabel += ", ";
                }
                first = false;
                methodLabel += paramType.getSimpleName();
            }
            methodLabel += ")";
            return methodLabel;
        }

        /**
         * Assembles (minimal) arguments array for given method.
         */
        private Object[] makeArgs(Method method) {
            final List<Object> argsList = new ArrayList<>();
            for (Class<?> paramType : method.getParameterTypes()) {
                argsList.add(Drill2769UnsupportedReportsUseSqlExceptionTest.NoNonSqlExceptionsChecker.getDummyValueForType(paramType));
            }
            Object[] argsArray = argsList.toArray();
            return argsArray;
        }

        /**
         * Tests one method.
         * (Disturbs members set by makeArgsAndLabel, but those shouldn't be used
         * except by this method.)
         */
        private void testOneMethod(Method method) {
            final String methodLabel = makeLabel(method);
            try {
                final INTF jdbcObject;
                try {
                    jdbcObject = getJdbcObject();
                } catch (SQLException e) {
                    Assert.fail((("Unexpected exception: " + e) + " from getJdbcObject()"));
                    throw new RuntimeException("DUMMY; so compiler know block throws");
                }
                // See if method throws exception:
                method.invoke(jdbcObject, makeArgs(method));
                // If here, method didn't throw--check if it's an expected non-throwing
                // method (e.g., an isClosed).  (If not, report error.)
                final String resultLine = ("- " + methodLabel) + " didn\'t throw\n";
                successLinesBuf.append(resultLine);
            } catch (InvocationTargetException wrapperEx) {
                final Throwable cause = wrapperEx.getCause();
                final String resultLine = ((("- " + methodLabel) + " threw <") + cause) + ">\n";
                if ((SQLException.class.isAssignableFrom(cause.getClass())) && (!(AlreadyClosedSqlException.class.isAssignableFrom(cause.getClass())))) {
                    // Good case--almost any exception should be SQLException or subclass
                    // (but make sure not accidentally closed).
                    successLinesBuf.append(resultLine);
                } else
                    if (((NullPointerException.class) == (cause.getClass())) && ((method.getName().equals("isWrapperFor")) || (method.getName().equals("unwrap")))) {
                        // Known good-enough case--these methods throw NullPointerException
                        // because of the way we call them (with null) and the way Avatica
                        // code implements them.
                        successLinesBuf.append(resultLine);
                    } else
                        if (isOkaySpecialCaseException(method, cause)) {
                            successLinesBuf.append(resultLine);
                        } else {
                            final String badResultLine = (((((("- " + methodLabel) + " threw <") + cause) + "> instead") + " of a ") + (SQLException.class.getSimpleName())) + "\n";
                            Drill2769UnsupportedReportsUseSqlExceptionTest.logger.trace(("Failure: " + resultLine));
                            failureLinesBuf.append(badResultLine);
                        }


            } catch (IllegalAccessException | IllegalArgumentException e) {
                Assert.fail(((((("Unexpected exception: " + e) + ", cause = ") + (e.getCause())) + "  from ") + method));
            }
        }

        public void testMethods() {
            for (Method method : jdbcIntf.getMethods()) {
                final String methodLabel = makeLabel(method);
                if ("close".equals(method.getName())) {
                    Drill2769UnsupportedReportsUseSqlExceptionTest.logger.debug(("Skipping (because closes): " + methodLabel));
                } else /* Uncomment to suppress calling DatabaseMetaData.getColumns(...), which
                sometimes takes about 2 minutes, and other DatabaseMetaData methods
                that query, collectively taking a while too:
                else if (DatabaseMetaData.class == jdbcIntf
                && "getColumns".equals(method.getName())) {
                logger.debug("Skipping (because really slow): " + methodLabel);
                }
                else if (DatabaseMetaData.class == jdbcIntf
                && ResultSet.class == method.getReturnType()) {
                logger.debug("Skipping (because a bit slow): " + methodLabel);
                }
                 */
                {
                    Drill2769UnsupportedReportsUseSqlExceptionTest.logger.debug(("Testing method " + methodLabel));
                    testOneMethod(method);
                }
            }
        }

        /**
         * Reports whether it's okay if given method throw given exception (that is
         * not preferred AlreadyClosedException with regular message).
         */
        protected boolean isOkaySpecialCaseException(Method method, Throwable cause) {
            return false;
        }

        public boolean hadAnyFailures() {
            return 0 != (failureLinesBuf.length());
        }

        public String getFailureLines() {
            return failureLinesBuf.toString();
        }

        public String getSuccessLines() {
            return successLinesBuf.toString();
        }

        public String getReport() {
            final String report = ((("Failures:\n" + (getFailureLines())) + "(Successes:\n") + (getSuccessLines())) + ")";
            return report;
        }
    }

    @Test
    public void testConnectionMethodsThrowRight() {
        Drill2769UnsupportedReportsUseSqlExceptionTest.NoNonSqlExceptionsChecker<Connection> checker = new Drill2769UnsupportedReportsUseSqlExceptionTest.NoNonSqlExceptionsChecker<Connection>(Connection.class, Drill2769UnsupportedReportsUseSqlExceptionTest.connection);
        checker.testMethods();
        if (checker.hadAnyFailures()) {
            System.err.println(checker.getReport());
            Assert.fail(("Non-SQLException exception error(s): \n" + (checker.getReport())));
        }
    }

    private static class PlainStatementChecker extends Drill2769UnsupportedReportsUseSqlExceptionTest.NoNonSqlExceptionsChecker<Statement> {
        private final Connection factoryConnection;

        PlainStatementChecker(Connection factoryConnection) {
            super(Statement.class, null);
            this.factoryConnection = factoryConnection;
        }

        @Override
        protected Statement getJdbcObject() throws SQLException {
            return factoryConnection.createStatement();
        }

        @Override
        protected boolean isOkaySpecialCaseException(Method method, Throwable cause) {
            // New Java 8 method not supported by Avatica
            return method.getName().equals("executeLargeBatch");
        }
    }

    @Test
    public void testPlainStatementMethodsThrowRight() {
        Drill2769UnsupportedReportsUseSqlExceptionTest.NoNonSqlExceptionsChecker<Statement> checker = new Drill2769UnsupportedReportsUseSqlExceptionTest.PlainStatementChecker(Drill2769UnsupportedReportsUseSqlExceptionTest.connection);
        checker.testMethods();
        if (checker.hadAnyFailures()) {
            Assert.fail(("Non-SQLException exception error(s): \n" + (checker.getReport())));
        }
    }

    private static class PreparedStatementChecker extends Drill2769UnsupportedReportsUseSqlExceptionTest.NoNonSqlExceptionsChecker<PreparedStatement> {
        private final Connection factoryConnection;

        PreparedStatementChecker(Connection factoryConnection) {
            super(PreparedStatement.class, null);
            this.factoryConnection = factoryConnection;
        }

        @Override
        protected PreparedStatement getJdbcObject() throws SQLException {
            return factoryConnection.prepareStatement("VALUES 1");
        }

        @Override
        protected boolean isOkaySpecialCaseException(Method method, Throwable cause) {
            // New Java 8 method not supported by Avatica
            return method.getName().equals("executeLargeBatch");
        }
    }

    @Test
    public void testPreparedStatementMethodsThrowRight() {
        Drill2769UnsupportedReportsUseSqlExceptionTest.NoNonSqlExceptionsChecker<PreparedStatement> checker = new Drill2769UnsupportedReportsUseSqlExceptionTest.PreparedStatementChecker(Drill2769UnsupportedReportsUseSqlExceptionTest.connection);
        checker.testMethods();
        if (checker.hadAnyFailures()) {
            Assert.fail(("Non-SQLException exception error(s): \n" + (checker.getReport())));
        }
    }

    @Test
    public void testResultSetMethodsThrowRight() {
        Drill2769UnsupportedReportsUseSqlExceptionTest.NoNonSqlExceptionsChecker<ResultSet> checker = new Drill2769UnsupportedReportsUseSqlExceptionTest.NoNonSqlExceptionsChecker<ResultSet>(ResultSet.class, Drill2769UnsupportedReportsUseSqlExceptionTest.resultSet);
        checker.testMethods();
        if (checker.hadAnyFailures()) {
            Assert.fail(("Non-SQLException exception error(s): \n" + (checker.getReport())));
        }
    }

    @Test
    public void testResultSetMetaDataMethodsThrowRight() {
        Drill2769UnsupportedReportsUseSqlExceptionTest.NoNonSqlExceptionsChecker<ResultSetMetaData> checker = new Drill2769UnsupportedReportsUseSqlExceptionTest.NoNonSqlExceptionsChecker<ResultSetMetaData>(ResultSetMetaData.class, Drill2769UnsupportedReportsUseSqlExceptionTest.resultSetMetaData);
        checker.testMethods();
        if (checker.hadAnyFailures()) {
            Assert.fail(("Non-SQLException exception error(s): \n" + (checker.getReport())));
        }
    }

    @Test
    public void testDatabaseMetaDataMethodsThrowRight() {
        Drill2769UnsupportedReportsUseSqlExceptionTest.NoNonSqlExceptionsChecker<DatabaseMetaData> checker = new Drill2769UnsupportedReportsUseSqlExceptionTest.NoNonSqlExceptionsChecker<DatabaseMetaData>(DatabaseMetaData.class, Drill2769UnsupportedReportsUseSqlExceptionTest.databaseMetaData);
        checker.testMethods();
        if (checker.hadAnyFailures()) {
            Assert.fail(("Non-SQLException exception error(s): \n" + (checker.getReport())));
        }
    }
}

