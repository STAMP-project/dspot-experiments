package net.librec;


import Configuration.Resource;
import Measure.MeasureValue;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;
import net.librec.common.LibrecException;
import net.librec.conf.Configuration;
import net.librec.eval.Measure;
import net.librec.job.RecommenderJob;
import net.librec.recommender.Recommender;
import net.librec.util.FileUtil;
import net.librec.util.ReflectionUtil;
import org.junit.Test;


public class TestResultGenerator extends BaseTestCase {
    /**
     * test the all the recommenders
     *
     * @throws ClassNotFoundException
     * 		
     * @throws LibrecException
     * 		
     * @throws IOException
     * 		
     */
    @Test
    public void testRecommender() throws IOException, ClassNotFoundException, LibrecException {
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm").format(Calendar.getInstance().getTime());
        List<String> filePaths = TestResultGenerator.getResourceFolderFiles("rec/");
        List<String> recNames = new ArrayList<>();
        List<String> timeList = new ArrayList<>();
        List<Map<Measure.MeasureValue, Double>> evalMaps = new ArrayList<>();
        System.out.println(filePaths);
        DecimalFormat formatter = new DecimalFormat("#0.0000");
        String outputPath = "../result/rec-test-result-3.0-" + timeStamp;
        try {
            FileUtil.writeString(outputPath, (("librec-3.0.0 test at: " + timeStamp) + "\n"), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (String filePath : filePaths) {
            Recommender rec = null;
            Configuration conf = new Configuration();
            filePath = filePath.split("classes")[1];
            Configuration.Resource resource = new Configuration.Resource(filePath);
            conf.addResource(resource);
            try {
                rec = ReflectionUtil.newInstance(((Class<Recommender>) (getRecommenderClass(conf))), conf);
            } catch (Exception e) {
                TestResultGenerator.writeError(outputPath, filePath, e.getMessage());
                e.printStackTrace();
            }
            if (rec != null) {
                RecommenderJob job = new RecommenderJob(conf);
                try {
                    long startTime = System.nanoTime();
                    job.runJob();
                    long endTime = System.nanoTime();
                    String time = formatter.format(((endTime - startTime) / 1000000.0));
                    timeList.add(time);
                    Map<Measure.MeasureValue, Double> evalMap = job.getEvaluatedMap();
                    evalMaps.add(evalMap);
                    recNames.add(filePath);
                    TestResultGenerator.writeResult(outputPath, filePath, time, evalMap, formatter);
                } catch (Exception e) {
                    TestResultGenerator.writeError(outputPath, filePath, e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * List the all recommenders' info.
     *
     * @throws ClassNotFoundException
     * 		
     * @throws LibrecException
     * 		
     * @throws IOException
     * 		
     */
    @Test
    public void listRecommenderInfo() throws IOException, ClassNotFoundException, LibrecException {
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm").format(Calendar.getInstance().getTime());
        List<String> filePaths = TestResultGenerator.getResourceFolderFiles("rec/");
        List<String> recNames = new ArrayList<>();
        List<String> timeList = new ArrayList<>();
        List<Map<Measure.MeasureValue, Double>> evalMaps = new ArrayList<>();
        System.out.println(filePaths);
        DecimalFormat formatter = new DecimalFormat("#0.0000");
        String outputPath = "../result/rec-test-result-3.0-" + timeStamp;
        try {
            FileUtil.writeString(outputPath, (("librec-3.0.0 test at: " + timeStamp) + "\n"), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (String filePath : filePaths) {
            Recommender rec = null;
            Configuration conf = new Configuration();
            filePath = filePath.split("classes")[1];
            Configuration.Resource resource = new Configuration.Resource(filePath);
            conf.addResource(resource);
            try {
                rec = ReflectionUtil.newInstance(((Class<Recommender>) (getRecommenderClass(conf))), conf);
            } catch (Exception e) {
                TestResultGenerator.writeError(outputPath, filePath, e.getMessage());
                e.printStackTrace();
            }
            if (rec != null) {
                String dirRec = rec.getClass().getName().replaceFirst("net.librec.recommender.", "");
                String recName = dirRec.split("\\.")[((dirRec.split("\\.").length) - 1)];
                String dirPath = dirRec.replaceAll(("\\." + recName), "");
                String superName = rec.getClass().getSuperclass().getName().replaceFirst("net.librec.recommender.", "");
                String shortName = conf.get("rec.recommender.class");
                StringBuilder sb = new StringBuilder();
                sb.append((((((dirPath + " |   ") + shortName) + "   |   ") + recName) + "|"));
                try {
                    FileUtil.writeString(outputPath, sb.toString(), true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * List the all recommenders' conf.
     *
     * @throws ClassNotFoundException
     * 		
     * @throws LibrecException
     * 		
     * @throws IOException
     * 		
     */
    @Test
    public void listRecommenderConf() throws IOException, ClassNotFoundException, LibrecException {
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm").format(Calendar.getInstance().getTime());
        List<String> filePaths = TestResultGenerator.getResourceFolderFiles("rec/");
        List<String> recNames = new ArrayList<>();
        List<String> timeList = new ArrayList<>();
        List<Map<Measure.MeasureValue, Double>> evalMaps = new ArrayList<>();
        System.out.println(filePaths);
        DecimalFormat formatter = new DecimalFormat("#0.0000");
        String outputPath = "../result/rec-test-result-3.0-" + timeStamp;
        try {
            FileUtil.writeString(outputPath, (("librec-3.0.0 test at: " + timeStamp) + "\n"), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (String filePath : filePaths) {
            Recommender rec = null;
            Configuration conf = new Configuration();
            String rawFilePath = new String(filePath);
            filePath = filePath.split("classes")[1];
            Configuration.Resource resource = new Configuration.Resource(filePath);
            conf.addResource(resource);
            try {
                rec = ReflectionUtil.newInstance(((Class<Recommender>) (getRecommenderClass(conf))), conf);
            } catch (Exception e) {
                TestResultGenerator.writeError(outputPath, filePath, e.getMessage());
                e.printStackTrace();
            }
            if (rec != null) {
                String dirRec = rec.getClass().getName().replaceFirst("net.librec.recommender.", "");
                String recName = dirRec.split("\\.")[((dirRec.split("\\.").length) - 1)];
                StringBuilder sb = new StringBuilder();
                sb.append((("#####" + recName) + "\n"));
                sb.append("```\n");
                try (Stream<String> stream = Files.lines(Paths.get(rawFilePath), StandardCharsets.UTF_8)) {
                    stream.forEach(( s) -> sb.append(s).append("\n"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                sb.append("```\n");
                try {
                    FileUtil.writeString(outputPath, sb.toString(), true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

