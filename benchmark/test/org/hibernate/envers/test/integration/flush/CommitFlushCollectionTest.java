/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.envers.test.integration.flush;


import FlushModeType.AUTO;
import FlushModeType.COMMIT;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.Version;
import org.hibernate.envers.AuditMappedBy;
import org.hibernate.envers.Audited;
import org.hibernate.envers.test.BaseEnversJPAFunctionalTestCase;
import org.hibernate.envers.test.Priority;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Chris Cranford
 */
@TestForIssue(jiraKey = "HHH-12826 and HHH-12846")
public class CommitFlushCollectionTest extends BaseEnversJPAFunctionalTestCase {
    @MappedSuperclass
    public abstract static class AbstractEntity {
        private Long id;

        private Long version;

        @Id
        @GeneratedValue
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        @Version
        @Column(nullable = false)
        public Long getVersion() {
            return version;
        }

        public void setVersion(Long version) {
            this.version = version;
        }
    }

    @Audited
    @MappedSuperclass
    public static class BaseDocument extends CommitFlushCollectionTest.AbstractEntity {
        private String number;

        private Date date;

        @Column(name = "numberValue", nullable = false)
        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        @Column(name = "dateValue", nullable = false)
        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }
    }

    @Audited
    @Entity(name = "DocumentA")
    public static class DocumentA extends CommitFlushCollectionTest.BaseDocument {
        private List<CommitFlushCollectionTest.DocumentLineA> lines = new LinkedList<>();

        @OneToMany(fetch = FetchType.LAZY, mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true)
        @AuditMappedBy(mappedBy = "document")
        public List<CommitFlushCollectionTest.DocumentLineA> getLines() {
            return lines;
        }

        public void setLines(List<CommitFlushCollectionTest.DocumentLineA> lines) {
            this.lines = lines;
        }

        public CommitFlushCollectionTest.DocumentA addLine(CommitFlushCollectionTest.DocumentLineA line) {
            if (line != null) {
                line.setDocument(this);
                getLines().add(line);
            }
            return this;
        }
    }

    @MappedSuperclass
    public abstract static class BaseDocumentLine extends CommitFlushCollectionTest.AbstractEntity {
        private String text;

        @Column(name = "textValue", nullable = false)
        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    @Audited
    @Entity(name = "DocumentLineA")
    public static class DocumentLineA extends CommitFlushCollectionTest.BaseDocumentLine {
        private CommitFlushCollectionTest.DocumentA document;

        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(updatable = false, insertable = true, nullable = false)
        public CommitFlushCollectionTest.DocumentA getDocument() {
            return document;
        }

        public void setDocument(CommitFlushCollectionTest.DocumentA document) {
            this.document = document;
        }
    }

    private Long entityId1;

    private Long entityId2;

    @Test
    @Priority(10)
    public void initData() {
        // This failed when using Envers.
        entityId1 = persistDocument(COMMIT);
        // This worked
        entityId2 = persistDocument(AUTO);
        // This failed
        mergeDocument(COMMIT, entityId1);
        // This worked
        mergeDocument(AUTO, entityId2);
    }

    @Test
    public void testWithFlushModeCommit() {
        Assert.assertEquals(Arrays.asList(1, 3), getAuditReader().getRevisions(CommitFlushCollectionTest.DocumentA.class, entityId1));
    }

    @Test
    @Priority(1)
    public void testWithFlushModeAuto() {
        Assert.assertEquals(Arrays.asList(2, 4), getAuditReader().getRevisions(CommitFlushCollectionTest.DocumentA.class, entityId2));
    }
}

