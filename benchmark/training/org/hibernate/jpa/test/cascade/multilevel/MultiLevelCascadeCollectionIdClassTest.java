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
import java.util.function.Function;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
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

import static java.util.Optional.ofNullable;


public class MultiLevelCascadeCollectionIdClassTest extends BaseEntityManagerFunctionalTestCase {
    private static final Logger log = Logger.getLogger(MultiLevelCascadeCollectionIdClassTest.class);

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
        private List<MultiLevelCascadeCollectionIdClassTest.SubEntity> subEntities = new ArrayList<>();

        public void addSubEntity(MultiLevelCascadeCollectionIdClassTest.SubEntity subEntity) {
            subEntity.setMainEntity(this);
            subEntities.add(subEntity);
        }

        public void removeSubEntity(MultiLevelCascadeCollectionIdClassTest.SubEntity subEntity) {
            subEntity.setMainEntity(null);
            subEntities.remove(subEntity);
        }

        public Long getIdNum() {
            return idNum;
        }

        public void setIdNum(Long idNum) {
            this.idNum = idNum;
        }

        public List<MultiLevelCascadeCollectionIdClassTest.SubEntity> getSubEntities() {
            return subEntities;
        }

        public void setSubEntities(List<MultiLevelCascadeCollectionIdClassTest.SubEntity> subEntities) {
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

            MultiLevelCascadeCollectionIdClassTest.MainEntity that = ((MultiLevelCascadeCollectionIdClassTest.MainEntity) (o));
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
        private MultiLevelCascadeCollectionIdClassTest.MainEntity mainEntity;

        @OneToMany(mappedBy = "subEntity", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<MultiLevelCascadeCollectionIdClassTest.SubSubEntity> subSubEntities = new ArrayList<>();

        @OneToMany(mappedBy = "subEntity", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<MultiLevelCascadeCollectionIdClassTest.AnotherSubSubEntity> anotherSubSubEntities = new ArrayList<>();

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

        public MultiLevelCascadeCollectionIdClassTest.MainEntity getMainEntity() {
            return mainEntity;
        }

        public void setMainEntity(MultiLevelCascadeCollectionIdClassTest.MainEntity mainEntity) {
            this.mainEntity = mainEntity;
        }

        public List<MultiLevelCascadeCollectionIdClassTest.SubSubEntity> getSubSubEntities() {
            return subSubEntities;
        }

        public void setSubSubEntities(List<MultiLevelCascadeCollectionIdClassTest.SubSubEntity> subSubEntities) {
            this.subSubEntities = subSubEntities;
        }

        public List<MultiLevelCascadeCollectionIdClassTest.AnotherSubSubEntity> getAnotherSubSubEntities() {
            return anotherSubSubEntities;
        }

        public void setAnotherSubSubEntities(List<MultiLevelCascadeCollectionIdClassTest.AnotherSubSubEntity> anotherSubSubEntities) {
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

            MultiLevelCascadeCollectionIdClassTest.SubEntity subEntity = ((MultiLevelCascadeCollectionIdClassTest.SubEntity) (o));
            return Objects.equals(subIdNum, subEntity.subIdNum);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), subIdNum);
        }
    }

    @Entity
    @Table(name = "ANOTHER_SUB_SUB_TABLE")
    @IdClass(MultiLevelCascadeCollectionIdClassTest.AnotherSubSubEntity.AnotherSubSubEntityId.class)
    public static class AnotherSubSubEntity implements Serializable {
        @Id
        @Column(name = "ID_NUM", insertable = false, updatable = false)
        private Long idNum;

        @Id
        @Column(name = "PERSON", insertable = false, updatable = false)
        private String person;

        @Id
        @Column(name = "SOURCE_CODE")
        private String sourceCode;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumns({ @JoinColumn(name = "ID_NUM", referencedColumnName = "ID_NUM", insertable = false, updatable = false), @JoinColumn(name = "PERSON", referencedColumnName = "FAMILY_IDENTIFIER", insertable = false, updatable = false) })
        private MultiLevelCascadeCollectionIdClassTest.SubEntity subEntity;

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

        public MultiLevelCascadeCollectionIdClassTest.SubEntity getSubEntity() {
            return subEntity;
        }

        public void setSubEntity(MultiLevelCascadeCollectionIdClassTest.SubEntity subEntity) {
            idNum = ofNullable(subEntity).map(MultiLevelCascadeCollectionIdClassTest.SubEntity::getMainEntity).map(MultiLevelCascadeCollectionIdClassTest.MainEntity::getIdNum).orElse(null);
            person = ofNullable(subEntity).map(MultiLevelCascadeCollectionIdClassTest.SubEntity::getFamilyIdentifier).orElse(null);
            this.subEntity = subEntity;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o)
                return true;

            if ((o == null) || ((getClass()) != (o.getClass())))
                return false;

            MultiLevelCascadeCollectionIdClassTest.AnotherSubSubEntity that = ((MultiLevelCascadeCollectionIdClassTest.AnotherSubSubEntity) (o));
            return ((Objects.equals(idNum, that.idNum)) && (Objects.equals(person, that.person))) && (Objects.equals(sourceCode, that.sourceCode));
        }

        @Override
        public int hashCode() {
            return Objects.hash(idNum, person, sourceCode);
        }

        @Override
        public String toString() {
            return (((((((("AnotherSubSubEntity{" + "idNum=") + (idNum)) + ", person='") + (person)) + '\'') + ", sourceCode='") + (sourceCode)) + '\'') + '}';
        }

        public static class AnotherSubSubEntityId implements Serializable {
            private Long idNum;

            private String person;

            private String sourceCode;

            @Override
            public boolean equals(Object o) {
                if ((this) == o)
                    return true;

                if ((o == null) || ((getClass()) != (o.getClass())))
                    return false;

                MultiLevelCascadeCollectionIdClassTest.AnotherSubSubEntity.AnotherSubSubEntityId that = ((MultiLevelCascadeCollectionIdClassTest.AnotherSubSubEntity.AnotherSubSubEntityId) (o));
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
    }

    @Entity
    @Table(name = "SUB_SUB_TABLE")
    @IdClass(MultiLevelCascadeCollectionIdClassTest.SubSubEntity.SubSubEntityId.class)
    public static class SubSubEntity implements Serializable {
        @Id
        @Column(name = "ID_NUM", insertable = false, updatable = false)
        private Long idNum;

        @Id
        @Column(name = "CODE")
        private String code;

        @Id
        @Column(name = "IND_NUM", insertable = false, updatable = false)
        private String indNum;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumns({ @JoinColumn(name = "ID_NUM", referencedColumnName = "ID_NUM"), @JoinColumn(name = "IND_NUM", referencedColumnName = "IND_NUM") })
        private MultiLevelCascadeCollectionIdClassTest.SubEntity subEntity;

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

        public MultiLevelCascadeCollectionIdClassTest.SubEntity getSubEntity() {
            return subEntity;
        }

        public void setSubEntity(MultiLevelCascadeCollectionIdClassTest.SubEntity subEntity) {
            idNum = ofNullable(subEntity).map(MultiLevelCascadeCollectionIdClassTest.SubEntity::getMainEntity).map(MultiLevelCascadeCollectionIdClassTest.MainEntity::getIdNum).orElse(null);
            code = ofNullable(subEntity).map(MultiLevelCascadeCollectionIdClassTest.SubEntity::getIndNum).orElse(null);
            this.subEntity = subEntity;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o)
                return true;

            if ((o == null) || ((getClass()) != (o.getClass())))
                return false;

            if (!(super.equals(o)))
                return false;

            MultiLevelCascadeCollectionIdClassTest.SubSubEntity that = ((MultiLevelCascadeCollectionIdClassTest.SubSubEntity) (o));
            return ((Objects.equals(idNum, that.idNum)) && (Objects.equals(code, that.code))) && (Objects.equals(indNum, that.indNum));
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), idNum, code, indNum);
        }

        @Override
        public String toString() {
            return (((((((("SubSubEntity{" + "idNum=") + (idNum)) + ", code='") + (code)) + '\'') + ", indNum='") + (indNum)) + '\'') + '}';
        }

        public static class SubSubEntityId implements Serializable {
            private Long idNum;

            private String code;

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

                MultiLevelCascadeCollectionIdClassTest.SubSubEntity.SubSubEntityId that = ((MultiLevelCascadeCollectionIdClassTest.SubSubEntity.SubSubEntityId) (o));
                return ((Objects.equals(idNum, that.idNum)) && (Objects.equals(code, that.code))) && (Objects.equals(indNum, that.indNum));
            }

            @Override
            public int hashCode() {
                return Objects.hash(idNum, code, indNum);
            }
        }
    }
}

