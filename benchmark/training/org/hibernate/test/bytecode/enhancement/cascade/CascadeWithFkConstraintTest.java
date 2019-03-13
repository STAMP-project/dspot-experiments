/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.bytecode.enhancement.cascade;


import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.bytecode.enhancement.BytecodeEnhancerRunner;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Luis Barreiro
 */
@TestForIssue(jiraKey = "HHH-10252")
@RunWith(BytecodeEnhancerRunner.class)
public class CascadeWithFkConstraintTest extends BaseCoreFunctionalTestCase {
    private String garageId;

    private String car1Id;

    private String car2Id;

    @Test
    public void test() {
        // Remove garage
        TransactionUtil.doInJPA(this::sessionFactory, ( em) -> {
            org.hibernate.test.bytecode.enhancement.cascade.Garage toRemoveGarage = em.find(.class, garageId);
            em.remove(toRemoveGarage);
        });
        // Check if there is no garage but cars are still present
        TransactionUtil.doInJPA(this::sessionFactory, ( em) -> {
            org.hibernate.test.bytecode.enhancement.cascade.Garage foundGarage = em.find(.class, garageId);
            Assert.assertNull(foundGarage);
            org.hibernate.test.bytecode.enhancement.cascade.Car foundCar1 = em.find(.class, car1Id);
            Assert.assertEquals(car1Id, foundCar1.id);
            org.hibernate.test.bytecode.enhancement.cascade.Car foundCar2 = em.find(.class, car2Id);
            Assert.assertEquals(car2Id, foundCar2.id);
        });
    }

    // --- //
    @Entity
    @Table(name = "GARAGE")
    private static class Garage {
        @Id
        String id;

        @OneToMany
        @JoinColumn(name = "GARAGE_ID")
        Set<CascadeWithFkConstraintTest.Car> cars = new HashSet<>();

        Garage() {
            id = UUID.randomUUID().toString();
        }

        void insert(CascadeWithFkConstraintTest.Car aCar) {
            cars.add(aCar);
        }
    }

    @Entity
    @Table(name = "CAR")
    public static class Car {
        @Id
        String id;

        Car() {
            id = UUID.randomUUID().toString();
        }
    }
}

