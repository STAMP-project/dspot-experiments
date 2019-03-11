package org.hibernate.test.notfound;


import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
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
public class OptionalEagerRefNonPKNotFoundTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testOneToOneJoinIgnore() {
        setupTest(OptionalEagerRefNonPKNotFoundTest.PersonOneToOneJoinIgnore.class, 1L, false);
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.notfound.Person pCheck = session.find(.class, 1L);
            checkIgnore(pCheck);
        });
    }

    @Test
    public void testOneToOneSelectIgnore() {
        setupTest(OptionalEagerRefNonPKNotFoundTest.PersonOneToOneSelectIgnore.class, 1L, false);
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.notfound.Person pCheck = session.find(.class, 1L);
            checkIgnore(pCheck);
        });
    }

    @Test
    public void testManyToOneJoinIgnore() {
        setupTest(OptionalEagerRefNonPKNotFoundTest.PersonManyToOneJoinIgnore.class, 1L, false);
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.notfound.Person pCheck = session.find(.class, 1L);
            checkIgnore(pCheck);
        });
    }

    @Test
    public void testManyToOneSelectIgnore() {
        setupTest(OptionalEagerRefNonPKNotFoundTest.PersonManyToOneSelectIgnore.class, 1L, false);
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.notfound.Person pCheck = session.find(.class, 1L);
            checkIgnore(pCheck);
        });
    }

    @Test
    public void testPkjcOneToOneJoinException() {
        setupTest(OptionalEagerRefNonPKNotFoundTest.PersonPkjcJoinException.class, 1L, false);
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.notfound.Person pCheck = session.find(.class, 1L);
            // @OneToOne @PrimaryKeyJoinColumn always assumes @NotFound(IGNORE)
            checkIgnore(pCheck);
        });
    }

    @Test
    public void testPkjcOneToOneJoinIgnore() {
        setupTest(OptionalEagerRefNonPKNotFoundTest.PersonPkjcJoinIgnore.class, 1L, false);
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.notfound.Person pCheck = session.find(.class, 1L);
            // Person is non-null and association is null.
            checkIgnore(pCheck);
        });
    }

    @Test
    public void testPkjcOneToOneSelectException() {
        setupTest(OptionalEagerRefNonPKNotFoundTest.PersonPkjcSelectException.class, 1L, false);
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.notfound.Person pCheck = session.find(.class, 1L);
            // @OneToOne @PrimaryKeyJoinColumn always assumes @NotFound(IGNORE)
            checkIgnore(pCheck);
        });
    }

    @Test
    public void testPkjcOneToOneSelectIgnore() {
        setupTest(OptionalEagerRefNonPKNotFoundTest.PersonPkjcSelectIgnore.class, 1L, false);
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.notfound.Person pCheck = session.find(.class, 1L);
            // Person is non-null and association is null.
            checkIgnore(pCheck);
        });
    }

    @Test
    public void testMapsIdOneToOneJoinIgnore() {
        setupTest(OptionalEagerRefNonPKNotFoundTest.PersonMapsIdJoinIgnore.class, 1L, true);
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.notfound.Person pCheck = session.find(.class, 1L);
            // Person is non-null association is null.
            checkIgnore(pCheck);
        });
    }

    @Test
    public void testMapsIdOneToOneSelectIgnore() {
        setupTest(OptionalEagerRefNonPKNotFoundTest.PersonMapsIdSelectIgnore.class, 1L, true);
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.notfound.Person pCheck = session.find(.class, 1L);
            // Person is non-null association is null.
            checkIgnore(pCheck);
        });
    }

    @Test
    public void testMapsIdJoinColumnOneToOneJoinIgnore() {
        setupTest(OptionalEagerRefNonPKNotFoundTest.PersonMapsIdColumnJoinIgnore.class, 1L, true);
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.notfound.Person pCheck = session.find(.class, 1L);
            checkIgnore(pCheck);
        });
    }

    @Test
    public void testMapsIdJoinColumnOneToOneSelectIgnore() {
        setupTest(OptionalEagerRefNonPKNotFoundTest.PersonMapsIdColumnSelectIgnore.class, 1L, true);
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.notfound.Person pCheck = session.find(.class, 1L);
            checkIgnore(pCheck);
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

        public abstract OptionalEagerRefNonPKNotFoundTest.City getCity();

        public abstract void setCity(OptionalEagerRefNonPKNotFoundTest.City city);
    }

    @Entity
    @Table(name = "PersonOneToOneJoinException")
    public static class PersonOneToOneJoinException extends OptionalEagerRefNonPKNotFoundTest.Person {
        @Id
        private Long id;

        @OneToOne(cascade = CascadeType.PERSIST)
        @JoinColumn(name = "cityName", referencedColumnName = "name", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
        @Fetch(FetchMode.JOIN)
        private OptionalEagerRefNonPKNotFoundTest.City city;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public OptionalEagerRefNonPKNotFoundTest.City getCity() {
            return city;
        }

        @Override
        public void setCity(OptionalEagerRefNonPKNotFoundTest.City city) {
            this.city = city;
        }
    }

    @Entity
    @Table(name = "PersonOneToOneJoinIgnore")
    public static class PersonOneToOneJoinIgnore extends OptionalEagerRefNonPKNotFoundTest.Person {
        @Id
        private Long id;

        @OneToOne(cascade = CascadeType.PERSIST)
        @NotFound(action = NotFoundAction.IGNORE)
        @JoinColumn(name = "cityName", referencedColumnName = "name", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
        @Fetch(FetchMode.JOIN)
        private OptionalEagerRefNonPKNotFoundTest.City city;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public OptionalEagerRefNonPKNotFoundTest.City getCity() {
            return city;
        }

        @Override
        public void setCity(OptionalEagerRefNonPKNotFoundTest.City city) {
            this.city = city;
        }
    }

    @Entity
    @Table(name = "PersonOneToOneSelectException")
    public static class PersonOneToOneSelectException extends OptionalEagerRefNonPKNotFoundTest.Person {
        @Id
        private Long id;

        @OneToOne(cascade = CascadeType.PERSIST)
        @JoinColumn(name = "cityName", referencedColumnName = "name", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
        @Fetch(FetchMode.SELECT)
        private OptionalEagerRefNonPKNotFoundTest.City city;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public OptionalEagerRefNonPKNotFoundTest.City getCity() {
            return city;
        }

        @Override
        public void setCity(OptionalEagerRefNonPKNotFoundTest.City city) {
            this.city = city;
        }
    }

    @Entity
    @Table(name = "PersonOneToOneSelectIgnore")
    public static class PersonOneToOneSelectIgnore extends OptionalEagerRefNonPKNotFoundTest.Person {
        @Id
        private Long id;

        @OneToOne(cascade = CascadeType.PERSIST)
        @NotFound(action = NotFoundAction.IGNORE)
        @JoinColumn(name = "cityName", referencedColumnName = "name", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
        @Fetch(FetchMode.SELECT)
        private OptionalEagerRefNonPKNotFoundTest.City city;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public OptionalEagerRefNonPKNotFoundTest.City getCity() {
            return city;
        }

        @Override
        public void setCity(OptionalEagerRefNonPKNotFoundTest.City city) {
            this.city = city;
        }
    }

    @Entity
    @Table(name = "PersonManyToOneJoinException")
    public static class PersonManyToOneJoinException extends OptionalEagerRefNonPKNotFoundTest.Person {
        @Id
        private Long id;

        @ManyToOne(cascade = CascadeType.PERSIST)
        @JoinColumn(name = "cityName", referencedColumnName = "name", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
        @Fetch(FetchMode.JOIN)
        private OptionalEagerRefNonPKNotFoundTest.City city;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public OptionalEagerRefNonPKNotFoundTest.City getCity() {
            return city;
        }

        @Override
        public void setCity(OptionalEagerRefNonPKNotFoundTest.City city) {
            this.city = city;
        }
    }

    @Entity
    @Table(name = "PersonManyToOneJoinIgnore")
    public static class PersonManyToOneJoinIgnore extends OptionalEagerRefNonPKNotFoundTest.Person {
        @Id
        private Long id;

        @ManyToOne(cascade = CascadeType.PERSIST)
        @NotFound(action = NotFoundAction.IGNORE)
        @JoinColumn(name = "cityName", referencedColumnName = "name", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
        @Fetch(FetchMode.JOIN)
        private OptionalEagerRefNonPKNotFoundTest.City city;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public OptionalEagerRefNonPKNotFoundTest.City getCity() {
            return city;
        }

        @Override
        public void setCity(OptionalEagerRefNonPKNotFoundTest.City city) {
            this.city = city;
        }
    }

    @Entity
    @Table(name = "PersonManyToOneSelectException")
    public static class PersonManyToOneSelectException extends OptionalEagerRefNonPKNotFoundTest.Person {
        @Id
        private Long id;

        @ManyToOne(cascade = CascadeType.PERSIST)
        @JoinColumn(name = "cityName", referencedColumnName = "name", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
        @Fetch(FetchMode.SELECT)
        private OptionalEagerRefNonPKNotFoundTest.City city;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public OptionalEagerRefNonPKNotFoundTest.City getCity() {
            return city;
        }

        @Override
        public void setCity(OptionalEagerRefNonPKNotFoundTest.City city) {
            this.city = city;
        }
    }

    @Entity
    @Table(name = "PersonManyToOneSelectIgnore")
    public static class PersonManyToOneSelectIgnore extends OptionalEagerRefNonPKNotFoundTest.Person {
        @Id
        private Long id;

        @ManyToOne(cascade = CascadeType.PERSIST)
        @NotFound(action = NotFoundAction.IGNORE)
        @JoinColumn(name = "cityName", referencedColumnName = "name", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
        @Fetch(FetchMode.SELECT)
        private OptionalEagerRefNonPKNotFoundTest.City city;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public OptionalEagerRefNonPKNotFoundTest.City getCity() {
            return city;
        }

        @Override
        public void setCity(OptionalEagerRefNonPKNotFoundTest.City city) {
            this.city = city;
        }
    }

    @Entity
    @Table(name = "PersonPkjcJoinException")
    public static class PersonPkjcJoinException extends OptionalEagerRefNonPKNotFoundTest.Person {
        @Id
        private Long id;

        @OneToOne(cascade = CascadeType.PERSIST)
        @PrimaryKeyJoinColumn
        @Fetch(FetchMode.JOIN)
        @JoinColumn(name = "cityName", referencedColumnName = "name", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
        private OptionalEagerRefNonPKNotFoundTest.City city;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public OptionalEagerRefNonPKNotFoundTest.City getCity() {
            return city;
        }

        @Override
        public void setCity(OptionalEagerRefNonPKNotFoundTest.City city) {
            this.city = city;
        }
    }

    @Entity
    @Table(name = "PersonPkjcJoinIgnore")
    public static class PersonPkjcJoinIgnore extends OptionalEagerRefNonPKNotFoundTest.Person {
        @Id
        private Long id;

        @OneToOne(cascade = CascadeType.PERSIST)
        @PrimaryKeyJoinColumn
        @NotFound(action = NotFoundAction.IGNORE)
        @Fetch(FetchMode.JOIN)
        @JoinColumn(name = "cityName", referencedColumnName = "name", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
        private OptionalEagerRefNonPKNotFoundTest.City city;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public OptionalEagerRefNonPKNotFoundTest.City getCity() {
            return city;
        }

        @Override
        public void setCity(OptionalEagerRefNonPKNotFoundTest.City city) {
            this.city = city;
        }
    }

    @Entity
    @Table(name = "PersonPkjcSelectException")
    public static class PersonPkjcSelectException extends OptionalEagerRefNonPKNotFoundTest.Person {
        @Id
        private Long id;

        @OneToOne(cascade = CascadeType.PERSIST)
        @PrimaryKeyJoinColumn
        @Fetch(FetchMode.SELECT)
        @JoinColumn(name = "cityName", referencedColumnName = "name", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
        private OptionalEagerRefNonPKNotFoundTest.City city;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public OptionalEagerRefNonPKNotFoundTest.City getCity() {
            return city;
        }

        @Override
        public void setCity(OptionalEagerRefNonPKNotFoundTest.City city) {
            this.city = city;
        }
    }

    @Entity
    @Table(name = "PersonPkjcSelectIgnore")
    public static class PersonPkjcSelectIgnore extends OptionalEagerRefNonPKNotFoundTest.Person {
        @Id
        private Long id;

        @OneToOne(cascade = CascadeType.PERSIST)
        @PrimaryKeyJoinColumn
        @NotFound(action = NotFoundAction.IGNORE)
        @Fetch(FetchMode.SELECT)
        @JoinColumn(name = "cityName", referencedColumnName = "name", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
        private OptionalEagerRefNonPKNotFoundTest.City city;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public OptionalEagerRefNonPKNotFoundTest.City getCity() {
            return city;
        }

        @Override
        public void setCity(OptionalEagerRefNonPKNotFoundTest.City city) {
            this.city = city;
        }
    }

    @Entity
    @Table(name = "PersonMapsIdJoinException")
    public static class PersonMapsIdJoinException extends OptionalEagerRefNonPKNotFoundTest.Person {
        @Id
        private Long id;

        @OneToOne
        @MapsId
        @Fetch(FetchMode.JOIN)
        @JoinColumn(name = "cityName", referencedColumnName = "name", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
        private OptionalEagerRefNonPKNotFoundTest.City city;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public OptionalEagerRefNonPKNotFoundTest.City getCity() {
            return city;
        }

        @Override
        public void setCity(OptionalEagerRefNonPKNotFoundTest.City city) {
            this.city = city;
        }
    }

    @Entity
    @Table(name = "PersonMapsIdJoinIgnore")
    public static class PersonMapsIdJoinIgnore extends OptionalEagerRefNonPKNotFoundTest.Person {
        @Id
        private Long id;

        @OneToOne
        @MapsId
        @NotFound(action = NotFoundAction.IGNORE)
        @Fetch(FetchMode.JOIN)
        @JoinColumn(name = "cityName", referencedColumnName = "name", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
        private OptionalEagerRefNonPKNotFoundTest.City city;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public OptionalEagerRefNonPKNotFoundTest.City getCity() {
            return city;
        }

        @Override
        public void setCity(OptionalEagerRefNonPKNotFoundTest.City city) {
            this.city = city;
        }
    }

    @Entity
    @Table(name = "PersonMapsIdSelectException")
    public static class PersonMapsIdSelectException extends OptionalEagerRefNonPKNotFoundTest.Person {
        @Id
        private Long id;

        @OneToOne
        @MapsId
        @Fetch(FetchMode.SELECT)
        @JoinColumn(name = "cityName", referencedColumnName = "name", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
        private OptionalEagerRefNonPKNotFoundTest.City city;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public OptionalEagerRefNonPKNotFoundTest.City getCity() {
            return city;
        }

        @Override
        public void setCity(OptionalEagerRefNonPKNotFoundTest.City city) {
            this.city = city;
        }
    }

    @Entity
    @Table(name = "PersonMapsIdSelectIgnore")
    public static class PersonMapsIdSelectIgnore extends OptionalEagerRefNonPKNotFoundTest.Person {
        @Id
        private Long id;

        @OneToOne
        @MapsId
        @NotFound(action = NotFoundAction.IGNORE)
        @Fetch(FetchMode.SELECT)
        @JoinColumn(name = "cityName", referencedColumnName = "name", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
        private OptionalEagerRefNonPKNotFoundTest.City city;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public OptionalEagerRefNonPKNotFoundTest.City getCity() {
            return city;
        }

        @Override
        public void setCity(OptionalEagerRefNonPKNotFoundTest.City city) {
            this.city = city;
        }
    }

    @Entity
    @Table(name = "PersonMapsIdColumnJoinException")
    public static class PersonMapsIdColumnJoinException extends OptionalEagerRefNonPKNotFoundTest.Person {
        @Id
        private Long id;

        @OneToOne
        @MapsId
        @JoinColumn(name = "cityName", referencedColumnName = "name", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
        @Fetch(FetchMode.JOIN)
        private OptionalEagerRefNonPKNotFoundTest.City city;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public OptionalEagerRefNonPKNotFoundTest.City getCity() {
            return city;
        }

        @Override
        public void setCity(OptionalEagerRefNonPKNotFoundTest.City city) {
            this.city = city;
        }
    }

    @Entity
    @Table(name = "PersonMapsIdColumnJoinIgnore")
    public static class PersonMapsIdColumnJoinIgnore extends OptionalEagerRefNonPKNotFoundTest.Person {
        @Id
        private Long id;

        @OneToOne
        @MapsId
        @NotFound(action = NotFoundAction.IGNORE)
        @JoinColumn(name = "cityName", referencedColumnName = "name", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
        @Fetch(FetchMode.JOIN)
        private OptionalEagerRefNonPKNotFoundTest.City city;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public OptionalEagerRefNonPKNotFoundTest.City getCity() {
            return city;
        }

        @Override
        public void setCity(OptionalEagerRefNonPKNotFoundTest.City city) {
            this.city = city;
        }
    }

    @Entity
    @Table(name = "PersonMapsIdColumnSelectException")
    public static class PersonMapsIdColumnSelectException extends OptionalEagerRefNonPKNotFoundTest.Person {
        @Id
        private Long id;

        @OneToOne
        @MapsId
        @JoinColumn(name = "cityName", referencedColumnName = "name", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
        @Fetch(FetchMode.SELECT)
        private OptionalEagerRefNonPKNotFoundTest.City city;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public OptionalEagerRefNonPKNotFoundTest.City getCity() {
            return city;
        }

        @Override
        public void setCity(OptionalEagerRefNonPKNotFoundTest.City city) {
            this.city = city;
        }
    }

    @Entity
    @Table(name = "PersonMapsIdColumnSelectIgnore")
    public static class PersonMapsIdColumnSelectIgnore extends OptionalEagerRefNonPKNotFoundTest.Person {
        @Id
        private Long id;

        @OneToOne
        @MapsId
        @NotFound(action = NotFoundAction.IGNORE)
        @JoinColumn(name = "cityName", referencedColumnName = "name", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
        @Fetch(FetchMode.SELECT)
        private OptionalEagerRefNonPKNotFoundTest.City city;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public OptionalEagerRefNonPKNotFoundTest.City getCity() {
            return city;
        }

        @Override
        public void setCity(OptionalEagerRefNonPKNotFoundTest.City city) {
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

