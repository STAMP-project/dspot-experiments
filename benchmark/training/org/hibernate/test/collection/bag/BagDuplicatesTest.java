/**
 * Copyright 2014 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hibernate.test.collection.bag;


import java.util.ArrayList;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;

import static CascadeType.ALL;
import static GenerationType.AUTO;


/**
 * This template demonstrates how to develop a test case for Hibernate ORM, using its built-in unit test framework.
 * Although ORMStandaloneTestCase is perfectly acceptable as a reproducer, usage of this class is much preferred.
 * Since we nearly always include a regression test with bug fixes, providing your reproducer using this method
 * simplifies the process.
 *
 * What's even better?  Fork hibernate-orm itself, add your test case directly to a module's unit tests, then
 * submit it as a PR!
 */
public class BagDuplicatesTest extends BaseCoreFunctionalTestCase {
    // Add your tests, using standard JUnit.
    @Test
    public void HHH10385Test() throws Exception {
        // BaseCoreFunctionalTestCase automatically creates the SessionFactory and provides the Session.
        Session session = null;
        Transaction transaction = null;
        Long parentId = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();
            BagDuplicatesTest.Parent parent = new BagDuplicatesTest.Parent();
            session.persist(parent);
            session.flush();
            parentId = parent.getId();
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            Assert.fail(e.getMessage());
        } finally {
            if (session != null) {
                session.close();
            }
        }
        try {
            session = openSession();
            transaction = session.beginTransaction();
            BagDuplicatesTest.Parent parent = session.get(BagDuplicatesTest.Parent.class, parentId);
            BagDuplicatesTest.Child child1 = new BagDuplicatesTest.Child();
            child1.setName("child1");
            child1.setParent(parent);
            parent.addChild(child1);
            parent = ((BagDuplicatesTest.Parent) (session.merge(parent)));
            session.flush();
            // assertEquals(1, parent.getChildren().size());
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            Assert.fail(e.getMessage());
        } finally {
            if (session != null) {
                session.close();
            }
        }
        try {
            session = openSession();
            transaction = session.beginTransaction();
            BagDuplicatesTest.Parent parent = session.get(BagDuplicatesTest.Parent.class, parentId);
            Assert.assertEquals(1, parent.getChildren().size());
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            Assert.fail(e.getMessage());
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Entity(name = "Parent")
    public static class Parent {
        @Id
        @GeneratedValue(strategy = AUTO)
        private Long id;

        @OneToMany(cascade = ALL, mappedBy = "parent", orphanRemoval = true)
        private List<BagDuplicatesTest.Child> children = new ArrayList<BagDuplicatesTest.Child>();

        public Parent() {
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public List<BagDuplicatesTest.Child> getChildren() {
            return children;
        }

        public void addChild(BagDuplicatesTest.Child child) {
            children.add(child);
            child.setParent(this);
        }

        public void removeChild(BagDuplicatesTest.Child child) {
            children.remove(child);
            child.setParent(null);
        }
    }

    @Entity(name = "Child")
    public static class Child {
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private Long id;

        private String name;

        @ManyToOne
        private BagDuplicatesTest.Parent parent;

        public Child() {
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public BagDuplicatesTest.Parent getParent() {
            return parent;
        }

        public void setParent(BagDuplicatesTest.Parent parent) {
            this.parent = parent;
        }

        @Override
        public String toString() {
            return ((((("Child{" + "id=") + (id)) + ", name='") + (name)) + '\'') + '}';
        }
    }
}

