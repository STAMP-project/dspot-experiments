package com.alibaba.json.bvt.issue_1300;


import com.alibaba.fastjson.JSON;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import junit.framework.TestCase;


/**
 * Created by kimmking on 02/07/2017.
 */
public class Issue1306 extends TestCase {
    public void test_for_issue() {
        Issue1306.Goods goods = new Issue1306.Goods();
        goods.setProperties(Arrays.asList(new Issue1306.Goods.Property()));
        Issue1306.TT tt = new Issue1306.TT(goods);
        String json = JSON.toJSONString(tt);
        TestCase.assertEquals("{\"goodsList\":[{\"properties\":[{}]}]}", json);
        Issue1306.TT n = JSON.parseObject(json, Issue1306.TT.class);
        TestCase.assertNotNull(n);
        TestCase.assertNotNull(n.getGoodsList());
        TestCase.assertNotNull(n.getGoodsList().get(0));
        TestCase.assertNotNull(n.getGoodsList().get(0).getProperties());
    }

    public abstract static class IdEntity<ID extends Serializable> implements Serializable , Cloneable {
        private static final long serialVersionUID = 4877536176216854937L;

        public IdEntity() {
        }

        public abstract ID getId();

        public abstract void setId(ID id);
    }

    public static class LongEntity extends Issue1306.IdEntity<Long> {
        private static final long serialVersionUID = -2740365657805589848L;

        private Long id;

        @Override
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }
    }

    public static class Goods extends Issue1306.LongEntity {
        private static final long serialVersionUID = -5751106975913625097L;

        private List<Issue1306.Goods.Property> properties;

        public List<Issue1306.Goods.Property> getProperties() {
            return properties;
        }

        public void setProperties(List<Issue1306.Goods.Property> properties) {
            this.properties = properties;
        }

        public static class Property extends Issue1306.LongEntity {
            private static final long serialVersionUID = 7941148286688199390L;
        }
    }

    public static class TT extends Issue1306.LongEntity {
        private static final long serialVersionUID = 2988415809510669142L;

        public TT() {
        }

        public TT(Issue1306.Goods goods) {
            goodsList = Arrays.asList(goods);
        }

        private List<Issue1306.Goods> goodsList;

        public List<Issue1306.Goods> getGoodsList() {
            return goodsList;
        }

        public void setGoodsList(List<Issue1306.Goods> goodsList) {
            this.goodsList = goodsList;
        }
    }
}

