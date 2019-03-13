/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.bytecode.enhancement.lazy;


import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Query;
import javax.persistence.Table;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.bytecode.enhancement.BytecodeEnhancerRunner;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Luis Barreiro
 */
@TestForIssue(jiraKey = "HHH-11576")
@RunWith(BytecodeEnhancerRunner.class)
public class LazyCollectionDeletedTest extends BaseCoreFunctionalTestCase {
    private Long postId;

    @Test
    public void test() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            Query query = s.createQuery(("from AdditionalDetails where id=" + (postId)));
            org.hibernate.test.bytecode.enhancement.lazy.AdditionalDetails additionalDetails = ((org.hibernate.test.bytecode.enhancement.lazy.AdditionalDetails) (query.getSingleResult()));
            additionalDetails.details = "New data";
            s.persist(additionalDetails);
            // additionalDetais.post.tags get deleted on commit
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            Query query = s.createQuery(("from Post where id=" + (postId)));
            org.hibernate.test.bytecode.enhancement.lazy.Post retrievedPost = ((org.hibernate.test.bytecode.enhancement.lazy.Post) (query.getSingleResult()));
            assertFalse("No tags found", retrievedPost.tags.isEmpty());
            retrievedPost.tags.forEach(( tag) -> System.out.println(("Found tag: " + tag)));
        });
    }

    // --- //
    @Entity(name = "Tag")
    @Table(name = "TAG")
    private static class Tag {
        @Id
        @GeneratedValue
        Long id;

        String name;

        Tag() {
        }

        Tag(String name) {
            this.name = name;
        }
    }

    @Entity(name = "Post")
    @Table(name = "POST")
    private static class Post {
        @Id
        @GeneratedValue
        Long id;

        @ManyToMany(cascade = CascadeType.ALL)
        Set<LazyCollectionDeletedTest.Tag> tags;

        @OneToOne(fetch = FetchType.LAZY, mappedBy = "post", cascade = CascadeType.ALL)
        LazyCollectionDeletedTest.AdditionalDetails additionalDetails;
    }

    @Entity(name = "AdditionalDetails")
    @Table(name = "ADDITIONAL_DETAILS")
    private static class AdditionalDetails {
        @Id
        Long id;

        String details;

        @OneToOne(optional = false)
        @MapsId
        LazyCollectionDeletedTest.Post post;
    }
}

