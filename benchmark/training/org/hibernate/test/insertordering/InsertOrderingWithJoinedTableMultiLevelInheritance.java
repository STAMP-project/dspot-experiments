/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.insertordering;


import DialectChecks.SupportsJdbcDriverProxying;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import org.hibernate.annotations.BatchSize;
import org.hibernate.test.util.jdbc.PreparedStatementSpyConnectionProvider;
import org.hibernate.testing.RequiresDialectFeature;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
@TestForIssue(jiraKey = "HHH-9864")
@RequiresDialectFeature(SupportsJdbcDriverProxying.class)
public class InsertOrderingWithJoinedTableMultiLevelInheritance extends BaseNonConfigCoreFunctionalTestCase {
    private PreparedStatementSpyConnectionProvider connectionProvider = new PreparedStatementSpyConnectionProvider(false, false);

    @Test
    public void testBatchingAmongstSubClasses() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            int iterations = 2;
            for (int i = 0; i < iterations; i++) {
                final org.hibernate.test.insertordering.President president = new org.hibernate.test.insertordering.President();
                president.addAddress(new org.hibernate.test.insertordering.Address());
                session.persist(president);
                final org.hibernate.test.insertordering.AnotherPerson anotherPerson = new org.hibernate.test.insertordering.AnotherPerson();
                org.hibernate.test.insertordering.Office office = new org.hibernate.test.insertordering.Office();
                session.persist(office);
                anotherPerson.office = office;
                session.persist(anotherPerson);
                final org.hibernate.test.insertordering.Person person = new org.hibernate.test.insertordering.Person();
                session.persist(person);
                final org.hibernate.test.insertordering.SpecialPerson specialPerson = new org.hibernate.test.insertordering.SpecialPerson();
                specialPerson.addAddress(new org.hibernate.test.insertordering.Address());
                session.persist(specialPerson);
            }
            connectionProvider.clear();
        });
        Assert.assertEquals(17, connectionProvider.getPreparedStatements().size());
    }

    @Entity(name = "Address")
    @Table(name = "ADDRESS")
    public static class Address {
        @Id
        @Column(name = "ID", nullable = false)
        @SequenceGenerator(name = "ID", sequenceName = "ADDRESS_SEQ")
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ID")
        private Long id;
    }

    @Entity(name = "Office")
    public static class Office {
        @Id
        @Column(name = "ID", nullable = false)
        @SequenceGenerator(name = "ID_2", sequenceName = "ADDRESS_SEQ")
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ID_2")
        private Long id;
    }

    @Entity(name = "Person")
    @Table(name = "PERSON")
    @Inheritance(strategy = InheritanceType.JOINED)
    @DiscriminatorColumn(name = "CLASSINDICATOR", discriminatorType = DiscriminatorType.INTEGER)
    public static class Person {
        @Id
        @Column(name = "ID", nullable = false)
        @SequenceGenerator(name = "ID_3", sequenceName = "PERSON_SEQ")
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ID_3")
        private Long id;
    }

    @Entity(name = "SpecialPerson")
    public static class SpecialPerson extends InsertOrderingWithJoinedTableMultiLevelInheritance.Person {
        @Column(name = "special")
        private String special;

        @OneToMany(orphanRemoval = true, cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
        @JoinColumn(name = "PERSONID", referencedColumnName = "ID", nullable = false, updatable = false)
        @BatchSize(size = 100)
        private Set<InsertOrderingWithJoinedTableMultiLevelInheritance.Address> addresses = new HashSet<InsertOrderingWithJoinedTableMultiLevelInheritance.Address>();

        public void addAddress(InsertOrderingWithJoinedTableMultiLevelInheritance.Address address) {
            this.addresses.add(address);
        }
    }

    @Entity(name = "AnotherPerson")
    public static class AnotherPerson extends InsertOrderingWithJoinedTableMultiLevelInheritance.Person {
        private boolean working;

        @ManyToOne
        private InsertOrderingWithJoinedTableMultiLevelInheritance.Office office;
    }

    @Entity(name = "President")
    public static class President extends InsertOrderingWithJoinedTableMultiLevelInheritance.SpecialPerson {
        @Column(name = "salary")
        private BigDecimal salary;
    }
}

