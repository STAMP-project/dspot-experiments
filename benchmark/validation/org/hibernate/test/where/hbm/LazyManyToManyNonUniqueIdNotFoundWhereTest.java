/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.where.hbm;


import java.util.HashSet;
import java.util.Set;
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
public class LazyManyToManyNonUniqueIdNotFoundWhereTest extends BaseCoreFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-12875")
    public void testInitializeFromUniqueAssociationTable() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.where.hbm.Material material = session.get(.class, 1);
            assertEquals("plastic", material.getName());
            // Material#ratings is mapped with lazy="true"
            assertFalse(Hibernate.isInitialized(material.getRatings()));
            assertEquals(1, material.getRatings().size());
            assertTrue(Hibernate.isInitialized(material.getRatings()));
            final org.hibernate.test.where.hbm.Rating rating = material.getRatings().iterator().next();
            assertEquals("high", rating.getName());
            org.hibernate.test.where.hbm.Building building = session.get(.class, 1);
            assertEquals("house", building.getName());
            // Building#ratings is mapped with lazy="true"
            assertFalse(Hibernate.isInitialized(building.getRatings()));
            assertEquals(1, building.getRatings().size());
            assertTrue(Hibernate.isInitialized(building.getRatings()));
            assertSame(rating, building.getRatings().iterator().next());
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-12875")
    public void testInitializeFromNonUniqueAssociationTable() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.where.hbm.Material material = session.get(.class, 1);
            assertEquals("plastic", material.getName());
            // Material#ratingsFromCombined is mapped with lazy="true"
            assertFalse(Hibernate.isInitialized(material.getRatingsFromCombined()));
            assertEquals(1, material.getRatingsFromCombined().size());
            assertTrue(Hibernate.isInitialized(material.getRatingsFromCombined()));
            final org.hibernate.test.where.hbm.Rating rating = material.getRatingsFromCombined().iterator().next();
            assertEquals("high", rating.getName());
            // Material#sizesFromCombined is mapped with lazy="true"
            assertFalse(Hibernate.isInitialized(material.getSizesFromCombined()));
            assertEquals(1, material.getSizesFromCombined().size());
            assertTrue(Hibernate.isInitialized(material.getSizesFromCombined()));
            final org.hibernate.test.where.hbm.Size size = material.getSizesFromCombined().iterator().next();
            assertEquals("small", size.getName());
            org.hibernate.test.where.hbm.Building building = session.get(.class, 1);
            // building.ratingsFromCombined is mapped with lazy="true"
            assertFalse(Hibernate.isInitialized(building.getRatingsFromCombined()));
            assertEquals(1, building.getRatingsFromCombined().size());
            assertTrue(Hibernate.isInitialized(building.getRatingsFromCombined()));
            assertSame(rating, building.getRatingsFromCombined().iterator().next());
            // Building#sizesFromCombined is mapped with lazy="true"
            assertFalse(Hibernate.isInitialized(building.getSizesFromCombined()));
            assertEquals(1, building.getSizesFromCombined().size());
            assertTrue(Hibernate.isInitialized(building.getSizesFromCombined()));
            assertSame(size, building.getSizesFromCombined().iterator().next());
        });
    }

    public static class Material {
        private int id;

        private String name;

        private Set<LazyManyToManyNonUniqueIdNotFoundWhereTest.Size> sizesFromCombined = new HashSet<>();

        private Set<LazyManyToManyNonUniqueIdNotFoundWhereTest.Rating> ratingsFromCombined = new HashSet<>();

        private Set<LazyManyToManyNonUniqueIdNotFoundWhereTest.Rating> ratings = new HashSet<>();

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Set<LazyManyToManyNonUniqueIdNotFoundWhereTest.Size> getSizesFromCombined() {
            return sizesFromCombined;
        }

        public void setSizesFromCombined(Set<LazyManyToManyNonUniqueIdNotFoundWhereTest.Size> sizesFromCombined) {
            this.sizesFromCombined = sizesFromCombined;
        }

        public Set<LazyManyToManyNonUniqueIdNotFoundWhereTest.Rating> getRatingsFromCombined() {
            return ratingsFromCombined;
        }

        public void setRatingsFromCombined(Set<LazyManyToManyNonUniqueIdNotFoundWhereTest.Rating> ratingsFromCombined) {
            this.ratingsFromCombined = ratingsFromCombined;
        }

        public Set<LazyManyToManyNonUniqueIdNotFoundWhereTest.Rating> getRatings() {
            return ratings;
        }

        public void setRatings(Set<LazyManyToManyNonUniqueIdNotFoundWhereTest.Rating> ratings) {
            this.ratings = ratings;
        }
    }

    public static class Building {
        private int id;

        private String name;

        private Set<LazyManyToManyNonUniqueIdNotFoundWhereTest.Size> sizesFromCombined = new HashSet<>();

        private Set<LazyManyToManyNonUniqueIdNotFoundWhereTest.Rating> ratingsFromCombined = new HashSet<>();

        private Set<LazyManyToManyNonUniqueIdNotFoundWhereTest.Rating> ratings = new HashSet<>();

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Set<LazyManyToManyNonUniqueIdNotFoundWhereTest.Size> getSizesFromCombined() {
            return sizesFromCombined;
        }

        public void setSizesFromCombined(Set<LazyManyToManyNonUniqueIdNotFoundWhereTest.Size> sizesFromCombined) {
            this.sizesFromCombined = sizesFromCombined;
        }

        public Set<LazyManyToManyNonUniqueIdNotFoundWhereTest.Rating> getRatingsFromCombined() {
            return ratingsFromCombined;
        }

        public void setRatingsFromCombined(Set<LazyManyToManyNonUniqueIdNotFoundWhereTest.Rating> ratingsFromCombined) {
            this.ratingsFromCombined = ratingsFromCombined;
        }

        public Set<LazyManyToManyNonUniqueIdNotFoundWhereTest.Rating> getRatings() {
            return ratings;
        }

        public void setRatings(Set<LazyManyToManyNonUniqueIdNotFoundWhereTest.Rating> ratings) {
            this.ratings = ratings;
        }
    }

    public static class Size {
        private int id;

        private String name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class Rating {
        private int id;

        private String name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
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

