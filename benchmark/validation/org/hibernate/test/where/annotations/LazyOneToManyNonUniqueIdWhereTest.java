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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Where;
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

        private Set<LazyOneToManyNonUniqueIdWhereTest.Size> sizesFromCombined = new HashSet<>();

        private List<LazyOneToManyNonUniqueIdWhereTest.Rating> mediumOrHighRatingsFromCombined = new ArrayList<>();

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

        @OneToMany
        @JoinColumn(name = "MATERIAL_OWNER_ID")
        @Immutable
        public Set<LazyOneToManyNonUniqueIdWhereTest.Size> getSizesFromCombined() {
            return sizesFromCombined;
        }

        public void setSizesFromCombined(Set<LazyOneToManyNonUniqueIdWhereTest.Size> sizesFromCombined) {
            this.sizesFromCombined = sizesFromCombined;
        }

        @OneToMany
        @JoinColumn(name = "MATERIAL_OWNER_ID")
        @Where(clause = "name = 'high' or name = 'medium'")
        @Immutable
        public List<LazyOneToManyNonUniqueIdWhereTest.Rating> getMediumOrHighRatingsFromCombined() {
            return mediumOrHighRatingsFromCombined;
        }

        public void setMediumOrHighRatingsFromCombined(List<LazyOneToManyNonUniqueIdWhereTest.Rating> mediumOrHighRatingsFromCombined) {
            this.mediumOrHighRatingsFromCombined = mediumOrHighRatingsFromCombined;
        }
    }

    @Entity(name = "Building")
    @Table(name = "MAIN_TABLE")
    @Where(clause = "CODE = 'BUILDING'")
    public static class Building {
        private int id;

        private String name;

        private Set<LazyOneToManyNonUniqueIdWhereTest.Size> sizesFromCombined = new HashSet<>();

        private Set<LazyOneToManyNonUniqueIdWhereTest.Rating> ratingsFromCombined = new HashSet<>();

        private List<LazyOneToManyNonUniqueIdWhereTest.Rating> mediumOrHighRatings = new ArrayList<>();

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

        @OneToMany
        @JoinColumn(name = "BUILDING_OWNER_ID")
        @Immutable
        public Set<LazyOneToManyNonUniqueIdWhereTest.Size> getSizesFromCombined() {
            return sizesFromCombined;
        }

        public void setSizesFromCombined(Set<LazyOneToManyNonUniqueIdWhereTest.Size> sizesFromCombined) {
            this.sizesFromCombined = sizesFromCombined;
        }

        @OneToMany
        @JoinColumn(name = "BUILDING_OWNER_ID")
        @Immutable
        public Set<LazyOneToManyNonUniqueIdWhereTest.Rating> getRatingsFromCombined() {
            return ratingsFromCombined;
        }

        public void setRatingsFromCombined(Set<LazyOneToManyNonUniqueIdWhereTest.Rating> ratingsFromCombined) {
            this.ratingsFromCombined = ratingsFromCombined;
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

