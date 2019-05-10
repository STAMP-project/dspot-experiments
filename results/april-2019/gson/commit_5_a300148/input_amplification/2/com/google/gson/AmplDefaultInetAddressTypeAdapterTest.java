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

    public void testInetAddressSerializationAndDeserialization_add8_literalMutationString64_failAssert0() throws Exception {
        try {
            InetAddress address = InetAddress.getByName("  ");
            String o_testInetAddressSerializationAndDeserialization_add8__3 = gson.toJson(address);
            String jsonAddress = gson.toJson(address);
            InetAddress value = this.gson.fromJson(jsonAddress, InetAddress.class);
            junit.framework.TestCase.fail("testInetAddressSerializationAndDeserialization_add8_literalMutationString64 should have thrown UnknownHostException");
        } catch (UnknownHostException expected) {
            TestCase.assertEquals("  ", expected.getMessage());
        }
    }

    public void testInetAddressSerializationAndDeserialization_literalMutationString4_failAssert0_add480_failAssert0() throws Exception {
        try {
            {
                InetAddress address = InetAddress.getByName("8.8.n.8");
                String jsonAddress = gson.toJson(address);
                gson.fromJson(jsonAddress, InetAddress.class);
                InetAddress value = gson.fromJson(jsonAddress, InetAddress.class);
                junit.framework.TestCase.fail("testInetAddressSerializationAndDeserialization_literalMutationString4 should have thrown UnknownHostException");
            }
            junit.framework.TestCase.fail("testInetAddressSerializationAndDeserialization_literalMutationString4_failAssert0_add480 should have thrown UnknownHostException");
        } catch (UnknownHostException expected) {
            TestCase.assertEquals("8.8.n.8", expected.getMessage());
        }
    }

    public void testInetAddressSerializationAndDeserialization_literalMutationString3_failAssert0_add485_failAssert0() throws Exception {
        try {
            {
                InetAddress address = InetAddress.getByName("8*.8.8.8");
                String jsonAddress = gson.toJson(address);
                InetAddress value = gson.fromJson(jsonAddress, InetAddress.class);
                junit.framework.TestCase.fail("testInetAddressSerializationAndDeserialization_literalMutationString3 should have thrown UnknownHostException");
            }
            junit.framework.TestCase.fail("testInetAddressSerializationAndDeserialization_literalMutationString3_failAssert0_add485 should have thrown UnknownHostException");
        } catch (UnknownHostException expected) {
            TestCase.assertEquals("8*.8.8.8", expected.getMessage());
        }
    }

    public void testInetAddressSerializationAndDeserialization_literalMutationString5_failAssert0_add487_failAssert0() throws Exception {
        try {
            {
                InetAddress address = InetAddress.getByName("v$>}tzb");
                gson.toJson(address);
                String jsonAddress = gson.toJson(address);
                InetAddress value = gson.fromJson(jsonAddress, InetAddress.class);
                junit.framework.TestCase.fail("testInetAddressSerializationAndDeserialization_literalMutationString5 should have thrown UnknownHostException");
            }
            junit.framework.TestCase.fail("testInetAddressSerializationAndDeserialization_literalMutationString5_failAssert0_add487 should have thrown UnknownHostException");
        } catch (UnknownHostException expected) {
            TestCase.assertEquals("v$>}tzb", expected.getMessage());
        }
    }
}

