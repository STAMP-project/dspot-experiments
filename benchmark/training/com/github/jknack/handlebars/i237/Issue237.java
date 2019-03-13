package com.github.jknack.handlebars.i237;


import com.github.jknack.handlebars.AbstractTest;
import java.io.IOException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;


public class Issue237 extends AbstractTest {
    public enum Status {

        NEW,
        DONE,
        CLOSED;}

    @Test
    public void workForEnumMap() throws IOException {
        Map<Issue237.Status, Integer> statuses = new EnumMap<>(Issue237.Status.class);
        statuses.put(Issue237.Status.NEW, 10);
        statuses.put(Issue237.Status.DONE, 20);
        statuses.put(Issue237.Status.CLOSED, 3);
        shouldCompileTo("{{statuses.NEW}}", AbstractTest.$("statuses", statuses), "10");
    }

    @Test
    public void wontWorkForNormalMap() throws IOException {
        Map<Issue237.Status, Integer> statuses = new HashMap<>();
        statuses.put(Issue237.Status.NEW, 10);
        statuses.put(Issue237.Status.DONE, 20);
        statuses.put(Issue237.Status.CLOSED, 3);
        shouldCompileTo("{{statuses.NEW}}", AbstractTest.$("statuses", statuses), "");
    }
}

