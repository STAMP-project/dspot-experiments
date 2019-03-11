package org.hibernate.test.notfound;


import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import org.hibernate.Hibernate;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Gail Badner
 */
@TestForIssue(jiraKey = "HHH-12436")
public class RequiredLazyNotFoundTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testOneToOneSelectException() {
        setupTest(RequiredLazyNotFoundTest.PersonOneToOneSelectException.class, 1L, false);
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.notfound.Person pCheck = session.find(.class, 1L);
            assertNotNull(pCheck);
            assertFalse(Hibernate.isInitialized(pCheck.getCity()));
            try {
                Hibernate.initialize(pCheck.getCity());
                fail("Should have thrown ObjectNotFoundException");
            } catch ( expected) {
                session.getTransaction().setRollbackOnly();
            }
        });
    }

    @Test
    public void testManyToOneSelectException() {
        setupTest(RequiredLazyNotFoundTest.PersonManyToOneSelectException.class, 1L, false);
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.notfound.Person pCheck = session.find(.class, 1L);
            assertNotNull(pCheck);
            assertFalse(Hibernate.isInitialized(pCheck.getCity()));
            try {
                Hibernate.initialize(pCheck.getCity());
                fail("Should have thrown ObjectNotFoundException");
            } catch ( expected) {
                session.getTransaction().setRollbackOnly();
            }
        });
    }

    @Test
    public void testPkjcOneToOneSelectException() {
        setupTest(RequiredLazyNotFoundTest.PersonPkjcSelectException.class, 1L, false);
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.notfound.Person pCheck = session.find(.class, 1L);
            assertNotNull(pCheck);
            assertFalse(Hibernate.isInitialized(pCheck.getCity()));
            try {
                Hibernate.initialize(pCheck.getCity());
                fail("Should have thrown ObjectNotFoundException");
            } catch ( expected) {
                session.getTransaction().setRollbackOnly();
            }
        });
    }

    @Test
    public void testMapsIdOneToOneSelectException() {
        setupTest(RequiredLazyNotFoundTest.PersonMapsIdSelectException.class, 1L, true);
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.notfound.Person pCheck = session.find(.class, 1L);
            assertNotNull(pCheck);
            assertFalse(Hibernate.isInitialized(pCheck.getCity()));
            try {
                Hibernate.initialize(pCheck.getCity());
                fail("Should have thrown ObjectNotFoundException");
            } catch ( expected) {
                session.getTransaction().setRollbackOnly();
            }
        });
    }

    @Test
    public void testMapsIdJoinColumnOneToOneSelectException() {
        setupTest(RequiredLazyNotFoundTest.PersonMapsIdColumnSelectException.class, 1L, true);
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.notfound.Person pCheck = session.find(.class, 1L);
            assertNotNull(pCheck);
            assertFalse(Hibernate.isInitialized(pCheck.getCity()));
            try {
                Hibernate.initialize(pCheck.getCity());
                fail("Should have thrown ObjectNotFoundException");
            } catch ( expected) {
                session.getTransaction().setRollbackOnly();
            }
        });
    }

    @MappedSuperclass
    public abstract static class Person {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public abstract void setId(Long id);

        public abstract RequiredLazyNotFoundTest.City getCity();

        public abstract void setCity(RequiredLazyNotFoundTest.City city);
    }

    @Entity
    @Table(name = "PersonOneToOneSelectException")
    public static class PersonOneToOneSelectException extends RequiredLazyNotFoundTest.Person {
        @Id
        private Long id;

        @OneToOne(optional = false, fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
        @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
        private RequiredLazyNotFoundTest.City city;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public RequiredLazyNotFoundTest.City getCity() {
            return city;
        }

        @Override
        public void setCity(RequiredLazyNotFoundTest.City city) {
            this.city = city;
        }
    }

    @Entity
    @Table(name = "PersonManyToOneSelectException")
    public static class PersonManyToOneSelectException extends RequiredLazyNotFoundTest.Person {
        @Id
        private Long id;

        @ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
        @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
        private RequiredLazyNotFoundTest.City city;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public RequiredLazyNotFoundTest.City getCity() {
            return city;
        }

        @Override
        public void setCity(RequiredLazyNotFoundTest.City city) {
            this.city = city;
        }
    }

    @Entity
    @Table(name = "PersonPkjcSelectException")
    public static class PersonPkjcSelectException extends RequiredLazyNotFoundTest.Person {
        @Id
        private Long id;

        @OneToOne(optional = false, fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
        @PrimaryKeyJoinColumn
        @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
        private RequiredLazyNotFoundTest.City city;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public RequiredLazyNotFoundTest.City getCity() {
            return city;
        }

        @Override
        public void setCity(RequiredLazyNotFoundTest.City city) {
            this.city = city;
        }
    }

    @Entity
    @Table(name = "PersonMapsIdJoinException")
    public static class PersonMapsIdJoinException extends RequiredLazyNotFoundTest.Person {
        @Id
        private Long id;

        @OneToOne(optional = false, fetch = FetchType.LAZY)
        @MapsId
        @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
        private RequiredLazyNotFoundTest.City city;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public RequiredLazyNotFoundTest.City getCity() {
            return city;
        }

        @Override
        public void setCity(RequiredLazyNotFoundTest.City city) {
            this.city = city;
        }
    }

    @Entity
    @Table(name = "PersonMapsIdSelectException")
    public static class PersonMapsIdSelectException extends RequiredLazyNotFoundTest.Person {
        @Id
        private Long id;

        @OneToOne(optional = false, fetch = FetchType.LAZY)
        @MapsId
        @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
        private RequiredLazyNotFoundTest.City city;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public RequiredLazyNotFoundTest.City getCity() {
            return city;
        }

        @Override
        public void setCity(RequiredLazyNotFoundTest.City city) {
            this.city = city;
        }
    }

    @Entity
    @Table(name = "PersonMapsIdColumnJoinException")
    public static class PersonMapsIdColumnJoinException extends RequiredLazyNotFoundTest.Person {
        @Id
        private Long id;

        @OneToOne(optional = false, fetch = FetchType.LAZY)
        @MapsId
        @JoinColumn(name = "fk", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
        private RequiredLazyNotFoundTest.City city;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public RequiredLazyNotFoundTest.City getCity() {
            return city;
        }

        @Override
        public void setCity(RequiredLazyNotFoundTest.City city) {
            this.city = city;
        }
    }

    @Entity
    @Table(name = "PersonMapsIdColumnSelectExcept")
    public static class PersonMapsIdColumnSelectException extends RequiredLazyNotFoundTest.Person {
        @Id
        private Long id;

        @OneToOne(optional = false, fetch = FetchType.LAZY)
        @MapsId
        @JoinColumn(name = "fk", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
        private RequiredLazyNotFoundTest.City city;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public RequiredLazyNotFoundTest.City getCity() {
            return city;
        }

        @Override
        public void setCity(RequiredLazyNotFoundTest.City city) {
            this.city = city;
        }
    }

    @Entity
    @Table(name = "City")
    public static class City implements Serializable {
        @Id
        private Long id;

        private String name;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

