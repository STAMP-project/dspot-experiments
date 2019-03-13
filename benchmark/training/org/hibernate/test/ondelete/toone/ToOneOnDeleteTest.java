package org.hibernate.test.ondelete.toone;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import org.hibernate.Session;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
public class ToOneOnDeleteTest extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    public void testManyToOne() throws Exception {
        Session session = openSession();
        session.getTransaction().begin();
        ToOneOnDeleteTest.Parent parent = new ToOneOnDeleteTest.Parent();
        parent.id = 1L;
        session.persist(parent);
        ToOneOnDeleteTest.Child child1 = new ToOneOnDeleteTest.Child();
        child1.id = 1L;
        child1.parent = parent;
        session.persist(child1);
        ToOneOnDeleteTest.GrandChild grandChild11 = new ToOneOnDeleteTest.GrandChild();
        grandChild11.id = 1L;
        grandChild11.parent = child1;
        session.persist(grandChild11);
        ToOneOnDeleteTest.Child child2 = new ToOneOnDeleteTest.Child();
        child2.id = 2L;
        child2.parent = parent;
        session.persist(child2);
        ToOneOnDeleteTest.GrandChild grandChild21 = new ToOneOnDeleteTest.GrandChild();
        grandChild21.id = 2L;
        grandChild21.parent = child2;
        session.persist(grandChild21);
        ToOneOnDeleteTest.GrandChild grandChild22 = new ToOneOnDeleteTest.GrandChild();
        grandChild22.id = 3L;
        grandChild22.parent = child2;
        session.persist(grandChild22);
        session.getTransaction().commit();
        session.close();
        session = openSession();
        session.getTransaction().begin();
        parent = session.get(ToOneOnDeleteTest.Parent.class, 1L);
        session.delete(parent);
        session.getTransaction().commit();
        session.close();
    }

    @Entity(name = "Parent")
    public static class Parent {
        @Id
        private Long id;
    }

    @Entity(name = "Child")
    public static class Child {
        @Id
        private Long id;

        @ManyToOne
        @OnDelete(action = OnDeleteAction.CASCADE)
        private ToOneOnDeleteTest.Parent parent;
    }

    @Entity(name = "GrandChild")
    public static class GrandChild {
        @Id
        private Long id;

        @OneToOne
        @OnDelete(action = OnDeleteAction.CASCADE)
        private ToOneOnDeleteTest.Child parent;
    }
}

