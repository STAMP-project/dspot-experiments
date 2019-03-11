/**
 * Copyright 2019 Google LLC.  All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.examples.bigtable;


import com.google.cloud.bigtable.admin.v2.BigtableTableAdminClient;
import com.google.cloud.bigtable.admin.v2.models.ColumnFamily;
import com.google.cloud.bigtable.admin.v2.models.GCRules.DurationRule;
import com.google.cloud.bigtable.admin.v2.models.GCRules.GCRule;
import com.google.cloud.bigtable.admin.v2.models.GCRules.IntersectionRule;
import com.google.cloud.bigtable.admin.v2.models.GCRules.UnionRule;
import com.google.cloud.bigtable.admin.v2.models.GCRules.VersionRule;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;


/**
 * Integration tests for {@link TableAdminExample}
 */
public class ITTableAdminExample {
    private static final String PROJECT_PROPERTY_NAME = "bigtable.project";

    private static final String INSTANCE_PROPERTY_NAME = "bigtable.instance";

    private static final String TABLE_PREFIX = "table";

    private static BigtableTableAdminClient adminClient;

    private static String instanceId;

    private static String projectId;

    private String tableId;

    private TableAdminExample tableAdmin;

    @Test
    public void testCreateAndDeleteTable() throws IOException {
        // Creates a table.
        String testTable = generateTableId();
        TableAdminExample testTableAdmin = new TableAdminExample(ITTableAdminExample.projectId, ITTableAdminExample.instanceId, testTable);
        testTableAdmin.createTable();
        Assert.assertTrue(ITTableAdminExample.adminClient.exists(testTable));
        // Deletes a table.
        testTableAdmin.deleteTable();
        Assert.assertFalse(ITTableAdminExample.adminClient.exists(testTable));
    }

    @Test
    public void testCreateMaxAgeRuleAndModifyAndPrintColumnFamily() {
        // Max age rule
        tableAdmin.addFamilyWithMaxAgeRule();
        DurationRule maxAgeCondition = GCRULES.maxAge(5, TimeUnit.DAYS);
        boolean maxAgeRule = ruleCheck(maxAgeCondition);
        Assert.assertTrue(maxAgeRule);
        // Modifies cf1.
        tableAdmin.modifyColumnFamilyRule();
        GCRule modifiedRule = GCRULES.maxVersions(1);
        boolean maxVersionRule = ruleCheck(modifiedRule);
        Assert.assertTrue(maxVersionRule);
    }

    @Test
    public void testCreateMaxVersionsRuleAndDeleteColumnFamily() {
        // Max versions rule
        tableAdmin.addFamilyWithMaxVersionsRule();
        VersionRule maxVersionCondition = GCRULES.maxVersions(2);
        boolean maxVersionRule = ruleCheck(maxVersionCondition);
        Assert.assertTrue(maxVersionRule);
        // Deletes cf2.
        tableAdmin.deleteColumnFamily();
        boolean found = true;
        List<ColumnFamily> columnFamilies = ITTableAdminExample.adminClient.getTable(tableId).getColumnFamilies();
        for (ColumnFamily columnFamily : columnFamilies) {
            if (columnFamily.equals("cf2")) {
                found = false;
                break;
            }
        }
        Assert.assertTrue(found);
    }

    @Test
    public void testCreateUnionRule() {
        // Union rule
        tableAdmin.addFamilyWithUnionRule();
        DurationRule maxAgeRule = GCRULES.maxAge(5, TimeUnit.DAYS);
        VersionRule versionRule = GCRULES.maxVersions(1);
        UnionRule unionCondition = GCRULES.union().rule(maxAgeRule).rule(versionRule);
        boolean unionRule = ruleCheck(unionCondition);
        Assert.assertTrue(unionRule);
    }

    @Test
    public void testCreateIntersectionRule() {
        // Intersection rule
        tableAdmin.addFamilyWithIntersectionRule();
        DurationRule maxAgeRule = GCRULES.maxAge(5, TimeUnit.DAYS);
        VersionRule versionRule = GCRULES.maxVersions(2);
        IntersectionRule intersectionCondition = GCRULES.intersection().rule(maxAgeRule).rule(versionRule);
        boolean intersectionRule = ruleCheck(intersectionCondition);
        Assert.assertTrue(intersectionRule);
    }

    @Test
    public void testCreateNestedRule() {
        // Nested rule
        tableAdmin.addFamilyWithNestedRule();
        VersionRule versionRule = GCRULES.maxVersions(10);
        DurationRule maxAgeRule = GCRULES.maxAge(30, TimeUnit.DAYS);
        VersionRule versionRule2 = GCRULES.maxVersions(2);
        IntersectionRule intersectionRule = GCRULES.intersection().rule(maxAgeRule).rule(versionRule2);
        UnionRule nestedCondition = GCRULES.union().rule(intersectionRule).rule(versionRule);
        boolean nestedRule = ruleCheck(nestedCondition);
        Assert.assertTrue(nestedRule);
    }

    @Test
    public void testRunDoesNotFail() {
        tableAdmin.run();
    }
}

