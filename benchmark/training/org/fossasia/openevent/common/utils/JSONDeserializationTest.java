package org.fossasia.openevent.common.utils;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import junit.framework.Assert;
import org.fossasia.openevent.data.Event;
import org.fossasia.openevent.data.Microlocation;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.data.SessionType;
import org.fossasia.openevent.data.Speaker;
import org.fossasia.openevent.data.Sponsor;
import org.fossasia.openevent.data.Track;
import org.junit.Test;


public class JSONDeserializationTest {
    private ObjectMapper objectMapper;

    @Test
    public void testEventModelDeserialization() throws IOException {
        Event event = objectMapper.readValue(readFile("event"), Event.class);
        Assert.assertNotNull(event);
        Assert.assertEquals(6, event.getId());
        Assert.assertTrue(event.getIsSessionsSpeakersEnabled());
        Assert.assertEquals(9, event.getSocialLinks().size());
    }

    @Test
    public void testTrackModelDeserialization() throws IOException {
        List<Track> tracks = objectMapper.readValue(readFile("tracks"), objectMapper.getTypeFactory().constructCollectionType(List.class, Track.class));
        Assert.assertNotNull(tracks);
        Assert.assertEquals(23, tracks.size());
        Assert.assertEquals(3, tracks.get(0).getSessions().size());
    }

    @Test
    public void testMicrolocationModelDeserialization() throws IOException {
        List<Microlocation> microlocations = objectMapper.readValue(readFile("microlocations"), objectMapper.getTypeFactory().constructCollectionType(List.class, Microlocation.class));
        Assert.assertNotNull(microlocations);
        Assert.assertEquals(15, microlocations.size());
    }

    @Test
    public void testSponsorModelDeserialization() throws IOException {
        List<Track> sponsors = objectMapper.readValue(readFile("sponsors"), objectMapper.getTypeFactory().constructCollectionType(List.class, Sponsor.class));
        Assert.assertNotNull(sponsors);
        Assert.assertEquals(17, sponsors.size());
    }

    @Test
    public void testSessionModelDeserialization() throws IOException {
        List<Session> sessions = objectMapper.readValue(readFile("sessions"), objectMapper.getTypeFactory().constructCollectionType(List.class, Session.class));
        Assert.assertNotNull(sessions);
        Assert.assertEquals(228, sessions.size());
        Assert.assertEquals(1, sessions.get(0).getSpeakers().size());
        Assert.assertFalse(sessions.get(0).getIsMailSent());
    }

    @Test
    public void testSessionTypeModelDeserialization() throws IOException {
        List<SessionType> sessionTypes = objectMapper.readValue(readFile("session_types"), objectMapper.getTypeFactory().constructCollectionType(List.class, SessionType.class));
        Assert.assertNotNull(sessionTypes);
        Assert.assertEquals(32, sessionTypes.size());
    }

    @Test
    public void testSpeakerModelDeserialization() throws IOException {
        List<Speaker> speakers = objectMapper.readValue(readFile("speakers"), objectMapper.getTypeFactory().constructCollectionType(List.class, Speaker.class));
        Assert.assertNotNull(speakers);
        Assert.assertEquals(207, speakers.size());
        Assert.assertFalse(speakers.get(0).getIsFeatured());
        Assert.assertEquals(1, speakers.get(0).getSessions().size());
    }

    @Test
    public void testLocalJsonDeserialization() throws IOException {
        // Event Deserialization
        Assert.assertTrue(doModelDeserialization(Event.class, "event", false));
        // Microlocations Deserialization
        Assert.assertTrue(doModelDeserialization(Microlocation.class, "microlocations", true));
        // Sponsor Deserialization
        Assert.assertTrue(doModelDeserialization(Sponsor.class, "sponsors", true));
        // Track Deserialization
        Assert.assertTrue(doModelDeserialization(Track.class, "tracks", true));
        // SessionType Deserialization
        Assert.assertTrue(doModelDeserialization(SessionType.class, "session_types", true));
        // Session Deserialization
        Assert.assertTrue(doModelDeserialization(Session.class, "sessions", true));
        // Speakers Deserialization
        Assert.assertTrue(doModelDeserialization(Speaker.class, "speakers", true));
    }
}

