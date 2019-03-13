/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.cid.keymanytoone;


import org.hibernate.Session;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class EagerKeyManyToOneTest extends BaseCoreFunctionalTestCase {
    public static final String CARD_ID = "cardId";

    public static final String KEY_ID = "keyId";

    @Test
    @TestForIssue(jiraKey = "HHH-4147")
    public void testLoadEntityWithEagerFetchingToKeyManyToOneReferenceBackToSelf() {
        // based on the core testsuite test of same name in org.hibernate.test.keymanytoone.bidir.component.EagerKeyManyToOneTest
        // meant to test against regression relating to http://opensource.atlassian.com/projects/hibernate/browse/HHH-2277
        // and http://opensource.atlassian.com/projects/hibernate/browse/HHH-4147
        {
            Session s = openSession();
            s.beginTransaction();
            Card card = new Card(EagerKeyManyToOneTest.CARD_ID);
            Key key = new Key(EagerKeyManyToOneTest.KEY_ID);
            card.addField(card, key);
            s.persist(key);
            s.persist(card);
            s.getTransaction().commit();
            s.close();
        }
        {
            Session s = openSession();
            s.beginTransaction();
            try {
                Card card = ((Card) (s.get(Card.class, EagerKeyManyToOneTest.CARD_ID)));
                Assert.assertEquals(1, card.getFields().size());
                CardField cf = card.getFields().iterator().next();
                Assert.assertSame(card, cf.getPrimaryKey().getCard());
            } catch (StackOverflowError soe) {
                Assert.fail("eager + key-many-to-one caused stack-overflow in annotations");
            } finally {
                s.getTransaction().commit();
                s.close();
            }
        }
        {
            Session s = openSession();
            s.beginTransaction();
            Card card = ((Card) (s.get(Card.class, EagerKeyManyToOneTest.CARD_ID)));
            Key key = ((Key) (s.get(Key.class, EagerKeyManyToOneTest.KEY_ID)));
            s.delete(card);
            s.delete(key);
            s.getTransaction().commit();
            s.close();
        }
    }
}

