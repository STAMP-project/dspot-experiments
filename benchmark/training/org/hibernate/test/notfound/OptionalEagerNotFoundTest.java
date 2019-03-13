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
import org.junit.Test;


/**
 *
 *
 * @author Gail Badner
 */
@TestForIssue(jiraKey = "HHH-12436")
public class OptionalEagerNotFoundTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testOneToOneJoinIgnore() {
        setupTest(OptionalEagerNotFoundTest.PersonOneToOneJoinIgnore.class, 1L, false);
        executeIgnoreTest(OptionalEagerNotFoundTest.PersonOneToOneJoinIgnore.class, 1L);
    }

    @Test
    public void testOneToOneSelectIgnore() {
        setupTest(OptionalEagerNotFoundTest.PersonOneToOneSelectIgnore.class, 1L, false);
        executeIgnoreTest(OptionalEagerNotFoundTest.PersonOneToOneSelectIgnore.class, 1L);
    }

    @Test
    public void testManyToOneJoinIgnore() {
        setupTest(OptionalEagerNotFoundTest.PersonManyToOneJoinIgnore.class, 1L, false);
        executeIgnoreTest(OptionalEagerNotFoundTest.PersonManyToOneJoinIgnore.class, 1L);
    }

    @Test
    public void testManyToOneSelectIgnore() {
        setupTest(OptionalEagerNotFoundTest.PersonManyToOneSelectIgnore.class, 1L, false);
        executeIgnoreTest(OptionalEagerNotFoundTest.PersonManyToOneSelectIgnore.class, 1L);
    }

    @Test
    public void testPkjcOneToOneJoinException() {
        setupTest(OptionalEagerNotFoundTest.PersonPkjcJoinException.class, 1L, false);
        // optional @OneToOne @PKJC implicitly maps @NotFound(IGNORE)
        executeIgnoreTest(OptionalEagerNotFoundTest.PersonPkjcJoinException.class, 1L);
    }

    @Test
    public void testPkjcOneToOneJoinIgnore() {
        setupTest(OptionalEagerNotFoundTest.PersonPkjcJoinIgnore.class, 1L, false);
        executeIgnoreTest(OptionalEagerNotFoundTest.PersonPkjcJoinIgnore.class, 1L);
    }

    @Test
    public void testPkjcOneToOneSelectException() {
        setupTest(OptionalEagerNotFoundTest.PersonPkjcSelectException.class, 1L, false);
        // optional @OneToOne @PKJC implicitly maps @NotFound(IGNORE)
        executeIgnoreTest(OptionalEagerNotFoundTest.PersonPkjcSelectException.class, 1L);
    }

    @Test
    public void testPkjcOneToOneSelectIgnore() {
        setupTest(OptionalEagerNotFoundTest.PersonPkjcSelectIgnore.class, 1L, false);
        executeIgnoreTest(OptionalEagerNotFoundTest.PersonPkjcSelectIgnore.class, 1L);
    }

    @Test
    public void testMapsIdOneToOneJoinIgnore() {
        setupTest(OptionalEagerNotFoundTest.PersonMapsIdJoinIgnore.class, 1L, true);
        executeIgnoreTest(OptionalEagerNotFoundTest.PersonMapsIdJoinIgnore.class, 1L);
    }

    @Test
    public void testMapsIdOneToOneSelectIgnore() {
        setupTest(OptionalEagerNotFoundTest.PersonMapsIdSelectIgnore.class, 1L, true);
        executeIgnoreTest(OptionalEagerNotFoundTest.PersonMapsIdSelectIgnore.class, 1L);
    }

    @Test
    public void testMapsIdJoinColumnOneToOneJoinIgnore() {
        setupTest(OptionalEagerNotFoundTest.PersonMapsIdColumnJoinIgnore.class, 1L, true);
        executeIgnoreTest(OptionalEagerNotFoundTest.PersonMapsIdColumnJoinIgnore.class, 1L);
    }

    @Test
    public void testMapsIdJoinColumnOneToOneSelectIgnore() {
        setupTest(OptionalEagerNotFoundTest.PersonMapsIdColumnSelectIgnore.class, 1L, true);
        executeIgnoreTest(OptionalEagerNotFoundTest.PersonMapsIdColumnSelectIgnore.class, 1L);
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

        public abstract OptionalEagerNotFoundTest.City getCity();

        public abstract void setCity(OptionalEagerNotFoundTest.City city);
    }

    @Entity
    @Table(name = "PersonOneToOneJoinIgnore")
    public static class PersonOneToOneJoinIgnore extends OptionalEagerNotFoundTest.Person {
        @Id
        private Long id;

        @OneToOne(cascade = CascadeType.PERSIST)
        @NotFound(action = NotFoundAction.IGNORE)
        @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
        @Fetch(FetchMode.JOIN)
        private OptionalEagerNotFoundTest.City city;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public OptionalEagerNotFoundTest.City getCity() {
            return city;
        }

        @Override
        public void setCity(OptionalEagerNotFoundTest.City city) {
            this.city = city;
        }
    }

    @Entity
    @Table(name = "PersonOneToOneSelectIgnore")
    public static class PersonOneToOneSelectIgnore extends OptionalEagerNotFoundTest.Person {
        @Id
        private Long id;

        @OneToOne(cascade = CascadeType.PERSIST)
        @NotFound(action = NotFoundAction.IGNORE)
        @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
        @Fetch(FetchMode.SELECT)
        private OptionalEagerNotFoundTest.City city;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public OptionalEagerNotFoundTest.City getCity() {
            return city;
        }

        @Override
        public void setCity(OptionalEagerNotFoundTest.City city) {
            this.city = city;
        }
    }

    @Entity
    @Table(name = "PersonManyToOneJoinIgnore")
    public static class PersonManyToOneJoinIgnore extends OptionalEagerNotFoundTest.Person {
        @Id
        private Long id;

        @ManyToOne(cascade = CascadeType.PERSIST)
        @NotFound(action = NotFoundAction.IGNORE)
        @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
        @Fetch(FetchMode.JOIN)
        private OptionalEagerNotFoundTest.City city;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public OptionalEagerNotFoundTest.City getCity() {
            return city;
        }

        @Override
        public void setCity(OptionalEagerNotFoundTest.City city) {
            this.city = city;
        }
    }

    @Entity
    @Table(name = "PersonManyToOneSelectIgnore")
    public static class PersonManyToOneSelectIgnore extends OptionalEagerNotFoundTest.Person {
        @Id
        private Long id;

        @ManyToOne(cascade = CascadeType.PERSIST)
        @NotFound(action = NotFoundAction.IGNORE)
        @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
        @Fetch(FetchMode.SELECT)
        private OptionalEagerNotFoundTest.City city;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public OptionalEagerNotFoundTest.City getCity() {
            return city;
        }

        @Override
        public void setCity(OptionalEagerNotFoundTest.City city) {
            this.city = city;
        }
    }

    @Entity
    @Table(name = "PersonPkjcJoinException")
    public static class PersonPkjcJoinException extends OptionalEagerNotFoundTest.Person {
        @Id
        private Long id;

        @OneToOne(cascade = CascadeType.PERSIST)
        @PrimaryKeyJoinColumn
        @Fetch(FetchMode.JOIN)
        @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
        private OptionalEagerNotFoundTest.City city;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public OptionalEagerNotFoundTest.City getCity() {
            return city;
        }

        @Override
        public void setCity(OptionalEagerNotFoundTest.City city) {
            this.city = city;
        }
    }

    @Entity
    @Table(name = "PersonPkjcJoinIgnore")
    public static class PersonPkjcJoinIgnore extends OptionalEagerNotFoundTest.Person {
        @Id
        private Long id;

        @OneToOne(cascade = CascadeType.PERSIST)
        @PrimaryKeyJoinColumn
        @NotFound(action = NotFoundAction.IGNORE)
        @Fetch(FetchMode.JOIN)
        @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
        private OptionalEagerNotFoundTest.City city;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public OptionalEagerNotFoundTest.City getCity() {
            return city;
        }

        @Override
        public void setCity(OptionalEagerNotFoundTest.City city) {
            this.city = city;
        }
    }

    @Entity
    @Table(name = "PersonPkjcSelectException")
    public static class PersonPkjcSelectException extends OptionalEagerNotFoundTest.Person {
        @Id
        private Long id;

        @OneToOne(cascade = CascadeType.PERSIST)
        @PrimaryKeyJoinColumn
        @Fetch(FetchMode.SELECT)
        @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
        private OptionalEagerNotFoundTest.City city;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public OptionalEagerNotFoundTest.City getCity() {
            return city;
        }

        @Override
        public void setCity(OptionalEagerNotFoundTest.City city) {
            this.city = city;
        }
    }

    @Entity
    @Table(name = "PersonPkjcSelectIgnore")
    public static class PersonPkjcSelectIgnore extends OptionalEagerNotFoundTest.Person {
        @Id
        private Long id;

        @OneToOne(cascade = CascadeType.PERSIST)
        @PrimaryKeyJoinColumn
        @NotFound(action = NotFoundAction.IGNORE)
        @Fetch(FetchMode.SELECT)
        @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
        private OptionalEagerNotFoundTest.City city;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public OptionalEagerNotFoundTest.City getCity() {
            return city;
        }

        @Override
        public void setCity(OptionalEagerNotFoundTest.City city) {
            this.city = city;
        }
    }

    @Entity
    @Table(name = "PersonMapsIdJoinIgnore")
    public static class PersonMapsIdJoinIgnore extends OptionalEagerNotFoundTest.Person {
        @Id
        private Long id;

        @OneToOne
        @MapsId
        @NotFound(action = NotFoundAction.IGNORE)
        @Fetch(FetchMode.JOIN)
        @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
        private OptionalEagerNotFoundTest.City city;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public OptionalEagerNotFoundTest.City getCity() {
            return city;
        }

        @Override
        public void setCity(OptionalEagerNotFoundTest.City city) {
            this.city = city;
        }
    }

    @Entity
    @Table(name = "PersonMapsIdSelectIgnore")
    public static class PersonMapsIdSelectIgnore extends OptionalEagerNotFoundTest.Person {
        @Id
        private Long id;

        @OneToOne
        @MapsId
        @NotFound(action = NotFoundAction.IGNORE)
        @Fetch(FetchMode.SELECT)
        @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
        private OptionalEagerNotFoundTest.City city;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public OptionalEagerNotFoundTest.City getCity() {
            return city;
        }

        @Override
        public void setCity(OptionalEagerNotFoundTest.City city) {
            this.city = city;
        }
    }

    @Entity
    @Table(name = "PersonMapsIdColumnJoinIgnore")
    public static class PersonMapsIdColumnJoinIgnore extends OptionalEagerNotFoundTest.Person {
        @Id
        private Long id;

        @OneToOne
        @MapsId
        @NotFound(action = NotFoundAction.IGNORE)
        @JoinColumn(name = "fk", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
        @Fetch(FetchMode.JOIN)
        private OptionalEagerNotFoundTest.City city;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public OptionalEagerNotFoundTest.City getCity() {
            return city;
        }

        @Override
        public void setCity(OptionalEagerNotFoundTest.City city) {
            this.city = city;
        }
    }

    @Entity
    @Table(name = "PersonMapsIdColumnSelectIgnore")
    public static class PersonMapsIdColumnSelectIgnore extends OptionalEagerNotFoundTest.Person {
        @Id
        private Long id;

        @OneToOne
        @MapsId
        @NotFound(action = NotFoundAction.IGNORE)
        @JoinColumn(name = "fk", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
        @Fetch(FetchMode.SELECT)
        private OptionalEagerNotFoundTest.City city;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public OptionalEagerNotFoundTest.City getCity() {
            return city;
        }

        @Override
        public void setCity(OptionalEagerNotFoundTest.City city) {
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

