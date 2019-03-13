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
package com.springsource.greenhouse.events.load;


import com.springsource.greenhouse.events.Event;
import com.springsource.greenhouse.events.EventRepository;
import com.springsource.greenhouse.events.Venue;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.joda.time.tz.CachedDateTimeZone;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;


public class JdbcEventLoaderRepositoryTest {
    private EmbeddedDatabase db;

    private JdbcTemplate jdbcTemplate;

    private EventRepository eventRepository;

    private EventLoaderRepository eventLoaderRepository;

    @Test
    public void loadEventData() {
        long eventId = eventLoaderRepository.loadEvent(new EventData(1, "Test Event", "Test Event Description", "test", "2012-10-15T00:00:00", "2012-10-18T23:59:59", "America/New_York", "NFJS", 297), new VenueData("Some Fancy Hotel", "1234 North Street, Chicago, IL 60605", 41.89001, (-87.677765), "It's in Illinois"));
        Assert.assertEquals(1L, eventId);
        Event event = eventRepository.findEventBySlug("s2gx", 2012, 10, "test");
        Assert.assertEquals(1L, event.getId().longValue());
        Assert.assertEquals("Test Event", event.getTitle());
        Assert.assertEquals("test", event.getSlug());
        Assert.assertEquals("Test Event Description", event.getDescription());
        // assertEquals(new DateTime(2012, 10, 15, 1, 0, 0, 0, event.getTimeZone()), event.getStartTime().withZone(event.getTimeZone()));
        // assertEquals(new DateTime(2012, 10, 19, 0, 59, 59, 0, event.getTimeZone()), event.getEndTime().withZone(event.getTimeZone()));
        Assert.assertEquals(CachedDateTimeZone.forID("America/New_York"), event.getTimeZone());
        Set<Venue> venues = event.getVenues();
        Assert.assertEquals(1, venues.size());
        Venue venue = new ArrayList<Venue>(venues).get(0);
        Assert.assertEquals("Some Fancy Hotel", venue.getName());
        Assert.assertEquals("1234 North Street, Chicago, IL 60605", venue.getPostalAddress());
        Assert.assertEquals(41.89001, venue.getLocation().getLatitude().doubleValue(), 1.0E-12);
        Assert.assertEquals((-87.677765), venue.getLocation().getLongitude().doubleValue(), 1.0E-12);
        Assert.assertEquals("It's in Illinois", venue.getLocationHint());
    }

    @Test
    public void updateEventData() {
        long eventId = eventLoaderRepository.loadEvent(new EventData(1, "Test Event", "Test Event Description", "test", "2012-10-15T00:00:00", "2012-10-18T23:59:59", "America/New_York", "NFJS", 297), new VenueData("Some Fancy Hotel", "1234 North Street, Chicago, IL 60605", 41.89001, (-87.677765), "It's in Illinois"));
        Assert.assertEquals(1L, eventId);
        Event event = eventRepository.findEventBySlug("s2gx", 2012, 10, "test");
        Assert.assertEquals("Test Event", event.getTitle());
        Set<Venue> venues = event.getVenues();
        Assert.assertEquals(1, venues.size());
        Venue venue = new ArrayList<Venue>(venues).get(0);
        Assert.assertEquals("Some Fancy Hotel", venue.getName());
        long updatedEventId = eventLoaderRepository.loadEvent(new EventData(1, "Updated Event", "Test Event Description", "test", "2012-10-15T00:00:00", "2012-10-18T23:59:59", "America/New_York", "NFJS", 297), new VenueData("Some Conference Hall", "1234 North Street, Chicago, IL 60605", 41.89001, (-87.677765), "It's in Illinois"));
        Assert.assertEquals(1L, updatedEventId);
        Event updatedEvent = eventRepository.findEventBySlug("s2gx", 2012, 10, "test");
        Assert.assertEquals("Updated Event", updatedEvent.getTitle());
        Set<Venue> updatedVenues = updatedEvent.getVenues();
        Assert.assertEquals(1, updatedVenues.size());
        Venue updatedVenue = new ArrayList<Venue>(updatedVenues).get(0);
        Assert.assertEquals("Some Conference Hall", updatedVenue.getName());
    }

    @Test
    public void loadLeaderData() throws SQLException {
        long leaderId = eventLoaderRepository.loadLeader(new LeaderData("Craig Walls", "Craig is the Spring Social project lead", "http://www.habuma.com", "habuma", "NFJS", 1234));
        Assert.assertEquals(1L, leaderId);
        jdbcTemplate.queryForObject("select id, name, bio, personalUrl, twitterUsername from Leader where id=?", new org.springframework.jdbc.core.RowMapper<ResultSet>() {
            public ResultSet mapRow(ResultSet rs, int rowNum) throws SQLException {
                Assert.assertEquals(1L, rs.getLong("id"));
                Assert.assertEquals("Craig Walls", rs.getString("name"));
                Assert.assertEquals("Craig is the Spring Social project lead", rs.getString("bio"));
                Assert.assertEquals("http://www.habuma.com", rs.getString("personalUrl"));
                Assert.assertEquals("habuma", rs.getString("twitterUsername"));
                return null;
            }
        }, leaderId);
        jdbcTemplate.queryForObject("select leader, sourceId, source from ExternalLeader where leader=?", new org.springframework.jdbc.core.RowMapper<ResultSet>() {
            public ResultSet mapRow(ResultSet rs, int rowNum) throws SQLException {
                Assert.assertEquals(1L, rs.getLong("leader"));
                Assert.assertEquals(1234L, rs.getLong("sourceId"));
                Assert.assertEquals("NFJS", rs.getString("source"));
                return null;
            }
        }, leaderId);
    }

    @Test
    public void loadTimeSlot() throws SQLException {
        long eventId = eventLoaderRepository.loadEvent(new EventData(1, "Test Event", "Test Event Description", "test", "2012-10-15T00:00:00", "2012-10-18T23:59:59", "America/New_York", "NFJS", 297), new VenueData("Some Fancy Hotel", "1234 North Street, Chicago, IL 60605", 41.89001, (-87.677765), "It's in Illinois"));
        long timeSlotId = eventLoaderRepository.loadTimeSlot(new TimeSlotData(eventId, "Time Slot 1", "2012-10-15T00:00:00", "2012-10-15T01:30:00", "NFJS", 6296));
        Assert.assertEquals(1L, timeSlotId);
        jdbcTemplate.queryForObject("select id, event, label, startTime, endTime from EventTimeSlot where id=?", new org.springframework.jdbc.core.RowMapper<ResultSet>() {
            public ResultSet mapRow(ResultSet rs, int rowNum) throws SQLException {
                Assert.assertEquals(1L, rs.getLong("id"));
                Assert.assertEquals(1L, rs.getLong("event"));
                Assert.assertEquals("Time Slot 1", rs.getString("label"));
                Timestamp startTime = rs.getTimestamp("startTime");
                Timestamp endTime = rs.getTimestamp("endTime");
                // assertEquals(new DateTime(2012, 10, 15, 0, 0, 0, 0, DateTimeZone.getDefault()).getMillis(), startTime.getTime());
                // assertEquals(new DateTime(2012, 10, 15, 1, 30, 0, 0, DateTimeZone.getDefault()).getMillis(), endTime.getTime());
                return null;
            }
        }, timeSlotId);
    }

    @Test
    public void updateTimeSlot() throws SQLException {
        long eventId = eventLoaderRepository.loadEvent(new EventData(1, "Test Event", "Test Event Description", "test", "2012-10-15T00:00:00", "2012-10-18T23:59:59", "America/New_York", "NFJS", 297), new VenueData("Some Fancy Hotel", "1234 North Street, Chicago, IL 60605", 41.89001, (-87.677765), "It's in Illinois"));
        long timeSlotId = eventLoaderRepository.loadTimeSlot(new TimeSlotData(eventId, "Time Slot 1", "2012-10-15T00:00:00", "2012-10-18T23:59:59", "NFJS", 6296));
        Assert.assertEquals(1L, timeSlotId);
        jdbcTemplate.queryForObject("select id, event, label, startTime, endTime from EventTimeSlot where id=?", new org.springframework.jdbc.core.RowMapper<ResultSet>() {
            public ResultSet mapRow(ResultSet rs, int rowNum) throws SQLException {
                Assert.assertEquals(1L, rs.getLong("id"));
                Assert.assertEquals(1L, rs.getLong("event"));
                Timestamp startTime = rs.getTimestamp("startTime");
                Timestamp endTime = rs.getTimestamp("endTime");
                // assertEquals(new DateTime(2012, 10, 15, 0, 0, 0, 0, DateTimeZone.getDefault()).getMillis(), startTime.getTime());
                // assertEquals(new DateTime(2012, 10, 18, 23, 59, 59, 0, DateTimeZone.getDefault()).getMillis(), endTime.getTime());
                return null;
            }
        }, timeSlotId);
        long updatedTimeSlotId = eventLoaderRepository.loadTimeSlot(new TimeSlotData(eventId, "Time Slot One", "2012-10-15T01:00:00", "2012-10-15T02:30:00", "NFJS", 6296));
        Assert.assertEquals(1L, updatedTimeSlotId);
        jdbcTemplate.queryForObject("select id, event, label, startTime, endTime from EventTimeSlot where id=?", new org.springframework.jdbc.core.RowMapper<ResultSet>() {
            public ResultSet mapRow(ResultSet rs, int rowNum) throws SQLException {
                Assert.assertEquals(1L, rs.getLong("id"));
                Assert.assertEquals(1L, rs.getLong("event"));
                Assert.assertEquals("Time Slot One", rs.getString("label"));
                Timestamp startTime = rs.getTimestamp("startTime");
                Timestamp endTime = rs.getTimestamp("endTime");
                // assertEquals(new DateTime(2012, 10, 15, 1, 0, 0, 0, DateTimeZone.getDefault()).getMillis(), startTime.getTime());
                // assertEquals(new DateTime(2012, 10, 15, 2, 30, 0, 0, DateTimeZone.getDefault()).getMillis(), endTime.getTime());
                return null;
            }
        }, updatedTimeSlotId);
    }

    @Test
    public void loadEventSession() throws SQLException {
        long eventId = eventLoaderRepository.loadEvent(new EventData(1, "Test Event", "Test Event Description", "test", "2012-10-15T00:00:00", "2012-10-18T23:59:59", "America/New_York", "NFJS", 297), new VenueData("Some Fancy Hotel", "1234 North Street, Chicago, IL 60605", 41.89001, (-87.677765), "It's in Illinois"));
        long timeSlotId = eventLoaderRepository.loadTimeSlot(new TimeSlotData(eventId, "Time Slot 1", "2012-10-15T00:00:00", "2012-10-18T23:59:59", "NFJS", 6296));
        int eventSessionId = 1;
        List<Long> leaderIds = Collections.emptyList();
        eventLoaderRepository.loadEventSession(new EventSessionData(eventId, eventSessionId, "What's new in Spring", "Come find out what's new in Spring", "#newspring", 1L, timeSlotId, "NFJS", 24409L, leaderIds));
        jdbcTemplate.queryForObject("select event, id, title, description, hashtag, venue, timeslot from EventSession where event=? and id=?", new org.springframework.jdbc.core.RowMapper<ResultSet>() {
            public ResultSet mapRow(ResultSet rs, int rowNum) throws SQLException {
                Assert.assertEquals(1L, rs.getLong("event"));
                Assert.assertEquals(1L, rs.getLong("id"));
                Assert.assertEquals("What's new in Spring", rs.getString("title"));
                Assert.assertEquals("Come find out what's new in Spring", rs.getString("description"));
                Assert.assertEquals("#newspring", rs.getString("hashtag"));
                Assert.assertEquals(1, rs.getLong("venue"));
                Assert.assertEquals(1, rs.getLong("timeslot"));
                return null;
            }
        }, eventId, eventSessionId);
    }

    @Test
    public void updateEventSession() throws SQLException {
        long eventId = eventLoaderRepository.loadEvent(new EventData(1, "Test Event", "Test Event Description", "test", "2012-10-15T00:00:00", "2012-10-18T23:59:59", "America/New_York", "NFJS", 297), new VenueData("Some Fancy Hotel", "1234 North Street, Chicago, IL 60605", 41.89001, (-87.677765), "It's in Illinois"));
        long timeSlotId = eventLoaderRepository.loadTimeSlot(new TimeSlotData(eventId, "Time Slot 1", "2012-10-15T00:00:00", "2012-10-18T23:59:59", "NFJS", 6296));
        int eventSessionId = 1;
        List<Long> leaderIds = Collections.emptyList();
        eventLoaderRepository.loadEventSession(new EventSessionData(eventId, eventSessionId, "What's new in Spring", "Come find out what's new in Spring", "#newspring", 1L, timeSlotId, "NFJS", 24409L, leaderIds));
        jdbcTemplate.queryForObject("select event, id, title, description, hashtag, venue, timeslot from EventSession where event=? and id=?", new org.springframework.jdbc.core.RowMapper<ResultSet>() {
            public ResultSet mapRow(ResultSet rs, int rowNum) throws SQLException {
                Assert.assertEquals(1L, rs.getLong("event"));
                Assert.assertEquals(1L, rs.getLong("id"));
                Assert.assertEquals("What's new in Spring", rs.getString("title"));
                Assert.assertEquals("Come find out what's new in Spring", rs.getString("description"));
                Assert.assertEquals("#newspring", rs.getString("hashtag"));
                Assert.assertEquals(1, rs.getLong("venue"));
                Assert.assertEquals(1, rs.getLong("timeslot"));
                return null;
            }
        }, eventId, eventSessionId);
        eventLoaderRepository.loadEventSession(new EventSessionData(eventId, eventSessionId, "What's new in Spring?", "Juergen gives the dish on the latest in Spring", "#spring3", 1L, timeSlotId, "NFJS", 24409L, leaderIds));
        jdbcTemplate.queryForObject("select event, id, title, description, hashtag, venue, timeslot from EventSession where event=? and id=?", new org.springframework.jdbc.core.RowMapper<ResultSet>() {
            public ResultSet mapRow(ResultSet rs, int rowNum) throws SQLException {
                Assert.assertEquals(1L, rs.getLong("event"));
                Assert.assertEquals(1L, rs.getLong("id"));
                Assert.assertEquals("What's new in Spring?", rs.getString("title"));
                Assert.assertEquals("Juergen gives the dish on the latest in Spring", rs.getString("description"));
                Assert.assertEquals("#spring3", rs.getString("hashtag"));
                Assert.assertEquals(1, rs.getLong("venue"));
                Assert.assertEquals(1, rs.getLong("timeslot"));
                return null;
            }
        }, eventId, eventSessionId);
    }
}

