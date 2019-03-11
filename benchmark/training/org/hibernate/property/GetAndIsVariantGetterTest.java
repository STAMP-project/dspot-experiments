/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.property;


import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import org.hamcrest.CoreMatchers;
import org.hibernate.MappingException;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.internal.util.ReflectHelper;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;


/**
 * Originally written to verify fix for HHH-10172
 *
 * @author Steve Ebersole
 */
public class GetAndIsVariantGetterTest {
    private static StandardServiceRegistry ssr;

    @Test
    @TestForIssue(jiraKey = "HHH-10172")
    public void testHbmXml() {
        try {
            addResource("org/hibernate/property/TheEntity.hbm.xml").buildMetadata();
            Assert.fail("Expecting a failure");
        } catch (MappingException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.startsWith("In trying to locate getter for property [id]"));
        }
    }

    @Test
    @TestForIssue(jiraKey = "HHH-10172")
    public void testAnnotations() {
        try {
            addAnnotatedClass(GetAndIsVariantGetterTest.TheEntity.class).buildMetadata();
            Assert.fail("Expecting a failure");
        } catch (MappingException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.startsWith("HHH000474: Ambiguous persistent property methods detected on"));
        }
    }

    @Test
    @TestForIssue(jiraKey = "HHH-10242")
    public void testAnnotationsCorrected() {
        Metadata metadata = addAnnotatedClass(GetAndIsVariantGetterTest.TheEntity2.class).buildMetadata();
        Assert.assertNotNull(metadata.getEntityBinding(GetAndIsVariantGetterTest.TheEntity2.class.getName()).getIdentifier());
        Assert.assertNotNull(metadata.getEntityBinding(GetAndIsVariantGetterTest.TheEntity2.class.getName()).getIdentifierProperty());
    }

    @Test
    @TestForIssue(jiraKey = "HHH-10309")
    public void testAnnotationsFieldAccess() {
        // this one should be ok because the AccessType is FIELD
        Metadata metadata = addAnnotatedClass(GetAndIsVariantGetterTest.AnotherEntity.class).buildMetadata();
        Assert.assertNotNull(metadata.getEntityBinding(GetAndIsVariantGetterTest.AnotherEntity.class.getName()).getIdentifier());
        Assert.assertNotNull(metadata.getEntityBinding(GetAndIsVariantGetterTest.AnotherEntity.class.getName()).getIdentifierProperty());
    }

    @Test
    @TestForIssue(jiraKey = "HHH-12046")
    public void testInstanceStaticConflict() {
        Metadata metadata = addAnnotatedClass(GetAndIsVariantGetterTest.InstanceStaticEntity.class).buildMetadata();
        Assert.assertNotNull(metadata.getEntityBinding(GetAndIsVariantGetterTest.InstanceStaticEntity.class.getName()).getIdentifier());
        Assert.assertNotNull(metadata.getEntityBinding(GetAndIsVariantGetterTest.InstanceStaticEntity.class.getName()).getIdentifierProperty());
        Assert.assertTrue(metadata.getEntityBinding(GetAndIsVariantGetterTest.InstanceStaticEntity.class.getName()).hasProperty("foo"));
        ReflectHelper.findGetterMethod(GetAndIsVariantGetterTest.InstanceStaticEntity.class, "foo");
    }

    @Entity
    public static class TheEntity {
        private Integer id;

        public boolean isId() {
            return false;
        }

        @Id
        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }
    }

    @Entity
    public static class TheEntity2 {
        private Integer id;

        @Transient
        public boolean isId() {
            return false;
        }

        @Id
        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }
    }

    @Entity
    @Access(AccessType.PROPERTY)
    public static class AnotherEntity {
        @Id
        @Access(AccessType.FIELD)
        private Integer id;

        public boolean isId() {
            return false;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }
    }

    @Entity
    public static class InstanceStaticEntity {
        private Integer id;

        private boolean foo;

        @Id
        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public boolean isFoo() {
            return this.foo;
        }

        public void setFoo(boolean foo) {
            this.foo = foo;
        }

        public static Object getFoo() {
            return null;
        }

        public static boolean isId() {
            return false;
        }
    }
}

