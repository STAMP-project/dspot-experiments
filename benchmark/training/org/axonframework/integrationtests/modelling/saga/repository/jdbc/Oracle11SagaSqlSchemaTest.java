/**
 * Copyright (c) 2010-2018. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.axonframework.integrationtests.modelling.saga.repository.jdbc;


import java.sql.Connection;
import org.axonframework.modelling.saga.repository.jdbc.Oracle11SagaSqlSchema;
import org.axonframework.modelling.saga.repository.jdbc.SagaSchema;
import org.junit.Test;


public class Oracle11SagaSqlSchemaTest {
    private Oracle11SagaSqlSchema testSubject;

    private Connection connection;

    private SagaSchema sagaSchema;

    @Test
    public void testSql_createTableAssocValueEntry() throws Exception {
        // test passes if no exception is thrown
        testSubject.sql_createTableAssocValueEntry(connection).execute();
        connection.prepareStatement(("SELECT * FROM " + (sagaSchema.associationValueEntryTable()))).execute();
        connection.prepareStatement(("DROP TABLE " + (sagaSchema.associationValueEntryTable()))).execute();
        connection.prepareStatement((("DROP SEQUENCE " + (sagaSchema.associationValueEntryTable())) + "_seq")).execute();
    }

    @Test
    public void testSql_createTableSagaEntry() throws Exception {
        // test passes if no exception is thrown
        testSubject.sql_createTableSagaEntry(connection).execute();
        connection.prepareStatement(("SELECT * FROM " + (sagaSchema.sagaEntryTable()))).execute();
        connection.prepareStatement(("DROP TABLE " + (sagaSchema.sagaEntryTable()))).execute();
    }
}

