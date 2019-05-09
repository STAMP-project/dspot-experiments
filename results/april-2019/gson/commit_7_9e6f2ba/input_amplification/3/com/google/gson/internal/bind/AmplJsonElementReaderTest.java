package com.google.gson.internal.bind;


import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.IOException;
import junit.framework.TestCase;


@SuppressWarnings("resource")
public final class AmplJsonElementReaderTest extends TestCase {
    public void testSkipValue_literalMutationString3_failAssert0_add334_failAssert0() throws IOException {
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
                junit.framework.TestCase.fail("testSkipValue_literalMutationString3 should have thrown IllegalStateException");
            }
            junit.framework.TestCase.fail("testSkipValue_literalMutationString3_failAssert0_add334 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            TestCase.assertEquals("-1", expected.getMessage());
        }
    }

    public void testSkipValue_add12_failAssert0_literalMutationString117_failAssert0() throws IOException {
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
            junit.framework.TestCase.fail("testSkipValue_add12_failAssert0_literalMutationString117 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            TestCase.assertEquals("-1", expected.getMessage());
        }
    }
}

