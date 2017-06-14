

package com.twilio.base;


public class AmplReaderTest {
    @org.junit.Test
    public void testNoPagingDefaults() {
        com.twilio.base.Reader<com.twilio.rest.api.v2010.account.Call> reader = new com.twilio.rest.api.v2010.account.CallReader();
        org.junit.Assert.assertNull(reader.getLimit());
        org.junit.Assert.assertNull(reader.getPageSize());
    }

    @org.junit.Test
    public void testSetPageSize() {
        com.twilio.base.Reader<com.twilio.rest.api.v2010.account.Call> reader = new com.twilio.rest.api.v2010.account.CallReader().pageSize(100);
        org.junit.Assert.assertEquals(100, reader.getPageSize().intValue());
        org.junit.Assert.assertNull(reader.getLimit());
    }

    @org.junit.Test
    public void testMaxPageSize() {
        com.twilio.base.Reader<com.twilio.rest.api.v2010.account.Call> reader = new com.twilio.rest.api.v2010.account.CallReader().pageSize(java.lang.Integer.MAX_VALUE);
        org.junit.Assert.assertEquals(1000, reader.getPageSize().intValue());
        org.junit.Assert.assertNull(reader.getLimit());
    }

    @org.junit.Test
    public void testSetLimit() {
        com.twilio.base.Reader<com.twilio.rest.api.v2010.account.Call> reader = new com.twilio.rest.api.v2010.account.CallReader().limit(100);
        org.junit.Assert.assertEquals(100, reader.getLimit().intValue());
        org.junit.Assert.assertEquals(100, reader.getPageSize().intValue());
    }

    @org.junit.Test
    public void testSetLimitMaxPageSize() {
        com.twilio.base.Reader<com.twilio.rest.api.v2010.account.Call> reader = new com.twilio.rest.api.v2010.account.CallReader().limit(java.lang.Integer.MAX_VALUE);
        org.junit.Assert.assertEquals(java.lang.Integer.MAX_VALUE, reader.getLimit().intValue());
        org.junit.Assert.assertEquals(1000, reader.getPageSize().intValue());
    }

    @org.junit.Test
    public void testSetPageSizeLimit() {
        com.twilio.base.Reader<com.twilio.rest.api.v2010.account.Call> reader = new com.twilio.rest.api.v2010.account.CallReader().limit(1000).pageSize(5);
        org.junit.Assert.assertEquals(1000, reader.getLimit().intValue());
        org.junit.Assert.assertEquals(5, reader.getPageSize().intValue());
    }

    @org.junit.Test
    public void testNoPageLimit(@mockit.Mocked
    final com.twilio.http.TwilioRestClient client, @mockit.Mocked
    final com.twilio.base.Page<com.twilio.rest.api.v2010.account.Call> page) {
        new mockit.Expectations() {
            {
                page.getRecords();
                result = java.util.Collections.emptyList();
            }
        };
        com.twilio.base.Reader<com.twilio.rest.api.v2010.account.Call> reader = new com.twilio.rest.api.v2010.account.CallReader();
        com.twilio.base.ResourceSet<com.twilio.rest.api.v2010.account.Call> set = new com.twilio.base.ResourceSet<>(reader, client, page);
        org.junit.Assert.assertEquals(java.lang.Long.MAX_VALUE, set.getPageLimit());
    }

    @org.junit.Test
    public void testHasPageLimit(@mockit.Mocked
    final com.twilio.http.TwilioRestClient client, @mockit.Mocked
    final com.twilio.base.Page<com.twilio.rest.api.v2010.account.Call> page) {
        new mockit.Expectations() {
            {
                page.getRecords();
                result = java.util.Collections.emptyList();
                page.getPageSize();
                result = 50;
            }
        };
        com.twilio.base.Reader<com.twilio.rest.api.v2010.account.Call> reader = new com.twilio.rest.api.v2010.account.CallReader().limit(100);
        com.twilio.base.ResourceSet<com.twilio.rest.api.v2010.account.Call> set = new com.twilio.base.ResourceSet<>(reader, client, page);
        org.junit.Assert.assertEquals(2, set.getPageLimit());
    }

    @org.junit.Test
    public void testUnevenHasPageLimit(@mockit.Mocked
    final com.twilio.http.TwilioRestClient client, @mockit.Mocked
    final com.twilio.base.Page<com.twilio.rest.api.v2010.account.Call> page) {
        new mockit.Expectations() {
            {
                page.getRecords();
                result = java.util.Collections.emptyList();
                page.getPageSize();
                result = 50;
            }
        };
        com.twilio.base.Reader<com.twilio.rest.api.v2010.account.Call> reader = new com.twilio.rest.api.v2010.account.CallReader().limit(125);
        com.twilio.base.ResourceSet<com.twilio.rest.api.v2010.account.Call> set = new com.twilio.base.ResourceSet<>(reader, client, page);
        org.junit.Assert.assertEquals(3, set.getPageLimit());
    }
}

