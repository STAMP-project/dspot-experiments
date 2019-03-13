package org.deeplearning4j.nn.weights;


import WeightInit.DISTRIBUTION;
import WeightInitUtil.DEFAULT_WEIGHT_INIT_ORDER;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.deeplearning4j.nn.conf.serde.JsonMappers;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.rng.Random;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.factory.RandomFactory;
import org.nd4j.shade.jackson.databind.ObjectMapper;

import static WeightInit.DISTRIBUTION;
import static WeightInit.LECUN_NORMAL;
import static WeightInit.NORMAL;


/**
 * Test that {@link WeightInit} is compatible with the corresponding classes which implement {@link IWeightInit}. Mocks
 * Nd4j.randomFactory so that legacy and new implementation can be compared exactly.
 *
 * @author Christian Skarby
 */
public class LegacyWeightInitTest {
    private RandomFactory prevFactory;

    private static final int SEED = 666;

    private static final List<Distribution> distributions = Arrays.asList(new LogNormalDistribution(12.3, 4.56), new BinomialDistribution(3, 0.3), new NormalDistribution(0.666, 0.333), new UniformDistribution((-1.23), 4.56), new OrthogonalDistribution(3.45), new TruncatedNormalDistribution(0.456, 0.123), new ConstantDistribution(666));

    /**
     * Test that param init is identical to legacy implementation
     */
    @Test
    public void initParams() {
        final long[] shape = new long[]{ 5, 5 };// To make identity happy

        final long fanIn = shape[0];
        final long fanOut = shape[1];
        final INDArray inLegacy = Nd4j.create((fanIn * fanOut));
        final INDArray inTest = inLegacy.dup();
        for (WeightInit legacyWi : WeightInit.values()) {
            if (legacyWi != (DISTRIBUTION)) {
                Nd4j.getRandom().setSeed(LegacyWeightInitTest.SEED);
                final INDArray expected = WeightInitUtil.initWeights(fanIn, fanOut, shape, legacyWi, null, inLegacy);
                Nd4j.getRandom().setSeed(LegacyWeightInitTest.SEED);
                final INDArray actual = legacyWi.getWeightInitFunction().init(fanIn, fanOut, shape, DEFAULT_WEIGHT_INIT_ORDER, inTest);
                Assert.assertArrayEquals((("Incorrect shape for " + legacyWi) + "!"), shape, actual.shape());
                Assert.assertEquals((("Incorrect weight initialization for " + legacyWi) + "!"), expected, actual);
            }
        }
    }

    /**
     * Test that param init is identical to legacy implementation
     */
    @Test
    public void initParamsFromDistribution() {
        final long[] shape = new long[]{ 3, 7 };// To make identity happy

        final long fanIn = shape[0];
        final long fanOut = shape[1];
        final INDArray inLegacy = Nd4j.create((fanIn * fanOut));
        final INDArray inTest = inLegacy.dup();
        for (Distribution dist : LegacyWeightInitTest.distributions) {
            Nd4j.getRandom().setSeed(LegacyWeightInitTest.SEED);
            final INDArray expected = WeightInitUtil.initWeights(fanIn, fanOut, shape, DISTRIBUTION, Distributions.createDistribution(dist), inLegacy);
            final INDArray actual = new WeightInitDistribution(dist).init(fanIn, fanOut, shape, DEFAULT_WEIGHT_INIT_ORDER, inTest);
            Assert.assertArrayEquals((("Incorrect shape for " + (dist.getClass().getSimpleName())) + "!"), shape, actual.shape());
            Assert.assertEquals((("Incorrect weight initialization for " + (dist.getClass().getSimpleName())) + "!"), expected, actual);
        }
    }

    /**
     * Test that weight inits can be serialized and de-serialized in JSON format
     */
    @Test
    public void serializeDeserializeJson() throws IOException {
        final long[] shape = new long[]{ 5, 5 };// To make identity happy

        final long fanIn = shape[0];
        final long fanOut = shape[1];
        final ObjectMapper mapper = JsonMappers.getMapper();
        final INDArray inBefore = Nd4j.create((fanIn * fanOut));
        final INDArray inAfter = inBefore.dup();
        // Just use to enum to loop over all strategies
        for (WeightInit legacyWi : WeightInit.values()) {
            if (legacyWi != (DISTRIBUTION)) {
                Nd4j.getRandom().setSeed(LegacyWeightInitTest.SEED);
                final IWeightInit before = legacyWi.getWeightInitFunction();
                final INDArray expected = before.init(fanIn, fanOut, shape, inBefore.ordering(), inBefore);
                final String json = mapper.writeValueAsString(before);
                final IWeightInit after = mapper.readValue(json, IWeightInit.class);
                Nd4j.getRandom().setSeed(LegacyWeightInitTest.SEED);
                final INDArray actual = after.init(fanIn, fanOut, shape, inAfter.ordering(), inAfter);
                Assert.assertArrayEquals((("Incorrect shape for " + legacyWi) + "!"), shape, actual.shape());
                Assert.assertEquals((("Incorrect weight initialization for " + legacyWi) + "!"), expected, actual);
            }
        }
    }

    /**
     * Test that distribution can be serialized and de-serialized in JSON format
     */
    @Test
    public void serializeDeserializeDistributionJson() throws IOException {
        final long[] shape = new long[]{ 3, 7 };// To make identity happy

        final long fanIn = shape[0];
        final long fanOut = shape[1];
        final ObjectMapper mapper = JsonMappers.getMapper();
        final INDArray inBefore = Nd4j.create((fanIn * fanOut));
        final INDArray inAfter = inBefore.dup();
        for (Distribution dist : LegacyWeightInitTest.distributions) {
            Nd4j.getRandom().setSeed(LegacyWeightInitTest.SEED);
            final IWeightInit before = new WeightInitDistribution(dist);
            final INDArray expected = before.init(fanIn, fanOut, shape, inBefore.ordering(), inBefore);
            final String json = mapper.writeValueAsString(before);
            final IWeightInit after = mapper.readValue(json, IWeightInit.class);
            Nd4j.getRandom().setSeed(LegacyWeightInitTest.SEED);
            final INDArray actual = after.init(fanIn, fanOut, shape, inAfter.ordering(), inAfter);
            Assert.assertArrayEquals((("Incorrect shape for " + (dist.getClass().getSimpleName())) + "!"), shape, actual.shape());
            Assert.assertEquals((("Incorrect weight initialization for " + (dist.getClass().getSimpleName())) + "!"), expected, actual);
        }
    }

    /**
     * Test equals and hashcode implementation. Redundant as one can trust Lombok on this??
     */
    @Test
    public void equalsAndHashCode() {
        WeightInit lastInit = WeightInit.values()[((WeightInit.values().length) - 1)];
        for (WeightInit legacyWi : WeightInit.values()) {
            if (legacyWi != (DISTRIBUTION)) {
                Assert.assertEquals("Shall be equal!", legacyWi.getWeightInitFunction(), legacyWi.getWeightInitFunction());
                Assert.assertNotEquals("Shall not be equal!", lastInit.getWeightInitFunction(), legacyWi.getWeightInitFunction());
                if ((legacyWi != (NORMAL)) && (legacyWi != (LECUN_NORMAL))) {
                    lastInit = legacyWi;
                }
            }
        }
        Distribution lastDist = LegacyWeightInitTest.distributions.get(((LegacyWeightInitTest.distributions.size()) - 1));
        for (Distribution distribution : LegacyWeightInitTest.distributions) {
            Assert.assertEquals("Shall be equal!", new WeightInitDistribution(distribution), new WeightInitDistribution(distribution.clone()));
            Assert.assertNotEquals("Shall not be equal!", new WeightInitDistribution(lastDist), new WeightInitDistribution(distribution));
            lastDist = distribution;
        }
    }

    /**
     * Assumes RandomFactory will only call no-args constructor while this test runs
     */
    private static class FixedSeedRandomFactory extends RandomFactory {
        private final RandomFactory factory;

        private FixedSeedRandomFactory(RandomFactory factory) {
            super(factory.getRandom().getClass());
            this.factory = factory;
        }

        @Override
        public Random getRandom() {
            return getNewRandomInstance(LegacyWeightInitTest.SEED);
        }

        @Override
        public Random getNewRandomInstance() {
            return factory.getNewRandomInstance();
        }

        @Override
        public Random getNewRandomInstance(long seed) {
            return factory.getNewRandomInstance(seed);
        }

        @Override
        public Random getNewRandomInstance(long seed, long size) {
            return factory.getNewRandomInstance(seed, size);
        }
    }
}

