/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.agera.database;


import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import com.google.android.agera.Function;
import com.google.android.agera.Result;
import com.google.android.agera.Supplier;
import com.google.android.agera.Suppliers;
import com.google.android.agera.database.test.matchers.HasPrivateConstructor;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = NONE)
public final class SqlDatabaseFunctionsTest {
    private static final String SELECT_TABLE = "SELECT * FROM test";

    private static final String HAS_VALUE = "SELECT * FROM test WHERE column='value'";

    private static final String TABLE = "test";

    private static final String INVALID_TABLE = "test invalid$";

    private static final String SQL_QUERY = "SELECT * FROM test ORDER BY column";

    private static final String INVALID_QUERY = "invalid query";

    private static final String SQL_QUERY_FOR_ARGUMENT = "SELECT * FROM test WHERE column=?";

    private static final String NON_MATCHING_SQL_QUERY = "SELECT * FROM test WHERE column='a' ORDER BY column";

    private static final Supplier<Result<SQLiteDatabase>> FAILURE = Suppliers.staticSupplier(Result.<SQLiteDatabase>failure(new Exception()));

    private static final SqlDatabaseFunctionsTest.CursorStringFunction CURSOR_STRING_FUNCTION = new SqlDatabaseFunctionsTest.CursorStringFunction();

    public static final String COLUMN = "column";

    private SQLiteDatabase database;

    private Supplier<Result<SQLiteDatabase>> databaseSupplier;

    @Test
    public void shouldGetValuesForDatabaseQuery() throws Throwable {
        MatcherAssert.assertThat(SqlDatabaseFunctions.databaseQueryFunction(databaseSupplier, SqlDatabaseFunctionsTest.CURSOR_STRING_FUNCTION).apply(SqlRequests.sqlRequest().sql(SqlDatabaseFunctionsTest.SQL_QUERY).compile()).get(), Matchers.contains("value1", "value2", "value3"));
    }

    @Test
    public void shouldNotGetValuesForDatabaseQueryWithNonMatchingWhere() throws Throwable {
        MatcherAssert.assertThat(SqlDatabaseFunctions.databaseQueryFunction(databaseSupplier, SqlDatabaseFunctionsTest.CURSOR_STRING_FUNCTION).apply(SqlRequests.sqlRequest().sql(SqlDatabaseFunctionsTest.NON_MATCHING_SQL_QUERY).compile()).get(), Matchers.empty());
    }

    @Test
    public void shouldReturnFailureForInvalidQuery() {
        MatcherAssert.assertThat(SqlDatabaseFunctions.databaseQueryFunction(databaseSupplier, SqlDatabaseFunctionsTest.CURSOR_STRING_FUNCTION).apply(SqlRequests.sqlRequest().sql(SqlDatabaseFunctionsTest.INVALID_QUERY).compile()).getFailure(), Matchers.instanceOf(SQLException.class));
    }

    @Test
    public void shouldPassArgumentsToDatabaseQuery() throws Throwable {
        MatcherAssert.assertThat(SqlDatabaseFunctions.databaseQueryFunction(databaseSupplier, SqlDatabaseFunctionsTest.CURSOR_STRING_FUNCTION).apply(SqlRequests.sqlRequest().sql(SqlDatabaseFunctionsTest.SQL_QUERY_FOR_ARGUMENT).arguments("value2").compile()).get(), Matchers.contains("value2"));
    }

    @Test
    public void shouldReturnErrorForFailedDatabaseCreationInQuery() throws Throwable {
        MatcherAssert.assertThat(SqlDatabaseFunctions.databaseQueryFunction(SqlDatabaseFunctionsTest.FAILURE, SqlDatabaseFunctionsTest.CURSOR_STRING_FUNCTION).apply(SqlRequests.sqlRequest().sql(SqlDatabaseFunctionsTest.SQL_QUERY_FOR_ARGUMENT).arguments("value2").compile()).failed(), Is.is(true));
    }

    @Test
    public void shouldClearTableForDeleteWithoutArguments() throws Throwable {
        MatcherAssert.assertThat(SqlDatabaseFunctions.databaseDeleteFunction(databaseSupplier).apply(SqlRequests.sqlDeleteRequest().table(SqlDatabaseFunctionsTest.TABLE).compile()).get(), Is.is(3));
        assertDatabaseEmpty();
    }

    @Test
    public void shouldReturnFailureForInvalidDelete() {
        MatcherAssert.assertThat(SqlDatabaseFunctions.databaseDeleteFunction(databaseSupplier).apply(SqlRequests.sqlDeleteRequest().table(SqlDatabaseFunctionsTest.INVALID_TABLE).compile()).getFailure(), Matchers.instanceOf(SQLException.class));
    }

    @Test
    public void shouldPassArgumentsToDatabaseDelete() throws Throwable {
        MatcherAssert.assertThat(SqlDatabaseFunctions.databaseDeleteFunction(databaseSupplier).apply(SqlRequests.sqlDeleteRequest().table(SqlDatabaseFunctionsTest.TABLE).where("column=?").arguments("value2").compile()).get(), Is.is(1));
    }

    @Test
    public void shouldReturnErrorForFailedDatabaseCreationInDelete() throws Throwable {
        MatcherAssert.assertThat(SqlDatabaseFunctions.databaseDeleteFunction(SqlDatabaseFunctionsTest.FAILURE).apply(SqlRequests.sqlDeleteRequest().table(SqlDatabaseFunctionsTest.TABLE).where("column=?").arguments("value2").compile()).failed(), Is.is(true));
    }

    @Test
    public void shouldUpdateTableForUpdateWithoutArguments() throws Throwable {
        MatcherAssert.assertThat(SqlDatabaseFunctions.databaseUpdateFunction(databaseSupplier).apply(SqlRequests.sqlUpdateRequest().table(SqlDatabaseFunctionsTest.TABLE).column("column", "value4").compile()).get(), Is.is(3));
    }

    @Test
    public void shouldReturnFailureForInvalidUpdate() {
        MatcherAssert.assertThat(SqlDatabaseFunctions.databaseUpdateFunction(databaseSupplier).apply(SqlRequests.sqlUpdateRequest().table(SqlDatabaseFunctionsTest.INVALID_TABLE).emptyColumn("column").compile()).getFailure(), Matchers.instanceOf(SQLException.class));
    }

    @Test
    public void shouldPassArgumentsToDatabaseUpdate() throws Throwable {
        MatcherAssert.assertThat(SqlDatabaseFunctions.databaseUpdateFunction(databaseSupplier).apply(SqlRequests.sqlUpdateRequest().table(SqlDatabaseFunctionsTest.TABLE).column("column", "value4").where("column=?").arguments("value3").compile()).get(), Is.is(1));
    }

    @Test
    public void shouldReturnErrorForFailedDatabaseCreationInUpdate() throws Throwable {
        MatcherAssert.assertThat(SqlDatabaseFunctions.databaseUpdateFunction(SqlDatabaseFunctionsTest.FAILURE).apply(SqlRequests.sqlUpdateRequest().table(SqlDatabaseFunctionsTest.TABLE).column("column", "value4").where("column=?").arguments("value3").compile()).failed(), Is.is(true));
    }

    @Test
    public void shouldReturnFailureForInvalidInsert() {
        MatcherAssert.assertThat(SqlDatabaseFunctions.databaseInsertFunction(databaseSupplier).apply(SqlRequests.sqlInsertRequest().table(SqlDatabaseFunctionsTest.INVALID_TABLE).emptyColumn("column").compile()).getFailure(), Matchers.instanceOf(SQLException.class));
    }

    @Test
    public void shouldPassArgumentsToDatabaseInsert() throws Throwable {
        MatcherAssert.assertThat(SqlDatabaseFunctions.databaseInsertFunction(databaseSupplier).apply(SqlRequests.sqlInsertRequest().table(SqlDatabaseFunctionsTest.TABLE).column("column", "value").compile()).succeeded(), Is.is(true));
        assertDatabaseContainsValue();
    }

    @Test
    public void shouldAddFailConflictAlgorithmForUpdate() {
        MatcherAssert.assertThat(SqlRequests.sqlUpdateRequest().table(SqlDatabaseFunctionsTest.TABLE).column("column", "value4").where("column=?").arguments("value3").failOnConflict().compile().conflictAlgorithm, Is.is(CONFLICT_FAIL));
    }

    @Test
    public void shouldAddReplaceConflictAlgorithmForUpdate() {
        MatcherAssert.assertThat(SqlRequests.sqlUpdateRequest().table(SqlDatabaseFunctionsTest.TABLE).column("column", "value4").where("column=?").arguments("value3").replaceOnConflict().compile().conflictAlgorithm, Is.is(CONFLICT_REPLACE));
    }

    @Test
    public void shouldAddIgnoreConflictAlgorithmForUpdate() {
        MatcherAssert.assertThat(SqlRequests.sqlUpdateRequest().table(SqlDatabaseFunctionsTest.TABLE).column("column", "value4").where("column=?").arguments("value3").ignoreOnConflict().compile().conflictAlgorithm, Is.is(CONFLICT_IGNORE));
    }

    @Test
    public void shouldNotAddConflictAlgorithmForUpdate() {
        MatcherAssert.assertThat(SqlRequests.sqlUpdateRequest().table(SqlDatabaseFunctionsTest.TABLE).column("column", "value4").where("column=?").arguments("value3").compile().conflictAlgorithm, Is.is(CONFLICT_NONE));
    }

    @Test
    public void shouldNotAddConflictAlgorithmForInsert() {
        MatcherAssert.assertThat(SqlRequests.sqlInsertRequest().table(SqlDatabaseFunctionsTest.TABLE).emptyColumn("column").compile().conflictAlgorithm, Is.is(CONFLICT_NONE));
    }

    @Test
    public void shouldAddFailConflictAlgorithmForInsert() {
        MatcherAssert.assertThat(SqlRequests.sqlInsertRequest().table(SqlDatabaseFunctionsTest.TABLE).emptyColumn("column").failOnConflict().compile().conflictAlgorithm, Is.is(CONFLICT_FAIL));
    }

    @Test
    public void shouldAddIgnoreConflictAlgorithmForInsert() {
        MatcherAssert.assertThat(SqlRequests.sqlInsertRequest().table(SqlDatabaseFunctionsTest.TABLE).emptyColumn("column").ignoreOnConflict().compile().conflictAlgorithm, Is.is(CONFLICT_IGNORE));
    }

    @Test
    public void shouldAddReplaceConflictAlgorithmForInsert() {
        MatcherAssert.assertThat(SqlRequests.sqlInsertRequest().table(SqlDatabaseFunctionsTest.TABLE).emptyColumn("column").replaceOnConflict().compile().conflictAlgorithm, Is.is(CONFLICT_REPLACE));
    }

    @Test
    public void shouldReturnErrorForFailedDatabaseCreationInInsert() throws Throwable {
        MatcherAssert.assertThat(SqlDatabaseFunctions.databaseInsertFunction(SqlDatabaseFunctionsTest.FAILURE).apply(SqlRequests.sqlInsertRequest().table(SqlDatabaseFunctionsTest.TABLE).column("column", "value").compile()).failed(), Is.is(true));
    }

    @Test
    public void shouldAddBooleanColumnForInsert() {
        final boolean value = true;
        MatcherAssert.assertThat(SqlRequests.sqlInsertRequest().table(SqlDatabaseFunctionsTest.TABLE).column(SqlDatabaseFunctionsTest.COLUMN, value).compile().contentValues.getAsBoolean(SqlDatabaseFunctionsTest.COLUMN), Is.is(value));
    }

    @Test
    public void shouldAddNullBooleanColumnForInsert() {
        final Boolean nullValue = null;
        MatcherAssert.assertThat(SqlRequests.sqlInsertRequest().table(SqlDatabaseFunctionsTest.TABLE).column(SqlDatabaseFunctionsTest.COLUMN, nullValue).compile().contentValues.getAsBoolean(SqlDatabaseFunctionsTest.COLUMN), Is.is(nullValue));
    }

    @Test
    public void shouldAddStringColumnForInsert() {
        final String value = "string";
        MatcherAssert.assertThat(SqlRequests.sqlInsertRequest().table(SqlDatabaseFunctionsTest.TABLE).column(SqlDatabaseFunctionsTest.COLUMN, value).compile().contentValues.getAsString(SqlDatabaseFunctionsTest.COLUMN), Is.is(value));
    }

    @Test
    public void shouldAddNullStringColumnForInsert() {
        final String nullValue = null;
        MatcherAssert.assertThat(SqlRequests.sqlInsertRequest().table(SqlDatabaseFunctionsTest.TABLE).column(SqlDatabaseFunctionsTest.COLUMN, nullValue).compile().contentValues.getAsString(SqlDatabaseFunctionsTest.COLUMN), Is.is(nullValue));
    }

    @Test
    public void shouldAddByteColumnForInsert() {
        final byte value = 2;
        MatcherAssert.assertThat(SqlRequests.sqlInsertRequest().table(SqlDatabaseFunctionsTest.TABLE).column(SqlDatabaseFunctionsTest.COLUMN, value).compile().contentValues.getAsByte(SqlDatabaseFunctionsTest.COLUMN), Is.is(value));
    }

    @Test
    public void shouldAddNullByteColumnForInsert() {
        final Byte nullValue = null;
        MatcherAssert.assertThat(SqlRequests.sqlInsertRequest().table(SqlDatabaseFunctionsTest.TABLE).column(SqlDatabaseFunctionsTest.COLUMN, nullValue).compile().contentValues.getAsByte(SqlDatabaseFunctionsTest.COLUMN), Is.is(nullValue));
    }

    @Test
    public void shouldAddIntegerColumnForInsert() {
        final int value = 2;
        MatcherAssert.assertThat(SqlRequests.sqlInsertRequest().table(SqlDatabaseFunctionsTest.TABLE).column(SqlDatabaseFunctionsTest.COLUMN, value).compile().contentValues.getAsInteger(SqlDatabaseFunctionsTest.COLUMN), Is.is(value));
    }

    @Test
    public void shouldAddNullIntegerColumnForInsert() {
        final Integer nullValue = null;
        MatcherAssert.assertThat(SqlRequests.sqlInsertRequest().table(SqlDatabaseFunctionsTest.TABLE).column(SqlDatabaseFunctionsTest.COLUMN, nullValue).compile().contentValues.getAsInteger(SqlDatabaseFunctionsTest.COLUMN), Is.is(nullValue));
    }

    @Test
    public void shouldAddShortColumnForInsert() {
        final short value = 2;
        MatcherAssert.assertThat(SqlRequests.sqlInsertRequest().table(SqlDatabaseFunctionsTest.TABLE).column(SqlDatabaseFunctionsTest.COLUMN, value).compile().contentValues.getAsShort(SqlDatabaseFunctionsTest.COLUMN), Is.is(value));
    }

    @Test
    public void shouldAddNullShortColumnForInsert() {
        final Short nullValue = null;
        MatcherAssert.assertThat(SqlRequests.sqlInsertRequest().table(SqlDatabaseFunctionsTest.TABLE).column(SqlDatabaseFunctionsTest.COLUMN, nullValue).compile().contentValues.getAsShort(SqlDatabaseFunctionsTest.COLUMN), Is.is(nullValue));
    }

    @Test
    public void shouldAddDoubleColumnForInsert() {
        final double value = 2;
        MatcherAssert.assertThat(SqlRequests.sqlInsertRequest().table(SqlDatabaseFunctionsTest.TABLE).column(SqlDatabaseFunctionsTest.COLUMN, value).compile().contentValues.getAsDouble(SqlDatabaseFunctionsTest.COLUMN), Is.is(value));
    }

    @Test
    public void shouldAddNullDoubleColumnForInsert() {
        final Double nullValue = null;
        MatcherAssert.assertThat(SqlRequests.sqlInsertRequest().table(SqlDatabaseFunctionsTest.TABLE).column(SqlDatabaseFunctionsTest.COLUMN, nullValue).compile().contentValues.getAsDouble(SqlDatabaseFunctionsTest.COLUMN), Is.is(nullValue));
    }

    @Test
    public void shouldAddFloatColumnForInsert() {
        final float value = 2;
        MatcherAssert.assertThat(SqlRequests.sqlInsertRequest().table(SqlDatabaseFunctionsTest.TABLE).column(SqlDatabaseFunctionsTest.COLUMN, value).compile().contentValues.getAsFloat(SqlDatabaseFunctionsTest.COLUMN), Is.is(value));
    }

    @Test
    public void shouldAddNullFloatColumnForInsert() {
        final Float nullValue = null;
        MatcherAssert.assertThat(SqlRequests.sqlInsertRequest().table(SqlDatabaseFunctionsTest.TABLE).column(SqlDatabaseFunctionsTest.COLUMN, nullValue).compile().contentValues.getAsFloat(SqlDatabaseFunctionsTest.COLUMN), Is.is(nullValue));
    }

    @Test
    public void shouldAddLongColumnForInsert() {
        final long value = 2;
        MatcherAssert.assertThat(SqlRequests.sqlInsertRequest().table(SqlDatabaseFunctionsTest.TABLE).column(SqlDatabaseFunctionsTest.COLUMN, value).compile().contentValues.getAsLong(SqlDatabaseFunctionsTest.COLUMN), Is.is(value));
    }

    @Test
    public void shouldAddNullLongColumnForInsert() {
        final Long nullValue = null;
        MatcherAssert.assertThat(SqlRequests.sqlInsertRequest().table(SqlDatabaseFunctionsTest.TABLE).column(SqlDatabaseFunctionsTest.COLUMN, nullValue).compile().contentValues.getAsLong(SqlDatabaseFunctionsTest.COLUMN), Is.is(nullValue));
    }

    @Test
    public void shouldAddByteArrayColumnForInsert() {
        final byte[] value = "value".getBytes();
        MatcherAssert.assertThat(SqlRequests.sqlInsertRequest().table(SqlDatabaseFunctionsTest.TABLE).column(SqlDatabaseFunctionsTest.COLUMN, value).compile().contentValues.getAsByteArray(SqlDatabaseFunctionsTest.COLUMN), Is.is(value));
    }

    @Test
    public void shouldAddNullByteArrayColumnForInsert() {
        final byte[] nullValue = null;
        MatcherAssert.assertThat(SqlRequests.sqlInsertRequest().table(SqlDatabaseFunctionsTest.TABLE).column(SqlDatabaseFunctionsTest.COLUMN, nullValue).compile().contentValues.getAsByteArray(SqlDatabaseFunctionsTest.COLUMN), Is.is(nullValue));
    }

    @Test
    public void shouldHavePrivateConstructor() {
        MatcherAssert.assertThat(SqlDatabaseFunctions.class, HasPrivateConstructor.hasPrivateConstructor());
    }

    private static class CursorStringFunction implements Function<Cursor, String> {
        @NonNull
        @Override
        public String apply(@NonNull
        final Cursor input) {
            return input.getString(input.getColumnIndex("column"));
        }
    }
}

