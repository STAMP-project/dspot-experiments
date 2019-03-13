/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.inheritance.singletable;


import SessionFactoryRegistry.INSTANCE;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import org.hibernate.MappingException;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Lukasz Antoniak (lukasz dot antoniak at gmail dot com)
 */
@TestForIssue(jiraKey = "HHH-7214")
public class DuplicatedDiscriminatorValueTest extends BaseUnitTestCase {
    private static final String DISCRIMINATOR_VALUE = "D";

    @Test
    public void testDuplicatedDiscriminatorValueSameHierarchy() {
        try {
            tryBuildingSessionFactory(Building.class, DuplicatedDiscriminatorValueTest.Building1.class, DuplicatedDiscriminatorValueTest.Building2.class);
            Assert.fail(((MappingException.class.getName()) + " expected when two subclasses are mapped with the same discriminator value."));
        } catch (MappingException e) {
            final String errorMsg = e.getCause().getMessage();
            // Check if error message contains descriptive information.
            Assert.assertTrue(errorMsg.contains(DuplicatedDiscriminatorValueTest.Building1.class.getName()));
            Assert.assertTrue(errorMsg.contains(DuplicatedDiscriminatorValueTest.Building2.class.getName()));
            Assert.assertTrue(errorMsg.contains((("discriminator value '" + (DuplicatedDiscriminatorValueTest.DISCRIMINATOR_VALUE)) + "'.")));
        }
        Assert.assertFalse(INSTANCE.hasRegistrations());
    }

    @Test
    public void testDuplicatedDiscriminatorValueDifferentHierarchy() {
        tryBuildingSessionFactory(Building.class, DuplicatedDiscriminatorValueTest.Building1.class, DuplicatedDiscriminatorValueTest.Furniture.class, DuplicatedDiscriminatorValueTest.Chair.class);
    }

    // Duplicated discriminator value in single hierarchy.
    @Entity
    @DiscriminatorValue(DuplicatedDiscriminatorValueTest.DISCRIMINATOR_VALUE)
    public static class Building1 extends Building {}

    // Duplicated discriminator value in single hierarchy.
    @Entity
    @DiscriminatorValue(DuplicatedDiscriminatorValueTest.DISCRIMINATOR_VALUE)
    public static class Building2 extends Building {}

    @Entity
    @DiscriminatorColumn(name = "entity_type")
    @DiscriminatorValue("F")
    public static class Furniture {
        @Id
        @GeneratedValue
        private Integer id;
    }

    // Duplicated discriminator value in different hierarchy.
    @Entity
    @DiscriminatorValue(DuplicatedDiscriminatorValueTest.DISCRIMINATOR_VALUE)
    public static class Chair extends DuplicatedDiscriminatorValueTest.Furniture {}
}

