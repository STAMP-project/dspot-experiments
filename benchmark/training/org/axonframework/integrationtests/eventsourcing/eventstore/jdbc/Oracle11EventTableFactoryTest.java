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
package org.axonframework.integrationtests.eventsourcing.eventstore.jdbc;


import java.sql.Connection;
import org.axonframework.eventsourcing.eventstore.jdbc.EventSchema;
import org.axonframework.eventsourcing.eventstore.jdbc.Oracle11EventTableFactory;
import org.junit.Test;


public class Oracle11EventTableFactoryTest {
    private Oracle11EventTableFactory testSubject;

    private Connection connection;

    private EventSchema eventSchema;

    @Test
    public void testCreateDomainEventTable() throws Exception {
        // test passes if no exception is thrown
        testSubject.createDomainEventTable(connection, eventSchema).execute();
        connection.prepareStatement(("SELECT * FROM " + (eventSchema.domainEventTable()))).execute();
        connection.prepareStatement(("DROP TABLE " + (eventSchema.domainEventTable()))).execute();
        connection.prepareStatement((("DROP SEQUENCE " + (eventSchema.domainEventTable())) + "_seq")).execute();
    }

    @Test
    public void testCreateSnapshotEventTable() throws Exception {
        // test passes if no exception is thrown
        testSubject.createSnapshotEventTable(connection, eventSchema).execute();
        connection.prepareStatement(("SELECT * FROM " + (eventSchema.snapshotTable()))).execute();
        connection.prepareStatement(("DROP TABLE " + (eventSchema.snapshotTable()))).execute();
    }
}

