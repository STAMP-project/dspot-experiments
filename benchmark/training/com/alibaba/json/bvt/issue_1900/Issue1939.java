package com.alibaba.json.bvt.issue_1900;


import com.alibaba.fastjson.JSON;
import java.io.Serializable;
import java.io.StringReader;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import junit.framework.TestCase;


public class Issue1939 extends TestCase {
    @XmlRootElement(name = "Container")
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = { "any" })
    public static class Container implements Serializable {
        @XmlAnyElement(lax = true)
        public List<Object> any;
    }

    private static final String MESSAGE = "<Container>" + ("<WeightMajor measurementSystem=\"English\" unit=\"lbs\">0</WeightMajor>" + "</Container>");

    public void test_for_issue() throws Exception {
        JAXBContext context = JAXBContext.newInstance(Issue1939.Container.class, Issue1939.class);
        Issue1939.Container con = ((Issue1939.Container) (context.createUnmarshaller().unmarshal(new StringReader(Issue1939.MESSAGE))));
        TestCase.assertEquals("{\"any\":[\"<?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\"?><WeightMajor measurementSystem=\\\"English\\\" unit=\\\"lbs\\\">0</WeightMajor>\"]}", JSON.toJSONString(con));
    }

    public void test_for_issue_1() throws Exception {
        JAXBContext context = JAXBContext.newInstance(Issue1939.Container.class, Issue1939.class);
        Issue1939.Container con = ((Issue1939.Container) (context.createUnmarshaller().unmarshal(new StringReader(Issue1939.MESSAGE))));
        TestCase.assertEquals("{\"any\":[\"<?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\"?><WeightMajor measurementSystem=\\\"English\\\" unit=\\\"lbs\\\">0</WeightMajor>\"]}", JSON.toJSON(con).toString());
    }
}

