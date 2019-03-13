package com.salesmanager.test.shop.integration.product;


import com.salesmanager.core.business.constants.Constants;
import com.salesmanager.shop.model.catalog.category.Category;
import com.salesmanager.shop.model.catalog.category.CategoryDescription;
import com.salesmanager.shop.model.catalog.category.PersistableCategory;
import com.salesmanager.shop.model.catalog.product.PersistableProduct;
import com.salesmanager.test.shop.common.ServicesTestSupport;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;


public class ProductManagementAPIIntegrationTest extends ServicesTestSupport {
    private RestTemplate restTemplate;

    private Long testCategoryID;

    private Long testProductID;

    @Test
    public void createProductWithCategory() throws Exception {
        final PersistableCategory newCategory = new PersistableCategory();
        newCategory.setCode("test-cat");
        newCategory.setSortOrder(1);
        newCategory.setVisible(true);
        newCategory.setDepth(4);
        final Category parent = new Category();
        newCategory.setParent(parent);
        final CategoryDescription description = new CategoryDescription();
        description.setLanguage("en");
        description.setName("test-cat");
        description.setFriendlyUrl("test-cat");
        description.setTitle("test-cat");
        final List<CategoryDescription> descriptions = new ArrayList<>();
        descriptions.add(description);
        newCategory.setDescriptions(descriptions);
        final HttpEntity<PersistableCategory> categoryEntity = new HttpEntity(newCategory, getHeader());
        final ResponseEntity<PersistableCategory> categoryResponse = testRestTemplate.postForEntity(("/api/v1/private/category?store=" + (Constants.DEFAULT_STORE)), categoryEntity, PersistableCategory.class);
        final PersistableCategory cat = categoryResponse.getBody();
        Assert.assertThat(categoryResponse.getStatusCode(), Is.is(OK));
        Assert.assertNotNull(cat.getId());
        final PersistableProduct product = new PersistableProduct();
        final ArrayList<Category> categories = new ArrayList<>();
        categories.add(cat);
        product.setCategories(categories);
        product.setManufacturer(createManufacturer());
        product.setPrice(BigDecimal.TEN);
        product.setSku("123");
        final HttpEntity<PersistableProduct> entity = new HttpEntity(product, getHeader());
        final ResponseEntity<PersistableProduct> response = testRestTemplate.postForEntity(("/api/v1/private/products?store=" + (Constants.DEFAULT_STORE)), entity, PersistableProduct.class);
        Assert.assertThat(response.getStatusCode(), Is.is(CREATED));
    }
}

