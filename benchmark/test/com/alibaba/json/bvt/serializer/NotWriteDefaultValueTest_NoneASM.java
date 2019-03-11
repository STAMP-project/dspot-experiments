package com.alibaba.json.bvt.serializer;


import SerializerFeature.NotWriteDefaultValue;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class NotWriteDefaultValueTest_NoneASM extends TestCase {
    public void test_for_byte() throws Exception {
        NotWriteDefaultValueTest_NoneASM.VO_Byte vo = new NotWriteDefaultValueTest_NoneASM.VO_Byte();
        String text = JSON.toJSONString(vo, NotWriteDefaultValue);
        Assert.assertEquals("{}", text);
    }

    public void test_for_short() throws Exception {
        NotWriteDefaultValueTest_NoneASM.VO_Short vo = new NotWriteDefaultValueTest_NoneASM.VO_Short();
        String text = JSON.toJSONString(vo, NotWriteDefaultValue);
        Assert.assertEquals("{}", text);
    }

    public void test_for_int() throws Exception {
        NotWriteDefaultValueTest_NoneASM.VO_Int vo = new NotWriteDefaultValueTest_NoneASM.VO_Int();
        String text = JSON.toJSONString(vo, NotWriteDefaultValue);
        Assert.assertEquals("{}", text);
    }

    public void test_for_long() throws Exception {
        NotWriteDefaultValueTest_NoneASM.VO_Long vo = new NotWriteDefaultValueTest_NoneASM.VO_Long();
        String text = JSON.toJSONString(vo, NotWriteDefaultValue);
        Assert.assertEquals("{}", text);
    }

    public void test_for_float() throws Exception {
        NotWriteDefaultValueTest_NoneASM.VO_Float vo = new NotWriteDefaultValueTest_NoneASM.VO_Float();
        String text = JSON.toJSONString(vo, NotWriteDefaultValue);
        Assert.assertEquals("{}", text);
    }

    public void test_for_double() throws Exception {
        NotWriteDefaultValueTest_NoneASM.VO_Double vo = new NotWriteDefaultValueTest_NoneASM.VO_Double();
        String text = JSON.toJSONString(vo, NotWriteDefaultValue);
        Assert.assertEquals("{}", text);
    }

    public void test_for_boolean() throws Exception {
        NotWriteDefaultValueTest_NoneASM.VO_Boolean vo = new NotWriteDefaultValueTest_NoneASM.VO_Boolean();
        vo.f1 = true;
        String text = JSON.toJSONString(vo, NotWriteDefaultValue);
        Assert.assertEquals("{\"f1\":true}", text);
    }

    private static class VO_Byte {
        private byte f0;

        private byte f1;

        public byte getF0() {
            return f0;
        }

        public void setF0(byte f0) {
            this.f0 = f0;
        }

        public byte getF1() {
            return f1;
        }

        public void setF1(byte f1) {
            this.f1 = f1;
        }
    }

    private static class VO_Short {
        private short f0;

        private short f1;

        public short getF0() {
            return f0;
        }

        public void setF0(short f0) {
            this.f0 = f0;
        }

        public short getF1() {
            return f1;
        }

        public void setF1(short f1) {
            this.f1 = f1;
        }
    }

    private static class VO_Int {
        private int f0;

        private int f1;

        public int getF0() {
            return f0;
        }

        public void setF0(int f0) {
            this.f0 = f0;
        }

        public int getF1() {
            return f1;
        }

        public void setF1(int f1) {
            this.f1 = f1;
        }
    }

    private static class VO_Long {
        private long f0;

        private long f1;

        public long getF0() {
            return f0;
        }

        public void setF0(long f0) {
            this.f0 = f0;
        }

        public long getF1() {
            return f1;
        }

        public void setF1(long f1) {
            this.f1 = f1;
        }
    }

    private static class VO_Float {
        private float f0;

        private float f1;

        public float getF0() {
            return f0;
        }

        public void setF0(float f0) {
            this.f0 = f0;
        }

        public float getF1() {
            return f1;
        }

        public void setF1(float f1) {
            this.f1 = f1;
        }
    }

    private static class VO_Double {
        private double f0;

        private double f1;

        public double getF0() {
            return f0;
        }

        public void setF0(double f0) {
            this.f0 = f0;
        }

        public double getF1() {
            return f1;
        }

        public void setF1(double f1) {
            this.f1 = f1;
        }
    }

    private static class VO_Boolean {
        private boolean f0;

        private boolean f1;

        public boolean isF0() {
            return f0;
        }

        public void setF0(boolean f0) {
            this.f0 = f0;
        }

        public boolean isF1() {
            return f1;
        }

        public void setF1(boolean f1) {
            this.f1 = f1;
        }
    }
}

