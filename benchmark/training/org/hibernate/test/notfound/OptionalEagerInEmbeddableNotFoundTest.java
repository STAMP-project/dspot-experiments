package org.hibernate.test.notfound;


import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.ConstraintMode;
import javax.persistence.Embeddable;
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
public class OptionalEagerInEmbeddableNotFoundTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testOneToOneJoinIgnore() {
        setupTest(OptionalEagerInEmbeddableNotFoundTest.PersonOneToOneJoinIgnore.class, 1L, false);
        executeIgnoreTest(OptionalEagerInEmbeddableNotFoundTest.PersonOneToOneJoinIgnore.class, 1L);
    }

    @Test
    public void testOneToOneSelectIgnore() {
        setupTest(OptionalEagerInEmbeddableNotFoundTest.PersonOneToOneSelectIgnore.class, 1L, false);
        executeIgnoreTest(OptionalEagerInEmbeddableNotFoundTest.PersonOneToOneSelectIgnore.class, 1L);
    }

    @Test
    public void testManyToOneJoinIgnore() {
        setupTest(OptionalEagerInEmbeddableNotFoundTest.PersonManyToOneJoinIgnore.class, 1L, false);
        executeIgnoreTest(OptionalEagerInEmbeddableNotFoundTest.PersonManyToOneJoinIgnore.class, 1L);
    }

    @Test
    public void testManyToOneSelectIgnore() {
        setupTest(OptionalEagerInEmbeddableNotFoundTest.PersonManyToOneSelectIgnore.class, 1L, false);
        executeIgnoreTest(OptionalEagerInEmbeddableNotFoundTest.PersonManyToOneSelectIgnore.class, 1L);
    }

    @Test
    public void testPkjcOneToOneJoinException() {
        setupTest(OptionalEagerInEmbeddableNotFoundTest.PersonPkjcJoinException.class, 1L, false);
        // optional @OneToOne @PKJC implicitly maps @NotFound(IGNORE)
        executeIgnoreTest(OptionalEagerInEmbeddableNotFoundTest.PersonPkjcJoinException.class, 1L);
    }

    @Test
    public void testPkjcOneToOneJoinIgnore() {
        setupTest(OptionalEagerInEmbeddableNotFoundTest.PersonPkjcJoinIgnore.class, 1L, false);
        executeIgnoreTest(OptionalEagerInEmbeddableNotFoundTest.PersonPkjcJoinIgnore.class, 1L);
    }

    @Test
    public void testPkjcOneToOneSelectException() {
        setupTest(OptionalEagerInEmbeddableNotFoundTest.PersonPkjcSelectException.class, 1L, false);
        // optional @OneToOne @PKJC implicitly maps @NotFound(IGNORE)
        executeIgnoreTest(OptionalEagerInEmbeddableNotFoundTest.PersonPkjcSelectException.class, 1L);
    }

    @Test
    public void testPkjcOneToOneSelectIgnore() {
        setupTest(OptionalEagerInEmbeddableNotFoundTest.PersonPkjcSelectIgnore.class, 1L, false);
        executeIgnoreTest(OptionalEagerInEmbeddableNotFoundTest.PersonPkjcSelectIgnore.class, 1L);
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

        public abstract OptionalEagerInEmbeddableNotFoundTest.City getCity();

        public abstract void setCity(OptionalEagerInEmbeddableNotFoundTest.City city);
    }

    @Entity
    @Table(name = "PersonOneToOneJoinIgnore")
    public static class PersonOneToOneJoinIgnore extends OptionalEagerInEmbeddableNotFoundTest.Person {
        @Id
        private Long id;

        private OptionalEagerInEmbeddableNotFoundTest.PersonOneToOneJoinIgnore.CityInEmbeddable cityInEmbeddable;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public OptionalEagerInEmbeddableNotFoundTest.PersonOneToOneJoinIgnore.CityInEmbeddable getCityInEmbeddable() {
            return cityInEmbeddable;
        }

        public void setCityInEmbeddable(OptionalEagerInEmbeddableNotFoundTest.PersonOneToOneJoinIgnore.CityInEmbeddable cityInEmbeddable) {
            this.cityInEmbeddable = cityInEmbeddable;
        }

        public OptionalEagerInEmbeddableNotFoundTest.City getCity() {
            return (cityInEmbeddable) == null ? null : cityInEmbeddable.getCity();
        }

        @Override
        public void setCity(OptionalEagerInEmbeddableNotFoundTest.City city) {
            if ((cityInEmbeddable) == null) {
                cityInEmbeddable = new OptionalEagerInEmbeddableNotFoundTest.PersonOneToOneJoinIgnore.CityInEmbeddable();
            }
            cityInEmbeddable.setCity(city);
        }

        @Embeddable
        public static class CityInEmbeddable {
            @OneToOne(cascade = CascadeType.PERSIST)
            @NotFound(action = NotFoundAction.IGNORE)
            @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
            @Fetch(FetchMode.JOIN)
            private OptionalEagerInEmbeddableNotFoundTest.City city;

            public OptionalEagerInEmbeddableNotFoundTest.City getCity() {
                return city;
            }

            public void setCity(OptionalEagerInEmbeddableNotFoundTest.City city) {
                this.city = city;
            }
        }
    }

    @Entity
    @Table(name = "PersonOneToOneSelectIgnore")
    public static class PersonOneToOneSelectIgnore extends OptionalEagerInEmbeddableNotFoundTest.Person {
        @Id
        private Long id;

        private OptionalEagerInEmbeddableNotFoundTest.PersonOneToOneSelectIgnore.CityInEmbeddable cityInEmbeddable;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public OptionalEagerInEmbeddableNotFoundTest.PersonOneToOneSelectIgnore.CityInEmbeddable getCityInEmbeddable() {
            return cityInEmbeddable;
        }

        public void setCityInEmbeddable(OptionalEagerInEmbeddableNotFoundTest.PersonOneToOneSelectIgnore.CityInEmbeddable cityInEmbeddable) {
            this.cityInEmbeddable = cityInEmbeddable;
        }

        public OptionalEagerInEmbeddableNotFoundTest.City getCity() {
            return (cityInEmbeddable) == null ? null : cityInEmbeddable.getCity();
        }

        @Override
        public void setCity(OptionalEagerInEmbeddableNotFoundTest.City city) {
            if ((cityInEmbeddable) == null) {
                cityInEmbeddable = new OptionalEagerInEmbeddableNotFoundTest.PersonOneToOneSelectIgnore.CityInEmbeddable();
            }
            cityInEmbeddable.setCity(city);
        }

        @Embeddable
        public static class CityInEmbeddable {
            @OneToOne(cascade = CascadeType.PERSIST)
            @NotFound(action = NotFoundAction.IGNORE)
            @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
            @Fetch(FetchMode.SELECT)
            private OptionalEagerInEmbeddableNotFoundTest.City city;

            public OptionalEagerInEmbeddableNotFoundTest.City getCity() {
                return city;
            }

            public void setCity(OptionalEagerInEmbeddableNotFoundTest.City city) {
                this.city = city;
            }
        }
    }

    @Entity
    @Table(name = "PersonManyToOneJoinIgnore")
    public static class PersonManyToOneJoinIgnore extends OptionalEagerInEmbeddableNotFoundTest.Person {
        @Id
        private Long id;

        private OptionalEagerInEmbeddableNotFoundTest.PersonManyToOneJoinIgnore.CityInEmbeddable cityInEmbeddable;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public OptionalEagerInEmbeddableNotFoundTest.PersonManyToOneJoinIgnore.CityInEmbeddable getCityInEmbeddable() {
            return cityInEmbeddable;
        }

        public void setCityInEmbeddable(OptionalEagerInEmbeddableNotFoundTest.PersonManyToOneJoinIgnore.CityInEmbeddable cityInEmbeddable) {
            this.cityInEmbeddable = cityInEmbeddable;
        }

        public OptionalEagerInEmbeddableNotFoundTest.City getCity() {
            return (cityInEmbeddable) == null ? null : cityInEmbeddable.getCity();
        }

        @Override
        public void setCity(OptionalEagerInEmbeddableNotFoundTest.City city) {
            if ((cityInEmbeddable) == null) {
                cityInEmbeddable = new OptionalEagerInEmbeddableNotFoundTest.PersonManyToOneJoinIgnore.CityInEmbeddable();
            }
            cityInEmbeddable.setCity(city);
        }

        @Embeddable
        public static class CityInEmbeddable {
            @ManyToOne(cascade = CascadeType.PERSIST)
            @NotFound(action = NotFoundAction.IGNORE)
            @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
            @Fetch(FetchMode.JOIN)
            private OptionalEagerInEmbeddableNotFoundTest.City city;

            public OptionalEagerInEmbeddableNotFoundTest.City getCity() {
                return city;
            }

            public void setCity(OptionalEagerInEmbeddableNotFoundTest.City city) {
                this.city = city;
            }
        }
    }

    @Entity
    @Table(name = "PersonManyToOneSelectIgnore")
    public static class PersonManyToOneSelectIgnore extends OptionalEagerInEmbeddableNotFoundTest.Person {
        @Id
        private Long id;

        private OptionalEagerInEmbeddableNotFoundTest.PersonManyToOneSelectIgnore.CityInEmbeddable cityInEmbeddable;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public OptionalEagerInEmbeddableNotFoundTest.PersonManyToOneSelectIgnore.CityInEmbeddable getCityInEmbeddable() {
            return cityInEmbeddable;
        }

        public void setCityInEmbeddable(OptionalEagerInEmbeddableNotFoundTest.PersonManyToOneSelectIgnore.CityInEmbeddable cityInEmbeddable) {
            this.cityInEmbeddable = cityInEmbeddable;
        }

        public OptionalEagerInEmbeddableNotFoundTest.City getCity() {
            return (cityInEmbeddable) == null ? null : cityInEmbeddable.getCity();
        }

        @Override
        public void setCity(OptionalEagerInEmbeddableNotFoundTest.City city) {
            if ((cityInEmbeddable) == null) {
                cityInEmbeddable = new OptionalEagerInEmbeddableNotFoundTest.PersonManyToOneSelectIgnore.CityInEmbeddable();
            }
            cityInEmbeddable.setCity(city);
        }

        @Embeddable
        public static class CityInEmbeddable {
            @ManyToOne(cascade = CascadeType.PERSIST)
            @NotFound(action = NotFoundAction.IGNORE)
            @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
            @Fetch(FetchMode.SELECT)
            private OptionalEagerInEmbeddableNotFoundTest.City city;

            public OptionalEagerInEmbeddableNotFoundTest.City getCity() {
                return city;
            }

            public void setCity(OptionalEagerInEmbeddableNotFoundTest.City city) {
                this.city = city;
            }
        }
    }

    @Entity
    @Table(name = "PersonPkjcJoinException")
    public static class PersonPkjcJoinException extends OptionalEagerInEmbeddableNotFoundTest.Person {
        @Id
        private Long id;

        private OptionalEagerInEmbeddableNotFoundTest.PersonPkjcJoinException.CityInEmbeddable cityInEmbeddable;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public OptionalEagerInEmbeddableNotFoundTest.PersonPkjcJoinException.CityInEmbeddable getCityInEmbeddable() {
            return cityInEmbeddable;
        }

        public void setCityInEmbeddable(OptionalEagerInEmbeddableNotFoundTest.PersonPkjcJoinException.CityInEmbeddable cityInEmbeddable) {
            this.cityInEmbeddable = cityInEmbeddable;
        }

        public OptionalEagerInEmbeddableNotFoundTest.City getCity() {
            return (cityInEmbeddable) == null ? null : cityInEmbeddable.getCity();
        }

        @Override
        public void setCity(OptionalEagerInEmbeddableNotFoundTest.City city) {
            if ((cityInEmbeddable) == null) {
                cityInEmbeddable = new OptionalEagerInEmbeddableNotFoundTest.PersonPkjcJoinException.CityInEmbeddable();
            }
            cityInEmbeddable.setCity(city);
        }

        @Embeddable
        public static class CityInEmbeddable {
            @OneToOne(cascade = CascadeType.PERSIST)
            @PrimaryKeyJoinColumn
            @Fetch(FetchMode.JOIN)
            @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
            private OptionalEagerInEmbeddableNotFoundTest.City city;

            public OptionalEagerInEmbeddableNotFoundTest.City getCity() {
                return city;
            }

            public void setCity(OptionalEagerInEmbeddableNotFoundTest.City city) {
                this.city = city;
            }
        }
    }

    @Entity
    @Table(name = "PersonPkjcJoinIgnore")
    public static class PersonPkjcJoinIgnore extends OptionalEagerInEmbeddableNotFoundTest.Person {
        @Id
        private Long id;

        private OptionalEagerInEmbeddableNotFoundTest.PersonPkjcJoinIgnore.CityInEmbeddable cityInEmbeddable;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public OptionalEagerInEmbeddableNotFoundTest.PersonPkjcJoinIgnore.CityInEmbeddable getCityInEmbeddable() {
            return cityInEmbeddable;
        }

        public void setCityInEmbeddable(OptionalEagerInEmbeddableNotFoundTest.PersonPkjcJoinIgnore.CityInEmbeddable cityInEmbeddable) {
            this.cityInEmbeddable = cityInEmbeddable;
        }

        public OptionalEagerInEmbeddableNotFoundTest.City getCity() {
            return (cityInEmbeddable) == null ? null : cityInEmbeddable.getCity();
        }

        @Override
        public void setCity(OptionalEagerInEmbeddableNotFoundTest.City city) {
            if ((cityInEmbeddable) == null) {
                cityInEmbeddable = new OptionalEagerInEmbeddableNotFoundTest.PersonPkjcJoinIgnore.CityInEmbeddable();
            }
            cityInEmbeddable.setCity(city);
        }

        @Embeddable
        public static class CityInEmbeddable {
            @OneToOne(cascade = CascadeType.PERSIST)
            @PrimaryKeyJoinColumn
            @NotFound(action = NotFoundAction.IGNORE)
            @Fetch(FetchMode.JOIN)
            @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
            private OptionalEagerInEmbeddableNotFoundTest.City city;

            public OptionalEagerInEmbeddableNotFoundTest.City getCity() {
                return city;
            }

            public void setCity(OptionalEagerInEmbeddableNotFoundTest.City city) {
                this.city = city;
            }
        }
    }

    @Entity
    @Table(name = "PersonPkjcSelectException")
    public static class PersonPkjcSelectException extends OptionalEagerInEmbeddableNotFoundTest.Person {
        @Id
        private Long id;

        private OptionalEagerInEmbeddableNotFoundTest.PersonPkjcSelectException.CityInEmbeddable cityInEmbeddable;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public OptionalEagerInEmbeddableNotFoundTest.PersonPkjcSelectException.CityInEmbeddable getCityInEmbeddable() {
            return cityInEmbeddable;
        }

        public void setCityInEmbeddable(OptionalEagerInEmbeddableNotFoundTest.PersonPkjcSelectException.CityInEmbeddable cityInEmbeddable) {
            this.cityInEmbeddable = cityInEmbeddable;
        }

        public OptionalEagerInEmbeddableNotFoundTest.City getCity() {
            return (cityInEmbeddable) == null ? null : cityInEmbeddable.getCity();
        }

        @Override
        public void setCity(OptionalEagerInEmbeddableNotFoundTest.City city) {
            if ((cityInEmbeddable) == null) {
                cityInEmbeddable = new OptionalEagerInEmbeddableNotFoundTest.PersonPkjcSelectException.CityInEmbeddable();
            }
            cityInEmbeddable.setCity(city);
        }

        @Embeddable
        public static class CityInEmbeddable {
            @OneToOne(cascade = CascadeType.PERSIST)
            @PrimaryKeyJoinColumn
            @Fetch(FetchMode.SELECT)
            @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
            private OptionalEagerInEmbeddableNotFoundTest.City city;

            public OptionalEagerInEmbeddableNotFoundTest.City getCity() {
                return city;
            }

            public void setCity(OptionalEagerInEmbeddableNotFoundTest.City city) {
                this.city = city;
            }
        }
    }

    @Entity
    @Table(name = "PersonPkjcSelectIgnore")
    public static class PersonPkjcSelectIgnore extends OptionalEagerInEmbeddableNotFoundTest.Person {
        @Id
        private Long id;

        private OptionalEagerInEmbeddableNotFoundTest.PersonPkjcSelectIgnore.CityInEmbeddable cityInEmbeddable;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public OptionalEagerInEmbeddableNotFoundTest.PersonPkjcSelectIgnore.CityInEmbeddable getCityInEmbeddable() {
            return cityInEmbeddable;
        }

        public void setCityInEmbeddable(OptionalEagerInEmbeddableNotFoundTest.PersonPkjcSelectIgnore.CityInEmbeddable cityInEmbeddable) {
            this.cityInEmbeddable = cityInEmbeddable;
        }

        public OptionalEagerInEmbeddableNotFoundTest.City getCity() {
            return (cityInEmbeddable) == null ? null : cityInEmbeddable.getCity();
        }

        @Override
        public void setCity(OptionalEagerInEmbeddableNotFoundTest.City city) {
            if ((cityInEmbeddable) == null) {
                cityInEmbeddable = new OptionalEagerInEmbeddableNotFoundTest.PersonPkjcSelectIgnore.CityInEmbeddable();
            }
            cityInEmbeddable.setCity(city);
        }

        @Embeddable
        public static class CityInEmbeddable {
            @OneToOne(cascade = CascadeType.PERSIST)
            @PrimaryKeyJoinColumn
            @NotFound(action = NotFoundAction.IGNORE)
            @Fetch(FetchMode.SELECT)
            @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
            private OptionalEagerInEmbeddableNotFoundTest.City city;

            public OptionalEagerInEmbeddableNotFoundTest.City getCity() {
                return city;
            }

            public void setCity(OptionalEagerInEmbeddableNotFoundTest.City city) {
                this.city = city;
            }
        }
    }

    @Entity
    @Table(name = "PersonMapsIdJoinIgnore")
    public static class PersonMapsIdJoinIgnore extends OptionalEagerInEmbeddableNotFoundTest.Person {
        @Id
        private Long id;

        private OptionalEagerInEmbeddableNotFoundTest.PersonMapsIdJoinIgnore.CityInEmbeddable cityInEmbeddable;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public OptionalEagerInEmbeddableNotFoundTest.PersonMapsIdJoinIgnore.CityInEmbeddable getCityInEmbeddable() {
            return cityInEmbeddable;
        }

        public void setCityInEmbeddable(OptionalEagerInEmbeddableNotFoundTest.PersonMapsIdJoinIgnore.CityInEmbeddable cityInEmbeddable) {
            this.cityInEmbeddable = cityInEmbeddable;
        }

        public OptionalEagerInEmbeddableNotFoundTest.City getCity() {
            return (cityInEmbeddable) == null ? null : cityInEmbeddable.getCity();
        }

        @Override
        public void setCity(OptionalEagerInEmbeddableNotFoundTest.City city) {
            if ((cityInEmbeddable) == null) {
                cityInEmbeddable = new OptionalEagerInEmbeddableNotFoundTest.PersonMapsIdJoinIgnore.CityInEmbeddable();
            }
            cityInEmbeddable.setCity(city);
        }

        @Embeddable
        public static class CityInEmbeddable {
            @OneToOne
            @MapsId
            @NotFound(action = NotFoundAction.IGNORE)
            @Fetch(FetchMode.JOIN)
            @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
            private OptionalEagerInEmbeddableNotFoundTest.City city;

            public OptionalEagerInEmbeddableNotFoundTest.City getCity() {
                return city;
            }

            public void setCity(OptionalEagerInEmbeddableNotFoundTest.City city) {
                this.city = city;
            }
        }
    }

    @Entity
    @Table(name = "PersonMapsIdSelectIgnore")
    public static class PersonMapsIdSelectIgnore extends OptionalEagerInEmbeddableNotFoundTest.Person {
        @Id
        private Long id;

        private OptionalEagerInEmbeddableNotFoundTest.PersonMapsIdSelectIgnore.CityInEmbeddable cityInEmbeddable;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public OptionalEagerInEmbeddableNotFoundTest.PersonMapsIdSelectIgnore.CityInEmbeddable getCityInEmbeddable() {
            return cityInEmbeddable;
        }

        public void setCityInEmbeddable(OptionalEagerInEmbeddableNotFoundTest.PersonMapsIdSelectIgnore.CityInEmbeddable cityInEmbeddable) {
            this.cityInEmbeddable = cityInEmbeddable;
        }

        public OptionalEagerInEmbeddableNotFoundTest.City getCity() {
            return (cityInEmbeddable) == null ? null : cityInEmbeddable.getCity();
        }

        @Override
        public void setCity(OptionalEagerInEmbeddableNotFoundTest.City city) {
            if ((cityInEmbeddable) == null) {
                cityInEmbeddable = new OptionalEagerInEmbeddableNotFoundTest.PersonMapsIdSelectIgnore.CityInEmbeddable();
            }
            cityInEmbeddable.setCity(city);
        }

        @Embeddable
        public static class CityInEmbeddable {
            @OneToOne
            @MapsId
            @NotFound(action = NotFoundAction.IGNORE)
            @Fetch(FetchMode.SELECT)
            @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
            private OptionalEagerInEmbeddableNotFoundTest.City city;

            public OptionalEagerInEmbeddableNotFoundTest.City getCity() {
                return city;
            }

            public void setCity(OptionalEagerInEmbeddableNotFoundTest.City city) {
                this.city = city;
            }
        }
    }

    @Entity
    @Table(name = "PersonMapsIdColumnJoinIgnore")
    public static class PersonMapsIdColumnJoinIgnore extends OptionalEagerInEmbeddableNotFoundTest.Person {
        @Id
        private Long id;

        private OptionalEagerInEmbeddableNotFoundTest.PersonMapsIdColumnJoinIgnore.CityInEmbeddable cityInEmbeddable;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public OptionalEagerInEmbeddableNotFoundTest.PersonMapsIdColumnJoinIgnore.CityInEmbeddable getCityInEmbeddable() {
            return cityInEmbeddable;
        }

        public void setCityInEmbeddable(OptionalEagerInEmbeddableNotFoundTest.PersonMapsIdColumnJoinIgnore.CityInEmbeddable cityInEmbeddable) {
            this.cityInEmbeddable = cityInEmbeddable;
        }

        public OptionalEagerInEmbeddableNotFoundTest.City getCity() {
            return (cityInEmbeddable) == null ? null : cityInEmbeddable.getCity();
        }

        @Override
        public void setCity(OptionalEagerInEmbeddableNotFoundTest.City city) {
            if ((cityInEmbeddable) == null) {
                cityInEmbeddable = new OptionalEagerInEmbeddableNotFoundTest.PersonMapsIdColumnJoinIgnore.CityInEmbeddable();
            }
            cityInEmbeddable.setCity(city);
        }

        @Embeddable
        public static class CityInEmbeddable {
            @OneToOne
            @MapsId
            @NotFound(action = NotFoundAction.IGNORE)
            @JoinColumn(name = "fk", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
            @Fetch(FetchMode.JOIN)
            private OptionalEagerInEmbeddableNotFoundTest.City city;

            public OptionalEagerInEmbeddableNotFoundTest.City getCity() {
                return city;
            }

            public void setCity(OptionalEagerInEmbeddableNotFoundTest.City city) {
                this.city = city;
            }
        }
    }

    @Entity
    @Table(name = "PersonMapsIdColumnSelectIgnore")
    public static class PersonMapsIdColumnSelectIgnore extends OptionalEagerInEmbeddableNotFoundTest.Person {
        @Id
        private Long id;

        private OptionalEagerInEmbeddableNotFoundTest.PersonMapsIdColumnSelectIgnore.CityInEmbeddable cityInEmbeddable;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public OptionalEagerInEmbeddableNotFoundTest.PersonMapsIdColumnSelectIgnore.CityInEmbeddable getCityInEmbeddable() {
            return cityInEmbeddable;
        }

        public void setCityInEmbeddable(OptionalEagerInEmbeddableNotFoundTest.PersonMapsIdColumnSelectIgnore.CityInEmbeddable cityInEmbeddable) {
            this.cityInEmbeddable = cityInEmbeddable;
        }

        public OptionalEagerInEmbeddableNotFoundTest.City getCity() {
            return (cityInEmbeddable) == null ? null : cityInEmbeddable.getCity();
        }

        @Override
        public void setCity(OptionalEagerInEmbeddableNotFoundTest.City city) {
            if ((cityInEmbeddable) == null) {
                cityInEmbeddable = new OptionalEagerInEmbeddableNotFoundTest.PersonMapsIdColumnSelectIgnore.CityInEmbeddable();
            }
            cityInEmbeddable.setCity(city);
        }

        @Embeddable
        public static class CityInEmbeddable {
            @OneToOne
            @MapsId
            @NotFound(action = NotFoundAction.IGNORE)
            @JoinColumn(name = "fk", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
            @Fetch(FetchMode.SELECT)
            private OptionalEagerInEmbeddableNotFoundTest.City city;

            public OptionalEagerInEmbeddableNotFoundTest.City getCity() {
                return city;
            }

            public void setCity(OptionalEagerInEmbeddableNotFoundTest.City city) {
                this.city = city;
            }
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

        @Embeddable
        public static class CityInEmbeddable {}
    }
}

