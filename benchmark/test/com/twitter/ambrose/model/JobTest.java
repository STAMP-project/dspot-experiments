package com.twitter.ambrose.model;


import com.google.common.collect.Maps;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import org.junit.Test;


/**
 * Unit tests for {@link com.twitter.ambrose.model.JobTest}.
 */
public class JobTest {
    @Test
    public void testRoundTrip() throws IOException {
        Properties properties = new Properties();
        properties.setProperty("someprop", "propvalue");
        Map<String, Number> metrics = Maps.newHashMap();
        metrics.put("somemetric", 6);
        Job job = new Job("scope-123", properties, metrics);
        testRoundTrip(job);
    }

    @Test
    public void testPolymorphism() throws IOException {
        Job job;
        job = new ExtendedJob1();
        job.setId("extendedjob-1");
        ((ExtendedJob1) (job)).setAliases(new String[]{ "a1", "a2" });
        testRoundTrip(job);
        job = new ExtendedJob2();
        job.setId("extendedjob-2");
        ((ExtendedJob2) (job)).setFeatures(new String[]{ "f1", "f2" });
        testRoundTrip(job);
        // check if you can still deserialize ExtendedJob1
        job = new ExtendedJob1();
        job.setId("extendedjob-3");
        ((ExtendedJob1) (job)).setAliases(new String[]{ "a1", "a2" });
        testRoundTrip(job);
    }
}

