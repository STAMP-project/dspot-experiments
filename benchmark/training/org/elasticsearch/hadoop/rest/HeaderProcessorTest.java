package org.elasticsearch.hadoop.rest;


import org.apache.commons.httpclient.Header;
import org.elasticsearch.hadoop.EsHadoopIllegalArgumentException;
import org.elasticsearch.hadoop.cfg.Settings;
import org.elasticsearch.hadoop.util.TestSettings;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class HeaderProcessorTest {
    @Test
    public void testApplyValidHeader() throws Exception {
        Settings settings = new TestSettings();
        settings.setProperty("es.net.http.header.Max-Forwards", "10");
        Header[] headers = HeaderProcessorTest.applyHeaders(settings);
        Assert.assertThat(headers, Matchers.arrayWithSize(3));
        Assert.assertThat(headers, Matchers.arrayContainingInAnyOrder(new Header("Accept", "application/json"), new Header("Content-Type", "application/json"), new Header("Max-Forwards", "10")));
    }

    @Test(expected = EsHadoopIllegalArgumentException.class)
    public void applyReservedHeader() throws Exception {
        Settings settings = new TestSettings();
        settings.setProperty("es.net.http.header.Content-Type", "application/x-ldjson");
        HeaderProcessorTest.applyHeaders(settings);
        Assert.fail("Should not execute since we tried to set a reserved header");
    }

    @Test(expected = EsHadoopIllegalArgumentException.class)
    public void applyEmptyHeaderName() throws Exception {
        Settings settings = new TestSettings();
        settings.setProperty("es.net.http.header.", "application/x-ldjson");
        HeaderProcessorTest.applyHeaders(settings);
        Assert.fail("Should not execute since we gave only the header prefix, and no header key");
    }

    @Test
    public void testApplyArrayValues() throws Exception {
        Settings settings = new TestSettings();
        settings.asProperties().put("es.net.http.header.Accept-Encoding", new Object[]{ "gzip", "deflate" });
        Header[] headers = HeaderProcessorTest.applyHeaders(settings);
        Assert.assertThat(headers, Matchers.arrayWithSize(3));
        Assert.assertThat(headers, Matchers.arrayContainingInAnyOrder(new Header("Accept", "application/json"), new Header("Content-Type", "application/json"), new Header("Accept-Encoding", "gzip,deflate")));
    }

    @Test
    public void testApplyMultiValues() throws Exception {
        Settings settings = new TestSettings();
        settings.setProperty("es.net.http.header.Accept-Encoding", "gzip,deflate");
        Header[] headers = HeaderProcessorTest.applyHeaders(settings);
        Assert.assertThat(headers, Matchers.arrayWithSize(3));
        Assert.assertThat(headers, Matchers.arrayContainingInAnyOrder(new Header("Accept", "application/json"), new Header("Content-Type", "application/json"), new Header("Accept-Encoding", "gzip,deflate")));
    }
}

