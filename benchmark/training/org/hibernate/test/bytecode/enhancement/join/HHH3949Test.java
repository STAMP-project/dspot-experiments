package org.hibernate.test.bytecode.enhancement.join;


import FetchMode.JOIN;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.hibernate.Session;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.bytecode.enhancement.BytecodeEnhancerRunner;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@TestForIssue(jiraKey = "HHH-3949")
@RunWith(BytecodeEnhancerRunner.class)
public class HHH3949Test extends BaseCoreFunctionalTestCase {
    @Test
    public void test1() {
        // verify the work around query
        performQueryAndVerifyPersonResults("from Person p fetch all properties left join fetch p.vehicle");
        performQueryAndVerifyPersonResults("from Person p left join fetch p.vehicle");
    }

    @Test
    public void test2() {
        performQueryAndVerifyVehicleResults("from Vehicle v fetch all properties left join fetch v.driver");
        performQueryAndVerifyVehicleResults("from Vehicle v left join fetch v.driver");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test3() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            List<org.hibernate.test.bytecode.enhancement.join.Person> persons = ((List<org.hibernate.test.bytecode.enhancement.join.Person>) (s.createCriteria(.class).setFetchMode("vehicle", FetchMode.JOIN).list()));
            for (org.hibernate.test.bytecode.enhancement.join.Person person : persons) {
                if (shouldHaveVehicle(person)) {
                    assertNotNull(person.getVehicle());
                    assertNotNull(person.getVehicle().getDriver());
                }
            }
        });
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test4() {
        java.util.List<HHH3949Test.Vehicle> vehicles;
        try (Session s = openSession()) {
            vehicles = ((java.util.List<HHH3949Test.Vehicle>) (s.createCriteria(HHH3949Test.Vehicle.class).setFetchMode("driver", JOIN).list()));
        }
        for (HHH3949Test.Vehicle vehicle : vehicles) {
            if (shouldHaveDriver(vehicle)) {
                Assert.assertNotNull(vehicle.getDriver());
                Assert.assertNotNull(vehicle.getDriver().getVehicle());
            }
        }
    }

    // --- //
    @Entity(name = "Person")
    @Table(name = "PERSON")
    private static class Person {
        @Id
        @GeneratedValue
        Long id;

        String name;

        @OneToOne(optional = true, mappedBy = "driver", fetch = FetchType.LAZY)
        @LazyToOne(LazyToOneOption.NO_PROXY)
        HHH3949Test.Vehicle vehicle;

        Person() {
        }

        Person(String name) {
            this.name = name;
        }

        HHH3949Test.Vehicle getVehicle() {
            return vehicle;
        }

        void setVehicle(HHH3949Test.Vehicle vehicle) {
            this.vehicle = vehicle;
        }
    }

    @Entity(name = "Vehicle")
    @Table(name = "VEHICLE")
    private static class Vehicle {
        @Id
        @GeneratedValue
        Long id;

        String name;

        @OneToOne(optional = true, fetch = FetchType.LAZY)
        HHH3949Test.Person driver;

        Vehicle() {
        }

        Vehicle(String name) {
            this.name = name;
        }

        HHH3949Test.Person getDriver() {
            return driver;
        }

        void setDriver(HHH3949Test.Person driver) {
            this.driver = driver;
        }
    }
}

