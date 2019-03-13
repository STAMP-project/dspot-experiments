/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.test.naturalid.inheritance;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;
import org.hibernate.annotations.NaturalId;
import org.hibernate.testing.FailureExpected;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
@FailureExpected(jiraKey = "HHH-12085")
public class MappedSuperclassOverrideTest extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    public void testModel() {
        Assert.assertTrue(sessionFactory().getMetamodel().entityPersister(MappedSuperclassOverrideTest.MyEntity.class).hasNaturalIdentifier());
    }

    @MappedSuperclass
    public abstract static class MyMappedSuperclass {
        private Integer id;

        private String name;

        public MyMappedSuperclass() {
        }

        public MyMappedSuperclass(Integer id, String name) {
            this.id = id;
            this.name = name;
        }

        @Id
        public Integer getId() {
            return id;
        }

        protected void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @Entity(name = "MyEntity")
    @Table(name = "the_entity")
    public static class MyEntity extends MappedSuperclassOverrideTest.MyMappedSuperclass {
        public MyEntity() {
            super();
        }

        public MyEntity(Integer id, String name) {
            super(id, name);
        }

        // this should not be allowed, and supposedly fails anyway...
        @Override
        @NaturalId
        public String getName() {
            return super.getName();
        }
    }
}

