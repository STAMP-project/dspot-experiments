/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.envers.test.integration.modifiedflags;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.envers.Audited;
import org.hibernate.envers.test.Priority;
import org.hibernate.envers.test.tools.TestTools;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Chris Cranford
 */
@TestForIssue(jiraKey = "HHH-7949")
public class HasChangedBidirectionalTest extends AbstractModifiedFlagsEntityTest {
    @Test
    @Priority(10)
    public void initData() throws Exception {
        // Revision 1 | Create ticket with comments
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            final org.hibernate.envers.test.integration.modifiedflags.Ticket ticket = new org.hibernate.envers.test.integration.modifiedflags.Ticket(1, "data-t1");
            final org.hibernate.envers.test.integration.modifiedflags.Comment comment = new org.hibernate.envers.test.integration.modifiedflags.Comment(1, "Initial comment-t1");
            ticket.addComment(comment);
            entityManager.persist(comment);
            entityManager.persist(ticket);
        });
        // Revision 2 | Create ticket without comments
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            final org.hibernate.envers.test.integration.modifiedflags.Ticket ticket = new org.hibernate.envers.test.integration.modifiedflags.Ticket(2, "data-t2");
            entityManager.persist(ticket);
        });
        // Revision 3 | Update ticket with comments
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            final org.hibernate.envers.test.integration.modifiedflags.Ticket ticket = entityManager.find(.class, 1);
            ticket.setData("data-changed-t1");
            entityManager.merge(ticket);
        });
        // Revision 4 | Update ticket without comments
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            final org.hibernate.envers.test.integration.modifiedflags.Ticket ticket = entityManager.find(.class, 2);
            ticket.setData("data-changed-t2");
            entityManager.merge(ticket);
        });
        // Revision 5 | Update ticket and comment
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            final org.hibernate.envers.test.integration.modifiedflags.Ticket ticket = entityManager.find(.class, 1);
            ticket.setData("data-changed-twice");
            ticket.getComments().get(0).setText("comment-modified");
            ticket.getComments().forEach(entityManager::merge);
            entityManager.merge(ticket);
        });
        // Revision 6 | Update ticket and comment collection
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            final org.hibernate.envers.test.integration.modifiedflags.Ticket ticket = entityManager.find(.class, 1);
            final org.hibernate.envers.test.integration.modifiedflags.Comment comment = new org.hibernate.envers.test.integration.modifiedflags.Comment(2, "Comment2");
            ticket.addComment(comment);
            entityManager.merge(comment);
            entityManager.merge(ticket);
        });
    }

    @Test
    public void testRevisionCounts() {
        Assert.assertEquals(Arrays.asList(1, 3, 5, 6), getAuditReader().getRevisions(HasChangedBidirectionalTest.Ticket.class, 1));
        Assert.assertEquals(Arrays.asList(2, 4), getAuditReader().getRevisions(HasChangedBidirectionalTest.Ticket.class, 2));
        Assert.assertEquals(Arrays.asList(1, 5), getAuditReader().getRevisions(HasChangedBidirectionalTest.Comment.class, 1));
        Assert.assertEquals(Arrays.asList(6), getAuditReader().getRevisions(HasChangedBidirectionalTest.Comment.class, 2));
    }

    @Test
    public void testHasChanged() {
        Assert.assertEquals(Arrays.asList(1, 6), TestTools.extractRevisionNumbers(queryForPropertyHasChanged(HasChangedBidirectionalTest.Ticket.class, 1, "comments")));
        Assert.assertEquals(Arrays.asList(2), TestTools.extractRevisionNumbers(queryForPropertyHasChanged(HasChangedBidirectionalTest.Ticket.class, 2, "comments")));
    }

    @Test
    public void testHasNotChanged() {
        Assert.assertEquals(Arrays.asList(3, 5), TestTools.extractRevisionNumbers(queryForPropertyHasNotChanged(HasChangedBidirectionalTest.Ticket.class, 1, "comments")));
        Assert.assertEquals(Arrays.asList(4), TestTools.extractRevisionNumbers(queryForPropertyHasNotChanged(HasChangedBidirectionalTest.Ticket.class, 2, "comments")));
    }

    @Entity(name = "Ticket")
    @Audited(withModifiedFlag = true)
    public static class Ticket {
        @Id
        private Integer id;

        private String data;

        @OneToMany(mappedBy = "ticket")
        private List<HasChangedBidirectionalTest.Comment> comments = new ArrayList<>();

        Ticket() {
        }

        public Ticket(Integer id, String data) {
            this.id = id;
            this.data = data;
        }

        public Integer getId() {
            return id;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        public List<HasChangedBidirectionalTest.Comment> getComments() {
            return comments;
        }

        public void addComment(HasChangedBidirectionalTest.Comment comment) {
            comment.setTicket(this);
            comments.add(comment);
        }
    }

    @Entity(name = "Comment")
    @Table(name = "COMMENTS")
    @Audited(withModifiedFlag = true)
    public static class Comment {
        @Id
        private Integer id;

        @ManyToOne
        private HasChangedBidirectionalTest.Ticket ticket;

        private String text;

        Comment() {
        }

        public Comment(Integer id, String text) {
            this.id = id;
            this.text = text;
        }

        public Integer getId() {
            return id;
        }

        public HasChangedBidirectionalTest.Ticket getTicket() {
            return ticket;
        }

        public void setTicket(HasChangedBidirectionalTest.Ticket ticket) {
            this.ticket = ticket;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}

