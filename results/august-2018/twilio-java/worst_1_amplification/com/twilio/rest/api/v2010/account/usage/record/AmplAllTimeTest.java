package com.twilio.rest.api.v2010.account.usage.record;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.twilio.Twilio;
import com.twilio.base.ResourceSet;
import com.twilio.exception.ApiConnectionException;
import com.twilio.http.Request;
import com.twilio.http.Response;
import com.twilio.http.TwilioRestClient;
import mockit.Expectations;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class AmplAllTimeTest {
    @Mocked
    private TwilioRestClient twilioRestClient;

    @Before
    public void setUp() throws Exception {
        Twilio.init("AC123", "AUTH TOKEN");
    }

    @Test(timeout = 10000)
    public void testReadFullResponse_rv5442() throws Exception {
        new NonStrictExpectations() {
            {
                Response __DSPOT_invoc_6 = twilioRestClient.request(((Request) (any)));
                result = new Response("{\"end\": 0,\"first_page_uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/AllTime?Page=0&PageSize=1\",\"last_page_uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/AllTime?Page=68&PageSize=1\",\"next_page_uri\": null,\"num_pages\": 69,\"page\": 0,\"page_size\": 1,\"previous_page_uri\": null,\"start\": 0,\"total\": 69,\"uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/AllTime\",\"usage_records\": [{\"account_sid\": \"ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa\",\"api_version\": \"2010-04-01\",\"category\": \"sms-inbound-shortcode\",\"count\": \"0\",\"count_unit\": \"messages\",\"description\": \"Short Code Inbound SMS\",\"end_date\": \"2015-09-04\",\"price\": \"0\",\"price_unit\": \"usd\",\"start_date\": \"2011-08-23\",\"subresource_uris\": {\"all_time\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/AllTime.json?Category=sms-inbound-shortcode\",\"daily\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Daily.json?Category=sms-inbound-shortcode\",\"last_month\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/LastMonth.json?Category=sms-inbound-shortcode\",\"monthly\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Monthly.json?Category=sms-inbound-shortcode\",\"this_month\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/ThisMonth.json?Category=sms-inbound-shortcode\",\"today\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Today.json?Category=sms-inbound-shortcode\",\"yearly\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Yearly.json?Category=sms-inbound-shortcode\",\"yesterday\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Yesterday.json?Category=sms-inbound-shortcode\"},\"uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/AllTime?Category=sms-inbound-shortcode&StartDate=2011-08-23&EndDate=2015-09-04\",\"usage\": \"0\",\"usage_unit\": \"messages\"}]}", TwilioRestClient.HTTP_STATUS_CODE_OK);
                twilioRestClient.getObjectMapper();
                result = new ObjectMapper();
                __DSPOT_invoc_6.getStatusCode();
            }
        };
        ResourceSet<AllTime> o_testReadFullResponse_rv5442__15 = AllTime.reader("ACXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX").read();
        Assert.assertEquals(1, ((int) (((ResourceSet) (o_testReadFullResponse_rv5442__15)).getPageSize())));
        Assert.assertNull(((ResourceSet) (o_testReadFullResponse_rv5442__15)).getLimit());
        Assert.assertTrue(((ResourceSet) (o_testReadFullResponse_rv5442__15)).isAutoPaging());
        Assert.assertEquals(9223372036854775807L, ((long) (((ResourceSet) (o_testReadFullResponse_rv5442__15)).getPageLimit())));
    }

    @Test(timeout = 10000)
    public void testReadFullResponselitString5425_failAssert90() throws Exception {
        try {
            new NonStrictExpectations() {
                {
                    twilioRestClient.request(((Request) (any)));
                    result = new Response(":", TwilioRestClient.HTTP_STATUS_CODE_OK);
                    twilioRestClient.getObjectMapper();
                    result = new ObjectMapper();
                }
            };
            AllTime.reader("ACXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX").read();
            org.junit.Assert.fail("testReadFullResponselitString5425 should have thrown ApiConnectionException");
        } catch (ApiConnectionException expected) {
            Assert.assertEquals("Unable to deserialize response: Unexpected character (\':\' (code 58)): expected a valid value (number, String, array, object, \'true\', \'false\' or \'null\')\n at [Source: :; line: 1, column: 2]\nJSON: :", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testReadFullResponse_rv5441() throws Exception {
        new NonStrictExpectations() {
            {
                Response __DSPOT_invoc_6 = twilioRestClient.request(((Request) (any)));
                result = new Response("{\"end\": 0,\"first_page_uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/AllTime?Page=0&PageSize=1\",\"last_page_uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/AllTime?Page=68&PageSize=1\",\"next_page_uri\": null,\"num_pages\": 69,\"page\": 0,\"page_size\": 1,\"previous_page_uri\": null,\"start\": 0,\"total\": 69,\"uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/AllTime\",\"usage_records\": [{\"account_sid\": \"ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa\",\"api_version\": \"2010-04-01\",\"category\": \"sms-inbound-shortcode\",\"count\": \"0\",\"count_unit\": \"messages\",\"description\": \"Short Code Inbound SMS\",\"end_date\": \"2015-09-04\",\"price\": \"0\",\"price_unit\": \"usd\",\"start_date\": \"2011-08-23\",\"subresource_uris\": {\"all_time\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/AllTime.json?Category=sms-inbound-shortcode\",\"daily\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Daily.json?Category=sms-inbound-shortcode\",\"last_month\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/LastMonth.json?Category=sms-inbound-shortcode\",\"monthly\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Monthly.json?Category=sms-inbound-shortcode\",\"this_month\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/ThisMonth.json?Category=sms-inbound-shortcode\",\"today\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Today.json?Category=sms-inbound-shortcode\",\"yearly\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Yearly.json?Category=sms-inbound-shortcode\",\"yesterday\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Yesterday.json?Category=sms-inbound-shortcode\"},\"uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/AllTime?Category=sms-inbound-shortcode&StartDate=2011-08-23&EndDate=2015-09-04\",\"usage\": \"0\",\"usage_unit\": \"messages\"}]}", TwilioRestClient.HTTP_STATUS_CODE_OK);
                twilioRestClient.getObjectMapper();
                result = new ObjectMapper();
                __DSPOT_invoc_6.getContent();
            }
        };
        ResourceSet<AllTime> o_testReadFullResponse_rv5441__15 = AllTime.reader("ACXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX").read();
        Assert.assertEquals(1, ((int) (((ResourceSet) (o_testReadFullResponse_rv5441__15)).getPageSize())));
        Assert.assertNull(((ResourceSet) (o_testReadFullResponse_rv5441__15)).getLimit());
        Assert.assertTrue(((ResourceSet) (o_testReadFullResponse_rv5441__15)).isAutoPaging());
        Assert.assertEquals(9223372036854775807L, ((long) (((ResourceSet) (o_testReadFullResponse_rv5441__15)).getPageLimit())));
    }

    @Test(timeout = 10000)
    public void testReadFullResponse_rv5442_mg6923() throws Exception {
        boolean __DSPOT_autoPaging_1357 = true;
        new NonStrictExpectations() {
            {
                Response __DSPOT_invoc_6 = twilioRestClient.request(((Request) (any)));
                result = new Response("{\"end\": 0,\"first_page_uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/AllTime?Page=0&PageSize=1\",\"last_page_uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/AllTime?Page=68&PageSize=1\",\"next_page_uri\": null,\"num_pages\": 69,\"page\": 0,\"page_size\": 1,\"previous_page_uri\": null,\"start\": 0,\"total\": 69,\"uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/AllTime\",\"usage_records\": [{\"account_sid\": \"ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa\",\"api_version\": \"2010-04-01\",\"category\": \"sms-inbound-shortcode\",\"count\": \"0\",\"count_unit\": \"messages\",\"description\": \"Short Code Inbound SMS\",\"end_date\": \"2015-09-04\",\"price\": \"0\",\"price_unit\": \"usd\",\"start_date\": \"2011-08-23\",\"subresource_uris\": {\"all_time\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/AllTime.json?Category=sms-inbound-shortcode\",\"daily\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Daily.json?Category=sms-inbound-shortcode\",\"last_month\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/LastMonth.json?Category=sms-inbound-shortcode\",\"monthly\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Monthly.json?Category=sms-inbound-shortcode\",\"this_month\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/ThisMonth.json?Category=sms-inbound-shortcode\",\"today\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Today.json?Category=sms-inbound-shortcode\",\"yearly\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Yearly.json?Category=sms-inbound-shortcode\",\"yesterday\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Yesterday.json?Category=sms-inbound-shortcode\"},\"uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/AllTime?Category=sms-inbound-shortcode&StartDate=2011-08-23&EndDate=2015-09-04\",\"usage\": \"0\",\"usage_unit\": \"messages\"}]}", TwilioRestClient.HTTP_STATUS_CODE_OK);
                twilioRestClient.getObjectMapper();
                result = new ObjectMapper();
                __DSPOT_invoc_6.getStatusCode();
            }
        };
        ResourceSet<AllTime> o_testReadFullResponse_rv5442__15 = AllTime.reader("ACXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX").read();
        ResourceSet o_testReadFullResponse_rv5442_mg6923__20 = o_testReadFullResponse_rv5442__15.setAutoPaging(__DSPOT_autoPaging_1357);
        Assert.assertTrue(((ResourceSet) (o_testReadFullResponse_rv5442_mg6923__20)).isAutoPaging());
        Assert.assertEquals(9223372036854775807L, ((long) (((ResourceSet) (o_testReadFullResponse_rv5442_mg6923__20)).getPageLimit())));
        Assert.assertNull(((ResourceSet) (o_testReadFullResponse_rv5442_mg6923__20)).getLimit());
        Assert.assertEquals(1, ((int) (((ResourceSet) (o_testReadFullResponse_rv5442_mg6923__20)).getPageSize())));
    }

    @Test(timeout = 10000)
    public void testReadFullResponse_rv5441litString5723_failAssert125() throws Exception {
        try {
            new NonStrictExpectations() {
                {
                    Response __DSPOT_invoc_6 = twilioRestClient.request(((Request) (any)));
                    result = new Response(":", TwilioRestClient.HTTP_STATUS_CODE_OK);
                    twilioRestClient.getObjectMapper();
                    result = new ObjectMapper();
                    __DSPOT_invoc_6.getContent();
                }
            };
            ResourceSet<AllTime> o_testReadFullResponse_rv5441__15 = AllTime.reader("ACXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX").read();
            org.junit.Assert.fail("testReadFullResponse_rv5441litString5723 should have thrown ApiConnectionException");
        } catch (ApiConnectionException expected) {
            Assert.assertEquals("Unable to deserialize response: Unexpected character (\':\' (code 58)): expected a valid value (number, String, array, object, \'true\', \'false\' or \'null\')\n at [Source: :; line: 1, column: 2]\nJSON: :", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testReadFullResponse_rv5442litString5707_failAssert124() throws Exception {
        try {
            new NonStrictExpectations() {
                {
                    Response __DSPOT_invoc_6 = twilioRestClient.request(((Request) (any)));
                    result = new Response(":", TwilioRestClient.HTTP_STATUS_CODE_OK);
                    twilioRestClient.getObjectMapper();
                    result = new ObjectMapper();
                    __DSPOT_invoc_6.getStatusCode();
                }
            };
            ResourceSet<AllTime> o_testReadFullResponse_rv5442__15 = AllTime.reader("ACXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX").read();
            org.junit.Assert.fail("testReadFullResponse_rv5442litString5707 should have thrown ApiConnectionException");
        } catch (ApiConnectionException expected) {
            Assert.assertEquals("Unable to deserialize response: Unexpected character (\':\' (code 58)): expected a valid value (number, String, array, object, \'true\', \'false\' or \'null\')\n at [Source: :; line: 1, column: 2]\nJSON: :", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testReadFullResponse_rv5442_add6657() throws Exception {
        new NonStrictExpectations() {
            {
                Response __DSPOT_invoc_6 = twilioRestClient.request(((Request) (any)));
                result = new Response("{\"end\": 0,\"first_page_uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/AllTime?Page=0&PageSize=1\",\"last_page_uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/AllTime?Page=68&PageSize=1\",\"next_page_uri\": null,\"num_pages\": 69,\"page\": 0,\"page_size\": 1,\"previous_page_uri\": null,\"start\": 0,\"total\": 69,\"uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/AllTime\",\"usage_records\": [{\"account_sid\": \"ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa\",\"api_version\": \"2010-04-01\",\"category\": \"sms-inbound-shortcode\",\"count\": \"0\",\"count_unit\": \"messages\",\"description\": \"Short Code Inbound SMS\",\"end_date\": \"2015-09-04\",\"price\": \"0\",\"price_unit\": \"usd\",\"start_date\": \"2011-08-23\",\"subresource_uris\": {\"all_time\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/AllTime.json?Category=sms-inbound-shortcode\",\"daily\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Daily.json?Category=sms-inbound-shortcode\",\"last_month\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/LastMonth.json?Category=sms-inbound-shortcode\",\"monthly\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Monthly.json?Category=sms-inbound-shortcode\",\"this_month\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/ThisMonth.json?Category=sms-inbound-shortcode\",\"today\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Today.json?Category=sms-inbound-shortcode\",\"yearly\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Yearly.json?Category=sms-inbound-shortcode\",\"yesterday\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Yesterday.json?Category=sms-inbound-shortcode\"},\"uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/AllTime?Category=sms-inbound-shortcode&StartDate=2011-08-23&EndDate=2015-09-04\",\"usage\": \"0\",\"usage_unit\": \"messages\"}]}", TwilioRestClient.HTTP_STATUS_CODE_OK);
                twilioRestClient.getObjectMapper();
                result = new ObjectMapper();
                __DSPOT_invoc_6.getStatusCode();
            }
        };
        ResourceSet<AllTime> o_testReadFullResponse_rv5442_add6657__15 = AllTime.reader("ACXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX").read();
        Assert.assertTrue(((ResourceSet) (o_testReadFullResponse_rv5442_add6657__15)).isAutoPaging());
        Assert.assertEquals(9223372036854775807L, ((long) (((ResourceSet) (o_testReadFullResponse_rv5442_add6657__15)).getPageLimit())));
        Assert.assertNull(((ResourceSet) (o_testReadFullResponse_rv5442_add6657__15)).getLimit());
        Assert.assertEquals(1, ((int) (((ResourceSet) (o_testReadFullResponse_rv5442_add6657__15)).getPageSize())));
        ResourceSet<AllTime> o_testReadFullResponse_rv5442__15 = AllTime.reader("ACXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX").read();
        Assert.assertTrue(((ResourceSet) (o_testReadFullResponse_rv5442_add6657__15)).isAutoPaging());
        Assert.assertEquals(9223372036854775807L, ((long) (((ResourceSet) (o_testReadFullResponse_rv5442_add6657__15)).getPageLimit())));
        Assert.assertNull(((ResourceSet) (o_testReadFullResponse_rv5442_add6657__15)).getLimit());
        Assert.assertEquals(1, ((int) (((ResourceSet) (o_testReadFullResponse_rv5442_add6657__15)).getPageSize())));
    }

    @Test(timeout = 10000)
    public void testReadFullResponse_rv5442_mg6923_mg10308() throws Exception {
        boolean __DSPOT_autoPaging_2492 = true;
        boolean __DSPOT_autoPaging_1357 = true;
        new NonStrictExpectations() {
            {
                Response __DSPOT_invoc_6 = twilioRestClient.request(((Request) (any)));
                result = new Response("{\"end\": 0,\"first_page_uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/AllTime?Page=0&PageSize=1\",\"last_page_uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/AllTime?Page=68&PageSize=1\",\"next_page_uri\": null,\"num_pages\": 69,\"page\": 0,\"page_size\": 1,\"previous_page_uri\": null,\"start\": 0,\"total\": 69,\"uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/AllTime\",\"usage_records\": [{\"account_sid\": \"ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa\",\"api_version\": \"2010-04-01\",\"category\": \"sms-inbound-shortcode\",\"count\": \"0\",\"count_unit\": \"messages\",\"description\": \"Short Code Inbound SMS\",\"end_date\": \"2015-09-04\",\"price\": \"0\",\"price_unit\": \"usd\",\"start_date\": \"2011-08-23\",\"subresource_uris\": {\"all_time\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/AllTime.json?Category=sms-inbound-shortcode\",\"daily\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Daily.json?Category=sms-inbound-shortcode\",\"last_month\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/LastMonth.json?Category=sms-inbound-shortcode\",\"monthly\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Monthly.json?Category=sms-inbound-shortcode\",\"this_month\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/ThisMonth.json?Category=sms-inbound-shortcode\",\"today\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Today.json?Category=sms-inbound-shortcode\",\"yearly\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Yearly.json?Category=sms-inbound-shortcode\",\"yesterday\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Yesterday.json?Category=sms-inbound-shortcode\"},\"uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/AllTime?Category=sms-inbound-shortcode&StartDate=2011-08-23&EndDate=2015-09-04\",\"usage\": \"0\",\"usage_unit\": \"messages\"}]}", TwilioRestClient.HTTP_STATUS_CODE_OK);
                twilioRestClient.getObjectMapper();
                result = new ObjectMapper();
                __DSPOT_invoc_6.getStatusCode();
            }
        };
        ResourceSet<AllTime> o_testReadFullResponse_rv5442__15 = AllTime.reader("ACXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX").read();
        ResourceSet o_testReadFullResponse_rv5442_mg6923__20 = o_testReadFullResponse_rv5442__15.setAutoPaging(__DSPOT_autoPaging_1357);
        ResourceSet o_testReadFullResponse_rv5442_mg6923_mg10308__24 = o_testReadFullResponse_rv5442__15.setAutoPaging(__DSPOT_autoPaging_2492);
        Assert.assertEquals(1, ((int) (((ResourceSet) (o_testReadFullResponse_rv5442_mg6923_mg10308__24)).getPageSize())));
        Assert.assertEquals(9223372036854775807L, ((long) (((ResourceSet) (o_testReadFullResponse_rv5442_mg6923_mg10308__24)).getPageLimit())));
        Assert.assertTrue(((ResourceSet) (o_testReadFullResponse_rv5442_mg6923_mg10308__24)).isAutoPaging());
        Assert.assertNull(((ResourceSet) (o_testReadFullResponse_rv5442_mg6923_mg10308__24)).getLimit());
    }

    @Test(timeout = 10000)
    public void testReadFullResponse_rv5442_mg6923litString9842_failAssert138() throws Exception {
        try {
            boolean __DSPOT_autoPaging_1357 = true;
            new NonStrictExpectations() {
                {
                    Response __DSPOT_invoc_6 = twilioRestClient.request(((Request) (any)));
                    result = new Response(":", TwilioRestClient.HTTP_STATUS_CODE_OK);
                    twilioRestClient.getObjectMapper();
                    result = new ObjectMapper();
                    __DSPOT_invoc_6.getStatusCode();
                }
            };
            ResourceSet<AllTime> o_testReadFullResponse_rv5442__15 = AllTime.reader("ACXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX").read();
            ResourceSet o_testReadFullResponse_rv5442_mg6923__20 = o_testReadFullResponse_rv5442__15.setAutoPaging(__DSPOT_autoPaging_1357);
            org.junit.Assert.fail("testReadFullResponse_rv5442_mg6923litString9842 should have thrown ApiConnectionException");
        } catch (ApiConnectionException expected) {
            Assert.assertEquals("Unable to deserialize response: Unexpected character (\':\' (code 58)): expected a valid value (number, String, array, object, \'true\', \'false\' or \'null\')\n at [Source: :; line: 1, column: 2]\nJSON: :", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testReadFullResponse_rv5442litString5707_failAssert124_add10275() throws Exception {
        try {
            new NonStrictExpectations() {
                {
                    Response __DSPOT_invoc_6 = AmplAllTimeTest.this.twilioRestClient.request(((Request) (this.any)));
                    this.result = new Response(":", TwilioRestClient.HTTP_STATUS_CODE_OK);
                    AmplAllTimeTest.this.twilioRestClient.getObjectMapper();
                    this.result = new ObjectMapper();
                    __DSPOT_invoc_6.getStatusCode();
                }
            };
            AllTimeReader o_testReadFullResponse_rv5442litString5707_failAssert124_add10275__17 = AllTime.reader("ACXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
            Assert.assertNull(((AllTimeReader) (o_testReadFullResponse_rv5442litString5707_failAssert124_add10275__17)).getPageSize());
            Assert.assertNull(((AllTimeReader) (o_testReadFullResponse_rv5442litString5707_failAssert124_add10275__17)).getLimit());
            ResourceSet<AllTime> o_testReadFullResponse_rv5442__15 = AllTime.reader("ACXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX").read();
            org.junit.Assert.fail("testReadFullResponse_rv5442litString5707 should have thrown ApiConnectionException");
        } catch (ApiConnectionException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testReadFullResponse_rv5441_mg6927_add10194() throws Exception {
        boolean __DSPOT_autoPaging_1360 = true;
        new NonStrictExpectations() {
            {
                Response __DSPOT_invoc_6 = twilioRestClient.request(((Request) (any)));
                result = new Response("{\"end\": 0,\"first_page_uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/AllTime?Page=0&PageSize=1\",\"last_page_uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/AllTime?Page=68&PageSize=1\",\"next_page_uri\": null,\"num_pages\": 69,\"page\": 0,\"page_size\": 1,\"previous_page_uri\": null,\"start\": 0,\"total\": 69,\"uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/AllTime\",\"usage_records\": [{\"account_sid\": \"ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa\",\"api_version\": \"2010-04-01\",\"category\": \"sms-inbound-shortcode\",\"count\": \"0\",\"count_unit\": \"messages\",\"description\": \"Short Code Inbound SMS\",\"end_date\": \"2015-09-04\",\"price\": \"0\",\"price_unit\": \"usd\",\"start_date\": \"2011-08-23\",\"subresource_uris\": {\"all_time\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/AllTime.json?Category=sms-inbound-shortcode\",\"daily\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Daily.json?Category=sms-inbound-shortcode\",\"last_month\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/LastMonth.json?Category=sms-inbound-shortcode\",\"monthly\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Monthly.json?Category=sms-inbound-shortcode\",\"this_month\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/ThisMonth.json?Category=sms-inbound-shortcode\",\"today\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Today.json?Category=sms-inbound-shortcode\",\"yearly\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Yearly.json?Category=sms-inbound-shortcode\",\"yesterday\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Yesterday.json?Category=sms-inbound-shortcode\"},\"uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/AllTime?Category=sms-inbound-shortcode&StartDate=2011-08-23&EndDate=2015-09-04\",\"usage\": \"0\",\"usage_unit\": \"messages\"}]}", TwilioRestClient.HTTP_STATUS_CODE_OK);
                twilioRestClient.getObjectMapper();
                result = new ObjectMapper();
                __DSPOT_invoc_6.getContent();
            }
        };
        ResourceSet<AllTime> o_testReadFullResponse_rv5441_mg6927_add10194__16 = AllTime.reader("ACXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX").read();
        Assert.assertEquals(1, ((int) (((ResourceSet) (o_testReadFullResponse_rv5441_mg6927_add10194__16)).getPageSize())));
        Assert.assertEquals(9223372036854775807L, ((long) (((ResourceSet) (o_testReadFullResponse_rv5441_mg6927_add10194__16)).getPageLimit())));
        Assert.assertTrue(((ResourceSet) (o_testReadFullResponse_rv5441_mg6927_add10194__16)).isAutoPaging());
        Assert.assertNull(((ResourceSet) (o_testReadFullResponse_rv5441_mg6927_add10194__16)).getLimit());
        ResourceSet<AllTime> o_testReadFullResponse_rv5441__15 = AllTime.reader("ACXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX").read();
        ResourceSet o_testReadFullResponse_rv5441_mg6927__20 = o_testReadFullResponse_rv5441__15.setAutoPaging(__DSPOT_autoPaging_1360);
        Assert.assertEquals(1, ((int) (((ResourceSet) (o_testReadFullResponse_rv5441_mg6927_add10194__16)).getPageSize())));
        Assert.assertEquals(9223372036854775807L, ((long) (((ResourceSet) (o_testReadFullResponse_rv5441_mg6927_add10194__16)).getPageLimit())));
        Assert.assertTrue(((ResourceSet) (o_testReadFullResponse_rv5441_mg6927_add10194__16)).isAutoPaging());
        Assert.assertNull(((ResourceSet) (o_testReadFullResponse_rv5441_mg6927_add10194__16)).getLimit());
    }

    @Test(timeout = 10000)
    public void testReadFullResponse_rv5441litString5723_failAssert125_add10281() throws Exception {
        try {
            new NonStrictExpectations() {
                {
                    Response __DSPOT_invoc_6 = AmplAllTimeTest.this.twilioRestClient.request(((Request) (this.any)));
                    this.result = new Response(":", TwilioRestClient.HTTP_STATUS_CODE_OK);
                    AmplAllTimeTest.this.twilioRestClient.getObjectMapper();
                    this.result = new ObjectMapper();
                    __DSPOT_invoc_6.getContent();
                }
            };
            AllTimeReader o_testReadFullResponse_rv5441litString5723_failAssert125_add10281__17 = AllTime.reader("ACXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
            Assert.assertNull(((AllTimeReader) (o_testReadFullResponse_rv5441litString5723_failAssert125_add10281__17)).getPageSize());
            Assert.assertNull(((AllTimeReader) (o_testReadFullResponse_rv5441litString5723_failAssert125_add10281__17)).getLimit());
            ResourceSet<AllTime> o_testReadFullResponse_rv5441__15 = AllTime.reader("ACXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX").read();
            org.junit.Assert.fail("testReadFullResponse_rv5441litString5723 should have thrown ApiConnectionException");
        } catch (ApiConnectionException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testReadEmptyResponselitString6_failAssert17() throws Exception {
        try {
            new NonStrictExpectations() {
                {
                    twilioRestClient.request(((Request) (any)));
                    result = new Response("", TwilioRestClient.HTTP_STATUS_CODE_OK);
                    twilioRestClient.getObjectMapper();
                    result = new ObjectMapper();
                }
            };
            AllTime.reader("ACXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX").read();
            org.junit.Assert.fail("testReadEmptyResponselitString6 should have thrown ApiConnectionException");
        } catch (ApiConnectionException expected) {
            Assert.assertEquals("Unable to deserialize response: No content to map due to end-of-input\n at [Source: ; line: 1, column: 0]\nJSON: ", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testReadEmptyResponse_rv26() throws Exception {
        new NonStrictExpectations() {
            {
                Response __DSPOT_invoc_6 = twilioRestClient.request(((Request) (any)));
                result = new Response("{\"end\": 0,\"first_page_uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/AllTime?Page=0&PageSize=1\",\"last_page_uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/AllTime?Page=68&PageSize=1\",\"next_page_uri\": null,\"num_pages\": 69,\"page\": 0,\"page_size\": 1,\"previous_page_uri\": null,\"start\": 0,\"total\": 69,\"uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/AllTime\",\"usage_records\": []}", TwilioRestClient.HTTP_STATUS_CODE_OK);
                twilioRestClient.getObjectMapper();
                result = new ObjectMapper();
                __DSPOT_invoc_6.getStatusCode();
            }
        };
        ResourceSet<AllTime> o_testReadEmptyResponse_rv26__15 = AllTime.reader("ACXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX").read();
        Assert.assertEquals(1, ((int) (((ResourceSet) (o_testReadEmptyResponse_rv26__15)).getPageSize())));
        Assert.assertNull(((ResourceSet) (o_testReadEmptyResponse_rv26__15)).getLimit());
        Assert.assertTrue(((ResourceSet) (o_testReadEmptyResponse_rv26__15)).isAutoPaging());
        Assert.assertEquals(9223372036854775807L, ((long) (((ResourceSet) (o_testReadEmptyResponse_rv26__15)).getPageLimit())));
    }

    @Test(timeout = 10000)
    public void testReadEmptyResponse_rv25() throws Exception {
        new NonStrictExpectations() {
            {
                Response __DSPOT_invoc_6 = twilioRestClient.request(((Request) (any)));
                result = new Response("{\"end\": 0,\"first_page_uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/AllTime?Page=0&PageSize=1\",\"last_page_uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/AllTime?Page=68&PageSize=1\",\"next_page_uri\": null,\"num_pages\": 69,\"page\": 0,\"page_size\": 1,\"previous_page_uri\": null,\"start\": 0,\"total\": 69,\"uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/AllTime\",\"usage_records\": []}", TwilioRestClient.HTTP_STATUS_CODE_OK);
                twilioRestClient.getObjectMapper();
                result = new ObjectMapper();
                __DSPOT_invoc_6.getContent();
            }
        };
        ResourceSet<AllTime> o_testReadEmptyResponse_rv25__15 = AllTime.reader("ACXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX").read();
        Assert.assertEquals(1, ((int) (((ResourceSet) (o_testReadEmptyResponse_rv25__15)).getPageSize())));
        Assert.assertNull(((ResourceSet) (o_testReadEmptyResponse_rv25__15)).getLimit());
        Assert.assertTrue(((ResourceSet) (o_testReadEmptyResponse_rv25__15)).isAutoPaging());
        Assert.assertEquals(9223372036854775807L, ((long) (((ResourceSet) (o_testReadEmptyResponse_rv25__15)).getPageLimit())));
    }

    @Test(timeout = 10000)
    public void testReadEmptyResponselitString7_failAssert15() throws Exception {
        try {
            new NonStrictExpectations() {
                {
                    twilioRestClient.request(((Request) (any)));
                    result = new Response("\n", TwilioRestClient.HTTP_STATUS_CODE_OK);
                    twilioRestClient.getObjectMapper();
                    result = new ObjectMapper();
                }
            };
            AllTime.reader("ACXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX").read();
            org.junit.Assert.fail("testReadEmptyResponselitString7 should have thrown ApiConnectionException");
        } catch (ApiConnectionException expected) {
            Assert.assertEquals("Unable to deserialize response: No content to map due to end-of-input\n at [Source: \n; line: 1, column: 0]\nJSON: \n", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testReadEmptyResponse_rv25_mg1487() throws Exception {
        boolean __DSPOT_autoPaging_33 = true;
        new NonStrictExpectations() {
            {
                Response __DSPOT_invoc_6 = twilioRestClient.request(((Request) (any)));
                result = new Response("{\"end\": 0,\"first_page_uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/AllTime?Page=0&PageSize=1\",\"last_page_uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/AllTime?Page=68&PageSize=1\",\"next_page_uri\": null,\"num_pages\": 69,\"page\": 0,\"page_size\": 1,\"previous_page_uri\": null,\"start\": 0,\"total\": 69,\"uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/AllTime\",\"usage_records\": []}", TwilioRestClient.HTTP_STATUS_CODE_OK);
                twilioRestClient.getObjectMapper();
                result = new ObjectMapper();
                __DSPOT_invoc_6.getContent();
            }
        };
        ResourceSet<AllTime> o_testReadEmptyResponse_rv25__15 = AllTime.reader("ACXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX").read();
        ResourceSet o_testReadEmptyResponse_rv25_mg1487__20 = o_testReadEmptyResponse_rv25__15.setAutoPaging(__DSPOT_autoPaging_33);
        Assert.assertNull(((ResourceSet) (o_testReadEmptyResponse_rv25_mg1487__20)).getLimit());
        Assert.assertTrue(((ResourceSet) (o_testReadEmptyResponse_rv25_mg1487__20)).isAutoPaging());
        Assert.assertEquals(1, ((int) (((ResourceSet) (o_testReadEmptyResponse_rv25_mg1487__20)).getPageSize())));
        Assert.assertEquals(9223372036854775807L, ((long) (((ResourceSet) (o_testReadEmptyResponse_rv25_mg1487__20)).getPageLimit())));
    }

    @Test(timeout = 10000)
    public void testReadEmptyResponse_rv26_mg1483() throws Exception {
        boolean __DSPOT_autoPaging_30 = false;
        new NonStrictExpectations() {
            {
                Response __DSPOT_invoc_6 = twilioRestClient.request(((Request) (any)));
                result = new Response("{\"end\": 0,\"first_page_uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/AllTime?Page=0&PageSize=1\",\"last_page_uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/AllTime?Page=68&PageSize=1\",\"next_page_uri\": null,\"num_pages\": 69,\"page\": 0,\"page_size\": 1,\"previous_page_uri\": null,\"start\": 0,\"total\": 69,\"uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/AllTime\",\"usage_records\": []}", TwilioRestClient.HTTP_STATUS_CODE_OK);
                twilioRestClient.getObjectMapper();
                result = new ObjectMapper();
                __DSPOT_invoc_6.getStatusCode();
            }
        };
        ResourceSet<AllTime> o_testReadEmptyResponse_rv26__15 = AllTime.reader("ACXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX").read();
        ResourceSet o_testReadEmptyResponse_rv26_mg1483__20 = o_testReadEmptyResponse_rv26__15.setAutoPaging(__DSPOT_autoPaging_30);
        Assert.assertNull(((ResourceSet) (o_testReadEmptyResponse_rv26_mg1483__20)).getLimit());
        Assert.assertFalse(((ResourceSet) (o_testReadEmptyResponse_rv26_mg1483__20)).isAutoPaging());
        Assert.assertEquals(1, ((int) (((ResourceSet) (o_testReadEmptyResponse_rv26_mg1483__20)).getPageSize())));
        Assert.assertEquals(9223372036854775807L, ((long) (((ResourceSet) (o_testReadEmptyResponse_rv26_mg1483__20)).getPageLimit())));
    }

    @Test(timeout = 10000)
    public void testReadEmptyResponse_rv25_mg1487litString4478_failAssert66() throws Exception {
        try {
            boolean __DSPOT_autoPaging_33 = true;
            new NonStrictExpectations() {
                {
                    Response __DSPOT_invoc_6 = twilioRestClient.request(((Request) (any)));
                    result = new Response(":", TwilioRestClient.HTTP_STATUS_CODE_OK);
                    twilioRestClient.getObjectMapper();
                    result = new ObjectMapper();
                    __DSPOT_invoc_6.getContent();
                }
            };
            ResourceSet<AllTime> o_testReadEmptyResponse_rv25__15 = AllTime.reader("ACXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX").read();
            ResourceSet o_testReadEmptyResponse_rv25_mg1487__20 = o_testReadEmptyResponse_rv25__15.setAutoPaging(__DSPOT_autoPaging_33);
            org.junit.Assert.fail("testReadEmptyResponse_rv25_mg1487litString4478 should have thrown ApiConnectionException");
        } catch (ApiConnectionException expected) {
            Assert.assertEquals("Unable to deserialize response: Unexpected character (\':\' (code 58)): expected a valid value (number, String, array, object, \'true\', \'false\' or \'null\')\n at [Source: :; line: 1, column: 2]\nJSON: :", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testReadEmptyResponse_rv25_mg1487_add4556() throws Exception {
        boolean __DSPOT_autoPaging_33 = true;
        new NonStrictExpectations() {
            {
                Response __DSPOT_invoc_6 = twilioRestClient.request(((Request) (any)));
                result = new Response("{\"end\": 0,\"first_page_uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/AllTime?Page=0&PageSize=1\",\"last_page_uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/AllTime?Page=68&PageSize=1\",\"next_page_uri\": null,\"num_pages\": 69,\"page\": 0,\"page_size\": 1,\"previous_page_uri\": null,\"start\": 0,\"total\": 69,\"uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/AllTime\",\"usage_records\": []}", TwilioRestClient.HTTP_STATUS_CODE_OK);
                twilioRestClient.getObjectMapper();
                result = new ObjectMapper();
                __DSPOT_invoc_6.getContent();
            }
        };
        AllTimeReader o_testReadEmptyResponse_rv25_mg1487_add4556__16 = AllTime.reader("ACXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
        Assert.assertNull(((AllTimeReader) (o_testReadEmptyResponse_rv25_mg1487_add4556__16)).getPageSize());
        Assert.assertNull(((AllTimeReader) (o_testReadEmptyResponse_rv25_mg1487_add4556__16)).getLimit());
        ResourceSet<AllTime> o_testReadEmptyResponse_rv25__15 = AllTime.reader("ACXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX").read();
        ResourceSet o_testReadEmptyResponse_rv25_mg1487__20 = o_testReadEmptyResponse_rv25__15.setAutoPaging(__DSPOT_autoPaging_33);
        Assert.assertNull(((AllTimeReader) (o_testReadEmptyResponse_rv25_mg1487_add4556__16)).getPageSize());
        Assert.assertNull(((AllTimeReader) (o_testReadEmptyResponse_rv25_mg1487_add4556__16)).getLimit());
    }

    @Test(timeout = 10000)
    public void testReadEmptyResponse_rv26_mg1483_add4545() throws Exception {
        boolean __DSPOT_autoPaging_30 = false;
        new NonStrictExpectations() {
            {
                Response __DSPOT_invoc_6 = twilioRestClient.request(((Request) (any)));
                result = new Response("{\"end\": 0,\"first_page_uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/AllTime?Page=0&PageSize=1\",\"last_page_uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/AllTime?Page=68&PageSize=1\",\"next_page_uri\": null,\"num_pages\": 69,\"page\": 0,\"page_size\": 1,\"previous_page_uri\": null,\"start\": 0,\"total\": 69,\"uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/AllTime\",\"usage_records\": []}", TwilioRestClient.HTTP_STATUS_CODE_OK);
                twilioRestClient.getObjectMapper();
                result = new ObjectMapper();
                __DSPOT_invoc_6.getStatusCode();
            }
        };
        ResourceSet<AllTime> o_testReadEmptyResponse_rv26_mg1483_add4545__16 = AllTime.reader("ACXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX").read();
        Assert.assertEquals(1, ((int) (((ResourceSet) (o_testReadEmptyResponse_rv26_mg1483_add4545__16)).getPageSize())));
        Assert.assertNull(((ResourceSet) (o_testReadEmptyResponse_rv26_mg1483_add4545__16)).getLimit());
        Assert.assertTrue(((ResourceSet) (o_testReadEmptyResponse_rv26_mg1483_add4545__16)).isAutoPaging());
        Assert.assertEquals(9223372036854775807L, ((long) (((ResourceSet) (o_testReadEmptyResponse_rv26_mg1483_add4545__16)).getPageLimit())));
        ResourceSet<AllTime> o_testReadEmptyResponse_rv26__15 = AllTime.reader("ACXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX").read();
        ResourceSet o_testReadEmptyResponse_rv26_mg1483__20 = o_testReadEmptyResponse_rv26__15.setAutoPaging(__DSPOT_autoPaging_30);
        Assert.assertEquals(1, ((int) (((ResourceSet) (o_testReadEmptyResponse_rv26_mg1483_add4545__16)).getPageSize())));
        Assert.assertNull(((ResourceSet) (o_testReadEmptyResponse_rv26_mg1483_add4545__16)).getLimit());
        Assert.assertTrue(((ResourceSet) (o_testReadEmptyResponse_rv26_mg1483_add4545__16)).isAutoPaging());
        Assert.assertEquals(9223372036854775807L, ((long) (((ResourceSet) (o_testReadEmptyResponse_rv26_mg1483_add4545__16)).getPageLimit())));
    }

    @Test(timeout = 10000)
    public void testReadEmptyResponse_rv25_mg1487_mg4603() throws Exception {
        boolean __DSPOT_autoPaging_1201 = true;
        boolean __DSPOT_autoPaging_33 = true;
        new NonStrictExpectations() {
            {
                Response __DSPOT_invoc_6 = twilioRestClient.request(((Request) (any)));
                result = new Response("{\"end\": 0,\"first_page_uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/AllTime?Page=0&PageSize=1\",\"last_page_uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/AllTime?Page=68&PageSize=1\",\"next_page_uri\": null,\"num_pages\": 69,\"page\": 0,\"page_size\": 1,\"previous_page_uri\": null,\"start\": 0,\"total\": 69,\"uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/AllTime\",\"usage_records\": []}", TwilioRestClient.HTTP_STATUS_CODE_OK);
                twilioRestClient.getObjectMapper();
                result = new ObjectMapper();
                __DSPOT_invoc_6.getContent();
            }
        };
        ResourceSet<AllTime> o_testReadEmptyResponse_rv25__15 = AllTime.reader("ACXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX").read();
        ResourceSet o_testReadEmptyResponse_rv25_mg1487__20 = o_testReadEmptyResponse_rv25__15.setAutoPaging(__DSPOT_autoPaging_33);
        ResourceSet o_testReadEmptyResponse_rv25_mg1487_mg4603__24 = o_testReadEmptyResponse_rv25_mg1487__20.setAutoPaging(__DSPOT_autoPaging_1201);
        Assert.assertEquals(1, ((int) (((ResourceSet) (o_testReadEmptyResponse_rv25_mg1487_mg4603__24)).getPageSize())));
        Assert.assertNull(((ResourceSet) (o_testReadEmptyResponse_rv25_mg1487_mg4603__24)).getLimit());
        Assert.assertTrue(((ResourceSet) (o_testReadEmptyResponse_rv25_mg1487_mg4603__24)).isAutoPaging());
        Assert.assertEquals(9223372036854775807L, ((long) (((ResourceSet) (o_testReadEmptyResponse_rv25_mg1487_mg4603__24)).getPageLimit())));
    }

    @Test(timeout = 10000)
    public void testReadEmptyResponse_rv26_mg1483_mg4591() throws Exception {
        boolean __DSPOT_autoPaging_1192 = false;
        boolean __DSPOT_autoPaging_30 = false;
        new NonStrictExpectations() {
            {
                Response __DSPOT_invoc_6 = twilioRestClient.request(((Request) (any)));
                result = new Response("{\"end\": 0,\"first_page_uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/AllTime?Page=0&PageSize=1\",\"last_page_uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/AllTime?Page=68&PageSize=1\",\"next_page_uri\": null,\"num_pages\": 69,\"page\": 0,\"page_size\": 1,\"previous_page_uri\": null,\"start\": 0,\"total\": 69,\"uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/AllTime\",\"usage_records\": []}", TwilioRestClient.HTTP_STATUS_CODE_OK);
                twilioRestClient.getObjectMapper();
                result = new ObjectMapper();
                __DSPOT_invoc_6.getStatusCode();
            }
        };
        ResourceSet<AllTime> o_testReadEmptyResponse_rv26__15 = AllTime.reader("ACXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX").read();
        ResourceSet o_testReadEmptyResponse_rv26_mg1483__20 = o_testReadEmptyResponse_rv26__15.setAutoPaging(__DSPOT_autoPaging_30);
        ResourceSet o_testReadEmptyResponse_rv26_mg1483_mg4591__24 = o_testReadEmptyResponse_rv26__15.setAutoPaging(__DSPOT_autoPaging_1192);
        Assert.assertEquals(1, ((int) (((ResourceSet) (o_testReadEmptyResponse_rv26_mg1483_mg4591__24)).getPageSize())));
        Assert.assertNull(((ResourceSet) (o_testReadEmptyResponse_rv26_mg1483_mg4591__24)).getLimit());
        Assert.assertFalse(((ResourceSet) (o_testReadEmptyResponse_rv26_mg1483_mg4591__24)).isAutoPaging());
        Assert.assertEquals(9223372036854775807L, ((long) (((ResourceSet) (o_testReadEmptyResponse_rv26_mg1483_mg4591__24)).getPageLimit())));
    }
}

