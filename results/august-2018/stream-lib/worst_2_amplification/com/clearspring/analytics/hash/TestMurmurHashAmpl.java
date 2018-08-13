package com.clearspring.analytics.hash;


import org.junit.Assert;
import org.junit.Test;


public class TestMurmurHashAmpl {
    @Test(timeout = 10000)
    public void testHashByteArrayOverloadlitString11717() throws Exception {
        String input = "hashths";
        Assert.assertEquals("hashths", input);
        byte[] inputBytes = input.getBytes();
        int hashOfString = MurmurHash.hash(input);
        Assert.assertEquals(-1941958753, ((int) (hashOfString)));
        int o_testHashByteArrayOverloadlitString11717__6 = MurmurHash.hash(inputBytes);
        Assert.assertEquals(-1941958753, ((int) (o_testHashByteArrayOverloadlitString11717__6)));
        Object bytesAsObject = inputBytes;
        int o_testHashByteArrayOverloadlitString11717__8 = MurmurHash.hash(bytesAsObject);
        Assert.assertEquals(-1941958753, ((int) (o_testHashByteArrayOverloadlitString11717__8)));
        Assert.assertEquals("hashths", input);
        Assert.assertEquals(-1941958753, ((int) (hashOfString)));
        Assert.assertEquals(-1941958753, ((int) (o_testHashByteArrayOverloadlitString11717__6)));
    }

    @Test(timeout = 10000)
    public void testHashByteArrayOverloadlitString11716() throws Exception {
        String input = "hash3this";
        Assert.assertEquals("hash3this", input);
        byte[] inputBytes = input.getBytes();
        int hashOfString = MurmurHash.hash(input);
        Assert.assertEquals(1719041653, ((int) (hashOfString)));
        int o_testHashByteArrayOverloadlitString11716__6 = MurmurHash.hash(inputBytes);
        Assert.assertEquals(1719041653, ((int) (o_testHashByteArrayOverloadlitString11716__6)));
        Object bytesAsObject = inputBytes;
        int o_testHashByteArrayOverloadlitString11716__8 = MurmurHash.hash(bytesAsObject);
        Assert.assertEquals(1719041653, ((int) (o_testHashByteArrayOverloadlitString11716__8)));
        Assert.assertEquals("hash3this", input);
        Assert.assertEquals(1719041653, ((int) (hashOfString)));
        Assert.assertEquals(1719041653, ((int) (o_testHashByteArrayOverloadlitString11716__6)));
    }

    @Test(timeout = 10000)
    public void testHashByteArrayOverloadnull11738() throws Exception {
        String input = "hashthis";
        Assert.assertEquals("hashthis", input);
        byte[] inputBytes = input.getBytes();
        int hashOfString = MurmurHash.hash(input);
        Assert.assertEquals(-1974946086, ((int) (hashOfString)));
        int o_testHashByteArrayOverloadnull11738__6 = MurmurHash.hash(inputBytes);
        Assert.assertEquals(-1974946086, ((int) (o_testHashByteArrayOverloadnull11738__6)));
        Object bytesAsObject = null;
        int o_testHashByteArrayOverloadnull11738__8 = MurmurHash.hash(bytesAsObject);
        Assert.assertEquals(0, ((int) (o_testHashByteArrayOverloadnull11738__8)));
        Assert.assertEquals("hashthis", input);
        Assert.assertEquals(-1974946086, ((int) (hashOfString)));
        Assert.assertEquals(-1974946086, ((int) (o_testHashByteArrayOverloadnull11738__6)));
    }

    @Test(timeout = 10000)
    public void testHashByteArrayOverloadnull11744_failAssert22litString12758() throws Exception {
        try {
            String input = "hashths";
            Assert.assertEquals("hashths", input);
            byte[] inputBytes = input.getBytes();
            int hashOfString = MurmurHash.hash(input);
            int o_testHashByteArrayOverloadnull11744_failAssert22litString12758__8 = MurmurHash.hash(inputBytes);
            Assert.assertEquals(-1941958753, ((int) (o_testHashByteArrayOverloadnull11744_failAssert22litString12758__8)));
            Object bytesAsObject = inputBytes;
            MurmurHash.hash(null);
            org.junit.Assert.fail("testHashByteArrayOverloadnull11744 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testHashByteArrayOverload_add11725litString11889() throws Exception {
        String input = "hashths";
        Assert.assertEquals("hashths", input);
        byte[] inputBytes = input.getBytes();
        int hashOfString = MurmurHash.hash(input);
        int o_testHashByteArrayOverload_add11725__6 = MurmurHash.hash(inputBytes);
        Object bytesAsObject = inputBytes;
        int o_testHashByteArrayOverload_add11725__8 = MurmurHash.hash(bytesAsObject);
        int o_testHashByteArrayOverload_add11725__9 = MurmurHash.hash(bytesAsObject);
        Assert.assertEquals("hashths", input);
    }

    @Test(timeout = 10000)
    public void testHashByteArrayOverloadnull11737_failAssert21litString12738() throws Exception {
        try {
            String input = "\n";
            Assert.assertEquals("\n", input);
            byte[] inputBytes = input.getBytes();
            int hashOfString = MurmurHash.hash(input);
            MurmurHash.hash(null);
            Object bytesAsObject = inputBytes;
            MurmurHash.hash(bytesAsObject);
            org.junit.Assert.fail("testHashByteArrayOverloadnull11737 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testHashByteArrayOverload_add11724null12357() throws Exception {
        String input = "hashthis";
        Assert.assertEquals("hashthis", input);
        byte[] inputBytes = input.getBytes();
        int hashOfString = MurmurHash.hash(input);
        int o_testHashByteArrayOverload_add11724__6 = MurmurHash.hash(inputBytes);
        int o_testHashByteArrayOverload_add11724__7 = MurmurHash.hash(inputBytes);
        Object bytesAsObject = null;
        int o_testHashByteArrayOverload_add11724__9 = MurmurHash.hash(bytesAsObject);
        Assert.assertEquals("hashthis", input);
    }

    @Test(timeout = 10000)
    public void testHashByteArrayOverloadnull11744_failAssert22litString12762() throws Exception {
        try {
            String input = ":";
            Assert.assertEquals(":", input);
            byte[] inputBytes = input.getBytes();
            int hashOfString = MurmurHash.hash(input);
            int o_testHashByteArrayOverloadnull11744_failAssert22litString12762__8 = MurmurHash.hash(inputBytes);
            Assert.assertEquals(-100914771, ((int) (o_testHashByteArrayOverloadnull11744_failAssert22litString12762__8)));
            Object bytesAsObject = inputBytes;
            MurmurHash.hash(null);
            org.junit.Assert.fail("testHashByteArrayOverloadnull11744 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testHashByteArrayOverload_add11725_add12181litString17331() throws Exception {
        String input = "hashths";
        Assert.assertEquals("hashths", input);
        byte[] inputBytes = input.getBytes();
        int hashOfString = MurmurHash.hash(input);
        int o_testHashByteArrayOverload_add11725__6 = MurmurHash.hash(inputBytes);
        Object bytesAsObject = inputBytes;
        int o_testHashByteArrayOverload_add11725_add12181__10 = MurmurHash.hash(bytesAsObject);
        int o_testHashByteArrayOverload_add11725__8 = MurmurHash.hash(bytesAsObject);
        int o_testHashByteArrayOverload_add11725__9 = MurmurHash.hash(bytesAsObject);
        Assert.assertEquals("hashths", input);
    }

    @Test(timeout = 10000)
    public void testHashByteArrayOverload_add11725_add12181null17452() throws Exception {
        String input = "hashthis";
        Assert.assertEquals("hashthis", input);
        byte[] inputBytes = input.getBytes();
        int hashOfString = MurmurHash.hash(input);
        int o_testHashByteArrayOverload_add11725__6 = MurmurHash.hash(inputBytes);
        Object bytesAsObject = null;
        int o_testHashByteArrayOverload_add11725_add12181__10 = MurmurHash.hash(bytesAsObject);
        int o_testHashByteArrayOverload_add11725__8 = MurmurHash.hash(bytesAsObject);
        int o_testHashByteArrayOverload_add11725__9 = MurmurHash.hash(bytesAsObject);
        Assert.assertEquals("hashthis", input);
    }

    @Test(timeout = 10000)
    public void testHashByteArrayOverload_add11725_add12176litString16753() throws Exception {
        String input = ":";
        Assert.assertEquals(":", input);
        byte[] inputBytes = input.getBytes();
        int hashOfString = MurmurHash.hash(input);
        int o_testHashByteArrayOverload_add11725_add12176__6 = MurmurHash.hash(inputBytes);
        int o_testHashByteArrayOverload_add11725__6 = MurmurHash.hash(inputBytes);
        Object bytesAsObject = inputBytes;
        int o_testHashByteArrayOverload_add11725__8 = MurmurHash.hash(bytesAsObject);
        int o_testHashByteArrayOverload_add11725__9 = MurmurHash.hash(bytesAsObject);
        Assert.assertEquals(":", input);
    }

    @Test(timeout = 10000)
    public void testHash64ByteArrayOverloadnull4222() throws Exception {
        String input = "hashthis";
        Assert.assertEquals("hashthis", input);
        byte[] inputBytes = input.getBytes();
        long hashOfString = MurmurHash.hash64(input);
        Assert.assertEquals(-8896273065425798843L, ((long) (hashOfString)));
        long o_testHash64ByteArrayOverloadnull4222__6 = MurmurHash.hash64(inputBytes);
        Assert.assertEquals(-8896273065425798843L, ((long) (o_testHash64ByteArrayOverloadnull4222__6)));
        Object bytesAsObject = inputBytes;
        long o_testHash64ByteArrayOverloadnull4222__8 = MurmurHash.hash64(null);
        Assert.assertEquals(0L, ((long) (o_testHash64ByteArrayOverloadnull4222__8)));
        Assert.assertEquals("hashthis", input);
        Assert.assertEquals(-8896273065425798843L, ((long) (hashOfString)));
        Assert.assertEquals(-8896273065425798843L, ((long) (o_testHash64ByteArrayOverloadnull4222__6)));
    }

    @Test(timeout = 10000)
    public void testHash64ByteArrayOverloadlitString4195() throws Exception {
        String input = "hashhis";
        Assert.assertEquals("hashhis", input);
        byte[] inputBytes = input.getBytes();
        long hashOfString = MurmurHash.hash64(input);
        Assert.assertEquals(-2651218987042332986L, ((long) (hashOfString)));
        long o_testHash64ByteArrayOverloadlitString4195__6 = MurmurHash.hash64(inputBytes);
        Assert.assertEquals(-2651218987042332986L, ((long) (o_testHash64ByteArrayOverloadlitString4195__6)));
        Object bytesAsObject = inputBytes;
        long o_testHash64ByteArrayOverloadlitString4195__8 = MurmurHash.hash64(bytesAsObject);
        Assert.assertEquals(-2651218987042332986L, ((long) (o_testHash64ByteArrayOverloadlitString4195__8)));
        Assert.assertEquals("hashhis", input);
        Assert.assertEquals(-2651218987042332986L, ((long) (hashOfString)));
        Assert.assertEquals(-2651218987042332986L, ((long) (o_testHash64ByteArrayOverloadlitString4195__6)));
    }

    @Test(timeout = 10000)
    public void testHash64ByteArrayOverloadlitString4192() throws Exception {
        String input = "MurmurHash.hash64(byte[]) did not match MurmurHash.hash64(String)";
        Assert.assertEquals("MurmurHash.hash64(byte[]) did not match MurmurHash.hash64(String)", input);
        byte[] inputBytes = input.getBytes();
        long hashOfString = MurmurHash.hash64(input);
        Assert.assertEquals(-2406613350493541354L, ((long) (hashOfString)));
        long o_testHash64ByteArrayOverloadlitString4192__6 = MurmurHash.hash64(inputBytes);
        Assert.assertEquals(-2406613350493541354L, ((long) (o_testHash64ByteArrayOverloadlitString4192__6)));
        Object bytesAsObject = inputBytes;
        long o_testHash64ByteArrayOverloadlitString4192__8 = MurmurHash.hash64(bytesAsObject);
        Assert.assertEquals(-2406613350493541354L, ((long) (o_testHash64ByteArrayOverloadlitString4192__8)));
        Assert.assertEquals("MurmurHash.hash64(byte[]) did not match MurmurHash.hash64(String)", input);
        Assert.assertEquals(-2406613350493541354L, ((long) (hashOfString)));
        Assert.assertEquals(-2406613350493541354L, ((long) (o_testHash64ByteArrayOverloadlitString4192__6)));
    }

    @Test(timeout = 10000)
    public void testHash64ByteArrayOverload_add4203null4990() throws Exception {
        String input = "hashthis";
        Assert.assertEquals("hashthis", input);
        byte[] inputBytes = null;
        long hashOfString = MurmurHash.hash64(input);
        long o_testHash64ByteArrayOverload_add4203__6 = MurmurHash.hash64(inputBytes);
        Object bytesAsObject = inputBytes;
        long o_testHash64ByteArrayOverload_add4203__8 = MurmurHash.hash64(bytesAsObject);
        long o_testHash64ByteArrayOverload_add4203__9 = MurmurHash.hash64(bytesAsObject);
        Assert.assertEquals("hashthis", input);
    }

    @Test(timeout = 10000)
    public void testHash64ByteArrayOverload_add4203litString4773() throws Exception {
        String input = "MurmurHash.hash64(byte[]) did not match MurmurHash.hash64(String)";
        Assert.assertEquals("MurmurHash.hash64(byte[]) did not match MurmurHash.hash64(String)", input);
        byte[] inputBytes = input.getBytes();
        long hashOfString = MurmurHash.hash64(input);
        long o_testHash64ByteArrayOverload_add4203__6 = MurmurHash.hash64(inputBytes);
        Object bytesAsObject = inputBytes;
        long o_testHash64ByteArrayOverload_add4203__8 = MurmurHash.hash64(bytesAsObject);
        long o_testHash64ByteArrayOverload_add4203__9 = MurmurHash.hash64(bytesAsObject);
        Assert.assertEquals("MurmurHash.hash64(byte[]) did not match MurmurHash.hash64(String)", input);
    }

    @Test(timeout = 10000)
    public void testHash64ByteArrayOverloadlitString4192_add5039() throws Exception {
        String input = "MurmurHash.hash64(byte[]) did not match MurmurHash.hash64(String)";
        Assert.assertEquals("MurmurHash.hash64(byte[]) did not match MurmurHash.hash64(String)", input);
        byte[] inputBytes = input.getBytes();
        long hashOfString = MurmurHash.hash64(input);
        long o_testHash64ByteArrayOverloadlitString4192_add5039__6 = MurmurHash.hash64(inputBytes);
        Assert.assertEquals(-2406613350493541354L, ((long) (o_testHash64ByteArrayOverloadlitString4192_add5039__6)));
        long o_testHash64ByteArrayOverloadlitString4192__6 = MurmurHash.hash64(inputBytes);
        Object bytesAsObject = inputBytes;
        long o_testHash64ByteArrayOverloadlitString4192__8 = MurmurHash.hash64(bytesAsObject);
        Assert.assertEquals("MurmurHash.hash64(byte[]) did not match MurmurHash.hash64(String)", input);
        Assert.assertEquals(-2406613350493541354L, ((long) (o_testHash64ByteArrayOverloadlitString4192_add5039__6)));
    }

    @Test(timeout = 10000)
    public void testHash64ByteArrayOverload_add4202litString5072() throws Exception {
        String input = "MurmurHash.hash64(String) returns wrong hash value";
        Assert.assertEquals("MurmurHash.hash64(String) returns wrong hash value", input);
        byte[] inputBytes = input.getBytes();
        long hashOfString = MurmurHash.hash64(input);
        long o_testHash64ByteArrayOverload_add4202__6 = MurmurHash.hash64(inputBytes);
        long o_testHash64ByteArrayOverload_add4202__7 = MurmurHash.hash64(inputBytes);
        Object bytesAsObject = inputBytes;
        long o_testHash64ByteArrayOverload_add4202__9 = MurmurHash.hash64(bytesAsObject);
        Assert.assertEquals("MurmurHash.hash64(String) returns wrong hash value", input);
    }

    @Test(timeout = 10000)
    public void testHash64ByteArrayOverload_add4203litString4780() throws Exception {
        String input = "hahthis";
        Assert.assertEquals("hahthis", input);
        byte[] inputBytes = input.getBytes();
        long hashOfString = MurmurHash.hash64(input);
        long o_testHash64ByteArrayOverload_add4203__6 = MurmurHash.hash64(inputBytes);
        Object bytesAsObject = inputBytes;
        long o_testHash64ByteArrayOverload_add4203__8 = MurmurHash.hash64(bytesAsObject);
        long o_testHash64ByteArrayOverload_add4203__9 = MurmurHash.hash64(bytesAsObject);
        Assert.assertEquals("hahthis", input);
    }

    @Test(timeout = 10000)
    public void testHash64ByteArrayOverload_add4202litString5086() throws Exception {
        String input = "hash%this";
        Assert.assertEquals("hash%this", input);
        byte[] inputBytes = input.getBytes();
        long hashOfString = MurmurHash.hash64(input);
        long o_testHash64ByteArrayOverload_add4202__6 = MurmurHash.hash64(inputBytes);
        long o_testHash64ByteArrayOverload_add4202__7 = MurmurHash.hash64(inputBytes);
        Object bytesAsObject = inputBytes;
        long o_testHash64ByteArrayOverload_add4202__9 = MurmurHash.hash64(bytesAsObject);
        Assert.assertEquals("hash%this", input);
    }

    @Test(timeout = 10000)
    public void testHash64ByteArrayOverload_add4202_add5212litString10529() throws Exception {
        String input = "hasthis";
        Assert.assertEquals("hasthis", input);
        byte[] inputBytes = input.getBytes();
        long hashOfString = MurmurHash.hash64(input);
        long o_testHash64ByteArrayOverload_add4202_add5212__6 = MurmurHash.hash64(inputBytes);
        long o_testHash64ByteArrayOverload_add4202__6 = MurmurHash.hash64(inputBytes);
        long o_testHash64ByteArrayOverload_add4202__7 = MurmurHash.hash64(inputBytes);
        Object bytesAsObject = inputBytes;
        long o_testHash64ByteArrayOverload_add4202__9 = MurmurHash.hash64(bytesAsObject);
        Assert.assertEquals("hasthis", input);
    }

    @Test(timeout = 10000)
    public void testHash64ByteArrayOverload_add4203_add4950litString10905() throws Exception {
        String input = "MurmurHash.hash64(byte[]) did not match MurmurHash.hash64(String)";
        Assert.assertEquals("MurmurHash.hash64(byte[]) did not match MurmurHash.hash64(String)", input);
        byte[] inputBytes = input.getBytes();
        long hashOfString = MurmurHash.hash64(input);
        long o_testHash64ByteArrayOverload_add4203_add4950__6 = MurmurHash.hash64(inputBytes);
        long o_testHash64ByteArrayOverload_add4203__6 = MurmurHash.hash64(inputBytes);
        Object bytesAsObject = inputBytes;
        long o_testHash64ByteArrayOverload_add4203__8 = MurmurHash.hash64(bytesAsObject);
        long o_testHash64ByteArrayOverload_add4203__9 = MurmurHash.hash64(bytesAsObject);
        Assert.assertEquals("MurmurHash.hash64(byte[]) did not match MurmurHash.hash64(String)", input);
    }

    @Test(timeout = 10000)
    public void testHash64ByteArrayOverload_add4202_add5210litString9943() throws Exception {
        String input = "MurmurHash.hash(byte[]) did not match MurmurHash.hash(String)";
        Assert.assertEquals("MurmurHash.hash(byte[]) did not match MurmurHash.hash(String)", input);
        byte[] inputBytes = input.getBytes();
        long o_testHash64ByteArrayOverload_add4202_add5210__4 = MurmurHash.hash64(input);
        long hashOfString = MurmurHash.hash64(input);
        long o_testHash64ByteArrayOverload_add4202__6 = MurmurHash.hash64(inputBytes);
        long o_testHash64ByteArrayOverload_add4202__7 = MurmurHash.hash64(inputBytes);
        Object bytesAsObject = inputBytes;
        long o_testHash64ByteArrayOverload_add4202__9 = MurmurHash.hash64(bytesAsObject);
        Assert.assertEquals("MurmurHash.hash(byte[]) did not match MurmurHash.hash(String)", input);
    }

    @Test(timeout = 10000)
    public void testHash64ByteArrayOverloadlitString4192_add5039_add8454() throws Exception {
        String input = "MurmurHash.hash64(byte[]) did not match MurmurHash.hash64(String)";
        Assert.assertEquals("MurmurHash.hash64(byte[]) did not match MurmurHash.hash64(String)", input);
        byte[] inputBytes = input.getBytes();
        long hashOfString = MurmurHash.hash64(input);
        long o_testHash64ByteArrayOverloadlitString4192_add5039_add8454__6 = MurmurHash.hash64(inputBytes);
        Assert.assertEquals(-2406613350493541354L, ((long) (o_testHash64ByteArrayOverloadlitString4192_add5039_add8454__6)));
        long o_testHash64ByteArrayOverloadlitString4192_add5039__6 = MurmurHash.hash64(inputBytes);
        long o_testHash64ByteArrayOverloadlitString4192__6 = MurmurHash.hash64(inputBytes);
        Object bytesAsObject = inputBytes;
        long o_testHash64ByteArrayOverloadlitString4192__8 = MurmurHash.hash64(bytesAsObject);
        Assert.assertEquals("MurmurHash.hash64(byte[]) did not match MurmurHash.hash64(String)", input);
        Assert.assertEquals(-2406613350493541354L, ((long) (o_testHash64ByteArrayOverloadlitString4192_add5039_add8454__6)));
    }

    @Test(timeout = 10000)
    public void testHash64ByteArrayOverloadlitString4192_add5039null8501() throws Exception {
        String input = "MurmurHash.hash64(byte[]) did not match MurmurHash.hash64(String)";
        Assert.assertEquals("MurmurHash.hash64(byte[]) did not match MurmurHash.hash64(String)", input);
        byte[] inputBytes = input.getBytes();
        long hashOfString = MurmurHash.hash64(input);
        long o_testHash64ByteArrayOverloadlitString4192_add5039__6 = MurmurHash.hash64(null);
        long o_testHash64ByteArrayOverloadlitString4192__6 = MurmurHash.hash64(inputBytes);
        Object bytesAsObject = inputBytes;
        long o_testHash64ByteArrayOverloadlitString4192__8 = MurmurHash.hash64(bytesAsObject);
        Assert.assertEquals("MurmurHash.hash64(byte[]) did not match MurmurHash.hash64(String)", input);
    }

    @Test(timeout = 10000)
    public void testHash64litString2119() throws Exception {
        final long actualHash = MurmurHash.hash64(":");
        Assert.assertEquals(-1293469037080459059L, ((long) (actualHash)));
        final long expectedHash = -8896273065425798843L;
        Assert.assertEquals(-8896273065425798843L, ((long) (expectedHash)));
        Assert.assertEquals(-1293469037080459059L, ((long) (actualHash)));
    }

    @Test(timeout = 10000)
    public void testHash64litString2112() throws Exception {
        final long actualHash = MurmurHash.hash64("MurmurHash.hash64(String) returns wrong hash value");
        Assert.assertEquals(-8313387402121601810L, ((long) (actualHash)));
        final long expectedHash = -8896273065425798843L;
        Assert.assertEquals(-8896273065425798843L, ((long) (expectedHash)));
        Assert.assertEquals(-8313387402121601810L, ((long) (actualHash)));
    }

    @Test(timeout = 10000)
    public void testHash64litString2115() throws Exception {
        final long actualHash = MurmurHash.hash64("hashhis");
        Assert.assertEquals(-2651218987042332986L, ((long) (actualHash)));
        final long expectedHash = -8896273065425798843L;
        Assert.assertEquals(-8896273065425798843L, ((long) (expectedHash)));
        Assert.assertEquals(-2651218987042332986L, ((long) (actualHash)));
    }

    @Test(timeout = 10000)
    public void testHash64null2129() throws Exception {
        final long actualHash = MurmurHash.hash64(null);
        Assert.assertEquals(0L, ((long) (actualHash)));
        final long expectedHash = -8896273065425798843L;
        Assert.assertEquals(-8896273065425798843L, ((long) (expectedHash)));
        Assert.assertEquals(0L, ((long) (actualHash)));
    }

    @Test(timeout = 10000)
    public void testHash64litString2112_add2538() throws Exception {
        long o_testHash64litString2112_add2538__1 = MurmurHash.hash64("MurmurHash.hash64(String) returns wrong hash value");
        Assert.assertEquals(-8313387402121601810L, ((long) (o_testHash64litString2112_add2538__1)));
        final long actualHash = MurmurHash.hash64("MurmurHash.hash64(String) returns wrong hash value");
        final long expectedHash = -8896273065425798843L;
        Assert.assertEquals(-8313387402121601810L, ((long) (o_testHash64litString2112_add2538__1)));
    }

    @Test(timeout = 10000)
    public void testHash64litString2115_add2595() throws Exception {
        long o_testHash64litString2115_add2595__1 = MurmurHash.hash64("hashhis");
        Assert.assertEquals(-2651218987042332986L, ((long) (o_testHash64litString2115_add2595__1)));
        final long actualHash = MurmurHash.hash64("hashhis");
        final long expectedHash = -8896273065425798843L;
        Assert.assertEquals(-2651218987042332986L, ((long) (o_testHash64litString2115_add2595__1)));
    }

    @Test(timeout = 10000)
    public void testHash64litString2114_add2379() throws Exception {
        long o_testHash64litString2114_add2379__1 = MurmurHash.hash64("hashtDhis");
        Assert.assertEquals(-8273957984789579749L, ((long) (o_testHash64litString2114_add2379__1)));
        final long actualHash = MurmurHash.hash64("hashtDhis");
        final long expectedHash = -8896273065425798843L;
        Assert.assertEquals(-8273957984789579749L, ((long) (o_testHash64litString2114_add2379__1)));
    }

    @Test(timeout = 10000)
    public void testHash64litString2114_add2379_add3657() throws Exception {
        long o_testHash64litString2114_add2379_add3657__1 = MurmurHash.hash64("hashtDhis");
        Assert.assertEquals(-8273957984789579749L, ((long) (o_testHash64litString2114_add2379_add3657__1)));
        long o_testHash64litString2114_add2379__1 = MurmurHash.hash64("hashtDhis");
        final long actualHash = MurmurHash.hash64("hashtDhis");
        final long expectedHash = -8896273065425798843L;
        Assert.assertEquals(-8273957984789579749L, ((long) (o_testHash64litString2114_add2379_add3657__1)));
    }

    @Test(timeout = 10000)
    public void testHash64litString2115_add2595_add3538() throws Exception {
        long o_testHash64litString2115_add2595_add3538__1 = MurmurHash.hash64("hashhis");
        Assert.assertEquals(-2651218987042332986L, ((long) (o_testHash64litString2115_add2595_add3538__1)));
        long o_testHash64litString2115_add2595__1 = MurmurHash.hash64("hashhis");
        final long actualHash = MurmurHash.hash64("hashhis");
        final long expectedHash = -8896273065425798843L;
        Assert.assertEquals(-2651218987042332986L, ((long) (o_testHash64litString2115_add2595_add3538__1)));
    }

    @Test(timeout = 10000)
    public void testHash64litString2112_add2538_add3369() throws Exception {
        long o_testHash64litString2112_add2538_add3369__1 = MurmurHash.hash64("MurmurHash.hash64(String) returns wrong hash value");
        Assert.assertEquals(-8313387402121601810L, ((long) (o_testHash64litString2112_add2538_add3369__1)));
        long o_testHash64litString2112_add2538__1 = MurmurHash.hash64("MurmurHash.hash64(String) returns wrong hash value");
        final long actualHash = MurmurHash.hash64("MurmurHash.hash64(String) returns wrong hash value");
        final long expectedHash = -8896273065425798843L;
        Assert.assertEquals(-8313387402121601810L, ((long) (o_testHash64litString2112_add2538_add3369__1)));
    }

    @Test(timeout = 10000)
    public void testHashlitString8() throws Exception {
        final long actualHash = MurmurHash.hash(":");
        Assert.assertEquals(-100914771L, ((long) (actualHash)));
        final long expectedHash = -1974946086L;
        Assert.assertEquals(-1974946086L, ((long) (expectedHash)));
        Assert.assertEquals(-100914771L, ((long) (actualHash)));
    }

    @Test(timeout = 10000)
    public void testHashlitString1() throws Exception {
        final long actualHash = MurmurHash.hash("MurmurHash.hash64(String) returns wrong hash value");
        Assert.assertEquals(-1053542750L, ((long) (actualHash)));
        final long expectedHash = -1974946086L;
        Assert.assertEquals(-1974946086L, ((long) (expectedHash)));
        Assert.assertEquals(-1053542750L, ((long) (actualHash)));
    }

    @Test(timeout = 10000)
    public void testHashlitString4() throws Exception {
        final long actualHash = MurmurHash.hash("hashhis");
        Assert.assertEquals(-590652159L, ((long) (actualHash)));
        final long expectedHash = -1974946086L;
        Assert.assertEquals(-1974946086L, ((long) (expectedHash)));
        Assert.assertEquals(-590652159L, ((long) (actualHash)));
    }

    @Test(timeout = 10000)
    public void testHashlitString4_add204() throws Exception {
        int o_testHashlitString4_add204__1 = MurmurHash.hash("hashhis");
        Assert.assertEquals(-590652159, ((int) (o_testHashlitString4_add204__1)));
        final long actualHash = MurmurHash.hash("hashhis");
        final long expectedHash = -1974946086L;
        Assert.assertEquals(-590652159, ((int) (o_testHashlitString4_add204__1)));
    }

    @Test(timeout = 10000)
    public void testHashlitString1_add284() throws Exception {
        int o_testHashlitString1_add284__1 = MurmurHash.hash("MurmurHash.hash64(String) returns wrong hash value");
        Assert.assertEquals(-1053542750, ((int) (o_testHashlitString1_add284__1)));
        final long actualHash = MurmurHash.hash("MurmurHash.hash64(String) returns wrong hash value");
        final long expectedHash = -1974946086L;
        Assert.assertEquals(-1053542750, ((int) (o_testHashlitString1_add284__1)));
    }

    @Test(timeout = 10000)
    public void testHashlitString7_add145() throws Exception {
        int o_testHashlitString7_add145__1 = MurmurHash.hash("\n");
        Assert.assertEquals(-330957644, ((int) (o_testHashlitString7_add145__1)));
        final long actualHash = MurmurHash.hash("\n");
        final long expectedHash = -1974946086L;
        Assert.assertEquals(-330957644, ((int) (o_testHashlitString7_add145__1)));
    }

    @Test(timeout = 10000)
    public void testHashlitString3_add134_add1215() throws Exception {
        int o_testHashlitString3_add134_add1215__1 = MurmurHash.hash("hash,this");
        Assert.assertEquals(1486645312, ((int) (o_testHashlitString3_add134_add1215__1)));
        int o_testHashlitString3_add134__1 = MurmurHash.hash("hash,this");
        final long actualHash = MurmurHash.hash("hash,this");
        final long expectedHash = -1974946086L;
        Assert.assertEquals(1486645312, ((int) (o_testHashlitString3_add134_add1215__1)));
    }

    @Test(timeout = 10000)
    public void testHashlitString4_add204_add1447() throws Exception {
        int o_testHashlitString4_add204_add1447__1 = MurmurHash.hash("hashhis");
        Assert.assertEquals(-590652159, ((int) (o_testHashlitString4_add204_add1447__1)));
        int o_testHashlitString4_add204__1 = MurmurHash.hash("hashhis");
        final long actualHash = MurmurHash.hash("hashhis");
        final long expectedHash = -1974946086L;
        Assert.assertEquals(-590652159, ((int) (o_testHashlitString4_add204_add1447__1)));
    }

    @Test(timeout = 10000)
    public void testHashlitString1_add284_add1255() throws Exception {
        int o_testHashlitString1_add284_add1255__1 = MurmurHash.hash("MurmurHash.hash64(String) returns wrong hash value");
        Assert.assertEquals(-1053542750, ((int) (o_testHashlitString1_add284_add1255__1)));
        int o_testHashlitString1_add284__1 = MurmurHash.hash("MurmurHash.hash64(String) returns wrong hash value");
        final long actualHash = MurmurHash.hash("MurmurHash.hash64(String) returns wrong hash value");
        final long expectedHash = -1974946086L;
        Assert.assertEquals(-1053542750, ((int) (o_testHashlitString1_add284_add1255__1)));
    }
}

