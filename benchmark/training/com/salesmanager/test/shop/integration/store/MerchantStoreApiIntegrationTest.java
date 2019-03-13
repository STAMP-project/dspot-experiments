package com.salesmanager.test.shop.integration.store;


import HttpMethod.DELETE;
import HttpMethod.GET;
import HttpMethod.POST;
import HttpStatus.CREATED;
import HttpStatus.OK;
import MediaType.MULTIPART_FORM_DATA;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.shop.application.ShopApplication;
import com.salesmanager.shop.model.references.PersistableAddress;
import com.salesmanager.shop.model.shop.PersistableMerchantStore;
import com.salesmanager.shop.model.shop.ReadableMerchantStore;
import com.salesmanager.test.shop.common.ServicesTestSupport;
import java.util.Arrays;
import javax.inject.Inject;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;


@SpringBootTest(classes = ShopApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class MerchantStoreApiIntegrationTest extends ServicesTestSupport {
    private static final String TEST_STORE_CODE = "test";

    private static final String CURRENCY = "CAD";

    private static final String DEFAULT_LANGUAGE = "en";

    @Inject
    private TestRestTemplate testRestTemplate;

    /**
     * Test get DEFAULT store
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testGetDefaultStore() throws Exception {
        final HttpEntity<String> httpEntity = new HttpEntity(getHeader());
        final ResponseEntity<ReadableMerchantStore> response = testRestTemplate.exchange(String.format(("/api/v1/store/" + (MerchantStore.DEFAULT_STORE))), GET, httpEntity, ReadableMerchantStore.class);
        if ((response.getStatusCode()) != (HttpStatus.OK)) {
            throw new Exception(response.toString());
        } else {
            final ReadableMerchantStore store = response.getBody();
            Assert.assertNotNull(store);
        }
    }

    /**
     * Create a new store then delete it
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testCreateStoreAndDelete() throws Exception {
        PersistableAddress address = new PersistableAddress();
        address.setAddress("121212 simple address");
        address.setPostalCode("12345");
        address.setCountry("US");
        address.setCity("FT LD");
        address.setStateProvince("FL");
        PersistableMerchantStore createdStore = new PersistableMerchantStore();
        createdStore.setCode(MerchantStoreApiIntegrationTest.TEST_STORE_CODE);
        createdStore.setCurrency(MerchantStoreApiIntegrationTest.CURRENCY);
        createdStore.setDefaultLanguage(MerchantStoreApiIntegrationTest.DEFAULT_LANGUAGE);
        createdStore.setEmail("test@test.com");
        createdStore.setName(MerchantStoreApiIntegrationTest.TEST_STORE_CODE);
        createdStore.setPhone("444-555-6666");
        createdStore.setSupportedLanguages(Arrays.asList(MerchantStoreApiIntegrationTest.DEFAULT_LANGUAGE));
        createdStore.setAddress(address);
        final HttpEntity<PersistableMerchantStore> httpEntity = new HttpEntity<PersistableMerchantStore>(createdStore, getHeader());
        ResponseEntity<ReadableMerchantStore> response = testRestTemplate.exchange(String.format("/api/v1/private/store/"), POST, httpEntity, ReadableMerchantStore.class);
        if ((response.getStatusCode()) != (HttpStatus.OK)) {
            throw new Exception(response.toString());
        } else {
            final ReadableMerchantStore store = response.getBody();
            Assert.assertNotNull(store);
        }
        // delete store
        ResponseEntity<Void> deleteResponse = testRestTemplate.exchange(String.format(("/api/v1/private/store/" + (MerchantStoreApiIntegrationTest.TEST_STORE_CODE))), DELETE, httpEntity, Void.class);
        MatcherAssert.assertThat(deleteResponse.getStatusCode(), Matchers.is(OK));
    }

    @Test
    public void testAddAndDeleteStoreLogo() {
        LinkedMultiValueMap<String, Object> parameters = new LinkedMultiValueMap<String, Object>();
        parameters.add("file", new ClassPathResource("image.jpg"));
        HttpHeaders headers = getHeader();
        headers.setContentType(MULTIPART_FORM_DATA);
        HttpEntity<LinkedMultiValueMap<String, Object>> entity = new HttpEntity<LinkedMultiValueMap<String, Object>>(parameters, headers);
        ResponseEntity<Void> createResponse = testRestTemplate.exchange(String.format((("/api/v1/private/store/" + (MerchantStore.DEFAULT_STORE)) + "/marketing/logo")), POST, entity, Void.class);
        // Expect Created
        MatcherAssert.assertThat(createResponse.getStatusCode(), Matchers.is(CREATED));
        // now remove logo
        HttpEntity<Void> deleteRequest = new HttpEntity<Void>(getHeader());
        ResponseEntity<Void> deleteResponse = testRestTemplate.exchange(String.format((("/api/v1/private/store/" + (MerchantStore.DEFAULT_STORE)) + "/marketing/logo")), DELETE, deleteRequest, Void.class);
        // Expect Ok
        MatcherAssert.assertThat(deleteResponse.getStatusCode(), Matchers.is(OK));
    }
}

