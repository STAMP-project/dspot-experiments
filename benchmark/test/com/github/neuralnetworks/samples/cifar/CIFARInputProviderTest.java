package com.github.neuralnetworks.samples.cifar;


import CIFARInputProvider.CIFAR10TrainingInputProvider;
import com.github.neuralnetworks.tensor.TensorFactory;
import com.github.neuralnetworks.training.TrainingInputDataImpl;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


/**
 * Created by akohl on 08.12.2014.
 */
// since test data (cifar-10) not yet provided
@Ignore
public class CIFARInputProviderTest {
    @Test
    public void testCIFAR10TrainingInputProvider_RGB_Input() {
        CIFARInputProvider.CIFAR10TrainingInputProvider cifarTrainInputProvider = new CIFARInputProvider.CIFAR10TrainingInputProvider("cifar-10-batches-bin");
        int imageCount = 5;
        cifarTrainInputProvider.setup("cifar-10-batches-bin", 1, imageCount, 3072, "data_batch_1.bin");
        cifarTrainInputProvider.getProperties().setGroupByChannel(false);
        cifarTrainInputProvider.getProperties().setScaleColors(false);
        cifarTrainInputProvider.getProperties().setSubtractMean(false);
        cifarTrainInputProvider.getProperties().setParallelPreprocessing(false);
        cifarTrainInputProvider.reset();
        // read 5 images from binary file.
        TrainingInputDataImpl[] ti = new TrainingInputDataImpl[imageCount];
        for (int i = 0; i < imageCount; i++) {
            ti[i] = new TrainingInputDataImpl(TensorFactory.tensor(1, cifarTrainInputProvider.getInputDimensions()), TensorFactory.tensor(1, cifarTrainInputProvider.getTargetDimensions()));
            cifarTrainInputProvider.populateNext(ti[i]);
        }
        RandomAccessFile data_batch_1 = null;
        byte[] categories = new byte[imageCount];
        byte[][] pixels = new byte[imageCount][3072];
        try {
            data_batch_1 = new RandomAccessFile("cifar-10-batches-bin/data_batch_1.bin", "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(data_batch_1);
        try {
            for (int i = 0; i < imageCount; i++) {
                categories[i] = data_batch_1.readByte();
                byte[] pixelTemp = new byte[3072];
                data_batch_1.readFully(pixelTemp);
                for (int j = 0; j < 1024; j++) {
                    pixels[i][(j * 3)] = pixelTemp[j];
                    pixels[i][((j * 3) + 1)] = pixelTemp[(1024 + j)];
                    pixels[i][((j * 3) + 2)] = pixelTemp[((1024 * 2) + j)];
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < imageCount; i++) {
            // check input (pixel values)
            float[] inputImporter = ti[i].getInput().getElements();
            byte[] inputCompare = pixels[i];
            Assert.assertEquals(inputImporter.length, inputCompare.length);
            for (int j = 0; j < (inputImporter.length); j++) {
                Assert.assertEquals(inputImporter[j], ((inputCompare[j]) & 255), 1.0E-5F);
            }
            float[] outputImporter = ti[i].getTarget().getElements();
            byte outputIndex = 0;
            for (byte j = 0; j < (outputImporter.length); j++) {
                if ((outputImporter[j]) > 0.0F) {
                    outputIndex = j;
                    break;
                }
            }
            Assert.assertEquals(categories[i], outputIndex);
        }
    }

    @Test
    public void testCIFAR10TrainingInputProvider_GroupByChannel_Input() {
        CIFARInputProvider.CIFAR10TrainingInputProvider cifarTrainInputProvider = new CIFARInputProvider.CIFAR10TrainingInputProvider("cifar-10-batches-bin");
        int imageCount = 5;
        cifarTrainInputProvider.setup("cifar-10-batches-bin", 1, imageCount, 3072, "data_batch_1.bin");
        cifarTrainInputProvider.getProperties().setGroupByChannel(true);
        cifarTrainInputProvider.getProperties().setScaleColors(false);
        cifarTrainInputProvider.getProperties().setSubtractMean(false);
        cifarTrainInputProvider.getProperties().setParallelPreprocessing(false);
        cifarTrainInputProvider.reset();
        // read 5 images from binary file.
        TrainingInputDataImpl[] ti = new TrainingInputDataImpl[imageCount];
        for (int i = 0; i < imageCount; i++) {
            ti[i] = new TrainingInputDataImpl(TensorFactory.tensor(1, cifarTrainInputProvider.getInputDimensions()), TensorFactory.tensor(1, cifarTrainInputProvider.getTargetDimensions()));
            cifarTrainInputProvider.populateNext(ti[i]);
        }
        RandomAccessFile data_batch_1 = null;
        byte[] categories = new byte[imageCount];
        byte[][] pixels = new byte[imageCount][3072];
        try {
            data_batch_1 = new RandomAccessFile("cifar-10-batches-bin/data_batch_1.bin", "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(data_batch_1);
        try {
            for (int i = 0; i < imageCount; i++) {
                categories[i] = data_batch_1.readByte();
                data_batch_1.readFully(pixels[i]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < imageCount; i++) {
            // check input (pixel values)
            float[] inputImporter = ti[i].getInput().getElements();
            byte[] inputCompare = pixels[i];
            Assert.assertEquals(inputImporter.length, inputCompare.length);
            for (int j = 0; j < (inputImporter.length); j++) {
                Assert.assertEquals(inputImporter[j], ((inputCompare[j]) & 255), 1.0E-5F);
            }
            float[] outputImporter = ti[i].getTarget().getElements();
            byte outputIndex = 0;
            for (byte j = 0; j < (outputImporter.length); j++) {
                if ((outputImporter[j]) > 0.0F) {
                    outputIndex = j;
                    break;
                }
            }
            Assert.assertEquals(categories[i], outputIndex);
        }
    }

    @Test
    public void testCIFAR10TrainingInputProvider_RGB_Scale_Input() {
        CIFARInputProvider.CIFAR10TrainingInputProvider cifarTrainInputProvider = new CIFARInputProvider.CIFAR10TrainingInputProvider("cifar-10-batches-bin");
        int imageCount = 5;
        cifarTrainInputProvider.setup("cifar-10-batches-bin", 1, imageCount, 3072, "data_batch_1.bin");
        cifarTrainInputProvider.getProperties().setGroupByChannel(false);
        cifarTrainInputProvider.getProperties().setScaleColors(true);
        cifarTrainInputProvider.getProperties().setSubtractMean(false);
        cifarTrainInputProvider.getProperties().setParallelPreprocessing(false);
        cifarTrainInputProvider.reset();
        // read 5 images from binary file.
        TrainingInputDataImpl[] ti = new TrainingInputDataImpl[imageCount];
        for (int i = 0; i < imageCount; i++) {
            ti[i] = new TrainingInputDataImpl(TensorFactory.tensor(1, cifarTrainInputProvider.getInputDimensions()), TensorFactory.tensor(1, cifarTrainInputProvider.getTargetDimensions()));
            cifarTrainInputProvider.populateNext(ti[i]);
        }
        RandomAccessFile data_batch_1 = null;
        byte[] categories = new byte[imageCount];
        float[][] pixels = new float[imageCount][3072];
        try {
            data_batch_1 = new RandomAccessFile("cifar-10-batches-bin/data_batch_1.bin", "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(data_batch_1);
        try {
            for (int i = 0; i < imageCount; i++) {
                categories[i] = data_batch_1.readByte();
                byte[] pixelTemp = new byte[3072];
                data_batch_1.readFully(pixelTemp);
                for (int j = 0; j < 1024; j++) {
                    pixels[i][(j * 3)] = ((pixelTemp[j]) & 255) / 255.0F;
                    pixels[i][((j * 3) + 1)] = ((pixelTemp[(1024 + j)]) & 255) / 255.0F;
                    pixels[i][((j * 3) + 2)] = ((pixelTemp[((1024 * 2) + j)]) & 255) / 255.0F;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < imageCount; i++) {
            // check input (pixel values)
            float[] inputImporter = ti[i].getInput().getElements();
            float[] inputCompare = pixels[i];
            Assert.assertEquals(inputImporter.length, inputCompare.length);
            for (int j = 0; j < (inputImporter.length); j++) {
                Assert.assertEquals(inputImporter[j], inputCompare[j], 1.0E-5F);
            }
            float[] outputImporter = ti[i].getTarget().getElements();
            byte outputIndex = 0;
            for (byte j = 0; j < (outputImporter.length); j++) {
                if ((outputImporter[j]) > 0.0F) {
                    outputIndex = j;
                    break;
                }
            }
            Assert.assertEquals(categories[i], outputIndex);
        }
    }

    @Test
    public void testCIFAR10TrainingInputProvider_GroupByChannel_Scale_Input() {
        CIFARInputProvider.CIFAR10TrainingInputProvider cifarTrainInputProvider = new CIFARInputProvider.CIFAR10TrainingInputProvider("cifar-10-batches-bin");
        int imageCount = 5;
        cifarTrainInputProvider.setup("cifar-10-batches-bin", 1, imageCount, 3072, "data_batch_1.bin");
        cifarTrainInputProvider.getProperties().setGroupByChannel(true);
        cifarTrainInputProvider.getProperties().setScaleColors(true);
        cifarTrainInputProvider.getProperties().setSubtractMean(false);
        cifarTrainInputProvider.getProperties().setParallelPreprocessing(false);
        cifarTrainInputProvider.reset();
        // read 5 images from binary file.
        TrainingInputDataImpl[] ti = new TrainingInputDataImpl[imageCount];
        for (int i = 0; i < imageCount; i++) {
            ti[i] = new TrainingInputDataImpl(TensorFactory.tensor(1, cifarTrainInputProvider.getInputDimensions()), TensorFactory.tensor(1, cifarTrainInputProvider.getTargetDimensions()));
            cifarTrainInputProvider.populateNext(ti[i]);
        }
        RandomAccessFile data_batch_1 = null;
        byte[] categories = new byte[imageCount];
        float[][] pixels = new float[imageCount][3072];
        try {
            data_batch_1 = new RandomAccessFile("cifar-10-batches-bin/data_batch_1.bin", "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(data_batch_1);
        try {
            for (int i = 0; i < imageCount; i++) {
                categories[i] = data_batch_1.readByte();
                byte[] pixelTemp = new byte[3072];
                data_batch_1.readFully(pixelTemp);
                for (int j = 0; j < 3072; j++) {
                    pixels[i][j] = ((pixelTemp[j]) & 255) / 255.0F;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < imageCount; i++) {
            // check input (pixel values)
            float[] inputImporter = ti[i].getInput().getElements();
            float[] inputCompare = pixels[i];
            Assert.assertEquals(inputImporter.length, inputCompare.length);
            for (int j = 0; j < (inputImporter.length); j++) {
                Assert.assertEquals(inputImporter[j], inputCompare[j], 1.0E-5F);
            }
            float[] outputImporter = ti[i].getTarget().getElements();
            byte outputIndex = 0;
            for (byte j = 0; j < (outputImporter.length); j++) {
                if ((outputImporter[j]) > 0.0F) {
                    outputIndex = j;
                    break;
                }
            }
            Assert.assertEquals(categories[i], outputIndex);
        }
    }

    @Test
    public void testCIFAR10TrainingInputProvider_RGB_Mean_Input() {
        CIFARInputProvider.CIFAR10TrainingInputProvider cifarTrainInputProvider = new CIFARInputProvider.CIFAR10TrainingInputProvider("cifar-10-batches-bin");
        int imageCount = 5;
        cifarTrainInputProvider.setup("cifar-10-batches-bin", 1, imageCount, 3072, "data_batch_1.bin");
        cifarTrainInputProvider.getProperties().setGroupByChannel(false);
        cifarTrainInputProvider.getProperties().setScaleColors(false);
        cifarTrainInputProvider.getProperties().setSubtractMean(true);
        cifarTrainInputProvider.getProperties().setParallelPreprocessing(false);
        cifarTrainInputProvider.reset();
        // read 5 images from binary file.
        TrainingInputDataImpl[] ti = new TrainingInputDataImpl[imageCount];
        for (int i = 0; i < imageCount; i++) {
            ti[i] = new TrainingInputDataImpl(TensorFactory.tensor(1, cifarTrainInputProvider.getInputDimensions()), TensorFactory.tensor(1, cifarTrainInputProvider.getTargetDimensions()));
            cifarTrainInputProvider.populateNext(ti[i]);
        }
        RandomAccessFile data_batch_1 = null;
        byte[] categories = new byte[imageCount];
        float[][] pixels = new float[imageCount][3072];
        try {
            data_batch_1 = new RandomAccessFile("cifar-10-batches-bin/data_batch_1.bin", "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(data_batch_1);
        try {
            for (int i = 0; i < imageCount; i++) {
                categories[i] = data_batch_1.readByte();
                byte[] pixelTemp = new byte[3072];
                data_batch_1.readFully(pixelTemp);
                for (int j = 0; j < 1024; j++) {
                    pixels[i][(j * 3)] = (pixelTemp[j]) & 255;
                    pixels[i][((j * 3) + 1)] = (pixelTemp[(1024 + j)]) & 255;
                    pixels[i][((j * 3) + 2)] = (pixelTemp[((1024 * 2) + j)]) & 255;
                }
                float mR = 0.0F;
                float mG = 0.0F;
                float mB = 0.0F;
                for (int j = 0; j < 1024; j++) {
                    mR += pixels[i][(j * 3)];
                    mG += pixels[i][((j * 3) + 1)];
                    mB += pixels[i][((j * 3) + 2)];
                }
                mR /= 1024;
                mG /= 1024;
                mB /= 1024;
                for (int j = 0; j < 1024; j++) {
                    pixels[i][(j * 3)] -= mR;
                    pixels[i][((j * 3) + 1)] -= mG;
                    pixels[i][((j * 3) + 2)] -= mB;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < imageCount; i++) {
            // check input (pixel values)
            float[] inputImporter = ti[i].getInput().getElements();
            float[] inputCompare = pixels[i];
            Assert.assertEquals(inputImporter.length, inputCompare.length);
            for (int j = 0; j < (inputImporter.length); j++) {
                Assert.assertEquals(inputImporter[j], inputCompare[j], 1.0E-5F);
            }
            float[] outputImporter = ti[i].getTarget().getElements();
            byte outputIndex = 0;
            for (byte j = 0; j < (outputImporter.length); j++) {
                if ((outputImporter[j]) > 0.0F) {
                    outputIndex = j;
                    break;
                }
            }
            Assert.assertEquals(categories[i], outputIndex);
        }
    }

    @Test
    public void testCIFAR10TrainingInputProvider_GroupByChannel_Mean_Input() {
        CIFARInputProvider.CIFAR10TrainingInputProvider cifarTrainInputProvider = new CIFARInputProvider.CIFAR10TrainingInputProvider("cifar-10-batches-bin");
        int imageCount = 5;
        cifarTrainInputProvider.setup("cifar-10-batches-bin", 1, imageCount, 3072, "data_batch_1.bin");
        cifarTrainInputProvider.getProperties().setGroupByChannel(true);
        cifarTrainInputProvider.getProperties().setScaleColors(false);
        cifarTrainInputProvider.getProperties().setSubtractMean(true);
        cifarTrainInputProvider.getProperties().setParallelPreprocessing(false);
        cifarTrainInputProvider.reset();
        // read 5 images from binary file.
        TrainingInputDataImpl[] ti = new TrainingInputDataImpl[imageCount];
        for (int i = 0; i < imageCount; i++) {
            ti[i] = new TrainingInputDataImpl(TensorFactory.tensor(1, cifarTrainInputProvider.getInputDimensions()), TensorFactory.tensor(1, cifarTrainInputProvider.getTargetDimensions()));
            cifarTrainInputProvider.populateNext(ti[i]);
        }
        RandomAccessFile data_batch_1 = null;
        byte[] categories = new byte[imageCount];
        float[][] pixels = new float[imageCount][3072];
        try {
            data_batch_1 = new RandomAccessFile("cifar-10-batches-bin/data_batch_1.bin", "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(data_batch_1);
        try {
            for (int i = 0; i < imageCount; i++) {
                categories[i] = data_batch_1.readByte();
                byte[] pixelTemp = new byte[3072];
                data_batch_1.readFully(pixelTemp);
                for (int j = 0; j < 3072; j++) {
                    pixels[i][j] = (pixelTemp[j]) & 255;
                }
                float mR = 0.0F;
                float mG = 0.0F;
                float mB = 0.0F;
                for (int j = 0; j < 1024; j++) {
                    mR += pixels[i][j];
                    mG += pixels[i][(j + 1024)];
                    mB += pixels[i][(j + 2048)];
                }
                mR /= 1024;
                mG /= 1024;
                mB /= 1024;
                for (int j = 0; j < 1024; j++) {
                    pixels[i][j] -= mR;
                    pixels[i][(j + 1024)] -= mG;
                    pixels[i][(j + 2048)] -= mB;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < imageCount; i++) {
            // check input (pixel values)
            float[] inputImporter = ti[i].getInput().getElements();
            float[] inputCompare = pixels[i];
            Assert.assertEquals(inputImporter.length, inputCompare.length);
            for (int j = 0; j < (inputImporter.length); j++) {
                Assert.assertEquals(inputImporter[j], inputCompare[j], 1.0E-5F);
            }
            float[] outputImporter = ti[i].getTarget().getElements();
            byte outputIndex = 0;
            for (byte j = 0; j < (outputImporter.length); j++) {
                if ((outputImporter[j]) > 0.0F) {
                    outputIndex = j;
                    break;
                }
            }
            Assert.assertEquals(categories[i], outputIndex);
        }
    }

    @Test
    public void testCIFAR10TrainingInputProvider_RGB_Scale_Mean_Input() {
        CIFARInputProvider.CIFAR10TrainingInputProvider cifarTrainInputProvider = new CIFARInputProvider.CIFAR10TrainingInputProvider("cifar-10-batches-bin");
        int imageCount = 5;
        cifarTrainInputProvider.setup("cifar-10-batches-bin", 1, imageCount, 3072, "data_batch_1.bin");
        cifarTrainInputProvider.getProperties().setGroupByChannel(false);
        cifarTrainInputProvider.getProperties().setScaleColors(true);
        cifarTrainInputProvider.getProperties().setSubtractMean(true);
        cifarTrainInputProvider.getProperties().setParallelPreprocessing(false);
        cifarTrainInputProvider.reset();
        // read 5 images from binary file.
        TrainingInputDataImpl[] ti = new TrainingInputDataImpl[imageCount];
        for (int i = 0; i < imageCount; i++) {
            ti[i] = new TrainingInputDataImpl(TensorFactory.tensor(1, cifarTrainInputProvider.getInputDimensions()), TensorFactory.tensor(1, cifarTrainInputProvider.getTargetDimensions()));
            cifarTrainInputProvider.populateNext(ti[i]);
        }
        RandomAccessFile data_batch_1 = null;
        byte[] categories = new byte[imageCount];
        float[][] pixels = new float[imageCount][3072];
        try {
            data_batch_1 = new RandomAccessFile("cifar-10-batches-bin/data_batch_1.bin", "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(data_batch_1);
        try {
            for (int i = 0; i < imageCount; i++) {
                categories[i] = data_batch_1.readByte();
                byte[] pixelTemp = new byte[3072];
                data_batch_1.readFully(pixelTemp);
                for (int j = 0; j < 1024; j++) {
                    pixels[i][(j * 3)] = (pixelTemp[j]) & 255;
                    pixels[i][((j * 3) + 1)] = (pixelTemp[(1024 + j)]) & 255;
                    pixels[i][((j * 3) + 2)] = (pixelTemp[((1024 * 2) + j)]) & 255;
                }
                float mR = 0.0F;
                float mG = 0.0F;
                float mB = 0.0F;
                for (int j = 0; j < 1024; j++) {
                    mR += pixels[i][(j * 3)];
                    mG += pixels[i][((j * 3) + 1)];
                    mB += pixels[i][((j * 3) + 2)];
                }
                mR /= 1024;
                mG /= 1024;
                mB /= 1024;
                for (int j = 0; j < 1024; j++) {
                    pixels[i][(j * 3)] -= mR;
                    pixels[i][((j * 3) + 1)] -= mG;
                    pixels[i][((j * 3) + 2)] -= mB;
                }
                for (int j = 0; j < 1024; j++) {
                    pixels[i][(j * 3)] /= 255.0F;
                    pixels[i][((j * 3) + 1)] /= 255.0F;
                    pixels[i][((j * 3) + 2)] /= 255.0F;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < imageCount; i++) {
            // check input (pixel values)
            float[] inputImporter = ti[i].getInput().getElements();
            float[] inputCompare = pixels[i];
            Assert.assertEquals(inputImporter.length, inputCompare.length);
            for (int j = 0; j < (inputImporter.length); j++) {
                Assert.assertEquals(inputImporter[j], inputCompare[j], 1.0E-5F);
            }
            float[] outputImporter = ti[i].getTarget().getElements();
            byte outputIndex = 0;
            for (byte j = 0; j < (outputImporter.length); j++) {
                if ((outputImporter[j]) > 0.0F) {
                    outputIndex = j;
                    break;
                }
            }
            Assert.assertEquals(categories[i], outputIndex);
        }
    }

    @Test
    public void testCIFAR10TrainingInputProvider_GroupByChannel_Scale_Mean_Input() {
        CIFARInputProvider.CIFAR10TrainingInputProvider cifarTrainInputProvider = new CIFARInputProvider.CIFAR10TrainingInputProvider("cifar-10-batches-bin");
        int imageCount = 5;
        cifarTrainInputProvider.setup("cifar-10-batches-bin", 1, imageCount, 3072, "data_batch_1.bin");
        cifarTrainInputProvider.getProperties().setGroupByChannel(true);
        cifarTrainInputProvider.getProperties().setScaleColors(true);
        cifarTrainInputProvider.getProperties().setSubtractMean(true);
        cifarTrainInputProvider.getProperties().setParallelPreprocessing(false);
        cifarTrainInputProvider.reset();
        // read 5 images from binary file.
        TrainingInputDataImpl[] ti = new TrainingInputDataImpl[imageCount];
        for (int i = 0; i < imageCount; i++) {
            ti[i] = new TrainingInputDataImpl(TensorFactory.tensor(1, cifarTrainInputProvider.getInputDimensions()), TensorFactory.tensor(1, cifarTrainInputProvider.getTargetDimensions()));
            cifarTrainInputProvider.populateNext(ti[i]);
        }
        RandomAccessFile data_batch_1 = null;
        byte[] categories = new byte[imageCount];
        float[][] pixels = new float[imageCount][3072];
        try {
            data_batch_1 = new RandomAccessFile("cifar-10-batches-bin/data_batch_1.bin", "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(data_batch_1);
        try {
            for (int i = 0; i < imageCount; i++) {
                categories[i] = data_batch_1.readByte();
                byte[] pixelTemp = new byte[3072];
                data_batch_1.readFully(pixelTemp);
                for (int j = 0; j < 3072; j++) {
                    pixels[i][j] = ((pixelTemp[j]) & 255) / 255.0F;
                }
                float mR = 0.0F;
                float mG = 0.0F;
                float mB = 0.0F;
                for (int j = 0; j < 1024; j++) {
                    mR += pixels[i][j];
                    mG += pixels[i][(j + 1024)];
                    mB += pixels[i][(j + 2048)];
                }
                mR /= 1024;
                mG /= 1024;
                mB /= 1024;
                for (int j = 0; j < 1024; j++) {
                    pixels[i][j] -= mR;
                    pixels[i][(j + 1024)] -= mG;
                    pixels[i][(j + 2048)] -= mB;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < imageCount; i++) {
            // check input (pixel values)
            float[] inputImporter = ti[i].getInput().getElements();
            float[] inputCompare = pixels[i];
            Assert.assertEquals(inputImporter.length, inputCompare.length);
            for (int j = 0; j < (inputImporter.length); j++) {
                Assert.assertEquals(inputImporter[j], inputCompare[j], 1.0E-5F);
            }
            float[] outputImporter = ti[i].getTarget().getElements();
            byte outputIndex = 0;
            for (byte j = 0; j < (outputImporter.length); j++) {
                if ((outputImporter[j]) > 0.0F) {
                    outputIndex = j;
                    break;
                }
            }
            Assert.assertEquals(categories[i], outputIndex);
        }
    }
}

