/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.cascade.multilevel;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.FailureExpected;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.jboss.logging.Logger;
import org.junit.Test;


public class MultiLevelCascadeCollectionEmbeddableTest extends BaseEntityManagerFunctionalTestCase {
    private static final Logger log = Logger.getLogger(MultiLevelCascadeCollectionEmbeddableTest.class);

    @Test
    @FailureExpected(jiraKey = "HHH-12291")
    public void testHibernateDeleteEntityWithoutInitializingCollections() throws Exception {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.jpa.test.cascade.multilevel.MainEntity mainEntity = entityManager.find(.class, 99427L);
            assertNotNull(mainEntity);
            assertFalse(mainEntity.getSubEntities().isEmpty());
            Optional<org.hibernate.jpa.test.cascade.multilevel.SubEntity> subEntityToRemove = mainEntity.getSubEntities().stream().filter(( subEntity) -> "123A".equals(subEntity.getIndNum())).findFirst();
            subEntityToRemove.ifPresent(mainEntity::removeSubEntity);
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-12294")
    public void testHibernateDeleteEntityInitializeCollections() throws Exception {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.jpa.test.cascade.multilevel.MainEntity mainEntity = entityManager.find(.class, 99427L);
            assertNotNull(mainEntity);
            assertFalse(mainEntity.getSubEntities().isEmpty());
            Optional<org.hibernate.jpa.test.cascade.multilevel.SubEntity> subEntityToRemove = mainEntity.getSubEntities().stream().filter(( subEntity) -> "123A".equals(subEntity.getIndNum())).findFirst();
            if (subEntityToRemove.isPresent()) {
                org.hibernate.jpa.test.cascade.multilevel.SubEntity subEntity = subEntityToRemove.get();
                assertEquals(1, subEntity.getSubSubEntities().size());
                assertEquals(0, subEntity.getAnotherSubSubEntities().size());
                mainEntity.removeSubEntity(subEntity);
            }
        });
    }

    @Entity
    @Table(name = "MAIN_TABLE")
    public static class MainEntity implements Serializable {
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idNum")
        @SequenceGenerator(name = "idNum", sequenceName = "id_num", allocationSize = 1)
        @Column(name = "ID_NUM")
        private Long idNum;

        @OneToMany(mappedBy = "mainEntity", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<MultiLevelCascadeCollectionEmbeddableTest.SubEntity> subEntities = new ArrayList<>();

        public void addSubEntity(MultiLevelCascadeCollectionEmbeddableTest.SubEntity subEntity) {
            subEntity.setMainEntity(this);
            subEntities.add(subEntity);
        }

        public void removeSubEntity(MultiLevelCascadeCollectionEmbeddableTest.SubEntity subEntity) {
            subEntity.setMainEntity(null);
            subEntities.remove(subEntity);
        }

        public Long getIdNum() {
            return idNum;
        }

        public void setIdNum(Long idNum) {
            this.idNum = idNum;
        }

        public List<MultiLevelCascadeCollectionEmbeddableTest.SubEntity> getSubEntities() {
            return subEntities;
        }

        public void setSubEntities(List<MultiLevelCascadeCollectionEmbeddableTest.SubEntity> subEntities) {
            this.subEntities = subEntities;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o)
                return true;

            if ((o == null) || ((getClass()) != (o.getClass())))
                return false;

            if (!(super.equals(o)))
                return false;

            MultiLevelCascadeCollectionEmbeddableTest.MainEntity that = ((MultiLevelCascadeCollectionEmbeddableTest.MainEntity) (o));
            return Objects.equals(idNum, that.idNum);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), idNum);
        }
    }

    @Entity
    @Table(name = "SUB_TABLE")
    public static class SubEntity implements Serializable {
        @Id
        @Column(name = "SUB_ID")
        private Long subIdNum;

        @Column(name = "IND_NUM")
        private String indNum;

        @Column(name = "FAMILY_IDENTIFIER")
        private String familyIdentifier;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "ID_NUM")
        private MultiLevelCascadeCollectionEmbeddableTest.MainEntity mainEntity;

        @OneToMany(mappedBy = "subEntity", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<MultiLevelCascadeCollectionEmbeddableTest.SubSubEntity> subSubEntities = new ArrayList<>();

        @OneToMany(mappedBy = "subEntity", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<MultiLevelCascadeCollectionEmbeddableTest.AnotherSubSubEntity> anotherSubSubEntities = new ArrayList<>();

        public Long getSubIdNum() {
            return subIdNum;
        }

        public void setSubIdNum(Long subIdNum) {
            this.subIdNum = subIdNum;
        }

        public String getIndNum() {
            return indNum;
        }

        public void setIndNum(String indNum) {
            this.indNum = indNum;
        }

        public String getFamilyIdentifier() {
            return familyIdentifier;
        }

        public void setFamilyIdentifier(String familyIdentifier) {
            this.familyIdentifier = familyIdentifier;
        }

        public MultiLevelCascadeCollectionEmbeddableTest.MainEntity getMainEntity() {
            return mainEntity;
        }

        public void setMainEntity(MultiLevelCascadeCollectionEmbeddableTest.MainEntity mainEntity) {
            this.mainEntity = mainEntity;
        }

        public List<MultiLevelCascadeCollectionEmbeddableTest.SubSubEntity> getSubSubEntities() {
            return subSubEntities;
        }

        public void setSubSubEntities(List<MultiLevelCascadeCollectionEmbeddableTest.SubSubEntity> subSubEntities) {
            this.subSubEntities = subSubEntities;
        }

        public List<MultiLevelCascadeCollectionEmbeddableTest.AnotherSubSubEntity> getAnotherSubSubEntities() {
            return anotherSubSubEntities;
        }

        public void setAnotherSubSubEntities(List<MultiLevelCascadeCollectionEmbeddableTest.AnotherSubSubEntity> anotherSubSubEntities) {
            this.anotherSubSubEntities = anotherSubSubEntities;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o)
                return true;

            if ((o == null) || ((getClass()) != (o.getClass())))
                return false;

            if (!(super.equals(o)))
                return false;

            MultiLevelCascadeCollectionEmbeddableTest.SubEntity subEntity = ((MultiLevelCascadeCollectionEmbeddableTest.SubEntity) (o));
            return Objects.equals(subIdNum, subEntity.subIdNum);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), subIdNum);
        }
    }

    @Entity
    @Table(name = "ANOTHER_SUB_SUB_TABLE")
    public static class AnotherSubSubEntity implements Serializable {
        @EmbeddedId
        private MultiLevelCascadeCollectionEmbeddableTest.AnotherSubSubEntityId id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumns({ @JoinColumn(name = "ID_NUM", referencedColumnName = "ID_NUM", insertable = false, updatable = false), @JoinColumn(name = "PERSON", referencedColumnName = "FAMILY_IDENTIFIER", insertable = false, updatable = false) })
        private MultiLevelCascadeCollectionEmbeddableTest.SubEntity subEntity;
    }

    @Embeddable
    public static class AnotherSubSubEntityId implements Serializable {
        @Column(name = "ID_NUM", insertable = false, updatable = false)
        private Long idNum;

        @Column(name = "PERSON", insertable = false, updatable = false)
        private String person;

        @Column(name = "SOURCE_CODE")
        private String sourceCode;

        @Override
        public boolean equals(Object o) {
            if ((this) == o)
                return true;

            if ((o == null) || ((getClass()) != (o.getClass())))
                return false;

            MultiLevelCascadeCollectionEmbeddableTest.AnotherSubSubEntityId that = ((MultiLevelCascadeCollectionEmbeddableTest.AnotherSubSubEntityId) (o));
            return ((Objects.equals(idNum, that.idNum)) && (Objects.equals(person, that.person))) && (Objects.equals(sourceCode, that.sourceCode));
        }

        @Override
        public int hashCode() {
            return Objects.hash(idNum, person, sourceCode);
        }

        public Long getIdNum() {
            return idNum;
        }

        public void setIdNum(Long idNum) {
            this.idNum = idNum;
        }

        public String getPerson() {
            return person;
        }

        public void setPerson(String person) {
            this.person = person;
        }

        public String getSourceCode() {
            return sourceCode;
        }

        public void setSourceCode(String sourceCode) {
            this.sourceCode = sourceCode;
        }
    }

    @Entity
    @Table(name = "SUB_SUB_TABLE")
    public static class SubSubEntity {
        @EmbeddedId
        private MultiLevelCascadeCollectionEmbeddableTest.SubSubEntityId id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumns({ @JoinColumn(name = "ID_NUM", referencedColumnName = "ID_NUM", insertable = false, updatable = false), @JoinColumn(name = "IND_NUM", referencedColumnName = "IND_NUM", insertable = false, updatable = false) })
        private MultiLevelCascadeCollectionEmbeddableTest.SubEntity subEntity;
    }

    @Embeddable
    public static class SubSubEntityId implements Serializable {
        @Column(name = "ID_NUM")
        private Long idNum;

        @Column(name = "CODE")
        private String code;

        @Column(name = "IND_NUM")
        private String indNum;

        public Long getIdNum() {
            return idNum;
        }

        public void setIdNum(Long idNum) {
            this.idNum = idNum;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getIndNum() {
            return indNum;
        }

        public void setIndNum(String indNum) {
            this.indNum = indNum;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o)
                return true;

            if ((o == null) || ((getClass()) != (o.getClass())))
                return false;

            MultiLevelCascadeCollectionEmbeddableTest.SubSubEntityId that = ((MultiLevelCascadeCollectionEmbeddableTest.SubSubEntityId) (o));
            return ((Objects.equals(idNum, that.idNum)) && (Objects.equals(code, that.code))) && (Objects.equals(indNum, that.indNum));
        }

        @Override
        public int hashCode() {
            return Objects.hash(idNum, code, indNum);
        }
    }
}

