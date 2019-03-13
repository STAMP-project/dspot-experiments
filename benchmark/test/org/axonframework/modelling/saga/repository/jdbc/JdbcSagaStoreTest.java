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
package org.axonframework.modelling.saga.repository.jdbc;


import java.sql.Connection;
import java.util.Collections;
import java.util.Set;
import org.axonframework.modelling.saga.AssociationValue;
import org.axonframework.modelling.saga.AssociationValues;
import org.axonframework.modelling.saga.repository.SagaStore;
import org.axonframework.modelling.saga.repository.StubSaga;
import org.hsqldb.jdbc.JDBCDataSource;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Kristian Rosenvold
 */
public class JdbcSagaStoreTest {
    private Connection connection;

    private JdbcSagaStore testSubject;

    private JDBCDataSource dataSource;

    @Test
    public void testInsertUpdateAndLoadSaga() {
        StubSaga saga = new StubSaga();
        Set<AssociationValue> associationValues = Collections.singleton(new AssociationValue("key", "value"));
        testSubject.insertSaga(StubSaga.class, "123", saga, associationValues);
        testSubject.updateSaga(StubSaga.class, "123", saga, new org.axonframework.modelling.saga.AssociationValuesImpl(associationValues));
        SagaStore.Entry<StubSaga> entry = testSubject.loadSaga(StubSaga.class, "123");
        Assert.assertNotNull(entry);
        Assert.assertNotNull(entry.saga());
        Assert.assertEquals(associationValues, entry.associationValues());
    }

    @Test
    public void testLoadSaga_NotFound() {
        Assert.assertNull(testSubject.loadSaga(StubSaga.class, "123456"));
    }

    @Test
    public void testLoadSagaByAssociationValue() {
        AssociationValues associationsValues = new org.axonframework.modelling.saga.AssociationValuesImpl(Collections.singleton(new AssociationValue("key", "value")));
        testSubject.insertSaga(StubSaga.class, "123", new StubSaga(), associationsValues.asSet());
        testSubject.insertSaga(StubSaga.class, "456", new StubSaga(), Collections.singleton(new AssociationValue("key", "value2")));
        associationsValues.add(new AssociationValue("key", "value2"));
        testSubject.updateSaga(StubSaga.class, "123", new StubSaga(), associationsValues);
        associationsValues.commit();
        associationsValues.remove(new AssociationValue("key", "value2"));
        testSubject.updateSaga(StubSaga.class, "123", new StubSaga(), associationsValues);
        associationsValues.commit();
        Set<String> actual = testSubject.findSagas(StubSaga.class, new AssociationValue("key", "value"));
        Assert.assertEquals(Collections.singleton("123"), actual);
    }
}

