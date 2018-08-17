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


public class AmplDailyTest {
    @Mocked
    private TwilioRestClient twilioRestClient;

    @Before
    public void setUp() throws Exception {
        Twilio.init("AC123", "AUTH TOKEN");
    }

    @Test(timeout = 10000)
    public void testReadFullResponselitString5316_failAssert89() throws Exception {
        try {
            new NonStrictExpectations() {
                {
                    twilioRestClient.request(((Request) (any)));
                    result = new Response("{\"end\": 0,\"first_page_uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Daily?Page=0&PageSize=1\",\"last_page_uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Daily?Page=101843&PageSize=1\",\"next_page_uri\": null,\"num_pages\": 101844,\"page\": 0,\"page_size\": 1,\"previous_page_uri\": null,\"start\": 0,\"total\": 101844,\"uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Daily\",\"usage_records\": [{\"account_sid\": \"ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa\",\"api_version\": \"2010-04-01\",\"category\": \"sms-inbound-shortcode\",\"count\": \"0\",\"count_unit\": \"messages\",\"description: \"Short Code Inbound SMS\",\"end_date\": \"2015-09-06\",\"price\": \"0\",\"price_unit\": \"usd\",\"start_date\": \"2015-09-06\",\"subresource_uris\": {\"all_time\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/AllTime.json?Category=sms-inbound-shortcode\",\"daily\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Daily.json?Category=sms-inbound-shortcode\",\"last_month\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/LastMonth.json?Category=sms-inbound-shortcode\",\"monthly\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Monthly.json?Category=sms-inbound-shortcode\",\"this_month\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/ThisMonth.json?Category=sms-inbound-shortcode\",\"today\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Today.json?Category=sms-inbound-shortcode\",\"yearly\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Yearly.json?Category=sms-inbound-shortcode\",\"yesterday\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Yesterday.json?Category=sms-inbound-shortcode\"},\"uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Daily?Category=sms-inbound-shortcode&StartDate=2015-09-06&EndDate=2015-09-06\",\"usage\": \"0\",\"usage_unit\": \"messages\"}]}", TwilioRestClient.HTTP_STATUS_CODE_OK);
                    twilioRestClient.getObjectMapper();
                    result = new ObjectMapper();
                }
            };
            Daily.reader("ACXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX").read();
            org.junit.Assert.fail("testReadFullResponselitString5316 should have thrown ApiConnectionException");
        } catch (ApiConnectionException expected) {
            Assert.assertEquals("Unable to deserialize response: Unexpected character (\'S\' (code 83)): was expecting a colon to separate field name and value\n at [Source: {\"end\": 0,\"first_page_uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Daily?Page=0&PageSize=1\",\"last_page_uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Daily?Page=101843&PageSize=1\",\"next_page_uri\": null,\"num_pages\": 101844,\"page\": 0,\"page_size\": 1,\"previous_page_uri\": null,\"start\": 0,\"total\": 101844,\"uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Daily\",\"usage_records\": [{\"account_sid\": \"ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa\",\"api_version\": \"2010-04-01\",\"category\": \"sms-inbound-shortcode\",\"count\": \"0\",\"count_unit\": \"messages\",\"description: \"Short Code Inbound SMS\",\"end_date\": \"2015-09-06\",\"price\": \"0\",\"price_unit\": \"usd\",\"start_date\": \"2015-09-06\",\"subresource_uris\": {\"all_time\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/AllTime.json?Category=sms-inbound-shortcode\",\"daily\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Daily.json?Category=sms-inbound-shortcode\",\"last_month\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/LastMonth.json?Category=sms-inbound-shortcode\",\"monthly\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Monthly.json?Category=sms-inbound-shortcode\",\"this_month\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/ThisMonth.json?Category=sms-inbound-shortcode\",\"today\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Today.json?Category=sms-inbound-shortcode\",\"yearly\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Yearly.json?Category=sms-inbound-shortcode\",\"yesterday\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Yesterday.json?Category=sms-inbound-shortcode\"},\"uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Daily?Category=sms-inbound-shortcode&StartDate=2015-09-06&EndDate=2015-09-06\",\"usage\": \"0\",\"usage_unit\": \"messages\"}]}; line: 1, column: 637]\nJSON: {\"end\": 0,\"first_page_uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Daily?Page=0&PageSize=1\",\"last_page_uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Daily?Page=101843&PageSize=1\",\"next_page_uri\": null,\"num_pages\": 101844,\"page\": 0,\"page_size\": 1,\"previous_page_uri\": null,\"start\": 0,\"total\": 101844,\"uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Daily\",\"usage_records\": [{\"account_sid\": \"ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa\",\"api_version\": \"2010-04-01\",\"category\": \"sms-inbound-shortcode\",\"count\": \"0\",\"count_unit\": \"messages\",\"description: \"Short Code Inbound SMS\",\"end_date\": \"2015-09-06\",\"price\": \"0\",\"price_unit\": \"usd\",\"start_date\": \"2015-09-06\",\"subresource_uris\": {\"all_time\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/AllTime.json?Category=sms-inbound-shortcode\",\"daily\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Daily.json?Category=sms-inbound-shortcode\",\"last_month\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/LastMonth.json?Category=sms-inbound-shortcode\",\"monthly\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Monthly.json?Category=sms-inbound-shortcode\",\"this_month\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/ThisMonth.json?Category=sms-inbound-shortcode\",\"today\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Today.json?Category=sms-inbound-shortcode\",\"yearly\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Yearly.json?Category=sms-inbound-shortcode\",\"yesterday\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Yesterday.json?Category=sms-inbound-shortcode\"},\"uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Daily?Category=sms-inbound-shortcode&StartDate=2015-09-06&EndDate=2015-09-06\",\"usage\": \"0\",\"usage_unit\": \"messages\"}]}", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testReadFullResponselitString5313_failAssert90() throws Exception {
        try {
            new NonStrictExpectations() {
                {
                    twilioRestClient.request(((Request) (any)));
                    result = new Response("", TwilioRestClient.HTTP_STATUS_CODE_OK);
                    twilioRestClient.getObjectMapper();
                    result = new ObjectMapper();
                }
            };
            Daily.reader("ACXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX").read();
            org.junit.Assert.fail("testReadFullResponselitString5313 should have thrown ApiConnectionException");
        } catch (ApiConnectionException expected) {
            Assert.assertEquals("Unable to deserialize response: No content to map due to end-of-input\n at [Source: ; line: 1, column: 0]\nJSON: ", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testReadFullResponse_rv5337() throws Exception {
        new NonStrictExpectations() {
            {
                Response __DSPOT_invoc_6 = twilioRestClient.request(((Request) (any)));
                result = new Response("{\"end\": 0,\"first_page_uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Daily?Page=0&PageSize=1\",\"last_page_uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Daily?Page=101843&PageSize=1\",\"next_page_uri\": null,\"num_pages\": 101844,\"page\": 0,\"page_size\": 1,\"previous_page_uri\": null,\"start\": 0,\"total\": 101844,\"uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Daily\",\"usage_records\": [{\"account_sid\": \"ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa\",\"api_version\": \"2010-04-01\",\"category\": \"sms-inbound-shortcode\",\"count\": \"0\",\"count_unit\": \"messages\",\"description\": \"Short Code Inbound SMS\",\"end_date\": \"2015-09-06\",\"price\": \"0\",\"price_unit\": \"usd\",\"start_date\": \"2015-09-06\",\"subresource_uris\": {\"all_time\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/AllTime.json?Category=sms-inbound-shortcode\",\"daily\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Daily.json?Category=sms-inbound-shortcode\",\"last_month\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/LastMonth.json?Category=sms-inbound-shortcode\",\"monthly\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Monthly.json?Category=sms-inbound-shortcode\",\"this_month\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/ThisMonth.json?Category=sms-inbound-shortcode\",\"today\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Today.json?Category=sms-inbound-shortcode\",\"yearly\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Yearly.json?Category=sms-inbound-shortcode\",\"yesterday\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Yesterday.json?Category=sms-inbound-shortcode\"},\"uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Daily?Category=sms-inbound-shortcode&StartDate=2015-09-06&EndDate=2015-09-06\",\"usage\": \"0\",\"usage_unit\": \"messages\"}]}", TwilioRestClient.HTTP_STATUS_CODE_OK);
                twilioRestClient.getObjectMapper();
                result = new ObjectMapper();
                __DSPOT_invoc_6.getStatusCode();
            }
        };
        ResourceSet<Daily> o_testReadFullResponse_rv5337__15 = Daily.reader("ACXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX").read();
        Assert.assertEquals(1, ((int) (((ResourceSet) (o_testReadFullResponse_rv5337__15)).getPageSize())));
        Assert.assertNull(((ResourceSet) (o_testReadFullResponse_rv5337__15)).getLimit());
        Assert.assertTrue(((ResourceSet) (o_testReadFullResponse_rv5337__15)).isAutoPaging());
        Assert.assertEquals(9223372036854775807L, ((long) (((ResourceSet) (o_testReadFullResponse_rv5337__15)).getPageLimit())));
    }

    @Test(timeout = 10000)
    public void testReadFullResponse_rv5336() throws Exception {
        new NonStrictExpectations() {
            {
                Response __DSPOT_invoc_6 = twilioRestClient.request(((Request) (any)));
                result = new Response("{\"end\": 0,\"first_page_uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Daily?Page=0&PageSize=1\",\"last_page_uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Daily?Page=101843&PageSize=1\",\"next_page_uri\": null,\"num_pages\": 101844,\"page\": 0,\"page_size\": 1,\"previous_page_uri\": null,\"start\": 0,\"total\": 101844,\"uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Daily\",\"usage_records\": [{\"account_sid\": \"ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa\",\"api_version\": \"2010-04-01\",\"category\": \"sms-inbound-shortcode\",\"count\": \"0\",\"count_unit\": \"messages\",\"description\": \"Short Code Inbound SMS\",\"end_date\": \"2015-09-06\",\"price\": \"0\",\"price_unit\": \"usd\",\"start_date\": \"2015-09-06\",\"subresource_uris\": {\"all_time\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/AllTime.json?Category=sms-inbound-shortcode\",\"daily\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Daily.json?Category=sms-inbound-shortcode\",\"last_month\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/LastMonth.json?Category=sms-inbound-shortcode\",\"monthly\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Monthly.json?Category=sms-inbound-shortcode\",\"this_month\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/ThisMonth.json?Category=sms-inbound-shortcode\",\"today\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Today.json?Category=sms-inbound-shortcode\",\"yearly\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Yearly.json?Category=sms-inbound-shortcode\",\"yesterday\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Yesterday.json?Category=sms-inbound-shortcode\"},\"uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Daily?Category=sms-inbound-shortcode&StartDate=2015-09-06&EndDate=2015-09-06\",\"usage\": \"0\",\"usage_unit\": \"messages\"}]}", TwilioRestClient.HTTP_STATUS_CODE_OK);
                twilioRestClient.getObjectMapper();
                result = new ObjectMapper();
                __DSPOT_invoc_6.getContent();
            }
        };
        ResourceSet<Daily> o_testReadFullResponse_rv5336__15 = Daily.reader("ACXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX").read();
        Assert.assertEquals(1, ((int) (((ResourceSet) (o_testReadFullResponse_rv5336__15)).getPageSize())));
        Assert.assertNull(((ResourceSet) (o_testReadFullResponse_rv5336__15)).getLimit());
        Assert.assertTrue(((ResourceSet) (o_testReadFullResponse_rv5336__15)).isAutoPaging());
        Assert.assertEquals(9223372036854775807L, ((long) (((ResourceSet) (o_testReadFullResponse_rv5336__15)).getPageLimit())));
    }

    @Test(timeout = 10000)
    public void testReadFullResponse_rv5336_mg6854() throws Exception {
        boolean __DSPOT_autoPaging_1349 = true;
        new NonStrictExpectations() {
            {
                Response __DSPOT_invoc_6 = twilioRestClient.request(((Request) (any)));
                result = new Response("{\"end\": 0,\"first_page_uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Daily?Page=0&PageSize=1\",\"last_page_uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Daily?Page=101843&PageSize=1\",\"next_page_uri\": null,\"num_pages\": 101844,\"page\": 0,\"page_size\": 1,\"previous_page_uri\": null,\"start\": 0,\"total\": 101844,\"uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Daily\",\"usage_records\": [{\"account_sid\": \"ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa\",\"api_version\": \"2010-04-01\",\"category\": \"sms-inbound-shortcode\",\"count\": \"0\",\"count_unit\": \"messages\",\"description\": \"Short Code Inbound SMS\",\"end_date\": \"2015-09-06\",\"price\": \"0\",\"price_unit\": \"usd\",\"start_date\": \"2015-09-06\",\"subresource_uris\": {\"all_time\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/AllTime.json?Category=sms-inbound-shortcode\",\"daily\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Daily.json?Category=sms-inbound-shortcode\",\"last_month\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/LastMonth.json?Category=sms-inbound-shortcode\",\"monthly\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Monthly.json?Category=sms-inbound-shortcode\",\"this_month\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/ThisMonth.json?Category=sms-inbound-shortcode\",\"today\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Today.json?Category=sms-inbound-shortcode\",\"yearly\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Yearly.json?Category=sms-inbound-shortcode\",\"yesterday\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Yesterday.json?Category=sms-inbound-shortcode\"},\"uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Daily?Category=sms-inbound-shortcode&StartDate=2015-09-06&EndDate=2015-09-06\",\"usage\": \"0\",\"usage_unit\": \"messages\"}]}", TwilioRestClient.HTTP_STATUS_CODE_OK);
                twilioRestClient.getObjectMapper();
                result = new ObjectMapper();
                __DSPOT_invoc_6.getContent();
            }
        };
        ResourceSet<Daily> o_testReadFullResponse_rv5336__15 = Daily.reader("ACXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX").read();
        ResourceSet o_testReadFullResponse_rv5336_mg6854__20 = o_testReadFullResponse_rv5336__15.setAutoPaging(__DSPOT_autoPaging_1349);
        Assert.assertNull(((ResourceSet) (o_testReadFullResponse_rv5336_mg6854__20)).getLimit());
        Assert.assertTrue(((ResourceSet) (o_testReadFullResponse_rv5336_mg6854__20)).isAutoPaging());
        Assert.assertEquals(9223372036854775807L, ((long) (((ResourceSet) (o_testReadFullResponse_rv5336_mg6854__20)).getPageLimit())));
        Assert.assertEquals(1, ((int) (((ResourceSet) (o_testReadFullResponse_rv5336_mg6854__20)).getPageSize())));
    }

    @Test(timeout = 10000)
    public void testReadFullResponse_rv5337_mg6850() throws Exception {
        boolean __DSPOT_autoPaging_1346 = false;
        new NonStrictExpectations() {
            {
                Response __DSPOT_invoc_6 = twilioRestClient.request(((Request) (any)));
                result = new Response("{\"end\": 0,\"first_page_uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Daily?Page=0&PageSize=1\",\"last_page_uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Daily?Page=101843&PageSize=1\",\"next_page_uri\": null,\"num_pages\": 101844,\"page\": 0,\"page_size\": 1,\"previous_page_uri\": null,\"start\": 0,\"total\": 101844,\"uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Daily\",\"usage_records\": [{\"account_sid\": \"ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa\",\"api_version\": \"2010-04-01\",\"category\": \"sms-inbound-shortcode\",\"count\": \"0\",\"count_unit\": \"messages\",\"description\": \"Short Code Inbound SMS\",\"end_date\": \"2015-09-06\",\"price\": \"0\",\"price_unit\": \"usd\",\"start_date\": \"2015-09-06\",\"subresource_uris\": {\"all_time\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/AllTime.json?Category=sms-inbound-shortcode\",\"daily\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Daily.json?Category=sms-inbound-shortcode\",\"last_month\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/LastMonth.json?Category=sms-inbound-shortcode\",\"monthly\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Monthly.json?Category=sms-inbound-shortcode\",\"this_month\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/ThisMonth.json?Category=sms-inbound-shortcode\",\"today\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Today.json?Category=sms-inbound-shortcode\",\"yearly\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Yearly.json?Category=sms-inbound-shortcode\",\"yesterday\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Yesterday.json?Category=sms-inbound-shortcode\"},\"uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Daily?Category=sms-inbound-shortcode&StartDate=2015-09-06&EndDate=2015-09-06\",\"usage\": \"0\",\"usage_unit\": \"messages\"}]}", TwilioRestClient.HTTP_STATUS_CODE_OK);
                twilioRestClient.getObjectMapper();
                result = new ObjectMapper();
                __DSPOT_invoc_6.getStatusCode();
            }
        };
        ResourceSet<Daily> o_testReadFullResponse_rv5337__15 = Daily.reader("ACXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX").read();
        ResourceSet o_testReadFullResponse_rv5337_mg6850__20 = o_testReadFullResponse_rv5337__15.setAutoPaging(__DSPOT_autoPaging_1346);
        Assert.assertNull(((ResourceSet) (o_testReadFullResponse_rv5337_mg6850__20)).getLimit());
        Assert.assertFalse(((ResourceSet) (o_testReadFullResponse_rv5337_mg6850__20)).isAutoPaging());
        Assert.assertEquals(9223372036854775807L, ((long) (((ResourceSet) (o_testReadFullResponse_rv5337_mg6850__20)).getPageLimit())));
        Assert.assertEquals(1, ((int) (((ResourceSet) (o_testReadFullResponse_rv5337_mg6850__20)).getPageSize())));
    }

    @Test(timeout = 10000)
    public void testReadFullResponse_rv5337_mg6850litString9765_failAssert127() throws Exception {
        try {
            boolean __DSPOT_autoPaging_1346 = false;
            new NonStrictExpectations() {
                {
                    Response __DSPOT_invoc_6 = twilioRestClient.request(((Request) (any)));
                    result = new Response("\n", TwilioRestClient.HTTP_STATUS_CODE_OK);
                    twilioRestClient.getObjectMapper();
                    result = new ObjectMapper();
                    __DSPOT_invoc_6.getStatusCode();
                }
            };
            ResourceSet<Daily> o_testReadFullResponse_rv5337__15 = Daily.reader("ACXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX").read();
            ResourceSet o_testReadFullResponse_rv5337_mg6850__20 = o_testReadFullResponse_rv5337__15.setAutoPaging(__DSPOT_autoPaging_1346);
            org.junit.Assert.fail("testReadFullResponse_rv5337_mg6850litString9765 should have thrown ApiConnectionException");
        } catch (ApiConnectionException expected) {
            Assert.assertEquals("Unable to deserialize response: No content to map due to end-of-input\n at [Source: \n; line: 1, column: 0]\nJSON: \n", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testReadFullResponse_rv5336_mg6854_mg10101() throws Exception {
        boolean __DSPOT_autoPaging_2487 = false;
        boolean __DSPOT_autoPaging_1349 = true;
        new NonStrictExpectations() {
            {
                Response __DSPOT_invoc_6 = twilioRestClient.request(((Request) (any)));
                result = new Response("{\"end\": 0,\"first_page_uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Daily?Page=0&PageSize=1\",\"last_page_uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Daily?Page=101843&PageSize=1\",\"next_page_uri\": null,\"num_pages\": 101844,\"page\": 0,\"page_size\": 1,\"previous_page_uri\": null,\"start\": 0,\"total\": 101844,\"uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Daily\",\"usage_records\": [{\"account_sid\": \"ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa\",\"api_version\": \"2010-04-01\",\"category\": \"sms-inbound-shortcode\",\"count\": \"0\",\"count_unit\": \"messages\",\"description\": \"Short Code Inbound SMS\",\"end_date\": \"2015-09-06\",\"price\": \"0\",\"price_unit\": \"usd\",\"start_date\": \"2015-09-06\",\"subresource_uris\": {\"all_time\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/AllTime.json?Category=sms-inbound-shortcode\",\"daily\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Daily.json?Category=sms-inbound-shortcode\",\"last_month\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/LastMonth.json?Category=sms-inbound-shortcode\",\"monthly\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Monthly.json?Category=sms-inbound-shortcode\",\"this_month\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/ThisMonth.json?Category=sms-inbound-shortcode\",\"today\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Today.json?Category=sms-inbound-shortcode\",\"yearly\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Yearly.json?Category=sms-inbound-shortcode\",\"yesterday\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Yesterday.json?Category=sms-inbound-shortcode\"},\"uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Daily?Category=sms-inbound-shortcode&StartDate=2015-09-06&EndDate=2015-09-06\",\"usage\": \"0\",\"usage_unit\": \"messages\"}]}", TwilioRestClient.HTTP_STATUS_CODE_OK);
                twilioRestClient.getObjectMapper();
                result = new ObjectMapper();
                __DSPOT_invoc_6.getContent();
            }
        };
        ResourceSet<Daily> o_testReadFullResponse_rv5336__15 = Daily.reader("ACXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX").read();
        ResourceSet o_testReadFullResponse_rv5336_mg6854__20 = o_testReadFullResponse_rv5336__15.setAutoPaging(__DSPOT_autoPaging_1349);
        ResourceSet o_testReadFullResponse_rv5336_mg6854_mg10101__24 = o_testReadFullResponse_rv5336_mg6854__20.setAutoPaging(__DSPOT_autoPaging_2487);
        Assert.assertEquals(1, ((int) (((ResourceSet) (o_testReadFullResponse_rv5336_mg6854_mg10101__24)).getPageSize())));
        Assert.assertEquals(9223372036854775807L, ((long) (((ResourceSet) (o_testReadFullResponse_rv5336_mg6854_mg10101__24)).getPageLimit())));
        Assert.assertFalse(((ResourceSet) (o_testReadFullResponse_rv5336_mg6854_mg10101__24)).isAutoPaging());
        Assert.assertNull(((ResourceSet) (o_testReadFullResponse_rv5336_mg6854_mg10101__24)).getLimit());
    }

    @Test(timeout = 10000)
    public void testReadEmptyResponse_rv26() throws Exception {
        new NonStrictExpectations() {
            {
                Response __DSPOT_invoc_6 = twilioRestClient.request(((Request) (any)));
                result = new Response("{\"end\": 0,\"first_page_uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Daily?Page=0&PageSize=1\",\"last_page_uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Daily?Page=101843&PageSize=1\",\"next_page_uri\": null,\"num_pages\": 101844,\"page\": 0,\"page_size\": 1,\"previous_page_uri\": null,\"start\": 0,\"total\": 101844,\"uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Daily\",\"usage_records\": []}", TwilioRestClient.HTTP_STATUS_CODE_OK);
                twilioRestClient.getObjectMapper();
                result = new ObjectMapper();
                __DSPOT_invoc_6.getStatusCode();
            }
        };
        ResourceSet<Daily> o_testReadEmptyResponse_rv26__15 = Daily.reader("ACXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX").read();
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
                result = new Response("{\"end\": 0,\"first_page_uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Daily?Page=0&PageSize=1\",\"last_page_uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Daily?Page=101843&PageSize=1\",\"next_page_uri\": null,\"num_pages\": 101844,\"page\": 0,\"page_size\": 1,\"previous_page_uri\": null,\"start\": 0,\"total\": 101844,\"uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Daily\",\"usage_records\": []}", TwilioRestClient.HTTP_STATUS_CODE_OK);
                twilioRestClient.getObjectMapper();
                result = new ObjectMapper();
                __DSPOT_invoc_6.getContent();
            }
        };
        ResourceSet<Daily> o_testReadEmptyResponse_rv25__15 = Daily.reader("ACXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX").read();
        Assert.assertEquals(1, ((int) (((ResourceSet) (o_testReadEmptyResponse_rv25__15)).getPageSize())));
        Assert.assertNull(((ResourceSet) (o_testReadEmptyResponse_rv25__15)).getLimit());
        Assert.assertTrue(((ResourceSet) (o_testReadEmptyResponse_rv25__15)).isAutoPaging());
        Assert.assertEquals(9223372036854775807L, ((long) (((ResourceSet) (o_testReadEmptyResponse_rv25__15)).getPageLimit())));
    }

    @Test(timeout = 10000)
    public void testReadEmptyResponselitString5_failAssert19() throws Exception {
        try {
            new NonStrictExpectations() {
                {
                    twilioRestClient.request(((Request) (any)));
                    result = new Response("dhscbCS@!x*zH_,y(q2 5[gpbL[{$QV5:Wz2[|+mr6#-VtX(r!Fs2l>UgIvC=TU&zgYc TM1`_8;0L`A=SO/woO!OKS@Rl&{ha!&Bcvg[?i!rb0/|]6^FT)-ef&bk*201yCi*OdwpauR%h1,xavU[1Rvnj|}8wu]&8(Dgh`l V!3a(!.#b{[Iz>YSe|%xHdm7#=ToX)D7x>[Bob5_83OI`-k-a8(J8Bp[$XdYQ7-#sa<}t>?]?7gchsEp#5_w)+KtmI6N*:lKC*+{5@T5!^MYU(dM7KJ&><6ycw,-c^.vZ(8(U^r,Jp9Flz5*yC=M]:bMoV#NG^1yAAF?5P&+YTN/#yO[*WW4JN-$nw<}7EGpwmm(EQndBdj-qEHp!#I]LDWP=,y4JV)d4}^w[&oDAIOw? O!T}Lq8xal:7%uE_&Ml%;sG#Ahw*&z*$G`c:wkJ!][,J^uy}s#6CE3#^t lG", TwilioRestClient.HTTP_STATUS_CODE_OK);
                    twilioRestClient.getObjectMapper();
                    result = new ObjectMapper();
                }
            };
            Daily.reader("ACXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX").read();
            org.junit.Assert.fail("testReadEmptyResponselitString5 should have thrown ApiConnectionException");
        } catch (ApiConnectionException expected) {
            Assert.assertEquals("Unable to deserialize response: Unrecognized token \'dhscbCS\': was expecting (\'true\', \'false\' or \'null\')\n at [Source: dhscbCS@!x*zH_,y(q2 5[gpbL[{$QV5:Wz2[|+mr6#-VtX(r!Fs2l>UgIvC=TU&zgYc TM1`_8;0L`A=SO/woO!OKS@Rl&{ha!&Bcvg[?i!rb0/|]6^FT)-ef&bk*201yCi*OdwpauR%h1,xavU[1Rvnj|}8wu]&8(Dgh`l V!3a(!.#b{[Iz>YSe|%xHdm7#=ToX)D7x>[Bob5_83OI`-k-a8(J8Bp[$XdYQ7-#sa<}t>?]?7gchsEp#5_w)+KtmI6N*:lKC*+{5@T5!^MYU(dM7KJ&><6ycw,-c^.vZ(8(U^r,Jp9Flz5*yC=M]:bMoV#NG^1yAAF?5P&+YTN/#yO[*WW4JN-$nw<}7EGpwmm(EQndBdj-qEHp!#I]LDWP=,y4JV)d4}^w[&oDAIOw? O!T}Lq8xal:7%uE_&Ml%;sG#Ahw*&z*$G`c:wkJ!][,J^uy}s#6CE3#^t lG; line: 1, column: 8]\nJSON: dhscbCS@!x*zH_,y(q2 5[gpbL[{$QV5:Wz2[|+mr6#-VtX(r!Fs2l>UgIvC=TU&zgYc TM1`_8;0L`A=SO/woO!OKS@Rl&{ha!&Bcvg[?i!rb0/|]6^FT)-ef&bk*201yCi*OdwpauR%h1,xavU[1Rvnj|}8wu]&8(Dgh`l V!3a(!.#b{[Iz>YSe|%xHdm7#=ToX)D7x>[Bob5_83OI`-k-a8(J8Bp[$XdYQ7-#sa<}t>?]?7gchsEp#5_w)+KtmI6N*:lKC*+{5@T5!^MYU(dM7KJ&><6ycw,-c^.vZ(8(U^r,Jp9Flz5*yC=M]:bMoV#NG^1yAAF?5P&+YTN/#yO[*WW4JN-$nw<}7EGpwmm(EQndBdj-qEHp!#I]LDWP=,y4JV)d4}^w[&oDAIOw? O!T}Lq8xal:7%uE_&Ml%;sG#Ahw*&z*$G`c:wkJ!][,J^uy}s#6CE3#^t lG", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testReadEmptyResponse_rv26_mg1443() throws Exception {
        boolean __DSPOT_autoPaging_30 = false;
        new NonStrictExpectations() {
            {
                Response __DSPOT_invoc_6 = twilioRestClient.request(((Request) (any)));
                result = new Response("{\"end\": 0,\"first_page_uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Daily?Page=0&PageSize=1\",\"last_page_uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Daily?Page=101843&PageSize=1\",\"next_page_uri\": null,\"num_pages\": 101844,\"page\": 0,\"page_size\": 1,\"previous_page_uri\": null,\"start\": 0,\"total\": 101844,\"uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Daily\",\"usage_records\": []}", TwilioRestClient.HTTP_STATUS_CODE_OK);
                twilioRestClient.getObjectMapper();
                result = new ObjectMapper();
                __DSPOT_invoc_6.getStatusCode();
            }
        };
        ResourceSet<Daily> o_testReadEmptyResponse_rv26__15 = Daily.reader("ACXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX").read();
        ResourceSet o_testReadEmptyResponse_rv26_mg1443__20 = o_testReadEmptyResponse_rv26__15.setAutoPaging(__DSPOT_autoPaging_30);
        Assert.assertEquals(1, ((int) (((ResourceSet) (o_testReadEmptyResponse_rv26_mg1443__20)).getPageSize())));
        Assert.assertNull(((ResourceSet) (o_testReadEmptyResponse_rv26_mg1443__20)).getLimit());
        Assert.assertFalse(((ResourceSet) (o_testReadEmptyResponse_rv26_mg1443__20)).isAutoPaging());
        Assert.assertEquals(9223372036854775807L, ((long) (((ResourceSet) (o_testReadEmptyResponse_rv26_mg1443__20)).getPageLimit())));
    }

    @Test(timeout = 10000)
    public void testReadEmptyResponse_rv25_mg1447() throws Exception {
        boolean __DSPOT_autoPaging_33 = false;
        new NonStrictExpectations() {
            {
                Response __DSPOT_invoc_6 = twilioRestClient.request(((Request) (any)));
                result = new Response("{\"end\": 0,\"first_page_uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Daily?Page=0&PageSize=1\",\"last_page_uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Daily?Page=101843&PageSize=1\",\"next_page_uri\": null,\"num_pages\": 101844,\"page\": 0,\"page_size\": 1,\"previous_page_uri\": null,\"start\": 0,\"total\": 101844,\"uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Daily\",\"usage_records\": []}", TwilioRestClient.HTTP_STATUS_CODE_OK);
                twilioRestClient.getObjectMapper();
                result = new ObjectMapper();
                __DSPOT_invoc_6.getContent();
            }
        };
        ResourceSet<Daily> o_testReadEmptyResponse_rv25__15 = Daily.reader("ACXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX").read();
        ResourceSet o_testReadEmptyResponse_rv25_mg1447__20 = o_testReadEmptyResponse_rv25__15.setAutoPaging(__DSPOT_autoPaging_33);
        Assert.assertEquals(1, ((int) (((ResourceSet) (o_testReadEmptyResponse_rv25_mg1447__20)).getPageSize())));
        Assert.assertNull(((ResourceSet) (o_testReadEmptyResponse_rv25_mg1447__20)).getLimit());
        Assert.assertFalse(((ResourceSet) (o_testReadEmptyResponse_rv25_mg1447__20)).isAutoPaging());
        Assert.assertEquals(9223372036854775807L, ((long) (((ResourceSet) (o_testReadEmptyResponse_rv25_mg1447__20)).getPageLimit())));
    }

    @Test(timeout = 10000)
    public void testReadEmptyResponse_rv26_mg1443_add4434() throws Exception {
        boolean __DSPOT_autoPaging_30 = false;
        new NonStrictExpectations() {
            {
                Response __DSPOT_invoc_6 = twilioRestClient.request(((Request) (any)));
                result = new Response("{\"end\": 0,\"first_page_uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Daily?Page=0&PageSize=1\",\"last_page_uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Daily?Page=101843&PageSize=1\",\"next_page_uri\": null,\"num_pages\": 101844,\"page\": 0,\"page_size\": 1,\"previous_page_uri\": null,\"start\": 0,\"total\": 101844,\"uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Daily\",\"usage_records\": []}", TwilioRestClient.HTTP_STATUS_CODE_OK);
                twilioRestClient.getObjectMapper();
                result = new ObjectMapper();
                __DSPOT_invoc_6.getStatusCode();
            }
        };
        ResourceSet<Daily> o_testReadEmptyResponse_rv26_mg1443_add4434__16 = Daily.reader("ACXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX").read();
        Assert.assertEquals(1, ((int) (((ResourceSet) (o_testReadEmptyResponse_rv26_mg1443_add4434__16)).getPageSize())));
        Assert.assertEquals(9223372036854775807L, ((long) (((ResourceSet) (o_testReadEmptyResponse_rv26_mg1443_add4434__16)).getPageLimit())));
        Assert.assertNull(((ResourceSet) (o_testReadEmptyResponse_rv26_mg1443_add4434__16)).getLimit());
        Assert.assertTrue(((ResourceSet) (o_testReadEmptyResponse_rv26_mg1443_add4434__16)).isAutoPaging());
        ResourceSet<Daily> o_testReadEmptyResponse_rv26__15 = Daily.reader("ACXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX").read();
        ResourceSet o_testReadEmptyResponse_rv26_mg1443__20 = o_testReadEmptyResponse_rv26__15.setAutoPaging(__DSPOT_autoPaging_30);
        Assert.assertEquals(1, ((int) (((ResourceSet) (o_testReadEmptyResponse_rv26_mg1443_add4434__16)).getPageSize())));
        Assert.assertEquals(9223372036854775807L, ((long) (((ResourceSet) (o_testReadEmptyResponse_rv26_mg1443_add4434__16)).getPageLimit())));
        Assert.assertNull(((ResourceSet) (o_testReadEmptyResponse_rv26_mg1443_add4434__16)).getLimit());
        Assert.assertTrue(((ResourceSet) (o_testReadEmptyResponse_rv26_mg1443_add4434__16)).isAutoPaging());
    }

    @Test(timeout = 10000)
    public void testReadEmptyResponse_rv25_mg1447litString4364_failAssert65() throws Exception {
        try {
            boolean __DSPOT_autoPaging_33 = false;
            new NonStrictExpectations() {
                {
                    Response __DSPOT_invoc_6 = twilioRestClient.request(((Request) (any)));
                    result = new Response(":", TwilioRestClient.HTTP_STATUS_CODE_OK);
                    twilioRestClient.getObjectMapper();
                    result = new ObjectMapper();
                    __DSPOT_invoc_6.getContent();
                }
            };
            ResourceSet<Daily> o_testReadEmptyResponse_rv25__15 = Daily.reader("ACXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX").read();
            ResourceSet o_testReadEmptyResponse_rv25_mg1447__20 = o_testReadEmptyResponse_rv25__15.setAutoPaging(__DSPOT_autoPaging_33);
            org.junit.Assert.fail("testReadEmptyResponse_rv25_mg1447litString4364 should have thrown ApiConnectionException");
        } catch (ApiConnectionException expected) {
            Assert.assertEquals("Unable to deserialize response: Unexpected character (\':\' (code 58)): expected a valid value (number, String, array, object, \'true\', \'false\' or \'null\')\n at [Source: :; line: 1, column: 2]\nJSON: :", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testReadEmptyResponse_rv25_mg1447_mg4498() throws Exception {
        boolean __DSPOT_autoPaging_1168 = true;
        boolean __DSPOT_autoPaging_33 = false;
        new NonStrictExpectations() {
            {
                Response __DSPOT_invoc_6 = twilioRestClient.request(((Request) (any)));
                result = new Response("{\"end\": 0,\"first_page_uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Daily?Page=0&PageSize=1\",\"last_page_uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Daily?Page=101843&PageSize=1\",\"next_page_uri\": null,\"num_pages\": 101844,\"page\": 0,\"page_size\": 1,\"previous_page_uri\": null,\"start\": 0,\"total\": 101844,\"uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Daily\",\"usage_records\": []}", TwilioRestClient.HTTP_STATUS_CODE_OK);
                twilioRestClient.getObjectMapper();
                result = new ObjectMapper();
                __DSPOT_invoc_6.getContent();
            }
        };
        ResourceSet<Daily> o_testReadEmptyResponse_rv25__15 = Daily.reader("ACXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX").read();
        ResourceSet o_testReadEmptyResponse_rv25_mg1447__20 = o_testReadEmptyResponse_rv25__15.setAutoPaging(__DSPOT_autoPaging_33);
        ResourceSet o_testReadEmptyResponse_rv25_mg1447_mg4498__24 = o_testReadEmptyResponse_rv25_mg1447__20.setAutoPaging(__DSPOT_autoPaging_1168);
        Assert.assertEquals(1, ((int) (((ResourceSet) (o_testReadEmptyResponse_rv25_mg1447_mg4498__24)).getPageSize())));
        Assert.assertEquals(9223372036854775807L, ((long) (((ResourceSet) (o_testReadEmptyResponse_rv25_mg1447_mg4498__24)).getPageLimit())));
        Assert.assertNull(((ResourceSet) (o_testReadEmptyResponse_rv25_mg1447_mg4498__24)).getLimit());
        Assert.assertTrue(((ResourceSet) (o_testReadEmptyResponse_rv25_mg1447_mg4498__24)).isAutoPaging());
    }

    @Test(timeout = 10000)
    public void testReadEmptyResponse_rv25_mg1447_add4445() throws Exception {
        boolean __DSPOT_autoPaging_33 = false;
        new NonStrictExpectations() {
            {
                Response __DSPOT_invoc_6 = twilioRestClient.request(((Request) (any)));
                result = new Response("{\"end\": 0,\"first_page_uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Daily?Page=0&PageSize=1\",\"last_page_uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Daily?Page=101843&PageSize=1\",\"next_page_uri\": null,\"num_pages\": 101844,\"page\": 0,\"page_size\": 1,\"previous_page_uri\": null,\"start\": 0,\"total\": 101844,\"uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Daily\",\"usage_records\": []}", TwilioRestClient.HTTP_STATUS_CODE_OK);
                twilioRestClient.getObjectMapper();
                result = new ObjectMapper();
                __DSPOT_invoc_6.getContent();
            }
        };
        DailyReader o_testReadEmptyResponse_rv25_mg1447_add4445__16 = Daily.reader("ACXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
        Assert.assertNull(((DailyReader) (o_testReadEmptyResponse_rv25_mg1447_add4445__16)).getPageSize());
        Assert.assertNull(((DailyReader) (o_testReadEmptyResponse_rv25_mg1447_add4445__16)).getLimit());
        ResourceSet<Daily> o_testReadEmptyResponse_rv25__15 = Daily.reader("ACXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX").read();
        ResourceSet o_testReadEmptyResponse_rv25_mg1447__20 = o_testReadEmptyResponse_rv25__15.setAutoPaging(__DSPOT_autoPaging_33);
        Assert.assertNull(((DailyReader) (o_testReadEmptyResponse_rv25_mg1447_add4445__16)).getPageSize());
        Assert.assertNull(((DailyReader) (o_testReadEmptyResponse_rv25_mg1447_add4445__16)).getLimit());
    }

    @Test(timeout = 10000)
    public void testReadEmptyResponse_rv26_mg1443_add4436() throws Exception {
        boolean __DSPOT_autoPaging_30 = false;
        new NonStrictExpectations() {
            {
                Response __DSPOT_invoc_6 = twilioRestClient.request(((Request) (any)));
                result = new Response("{\"end\": 0,\"first_page_uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Daily?Page=0&PageSize=1\",\"last_page_uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Daily?Page=101843&PageSize=1\",\"next_page_uri\": null,\"num_pages\": 101844,\"page\": 0,\"page_size\": 1,\"previous_page_uri\": null,\"start\": 0,\"total\": 101844,\"uri\": \"/2010-04-01/Accounts/ACaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/Usage/Records/Daily\",\"usage_records\": []}", TwilioRestClient.HTTP_STATUS_CODE_OK);
                twilioRestClient.getObjectMapper();
                result = new ObjectMapper();
                __DSPOT_invoc_6.getStatusCode();
            }
        };
        ResourceSet<Daily> o_testReadEmptyResponse_rv26__15 = Daily.reader("ACXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX").read();
        ResourceSet o_testReadEmptyResponse_rv26_mg1443_add4436__20 = o_testReadEmptyResponse_rv26__15.setAutoPaging(__DSPOT_autoPaging_30);
        Assert.assertEquals(1, ((int) (((ResourceSet) (o_testReadEmptyResponse_rv26_mg1443_add4436__20)).getPageSize())));
        Assert.assertEquals(9223372036854775807L, ((long) (((ResourceSet) (o_testReadEmptyResponse_rv26_mg1443_add4436__20)).getPageLimit())));
        Assert.assertNull(((ResourceSet) (o_testReadEmptyResponse_rv26_mg1443_add4436__20)).getLimit());
        Assert.assertFalse(((ResourceSet) (o_testReadEmptyResponse_rv26_mg1443_add4436__20)).isAutoPaging());
        ResourceSet o_testReadEmptyResponse_rv26_mg1443__20 = o_testReadEmptyResponse_rv26__15.setAutoPaging(__DSPOT_autoPaging_30);
        Assert.assertEquals(1, ((int) (((ResourceSet) (o_testReadEmptyResponse_rv26_mg1443_add4436__20)).getPageSize())));
        Assert.assertEquals(9223372036854775807L, ((long) (((ResourceSet) (o_testReadEmptyResponse_rv26_mg1443_add4436__20)).getPageLimit())));
        Assert.assertNull(((ResourceSet) (o_testReadEmptyResponse_rv26_mg1443_add4436__20)).getLimit());
        Assert.assertFalse(((ResourceSet) (o_testReadEmptyResponse_rv26_mg1443_add4436__20)).isAutoPaging());
    }
}

