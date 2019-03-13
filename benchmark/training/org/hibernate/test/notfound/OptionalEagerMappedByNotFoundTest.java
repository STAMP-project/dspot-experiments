package org.hibernate.test.notfound;


import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToOne;
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
public class OptionalEagerMappedByNotFoundTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testOneToOneJoinException() {
        setupTest(OptionalEagerMappedByNotFoundTest.PersonOneToOneJoinException.class, 1L, false);
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.notfound.Person pCheck = session.find(.class, 1L);
            checkResult(pCheck);
        });
    }

    @Test
    public void testOneToOneJoinIgnore() {
        setupTest(OptionalEagerMappedByNotFoundTest.PersonOneToOneJoinIgnore.class, 1L, false);
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.notfound.Person pCheck = session.find(.class, 1L);
            checkResult(pCheck);
        });
    }

    @Test
    public void testOneToOneSelectException() {
        setupTest(OptionalEagerMappedByNotFoundTest.PersonOneToOneSelectException.class, 1L, false);
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.notfound.Person pCheck = session.find(.class, 1L);
            checkResult(pCheck);
        });
    }

    @Test
    public void testOneToOneSelectIgnore() {
        setupTest(OptionalEagerMappedByNotFoundTest.PersonOneToOneSelectIgnore.class, 1L, false);
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.notfound.Person pCheck = session.find(.class, 1L);
            checkResult(pCheck);
        });
    }

    @Entity(name = "Person")
    @Inheritance(strategy = InheritanceType.JOINED)
    public abstract static class Person {
        @Id
        private Long id;

        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public abstract OptionalEagerMappedByNotFoundTest.Employment getEmployment();

        public abstract void setEmployment(OptionalEagerMappedByNotFoundTest.Employment employment);
    }

    @Entity
    @Table(name = "PersonOneToOneJoinException")
    public static class PersonOneToOneJoinException extends OptionalEagerMappedByNotFoundTest.Person {
        @OneToOne(mappedBy = "person", cascade = CascadeType.PERSIST)
        @NotFound(action = NotFoundAction.EXCEPTION)
        @Fetch(FetchMode.JOIN)
        private OptionalEagerMappedByNotFoundTest.Employment employment;

        public OptionalEagerMappedByNotFoundTest.Employment getEmployment() {
            return employment;
        }

        @Override
        public void setEmployment(OptionalEagerMappedByNotFoundTest.Employment employment) {
            this.employment = employment;
        }
    }

    @Entity
    @Table(name = "PersonOneToOneJoinIgnore")
    public static class PersonOneToOneJoinIgnore extends OptionalEagerMappedByNotFoundTest.Person {
        @Id
        private Long id;

        @OneToOne(mappedBy = "person", cascade = CascadeType.PERSIST)
        @NotFound(action = NotFoundAction.IGNORE)
        @Fetch(FetchMode.JOIN)
        private OptionalEagerMappedByNotFoundTest.Employment employment;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public OptionalEagerMappedByNotFoundTest.Employment getEmployment() {
            return employment;
        }

        @Override
        public void setEmployment(OptionalEagerMappedByNotFoundTest.Employment employment) {
            this.employment = employment;
        }
    }

    @Entity
    @Table(name = "PersonOneToOneSelectException")
    public static class PersonOneToOneSelectException extends OptionalEagerMappedByNotFoundTest.Person {
        @Id
        private Long id;

        @OneToOne(mappedBy = "person", cascade = CascadeType.PERSIST)
        @NotFound(action = NotFoundAction.EXCEPTION)
        @Fetch(FetchMode.SELECT)
        private OptionalEagerMappedByNotFoundTest.Employment employment;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public OptionalEagerMappedByNotFoundTest.Employment getEmployment() {
            return employment;
        }

        @Override
        public void setEmployment(OptionalEagerMappedByNotFoundTest.Employment employment) {
            this.employment = employment;
        }
    }

    @Entity
    @Table(name = "PersonOneToOneSelectIgnore")
    public static class PersonOneToOneSelectIgnore extends OptionalEagerMappedByNotFoundTest.Person {
        @Id
        private Long id;

        @OneToOne(mappedBy = "person", cascade = CascadeType.PERSIST)
        @NotFound(action = NotFoundAction.IGNORE)
        @Fetch(FetchMode.SELECT)
        private OptionalEagerMappedByNotFoundTest.Employment employment;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public OptionalEagerMappedByNotFoundTest.Employment getEmployment() {
            return employment;
        }

        @Override
        public void setEmployment(OptionalEagerMappedByNotFoundTest.Employment employment) {
            this.employment = employment;
        }
    }

    @Entity(name = "Employment")
    public static class Employment implements Serializable {
        @Id
        private Long id;

        private String name;

        // @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
        @OneToOne
        private OptionalEagerMappedByNotFoundTest.Person person;

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

        public OptionalEagerMappedByNotFoundTest.Person getPerson() {
            return person;
        }

        public void setPerson(OptionalEagerMappedByNotFoundTest.Person person) {
            this.person = person;
        }
    }
}

