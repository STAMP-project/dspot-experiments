package com.nytimes.android.external.store;


import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class CharsetTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void charsetUtf8() {
        Charset charset = Charset.forName("UTF-8");
        assertThat(charset).isNotNull();
    }

    @Test
    public void shouldThrowExceptionWhenCreatingInvalidCharset() {
        expectedException.expect(UnsupportedCharsetException.class);
        Charset.forName("UTF-6");
    }
}

