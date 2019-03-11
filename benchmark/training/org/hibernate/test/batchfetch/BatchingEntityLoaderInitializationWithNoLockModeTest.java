package org.hibernate.test.batchfetch;


import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import org.hibernate.LockOptions;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.metamodel.spi.MetamodelImplementor;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


public class BatchingEntityLoaderInitializationWithNoLockModeTest extends BaseEntityManagerFunctionalTestCase {
    private Long mainId;

    @Test
    public void testJoin() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( em) -> {
            org.hibernate.test.batchfetch.SubEntity sub = new org.hibernate.test.batchfetch.SubEntity();
            em.persist(sub);
            org.hibernate.test.batchfetch.MainEntity main = new org.hibernate.test.batchfetch.MainEntity();
            main.setSub(sub);
            em.persist(main);
            this.mainId = main.getId();
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( em) -> {
            EntityPersister entityPersister = ((MetamodelImplementor) (em.getMetamodel())).entityPersister(.class);
            // use some specific lock options to trigger the creation of a loader with lock options
            LockOptions lockOptions = new LockOptions(LockMode.NONE);
            lockOptions.setTimeOut(10);
            org.hibernate.test.batchfetch.MainEntity main = ((org.hibernate.test.batchfetch.MainEntity) (entityPersister.load(this.mainId, null, lockOptions, ((SharedSessionContractImplementor) (em)))));
            assertNotNull(main.getSub());
        });
    }

    @Entity(name = "MainEntity")
    public static class MainEntity {
        @Id
        @GeneratedValue
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        @Fetch(FetchMode.JOIN)
        private BatchingEntityLoaderInitializationWithNoLockModeTest.SubEntity sub;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public BatchingEntityLoaderInitializationWithNoLockModeTest.SubEntity getSub() {
            return sub;
        }

        public void setSub(BatchingEntityLoaderInitializationWithNoLockModeTest.SubEntity sub) {
            this.sub = sub;
        }
    }

    @Entity(name = "SubEntity")
    public static class SubEntity {
        @Id
        @GeneratedValue
        private Long id;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }
    }
}

