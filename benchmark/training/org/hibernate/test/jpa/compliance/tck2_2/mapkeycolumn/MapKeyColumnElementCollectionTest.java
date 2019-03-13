/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.test.jpa.compliance.tck2_2.mapkeycolumn;


import Map.Entry;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class MapKeyColumnElementCollectionTest extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-12150")
    public void testReferenceToAlreadyMappedColumn() {
        inTransaction(( session) -> {
            org.hibernate.test.jpa.compliance.tck2_2.mapkeycolumn.AddressCapable2 holder = new org.hibernate.test.jpa.compliance.tck2_2.mapkeycolumn.AddressCapable2(1, "osd");
            session.persist(holder);
        });
        inTransaction(( session) -> {
            org.hibernate.test.jpa.compliance.tck2_2.mapkeycolumn.AddressCapable2 holder = session.get(.class, 1);
            org.hibernate.test.jpa.compliance.tck2_2.mapkeycolumn.Address2 address = new org.hibernate.test.jpa.compliance.tck2_2.mapkeycolumn.Address2(1, "123 Main St");
            address.type = "work";
            holder.addresses.put("work", address);
            session.persist(holder);
        });
        inTransaction(( session) -> {
            org.hibernate.test.jpa.compliance.tck2_2.mapkeycolumn.AddressCapable2 holder = session.get(.class, 1);
            assertEquals(1, holder.addresses.size());
            final Entry<String, org.hibernate.test.jpa.compliance.tck2_2.mapkeycolumn.Address2> entry = holder.addresses.entrySet().iterator().next();
            assertEquals("work", entry.getKey());
            assertEquals("work", entry.getValue().type);
            session.remove(holder);
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-12150")
    public void testReferenceToNonMappedColumn() {
        inTransaction(( session) -> {
            org.hibernate.test.jpa.compliance.tck2_2.mapkeycolumn.AddressCapable holder = new org.hibernate.test.jpa.compliance.tck2_2.mapkeycolumn.AddressCapable(1, "osd");
            session.persist(holder);
        });
        inTransaction(( session) -> {
            org.hibernate.test.jpa.compliance.tck2_2.mapkeycolumn.AddressCapable holder = session.get(.class, 1);
            holder.addresses.put("work", new org.hibernate.test.jpa.compliance.tck2_2.mapkeycolumn.Address(1, "123 Main St"));
            session.persist(holder);
        });
        inTransaction(( session) -> {
            org.hibernate.test.jpa.compliance.tck2_2.mapkeycolumn.AddressCapable holder = session.get(.class, 1);
            assertEquals(1, holder.addresses.size());
            final Entry<String, org.hibernate.test.jpa.compliance.tck2_2.mapkeycolumn.Address> entry = holder.addresses.entrySet().iterator().next();
            assertEquals("work", entry.getKey());
            session.remove(holder);
        });
    }

    @Entity(name = "AddressCapable")
    @Table(name = "address_capables")
    public static class AddressCapable {
        @Id
        public Integer id;

        public String name;

        @MapKeyColumn(name = "a_type")
        @ElementCollection
        public Map<String, MapKeyColumnElementCollectionTest.Address> addresses = new HashMap<>();

        public AddressCapable() {
        }

        public AddressCapable(Integer id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    @Embeddable
    public static class Address {
        public Integer buildingNumber;

        public String street;

        public Address() {
        }

        public Address(Integer buildingNumber, String street) {
            this.buildingNumber = buildingNumber;
            this.street = street;
        }
    }

    @Entity(name = "AddressCapable2")
    @Table(name = "address_capables2")
    public static class AddressCapable2 {
        @Id
        public Integer id;

        public String name;

        @MapKeyColumn(name = "a_type", insertable = false, updatable = false)
        @ElementCollection
        public Map<String, MapKeyColumnElementCollectionTest.Address2> addresses = new HashMap<>();

        public AddressCapable2() {
        }

        public AddressCapable2(Integer id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    @Embeddable
    public static class Address2 {
        public Integer buildingNumber;

        public String street;

        @Column(name = "a_type")
        public String type;

        public Address2() {
        }

        public Address2(Integer buildingNumber, String street) {
            this.buildingNumber = buildingNumber;
            this.street = street;
        }
    }
}

