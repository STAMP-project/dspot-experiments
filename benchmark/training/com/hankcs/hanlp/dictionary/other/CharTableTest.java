package com.hankcs.hanlp.dictionary.other;


import junit.framework.TestCase;


// public void testConvert() throws Exception
// {
// System.out.println(CharTable.CONVERT['?']);
// System.out.println(CharTable.CONVERT['?']);
// System.out.println(CharTable.CONVERT['?']);
// System.out.println(CharTable.CONVERT['?']);
// }
// 
// public void testEnd() throws Exception
// {
// System.out.println(CharTable.CONVERT['?']);
// System.out.println(CharTable.CONVERT['?']);
// System.out.println(CharTable.CONVERT['?']);
// System.out.println(CharTable.CONVERT['?']);
// }
// 
// public void testFix() throws Exception
// {
// char[] CONVERT = CharTable.CONVERT;
// CONVERT['.'] = '.';
// CONVERT['?'] = '.';
// CONVERT['?'] = '.';
// CONVERT['?'] = '!';
// CONVERT['?'] = ',';
// CONVERT['!'] = '!';
// CONVERT['#'] = '#';
// CONVERT['&'] = '&';
// CONVERT['*'] = '*';
// CONVERT[','] = ',';
// CONVERT['/'] = '/';
// CONVERT[';'] = ';';
// CONVERT['?'] = '?';
// CONVERT['\\'] = '\\';
// CONVERT['^'] = '^';
// CONVERT['_'] = '_';
// CONVERT['`'] = '`';
// CONVERT['|'] = '|';
// CONVERT['~'] = '~';
// CONVERT['?'] = '?';
// CONVERT['?'] = '?';
// CONVERT['?'] = '?';
// CONVERT['?'] = '?';
// CONVERT['?'] = '?';
// CONVERT['?'] = '?';
// CONVERT['?'] = '?';
// CONVERT['?'] = '?';
// CONVERT['?'] = '?';
// CONVERT['?'] = '?';
// CONVERT['?'] = '?';
// CONVERT['?'] = '?';
// CONVERT['?'] = '?';
// CONVERT['?'] = '?';
// CONVERT['?'] = '?';
// CONVERT['?'] = '?';
// CONVERT['?'] = '?';
// CONVERT['?'] = '?';
// CONVERT['?'] = '?';
// CONVERT['?'] = '?';
// CONVERT['?'] = '?';
// CONVERT['?'] = '?';
// CONVERT['?'] = '?';
// CONVERT['?'] = '?';
// CONVERT['?'] = '?';
// CONVERT['?'] = '?';
// CONVERT['?'] = '?';
// CONVERT['?'] = '?';
// CONVERT['?'] = '?';
// CONVERT['?'] = '?';
// CONVERT['?'] = '?';
// CONVERT['?'] = '?';
// CONVERT['?'] = '?';
// CONVERT['?'] = '?';
// CONVERT['?'] = '?';
// CONVERT['?'] = '?';
// CONVERT['?'] = '?';
// CONVERT['?'] = '?';
// CONVERT['?'] = '?';
// CONVERT['?'] = '?';
// for (int i = 0; i < CONVERT.length; i++)
// {
// if (CONVERT[i] == '\u0000')
// {
// if (i != '\u0000') CONVERT[i] = (char) i;
// else CONVERT[i] = ' ';
// }
// }
// ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(HanLP.Config.CharTablePath));
// out.writeObject(CONVERT);
// out.close();
// }
// 
// public void testImportSingleCharFromTraditionalChineseDictionary() throws Exception
// {
// //        char[] CONVERT = CharTable.CONVERT;
// //        StringDictionary dictionary = new StringDictionary("=");
// //        dictionary.load(HanLP.Config.t2sDictionaryPath);
// //        for (Map.Entry<String, String> entry : dictionary.entrySet())
// //        {
// //            String key = entry.getKey();
// //            if (key.length() != 1) continue;
// //            String value = entry.getValue();
// //            char t = key.charAt(0);
// //            char s = value.charAt(0);
// ////            if (CONVERT[t] != s)
// ////            {
// ////                System.out.printf("%s\t%c=%c\n", entry, t, CONVERT[t]);
// ////            }
// //            CONVERT[t] = s;
// //        }
// //
// //        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(HanLP.Config.CharTablePath));
// //        out.writeObject(CONVERT);
// //        out.close();
// }
// 
// public void testDumpCharTable() throws Exception
// {
// BufferedWriter bw = IOUtil.newBufferedWriter(HanLP.Config.CharTablePath.replace(".bin.yes", ".txt"));
// char[] CONVERT = CharTable.CONVERT;
// for (int i = 0; i < CONVERT.length; i++)
// {
// if (i != CONVERT[i])
// {
// bw.write(String.format("%c=%c\n", i, CONVERT[i]));
// }
// }
// bw.close();
// }
// 
// public void testLoadCharTableFromTxt() throws Exception
// {
// //        CharTable.load(HanLP.Config.CharTablePath.replace(".bin.yes", ".txt"));
// }
public class CharTableTest extends TestCase {
    public void testNormalization() throws Exception {
        System.out.println(CharTable.convert('?'));
        TestCase.assertEquals('(', CharTable.convert('?'));
    }

    public void testNormalizeSpace() throws Exception {
        TestCase.assertEquals(CharTable.convert('\t'), ' ');
        TestCase.assertEquals(CharTable.convert('\n'), ' ');
        TestCase.assertEquals(CharTable.convert('\f'), ' ');
    }
}

