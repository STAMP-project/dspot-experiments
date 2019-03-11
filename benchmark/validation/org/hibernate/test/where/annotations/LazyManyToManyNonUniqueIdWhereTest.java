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
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Where;
import org.hibernate.annotations.WhereJoinTable;
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
            org.hibernate.test.where.annotations.Material material = session.get(.class, 1);
            assertEquals("plastic", material.getName());
            // Material#ratings is mapped with lazy="true"
            assertFalse(Hibernate.isInitialized(material.getRatings()));
            assertEquals(1, material.getRatings().size());
            assertTrue(Hibernate.isInitialized(material.getRatings()));
            final org.hibernate.test.where.annotations.Rating rating = material.getRatings().iterator().next();
            assertEquals("high", rating.getName());
            org.hibernate.test.where.annotations.Building building = session.get(.class, 1);
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
            org.hibernate.test.where.annotations.Material material = session.get(.class, 1);
            assertEquals("plastic", material.getName());
            // Material#mediumOrHighRatingsFromCombined is mapped with lazy="true"
            assertFalse(Hibernate.isInitialized(material.getMediumOrHighRatingsFromCombined()));
            checkMediumOrHighRatings(material.getMediumOrHighRatingsFromCombined());
            org.hibernate.test.where.annotations.Rating highRating = null;
            for (org.hibernate.test.where.annotations.Rating rating : material.getMediumOrHighRatingsFromCombined()) {
                if ("high".equals(rating.getName())) {
                    highRating = rating;
                }
            }
            assertNotNull(highRating);
            // Material#sizesFromCombined is mapped with lazy="true"
            assertFalse(Hibernate.isInitialized(material.getSizesFromCombined()));
            assertEquals(1, material.getSizesFromCombined().size());
            assertTrue(Hibernate.isInitialized(material.getSizesFromCombined()));
            final org.hibernate.test.where.annotations.Size size = material.getSizesFromCombined().iterator().next();
            assertEquals("medium", size.getName());
            org.hibernate.test.where.annotations.Building building = session.get(.class, 1);
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

    @Entity(name = "Material")
    @Table(name = "MAIN_TABLE")
    @Where(clause = "CODE = 'MATERIAL'")
    public static class Material {
        private int id;

        private String name;

        private Set<LazyManyToManyNonUniqueIdWhereTest.Size> sizesFromCombined = new HashSet<>();

        private List<LazyManyToManyNonUniqueIdWhereTest.Rating> mediumOrHighRatingsFromCombined = new ArrayList<>();

        private Set<LazyManyToManyNonUniqueIdWhereTest.Rating> ratings = new HashSet<>();

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

        @ManyToMany
        @JoinTable(name = "ASSOCIATION_TABLE", joinColumns = { @JoinColumn(name = "MAIN_ID") }, inverseJoinColumns = { @JoinColumn(name = "ASSOCIATION_ID") })
        @WhereJoinTable(clause = "MAIN_CODE='MATERIAL' AND ASSOCIATION_CODE='SIZE'")
        @Immutable
        public Set<LazyManyToManyNonUniqueIdWhereTest.Size> getSizesFromCombined() {
            return sizesFromCombined;
        }

        public void setSizesFromCombined(Set<LazyManyToManyNonUniqueIdWhereTest.Size> sizesFromCombined) {
            this.sizesFromCombined = sizesFromCombined;
        }

        @ManyToMany
        @JoinTable(name = "ASSOCIATION_TABLE", joinColumns = { @JoinColumn(name = "MAIN_ID") }, inverseJoinColumns = { @JoinColumn(name = "ASSOCIATION_ID") })
        @WhereJoinTable(clause = "MAIN_CODE='MATERIAL' AND ASSOCIATION_CODE='RATING'")
        @Where(clause = "name = 'high' or name = 'medium'")
        @Immutable
        public List<LazyManyToManyNonUniqueIdWhereTest.Rating> getMediumOrHighRatingsFromCombined() {
            return mediumOrHighRatingsFromCombined;
        }

        public void setMediumOrHighRatingsFromCombined(List<LazyManyToManyNonUniqueIdWhereTest.Rating> mediumOrHighRatingsFromCombined) {
            this.mediumOrHighRatingsFromCombined = mediumOrHighRatingsFromCombined;
        }

        @ManyToMany
        @JoinTable(name = "MATERIAL_RATINGS", joinColumns = { @JoinColumn(name = "MATERIAL_ID") }, inverseJoinColumns = { @JoinColumn(name = "RATING_ID") })
        @Immutable
        public Set<LazyManyToManyNonUniqueIdWhereTest.Rating> getRatings() {
            return ratings;
        }

        public void setRatings(Set<LazyManyToManyNonUniqueIdWhereTest.Rating> ratings) {
            this.ratings = ratings;
        }
    }

    @Entity(name = "Building")
    @Table(name = "MAIN_TABLE")
    @Where(clause = "CODE = 'BUILDING'")
    public static class Building {
        private int id;

        private String name;

        private Set<LazyManyToManyNonUniqueIdWhereTest.Size> sizesFromCombined = new HashSet<>();

        private Set<LazyManyToManyNonUniqueIdWhereTest.Rating> ratingsFromCombined = new HashSet<>();

        private List<LazyManyToManyNonUniqueIdWhereTest.Rating> mediumOrHighRatings = new ArrayList<>();

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

        @ManyToMany
        @JoinTable(name = "ASSOCIATION_TABLE", joinColumns = { @JoinColumn(name = "MAIN_ID") }, inverseJoinColumns = { @JoinColumn(name = "ASSOCIATION_ID") })
        @WhereJoinTable(clause = "MAIN_CODE='BUILDING' AND ASSOCIATION_CODE='SIZE'")
        @Immutable
        public Set<LazyManyToManyNonUniqueIdWhereTest.Size> getSizesFromCombined() {
            return sizesFromCombined;
        }

        public void setSizesFromCombined(Set<LazyManyToManyNonUniqueIdWhereTest.Size> sizesFromCombined) {
            this.sizesFromCombined = sizesFromCombined;
        }

        @ManyToMany
        @JoinTable(name = "ASSOCIATION_TABLE", joinColumns = { @JoinColumn(name = "MAIN_ID") }, inverseJoinColumns = { @JoinColumn(name = "ASSOCIATION_ID") })
        @WhereJoinTable(clause = "MAIN_CODE='BUILDING' AND ASSOCIATION_CODE='RATING'")
        @Immutable
        public Set<LazyManyToManyNonUniqueIdWhereTest.Rating> getRatingsFromCombined() {
            return ratingsFromCombined;
        }

        public void setRatingsFromCombined(Set<LazyManyToManyNonUniqueIdWhereTest.Rating> ratingsFromCombined) {
            this.ratingsFromCombined = ratingsFromCombined;
        }

        @ManyToMany
        @JoinTable(name = "BUILDING_RATINGS", joinColumns = { @JoinColumn(name = "BUILDING_ID") }, inverseJoinColumns = { @JoinColumn(name = "RATING_ID") })
        @Where(clause = "name = 'high' or name = 'medium'")
        @Immutable
        public List<LazyManyToManyNonUniqueIdWhereTest.Rating> getMediumOrHighRatings() {
            return mediumOrHighRatings;
        }

        public void setMediumOrHighRatings(List<LazyManyToManyNonUniqueIdWhereTest.Rating> mediumOrHighRatings) {
            this.mediumOrHighRatings = mediumOrHighRatings;
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
}

