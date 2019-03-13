/**
 * Copyright 2015, The Querydsl Team (http://www.querydsl.com/team)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.querydsl.sql.mssql;


import com.querydsl.sql.Configuration;
import com.querydsl.sql.Constants;
import com.querydsl.sql.SQLExpressions;
import com.querydsl.sql.SQLTemplates;
import com.querydsl.sql.WindowFunction;
import com.querydsl.sql.domain.QEmployee;
import org.junit.Assert;
import org.junit.Test;


public class WindowFunctionTest {
    private static final Configuration configuration = new Configuration(SQLTemplates.DEFAULT);

    // ROW_NUMBER() OVER (ORDER BY OrderDate) AS 'RowNumber'
    // ROW_NUMBER() OVER (PARTITION BY PostalCode ORDER BY SalesYTD DESC)
    @Test
    public void mutable() {
        WindowFunction<Long> rn = SQLExpressions.rowNumber().over().orderBy(Constants.employee.firstname);
        Assert.assertEquals("row_number() over (order by e.FIRSTNAME asc)", WindowFunctionTest.toString(rn));
        Assert.assertEquals("row_number() over (order by e.FIRSTNAME asc, e.LASTNAME asc)", WindowFunctionTest.toString(rn.orderBy(Constants.employee.lastname)));
    }

    @Test
    public void orderBy() {
        Assert.assertEquals("row_number() over (order by e.FIRSTNAME asc)", WindowFunctionTest.toString(SQLExpressions.rowNumber().over().orderBy(Constants.employee.firstname.asc())));
        Assert.assertEquals("row_number() over (order by e.FIRSTNAME asc)", WindowFunctionTest.toString(SQLExpressions.rowNumber().over().orderBy(Constants.employee.firstname)));
        Assert.assertEquals("row_number() over (order by e.FIRSTNAME asc) as rn", WindowFunctionTest.toString(SQLExpressions.rowNumber().over().orderBy(Constants.employee.firstname.asc()).as("rn")));
        Assert.assertEquals("row_number() over (order by e.FIRSTNAME desc)", WindowFunctionTest.toString(SQLExpressions.rowNumber().over().orderBy(Constants.employee.firstname.desc())));
    }

    @Test
    public void partitionBy() {
        Assert.assertEquals("row_number() over (partition by e.LASTNAME order by e.FIRSTNAME asc)", WindowFunctionTest.toString(SQLExpressions.rowNumber().over().partitionBy(Constants.employee.lastname).orderBy(Constants.employee.firstname.asc())));
        Assert.assertEquals("row_number() over (partition by e.LASTNAME, e.FIRSTNAME order by e.FIRSTNAME asc)", WindowFunctionTest.toString(SQLExpressions.rowNumber().over().partitionBy(Constants.employee.lastname, Constants.employee.firstname).orderBy(Constants.employee.firstname.asc())));
    }
}

