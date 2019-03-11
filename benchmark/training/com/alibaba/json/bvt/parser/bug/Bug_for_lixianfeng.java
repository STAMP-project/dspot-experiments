package com.alibaba.json.bvt.parser.bug;


import com.alibaba.fastjson.JSON;
import java.util.List;
import junit.framework.TestCase;


/**
 * ??bug??????
 *
 * @author wenshao
 */
public class Bug_for_lixianfeng extends TestCase {
    public void test_long_list() throws Exception {
        String str = "{\"id\":14281,\"name\":\"test\",\"canPurchase\":1,\"categoryId\":955063}";
        JSON.parseObject(str, Bug_for_lixianfeng.Te.class);
    }

    public static class Te {
        private Long id;

        private String name;

        private List<Long> catIds;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<Long> getCatIds() {
            return catIds;
        }

        public void setCatIds(List<Long> catIds) {
            this.catIds = catIds;
        }
    }
}

