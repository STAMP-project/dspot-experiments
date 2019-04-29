package com.google.gson.internal.bind;


import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.IOException;
import junit.framework.TestCase;


@SuppressWarnings("resource")
public final class AmplJsonElementReaderTest extends TestCase {
    public void testSkipValue_add12_failAssert0_literalMutationString126_failAssert0() throws IOException {
        try {
            {
                JsonElement element = new JsonParser().parse("[NaN,-Infinity,Infinity]");
                JsonTreeReader reader = new JsonTreeReader(element);
                reader.beginArray();
                reader.nextString();
                reader.skipValue();
                reader.nextString();
                reader.skipValue();
                reader.skipValue();
                reader.nextString();
                reader.skipValue();
                reader.endArray();
                junit.framework.TestCase.fail("testSkipValue_add12 should have thrown IllegalStateException");
            }
            junit.framework.TestCase.fail("testSkipValue_add12_failAssert0_literalMutationString126 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            TestCase.assertEquals("-1", expected.getMessage());
        }
    }

    public void testSkipValue_literalMutationString6_literalMutationString66_failAssert0_add3300_failAssert0() throws IOException {
        try {
            {
                JsonElement element = new JsonParser().parse("[NaN,-Infinity,Infinity]");
                JsonTreeReader reader = new JsonTreeReader(element);
                reader.beginArray();
                String o_testSkipValue_literalMutationString6__7 = reader.nextString();
                reader.skipValue();
                String o_testSkipValue_literalMutationString6__9 = reader.nextString();
                reader.skipValue();
                reader.skipValue();
                String o_testSkipValue_literalMutationString6__11 = reader.nextString();
                reader.skipValue();
                reader.endArray();
                junit.framework.TestCase.fail("testSkipValue_literalMutationString6_literalMutationString66 should have thrown IllegalStateException");
            }
            junit.framework.TestCase.fail("testSkipValue_literalMutationString6_literalMutationString66_failAssert0_add3300 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            TestCase.assertEquals("-1", expected.getMessage());
        }
    }

    public void testSkipValue_add14_failAssert0_add272_failAssert0() throws IOException {
        try {
            {
                JsonElement element = new JsonParser().parse("[\"A\",{\"B\":[[]]},\"C\",[[]],\"D\",null]");
                JsonTreeReader reader = new JsonTreeReader(element);
                reader.beginArray();
                reader.nextString();
                reader.skipValue();
                reader.nextString();
                reader.skipValue();
                reader.nextString();
                reader.skipValue();
                reader.skipValue();
                reader.skipValue();
                reader.endArray();
                junit.framework.TestCase.fail("testSkipValue_add14 should have thrown IllegalStateException");
            }
            junit.framework.TestCase.fail("testSkipValue_add14_failAssert0_add272 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            TestCase.assertEquals("-1", expected.getMessage());
        }
    }

    public void testSkipValue_remove20_failAssert0_add354_failAssert0_literalMutationString1776_failAssert0() throws IOException {
        try {
            {
                {
                    JsonElement element = new JsonParser().parse("[NaN,-Infinity,Infinity]");
                    JsonTreeReader reader = new JsonTreeReader(element);
                    reader.beginArray();
                    reader.nextString();
                    reader.skipValue();
                    reader.skipValue();
                    reader.skipValue();
                    reader.skipValue();
                    reader.nextString();
                    reader.nextString();
                    junit.framework.TestCase.fail("testSkipValue_remove20 should have thrown IllegalStateException");
                }
                junit.framework.TestCase.fail("testSkipValue_remove20_failAssert0_add354 should have thrown IllegalStateException");
            }
            junit.framework.TestCase.fail("testSkipValue_remove20_failAssert0_add354_failAssert0_literalMutationString1776 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            TestCase.assertEquals("-1", expected.getMessage());
        }
    }
}

