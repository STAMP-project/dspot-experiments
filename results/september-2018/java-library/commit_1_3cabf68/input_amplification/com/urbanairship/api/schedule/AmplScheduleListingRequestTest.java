package com.urbanairship.api.schedule;


import com.fasterxml.jackson.databind.JsonMappingException;
import com.urbanairship.api.client.ResponseParser;
import com.urbanairship.api.common.parse.APIParsingException;
import com.urbanairship.api.schedule.model.ListAllSchedulesResponse;
import com.urbanairship.api.schedule.model.SchedulePayload;
import com.urbanairship.api.schedule.parse.ScheduleObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;


public class AmplScheduleListingRequestTest {
    ScheduleListingRequest listAllSchedulesRequest = ScheduleListingRequest.newRequest();

    ScheduleListingRequest listSchedulesWithParamsRequest = ScheduleListingRequest.newRequest(UUID.fromString("643a297a-7313-45f0-853f-e68785e54c77"), 25, ListSchedulesOrderType.ASC);

    ScheduleListingRequest listSingleScheduleRequest = ScheduleListingRequest.newRequest("id");

    ScheduleListingRequest listNextPageSchedulesRequest = ScheduleListingRequest.newRequest(URI.create("https://go.urbanairship.com/api/schedules/?start=643a297a-7313-45f0-853f-e68785e54c77&limit=25&order=asc"));

    @Test(timeout = 10000)
    public void testListSchedulesParsernull89_failAssert43_literalMutationString1842_failAssert123() throws Exception {
        try {
            try {
                ResponseParser responseParser = new ResponseParser<ListAllSchedulesResponse>() {
                    @Override
                    public ListAllSchedulesResponse parse(String response) throws IOException {
                        return ScheduleObjectMapper.getInstance().readValue(response, ListAllSchedulesResponse.class);
                    }
                };
                String response = "{\"ok\":true,\"count\":5,\"total_count\":6,\"schedules\":" + ((((((("[{\"url\":\"https://go.urbanairship.com/api/schedules/5a60e0a6-9aa7-449f-a038-6806e572baf3\",\"" + "schedule\":{\"scheduled_time\":\"2015-01-01T08:00:00\"},\"push\":{\"audience\":\"ALL\",\"device") + "_types\":[\"android\",\"ios\"],\"notification\":{\"alert\":\"Happy New Year 2015!\",\"android\"") + ":{},\"ios\":{}}},\"push_ids\":[\"8430f2e0-ec07-4c1e-adc4-0c7c7978e648\"]},{\"url\":\"https://go") + ".urbanairship.com/api/schedules/f53aa2bd-018a-4482-8d7d-691d13407973\",\"schedule\":{\"schedule") + "h_ids\":[\"b217a321-922f-4aee-b239-ca1b58c6b652\"]}]}") + "\"ios\"],\"notification\":{\"alert\":\"Happy New Year 2016!\",\"android\":{},\"ios\":{}}},\"pus") + "h_ids\":[\"b217a321-922f-4aee-b239-ca1b58c6b652\"]}]}");
                this.listAllSchedulesRequest.getResponseParser().parse(response);
                responseParser.parse(response);
                this.listNextPageSchedulesRequest.getResponseParser().parse(response);
                responseParser.parse(response);
                this.listSchedulesWithParamsRequest.getResponseParser().parse(response);
                responseParser.parse(null);
                org.junit.Assert.fail("testListSchedulesParsernull89 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("testListSchedulesParsernull89_failAssert43_literalMutationString1842 should have thrown JsonMappingException");
        } catch (JsonMappingException expected_1) {
            Assert.assertEquals("Error parsing schedule object. Either scheduled_time or local_scheduled_time must be set. (through reference chain: java.util.ArrayList[1])", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testListSchedulesParsernull87_failAssert41_literalMutationString1270_failAssert121() throws Exception {
        try {
            try {
                ResponseParser responseParser = new ResponseParser<ListAllSchedulesResponse>() {
                    @Override
                    public ListAllSchedulesResponse parse(String response) throws IOException {
                        return ScheduleObjectMapper.getInstance().readValue(response, ListAllSchedulesResponse.class);
                    }
                };
                String response = "{\"ok\":true,\"count\":5,\"total_count\":6,\"schedules\":" + ((((((("[{\"url\":\"https://go.urbanairship.com/api/schedules/5a60e0a6-9aa7-449f-a038-6806e572baf3\",\"" + "schedule\":{\"scheduled_time\":\"2015-01-01T08:00:00\"},\"push\":{\"audience\":\"ALL\",\"device") + "_types\":[\"android\",\"ios\"],\"notification\":{\"alert\":\"Happy New Year 2015!\",\"android\"") + ":{},\"ios\":{}}},\"push_ids\":[\"8430f2e0-ec07-4c1e-adc4-0c7c7978e648\"]},{\"url\":\"https://go") + ".urbanairship.com/api/schedules/f53aa2bd-018a-4482-8d7d-691d13407973\",\"schedule\":{\"schedule") + "h_ids\":[\"b217a321-922f-4aee-b239-ca1b58c6b652\"]}]}") + "\"ios\"],\"notification\":{\"alert\":\"Happy New Year 2016!\",\"android\":{},\"ios\":{}}},\"pus") + "h_ids\":[\"b217a321-922f-4aee-b239-ca1b58c6b652\"]}]}");
                this.listAllSchedulesRequest.getResponseParser().parse(response);
                responseParser.parse(response);
                this.listNextPageSchedulesRequest.getResponseParser().parse(response);
                responseParser.parse(null);
                this.listSchedulesWithParamsRequest.getResponseParser().parse(response);
                responseParser.parse(response);
                org.junit.Assert.fail("testListSchedulesParsernull87 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("testListSchedulesParsernull87_failAssert41_literalMutationString1270 should have thrown JsonMappingException");
        } catch (JsonMappingException expected_1) {
            Assert.assertEquals("Error parsing schedule object. Either scheduled_time or local_scheduled_time must be set. (through reference chain: java.util.ArrayList[1])", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testListSchedulesParsernull85_failAssert39_literalMutationString695_failAssert120() throws Exception {
        try {
            try {
                ResponseParser responseParser = new ResponseParser<ListAllSchedulesResponse>() {
                    @Override
                    public ListAllSchedulesResponse parse(String response) throws IOException {
                        return ScheduleObjectMapper.getInstance().readValue(response, ListAllSchedulesResponse.class);
                    }
                };
                String response = "{\"ok\":true,\"count\":5,\"total_count\":6,\"schedules\":" + ((((((("[{\"url\":\"https://go.urbanairship.com/api/schedules/5a60e0a6-9aa7-449f-a038-6806e572baf3\",\"" + "schedule\":{\"scheduled_time\":\"2015-01-01T08:00:00\"},\"push\":{\"audience\":\"ALL\",\"device") + "_types\":[\"android\",\"ios\"],\"notification\":{\"alert\":\"Happy New Year 2015!\",\"android\"") + ":{},\"ios\":{}}},\"push_ids\":[\"8430f2e0-ec07-4c1e-adc4-0c7c7978e648\"]},{\"url\":\"https://go") + ".urbanairship.com/api/schedules/f53aa2bd-018a-4482-8d7d-691d13407973\",\"schedule\":{\"schedule") + "h_ids\":[\"b217a321-922f-4aee-b239-ca1b58c6b652\"]}]}") + "\"ios\"],\"notification\":{\"alert\":\"Happy New Year 2016!\",\"android\":{},\"ios\":{}}},\"pus") + "h_ids\":[\"b217a321-922f-4aee-b239-ca1b58c6b652\"]}]}");
                this.listAllSchedulesRequest.getResponseParser().parse(response);
                responseParser.parse(null);
                this.listNextPageSchedulesRequest.getResponseParser().parse(response);
                responseParser.parse(response);
                this.listSchedulesWithParamsRequest.getResponseParser().parse(response);
                responseParser.parse(response);
                org.junit.Assert.fail("testListSchedulesParsernull85 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("testListSchedulesParsernull85_failAssert39_literalMutationString695 should have thrown JsonMappingException");
        } catch (JsonMappingException expected_1) {
            Assert.assertEquals("Error parsing schedule object. Either scheduled_time or local_scheduled_time must be set. (through reference chain: java.util.ArrayList[1])", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testListSchedulesParser_literalMutationString54_failAssert26() throws Exception {
        try {
            ResponseParser responseParser = new ResponseParser<ListAllSchedulesResponse>() {
                @Override
                public ListAllSchedulesResponse parse(String response) throws IOException {
                    return ScheduleObjectMapper.getInstance().readValue(response, ListAllSchedulesResponse.class);
                }
            };
            String response = "{\"ok\":true,\"count\":5,\"total_count\":6,\"schedules\":" + ((((((("[{\"url\":\"https://go.urbanairship.com/api/schedules/5a60e0a6-9aa7-449f-a038-6806e572baf3\",\"" + "schedule\":{\"scheduled_time\":\"2015-01-01T08:00:00\"},\"push\":{\"audience\":\"ALL\",\"device") + "_types\":[\"android\",\"ios\"],\"notification\":{\"alert\":\"Happy New Year 2015!\",\"android\"") + ":{},\"ios\":{}}},\"push_ids\":[\"8430f2e0-ec07-4c1e-adc4-0c7c7978e648\"]},{\"url\":\"https://go") + ".urbanairship.com/api/schedules/f53aa2bd-018a-4482-8d7d-691d13407973\",\"schedule\":{\"schedule") + "h_ids\":[\"b217a321-922f-4aee-b239-ca1b58c6b652\"]}]}") + "\"ios\"],\"notification\":{\"alert\":\"Happy New Year 2016!\",\"android\":{},\"ios\":{}}},\"pus") + "h_ids\":[\"b217a321-922f-4aee-b239-ca1b58c6b652\"]}]}");
            listAllSchedulesRequest.getResponseParser().parse(response);
            responseParser.parse(response);
            listNextPageSchedulesRequest.getResponseParser().parse(response);
            responseParser.parse(response);
            listSchedulesWithParamsRequest.getResponseParser().parse(response);
            responseParser.parse(response);
            org.junit.Assert.fail("testListSchedulesParser_literalMutationString54 should have thrown JsonMappingException");
        } catch (JsonMappingException expected) {
            Assert.assertEquals("Error parsing schedule object. Either scheduled_time or local_scheduled_time must be set. (through reference chain: java.util.ArrayList[1])", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testListSchedulesParsernull88_failAssert42_literalMutationString1550_failAssert122() throws Exception {
        try {
            try {
                ResponseParser responseParser = new ResponseParser<ListAllSchedulesResponse>() {
                    @Override
                    public ListAllSchedulesResponse parse(String response) throws IOException {
                        return ScheduleObjectMapper.getInstance().readValue(response, ListAllSchedulesResponse.class);
                    }
                };
                String response = "{\"ok\":true,\"count\":5,\"total_count\":6,\"schedules\":" + ((((((("[{\"url\":\"https://go.urbanairship.com/api/schedules/5a60e0a6-9aa7-449f-a038-6806e572baf3\",\"" + "schedule\":{\"scheduled_time\":\"2015-01-01T08:00:00\"},\"push\":{\"audience\":\"ALL\",\"device") + "_types\":[\"android\",\"ios\"],\"notification\":{\"alert\":\"Happy New Year 2015!\",\"android\"") + ":{},\"ios\":{}}},\"push_ids\":[\"8430f2e0-ec07-4c1e-adc4-0c7c7978e648\"]},{\"url\":\"https://go") + ".urbanairship.com/api/schedules/f53aa2bd-018a-4482-8d7d-691d13407973\",\"schedule\":{\"schedule") + "h_ids\":[\"b217a321-922f-4aee-b239-ca1b58c6b652\"]}]}") + "\"ios\"],\"notification\":{\"alert\":\"Happy New Year 2016!\",\"android\":{},\"ios\":{}}},\"pus") + "h_ids\":[\"b217a321-922f-4aee-b239-ca1b58c6b652\"]}]}");
                this.listAllSchedulesRequest.getResponseParser().parse(response);
                responseParser.parse(response);
                this.listNextPageSchedulesRequest.getResponseParser().parse(response);
                responseParser.parse(response);
                this.listSchedulesWithParamsRequest.getResponseParser().parse(null);
                responseParser.parse(response);
                org.junit.Assert.fail("testListSchedulesParsernull88 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("testListSchedulesParsernull88_failAssert42_literalMutationString1550 should have thrown JsonMappingException");
        } catch (JsonMappingException expected_1) {
            Assert.assertEquals("Error parsing schedule object. Either scheduled_time or local_scheduled_time must be set. (through reference chain: java.util.ArrayList[1])", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testListSchedulesParsernull86_failAssert40_literalMutationString981_failAssert119() throws Exception {
        try {
            try {
                ResponseParser responseParser = new ResponseParser<ListAllSchedulesResponse>() {
                    @Override
                    public ListAllSchedulesResponse parse(String response) throws IOException {
                        return ScheduleObjectMapper.getInstance().readValue(response, ListAllSchedulesResponse.class);
                    }
                };
                String response = "{\"ok\":true,\"count\":5,\"total_count\":6,\"schedules\":" + ((((((("[{\"url\":\"https://go.urbanairship.com/api/schedules/5a60e0a6-9aa7-449f-a038-6806e572baf3\",\"" + "schedule\":{\"scheduled_time\":\"2015-01-01T08:00:00\"},\"push\":{\"audience\":\"ALL\",\"device") + "_types\":[\"android\",\"ios\"],\"notification\":{\"alert\":\"Happy New Year 2015!\",\"android\"") + ":{},\"ios\":{}}},\"push_ids\":[\"8430f2e0-ec07-4c1e-adc4-0c7c7978e648\"]},{\"url\":\"https://go") + ".urbanairship.com/api/schedules/f53aa2bd-018a-4482-8d7d-691d13407973\",\"schedule\":{\"schedule") + "h_ids\":[\"b217a321-922f-4aee-b239-ca1b58c6b652\"]}]}") + "\"ios\"],\"notification\":{\"alert\":\"Happy New Year 2016!\",\"android\":{},\"ios\":{}}},\"pus") + "h_ids\":[\"b217a321-922f-4aee-b239-ca1b58c6b652\"]}]}");
                this.listAllSchedulesRequest.getResponseParser().parse(response);
                responseParser.parse(response);
                this.listNextPageSchedulesRequest.getResponseParser().parse(null);
                responseParser.parse(response);
                this.listSchedulesWithParamsRequest.getResponseParser().parse(response);
                responseParser.parse(response);
                org.junit.Assert.fail("testListSchedulesParsernull86 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("testListSchedulesParsernull86_failAssert40_literalMutationString981 should have thrown JsonMappingException");
        } catch (JsonMappingException expected_1) {
            Assert.assertEquals("Error parsing schedule object. Either scheduled_time or local_scheduled_time must be set. (through reference chain: java.util.ArrayList[1])", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testListSchedulesParser_literalMutationString55_failAssert27() throws Exception {
        try {
            ResponseParser responseParser = new ResponseParser<ListAllSchedulesResponse>() {
                @Override
                public ListAllSchedulesResponse parse(String response) throws IOException {
                    return ScheduleObjectMapper.getInstance().readValue(response, ListAllSchedulesResponse.class);
                }
            };
            String response = "{\"ok\":true,\"count\":5,\"total_count\":6,\"schedules\":" + ((((((("[{\"url\":\"https://go.urbanairship.com/api/schedules/5a60e0a6-9aa7-449f-a038-6806e572baf3\",\"" + "schedule\":{\"scheduled_time\":\"2015-01-01T08:00:00\"},\"push\":{\"audience\":\"ALL\",\"device") + "_types\":[\"android\",\"ios\"],\"notification\":{\"alert\":\"Happy New Year 2015!\",\"android\"") + ":{},\"ios\":{}}},\"push_ids\":[\"8430f2e0-ec07-4c1e-adc4-0c7c7978e648\"]},{\"url\":\"https://go") + ".urbanairship.com/api/schedules/f53aa2bd-018a-4482-8d7d-691d13407973\",\"schedule\":{\"schedule") + "d_oime\":\"2016-01-01T08:00:00\"},\"push\":{\"audience\":\"ALL\",\"device_types\":[\"android\",") + "\"ios\"],\"notification\":{\"alert\":\"Happy New Year 2016!\",\"android\":{},\"ios\":{}}},\"pus") + "h_ids\":[\"b217a321-922f-4aee-b239-ca1b58c6b652\"]}]}");
            listAllSchedulesRequest.getResponseParser().parse(response);
            responseParser.parse(response);
            listNextPageSchedulesRequest.getResponseParser().parse(response);
            responseParser.parse(response);
            listSchedulesWithParamsRequest.getResponseParser().parse(response);
            responseParser.parse(response);
            org.junit.Assert.fail("testListSchedulesParser_literalMutationString55 should have thrown JsonMappingException");
        } catch (JsonMappingException expected) {
            Assert.assertEquals("Error parsing schedule object. Either scheduled_time or local_scheduled_time must be set. (through reference chain: java.util.ArrayList[1])", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testListSingleScheduleParser_literalMutationString16311_failAssert200() throws Exception {
        try {
            ResponseParser responseParser = new ResponseParser<ListAllSchedulesResponse>() {
                @Override
                public ListAllSchedulesResponse parse(String response) throws IOException {
                    return ListAllSchedulesResponse.newBuilder().setCount(1).setTotalCount(1).setOk(true).addSchedule(ScheduleObjectMapper.getInstance().readValue(response, SchedulePayload.class)).build();
                }
            };
            String response = "{\"schedule\":{\"Fscheduled_time\":\"2015-08-07T22:10:44\"},\"name\":\"Special Scheduled" + ("Push 20\",\"push\":{\"audience\":\"ALL\",\"device_types\":\"all\",\"notification\":{\"alert\":\"Scheduled" + "Push 20\"}},\"push_ids\":[\"274f9aa4-2d00-4911-a043-70129f29adf2\"]}");
            listSingleScheduleRequest.getResponseParser().parse(response);
            responseParser.parse(response);
            org.junit.Assert.fail("testListSingleScheduleParser_literalMutationString16311 should have thrown APIParsingException");
        } catch (APIParsingException expected) {
            Assert.assertEquals("Error parsing schedule object. Either scheduled_time or local_scheduled_time must be set.", expected.getMessage());
        }
    }
}

