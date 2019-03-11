/**
 * Copyright 2011-2019 the original author or authors.
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
package org.springframework.data.jpa.repository.support;


import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import lombok.Data;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.jpa.domain.sample.Access;
import org.springframework.data.jpa.domain.sample.Entity;
import org.springframework.data.jpa.domain.sample.Id;
import org.springframework.data.jpa.domain.sample.IdClass;
import org.springframework.data.jpa.domain.sample.Item;
import org.springframework.data.jpa.domain.sample.ItemId;
import org.springframework.data.jpa.domain.sample.ItemSite;
import org.springframework.data.jpa.domain.sample.ItemSiteId;
import org.springframework.data.jpa.domain.sample.ManyToOne;
import org.springframework.data.jpa.domain.sample.MappedSuperclass;
import org.springframework.data.jpa.domain.sample.PersistableWithIdClass;
import org.springframework.data.jpa.domain.sample.PersistableWithIdClassPK;
import org.springframework.data.jpa.domain.sample.PrimitiveVersionProperty;
import org.springframework.data.jpa.domain.sample.Role;
import org.springframework.data.jpa.domain.sample.SampleWithIdClass;
import org.springframework.data.jpa.domain.sample.SampleWithIdClassIncludingEntity;
import org.springframework.data.jpa.domain.sample.SampleWithPrimitiveId;
import org.springframework.data.jpa.domain.sample.SampleWithTimestampVersion;
import org.springframework.data.jpa.domain.sample.Site;
import org.springframework.data.jpa.domain.sample.User;
import org.springframework.data.jpa.domain.sample.VersionedUser;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static AccessType.FIELD;


/**
 * Integration tests for {@link JpaMetamodelEntityInformation}.
 *
 * @author Oliver Gierke
 * @author Thomas Darimont
 * @author Christoph Strobl
 * @author Jens Schauder
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:infrastructure.xml" })
public class JpaMetamodelEntityInformationIntegrationTests {
    @PersistenceContext
    EntityManager em;

    @Test
    public void detectsIdTypeForEntity() {
        JpaEntityInformation<User, ?> information = getEntityInformation(User.class, em);
        assertThat(information.getIdType()).isAssignableFrom(Integer.class);
    }

    // DATAJPA-50
    @Test
    public void detectsIdClass() {
        EntityInformation<PersistableWithIdClass, ?> information = getEntityInformation(PersistableWithIdClass.class, em);
        assertThat(information.getIdType()).isAssignableFrom(PersistableWithIdClassPK.class);
    }

    // DATAJPA-50
    @Test
    public void returnsIdOfPersistableInstanceCorrectly() {
        PersistableWithIdClass entity = new PersistableWithIdClass(2L, 4L);
        JpaEntityInformation<PersistableWithIdClass, ?> information = getEntityInformation(PersistableWithIdClass.class, em);
        Object id = information.getId(entity);
        assertThat(id).isEqualTo(new PersistableWithIdClassPK(2L, 4L));
    }

    // DATAJPA-413
    @Test
    public void returnsIdOfEntityWithIdClassCorrectly() {
        Item item = new Item(2, 1);
        JpaEntityInformation<Item, ?> information = getEntityInformation(Item.class, em);
        Object id = information.getId(item);
        assertThat(id).isEqualTo(new ItemId(2, 1));
    }

    // DATAJPA-413
    @Test
    public void returnsDerivedIdOfEntityWithIdClassCorrectly() {
        Item item = new Item(1, 2);
        Site site = new Site(3);
        ItemSite itemSite = new ItemSite(item, site);
        JpaEntityInformation<ItemSite, ?> information = getEntityInformation(ItemSite.class, em);
        Object id = information.getId(itemSite);
        assertThat(id).isEqualTo(new ItemSiteId(new ItemId(1, 2), 3));
    }

    // DATAJPA-413
    @Test
    public void returnsPartialEmptyDerivedIdOfEntityWithIdClassCorrectly() {
        Item item = new Item(1, null);
        Site site = new Site(3);
        ItemSite itemSite = new ItemSite(item, site);
        JpaEntityInformation<ItemSite, ?> information = getEntityInformation(ItemSite.class, em);
        Object id = information.getId(itemSite);
        assertThat(id).isEqualTo(new ItemSiteId(new ItemId(1, null), 3));
    }

    // DATAJPA-119
    @Test
    public void favoursVersionAnnotationIfPresent() {
        EntityInformation<VersionedUser, Long> information = new JpaMetamodelEntityInformation(VersionedUser.class, em.getMetamodel());
        VersionedUser entity = new VersionedUser();
        assertThat(information.isNew(entity)).isTrue();
        entity.setId(1L);
        assertThat(information.isNew(entity)).isTrue();
        entity.setVersion(1L);
        assertThat(information.isNew(entity)).isFalse();
        entity.setId(null);
        assertThat(information.isNew(entity)).isFalse();
    }

    // DATAJPA-348
    @Test
    public void findsIdClassOnMappedSuperclass() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(getMetadadataPersitenceUnitName());
        EntityManager em = emf.createEntityManager();
        EntityInformation<JpaMetamodelEntityInformationIntegrationTests.Sample, JpaMetamodelEntityInformationIntegrationTests.BaseIdClass> information = new JpaMetamodelEntityInformation(JpaMetamodelEntityInformationIntegrationTests.Sample.class, em.getMetamodel());
        assertThat(information.getIdType()).isEqualTo(JpaMetamodelEntityInformationIntegrationTests.BaseIdClass.class);
    }

    // DATACMNS-357
    @Test
    public void detectsNewStateForEntityWithPrimitiveId() {
        EntityInformation<SampleWithPrimitiveId, Long> information = new JpaMetamodelEntityInformation(SampleWithPrimitiveId.class, em.getMetamodel());
        SampleWithPrimitiveId sample = new SampleWithPrimitiveId();
        assertThat(information.isNew(sample)).isTrue();
        sample.setId(5L);
        assertThat(information.isNew(sample)).isFalse();
    }

    // DATAJPA-509
    @Test
    public void jpaMetamodelEntityInformationShouldRespectExplicitlyConfiguredEntityNameFromOrmXml() {
        JpaEntityInformation<Role, Integer> info = new JpaMetamodelEntityInformation(Role.class, em.getMetamodel());
        assertThat(info.getEntityName()).isEqualTo("ROLE");
    }

    // DATAJPA-561
    @Test
    public void considersEntityWithPrimitiveVersionPropertySetToDefaultNew() {
        EntityInformation<PrimitiveVersionProperty, Serializable> information = new JpaMetamodelEntityInformation(PrimitiveVersionProperty.class, em.getMetamodel());
        assertThat(information.isNew(new PrimitiveVersionProperty())).isTrue();
    }

    // DATAJPA-568
    @Test
    public void considersEntityAsNotNewWhenHavingIdSetAndUsingPrimitiveTypeForVersionProperty() {
        EntityInformation<PrimitiveVersionProperty, Serializable> information = new JpaMetamodelEntityInformation(PrimitiveVersionProperty.class, em.getMetamodel());
        PrimitiveVersionProperty pvp = new PrimitiveVersionProperty();
        pvp.id = 100L;
        assertThat(information.isNew(pvp)).isFalse();
    }

    // DATAJPA-568
    @Test
    public void fallsBackToIdInspectionForAPrimitiveVersionProperty() {
        EntityInformation<PrimitiveVersionProperty, Serializable> information = new JpaMetamodelEntityInformation(PrimitiveVersionProperty.class, em.getMetamodel());
        PrimitiveVersionProperty pvp = new PrimitiveVersionProperty();
        pvp.version = 1L;
        assertThat(information.isNew(pvp)).isTrue();
        pvp.id = 1L;
        assertThat(information.isNew(pvp)).isFalse();
    }

    // DATAJPA-582
    @Test
    public void considersEntityWithUnsetCompundIdNew() {
        EntityInformation<SampleWithIdClass, ?> information = getEntityInformation(SampleWithIdClass.class, em);
        assertThat(information.isNew(new SampleWithIdClass())).isTrue();
    }

    // DATAJPA-582
    @Test
    public void considersEntityWithSetTimestampVersionNotNew() {
        EntityInformation<SampleWithTimestampVersion, ?> information = getEntityInformation(SampleWithTimestampVersion.class, em);
        SampleWithTimestampVersion entity = new SampleWithTimestampVersion();
        entity.version = new Timestamp(new Date().getTime());
        assertThat(information.isNew(entity)).isFalse();
    }

    // DATAJPA-582, DATAJPA-581
    @Test
    public void considersEntityWithNonPrimitiveNonNullIdTypeNotNew() {
        EntityInformation<User, ?> information = getEntityInformation(User.class, em);
        User user = new User();
        assertThat(information.isNew(user)).isTrue();
        user.setId(0);
        assertThat(information.isNew(user)).isFalse();
    }

    // DATAJPA-1105
    @Test
    public void correctlyDeterminesIdValueForNestedIdClassesWithNonPrimitiveNonManagedType() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(getMetadadataPersitenceUnitName());
        EntityManager em = emf.createEntityManager();
        JpaEntityInformation<JpaMetamodelEntityInformationIntegrationTests.EntityWithNestedIdClass, ?> information = getEntityInformation(JpaMetamodelEntityInformationIntegrationTests.EntityWithNestedIdClass.class, em);
        JpaMetamodelEntityInformationIntegrationTests.EntityWithNestedIdClass entity = new JpaMetamodelEntityInformationIntegrationTests.EntityWithNestedIdClass();
        entity.id = 23L;
        entity.reference = new JpaMetamodelEntityInformationIntegrationTests.EntityWithIdClass();
        entity.reference.id1 = "one";
        entity.reference.id2 = "two";
        Object id = information.getId(entity);
        assertThat(id).isNotNull();
    }

    // DATAJPA-1416
    @Test
    public void proxiedIdClassElement() {
        JpaEntityInformation<SampleWithIdClassIncludingEntity, ?> information = getEntityInformation(SampleWithIdClassIncludingEntity.class, em);
        SampleWithIdClassIncludingEntity entity = new SampleWithIdClassIncludingEntity();
        setFirst(23L);
        SampleWithIdClassIncludingEntity.OtherEntity$$PsudoProxy inner = new SampleWithIdClassIncludingEntity.OtherEntity$$PsudoProxy();
        setOtherId(42L);
        setSecond(inner);
        Object id = information.getId(entity);
        assertThat(id).isInstanceOf(SampleWithIdClassIncludingEntity.SampleWithIdClassPK.class);
        SampleWithIdClassIncludingEntity.SampleWithIdClassPK pk = ((SampleWithIdClassIncludingEntity.SampleWithIdClassPK) (id));
        assertThat(getFirst()).isEqualTo(23L);
        assertThat(getSecond()).isEqualTo(42L);
    }

    @SuppressWarnings("serial")
    public static class BaseIdClass implements Serializable {
        Long id;

        Long feedRunId;
    }

    @MappedSuperclass
    @IdClass(JpaMetamodelEntityInformationIntegrationTests.BaseIdClass.class)
    @Access(FIELD)
    public abstract static class Identifiable {
        @Id
        Long id;

        @Id
        Long feedRunId;
    }

    @Entity
    @Access(AccessType.FIELD)
    public static class Sample extends JpaMetamodelEntityInformationIntegrationTests.Identifiable {}

    @Entity
    @Access(AccessType.FIELD)
    @IdClass(JpaMetamodelEntityInformationIntegrationTests.EntityWithNestedIdClassPK.class)
    public static class EntityWithNestedIdClass {
        @Id
        Long id;

        @Id
        @ManyToOne
        private JpaMetamodelEntityInformationIntegrationTests.EntityWithIdClass reference;
    }

    @Entity
    @Access(AccessType.FIELD)
    @IdClass(JpaMetamodelEntityInformationIntegrationTests.EntityWithIdClassPK.class)
    public static class EntityWithIdClass {
        @Id
        String id1;

        @Id
        String id2;
    }

    @Data
    public static class EntityWithIdClassPK implements Serializable {
        String id1;

        String id2;
    }

    @Data
    public static class EntityWithNestedIdClassPK implements Serializable {
        Long id;

        JpaMetamodelEntityInformationIntegrationTests.EntityWithIdClassPK reference;
    }
}

