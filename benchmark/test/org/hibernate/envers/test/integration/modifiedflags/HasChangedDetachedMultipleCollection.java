/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.envers.test.integration.modifiedflags;


import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import org.hibernate.dialect.Oracle8iDialect;
import org.hibernate.envers.test.Priority;
import org.hibernate.envers.test.entities.collection.MultipleCollectionEntity;
import org.hibernate.envers.test.entities.collection.MultipleCollectionRefEntity1;
import org.hibernate.envers.test.entities.collection.MultipleCollectionRefEntity2;
import org.hibernate.envers.test.tools.TestTools;
import org.hibernate.testing.SkipForDialect;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Lukasz Antoniak (lukasz dot antoniak at gmail dot com)
 */
@TestForIssue(jiraKey = "HHH-7437")
@SkipForDialect(value = Oracle8iDialect.class, comment = "Oracle does not support identity key generation")
public class HasChangedDetachedMultipleCollection extends AbstractModifiedFlagsEntityTest {
    private Long mce1Id = null;

    private Long mce2Id = null;

    private Long mcre1Id = null;

    private Long mcre2Id = null;

    @Test
    @Priority(10)
    public void initData() {
        EntityManager em = getEntityManager();
        // Revision 1 - addition.
        em.getTransaction().begin();
        MultipleCollectionEntity mce1 = new MultipleCollectionEntity();
        mce1.setText("MultipleCollectionEntity-1-1");
        em.persist(mce1);// Persisting entity with empty collections.

        em.getTransaction().commit();
        mce1Id = mce1.getId();
        // Revision 2 - update.
        em.getTransaction().begin();
        mce1 = em.find(MultipleCollectionEntity.class, mce1.getId());
        MultipleCollectionRefEntity1 mcre1 = new MultipleCollectionRefEntity1();
        mcre1.setText("MultipleCollectionRefEntity1-1-1");
        mcre1.setMultipleCollectionEntity(mce1);
        mce1.addRefEntity1(mcre1);
        em.persist(mcre1);
        mce1 = em.merge(mce1);
        em.getTransaction().commit();
        mcre1Id = mcre1.getId();
        // No changes.
        em.getTransaction().begin();
        mce1 = em.find(MultipleCollectionEntity.class, mce1.getId());
        mce1 = em.merge(mce1);
        em.getTransaction().commit();
        em.close();
        em = getEntityManager();
        // Revision 3 - updating detached collection.
        em.getTransaction().begin();
        mce1.removeRefEntity1(mcre1);
        mce1 = em.merge(mce1);
        em.getTransaction().commit();
        em.close();
        em = getEntityManager();
        // Revision 4 - updating detached entity, no changes to collection attributes.
        em.getTransaction().begin();
        mce1.setRefEntities1(new ArrayList<MultipleCollectionRefEntity1>());
        mce1.setRefEntities2(new ArrayList<MultipleCollectionRefEntity2>());
        mce1.setText("MultipleCollectionEntity-1-2");
        mce1 = em.merge(mce1);
        em.getTransaction().commit();
        em.close();
        em = getEntityManager();
        // No changes to detached entity (collections were empty before).
        em.getTransaction().begin();
        mce1.setRefEntities1(new ArrayList<MultipleCollectionRefEntity1>());
        mce1.setRefEntities2(new ArrayList<MultipleCollectionRefEntity2>());
        mce1 = em.merge(mce1);
        em.getTransaction().commit();
        // Revision 5 - addition.
        em.getTransaction().begin();
        MultipleCollectionEntity mce2 = new MultipleCollectionEntity();
        mce2.setText("MultipleCollectionEntity-2-1");
        MultipleCollectionRefEntity2 mcre2 = new MultipleCollectionRefEntity2();
        mcre2.setText("MultipleCollectionRefEntity2-1-1");
        mcre2.setMultipleCollectionEntity(mce2);
        mce2.addRefEntity2(mcre2);
        em.persist(mce2);// Cascade persisting related entity.

        em.getTransaction().commit();
        mce2Id = mce2.getId();
        mcre2Id = mcre2.getId();
        em.close();
    }

    @Test
    public void testHasChanged() {
        List list = queryForPropertyHasChanged(MultipleCollectionEntity.class, mce1Id, "text");
        Assert.assertEquals(2, list.size());
        Assert.assertEquals(TestTools.makeList(1, 4), TestTools.extractRevisionNumbers(list));
        list = queryForPropertyHasChanged(MultipleCollectionEntity.class, mce1Id, "refEntities1");
        Assert.assertEquals(3, list.size());
        Assert.assertEquals(TestTools.makeList(1, 2, 3), TestTools.extractRevisionNumbers(list));
        list = queryForPropertyHasChanged(MultipleCollectionEntity.class, mce1Id, "refEntities2");
        Assert.assertEquals(1, list.size());
        Assert.assertEquals(TestTools.makeList(1), TestTools.extractRevisionNumbers(list));
        list = queryForPropertyHasChanged(MultipleCollectionRefEntity1.class, mcre1Id, "text");
        Assert.assertEquals(1, list.size());
        Assert.assertEquals(TestTools.makeList(2), TestTools.extractRevisionNumbers(list));
        list = queryForPropertyHasChanged(MultipleCollectionEntity.class, mce2Id, "text");
        Assert.assertEquals(1, list.size());
        Assert.assertEquals(TestTools.makeList(5), TestTools.extractRevisionNumbers(list));
        list = queryForPropertyHasChanged(MultipleCollectionEntity.class, mce2Id, "refEntities2");
        Assert.assertEquals(1, list.size());
        Assert.assertEquals(TestTools.makeList(5), TestTools.extractRevisionNumbers(list));
        list = queryForPropertyHasChanged(MultipleCollectionRefEntity2.class, mcre2Id, "text");
        Assert.assertEquals(1, list.size());
        Assert.assertEquals(TestTools.makeList(5), TestTools.extractRevisionNumbers(list));
    }
}

