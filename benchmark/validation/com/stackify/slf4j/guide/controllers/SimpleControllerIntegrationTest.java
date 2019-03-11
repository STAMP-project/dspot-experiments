package com.stackify.slf4j.guide.controllers;


import Level.DEBUG;
import Level.ERROR;
import Level.INFO;
import java.util.Collections;
import org.apache.logging.log4j.junit.LoggerContextRule;
import org.apache.logging.log4j.test.appender.ListAppender;
import org.assertj.core.api.SoftAssertions;
import org.junit.ClassRule;
import org.junit.Test;


public class SimpleControllerIntegrationTest {
    private static ListAppender appender;

    private SimpleController controller = new SimpleController();

    @ClassRule
    public static LoggerContextRule init = new LoggerContextRule("log4j2-test.xml");

    @Test
    public void whenSimpleRequestMade_thenAllRegularMessagesLogged() {
        String output = controller.processList(Collections.emptyList());
        SoftAssertions errorCollector = new SoftAssertions();
        errorCollector.assertThat(SimpleControllerIntegrationTest.appender.getEvents()).haveAtLeastOne(eventContains("Client requested process the following list: []", INFO)).haveAtLeastOne(eventContains("Starting process", DEBUG)).haveAtLeastOne(eventContains("Finished processing", INFO)).haveExactly(0, eventOfLevel(ERROR));
        errorCollector.assertThat(output).isEqualTo("done");
        errorCollector.assertAll();
    }

    @Test
    public void givenClientId_whenMDCRequestMade_thenMessagesWithClientIdLogged() throws Exception {
        String clientId = "id-1234";
        String output = controller.clientMDCRequest(clientId);
        SoftAssertions errorCollector = new SoftAssertions();
        errorCollector.assertThat(SimpleControllerIntegrationTest.appender.getEvents()).allMatch(( entry) -> {
            return clientId.equals(entry.getContextData().getValue("clientId"));
        }).haveAtLeastOne(eventContains("Client id-1234 has made a request", INFO)).haveAtLeastOne(eventContains("Starting request", INFO)).haveAtLeastOne(eventContains("Finished request", INFO));
        errorCollector.assertThat(output).isEqualTo("finished");
        errorCollector.assertAll();
    }
}

