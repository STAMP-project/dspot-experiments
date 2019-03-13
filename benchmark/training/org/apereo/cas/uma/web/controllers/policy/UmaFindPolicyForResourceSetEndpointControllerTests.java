package org.apereo.cas.uma.web.controllers.policy;


import java.util.Collection;
import java.util.Map;
import lombok.val;
import org.apereo.cas.uma.ticket.resource.ResourceSetPolicy;
import org.apereo.cas.uma.web.controllers.BaseUmaEndpointControllerTests;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * This is {@link UmaFindPolicyForResourceSetEndpointControllerTests}.
 *
 * @author Misagh Moayyed
 * @since 6.0.0
 */
public class UmaFindPolicyForResourceSetEndpointControllerTests extends BaseUmaEndpointControllerTests {
    @Test
    public void verifyOperation() throws Exception {
        val results = authenticateUmaRequestWithProtectionScope();
        var body = BaseUmaEndpointControllerTests.createUmaResourceRegistrationRequest().toJson();
        var response = umaCreateResourceSetRegistrationEndpointController.registerResourceSet(body, results.getLeft(), results.getMiddle());
        var model = ((Map) (response.getBody()));
        val resourceId = ((long) (model.get("resourceId")));
        body = BaseUmaEndpointControllerTests.createUmaPolicyRegistrationRequest(BaseUmaEndpointControllerTests.getCurrentProfile(results.getLeft(), results.getMiddle())).toJson();
        umaCreatePolicyForResourceSetEndpointController.createPolicyForResourceSet(resourceId, body, results.getLeft(), results.getMiddle());
        response = umaFindPolicyForResourceSetEndpointController.getPoliciesForResourceSet(resourceId, results.getLeft(), results.getMiddle());
        model = ((Map) (response.getBody()));
        val policyId = ((Collection<ResourceSetPolicy>) (model.get("entity"))).iterator().next().getId();
        response = umaFindPolicyForResourceSetEndpointController.getPolicyForResourceSet(resourceId, policyId, results.getLeft(), results.getMiddle());
        model = ((Map) (response.getBody()));
        Assertions.assertTrue(model.containsKey("code"));
        Assertions.assertTrue(model.containsKey("entity"));
    }
}

