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
public class LazyOneToManyNonUniqueIdWhereTest extends BaseCoreFunctionalTestCase {
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

        private Set<LazyOneToManyNonUniqueIdWhereTest.Size> sizesFromCombined = new HashSet<>();

        private List<LazyOneToManyNonUniqueIdWhereTest.Rating> mediumOrHighRatingsFromCombined = new ArrayList<>();

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

        public Set<LazyOneToManyNonUniqueIdWhereTest.Size> getSizesFromCombined() {
            return sizesFromCombined;
        }

        public void setSizesFromCombined(Set<LazyOneToManyNonUniqueIdWhereTest.Size> sizesFromCombined) {
            this.sizesFromCombined = sizesFromCombined;
        }

        public List<LazyOneToManyNonUniqueIdWhereTest.Rating> getMediumOrHighRatingsFromCombined() {
            return mediumOrHighRatingsFromCombined;
        }

        public void setMediumOrHighRatingsFromCombined(List<LazyOneToManyNonUniqueIdWhereTest.Rating> mediumOrHighRatingsFromCombined) {
            this.mediumOrHighRatingsFromCombined = mediumOrHighRatingsFromCombined;
        }
    }

    public static class Building {
        private int id;

        private String name;

        private Set<LazyOneToManyNonUniqueIdWhereTest.Size> sizesFromCombined = new HashSet<>();

        private Set<LazyOneToManyNonUniqueIdWhereTest.Rating> ratingsFromCombined = new HashSet<>();

        private List<LazyOneToManyNonUniqueIdWhereTest.Rating> mediumOrHighRatings = new ArrayList<>();

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

        public Set<LazyOneToManyNonUniqueIdWhereTest.Size> getSizesFromCombined() {
            return sizesFromCombined;
        }

        public void setSizesFromCombined(Set<LazyOneToManyNonUniqueIdWhereTest.Size> sizesFromCombined) {
            this.sizesFromCombined = sizesFromCombined;
        }

        public Set<LazyOneToManyNonUniqueIdWhereTest.Rating> getRatingsFromCombined() {
            return ratingsFromCombined;
        }

        public void setRatingsFromCombined(Set<LazyOneToManyNonUniqueIdWhereTest.Rating> ratingsFromCombined) {
            this.ratingsFromCombined = ratingsFromCombined;
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

