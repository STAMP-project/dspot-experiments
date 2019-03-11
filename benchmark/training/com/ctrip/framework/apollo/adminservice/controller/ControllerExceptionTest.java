package com.ctrip.framework.apollo.adminservice.controller;


import com.ctrip.framework.apollo.biz.service.AdminService;
import com.ctrip.framework.apollo.biz.service.AppService;
import com.ctrip.framework.apollo.common.dto.AppDTO;
import com.ctrip.framework.apollo.common.entity.App;
import com.ctrip.framework.apollo.common.exception.NotFoundException;
import com.ctrip.framework.apollo.common.exception.ServiceException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


@RunWith(MockitoJUnitRunner.class)
public class ControllerExceptionTest {
    private AppController appController;

    @Mock
    private AppService appService;

    @Mock
    private AdminService adminService;

    @Test(expected = NotFoundException.class)
    public void testFindNotExists() {
        Mockito.when(appService.findOne(ArgumentMatchers.any(String.class))).thenReturn(null);
        appController.get("unexist");
    }

    @Test(expected = NotFoundException.class)
    public void testDeleteNotExists() {
        Mockito.when(appService.findOne(ArgumentMatchers.any(String.class))).thenReturn(null);
        appController.delete("unexist", null);
    }

    @Test
    public void testFindEmpty() {
        Mockito.when(appService.findAll(ArgumentMatchers.any(Pageable.class))).thenReturn(new ArrayList<App>());
        Pageable pageable = PageRequest.of(0, 10);
        List<AppDTO> appDTOs = appController.find(null, pageable);
        Assert.assertNotNull(appDTOs);
        Assert.assertEquals(0, appDTOs.size());
        appDTOs = appController.find("", pageable);
        Assert.assertNotNull(appDTOs);
        Assert.assertEquals(0, appDTOs.size());
    }

    @Test
    public void testFindByName() {
        Pageable pageable = PageRequest.of(0, 10);
        List<AppDTO> appDTOs = appController.find("unexist", pageable);
        Assert.assertNotNull(appDTOs);
        Assert.assertEquals(0, appDTOs.size());
    }

    @Test(expected = ServiceException.class)
    public void createFailed() {
        AppDTO dto = generateSampleDTOData();
        Mockito.when(appService.findOne(ArgumentMatchers.any(String.class))).thenReturn(null);
        Mockito.when(adminService.createNewApp(ArgumentMatchers.any(App.class))).thenThrow(new ServiceException("create app failed"));
        appController.create(dto);
    }
}

