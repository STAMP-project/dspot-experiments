/**
 * Copyright 2017 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.spanner;


import DatabaseInfo.State.CREATING;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;


/**
 * Unit tests for {@link com.google.cloud.spanner.Database}.
 */
@RunWith(JUnit4.class)
public class DatabaseTest {
    private static final String NAME = "projects/test-project/instances/test-instance/databases/database-1";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    DatabaseAdminClient dbClient;

    @Test
    public void fromProto() {
        Database db = createDatabase();
        assertThat(db.getId().getName()).isEqualTo(DatabaseTest.NAME);
        assertThat(db.getState()).isEqualTo(CREATING);
    }
}

