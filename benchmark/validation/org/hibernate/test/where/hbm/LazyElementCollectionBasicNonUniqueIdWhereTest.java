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
public class LazyElementCollectionBasicNonUniqueIdWhereTest extends BaseCoreFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-12937")
    public void testInitializeFromUniqueAssociationTable() {
        Session session = openSession();
        session.beginTransaction();
        {
            LazyElementCollectionBasicNonUniqueIdWhereTest.Material material = session.get(LazyElementCollectionBasicNonUniqueIdWhereTest.Material.class, 1);
            Assert.assertEquals("plastic", material.getName());
            // Material#ratings is mapped with lazy="true"
            Assert.assertFalse(Hibernate.isInitialized(material.getRatings()));
            Assert.assertEquals(1, material.getRatings().size());
            Assert.assertTrue(Hibernate.isInitialized(material.getRatings()));
            Assert.assertEquals("high", material.getRatings().iterator().next());
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
            LazyElementCollectionBasicNonUniqueIdWhereTest.Material material = session.get(LazyElementCollectionBasicNonUniqueIdWhereTest.Material.class, 1);
            Assert.assertEquals("plastic", material.getName());
            // Material#sizesFromCombined is mapped with lazy="true"
            Assert.assertFalse(Hibernate.isInitialized(material.getSizesFromCombined()));
            Assert.assertEquals(1, material.getSizesFromCombined().size());
            Assert.assertTrue(Hibernate.isInitialized(material.getSizesFromCombined()));
            Assert.assertEquals("medium", material.getSizesFromCombined().iterator().next());
            LazyElementCollectionBasicNonUniqueIdWhereTest.Building building = session.get(LazyElementCollectionBasicNonUniqueIdWhereTest.Building.class, 1);
            // building.ratingsFromCombined is mapped with lazy="true"
            Assert.assertFalse(Hibernate.isInitialized(building.getRatingsFromCombined()));
            Assert.assertEquals(1, building.getRatingsFromCombined().size());
            Assert.assertTrue(Hibernate.isInitialized(building.getRatingsFromCombined()));
            Assert.assertEquals("high", building.getRatingsFromCombined().iterator().next());
            // Building#sizesFromCombined is mapped with lazy="true"
            Assert.assertFalse(Hibernate.isInitialized(building.getSizesFromCombined()));
            Assert.assertEquals(1, building.getSizesFromCombined().size());
            Assert.assertTrue(Hibernate.isInitialized(building.getSizesFromCombined()));
            Assert.assertEquals("small", building.getSizesFromCombined().iterator().next());
        }
        session.getTransaction().commit();
        session.close();
    }

    public static class Material {
        private int id;

        private String name;

        private Set<String> sizesFromCombined = new HashSet<>();

        private List<String> mediumOrHighRatingsFromCombined = new ArrayList<>();

        private Set<String> ratings = new HashSet<>();

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

        public Set<String> getSizesFromCombined() {
            return sizesFromCombined;
        }

        public void setSizesFromCombined(Set<String> sizesFromCombined) {
            this.sizesFromCombined = sizesFromCombined;
        }

        public Set<String> getRatings() {
            return ratings;
        }

        public void setRatings(Set<String> ratings) {
            this.ratings = ratings;
        }
    }

    public static class Building {
        private int id;

        private String name;

        private Set<String> sizesFromCombined = new HashSet<>();

        private Set<String> ratingsFromCombined = new HashSet<>();

        private List<String> mediumOrHighRatings = new ArrayList<>();

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

        public Set<String> getSizesFromCombined() {
            return sizesFromCombined;
        }

        public void setSizesFromCombined(Set<String> sizesFromCombined) {
            this.sizesFromCombined = sizesFromCombined;
        }

        public Set<String> getRatingsFromCombined() {
            return ratingsFromCombined;
        }

        public void setRatingsFromCombined(Set<String> ratingsFromCombined) {
            this.ratingsFromCombined = ratingsFromCombined;
        }
    }
}

