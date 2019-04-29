package com.google.gson.internal.bind;


import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.IOException;
import junit.framework.TestCase;


@SuppressWarnings("resource")
public final class AmplJsonElementReaderTest extends TestCase {
    public void testSkipValue_add14_failAssert0_add273_failAssert0() throws IOException {
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
            junit.framework.TestCase.fail("testSkipValue_add14_failAssert0_add273 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            TestCase.assertEquals("-1", expected.getMessage());
        }
    }
}

