/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.where.annotations;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Where;
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

    @Entity(name = "Material")
    @Table(name = "MAIN_TABLE")
    @Where(clause = "CODE = 'MATERIAL'")
    public static class Material {
        private int id;

        private String name;

        private Set<LazyElementCollectionWithLazyManyToOneNonUniqueIdWhereTest.ContainedSize> containedSizesFromCombined = new HashSet<>();

        private List<LazyElementCollectionWithLazyManyToOneNonUniqueIdWhereTest.Rating> mediumOrHighRatingsFromCombined = new ArrayList<>();

        private Set<LazyElementCollectionWithLazyManyToOneNonUniqueIdWhereTest.ContainedRating> containedRatings = new HashSet<>();

        @Id
        @Column(name = "ID")
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        @Column(name = "NAME")
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @ElementCollection
        @CollectionTable(name = "COLLECTION_TABLE", joinColumns = { @JoinColumn(name = "MAIN_ID") })
        @AssociationOverrides({ @AssociationOverride(name = "size", joinColumns = { @JoinColumn(name = "ASSOCIATION_ID") }) })
        @Where(clause = "MAIN_CODE='MATERIAL' AND ASSOCIATION_CODE='SIZE'")
        @Immutable
        public Set<LazyElementCollectionWithLazyManyToOneNonUniqueIdWhereTest.ContainedSize> getContainedSizesFromCombined() {
            return containedSizesFromCombined;
        }

        public void setContainedSizesFromCombined(Set<LazyElementCollectionWithLazyManyToOneNonUniqueIdWhereTest.ContainedSize> containedSizesFromCombined) {
            this.containedSizesFromCombined = containedSizesFromCombined;
        }

        @ElementCollection
        @CollectionTable(name = "MATERIAL_RATINGS", joinColumns = { @JoinColumn(name = "MATERIAL_ID") })
        @AssociationOverrides({ @AssociationOverride(name = "rating", joinColumns = { @JoinColumn(name = "RATING_ID") }) })
        @Immutable
        public Set<LazyElementCollectionWithLazyManyToOneNonUniqueIdWhereTest.ContainedRating> getContainedRatings() {
            return containedRatings;
        }

        public void setContainedRatings(Set<LazyElementCollectionWithLazyManyToOneNonUniqueIdWhereTest.ContainedRating> containedRatings) {
            this.containedRatings = containedRatings;
        }
    }

    @Entity(name = "Building")
    @Table(name = "MAIN_TABLE")
    @Where(clause = "CODE = 'BUILDING'")
    public static class Building {
        private int id;

        private String name;

        private Set<LazyElementCollectionWithLazyManyToOneNonUniqueIdWhereTest.ContainedSize> containedSizesFromCombined = new HashSet<>();

        private Set<LazyElementCollectionWithLazyManyToOneNonUniqueIdWhereTest.ContainedRating> containedRatingsFromCombined = new HashSet<>();

        private List<LazyElementCollectionWithLazyManyToOneNonUniqueIdWhereTest.Rating> mediumOrHighRatings = new ArrayList<>();

        @Id
        @Column(name = "ID")
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        @Column(name = "NAME")
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @ElementCollection
        @CollectionTable(name = "COLLECTION_TABLE", joinColumns = { @JoinColumn(name = "MAIN_ID") })
        @Where(clause = "MAIN_CODE='BUILDING' AND ASSOCIATION_CODE='SIZE'")
        @Immutable
        public Set<LazyElementCollectionWithLazyManyToOneNonUniqueIdWhereTest.ContainedSize> getContainedSizesFromCombined() {
            return containedSizesFromCombined;
        }

        public void setContainedSizesFromCombined(Set<LazyElementCollectionWithLazyManyToOneNonUniqueIdWhereTest.ContainedSize> containedSizesFromCombined) {
            this.containedSizesFromCombined = containedSizesFromCombined;
        }

        @ElementCollection
        @CollectionTable(name = "COLLECTION_TABLE", joinColumns = { @JoinColumn(name = "MAIN_ID") })
        @Where(clause = "MAIN_CODE='BUILDING' AND ASSOCIATION_CODE='RATING'")
        @Immutable
        public Set<LazyElementCollectionWithLazyManyToOneNonUniqueIdWhereTest.ContainedRating> getContainedRatingsFromCombined() {
            return containedRatingsFromCombined;
        }

        public void setContainedRatingsFromCombined(Set<LazyElementCollectionWithLazyManyToOneNonUniqueIdWhereTest.ContainedRating> containedRatingsFromCombined) {
            this.containedRatingsFromCombined = containedRatingsFromCombined;
        }
    }

    @Entity(name = "Size")
    @Table(name = "MAIN_TABLE")
    @Where(clause = "CODE = 'SIZE'")
    public static class Size {
        private int id;

        private String name;

        @Id
        @Column(name = "ID")
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        @Column(name = "NAME")
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @Embeddable
    public static class ContainedSize {
        private LazyElementCollectionWithLazyManyToOneNonUniqueIdWhereTest.Size size;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "ASSOCIATION_ID")
        public LazyElementCollectionWithLazyManyToOneNonUniqueIdWhereTest.Size getSize() {
            return size;
        }

        public void setSize(LazyElementCollectionWithLazyManyToOneNonUniqueIdWhereTest.Size size) {
            this.size = size;
        }
    }

    @Entity(name = "Rating")
    @Table(name = "MAIN_TABLE")
    @Where(clause = "CODE = 'RATING'")
    public static class Rating {
        private int id;

        private String name;

        @Id
        @Column(name = "ID")
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        @Column(name = "NAME")
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @Embeddable
    public static class ContainedRating {
        private LazyElementCollectionWithLazyManyToOneNonUniqueIdWhereTest.Rating rating;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "ASSOCIATION_ID")
        public LazyElementCollectionWithLazyManyToOneNonUniqueIdWhereTest.Rating getRating() {
            return rating;
        }

        public void setRating(LazyElementCollectionWithLazyManyToOneNonUniqueIdWhereTest.Rating rating) {
            this.rating = rating;
        }
    }
}

