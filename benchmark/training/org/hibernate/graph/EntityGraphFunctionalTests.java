/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.graph;


import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.Hibernate;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil2;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class EntityGraphFunctionalTests extends BaseEntityManagerFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-13175")
    public void testSubsequentSelectFromFind() {
        TransactionUtil2.inTransaction(entityManagerFactory(), ( session) -> {
            final RootGraph<org.hibernate.graph.Issue> graph = GraphParser.parse(.class, "comments", session);
            final org.hibernate.graph.Issue issue = session.find(.class, 1, Collections.singletonMap(GraphSemantic.FETCH.getJpaHintName(), graph));
            assertTrue(Hibernate.isInitialized(issue));
            assertTrue(Hibernate.isInitialized(issue.getComments()));
            assertTrue(Hibernate.isInitialized(issue.getReporter()));
            assertTrue(Hibernate.isInitialized(issue.getAssignee()));
            assertFalse(Hibernate.isInitialized(issue.getAssignee().getAssignedIssues()));
        });
    }

    @Entity(name = "Issue")
    @Table(name = "issue")
    public static class Issue {
        private Integer id;

        private String description;

        private EntityGraphFunctionalTests.User reporter;

        private EntityGraphFunctionalTests.User assignee;

        private List<EntityGraphFunctionalTests.Comment> comments;

        public Issue() {
        }

        public Issue(Integer id, String description, EntityGraphFunctionalTests.User reporter) {
            this.id = id;
            this.description = description;
            this.reporter = reporter;
        }

        @Id
        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        @ManyToOne
        public EntityGraphFunctionalTests.User getReporter() {
            return reporter;
        }

        public void setReporter(EntityGraphFunctionalTests.User reporter) {
            this.reporter = reporter;
        }

        @ManyToOne
        public EntityGraphFunctionalTests.User getAssignee() {
            return assignee;
        }

        public void setAssignee(EntityGraphFunctionalTests.User assignee) {
            this.assignee = assignee;
        }

        @OneToMany(mappedBy = "issue", cascade = CascadeType.ALL)
        public List<EntityGraphFunctionalTests.Comment> getComments() {
            return comments;
        }

        public void setComments(List<EntityGraphFunctionalTests.Comment> comments) {
            this.comments = comments;
        }

        public void addComment(String comment, EntityGraphFunctionalTests.User user) {
            if ((comments) == null) {
                comments = new ArrayList<>();
            }
            comments.add(new EntityGraphFunctionalTests.Comment(this, comment, user));
        }
    }

    @Entity(name = "User")
    @Table(name = "`user`")
    public static class User {
        private Integer id;

        private String name;

        private String login;

        private Set<EntityGraphFunctionalTests.Issue> assignedIssues;

        public User() {
        }

        public User(String name, String login) {
            this.name = name;
            this.login = login;
        }

        @Id
        @GeneratedValue
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

        @OneToMany(mappedBy = "assignee", fetch = FetchType.LAZY)
        public Set<EntityGraphFunctionalTests.Issue> getAssignedIssues() {
            return assignedIssues;
        }

        public void setAssignedIssues(Set<EntityGraphFunctionalTests.Issue> assignedIssues) {
            this.assignedIssues = assignedIssues;
        }
    }

    // "Comment" reserved in Oracle
    @Entity(name = "Comment")
    @Table(name = "CommentTable")
    public static class Comment {
        private Integer id;

        private EntityGraphFunctionalTests.Issue issue;

        private String text;

        private Instant addedOn;

        private EntityGraphFunctionalTests.User commenter;

        public Comment() {
        }

        public Comment(EntityGraphFunctionalTests.Issue issue, String text, EntityGraphFunctionalTests.User commenter) {
            this.issue = issue;
            this.text = text;
            this.commenter = commenter;
            this.addedOn = Instant.now();
        }

        @Id
        @GeneratedValue
        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        @ManyToOne
        @JoinColumn(name = "issue_id", nullable = false)
        public EntityGraphFunctionalTests.Issue getIssue() {
            return issue;
        }

        public void setIssue(EntityGraphFunctionalTests.Issue issue) {
            this.issue = issue;
        }

        @Column(name = "`text`")
        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public Instant getAddedOn() {
            return addedOn;
        }

        public void setAddedOn(Instant addedOn) {
            this.addedOn = addedOn;
        }

        @ManyToOne
        @JoinColumn(name = "user_id", nullable = false)
        public EntityGraphFunctionalTests.User getCommenter() {
            return commenter;
        }

        public void setCommenter(EntityGraphFunctionalTests.User commenter) {
            this.commenter = commenter;
        }
    }
}

