package org.apereo.cas.uma.web.controllers.resource;


import HttpStatus.OK;
import java.util.Map;
import lombok.val;
import org.apereo.cas.uma.web.controllers.BaseUmaEndpointControllerTests;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * This is {@link UmaCreateResourceSetRegistrationEndpointControllerTests}.
 *
 * @author Misagh Moayyed
 * @since 6.0.0
 */
public class UmaCreateResourceSetRegistrationEndpointControllerTests extends BaseUmaEndpointControllerTests {
    @Test
    public void verifyRegistrationOperation() throws Exception {
        val results = authenticateUmaRequestWithProtectionScope();
        var body = BaseUmaEndpointControllerTests.createUmaResourceRegistrationRequest().toJson();
        var response = umaCreateResourceSetRegistrationEndpointController.registerResourceSet(body, results.getLeft(), results.getMiddle());
        Assertions.assertEquals(OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        var model = ((Map) (response.getBody()));
        Assertions.assertTrue(model.containsKey("code"));
        Assertions.assertTrue(model.containsKey("location"));
        Assertions.assertTrue(model.containsKey("entity"));
        Assertions.assertTrue(model.containsKey("resourceId"));
        val resourceId = ((long) (model.get("resourceId")));
        response = umaFindResourceSetRegistrationEndpointController.findResourceSet(resourceId, results.getLeft(), results.getMiddle());
        Assertions.assertEquals(OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        model = ((Map) (response.getBody()));
        Assertions.assertTrue(model.containsKey("code"));
        Assertions.assertTrue(model.containsKey("entity"));
        body = BaseUmaEndpointControllerTests.createUmaPermissionRegistrationRequest(resourceId).toJson();
        response = umaPermissionRegistrationEndpointController.handle(body, results.getLeft(), results.getMiddle());
        Assertions.assertEquals(OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        model = ((Map) (response.getBody()));
        Assertions.assertTrue(model.containsKey("code"));
        Assertions.assertTrue(model.containsKey("ticket"));
    }
}

