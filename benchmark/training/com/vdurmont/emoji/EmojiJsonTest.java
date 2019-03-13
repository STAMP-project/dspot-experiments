package com.vdurmont.emoji;


import EmojiParser.UnicodeCandidate;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Test that checks emoji json.
 * <p>
 *     Currently contains checks for:
 *     <ul>
 *         <li>Unicode emoji presents in json</li>
 *         <li>Right fitzpatric flag for emoji</li>
 *     </ul>
 *
 * <p>
 *     The test data is taken from: <a href="http://unicode.org/Public/emoji/4.0/emoji-test.txt">Unicode test data</a>
 *     related to unicode 9.0
 */
@RunWith(Parameterized.class)
public class EmojiJsonTest {
    private static final int[] FITZPATRIC_CODEPOINTS = new int[]{ EmojiJsonTest.EmojiTestDataReader.convertFromCodepoint("1F3FB"), EmojiJsonTest.EmojiTestDataReader.convertFromCodepoint("1F3FC"), EmojiJsonTest.EmojiTestDataReader.convertFromCodepoint("1F3FD"), EmojiJsonTest.EmojiTestDataReader.convertFromCodepoint("1F3FE"), EmojiJsonTest.EmojiTestDataReader.convertFromCodepoint("1F3FF") };

    @Parameterized.Parameter
    public String emoji;

    @Test
    public void checkEmojiFitzpatricFlag() throws Exception {
        final int len = emoji.toCharArray().length;
        boolean shouldContainFitzpatric = false;
        int codepoint;
        for (int i = 0; i < len; i++) {
            codepoint = emoji.codePointAt(i);
            shouldContainFitzpatric = (Arrays.binarySearch(EmojiJsonTest.FITZPATRIC_CODEPOINTS, codepoint)) >= 0;
            if (shouldContainFitzpatric) {
                break;
            }
        }
        if (shouldContainFitzpatric) {
            EmojiParser.parseFromUnicode(emoji, new EmojiParser.EmojiTransformer() {
                public String transform(EmojiParser.UnicodeCandidate unicodeCandidate) {
                    if (unicodeCandidate.hasFitzpatrick()) {
                        Assert.assertTrue(((("Asserting emoji contains fitzpatric: " + (emoji)) + " ") + (unicodeCandidate.getEmoji())), unicodeCandidate.getEmoji().supportsFitzpatrick());
                    }
                    return "";
                }
            });
        }
    }

    private static class EmojiTestDataReader {
        static List<String> getEmojiList(final InputStream emojiFileStream) throws IOException {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(emojiFileStream));
            final List<String> result = new LinkedList<String>();
            String line = reader.readLine();
            String[] lineSplit;
            while (line != null) {
                if ((((!(line.startsWith("#"))) && (!(line.startsWith(" ")))) && (!(line.startsWith("\n")))) && ((line.length()) != 0)) {
                    lineSplit = line.split(";");
                    result.add(EmojiJsonTest.EmojiTestDataReader.convertToEmoji(lineSplit[0].trim()));
                }
                line = reader.readLine();
            } 
            return result;
        }

        private static String convertToEmoji(final String input) {
            String[] emojiCodepoints = input.split(" ");
            StringBuilder sb = new StringBuilder();
            for (String emojiCodepoint : emojiCodepoints) {
                int codePoint = EmojiJsonTest.EmojiTestDataReader.convertFromCodepoint(emojiCodepoint);
                sb.append(Character.toChars(codePoint));
            }
            return sb.toString();
        }

        static int convertFromCodepoint(String emojiCodepointAsString) {
            return Integer.parseInt(emojiCodepointAsString, 16);
        }
    }
}

