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
import org.hibernate.Session;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Gail Badner
 */
@RequiresDialect(H2Dialect.class)
public class LazyElementCollectionWithLazyManyToOneNonUniqueIdWhereTest extends BaseCoreFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-12937")
    public void testInitializeFromUniqueAssociationTable() {
        Session session = openSession();
        session.beginTransaction();
        {
            LazyElementCollectionWithLazyManyToOneNonUniqueIdWhereTest.Material material = session.get(LazyElementCollectionWithLazyManyToOneNonUniqueIdWhereTest.Material.class, 1);
            Assert.assertEquals("plastic", material.getName());
            // Material#ratings is mapped with lazy="true"
            Assert.assertFalse(Hibernate.isInitialized(material.getContainedRatings()));
            Assert.assertEquals(1, material.getContainedRatings().size());
            Assert.assertTrue(Hibernate.isInitialized(material.getContainedRatings()));
            final LazyElementCollectionWithLazyManyToOneNonUniqueIdWhereTest.ContainedRating containedRating = material.getContainedRatings().iterator().next();
            Assert.assertTrue(Hibernate.isInitialized(containedRating));
            Assert.assertEquals("high", containedRating.getRating().getName());
        }
        session.getTransaction().commit();
        session.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-12937")
    public void testInitializeFromNonUniqueAssociationTable() {
        Session session = openSession();
        session.beginTransaction();
        {
            LazyElementCollectionWithLazyManyToOneNonUniqueIdWhereTest.Material material = session.get(LazyElementCollectionWithLazyManyToOneNonUniqueIdWhereTest.Material.class, 1);
            Assert.assertEquals("plastic", material.getName());
            // Material#containedSizesFromCombined is mapped with lazy="true"
            Assert.assertFalse(Hibernate.isInitialized(material.getContainedSizesFromCombined()));
            Assert.assertEquals(1, material.getContainedSizesFromCombined().size());
            Assert.assertTrue(Hibernate.isInitialized(material.getContainedSizesFromCombined()));
            LazyElementCollectionWithLazyManyToOneNonUniqueIdWhereTest.ContainedSize containedSize = material.getContainedSizesFromCombined().iterator().next();
            Assert.assertFalse(Hibernate.isInitialized(containedSize.getSize()));
            Assert.assertEquals("medium", containedSize.getSize().getName());
            LazyElementCollectionWithLazyManyToOneNonUniqueIdWhereTest.Building building = session.get(LazyElementCollectionWithLazyManyToOneNonUniqueIdWhereTest.Building.class, 1);
            // building.ratingsFromCombined is mapped with lazy="true"
            Assert.assertFalse(Hibernate.isInitialized(building.getContainedRatingsFromCombined()));
            Assert.assertEquals(1, building.getContainedRatingsFromCombined().size());
            Assert.assertTrue(Hibernate.isInitialized(building.getContainedRatingsFromCombined()));
            LazyElementCollectionWithLazyManyToOneNonUniqueIdWhereTest.ContainedRating containedRating = building.getContainedRatingsFromCombined().iterator().next();
            Assert.assertFalse(Hibernate.isInitialized(containedRating.getRating()));
            Assert.assertEquals("high", containedRating.getRating().getName());
            // Building#containedSizesFromCombined is mapped with lazy="true"
            Assert.assertFalse(Hibernate.isInitialized(building.getContainedSizesFromCombined()));
            Assert.assertEquals(1, building.getContainedSizesFromCombined().size());
            Assert.assertTrue(Hibernate.isInitialized(building.getContainedSizesFromCombined()));
            containedSize = building.getContainedSizesFromCombined().iterator().next();
            Assert.assertFalse(Hibernate.isInitialized(containedSize.getSize()));
            Assert.assertEquals("small", containedSize.getSize().getName());
        }
        session.getTransaction().commit();
        session.close();
    }

    public static class Material {
        private int id;

        private String name;

        private Set<LazyElementCollectionWithLazyManyToOneNonUniqueIdWhereTest.ContainedSize> containedSizesFromCombined = new HashSet<>();

        private List<LazyElementCollectionWithLazyManyToOneNonUniqueIdWhereTest.Rating> mediumOrHighRatingsFromCombined = new ArrayList<>();

        private Set<LazyElementCollectionWithLazyManyToOneNonUniqueIdWhereTest.ContainedRating> containedRatings = new HashSet<>();

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

        public Set<LazyElementCollectionWithLazyManyToOneNonUniqueIdWhereTest.ContainedSize> getContainedSizesFromCombined() {
            return containedSizesFromCombined;
        }

        public void setContainedSizesFromCombined(Set<LazyElementCollectionWithLazyManyToOneNonUniqueIdWhereTest.ContainedSize> containedSizesFromCombined) {
            this.containedSizesFromCombined = containedSizesFromCombined;
        }

        public Set<LazyElementCollectionWithLazyManyToOneNonUniqueIdWhereTest.ContainedRating> getContainedRatings() {
            return containedRatings;
        }

        public void setContainedRatings(Set<LazyElementCollectionWithLazyManyToOneNonUniqueIdWhereTest.ContainedRating> containedRatings) {
            this.containedRatings = containedRatings;
        }
    }

    public static class Building {
        private int id;

        private String name;

        private Set<LazyElementCollectionWithLazyManyToOneNonUniqueIdWhereTest.ContainedSize> containedSizesFromCombined = new HashSet<>();

        private Set<LazyElementCollectionWithLazyManyToOneNonUniqueIdWhereTest.ContainedRating> containedRatingsFromCombined = new HashSet<>();

        private List<LazyElementCollectionWithLazyManyToOneNonUniqueIdWhereTest.Rating> mediumOrHighRatings = new ArrayList<>();

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

        public Set<LazyElementCollectionWithLazyManyToOneNonUniqueIdWhereTest.ContainedSize> getContainedSizesFromCombined() {
            return containedSizesFromCombined;
        }

        public void setContainedSizesFromCombined(Set<LazyElementCollectionWithLazyManyToOneNonUniqueIdWhereTest.ContainedSize> containedSizesFromCombined) {
            this.containedSizesFromCombined = containedSizesFromCombined;
        }

        public Set<LazyElementCollectionWithLazyManyToOneNonUniqueIdWhereTest.ContainedRating> getContainedRatingsFromCombined() {
            return containedRatingsFromCombined;
        }

        public void setContainedRatingsFromCombined(Set<LazyElementCollectionWithLazyManyToOneNonUniqueIdWhereTest.ContainedRating> containedRatingsFromCombined) {
            this.containedRatingsFromCombined = containedRatingsFromCombined;
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

    public static class ContainedSize {
        private LazyElementCollectionWithLazyManyToOneNonUniqueIdWhereTest.Size size;

        public LazyElementCollectionWithLazyManyToOneNonUniqueIdWhereTest.Size getSize() {
            return size;
        }

        public void setSize(LazyElementCollectionWithLazyManyToOneNonUniqueIdWhereTest.Size size) {
            this.size = size;
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

    public static class ContainedRating {
        private LazyElementCollectionWithLazyManyToOneNonUniqueIdWhereTest.Rating rating;

        public LazyElementCollectionWithLazyManyToOneNonUniqueIdWhereTest.Rating getRating() {
            return rating;
        }

        public void setRating(LazyElementCollectionWithLazyManyToOneNonUniqueIdWhereTest.Rating rating) {
            this.rating = rating;
        }
    }
}

