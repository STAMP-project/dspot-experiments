package com.urbanairship.api.schedule.model;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.urbanairship.api.schedule.parse.ScheduleObjectMapper;
import org.junit.Assert;
import org.junit.Test;


public class AmplScheduleResponseTest {
    @Test(timeout = 10000)
    public void testAPIScheduleResponse_literalMutationString6_literalMutationString572() throws Exception {
        String scheduleJSON = "{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}";
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
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
            String String_7 = "ExceVption " + (ex.getMessage());
            Assert.assertEquals("ExceVption Error parsing schedule object. Either scheduled_time, local_scheduled_time, or best time must be set. (through reference chain: java.util.ArrayList[0])", String_7);
        }
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
    }

    @Test(timeout = 10000)
    public void testAPIScheduleResponse_literalMutationString6_literalMutationString372() throws Exception {
        String scheduleJSON = "{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-491z7-8e05-4443cf4e9575\"]}]}";
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-491z7-8e05-4443cf4e9575\"]}]}", scheduleJSON);
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
            String String_7 = "Exception " + (ex.getMessage());
            Assert.assertEquals("Exception Error parsing schedule object. Either scheduled_time, local_scheduled_time, or best time must be set. (through reference chain: java.util.ArrayList[0])", String_7);
        }
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-491z7-8e05-4443cf4e9575\"]}]}", scheduleJSON);
    }

    @Test(timeout = 10000)
    public void testAPIScheduleResponse_literalMutationString6_literalMutationString432() throws Exception {
        String scheduleJSON = "{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}";
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
        ObjectMapper mapper = ScheduleObjectMapper.getInstance();
        try {
            ScheduleResponse response = mapper.readValue(scheduleJSON, ScheduleResponse.class);
            response.getOk();
            response.getOperationId().equals("47ecebe0-27c4-11e4-ad5c-001b21c78f20");
            response.getScheduleUrls().get(0).equals("https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fer4-873809acbd87");
            response.getScheduleIds().get(0).equals("4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getSchedulePayloads().get(0).getUrl().get().equals("https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getSchedulePayloads().get(0).getPushPayload().getNotification().get().getAlert().get().equals("Scheduled API v3");
        } catch (Exception ex) {
            String String_7 = "Exception " + (ex.getMessage());
            Assert.assertEquals("Exception Error parsing schedule object. Either scheduled_time, local_scheduled_time, or best time must be set. (through reference chain: java.util.ArrayList[0])", String_7);
        }
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
    }

    @Test(timeout = 10000)
    public void testAPIScheduleResponse_literalMutationString6_literalMutationString576() throws Exception {
        String scheduleJSON = "{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}";
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
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
            String String_7 = "Exczption " + (ex.getMessage());
            Assert.assertEquals("Exczption Error parsing schedule object. Either scheduled_time, local_scheduled_time, or best time must be set. (through reference chain: java.util.ArrayList[0])", String_7);
        }
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
    }

    @Test(timeout = 10000)
    public void testAPIScheduleResponse_literalMutationString6_literalMutationString475() throws Exception {
        String scheduleJSON = "{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}";
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
        ObjectMapper mapper = ScheduleObjectMapper.getInstance();
        try {
            ScheduleResponse response = mapper.readValue(scheduleJSON, ScheduleResponse.class);
            response.getOk();
            response.getOperationId().equals("47ecebe0-27c4-11e4-ad5c-001b21c78f20");
            response.getScheduleUrls().get(0).equals("https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getScheduleIds().get(0).equals("@9^Z,v.!sMDn(/Gj6P]@DOYZ!M|gJ2EWkqL-");
            response.getSchedulePayloads().get(0).getUrl().get().equals("https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getSchedulePayloads().get(0).getPushPayload().getNotification().get().getAlert().get().equals("Scheduled API v3");
        } catch (Exception ex) {
            String String_7 = "Exception " + (ex.getMessage());
            Assert.assertEquals("Exception Error parsing schedule object. Either scheduled_time, local_scheduled_time, or best time must be set. (through reference chain: java.util.ArrayList[0])", String_7);
        }
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
    }

    @Test(timeout = 10000)
    public void testAPIScheduleResponse_literalMutationString6_literalMutationString552() throws Exception {
        String scheduleJSON = "{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}";
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
        ObjectMapper mapper = ScheduleObjectMapper.getInstance();
        try {
            ScheduleResponse response = mapper.readValue(scheduleJSON, ScheduleResponse.class);
            response.getOk();
            response.getOperationId().equals("47ecebe0-27c4-11e4-ad5c-001b21c78f20");
            response.getScheduleUrls().get(0).equals("https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getScheduleIds().get(0).equals("4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getSchedulePayloads().get(0).getUrl().get().equals("https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getSchedulePayloads().get(0).getPushPayload().getNotification().get().getAlert().get().equals("ScheduledBAPI v3");
        } catch (Exception ex) {
            String String_7 = "Exception " + (ex.getMessage());
            Assert.assertEquals("Exception Error parsing schedule object. Either scheduled_time, local_scheduled_time, or best time must be set. (through reference chain: java.util.ArrayList[0])", String_7);
        }
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
    }

    @Test(timeout = 10000)
    public void testAPIScheduleResponse_literalMutationString6_literalMutationString392() throws Exception {
        String scheduleJSON = "{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}";
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
        ObjectMapper mapper = ScheduleObjectMapper.getInstance();
        try {
            ScheduleResponse response = mapper.readValue(scheduleJSON, ScheduleResponse.class);
            response.getOk();
            response.getOperationId().equals("`lHNxGXGv&Wfky$p_0p/:=5,:t:tL(oN8DTS");
            response.getScheduleUrls().get(0).equals("https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getScheduleIds().get(0).equals("4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getSchedulePayloads().get(0).getUrl().get().equals("https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getSchedulePayloads().get(0).getPushPayload().getNotification().get().getAlert().get().equals("Scheduled API v3");
        } catch (Exception ex) {
            String String_7 = "Exception " + (ex.getMessage());
            Assert.assertEquals("Exception Error parsing schedule object. Either scheduled_time, local_scheduled_time, or best time must be set. (through reference chain: java.util.ArrayList[0])", String_7);
        }
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
    }

    @Test(timeout = 10000)
    public void testAPIScheduleResponse_literalMutationString6_add670() throws Exception {
        String scheduleJSON = "{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}";
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
        ObjectMapper mapper = ScheduleObjectMapper.getInstance();
        try {
            ScheduleResponse response = mapper.readValue(scheduleJSON, ScheduleResponse.class);
            response.getOk();
            response.getOperationId().equals("47ecebe0-27c4-11e4-ad5c-001b21c78f20");
            response.getScheduleUrls().get(0).equals("https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getScheduleIds().get(0).equals("4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getSchedulePayloads().get(0).getUrl().get().equals("https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getSchedulePayloads().get(0).getPushPayload().getNotification();
            response.getSchedulePayloads().get(0).getPushPayload().getNotification().get().getAlert().get().equals("Scheduled API v3");
        } catch (Exception ex) {
            String String_7 = "Exception " + (ex.getMessage());
            Assert.assertEquals("Exception Error parsing schedule object. Either scheduled_time, local_scheduled_time, or best time must be set. (through reference chain: java.util.ArrayList[0])", String_7);
        }
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
    }

    @Test(timeout = 10000)
    public void testAPIScheduleResponse_literalMutationString6_add593() throws Exception {
        String scheduleJSON = "{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}";
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
        ScheduleObjectMapper.getInstance();
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
            String String_7 = "Exception " + (ex.getMessage());
            Assert.assertEquals("Exception Error parsing schedule object. Either scheduled_time, local_scheduled_time, or best time must be set. (through reference chain: java.util.ArrayList[0])", String_7);
        }
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
    }

    @Test(timeout = 10000)
    public void testAPIScheduleResponse_literalMutationString6_literalMutationNumber412() throws Exception {
        String scheduleJSON = "{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}";
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
        ObjectMapper mapper = ScheduleObjectMapper.getInstance();
        try {
            ScheduleResponse response = mapper.readValue(scheduleJSON, ScheduleResponse.class);
            response.getOk();
            response.getOperationId().equals("47ecebe0-27c4-11e4-ad5c-001b21c78f20");
            response.getScheduleUrls().get(-1).equals("https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getScheduleIds().get(0).equals("4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getSchedulePayloads().get(0).getUrl().get().equals("https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getSchedulePayloads().get(0).getPushPayload().getNotification().get().getAlert().get().equals("Scheduled API v3");
        } catch (Exception ex) {
            String String_7 = "Exception " + (ex.getMessage());
            Assert.assertEquals("Exception Error parsing schedule object. Either scheduled_time, local_scheduled_time, or best time must be set. (through reference chain: java.util.ArrayList[0])", String_7);
        }
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
    }

    @Test(timeout = 10000)
    public void testAPIScheduleResponse_literalMutationString6_add597() throws Exception {
        String scheduleJSON = "{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}";
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
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
            String String_7 = "Exception " + (ex.getMessage());
            Assert.assertEquals("Exception Error parsing schedule object. Either scheduled_time, local_scheduled_time, or best time must be set. (through reference chain: java.util.ArrayList[0])", String_7);
        }
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
    }

    @Test(timeout = 10000)
    public void testAPIScheduleResponse_literalMutationString6_add675() throws Exception {
        String scheduleJSON = "{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}";
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
        ObjectMapper mapper = ScheduleObjectMapper.getInstance();
        try {
            ScheduleResponse response = mapper.readValue(scheduleJSON, ScheduleResponse.class);
            response.getOk();
            response.getOperationId().equals("47ecebe0-27c4-11e4-ad5c-001b21c78f20");
            response.getScheduleUrls().get(0).equals("https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getScheduleIds().get(0).equals("4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getSchedulePayloads().get(0).getUrl().get().equals("https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getSchedulePayloads().get(0).getPushPayload();
            response.getSchedulePayloads().get(0).getPushPayload().getNotification().get().getAlert().get().equals("Scheduled API v3");
        } catch (Exception ex) {
            String String_7 = "Exception " + (ex.getMessage());
            Assert.assertEquals("Exception Error parsing schedule object. Either scheduled_time, local_scheduled_time, or best time must be set. (through reference chain: java.util.ArrayList[0])", String_7);
        }
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
    }

    @Test(timeout = 10000)
    public void testAPIScheduleResponse_literalMutationString6_add631() throws Exception {
        String scheduleJSON = "{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}";
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
        ObjectMapper mapper = ScheduleObjectMapper.getInstance();
        try {
            ScheduleResponse response = mapper.readValue(scheduleJSON, ScheduleResponse.class);
            response.getOk();
            response.getOperationId().equals("47ecebe0-27c4-11e4-ad5c-001b21c78f20");
            response.getScheduleUrls().get(0).equals("https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getScheduleIds();
            response.getScheduleIds().get(0).equals("4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getSchedulePayloads().get(0).getUrl().get().equals("https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getSchedulePayloads().get(0).getPushPayload().getNotification().get().getAlert().get().equals("Scheduled API v3");
        } catch (Exception ex) {
            String String_7 = "Exception " + (ex.getMessage());
            Assert.assertEquals("Exception Error parsing schedule object. Either scheduled_time, local_scheduled_time, or best time must be set. (through reference chain: java.util.ArrayList[0])", String_7);
        }
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
    }

    @Test(timeout = 10000)
    public void testAPIScheduleResponse_literalMutationString6_add612() throws Exception {
        String scheduleJSON = "{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}";
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
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
            String String_7 = "Exception " + (ex.getMessage());
            Assert.assertEquals("Exception Error parsing schedule object. Either scheduled_time, local_scheduled_time, or best time must be set. (through reference chain: java.util.ArrayList[0])", String_7);
        }
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
    }

    @Test(timeout = 10000)
    public void testAPIScheduleResponse_literalMutationString6_literalMutationString568() throws Exception {
        String scheduleJSON = "{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}";
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
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
            String String_7 = ", schedulePayload=" + (ex.getMessage());
            Assert.assertEquals(", schedulePayload=Error parsing schedule object. Either scheduled_time, local_scheduled_time, or best time must be set. (through reference chain: java.util.ArrayList[0])", String_7);
        }
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
    }

    @Test(timeout = 10000)
    public void testAPIScheduleResponse_literalMutationString6_literalMutationString546() throws Exception {
        String scheduleJSON = "{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}";
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
        ObjectMapper mapper = ScheduleObjectMapper.getInstance();
        try {
            ScheduleResponse response = mapper.readValue(scheduleJSON, ScheduleResponse.class);
            response.getOk();
            response.getOperationId().equals("47ecebe0-27c4-11e4-ad5c-001b21c78f20");
            response.getScheduleUrls().get(0).equals("https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getScheduleIds().get(0).equals("4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getSchedulePayloads().get(0).getUrl().get().equals("https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getSchedulePayloads().get(0).getPushPayload().getNotification().get().getAlert().get().equals(", schedulePayload=");
        } catch (Exception ex) {
            String String_7 = "Exception " + (ex.getMessage());
            Assert.assertEquals("Exception Error parsing schedule object. Either scheduled_time, local_scheduled_time, or best time must be set. (through reference chain: java.util.ArrayList[0])", String_7);
        }
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
    }

    @Test(timeout = 10000)
    public void testAPIScheduleResponse_literalMutationString6_add635() throws Exception {
        String scheduleJSON = "{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}";
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
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
            String String_7 = "Exception " + (ex.getMessage());
            Assert.assertEquals("Exception Error parsing schedule object. Either scheduled_time, local_scheduled_time, or best time must be set. (through reference chain: java.util.ArrayList[0])", String_7);
        }
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
    }

    @Test(timeout = 10000)
    public void testAPIScheduleResponse_literalMutationString6_add679() throws Exception {
        String scheduleJSON = "{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}";
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
        ObjectMapper mapper = ScheduleObjectMapper.getInstance();
        try {
            ScheduleResponse response = mapper.readValue(scheduleJSON, ScheduleResponse.class);
            response.getOk();
            response.getOperationId().equals("47ecebe0-27c4-11e4-ad5c-001b21c78f20");
            response.getScheduleUrls().get(0).equals("https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getScheduleIds().get(0).equals("4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getSchedulePayloads().get(0).getUrl().get().equals("https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getSchedulePayloads();
            response.getSchedulePayloads().get(0).getPushPayload().getNotification().get().getAlert().get().equals("Scheduled API v3");
        } catch (Exception ex) {
            String String_7 = "Exception " + (ex.getMessage());
            Assert.assertEquals("Exception Error parsing schedule object. Either scheduled_time, local_scheduled_time, or best time must be set. (through reference chain: java.util.ArrayList[0])", String_7);
        }
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
    }

    @Test(timeout = 10000)
    public void testAPIScheduleResponse_literalMutationString6_literalMutationString404() throws Exception {
        String scheduleJSON = "{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}";
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
        ObjectMapper mapper = ScheduleObjectMapper.getInstance();
        try {
            ScheduleResponse response = mapper.readValue(scheduleJSON, ScheduleResponse.class);
            response.getOk();
            response.getOperationId().equals("47ecyebe0-27c4-11e4-ad5c-001b21c78f20");
            response.getScheduleUrls().get(0).equals("https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getScheduleIds().get(0).equals("4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getSchedulePayloads().get(0).getUrl().get().equals("https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getSchedulePayloads().get(0).getPushPayload().getNotification().get().getAlert().get().equals("Scheduled API v3");
        } catch (Exception ex) {
            String String_7 = "Exception " + (ex.getMessage());
            Assert.assertEquals("Exception Error parsing schedule object. Either scheduled_time, local_scheduled_time, or best time must be set. (through reference chain: java.util.ArrayList[0])", String_7);
        }
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
    }

    @Test(timeout = 10000)
    public void testAPIScheduleResponse_literalMutationString6_add654() throws Exception {
        String scheduleJSON = "{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}";
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
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
            String String_7 = "Exception " + (ex.getMessage());
            Assert.assertEquals("Exception Error parsing schedule object. Either scheduled_time, local_scheduled_time, or best time must be set. (through reference chain: java.util.ArrayList[0])", String_7);
        }
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
    }

    @Test(timeout = 10000)
    public void testAPIScheduleResponse_literalMutationString6_literalMutationNumber492() throws Exception {
        String scheduleJSON = "{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}";
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
        ObjectMapper mapper = ScheduleObjectMapper.getInstance();
        try {
            ScheduleResponse response = mapper.readValue(scheduleJSON, ScheduleResponse.class);
            response.getOk();
            response.getOperationId().equals("47ecebe0-27c4-11e4-ad5c-001b21c78f20");
            response.getScheduleUrls().get(0).equals("https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getScheduleIds().get(0).equals("4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getSchedulePayloads().get(-1).getUrl().get().equals("https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getSchedulePayloads().get(0).getPushPayload().getNotification().get().getAlert().get().equals("Scheduled API v3");
        } catch (Exception ex) {
            String String_7 = "Exception " + (ex.getMessage());
            Assert.assertEquals("Exception Error parsing schedule object. Either scheduled_time, local_scheduled_time, or best time must be set. (through reference chain: java.util.ArrayList[0])", String_7);
        }
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
    }

    @Test(timeout = 10000)
    public void testAPIScheduleResponse_literalMutationString6_add677() throws Exception {
        String scheduleJSON = "{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}";
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
        ObjectMapper mapper = ScheduleObjectMapper.getInstance();
        try {
            ScheduleResponse response = mapper.readValue(scheduleJSON, ScheduleResponse.class);
            response.getOk();
            response.getOperationId().equals("47ecebe0-27c4-11e4-ad5c-001b21c78f20");
            response.getScheduleUrls().get(0).equals("https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getScheduleIds().get(0).equals("4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getSchedulePayloads().get(0).getUrl().get().equals("https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getSchedulePayloads().get(0);
            response.getSchedulePayloads().get(0).getPushPayload().getNotification().get().getAlert().get().equals("Scheduled API v3");
        } catch (Exception ex) {
            String String_7 = "Exception " + (ex.getMessage());
            Assert.assertEquals("Exception Error parsing schedule object. Either scheduled_time, local_scheduled_time, or best time must be set. (through reference chain: java.util.ArrayList[0])", String_7);
        }
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
    }

    @Test(timeout = 10000)
    public void testAPIScheduleResponse_literalMutationString6_add638() throws Exception {
        String scheduleJSON = "{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}";
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
        ObjectMapper mapper = ScheduleObjectMapper.getInstance();
        try {
            ScheduleResponse response = mapper.readValue(scheduleJSON, ScheduleResponse.class);
            response.getOk();
            response.getOperationId().equals("47ecebe0-27c4-11e4-ad5c-001b21c78f20");
            response.getScheduleUrls().get(0).equals("https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getScheduleIds().get(0).equals("4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getSchedulePayloads().get(0).getUrl().get();
            response.getSchedulePayloads().get(0).getUrl().get().equals("https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getSchedulePayloads().get(0).getPushPayload().getNotification().get().getAlert().get().equals("Scheduled API v3");
        } catch (Exception ex) {
            String String_7 = "Exception " + (ex.getMessage());
            Assert.assertEquals("Exception Error parsing schedule object. Either scheduled_time, local_scheduled_time, or best time must be set. (through reference chain: java.util.ArrayList[0])", String_7);
        }
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
    }

    @Test(timeout = 10000)
    public void testAPIScheduleResponse_literalMutationString6_add616() throws Exception {
        String scheduleJSON = "{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}";
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
        ObjectMapper mapper = ScheduleObjectMapper.getInstance();
        try {
            ScheduleResponse response = mapper.readValue(scheduleJSON, ScheduleResponse.class);
            response.getOk();
            response.getOperationId().equals("47ecebe0-27c4-11e4-ad5c-001b21c78f20");
            response.getScheduleUrls().get(0);
            response.getScheduleUrls().get(0).equals("https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getScheduleIds().get(0).equals("4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getSchedulePayloads().get(0).getUrl().get().equals("https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getSchedulePayloads().get(0).getPushPayload().getNotification().get().getAlert().get().equals("Scheduled API v3");
        } catch (Exception ex) {
            String String_7 = "Exception " + (ex.getMessage());
            Assert.assertEquals("Exception Error parsing schedule object. Either scheduled_time, local_scheduled_time, or best time must be set. (through reference chain: java.util.ArrayList[0])", String_7);
        }
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
    }

    @Test(timeout = 10000)
    public void testAPIScheduleResponse_literalMutationString6_literalMutationNumber532() throws Exception {
        String scheduleJSON = "{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}";
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
        ObjectMapper mapper = ScheduleObjectMapper.getInstance();
        try {
            ScheduleResponse response = mapper.readValue(scheduleJSON, ScheduleResponse.class);
            response.getOk();
            response.getOperationId().equals("47ecebe0-27c4-11e4-ad5c-001b21c78f20");
            response.getScheduleUrls().get(0).equals("https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getScheduleIds().get(0).equals("4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getSchedulePayloads().get(0).getUrl().get().equals("https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getSchedulePayloads().get(-1).getPushPayload().getNotification().get().getAlert().get().equals("Scheduled API v3");
        } catch (Exception ex) {
            String String_7 = "Exception " + (ex.getMessage());
            Assert.assertEquals("Exception Error parsing schedule object. Either scheduled_time, local_scheduled_time, or best time must be set. (through reference chain: java.util.ArrayList[0])", String_7);
        }
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
    }

    @Test(timeout = 10000)
    public void testAPIScheduleResponse_literalMutationString6_literalMutationNumber499() throws Exception {
        String scheduleJSON = "{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}";
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
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
            String String_7 = "Exception " + (ex.getMessage());
            Assert.assertEquals("Exception Error parsing schedule object. Either scheduled_time, local_scheduled_time, or best time must be set. (through reference chain: java.util.ArrayList[0])", String_7);
        }
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
    }

    @Test(timeout = 10000)
    public void testAPIScheduleResponse_literalMutationString6_add658() throws Exception {
        String scheduleJSON = "{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}";
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
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
            String String_7 = "Exception " + (ex.getMessage());
            Assert.assertEquals("Exception Error parsing schedule object. Either scheduled_time, local_scheduled_time, or best time must be set. (through reference chain: java.util.ArrayList[0])", String_7);
        }
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
    }

    @Test(timeout = 10000)
    public void testAPIScheduleResponse_literalMutationString6_literalMutationNumber452() throws Exception {
        String scheduleJSON = "{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}";
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
        ObjectMapper mapper = ScheduleObjectMapper.getInstance();
        try {
            ScheduleResponse response = mapper.readValue(scheduleJSON, ScheduleResponse.class);
            response.getOk();
            response.getOperationId().equals("47ecebe0-27c4-11e4-ad5c-001b21c78f20");
            response.getScheduleUrls().get(0).equals("https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getScheduleIds().get(-1).equals("4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getSchedulePayloads().get(0).getUrl().get().equals("https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getSchedulePayloads().get(0).getPushPayload().getNotification().get().getAlert().get().equals("Scheduled API v3");
        } catch (Exception ex) {
            String String_7 = "Exception " + (ex.getMessage());
            Assert.assertEquals("Exception Error parsing schedule object. Either scheduled_time, local_scheduled_time, or best time must be set. (through reference chain: java.util.ArrayList[0])", String_7);
        }
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
    }

    @Test(timeout = 10000)
    public void testAPIScheduleResponse_literalMutationString6_literalMutationString549() throws Exception {
        String scheduleJSON = "{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}";
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
        ObjectMapper mapper = ScheduleObjectMapper.getInstance();
        try {
            ScheduleResponse response = mapper.readValue(scheduleJSON, ScheduleResponse.class);
            response.getOk();
            response.getOperationId().equals("47ecebe0-27c4-11e4-ad5c-001b21c78f20");
            response.getScheduleUrls().get(0).equals("https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getScheduleIds().get(0).equals("4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getSchedulePayloads().get(0).getUrl().get().equals("https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getSchedulePayloads().get(0).getPushPayload().getNotification().get().getAlert().get().equals("Sncheduled API v3");
        } catch (Exception ex) {
            String String_7 = "Exception " + (ex.getMessage());
            Assert.assertEquals("Exception Error parsing schedule object. Either scheduled_time, local_scheduled_time, or best time must be set. (through reference chain: java.util.ArrayList[0])", String_7);
        }
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
    }

    @Test(timeout = 10000)
    public void testAPIScheduleResponse_literalMutationString6_add609() throws Exception {
        String scheduleJSON = "{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}";
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
        ObjectMapper mapper = ScheduleObjectMapper.getInstance();
        try {
            ScheduleResponse response = mapper.readValue(scheduleJSON, ScheduleResponse.class);
            response.getOk();
            response.getOperationId();
            response.getOperationId().equals("47ecebe0-27c4-11e4-ad5c-001b21c78f20");
            response.getScheduleUrls().get(0).equals("https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getScheduleIds().get(0).equals("4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getSchedulePayloads().get(0).getUrl().get().equals("https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getSchedulePayloads().get(0).getPushPayload().getNotification().get().getAlert().get().equals("Scheduled API v3");
        } catch (Exception ex) {
            String String_7 = "Exception " + (ex.getMessage());
            Assert.assertEquals("Exception Error parsing schedule object. Either scheduled_time, local_scheduled_time, or best time must be set. (through reference chain: java.util.ArrayList[0])", String_7);
        }
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
    }

    @Test(timeout = 10000)
    public void testAPIScheduleResponse_literalMutationString6_literalMutationString483() throws Exception {
        String scheduleJSON = "{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}";
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
        ObjectMapper mapper = ScheduleObjectMapper.getInstance();
        try {
            ScheduleResponse response = mapper.readValue(scheduleJSON, ScheduleResponse.class);
            response.getOk();
            response.getOperationId().equals("47ecebe0-27c4-11e4-ad5c-001b21c78f20");
            response.getScheduleUrls().get(0).equals("https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getScheduleIds().get(0).equals("4f636bb9-e278-4af*8-8fe4-873809acbd87");
            response.getSchedulePayloads().get(0).getUrl().get().equals("https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getSchedulePayloads().get(0).getPushPayload().getNotification().get().getAlert().get().equals("Scheduled API v3");
        } catch (Exception ex) {
            String String_7 = "Exception " + (ex.getMessage());
            Assert.assertEquals("Exception Error parsing schedule object. Either scheduled_time, local_scheduled_time, or best time must be set. (through reference chain: java.util.ArrayList[0])", String_7);
        }
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
    }

    @Test(timeout = 10000)
    public void testAPIScheduleResponse_literalMutationString6_literalMutationString519() throws Exception {
        String scheduleJSON = "{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}";
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
        ObjectMapper mapper = ScheduleObjectMapper.getInstance();
        try {
            ScheduleResponse response = mapper.readValue(scheduleJSON, ScheduleResponse.class);
            response.getOk();
            response.getOperationId().equals("47ecebe0-27c4-11e4-ad5c-001b21c78f20");
            response.getScheduleUrls().get(0).equals("https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getScheduleIds().get(0).equals("4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getSchedulePayloads().get(0).getUrl().get().equals("https://go.urbanairship.com/api/Aschedules/4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getSchedulePayloads().get(0).getPushPayload().getNotification().get().getAlert().get().equals("Scheduled API v3");
        } catch (Exception ex) {
            String String_7 = "Exception " + (ex.getMessage());
            Assert.assertEquals("Exception Error parsing schedule object. Either scheduled_time, local_scheduled_time, or best time must be set. (through reference chain: java.util.ArrayList[0])", String_7);
        }
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
    }

    @Test(timeout = 10000)
    public void testAPIScheduleResponse_literalMutationString6_add620() throws Exception {
        String scheduleJSON = "{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}";
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
        ObjectMapper mapper = ScheduleObjectMapper.getInstance();
        try {
            ScheduleResponse response = mapper.readValue(scheduleJSON, ScheduleResponse.class);
            response.getOk();
            response.getOperationId().equals("47ecebe0-27c4-11e4-ad5c-001b21c78f20");
            response.getScheduleUrls();
            response.getScheduleUrls().get(0).equals("https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getScheduleIds().get(0).equals("4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getSchedulePayloads().get(0).getUrl().get().equals("https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getSchedulePayloads().get(0).getPushPayload().getNotification().get().getAlert().get().equals("Scheduled API v3");
        } catch (Exception ex) {
            String String_7 = "Exception " + (ex.getMessage());
            Assert.assertEquals("Exception Error parsing schedule object. Either scheduled_time, local_scheduled_time, or best time must be set. (through reference chain: java.util.ArrayList[0])", String_7);
        }
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
    }

    @Test(timeout = 10000)
    public void testAPIScheduleResponse_literalMutationString6() throws Exception {
        String scheduleJSON = "{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}";
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
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
            String String_7 = "Exception " + (ex.getMessage());
            Assert.assertEquals("Exception Error parsing schedule object. Either scheduled_time, local_scheduled_time, or best time must be set. (through reference chain: java.util.ArrayList[0])", String_7);
        }
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
    }

    @Test(timeout = 10000)
    public void testAPIScheduleResponse_literalMutationString6_add662() throws Exception {
        String scheduleJSON = "{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}";
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
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
            String String_7 = "Exception " + (ex.getMessage());
            Assert.assertEquals("Exception Error parsing schedule object. Either scheduled_time, local_scheduled_time, or best time must be set. (through reference chain: java.util.ArrayList[0])", String_7);
        }
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
    }

    @Test(timeout = 10000)
    public void testAPIScheduleResponse_literalMutationString6_add624() throws Exception {
        String scheduleJSON = "{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}";
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
        ObjectMapper mapper = ScheduleObjectMapper.getInstance();
        try {
            ScheduleResponse response = mapper.readValue(scheduleJSON, ScheduleResponse.class);
            response.getOk();
            response.getOperationId().equals("47ecebe0-27c4-11e4-ad5c-001b21c78f20");
            response.getScheduleUrls().get(0).equals("https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getScheduleIds().get(0).equals("4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getScheduleIds().get(0).equals("4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getSchedulePayloads().get(0).getUrl().get().equals("https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getSchedulePayloads().get(0).getPushPayload().getNotification().get().getAlert().get().equals("Scheduled API v3");
        } catch (Exception ex) {
            String String_7 = "Exception " + (ex.getMessage());
            Assert.assertEquals("Exception Error parsing schedule object. Either scheduled_time, local_scheduled_time, or best time must be set. (through reference chain: java.util.ArrayList[0])", String_7);
        }
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
    }

    @Test(timeout = 10000)
    public void testAPIScheduleResponse_literalMutationString6_add665() throws Exception {
        String scheduleJSON = "{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}";
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
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
            String String_7 = "Exception " + (ex.getMessage());
            Assert.assertEquals("Exception Error parsing schedule object. Either scheduled_time, local_scheduled_time, or best time must be set. (through reference chain: java.util.ArrayList[0])", String_7);
        }
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
    }

    @Test(timeout = 10000)
    public void testAPIScheduleResponse_literalMutationString6_add643() throws Exception {
        String scheduleJSON = "{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}";
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
        ObjectMapper mapper = ScheduleObjectMapper.getInstance();
        try {
            ScheduleResponse response = mapper.readValue(scheduleJSON, ScheduleResponse.class);
            response.getOk();
            response.getOperationId().equals("47ecebe0-27c4-11e4-ad5c-001b21c78f20");
            response.getScheduleUrls().get(0).equals("https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getScheduleIds().get(0).equals("4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getSchedulePayloads().get(0).getUrl();
            response.getSchedulePayloads().get(0).getUrl().get().equals("https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getSchedulePayloads().get(0).getPushPayload().getNotification().get().getAlert().get().equals("Scheduled API v3");
        } catch (Exception ex) {
            String String_7 = "Exception " + (ex.getMessage());
            Assert.assertEquals("Exception Error parsing schedule object. Either scheduled_time, local_scheduled_time, or best time must be set. (through reference chain: java.util.ArrayList[0])", String_7);
        }
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
    }

    @Test(timeout = 10000)
    public void testAPIScheduleResponse_literalMutationString6_add605() throws Exception {
        String scheduleJSON = "{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}";
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
        ObjectMapper mapper = ScheduleObjectMapper.getInstance();
        try {
            ScheduleResponse response = mapper.readValue(scheduleJSON, ScheduleResponse.class);
            response.getOk();
            response.getOperationId().equals("47ecebe0-27c4-11e4-ad5c-001b21c78f20");
            response.getOperationId().equals("47ecebe0-27c4-11e4-ad5c-001b21c78f20");
            response.getScheduleUrls().get(0).equals("https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getScheduleIds().get(0).equals("4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getSchedulePayloads().get(0).getUrl().get().equals("https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getSchedulePayloads().get(0).getPushPayload().getNotification().get().getAlert().get().equals("Scheduled API v3");
        } catch (Exception ex) {
            String String_7 = "Exception " + (ex.getMessage());
            Assert.assertEquals("Exception Error parsing schedule object. Either scheduled_time, local_scheduled_time, or best time must be set. (through reference chain: java.util.ArrayList[0])", String_7);
        }
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
    }

    @Test(timeout = 10000)
    public void testAPIScheduleResponse_literalMutationString6_add649() throws Exception {
        String scheduleJSON = "{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}";
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
        ObjectMapper mapper = ScheduleObjectMapper.getInstance();
        try {
            ScheduleResponse response = mapper.readValue(scheduleJSON, ScheduleResponse.class);
            response.getOk();
            response.getOperationId().equals("47ecebe0-27c4-11e4-ad5c-001b21c78f20");
            response.getScheduleUrls().get(0).equals("https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getScheduleIds().get(0).equals("4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getSchedulePayloads();
            response.getSchedulePayloads().get(0).getUrl().get().equals("https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getSchedulePayloads().get(0).getPushPayload().getNotification().get().getAlert().get().equals("Scheduled API v3");
        } catch (Exception ex) {
            String String_7 = "Exception " + (ex.getMessage());
            Assert.assertEquals("Exception Error parsing schedule object. Either scheduled_time, local_scheduled_time, or best time must be set. (through reference chain: java.util.ArrayList[0])", String_7);
        }
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
    }

    @Test(timeout = 10000)
    public void testAPIScheduleResponse_literalMutationString6_add627() throws Exception {
        String scheduleJSON = "{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}";
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
        ObjectMapper mapper = ScheduleObjectMapper.getInstance();
        try {
            ScheduleResponse response = mapper.readValue(scheduleJSON, ScheduleResponse.class);
            response.getOk();
            response.getOperationId().equals("47ecebe0-27c4-11e4-ad5c-001b21c78f20");
            response.getScheduleUrls().get(0).equals("https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getScheduleIds().get(0);
            response.getScheduleIds().get(0).equals("4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getSchedulePayloads().get(0).getUrl().get().equals("https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getSchedulePayloads().get(0).getPushPayload().getNotification().get().getAlert().get().equals("Scheduled API v3");
        } catch (Exception ex) {
            String String_7 = "Exception " + (ex.getMessage());
            Assert.assertEquals("Exception Error parsing schedule object. Either scheduled_time, local_scheduled_time, or best time must be set. (through reference chain: java.util.ArrayList[0])", String_7);
        }
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
    }

    @Test(timeout = 10000)
    public void testAPIScheduleResponse_literalMutationString6_add647() throws Exception {
        String scheduleJSON = "{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}";
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
        ObjectMapper mapper = ScheduleObjectMapper.getInstance();
        try {
            ScheduleResponse response = mapper.readValue(scheduleJSON, ScheduleResponse.class);
            response.getOk();
            response.getOperationId().equals("47ecebe0-27c4-11e4-ad5c-001b21c78f20");
            response.getScheduleUrls().get(0).equals("https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getScheduleIds().get(0).equals("4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getSchedulePayloads().get(0);
            response.getSchedulePayloads().get(0).getUrl().get().equals("https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getSchedulePayloads().get(0).getPushPayload().getNotification().get().getAlert().get().equals("Scheduled API v3");
        } catch (Exception ex) {
            String String_7 = "Exception " + (ex.getMessage());
            Assert.assertEquals("Exception Error parsing schedule object. Either scheduled_time, local_scheduled_time, or best time must be set. (through reference chain: java.util.ArrayList[0])", String_7);
        }
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
    }

    @Test(timeout = 10000)
    public void testAPIScheduleResponse_literalMutationString6_literalMutationString515() throws Exception {
        String scheduleJSON = "{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}";
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
        ObjectMapper mapper = ScheduleObjectMapper.getInstance();
        try {
            ScheduleResponse response = mapper.readValue(scheduleJSON, ScheduleResponse.class);
            response.getOk();
            response.getOperationId().equals("47ecebe0-27c4-11e4-ad5c-001b21c78f20");
            response.getScheduleUrls().get(0).equals("https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getScheduleIds().get(0).equals("4f636bb9-e278-4af8-8fe4-873809acbd87");
            response.getSchedulePayloads().get(0).getUrl().get().equals("https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809rcbd87");
            response.getSchedulePayloads().get(0).getPushPayload().getNotification().get().getAlert().get().equals("Scheduled API v3");
        } catch (Exception ex) {
            String String_7 = "Exception " + (ex.getMessage());
            Assert.assertEquals("Exception Error parsing schedule object. Either scheduled_time, local_scheduled_time, or best time must be set. (through reference chain: java.util.ArrayList[0])", String_7);
        }
        Assert.assertEquals("{\"ok\":true,\"operation_id\":\"47ecebe0-27c4-11e4-ad5c-001b21c78f20\",\"schedule_urls\":[\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedule_ids\":[\"4f636bb9-e278-4af8-8fe4-873809acbd87\"],\"schedules\":[{\"url\":\"https://go.urbanairship.com/api/schedules/4f636bb9-e278-4af8-8fe4-873809acbd87\",\"schedule\":{\"schNduled_time\":\"2014-08-19T17:15:27\"},\"name\":\"Urban Airship Scheduled Push\",\"push\":{\"audience\":\"ALL\",\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"Scheduled API v3\"}},\"push_ids\":[\"70d84384-4c0a-4917-8e05-4443cf4e9575\"]}]}", scheduleJSON);
    }
}

