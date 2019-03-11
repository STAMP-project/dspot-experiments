/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.enumerated;


import EnumType.ORDINAL;
import EnumType.STRING;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class EnumeratedSmokeTest extends BaseUnitTestCase {
    private StandardServiceRegistry ssr;

    /**
     * I personally have been unable to repeoduce the bug as reported in HHH-10402.  This test
     * is equivalent to what the reporters say happens, but these tests pass fine.
     */
    @Test
    @TestForIssue(jiraKey = "HHH-10402")
    public void testEnumeratedTypeResolutions() {
        final MetadataImplementor mappings = ((MetadataImplementor) (addAnnotatedClass(EnumeratedSmokeTest.EntityWithEnumeratedAttributes.class).buildMetadata()));
        mappings.validate();
        final PersistentClass entityBinding = mappings.getEntityBinding(EnumeratedSmokeTest.EntityWithEnumeratedAttributes.class.getName());
        validateEnumMapping(entityBinding.getProperty("notAnnotated"), ORDINAL);
        validateEnumMapping(entityBinding.getProperty("noEnumType"), ORDINAL);
        validateEnumMapping(entityBinding.getProperty("ordinalEnumType"), ORDINAL);
        validateEnumMapping(entityBinding.getProperty("stringEnumType"), STRING);
    }

    @Entity
    public static class EntityWithEnumeratedAttributes {
        @Id
        public Integer id;

        public EnumeratedSmokeTest.Gender notAnnotated;

        @Enumerated
        public EnumeratedSmokeTest.Gender noEnumType;

        @Enumerated(EnumType.ORDINAL)
        public EnumeratedSmokeTest.Gender ordinalEnumType;

        @Enumerated(EnumType.STRING)
        public EnumeratedSmokeTest.Gender stringEnumType;
    }

    public static enum Gender {

        MALE,
        FEMALE,
        UNKNOWN;}
}

