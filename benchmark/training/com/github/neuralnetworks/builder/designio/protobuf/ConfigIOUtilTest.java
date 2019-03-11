package com.github.neuralnetworks.builder.designio.protobuf;


import InputProtoBufWrapper.InputData;
import NNProtoBufWrapper.NetConfiguration.Builder;
import TrainerProtoBufWrapper.TestParameter;
import TrainerProtoBufWrapper.ValidationParameter;
import com.github.neuralnetworks.builder.NeuralNetworkBuilder;
import com.github.neuralnetworks.builder.designio.ConfigIOUtil;
import com.github.neuralnetworks.builder.designio.protobuf.nn.NNProtoBufWrapper;
import com.github.neuralnetworks.builder.designio.protobuf.nn.TrainerProtoBufWrapper;
import com.github.neuralnetworks.builder.designio.protobuf.nn.mapping.NNMapper;
import com.google.protobuf.TextFormat;
import java.io.File;
import java.io.InputStreamReader;
import junit.framework.TestCase;
import org.junit.Test;


public class ConfigIOUtilTest extends TestCase {
    private File testDirectory;

    private String resource = "image1.png";

    @Test
    public void testMapProtoBufNetConfigToBuilder() throws Exception {
        InputStreamReader reader = new InputStreamReader(ClassLoader.getSystemResourceAsStream("nn/nnconfiguration.prototxt"));
        NNProtoBufWrapper.NetConfiguration.Builder builder = NNProtoBufWrapper.NetConfiguration.newBuilder();
        TextFormat.merge(reader, builder);
        NNProtoBufWrapper.NetConfiguration netConfiguration = builder.build();
        NeuralNetworkBuilder neuralNetworkBuilder = NNMapper.mapProtoBufNetConfigToBuilder(netConfiguration);
        System.out.println(neuralNetworkBuilder.toString());
    }

    @Test
    public void testReadProtoBufTrainer() throws Exception {
        InputStreamReader reader = new InputStreamReader(ClassLoader.getSystemResourceAsStream("nn/training.prototxt"));
        TrainerProtoBufWrapper.TrainParameter.Builder builder = TrainerProtoBufWrapper.TrainParameter.newBuilder();
        TextFormat.merge(reader, builder);
    }

    @Test
    public void test() throws Exception {
        createTmpInputDirectory();
        TrainerProtoBufWrapper.TrainParameter trainer;
        NNProtoBufWrapper.NetConfiguration netConfiguration;
        {
            InputStreamReader reader = new InputStreamReader(ClassLoader.getSystemResourceAsStream("nn/nnconfiguration.prototxt"));
            NNProtoBufWrapper.NetConfiguration.Builder builder = NNProtoBufWrapper.NetConfiguration.newBuilder();
            TextFormat.merge(reader, builder);
            netConfiguration = builder.build();
        }
        {
            InputStreamReader reader = new InputStreamReader(ClassLoader.getSystemResourceAsStream("nn/training.prototxt"));
            TrainerProtoBufWrapper.TrainParameter.Builder builder = TrainerProtoBufWrapper.TrainParameter.newBuilder();
            TextFormat.merge(reader, builder);
            // replace path
            {
                TrainerProtoBufWrapper.TrainParameter.Builder replaceBuilder = TrainerProtoBufWrapper.TrainParameter.newBuilder().setTrainInput(InputData.newBuilder().setPath(testDirectory.getAbsolutePath())).setTestParam(TestParameter.newBuilder().setTestInput(InputData.newBuilder().setPath(testDirectory.getAbsolutePath()))).setValidationParam(ValidationParameter.newBuilder().setValidationInput(InputData.newBuilder().setPath(testDirectory.getAbsolutePath())));
                builder.mergeFrom(replaceBuilder.build());
            }
            trainer = builder.build();
        }
        NeuralNetworkBuilder neuralNetworkBuilder = ConfigIOUtil.mapProtoBufTo(netConfiguration, trainer);
        System.out.println(neuralNetworkBuilder.toString());
    }
}

