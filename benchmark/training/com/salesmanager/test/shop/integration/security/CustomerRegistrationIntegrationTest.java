package com.salesmanager.test.shop.integration.security;


import Constants.DEFAULT_STORE;
import CustomerGender.M;
import com.salesmanager.shop.model.customer.PersistableCustomer;
import com.salesmanager.shop.model.customer.address.Address;
import com.salesmanager.shop.store.security.AuthenticationRequest;
import com.salesmanager.shop.store.security.AuthenticationResponse;
import com.salesmanager.test.shop.common.ServicesTestSupport;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;


public class CustomerRegistrationIntegrationTest extends ServicesTestSupport {
    @Test
    public void registerCustomer() {
        final PersistableCustomer testCustomer = new PersistableCustomer();
        testCustomer.setEmailAddress("customer1@test.com");
        testCustomer.setUserName("testCust1");
        testCustomer.setClearPassword("clear123");
        testCustomer.setGender(M.name());
        testCustomer.setLanguage("en");
        final Address billing = new Address();
        billing.setFirstName("customer1");
        billing.setLastName("ccstomer1");
        billing.setCountry("BE");
        testCustomer.setBilling(billing);
        testCustomer.setStoreCode(DEFAULT_STORE);
        final HttpEntity<PersistableCustomer> entity = new HttpEntity(testCustomer, getHeader());
        final ResponseEntity<PersistableCustomer> response = testRestTemplate.postForEntity("/api/v1/customer/register", entity, PersistableCustomer.class);
        Assert.assertThat(response.getStatusCode(), Is.is(OK));
        // created customer can login
        final ResponseEntity<AuthenticationResponse> loginResponse = testRestTemplate.postForEntity("/api/v1/customer/login", new HttpEntity(new AuthenticationRequest("testCust1", "clear123")), AuthenticationResponse.class);
        Assert.assertThat(response.getStatusCode(), Is.is(OK));
        Assert.assertNotNull(loginResponse.getBody().getToken());
    }
}

