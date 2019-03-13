/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.bytecode.enhancement.basic;


import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.bytecode.enhancement.BytecodeEnhancerRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(BytecodeEnhancerRunner.class)
public class GenericReturnValueMappedSuperclassEnhancementTest {
    @Test
    @TestForIssue(jiraKey = "HHH-12579")
    public void enhanceClassWithGenericReturnValueOnMappedSuperclass() {
        GenericReturnValueMappedSuperclassEnhancementTest.SimpleEntity implementation = new GenericReturnValueMappedSuperclassEnhancementTest.SimpleEntity();
        implementation.setEntity(GenericReturnValueMappedSuperclassEnhancementTest.SimpleEntity.Type.ONE);
        Assert.assertEquals(GenericReturnValueMappedSuperclassEnhancementTest.SimpleEntity.Type.ONE, implementation.getEntity());
    }

    @MappedSuperclass
    @Cache(usage = CacheConcurrencyStrategy.NONE)
    public static class AbstractMappedSuperclassWithGenericReturnValue<T extends GenericReturnValueMappedSuperclassEnhancementTest.Marker> {
        @Id
        @GeneratedValue
        public int id;

        @Access(AccessType.PROPERTY)
        private T entity;

        public T getEntity() {
            return entity;
        }

        public void setEntity(T entity) {
            this.entity = entity;
        }
    }

    public interface Marker {}

    @Entity
    @Cache(usage = CacheConcurrencyStrategy.NONE)
    public static class SimpleEntity extends GenericReturnValueMappedSuperclassEnhancementTest.AbstractMappedSuperclassWithGenericReturnValue<GenericReturnValueMappedSuperclassEnhancementTest.SimpleEntity.Type> {
        public enum Type implements GenericReturnValueMappedSuperclassEnhancementTest.Marker {

            ONE;}
    }
}

