/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.collection.list;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.hibernate.Session;
import org.hibernate.collection.internal.PersistentList;
import org.hibernate.jdbc.Work;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.collection.QueryableCollection;
import org.hibernate.sql.SimpleSelect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests related to operations on a PersistentList
 *
 * @author Steve Ebersole
 */
public class PersistentListTest extends BaseCoreFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-5732")
    public void testInverseListIndex() {
        // make sure no one changes the mapping
        final CollectionPersister collectionPersister = sessionFactory().getCollectionPersister(((ListOwner.class.getName()) + ".children"));
        Assert.assertTrue(collectionPersister.isInverse());
        // do some creations...
        Session session = openSession();
        session.beginTransaction();
        ListOwner root = new ListOwner("root");
        ListOwner child1 = new ListOwner("c1");
        root.getChildren().add(child1);
        child1.setParent(root);
        ListOwner child2 = new ListOwner("c2");
        root.getChildren().add(child2);
        child2.setParent(root);
        session.save(root);
        session.getTransaction().commit();
        session.close();
        // now, make sure the list-index column gotten written...
        final Session session2 = openSession();
        session2.beginTransaction();
        session2.doWork(new Work() {
            @Override
            public void execute(Connection connection) throws SQLException {
                final QueryableCollection queryableCollection = ((QueryableCollection) (collectionPersister));
                SimpleSelect select = new SimpleSelect(getDialect()).setTableName(queryableCollection.getTableName()).addColumn("NAME").addColumn("LIST_INDEX").addCondition("NAME", "<>", "?");
                PreparedStatement preparedStatement = getJdbcCoordinator().getStatementPreparer().prepareStatement(select.toStatementString());
                preparedStatement.setString(1, "root");
                ResultSet resultSet = getJdbcCoordinator().getResultSetReturn().extract(preparedStatement);
                Map<String, Integer> valueMap = new HashMap<String, Integer>();
                while (resultSet.next()) {
                    final String name = resultSet.getString(1);
                    Assert.assertFalse("NAME column was null", resultSet.wasNull());
                    final int position = resultSet.getInt(2);
                    Assert.assertFalse("LIST_INDEX column was null", resultSet.wasNull());
                    valueMap.put(name, position);
                } 
                Assert.assertEquals(2, valueMap.size());
                // c1 should be list index 0
                Assert.assertEquals(Integer.valueOf(0), valueMap.get("c1"));
                // c2 should be list index 1
                Assert.assertEquals(Integer.valueOf(1), valueMap.get("c2"));
            }
        });
        session2.delete(root);
        session2.getTransaction().commit();
        session2.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-5732")
    public void testInverseListIndex2() {
        // make sure no one changes the mapping
        final CollectionPersister collectionPersister = sessionFactory().getCollectionPersister(((Order.class.getName()) + ".lineItems"));
        Assert.assertTrue(collectionPersister.isInverse());
        // do some creations...
        Session session = openSession();
        session.beginTransaction();
        Order order = new Order("acme-1");
        order.addLineItem("abc", 2);
        order.addLineItem("def", 200);
        order.addLineItem("ghi", 13);
        session.save(order);
        session.getTransaction().commit();
        session.close();
        // now, make sure the list-index column gotten written...
        final Session session2 = openSession();
        session2.beginTransaction();
        session2.doWork(new Work() {
            @Override
            public void execute(Connection connection) throws SQLException {
                final QueryableCollection queryableCollection = ((QueryableCollection) (collectionPersister));
                SimpleSelect select = new SimpleSelect(getDialect()).setTableName(queryableCollection.getTableName()).addColumn("ORDER_ID").addColumn("INDX").addColumn("PRD_CODE");
                PreparedStatement preparedStatement = getJdbcCoordinator().getStatementPreparer().prepareStatement(select.toStatementString());
                ResultSet resultSet = getJdbcCoordinator().getResultSetReturn().extract(preparedStatement);
                Map<String, Integer> valueMap = new HashMap<String, Integer>();
                while (resultSet.next()) {
                    final int fk = resultSet.getInt(1);
                    Assert.assertFalse("Collection key (FK) column was null", resultSet.wasNull());
                    final int indx = resultSet.getInt(2);
                    Assert.assertFalse("List index column was null", resultSet.wasNull());
                    final String prodCode = resultSet.getString(3);
                    Assert.assertFalse("Prod code column was null", resultSet.wasNull());
                    valueMap.put(prodCode, indx);
                } 
                Assert.assertEquals(3, valueMap.size());
                Assert.assertEquals(Integer.valueOf(0), valueMap.get("abc"));
                Assert.assertEquals(Integer.valueOf(1), valueMap.get("def"));
                Assert.assertEquals(Integer.valueOf(2), valueMap.get("ghi"));
            }
        });
        session2.delete(order);
        session2.getTransaction().commit();
        session2.close();
    }

    @Test
    public void testWriteMethodDirtying() {
        ListOwner parent = new ListOwner("root");
        ListOwner child = new ListOwner("c1");
        parent.getChildren().add(child);
        child.setParent(parent);
        ListOwner otherChild = new ListOwner("c2");
        Session session = openSession();
        session.beginTransaction();
        session.save(parent);
        session.flush();
        // at this point, the list on parent has now been replaced with a PersistentList...
        PersistentList children = ((PersistentList) (parent.getChildren()));
        Assert.assertFalse(children.remove(otherChild));
        Assert.assertFalse(children.isDirty());
        ArrayList otherCollection = new ArrayList();
        otherCollection.add(child);
        Assert.assertFalse(children.retainAll(otherCollection));
        Assert.assertFalse(children.isDirty());
        otherCollection = new ArrayList();
        otherCollection.add(otherChild);
        Assert.assertFalse(children.removeAll(otherCollection));
        Assert.assertFalse(children.isDirty());
        children.clear();
        session.delete(child);
        Assert.assertTrue(children.isDirty());
        session.flush();
        children.clear();
        Assert.assertFalse(children.isDirty());
        session.delete(parent);
        session.getTransaction().commit();
        session.close();
    }
}

