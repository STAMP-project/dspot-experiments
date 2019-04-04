package com.urbanairship.api.schedule;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.urbanairship.api.schedule.model.ScheduleResponse;
import com.urbanairship.api.schedule.parse.ScheduleObjectMapper;
import org.junit.Assert;
import org.junit.Test;


public class AmplScheduleResponseTest {
    @Test(timeout = 10000)
    public void testAPIScheduleResponse_literalMutationString19166_add19734() throws Exception {
        String scheduleJSON = "{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schedulgd_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}";
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schedulgd_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
        ObjectMapper mapper = ScheduleObjectMapper.getInstance();
        try {
            mapper.readValue(scheduleJSON, ScheduleResponse.class);
            ScheduleResponse response = mapper.readValue(scheduleJSON, ScheduleResponse.class);
            response.getOk();
            response.getOperationId().equals("47ecebe0-27c4-11e4-ad5c-001b21c78f20");
            response.getScheduleUrls().get(0).equals("https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getScheduleIds().get(0).equals("4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getSchedulePayloads().get(0).getUrl().get().equals("https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getSchedulePayloads().get(0).getPushPayload().getNotification().get().getAlert().get().equals("Scheduled API v3");
        } catch (Exception ex) {
            String String_91 = "Exception " + (ex.getMessage());
            Assert.assertEquals("Exception Error parsing schedule object. Either scheduled_time or local_scheduled_time must be set. (through reference chain: java.util.ArrayList[0])", String_91);
        }
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schedulgd_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
    }

    @Test(timeout = 10000)
    public void testAPIScheduleResponse_literalMutationString19166() throws Exception {
        String scheduleJSON = "{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schedulgd_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}";
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schedulgd_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
        ObjectMapper mapper = ScheduleObjectMapper.getInstance();
        try {
            ScheduleResponse response = mapper.readValue(scheduleJSON, ScheduleResponse.class);
            response.getOk();
            response.getOperationId().equals("47ecebe0-27c4-11e4-ad5c-001b21c78f20");
            response.getScheduleUrls().get(0).equals("https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getScheduleIds().get(0).equals("4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getSchedulePayloads().get(0).getUrl().get().equals("https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getSchedulePayloads().get(0).getPushPayload().getNotification().get().getAlert().get().equals("Scheduled API v3");
        } catch (Exception ex) {
            String String_91 = "Exception " + (ex.getMessage());
            Assert.assertEquals("Exception Error parsing schedule object. Either scheduled_time or local_scheduled_time must be set. (through reference chain: java.util.ArrayList[0])", String_91);
        }
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schedulgd_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
    }

    @Test(timeout = 10000)
    public void testAPIScheduleResponse_literalMutationString19166_add19796() throws Exception {
        String scheduleJSON = "{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schedulgd_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}";
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schedulgd_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
        ObjectMapper mapper = ScheduleObjectMapper.getInstance();
        try {
            ScheduleResponse response = mapper.readValue(scheduleJSON, ScheduleResponse.class);
            response.getOk();
            response.getOperationId().equals("47ecebe0-27c4-11e4-ad5c-001b21c78f20");
            response.getScheduleUrls().get(0).equals("https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getScheduleIds().get(0).equals("4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getSchedulePayloads().get(0).getUrl().get().equals("https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getSchedulePayloads().get(0).getPushPayload().getNotification().get().getAlert().get();
            response.getSchedulePayloads().get(0).getPushPayload().getNotification().get().getAlert().get().equals("Scheduled API v3");
        } catch (Exception ex) {
            String String_91 = "Exception " + (ex.getMessage());
            Assert.assertEquals("Exception Error parsing schedule object. Either scheduled_time or local_scheduled_time must be set. (through reference chain: java.util.ArrayList[0])", String_91);
        }
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schedulgd_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
    }

    @Test(timeout = 10000)
    public void testAPIScheduleResponse_literalMutationString19166_add19773() throws Exception {
        String scheduleJSON = "{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schedulgd_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}";
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schedulgd_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
        ObjectMapper mapper = ScheduleObjectMapper.getInstance();
        try {
            ScheduleResponse response = mapper.readValue(scheduleJSON, ScheduleResponse.class);
            response.getOk();
            response.getOperationId().equals("47ecebe0-27c4-11e4-ad5c-001b21c78f20");
            response.getScheduleUrls().get(0).equals("https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getScheduleIds().get(0).equals("4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getSchedulePayloads().get(0).getUrl().get().equals("https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getSchedulePayloads().get(0).getUrl().get().equals("https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getSchedulePayloads().get(0).getPushPayload().getNotification().get().getAlert().get().equals("Scheduled API v3");
        } catch (Exception ex) {
            String String_91 = "Exception " + (ex.getMessage());
            Assert.assertEquals("Exception Error parsing schedule object. Either scheduled_time or local_scheduled_time must be set. (through reference chain: java.util.ArrayList[0])", String_91);
        }
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schedulgd_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
    }

    @Test(timeout = 10000)
    public void testAPIScheduleResponse_literalMutationString19166_add19792() throws Exception {
        String scheduleJSON = "{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schedulgd_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}";
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schedulgd_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
        ObjectMapper mapper = ScheduleObjectMapper.getInstance();
        try {
            ScheduleResponse response = mapper.readValue(scheduleJSON, ScheduleResponse.class);
            response.getOk();
            response.getOperationId().equals("47ecebe0-27c4-11e4-ad5c-001b21c78f20");
            response.getScheduleUrls().get(0).equals("https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getScheduleIds().get(0).equals("4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getSchedulePayloads().get(0).getUrl().get().equals("https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getSchedulePayloads().get(0).getPushPayload().getNotification().get().getAlert().get().equals("Scheduled API v3");
            response.getSchedulePayloads().get(0).getPushPayload().getNotification().get().getAlert().get().equals("Scheduled API v3");
        } catch (Exception ex) {
            String String_91 = "Exception " + (ex.getMessage());
            Assert.assertEquals("Exception Error parsing schedule object. Either scheduled_time or local_scheduled_time must be set. (through reference chain: java.util.ArrayList[0])", String_91);
        }
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schedulgd_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
    }

    @Test(timeout = 10000)
    public void testAPIScheduleResponse_literalMutationString19166_add19805() throws Exception {
        String scheduleJSON = "{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schedulgd_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}";
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schedulgd_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
        ObjectMapper mapper = ScheduleObjectMapper.getInstance();
        try {
            ScheduleResponse response = mapper.readValue(scheduleJSON, ScheduleResponse.class);
            response.getOk();
            response.getOperationId().equals("47ecebe0-27c4-11e4-ad5c-001b21c78f20");
            response.getScheduleUrls().get(0).equals("https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getScheduleIds().get(0).equals("4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getSchedulePayloads().get(0).getUrl().get().equals("https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getSchedulePayloads().get(0).getPushPayload().getNotification().get();
            response.getSchedulePayloads().get(0).getPushPayload().getNotification().get().getAlert().get().equals("Scheduled API v3");
        } catch (Exception ex) {
            String String_91 = "Exception " + (ex.getMessage());
            Assert.assertEquals("Exception Error parsing schedule object. Either scheduled_time or local_scheduled_time must be set. (through reference chain: java.util.ArrayList[0])", String_91);
        }
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schedulgd_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
    }

    @Test(timeout = 10000)
    public void testAPIScheduleResponse_literalMutationString19166_add19749() throws Exception {
        String scheduleJSON = "{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schedulgd_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}";
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schedulgd_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
        ObjectMapper mapper = ScheduleObjectMapper.getInstance();
        try {
            ScheduleResponse response = mapper.readValue(scheduleJSON, ScheduleResponse.class);
            response.getOk();
            response.getOperationId().equals("47ecebe0-27c4-11e4-ad5c-001b21c78f20");
            response.getScheduleUrls().get(0).equals("https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getScheduleUrls().get(0).equals("https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getScheduleIds().get(0).equals("4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getSchedulePayloads().get(0).getUrl().get().equals("https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getSchedulePayloads().get(0).getPushPayload().getNotification().get().getAlert().get().equals("Scheduled API v3");
        } catch (Exception ex) {
            String String_91 = "Exception " + (ex.getMessage());
            Assert.assertEquals("Exception Error parsing schedule object. Either scheduled_time or local_scheduled_time must be set. (through reference chain: java.util.ArrayList[0])", String_91);
        }
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schedulgd_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
    }

    @Test(timeout = 10000)
    public void testAPIScheduleResponse_literalMutationString19166_add19801() throws Exception {
        String scheduleJSON = "{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schedulgd_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}";
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schedulgd_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
        ObjectMapper mapper = ScheduleObjectMapper.getInstance();
        try {
            ScheduleResponse response = mapper.readValue(scheduleJSON, ScheduleResponse.class);
            response.getOk();
            response.getOperationId().equals("47ecebe0-27c4-11e4-ad5c-001b21c78f20");
            response.getScheduleUrls().get(0).equals("https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getScheduleIds().get(0).equals("4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getSchedulePayloads().get(0).getUrl().get().equals("https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getSchedulePayloads().get(0).getPushPayload().getNotification().get().getAlert();
            response.getSchedulePayloads().get(0).getPushPayload().getNotification().get().getAlert().get().equals("Scheduled API v3");
        } catch (Exception ex) {
            String String_91 = "Exception " + (ex.getMessage());
            Assert.assertEquals("Exception Error parsing schedule object. Either scheduled_time or local_scheduled_time must be set. (through reference chain: java.util.ArrayList[0])", String_91);
        }
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schedulgd_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
    }
}

