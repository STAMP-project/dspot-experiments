package com.querydsl.apt.domain;


import QGeneric14Test_AbstractPersistable.abstractPersistable;
import QGeneric14Test_BasePersistable.basePersistable;
import QGeneric14Test_BaseReferencablePersistable.baseReferencablePersistable;
import QGeneric14Test_UserAccount.userAccount;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import org.junit.Assert;
import org.junit.Test;


public class Generic14Test extends AbstractTest {
    @Entity
    public static class UserAccount extends Generic14Test.BaseReferencablePersistable<Generic14Test.UserAccount, Long> {
        public UserAccount() {
            super(Generic14Test.UserAccount.class);
        }
    }

    @MappedSuperclass
    public abstract static class BaseReferencablePersistable<T, PK extends Serializable> extends Generic14Test.BasePersistable<PK> {
        private Class<T> entityClass;

        public BaseReferencablePersistable(Class<T> entityClass) {
            this.entityClass = entityClass;
        }
    }

    @MappedSuperclass
    public static class BasePersistable<T extends Serializable> extends Generic14Test.AbstractPersistable<T> implements Generic14Test.UpdateInfo {
        private T id;

        @Override
        public T getId() {
            return id;
        }
    }

    @MappedSuperclass
    public abstract static class AbstractPersistable<PK extends Serializable> implements Generic14Test.Persistable<PK> {}

    public interface Persistable<T> {
        T getId();
    }

    public interface UpdateInfo {}

    @Test
    public void test() throws IllegalAccessException, NoSuchFieldException {
        Assert.assertNotNull(abstractPersistable);
        start(QGeneric14Test_BasePersistable.class, basePersistable);
        matchType(Serializable.class, "id");
        start(QGeneric14Test_BaseReferencablePersistable.class, baseReferencablePersistable);
        matchType(Class.class, "entityClass");
        matchType(Serializable.class, "id");
        start(QGeneric14Test_UserAccount.class, userAccount);
        matchType(Long.class, "id");
    }
}

