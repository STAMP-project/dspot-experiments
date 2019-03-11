package org.apereo.cas.uma.web.controllers.resource;


import java.util.Collection;
import lombok.val;
import org.apereo.cas.uma.web.controllers.BaseUmaEndpointControllerTests;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * This is {@link UmaFindResourceSetRegistrationEndpointControllerTests}.
 *
 * @author Misagh Moayyed
 * @since 6.0.0
 */
public class UmaFindResourceSetRegistrationEndpointControllerTests extends BaseUmaEndpointControllerTests {
    @Test
    public void verifyOperation() throws Exception {
        val results = authenticateUmaRequestWithProtectionScope();
        var body = BaseUmaEndpointControllerTests.createUmaResourceRegistrationRequest().toJson();
        umaCreateResourceSetRegistrationEndpointController.registerResourceSet(body, results.getLeft(), results.getMiddle());
        val response = umaFindResourceSetRegistrationEndpointController.findResourceSets(results.getLeft(), results.getMiddle());
        Assertions.assertNotNull(response.getBody());
        val model = ((Collection) (response.getBody()));
        Assertions.assertTrue(((model.size()) == 1));
    }
}

