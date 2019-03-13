/**
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.springsource.greenhouse.events;


import com.springsource.greenhouse.utils.Location;
import java.util.List;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;


public class JdbcEventRepositoryTest {
    private EmbeddedDatabase db;

    private JdbcTemplate jdbcTemplate;

    private EventRepository eventRepository;

    @Test
    public void findEventBySlug() {
        Event event = eventRepository.findEventBySlug("s2gx", 2010, 10, "chicago");
        Assert.assertNotNull(event);
        Assert.assertEquals("SpringOne2gx", event.getTitle());
        Assert.assertEquals("SpringOne 2GX is a one-of-a-kind conference for application developers, solution architects, web operations and IT teams who develop, deploy and manage business applications.", event.getDescription());
        // assertEquals(new DateTime(2010, 10, 19, 15, 0, 0, 0, event.getTimeZone()), event.getStartTime().withZone(event.getTimeZone()));
        // assertEquals(new DateTime(2010, 10, 22, 15, 0, 0, 0, event.getTimeZone()), event.getEndTime().withZone(event.getTimeZone()));
        Assert.assertEquals("s2gx", event.getGroup().getId());
        Assert.assertEquals("SpringOne2gx", event.getGroup().getLabel());
        Assert.assertEquals("Westin Lombard Yorktown Center", event.getVenues().iterator().next().getName());
        Assert.assertEquals("70 Yorktown Center Lombard, IL 60148", event.getVenues().iterator().next().getPostalAddress());
        Assert.assertEquals(new Location(41.8751108905486, (-88.0184300761646)), event.getVenues().iterator().next().getLocation());
        Assert.assertEquals("adjacent to Shopping Center", event.getVenues().iterator().next().getLocationHint());
    }

    @Test
    public void findEventSearchString() {
        Assert.assertEquals("#s2gx", eventRepository.findEventSearchString(1L));
    }

    @Test
    public void findEventSessionSearchString() {
        Assert.assertEquals("#s2gx #mvc", eventRepository.findSessionSearchString(1L, 1));
    }

    @Test
    public void findSessionsOnDay() {
        List<EventSession> sessions = eventRepository.findSessionsOnDay(1L, new LocalDate(2010, 10, 21), 1L);
        Assert.assertEquals(2, sessions.size());
        assertSocial(sessions.get(0), true);
        assertMobile(sessions.get(1), true);
    }

    @Test
    public void findSessionsOnDayEdge() {
        List<EventSession> sessions = eventRepository.findSessionsOnDay(1L, new LocalDate(2010, 10, 19), 1L);
        Assert.assertEquals(1, sessions.size());
    }

    @Test
    public void findEventFavorites() {
        List<EventSession> favorites = eventRepository.findEventFavorites(1L, 2L);
        Assert.assertEquals(2, favorites.size());
        assertSocial(favorites.get(0), false);
        assertMobile(favorites.get(1), false);
    }

    @Test
    public void findAttendeeFavorites() {
        List<EventSession> favorites = eventRepository.findAttendeeFavorites(1L, 1L);
        Assert.assertEquals(2, favorites.size());
        assertSocial(favorites.get(0), true);
        assertMobile(favorites.get(1), true);
    }

    @Test
    public void toggleFavorite() {
        Assert.assertFalse(eventRepository.toggleFavorite(1L, 3, 1L));
        Assert.assertTrue(eventRepository.toggleFavorite(1L, 3, 1L));
    }

    @Test
    public void rate() throws RatingPeriodClosedException {
        eventRepository.rate(2L, 6, 1L, new Rating(((short) (5)), "Rocked"));
        eventRepository.rate(2L, 6, 2L, new Rating(((short) (4)), "Rocked"));
        Float rating = eventRepository.rate(2L, 6, 3L, new Rating(((short) (2)), "Rocked"));
        Assert.assertEquals(new Float(3.5), rating);
    }
}

