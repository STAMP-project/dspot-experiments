package com.macro.mall.search;


import com.macro.mall.search.dao.EsProductDao;
import com.macro.mall.search.domain.EsProduct;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
public class MallSearchApplicationTests {
    @Autowired
    private EsProductDao productDao;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Test
    public void testGetAllEsProductList() {
        List<EsProduct> esProductList = productDao.getAllEsProductList(null);
        System.out.print(esProductList);
    }

    @Test
    public void testEsProductMapping() {
        elasticsearchTemplate.putMapping(EsProduct.class);
        Map mapping = elasticsearchTemplate.getMapping(EsProduct.class);
        System.out.println(mapping);
    }
}

