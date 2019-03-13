package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import com.alibaba.json.bvtVO.IEvent;
import com.alibaba.json.bvtVO.IEventDto;
import junit.framework.TestCase;


public class Bug_for_hmy8 extends TestCase {
    public void test_ser() throws Exception {
        IEventDto dto = new IEventDto();
        dto.getEventList().add(new IEvent());
        JSON.toJSONString(dto);
    }
}

