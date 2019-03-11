/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.override;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class InheritedAttributeOverridingTest extends BaseUnitTestCase {
    private StandardServiceRegistry standardServiceRegistry;

    @Test
    @TestForIssue(jiraKey = "HHH-9485")
    public void testInheritedAttributeOverridingMappedsuperclass() {
        Metadata metadata = addAnnotatedClass(InheritedAttributeOverridingTest.B.class).buildMetadata();
        validate();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-9485")
    public void testInheritedAttributeOverridingEntity() {
        Metadata metadata = addAnnotatedClass(InheritedAttributeOverridingTest.D.class).buildMetadata();
        validate();
    }

    @MappedSuperclass
    public static class A {
        private Integer id;

        private String name;

        @Id
        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @Entity(name = "B")
    public static class B extends InheritedAttributeOverridingTest.A {
        @Override
        public String getName() {
            return super.getName();
        }

        @Override
        public void setName(String name) {
            super.setName(name);
        }
    }

    @Entity(name = "C")
    public static class C {
        private Integer id;

        private String name;

        @Id
        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @Entity(name = "D")
    public static class D extends InheritedAttributeOverridingTest.C {
        @Override
        public String getName() {
            return super.getName();
        }

        @Override
        public void setName(String name) {
            super.setName(name);
        }
    }
}

