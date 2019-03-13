/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.inheritance.relationship;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import org.hibernate.testing.FailureExpected;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Christian Beikov
 */
@TestForIssue(jiraKey = "HHH-7406")
public class JoinedInheritancePropertyNameConflictTest extends BaseCoreFunctionalTestCase {
    @Test
    @FailureExpected(jiraKey = "HHH-7406")
    public void testQueryConflictingPropertyName() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.inheritance.relationship.Town town = new org.hibernate.test.inheritance.relationship.Town(1L, "London", 5000000);
            org.hibernate.test.inheritance.relationship.Country country = new org.hibernate.test.inheritance.relationship.Country(2L, "Andorra", 10000);
            org.hibernate.test.inheritance.relationship.Mountain mountain = new org.hibernate.test.inheritance.relationship.Mountain(3L, "Mont Blanc", 4810);
            session.persist(town);
            session.persist(country);
            session.persist(mountain);
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            List<org.hibernate.test.inheritance.relationship.Place> places = session.createQuery(((("select pl from " + (.class.getName())) + " pl ") + " where pl.population > 1000")).getResultList();
            // Expected list of length 2. Expected London and Andorra
            assertEquals(2L, places.size());
        });
    }

    @Entity
    @Table(name = "PLACE")
    @Inheritance(strategy = InheritanceType.JOINED)
    public abstract static class Place {
        @Id
        @Column(name = "PLACE_ID")
        private Long id;

        @Column(name = "PLACE_NAME")
        private String name;

        protected Place() {
        }

        protected Place(Long id, String name) {
            super();
            this.id = id;
            this.name = name;
        }
    }

    @Entity
    @Table(name = "COUNTRY")
    @PrimaryKeyJoinColumn(name = "PLACE_ID", referencedColumnName = "PLACE_ID")
    public static class Country extends JoinedInheritancePropertyNameConflictTest.Place {
        @Column(name = "NU_POPULATION")
        private Integer population;

        public Country() {
        }

        public Country(Long id, String name, Integer population) {
            super(id, name);
            this.population = population;
        }
    }

    @Entity
    @Table(name = "MOUNTAIN")
    @PrimaryKeyJoinColumn(name = "PLACE_ID", referencedColumnName = "PLACE_ID")
    public static class Mountain extends JoinedInheritancePropertyNameConflictTest.Place {
        @Column(name = "NU_HEIGHT")
        private Integer height;

        public Mountain() {
        }

        public Mountain(Long id, String name, Integer height) {
            super(id, name);
            this.height = height;
        }
    }

    @Entity
    @Table(name = "TOWN")
    @PrimaryKeyJoinColumn(name = "PLACE_ID", referencedColumnName = "PLACE_ID")
    public static class Town extends JoinedInheritancePropertyNameConflictTest.Place {
        @Column(name = "NU_POPULATION")
        private Integer population;

        public Town() {
        }

        public Town(Long id, String name, Integer population) {
            super(id, name);
            this.population = population;
        }
    }
}

