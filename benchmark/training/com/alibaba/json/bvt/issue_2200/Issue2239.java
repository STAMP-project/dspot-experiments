package com.alibaba.json.bvt.issue_2200;


import com.alibaba.fastjson.JSON;
import java.util.List;
import junit.framework.TestCase;


public class Issue2239 extends TestCase {
    public void test_for_issue() throws Exception {
        String json = "{\"page\":{}}";
        Issue2239.BaseResponse<Issue2239.Bean> bean = JSON.parseObject(json, new com.alibaba.fastjson.TypeReference<Issue2239.BaseResponse<Issue2239.Bean>>() {});
        // bean.getPage().getList(); // ?????
    }

    public static class Bean {}

    public static class BaseResponse<T> {
        private Issue2239.PageBean<T> page;

        public Issue2239.PageBean<T> getPage() {
            return page;
        }
    }

    public static class PageBean<T> {
        private List<T> list;

        public List<T> getList() {
            return list;
        }

        public void setList(List<T> list) {
            this.list = list;
        }
    }
}

