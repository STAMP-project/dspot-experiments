/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.hql;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.Session;
import org.hibernate.hql.internal.ast.ASTQueryTranslatorFactory;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.hibernate.testing.junit4.ExtraAssertions;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test originally written to help verify/diagnose HHH-10125
 *
 * @author Steve Ebersole
 */
public class MapFunctionExpressionsTest extends BaseNonConfigCoreFunctionalTestCase {
    private final ASTQueryTranslatorFactory queryTranslatorFactory = new ASTQueryTranslatorFactory();

    @Test
    public void testMapKeyExpressionInWhere() {
        // NOTE : JPA requires that an alias be used in the key() expression.  Hibernate allows
        // path or alias.
        Session s = openSession();
        s.getTransaction().begin();
        // JPA form
        List contacts = s.createQuery("select c from Contact c join c.addresses a where key(a) is not null").list();
        Assert.assertEquals(1, contacts.size());
        MapFunctionExpressionsTest.Contact contact = ExtraAssertions.assertTyping(MapFunctionExpressionsTest.Contact.class, contacts.get(0));
        // Hibernate additional form
        contacts = s.createQuery("select c from Contact c where key(c.addresses) is not null").list();
        Assert.assertEquals(1, contacts.size());
        contact = ExtraAssertions.assertTyping(MapFunctionExpressionsTest.Contact.class, contacts.get(0));
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testMapKeyExpressionInSelect() {
        // NOTE : JPA requires that an alias be used in the key() expression.  Hibernate allows
        // path or alias.
        Session s = openSession();
        s.getTransaction().begin();
        // JPA form
        List types = s.createQuery("select key(a) from Contact c join c.addresses a").list();
        Assert.assertEquals(1, types.size());
        ExtraAssertions.assertTyping(MapFunctionExpressionsTest.AddressType.class, types.get(0));
        // Hibernate additional form
        types = s.createQuery("select key(c.addresses) from Contact c").list();
        Assert.assertEquals(1, types.size());
        ExtraAssertions.assertTyping(MapFunctionExpressionsTest.AddressType.class, types.get(0));
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testMapValueExpressionInSelect() {
        Session s = openSession();
        s.getTransaction().begin();
        List addresses = s.createQuery("select value(a) from Contact c join c.addresses a").list();
        Assert.assertEquals(1, addresses.size());
        ExtraAssertions.assertTyping(MapFunctionExpressionsTest.Address.class, addresses.get(0));
        addresses = s.createQuery("select value(c.addresses) from Contact c").list();
        Assert.assertEquals(1, addresses.size());
        ExtraAssertions.assertTyping(MapFunctionExpressionsTest.Address.class, addresses.get(0));
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testMapEntryExpressionInSelect() {
        Session s = openSession();
        s.getTransaction().begin();
        List addresses = s.createQuery("select entry(a) from Contact c join c.addresses a").list();
        Assert.assertEquals(1, addresses.size());
        ExtraAssertions.assertTyping(Map.Entry.class, addresses.get(0));
        addresses = s.createQuery("select entry(c.addresses) from Contact c").list();
        Assert.assertEquals(1, addresses.size());
        ExtraAssertions.assertTyping(Map.Entry.class, addresses.get(0));
        s.getTransaction().commit();
        s.close();
    }

    @Entity(name = "AddressType")
    @Table(name = "address_type")
    public static class AddressType {
        @Id
        public Integer id;

        String name;

        public AddressType() {
        }

        public AddressType(Integer id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    // @Embeddable
    @Entity(name = "Address")
    @Table(name = "address")
    public static class Address {
        @Id
        public Integer id;

        String street;

        String city;

        public Address() {
        }

        public Address(Integer id, String street, String city) {
            this.id = id;
            this.street = street;
            this.city = city;
        }
    }

    @Entity(name = "Contact")
    @Table(name = "contact")
    public static class Contact {
        @Id
        public Integer id;

        String name;

        // @JoinColumn
        // @ElementCollection
        // @MapKeyEnumerated(EnumType.STRING)
        // @MapKeyColumn(name = "addr_type")
        @OneToMany
        @JoinTable(name = "contact_address")
        @MapKeyJoinColumn(name = "address_type_id", referencedColumnName = "id")
        Map<MapFunctionExpressionsTest.AddressType, MapFunctionExpressionsTest.Address> addresses = new HashMap<MapFunctionExpressionsTest.AddressType, MapFunctionExpressionsTest.Address>();

        public Contact() {
        }

        public Contact(Integer id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}

