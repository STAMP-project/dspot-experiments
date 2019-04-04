package com.urbanairship.api.schedule;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.urbanairship.api.push.model.DeviceType;
import com.urbanairship.api.push.model.PushPayload;
import com.urbanairship.api.schedule.model.ListAllSchedulesResponse;
import com.urbanairship.api.schedule.model.Schedule;
import com.urbanairship.api.schedule.model.SchedulePayload;
import com.urbanairship.api.schedule.parse.ScheduleObjectMapper;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class AmplListSchedulesResponseTest {
    @Test(timeout = 10000)
    public void testAPIScheduleResponse_literalMutationString38() throws Exception {
        String listscheduleresponse = "{\"ok\":true,\"count\":5,\"total_count\":6,\"schedules\":" + ((((((("[{\"url\":\"https://go.urbanairship.com/api/schedules/5a60e0a6-9aa7-449f-a038-6806e572baf3\",\"" + "schedule\":{\"scheduled_time\":\"2015-01-01T08:00:00\"},\"push\":{\"audience\":\"ALL\",\"device") + "_types\":[\"android\",\"ios\"],\"notification\":{\"alert\":\"Happy New Year 2015!\",\"android\"") + ":{},\"ios\":{}}},\"push_ids\":[\"8430f2e0-ec07-4c1e-adc4-0c7c7978e648\"]},{\"url\":\"https://go") + ".urbanairship.com/api/schedules/f53aa2bd-018a-4482-8d7d-691d13407973\",\"schedule\":{\"schedule") + "h_ids\":[\"b217a321-922f-4aee-b239-ca1b58c6b652\"]}]}") + "\"ios\"],\"notification\":{\"alert\":\"Happy New Year 2016!\",\"android\":{},\"ios\":{}}},\"pus") + "h_ids\":[\"b217a321-922f-4aee-b239-ca1b58c6b652\"]}]}");
        Assert.assertEquals("{\"ok\":true,\"count\":5,\"total_count\":6,\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/5a60e0a6-9aa7-449f-a038-6806e572baf3\",\"schedule\":{\"scheduled_time\":\"2015-01-01T08:00:00\"},\"push\":{\"audience\":\"ALL\",\"device_types\":[\"android\",\"ios\"],\"notification\":{\"alert\":\"Happy New Year 2015!\",\"android\":{},\"ios\":{}}},\"push_ids\":[\"8430f2e0-ec07-4c1e-adc4-0c7c7978e648\"]},{\"url\":\"https://go.urbanairship.com/api/schedules/f53aa2bd-018a-4482-8d7d-691d13407973\",\"schedule\":{\"scheduleh_ids\":[\"b217a321-922f-4aee-b239-ca1b58c6b652\"]}]}\"ios\"],\"notification\":{\"alert\":\"Happy New Year 2016!\",\"android\":{},\"ios\":{}}},\"push_ids\":[\"b217a321-922f-4aee-b239-ca1b58c6b652\"]}]}", listscheduleresponse);
        ObjectMapper mapper = ScheduleObjectMapper.getInstance();
        try {
            ListAllSchedulesResponse response = mapper.readValue(listscheduleresponse, ListAllSchedulesResponse.class);
            response.getOk();
            boolean boolean_163 = (response.getCount()) == 5;
            boolean boolean_164 = (response.getTotal_Count()) == 6;
            boolean boolean_165 = (response.getNext_Page()) == null;
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
            String String_166 = "Exception " + (ex.getMessage());
            Assert.assertEquals("Exception Error parsing schedule object. Either scheduled_time or local_scheduled_time must be set. (through reference chain: java.util.ArrayList[1])", String_166);
        }
        Assert.assertEquals("{\"ok\":true,\"count\":5,\"total_count\":6,\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/5a60e0a6-9aa7-449f-a038-6806e572baf3\",\"schedule\":{\"scheduled_time\":\"2015-01-01T08:00:00\"},\"push\":{\"audience\":\"ALL\",\"device_types\":[\"android\",\"ios\"],\"notification\":{\"alert\":\"Happy New Year 2015!\",\"android\":{},\"ios\":{}}},\"push_ids\":[\"8430f2e0-ec07-4c1e-adc4-0c7c7978e648\"]},{\"url\":\"https://go.urbanairship.com/api/schedules/f53aa2bd-018a-4482-8d7d-691d13407973\",\"schedule\":{\"scheduleh_ids\":[\"b217a321-922f-4aee-b239-ca1b58c6b652\"]}]}\"ios\"],\"notification\":{\"alert\":\"Happy New Year 2016!\",\"android\":{},\"ios\":{}}},\"push_ids\":[\"b217a321-922f-4aee-b239-ca1b58c6b652\"]}]}", listscheduleresponse);
    }

    @Test(timeout = 10000)
    public void testAPIScheduleResponse_literalMutationString16() throws Exception {
        String listscheduleresponse = "{\"ok\":true,\"count\":5,\"total_count\":6,\"schedules\":" + ((((((("[{\"url\":\"https://go.urbanairship.com/api/schedules/5a60e0a6-9aa7-449f-a038-6806e572baf3\",\"" + "schedule\":{\"scheduledxtime\":\"2015-01-01T08:00:00\"},\"push\":{\"audience\":\"ALL\",\"device") + "_types\":[\"android\",\"ios\"],\"notification\":{\"alert\":\"Happy New Year 2015!\",\"android\"") + ":{},\"ios\":{}}},\"push_ids\":[\"8430f2e0-ec07-4c1e-adc4-0c7c7978e648\"]},{\"url\":\"https://go") + ".urbanairship.com/api/schedules/f53aa2bd-018a-4482-8d7d-691d13407973\",\"schedule\":{\"schedule") + "d_time\":\"2016-01-01T08:00:00\"},\"push\":{\"audience\":\"ALL\",\"device_types\":[\"android\",") + "\"ios\"],\"notification\":{\"alert\":\"Happy New Year 2016!\",\"android\":{},\"ios\":{}}},\"pus") + "h_ids\":[\"b217a321-922f-4aee-b239-ca1b58c6b652\"]}]}");
        Assert.assertEquals("{\"ok\":true,\"count\":5,\"total_count\":6,\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/5a60e0a6-9aa7-449f-a038-6806e572baf3\",\"schedule\":{\"scheduledxtime\":\"2015-01-01T08:00:00\"},\"push\":{\"audience\":\"ALL\",\"device_types\":[\"android\",\"ios\"],\"notification\":{\"alert\":\"Happy New Year 2015!\",\"android\":{},\"ios\":{}}},\"push_ids\":[\"8430f2e0-ec07-4c1e-adc4-0c7c7978e648\"]},{\"url\":\"https://go.urbanairship.com/api/schedules/f53aa2bd-018a-4482-8d7d-691d13407973\",\"schedule\":{\"scheduled_time\":\"2016-01-01T08:00:00\"},\"push\":{\"audience\":\"ALL\",\"device_types\":[\"android\",\"ios\"],\"notification\":{\"alert\":\"Happy New Year 2016!\",\"android\":{},\"ios\":{}}},\"push_ids\":[\"b217a321-922f-4aee-b239-ca1b58c6b652\"]}]}", listscheduleresponse);
        ObjectMapper mapper = ScheduleObjectMapper.getInstance();
        try {
            ListAllSchedulesResponse response = mapper.readValue(listscheduleresponse, ListAllSchedulesResponse.class);
            response.getOk();
            boolean boolean_735 = (response.getCount()) == 5;
            boolean boolean_736 = (response.getTotal_Count()) == 6;
            boolean boolean_737 = (response.getNext_Page()) == null;
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
            String String_738 = "Exception " + (ex.getMessage());
            Assert.assertEquals("Exception Error parsing schedule object. Either scheduled_time or local_scheduled_time must be set. (through reference chain: java.util.ArrayList[0])", String_738);
        }
        Assert.assertEquals("{\"ok\":true,\"count\":5,\"total_count\":6,\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/5a60e0a6-9aa7-449f-a038-6806e572baf3\",\"schedule\":{\"scheduledxtime\":\"2015-01-01T08:00:00\"},\"push\":{\"audience\":\"ALL\",\"device_types\":[\"android\",\"ios\"],\"notification\":{\"alert\":\"Happy New Year 2015!\",\"android\":{},\"ios\":{}}},\"push_ids\":[\"8430f2e0-ec07-4c1e-adc4-0c7c7978e648\"]},{\"url\":\"https://go.urbanairship.com/api/schedules/f53aa2bd-018a-4482-8d7d-691d13407973\",\"schedule\":{\"scheduled_time\":\"2016-01-01T08:00:00\"},\"push\":{\"audience\":\"ALL\",\"device_types\":[\"android\",\"ios\"],\"notification\":{\"alert\":\"Happy New Year 2016!\",\"android\":{},\"ios\":{}}},\"push_ids\":[\"b217a321-922f-4aee-b239-ca1b58c6b652\"]}]}", listscheduleresponse);
    }

    @Test(timeout = 10000)
    public void testAPIScheduleResponse_literalMutationString59144() throws Exception {
        String listscheduleresponse = "{\"ok\":true,\"count\":5,\"total_count\":6,\"schedules\":" + ((((((("[{\"url\":\"https://go.urbanairship.com/api/schedules/5a60e0a6-9aa7-449f-a038-6806e572baf3\",\"" + "schedule\":{\"scheduled_time\":\"2015-01-01T08:00:00\"},\"push\":{\"audience\":\"ALL\",\"device") + "_types\":[\"android\",\"ios\"],\"notification\":{\"alert\":\"Happy New Year 2015!\",\"android\"") + ":{},\"ios\":{}}},\"push_ids\":[\"8430f2e0-ec07-4c1e-adc4-0c7c7978e648\"]},{\"url\":\"https://go") + ".urbanairship.com/api/schedules/f53aa2bd-018a-4482-8d7d-691d13407973\",\"schedule\":{\"schedule") + "h_ids\":[\"b217a321-922f-4aee-b239-ca1b58c6b652\"]}]}") + "\"ios\"],\"notification\":{\"alert\":\"Happy New Year 2016!\",\"android\":{},\"ios\":{}}},\"pus") + "h_ids\":[\"b217a321-922f-4aee-b239-ca1b58c6b652\"]}]}");
        Assert.assertEquals("{\"ok\":true,\"count\":5,\"total_count\":6,\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/5a60e0a6-9aa7-449f-a038-6806e572baf3\",\"schedule\":{\"scheduled_time\":\"2015-01-01T08:00:00\"},\"push\":{\"audience\":\"ALL\",\"device_types\":[\"android\",\"ios\"],\"notification\":{\"alert\":\"Happy New Year 2015!\",\"android\":{},\"ios\":{}}},\"push_ids\":[\"8430f2e0-ec07-4c1e-adc4-0c7c7978e648\"]},{\"url\":\"https://go.urbanairship.com/api/schedules/f53aa2bd-018a-4482-8d7d-691d13407973\",\"schedule\":{\"scheduleh_ids\":[\"b217a321-922f-4aee-b239-ca1b58c6b652\"]}]}\"ios\"],\"notification\":{\"alert\":\"Happy New Year 2016!\",\"android\":{},\"ios\":{}}},\"push_ids\":[\"b217a321-922f-4aee-b239-ca1b58c6b652\"]}]}", listscheduleresponse);
        ObjectMapper mapper = ScheduleObjectMapper.getInstance();
        try {
            ListAllSchedulesResponse response = mapper.readValue(listscheduleresponse, ListAllSchedulesResponse.class);
            response.getOk();
            boolean boolean_967 = (response.getCount()) == 5;
            boolean boolean_968 = (response.getTotal_Count()) == 6;
            boolean boolean_969 = (response.getNext_Page()) == null;
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
            String String_970 = "Exception " + (ex.getMessage());
            Assert.assertEquals("Exception Error parsing schedule object. Either scheduled_time or local_scheduled_time must be set. (through reference chain: java.util.ArrayList[1])", String_970);
        }
        Assert.assertEquals("{\"ok\":true,\"count\":5,\"total_count\":6,\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/5a60e0a6-9aa7-449f-a038-6806e572baf3\",\"schedule\":{\"scheduled_time\":\"2015-01-01T08:00:00\"},\"push\":{\"audience\":\"ALL\",\"device_types\":[\"android\",\"ios\"],\"notification\":{\"alert\":\"Happy New Year 2015!\",\"android\":{},\"ios\":{}}},\"push_ids\":[\"8430f2e0-ec07-4c1e-adc4-0c7c7978e648\"]},{\"url\":\"https://go.urbanairship.com/api/schedules/f53aa2bd-018a-4482-8d7d-691d13407973\",\"schedule\":{\"scheduleh_ids\":[\"b217a321-922f-4aee-b239-ca1b58c6b652\"]}]}\"ios\"],\"notification\":{\"alert\":\"Happy New Year 2016!\",\"android\":{},\"ios\":{}}},\"push_ids\":[\"b217a321-922f-4aee-b239-ca1b58c6b652\"]}]}", listscheduleresponse);
    }

    @Test(timeout = 10000)
    public void testAPIScheduleResponseWithNextPage_literalMutationString117463() throws Exception {
        String listscheduleresponse = "{\"ok\":true,\"count\":5,\"total_count\":6,\"next_page\":\"puppies\",\"schedules\":" + ((((((("[{\"url\":\"https://go.urbanairship.com/api/schedules/5a60e0a6-9aa7-449f-a038-6806e572baf3\",\"" + "schedule\":{\"scheduled_time\":\"2015-01-01T08:00:00\"},\"push\":{\"audience\":\"ALL\",\"device") + "_types\":[\"android\",\"ios\"],\"notification\":{\"alert\":\"Happy New Year 2015!\",\"android\"") + ":{},\"ios\":{}}},\"push_ids\":[\"8430f2e0-ec07-4c1e-adc4-0c7c7978e648\"]},{\"url\":\"https://go") + ".urbanairship.com/api/schedules/f53aa2bd-018a-4482-8d7d-691d13407973\",\"schedule\":{\"schedule") + "h_ids\":[\"b217a321-922f-4aee-b239-ca1b58c6b652\"]}]}") + "\"ios\"],\"notification\":{\"alert\":\"Happy New Year 2016!\",\"android\":{},\"ios\":{}}},\"pus") + "h_ids\":[\"b217a321-922f-4aee-b239-ca1b58c6b652\"]}]}");
        Assert.assertEquals("{\"ok\":true,\"count\":5,\"total_count\":6,\"next_page\":\"puppies\",\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/5a60e0a6-9aa7-449f-a038-6806e572baf3\",\"schedule\":{\"scheduled_time\":\"2015-01-01T08:00:00\"},\"push\":{\"audience\":\"ALL\",\"device_types\":[\"android\",\"ios\"],\"notification\":{\"alert\":\"Happy New Year 2015!\",\"android\":{},\"ios\":{}}},\"push_ids\":[\"8430f2e0-ec07-4c1e-adc4-0c7c7978e648\"]},{\"url\":\"https://go.urbanairship.com/api/schedules/f53aa2bd-018a-4482-8d7d-691d13407973\",\"schedule\":{\"scheduleh_ids\":[\"b217a321-922f-4aee-b239-ca1b58c6b652\"]}]}\"ios\"],\"notification\":{\"alert\":\"Happy New Year 2016!\",\"android\":{},\"ios\":{}}},\"push_ids\":[\"b217a321-922f-4aee-b239-ca1b58c6b652\"]}]}", listscheduleresponse);
        ObjectMapper mapper = ScheduleObjectMapper.getInstance();
        try {
            ListAllSchedulesResponse response = mapper.readValue(listscheduleresponse, ListAllSchedulesResponse.class);
            response.getOk();
            boolean boolean_1728 = (response.getCount()) == 5;
            boolean boolean_1729 = (response.getTotal_Count()) == 6;
            response.getNext_Page().equals("puppies");
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
            String String_1730 = "Exception " + (ex.getMessage());
            Assert.assertEquals("Exception Error parsing schedule object. Either scheduled_time or local_scheduled_time must be set. (through reference chain: java.util.ArrayList[1])", String_1730);
        }
        Assert.assertEquals("{\"ok\":true,\"count\":5,\"total_count\":6,\"next_page\":\"puppies\",\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/5a60e0a6-9aa7-449f-a038-6806e572baf3\",\"schedule\":{\"scheduled_time\":\"2015-01-01T08:00:00\"},\"push\":{\"audience\":\"ALL\",\"device_types\":[\"android\",\"ios\"],\"notification\":{\"alert\":\"Happy New Year 2015!\",\"android\":{},\"ios\":{}}},\"push_ids\":[\"8430f2e0-ec07-4c1e-adc4-0c7c7978e648\"]},{\"url\":\"https://go.urbanairship.com/api/schedules/f53aa2bd-018a-4482-8d7d-691d13407973\",\"schedule\":{\"scheduleh_ids\":[\"b217a321-922f-4aee-b239-ca1b58c6b652\"]}]}\"ios\"],\"notification\":{\"alert\":\"Happy New Year 2016!\",\"android\":{},\"ios\":{}}},\"push_ids\":[\"b217a321-922f-4aee-b239-ca1b58c6b652\"]}]}", listscheduleresponse);
    }

    @Test(timeout = 10000)
    public void testAPIScheduleResponseWithNextPage_literalMutationString117443() throws Exception {
        String listscheduleresponse = "{\"ok\":true,\"count\":5,\"total_count\":6,\"next_page\":\"puppies\",\"schedules\":" + ((((((("[{\"url\":\"https://go.urbanairship.com/api/schedules/5a60e0a6-9aa7-449f-a038-6806e572baf3\",\"" + "schedule\":{\"scheduled_bime\":\"2015-01-01T08:00:00\"},\"push\":{\"audience\":\"ALL\",\"device") + "_types\":[\"android\",\"ios\"],\"notification\":{\"alert\":\"Happy New Year 2015!\",\"android\"") + ":{},\"ios\":{}}},\"push_ids\":[\"8430f2e0-ec07-4c1e-adc4-0c7c7978e648\"]},{\"url\":\"https://go") + ".urbanairship.com/api/schedules/f53aa2bd-018a-4482-8d7d-691d13407973\",\"schedule\":{\"schedule") + "d_time\":\"2016-01-01T08:00:00\"},\"push\":{\"audience\":\"ALL\",\"device_types\":[\"android\",") + "\"ios\"],\"notification\":{\"alert\":\"Happy New Year 2016!\",\"android\":{},\"ios\":{}}},\"pus") + "h_ids\":[\"b217a321-922f-4aee-b239-ca1b58c6b652\"]}]}");
        Assert.assertEquals("{\"ok\":true,\"count\":5,\"total_count\":6,\"next_page\":\"puppies\",\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/5a60e0a6-9aa7-449f-a038-6806e572baf3\",\"schedule\":{\"scheduled_bime\":\"2015-01-01T08:00:00\"},\"push\":{\"audience\":\"ALL\",\"device_types\":[\"android\",\"ios\"],\"notification\":{\"alert\":\"Happy New Year 2015!\",\"android\":{},\"ios\":{}}},\"push_ids\":[\"8430f2e0-ec07-4c1e-adc4-0c7c7978e648\"]},{\"url\":\"https://go.urbanairship.com/api/schedules/f53aa2bd-018a-4482-8d7d-691d13407973\",\"schedule\":{\"scheduled_time\":\"2016-01-01T08:00:00\"},\"push\":{\"audience\":\"ALL\",\"device_types\":[\"android\",\"ios\"],\"notification\":{\"alert\":\"Happy New Year 2016!\",\"android\":{},\"ios\":{}}},\"push_ids\":[\"b217a321-922f-4aee-b239-ca1b58c6b652\"]}]}", listscheduleresponse);
        ObjectMapper mapper = ScheduleObjectMapper.getInstance();
        try {
            ListAllSchedulesResponse response = mapper.readValue(listscheduleresponse, ListAllSchedulesResponse.class);
            response.getOk();
            boolean boolean_2112 = (response.getCount()) == 5;
            boolean boolean_2113 = (response.getTotal_Count()) == 6;
            response.getNext_Page().equals("puppies");
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
            String String_2114 = "Exception " + (ex.getMessage());
            Assert.assertEquals("Exception Error parsing schedule object. Either scheduled_time or local_scheduled_time must be set. (through reference chain: java.util.ArrayList[0])", String_2114);
        }
        Assert.assertEquals("{\"ok\":true,\"count\":5,\"total_count\":6,\"next_page\":\"puppies\",\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/5a60e0a6-9aa7-449f-a038-6806e572baf3\",\"schedule\":{\"scheduled_bime\":\"2015-01-01T08:00:00\"},\"push\":{\"audience\":\"ALL\",\"device_types\":[\"android\",\"ios\"],\"notification\":{\"alert\":\"Happy New Year 2015!\",\"android\":{},\"ios\":{}}},\"push_ids\":[\"8430f2e0-ec07-4c1e-adc4-0c7c7978e648\"]},{\"url\":\"https://go.urbanairship.com/api/schedules/f53aa2bd-018a-4482-8d7d-691d13407973\",\"schedule\":{\"scheduled_time\":\"2016-01-01T08:00:00\"},\"push\":{\"audience\":\"ALL\",\"device_types\":[\"android\",\"ios\"],\"notification\":{\"alert\":\"Happy New Year 2016!\",\"android\":{},\"ios\":{}}},\"push_ids\":[\"b217a321-922f-4aee-b239-ca1b58c6b652\"]}]}", listscheduleresponse);
    }
}

