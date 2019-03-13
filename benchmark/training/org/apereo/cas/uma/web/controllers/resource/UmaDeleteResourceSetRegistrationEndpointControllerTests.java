package org.apereo.cas.uma.web.controllers.resource;


import java.util.Map;
import lombok.val;
import org.apereo.cas.uma.web.controllers.BaseUmaEndpointControllerTests;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * This is {@link UmaDeleteResourceSetRegistrationEndpointControllerTests}.
 *
 * @author Misagh Moayyed
 * @since 6.0.0
 */
public class UmaDeleteResourceSetRegistrationEndpointControllerTests extends BaseUmaEndpointControllerTests {
    @Test
    public void verifyOperation() throws Exception {
        val results = authenticateUmaRequestWithProtectionScope();
        var body = BaseUmaEndpointControllerTests.createUmaResourceRegistrationRequest().toJson();
        var response = umaCreateResourceSetRegistrationEndpointController.registerResourceSet(body, results.getLeft(), results.getMiddle());
        Assertions.assertNotNull(response.getBody());
        var model = ((Map) (response.getBody()));
        val resourceId = ((long) (model.get("resourceId")));
        response = umaDeleteResourceSetRegistrationEndpointController.deleteResourceSet(resourceId, results.getLeft(), results.getMiddle());
        Assertions.assertNotNull(response.getBody());
        model = ((Map) (response.getBody()));
        Assertions.assertTrue(model.containsKey("code"));
        Assertions.assertTrue(model.containsKey("resourceId"));
    }
}

