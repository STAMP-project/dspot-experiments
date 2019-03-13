/**
 * Copyright 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.zxing.common;


import com.google.zxing.MultiFormatReader;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.junit.Assert;
import org.junit.Test;


/**
 * This abstract class looks for negative results, i.e. it only allows a certain number of false
 * positives in images which should not decode. This helps ensure that we are not too lenient.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public abstract class AbstractNegativeBlackBoxTestCase extends AbstractBlackBoxTestCase {
    private static final Logger log = Logger.getLogger(AbstractNegativeBlackBoxTestCase.class.getSimpleName());

    private final List<AbstractNegativeBlackBoxTestCase.TestResult> testResults;

    private static final class TestResult {
        private final int falsePositivesAllowed;

        private final float rotation;

        TestResult(int falsePositivesAllowed, float rotation) {
            this.falsePositivesAllowed = falsePositivesAllowed;
            this.rotation = rotation;
        }

        int getFalsePositivesAllowed() {
            return falsePositivesAllowed;
        }

        float getRotation() {
            return rotation;
        }
    }

    // Use the multiformat reader to evaluate all decoders in the system.
    protected AbstractNegativeBlackBoxTestCase(String testBasePathSuffix) {
        super(testBasePathSuffix, new MultiFormatReader(), null);
        testResults = new ArrayList<>();
    }

    @Override
    @Test
    public void testBlackBox() throws IOException {
        Assert.assertFalse(testResults.isEmpty());
        List<Path> imageFiles = getImageFiles();
        int[] falsePositives = new int[testResults.size()];
        for (Path testImage : imageFiles) {
            AbstractNegativeBlackBoxTestCase.log.info(String.format("Starting %s", testImage));
            BufferedImage image = ImageIO.read(testImage.toFile());
            if (image == null) {
                throw new IOException(("Could not read image: " + testImage));
            }
            for (int x = 0; x < (testResults.size()); x++) {
                AbstractNegativeBlackBoxTestCase.TestResult testResult = testResults.get(x);
                if (!(checkForFalsePositives(image, testResult.getRotation()))) {
                    (falsePositives[x])++;
                }
            }
        }
        int totalFalsePositives = 0;
        int totalAllowed = 0;
        for (int x = 0; x < (testResults.size()); x++) {
            AbstractNegativeBlackBoxTestCase.TestResult testResult = testResults.get(x);
            totalFalsePositives += falsePositives[x];
            totalAllowed += testResult.getFalsePositivesAllowed();
        }
        if (totalFalsePositives < totalAllowed) {
            AbstractNegativeBlackBoxTestCase.log.warning(String.format("+++ Test too lax by %d images", (totalAllowed - totalFalsePositives)));
        } else
            if (totalFalsePositives > totalAllowed) {
                AbstractNegativeBlackBoxTestCase.log.warning(String.format("--- Test failed by %d images", (totalFalsePositives - totalAllowed)));
            }

        for (int x = 0; x < (testResults.size()); x++) {
            AbstractNegativeBlackBoxTestCase.TestResult testResult = testResults.get(x);
            AbstractNegativeBlackBoxTestCase.log.info(String.format("Rotation %d degrees: %d of %d images were false positives (%d allowed)", ((int) (testResult.getRotation())), falsePositives[x], imageFiles.size(), testResult.getFalsePositivesAllowed()));
            Assert.assertTrue((("Rotation " + (testResult.getRotation())) + " degrees: Too many false positives found"), ((falsePositives[x]) <= (testResult.getFalsePositivesAllowed())));
        }
    }
}

