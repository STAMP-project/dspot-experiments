package com.baeldung.dao.repositories.product;


import com.baeldung.config.PersistenceProductConfiguration;
import com.baeldung.domain.product.Product;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { PersistenceProductConfiguration.class })
@EnableTransactionManagement
public class ProductRepositoryIntegrationTest {
    @Autowired
    private ProductRepository productRepository;

    @Test
    public void whenRequestingFirstPageOfSizeTwo_ThenReturnFirstPage() {
        Pageable pageRequest = PageRequest.of(0, 2);
        Page<Product> result = productRepository.findAll(pageRequest);
        Assert.assertThat(result.getContent(), Matchers.hasSize(2));
        Assert.assertTrue(result.stream().map(Product::getId).allMatch(( id) -> Arrays.asList(1001, 1002).contains(id)));
    }

    @Test
    public void whenRequestingSecondPageOfSizeTwo_ThenReturnSecondPage() {
        Pageable pageRequest = PageRequest.of(1, 2);
        Page<Product> result = productRepository.findAll(pageRequest);
        Assert.assertThat(result.getContent(), Matchers.hasSize(2));
        Assert.assertTrue(result.stream().map(Product::getId).allMatch(( id) -> Arrays.asList(1003, 1004).contains(id)));
    }

    @Test
    public void whenRequestingLastPage_ThenReturnLastPageWithRemData() {
        Pageable pageRequest = PageRequest.of(2, 2);
        Page<Product> result = productRepository.findAll(pageRequest);
        Assert.assertThat(result.getContent(), Matchers.hasSize(1));
        Assert.assertTrue(result.stream().map(Product::getId).allMatch(( id) -> Arrays.asList(1005).contains(id)));
    }

    @Test
    public void whenSortingByNameAscAndPaging_ThenReturnSortedPagedResult() {
        Pageable pageRequest = PageRequest.of(0, 3, Sort.by("name"));
        Page<Product> result = productRepository.findAll(pageRequest);
        Assert.assertThat(result.getContent(), Matchers.hasSize(3));
        Assert.assertThat(result.getContent().stream().map(Product::getId).collect(Collectors.toList()), Matchers.equalTo(Arrays.asList(1005, 1001, 1002)));
    }

    @Test
    public void whenSortingByPriceDescAndPaging_ThenReturnSortedPagedResult() {
        Pageable pageRequest = PageRequest.of(0, 3, Sort.by("price").descending());
        Page<Product> result = productRepository.findAll(pageRequest);
        Assert.assertThat(result.getContent(), Matchers.hasSize(3));
        Assert.assertThat(result.getContent().stream().map(Product::getId).collect(Collectors.toList()), Matchers.equalTo(Arrays.asList(1004, 1003, 1001)));
    }

    @Test
    public void whenSortingByPriceDescAndNameAscAndPaging_ThenReturnSortedPagedResult() {
        Pageable pageRequest = PageRequest.of(0, 5, Sort.by("price").descending().and(Sort.by("name")));
        Page<Product> result = productRepository.findAll(pageRequest);
        Assert.assertThat(result.getContent(), Matchers.hasSize(5));
        Assert.assertThat(result.getContent().stream().map(Product::getId).collect(Collectors.toList()), Matchers.equalTo(Arrays.asList(1004, 1003, 1001, 1005, 1002)));
    }

    @Test
    public void whenRequestingFirstPageOfSizeTwoUsingCustomMethod_ThenReturnFirstPage() {
        Pageable pageRequest = PageRequest.of(0, 2);
        List<Product> result = productRepository.findAllByPrice(10, pageRequest);
        Assert.assertThat(result, Matchers.hasSize(2));
        Assert.assertTrue(result.stream().map(Product::getId).allMatch(( id) -> Arrays.asList(1002, 1005).contains(id)));
    }
}

