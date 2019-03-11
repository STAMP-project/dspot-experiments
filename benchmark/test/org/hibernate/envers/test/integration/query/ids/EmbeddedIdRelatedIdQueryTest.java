/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.envers.test.integration.query.ids;


import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.test.BaseEnversJPAFunctionalTestCase;
import org.hibernate.envers.test.Priority;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Chris Cranford
 */
@TestForIssue(jiraKey = "HHH-11748")
public class EmbeddedIdRelatedIdQueryTest extends BaseEnversJPAFunctionalTestCase {
    @Test
    @Priority(10)
    public void initData() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            final org.hibernate.envers.test.integration.query.ids.Person person = new org.hibernate.envers.test.integration.query.ids.Person(1, "Chris");
            final org.hibernate.envers.test.integration.query.ids.Document document = new org.hibernate.envers.test.integration.query.ids.Document(1, "DL");
            final org.hibernate.envers.test.integration.query.ids.PersonDocument pd = new org.hibernate.envers.test.integration.query.ids.PersonDocument(person, document);
            entityManager.persist(person);
            entityManager.persist(document);
            entityManager.persist(pd);
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            final org.hibernate.envers.test.integration.query.ids.Person person = entityManager.find(.class, 1);
            final org.hibernate.envers.test.integration.query.ids.Document document = new org.hibernate.envers.test.integration.query.ids.Document(2, "Passport");
            final org.hibernate.envers.test.integration.query.ids.PersonDocument pd = new org.hibernate.envers.test.integration.query.ids.PersonDocument(person, document);
            entityManager.persist(document);
            entityManager.persist(pd);
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            final org.hibernate.envers.test.integration.query.ids.Person person = entityManager.find(.class, 1);
            final org.hibernate.envers.test.integration.query.ids.Document document = entityManager.find(.class, 1);
            final org.hibernate.envers.test.integration.query.ids.PersonDocument pd = entityManager.createQuery("FROM PersonDocument WHERE id.person.id = :person AND id.document.id = :document", .class).setParameter("person", person.getId()).setParameter("document", document.getId()).getSingleResult();
            entityManager.remove(pd);
            entityManager.remove(document);
        });
    }

    @Test
    public void testRevisionCounts() {
        Assert.assertEquals(Arrays.asList(1), getAuditReader().getRevisions(EmbeddedIdRelatedIdQueryTest.Person.class, 1));
        Assert.assertEquals(Arrays.asList(1, 3), getAuditReader().getRevisions(EmbeddedIdRelatedIdQueryTest.Document.class, 1));
        Assert.assertEquals(Arrays.asList(2), getAuditReader().getRevisions(EmbeddedIdRelatedIdQueryTest.Document.class, 2));
    }

    @Test
    public void testRelatedIdQueries() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List results = getAuditReader().createQuery().forRevisionsOfEntity(.class, false, true).add(AuditEntity.relatedId("id.person").eq(1)).add(AuditEntity.revisionNumber().eq(1)).getResultList();
            assertEquals(1, results.size());
            final org.hibernate.envers.test.integration.query.ids.Document document = ((org.hibernate.envers.test.integration.query.ids.PersonDocument) (((Object[]) (results.get(0)))[0])).getId().getDocument();
            assertEquals("DL", document.getName());
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List results = getAuditReader().createQuery().forRevisionsOfEntity(.class, false, true).add(AuditEntity.relatedId("id.person").eq(1)).add(AuditEntity.revisionNumber().eq(2)).getResultList();
            assertEquals(1, results.size());
            final org.hibernate.envers.test.integration.query.ids.Document document = ((org.hibernate.envers.test.integration.query.ids.PersonDocument) (((Object[]) (results.get(0)))[0])).getId().getDocument();
            assertEquals("Passport", document.getName());
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List results = getAuditReader().createQuery().forRevisionsOfEntity(.class, false, true).add(AuditEntity.relatedId("id.person").eq(1)).add(AuditEntity.revisionNumber().eq(3)).getResultList();
            assertEquals(1, results.size());
            final org.hibernate.envers.test.integration.query.ids.Document document = ((org.hibernate.envers.test.integration.query.ids.PersonDocument) (((Object[]) (results.get(0)))[0])).getId().getDocument();
            assertNull(document.getName());
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List results = getAuditReader().createQuery().forRevisionsOfEntity(.class, false, true).add(AuditEntity.relatedId("id.document").eq(1)).getResultList();
            assertEquals(2, results.size());
            for (Object result : results) {
                Object[] row = ((Object[]) (result));
                final RevisionType revisionType = ((RevisionType) (row[2]));
                final org.hibernate.envers.test.integration.query.ids.Document document = ((org.hibernate.envers.test.integration.query.ids.PersonDocument) (row[0])).getId().getDocument();
                if (RevisionType.ADD.equals(revisionType)) {
                    assertEquals("DL", document.getName());
                } else
                    if (RevisionType.DEL.equals(revisionType)) {
                        assertNull(document.getName());
                    }

            }
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List results = getAuditReader().createQuery().forRevisionsOfEntity(.class, false, true).add(AuditEntity.relatedId("id.document").eq(2)).getResultList();
            assertEquals(1, results.size());
            for (Object result : results) {
                Object[] row = ((Object[]) (result));
                final RevisionType revisionType = ((RevisionType) (row[2]));
                final org.hibernate.envers.test.integration.query.ids.Document document = ((org.hibernate.envers.test.integration.query.ids.PersonDocument) (row[0])).getId().getDocument();
                assertEquals(RevisionType.ADD, revisionType);
                assertEquals("Passport", document.getName());
            }
        });
    }

    @Embeddable
    public static class PersonDocumentId implements Serializable {
        @ManyToOne(optional = false)
        private EmbeddedIdRelatedIdQueryTest.Document document;

        @ManyToOne(optional = false)
        private EmbeddedIdRelatedIdQueryTest.Person person;

        PersonDocumentId() {
        }

        PersonDocumentId(EmbeddedIdRelatedIdQueryTest.Person person, EmbeddedIdRelatedIdQueryTest.Document document) {
            this.person = person;
            this.document = document;
        }

        public EmbeddedIdRelatedIdQueryTest.Document getDocument() {
            return document;
        }

        public void setDocument(EmbeddedIdRelatedIdQueryTest.Document document) {
            this.document = document;
        }

        public EmbeddedIdRelatedIdQueryTest.Person getPerson() {
            return person;
        }

        public void setPerson(EmbeddedIdRelatedIdQueryTest.Person person) {
            this.person = person;
        }
    }

    @Audited
    @Entity(name = "PersonDocument")
    public static class PersonDocument implements Serializable {
        @EmbeddedId
        private EmbeddedIdRelatedIdQueryTest.PersonDocumentId id;

        PersonDocument() {
        }

        PersonDocument(EmbeddedIdRelatedIdQueryTest.Person person, EmbeddedIdRelatedIdQueryTest.Document document) {
            this.id = new EmbeddedIdRelatedIdQueryTest.PersonDocumentId(person, document);
        }

        public EmbeddedIdRelatedIdQueryTest.PersonDocumentId getId() {
            return id;
        }

        public void setId(EmbeddedIdRelatedIdQueryTest.PersonDocumentId id) {
            this.id = id;
        }
    }

    @Audited
    @Entity(name = "Document")
    public static class Document {
        @Id
        private Integer id;

        private String name;

        Document() {
        }

        Document(Integer id, String name) {
            this.id = id;
            this.name = name;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @Entity(name = "Person")
    @Audited
    public static class Person {
        @Id
        private Integer id;

        private String name;

        Person() {
        }

        Person(Integer id, String name) {
            this.id = id;
            this.name = name;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

