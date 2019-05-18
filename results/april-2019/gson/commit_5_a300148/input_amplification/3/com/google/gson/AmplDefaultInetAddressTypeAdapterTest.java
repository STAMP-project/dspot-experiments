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

    public void testInetAddressSerializationAndDeserialization_literalMutationString4_failAssert0_add434_failAssert0() throws Exception {
        try {
            {
                InetAddress address = InetAddress.getByName("8..8.8");
                gson.toJson(address);
                String jsonAddress = gson.toJson(address);
                InetAddress value = gson.fromJson(jsonAddress, InetAddress.class);
                junit.framework.TestCase.fail("testInetAddressSerializationAndDeserialization_literalMutationString4 should have thrown UnknownHostException");
            }
            junit.framework.TestCase.fail("testInetAddressSerializationAndDeserialization_literalMutationString4_failAssert0_add434 should have thrown UnknownHostException");
        } catch (UnknownHostException expected) {
            TestCase.assertEquals("8..8.8", expected.getMessage());
        }
    }

    public void testInetAddressSerializationAndDeserialization_literalMutationString5_failAssert0_add431_failAssert0() throws Exception {
        try {
            {
                InetAddress address = InetAddress.getByName("8?8.8.8");
                String jsonAddress = gson.toJson(address);
                gson.fromJson(jsonAddress, InetAddress.class);
                InetAddress value = gson.fromJson(jsonAddress, InetAddress.class);
                junit.framework.TestCase.fail("testInetAddressSerializationAndDeserialization_literalMutationString5 should have thrown UnknownHostException");
            }
            junit.framework.TestCase.fail("testInetAddressSerializationAndDeserialization_literalMutationString5_failAssert0_add431 should have thrown UnknownHostException");
        } catch (UnknownHostException expected) {
            TestCase.assertEquals("8?8.8.8", expected.getMessage());
        }
    }

    public void testInetAddressSerializationAndDeserialization_literalMutationString3_failAssert0_add420_failAssert0null1755_failAssert0() throws Exception {
        try {
            {
                {
                    InetAddress address = InetAddress.getByName("8.8.8(.8");
                    String jsonAddress = gson.toJson(null);
                    InetAddress value = gson.fromJson(jsonAddress, InetAddress.class);
                    junit.framework.TestCase.fail("testInetAddressSerializationAndDeserialization_literalMutationString3 should have thrown UnknownHostException");
                }
                junit.framework.TestCase.fail("testInetAddressSerializationAndDeserialization_literalMutationString3_failAssert0_add420 should have thrown UnknownHostException");
            }
            junit.framework.TestCase.fail("testInetAddressSerializationAndDeserialization_literalMutationString3_failAssert0_add420_failAssert0null1755 should have thrown UnknownHostException");
        } catch (UnknownHostException expected) {
            TestCase.assertEquals("8.8.8(.8", expected.getMessage());
        }
    }

    public void testInetAddressSerializationAndDeserialization_literalMutationString1_literalMutationString73_failAssert0_add1373_failAssert0() throws Exception {
        try {
            {
                InetAddress address = InetAddress.getByName("  ");
                gson.toJson(address);
                String jsonAddress = gson.toJson(address);
                InetAddress value = this.gson.fromJson(jsonAddress, InetAddress.class);
                junit.framework.TestCase.fail("testInetAddressSerializationAndDeserialization_literalMutationString1_literalMutationString73 should have thrown UnknownHostException");
            }
            junit.framework.TestCase.fail("testInetAddressSerializationAndDeserialization_literalMutationString1_literalMutationString73_failAssert0_add1373 should have thrown UnknownHostException");
        } catch (UnknownHostException expected) {
            TestCase.assertEquals("  ", expected.getMessage());
        }
    }

    public void testInetAddressSerializationAndDeserialization_literalMutationString3_failAssert0_add418_failAssert0() throws Exception {
        try {
            {
                InetAddress address = InetAddress.getByName("8.8.8(.8");
                gson.toJson(address);
                String jsonAddress = gson.toJson(address);
                InetAddress value = gson.fromJson(jsonAddress, InetAddress.class);
                junit.framework.TestCase.fail("testInetAddressSerializationAndDeserialization_literalMutationString3 should have thrown UnknownHostException");
            }
            junit.framework.TestCase.fail("testInetAddressSerializationAndDeserialization_literalMutationString3_failAssert0_add418 should have thrown UnknownHostException");
        } catch (UnknownHostException expected) {
            TestCase.assertEquals("8.8.8(.8", expected.getMessage());
        }
    }

    public void testInetAddressSerializationAndDeserialization_literalMutationString6_failAssert0_add428_failAssert0_add1426_failAssert0() throws Exception {
        try {
            {
                {
                    InetAddress.getByName("a[3{<U]");
                    InetAddress address = InetAddress.getByName("a[3{<U]");
                    String jsonAddress = gson.toJson(address);
                    InetAddress value = gson.fromJson(jsonAddress, InetAddress.class);
                    junit.framework.TestCase.fail("testInetAddressSerializationAndDeserialization_literalMutationString6 should have thrown UnknownHostException");
                }
                junit.framework.TestCase.fail("testInetAddressSerializationAndDeserialization_literalMutationString6_failAssert0_add428 should have thrown UnknownHostException");
            }
            junit.framework.TestCase.fail("testInetAddressSerializationAndDeserialization_literalMutationString6_failAssert0_add428_failAssert0_add1426 should have thrown UnknownHostException");
        } catch (UnknownHostException expected) {
            TestCase.assertEquals("a[3{<U]", expected.getMessage());
        }
    }
}

