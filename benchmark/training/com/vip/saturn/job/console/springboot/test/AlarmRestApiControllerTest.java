package com.vip.saturn.job.console.springboot.test;


import MediaType.APPLICATION_JSON;
import com.alibaba.fastjson.JSONObject;
import com.vip.saturn.job.console.AbstractSaturnConsoleTest;
import com.vip.saturn.job.console.controller.rest.AlarmRestApiController;
import com.vip.saturn.job.console.exception.SaturnJobConsoleHttpException;
import com.vip.saturn.job.console.service.RestApiService;
import com.vip.saturn.job.console.service.ZkTreeService;
import com.vip.saturn.job.integrate.entity.AlarmInfo;
import java.util.Map;
import org.assertj.core.util.Maps;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;


/**
 * Created by kfchu on 24/05/2017.
 */
@RunWith(SpringRunner.class)
@WebMvcTest(AlarmRestApiController.class)
public class AlarmRestApiControllerTest extends AbstractSaturnConsoleTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private RestApiService restApiService;

    @MockBean
    private ZkTreeService zkTreeService;

    @Test
    public void testRaiseAlarmSuccessfully() throws Exception {
        final String jobName = "job1";
        final String executorName = "exec001";
        final String alarmName = "name";
        final String alarmTitle = "title";
        AlarmRestApiControllerTest.AlarmEntity alarmEntity = new AlarmRestApiControllerTest.AlarmEntity(jobName, executorName, alarmName, alarmTitle, "CRITICAL");
        alarmEntity.getAdditionalInfo().put("key1", "value1");
        alarmEntity.setShardItem(1);
        alarmEntity.setMessage("message");
        mvc.perform(post("/rest/v1/mydomain/alarms/raise").contentType(APPLICATION_JSON).content(alarmEntity.toJSON())).andExpect(status().isCreated());
        ArgumentCaptor<AlarmInfo> argument = ArgumentCaptor.forClass(AlarmInfo.class);
        Mockito.verify(restApiService).raiseAlarm(ArgumentMatchers.eq("mydomain"), ArgumentMatchers.eq(jobName), ArgumentMatchers.eq(executorName), ArgumentMatchers.eq(1), argument.capture());
        compareAlarmInfo(alarmEntity, argument.getValue());
    }

    @Test
    public void testRaiseAlarmFailAsMissingMandatoryField() throws Exception {
        // missing executorname
        AlarmRestApiControllerTest.AlarmEntity alarmEntity = new AlarmRestApiControllerTest.AlarmEntity("job1", null, "name", "title", "CRITICAL");
        MvcResult result = mvc.perform(post("/rest/v1/mydomain/alarms/raise").contentType(APPLICATION_JSON).content(alarmEntity.toJSON())).andExpect(status().isBadRequest()).andReturn();
        Assert.assertEquals("error message not equal", "Invalid request. Missing parameter: {executorName}", fetchErrorMessage(result));
        // missing name
        alarmEntity = new AlarmRestApiControllerTest.AlarmEntity("job1", "exec", null, "title", "CRITICAL");
        result = mvc.perform(post("/rest/v1/mydomain/alarms/raise").contentType(APPLICATION_JSON).content(alarmEntity.toJSON())).andExpect(status().isBadRequest()).andReturn();
        Assert.assertEquals("error message not equal", "Invalid request. Missing parameter: {name}", fetchErrorMessage(result));
        // missing title
        alarmEntity = new AlarmRestApiControllerTest.AlarmEntity("job1", "exec", "name", null, "CRITICAL");
        result = mvc.perform(post("/rest/v1/mydomain/alarms/raise").contentType(APPLICATION_JSON).content(alarmEntity.toJSON())).andExpect(status().isBadRequest()).andReturn();
        Assert.assertEquals("error message not equal", "Invalid request. Missing parameter: {title}", fetchErrorMessage(result));
        // missing level
        alarmEntity = new AlarmRestApiControllerTest.AlarmEntity("job1", "exec", "name", "title", null);
        result = mvc.perform(post("/rest/v1/mydomain/alarms/raise").contentType(APPLICATION_JSON).content(alarmEntity.toJSON())).andExpect(status().isBadRequest()).andReturn();
        Assert.assertEquals("error message not equal", "Invalid request. Missing parameter: {level}", fetchErrorMessage(result));
    }

    @Test
    public void testRaiseAlarmFailWithExpectedException() throws Exception {
        String customErrMsg = "some exception throws";
        BDDMockito.willThrow(new SaturnJobConsoleHttpException(400, customErrMsg)).given(restApiService).raiseAlarm(ArgumentMatchers.any(String.class), ArgumentMatchers.any(String.class), ArgumentMatchers.any(String.class), ArgumentMatchers.any(Integer.class), ArgumentMatchers.any(AlarmInfo.class));
        AlarmRestApiControllerTest.AlarmEntity alarmEntity = new AlarmRestApiControllerTest.AlarmEntity("job", "exec", "name", "title", "CRITICAL");
        MvcResult result = mvc.perform(post("/rest/v1/mydomain/alarms/raise").contentType(APPLICATION_JSON).content(alarmEntity.toJSON())).andExpect(status().isBadRequest()).andReturn();
        Assert.assertEquals("error message not equal", customErrMsg, fetchErrorMessage(result));
    }

    @Test
    public void testRaiseAlarmFailWithUnExpectedException() throws Exception {
        String customErrMsg = "some exception throws";
        BDDMockito.willThrow(new RuntimeException(customErrMsg)).given(restApiService).raiseAlarm(ArgumentMatchers.any(String.class), ArgumentMatchers.any(String.class), ArgumentMatchers.any(String.class), ArgumentMatchers.any(Integer.class), ArgumentMatchers.any(AlarmInfo.class));
        AlarmRestApiControllerTest.AlarmEntity alarmEntity = new AlarmRestApiControllerTest.AlarmEntity("job", "exec", "name", "title", "CRITICAL");
        MvcResult result = mvc.perform(post("/rest/v1/mydomain/alarms/raise").contentType(APPLICATION_JSON).content(alarmEntity.toJSON())).andExpect(status().isInternalServerError()).andReturn();
        Assert.assertEquals("error message not equal", customErrMsg, fetchErrorMessage(result));
    }

    public class AlarmEntity {
        private String jobName;

        private String executorName;

        private int shardItem;

        private String name;

        private String level;

        private String title;

        private String message;

        private Map<String, String> additionalInfo = Maps.newHashMap();

        public AlarmEntity(String jobName, String executorName, String name, String title, String level) {
            this.jobName = jobName;
            this.executorName = executorName;
            this.name = name;
            this.title = title;
            this.level = level;
        }

        public String toJSON() {
            return JSONObject.toJSONString(this);
        }

        public String getJobName() {
            return jobName;
        }

        public void setJobName(String jobName) {
            this.jobName = jobName;
        }

        public String getExecutorName() {
            return executorName;
        }

        public void setExecutorName(String executorName) {
            this.executorName = executorName;
        }

        public int getShardItem() {
            return shardItem;
        }

        public void setShardItem(int shardItem) {
            this.shardItem = shardItem;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Map<String, String> getAdditionalInfo() {
            return additionalInfo;
        }

        public void setAdditionalInfo(Map<String, String> additionalInfo) {
            this.additionalInfo = additionalInfo;
        }
    }
}

