/**
 * Copyright 2011 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.json.bvt.bug;


import SerializerFeature.WriteClassName;
import com.alibaba.fastjson.JSON;
import com.alibaba.json.bvtVO.OptionKey;
import com.alibaba.json.bvtVO.OptionValue;
import com.alibaba.json.bvtVO.TempAttachMetaOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;


/**
 * ?SerDeserTest.java??????TODO ?????
 *
 * @author lei.yaol 2011-12-27 ??03:44:18
 */
public class SerDeserTest extends TestCase {
    /**
     * ???FastJson??????????
     */
    private static Map<OptionKey, OptionValue<?>> options;

    static {
        SerDeserTest.options = new HashMap<OptionKey, OptionValue<?>>();
        TempAttachMetaOption attach = new TempAttachMetaOption();
        attach.setId(1000);
        attach.setName("test_name");
        attach.setPath("http://alibaba-inc.com/test.txt");
        ArrayList<TempAttachMetaOption> attachList = new ArrayList<TempAttachMetaOption>();
        attachList.add(attach);
        // ??value
        OptionValue<ArrayList<TempAttachMetaOption>> optionValue = new OptionValue<ArrayList<TempAttachMetaOption>>();
        optionValue.setValue(attachList);
        SerDeserTest.options.put(OptionKey.TEMPALTE_ATTACH_META, optionValue);
    }

    public void test_for_yaolei() {
        // ???toJSONString()
        String jsonString = JSON.toJSONString(SerDeserTest.options);
        System.out.println(jsonString);
        {
            // ????parse()
            HashMap<OptionKey, OptionValue<?>> deserOptions = ((HashMap<OptionKey, OptionValue<?>>) (JSON.parseObject(jsonString, new com.alibaba.fastjson.TypeReference<HashMap<OptionKey, OptionValue<?>>>() {})));
            System.out.println(deserOptions.get(OptionKey.TEMPALTE_ATTACH_META));
        }
        // ???toJSONString(,)
        jsonString = JSON.toJSONString(SerDeserTest.options, WriteClassName);
        System.out.println(jsonString);
        // ????parse()
        HashMap<OptionKey, OptionValue<?>> deserOptions = ((HashMap<OptionKey, OptionValue<?>>) (JSON.parse(jsonString)));
        System.out.println(deserOptions.get(OptionKey.TEMPALTE_ATTACH_META));
    }
}

