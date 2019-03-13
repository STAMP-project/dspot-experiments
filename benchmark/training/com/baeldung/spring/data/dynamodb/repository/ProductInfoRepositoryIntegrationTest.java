package com.baeldung.spring.data.dynamodb.repository;


import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.baeldung.Application;
import com.baeldung.spring.data.dynamodb.model.ProductInfo;
import com.baeldung.spring.data.dynamodb.repositories.ProductInfoRepository;
import com.baeldung.spring.data.dynamodb.repository.rule.LocalDbCreationRule;
import java.util.List;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("local")
@TestPropertySource(properties = { "amazon.dynamodb.endpoint=http://localhost:8000/", "amazon.aws.accesskey=test1", "amazon.aws.secretkey=test231" })
public class ProductInfoRepositoryIntegrationTest {
    @ClassRule
    public static LocalDbCreationRule dynamoDB = new LocalDbCreationRule();

    private DynamoDBMapper dynamoDBMapper;

    @Autowired
    private AmazonDynamoDB amazonDynamoDB;

    @Autowired
    ProductInfoRepository repository;

    private static final String EXPECTED_COST = "20";

    private static final String EXPECTED_PRICE = "50";

    @Test
    public void givenItemWithExpectedCost_whenRunFindAll_thenItemIsFound() {
        ProductInfo productInfo = new ProductInfo(ProductInfoRepositoryIntegrationTest.EXPECTED_COST, ProductInfoRepositoryIntegrationTest.EXPECTED_PRICE);
        repository.save(productInfo);
        List<ProductInfo> result = ((List<ProductInfo>) (repository.findAll()));
        Assert.assertThat(result.size(), Is.is(Matchers.greaterThan(0)));
        Assert.assertThat(result.get(0).getCost(), Is.is(IsEqual.equalTo(ProductInfoRepositoryIntegrationTest.EXPECTED_COST)));
    }
}

