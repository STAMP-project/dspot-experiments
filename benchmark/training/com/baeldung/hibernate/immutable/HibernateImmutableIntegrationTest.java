package com.baeldung.hibernate.immutable;


import com.baeldung.hibernate.immutable.entities.Event;
import com.baeldung.hibernate.immutable.entities.EventGeneratedId;
import javax.persistence.PersistenceException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.hibernate.Session;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * Configured in: immutable.cfg.xml
 */
public class HibernateImmutableIntegrationTest {
    private static Session session;

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void addEvent() {
        Event event = new Event();
        event.setId(2L);
        event.setTitle("Public Event");
        HibernateImmutableIntegrationTest.session.save(event);
        HibernateImmutableIntegrationTest.session.getTransaction().commit();
        HibernateImmutableIntegrationTest.session.close();
    }

    @Test
    public void updateEvent() {
        HibernateImmutableIntegrationTest.createEvent();
        Event event = ((Event) (HibernateImmutableIntegrationTest.session.createQuery("FROM Event WHERE title='New Event'").list().get(0)));
        event.setTitle("Private Event");
        HibernateImmutableIntegrationTest.session.update(event);
        HibernateImmutableIntegrationTest.session.flush();
        HibernateImmutableIntegrationTest.session.refresh(event);
        HibernateImmutableIntegrationTest.session.close();
        MatcherAssert.assertThat(event.getTitle(), IsEqual.equalTo("New Event"));
        MatcherAssert.assertThat(event.getId(), IsEqual.equalTo(5L));
    }

    @Test
    public void deleteEvent() {
        HibernateImmutableIntegrationTest.createEvent();
        Event event = ((Event) (HibernateImmutableIntegrationTest.session.createQuery("FROM Event WHERE title='New Event'").list().get(0)));
        HibernateImmutableIntegrationTest.session.delete(event);
        HibernateImmutableIntegrationTest.session.getTransaction().commit();
        HibernateImmutableIntegrationTest.session.close();
    }

    @Test
    public void addGuest() {
        HibernateImmutableIntegrationTest.createEvent();
        Event event = ((Event) (HibernateImmutableIntegrationTest.session.createQuery("FROM Event WHERE title='New Event'").list().get(0)));
        String newGuest = "Sara";
        event.getGuestList().add(newGuest);
        exception.expect(PersistenceException.class);
        HibernateImmutableIntegrationTest.session.save(event);
        HibernateImmutableIntegrationTest.session.getTransaction().commit();
        HibernateImmutableIntegrationTest.session.close();
    }

    @Test
    public void deleteCascade() {
        Event event = ((Event) (HibernateImmutableIntegrationTest.session.createQuery("FROM Event WHERE title='New Event'").list().get(0)));
        String guest = event.getGuestList().iterator().next();
        event.getGuestList().remove(guest);
        exception.expect(PersistenceException.class);
        HibernateImmutableIntegrationTest.session.saveOrUpdate(event);
        HibernateImmutableIntegrationTest.session.getTransaction().commit();
    }

    @Test
    public void updateEventGenerated() {
        HibernateImmutableIntegrationTest.createEventGenerated();
        EventGeneratedId eventGeneratedId = ((EventGeneratedId) (HibernateImmutableIntegrationTest.session.createQuery("FROM EventGeneratedId WHERE name LIKE '%John%'").list().get(0)));
        eventGeneratedId.setName("Mike");
        HibernateImmutableIntegrationTest.session.update(eventGeneratedId);
        HibernateImmutableIntegrationTest.session.flush();
        HibernateImmutableIntegrationTest.session.refresh(eventGeneratedId);
        HibernateImmutableIntegrationTest.session.close();
        MatcherAssert.assertThat(eventGeneratedId.getName(), IsEqual.equalTo("John"));
        MatcherAssert.assertThat(eventGeneratedId.getId(), IsEqual.equalTo(1L));
    }
}

