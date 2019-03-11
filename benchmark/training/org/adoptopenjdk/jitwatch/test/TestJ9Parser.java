/**
 * Copyright (c) 2017 Chris Newland.
 * Licensed under https://github.com/AdoptOpenJDK/jitwatch/blob/master/LICENSE-BSD
 * Instructions: https://github.com/AdoptOpenJDK/jitwatch/wiki
 */
package org.adoptopenjdk.jitwatch.test;


import J9Line.TEMPERATURE_COLD;
import J9Line.TEMPERATURE_PROFILED_VERY_HOT;
import org.adoptopenjdk.jitwatch.model.MemberSignatureParts;
import org.adoptopenjdk.jitwatch.parser.j9.J9Line;
import org.adoptopenjdk.jitwatch.parser.j9.J9Util;
import org.junit.Assert;
import org.junit.Test;


public class TestJ9Parser {
    @Test
    public void testParseLine() throws Exception {
        String line = "+ (cold) java/lang/Double.longBitsToDouble(J)D @ 00007F0AAA60003C-00007F0AAA60005E OrdinaryMethod Q_SZ=0 Q_SZI=0 QW=1 j9m=0000000001E31FE0 bcsz=3 JNI compThread=0 CpuLoad=8%(4%avg) JvmCpu=0%";
        J9Line j9Line = J9Util.parseLine(line);
        Assert.assertEquals(TEMPERATURE_COLD, j9Line.getTemperature());
        Assert.assertEquals("java/lang/Double.longBitsToDouble(J)D", j9Line.getSignature());
        Assert.assertEquals("java/lang/Double longBitsToDouble (J)D", J9Util.convertJ9SigToLogCompilationSignature(j9Line.getSignature()));
        MemberSignatureParts msp = j9Line.getMemberSignatureParts();
        Assert.assertEquals("java.lang.Double", msp.getFullyQualifiedClassName());
        Assert.assertEquals("longBitsToDouble", msp.getMemberName());
        Assert.assertEquals("double", msp.getReturnType());
        Assert.assertEquals(1, msp.getParamTypes().size());
        Assert.assertEquals("long", msp.getParamTypes().get(0));
        Assert.assertEquals("00007F0AAA60003C", j9Line.getRangeStart());
        Assert.assertEquals("00007F0AAA60005E", j9Line.getRangeEnd());
        Assert.assertEquals(34, j9Line.getNativeSize());
        Assert.assertTrue(j9Line.hasFeature("OrdinaryMethod"));
        Assert.assertEquals(3, j9Line.getBytecodeSize());
    }

    @Test
    public void testParseLineWiothObjectReturn() throws Exception {
        String line = "+ (cold) java/lang/System.getEncoding(I)Ljava/lang/String; @ 00007F0AAA60007C-00007F0AAA6001C9 OrdinaryMethod Q_SZ=0 Q_SZI=0 QW=1 j9m=0000000001E1F440 bcsz=3 JNI compThread=0 CpuLoad=8%(4%avg) JvmCpu=0%";
        J9Line j9Line = J9Util.parseLine(line);
        Assert.assertEquals(TEMPERATURE_COLD, j9Line.getTemperature());
        Assert.assertEquals("java/lang/System.getEncoding(I)Ljava/lang/String;", j9Line.getSignature());
        Assert.assertEquals("java/lang/System getEncoding (I)Ljava/lang/String;", J9Util.convertJ9SigToLogCompilationSignature(j9Line.getSignature()));
        MemberSignatureParts msp = j9Line.getMemberSignatureParts();
        Assert.assertEquals("java.lang.System", msp.getFullyQualifiedClassName());
        Assert.assertEquals("getEncoding", msp.getMemberName());
        Assert.assertEquals("java.lang.String", msp.getReturnType());
        Assert.assertEquals(1, msp.getParamTypes().size());
        Assert.assertEquals("int", msp.getParamTypes().get(0));
        Assert.assertEquals("00007F0AAA60007C", j9Line.getRangeStart());
        Assert.assertEquals("00007F0AAA6001C9", j9Line.getRangeEnd());
        Assert.assertEquals(333, j9Line.getNativeSize());
        Assert.assertTrue(j9Line.hasFeature("OrdinaryMethod"));
        Assert.assertEquals(3, j9Line.getBytecodeSize());
    }

    @Test
    public void testParseLineWithComplexTemperature() throws Exception {
        String line = "+ (profiled very-hot) java/util/ComparableTimSort.mergeLo(IIII)V @ 00007F0AAA62BBA0-00007F0AAA633020 OrdinaryMethod 16.40% T Q_SZ=0 Q_SZI=0 QW=100 j9m=000000000204A130 bcsz=656 compThread=0 CpuLoad=100%(50%avg) JvmCpu=100%";
        J9Line j9Line = J9Util.parseLine(line);
        Assert.assertEquals(TEMPERATURE_PROFILED_VERY_HOT, j9Line.getTemperature());
        Assert.assertEquals("java/util/ComparableTimSort.mergeLo(IIII)V", j9Line.getSignature());
        Assert.assertEquals("java/util/ComparableTimSort mergeLo (IIII)V", J9Util.convertJ9SigToLogCompilationSignature(j9Line.getSignature()));
        MemberSignatureParts msp = j9Line.getMemberSignatureParts();
        Assert.assertEquals("java.util.ComparableTimSort", msp.getFullyQualifiedClassName());
        Assert.assertEquals("mergeLo", msp.getMemberName());
        Assert.assertEquals("void", msp.getReturnType());
        Assert.assertEquals(4, msp.getParamTypes().size());
        Assert.assertEquals("int", msp.getParamTypes().get(0));
        Assert.assertEquals("int", msp.getParamTypes().get(1));
        Assert.assertEquals("int", msp.getParamTypes().get(2));
        Assert.assertEquals("int", msp.getParamTypes().get(3));
        Assert.assertEquals("00007F0AAA62BBA0", j9Line.getRangeStart());
        Assert.assertEquals("00007F0AAA633020", j9Line.getRangeEnd());
        Assert.assertTrue(j9Line.hasFeature("OrdinaryMethod"));
        Assert.assertEquals(656, j9Line.getBytecodeSize());
    }
}

