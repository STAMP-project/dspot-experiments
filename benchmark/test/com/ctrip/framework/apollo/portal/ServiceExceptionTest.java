package com.ctrip.framework.apollo.portal;


import com.ctrip.framework.apollo.common.exception.ServiceException;
import com.ctrip.framework.apollo.portal.controller.AppController;
import com.ctrip.framework.apollo.portal.entity.model.AppModel;
import com.ctrip.framework.apollo.portal.service.AppService;
import com.google.gson.Gson;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpStatusCodeException;


public class ServiceExceptionTest extends AbstractUnitTest {
    @InjectMocks
    private AppController appController;

    @Mock
    private AppService appService;

    @Test
    public void testAdminServiceException() {
        String errorMsg = "No available admin service";
        String errorCode = "errorCode";
        String status = "500";
        Map<String, Object> errorAttributes = new LinkedHashMap<>();
        errorAttributes.put("status", status);
        errorAttributes.put("message", errorMsg);
        errorAttributes.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        errorAttributes.put("exception", ServiceException.class.getName());
        errorAttributes.put("errorCode", errorCode);
        HttpStatusCodeException adminException = new org.springframework.web.client.HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "admin server error", new Gson().toJson(errorAttributes).getBytes(), Charset.defaultCharset());
        Mockito.when(appService.createAppInLocal(ArgumentMatchers.any())).thenThrow(adminException);
        AppModel app = generateSampleApp();
        try {
            appController.create(app);
        } catch (HttpStatusCodeException e) {
            @SuppressWarnings("unchecked")
            Map<String, String> attr = new Gson().fromJson(e.getResponseBodyAsString(), Map.class);
            Assert.assertEquals(errorMsg, attr.get("message"));
            Assert.assertEquals(errorCode, attr.get("errorCode"));
            Assert.assertEquals(status, attr.get("status"));
        }
    }
}

