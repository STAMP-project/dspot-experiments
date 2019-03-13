/**
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.boot.test.autoconfigure.orm.jpa;


import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnitUtil;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Tests for {@link TestEntityManager}.
 *
 * @author Phillip Webb
 */
public class TestEntityManagerTests {
    @Mock
    private EntityManagerFactory entityManagerFactory;

    @Mock
    private EntityManager entityManager;

    @Mock
    private PersistenceUnitUtil persistenceUnitUtil;

    private TestEntityManager testEntityManager;

    @Test
    public void createWhenEntityManagerIsNullShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> new TestEntityManager(null)).withMessageContaining("EntityManagerFactory must not be null");
    }

    @Test
    public void persistAndGetIdShouldPersistAndGetId() {
        bindEntityManager();
        TestEntityManagerTests.TestEntity entity = new TestEntityManagerTests.TestEntity();
        BDDMockito.given(this.persistenceUnitUtil.getIdentifier(entity)).willReturn(123);
        Object result = this.testEntityManager.persistAndGetId(entity);
        Mockito.verify(this.entityManager).persist(entity);
        assertThat(result).isEqualTo(123);
    }

    @Test
    public void persistAndGetIdForTypeShouldPersistAndGetId() {
        bindEntityManager();
        TestEntityManagerTests.TestEntity entity = new TestEntityManagerTests.TestEntity();
        BDDMockito.given(this.persistenceUnitUtil.getIdentifier(entity)).willReturn(123);
        Integer result = this.testEntityManager.persistAndGetId(entity, Integer.class);
        Mockito.verify(this.entityManager).persist(entity);
        assertThat(result).isEqualTo(123);
    }

    @Test
    public void persistShouldPersist() {
        bindEntityManager();
        TestEntityManagerTests.TestEntity entity = new TestEntityManagerTests.TestEntity();
        TestEntityManagerTests.TestEntity result = this.testEntityManager.persist(entity);
        Mockito.verify(this.entityManager).persist(entity);
        assertThat(result).isSameAs(entity);
    }

    @Test
    public void persistAndFlushShouldPersistAndFlush() {
        bindEntityManager();
        TestEntityManagerTests.TestEntity entity = new TestEntityManagerTests.TestEntity();
        TestEntityManagerTests.TestEntity result = this.testEntityManager.persistAndFlush(entity);
        Mockito.verify(this.entityManager).persist(entity);
        Mockito.verify(this.entityManager).flush();
        assertThat(result).isSameAs(entity);
    }

    @Test
    public void persistFlushFindShouldPersistAndFlushAndFind() {
        bindEntityManager();
        TestEntityManagerTests.TestEntity entity = new TestEntityManagerTests.TestEntity();
        TestEntityManagerTests.TestEntity found = new TestEntityManagerTests.TestEntity();
        BDDMockito.given(this.persistenceUnitUtil.getIdentifier(entity)).willReturn(123);
        BDDMockito.given(this.entityManager.find(TestEntityManagerTests.TestEntity.class, 123)).willReturn(found);
        TestEntityManagerTests.TestEntity result = this.testEntityManager.persistFlushFind(entity);
        Mockito.verify(this.entityManager).persist(entity);
        Mockito.verify(this.entityManager).flush();
        assertThat(result).isSameAs(found);
    }

    @Test
    public void mergeShouldMerge() {
        bindEntityManager();
        TestEntityManagerTests.TestEntity entity = new TestEntityManagerTests.TestEntity();
        BDDMockito.given(this.entityManager.merge(entity)).willReturn(entity);
        TestEntityManagerTests.TestEntity result = this.testEntityManager.merge(entity);
        Mockito.verify(this.entityManager).merge(entity);
        assertThat(result).isSameAs(entity);
    }

    @Test
    public void removeShouldRemove() {
        bindEntityManager();
        TestEntityManagerTests.TestEntity entity = new TestEntityManagerTests.TestEntity();
        this.testEntityManager.remove(entity);
        Mockito.verify(this.entityManager).remove(entity);
    }

    @Test
    public void findShouldFind() {
        bindEntityManager();
        TestEntityManagerTests.TestEntity entity = new TestEntityManagerTests.TestEntity();
        BDDMockito.given(this.entityManager.find(TestEntityManagerTests.TestEntity.class, 123)).willReturn(entity);
        TestEntityManagerTests.TestEntity result = this.testEntityManager.find(TestEntityManagerTests.TestEntity.class, 123);
        assertThat(result).isSameAs(entity);
    }

    @Test
    public void flushShouldFlush() {
        bindEntityManager();
        this.testEntityManager.flush();
        Mockito.verify(this.entityManager).flush();
    }

    @Test
    public void refreshShouldRefresh() {
        bindEntityManager();
        TestEntityManagerTests.TestEntity entity = new TestEntityManagerTests.TestEntity();
        this.testEntityManager.refresh(entity);
        Mockito.verify(this.entityManager).refresh(entity);
    }

    @Test
    public void clearShouldClear() {
        bindEntityManager();
        this.testEntityManager.clear();
        Mockito.verify(this.entityManager).clear();
    }

    @Test
    public void detachShouldDetach() {
        bindEntityManager();
        TestEntityManagerTests.TestEntity entity = new TestEntityManagerTests.TestEntity();
        this.testEntityManager.detach(entity);
        Mockito.verify(this.entityManager).detach(entity);
    }

    @Test
    public void getIdForTypeShouldGetId() {
        TestEntityManagerTests.TestEntity entity = new TestEntityManagerTests.TestEntity();
        BDDMockito.given(this.persistenceUnitUtil.getIdentifier(entity)).willReturn(123);
        Integer result = this.testEntityManager.getId(entity, Integer.class);
        assertThat(result).isEqualTo(123);
    }

    @Test
    public void getIdForTypeWhenTypeIsWrongShouldThrowException() {
        TestEntityManagerTests.TestEntity entity = new TestEntityManagerTests.TestEntity();
        BDDMockito.given(this.persistenceUnitUtil.getIdentifier(entity)).willReturn(123);
        assertThatIllegalArgumentException().isThrownBy(() -> this.testEntityManager.getId(entity, .class)).withMessageContaining(("ID mismatch: Object of class [java.lang.Integer] " + "must be an instance of class java.lang.Long"));
    }

    @Test
    public void getIdShouldGetId() {
        TestEntityManagerTests.TestEntity entity = new TestEntityManagerTests.TestEntity();
        BDDMockito.given(this.persistenceUnitUtil.getIdentifier(entity)).willReturn(123);
        Object result = this.testEntityManager.getId(entity);
        assertThat(result).isEqualTo(123);
    }

    @Test
    public void getEntityManagerShouldGetEntityManager() {
        bindEntityManager();
        assertThat(this.testEntityManager.getEntityManager()).isEqualTo(this.entityManager);
    }

    @Test
    public void getEntityManagerWhenNotSetShouldThrowException() {
        assertThatIllegalStateException().isThrownBy(this.testEntityManager::getEntityManager).withMessageContaining("No transactional EntityManager found");
    }

    static class TestEntity {}
}

