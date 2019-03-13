/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.jpa.orphan.one2one;


import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Chris Cranford
 */
@TestForIssue(jiraKey = "HHH-9663")
public class OneToOneLazyNonOptionalOrphanRemovalTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testOneToOneLazyNonOptionalOrphanRemoval() {
        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        // Initialize the data
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            final org.hibernate.test.jpa.orphan.one2one.PaintColor color = new org.hibernate.test.jpa.orphan.one2one.PaintColor(1, "Red");
            final org.hibernate.test.jpa.orphan.one2one.Engine engine1 = new org.hibernate.test.jpa.orphan.one2one.Engine(1, 275);
            final org.hibernate.test.jpa.orphan.one2one.Engine engine2 = new org.hibernate.test.jpa.orphan.one2one.Engine(2, 295);
            final org.hibernate.test.jpa.orphan.one2one.Car car = new org.hibernate.test.jpa.orphan.one2one.Car(1, engine1, color);
            entityManager.persist(engine1);
            entityManager.persist(engine2);
            entityManager.persist(color);
            entityManager.persist(car);
        });
        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        // Test orphan removal for unidirectional relationship
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            final org.hibernate.test.jpa.orphan.one2one.Car car = entityManager.find(.class, 1);
            final org.hibernate.test.jpa.orphan.one2one.Engine engine = entityManager.find(.class, 2);
            car.setEngine(engine);
            entityManager.merge(car);
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            final org.hibernate.test.jpa.orphan.one2one.Car car = entityManager.find(.class, 1);
            assertNotNull(car.getEngine());
            final org.hibernate.test.jpa.orphan.one2one.Engine engine = entityManager.find(.class, 1);
            assertNull(engine);
        });
        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        // Test orphan removal for bidirectional relationship
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            final org.hibernate.test.jpa.orphan.one2one.PaintColor color = new org.hibernate.test.jpa.orphan.one2one.PaintColor(2, "Blue");
            final org.hibernate.test.jpa.orphan.one2one.Car car = entityManager.find(.class, 1);
            car.setPaintColor(color);
            entityManager.persist(color);
            entityManager.merge(car);
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            final org.hibernate.test.jpa.orphan.one2one.Car car = entityManager.find(.class, 1);
            assertNotNull(car.getPaintColor());
            final org.hibernate.test.jpa.orphan.one2one.PaintColor color = entityManager.find(.class, 1);
            assertNull(color);
        });
    }

    @Entity(name = "Car")
    public static class Car {
        @Id
        private Integer id;

        // represents a bidirectional one-to-one
        @OneToOne(orphanRemoval = true, fetch = FetchType.LAZY, optional = false)
        private OneToOneLazyNonOptionalOrphanRemovalTest.PaintColor paintColor;

        // represents a unidirectional one-to-one
        @OneToOne(orphanRemoval = true, fetch = FetchType.LAZY, optional = false)
        private OneToOneLazyNonOptionalOrphanRemovalTest.Engine engine;

        Car() {
            // Required by JPA
        }

        Car(Integer id, OneToOneLazyNonOptionalOrphanRemovalTest.Engine engine, OneToOneLazyNonOptionalOrphanRemovalTest.PaintColor paintColor) {
            this.id = id;
            this.engine = engine;
            this.paintColor = paintColor;
            paintColor.setCar(this);
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public OneToOneLazyNonOptionalOrphanRemovalTest.PaintColor getPaintColor() {
            return paintColor;
        }

        public void setPaintColor(OneToOneLazyNonOptionalOrphanRemovalTest.PaintColor paintColor) {
            this.paintColor = paintColor;
        }

        public OneToOneLazyNonOptionalOrphanRemovalTest.Engine getEngine() {
            return engine;
        }

        public void setEngine(OneToOneLazyNonOptionalOrphanRemovalTest.Engine engine) {
            this.engine = engine;
        }
    }

    @Entity(name = "Engine")
    public static class Engine {
        @Id
        private Integer id;

        private Integer horsePower;

        Engine() {
            // Required by JPA
        }

        Engine(Integer id, int horsePower) {
            this.id = id;
            this.horsePower = horsePower;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getHorsePower() {
            return horsePower;
        }

        public void setHorsePower(Integer horsePower) {
            this.horsePower = horsePower;
        }
    }

    @Entity(name = "PaintColor")
    public static class PaintColor {
        @Id
        private Integer id;

        private String color;

        @OneToOne(mappedBy = "paintColor")
        private OneToOneLazyNonOptionalOrphanRemovalTest.Car car;

        PaintColor() {
            // Required by JPA
        }

        PaintColor(Integer id, String color) {
            this.id = id;
            this.color = color;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public OneToOneLazyNonOptionalOrphanRemovalTest.Car getCar() {
            return car;
        }

        public void setCar(OneToOneLazyNonOptionalOrphanRemovalTest.Car car) {
            this.car = car;
        }
    }
}

