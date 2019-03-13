package org.apereo.cas.uma.web.controllers.permission;


import HttpStatus.BAD_REQUEST;
import java.util.Map;
import lombok.val;
import org.apereo.cas.uma.web.controllers.BaseUmaEndpointControllerTests;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * This is {@link UmaPermissionRegistrationEndpointControllerTests}.
 *
 * @author Misagh Moayyed
 * @since 6.0.0
 */
public class UmaPermissionRegistrationEndpointControllerTests extends BaseUmaEndpointControllerTests {
    @Test
    public void verifyPermissionRegistrationOperation() throws Exception {
        val results = authenticateUmaRequestWithProtectionScope();
        val body = BaseUmaEndpointControllerTests.createUmaPermissionRegistrationRequest(100).toJson();
        val response = umaPermissionRegistrationEndpointController.handle(body, results.getLeft(), results.getMiddle());
        Assertions.assertEquals(BAD_REQUEST, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        val model = ((Map) (response.getBody()));
        Assertions.assertTrue(model.containsKey("code"));
        Assertions.assertTrue(model.containsKey("message"));
    }
}

