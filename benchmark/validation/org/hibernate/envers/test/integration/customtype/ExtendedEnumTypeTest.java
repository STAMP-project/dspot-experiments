/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.envers.test.integration.customtype;


import EnumType.ORDINAL;
import EnumType.STRING;
import java.util.Arrays;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.envers.Audited;
import org.hibernate.envers.test.BaseEnversJPAFunctionalTestCase;
import org.hibernate.envers.test.Priority;
import org.hibernate.testing.TestForIssue;
import org.hibernate.type.EnumType;
import org.junit.Assert;
import org.junit.Test;

import static javax.persistence.EnumType.<init>;
import static javax.persistence.EnumType.STRING;


/**
 * Tests that a custom type which extends {@link org.hibernate.type.EnumType} continues to be
 * recognized as an EnumType rather than a basic custom type implementation since the values
 * which envers sends to describe the type in HBM differ whether its an Enum or not.
 *
 * Without the fix, this test would not even bootstrap and would throw a MappingException.
 *
 * @author Chris Cranford
 */
@TestForIssue(jiraKey = "HHH-12304")
public class ExtendedEnumTypeTest extends BaseEnversJPAFunctionalTestCase {
    // An extended type to trigger the need for Envers to supply type information in the HBM mappings.
    // This should be treated the same as any other property annotated as Enumerated or uses an Enum.
    public static class ExtendedEnumType extends EnumType {}

    @Entity(name = "Widget")
    @TypeDef(name = "extended_enum", typeClass = ExtendedEnumTypeTest.ExtendedEnumType.class)
    @Audited
    public static class Widget {
        @Id
        @GeneratedValue
        private Integer id;

        @Enumerated(STRING)
        @Type(type = "extended_enum")
        private ExtendedEnumTypeTest.Widget.Status status;

        @Enumerated
        @Type(type = "extended_enum")
        private ExtendedEnumTypeTest.Widget.Status status2;

        public enum Status {

            ARCHIVED,
            DRAFT;}

        Widget() {
        }

        Widget(ExtendedEnumTypeTest.Widget.Status status) {
            this.status = status;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public ExtendedEnumTypeTest.Widget.Status getStatus() {
            return status;
        }

        public void setStatus(ExtendedEnumTypeTest.Widget.Status status) {
            this.status = status;
        }

        public ExtendedEnumTypeTest.Widget.Status getStatus2() {
            return status2;
        }

        public void setStatus2(ExtendedEnumTypeTest.Widget.Status status2) {
            this.status2 = status2;
        }
    }

    private Integer widgetId;

    @Test
    @Priority(10)
    public void initData() {
        // Revision 1 - insert
        this.widgetId = doInJPA(this::entityManagerFactory, ( entityManager) -> {
            final org.hibernate.envers.test.integration.customtype.Widget widget = new org.hibernate.envers.test.integration.customtype.Widget(Widget.Status.DRAFT);
            entityManager.persist(widget);
            return widget.getId();
        });
        // Revision 2 - update
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            final org.hibernate.envers.test.integration.customtype.Widget widget = entityManager.find(.class, this.widgetId);
            widget.setStatus(Widget.Status.ARCHIVED);
            entityManager.merge(widget);
        });
        // Revision 3 - delete
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            final org.hibernate.envers.test.integration.customtype.Widget widget = entityManager.find(.class, this.widgetId);
            entityManager.remove(widget);
        });
    }

    @Test
    public void testRevisionHistory() {
        List revisions = getAuditReader().getRevisions(ExtendedEnumTypeTest.Widget.class, this.widgetId);
        Assert.assertEquals(Arrays.asList(1, 2, 3), revisions);
        final ExtendedEnumTypeTest.Widget rev1 = getAuditReader().find(ExtendedEnumTypeTest.Widget.class, this.widgetId, 1);
        Assert.assertEquals(ExtendedEnumTypeTest.Widget.Status.DRAFT, rev1.getStatus());
        final ExtendedEnumTypeTest.Widget rev2 = getAuditReader().find(ExtendedEnumTypeTest.Widget.class, this.widgetId, 2);
        Assert.assertEquals(ExtendedEnumTypeTest.Widget.Status.ARCHIVED, rev2.getStatus());
        final ExtendedEnumTypeTest.Widget rev3 = getAuditReader().find(ExtendedEnumTypeTest.Widget.class, this.widgetId, 3);
        Assert.assertNull(rev3);
    }

    @Test
    public void testEnumPropertyStorageType() {
        // test that property 'status' translates to an enum type that is stored by name (e.g. STRING)
        assertEnumProperty(ExtendedEnumTypeTest.Widget.class, ExtendedEnumTypeTest.ExtendedEnumType.class, "status", STRING);
        // test that property 'status2' translates to an enum type that is stored by position (e.g. ORDINAL)
        assertEnumProperty(ExtendedEnumTypeTest.Widget.class, ExtendedEnumTypeTest.ExtendedEnumType.class, "status2", ORDINAL);
    }
}

