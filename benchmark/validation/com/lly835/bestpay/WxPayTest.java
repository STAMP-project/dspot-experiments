package com.lly835.bestpay;


import BestPayTypeEnum.WXPAY_H5;
import com.lly835.bestpay.config.WxPayH5Config;
import com.lly835.bestpay.model.OrderQueryRequest;
import com.lly835.bestpay.model.OrderQueryResponse;
import com.lly835.bestpay.model.PayRequest;
import com.lly835.bestpay.model.PayResponse;
import com.lly835.bestpay.model.RefundRequest;
import com.lly835.bestpay.model.RefundResponse;
import com.lly835.bestpay.service.impl.BestPayServiceImpl;
import com.lly835.bestpay.utils.JsonUtil;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;


/**
 * Created by ???
 * 2017-07-02 14:26
 */
@Slf4j
public class WxPayTest {
    private WxPayH5Config wxPayH5Config;

    private BestPayServiceImpl bestPayService = new BestPayServiceImpl();

    @Test
    public void pay() {
        PayRequest request = new PayRequest();
        request.setPayTypeEnum(WXPAY_H5);
        request.setOrderId(("111111111222" + (new Random().nextInt(1000))));
        request.setOrderAmount(0.01);
        request.setOrderName("??h5??");
        request.setOpenid("oTgZpweNnfivA9ER9EIXoH-jlrWQ");
        PayResponse response = bestPayService.pay(request);
        System.out.println(JsonUtil.toJson(response));
    }

    @Test
    public void refund() {
        RefundRequest request = new RefundRequest();
        request.setOrderId("4171207152120180517165324719749");
        request.setOrderAmount(5.51);
        RefundResponse response = bestPayService.refund(request);
        log.info(JsonUtil.toJson(response));
    }

    @Test
    public void query() {
        OrderQueryRequest request = new OrderQueryRequest();
        request.setPayTypeEnum(WXPAY_H5);
        request.setOrderId("1528103259255858491");
        OrderQueryResponse response = bestPayService.query(request);
        log.info(JsonUtil.toJson(response));
    }
}

