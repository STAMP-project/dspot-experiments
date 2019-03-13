/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2014, Red Hat Inc. or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Inc.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.hibernate.test.inheritance.discriminator;


import java.util.List;
import java.util.Set;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;

import static FetchType.EAGER;
import static FetchType.LAZY;
import static InheritanceType.JOINED;


/**
 * Test cases for joined inheritance with eager fetching.
 *
 * @author Christian Beikov
 */
public class JoinedInheritanceEagerTest extends BaseCoreFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-12375")
    public void joinFindEntity() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.inheritance.discriminator.EntityA entityA = session.get(.class, 4L);
            Assert.assertTrue(Hibernate.isInitialized(entityA.getRelation()));
            Assert.assertFalse(Hibernate.isInitialized(entityA.getAttributes()));
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-12375")
    public void joinFindParenEntity() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.inheritance.discriminator.BaseEntity baseEntity = session.get(.class, 4L);
            Assert.assertThat(baseEntity, notNullValue());
            Assert.assertThat(baseEntity, instanceOf(.class));
            Assert.assertTrue(Hibernate.isInitialized(((org.hibernate.test.inheritance.discriminator.EntityA) (baseEntity)).getRelation()));
            Assert.assertFalse(Hibernate.isInitialized(((org.hibernate.test.inheritance.discriminator.EntityA) (baseEntity)).getAttributes()));
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.inheritance.discriminator.BaseEntity baseEntity = session.get(.class, 3L);
            Assert.assertThat(baseEntity, notNullValue());
            Assert.assertThat(baseEntity, instanceOf(.class));
            Assert.assertTrue(Hibernate.isInitialized(((org.hibernate.test.inheritance.discriminator.EntityB) (baseEntity)).getRelation()));
            Assert.assertFalse(Hibernate.isInitialized(((org.hibernate.test.inheritance.discriminator.EntityB) (baseEntity)).getAttributes()));
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-12375")
    public void joinUnrelatedCollectionOnBaseType() {
        final Session s = openSession();
        s.getTransaction().begin();
        try {
            s.createQuery("from BaseEntity b join b.attributes").list();
            Assert.fail("Expected a resolution exception for property 'attributes'!");
        } catch (IllegalArgumentException ex) {
            Assert.assertTrue(ex.getMessage().contains("could not resolve property: attributes "));
        } finally {
            s.getTransaction().commit();
            s.close();
        }
    }

    @Test
    @TestForIssue(jiraKey = "HHH-12375")
    public void selectBaseType() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            List result = session.createQuery("from BaseEntity").list();
            Assert.assertEquals(result.size(), 2);
        });
    }

    @Entity(name = "BaseEntity")
    @Inheritance(strategy = JOINED)
    public static class BaseEntity {
        @Id
        private Long id;

        public BaseEntity() {
        }

        public BaseEntity(Long id) {
            this.id = id;
        }
    }

    @Entity(name = "EntityA")
    public static class EntityA extends JoinedInheritanceEagerTest.BaseEntity {
        @OneToMany(fetch = LAZY)
        private Set<JoinedInheritanceEagerTest.EntityC> attributes;

        @ManyToOne(fetch = EAGER)
        private JoinedInheritanceEagerTest.EntityC relation;

        public EntityA() {
        }

        public EntityA(Long id) {
            super(id);
        }

        public void setRelation(JoinedInheritanceEagerTest.EntityC relation) {
            this.relation = relation;
        }

        public JoinedInheritanceEagerTest.EntityC getRelation() {
            return relation;
        }

        public Set<JoinedInheritanceEagerTest.EntityC> getAttributes() {
            return attributes;
        }
    }

    @Entity(name = "EntityB")
    public static class EntityB extends JoinedInheritanceEagerTest.BaseEntity {
        @OneToMany(fetch = FetchType.LAZY)
        private Set<JoinedInheritanceEagerTest.EntityD> attributes;

        @ManyToOne(fetch = FetchType.EAGER)
        private JoinedInheritanceEagerTest.EntityD relation;

        public EntityB() {
        }

        public EntityB(Long id) {
            super(id);
        }

        public void setRelation(JoinedInheritanceEagerTest.EntityD relation) {
            this.relation = relation;
        }

        public JoinedInheritanceEagerTest.EntityD getRelation() {
            return relation;
        }

        public Set<JoinedInheritanceEagerTest.EntityD> getAttributes() {
            return attributes;
        }
    }

    @Entity(name = "EntityC")
    public static class EntityC {
        @Id
        private Long id;

        public EntityC() {
        }

        public EntityC(Long id) {
            this.id = id;
        }
    }

    @Entity(name = "EntityD")
    public static class EntityD {
        @Id
        private Long id;

        public EntityD() {
        }

        public EntityD(Long id) {
            this.id = id;
        }
    }
}

