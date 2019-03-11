package com.alibaba.json.bvt.issue_1900;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;


public class Issue1933 extends TestCase {
    public void test_for_issue() throws Exception {
        Issue1933.OrderInfoVO v0 = JSON.parseObject("{\"orderStatus\":1}", Issue1933.OrderInfoVO.class);
        TestCase.assertEquals(1, v0.orderStatus);
        TestCase.assertEquals(0, v0.oldStatus);
        TestCase.assertEquals(0, v0.oldOrderStatus);
    }

    public void test_for_issue_1() throws Exception {
        Issue1933.OrderInfoVO v0 = JSON.parseObject("{\"oldStatus\":1}", Issue1933.OrderInfoVO.class);
        TestCase.assertEquals(0, v0.orderStatus);
        TestCase.assertEquals(1, v0.oldStatus);
        TestCase.assertEquals(0, v0.oldOrderStatus);
    }

    public void test_for_issue_2() throws Exception {
        Issue1933.OrderInfoVO v0 = JSON.parseObject("{\"oldOrderStatus\":1}", Issue1933.OrderInfoVO.class);
        TestCase.assertEquals(0, v0.orderStatus);
        TestCase.assertEquals(0, v0.oldStatus);
        TestCase.assertEquals(1, v0.oldOrderStatus);
    }

    public static class OrderInfoVO {
        private int orderStatus;

        private int oldStatus;

        private int oldOrderStatus;

        public int getOrderStatus() {
            return orderStatus;
        }

        public void setOrderStatus(int orderStatus) {
            this.orderStatus = orderStatus;
        }

        public int getOldStatus() {
            return oldStatus;
        }

        public void setOldStatus(int oldStatus) {
            this.oldStatus = oldStatus;
        }

        public int getOldOrderStatus() {
            return oldOrderStatus;
        }

        public void setOldOrderStatus(int oldOrderStatus) {
            this.oldOrderStatus = oldOrderStatus;
        }
    }
}

