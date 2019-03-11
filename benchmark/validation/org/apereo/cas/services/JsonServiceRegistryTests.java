package org.apereo.cas.services;


import lombok.SneakyThrows;
import lombok.val;
import org.apereo.cas.services.util.RegisteredServiceJsonSerializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;


/**
 * Handles test cases for {@link JsonServiceRegistry}.
 *
 * @author Misagh Moayyed
 * @since 4.1.0
 */
public class JsonServiceRegistryTests extends AbstractResourceBasedServiceRegistryTests {
    @Test
    @SneakyThrows
    public void verifyLegacyServiceDefinition() {
        val resource = new ClassPathResource("Legacy-10000003.json");
        val serializer = new RegisteredServiceJsonSerializer();
        val service = serializer.from(resource.getInputStream());
        Assertions.assertNotNull(service);
    }

    @Test
    @SneakyThrows
    public void verifyMultifactorNotSetFailureMode() {
        val resource = new ClassPathResource("MFA-FailureMode-1.json");
        val serializer = new RegisteredServiceJsonSerializer();
        val service = serializer.from(resource.getInputStream());
        Assertions.assertNotNull(service);
    }

    @Test
    @SneakyThrows
    public void verifyExistingDefinitionForCompatibility2() {
        val resource = new ClassPathResource("returnMappedAttributeReleasePolicyTest2.json");
        val serializer = new RegisteredServiceJsonSerializer();
        val service = serializer.from(resource.getInputStream());
        Assertions.assertNotNull(service);
        Assertions.assertNotNull(service.getAttributeReleasePolicy());
        val policy = ((ReturnMappedAttributeReleasePolicy) (service.getAttributeReleasePolicy()));
        Assertions.assertNotNull(policy);
        Assertions.assertEquals(2, policy.getAllowedAttributes().size());
    }

    @Test
    @SneakyThrows
    public void verifyExistingDefinitionForCompatibility1() {
        val resource = new ClassPathResource("returnMappedAttributeReleasePolicyTest1.json");
        val serializer = new RegisteredServiceJsonSerializer();
        val service = serializer.from(resource.getInputStream());
        Assertions.assertNotNull(service);
        Assertions.assertNotNull(service.getAttributeReleasePolicy());
        val policy = ((ReturnMappedAttributeReleasePolicy) (service.getAttributeReleasePolicy()));
        Assertions.assertNotNull(policy);
        Assertions.assertEquals(2, policy.getAllowedAttributes().size());
    }
}

