package org.apereo.cas.rest.audit;


import lombok.val;
import org.apereo.cas.util.CollectionUtils;
import org.apereo.cas.util.MockWebServer;
import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;


/**
 * This is {@link RestResponseEntityAuditResourceResolverTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@Tag("RestfulApi")
public class RestResponseEntityAuditResourceResolverTests {
    @Test
    public void verifyAction() {
        val r = new RestResponseEntityAuditResourceResolver(true);
        try (val webServer = new MockWebServer(9193)) {
            webServer.start();
            val headers = new org.springframework.util.LinkedMultiValueMap<String, String>();
            headers.put("header", CollectionUtils.wrapList("value"));
            headers.put("location", CollectionUtils.wrapList("someplace"));
            val entity = new org.springframework.http.ResponseEntity<String>("The Response Body", headers, HttpStatus.OK);
            Assertions.assertTrue(((r.resolveFrom(Mockito.mock(JoinPoint.class), entity).length) > 0));
        }
    }
}

