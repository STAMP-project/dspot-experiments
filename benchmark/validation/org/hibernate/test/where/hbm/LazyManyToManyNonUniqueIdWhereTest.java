/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.where.hbm;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
public class LazyManyToManyNonUniqueIdWhereTest extends BaseCoreFunctionalTestCase {
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
            assertFalse(Hibernate.isInitialized(building.getMediumOrHighRatings()));
            checkMediumOrHighRatings(building.getMediumOrHighRatings());
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-12875")
    public void testInitializeFromNonUniqueAssociationTable() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.where.hbm.Material material = session.get(.class, 1);
            assertEquals("plastic", material.getName());
            // Material#mediumOrHighRatingsFromCombined is mapped with lazy="true"
            assertFalse(Hibernate.isInitialized(material.getMediumOrHighRatingsFromCombined()));
            checkMediumOrHighRatings(material.getMediumOrHighRatingsFromCombined());
            org.hibernate.test.where.hbm.Rating highRating = null;
            for (org.hibernate.test.where.hbm.Rating rating : material.getMediumOrHighRatingsFromCombined()) {
                if ("high".equals(rating.getName())) {
                    highRating = rating;
                }
            }
            assertNotNull(highRating);
            // Material#sizesFromCombined is mapped with lazy="true"
            assertFalse(Hibernate.isInitialized(material.getSizesFromCombined()));
            assertEquals(1, material.getSizesFromCombined().size());
            assertTrue(Hibernate.isInitialized(material.getSizesFromCombined()));
            final org.hibernate.test.where.hbm.Size size = material.getSizesFromCombined().iterator().next();
            assertEquals("medium", size.getName());
            org.hibernate.test.where.hbm.Building building = session.get(.class, 1);
            // building.ratingsFromCombined is mapped with lazy="true"
            assertFalse(Hibernate.isInitialized(building.getRatingsFromCombined()));
            assertEquals(1, building.getRatingsFromCombined().size());
            assertTrue(Hibernate.isInitialized(building.getRatingsFromCombined()));
            assertSame(highRating, building.getRatingsFromCombined().iterator().next());
            // Building#sizesFromCombined is mapped with lazy="true"
            assertFalse(Hibernate.isInitialized(building.getSizesFromCombined()));
            assertEquals(1, building.getSizesFromCombined().size());
            assertTrue(Hibernate.isInitialized(building.getSizesFromCombined()));
            assertEquals("small", building.getSizesFromCombined().iterator().next().getName());
        });
    }

    public static class Material {
        private int id;

        private String name;

        private Set<LazyManyToManyNonUniqueIdWhereTest.Size> sizesFromCombined = new HashSet<>();

        private List<LazyManyToManyNonUniqueIdWhereTest.Rating> mediumOrHighRatingsFromCombined = new ArrayList<>();

        private Set<LazyManyToManyNonUniqueIdWhereTest.Rating> ratings = new HashSet<>();

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

        public Set<LazyManyToManyNonUniqueIdWhereTest.Size> getSizesFromCombined() {
            return sizesFromCombined;
        }

        public void setSizesFromCombined(Set<LazyManyToManyNonUniqueIdWhereTest.Size> sizesFromCombined) {
            this.sizesFromCombined = sizesFromCombined;
        }

        public List<LazyManyToManyNonUniqueIdWhereTest.Rating> getMediumOrHighRatingsFromCombined() {
            return mediumOrHighRatingsFromCombined;
        }

        public void setMediumOrHighRatingsFromCombined(List<LazyManyToManyNonUniqueIdWhereTest.Rating> mediumOrHighRatingsFromCombined) {
            this.mediumOrHighRatingsFromCombined = mediumOrHighRatingsFromCombined;
        }

        public Set<LazyManyToManyNonUniqueIdWhereTest.Rating> getRatings() {
            return ratings;
        }

        public void setRatings(Set<LazyManyToManyNonUniqueIdWhereTest.Rating> ratings) {
            this.ratings = ratings;
        }
    }

    public static class Building {
        private int id;

        private String name;

        private Set<LazyManyToManyNonUniqueIdWhereTest.Size> sizesFromCombined = new HashSet<>();

        private Set<LazyManyToManyNonUniqueIdWhereTest.Rating> ratingsFromCombined = new HashSet<>();

        private List<LazyManyToManyNonUniqueIdWhereTest.Rating> mediumOrHighRatings = new ArrayList<>();

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

        public Set<LazyManyToManyNonUniqueIdWhereTest.Size> getSizesFromCombined() {
            return sizesFromCombined;
        }

        public void setSizesFromCombined(Set<LazyManyToManyNonUniqueIdWhereTest.Size> sizesFromCombined) {
            this.sizesFromCombined = sizesFromCombined;
        }

        public Set<LazyManyToManyNonUniqueIdWhereTest.Rating> getRatingsFromCombined() {
            return ratingsFromCombined;
        }

        public void setRatingsFromCombined(Set<LazyManyToManyNonUniqueIdWhereTest.Rating> ratingsFromCombined) {
            this.ratingsFromCombined = ratingsFromCombined;
        }

        public List<LazyManyToManyNonUniqueIdWhereTest.Rating> getMediumOrHighRatings() {
            return mediumOrHighRatings;
        }

        public void setMediumOrHighRatings(List<LazyManyToManyNonUniqueIdWhereTest.Rating> mediumOrHighRatings) {
            this.mediumOrHighRatings = mediumOrHighRatings;
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

