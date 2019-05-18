package com.google.gson;


import java.net.InetAddress;
import java.net.UnknownHostException;
import junit.framework.TestCase;


public class AmplDefaultInetAddressTypeAdapterTest extends TestCase {
    private Gson gson;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        gson = new Gson();
    }

    public void testInetAddressSerializationAndDeserialization_literalMutationString5_failAssert0_literalMutationString76_failAssert0() throws Exception {
        try {
            {
                InetAddress address = InetAddress.getByName("  ");
                String jsonAddress = gson.toJson(address);
                InetAddress value = gson.fromJson(jsonAddress, InetAddress.class);
                junit.framework.TestCase.fail("testInetAddressSerializationAndDeserialization_literalMutationString5 should have thrown UnknownHostException");
            }
            junit.framework.TestCase.fail("testInetAddressSerializationAndDeserialization_literalMutationString5_failAssert0_literalMutationString76 should have thrown UnknownHostException");
        } catch (UnknownHostException expected) {
            TestCase.assertEquals("  ", expected.getMessage());
        }
    }

    public void testInetAddressSerializationAndDeserialization_literalMutationString3_failAssert0_add431_failAssert0() throws Exception {
        try {
            {
                InetAddress address = InetAddress.getByName("T&$K;Uc");
                String jsonAddress = gson.toJson(address);
                gson.fromJson(jsonAddress, InetAddress.class);
                InetAddress value = gson.fromJson(jsonAddress, InetAddress.class);
                junit.framework.TestCase.fail("testInetAddressSerializationAndDeserialization_literalMutationString3 should have thrown UnknownHostException");
            }
            junit.framework.TestCase.fail("testInetAddressSerializationAndDeserialization_literalMutationString3_failAssert0_add431 should have thrown UnknownHostException");
        } catch (UnknownHostException expected) {
            TestCase.assertEquals("T&$K;Uc", expected.getMessage());
        }
    }

    public void testInetAddressSerializationAndDeserialization_literalMutationString4_failAssert0_add424_failAssert0() throws Exception {
        try {
            {
                InetAddress address = InetAddress.getByName("8.8.Z8.8");
                String jsonAddress = gson.toJson(address);
                InetAddress value = gson.fromJson(jsonAddress, InetAddress.class);
                junit.framework.TestCase.fail("testInetAddressSerializationAndDeserialization_literalMutationString4 should have thrown UnknownHostException");
            }
            junit.framework.TestCase.fail("testInetAddressSerializationAndDeserialization_literalMutationString4_failAssert0_add424 should have thrown UnknownHostException");
        } catch (UnknownHostException expected) {
            TestCase.assertEquals("8.8.Z8.8", expected.getMessage());
        }
    }

    public void testInetAddressSerializationAndDeserialization_literalMutationString5_failAssert0_add418_failAssert0() throws Exception {
        try {
            {
                InetAddress address = InetAddress.getByName("8..8.8");
                gson.toJson(address);
                String jsonAddress = gson.toJson(address);
                InetAddress value = gson.fromJson(jsonAddress, InetAddress.class);
                junit.framework.TestCase.fail("testInetAddressSerializationAndDeserialization_literalMutationString5 should have thrown UnknownHostException");
            }
            junit.framework.TestCase.fail("testInetAddressSerializationAndDeserialization_literalMutationString5_failAssert0_add418 should have thrown UnknownHostException");
        } catch (UnknownHostException expected) {
            TestCase.assertEquals("8..8.8", expected.getMessage());
        }
    }
}

