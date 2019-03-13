/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.envers.test.integration.superclass.auditoverride;


import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import org.hibernate.UnknownEntityTypeException;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.envers.AuditJoinTable;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.AuditOverrides;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.test.BaseEnversJPAFunctionalTestCase;
import org.hibernate.envers.test.Priority;
import org.hibernate.envers.test.tools.TestTools;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Chris Cranford
 */
@TestForIssue(jiraKey = "HHH-12913")
public class AuditOverrideAuditJoinTableTest extends BaseEnversJPAFunctionalTestCase {
    private Long entityId;

    private Long overrideEntityId;

    private Long auditParentsEntityId;

    @Test
    @Priority(10)
    public void initData() {
        // Revision 1 - Persist audited subclass with non-audited super-type
        entityId = doInJPA(this::entityManagerFactory, ( entityManager) -> {
            final org.hibernate.envers.test.integration.superclass.auditoverride.OtherAuditedEntity entity = new org.hibernate.envers.test.integration.superclass.auditoverride.OtherAuditedEntity();
            entity.setId(1);
            entity.setVersion(Timestamp.valueOf(LocalDateTime.now()));
            entity.setSuperValue("SuperValue");
            entity.setValue("Value");
            entity.setNotAuditedValue("NotAuditedValue");
            List<String> list = new ArrayList<>();
            list.add("Entry1");
            list.add("Entry2");
            entity.setSuperStringList(list);
            entityManager.persist(entity);
            return entity.getId();
        });
        // Revision 2 - Persist audited subclass with audit-override non-audited super-type
        overrideEntityId = doInJPA(this::entityManagerFactory, ( entityManager) -> {
            final org.hibernate.envers.test.integration.superclass.auditoverride.OtherOverrideAuditedEntity entity = new org.hibernate.envers.test.integration.superclass.auditoverride.OtherOverrideAuditedEntity();
            entity.setId(1);
            entity.setVersion(Timestamp.valueOf(LocalDateTime.now()));
            entity.setSuperValue("SuperValue");
            entity.setValue("Value");
            entity.setNotAuditedValue("NotAuditedValue");
            List<String> list = new ArrayList<>();
            list.add("Entry1");
            list.add("Entry2");
            entity.setSuperStringList(list);
            entityManager.persist(entity);
            return entity.getId();
        });
        // Revision 3 - Persist audited subclass with audit-parents on non-audited super-type
        auditParentsEntityId = doInJPA(this::entityManagerFactory, ( entityManager) -> {
            final org.hibernate.envers.test.integration.superclass.auditoverride.OtherAuditParentsAuditEntity entity = new org.hibernate.envers.test.integration.superclass.auditoverride.OtherAuditParentsAuditEntity();
            entity.setId(1);
            entity.setVersion(Timestamp.valueOf(LocalDateTime.now()));
            entity.setSuperValue("SuperValue");
            entity.setValue("Value");
            entity.setNotAuditedValue("NotAuditedValue");
            List<String> list = new ArrayList<>();
            list.add("Entry1");
            list.add("Entry2");
            entity.setSuperStringList(list);
            entityManager.persist(entity);
            return entity.getId();
        });
    }

    @Test
    public void testMetadataAuditSuperClassWithAuditJoinTable() {
        try {
            entityManagerFactory().unwrap(SessionFactoryImplementor.class).getMetamodel().locateEntityPersister("SuperClass_StringList");
        } catch (UnknownEntityTypeException e) {
            Assert.fail("Expected to find an entity-persister for the string-list in the super audit type");
        }
    }

    @Test
    public void testMetadataNonAuditedSuperClassWithOverrideAuditJoinTable() {
        try {
            entityManagerFactory().unwrap(SessionFactoryImplementor.class).getMetamodel().locateEntityPersister("OOAE_StringList");
        } catch (UnknownEntityTypeException e) {
            Assert.fail("Expected to find an entity-persister for the string-list in the super audit type");
        }
    }

    @Test
    public void testMetadataNonAuditedSuperClassWithAuditParentsOverrideAuditJoinTable() {
        try {
            entityManagerFactory().unwrap(SessionFactoryImplementor.class).getMetamodel().locateEntityPersister("OAPAE_StringList");
        } catch (UnknownEntityTypeException e) {
            Assert.fail("Expected to find an entity-persister for the string-list in the super audit type");
        }
    }

    @Test
    public void testNonAuditedSuperclassAuditJoinTableHistory() {
        Assert.assertEquals(Arrays.asList(1), getAuditReader().getRevisions(AuditOverrideAuditJoinTableTest.OtherAuditedEntity.class, entityId));
        AuditOverrideAuditJoinTableTest.OtherAuditedEntity rev = getAuditReader().find(AuditOverrideAuditJoinTableTest.OtherAuditedEntity.class, entityId, 1);
        Assert.assertNotNull(rev);
        Assert.assertEquals(2, rev.getSuperStringList().size());
        TestTools.checkCollection(rev.getSuperStringList(), "Entry1", "Entry2");
    }

    @Test
    public void testNonAuditedSuperclassWithOverrideAuditJoinTableHistory() {
        Assert.assertEquals(Arrays.asList(2), getAuditReader().getRevisions(AuditOverrideAuditJoinTableTest.OtherOverrideAuditedEntity.class, overrideEntityId));
        AuditOverrideAuditJoinTableTest.OtherOverrideAuditedEntity rev = getAuditReader().find(AuditOverrideAuditJoinTableTest.OtherOverrideAuditedEntity.class, overrideEntityId, 2);
        Assert.assertNotNull(rev);
        Assert.assertEquals(2, rev.getSuperStringList().size());
        TestTools.checkCollection(rev.getSuperStringList(), "Entry1", "Entry2");
    }

    @Test
    public void testNonAuditedSuperclassWithAuditParentsOverrideAuditJoinTableHistory() {
        Assert.assertEquals(Arrays.asList(3), getAuditReader().getRevisions(AuditOverrideAuditJoinTableTest.OtherAuditParentsAuditEntity.class, auditParentsEntityId));
        AuditOverrideAuditJoinTableTest.OtherAuditParentsAuditEntity rev = getAuditReader().find(AuditOverrideAuditJoinTableTest.OtherAuditParentsAuditEntity.class, auditParentsEntityId, 3);
        Assert.assertNotNull(rev);
        Assert.assertEquals(2, rev.getSuperStringList().size());
        TestTools.checkCollection(rev.getSuperStringList(), "Entry1", "Entry2");
    }

    @MappedSuperclass
    @Audited
    public static class SuperClass {
        @Id
        private long id;

        private Timestamp version;

        private String superValue;

        @ElementCollection
        @AuditJoinTable(name = "SuperClass_StringList")
        private java.util.List<String> superStringList;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public Timestamp getVersion() {
            return version;
        }

        public void setVersion(Timestamp version) {
            this.version = version;
        }

        public String getSuperValue() {
            return superValue;
        }

        public void setSuperValue(String superValue) {
            this.superValue = superValue;
        }

        public java.util.List<String> getSuperStringList() {
            return superStringList;
        }

        public void setSuperStringList(java.util.List<String> superStringList) {
            this.superStringList = superStringList;
        }
    }

    @Entity(name = "OOE")
    @Audited
    public static class OtherAuditedEntity extends AuditOverrideAuditJoinTableTest.SuperClass {
        private String value;

        @NotAudited
        private String notAuditedValue;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getNotAuditedValue() {
            return notAuditedValue;
        }

        public void setNotAuditedValue(String notAuditedValue) {
            this.notAuditedValue = notAuditedValue;
        }
    }

    @MappedSuperclass
    public static class NonAuditedSuperClass {
        @Id
        private long id;

        private Timestamp version;

        private String superValue;

        @ElementCollection
        @AuditJoinTable(name = "NASC_StringList")
        private java.util.List<String> superStringList;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public Timestamp getVersion() {
            return version;
        }

        public void setVersion(Timestamp version) {
            this.version = version;
        }

        public String getSuperValue() {
            return superValue;
        }

        public void setSuperValue(String superValue) {
            this.superValue = superValue;
        }

        public java.util.List<String> getSuperStringList() {
            return superStringList;
        }

        public void setSuperStringList(java.util.List<String> superStringList) {
            this.superStringList = superStringList;
        }
    }

    @Entity(name = "OOAE")
    @Audited
    @AuditOverrides({ @AuditOverride(forClass = AuditOverrideAuditJoinTableTest.NonAuditedSuperClass.class, name = "superStringList", auditJoinTable = @AuditJoinTable(name = "OOAE_StringList")) })
    public static class OtherOverrideAuditedEntity extends AuditOverrideAuditJoinTableTest.NonAuditedSuperClass {
        private String value;

        @NotAudited
        private String notAuditedValue;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getNotAuditedValue() {
            return notAuditedValue;
        }

        public void setNotAuditedValue(String notAuditedValue) {
            this.notAuditedValue = notAuditedValue;
        }
    }

    @Entity(name = "OAPAE")
    @Audited(auditParents = AuditOverrideAuditJoinTableTest.NonAuditedSuperClass.class)
    @AuditOverrides({ @AuditOverride(forClass = AuditOverrideAuditJoinTableTest.NonAuditedSuperClass.class, name = "superStringList", auditJoinTable = @AuditJoinTable(name = "OAPAE_StringList")) })
    public static class OtherAuditParentsAuditEntity extends AuditOverrideAuditJoinTableTest.NonAuditedSuperClass {
        private String value;

        @NotAudited
        private String notAuditedValue;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getNotAuditedValue() {
            return notAuditedValue;
        }

        public void setNotAuditedValue(String notAuditedValue) {
            this.notAuditedValue = notAuditedValue;
        }
    }
}

