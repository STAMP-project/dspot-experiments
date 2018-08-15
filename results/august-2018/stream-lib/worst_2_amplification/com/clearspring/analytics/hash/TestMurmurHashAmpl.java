package com.clearspring.analytics.hash;


import org.junit.Assert;
import org.junit.Test;


public class TestMurmurHashAmpl {
    @Test(timeout = 10000)
    public void testHashByteArrayOverloadlitString6236() throws Exception {
        String input = "hashths";
        Assert.assertEquals("hashths", input);
        byte[] inputBytes = input.getBytes();
        int hashOfString = MurmurHash.hash(input);
        Assert.assertEquals(-1941958753, ((int) (hashOfString)));
        int o_testHashByteArrayOverloadlitString6236__6 = MurmurHash.hash(inputBytes);
        Assert.assertEquals(-1941958753, ((int) (o_testHashByteArrayOverloadlitString6236__6)));
        Object bytesAsObject = inputBytes;
        int o_testHashByteArrayOverloadlitString6236__8 = MurmurHash.hash(bytesAsObject);
        Assert.assertEquals(-1941958753, ((int) (o_testHashByteArrayOverloadlitString6236__8)));
        Assert.assertEquals("hashths", input);
        Assert.assertEquals(-1941958753, ((int) (hashOfString)));
        Assert.assertEquals(-1941958753, ((int) (o_testHashByteArrayOverloadlitString6236__6)));
    }

    @Test(timeout = 10000)
    public void testHashByteArrayOverloadlitString6239() throws Exception {
        String input = "\n";
        Assert.assertEquals("\n", input);
        byte[] inputBytes = input.getBytes();
        int hashOfString = MurmurHash.hash(input);
        Assert.assertEquals(-330957644, ((int) (hashOfString)));
        int o_testHashByteArrayOverloadlitString6239__6 = MurmurHash.hash(inputBytes);
        Assert.assertEquals(-330957644, ((int) (o_testHashByteArrayOverloadlitString6239__6)));
        Object bytesAsObject = inputBytes;
        int o_testHashByteArrayOverloadlitString6239__8 = MurmurHash.hash(bytesAsObject);
        Assert.assertEquals(-330957644, ((int) (o_testHashByteArrayOverloadlitString6239__8)));
        Assert.assertEquals("\n", input);
        Assert.assertEquals(-330957644, ((int) (hashOfString)));
        Assert.assertEquals(-330957644, ((int) (o_testHashByteArrayOverloadlitString6239__6)));
    }

    @Test(timeout = 10000)
    public void testHashByteArrayOverloadlitString6239_add6388() throws Exception {
        String input = "\n";
        Assert.assertEquals("\n", input);
        input.getBytes();
        byte[] inputBytes = input.getBytes();
        int hashOfString = MurmurHash.hash(input);
        int o_testHashByteArrayOverloadlitString6239__6 = MurmurHash.hash(inputBytes);
        Object bytesAsObject = inputBytes;
        int o_testHashByteArrayOverloadlitString6239__8 = MurmurHash.hash(bytesAsObject);
        Assert.assertEquals("\n", input);
    }

    @Test(timeout = 10000)
    public void testHashByteArrayOverloadlitString6236_add6447() throws Exception {
        String input = "hashths";
        Assert.assertEquals("hashths", input);
        byte[] inputBytes = input.getBytes();
        int hashOfString = MurmurHash.hash(input);
        int o_testHashByteArrayOverloadlitString6236_add6447__6 = MurmurHash.hash(inputBytes);
        Assert.assertEquals(-1941958753, ((int) (o_testHashByteArrayOverloadlitString6236_add6447__6)));
        int o_testHashByteArrayOverloadlitString6236__6 = MurmurHash.hash(inputBytes);
        Object bytesAsObject = inputBytes;
        int o_testHashByteArrayOverloadlitString6236__8 = MurmurHash.hash(bytesAsObject);
        Assert.assertEquals("hashths", input);
        Assert.assertEquals(-1941958753, ((int) (o_testHashByteArrayOverloadlitString6236_add6447__6)));
    }

    @Test(timeout = 10000)
    public void testHashByteArrayOverloadlitString6239_add6390() throws Exception {
        String input = "\n";
        Assert.assertEquals("\n", input);
        byte[] inputBytes = input.getBytes();
        int o_testHashByteArrayOverloadlitString6239_add6390__4 = MurmurHash.hash(input);
        Assert.assertEquals(-330957644, ((int) (o_testHashByteArrayOverloadlitString6239_add6390__4)));
        int hashOfString = MurmurHash.hash(input);
        int o_testHashByteArrayOverloadlitString6239__6 = MurmurHash.hash(inputBytes);
        Object bytesAsObject = inputBytes;
        int o_testHashByteArrayOverloadlitString6239__8 = MurmurHash.hash(bytesAsObject);
        Assert.assertEquals("\n", input);
        Assert.assertEquals(-330957644, ((int) (o_testHashByteArrayOverloadlitString6239_add6390__4)));
    }

    @Test(timeout = 10000)
    public void testHashByteArrayOverload_add6241litString6303_remove8408() throws Exception {
        String input = "hashtis";
        Assert.assertEquals("hashtis", input);
        byte[] inputBytes = input.getBytes();
        int hashOfString = MurmurHash.hash(input);
        int o_testHashByteArrayOverload_add6241__7 = MurmurHash.hash(inputBytes);
        Object bytesAsObject = inputBytes;
        int o_testHashByteArrayOverload_add6241__9 = MurmurHash.hash(bytesAsObject);
        Assert.assertEquals("hashtis", input);
    }

    @Test(timeout = 10000)
    public void testHashByteArrayOverload_add6244_add6360litString7576() throws Exception {
        String input = "MurmurHash.hash(byte[]) did not match MurmurHash.hash(String)";
        Assert.assertEquals("MurmurHash.hash(byte[]) did not match MurmurHash.hash(String)", input);
        byte[] inputBytes = input.getBytes();
        int hashOfString = MurmurHash.hash(input);
        int o_testHashByteArrayOverload_add6244_add6360__6 = MurmurHash.hash(inputBytes);
        int o_testHashByteArrayOverload_add6244__6 = MurmurHash.hash(inputBytes);
        Object bytesAsObject = inputBytes;
        int o_testHashByteArrayOverload_add6244__8 = MurmurHash.hash(bytesAsObject);
        int o_testHashByteArrayOverload_add6244__9 = MurmurHash.hash(bytesAsObject);
        Assert.assertEquals("MurmurHash.hash(byte[]) did not match MurmurHash.hash(String)", input);
    }

    @Test(timeout = 10000)
    public void testHashByteArrayOverloadlitString6239_add6388_remove7615() throws Exception {
        String input = "\n";
        Assert.assertEquals("\n", input);
        byte[] inputBytes = input.getBytes();
        int hashOfString = MurmurHash.hash(input);
        int o_testHashByteArrayOverloadlitString6239__6 = MurmurHash.hash(inputBytes);
        Object bytesAsObject = inputBytes;
        int o_testHashByteArrayOverloadlitString6239__8 = MurmurHash.hash(bytesAsObject);
        Assert.assertEquals("\n", input);
    }

    @Test(timeout = 10000)
    public void testHash64ByteArrayOverloadnull2993() throws Exception {
        String input = "hashthis";
        Assert.assertEquals("hashthis", input);
        byte[] inputBytes = input.getBytes();
        long hashOfString = MurmurHash.hash64(null);
        Assert.assertEquals(0L, ((long) (hashOfString)));
        long o_testHash64ByteArrayOverloadnull2993__6 = MurmurHash.hash64(inputBytes);
        Assert.assertEquals(-8896273065425798843L, ((long) (o_testHash64ByteArrayOverloadnull2993__6)));
        Object bytesAsObject = inputBytes;
        long o_testHash64ByteArrayOverloadnull2993__8 = MurmurHash.hash64(bytesAsObject);
        Assert.assertEquals(-8896273065425798843L, ((long) (o_testHash64ByteArrayOverloadnull2993__8)));
        Assert.assertEquals("hashthis", input);
        Assert.assertEquals(0L, ((long) (hashOfString)));
        Assert.assertEquals(-8896273065425798843L, ((long) (o_testHash64ByteArrayOverloadnull2993__6)));
    }

    @Test(timeout = 10000)
    public void testHash64ByteArrayOverloadlitString2982() throws Exception {
        String input = "hashth is";
        Assert.assertEquals("hashth is", input);
        byte[] inputBytes = input.getBytes();
        long hashOfString = MurmurHash.hash64(input);
        Assert.assertEquals(6125313423190383303L, ((long) (hashOfString)));
        long o_testHash64ByteArrayOverloadlitString2982__6 = MurmurHash.hash64(inputBytes);
        Assert.assertEquals(6125313423190383303L, ((long) (o_testHash64ByteArrayOverloadlitString2982__6)));
        Object bytesAsObject = inputBytes;
        long o_testHash64ByteArrayOverloadlitString2982__8 = MurmurHash.hash64(bytesAsObject);
        Assert.assertEquals(6125313423190383303L, ((long) (o_testHash64ByteArrayOverloadlitString2982__8)));
        Assert.assertEquals("hashth is", input);
        Assert.assertEquals(6125313423190383303L, ((long) (hashOfString)));
        Assert.assertEquals(6125313423190383303L, ((long) (o_testHash64ByteArrayOverloadlitString2982__6)));
    }

    @Test(timeout = 10000)
    public void testHash64ByteArrayOverloadlitString2983() throws Exception {
        String input = "hasthis";
        Assert.assertEquals("hasthis", input);
        byte[] inputBytes = input.getBytes();
        long hashOfString = MurmurHash.hash64(input);
        Assert.assertEquals(3602824184977593628L, ((long) (hashOfString)));
        long o_testHash64ByteArrayOverloadlitString2983__6 = MurmurHash.hash64(inputBytes);
        Assert.assertEquals(3602824184977593628L, ((long) (o_testHash64ByteArrayOverloadlitString2983__6)));
        Object bytesAsObject = inputBytes;
        long o_testHash64ByteArrayOverloadlitString2983__8 = MurmurHash.hash64(bytesAsObject);
        Assert.assertEquals(3602824184977593628L, ((long) (o_testHash64ByteArrayOverloadlitString2983__8)));
        Assert.assertEquals("hasthis", input);
        Assert.assertEquals(3602824184977593628L, ((long) (hashOfString)));
        Assert.assertEquals(3602824184977593628L, ((long) (o_testHash64ByteArrayOverloadlitString2983__6)));
    }

    @Test(timeout = 10000)
    public void testHash64ByteArrayOverloadlitString2986() throws Exception {
        String input = "\n";
        Assert.assertEquals("\n", input);
        byte[] inputBytes = input.getBytes();
        long hashOfString = MurmurHash.hash64(input);
        Assert.assertEquals(2044312949510316467L, ((long) (hashOfString)));
        long o_testHash64ByteArrayOverloadlitString2986__6 = MurmurHash.hash64(inputBytes);
        Assert.assertEquals(2044312949510316467L, ((long) (o_testHash64ByteArrayOverloadlitString2986__6)));
        Object bytesAsObject = inputBytes;
        long o_testHash64ByteArrayOverloadlitString2986__8 = MurmurHash.hash64(bytesAsObject);
        Assert.assertEquals(2044312949510316467L, ((long) (o_testHash64ByteArrayOverloadlitString2986__8)));
        Assert.assertEquals("\n", input);
        Assert.assertEquals(2044312949510316467L, ((long) (hashOfString)));
        Assert.assertEquals(2044312949510316467L, ((long) (o_testHash64ByteArrayOverloadlitString2986__6)));
    }

    @Test(timeout = 10000)
    public void testHash64ByteArrayOverload_add2989litString3155() throws Exception {
        String input = "hashhis";
        Assert.assertEquals("hashhis", input);
        byte[] inputBytes = input.getBytes();
        long o_testHash64ByteArrayOverload_add2989__4 = MurmurHash.hash64(input);
        long hashOfString = MurmurHash.hash64(input);
        long o_testHash64ByteArrayOverload_add2989__7 = MurmurHash.hash64(inputBytes);
        Object bytesAsObject = inputBytes;
        long o_testHash64ByteArrayOverload_add2989__9 = MurmurHash.hash64(bytesAsObject);
        Assert.assertEquals("hashhis", input);
    }

    @Test(timeout = 10000)
    public void testHash64ByteArrayOverloadlitString2983_add3073() throws Exception {
        String input = "hasthis";
        Assert.assertEquals("hasthis", input);
        byte[] inputBytes = input.getBytes();
        long o_testHash64ByteArrayOverloadlitString2983_add3073__4 = MurmurHash.hash64(input);
        Assert.assertEquals(3602824184977593628L, ((long) (o_testHash64ByteArrayOverloadlitString2983_add3073__4)));
        long hashOfString = MurmurHash.hash64(input);
        long o_testHash64ByteArrayOverloadlitString2983__6 = MurmurHash.hash64(inputBytes);
        Object bytesAsObject = inputBytes;
        long o_testHash64ByteArrayOverloadlitString2983__8 = MurmurHash.hash64(bytesAsObject);
        Assert.assertEquals("hasthis", input);
        Assert.assertEquals(3602824184977593628L, ((long) (o_testHash64ByteArrayOverloadlitString2983_add3073__4)));
    }

    @Test(timeout = 10000)
    public void testHash64ByteArrayOverloadnull2994litString3117() throws Exception {
        String input = "MurmurHash.hash64(byte[]) did not match MurmurHash.hash64(String)";
        Assert.assertEquals("MurmurHash.hash64(byte[]) did not match MurmurHash.hash64(String)", input);
        byte[] inputBytes = input.getBytes();
        long hashOfString = MurmurHash.hash64(input);
        long o_testHash64ByteArrayOverloadnull2994__6 = MurmurHash.hash64(null);
        Object bytesAsObject = inputBytes;
        long o_testHash64ByteArrayOverloadnull2994__8 = MurmurHash.hash64(bytesAsObject);
        Assert.assertEquals("MurmurHash.hash64(byte[]) did not match MurmurHash.hash64(String)", input);
    }

    @Test(timeout = 10000)
    public void testHash64ByteArrayOverloadnull2993_add3206() throws Exception {
        String input = "hashthis";
        Assert.assertEquals("hashthis", input);
        byte[] inputBytes = input.getBytes();
        long o_testHash64ByteArrayOverloadnull2993_add3206__4 = MurmurHash.hash64(null);
        Assert.assertEquals(0L, ((long) (o_testHash64ByteArrayOverloadnull2993_add3206__4)));
        long hashOfString = MurmurHash.hash64(null);
        long o_testHash64ByteArrayOverloadnull2993__6 = MurmurHash.hash64(inputBytes);
        Object bytesAsObject = inputBytes;
        long o_testHash64ByteArrayOverloadnull2993__8 = MurmurHash.hash64(bytesAsObject);
        Assert.assertEquals("hashthis", input);
        Assert.assertEquals(0L, ((long) (o_testHash64ByteArrayOverloadnull2993_add3206__4)));
    }

    @Test(timeout = 10000)
    public void testHash64ByteArrayOverloadlitString2986null3193() throws Exception {
        String input = "\n";
        Assert.assertEquals("\n", input);
        byte[] inputBytes = input.getBytes();
        long hashOfString = MurmurHash.hash64(null);
        long o_testHash64ByteArrayOverloadlitString2986__6 = MurmurHash.hash64(inputBytes);
        Object bytesAsObject = inputBytes;
        long o_testHash64ByteArrayOverloadlitString2986__8 = MurmurHash.hash64(bytesAsObject);
        Assert.assertEquals("\n", input);
    }

    @Test(timeout = 10000)
    public void testHash64ByteArrayOverloadlitString2982_add3069() throws Exception {
        String input = "hashth is";
        Assert.assertEquals("hashth is", input);
        byte[] inputBytes = input.getBytes();
        long o_testHash64ByteArrayOverloadlitString2982_add3069__4 = MurmurHash.hash64(input);
        Assert.assertEquals(6125313423190383303L, ((long) (o_testHash64ByteArrayOverloadlitString2982_add3069__4)));
        long hashOfString = MurmurHash.hash64(input);
        long o_testHash64ByteArrayOverloadlitString2982__6 = MurmurHash.hash64(inputBytes);
        Object bytesAsObject = inputBytes;
        long o_testHash64ByteArrayOverloadlitString2982__8 = MurmurHash.hash64(bytesAsObject);
        Assert.assertEquals("hashth is", input);
        Assert.assertEquals(6125313423190383303L, ((long) (o_testHash64ByteArrayOverloadlitString2982_add3069__4)));
    }

    @Test(timeout = 10000)
    public void testHash64ByteArrayOverloadlitString2984null3081() throws Exception {
        String input = "&pb?56Tt";
        Assert.assertEquals("&pb?56Tt", input);
        byte[] inputBytes = input.getBytes();
        long hashOfString = MurmurHash.hash64(input);
        long o_testHash64ByteArrayOverloadlitString2984__6 = MurmurHash.hash64(inputBytes);
        Object bytesAsObject = inputBytes;
        long o_testHash64ByteArrayOverloadlitString2984__8 = MurmurHash.hash64(null);
        Assert.assertEquals("&pb?56Tt", input);
    }

    @Test(timeout = 10000)
    public void testHash64ByteArrayOverload_add2991_add3140null5309() throws Exception {
        String input = "hashthis";
        Assert.assertEquals("hashthis", input);
        byte[] inputBytes = input.getBytes();
        long hashOfString = MurmurHash.hash64(input);
        long o_testHash64ByteArrayOverload_add2991__6 = MurmurHash.hash64(inputBytes);
        Object bytesAsObject = inputBytes;
        long o_testHash64ByteArrayOverload_add2991__8 = MurmurHash.hash64(bytesAsObject);
        long o_testHash64ByteArrayOverload_add2991_add3140__13 = MurmurHash.hash64(null);
        long o_testHash64ByteArrayOverload_add2991__9 = MurmurHash.hash64(bytesAsObject);
        Assert.assertEquals("hashthis", input);
    }

    @Test(timeout = 10000)
    public void testHash64ByteArrayOverload_add2990_add3063litString4981() throws Exception {
        String input = "MurmurHash.hash64(String) returns wrong hash value";
        Assert.assertEquals("MurmurHash.hash64(String) returns wrong hash value", input);
        byte[] inputBytes = input.getBytes();
        long o_testHash64ByteArrayOverload_add2990_add3063__4 = MurmurHash.hash64(input);
        long hashOfString = MurmurHash.hash64(input);
        long o_testHash64ByteArrayOverload_add2990__6 = MurmurHash.hash64(inputBytes);
        long o_testHash64ByteArrayOverload_add2990__7 = MurmurHash.hash64(inputBytes);
        Object bytesAsObject = inputBytes;
        long o_testHash64ByteArrayOverload_add2990__9 = MurmurHash.hash64(bytesAsObject);
        Assert.assertEquals("MurmurHash.hash64(String) returns wrong hash value", input);
    }

    @Test(timeout = 10000)
    public void testHash64ByteArrayOverload_add2991_add3139litString5122() throws Exception {
        String input = "hashNthis";
        Assert.assertEquals("hashNthis", input);
        byte[] inputBytes = input.getBytes();
        long hashOfString = MurmurHash.hash64(input);
        long o_testHash64ByteArrayOverload_add2991__6 = MurmurHash.hash64(inputBytes);
        Object bytesAsObject = inputBytes;
        long o_testHash64ByteArrayOverload_add2991_add3139__10 = MurmurHash.hash64(bytesAsObject);
        long o_testHash64ByteArrayOverload_add2991__8 = MurmurHash.hash64(bytesAsObject);
        long o_testHash64ByteArrayOverload_add2991__9 = MurmurHash.hash64(bytesAsObject);
        Assert.assertEquals("hashNthis", input);
    }

    @Test(timeout = 10000)
    public void testHash64ByteArrayOverload_add2991_add3140litString5212() throws Exception {
        String input = "hahthis";
        Assert.assertEquals("hahthis", input);
        byte[] inputBytes = input.getBytes();
        long hashOfString = MurmurHash.hash64(input);
        long o_testHash64ByteArrayOverload_add2991__6 = MurmurHash.hash64(inputBytes);
        Object bytesAsObject = inputBytes;
        long o_testHash64ByteArrayOverload_add2991__8 = MurmurHash.hash64(bytesAsObject);
        long o_testHash64ByteArrayOverload_add2991_add3140__13 = MurmurHash.hash64(bytesAsObject);
        long o_testHash64ByteArrayOverload_add2991__9 = MurmurHash.hash64(bytesAsObject);
        Assert.assertEquals("hahthis", input);
    }

    @Test(timeout = 10000)
    public void testHash64ByteArrayOverload_add2989_add3183litString4714() throws Exception {
        String input = "MurmurHash.hash(Object) given a byte[] did not match MurmurHash.hash(String)";
        Assert.assertEquals("MurmurHash.hash(Object) given a byte[] did not match MurmurHash.hash(String)", input);
        byte[] inputBytes = input.getBytes();
        long o_testHash64ByteArrayOverload_add2989__4 = MurmurHash.hash64(input);
        long hashOfString = MurmurHash.hash64(input);
        long o_testHash64ByteArrayOverload_add2989__7 = MurmurHash.hash64(inputBytes);
        Object bytesAsObject = inputBytes;
        long o_testHash64ByteArrayOverload_add2989_add3183__13 = MurmurHash.hash64(bytesAsObject);
        long o_testHash64ByteArrayOverload_add2989__9 = MurmurHash.hash64(bytesAsObject);
        Assert.assertEquals("MurmurHash.hash(Object) given a byte[] did not match MurmurHash.hash(String)", input);
    }

    @Test(timeout = 10000)
    public void testHash64ByteArrayOverloadlitString2982_add3071_add4395() throws Exception {
        String input = "hashth is";
        Assert.assertEquals("hashth is", input);
        byte[] inputBytes = input.getBytes();
        long hashOfString = MurmurHash.hash64(input);
        long o_testHash64ByteArrayOverloadlitString2982__6 = MurmurHash.hash64(inputBytes);
        Object bytesAsObject = inputBytes;
        long o_testHash64ByteArrayOverloadlitString2982_add3071_add4395__10 = MurmurHash.hash64(bytesAsObject);
        Assert.assertEquals(6125313423190383303L, ((long) (o_testHash64ByteArrayOverloadlitString2982_add3071_add4395__10)));
        long o_testHash64ByteArrayOverloadlitString2982_add3071__10 = MurmurHash.hash64(bytesAsObject);
        long o_testHash64ByteArrayOverloadlitString2982__8 = MurmurHash.hash64(bytesAsObject);
        Assert.assertEquals("hashth is", input);
        Assert.assertEquals(6125313423190383303L, ((long) (o_testHash64ByteArrayOverloadlitString2982_add3071_add4395__10)));
    }

    @Test(timeout = 10000)
    public void testHash64ByteArrayOverload_add2989_add3181litString4474() throws Exception {
        String input = "MurmurHash.hash(byte[]) did not match MurmurHash.hash(String)";
        Assert.assertEquals("MurmurHash.hash(byte[]) did not match MurmurHash.hash(String)", input);
        byte[] inputBytes = input.getBytes();
        long o_testHash64ByteArrayOverload_add2989__4 = MurmurHash.hash64(input);
        long o_testHash64ByteArrayOverload_add2989_add3181__7 = MurmurHash.hash64(input);
        long hashOfString = MurmurHash.hash64(input);
        long o_testHash64ByteArrayOverload_add2989__7 = MurmurHash.hash64(inputBytes);
        Object bytesAsObject = inputBytes;
        long o_testHash64ByteArrayOverload_add2989__9 = MurmurHash.hash64(bytesAsObject);
        Assert.assertEquals("MurmurHash.hash(byte[]) did not match MurmurHash.hash(String)", input);
    }

    @Test(timeout = 10000)
    public void testHash64litString1480() throws Exception {
        final long actualHash = MurmurHash.hash64("\n");
        Assert.assertEquals(2044312949510316467L, ((long) (actualHash)));
        final long expectedHash = -8896273065425798843L;
        Assert.assertEquals(-8896273065425798843L, ((long) (expectedHash)));
        Assert.assertEquals(2044312949510316467L, ((long) (actualHash)));
    }

    @Test(timeout = 10000)
    public void testHash64null1489() throws Exception {
        final long actualHash = MurmurHash.hash64(null);
        Assert.assertEquals(0L, ((long) (actualHash)));
        final long expectedHash = -8896273065425798843L;
        Assert.assertEquals(-8896273065425798843L, ((long) (expectedHash)));
        Assert.assertEquals(0L, ((long) (actualHash)));
    }

    @Test(timeout = 10000)
    public void testHash64litString1477() throws Exception {
        final long actualHash = MurmurHash.hash64("hashtis");
        Assert.assertEquals(1425033108509731271L, ((long) (actualHash)));
        final long expectedHash = -8896273065425798843L;
        Assert.assertEquals(-8896273065425798843L, ((long) (expectedHash)));
        Assert.assertEquals(1425033108509731271L, ((long) (actualHash)));
    }

    @Test(timeout = 10000)
    public void testHash64litString1476() throws Exception {
        final long actualHash = MurmurHash.hash64("ha?shthis");
        Assert.assertEquals(9152548290412618688L, ((long) (actualHash)));
        final long expectedHash = -8896273065425798843L;
        Assert.assertEquals(-8896273065425798843L, ((long) (expectedHash)));
        Assert.assertEquals(9152548290412618688L, ((long) (actualHash)));
    }

    @Test(timeout = 10000)
    public void testHash64null1489_add1610() throws Exception {
        long o_testHash64null1489_add1610__1 = MurmurHash.hash64(null);
        Assert.assertEquals(0L, ((long) (o_testHash64null1489_add1610__1)));
        final long actualHash = MurmurHash.hash64(null);
        final long expectedHash = -8896273065425798843L;
        Assert.assertEquals(0L, ((long) (o_testHash64null1489_add1610__1)));
    }

    @Test(timeout = 10000)
    public void testHash64litString1476_add1670() throws Exception {
        long o_testHash64litString1476_add1670__1 = MurmurHash.hash64("ha?shthis");
        Assert.assertEquals(9152548290412618688L, ((long) (o_testHash64litString1476_add1670__1)));
        final long actualHash = MurmurHash.hash64("ha?shthis");
        final long expectedHash = -8896273065425798843L;
        Assert.assertEquals(9152548290412618688L, ((long) (o_testHash64litString1476_add1670__1)));
    }

    @Test(timeout = 10000)
    public void testHash64litString1477_add1684() throws Exception {
        long o_testHash64litString1477_add1684__1 = MurmurHash.hash64("hashtis");
        Assert.assertEquals(1425033108509731271L, ((long) (o_testHash64litString1477_add1684__1)));
        final long actualHash = MurmurHash.hash64("hashtis");
        final long expectedHash = -8896273065425798843L;
        Assert.assertEquals(1425033108509731271L, ((long) (o_testHash64litString1477_add1684__1)));
    }

    @Test(timeout = 10000)
    public void testHash64litString1477_add1684_add2365() throws Exception {
        long o_testHash64litString1477_add1684__1 = MurmurHash.hash64("hashtis");
        long o_testHash64litString1477_add1684_add2365__4 = MurmurHash.hash64("hashtis");
        Assert.assertEquals(1425033108509731271L, ((long) (o_testHash64litString1477_add1684_add2365__4)));
        final long actualHash = MurmurHash.hash64("hashtis");
        final long expectedHash = -8896273065425798843L;
        Assert.assertEquals(1425033108509731271L, ((long) (o_testHash64litString1477_add1684_add2365__4)));
    }

    @Test(timeout = 10000)
    public void testHash64litString1476_add1670_add2350() throws Exception {
        long o_testHash64litString1476_add1670_add2350__1 = MurmurHash.hash64("ha?shthis");
        Assert.assertEquals(9152548290412618688L, ((long) (o_testHash64litString1476_add1670_add2350__1)));
        long o_testHash64litString1476_add1670__1 = MurmurHash.hash64("ha?shthis");
        final long actualHash = MurmurHash.hash64("ha?shthis");
        final long expectedHash = -8896273065425798843L;
        Assert.assertEquals(9152548290412618688L, ((long) (o_testHash64litString1476_add1670_add2350__1)));
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
    public void testHashlitString7_add126() throws Exception {
        int o_testHashlitString7_add126__1 = MurmurHash.hash("\n");
        Assert.assertEquals(-330957644, ((int) (o_testHashlitString7_add126__1)));
        final long actualHash = MurmurHash.hash("\n");
        final long expectedHash = -1974946086L;
        Assert.assertEquals(-330957644, ((int) (o_testHashlitString7_add126__1)));
    }

    @Test(timeout = 10000)
    public void testHashlitString4_add178() throws Exception {
        int o_testHashlitString4_add178__1 = MurmurHash.hash("hashhis");
        Assert.assertEquals(-590652159, ((int) (o_testHashlitString4_add178__1)));
        final long actualHash = MurmurHash.hash("hashhis");
        final long expectedHash = -1974946086L;
        Assert.assertEquals(-590652159, ((int) (o_testHashlitString4_add178__1)));
    }

    @Test(timeout = 10000)
    public void testHashlitString1_add135() throws Exception {
        int o_testHashlitString1_add135__1 = MurmurHash.hash("MurmurHash.hash64(String) returns wrong hash value");
        Assert.assertEquals(-1053542750, ((int) (o_testHashlitString1_add135__1)));
        final long actualHash = MurmurHash.hash("MurmurHash.hash64(String) returns wrong hash value");
        final long expectedHash = -1974946086L;
        Assert.assertEquals(-1053542750, ((int) (o_testHashlitString1_add135__1)));
    }

    @Test(timeout = 10000)
    public void testHashlitString3_add182_add668() throws Exception {
        int o_testHashlitString3_add182_add668__1 = MurmurHash.hash("hash,this");
        Assert.assertEquals(1486645312, ((int) (o_testHashlitString3_add182_add668__1)));
        int o_testHashlitString3_add182__1 = MurmurHash.hash("hash,this");
        final long actualHash = MurmurHash.hash("hash,this");
        final long expectedHash = -1974946086L;
        Assert.assertEquals(1486645312, ((int) (o_testHashlitString3_add182_add668__1)));
    }

    @Test(timeout = 10000)
    public void testHashlitString1_add135_add664() throws Exception {
        int o_testHashlitString1_add135__1 = MurmurHash.hash("MurmurHash.hash64(String) returns wrong hash value");
        int o_testHashlitString1_add135_add664__4 = MurmurHash.hash("MurmurHash.hash64(String) returns wrong hash value");
        Assert.assertEquals(-1053542750, ((int) (o_testHashlitString1_add135_add664__4)));
        final long actualHash = MurmurHash.hash("MurmurHash.hash64(String) returns wrong hash value");
        final long expectedHash = -1974946086L;
        Assert.assertEquals(-1053542750, ((int) (o_testHashlitString1_add135_add664__4)));
    }
}

