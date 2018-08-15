package com.clearspring.analytics.hash;


import org.junit.Assert;
import org.junit.Test;


public class TestMurmurHashAmpl {
    @Test(timeout = 10000)
    public void testHashByteArrayOverloadlitString6225() throws Exception {
        String input = "hahthis";
        Assert.assertEquals("hahthis", input);
        byte[] inputBytes = input.getBytes();
        int hashOfString = MurmurHash.hash(input);
        Assert.assertEquals(1840755242, ((int) (hashOfString)));
        int o_testHashByteArrayOverloadlitString6225__6 = MurmurHash.hash(inputBytes);
        Assert.assertEquals(1840755242, ((int) (o_testHashByteArrayOverloadlitString6225__6)));
        Object bytesAsObject = inputBytes;
        int o_testHashByteArrayOverloadlitString6225__8 = MurmurHash.hash(bytesAsObject);
        Assert.assertEquals(1840755242, ((int) (o_testHashByteArrayOverloadlitString6225__8)));
        Assert.assertEquals("hahthis", input);
        Assert.assertEquals(1840755242, ((int) (hashOfString)));
        Assert.assertEquals(1840755242, ((int) (o_testHashByteArrayOverloadlitString6225__6)));
    }

    @Test(timeout = 10000)
    public void testHashByteArrayOverloadlitString6229() throws Exception {
        String input = ":";
        Assert.assertEquals(":", input);
        byte[] inputBytes = input.getBytes();
        int hashOfString = MurmurHash.hash(input);
        Assert.assertEquals(-100914771, ((int) (hashOfString)));
        int o_testHashByteArrayOverloadlitString6229__6 = MurmurHash.hash(inputBytes);
        Assert.assertEquals(-100914771, ((int) (o_testHashByteArrayOverloadlitString6229__6)));
        Object bytesAsObject = inputBytes;
        int o_testHashByteArrayOverloadlitString6229__8 = MurmurHash.hash(bytesAsObject);
        Assert.assertEquals(-100914771, ((int) (o_testHashByteArrayOverloadlitString6229__8)));
        Assert.assertEquals(":", input);
        Assert.assertEquals(-100914771, ((int) (hashOfString)));
        Assert.assertEquals(-100914771, ((int) (o_testHashByteArrayOverloadlitString6229__6)));
    }

    @Test(timeout = 10000)
    public void testHashByteArrayOverloadlitString6229_add6364() throws Exception {
        String input = ":";
        Assert.assertEquals(":", input);
        byte[] inputBytes = input.getBytes();
        int hashOfString = MurmurHash.hash(input);
        int o_testHashByteArrayOverloadlitString6229_add6364__6 = MurmurHash.hash(inputBytes);
        Assert.assertEquals(-100914771, ((int) (o_testHashByteArrayOverloadlitString6229_add6364__6)));
        int o_testHashByteArrayOverloadlitString6229__6 = MurmurHash.hash(inputBytes);
        Object bytesAsObject = inputBytes;
        int o_testHashByteArrayOverloadlitString6229__8 = MurmurHash.hash(bytesAsObject);
        Assert.assertEquals(":", input);
        Assert.assertEquals(-100914771, ((int) (o_testHashByteArrayOverloadlitString6229_add6364__6)));
    }

    @Test(timeout = 10000)
    public void testHashByteArrayOverloadlitString6228_add6342() throws Exception {
        String input = "\n";
        Assert.assertEquals("\n", input);
        input.getBytes();
        byte[] inputBytes = input.getBytes();
        int hashOfString = MurmurHash.hash(input);
        int o_testHashByteArrayOverloadlitString6228__6 = MurmurHash.hash(inputBytes);
        Object bytesAsObject = inputBytes;
        int o_testHashByteArrayOverloadlitString6228__8 = MurmurHash.hash(bytesAsObject);
        Assert.assertEquals("\n", input);
    }

    @Test(timeout = 10000)
    public void testHashByteArrayOverloadlitString6225_add6352() throws Exception {
        String input = "hahthis";
        Assert.assertEquals("hahthis", input);
        byte[] inputBytes = input.getBytes();
        int hashOfString = MurmurHash.hash(input);
        int o_testHashByteArrayOverloadlitString6225_add6352__6 = MurmurHash.hash(inputBytes);
        Assert.assertEquals(1840755242, ((int) (o_testHashByteArrayOverloadlitString6225_add6352__6)));
        int o_testHashByteArrayOverloadlitString6225__6 = MurmurHash.hash(inputBytes);
        Object bytesAsObject = inputBytes;
        int o_testHashByteArrayOverloadlitString6225__8 = MurmurHash.hash(bytesAsObject);
        Assert.assertEquals("hahthis", input);
        Assert.assertEquals(1840755242, ((int) (o_testHashByteArrayOverloadlitString6225_add6352__6)));
    }

    @Test(timeout = 10000)
    public void testHashByteArrayOverloadlitString6224null6433_failAssert60() throws Exception {
        try {
            String input = "hasIhthis";
            byte[] inputBytes = input.getBytes();
            int hashOfString = MurmurHash.hash(input);
            int o_testHashByteArrayOverloadlitString6224__6 = MurmurHash.hash(null);
            Object bytesAsObject = inputBytes;
            int o_testHashByteArrayOverloadlitString6224__8 = MurmurHash.hash(bytesAsObject);
            org.junit.Assert.fail("testHashByteArrayOverloadlitString6224null6433 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHashByteArrayOverload_add6230litString6301_add7668() throws Exception {
        String input = "hahthis";
        Assert.assertEquals("hahthis", input);
        input.getBytes();
        byte[] inputBytes = input.getBytes();
        int hashOfString = MurmurHash.hash(input);
        int o_testHashByteArrayOverload_add6230__7 = MurmurHash.hash(inputBytes);
        Object bytesAsObject = inputBytes;
        int o_testHashByteArrayOverload_add6230litString6301_add7668__11 = MurmurHash.hash(bytesAsObject);
        Assert.assertEquals(1840755242, ((int) (o_testHashByteArrayOverload_add6230litString6301_add7668__11)));
        int o_testHashByteArrayOverload_add6230__9 = MurmurHash.hash(bytesAsObject);
        Assert.assertEquals("hahthis", input);
        Assert.assertEquals(1840755242, ((int) (o_testHashByteArrayOverload_add6230litString6301_add7668__11)));
    }

    @Test(timeout = 10000)
    public void testHashByteArrayOverloadnull6237_failAssert38_add6391litString7266() throws Exception {
        try {
            String input = "hahthis";
            Assert.assertEquals("hahthis", input);
            byte[] inputBytes = input.getBytes();
            int o_testHashByteArrayOverloadnull6237_failAssert38_add6391__6 = MurmurHash.hash(input);
            int hashOfString = MurmurHash.hash(input);
            int o_testHashByteArrayOverloadnull6237_failAssert38_add6391__9 = MurmurHash.hash(inputBytes);
            Object bytesAsObject = inputBytes;
            MurmurHash.hash(null);
            org.junit.Assert.fail("testHashByteArrayOverloadnull6237 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testHashByteArrayOverloadlitString6224_add6369_add7489() throws Exception {
        String input = "hasIhthis";
        Assert.assertEquals("hasIhthis", input);
        byte[] inputBytes = input.getBytes();
        int o_testHashByteArrayOverloadlitString6224_add6369_add7489__4 = MurmurHash.hash(input);
        Assert.assertEquals(-820024580, ((int) (o_testHashByteArrayOverloadlitString6224_add6369_add7489__4)));
        int hashOfString = MurmurHash.hash(input);
        int o_testHashByteArrayOverloadlitString6224__6 = MurmurHash.hash(inputBytes);
        Object bytesAsObject = inputBytes;
        int o_testHashByteArrayOverloadlitString6224_add6369__10 = MurmurHash.hash(bytesAsObject);
        int o_testHashByteArrayOverloadlitString6224__8 = MurmurHash.hash(bytesAsObject);
        Assert.assertEquals("hasIhthis", input);
        Assert.assertEquals(-820024580, ((int) (o_testHashByteArrayOverloadlitString6224_add6369_add7489__4)));
    }

    @Test(timeout = 10000)
    public void testHash64ByteArrayOverloadlitString2973() throws Exception {
        String input = ":";
        Assert.assertEquals(":", input);
        byte[] inputBytes = input.getBytes();
        long hashOfString = MurmurHash.hash64(input);
        Assert.assertEquals(-1293469037080459059L, ((long) (hashOfString)));
        long o_testHash64ByteArrayOverloadlitString2973__6 = MurmurHash.hash64(inputBytes);
        Assert.assertEquals(-1293469037080459059L, ((long) (o_testHash64ByteArrayOverloadlitString2973__6)));
        Object bytesAsObject = inputBytes;
        long o_testHash64ByteArrayOverloadlitString2973__8 = MurmurHash.hash64(bytesAsObject);
        Assert.assertEquals(-1293469037080459059L, ((long) (o_testHash64ByteArrayOverloadlitString2973__8)));
        Assert.assertEquals(":", input);
        Assert.assertEquals(-1293469037080459059L, ((long) (hashOfString)));
        Assert.assertEquals(-1293469037080459059L, ((long) (o_testHash64ByteArrayOverloadlitString2973__6)));
    }

    @Test(timeout = 10000)
    public void testHash64ByteArrayOverloadnull2979() throws Exception {
        String input = "hashthis";
        Assert.assertEquals("hashthis", input);
        byte[] inputBytes = input.getBytes();
        long hashOfString = MurmurHash.hash64(null);
        Assert.assertEquals(0L, ((long) (hashOfString)));
        long o_testHash64ByteArrayOverloadnull2979__6 = MurmurHash.hash64(inputBytes);
        Assert.assertEquals(-8896273065425798843L, ((long) (o_testHash64ByteArrayOverloadnull2979__6)));
        Object bytesAsObject = inputBytes;
        long o_testHash64ByteArrayOverloadnull2979__8 = MurmurHash.hash64(bytesAsObject);
        Assert.assertEquals(-8896273065425798843L, ((long) (o_testHash64ByteArrayOverloadnull2979__8)));
        Assert.assertEquals("hashthis", input);
        Assert.assertEquals(0L, ((long) (hashOfString)));
        Assert.assertEquals(-8896273065425798843L, ((long) (o_testHash64ByteArrayOverloadnull2979__6)));
    }

    @Test(timeout = 10000)
    public void testHash64ByteArrayOverloadlitString2968() throws Exception {
        String input = "hashthjis";
        Assert.assertEquals("hashthjis", input);
        byte[] inputBytes = input.getBytes();
        long hashOfString = MurmurHash.hash64(input);
        Assert.assertEquals(-333064861504934351L, ((long) (hashOfString)));
        long o_testHash64ByteArrayOverloadlitString2968__6 = MurmurHash.hash64(inputBytes);
        Assert.assertEquals(-333064861504934351L, ((long) (o_testHash64ByteArrayOverloadlitString2968__6)));
        Object bytesAsObject = inputBytes;
        long o_testHash64ByteArrayOverloadlitString2968__8 = MurmurHash.hash64(bytesAsObject);
        Assert.assertEquals(-333064861504934351L, ((long) (o_testHash64ByteArrayOverloadlitString2968__8)));
        Assert.assertEquals("hashthjis", input);
        Assert.assertEquals(-333064861504934351L, ((long) (hashOfString)));
        Assert.assertEquals(-333064861504934351L, ((long) (o_testHash64ByteArrayOverloadlitString2968__6)));
    }

    @Test(timeout = 10000)
    public void testHash64ByteArrayOverloadlitString2969() throws Exception {
        String input = "hashtis";
        Assert.assertEquals("hashtis", input);
        byte[] inputBytes = input.getBytes();
        long hashOfString = MurmurHash.hash64(input);
        Assert.assertEquals(1425033108509731271L, ((long) (hashOfString)));
        long o_testHash64ByteArrayOverloadlitString2969__6 = MurmurHash.hash64(inputBytes);
        Assert.assertEquals(1425033108509731271L, ((long) (o_testHash64ByteArrayOverloadlitString2969__6)));
        Object bytesAsObject = inputBytes;
        long o_testHash64ByteArrayOverloadlitString2969__8 = MurmurHash.hash64(bytesAsObject);
        Assert.assertEquals(1425033108509731271L, ((long) (o_testHash64ByteArrayOverloadlitString2969__8)));
        Assert.assertEquals("hashtis", input);
        Assert.assertEquals(1425033108509731271L, ((long) (hashOfString)));
        Assert.assertEquals(1425033108509731271L, ((long) (o_testHash64ByteArrayOverloadlitString2969__6)));
    }

    @Test(timeout = 10000)
    public void testHash64ByteArrayOverloadnull2979litString3082() throws Exception {
        String input = "";
        Assert.assertEquals("", input);
        byte[] inputBytes = input.getBytes();
        long hashOfString = MurmurHash.hash64(null);
        long o_testHash64ByteArrayOverloadnull2979__6 = MurmurHash.hash64(inputBytes);
        Object bytesAsObject = inputBytes;
        long o_testHash64ByteArrayOverloadnull2979__8 = MurmurHash.hash64(bytesAsObject);
        Assert.assertEquals("", input);
    }

    @Test(timeout = 10000)
    public void testHash64ByteArrayOverloadnull2981_add3140() throws Exception {
        String input = "hashthis";
        Assert.assertEquals("hashthis", input);
        byte[] inputBytes = input.getBytes();
        long hashOfString = MurmurHash.hash64(input);
        long o_testHash64ByteArrayOverloadnull2981__6 = MurmurHash.hash64(inputBytes);
        Object bytesAsObject = inputBytes;
        long o_testHash64ByteArrayOverloadnull2981_add3140__10 = MurmurHash.hash64(null);
        Assert.assertEquals(0L, ((long) (o_testHash64ByteArrayOverloadnull2981_add3140__10)));
        long o_testHash64ByteArrayOverloadnull2981__8 = MurmurHash.hash64(null);
        Assert.assertEquals("hashthis", input);
        Assert.assertEquals(0L, ((long) (o_testHash64ByteArrayOverloadnull2981_add3140__10)));
    }

    @Test(timeout = 10000)
    public void testHash64ByteArrayOverloadlitString2969_add3110() throws Exception {
        String input = "hashtis";
        Assert.assertEquals("hashtis", input);
        byte[] inputBytes = input.getBytes();
        long o_testHash64ByteArrayOverloadlitString2969_add3110__4 = MurmurHash.hash64(input);
        Assert.assertEquals(1425033108509731271L, ((long) (o_testHash64ByteArrayOverloadlitString2969_add3110__4)));
        long hashOfString = MurmurHash.hash64(input);
        long o_testHash64ByteArrayOverloadlitString2969__6 = MurmurHash.hash64(inputBytes);
        Object bytesAsObject = inputBytes;
        long o_testHash64ByteArrayOverloadlitString2969__8 = MurmurHash.hash64(bytesAsObject);
        Assert.assertEquals("hashtis", input);
        Assert.assertEquals(1425033108509731271L, ((long) (o_testHash64ByteArrayOverloadlitString2969_add3110__4)));
    }

    @Test(timeout = 10000)
    public void testHash64ByteArrayOverloadnull2979litString3083() throws Exception {
        String input = "\n";
        Assert.assertEquals("\n", input);
        byte[] inputBytes = input.getBytes();
        long hashOfString = MurmurHash.hash64(null);
        long o_testHash64ByteArrayOverloadnull2979__6 = MurmurHash.hash64(inputBytes);
        Object bytesAsObject = inputBytes;
        long o_testHash64ByteArrayOverloadnull2979__8 = MurmurHash.hash64(bytesAsObject);
        Assert.assertEquals("\n", input);
    }

    @Test(timeout = 10000)
    public void testHash64ByteArrayOverloadnull2980litString3069() throws Exception {
        String input = "MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)";
        Assert.assertEquals("MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)", input);
        byte[] inputBytes = input.getBytes();
        long hashOfString = MurmurHash.hash64(input);
        long o_testHash64ByteArrayOverloadnull2980__6 = MurmurHash.hash64(null);
        Object bytesAsObject = inputBytes;
        long o_testHash64ByteArrayOverloadnull2980__8 = MurmurHash.hash64(bytesAsObject);
        Assert.assertEquals("MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)", input);
    }

    @Test(timeout = 10000)
    public void testHash64ByteArrayOverloadlitString2968_add3086() throws Exception {
        String input = "hashthjis";
        Assert.assertEquals("hashthjis", input);
        byte[] inputBytes = input.getBytes();
        long o_testHash64ByteArrayOverloadlitString2968_add3086__4 = MurmurHash.hash64(input);
        Assert.assertEquals(-333064861504934351L, ((long) (o_testHash64ByteArrayOverloadlitString2968_add3086__4)));
        long hashOfString = MurmurHash.hash64(input);
        long o_testHash64ByteArrayOverloadlitString2968__6 = MurmurHash.hash64(inputBytes);
        Object bytesAsObject = inputBytes;
        long o_testHash64ByteArrayOverloadlitString2968__8 = MurmurHash.hash64(bytesAsObject);
        Assert.assertEquals("hashthjis", input);
        Assert.assertEquals(-333064861504934351L, ((long) (o_testHash64ByteArrayOverloadlitString2968_add3086__4)));
    }

    @Test(timeout = 10000)
    public void testHash64ByteArrayOverload_add2976litString3032() throws Exception {
        String input = "hasthis";
        Assert.assertEquals("hasthis", input);
        byte[] inputBytes = input.getBytes();
        long hashOfString = MurmurHash.hash64(input);
        long o_testHash64ByteArrayOverload_add2976__6 = MurmurHash.hash64(inputBytes);
        long o_testHash64ByteArrayOverload_add2976__7 = MurmurHash.hash64(inputBytes);
        Object bytesAsObject = inputBytes;
        long o_testHash64ByteArrayOverload_add2976__9 = MurmurHash.hash64(bytesAsObject);
        Assert.assertEquals("hasthis", input);
    }

    @Test(timeout = 10000)
    public void testHash64ByteArrayOverloadnull2979null3206_add4321() throws Exception {
        String input = "hashthis";
        Assert.assertEquals("hashthis", input);
        byte[] inputBytes = input.getBytes();
        long o_testHash64ByteArrayOverloadnull2979null3206_add4321__4 = MurmurHash.hash64(null);
        Assert.assertEquals(0L, ((long) (o_testHash64ByteArrayOverloadnull2979null3206_add4321__4)));
        long hashOfString = MurmurHash.hash64(null);
        long o_testHash64ByteArrayOverloadnull2979__6 = MurmurHash.hash64(inputBytes);
        Object bytesAsObject = inputBytes;
        long o_testHash64ByteArrayOverloadnull2979__8 = MurmurHash.hash64(null);
        Assert.assertEquals("hashthis", input);
        Assert.assertEquals(0L, ((long) (o_testHash64ByteArrayOverloadnull2979null3206_add4321__4)));
    }

    @Test(timeout = 10000)
    public void testHash64ByteArrayOverloadlitString2972null3158_add4932() throws Exception {
        String input = "\n";
        Assert.assertEquals("\n", input);
        byte[] inputBytes = input.getBytes();
        long hashOfString = MurmurHash.hash64(null);
        long o_testHash64ByteArrayOverloadlitString2972null3158_add4932__6 = MurmurHash.hash64(inputBytes);
        Assert.assertEquals(2044312949510316467L, ((long) (o_testHash64ByteArrayOverloadlitString2972null3158_add4932__6)));
        long o_testHash64ByteArrayOverloadlitString2972__6 = MurmurHash.hash64(inputBytes);
        Object bytesAsObject = inputBytes;
        long o_testHash64ByteArrayOverloadlitString2972__8 = MurmurHash.hash64(bytesAsObject);
        Assert.assertEquals("\n", input);
        Assert.assertEquals(2044312949510316467L, ((long) (o_testHash64ByteArrayOverloadlitString2972null3158_add4932__6)));
    }

    @Test(timeout = 10000)
    public void testHash64ByteArrayOverload_add2975_add3124litString3773() throws Exception {
        String input = "hashths";
        Assert.assertEquals("hashths", input);
        byte[] inputBytes = input.getBytes();
        long o_testHash64ByteArrayOverload_add2975__4 = MurmurHash.hash64(input);
        long o_testHash64ByteArrayOverload_add2975_add3124__7 = MurmurHash.hash64(input);
        long hashOfString = MurmurHash.hash64(input);
        long o_testHash64ByteArrayOverload_add2975__7 = MurmurHash.hash64(inputBytes);
        Object bytesAsObject = inputBytes;
        long o_testHash64ByteArrayOverload_add2975__9 = MurmurHash.hash64(bytesAsObject);
        Assert.assertEquals("hashths", input);
    }

    @Test(timeout = 10000)
    public void testHash64ByteArrayOverload_add2974litString3045_remove5017() throws Exception {
        String input = "MurmurHash.hash64(byte[]) did not match MurmurHash.hash64(String)";
        Assert.assertEquals("MurmurHash.hash64(byte[]) did not match MurmurHash.hash64(String)", input);
        byte[] inputBytes = input.getBytes();
        long hashOfString = MurmurHash.hash64(input);
        long o_testHash64ByteArrayOverload_add2974__7 = MurmurHash.hash64(inputBytes);
        Object bytesAsObject = inputBytes;
        long o_testHash64ByteArrayOverload_add2974__9 = MurmurHash.hash64(bytesAsObject);
        Assert.assertEquals("MurmurHash.hash64(byte[]) did not match MurmurHash.hash64(String)", input);
    }

    @Test(timeout = 10000)
    public void testHash64ByteArrayOverloadnull2980_add3143litString3867() throws Exception {
        String input = "h>ashthis";
        Assert.assertEquals("h>ashthis", input);
        byte[] inputBytes = input.getBytes();
        long hashOfString = MurmurHash.hash64(input);
        long o_testHash64ByteArrayOverloadnull2980_add3143__6 = MurmurHash.hash64(null);
        long o_testHash64ByteArrayOverloadnull2980__6 = MurmurHash.hash64(null);
        Object bytesAsObject = inputBytes;
        long o_testHash64ByteArrayOverloadnull2980__8 = MurmurHash.hash64(bytesAsObject);
        Assert.assertEquals("h>ashthis", input);
    }

    @Test(timeout = 10000)
    public void testHash64ByteArrayOverloadnull2979litString3079_add4554() throws Exception {
        String input = "hash+this";
        Assert.assertEquals("hash+this", input);
        byte[] inputBytes = input.getBytes();
        long hashOfString = MurmurHash.hash64(null);
        long o_testHash64ByteArrayOverloadnull2979litString3079_add4554__6 = MurmurHash.hash64(inputBytes);
        Assert.assertEquals(2073277719130082834L, ((long) (o_testHash64ByteArrayOverloadnull2979litString3079_add4554__6)));
        long o_testHash64ByteArrayOverloadnull2979__6 = MurmurHash.hash64(inputBytes);
        Object bytesAsObject = inputBytes;
        long o_testHash64ByteArrayOverloadnull2979__8 = MurmurHash.hash64(bytesAsObject);
        Assert.assertEquals("hash+this", input);
        Assert.assertEquals(2073277719130082834L, ((long) (o_testHash64ByteArrayOverloadnull2979litString3079_add4554__6)));
    }

    @Test(timeout = 10000)
    public void testHash64ByteArrayOverloadnull2979null3205null5547() throws Exception {
        String input = "hashthis";
        Assert.assertEquals("hashthis", input);
        byte[] inputBytes = input.getBytes();
        long hashOfString = MurmurHash.hash64(null);
        long o_testHash64ByteArrayOverloadnull2979__6 = MurmurHash.hash64(null);
        Object bytesAsObject = inputBytes;
        long o_testHash64ByteArrayOverloadnull2979__8 = MurmurHash.hash64(null);
        Assert.assertEquals("hashthis", input);
    }

    @Test(timeout = 10000)
    public void testHash64ByteArrayOverload_add2975litString3040_add4452() throws Exception {
        String input = "hashtis";
        Assert.assertEquals("hashtis", input);
        byte[] inputBytes = input.getBytes();
        long o_testHash64ByteArrayOverload_add2975__4 = MurmurHash.hash64(input);
        long hashOfString = MurmurHash.hash64(input);
        long o_testHash64ByteArrayOverload_add2975__7 = MurmurHash.hash64(inputBytes);
        Object bytesAsObject = inputBytes;
        long o_testHash64ByteArrayOverload_add2975litString3040_add4452__13 = MurmurHash.hash64(bytesAsObject);
        Assert.assertEquals(1425033108509731271L, ((long) (o_testHash64ByteArrayOverload_add2975litString3040_add4452__13)));
        long o_testHash64ByteArrayOverload_add2975__9 = MurmurHash.hash64(bytesAsObject);
        Assert.assertEquals("hashtis", input);
        Assert.assertEquals(1425033108509731271L, ((long) (o_testHash64ByteArrayOverload_add2975litString3040_add4452__13)));
    }

    @Test(timeout = 10000)
    public void testHash64litString1526() throws Exception {
        final long actualHash = MurmurHash.hash64("MurmurHash.hash(Object) given a byte[] did not match MurmurHash.hash(String)");
        Assert.assertEquals(9067269536460420435L, ((long) (actualHash)));
        final long expectedHash = -8896273065425798843L;
        Assert.assertEquals(-8896273065425798843L, ((long) (expectedHash)));
        Assert.assertEquals(9067269536460420435L, ((long) (actualHash)));
    }

    @Test(timeout = 10000)
    public void testHash64litString1529() throws Exception {
        final long actualHash = MurmurHash.hash64("hasthis");
        Assert.assertEquals(3602824184977593628L, ((long) (actualHash)));
        final long expectedHash = -8896273065425798843L;
        Assert.assertEquals(-8896273065425798843L, ((long) (expectedHash)));
        Assert.assertEquals(3602824184977593628L, ((long) (actualHash)));
    }

    @Test(timeout = 10000)
    public void testHash64null1541() throws Exception {
        final long actualHash = MurmurHash.hash64(null);
        Assert.assertEquals(0L, ((long) (actualHash)));
        final long expectedHash = -8896273065425798843L;
        Assert.assertEquals(-8896273065425798843L, ((long) (expectedHash)));
        Assert.assertEquals(0L, ((long) (actualHash)));
    }

    @Test(timeout = 10000)
    public void testHash64litString1528() throws Exception {
        final long actualHash = MurmurHash.hash64("hashth,is");
        Assert.assertEquals(-4558760507743029536L, ((long) (actualHash)));
        final long expectedHash = -8896273065425798843L;
        Assert.assertEquals(-8896273065425798843L, ((long) (expectedHash)));
        Assert.assertEquals(-4558760507743029536L, ((long) (actualHash)));
    }

    @Test(timeout = 10000)
    public void testHash64litString1526_add1707() throws Exception {
        long o_testHash64litString1526_add1707__1 = MurmurHash.hash64("MurmurHash.hash(Object) given a byte[] did not match MurmurHash.hash(String)");
        Assert.assertEquals(9067269536460420435L, ((long) (o_testHash64litString1526_add1707__1)));
        final long actualHash = MurmurHash.hash64("MurmurHash.hash(Object) given a byte[] did not match MurmurHash.hash(String)");
        final long expectedHash = -8896273065425798843L;
        Assert.assertEquals(9067269536460420435L, ((long) (o_testHash64litString1526_add1707__1)));
    }

    @Test(timeout = 10000)
    public void testHash64litString1529_add1709() throws Exception {
        long o_testHash64litString1529_add1709__1 = MurmurHash.hash64("hasthis");
        Assert.assertEquals(3602824184977593628L, ((long) (o_testHash64litString1529_add1709__1)));
        final long actualHash = MurmurHash.hash64("hasthis");
        final long expectedHash = -8896273065425798843L;
        Assert.assertEquals(3602824184977593628L, ((long) (o_testHash64litString1529_add1709__1)));
    }

    @Test(timeout = 10000)
    public void testHash64litString1533_add1711() throws Exception {
        long o_testHash64litString1533_add1711__1 = MurmurHash.hash64(":");
        Assert.assertEquals(-1293469037080459059L, ((long) (o_testHash64litString1533_add1711__1)));
        final long actualHash = MurmurHash.hash64(":");
        final long expectedHash = -8896273065425798843L;
        Assert.assertEquals(-1293469037080459059L, ((long) (o_testHash64litString1533_add1711__1)));
    }

    @Test(timeout = 10000)
    public void testHash64litString1528_add1705() throws Exception {
        long o_testHash64litString1528_add1705__1 = MurmurHash.hash64("hashth,is");
        Assert.assertEquals(-4558760507743029536L, ((long) (o_testHash64litString1528_add1705__1)));
        final long actualHash = MurmurHash.hash64("hashth,is");
        final long expectedHash = -8896273065425798843L;
        Assert.assertEquals(-4558760507743029536L, ((long) (o_testHash64litString1528_add1705__1)));
    }

    @Test(timeout = 10000)
    public void testHash64null1541_add1719() throws Exception {
        long o_testHash64null1541_add1719__1 = MurmurHash.hash64(null);
        Assert.assertEquals(0L, ((long) (o_testHash64null1541_add1719__1)));
        final long actualHash = MurmurHash.hash64(null);
        final long expectedHash = -8896273065425798843L;
        Assert.assertEquals(0L, ((long) (o_testHash64null1541_add1719__1)));
    }

    @Test(timeout = 10000)
    public void testHash64null1541_add1719_add2330() throws Exception {
        long o_testHash64null1541_add1719_add2330__1 = MurmurHash.hash64(null);
        Assert.assertEquals(0L, ((long) (o_testHash64null1541_add1719_add2330__1)));
        long o_testHash64null1541_add1719__1 = MurmurHash.hash64(null);
        final long actualHash = MurmurHash.hash64(null);
        final long expectedHash = -8896273065425798843L;
        Assert.assertEquals(0L, ((long) (o_testHash64null1541_add1719_add2330__1)));
    }

    @Test(timeout = 10000)
    public void testHash64litString1529_add1709_add2352() throws Exception {
        long o_testHash64litString1529_add1709_add2352__1 = MurmurHash.hash64("hasthis");
        Assert.assertEquals(3602824184977593628L, ((long) (o_testHash64litString1529_add1709_add2352__1)));
        long o_testHash64litString1529_add1709__1 = MurmurHash.hash64("hasthis");
        final long actualHash = MurmurHash.hash64("hasthis");
        final long expectedHash = -8896273065425798843L;
        Assert.assertEquals(3602824184977593628L, ((long) (o_testHash64litString1529_add1709_add2352__1)));
    }

    @Test(timeout = 10000)
    public void testHash64litString1526_add1707_add2350() throws Exception {
        long o_testHash64litString1526_add1707_add2350__1 = MurmurHash.hash64("MurmurHash.hash(Object) given a byte[] did not match MurmurHash.hash(String)");
        Assert.assertEquals(9067269536460420435L, ((long) (o_testHash64litString1526_add1707_add2350__1)));
        long o_testHash64litString1526_add1707__1 = MurmurHash.hash64("MurmurHash.hash(Object) given a byte[] did not match MurmurHash.hash(String)");
        final long actualHash = MurmurHash.hash64("MurmurHash.hash(Object) given a byte[] did not match MurmurHash.hash(String)");
        final long expectedHash = -8896273065425798843L;
        Assert.assertEquals(9067269536460420435L, ((long) (o_testHash64litString1526_add1707_add2350__1)));
    }

    @Test(timeout = 10000)
    public void testHash64litString1528_add1705_add2356() throws Exception {
        long o_testHash64litString1528_add1705_add2356__1 = MurmurHash.hash64("hashth,is");
        Assert.assertEquals(-4558760507743029536L, ((long) (o_testHash64litString1528_add1705_add2356__1)));
        long o_testHash64litString1528_add1705__1 = MurmurHash.hash64("hashth,is");
        final long actualHash = MurmurHash.hash64("hashth,is");
        final long expectedHash = -8896273065425798843L;
        Assert.assertEquals(-4558760507743029536L, ((long) (o_testHash64litString1528_add1705_add2356__1)));
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
    public void testHashlitString4_add191() throws Exception {
        int o_testHashlitString4_add191__1 = MurmurHash.hash("hashhis");
        Assert.assertEquals(-590652159, ((int) (o_testHashlitString4_add191__1)));
        final long actualHash = MurmurHash.hash("hashhis");
        final long expectedHash = -1974946086L;
        Assert.assertEquals(-590652159, ((int) (o_testHashlitString4_add191__1)));
    }

    @Test(timeout = 10000)
    public void testHashlitString1_add192() throws Exception {
        int o_testHashlitString1_add192__1 = MurmurHash.hash("MurmurHash.hash64(String) returns wrong hash value");
        Assert.assertEquals(-1053542750, ((int) (o_testHashlitString1_add192__1)));
        final long actualHash = MurmurHash.hash("MurmurHash.hash64(String) returns wrong hash value");
        final long expectedHash = -1974946086L;
        Assert.assertEquals(-1053542750, ((int) (o_testHashlitString1_add192__1)));
    }

    @Test(timeout = 10000)
    public void testHashlitString7_add193() throws Exception {
        int o_testHashlitString7_add193__1 = MurmurHash.hash("\n");
        Assert.assertEquals(-330957644, ((int) (o_testHashlitString7_add193__1)));
        final long actualHash = MurmurHash.hash("\n");
        final long expectedHash = -1974946086L;
        Assert.assertEquals(-330957644, ((int) (o_testHashlitString7_add193__1)));
    }

    @Test(timeout = 10000)
    public void testHashlitString1_add192_add899() throws Exception {
        int o_testHashlitString1_add192_add899__1 = MurmurHash.hash("MurmurHash.hash64(String) returns wrong hash value");
        Assert.assertEquals(-1053542750, ((int) (o_testHashlitString1_add192_add899__1)));
        int o_testHashlitString1_add192__1 = MurmurHash.hash("MurmurHash.hash64(String) returns wrong hash value");
        final long actualHash = MurmurHash.hash("MurmurHash.hash64(String) returns wrong hash value");
        final long expectedHash = -1974946086L;
        Assert.assertEquals(-1053542750, ((int) (o_testHashlitString1_add192_add899__1)));
    }

    @Test(timeout = 10000)
    public void testHashlitString4_add191_add911() throws Exception {
        int o_testHashlitString4_add191_add911__1 = MurmurHash.hash("hashhis");
        Assert.assertEquals(-590652159, ((int) (o_testHashlitString4_add191_add911__1)));
        int o_testHashlitString4_add191__1 = MurmurHash.hash("hashhis");
        final long actualHash = MurmurHash.hash("hashhis");
        final long expectedHash = -1974946086L;
        Assert.assertEquals(-590652159, ((int) (o_testHashlitString4_add191_add911__1)));
    }

    @Test(timeout = 10000)
    public void testHashlitString7_add193_add885() throws Exception {
        int o_testHashlitString7_add193_add885__1 = MurmurHash.hash("\n");
        Assert.assertEquals(-330957644, ((int) (o_testHashlitString7_add193_add885__1)));
        int o_testHashlitString7_add193__1 = MurmurHash.hash("\n");
        final long actualHash = MurmurHash.hash("\n");
        final long expectedHash = -1974946086L;
        Assert.assertEquals(-330957644, ((int) (o_testHashlitString7_add193_add885__1)));
    }
}

