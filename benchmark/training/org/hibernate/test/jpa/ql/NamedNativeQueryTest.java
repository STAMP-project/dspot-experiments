/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.jpa.ql;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.dialect.MySQLDialect;
import org.hibernate.dialect.SQLServerDialect;
import org.hibernate.testing.SkipForDialect;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Janario Oliveira
 */
public class NamedNativeQueryTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testSingleSelect() {
        final String name = "Name";
        final String lastName = "LastName";
        final String fullName = (name + " ") + lastName;
        final DestinationEntity destination = createDestination(createFrom(name, lastName), fullName);
        Session session = openSession();
        Query select = session.getNamedQuery("DestinationEntity.selectIds");
        select.setParameterList("ids", Collections.singletonList(destination.id));
        Object[] unique = ((Object[]) (select.uniqueResult()));
        session.close();
        // Compare the Strings, not the actual IDs.  Can come back as, for ex,
        // a BigDecimal in Oracle.
        Assert.assertEquals(((destination.id) + ""), ((unique[0]) + ""));
        Assert.assertEquals(((destination.from.id) + ""), ((unique[1]) + ""));
        Assert.assertEquals(destination.fullNameFrom, unique[2]);
    }

    @Test
    public void testMultipleSelect() {
        final String name = "Name";
        final String lastName = "LastName";
        final List<Integer> ids = new ArrayList<Integer>();
        final int quantity = 10;
        final List<DestinationEntity> destinations = new ArrayList<DestinationEntity>();
        for (int i = 0; i < quantity; i++) {
            DestinationEntity createDestination = createDestination(createFrom((name + i), (lastName + i)), (((name + i) + lastName) + i));
            ids.add(createDestination.id);
            destinations.add(createDestination);
        }
        Session session = openSession();
        Query select = session.getNamedQuery("DestinationEntity.selectIds");
        select.setParameterList("ids", ids);
        List list = select.list();
        session.close();
        Assert.assertEquals(quantity, list.size());
        for (int i = 0; i < (list.size()); i++) {
            Object[] object = ((Object[]) (list.get(i)));
            DestinationEntity destination = destinations.get(i);
            // Compare the Strings, not the actual IDs.  Can come back as, for ex,
            // a BigDecimal in Oracle.
            Assert.assertEquals(((destination.id) + ""), ((object[0]) + ""));
            Assert.assertEquals(((destination.from.id) + ""), ((object[1]) + ""));
            Assert.assertEquals(destination.fullNameFrom, object[2]);
        }
    }

    @Test
    public void testInsertSingleValue() {
        final String name = "Name";
        final String lastName = "LastName";
        final String fullName = (name + " ") + lastName;
        final FromEntity fromEntity = createFrom(name, lastName);
        final int id = 10000;// id fake

        Session session = openSession();
        session.getTransaction().begin();
        Query insert = session.getNamedQuery("DestinationEntity.insert");
        insert.setParameter("generatedId", id);
        insert.setParameter("fromId", fromEntity.id);
        insert.setParameter("fullName", fullName);
        int executeUpdate = insert.executeUpdate();
        Assert.assertEquals(1, executeUpdate);
        session.getTransaction().commit();
        session.close();
        session = openSession();
        DestinationEntity get = ((DestinationEntity) (session.get(DestinationEntity.class, id)));
        session.close();
        Assert.assertEquals(fromEntity, get.from);
        Assert.assertEquals(fullName, get.fullNameFrom);
    }

    // TODO: Re-form DestinationEntity.insertSelect to something more supported?
    @Test
    @SkipForDialect(value = MySQLDialect.class, comment = "MySQL appears to have trouble with fe.id selected twice in one statement")
    @SkipForDialect(value = SQLServerDialect.class, comment = "SQL Server does not support the || operator.")
    public void testInsertMultipleValues() {
        final String name = "Name";
        final String lastName = "LastName";
        final List<Integer> ids = new ArrayList<Integer>();
        final int quantity = 10;
        final List<FromEntity> froms = new ArrayList<FromEntity>();
        for (int i = 0; i < quantity; i++) {
            FromEntity fe = createFrom((name + i), (lastName + i));
            froms.add(fe);
            ids.add(fe.id);
        }
        Session session = openSession();
        session.getTransaction().begin();
        Query insertSelect = session.getNamedQuery("DestinationEntity.insertSelect");
        insertSelect.setParameterList("ids", ids);
        int executeUpdate = insertSelect.executeUpdate();
        Assert.assertEquals(quantity, executeUpdate);
        session.getTransaction().commit();
        session.close();
        List<DestinationEntity> list = findDestinationByIds(ids);
        Assert.assertEquals(quantity, list.size());
        for (int i = 0; i < quantity; i++) {
            DestinationEntity de = ((DestinationEntity) (list.get(i)));
            FromEntity from = froms.get(i);
            Assert.assertEquals(from, de.from);
            Assert.assertEquals(((from.name) + (from.lastName)), de.fullNameFrom);
        }
    }

    @Test
    public void testUpdateSingleValue() {
        final String name = "Name";
        final String lastName = "LastName";
        final String fullName = (name + " ") + lastName;
        final FromEntity fromEntity = createFrom(name, lastName);
        final DestinationEntity destinationEntity = createDestination(fromEntity, fullName);
        final String inverseFullName = (lastName + " ") + name;
        final FromEntity anotherFrom = createFrom(lastName, name);
        Session session = openSession();
        session.getTransaction().begin();
        Query update = session.getNamedQuery("DestinationEntity.update");
        update.setParameter("idFrom", anotherFrom.id);
        update.setParameter("fullName", inverseFullName);
        update.setParameterList("ids", Collections.singletonList(destinationEntity.id));
        int executeUpdate = update.executeUpdate();
        Assert.assertEquals(1, executeUpdate);
        session.getTransaction().commit();
        session.close();
        session = openSession();
        session.beginTransaction();
        DestinationEntity get = ((DestinationEntity) (session.get(DestinationEntity.class, destinationEntity.id)));
        Assert.assertEquals(anotherFrom, get.from);
        Assert.assertEquals(inverseFullName, get.fullNameFrom);
        session.getTransaction().commit();
        session.close();
    }

    @Test
    public void testUpdateMultipleValues() {
        final String name = "Name";
        final String lastName = "LastName";
        final List<Integer> ids = new ArrayList<Integer>();
        final int quantity = 10;
        final List<DestinationEntity> destinations = new ArrayList<DestinationEntity>();
        for (int i = 0; i < quantity; i++) {
            FromEntity fe = createFrom((name + i), (lastName + i));
            DestinationEntity destination = createDestination(fe, ((fe.name) + (fe.lastName)));
            destinations.add(destination);
            ids.add(destination.id);
        }
        final String inverseFullName = (lastName + " ") + name;
        final FromEntity anotherFrom = createFrom(lastName, name);
        Session session = openSession();
        session.getTransaction().begin();
        Query update = session.getNamedQuery("DestinationEntity.update");
        update.setParameter("idFrom", anotherFrom.id);
        update.setParameter("fullName", inverseFullName);
        update.setParameterList("ids", ids);
        int executeUpdate = update.executeUpdate();
        Assert.assertEquals(quantity, executeUpdate);
        session.getTransaction().commit();
        session.close();
        List<DestinationEntity> list = findDestinationByIds(ids);
        Assert.assertEquals(quantity, list.size());
        for (int i = 0; i < quantity; i++) {
            DestinationEntity updated = ((DestinationEntity) (list.get(i)));
            Assert.assertEquals(anotherFrom, updated.from);
            Assert.assertEquals(inverseFullName, updated.fullNameFrom);
        }
    }

    @Test
    public void testDeleteSingleValue() {
        final String name = "Name";
        final String lastName = "LastName";
        final String fullName = (name + " ") + lastName;
        final FromEntity fromEntity = createFrom(name, lastName);
        final DestinationEntity destinationEntity = createDestination(fromEntity, fullName);
        Session session = openSession();
        session.getTransaction().begin();
        Query delete = session.getNamedQuery("DestinationEntity.delete");
        delete.setParameterList("ids", Collections.singletonList(destinationEntity.id));
        int executeUpdate = delete.executeUpdate();
        Assert.assertEquals(1, executeUpdate);
        session.getTransaction().commit();
        session.close();
        session = openSession();
        DestinationEntity get = ((DestinationEntity) (session.get(DestinationEntity.class, destinationEntity.id)));
        session.close();
        Assert.assertNull(get);
    }

    @Test
    public void testDeleteMultipleValues() {
        final String name = "Name";
        final String lastName = "LastName";
        final List<Integer> ids = new ArrayList<Integer>();
        final int quantity = 10;
        final List<DestinationEntity> destinations = new ArrayList<DestinationEntity>();
        for (int i = 0; i < quantity; i++) {
            FromEntity fe = createFrom((name + i), (lastName + i));
            DestinationEntity destination = createDestination(fe, ((fe.name) + (fe.lastName)));
            destinations.add(destination);
            ids.add(destination.id);
        }
        Session session = openSession();
        session.getTransaction().begin();
        Query delete = session.getNamedQuery("DestinationEntity.delete");
        delete.setParameterList("ids", ids);
        int executeUpdate = delete.executeUpdate();
        Assert.assertEquals(quantity, executeUpdate);
        session.getTransaction().commit();
        session.close();
        List<DestinationEntity> list = findDestinationByIds(ids);
        Assert.assertTrue(list.isEmpty());
    }
}

