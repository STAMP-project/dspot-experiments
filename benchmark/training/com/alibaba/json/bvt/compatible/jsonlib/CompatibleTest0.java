package com.alibaba.json.bvt.compatible.jsonlib;


import com.alibaba.fastjson.serializer.JSONLibDataFormatSerializer;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;


public class CompatibleTest0 extends TestCase {
    public void test_0() throws Exception {
        Map<String, Object> obj = new HashMap<String, Object>();
        CompatibleTest0.assertEquals(CompatibleTest0.toCompatibleJSONString(obj), CompatibleTest0.toJSONLibString(obj));
    }

    public void test_1() throws Exception {
        CompatibleTest0.VO vo = new CompatibleTest0.VO();
        CompatibleTest0.assertEquals(CompatibleTest0.toCompatibleJSONString(vo), CompatibleTest0.toJSONLibString(vo));
    }

    public void test_2() throws Exception {
        CompatibleTest0.V1 vo = new CompatibleTest0.V1();
        CompatibleTest0.assertEquals(CompatibleTest0.toCompatibleJSONString(vo), CompatibleTest0.toJSONLibString(vo));
    }

    // {"media":{"size":58982400,"format":"video/mpg4","uri":"http://javaone.com/keynote.mpg","title":"Javaone Keynote","width":640,"height":480,"duration":18000000,"bitrate":262144,"persons":["Bill Gates","Steve Jobs"],"player":"JAVA"}{"images":[{"size":"LARGE","uri":"http://javaone.com/keynote_large.jpg","title":"Javaone Keynote","width":1024,"height":768},{"size":"SMALL","uri":"http://javaone.com/keynote_small.jpg","title":"Javaone Keynote","width":320,"height":240}]}
    public void test_3() throws Exception {
        CompatibleTest0.V1 vo = new CompatibleTest0.V1();
        vo.setDate(new Date());
        CompatibleTest0.assertEquals(CompatibleTest0.toCompatibleJSONString(vo), CompatibleTest0.toJSONLibString(vo));
    }

    public void test_4() throws Exception {
        CompatibleTest0.V1 vo = new CompatibleTest0.V1();
        vo.setF2('?');
        CompatibleTest0.assertEquals(CompatibleTest0.toCompatibleJSONString(vo), CompatibleTest0.toJSONLibString(vo));
    }

    public void test_5() throws Exception {
        CompatibleTest0.V2 vo = new CompatibleTest0.V2();
        vo.setF1(0.2F);
        vo.setF2(33.3);
        CompatibleTest0.assertEquals(CompatibleTest0.toCompatibleJSONString(vo), CompatibleTest0.toJSONLibString(vo));
    }

    public void test_6() throws Exception {
        CompatibleTest0.V2 vo = new CompatibleTest0.V2();
        vo.setF1(0.1F);
        vo.setF2(33.3);
        CompatibleTest0.assertEquals(CompatibleTest0.toCompatibleJSONString(vo), CompatibleTest0.toJSONLibString(vo));
    }

    public void test_7() throws Exception {
        CompatibleTest0.V2 vo = new CompatibleTest0.V2();
        vo.setF2(0.1);
        vo.setF1(33.3F);
        CompatibleTest0.assertEquals(CompatibleTest0.toCompatibleJSONString(vo), CompatibleTest0.toJSONLibString(vo));
    }

    public void test_8() throws Exception {
        CompatibleTest0.V3 vo = new CompatibleTest0.V3();
        CompatibleTest0.assertEquals(CompatibleTest0.toCompatibleJSONString(vo), CompatibleTest0.toJSONLibString(vo));
    }

    public void test_9() throws Exception {
        CompatibleTest0.V4 vo = new CompatibleTest0.V4();
        CompatibleTest0.assertEquals(CompatibleTest0.toCompatibleJSONString(vo), CompatibleTest0.toJSONLibString(vo));
    }

    public void test_10() throws Exception {
        Object vo = null;
        CompatibleTest0.assertEquals(CompatibleTest0.toCompatibleJSONString(vo), CompatibleTest0.toJSONLibString(vo));
    }

    public void test_11() throws Exception {
        Object vo = new HashMap();
        CompatibleTest0.assertEquals(CompatibleTest0.toCompatibleJSONString(vo), CompatibleTest0.toJSONLibString(vo));
    }

    private static final SerializeConfig mapping;

    static {
        mapping = new SerializeConfig();
        CompatibleTest0.mapping.put(Date.class, new JSONLibDataFormatSerializer());// ???json-lib?????????

    }

    private static final SerializerFeature[] features = new SerializerFeature[]{ SerializerFeature.WriteMapNullValue// ??????
    , SerializerFeature.WriteNullListAsEmpty// list?????null????[]????null
    , SerializerFeature.WriteNullNumberAsZero// ???????null????0????null
    , SerializerFeature.WriteNullBooleanAsFalse// Boolean?????null????false????null
    , SerializerFeature.WriteNullStringAsEmpty// ?????????null????""????null
     };

    public static class V4 {
        private Map<String, Object> items;

        public Map<String, Object> getItems() {
            return items;
        }

        public void setItems(Map<String, Object> items) {
            this.items = items;
        }
    }

    public static class V3 {
        private List<String> items;

        public List<String> getItems() {
            return items;
        }

        public void setItems(List<String> items) {
            this.items = items;
        }
    }

    public static class V2 {
        private float f1;

        private double f2;

        private Float f3;

        private Double f4;

        public float getF1() {
            return f1;
        }

        public void setF1(float f1) {
            this.f1 = f1;
        }

        public double getF2() {
            return f2;
        }

        public void setF2(double f2) {
            this.f2 = f2;
        }

        public Float getF3() {
            return f3;
        }

        public void setF3(Float f3) {
            this.f3 = f3;
        }

        public Double getF4() {
            return f4;
        }

        public void setF4(Double f4) {
            this.f4 = f4;
        }
    }

    public static class V1 {
        private Boolean f1;

        private Character f2;

        private String f3;

        private Date date;

        private boolean f4;

        private char f5;

        public Boolean getF1() {
            return f1;
        }

        public void setF1(Boolean f1) {
            this.f1 = f1;
        }

        public Character getF2() {
            return f2;
        }

        public void setF2(Character f2) {
            this.f2 = f2;
        }

        public String getF3() {
            return f3;
        }

        public void setF3(String f3) {
            this.f3 = f3;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public boolean isF4() {
            return f4;
        }

        public void setF4(boolean f4) {
            this.f4 = f4;
        }

        public char getF5() {
            return f5;
        }

        public void setF5(char f5) {
            this.f5 = f5;
        }
    }

    public static class VO {
        private int id;

        private String name;

        private BigDecimal salary;

        private List<String> items;

        private Byte f1;

        private Short f2;

        private Integer f3;

        private Long f4;

        private BigInteger f5;

        private BigDecimal f6;

        private byte f7;

        private short f8;

        private int f9;

        private long f10;

        public Byte getF1() {
            return f1;
        }

        public void setF1(Byte f1) {
            this.f1 = f1;
        }

        public Short getF2() {
            return f2;
        }

        public void setF2(Short f2) {
            this.f2 = f2;
        }

        public Integer getF3() {
            return f3;
        }

        public void setF3(Integer f3) {
            this.f3 = f3;
        }

        public Long getF4() {
            return f4;
        }

        public void setF4(Long f4) {
            this.f4 = f4;
        }

        public BigInteger getF5() {
            return f5;
        }

        public void setF5(BigInteger f5) {
            this.f5 = f5;
        }

        public BigDecimal getF6() {
            return f6;
        }

        public void setF6(BigDecimal f6) {
            this.f6 = f6;
        }

        public byte getF7() {
            return f7;
        }

        public void setF7(byte f7) {
            this.f7 = f7;
        }

        public short getF8() {
            return f8;
        }

        public void setF8(short f8) {
            this.f8 = f8;
        }

        public int getF9() {
            return f9;
        }

        public void setF9(int f9) {
            this.f9 = f9;
        }

        public long getF10() {
            return f10;
        }

        public void setF10(long f10) {
            this.f10 = f10;
        }

        public BigDecimal getSalary() {
            return salary;
        }

        public void setSalary(BigDecimal salary) {
            this.salary = salary;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<String> getItems() {
            return items;
        }

        public void setItems(List<String> items) {
            this.items = items;
        }
    }
}

