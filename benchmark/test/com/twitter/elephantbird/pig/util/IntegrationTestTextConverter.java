package com.twitter.elephantbird.pig.util;


import com.twitter.elephantbird.pig.store.SequenceFileStorage;
import java.io.IOException;
import org.apache.hadoop.io.Text;
import org.junit.Test;


/**
 *
 *
 * @author Andy Schlaikjer
 */
public class IntegrationTestTextConverter extends AbstractTestWritableConverter<Text, TextConverter> {
    private static final String V1 = "one, two, buckle my shoe";

    private static final String V2 = "three, four, knock on my door";

    private static final String V3 = "five, six, pickup sticks";

    private static final Text[] DATA = new Text[]{ new Text(IntegrationTestTextConverter.V1), new Text(IntegrationTestTextConverter.V2), new Text(IntegrationTestTextConverter.V3) };

    private static final String[] EXPECTED = new String[]{ IntegrationTestTextConverter.V1, IntegrationTestTextConverter.V2, IntegrationTestTextConverter.V3 };

    public IntegrationTestTextConverter() {
        super(Text.class, TextConverter.class, "", IntegrationTestTextConverter.DATA, IntegrationTestTextConverter.EXPECTED, "chararray");
    }

    @Test
    public void testDefaultCtor() throws IOException {
        pigServer.registerQuery(String.format("A = LOAD 'file:%s' USING %s();", tempFilename, SequenceFileStorage.class.getName()));
        validate(pigServer.openIterator("A"));
    }

    @Test
    public void testDefaultCtor02() throws IOException {
        pigServer.registerQuery(String.format("A = LOAD 'file:%s' USING %s('', '');", tempFilename, SequenceFileStorage.class.getName()));
        validate(pigServer.openIterator("A"));
    }
}

