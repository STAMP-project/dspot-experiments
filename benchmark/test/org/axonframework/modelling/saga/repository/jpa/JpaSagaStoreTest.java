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
package org.axonframework.modelling.saga.repository.jpa;


import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.axonframework.common.IdentifierFactory;
import org.axonframework.messaging.Message;
import org.axonframework.messaging.unitofwork.DefaultUnitOfWork;
import org.axonframework.modelling.saga.AssociationValue;
import org.axonframework.modelling.saga.repository.AnnotatedSagaRepository;
import org.axonframework.modelling.saga.repository.StubSaga;
import org.axonframework.serialization.Serializer;
import org.axonframework.serialization.SimpleSerializedObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.jmx.support.RegistrationPolicy;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;


/**
 *
 *
 * @author Allard Buijze
 */
@RunWith(SpringJUnit4ClassRunner.class)
@EnableMBeanExport(registration = RegistrationPolicy.IGNORE_EXISTING)
@ContextConfiguration(locations = "/META-INF/spring/saga-repository-test.xml")
@Transactional
public class JpaSagaStoreTest {
    private AnnotatedSagaRepository<StubSaga> repository;

    @Autowired
    private PlatformTransactionManager txManager;

    @PersistenceContext
    private EntityManager entityManager;

    private DefaultUnitOfWork<Message<?>> unitOfWork;

    @DirtiesContext
    @Test
    public void testAddingAnInactiveSagaDoesntStoreIt() throws Exception {
        unitOfWork.executeWithResult(() -> {
            Saga<StubSaga> saga = repository.createInstance(IdentifierFactory.getInstance().generateIdentifier(), StubSaga::new);
            saga.execute(( testSaga) -> {
                testSaga.registerAssociationValue(new AssociationValue("key", "value"));
                testSaga.end();
            });
            return null;
        });
        entityManager.flush();
        entityManager.clear();
        long result = entityManager.createQuery("select count(*) from SagaEntry", Long.class).getSingleResult();
        Assert.assertEquals(0L, result);
    }

    @DirtiesContext
    @Test
    public void testAddAndLoadSaga_ByIdentifier() throws Exception {
        String identifier = unitOfWork.executeWithResult(() -> repository.createInstance(IdentifierFactory.getInstance().generateIdentifier(), StubSaga::new).getSagaIdentifier()).getPayload();
        entityManager.clear();
        startUnitOfWork();
        unitOfWork.execute(() -> {
            org.axonframework.modelling.saga.Saga loaded = repository.load(identifier);
            assertEquals(identifier, loaded.getSagaIdentifier());
            assertNotNull(entityManager.find(.class, identifier));
        });
    }

    @DirtiesContext
    @Test
    public void testAddAndLoadSaga_ByAssociationValue() throws Exception {
        String identifier = unitOfWork.executeWithResult(() -> {
            Saga<StubSaga> saga = repository.createInstance(IdentifierFactory.getInstance().generateIdentifier(), StubSaga::new);
            saga.execute(( s) -> s.associate("key", "value"));
            return saga.getSagaIdentifier();
        }).getPayload();
        entityManager.clear();
        startUnitOfWork();
        unitOfWork.execute(() -> {
            Set<String> loaded = repository.find(new AssociationValue("key", "value"));
            assertEquals(1, loaded.size());
            org.axonframework.modelling.saga.Saga loadedSaga = repository.load(loaded.iterator().next());
            assertEquals(identifier, loadedSaga.getSagaIdentifier());
            assertNotNull(entityManager.find(.class, identifier));
        });
    }

    @DirtiesContext
    @Test
    public void testLoadSaga_AssociationValueRemoved() throws Exception {
        String identifier = unitOfWork.executeWithResult(() -> {
            Saga<StubSaga> saga = repository.createInstance(IdentifierFactory.getInstance().generateIdentifier(), StubSaga::new);
            saga.execute(( s) -> s.associate("key", "value"));
            return saga.getSagaIdentifier();
        }).getPayload();
        entityManager.clear();
        startUnitOfWork();
        unitOfWork.execute(() -> {
            Saga<StubSaga> loaded = repository.load(identifier);
            loaded.execute(( s) -> s.removeAssociationValue("key", "value"));
        });
        entityManager.clear();
        startUnitOfWork();
        java.util.Set<String> found = unitOfWork.executeWithResult(() -> repository.find(new AssociationValue("key", "value"))).getPayload();
        Assert.assertEquals(0, found.size());
    }

    @DirtiesContext
    @Test
    public void testEndSaga() throws Exception {
        String identifier = unitOfWork.executeWithResult(() -> {
            Saga<StubSaga> saga = repository.createInstance(IdentifierFactory.getInstance().generateIdentifier(), StubSaga::new);
            saga.execute(( s) -> s.associate("key", "value"));
            return saga.getSagaIdentifier();
        }).getPayload();
        entityManager.clear();
        Assert.assertFalse(entityManager.createQuery("SELECT ae FROM AssociationValueEntry ae WHERE ae.sagaId = :id").setParameter("id", identifier).getResultList().isEmpty());
        startUnitOfWork();
        unitOfWork.execute(() -> {
            Saga<StubSaga> loaded = repository.load(identifier);
            loaded.execute(StubSaga::end);
        });
        entityManager.clear();
        Assert.assertNull(entityManager.find(SagaEntry.class, identifier));
        Assert.assertTrue(entityManager.createQuery("SELECT ae FROM AssociationValueEntry ae WHERE ae.sagaId = :id").setParameter("id", identifier).getResultList().isEmpty());
    }

    @DirtiesContext
    @Test
    public void testStoreSagaWithCustomEntity() throws Exception {
        JpaSagaStore sagaStore = new JpaSagaStore(JpaSagaStore.builder().entityManagerProvider(new org.axonframework.common.jpa.SimpleEntityManagerProvider(entityManager))) {
            @Override
            protected AbstractSagaEntry<?> createSagaEntry(Object saga, String sagaIdentifier, Serializer serializer) {
                return new CustomSagaEntry(saga, sagaIdentifier, serializer);
            }

            @Override
            protected String sagaEntryEntityName() {
                return CustomSagaEntry.class.getSimpleName();
            }

            @Override
            protected Class<? extends SimpleSerializedObject<?>> serializedObjectType() {
                return CustomSerializedSaga.class;
            }
        };
        repository = AnnotatedSagaRepository.<StubSaga>builder().sagaType(StubSaga.class).sagaStore(sagaStore).build();
        String identifier = unitOfWork.executeWithResult(() -> repository.createInstance(IdentifierFactory.getInstance().generateIdentifier(), StubSaga::new).getSagaIdentifier()).getPayload();
        Assert.assertFalse(entityManager.createQuery("SELECT e FROM CustomSagaEntry e").getResultList().isEmpty());
        entityManager.clear();
        startUnitOfWork();
        unitOfWork.execute(() -> {
            Saga<StubSaga> loaded = repository.load(identifier);
            loaded.execute(StubSaga::end);
            assertNotNull(entityManager.find(.class, identifier));
        });
    }
}

