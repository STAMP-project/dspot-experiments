/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.criteria.mapsid;


import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.criteria.CriteriaBuilder;
import javax.validation.constraints.NotNull;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Cody Lerum
 */
public class MapsIdOneToOneSelectTest extends BaseEntityManagerFunctionalTestCase {
    @TestForIssue(jiraKey = "HHH-9296")
    @Test
    public void selectByParent() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.test.criteria.mapsid.Post post = entityManager.find(.class, 1);
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<org.hibernate.test.criteria.mapsid.PostDetails> query = cb.createQuery(.class);
            Root<org.hibernate.test.criteria.mapsid.PostDetails> root = query.from(.class);
            query.where(cb.equal(root.get("post"), post));
            final org.hibernate.test.criteria.mapsid.PostDetails result = entityManager.createQuery(query).getSingleResult();
            assertNotNull(result);
        });
    }

    @TestForIssue(jiraKey = "HHH-9296")
    @Test
    public void findByParentId() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.test.criteria.mapsid.Post post = entityManager.find(.class, 1);
            org.hibernate.test.criteria.mapsid.PostDetails result = entityManager.find(.class, post.getId());
            assertNotNull(result);
        });
    }

    @Entity(name = "Post")
    public static class Post implements Serializable {
        private static final long serialVersionUID = 1L;

        private Integer id;

        private String name;

        @Id
        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        @NotNull
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @Entity(name = "PostDetails")
    public static class PostDetails implements Serializable {
        private static final long serialVersionUID = 1L;

        private Integer id;

        private String name;

        private MapsIdOneToOneSelectTest.Post post;

        @Id
        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        @NotNull
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @OneToOne
        @JoinColumn(name = "id")
        @MapsId
        public MapsIdOneToOneSelectTest.Post getPost() {
            return post;
        }

        public void setPost(MapsIdOneToOneSelectTest.Post post) {
            this.post = post;
        }
    }
}

