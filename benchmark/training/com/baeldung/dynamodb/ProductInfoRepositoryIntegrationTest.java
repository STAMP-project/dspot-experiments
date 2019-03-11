package com.baeldung.dynamodb;


import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.baeldung.dynamodb.entity.ProductInfo;
import com.baeldung.dynamodb.repository.ProductInfoRepository;
import com.baeldung.dynamodb.rule.LocalDbCreationRule;
import java.util.List;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;


public class ProductInfoRepositoryIntegrationTest {
    @ClassRule
    public static LocalDbCreationRule dynamoDB = new LocalDbCreationRule();

    private static DynamoDBMapper dynamoDBMapper;

    private static AmazonDynamoDB amazonDynamoDB;

    private ProductInfoRepository repository;

    private static final String DYNAMODB_ENDPOINT = "amazon.dynamodb.endpoint";

    private static final String AWS_ACCESSKEY = "amazon.aws.accesskey";

    private static final String AWS_SECRETKEY = "amazon.aws.secretkey";

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

