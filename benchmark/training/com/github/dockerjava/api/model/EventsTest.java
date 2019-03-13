package com.github.dockerjava.api.model;


import RemoteApiVersion.VERSION_1_24;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dockerjava.test.serdes.JSONSamples;
import java.io.IOException;
import java.util.HashMap;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;


/**
 *
 *
 * @author Kanstantsin Shautsou
 */
public class EventsTest {
    @Test
    public void serderDocs1() throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        final JavaType type = mapper.getTypeFactory().constructType(Event.class);
        final Event event = JSONSamples.testRoundTrip(VERSION_1_24, "/events/docs1.json", type);
        MatcherAssert.assertThat(event, Matchers.notNullValue());
        MatcherAssert.assertThat(event.getType(), CoreMatchers.is(EventType.CONTAINER));
        MatcherAssert.assertThat(event.getAction(), CoreMatchers.is("create"));
        MatcherAssert.assertThat(event.getId(), CoreMatchers.is("ede54ee1afda366ab42f824e8a5ffd195155d853ceaec74a927f249ea270c743"));
        MatcherAssert.assertThat(event.getFrom(), CoreMatchers.is("alpine"));
        MatcherAssert.assertThat(event.getTime(), CoreMatchers.is(1461943101L));
        MatcherAssert.assertThat(event.getNode(), CoreMatchers.nullValue());
        MatcherAssert.assertThat(event.getTimeNano(), CoreMatchers.is(1461943101381709551L));
        final HashMap<String, String> attributes = new HashMap<>();
        attributes.put("com.example.some-label", "some-label-value");
        attributes.put("image", "alpine");
        attributes.put("name", "my-container");
        final EventActor actor = new EventActor().withId("ede54ee1afda366ab42f824e8a5ffd195155d853ceaec74a927f249ea270c743").withAttributes(attributes);
        final Event event1 = new Event().withType(EventType.CONTAINER).withStatus("create").withId("ede54ee1afda366ab42f824e8a5ffd195155d853ceaec74a927f249ea270c743").withFrom("alpine").withTime(1461943101L).withTimenano(1461943101381709551L).withAction("create").withEventActor(actor);
        MatcherAssert.assertThat(event1, CoreMatchers.equalTo(event));
    }
}

