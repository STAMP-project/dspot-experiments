package org.apereo.cas.uma.web.controllers.authz;


import HttpStatus.OK;
import OAuth20GrantTypes.UMA_TICKET;
import java.util.Map;
import lombok.val;
import org.apereo.cas.uma.web.controllers.BaseUmaEndpointControllerTests;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * This is {@link UmaAuthorizationRequestEndpointControllerTests}.
 *
 * @author Misagh Moayyed
 * @since 6.0.0
 */
public class UmaAuthorizationRequestEndpointControllerTests extends BaseUmaEndpointControllerTests {
    @Test
    public void verifyAuthorizationOperation() throws Exception {
        var results = authenticateUmaRequestWithProtectionScope();
        var body = BaseUmaEndpointControllerTests.createUmaResourceRegistrationRequest().toJson();
        var response = umaCreateResourceSetRegistrationEndpointController.registerResourceSet(body, results.getLeft(), results.getMiddle());
        Assertions.assertEquals(OK, response.getStatusCode());
        var model = ((Map) (response.getBody()));
        val resourceId = ((long) (model.get("resourceId")));
        val profile = BaseUmaEndpointControllerTests.getCurrentProfile(results.getLeft(), results.getMiddle());
        body = BaseUmaEndpointControllerTests.createUmaPolicyRegistrationRequest(profile).toJson();
        response = umaCreatePolicyForResourceSetEndpointController.createPolicyForResourceSet(resourceId, body, results.getLeft(), results.getMiddle());
        Assertions.assertEquals(OK, response.getStatusCode());
        body = BaseUmaEndpointControllerTests.createUmaPermissionRegistrationRequest(resourceId).toJson();
        response = umaPermissionRegistrationEndpointController.handle(body, results.getLeft(), results.getMiddle());
        Assertions.assertEquals(OK, response.getStatusCode());
        model = ((Map) (response.getBody()));
        val permissionTicket = model.get("ticket").toString();
        results = authenticateUmaRequestWithAuthorizationScope();
        val authzRequest = new UmaAuthorizationRequest();
        authzRequest.setGrantType(UMA_TICKET.getType());
        authzRequest.setTicket(permissionTicket);
        body = authzRequest.toJson();
        response = umaAuthorizationRequestEndpointController.handleAuthorizationRequest(body, results.getLeft(), results.getMiddle());
        Assertions.assertEquals(OK, response.getStatusCode());
        model = ((Map) (response.getBody()));
        Assertions.assertTrue(model.containsKey("code"));
        Assertions.assertTrue(model.containsKey("rpt"));
    }
}

