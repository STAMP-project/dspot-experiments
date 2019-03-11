/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.filter;


import java.sql.Timestamp;
import java.util.HashSet;
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
import org.hibernate.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.FilterDefs;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.ParamDef;
import org.hibernate.query.Query;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Boriero
 */
public class OneToManyWithDynamicFilterTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testForIssue() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            final Filter enableFilter = session.enableFilter("aliveOnly");
            enableFilter.setParameter("aliveTimestamp", Timestamp.valueOf("9999-12-31 00:00:00"));
            enableFilter.setParameter("deleted", true);
            enableFilter.validate();
            final Query query = session.createQuery(("select a.id from ArticleRevision as a " + ("left join a.articleTradings as t " + "with ( (t.partyId = :p_0)  and  (t.classifier = :p_1) )")));
            query.setParameter("p_0", 1L);
            query.setParameter("p_1", "no_classification");
            final List list = query.list();
            assertThat(list.size(), is(1));
        });
    }

    @Entity(name = "ArticleRevision")
    @Table(name = "REVISION")
    @FilterDefs({ @FilterDef(name = "aliveOnly", parameters = { @ParamDef(name = "aliveTimestamp", type = "timestamp"), @ParamDef(name = "deleted", type = "boolean") }, defaultCondition = "DELETION_TIMESTAMP = :aliveTimestamp and DELETED = :deleted") })
    @Filters({ @org.hibernate.annotations.Filter(name = "aliveOnly", condition = "DELETION_TIMESTAMP = :aliveTimestamp and DELETED = :deleted") })
    public static class ArticleRevision {
        @Id
        @GeneratedValue
        private long id;

        @Column(name = "DELETION_TIMESTAMP")
        private Timestamp deletionTimestamp;

        @Column(name = "DELETED")
        private boolean deleted;

        @OneToMany(mappedBy = "articleRevision", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
        @org.hibernate.annotations.Filter(name = "aliveOnly")
        private Set<OneToManyWithDynamicFilterTest.ArticleTrading> articleTradings = new HashSet<OneToManyWithDynamicFilterTest.ArticleTrading>();

        public void setDeletionTimestamp(Timestamp deletionTimestamp) {
            this.deletionTimestamp = deletionTimestamp;
        }

        public void setDeleted(boolean deleted) {
            this.deleted = deleted;
        }

        public void addArticleTradings(OneToManyWithDynamicFilterTest.ArticleTrading articleTrading) {
            this.articleTradings.add(articleTrading);
            articleTrading.setArticleRevision(this);
        }
    }

    @Entity(name = "ArticleTrading")
    @Table(name = "TRADING")
    public static class ArticleTrading {
        @Id
        @GeneratedValue
        private long id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "articleRevision", nullable = false)
        private OneToManyWithDynamicFilterTest.ArticleRevision articleRevision;

        private long partyId;

        private String classifier;

        @Column(name = "DELETED")
        private boolean deleted;

        @Column(name = "DELETION_TIMESTAMP")
        protected Timestamp deletionTimestamp;

        public void setArticleRevision(OneToManyWithDynamicFilterTest.ArticleRevision articleRevision) {
            this.articleRevision = articleRevision;
        }

        public void setPartyId(long partyId) {
            this.partyId = partyId;
        }

        public void setClassifier(String classifier) {
            this.classifier = classifier;
        }

        public void setDeletionTimestamp(Timestamp deletionTimestamp) {
            this.deletionTimestamp = deletionTimestamp;
        }

        public void setDeleted(boolean deleted) {
            this.deleted = deleted;
        }
    }
}

