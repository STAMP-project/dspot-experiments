package org.apereo.cas.services;


import com.google.common.collect.ArrayListMultimap;
import java.util.Arrays;
import java.util.HashMap;
import lombok.Setter;
import lombok.val;
import org.apereo.cas.authentication.principal.Principal;
import org.apereo.cas.authentication.principal.Service;
import org.apereo.cas.util.CollectionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Unit test for {@link AbstractRegisteredService}.
 *
 * @author Marvin S. Addison
 * @since 3.4.12
 */
@Setter
public class AbstractRegisteredServiceTests {
    private static final long ID = 1000;

    private static final String SERVICE_ID = "test";

    private static final String DESCRIPTION = "test";

    private static final String SERVICEID = "serviceId";

    private static final String THEME = "theme";

    private static final String NAME = "name";

    private static final boolean ENABLED = false;

    private static final boolean ALLOWED_TO_PROXY = false;

    private static final boolean SSO_ENABLED = false;

    private static final String ATTR_1 = "attr1";

    private static final String ATTR_2 = "attr2";

    private static final String ATTR_3 = "attr3";

    private final AbstractRegisteredService r = new AbstractRegisteredService() {
        private static final long serialVersionUID = 1L;

        @Override
        public void setServiceId(final String id) {
            serviceId = id;
        }

        @Override
        protected AbstractRegisteredService newInstance() {
            return this;
        }

        @Override
        public boolean matches(final Service service) {
            return true;
        }

        @Override
        public boolean matches(final String serviceId) {
            return true;
        }
    };

    @Test
    public void verifyAllowToProxyIsFalseByDefault() {
        val regexRegisteredService = new RegexRegisteredService();
        Assertions.assertFalse(regexRegisteredService.getProxyPolicy().isAllowedToProxy());
        val service = new RegexRegisteredService();
        Assertions.assertFalse(service.getProxyPolicy().isAllowedToProxy());
    }

    @Test
    public void verifySettersAndGetters() {
        prepareService();
        Assertions.assertEquals(AbstractRegisteredServiceTests.ALLOWED_TO_PROXY, this.r.getProxyPolicy().isAllowedToProxy());
        Assertions.assertEquals(AbstractRegisteredServiceTests.DESCRIPTION, this.r.getDescription());
        Assertions.assertEquals(AbstractRegisteredServiceTests.ENABLED, this.r.getAccessStrategy().isServiceAccessAllowed());
        Assertions.assertEquals(AbstractRegisteredServiceTests.ID, this.r.getId());
        Assertions.assertEquals(AbstractRegisteredServiceTests.NAME, this.r.getName());
        Assertions.assertEquals(AbstractRegisteredServiceTests.SERVICEID, this.r.getServiceId());
        Assertions.assertEquals(AbstractRegisteredServiceTests.SSO_ENABLED, this.r.getAccessStrategy().isServiceAccessAllowedForSso());
        Assertions.assertEquals(AbstractRegisteredServiceTests.THEME, this.r.getTheme());
        Assertions.assertNotNull(this.r);
        Assertions.assertNotEquals(this.r, new Object());
        Assertions.assertEquals(this.r, this.r);
    }

    @Test
    public void verifyServiceAttributeFilterAllAttributes() {
        prepareService();
        this.r.setAttributeReleasePolicy(new ReturnAllAttributeReleasePolicy());
        val p = Mockito.mock(Principal.class);
        val map = new HashMap<String, Object>();
        map.put(AbstractRegisteredServiceTests.ATTR_1, "value1");
        map.put(AbstractRegisteredServiceTests.ATTR_2, "value2");
        map.put(AbstractRegisteredServiceTests.ATTR_3, Arrays.asList("v3", "v4"));
        Mockito.when(p.getAttributes()).thenReturn(map);
        Mockito.when(p.getId()).thenReturn("principalId");
        val attr = this.r.getAttributeReleasePolicy().getAttributes(p, RegisteredServiceTestUtils.getService(), RegisteredServiceTestUtils.getRegisteredService(AbstractRegisteredServiceTests.SERVICE_ID));
        Assertions.assertEquals(attr.size(), map.size());
    }

    @Test
    public void verifyServiceAttributeFilterAllowedAttributes() {
        prepareService();
        val policy = new ReturnAllowedAttributeReleasePolicy();
        policy.setAllowedAttributes(Arrays.asList(AbstractRegisteredServiceTests.ATTR_1, AbstractRegisteredServiceTests.ATTR_3));
        this.r.setAttributeReleasePolicy(policy);
        val p = Mockito.mock(Principal.class);
        val map = new HashMap<String, Object>();
        map.put(AbstractRegisteredServiceTests.ATTR_1, "value1");
        map.put(AbstractRegisteredServiceTests.ATTR_2, "value2");
        map.put(AbstractRegisteredServiceTests.ATTR_3, Arrays.asList("v3", "v4"));
        Mockito.when(p.getAttributes()).thenReturn(map);
        Mockito.when(p.getId()).thenReturn("principalId");
        val attr = this.r.getAttributeReleasePolicy().getAttributes(p, RegisteredServiceTestUtils.getService(), RegisteredServiceTestUtils.getRegisteredService(AbstractRegisteredServiceTests.SERVICE_ID));
        Assertions.assertEquals(2, attr.size());
        Assertions.assertTrue(attr.containsKey(AbstractRegisteredServiceTests.ATTR_1));
        Assertions.assertTrue(attr.containsKey(AbstractRegisteredServiceTests.ATTR_3));
    }

    @Test
    public void verifyServiceAttributeFilterMappedAttributes() {
        prepareService();
        val policy = new ReturnMappedAttributeReleasePolicy();
        val mappedAttr = ArrayListMultimap.<String, Object>create();
        mappedAttr.put(AbstractRegisteredServiceTests.ATTR_1, "newAttr1");
        policy.setAllowedAttributes(CollectionUtils.wrap(mappedAttr));
        this.r.setAttributeReleasePolicy(policy);
        val p = Mockito.mock(Principal.class);
        val map = new HashMap<String, Object>();
        map.put(AbstractRegisteredServiceTests.ATTR_1, "value1");
        map.put(AbstractRegisteredServiceTests.ATTR_2, "value2");
        map.put(AbstractRegisteredServiceTests.ATTR_3, Arrays.asList("v3", "v4"));
        Mockito.when(p.getAttributes()).thenReturn(map);
        Mockito.when(p.getId()).thenReturn("principalId");
        val attr = this.r.getAttributeReleasePolicy().getAttributes(p, RegisteredServiceTestUtils.getService(), RegisteredServiceTestUtils.getRegisteredService(AbstractRegisteredServiceTests.SERVICE_ID));
        Assertions.assertEquals(1, attr.size());
        Assertions.assertTrue(attr.containsKey("newAttr1"));
    }

    @Test
    public void verifyServiceEquality() {
        val svc1 = RegisteredServiceTestUtils.getRegisteredService(AbstractRegisteredServiceTests.SERVICEID, false);
        val svc2 = RegisteredServiceTestUtils.getRegisteredService(AbstractRegisteredServiceTests.SERVICEID, false);
        Assertions.assertEquals(svc1, svc2);
    }

    @Test
    public void verifyServiceWithInvalidIdStillHasTheSameIdAfterCallingMatches() {
        val invalidId = "***";
        val service = RegisteredServiceTestUtils.getRegisteredService(invalidId);
        service.matches("notRelevant");
        Assertions.assertEquals(invalidId, service.getServiceId());
    }
}

