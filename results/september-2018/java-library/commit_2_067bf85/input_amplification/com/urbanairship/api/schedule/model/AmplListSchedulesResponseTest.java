package com.urbanairship.api.schedule.model;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.urbanairship.api.push.model.DeviceType;
import com.urbanairship.api.push.model.PushPayload;
import com.urbanairship.api.schedule.parse.ScheduleObjectMapper;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class AmplListSchedulesResponseTest {
    @Test(timeout = 10000)
    public void testAPIScheduleResponse_literalMutationString56930() throws Exception {
        String listscheduleresponse = "{\"ok\":true,\"count\":5,\"total_count\":6,\"schedules\":" + ((((((("[{\"url\":\"https://go.urbanairship.com/api/schedules/5a60e0a6-9aa7-449f-a038-6806e572baf3\",\"" + "schedule\":{\"sched1uled_time\":\"2015-01-01T08:00:00\"},\"push\":{\"audience\":\"ALL\",\"device") + "_types\":[\"android\",\"ios\"],\"notification\":{\"alert\":\"Happy New Year 2015!\",\"android\"") + ":{},\"ios\":{}}},\"push_ids\":[\"8430f2e0-ec07-4c1e-adc4-0c7c7978e648\"]},{\"url\":\"https://go") + ".urbanairship.com/api/schedules/f53aa2bd-018a-4482-8d7d-691d13407973\",\"schedule\":{\"schedule") + "d_time\":\"2016-01-01T08:00:00\"},\"push\":{\"audience\":\"ALL\",\"device_types\":[\"android\",") + "\"ios\"],\"notification\":{\"alert\":\"Happy New Year 2016!\",\"android\":{},\"ios\":{}}},\"pus") + "h_ids\":[\"b217a321-922f-4aee-b239-ca1b58c6b652\"]}]}");
        Assert.assertEquals("{\"ok\":true,\"count\":5,\"total_count\":6,\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/5a60e0a6-9aa7-449f-a038-6806e572baf3\",\"schedule\":{\"sched1uled_time\":\"2015-01-01T08:00:00\"},\"push\":{\"audience\":\"ALL\",\"device_types\":[\"android\",\"ios\"],\"notification\":{\"alert\":\"Happy New Year 2015!\",\"android\":{},\"ios\":{}}},\"push_ids\":[\"8430f2e0-ec07-4c1e-adc4-0c7c7978e648\"]},{\"url\":\"https://go.urbanairship.com/api/schedules/f53aa2bd-018a-4482-8d7d-691d13407973\",\"schedule\":{\"scheduled_time\":\"2016-01-01T08:00:00\"},\"push\":{\"audience\":\"ALL\",\"device_types\":[\"android\",\"ios\"],\"notification\":{\"alert\":\"Happy New Year 2016!\",\"android\":{},\"ios\":{}}},\"push_ids\":[\"b217a321-922f-4aee-b239-ca1b58c6b652\"]}]}", listscheduleresponse);
        ObjectMapper mapper = ScheduleObjectMapper.getInstance();
        try {
            ListAllSchedulesResponse response = mapper.readValue(listscheduleresponse, ListAllSchedulesResponse.class);
            response.getOk();
            boolean boolean_1415 = (response.getCount()) == 5;
            boolean boolean_1416 = (response.getTotal_Count()) == 6;
            boolean boolean_1417 = (response.getNext_Page()) == null;
            List<SchedulePayload> list = response.getSchedules();
            list.get(0).getUrl().get().equals("https://go.urbanairship.com/api/schedules/5a60e0a6-9aa7-449f-a038-6806e572baf3");
            list.get(0).getPushIds().toString().equals("[8430f2e0-ec07-4c1e-adc4-0c7c7978e648]");
            Schedule firstschedule = list.get(0).getSchedule();
            firstschedule.getScheduledTimestamp().toString().equals("2015-01-01T08:00:00.000Z");
            PushPayload firstpush = list.get(0).getPushPayload();
            firstpush.getAudience().getType().getIdentifier().equals("all");
            firstpush.getDeviceTypes().getDeviceTypes().get().contains(DeviceType.IOS);
            firstpush.getDeviceTypes().getDeviceTypes().get().contains(DeviceType.ANDROID);
            firstpush.getNotification().get().getAlert().get().equals("Happy New Year 2015!");
            list.get(1).getUrl().get().equals("https://go.urbanairship.com/api/schedules/f53aa2bd-018a-4482-8d7d-691d13407973");
            list.get(1).getPushIds().toString().equals("[b217a321-922f-4aee-b239-ca1b58c6b652]");
            Schedule secondschedule = list.get(1).getSchedule();
            secondschedule.getScheduledTimestamp().toString().equals("2016-01-01T08:00:00.000Z");
            PushPayload secondpush = list.get(1).getPushPayload();
            secondpush.getAudience().getType().getIdentifier().equals("all");
            secondpush.getDeviceTypes().getDeviceTypes().get().contains(DeviceType.IOS);
            secondpush.getDeviceTypes().getDeviceTypes().get().contains(DeviceType.ANDROID);
            secondpush.getNotification().get().getAlert().get().equals("Happy New Year 2016!");
        } catch (Exception ex) {
            String String_1418 = "Exception " + (ex.getMessage());
            Assert.assertEquals("Exception Error parsing schedule object. Either scheduled_time, local_scheduled_time, or best time must be set. (through reference chain: java.util.ArrayList[0])", String_1418);
        }
        Assert.assertEquals("{\"ok\":true,\"count\":5,\"total_count\":6,\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/5a60e0a6-9aa7-449f-a038-6806e572baf3\",\"schedule\":{\"sched1uled_time\":\"2015-01-01T08:00:00\"},\"push\":{\"audience\":\"ALL\",\"device_types\":[\"android\",\"ios\"],\"notification\":{\"alert\":\"Happy New Year 2015!\",\"android\":{},\"ios\":{}}},\"push_ids\":[\"8430f2e0-ec07-4c1e-adc4-0c7c7978e648\"]},{\"url\":\"https://go.urbanairship.com/api/schedules/f53aa2bd-018a-4482-8d7d-691d13407973\",\"schedule\":{\"scheduled_time\":\"2016-01-01T08:00:00\"},\"push\":{\"audience\":\"ALL\",\"device_types\":[\"android\",\"ios\"],\"notification\":{\"alert\":\"Happy New Year 2016!\",\"android\":{},\"ios\":{}}},\"push_ids\":[\"b217a321-922f-4aee-b239-ca1b58c6b652\"]}]}", listscheduleresponse);
    }
}

