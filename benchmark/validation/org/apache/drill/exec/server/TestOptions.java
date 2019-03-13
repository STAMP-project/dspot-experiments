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
package org.apache.drill.exec.server;


import ExecConstants.SLICE_TARGET_DEFAULT;
import org.apache.drill.categories.OptionsTest;
import org.apache.drill.exec.ExecConstants;
import org.apache.drill.exec.proto.UserBitShared.DrillPBError.ErrorType;
import org.apache.drill.test.BaseTestQuery;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(OptionsTest.class)
public class TestOptions extends BaseTestQuery {
    // static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(TestOptions.class);
    @Test
    public void testDrillbits() throws Exception {
        BaseTestQuery.test("select * from sys.drillbits;");
    }

    @Test
    public void testOptions() throws Exception {
        BaseTestQuery.test(("select * from sys.options;" + ((("ALTER SYSTEM set `planner.disable_exchanges` = true;" + "select * from sys.options;") + "ALTER SESSION set `planner.disable_exchanges` = true;") + "select * from sys.options;")));
    }

    @Test
    public void checkValidationException() throws Exception {
        thrownException.expect(new org.apache.drill.test.UserExceptionMatcher(ErrorType.VALIDATION));
        BaseTestQuery.test("ALTER session SET %s = '%s';", ExecConstants.SLICE_TARGET, "fail");
    }

    // DRILL-3122
    @Test
    public void checkChangedColumn() throws Exception {
        BaseTestQuery.test("ALTER session SET `%s` = %d;", ExecConstants.SLICE_TARGET, SLICE_TARGET_DEFAULT);
        BaseTestQuery.testBuilder().sqlQuery("SELECT status FROM sys.options WHERE name = '%s' AND optionScope = 'SESSION'", ExecConstants.SLICE_TARGET).unOrdered().baselineColumns("status").baselineValues("DEFAULT").build().run();
    }

    @Test
    public void setAndResetSessionOption() throws Exception {
        // check unchanged
        BaseTestQuery.testBuilder().sqlQuery("SELECT status FROM sys.options WHERE name = '%s' AND optionScope = 'SESSION'", ExecConstants.SLICE_TARGET).unOrdered().expectsEmptyResultSet().build().run();
        // change option
        BaseTestQuery.test("SET `%s` = %d;", ExecConstants.SLICE_TARGET, 10);
        // check changed
        BaseTestQuery.test("SELECT status, accessibleScopes, name FROM sys.options WHERE optionScope = 'SESSION';");
        BaseTestQuery.testBuilder().sqlQuery("SELECT val FROM sys.options WHERE name = '%s' AND optionScope = 'SESSION'", ExecConstants.SLICE_TARGET).unOrdered().baselineColumns("val").baselineValues(String.valueOf(10L)).build().run();
        // reset option
        BaseTestQuery.test("RESET `%s`;", ExecConstants.SLICE_TARGET);
        // check reverted
        BaseTestQuery.testBuilder().sqlQuery("SELECT status FROM sys.options WHERE name = '%s' AND optionScope = 'SESSION'", ExecConstants.SLICE_TARGET).unOrdered().expectsEmptyResultSet().build().run();
    }

    @Test
    public void setAndResetSystemOption() throws Exception {
        // check unchanged
        BaseTestQuery.testBuilder().sqlQuery("SELECT status FROM sys.options WHERE name = '%s' AND optionScope = 'BOOT'", ExecConstants.ENABLE_VERBOSE_ERRORS_KEY).unOrdered().baselineColumns("status").baselineValues("DEFAULT").build().run();
        // change option
        BaseTestQuery.test("ALTER system SET `%s` = %b;", ExecConstants.ENABLE_VERBOSE_ERRORS_KEY, true);
        // check changed
        BaseTestQuery.testBuilder().sqlQuery("SELECT val FROM sys.options WHERE name = '%s' AND optionScope = 'SYSTEM'", ExecConstants.ENABLE_VERBOSE_ERRORS_KEY).unOrdered().baselineColumns("val").baselineValues(String.valueOf(true)).build().run();
        // reset option
        BaseTestQuery.test("ALTER system RESET `%s`;", ExecConstants.ENABLE_VERBOSE_ERRORS_KEY);
        // check reverted
        BaseTestQuery.testBuilder().sqlQuery("SELECT status FROM sys.options WHERE name = '%s' AND optionScope = 'BOOT'", ExecConstants.ENABLE_VERBOSE_ERRORS_KEY).unOrdered().baselineColumns("status").baselineValues("DEFAULT").build().run();
    }

    @Test
    public void resetAllSessionOptions() throws Exception {
        // change options
        BaseTestQuery.test("SET `%s` = %b;", ExecConstants.ENABLE_VERBOSE_ERRORS_KEY, true);
        // check changed
        BaseTestQuery.testBuilder().sqlQuery("SELECT val FROM sys.options WHERE optionScope = 'SESSION' AND name = '%s'", ExecConstants.ENABLE_VERBOSE_ERRORS_KEY).unOrdered().baselineColumns("val").baselineValues(String.valueOf(true)).build().run();
        // reset all options
        BaseTestQuery.test("RESET ALL;");
        // check no session options changed
        BaseTestQuery.testBuilder().sqlQuery("SELECT status FROM sys.options WHERE status <> 'DEFAULT' AND optionScope = 'SESSION'").unOrdered().expectsEmptyResultSet().build().run();
    }

    @Test
    public void changeSessionAndSystemButRevertSession() throws Exception {
        // change options
        BaseTestQuery.test("ALTER SESSION SET `%s` = %b;", ExecConstants.ENABLE_VERBOSE_ERRORS_KEY, true);
        BaseTestQuery.test("ALTER SYSTEM SET `%s` = %b;", ExecConstants.ENABLE_VERBOSE_ERRORS_KEY, true);
        // check changed
        BaseTestQuery.testBuilder().sqlQuery("SELECT bool_val FROM sys.options_old WHERE optionScope = 'SESSION' AND name = '%s'", ExecConstants.ENABLE_VERBOSE_ERRORS_KEY).unOrdered().baselineColumns("bool_val").baselineValues(true).build().run();
        // check changed
        BaseTestQuery.testBuilder().sqlQuery("SELECT bool_val FROM sys.options_old WHERE optionScope = 'SYSTEM' AND name = '%s'", ExecConstants.ENABLE_VERBOSE_ERRORS_KEY).unOrdered().baselineColumns("bool_val").baselineValues(true).build().run();
        // check changed new table
        BaseTestQuery.testBuilder().sqlQuery("SELECT val FROM sys.options WHERE optionScope = 'SESSION' AND name = '%s'", ExecConstants.ENABLE_VERBOSE_ERRORS_KEY).unOrdered().baselineColumns("val").baselineValues(String.valueOf(true)).build().run();
        // reset session option
        BaseTestQuery.test("RESET `%s`;", ExecConstants.ENABLE_VERBOSE_ERRORS_KEY);
        // check reverted
        BaseTestQuery.testBuilder().sqlQuery("SELECT status FROM sys.options WHERE name = '%s' AND optionScope = 'SESSION'", ExecConstants.ENABLE_VERBOSE_ERRORS_KEY).unOrdered().expectsEmptyResultSet().build().run();
        // check unchanged
        BaseTestQuery.testBuilder().sqlQuery("SELECT val FROM sys.options WHERE optionScope = 'SYSTEM' AND name = '%s'", ExecConstants.ENABLE_VERBOSE_ERRORS_KEY).unOrdered().baselineColumns("val").baselineValues(String.valueOf(true)).build().run();
        // reset system option
        BaseTestQuery.test("ALTER SYSTEM RESET `%s`;", ExecConstants.ENABLE_VERBOSE_ERRORS_KEY);
    }

    @Test
    public void changeSessionAndNotSystem() throws Exception {
        // change options
        BaseTestQuery.test("ALTER SESSION SET `%s` = %b;", ExecConstants.ENABLE_VERBOSE_ERRORS_KEY, true);
        BaseTestQuery.test("ALTER SYSTEM SET `%s` = %b;", ExecConstants.ENABLE_VERBOSE_ERRORS_KEY, true);
        // check changed
        BaseTestQuery.testBuilder().sqlQuery("SELECT bool_val FROM sys.options_old WHERE optionScope = 'SESSION' AND name = '%s'", ExecConstants.ENABLE_VERBOSE_ERRORS_KEY).unOrdered().baselineColumns("bool_val").baselineValues(true).build().run();
        // check changed
        BaseTestQuery.testBuilder().sqlQuery("SELECT bool_val FROM sys.options_old WHERE optionScope = 'SYSTEM' AND name = '%s'", ExecConstants.ENABLE_VERBOSE_ERRORS_KEY).unOrdered().baselineColumns("bool_val").baselineValues(true).build().run();
        // check changed for new table
        BaseTestQuery.testBuilder().sqlQuery("SELECT val FROM sys.options WHERE optionScope = 'SESSION' AND name = '%s'", ExecConstants.ENABLE_VERBOSE_ERRORS_KEY).unOrdered().baselineColumns("val").baselineValues(String.valueOf(true)).build().run();
        // reset all session options
        BaseTestQuery.test("ALTER SESSION RESET ALL;");
        // check no session options changed
        BaseTestQuery.testBuilder().sqlQuery("SELECT status FROM sys.options WHERE status <> 'DEFAULT' AND optionScope = 'SESSION'").unOrdered().expectsEmptyResultSet().build().run();
        // check changed
        BaseTestQuery.testBuilder().sqlQuery("SELECT val FROM sys.options WHERE optionScope = 'SYSTEM' AND name = '%s'", ExecConstants.ENABLE_VERBOSE_ERRORS_KEY).unOrdered().baselineColumns("val").baselineValues(String.valueOf(true)).build().run();
    }

    @Test
    public void changeSystemAndNotSession() throws Exception {
        // change options
        BaseTestQuery.test("ALTER SESSION SET `%s` = %b;", ExecConstants.ENABLE_VERBOSE_ERRORS_KEY, true);
        BaseTestQuery.test("ALTER SYSTEM SET `%s` = %b;", ExecConstants.ENABLE_VERBOSE_ERRORS_KEY, true);
        // check changed
        BaseTestQuery.testBuilder().sqlQuery("SELECT bool_val FROM sys.options_old WHERE optionScope = 'SESSION' AND name = '%s'", ExecConstants.ENABLE_VERBOSE_ERRORS_KEY).unOrdered().baselineColumns("bool_val").baselineValues(true).build().run();
        // check changed
        BaseTestQuery.testBuilder().sqlQuery("SELECT bool_val FROM sys.options_old WHERE optionScope = 'SYSTEM' AND name = '%s'", ExecConstants.ENABLE_VERBOSE_ERRORS_KEY).unOrdered().baselineColumns("bool_val").baselineValues(true).build().run();
        // check changed in new table
        BaseTestQuery.testBuilder().sqlQuery("SELECT val FROM sys.options WHERE optionScope = 'SESSION' AND name = '%s'", ExecConstants.ENABLE_VERBOSE_ERRORS_KEY).unOrdered().baselineColumns("val").baselineValues(String.valueOf(true)).build().run();
        // reset option
        BaseTestQuery.test("ALTER system RESET `%s`;", ExecConstants.ENABLE_VERBOSE_ERRORS_KEY);
        // check reverted
        BaseTestQuery.testBuilder().sqlQuery("SELECT status FROM sys.options_old WHERE optionScope = 'BOOT' AND name = '%s'", ExecConstants.ENABLE_VERBOSE_ERRORS_KEY).unOrdered().baselineColumns("status").baselineValues("DEFAULT").build().run();
        // check changed
        BaseTestQuery.testBuilder().sqlQuery("SELECT val FROM sys.options WHERE optionScope = 'SESSION' AND name = '%s'", ExecConstants.ENABLE_VERBOSE_ERRORS_KEY).unOrdered().baselineColumns("val").baselineValues(String.valueOf(true)).build().run();
    }

    @Test
    public void unsupportedLiteralValidation() throws Exception {
        thrownException.expect(new org.apache.drill.test.UserExceptionMatcher(ErrorType.VALIDATION, "Drill doesn't support assigning literals of type"));
        BaseTestQuery.test("ALTER session SET `%s` = DATE '1995-01-01';", ExecConstants.ENABLE_VERBOSE_ERRORS_KEY);
    }
}

