/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.event.collection.detached;


import java.util.Collections;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.event.spi.PostCollectionRecreateEvent;
import org.hibernate.event.spi.PostCollectionUpdateEvent;
import org.hibernate.event.spi.PreCollectionRecreateEvent;
import org.hibernate.event.spi.PreCollectionUpdateEvent;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
@TestForIssue(jiraKey = "HHH-7928")
public class MergeCollectionEventTest extends BaseCoreFunctionalTestCase {
    private AggregatedCollectionEventListener.IntegratorImpl collectionListenerIntegrator = new AggregatedCollectionEventListener.IntegratorImpl();

    @Test
    public void testCollectionEventHandlingOnMerge() {
        final AggregatedCollectionEventListener listener = collectionListenerIntegrator.getListener();
        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        // This first bit really is just preparing the entities.  There is generally no collection
        // events of real interest during this part
        Session s = openSession();
        s.beginTransaction();
        Character paul = new Character(1, "Paul Atreides");
        s.save(paul);
        s.getTransaction().commit();
        s.close();
        Assert.assertEquals(2, listener.getEventEntryList().size());
        checkListener(0, PreCollectionRecreateEvent.class, paul, Collections.EMPTY_LIST);
        checkListener(1, PostCollectionRecreateEvent.class, paul, Collections.EMPTY_LIST);
        listener.reset();
        s = openSession();
        s.beginTransaction();
        Character paulo = new Character(2, "Paulo Atreides");
        s.save(paulo);
        s.getTransaction().commit();
        s.close();
        Assert.assertEquals(2, listener.getEventEntryList().size());
        checkListener(0, PreCollectionRecreateEvent.class, paulo, Collections.EMPTY_LIST);
        checkListener(1, PostCollectionRecreateEvent.class, paulo, Collections.EMPTY_LIST);
        listener.reset();
        s = openSession();
        s.beginTransaction();
        Alias alias1 = new Alias(1, "Paul Muad'Dib");
        s.save(alias1);
        s.getTransaction().commit();
        s.close();
        Assert.assertEquals(2, listener.getEventEntryList().size());
        checkListener(0, PreCollectionRecreateEvent.class, alias1, Collections.EMPTY_LIST);
        checkListener(1, PostCollectionRecreateEvent.class, alias1, Collections.EMPTY_LIST);
        listener.reset();
        s = openSession();
        s.beginTransaction();
        Alias alias2 = new Alias(2, "Usul");
        s.save(alias2);
        s.getTransaction().commit();
        s.close();
        Assert.assertEquals(2, listener.getEventEntryList().size());
        checkListener(0, PreCollectionRecreateEvent.class, alias2, Collections.EMPTY_LIST);
        checkListener(1, PostCollectionRecreateEvent.class, alias2, Collections.EMPTY_LIST);
        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        // at this point we can start setting up the associations and checking collection events
        // of "real interest"
        listener.reset();
        paul.associateAlias(alias1);
        paul.associateAlias(alias2);
        paulo.associateAlias(alias1);
        paulo.associateAlias(alias2);
        s = openSession();
        s.beginTransaction();
        s.merge(alias1);
        Assert.assertEquals(0, listener.getEventEntryList().size());
        // this is where HHH-7928 (problem with HHH-6361 fix) shows up...
        s.flush();
        Assert.assertEquals(8, listener.getEventEntryList().size());// 4 collections x 2 events per

        checkListener(0, PreCollectionUpdateEvent.class, alias1, Collections.EMPTY_LIST);
        checkListener(1, PostCollectionUpdateEvent.class, alias1, alias1.getCharacters());
        checkListener(2, PreCollectionUpdateEvent.class, paul, Collections.EMPTY_LIST);
        checkListener(3, PostCollectionUpdateEvent.class, paul, paul.getAliases());
        checkListener(4, PreCollectionUpdateEvent.class, alias2, Collections.EMPTY_LIST);
        checkListener(5, PostCollectionUpdateEvent.class, alias2, alias2.getCharacters());
        checkListener(6, PreCollectionUpdateEvent.class, paulo, Collections.EMPTY_LIST);
        checkListener(7, PostCollectionUpdateEvent.class, paulo, paul.getAliases());
        List<Character> alias1CharactersSnapshot = copy(alias1.getCharacters());
        List<Character> alias2CharactersSnapshot = copy(alias2.getCharacters());
        listener.reset();
        s.merge(alias2);
        Assert.assertEquals(0, listener.getEventEntryList().size());
        s.flush();
        Assert.assertEquals(8, listener.getEventEntryList().size());// 4 collections x 2 events per

        checkListener(0, PreCollectionUpdateEvent.class, alias1, alias1CharactersSnapshot);
        checkListener(1, PostCollectionUpdateEvent.class, alias1, alias1CharactersSnapshot);
        // checkListener( 2, PreCollectionUpdateEvent.class, paul, Collections.EMPTY_LIST );
        // checkListener( 3, PostCollectionUpdateEvent.class, paul, paul.getAliases() );
        checkListener(4, PreCollectionUpdateEvent.class, alias2, alias2CharactersSnapshot);
        checkListener(5, PostCollectionUpdateEvent.class, alias2, alias2.getCharacters());
        // checkListener( 6, PreCollectionUpdateEvent.class, paulo, Collections.EMPTY_LIST );
        // checkListener( 7, PostCollectionUpdateEvent.class, paulo, paul.getAliases() );
        s.getTransaction().commit();
        s.close();
        // 
        // checkListener(listeners, listeners.getInitializeCollectionListener(),
        // mce, null, eventCount++);
        // checkListener(listeners, listeners.getPreCollectionUpdateListener(),
        // mce, oldRefentities1, eventCount++);
        // checkListener(listeners, listeners.getPostCollectionUpdateListener(),
        // mce, mce.getRefEntities1(), eventCount++);
    }
}

