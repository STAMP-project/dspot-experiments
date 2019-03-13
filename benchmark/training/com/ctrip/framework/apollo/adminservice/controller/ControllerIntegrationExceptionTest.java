package com.ctrip.framework.apollo.adminservice.controller;


import com.ctrip.framework.apollo.biz.service.AdminService;
import com.ctrip.framework.apollo.biz.service.AppService;
import com.ctrip.framework.apollo.common.dto.AppDTO;
import com.ctrip.framework.apollo.common.entity.App;
import com.google.gson.Gson;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.web.client.HttpStatusCodeException;


public class ControllerIntegrationExceptionTest extends AbstractControllerTest {
    @Autowired
    AppController appController;

    @Mock
    AdminService adminService;

    private Object realAdminService;

    @Autowired
    AppService appService;

    Gson gson = new Gson();

    @Test
    @Sql(scripts = "/controller/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testCreateFailed() {
        AppDTO dto = generateSampleDTOData();
        Mockito.when(adminService.createNewApp(ArgumentMatchers.any(App.class))).thenThrow(new RuntimeException("save failed"));
        try {
            restTemplate.postForEntity(getBaseAppUrl(), dto, AppDTO.class);
        } catch (HttpStatusCodeException e) {
            @SuppressWarnings("unchecked")
            Map<String, String> attr = gson.fromJson(e.getResponseBodyAsString(), Map.class);
            Assert.assertEquals("save failed", attr.get("message"));
        }
        App savedApp = appService.findOne(dto.getAppId());
        Assert.assertNull(savedApp);
    }
}

