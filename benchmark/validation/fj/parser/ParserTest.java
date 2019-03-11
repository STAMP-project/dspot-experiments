package fj.parser;


import Parser.CharsParser;
import fj.data.Stream;
import fj.data.Validation;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


public class ParserTest {
    @Test
    public void testParserFail() {
        final Parser<String, String, Exception> fail = Parser.fail(new ParserTest.ParseException());
        Assert.assertThat(fail.parse("").fail(), Is.is(new ParserTest.ParseException()));
    }

    @Test
    public void testParserValue() {
        final Parser<String, String, Exception> p = Parser.parser(( s) -> s.isEmpty() ? Validation.fail(new fj.parser.ParseException()) : Validation.success(result(s.substring(1), s.substring(0, 1))));
        final Result<String, String> r = p.parse("abc").success();
        Assert.assertThat(r.value(), Is.is("a"));
        Assert.assertThat(r.rest(), Is.is("bc"));
    }

    @Test
    public void testParserBind() {
        final Parser<String, String, Exception> p = Parser.value("a");
        final Parser<String, String, Exception> fail = Parser.fail(new ParserTest.ParseException());
        Assert.assertThat(p.bind(( o) -> fail).parse("aaaa").fail(), Is.is(new ParserTest.ParseException()));
    }

    @Test
    public void testParserStream() {
        Stream<Character> s = Stream.fromString("abc");
        Result<Stream<Character>, Character> r = CharsParser.character('a').parse(s).success();
        Assert.assertThat(r, Is.is(Result.result(Stream.fromString("bc"), 'a')));
    }

    class ParseException extends Exception {
        @Override
        public boolean equals(Object obj) {
            return obj instanceof ParserTest.ParseException;
        }
    }
}

