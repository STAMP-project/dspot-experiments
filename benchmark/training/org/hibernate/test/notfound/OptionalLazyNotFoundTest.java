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
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
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
public class OptionalLazyNotFoundTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testOneToOneSelectException() {
        setupTest(OptionalLazyNotFoundTest.PersonOneToOneSelectException.class, 1L, false);
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
    public void testOneToOneSelectIgnore() {
        setupTest(OptionalLazyNotFoundTest.PersonOneToOneSelectIgnore.class, 1L, false);
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.notfound.Person pCheck = session.find(.class, 1L);
            assertNotNull(pCheck);
            assertNull(pCheck.getCity());
        });
    }

    @Test
    public void testManyToOneSelectException() {
        setupTest(OptionalLazyNotFoundTest.PersonManyToOneSelectException.class, 1L, false);
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
    public void testManyToOneSelectIgnore() {
        setupTest(OptionalLazyNotFoundTest.PersonManyToOneSelectIgnore.class, 1L, false);
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.notfound.Person pCheck = session.find(.class, 1L);
            assertNotNull(pCheck);
            assertNull(pCheck.getCity());
        });
    }

    @Test
    public void testPkjcOneToOneSelectException() {
        setupTest(OptionalLazyNotFoundTest.PersonPkjcSelectException.class, 1L, false);
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.notfound.Person pCheck = session.find(.class, 1L);
            assertNotNull(pCheck);
            // eagerly loaded because @PKJC assumes ignoreNotFound
            assertTrue(Hibernate.isInitialized(pCheck.getCity()));
            assertNull(pCheck.getCity());
            /* assertFalse( Hibernate.isInitialized( pCheck.getCity() ) );
            try {
            Hibernate.initialize( pCheck.getCity() );
            fail( "Should have thrown ObjectNotFoundException" );
            }
            catch (ObjectNotFoundException expected) {
            session.getTransaction().setRollbackOnly();
            }
             */
        });
    }

    @Test
    public void testPkjcOneToOneSelectIgnore() {
        setupTest(OptionalLazyNotFoundTest.PersonPkjcSelectIgnore.class, 1L, false);
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.notfound.Person pCheck = session.find(.class, 1L);
            // Person is non-null and association is null.
            assertNotNull(pCheck);
            assertNull(pCheck.getCity());
        });
    }

    @Test
    public void testMapsIdOneToOneSelectException() {
        setupTest(OptionalLazyNotFoundTest.PersonMapsIdSelectException.class, 1L, true);
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
    public void testMapsIdOneToOneSelectIgnore() {
        setupTest(OptionalLazyNotFoundTest.PersonMapsIdSelectIgnore.class, 1L, true);
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.notfound.Person pCheck = session.find(.class, 1L);
            // Person is non-null association is null.
            assertNotNull(pCheck);
            assertNull(pCheck.getCity());
        });
    }

    @Test
    public void testMapsIdJoinColumnOneToOneSelectException() {
        setupTest(OptionalLazyNotFoundTest.PersonMapsIdColumnSelectException.class, 1L, true);
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
    public void testMapsIdJoinColumnOneToOneSelectIgnore() {
        setupTest(OptionalLazyNotFoundTest.PersonMapsIdColumnSelectIgnore.class, 1L, true);
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.notfound.Person pCheck = session.find(.class, 1L);
            // Person should be non-null;association should be null.
            assertNotNull(pCheck);
            assertNull(pCheck.getCity());
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

        public abstract OptionalLazyNotFoundTest.City getCity();

        public abstract void setCity(OptionalLazyNotFoundTest.City city);
    }

    @Entity
    @Table(name = "PersonOneToOneSelectException")
    public static class PersonOneToOneSelectException extends OptionalLazyNotFoundTest.Person {
        @Id
        private Long id;

        @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
        @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
        private OptionalLazyNotFoundTest.City city;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public OptionalLazyNotFoundTest.City getCity() {
            return city;
        }

        @Override
        public void setCity(OptionalLazyNotFoundTest.City city) {
            this.city = city;
        }
    }

    @Entity
    @Table(name = "PersonOneToOneSelectIgnore")
    public static class PersonOneToOneSelectIgnore extends OptionalLazyNotFoundTest.Person {
        @Id
        private Long id;

        @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
        @NotFound(action = NotFoundAction.IGNORE)
        @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
        private OptionalLazyNotFoundTest.City city;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public OptionalLazyNotFoundTest.City getCity() {
            return city;
        }

        @Override
        public void setCity(OptionalLazyNotFoundTest.City city) {
            this.city = city;
        }
    }

    @Entity
    @Table(name = "PersonManyToOneSelectException")
    public static class PersonManyToOneSelectException extends OptionalLazyNotFoundTest.Person {
        @Id
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
        @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
        private OptionalLazyNotFoundTest.City city;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public OptionalLazyNotFoundTest.City getCity() {
            return city;
        }

        @Override
        public void setCity(OptionalLazyNotFoundTest.City city) {
            this.city = city;
        }
    }

    @Entity
    @Table(name = "PersonManyToOneSelectIgnore")
    public static class PersonManyToOneSelectIgnore extends OptionalLazyNotFoundTest.Person {
        @Id
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
        @NotFound(action = NotFoundAction.IGNORE)
        @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
        private OptionalLazyNotFoundTest.City city;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public OptionalLazyNotFoundTest.City getCity() {
            return city;
        }

        @Override
        public void setCity(OptionalLazyNotFoundTest.City city) {
            this.city = city;
        }
    }

    @Entity
    @Table(name = "PersonPkjcSelectException")
    public static class PersonPkjcSelectException extends OptionalLazyNotFoundTest.Person {
        @Id
        private Long id;

        @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
        @PrimaryKeyJoinColumn
        @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
        private OptionalLazyNotFoundTest.City city;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public OptionalLazyNotFoundTest.City getCity() {
            return city;
        }

        @Override
        public void setCity(OptionalLazyNotFoundTest.City city) {
            this.city = city;
        }
    }

    @Entity
    @Table(name = "PersonPkjcSelectIgnore")
    public static class PersonPkjcSelectIgnore extends OptionalLazyNotFoundTest.Person {
        @Id
        private Long id;

        @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
        @PrimaryKeyJoinColumn
        @NotFound(action = NotFoundAction.IGNORE)
        @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
        private OptionalLazyNotFoundTest.City city;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public OptionalLazyNotFoundTest.City getCity() {
            return city;
        }

        @Override
        public void setCity(OptionalLazyNotFoundTest.City city) {
            this.city = city;
        }
    }

    @Entity
    @Table(name = "PersonMapsIdJoinException")
    public static class PersonMapsIdJoinException extends OptionalLazyNotFoundTest.Person {
        @Id
        private Long id;

        @OneToOne(fetch = FetchType.LAZY)
        @MapsId
        @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
        private OptionalLazyNotFoundTest.City city;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public OptionalLazyNotFoundTest.City getCity() {
            return city;
        }

        @Override
        public void setCity(OptionalLazyNotFoundTest.City city) {
            this.city = city;
        }
    }

    @Entity
    @Table(name = "PersonMapsIdJoinIgnore")
    public static class PersonMapsIdJoinIgnore extends OptionalLazyNotFoundTest.Person {
        @Id
        private Long id;

        @OneToOne(fetch = FetchType.LAZY)
        @MapsId
        @NotFound(action = NotFoundAction.IGNORE)
        @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
        private OptionalLazyNotFoundTest.City city;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public OptionalLazyNotFoundTest.City getCity() {
            return city;
        }

        @Override
        public void setCity(OptionalLazyNotFoundTest.City city) {
            this.city = city;
        }
    }

    @Entity
    @Table(name = "PersonMapsIdSelectException")
    public static class PersonMapsIdSelectException extends OptionalLazyNotFoundTest.Person {
        @Id
        private Long id;

        @OneToOne(fetch = FetchType.LAZY)
        @MapsId
        @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
        private OptionalLazyNotFoundTest.City city;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public OptionalLazyNotFoundTest.City getCity() {
            return city;
        }

        @Override
        public void setCity(OptionalLazyNotFoundTest.City city) {
            this.city = city;
        }
    }

    @Entity
    @Table(name = "PersonMapsIdSelectIgnore")
    public static class PersonMapsIdSelectIgnore extends OptionalLazyNotFoundTest.Person {
        @Id
        private Long id;

        @OneToOne(fetch = FetchType.LAZY)
        @MapsId
        @NotFound(action = NotFoundAction.IGNORE)
        @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
        private OptionalLazyNotFoundTest.City city;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public OptionalLazyNotFoundTest.City getCity() {
            return city;
        }

        @Override
        public void setCity(OptionalLazyNotFoundTest.City city) {
            this.city = city;
        }
    }

    @Entity
    @Table(name = "PersonMapsIdColumnJoinException")
    public static class PersonMapsIdColumnJoinException extends OptionalLazyNotFoundTest.Person {
        @Id
        private Long id;

        @OneToOne(fetch = FetchType.LAZY)
        @MapsId
        @JoinColumn(name = "fk", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
        private OptionalLazyNotFoundTest.City city;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public OptionalLazyNotFoundTest.City getCity() {
            return city;
        }

        @Override
        public void setCity(OptionalLazyNotFoundTest.City city) {
            this.city = city;
        }
    }

    @Entity
    @Table(name = "PersonMapsIdColumnJoinIgnore")
    public static class PersonMapsIdColumnJoinIgnore extends OptionalLazyNotFoundTest.Person {
        @Id
        private Long id;

        @OneToOne(fetch = FetchType.LAZY)
        @MapsId
        @NotFound(action = NotFoundAction.IGNORE)
        @JoinColumn(name = "fk", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
        private OptionalLazyNotFoundTest.City city;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public OptionalLazyNotFoundTest.City getCity() {
            return city;
        }

        @Override
        public void setCity(OptionalLazyNotFoundTest.City city) {
            this.city = city;
        }
    }

    @Entity
    @Table(name = "PersonMapsIdColumnSelectExcept")
    public static class PersonMapsIdColumnSelectException extends OptionalLazyNotFoundTest.Person {
        @Id
        private Long id;

        @OneToOne(fetch = FetchType.LAZY)
        @MapsId
        @JoinColumn(name = "fk", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
        private OptionalLazyNotFoundTest.City city;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public OptionalLazyNotFoundTest.City getCity() {
            return city;
        }

        @Override
        public void setCity(OptionalLazyNotFoundTest.City city) {
            this.city = city;
        }
    }

    @Entity
    @Table(name = "PersonMapsIdColumnSelectIgnore")
    public static class PersonMapsIdColumnSelectIgnore extends OptionalLazyNotFoundTest.Person {
        @Id
        private Long id;

        @OneToOne(fetch = FetchType.LAZY)
        @MapsId
        @NotFound(action = NotFoundAction.IGNORE)
        @JoinColumn(name = "fk", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
        private OptionalLazyNotFoundTest.City city;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public OptionalLazyNotFoundTest.City getCity() {
            return city;
        }

        @Override
        public void setCity(OptionalLazyNotFoundTest.City city) {
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

