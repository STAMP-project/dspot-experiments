package com.alibaba.json.bvtVO;


import com.alibaba.fastjson.JSON;
import org.junit.Test;


public class ArgCheckTest {
    @Test
    public void testJSON() {
        ArgCheckTest.TestDTO dto = new ArgCheckTest.TestDTO();
        dto.setChannel("channel");
        ArgCheckTest.TestDTO[] dtos = new ArgCheckTest.TestDTO[2];
        dtos[0] = dto;
        dtos[1] = dto;
        JSON.toJSONString(dtos);
    }

    public static class TestDTO {
        private String channel;

        private String txCode;

        public String getChannel() {
            return channel;
        }

        public void setChannel(String channel) {
            this.channel = channel;
        }

        public String getTxCode() {
            return txCode;
        }

        public void setTxCode(String txCode) {
            this.txCode = txCode;
        }
    }
}

