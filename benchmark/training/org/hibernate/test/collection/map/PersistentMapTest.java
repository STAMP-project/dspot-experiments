/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.collection.map;


import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.collection.internal.PersistentMap;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test various situations using a {@link PersistentMap}.
 *
 * @author Steve Ebersole
 * @author Brett Meyer
 * @author Gail Badner
 */
public class PersistentMapTest extends BaseCoreFunctionalTestCase {
    @Test
    @SuppressWarnings({ "unchecked" })
    public void testWriteMethodDirtying() {
        Parent parent = new Parent("p1");
        Child child = new Child("c1");
        parent.getChildren().put(child.getName(), child);
        child.setParent(parent);
        Child otherChild = new Child("c2");
        Session session = openSession();
        session.beginTransaction();
        session.save(parent);
        session.flush();
        // at this point, the map on parent has now been replaced with a PersistentMap...
        PersistentMap children = ((PersistentMap) (parent.getChildren()));
        Object old = children.put(child.getName(), child);
        Assert.assertTrue((old == child));
        Assert.assertFalse(children.isDirty());
        old = children.remove(otherChild.getName());
        Assert.assertNull(old);
        Assert.assertFalse(children.isDirty());
        HashMap otherMap = new HashMap();
        otherMap.put(child.getName(), child);
        children.putAll(otherMap);
        Assert.assertFalse(children.isDirty());
        otherMap = new HashMap();
        otherMap.put(otherChild.getName(), otherChild);
        children.putAll(otherMap);
        Assert.assertTrue(children.isDirty());
        children.clearDirty();
        session.delete(child);
        children.clear();
        Assert.assertTrue(children.isDirty());
        session.flush();
        children.clear();
        Assert.assertFalse(children.isDirty());
        session.delete(parent);
        session.getTransaction().commit();
        session.close();
    }

    @Test
    public void testPutAgainstUninitializedMap() {
        // prepare map owner...
        Session session = openSession();
        session.beginTransaction();
        Parent parent = new Parent("p1");
        session.save(parent);
        session.getTransaction().commit();
        session.close();
        // Now, reload the parent and test adding children
        session = openSession();
        session.beginTransaction();
        parent = ((Parent) (session.get(Parent.class, parent.getName())));
        parent.addChild("c1");
        parent.addChild("c2");
        session.getTransaction().commit();
        session.close();
        Assert.assertEquals(2, parent.getChildren().size());
        session = openSession();
        session.beginTransaction();
        session.delete(parent);
        session.getTransaction().commit();
        session.close();
    }

    @Test
    public void testRemoveAgainstUninitializedMap() {
        Parent parent = new Parent("p1");
        Child child = new Child("c1");
        parent.addChild(child);
        Session session = openSession();
        session.beginTransaction();
        session.save(parent);
        session.getTransaction().commit();
        session.close();
        // Now reload the parent and test removing the child
        session = openSession();
        session.beginTransaction();
        parent = ((Parent) (session.get(Parent.class, parent.getName())));
        Child child2 = ((Child) (parent.getChildren().remove(child.getName())));
        child2.setParent(null);
        Assert.assertNotNull(child2);
        Assert.assertTrue(parent.getChildren().isEmpty());
        session.getTransaction().commit();
        session.close();
        // Load the parent once again and make sure child is still gone
        // then cleanup
        session = openSession();
        session.beginTransaction();
        parent = ((Parent) (session.get(Parent.class, parent.getName())));
        Assert.assertTrue(parent.getChildren().isEmpty());
        session.delete(child2);
        session.delete(parent);
        session.getTransaction().commit();
        session.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-5732")
    public void testClearMap() {
        Session s = openSession();
        s.beginTransaction();
        PersistentMapTest.User user = new PersistentMapTest.User();
        PersistentMapTest.UserData userData = new PersistentMapTest.UserData();
        userData.user = user;
        user.userDatas.put("foo", userData);
        s.persist(user);
        s.getTransaction().commit();
        s.clear();
        s.beginTransaction();
        user = s.get(PersistentMapTest.User.class, 1);
        user.userDatas.clear();
        s.update(user);
        Query q = s.createQuery((("DELETE FROM " + (PersistentMapTest.UserData.class.getName())) + " d WHERE d.user = :user"));
        q.setParameter("user", user);
        q.executeUpdate();
        s.getTransaction().commit();
        s.getTransaction().begin();
        Assert.assertEquals(s.get(PersistentMapTest.User.class, user.id).userDatas.size(), 0);
        Assert.assertEquals(s.createQuery(("FROM " + (PersistentMapTest.UserData.class.getName()))).list().size(), 0);
        s.createQuery(("delete " + (PersistentMapTest.User.class.getName()))).executeUpdate();
        s.getTransaction().commit();
        s.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-5393")
    public void testMapKeyColumnInEmbeddableElement() {
        Session s = openSession();
        s.getTransaction().begin();
        MultilingualString m = new MultilingualString();
        LocalizedString localizedString = new LocalizedString();
        localizedString.setLanguage("English");
        localizedString.setText("name");
        m.getMap().put(localizedString.getLanguage(), localizedString);
        localizedString = new LocalizedString();
        localizedString.setLanguage("English Pig Latin");
        localizedString.setText("amenay");
        m.getMap().put(localizedString.getLanguage(), localizedString);
        s.persist(m);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        m = s.get(MultilingualString.class, m.getId());
        Assert.assertEquals(2, m.getMap().size());
        localizedString = m.getMap().get("English");
        Assert.assertEquals("English", localizedString.getLanguage());
        Assert.assertEquals("name", localizedString.getText());
        localizedString = m.getMap().get("English Pig Latin");
        Assert.assertEquals("English Pig Latin", localizedString.getLanguage());
        Assert.assertEquals("amenay", localizedString.getText());
        s.delete(m);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    @TestForIssue(jiraKey = "HQLPARSER-15")
    public void testJoinFetchElementCollectionWithParentSelect() {
        Session s = openSession();
        s.getTransaction().begin();
        MultilingualString m = new MultilingualString();
        LocalizedString localizedString = new LocalizedString();
        localizedString.setLanguage("English");
        localizedString.setText("name");
        m.getMap().put(localizedString.getLanguage(), localizedString);
        localizedString = new LocalizedString();
        localizedString.setLanguage("English Pig Latin");
        localizedString.setText("amenay");
        m.getMap().put(localizedString.getLanguage(), localizedString);
        MultilingualStringParent parent = new MultilingualStringParent();
        parent.setString(m);
        s.persist(m);
        s.persist(parent);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        m = s.createQuery(("SELECT s FROM MultilingualStringParent parent " + ("JOIN parent.string s " + "JOIN FETCH s.map")), MultilingualString.class).getSingleResult();
        Assert.assertEquals(2, m.getMap().size());
        localizedString = m.getMap().get("English");
        Assert.assertEquals("English", localizedString.getLanguage());
        Assert.assertEquals("name", localizedString.getText());
        localizedString = m.getMap().get("English Pig Latin");
        Assert.assertEquals("English Pig Latin", localizedString.getLanguage());
        Assert.assertEquals("amenay", localizedString.getText());
        s.delete(parent);
        s.delete(m);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-11038")
    public void testMapKeyColumnNonInsertableNonUpdatableBidirOneToMany() {
        Session s = openSession();
        s.getTransaction().begin();
        PersistentMapTest.User user = new PersistentMapTest.User();
        PersistentMapTest.Address address = new PersistentMapTest.Address();
        address.addressType = "email";
        address.addressText = "jane@doe.com";
        user.addresses.put(address.addressType, address);
        address.user = user;
        s.persist(user);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.getTransaction().begin();
        user = s.get(PersistentMapTest.User.class, user.id);
        user.addresses.clear();
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.getTransaction().begin();
        user = s.get(PersistentMapTest.User.class, user.id);
        s.delete(user);
        s.createQuery(("delete from " + (PersistentMapTest.User.class.getName()))).executeUpdate();
        s.getTransaction().commit();
        s.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-11038")
    public void testMapKeyColumnNonInsertableNonUpdatableUnidirOneToMany() {
        Session s = openSession();
        s.getTransaction().begin();
        PersistentMapTest.User user = new PersistentMapTest.User();
        PersistentMapTest.Detail detail = new PersistentMapTest.Detail();
        detail.description = "desc";
        detail.detailType = "trivial";
        user.details.put(detail.detailType, detail);
        s.persist(user);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.getTransaction().begin();
        user = s.get(PersistentMapTest.User.class, user.id);
        user.details.clear();
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.getTransaction().begin();
        user = s.get(PersistentMapTest.User.class, user.id);
        s.delete(user);
        s.createQuery(("delete from " + (PersistentMapTest.User.class.getName()))).executeUpdate();
        s.getTransaction().commit();
        s.close();
    }

    @Entity
    @Table(name = "MyUser")
    private static class User implements Serializable {
        @Id
        @GeneratedValue
        private Integer id;

        @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL)
        @MapKeyColumn(name = "name", nullable = true)
        private Map<String, PersistentMapTest.UserData> userDatas = new HashMap<String, PersistentMapTest.UserData>();

        @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL)
        @MapKeyColumn(name = "addressType", insertable = false, updatable = false)
        private Map<String, PersistentMapTest.Address> addresses = new HashMap<String, PersistentMapTest.Address>();

        @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
        @MapKeyColumn(name = "detailType", insertable = false, updatable = false)
        @JoinColumn
        private Map<String, PersistentMapTest.Detail> details = new HashMap<String, PersistentMapTest.Detail>();
    }

    @Entity
    @Table(name = "UserData")
    private static class UserData {
        @Id
        @GeneratedValue
        private Integer id;

        @ManyToOne
        @JoinColumn(name = "userId")
        private PersistentMapTest.User user;
    }

    @Entity
    @Table(name = "Address")
    private static class Address {
        @Id
        @GeneratedValue
        private Integer id;

        @ManyToOne
        @JoinColumn(name = "userId")
        private PersistentMapTest.User user;

        @Column(nullable = false)
        private String addressType;

        @Column(nullable = false)
        private String addressText;
    }

    @Entity
    @Table(name = "Detail")
    private static class Detail {
        @Id
        @GeneratedValue
        private Integer id;

        @Column(nullable = false)
        private String detailType;

        private String description;
    }
}

