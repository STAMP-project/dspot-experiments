package com.nytimes.android.external.store3.base.impl;


import com.nytimes.android.external.store3.base.Parser;
import com.nytimes.android.external.store3.util.KeyParser;
import com.nytimes.android.external.store3.util.ParserException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class MultiParserTest {
    private static final Parser<Integer, String> PARSER_1 = String::valueOf;

    private static final Parser<String, BarCode> PARSER_2 = ( value) -> new BarCode(value, "KEY");

    private static final Parser<BarCode, UUID> PARSER_3 = ( barCode) -> UUID.randomUUID();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldParseChainProperly() {
        List<KeyParser> parsersChain = new ArrayList<>();
        parsersChain.add(new com.nytimes.android.external.store3.util.NoKeyParser(MultiParserTest.PARSER_1));
        parsersChain.add(new com.nytimes.android.external.store3.util.NoKeyParser(MultiParserTest.PARSER_2));
        parsersChain.add(new com.nytimes.android.external.store3.util.NoKeyParser(MultiParserTest.PARSER_3));
        KeyParser<Object, Integer, UUID> parser = new MultiParser(parsersChain);
        UUID parsed = parser.apply(new Object(), 100);
        Assert.assertNotNull(parsed);
    }

    @Test
    public void shouldFailIfOneOfParsersIsInvalid() {
        expectedException.expect(ParserException.class);
        List<KeyParser> parsersChain = new ArrayList<>();
        parsersChain.add(new com.nytimes.android.external.store3.util.NoKeyParser(MultiParserTest.PARSER_1));
        parsersChain.add(new com.nytimes.android.external.store3.util.NoKeyParser(MultiParserTest.PARSER_3));
        parsersChain.add(new com.nytimes.android.external.store3.util.NoKeyParser(MultiParserTest.PARSER_2));
        KeyParser<Object, Integer, UUID> parser = new MultiParser(parsersChain);
        UUID parsed = parser.apply(new Object(), 100);
        Assert.assertNotNull(parsed);
    }
}

