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
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
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
public class MapKeyColumnManyToManyTest extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-12150")
    public void testReferenceToAlreadyMappedColumn() {
        inTransaction(( session) -> {
            org.hibernate.test.jpa.compliance.tck2_2.mapkeycolumn.AddressCapable2 holder = new org.hibernate.test.jpa.compliance.tck2_2.mapkeycolumn.AddressCapable2(1, "osd");
            org.hibernate.test.jpa.compliance.tck2_2.mapkeycolumn.Address2 address = new org.hibernate.test.jpa.compliance.tck2_2.mapkeycolumn.Address2(1, "123 Main St");
            session.persist(holder);
            session.persist(address);
        });
        inTransaction(( session) -> {
            org.hibernate.test.jpa.compliance.tck2_2.mapkeycolumn.AddressCapable2 holder = session.get(.class, 1);
            org.hibernate.test.jpa.compliance.tck2_2.mapkeycolumn.Address2 address = session.get(.class, 1);
            holder.addresses.put("work", address);
            session.persist(holder);
        });
        inTransaction(( session) -> {
            org.hibernate.test.jpa.compliance.tck2_2.mapkeycolumn.AddressCapable2 holder = session.get(.class, 1);
            assertEquals(1, holder.addresses.size());
            final Entry<String, org.hibernate.test.jpa.compliance.tck2_2.mapkeycolumn.Address2> entry = holder.addresses.entrySet().iterator().next();
            assertEquals("work", entry.getKey());
            assertEquals(null, entry.getValue().type);
            session.remove(holder);
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-12150")
    public void testReferenceToNonMappedColumn() {
        inTransaction(( session) -> {
            org.hibernate.test.jpa.compliance.tck2_2.mapkeycolumn.AddressCapable holder = new org.hibernate.test.jpa.compliance.tck2_2.mapkeycolumn.AddressCapable(1, "osd");
            org.hibernate.test.jpa.compliance.tck2_2.mapkeycolumn.Address address = new org.hibernate.test.jpa.compliance.tck2_2.mapkeycolumn.Address(1, "123 Main St");
            session.persist(holder);
            session.persist(address);
        });
        inTransaction(( session) -> {
            org.hibernate.test.jpa.compliance.tck2_2.mapkeycolumn.AddressCapable holder = session.get(.class, 1);
            org.hibernate.test.jpa.compliance.tck2_2.mapkeycolumn.Address address = session.get(.class, 1);
            holder.addresses.put("work", address);
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
        @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
        public Map<String, MapKeyColumnManyToManyTest.Address> addresses = new HashMap<>();

        public AddressCapable() {
        }

        public AddressCapable(Integer id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    @Entity(name = "Address")
    @Table(name = "addresses")
    public static class Address {
        @Id
        public Integer id;

        public String street;

        public Address() {
        }

        public Address(Integer id, String street) {
            this.id = id;
            this.street = street;
        }
    }

    @Entity(name = "AddressCapable2")
    @Table(name = "address_capables2")
    public static class AddressCapable2 {
        @Id
        public Integer id;

        public String name;

        @MapKeyColumn(name = "a_type")
        @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
        public Map<String, MapKeyColumnManyToManyTest.Address2> addresses = new HashMap<>();

        public AddressCapable2() {
        }

        public AddressCapable2(Integer id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    @Entity(name = "Address2")
    @Table(name = "addresses2")
    public static class Address2 {
        @Id
        public Integer id;

        public String street;

        @Column(name = "a_type")
        public String type;

        public Address2() {
        }

        public Address2(Integer id, String street) {
            this.id = id;
            this.street = street;
        }
    }
}

