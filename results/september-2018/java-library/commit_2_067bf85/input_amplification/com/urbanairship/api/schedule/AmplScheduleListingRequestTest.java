package com.urbanairship.api.schedule;


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
    public void testListSingleScheduleParsernull21021_failAssert61_literalMutationString21358_failAssert78() throws Exception {
        try {
            try {
                ResponseParser responseParser = new ResponseParser<ListAllSchedulesResponse>() {
                    @Override
                    public ListAllSchedulesResponse parse(String response) throws IOException {
                        return ListAllSchedulesResponse.newBuilder().setCount(1).setTotalCount(1).setOk(true).addSchedule(ScheduleObjectMapper.getInstance().readValue(response, SchedulePayload.class)).build();
                    }
                };
                String response = "{\"schedule\":{\"scheduled_tiPe\":\"2015-08-07T22:10:44\"},\"name\":\"Special Scheduled" + ("Push 20\",\"push\":{\"audience\":\"ALL\",\"device_types\":\"all\",\"notification\":{\"alert\":\"Scheduled" + "Push 20\"}},\"push_ids\":[\"274f9aa4-2d00-4911-a043-70129f29adf2\"]}");
                this.listSingleScheduleRequest.getResponseParser().parse(response);
                responseParser.parse(null);
                org.junit.Assert.fail("testListSingleScheduleParsernull21021 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("testListSingleScheduleParsernull21021_failAssert61_literalMutationString21358 should have thrown APIParsingException");
        } catch (APIParsingException expected_1) {
            Assert.assertEquals("Error parsing schedule object. Either scheduled_time, local_scheduled_time, or best time must be set.", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testListSingleScheduleParser_literalMutationString20994_failAssert49() throws Exception {
        try {
            ResponseParser responseParser = new ResponseParser<ListAllSchedulesResponse>() {
                @Override
                public ListAllSchedulesResponse parse(String response) throws IOException {
                    return ListAllSchedulesResponse.newBuilder().setCount(1).setTotalCount(1).setOk(true).addSchedule(ScheduleObjectMapper.getInstance().readValue(response, SchedulePayload.class)).build();
                }
            };
            String response = "{\"schedule\":{\"scheduled_tme\":\"2015-08-07T22:10:44\"},\"name\":\"Special Scheduled" + ("Push 20\",\"push\":{\"audience\":\"ALL\",\"device_types\":\"all\",\"notification\":{\"alert\":\"Scheduled" + "Push 20\"}},\"push_ids\":[\"274f9aa4-2d00-4911-a043-70129f29adf2\"]}");
            listSingleScheduleRequest.getResponseParser().parse(response);
            responseParser.parse(response);
            org.junit.Assert.fail("testListSingleScheduleParser_literalMutationString20994 should have thrown APIParsingException");
        } catch (APIParsingException expected) {
            Assert.assertEquals("Error parsing schedule object. Either scheduled_time, local_scheduled_time, or best time must be set.", expected.getMessage());
        }
    }
}

