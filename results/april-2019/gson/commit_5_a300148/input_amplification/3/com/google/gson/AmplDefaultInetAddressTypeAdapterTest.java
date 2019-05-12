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

    public void testInetAddressSerializationAndDeserialization_literalMutationString2_failAssert0_literalMutationString97_failAssert0_add1589_failAssert0() throws Exception {
        try {
            {
                {
                    InetAddress address = InetAddress.getByName("|");
                    gson.toJson(address);
                    String jsonAddress = gson.toJson(address);
                    InetAddress value = gson.fromJson(jsonAddress, InetAddress.class);
                    junit.framework.TestCase.fail("testInetAddressSerializationAndDeserialization_literalMutationString2 should have thrown UnknownHostException");
                }
                junit.framework.TestCase.fail("testInetAddressSerializationAndDeserialization_literalMutationString2_failAssert0_literalMutationString97 should have thrown UnknownHostException");
            }
            junit.framework.TestCase.fail("testInetAddressSerializationAndDeserialization_literalMutationString2_failAssert0_literalMutationString97_failAssert0_add1589 should have thrown UnknownHostException");
        } catch (UnknownHostException expected) {
            TestCase.assertEquals("|", expected.getMessage());
        }
    }

    public void testInetAddressSerializationAndDeserialization_add7_literalMutationString63_failAssert0_literalMutationString1168_failAssert0() throws Exception {
        try {
            {
                InetAddress o_testInetAddressSerializationAndDeserialization_add7__1 = InetAddress.getByName("  ");
                InetAddress address = InetAddress.getByName("  ");
                String jsonAddress = gson.toJson(address);
                InetAddress value = this.gson.fromJson(jsonAddress, InetAddress.class);
                junit.framework.TestCase.fail("testInetAddressSerializationAndDeserialization_add7_literalMutationString63 should have thrown UnknownHostException");
            }
            junit.framework.TestCase.fail("testInetAddressSerializationAndDeserialization_add7_literalMutationString63_failAssert0_literalMutationString1168 should have thrown UnknownHostException");
        } catch (UnknownHostException expected) {
            TestCase.assertEquals("  ", expected.getMessage());
        }
    }

    public void testInetAddressSerializationAndDeserialization_add7_literalMutationString66_failAssert0_add1502_failAssert0() throws Exception {
        try {
            {
                InetAddress o_testInetAddressSerializationAndDeserialization_add7__1 = InetAddress.getByName("8.8.8.8");
                InetAddress.getByName("8..8.8");
                InetAddress address = InetAddress.getByName("8..8.8");
                String jsonAddress = gson.toJson(address);
                InetAddress value = this.gson.fromJson(jsonAddress, InetAddress.class);
                junit.framework.TestCase.fail("testInetAddressSerializationAndDeserialization_add7_literalMutationString66 should have thrown UnknownHostException");
            }
            junit.framework.TestCase.fail("testInetAddressSerializationAndDeserialization_add7_literalMutationString66_failAssert0_add1502 should have thrown UnknownHostException");
        } catch (UnknownHostException expected) {
            TestCase.assertEquals("8..8.8", expected.getMessage());
        }
    }

    public void testInetAddressSerializationAndDeserialization_add7_literalMutationString63_failAssert0() throws Exception {
        try {
            InetAddress o_testInetAddressSerializationAndDeserialization_add7__1 = InetAddress.getByName("8.8.8.8");
            InetAddress address = InetAddress.getByName("  ");
            String jsonAddress = gson.toJson(address);
            InetAddress value = this.gson.fromJson(jsonAddress, InetAddress.class);
            junit.framework.TestCase.fail("testInetAddressSerializationAndDeserialization_add7_literalMutationString63 should have thrown UnknownHostException");
        } catch (UnknownHostException expected) {
            TestCase.assertEquals("  ", expected.getMessage());
        }
    }

    public void testInetAddressSerializationAndDeserialization_literalMutationString4_failAssert0null547_failAssert0_add1486_failAssert0() throws Exception {
        try {
            {
                {
                    InetAddress address = InetAddress.getByName("8.8.V.8");
                    String jsonAddress = gson.toJson(null);
                    InetAddress value = gson.fromJson(jsonAddress, InetAddress.class);
                    junit.framework.TestCase.fail("testInetAddressSerializationAndDeserialization_literalMutationString4 should have thrown UnknownHostException");
                }
                junit.framework.TestCase.fail("testInetAddressSerializationAndDeserialization_literalMutationString4_failAssert0null547 should have thrown UnknownHostException");
            }
            junit.framework.TestCase.fail("testInetAddressSerializationAndDeserialization_literalMutationString4_failAssert0null547_failAssert0_add1486 should have thrown UnknownHostException");
        } catch (UnknownHostException expected) {
            TestCase.assertEquals("8.8.V.8", expected.getMessage());
        }
    }

    public void testInetAddressSerializationAndDeserialization_literalMutationString3_failAssert0_add519_failAssert0() throws Exception {
        try {
            {
                InetAddress address = InetAddress.getByName(">-um>*a");
                String jsonAddress = gson.toJson(address);
                gson.fromJson(jsonAddress, InetAddress.class);
                InetAddress value = gson.fromJson(jsonAddress, InetAddress.class);
                junit.framework.TestCase.fail("testInetAddressSerializationAndDeserialization_literalMutationString3 should have thrown UnknownHostException");
            }
            junit.framework.TestCase.fail("testInetAddressSerializationAndDeserialization_literalMutationString3_failAssert0_add519 should have thrown UnknownHostException");
        } catch (UnknownHostException expected) {
            TestCase.assertEquals(">-um>*a", expected.getMessage());
        }
    }
}

