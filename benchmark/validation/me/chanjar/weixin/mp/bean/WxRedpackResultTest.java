package me.chanjar.weixin.mp.bean;


import com.thoughtworks.xstream.XStream;
import me.chanjar.weixin.mp.bean.result.WxRedpackResult;
import org.junit.Assert;
import org.junit.Test;


public class WxRedpackResultTest {
    private XStream xstream;

    @Test
    public void loadSuccessResult() {
        final String successSample = "<xml>\n" + (((((((((((("<return_code><![CDATA[SUCCESS]]></return_code>\n" + "<return_msg><![CDATA[\u53d1\u653e\u6210\u529f.]]></return_msg>\n") + "<result_code><![CDATA[SUCCESS]]></result_code>\n") + "<err_code><![CDATA[0]]></err_code>\n") + "<err_code_des><![CDATA[\u53d1\u653e\u6210\u529f.]]></err_code_des>\n") + "<mch_billno><![CDATA[0010010404201411170000046545]]></mch_billno>\n") + "<mch_id>10010404</mch_id>\n") + "<wxappid><![CDATA[wx6fa7e3bab7e15415]]></wxappid>\n") + "<re_openid><![CDATA[onqOjjmM1tad-3ROpncN-yUfa6uI]]></re_openid>\n") + "<total_amount>1</total_amount>\n") + "<send_listid>100000000020150520314766074200</send_listid>\n") + "<send_time>20150520102602</send_time>\n") + "</xml>");
        WxRedpackResult wxMpRedpackResult = ((WxRedpackResult) (xstream.fromXML(successSample)));
        Assert.assertEquals("SUCCESS", wxMpRedpackResult.getReturnCode());
        Assert.assertEquals("SUCCESS", wxMpRedpackResult.getResultCode());
        Assert.assertEquals("20150520102602", wxMpRedpackResult.getSendTime());
    }

    @Test
    public void loadFailureResult() {
        final String failureSample = "<xml>\n" + (((((((((("<return_code><![CDATA[FAIL]]></return_code>\n" + "<return_msg><![CDATA[\u7cfb\u7edf\u7e41\u5fd9,\u8bf7\u7a0d\u540e\u518d\u8bd5.]]></return_msg>\n") + "<result_code><![CDATA[FAIL]]></result_code>\n") + "<err_code><![CDATA[268458547]]></err_code>\n") + "<err_code_des><![CDATA[\u7cfb\u7edf\u7e41\u5fd9,\u8bf7\u7a0d\u540e\u518d\u8bd5.]]></err_code_des>\n") + "<mch_billno><![CDATA[0010010404201411170000046542]]></mch_billno>\n") + "<mch_id>10010404</mch_id>\n") + "<wxappid><![CDATA[wx6fa7e3bab7e15415]]></wxappid>\n") + "<re_openid><![CDATA[onqOjjmM1tad-3ROpncN-yUfa6uI]]></re_openid>\n") + "<total_amount>1</total_amount>\n") + "</xml>");
        WxRedpackResult wxMpRedpackResult = ((WxRedpackResult) (xstream.fromXML(failureSample)));
        Assert.assertEquals("FAIL", wxMpRedpackResult.getReturnCode());
        Assert.assertEquals("FAIL", wxMpRedpackResult.getResultCode());
        Assert.assertEquals("onqOjjmM1tad-3ROpncN-yUfa6uI", wxMpRedpackResult.getReOpenid());
        Assert.assertEquals(1, wxMpRedpackResult.getTotalAmount());
    }
}

