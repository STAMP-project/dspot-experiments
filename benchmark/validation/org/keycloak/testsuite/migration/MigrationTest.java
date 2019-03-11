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
import org.keycloak.testsuite.arquillian.migration.Migration;


/**
 *
 *
 * @author <a href="mailto:vramik@redhat.com">Vlastislav Ramik</a>
 */
public class MigrationTest extends AbstractMigrationTest {
    @Test
    @Migration(versionFrom = "4.")
    public void migration4_xTest() {
        testMigratedData();
        testMigrationTo5_x();
    }

    @Test
    @Migration(versionFrom = "3.")
    public void migration3_xTest() {
        testMigratedData();
        testMigrationTo4_x();
        testMigrationTo5_x();
    }

    @Test
    @Migration(versionFrom = "2.")
    public void migration2_xTest() {
        testMigratedData();
        testMigrationTo3_x();
        testMigrationTo4_x();
        testMigrationTo5_x();
    }

    @Test
    @Migration(versionFrom = "1.")
    public void migration1_xTest() throws Exception {
        testMigratedData(false);
        testMigrationTo2_x();
        testMigrationTo3_x();
        testMigrationTo4_x(false, false);
        testMigrationTo5_x();
    }
}

