package com.alibaba.json.bvt.bug;


import cn.com.tx.domain.notifyDetail.NotifyDetail;
import cn.com.tx.domain.pagination.Pagination;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;


public class Bug_for_generic_1 extends TestCase {
    public void test() {
        String json2 = "{\"@type\":\"cn.com.tx.domain.pagination.Pagination\",\"fromIndex\":0,\"list\":[{\"@type\":\"cn.com.tx.domain.notifyDetail.NotifyDetail\",\"args\":[\"61354557\",\"\u4f9d\u4f9d\",\"\u516d\"],\"destId\":60721687,\"detailId\":3155063,\"display\":false,\"foundTime\":{\"@type\":\"java.sql.Timestamp\",\"val\":1344530416000},\"hotId\":0,\"srcId\":1000,\"templateId\":482},{\"@type\":\"cn.com.tx.domain.notifyDetail.NotifyDetail\",\"args\":[\"14527269\",\"\u61d2\u6d0b\u6d0b\",\"///\u6700\u4f73\u62cd\u6863,\u975e\u5e38\",\"24472950\"],\"destId\":60721687,\"detailId\":3151609,\"display\":false,\"foundTime\":{\"@type\":\"java.sql.Timestamp\",\"val\":1344354485000},\"hotId\":0,\"srcId\":1000,\"templateId\":40},{\"@type\":\"cn.com.tx.domain.notifyDetail.NotifyDetail\",\"args\":[\"51090218\",\"\u5929\u4e4b\u6daf\",\"\u5929\u4f1a\u9ed1\uff0c\u4eba\u4f1a\u53d8\u3002\u4e09\u5206\"],\"destId\":60721687,\"detailId\":3149221,\"display\":false,\"foundTime\":{\"@type\":\"java.sql.Timestamp\",\"val\":1344247529000},\"hotId\":0,\"srcId\":1000,\"templateId\":459},{\"@type\":\"cn.com.tx.domain.notifyDetail.NotifyDetail\",\"args\":[\"51687981\",\"\u6479\u7136\u56de\u9996\u68a6\u5df2\u6210\u5e74\",\"\u661f\u661f\u5728\u54ea\u91cc\u90fd\u5f88\u4eae\u7684,\"],\"destId\":60721687,\"detailId\":3149173,\"display\":false,\"foundTime\":{\"@type\":\"java.sql.Timestamp\",\"val\":1344247414000},\"hotId\":0,\"srcId\":1000,\"templateId\":459},{\"@type\":\"cn.com.tx.domain.notifyDetail.NotifyDetail\",\"args\":[\"41486427\",\"\u5bd2\u6c5f\u84d1\u7b20\",\"\u53cc\u4f11\u4e86\"],\"destId\":60721687,\"detailId\":3148148,\"display\":false,\"foundTime\":{\"@type\":\"java.sql.Timestamp\",\"val\":1344244730000},\"hotId\":0,\"srcId\":1000,\"templateId\":459}],\"maxLength\":5,\"nextPage\":2,\"pageIndex\":1,\"prevPage\":1,\"toIndex\":5,\"totalPage\":3,\"totalResult\":13}";
        Pagination<NotifyDetail> pagination = JSON.parseObject(json2, new com.alibaba.fastjson.TypeReference<Pagination<NotifyDetail>>() {});
    }
}

