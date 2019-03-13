/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.lazyload;


import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
public class ManyToOneLazyLoadingByIdTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testLazyLoadById() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.test.lazyload.Continent continent = new org.hibernate.test.lazyload.Continent();
            continent.setId(1L);
            continent.setName("Europe");
            entityManager.persist(continent);
            org.hibernate.test.lazyload.Country country = new org.hibernate.test.lazyload.Country();
            country.setId(1L);
            country.setName("Romania");
            country.setContinent(continent);
            entityManager.persist(country);
        });
        ManyToOneLazyLoadingByIdTest.Continent continent = TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.test.lazyload.Country country = entityManager.find(.class, 1L);
            country.getContinent().getId();
            return country.getContinent();
        });
        Assert.assertEquals(1L, ((long) (continent.getId())));
        assertProxyState(continent);
    }

    @Entity(name = "Country")
    public static class Country {
        @Id
        private Long id;

        private String name;

        @ManyToOne(fetch = FetchType.LAZY)
        private ManyToOneLazyLoadingByIdTest.Continent continent;

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

        public ManyToOneLazyLoadingByIdTest.Continent getContinent() {
            return continent;
        }

        public void setContinent(ManyToOneLazyLoadingByIdTest.Continent continent) {
            this.continent = continent;
        }
    }

    @Entity(name = "Continent")
    public static class Continent {
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

