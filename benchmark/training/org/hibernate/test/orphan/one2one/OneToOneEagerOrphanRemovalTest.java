/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.orphan.one2one;


import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Chris Cranford
 */
@TestForIssue(jiraKey = "HHH-9663")
public class OneToOneEagerOrphanRemovalTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testOneToOneEagerOrphanRemoval() {
        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        // Initialize the data
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            final org.hibernate.test.orphan.one2one.PaintColor color = new org.hibernate.test.orphan.one2one.PaintColor(1, "Red");
            final org.hibernate.test.orphan.one2one.Engine engine = new org.hibernate.test.orphan.one2one.Engine(1, 275);
            final org.hibernate.test.orphan.one2one.Car car = new org.hibernate.test.orphan.one2one.Car(1, engine, color);
            session.save(engine);
            session.save(color);
            session.save(car);
        });
        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        // Test orphan removal for unidirectional relationship
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            final org.hibernate.test.orphan.one2one.Car car = session.find(.class, 1);
            car.setEngine(null);
            session.update(car);
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            final org.hibernate.test.orphan.one2one.Car car = session.find(.class, 1);
            assertNull(car.getEngine());
            final org.hibernate.test.orphan.one2one.Engine engine = session.find(.class, 1);
            assertNull(engine);
        });
        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        // Test orphan removal for bidirectional relationship
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            final org.hibernate.test.orphan.one2one.Car car = session.find(.class, 1);
            car.setPaintColor(null);
            session.update(car);
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            final org.hibernate.test.orphan.one2one.Car car = session.find(.class, 1);
            assertNull(car.getPaintColor());
            final org.hibernate.test.orphan.one2one.PaintColor color = session.find(.class, 1);
            assertNull(color);
        });
    }

    @Entity(name = "Car")
    public static class Car {
        @Id
        private Integer id;

        // represents a bidirectional one-to-one
        @OneToOne(orphanRemoval = true, fetch = FetchType.EAGER)
        private OneToOneEagerOrphanRemovalTest.PaintColor paintColor;

        // represents a unidirectional one-to-one
        @OneToOne(orphanRemoval = true, fetch = FetchType.EAGER)
        private OneToOneEagerOrphanRemovalTest.Engine engine;

        Car() {
            // Required by JPA
        }

        Car(Integer id, OneToOneEagerOrphanRemovalTest.Engine engine, OneToOneEagerOrphanRemovalTest.PaintColor paintColor) {
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

        public OneToOneEagerOrphanRemovalTest.PaintColor getPaintColor() {
            return paintColor;
        }

        public void setPaintColor(OneToOneEagerOrphanRemovalTest.PaintColor paintColor) {
            this.paintColor = paintColor;
        }

        public OneToOneEagerOrphanRemovalTest.Engine getEngine() {
            return engine;
        }

        public void setEngine(OneToOneEagerOrphanRemovalTest.Engine engine) {
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
        private OneToOneEagerOrphanRemovalTest.Car car;

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

        public OneToOneEagerOrphanRemovalTest.Car getCar() {
            return car;
        }

        public void setCar(OneToOneEagerOrphanRemovalTest.Car car) {
            this.car = car;
        }
    }
}

