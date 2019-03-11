package com.codahale.metrics.servlets;


import HttpHeader.CACHE_CONTROL;
import HttpHeader.CONTENT_TYPE;
import org.junit.Test;


public class CpuProfileServletTest extends AbstractServletTest {
    @Test
    public void returns200OK() {
        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    public void returnsPprofRaw() {
        assertThat(response.get(CONTENT_TYPE)).isEqualTo("pprof/raw");
    }

    @Test
    public void returnsUncacheable() {
        assertThat(response.get(CACHE_CONTROL)).isEqualTo("must-revalidate,no-cache,no-store");
    }
}

