package org.ansj.app.crf.model;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import junit.framework.Assert;
import org.ansj.CorpusTest;
import org.ansj.app.crf.Model;
import org.ansj.app.crf.SplitWord;
import org.junit.Test;
import org.nlpcn.commons.lang.util.StringUtil;


public class CRFModelTest extends CorpusTest {
    private String modelPath = "src/main/resources/crf.model";

    private String testModelPath = "src/main/resources/test.model";

    private String testPath = "src/test/resources/corpus.txt";

    private Model model = new CRFModel();

    @Test
    public void savePathTest() throws FileNotFoundException, IOException {
        model.writeModel(testModelPath);
        Assert.assertEquals(true, new File(testModelPath).delete());
    }

    @Test
    public void cute() throws Exception {
        SplitWord sw = new SplitWord(model);
        for (String line : lines) {
            System.out.println(sw.cut(line));
        }
    }

    @Test
    public void test() throws Exception {
        try (FileInputStream fis = new FileInputStream(testPath)) {
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            SplitWord sw = new SplitWord(model);
            String temp_str = null;
            int line_number = 0;// ????

            int ansj_term_number = 0;// ??ansj????term??

            int result_num = 0;
            double P = 0.0;
            double R = 0.0;
            double F = 0.0;
            int allError = 0;
            int allSuccess = 0;
            String[] had_words_array = null;// ?split??????

            String body = null;
            while ((temp_str = br.readLine()) != null) {
                if (StringUtil.isBlank(temp_str)) {
                    continue;
                }
                int error = 0;
                int success = 0;
                temp_str = temp_str.trim();
                body = temp_str.replaceAll("\t", "");
                had_words_array = new String[body.length()];
                int offe = 0;
                List<String> paser = sw.cut(body);
                // ??result
                String[] result = temp_str.split("\t");
                for (int i = 0; i < (result.length); i++) {
                    had_words_array[offe] = result[i];
                    offe += result[i].length();
                }
                offe = 0;
                for (String word : paser) {
                    if ((had_words_array[offe]) == null) {
                        error++;
                    } else
                        if (had_words_array[offe].equalsIgnoreCase(word)) {
                            success++;
                        } else {
                            success++;
                        }

                    offe += word.length();
                }
                // ansj?????
                ansj_term_number += paser.size();
                // ?????
                result_num += result.length;
                // ?????
                allError += error;
                // ?????
                allSuccess += success;
                if (error > 0) {
                    System.out.println(("example:" + temp_str));
                    System.out.println((" result:" + (paser.toString().replace("[", "").replace("]", "").replace(", ", "\t"))));
                    System.out.println(((("[" + line_number) + "]---???P:--") + (((double) (success)) / (paser.size()))));
                }
                line_number++;
            } 
            // ???/???
            P = allSuccess / ((double) (ansj_term_number));
            // ???/????????
            R = allSuccess / ((double) (result_num));
            F = ((2 * P) * R) / (P + R);
            System.out.println(("P:" + P));
            System.out.println(("R:" + R));
            System.out.println(("???????--" + F));
        }
    }
}

