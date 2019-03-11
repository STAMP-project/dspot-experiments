package io.dropwizard.jersey.jackson;


import MediaType.APPLICATION_JSON;
import MediaType.APPLICATION_JSON_TYPE;
import io.dropwizard.jersey.AbstractJerseyTest;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import org.junit.jupiter.api.Test;


public class JsonProcessingExceptionMapperTest extends AbstractJerseyTest {
    @Test
    public void returnsA500ForNonDeserializableRepresentationClasses() throws Exception {
        Response response = target("/json/broken").request(APPLICATION_JSON).post(Entity.entity(new BrokenRepresentation(Collections.singletonList("whee")), APPLICATION_JSON));
        assertThat(response.getStatus()).isEqualTo(500);
        assertThat(response.getMediaType()).isEqualTo(APPLICATION_JSON_TYPE);
    }

    @Test
    public void returnsA500ForListNonDeserializableRepresentationClasses() throws Exception {
        final List<BrokenRepresentation> ent = Arrays.asList(new BrokenRepresentation(Collections.emptyList()), new BrokenRepresentation(Collections.singletonList("whoo")));
        Response response = target("/json/brokenList").request(APPLICATION_JSON).post(Entity.entity(ent, APPLICATION_JSON));
        assertThat(response.getStatus()).isEqualTo(500);
        assertThat(response.getMediaType()).isEqualTo(APPLICATION_JSON_TYPE);
    }

    @Test
    public void returnsA500ForNonSerializableRepresentationClassesOutbound() throws Exception {
        Response response = target("/json/brokenOutbound").request(APPLICATION_JSON).get();
        assertThat(response.getStatus()).isEqualTo(500);
        assertThat(response.getMediaType()).isEqualTo(APPLICATION_JSON_TYPE);
    }

    @Test
    public void returnsA500ForAbstractEntity() throws Exception {
        Response response = target("/json/interface").request(APPLICATION_JSON).post(Entity.entity("\"hello\"", APPLICATION_JSON));
        assertThat(response.getStatus()).isEqualTo(500);
        assertThat(response.getMediaType()).isEqualTo(APPLICATION_JSON_TYPE);
    }

    @Test
    public void returnsA500ForAbstractEntities() throws Exception {
        Response response = target("/json/interfaceList").request(APPLICATION_JSON).post(Entity.entity("[\"hello\"]", APPLICATION_JSON));
        assertThat(response.getStatus()).isEqualTo(500);
        assertThat(response.getMediaType()).isEqualTo(APPLICATION_JSON_TYPE);
    }

    @Test
    public void returnsA400ForCustomDeserializer() throws Exception {
        Response response = target("/json/custom").request(APPLICATION_JSON).post(Entity.entity("{}", APPLICATION_JSON));
        assertThat(response.getStatus()).isEqualTo(400);
        assertThat(response.getMediaType()).isEqualTo(APPLICATION_JSON_TYPE);
        assertThat(response.readEntity(String.class)).contains("Unable to process JSON");
    }

    @Test
    public void returnsA500ForCustomDeserializerUnexpected() throws Exception {
        Response response = target("/json/custom").request(APPLICATION_JSON).post(Entity.entity("\"SQL_INECTION\"", APPLICATION_JSON));
        assertThat(response.getStatus()).isEqualTo(500);
        assertThat(response.getMediaType()).isEqualTo(APPLICATION_JSON_TYPE);
        assertThat(response.readEntity(String.class)).contains("There was an error processing your request.");
    }

    @Test
    public void returnsA400ForMalformedInputCausingIoException() throws Exception {
        assertEndpointReturns400("url", "\"no-scheme.com\"");
    }

    @Test
    public void returnsA400ForListWrongInputType() throws Exception {
        assertEndpointReturns400("urlList", "\"no-scheme.com\"");
    }

    @Test
    public void returnsA400ForMalformedListInputCausingIoException() throws Exception {
        assertEndpointReturns400("urlList", "[\"no-scheme.com\"]");
    }

    @Test
    public void returnsA400ForWrongInputType() throws Exception {
        assertEndpointReturns400("ok", "false");
    }

    @Test
    public void returnsA400ForInvalidFormatRequestEntities() throws Exception {
        assertEndpointReturns400("ok", "{\"message\": \"a\", \"date\": \"2016-01-01\"}");
    }

    @Test
    public void returnsA400ForInvalidFormatRequestEntitiesWrapped() throws Exception {
        assertEndpointReturns400("ok", "{\"message\": \"1\", \"date\": \"a\"}");
    }

    @Test
    public void returnsA400ForInvalidFormatRequestEntitiesArray() throws Exception {
        assertEndpointReturns400("ok", "{\"message\": \"1\", \"date\": [1,1,1,1]}");
    }

    @Test
    public void returnsA400ForSemanticInvalidDate() throws Exception {
        assertEndpointReturns400("ok", "{\"message\": \"1\", \"date\": [-1,-1,-1]}");
    }
}

