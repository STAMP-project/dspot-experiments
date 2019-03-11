package com.baeldung.antlr;


import LogLevel.ERROR;
import com.baeldung.antlr.log.LogListener;
import com.baeldung.antlr.log.model.LogEntry;
import java.time.LocalDateTime;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public class LogParserUnitTest {
    @Test
    public void whenLogContainsOneErrorLogEntry_thenOneErrorIsReturned() throws Exception {
        String logLines = "2018-May-05 14:20:21 DEBUG entering awesome method\r\n" + "2018-May-05 14:20:24 ERROR Bad thing happened\r\n";
        LogLexer serverLogLexer = new LogLexer(CharStreams.fromString(logLines));
        CommonTokenStream tokens = new CommonTokenStream(serverLogLexer);
        LogParser logParser = new LogParser(tokens);
        ParseTreeWalker walker = new ParseTreeWalker();
        LogListener logWalker = new LogListener();
        walker.walk(logWalker, logParser.log());
        MatcherAssert.assertThat(logWalker.getEntries().size(), CoreMatchers.is(2));
        LogEntry error = logWalker.getEntries().get(1);
        MatcherAssert.assertThat(error.getLevel(), CoreMatchers.is(ERROR));
        MatcherAssert.assertThat(error.getMessage(), CoreMatchers.is("Bad thing happened"));
        MatcherAssert.assertThat(error.getTimestamp(), CoreMatchers.is(LocalDateTime.of(2018, 5, 5, 14, 20, 24)));
    }
}

