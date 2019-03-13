package com.baeldung.crunch;


import org.apache.crunch.Emitter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class TokenizerUnitTest {
    @Mock
    private Emitter<String> emitter;

    @Test
    public void givenTokenizer_whenLineProcessed_thenOnlyExpectedWordsEmitted() {
        Tokenizer splitter = new Tokenizer();
        splitter.process("  hello  world ", emitter);
        Mockito.verify(emitter).emit("hello");
        Mockito.verify(emitter).emit("world");
        Mockito.verifyNoMoreInteractions(emitter);
    }
}

