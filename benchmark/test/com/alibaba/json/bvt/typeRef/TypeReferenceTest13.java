package com.alibaba.json.bvt.typeRef;


import com.alibaba.fastjson.annotation.JSONField;
import junit.framework.TestCase;


/**
 * Created by wenshao on 09/02/2017.
 */
public class TypeReferenceTest13 extends TestCase {
    public void test_typeRef() throws Exception {
        String json = "{\"result\":{}}";
        for (int i = 0; i < 100; ++i) {
            {
                TypeReferenceTest13.SearchResult<TypeReferenceTest13.ResultItem, TypeReferenceTest13.CountFacet> searchResult = TypeReferenceTest13.parseSearchResult(json, TypeReferenceTest13.ResultItem.class, TypeReferenceTest13.CountFacet.class);
            }
            {
                TypeReferenceTest13.SearchResult<TypeReferenceTest13.ResultItem1, TypeReferenceTest13.CountFacet1> searchResult = TypeReferenceTest13.parseSearchResult(json, TypeReferenceTest13.ResultItem1.class, TypeReferenceTest13.CountFacet1.class);
            }
        }
    }

    public static class ResultItem {}

    public static class CountFacet {}

    public static class ResultItem1 {}

    public static class CountFacet1 {}

    public static class SearchResult<I, F> extends TypeReferenceTest13.BaseResult {
        /**
         * ????????????????????????
         */
        @JSONField(name = "result")
        private TypeReferenceTest13.ResultDO<I, F> result;

        /**
         * ??????
         */
        @JSONField(name = "tracer")
        private String tracer;

        public String getTracer() {
            return tracer;
        }

        public void setTracer(String tracer) {
            this.tracer = tracer;
        }

        public TypeReferenceTest13.ResultDO<I, F> getResult() {
            return result;
        }

        public void setResult(TypeReferenceTest13.ResultDO<I, F> result) {
            this.result = result;
        }
    }

    public static class BaseResult {}

    public static class ResultDO<I, F> {}
}

