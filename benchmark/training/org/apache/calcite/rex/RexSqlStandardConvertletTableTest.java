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
package org.apache.calcite.rex;


import org.apache.calcite.rel.core.Project;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.test.SqlToRelTestBase;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test for {@link org.apache.calcite.rex.RexSqlStandardConvertletTable}.
 */
public class RexSqlStandardConvertletTableTest extends SqlToRelTestBase {
    @Test
    public void testCoalesce() {
        final Project project = ((Project) (convertSqlToRel("SELECT COALESCE(NULL, 'a')", false)));
        final RexNode rex = project.getChildExps().get(0);
        final RexToSqlNodeConverter rexToSqlNodeConverter = RexSqlStandardConvertletTableTest.rexToSqlNodeConverter();
        final SqlNode convertedSql = rexToSqlNodeConverter.convertNode(rex);
        Assert.assertEquals("CASE WHEN NULL IS NOT NULL THEN NULL ELSE 'a' END", convertedSql.toString());
    }

    @Test
    public void testCaseWithValue() {
        final Project project = ((Project) (convertSqlToRel("SELECT CASE NULL WHEN NULL THEN NULL ELSE 'a' END", false)));
        final RexNode rex = project.getChildExps().get(0);
        final RexToSqlNodeConverter rexToSqlNodeConverter = RexSqlStandardConvertletTableTest.rexToSqlNodeConverter();
        final SqlNode convertedSql = rexToSqlNodeConverter.convertNode(rex);
        Assert.assertEquals("CASE WHEN NULL = NULL THEN NULL ELSE 'a' END", convertedSql.toString());
    }

    @Test
    public void testCaseNoValue() {
        final Project project = ((Project) (convertSqlToRel("SELECT CASE WHEN NULL IS NULL THEN NULL ELSE 'a' END", false)));
        final RexNode rex = project.getChildExps().get(0);
        final RexToSqlNodeConverter rexToSqlNodeConverter = RexSqlStandardConvertletTableTest.rexToSqlNodeConverter();
        final SqlNode convertedSql = rexToSqlNodeConverter.convertNode(rex);
        Assert.assertEquals("CASE WHEN NULL IS NULL THEN NULL ELSE 'a' END", convertedSql.toString());
    }
}

/**
 * End RexSqlStandardConvertletTableTest.java
 */
