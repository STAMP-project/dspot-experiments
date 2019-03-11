/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.keycloak.testsuite.migration;


import org.junit.Test;


/**
 * Tests that we can import json file from previous version.  MigrationTest only tests DB.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class JsonFileImport198MigrationTest extends AbstractJsonFileImportMigrationTest {
    @Test
    public void migration1_9_8Test() throws Exception {
        checkRealmsImported();
        testMigratedMigrationData(false);
        testMigrationTo2_0_0();
        testMigrationTo2_1_0();
        testMigrationTo2_2_0();
        testMigrationTo2_3_0();
        testMigrationTo2_5_0();
        // testMigrationTo2_5_1(); // Offline tokens migration is skipped for JSON
        testMigrationTo3_x();
        testMigrationTo4_x(false, false);
        testMigrationTo5_x();
    }
}

