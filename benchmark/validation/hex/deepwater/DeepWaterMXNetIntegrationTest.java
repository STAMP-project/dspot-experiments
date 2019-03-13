package hex.deepwater;


import deepwater.backends.BackendModel;
import deepwater.backends.BackendParams;
import deepwater.backends.RuntimeOptions;
import deepwater.datasets.ImageDataSet;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.junit.Assert;
import org.junit.Test;
import water.fvec.Frame;
import water.parser.BufferedString;
import water.util.FileUtils;
import water.util.StringUtils;


public class DeepWaterMXNetIntegrationTest extends DeepWaterAbstractIntegrationTest {
    // This test has nothing to do with H2O - Pure integration test of deepwater/backends/mxnet
    @Test
    public void inceptionPredictionMX() throws IOException {
        for (boolean gpu : new boolean[]{ true, false }) {
            // Set model parameters
            int w = 224;
            int h = 224;
            int channels = 3;
            int nclasses = 1000;
            ImageDataSet id = new ImageDataSet(w, h, channels, nclasses);
            RuntimeOptions opts = new RuntimeOptions();
            opts.setSeed(1234);
            opts.setUseGPU(gpu);
            BackendParams bparm = new BackendParams();
            bparm.set("mini_batch_size", 1);
            // Load the model
            String path = "deepwater/backends/mxnet/models/Inception/";
            BackendModel _model = backend.buildNet(id, opts, bparm, nclasses, StringUtils.expandPath(DeepWaterMXNetIntegrationTest.extractFile(path, "Inception_BN-symbol.json")));
            backend.loadParam(_model, StringUtils.expandPath(DeepWaterMXNetIntegrationTest.extractFile(path, "Inception_BN-0039.params")));
            Frame labels = parse_test_file(DeepWaterMXNetIntegrationTest.extractFile(path, "synset.txt"));
            float[] mean = backend.loadMeanImage(_model, DeepWaterMXNetIntegrationTest.extractFile(path, "mean_224.nd"));
            // Turn the image into a vector of the correct size
            File imgFile = FileUtils.getFile("smalldata/deepwater/imagenet/test2.jpg");
            BufferedImage img = ImageIO.read(imgFile);
            BufferedImage scaledImg = new BufferedImage(w, h, img.getType());
            Graphics2D g2d = scaledImg.createGraphics();
            g2d.drawImage(img, 0, 0, w, h, null);
            g2d.dispose();
            float[] pixels = new float[(w * h) * channels];
            int r_idx = 0;
            int g_idx = r_idx + (w * h);
            int b_idx = g_idx + (w * h);
            for (int i = 0; i < h; i++) {
                for (int j = 0; j < w; j++) {
                    Color mycolor = new Color(scaledImg.getRGB(j, i));
                    int red = mycolor.getRed();
                    int green = mycolor.getGreen();
                    int blue = mycolor.getBlue();
                    pixels[r_idx] = red - (mean[r_idx]);
                    r_idx++;
                    pixels[g_idx] = green - (mean[g_idx]);
                    g_idx++;
                    pixels[b_idx] = blue - (mean[b_idx]);
                    b_idx++;
                }
            }
            float[] preds = backend.predict(_model, pixels);
            int K = 5;
            int[] topK = new int[K];
            for (int i = 0; i < (preds.length); i++) {
                for (int j = 0; j < K; j++) {
                    if ((preds[i]) > (preds[topK[j]])) {
                        topK[j] = i;
                        break;
                    }
                }
            }
            // Display the top 5 predictions
            StringBuilder sb = new StringBuilder();
            sb.append((("\nTop " + K) + " predictions:\n"));
            BufferedString str = new BufferedString();
            for (int j = 0; j < K; j++) {
                String label = labels.anyVec().atStr(str, topK[j]).toString();
                sb.append(((((" Score: " + (String.format("%.4f", preds[topK[j]]))) + "\t") + label) + "\n"));
            }
            System.out.println((("\n\n" + (sb.toString())) + "\n\n"));
            Assert.assertTrue("Illegal predictions!", sb.toString().substring(40, 60).contains("Pembroke"));
            labels.remove();
        }
    }
}

