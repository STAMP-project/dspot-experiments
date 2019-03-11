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


import java.util.Set;
import org.hibernate.Session;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;

import static FetchType.LAZY;
import static InheritanceType.JOINED;


/**
 * Test cases for joined inheritance with a discriminator column.
 *
 * @author Etienne Miret
 */
public class JoinedInheritanceTest extends BaseCoreFunctionalTestCase {
    @Test
    public void simpleSelectTest() {
        final Session s = openSession();
        s.getTransaction().begin();
        s.createQuery("from Polygon").list();
        s.getTransaction().commit();
        s.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-9357")
    public void selectWhereTypeEqual() {
        final Session s = openSession();
        s.getTransaction().begin();
        s.createQuery("from Polygon p where type(p) = Quadrilateral").list();
        s.getTransaction().commit();
        s.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-12332")
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

    // Test entities for metamodel building for HHH-12332
    @Entity(name = "BaseEntity")
    @Inheritance(strategy = JOINED)
    public static class BaseEntity {
        @Id
        private long id;
    }

    @Entity(name = "EntityA")
    public static class EntityA extends JoinedInheritanceTest.BaseEntity {
        @OneToMany(fetch = LAZY)
        private Set<JoinedInheritanceTest.EntityC> attributes;

        @ManyToOne(fetch = LAZY)
        private JoinedInheritanceTest.EntityC relation;
    }

    @Entity(name = "EntityB")
    public static class EntityB extends JoinedInheritanceTest.BaseEntity {
        @OneToMany(fetch = FetchType.LAZY)
        private Set<JoinedInheritanceTest.EntityD> attributes;

        @ManyToOne(fetch = FetchType.LAZY)
        private JoinedInheritanceTest.EntityD relation;
    }

    @Entity(name = "EntityC")
    public static class EntityC {
        @Id
        private long id;
    }

    @Entity(name = "EntityD")
    public static class EntityD {
        @Id
        private long id;
    }
}

