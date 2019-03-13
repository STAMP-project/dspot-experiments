/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.cfg;


import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Chris Cranford
 */
@TestForIssue(jiraKey = "HHH-12199")
public class PropertyAccessTypeDetectionType extends BaseCoreFunctionalTestCase {
    public static class FooEntity {
        public static final String intValue = "intValue";

        private Long id;

        private Integer _intValue;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Integer getIntValue() {
            return _intValue;
        }

        public void setIntValue(Integer intValue) {
            this._intValue = intValue;
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testPropertyAccessIgnoresStaticFields() {
        // verify that the entity persister is configured with property intValue as an Integer rather than
        // using the static field reference and determining the type to be String.
        Assert.assertTrue(sessionFactory().getMetamodel().entityPersister(PropertyAccessTypeDetectionType.FooEntity.class).getPropertyType("intValue").getReturnedClass().isAssignableFrom(Integer.class));
    }
}

