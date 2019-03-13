package io.dropwizard.jersey.jackson;


import MediaType.APPLICATION_JSON_TYPE;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.validation.Validated;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.annotation.Annotation;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.groups.Default;
import javax.ws.rs.WebApplicationException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


@SuppressWarnings("unchecked")
public class JacksonMessageBodyProviderTest {
    private static final Annotation[] NONE = new Annotation[0];

    public static class Example {
        @Min(0)
        @JsonProperty
        public int id;

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }

        @Override
        public boolean equals(Object obj) {
            if ((this) == obj) {
                return true;
            }
            if ((obj == null) || ((getClass()) != (obj.getClass()))) {
                return false;
            }
            final JacksonMessageBodyProviderTest.Example other = ((JacksonMessageBodyProviderTest.Example) (obj));
            return Objects.equals(this.id, other.id);
        }
    }

    public static class ListExample {
        @NotEmpty
        @Valid
        @JsonProperty
        public List<JacksonMessageBodyProviderTest.Example> examples = Collections.emptyList();
    }

    public interface Partial1 {}

    public interface Partial2 extends Default {}

    public static class PartialExample {
        @Min(value = 0, groups = JacksonMessageBodyProviderTest.Partial1.class)
        @JsonProperty
        public int id;

        @NotNull(groups = JacksonMessageBodyProviderTest.Partial2.class)
        @Nullable
        @JsonProperty
        public String text;
    }

    @JsonIgnoreType
    public static interface Ignorable {}

    @JsonIgnoreType(false)
    public static interface NonIgnorable extends JacksonMessageBodyProviderTest.Ignorable {}

    private final ObjectMapper mapper = Mockito.spy(Jackson.newObjectMapper());

    private final JacksonMessageBodyProvider provider = new JacksonMessageBodyProvider(mapper);

    @Test
    public void readsDeserializableTypes() throws Exception {
        assertThat(provider.isReadable(JacksonMessageBodyProviderTest.Example.class, null, null, null)).isTrue();
    }

    @Test
    public void writesSerializableTypes() throws Exception {
        assertThat(provider.isWriteable(JacksonMessageBodyProviderTest.Example.class, null, null, null)).isTrue();
    }

    @Test
    public void doesNotWriteIgnoredTypes() throws Exception {
        assertThat(provider.isWriteable(JacksonMessageBodyProviderTest.Ignorable.class, null, null, null)).isFalse();
    }

    @Test
    public void writesUnIgnoredTypes() throws Exception {
        assertThat(provider.isWriteable(JacksonMessageBodyProviderTest.NonIgnorable.class, null, null, null)).isTrue();
    }

    @Test
    public void doesNotReadIgnoredTypes() throws Exception {
        assertThat(provider.isReadable(JacksonMessageBodyProviderTest.Ignorable.class, null, null, null)).isFalse();
    }

    @Test
    public void readsUnIgnoredTypes() throws Exception {
        assertThat(provider.isReadable(JacksonMessageBodyProviderTest.NonIgnorable.class, null, null, null)).isTrue();
    }

    @Test
    public void isChunked() throws Exception {
        assertThat(provider.getSize(null, null, null, null, null)).isEqualTo((-1));
    }

    @Test
    public void deserializesRequestEntities() throws Exception {
        final ByteArrayInputStream entity = new ByteArrayInputStream("{\"id\":1}".getBytes(StandardCharsets.UTF_8));
        final Class<?> klass = JacksonMessageBodyProviderTest.Example.class;
        final Object obj = provider.readFrom(((Class<Object>) (klass)), JacksonMessageBodyProviderTest.Example.class, JacksonMessageBodyProviderTest.NONE, APPLICATION_JSON_TYPE, new javax.ws.rs.core.MultivaluedHashMap(), entity);
        assertThat(obj).isInstanceOf(JacksonMessageBodyProviderTest.Example.class);
        assertThat(((JacksonMessageBodyProviderTest.Example) (obj)).id).isEqualTo(1);
    }

    @Test
    public void returnsPartialValidatedRequestEntities() throws Exception {
        final Validated valid = Mockito.mock(Validated.class);
        Mockito.doReturn(Validated.class).when(valid).annotationType();
        Mockito.when(valid.value()).thenReturn(new Class<?>[]{ JacksonMessageBodyProviderTest.Partial1.class, JacksonMessageBodyProviderTest.Partial2.class });
        final ByteArrayInputStream entity = new ByteArrayInputStream("{\"id\":1,\"text\":\"hello Cemo\"}".getBytes(StandardCharsets.UTF_8));
        final Class<?> klass = JacksonMessageBodyProviderTest.PartialExample.class;
        final Object obj = provider.readFrom(((Class<Object>) (klass)), JacksonMessageBodyProviderTest.PartialExample.class, new Annotation[]{ valid }, APPLICATION_JSON_TYPE, new javax.ws.rs.core.MultivaluedHashMap(), entity);
        assertThat(obj).isInstanceOf(JacksonMessageBodyProviderTest.PartialExample.class);
        assertThat(((JacksonMessageBodyProviderTest.PartialExample) (obj)).id).isEqualTo(1);
    }

    @Test
    public void returnsPartialValidatedByGroupRequestEntities() throws Exception {
        final Validated valid = Mockito.mock(Validated.class);
        Mockito.doReturn(Validated.class).when(valid).annotationType();
        Mockito.when(valid.value()).thenReturn(new Class<?>[]{ JacksonMessageBodyProviderTest.Partial1.class });
        final ByteArrayInputStream entity = new ByteArrayInputStream("{\"id\":1}".getBytes(StandardCharsets.UTF_8));
        final Class<?> klass = JacksonMessageBodyProviderTest.PartialExample.class;
        final Object obj = provider.readFrom(((Class<Object>) (klass)), JacksonMessageBodyProviderTest.PartialExample.class, new Annotation[]{ valid }, APPLICATION_JSON_TYPE, new javax.ws.rs.core.MultivaluedHashMap(), entity);
        assertThat(obj).isInstanceOf(JacksonMessageBodyProviderTest.PartialExample.class);
        assertThat(((JacksonMessageBodyProviderTest.PartialExample) (obj)).id).isEqualTo(1);
    }

    @Test
    public void throwsAJsonProcessingExceptionForMalformedRequestEntities() throws Exception {
        final ByteArrayInputStream entity = new ByteArrayInputStream("{\"id\":-1d".getBytes(StandardCharsets.UTF_8));
        try {
            final Class<?> klass = JacksonMessageBodyProviderTest.Example.class;
            provider.readFrom(((Class<Object>) (klass)), JacksonMessageBodyProviderTest.Example.class, JacksonMessageBodyProviderTest.NONE, APPLICATION_JSON_TYPE, new javax.ws.rs.core.MultivaluedHashMap(), entity);
            failBecauseExceptionWasNotThrown(WebApplicationException.class);
        } catch (JsonProcessingException e) {
            assertThat(e.getMessage()).startsWith(("Unexpected character ('d' (code 100)): " + "was expecting comma to separate Object entries\n"));
        }
    }

    @Test
    public void serializesResponseEntities() throws Exception {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        final JacksonMessageBodyProviderTest.Example example = new JacksonMessageBodyProviderTest.Example();
        example.id = 500;
        provider.writeTo(example, JacksonMessageBodyProviderTest.Example.class, JacksonMessageBodyProviderTest.Example.class, JacksonMessageBodyProviderTest.NONE, APPLICATION_JSON_TYPE, new javax.ws.rs.core.MultivaluedHashMap(), output);
        assertThat(output.toString()).isEqualTo("{\"id\":500}");
    }

    @Test
    public void returnsValidatedCollectionRequestEntities() throws Exception {
        testValidatedCollectionType(Collection.class, new TypeReference<Collection<JacksonMessageBodyProviderTest.Example>>() {}.getType());
    }

    @Test
    public void returnsValidatedSetRequestEntities() throws Exception {
        testValidatedCollectionType(Set.class, new TypeReference<Set<JacksonMessageBodyProviderTest.Example>>() {}.getType());
    }

    @Test
    public void returnsValidatedListRequestEntities() throws Exception {
        testValidatedCollectionType(List.class, new TypeReference<List<JacksonMessageBodyProviderTest.Example>>() {}.getType());
    }
}

