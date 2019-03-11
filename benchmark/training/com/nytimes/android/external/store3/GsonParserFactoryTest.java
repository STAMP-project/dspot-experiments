package com.nytimes.android.external.store3;


import com.google.gson.Gson;
import com.nytimes.android.external.store3.middleware.GsonParserFactory;
import java.lang.reflect.Type;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;


public class GsonParserFactoryTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    Type type;

    private final Gson gson = new Gson();

    @Test
    public void shouldCreateParsersProperly() {
        GsonParserFactory.createReaderParser(gson, type);
        GsonParserFactory.createSourceParser(gson, type);
        GsonParserFactory.createStringParser(gson, type);
    }

    @Test
    public void shouldThrowExceptionWhenCreatingReaderWithNullType() {
        expectedException.expect(NullPointerException.class);
        GsonParserFactory.createReaderParser(gson, null);
    }

    @Test
    public void shouldThrowExceptionWhenCreatingReaderWithNullGson() {
        expectedException.expect(NullPointerException.class);
        GsonParserFactory.createReaderParser(null, type);
    }

    @Test
    public void shouldThrowExceptionWhenCreatingSourceWithNullType() {
        expectedException.expect(NullPointerException.class);
        GsonParserFactory.createSourceParser(gson, null);
    }

    @Test
    public void shouldThrowExceptionWhenCreatingSourceWithNullGson() {
        expectedException.expect(NullPointerException.class);
        GsonParserFactory.createSourceParser(null, type);
    }

    @Test
    public void shouldThrowExceptionWhenCreatingStringWithNullType() {
        expectedException.expect(NullPointerException.class);
        GsonParserFactory.createStringParser(gson, null);
    }

    @Test
    public void shouldThrowExceptionWhenCreatingStringWithNullGson() {
        expectedException.expect(NullPointerException.class);
        GsonParserFactory.createStringParser(null, type);
    }
}

