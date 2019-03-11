package com.ctrip.framework.apollo.adminservice.controller;


import HttpStatus.BAD_REQUEST;
import HttpStatus.OK;
import InputValidator.INVALID_CLUSTER_NAMESPACE_MESSAGE;
import com.ctrip.framework.apollo.biz.repository.AppRepository;
import com.ctrip.framework.apollo.common.dto.AppDTO;
import com.ctrip.framework.apollo.common.entity.App;
import com.ctrip.framework.apollo.common.utils.BeanUtils;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.web.client.HttpClientErrorException;


public class AppControllerTest extends AbstractControllerTest {
    @Autowired
    AppRepository appRepository;

    @Test
    @Sql(scripts = "/controller/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testCheckIfAppIdUnique() {
        AppDTO dto = generateSampleDTOData();
        ResponseEntity<AppDTO> response = restTemplate.postForEntity(getBaseAppUrl(), dto, AppDTO.class);
        AppDTO result = response.getBody();
        Assert.assertEquals(OK, response.getStatusCode());
        Assert.assertEquals(dto.getAppId(), result.getAppId());
        Assert.assertTrue(((result.getId()) > 0));
        Boolean falseUnique = restTemplate.getForObject((((getBaseAppUrl()) + (dto.getAppId())) + "/unique"), Boolean.class);
        Assert.assertFalse(falseUnique);
        Boolean trueUnique = restTemplate.getForObject(((((getBaseAppUrl()) + (dto.getAppId())) + "true") + "/unique"), Boolean.class);
        Assert.assertTrue(trueUnique);
    }

    @Test
    @Sql(scripts = "/controller/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testCreate() {
        AppDTO dto = generateSampleDTOData();
        ResponseEntity<AppDTO> response = restTemplate.postForEntity(getBaseAppUrl(), dto, AppDTO.class);
        AppDTO result = response.getBody();
        Assert.assertEquals(OK, response.getStatusCode());
        Assert.assertEquals(dto.getAppId(), result.getAppId());
        Assert.assertTrue(((result.getId()) > 0));
        App savedApp = appRepository.findById(result.getId()).orElse(null);
        Assert.assertEquals(dto.getAppId(), savedApp.getAppId());
        Assert.assertNotNull(savedApp.getDataChangeCreatedTime());
    }

    @Test
    @Sql(scripts = "/controller/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testCreateTwice() {
        AppDTO dto = generateSampleDTOData();
        ResponseEntity<AppDTO> response = restTemplate.postForEntity(getBaseAppUrl(), dto, AppDTO.class);
        AppDTO first = response.getBody();
        Assert.assertEquals(OK, response.getStatusCode());
        Assert.assertEquals(dto.getAppId(), first.getAppId());
        Assert.assertTrue(((first.getId()) > 0));
        App savedApp = appRepository.findById(first.getId()).orElse(null);
        Assert.assertEquals(dto.getAppId(), savedApp.getAppId());
        Assert.assertNotNull(savedApp.getDataChangeCreatedTime());
        try {
            restTemplate.postForEntity(getBaseAppUrl(), dto, AppDTO.class);
        } catch (HttpClientErrorException e) {
            Assert.assertEquals(BAD_REQUEST, e.getStatusCode());
        }
    }

    @Test
    @Sql(scripts = "/controller/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testFind() {
        AppDTO dto = generateSampleDTOData();
        App app = BeanUtils.transform(App.class, dto);
        app = appRepository.save(app);
        AppDTO result = restTemplate.getForObject(((getBaseAppUrl()) + (dto.getAppId())), AppDTO.class);
        Assert.assertEquals(dto.getAppId(), result.getAppId());
        Assert.assertEquals(dto.getName(), result.getName());
    }

    @Test(expected = HttpClientErrorException.class)
    @Sql(scripts = "/controller/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testFindNotExist() {
        restTemplate.getForEntity(((getBaseAppUrl()) + "notExists"), AppDTO.class);
    }

    @Test
    @Sql(scripts = "/controller/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testDelete() {
        AppDTO dto = generateSampleDTOData();
        App app = BeanUtils.transform(App.class, dto);
        app = appRepository.save(app);
        restTemplate.delete("http://localhost:{port}/apps/{appId}?operator={operator}", port, app.getAppId(), "test");
        App deletedApp = appRepository.findById(app.getId()).orElse(null);
        Assert.assertNull(deletedApp);
    }

    @Test
    @Sql(scripts = "/controller/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void shouldFailedWhenAppIdIsInvalid() {
        AppDTO dto = generateSampleDTOData();
        dto.setAppId("invalid app id");
        try {
            restTemplate.postForEntity(getBaseAppUrl(), dto, String.class);
            Assert.fail("Should throw");
        } catch (HttpClientErrorException e) {
            Assert.assertEquals(BAD_REQUEST, e.getStatusCode());
            Assert.assertThat(new String(e.getResponseBodyAsByteArray()), Matchers.containsString(INVALID_CLUSTER_NAMESPACE_MESSAGE));
        }
    }
}

