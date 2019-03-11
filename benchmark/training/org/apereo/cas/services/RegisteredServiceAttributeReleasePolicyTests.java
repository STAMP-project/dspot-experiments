package org.apereo.cas.services;


import com.google.common.collect.ArrayListMultimap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import lombok.val;
import org.apereo.cas.CoreAttributesTestUtils;
import org.apereo.cas.authentication.principal.DefaultPrincipalFactory;
import org.apereo.cas.authentication.principal.Principal;
import org.apereo.cas.authentication.principal.cache.CachingPrincipalAttributesRepository;
import org.apereo.cas.config.CasCoreUtilConfiguration;
import org.apereo.cas.util.CollectionUtils;
import org.apereo.cas.util.serialization.SerializationUtils;
import org.apereo.cas.util.spring.ApplicationContextProvider;
import org.apereo.services.persondir.IPersonAttributes;
import org.apereo.services.persondir.support.MergingPersonAttributeDaoImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;


/**
 * Attribute filtering policy tests.
 *
 * @author Misagh Moayyed
 * @since 4.0.0
 */
@SpringBootTest(classes = { RefreshAutoConfiguration.class, CasCoreUtilConfiguration.class })
public class RegisteredServiceAttributeReleasePolicyTests {
    private static final String ATTR_1 = "attr1";

    private static final String ATTR_2 = "attr2";

    private static final String ATTR_3 = "attr3";

    private static final String VALUE_1 = "value1";

    private static final String VALUE_2 = "value2";

    private static final String NEW_ATTR_1_VALUE = "newAttr1";

    private static final String PRINCIPAL_ID = "principalId";

    @Autowired
    private ConfigurableApplicationContext applicationContext;

    @Test
    public void verifyMappedAttributeFilterMappedAttributesIsCaseInsensitive() {
        val policy = new ReturnMappedAttributeReleasePolicy();
        val mappedAttr = ArrayListMultimap.<String, Object>create();
        mappedAttr.put(RegisteredServiceAttributeReleasePolicyTests.ATTR_1, RegisteredServiceAttributeReleasePolicyTests.NEW_ATTR_1_VALUE);
        policy.setAllowedAttributes(CollectionUtils.wrap(mappedAttr));
        val p = Mockito.mock(Principal.class);
        val map = new HashMap<String, Object>();
        map.put("ATTR1", RegisteredServiceAttributeReleasePolicyTests.VALUE_1);
        Mockito.when(p.getAttributes()).thenReturn(map);
        Mockito.when(p.getId()).thenReturn(RegisteredServiceAttributeReleasePolicyTests.PRINCIPAL_ID);
        val attr = policy.getAttributes(p, CoreAttributesTestUtils.getService(), CoreAttributesTestUtils.getRegisteredService());
        Assertions.assertEquals(1, attr.size());
        Assertions.assertTrue(attr.containsKey(RegisteredServiceAttributeReleasePolicyTests.NEW_ATTR_1_VALUE));
    }

    @Test
    public void verifyAttributeFilterMappedAttributesIsCaseInsensitive() {
        val policy = new ReturnAllowedAttributeReleasePolicy();
        val attrs = new ArrayList<String>();
        attrs.add(RegisteredServiceAttributeReleasePolicyTests.ATTR_1);
        attrs.add(RegisteredServiceAttributeReleasePolicyTests.ATTR_2);
        policy.setAllowedAttributes(attrs);
        val p = Mockito.mock(Principal.class);
        val map = new HashMap<String, Object>();
        map.put("ATTR1", RegisteredServiceAttributeReleasePolicyTests.VALUE_1);
        map.put("ATTR2", RegisteredServiceAttributeReleasePolicyTests.VALUE_2);
        Mockito.when(p.getAttributes()).thenReturn(map);
        Mockito.when(p.getId()).thenReturn(RegisteredServiceAttributeReleasePolicyTests.PRINCIPAL_ID);
        val attr = policy.getAttributes(p, CoreAttributesTestUtils.getService(), CoreAttributesTestUtils.getRegisteredService());
        Assertions.assertEquals(2, attr.size());
        Assertions.assertTrue(attr.containsKey(RegisteredServiceAttributeReleasePolicyTests.ATTR_1));
        Assertions.assertTrue(attr.containsKey(RegisteredServiceAttributeReleasePolicyTests.ATTR_2));
    }

    @Test
    public void verifyAttributeFilterMappedAttributes() {
        val policy = new ReturnMappedAttributeReleasePolicy();
        val mappedAttr = ArrayListMultimap.<String, Object>create();
        mappedAttr.put(RegisteredServiceAttributeReleasePolicyTests.ATTR_1, RegisteredServiceAttributeReleasePolicyTests.NEW_ATTR_1_VALUE);
        policy.setAllowedAttributes(CollectionUtils.wrap(mappedAttr));
        val p = Mockito.mock(Principal.class);
        val map = new HashMap<String, Object>();
        map.put(RegisteredServiceAttributeReleasePolicyTests.ATTR_1, RegisteredServiceAttributeReleasePolicyTests.VALUE_1);
        map.put(RegisteredServiceAttributeReleasePolicyTests.ATTR_2, RegisteredServiceAttributeReleasePolicyTests.VALUE_2);
        map.put(RegisteredServiceAttributeReleasePolicyTests.ATTR_3, Arrays.asList("v3", "v4"));
        Mockito.when(p.getAttributes()).thenReturn(map);
        Mockito.when(p.getId()).thenReturn(RegisteredServiceAttributeReleasePolicyTests.PRINCIPAL_ID);
        val attr = policy.getAttributes(p, CoreAttributesTestUtils.getService(), CoreAttributesTestUtils.getRegisteredService());
        Assertions.assertEquals(1, attr.size());
        Assertions.assertTrue(attr.containsKey(RegisteredServiceAttributeReleasePolicyTests.NEW_ATTR_1_VALUE));
        val data = SerializationUtils.serialize(policy);
        val p2 = SerializationUtils.deserializeAndCheckObject(data, ReturnMappedAttributeReleasePolicy.class);
        Assertions.assertNotNull(p2);
        Assertions.assertEquals(p2.getAllowedAttributes(), policy.getAllowedAttributes());
    }

    @Test
    public void verifyServiceAttributeFilterAllowedAttributes() {
        val policy = new ReturnAllowedAttributeReleasePolicy();
        policy.setAllowedAttributes(Arrays.asList(RegisteredServiceAttributeReleasePolicyTests.ATTR_1, RegisteredServiceAttributeReleasePolicyTests.ATTR_3));
        val p = Mockito.mock(Principal.class);
        val map = new HashMap<String, Object>();
        map.put(RegisteredServiceAttributeReleasePolicyTests.ATTR_1, RegisteredServiceAttributeReleasePolicyTests.VALUE_1);
        map.put(RegisteredServiceAttributeReleasePolicyTests.ATTR_2, RegisteredServiceAttributeReleasePolicyTests.VALUE_2);
        map.put(RegisteredServiceAttributeReleasePolicyTests.ATTR_3, Arrays.asList("v3", "v4"));
        Mockito.when(p.getAttributes()).thenReturn(map);
        Mockito.when(p.getId()).thenReturn(RegisteredServiceAttributeReleasePolicyTests.PRINCIPAL_ID);
        val attr = policy.getAttributes(p, CoreAttributesTestUtils.getService(), CoreAttributesTestUtils.getRegisteredService());
        Assertions.assertEquals(2, attr.size());
        Assertions.assertTrue(attr.containsKey(RegisteredServiceAttributeReleasePolicyTests.ATTR_1));
        Assertions.assertTrue(attr.containsKey(RegisteredServiceAttributeReleasePolicyTests.ATTR_3));
        val data = SerializationUtils.serialize(policy);
        val p2 = SerializationUtils.deserializeAndCheckObject(data, ReturnAllowedAttributeReleasePolicy.class);
        Assertions.assertNotNull(p2);
        Assertions.assertEquals(p2.getAllowedAttributes(), policy.getAllowedAttributes());
    }

    @Test
    public void verifyServiceAttributeDenyAllAttributes() {
        val policy = new DenyAllAttributeReleasePolicy();
        val p = Mockito.mock(Principal.class);
        val map = new HashMap<String, Object>();
        map.put("ATTR1", RegisteredServiceAttributeReleasePolicyTests.VALUE_1);
        map.put("ATTR2", RegisteredServiceAttributeReleasePolicyTests.VALUE_2);
        Mockito.when(p.getAttributes()).thenReturn(map);
        Mockito.when(p.getId()).thenReturn(RegisteredServiceAttributeReleasePolicyTests.PRINCIPAL_ID);
        val attr = policy.getAttributes(p, CoreAttributesTestUtils.getService(), CoreAttributesTestUtils.getRegisteredService());
        Assertions.assertTrue(attr.isEmpty());
    }

    @Test
    public void verifyServiceAttributeFilterAllAttributes() {
        val policy = new ReturnAllAttributeReleasePolicy();
        val p = Mockito.mock(Principal.class);
        val map = new HashMap<String, Object>();
        map.put(RegisteredServiceAttributeReleasePolicyTests.ATTR_1, RegisteredServiceAttributeReleasePolicyTests.VALUE_1);
        map.put(RegisteredServiceAttributeReleasePolicyTests.ATTR_2, RegisteredServiceAttributeReleasePolicyTests.VALUE_2);
        map.put(RegisteredServiceAttributeReleasePolicyTests.ATTR_3, Arrays.asList("v3", "v4"));
        Mockito.when(p.getAttributes()).thenReturn(map);
        Mockito.when(p.getId()).thenReturn(RegisteredServiceAttributeReleasePolicyTests.PRINCIPAL_ID);
        val attr = policy.getAttributes(p, CoreAttributesTestUtils.getService(), CoreAttributesTestUtils.getRegisteredService());
        Assertions.assertEquals(attr.size(), map.size());
        val data = SerializationUtils.serialize(policy);
        val p2 = SerializationUtils.deserializeAndCheckObject(data, ReturnAllAttributeReleasePolicy.class);
        Assertions.assertNotNull(p2);
    }

    @Test
    public void checkServiceAttributeFilterAllAttributesWithCachingTurnedOn() {
        val policy = new ReturnAllAttributeReleasePolicy();
        val attributes = new HashMap<String, java.util.List<Object>>();
        attributes.put("values", Arrays.asList(new Object[]{ "v1", "v2", "v3" }));
        attributes.put("cn", Arrays.asList(new Object[]{ "commonName" }));
        attributes.put("username", Arrays.asList(new Object[]{ "uid" }));
        val person = Mockito.mock(IPersonAttributes.class);
        Mockito.when(person.getName()).thenReturn("uid");
        Mockito.when(person.getAttributes()).thenReturn(attributes);
        val stub = new org.apereo.services.persondir.support.StubPersonAttributeDao(attributes);
        stub.setId("SampleStubRepository");
        val dao = new MergingPersonAttributeDaoImpl();
        dao.setPersonAttributeDaos(Collections.singletonList(stub));
        ApplicationContextProvider.registerBeanIntoApplicationContext(this.applicationContext, dao, "attributeRepository");
        val repository = new CachingPrincipalAttributesRepository(TimeUnit.MILLISECONDS.name(), 100);
        repository.setAttributeRepositoryIds(Set.of(stub.getId()));
        val p = new DefaultPrincipalFactory().createPrincipal("uid", Collections.singletonMap("mail", "final@example.com"));
        policy.setPrincipalAttributesRepository(repository);
        val service = CoreAttributesTestUtils.getService();
        val registeredService = CoreAttributesTestUtils.getRegisteredService();
        val attr = policy.getAttributes(p, service, registeredService);
        Assertions.assertEquals(((attributes.size()) + 1), attr.size());
    }

    @Test
    public void checkServiceAttributeFilterByAttributeRepositoryId() {
        val policy = new ReturnAllAttributeReleasePolicy();
        val attributes = new HashMap<String, java.util.List<Object>>();
        attributes.put("values", Arrays.asList(new Object[]{ "v1", "v2", "v3" }));
        attributes.put("cn", Arrays.asList(new Object[]{ "commonName" }));
        attributes.put("username", Arrays.asList(new Object[]{ "uid" }));
        val person = Mockito.mock(IPersonAttributes.class);
        Mockito.when(person.getName()).thenReturn("uid");
        Mockito.when(person.getAttributes()).thenReturn(attributes);
        val stub = new org.apereo.services.persondir.support.StubPersonAttributeDao(attributes);
        stub.setId("SampleStubRepository");
        val dao = new MergingPersonAttributeDaoImpl();
        dao.setPersonAttributeDaos(Collections.singletonList(stub));
        ApplicationContextProvider.registerBeanIntoApplicationContext(this.applicationContext, dao, "attributeRepository");
        val repository = new CachingPrincipalAttributesRepository(TimeUnit.MILLISECONDS.name(), 0);
        val p = new DefaultPrincipalFactory().createPrincipal("uid", Collections.singletonMap("mail", "final@example.com"));
        repository.setAttributeRepositoryIds(CollectionUtils.wrapSet("SampleStubRepository".toUpperCase()));
        policy.setPrincipalAttributesRepository(repository);
        var attr = policy.getAttributes(p, CoreAttributesTestUtils.getService(), CoreAttributesTestUtils.getRegisteredService());
        Assertions.assertEquals(attr.size(), ((attributes.size()) + 1));
        repository.setAttributeRepositoryIds(CollectionUtils.wrapSet("DoesNotExist"));
        policy.setPrincipalAttributesRepository(repository);
        attr = policy.getAttributes(p, CoreAttributesTestUtils.getService(), CoreAttributesTestUtils.getRegisteredService());
        Assertions.assertEquals(1, attr.size());
    }
}

