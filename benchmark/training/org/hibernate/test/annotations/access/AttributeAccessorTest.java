/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.access;


import javax.persistence.Entity;
import javax.persistence.Id;
import org.hibernate.annotations.AttributeAccessor;
import org.hibernate.boot.Metadata;
import org.hibernate.mapping.Property;
import org.hibernate.property.access.internal.PropertyAccessStrategyBasicImpl;
import org.hibernate.property.access.spi.PropertyAccess;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Chris Cranford
 */
@TestForIssue(jiraKey = "HHH-12062")
public class AttributeAccessorTest {
    private ServiceRegistry serviceRegistry;

    @Test
    public void testAttributeAccessorConfiguration() {
        final Metadata metadata = addAnnotatedClass(AttributeAccessorTest.Foo.class).buildMetadata();
        final Property property = metadata.getEntityBinding(AttributeAccessorTest.Foo.class.getName()).getProperty("name");
        Assert.assertEquals(AttributeAccessorTest.BasicAttributeAccessor.class.getName(), property.getPropertyAccessorName());
    }

    @Entity(name = "Foo")
    public static class Foo {
        private Integer id;

        private String name;

        public Foo() {
        }

        public Foo(Integer id) {
            this.id = id;
        }

        public Foo(Integer id, String name) {
            this.id = id;
            this.name = name;
        }

        @Id
        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        @AttributeAccessor("org.hibernate.test.annotations.access.AttributeAccessorTest$BasicAttributeAccessor")
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class BasicAttributeAccessor extends PropertyAccessStrategyBasicImpl {
        @Override
        public PropertyAccess buildPropertyAccess(Class containerJavaType, String propertyName) {
            return super.buildPropertyAccess(containerJavaType, propertyName);
        }
    }
}

