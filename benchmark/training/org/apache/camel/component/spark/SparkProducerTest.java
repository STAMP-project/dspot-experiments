/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.spark;


import com.google.common.truth.Truth;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.apache.camel.component.spark.annotations.org.apache.camel.component.spark.RddCallback;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaRDDLike;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.hive.HiveContext;
import org.junit.Assume;
import org.junit.Test;


public class SparkProducerTest extends CamelTestSupport {
    // Fixtures
    static JavaSparkContext sparkContext = Sparks.createLocalSparkContext();

    static boolean shouldRunHive = Boolean.parseBoolean(System.getenv("CAMEL_SPARK_HIVE_TESTS"));

    static HiveContext hiveContext;

    String sparkUri = "spark:rdd?rdd=#testFileRdd";

    String sparkDataFrameUri = "spark:dataframe?dataFrame=#jsonCars";

    String sparkHiveUri = "spark:hive";

    int numberOfLinesInTestFile = 19;

    // Tests
    @Test
    public void shouldExecuteRddCallback() {
        long linesCount = template.requestBodyAndHeader(sparkUri, null, SparkConstants.SPARK_RDD_CALLBACK_HEADER, new org.apache.camel.component.spark.RddCallback() {
            @Override
            public Long onRdd(JavaRDDLike rdd, Object... payloads) {
                return rdd.count();
            }
        }, Long.class);
        Truth.assertThat(linesCount).isEqualTo(numberOfLinesInTestFile);
    }

    @Test
    public void shouldExecuteRddCallbackWithSinglePayload() {
        long linesCount = template.requestBodyAndHeader(sparkUri, 10, SparkConstants.SPARK_RDD_CALLBACK_HEADER, new org.apache.camel.component.spark.RddCallback() {
            @Override
            public Long onRdd(JavaRDDLike rdd, Object... payloads) {
                return (rdd.count()) * ((int) (payloads[0]));
            }
        }, Long.class);
        Truth.assertThat(linesCount).isEqualTo(((numberOfLinesInTestFile) * 10));
    }

    @Test
    public void shouldExecuteRddCallbackWithPayloads() {
        long linesCount = template.requestBodyAndHeader(sparkUri, Arrays.asList(10, 10), SparkConstants.SPARK_RDD_CALLBACK_HEADER, new org.apache.camel.component.spark.RddCallback() {
            @Override
            public Long onRdd(JavaRDDLike rdd, Object... payloads) {
                return ((rdd.count()) * ((int) (payloads[0]))) * ((int) (payloads[1]));
            }
        }, Long.class);
        Truth.assertThat(linesCount).isEqualTo((((numberOfLinesInTestFile) * 10) * 10));
    }

    @Test
    public void shouldExecuteRddCallbackWithTypedPayloads() {
        ConvertingRddCallback rddCallback = new ConvertingRddCallback<Long>(context, int.class, int.class) {
            @Override
            public Long doOnRdd(JavaRDDLike rdd, Object... payloads) {
                return ((rdd.count()) * ((int) (payloads[0]))) * ((int) (payloads[1]));
            }
        };
        long linesCount = template.requestBodyAndHeader(sparkUri, Arrays.asList("10", "10"), SparkConstants.SPARK_RDD_CALLBACK_HEADER, rddCallback, Long.class);
        Truth.assertThat(linesCount).isEqualTo(1900);
    }

    @Test
    public void shouldUseTransformationFromRegistry() {
        long linesCount = template.requestBody(((sparkUri) + "&rddCallback=#countLinesTransformation"), null, Long.class);
        Truth.assertThat(linesCount).isGreaterThan(0L);
    }

    @Test
    public void shouldExecuteVoidCallback() throws IOException {
        // Given
        final File output = File.createTempFile("camel", "spark");
        output.delete();
        // When
        template.sendBodyAndHeader(sparkUri, null, SparkConstants.SPARK_RDD_CALLBACK_HEADER, new VoidRddCallback() {
            @Override
            public void doOnRdd(JavaRDDLike rdd, Object... payloads) {
                rdd.saveAsTextFile(output.getAbsolutePath());
            }
        });
        // Then
        Truth.assertThat(output.length()).isGreaterThan(0L);
    }

    @Test
    public void shouldExecuteAnnotatedCallback() {
        org.apache.camel.component.spark.RddCallback rddCallback = annotatedRddCallback(new Object() {
            @org.apache.camel.component.spark.annotations.RddCallback
            long countLines(JavaRDD<String> textFile) {
                return textFile.count();
            }
        });
        long pomLinesCount = template.requestBodyAndHeader(sparkUri, null, SparkConstants.SPARK_RDD_CALLBACK_HEADER, rddCallback, Long.class);
        Truth.assertThat(pomLinesCount).isEqualTo(19);
    }

    @Test
    public void shouldExecuteAnnotatedVoidCallback() throws IOException {
        // Given
        final File output = File.createTempFile("camel", "spark");
        output.delete();
        org.apache.camel.component.spark.RddCallback rddCallback = annotatedRddCallback(new Object() {
            @org.apache.camel.component.spark.annotations.RddCallback
            void countLines(JavaRDD<String> textFile) {
                textFile.saveAsTextFile(output.getAbsolutePath());
            }
        });
        // When
        template.sendBodyAndHeader(sparkUri, null, SparkConstants.SPARK_RDD_CALLBACK_HEADER, rddCallback);
        // Then
        Truth.assertThat(output.length()).isGreaterThan(0L);
    }

    @Test
    public void shouldExecuteAnnotatedCallbackWithParameters() {
        org.apache.camel.component.spark.RddCallback rddCallback = annotatedRddCallback(new Object() {
            @org.apache.camel.component.spark.annotations.RddCallback
            long countLines(JavaRDD<String> textFile, int first, int second) {
                return ((textFile.count()) * first) * second;
            }
        });
        long pomLinesCount = template.requestBodyAndHeader(sparkUri, Arrays.asList(10, 10), SparkConstants.SPARK_RDD_CALLBACK_HEADER, rddCallback, Long.class);
        Truth.assertThat(pomLinesCount).isEqualTo((((numberOfLinesInTestFile) * 10) * 10));
    }

    @Test
    public void shouldExecuteAnnotatedCallbackWithConversions() {
        org.apache.camel.component.spark.RddCallback rddCallback = annotatedRddCallback(new Object() {
            @org.apache.camel.component.spark.annotations.RddCallback
            long countLines(JavaRDD<String> textFile, int first, int second) {
                return ((textFile.count()) * first) * second;
            }
        }, context);
        long pomLinesCount = template.requestBodyAndHeader(sparkUri, Arrays.asList(10, "10"), SparkConstants.SPARK_RDD_CALLBACK_HEADER, rddCallback, Long.class);
        Truth.assertThat(pomLinesCount).isEqualTo((((numberOfLinesInTestFile) * 10) * 10));
    }

    // Hive tests
    @Test
    public void shouldExecuteHiveQuery() {
        Assume.assumeTrue(SparkProducerTest.shouldRunHive);
        List<Row> cars = template.requestBody(sparkHiveUri, "SELECT * FROM cars", List.class);
        Truth.assertThat(cars.get(0).getString(1)).isEqualTo("X-trail");
    }

    @Test
    public void shouldExecuteHiveCountQuery() {
        Assume.assumeTrue(SparkProducerTest.shouldRunHive);
        long carsCount = template.requestBody(((sparkHiveUri) + "?collect=false"), "SELECT * FROM cars", Long.class);
        Truth.assertThat(carsCount).isEqualTo(2);
    }

    // Data frames tests
    @Test
    public void shouldCountFrame() {
        Assume.assumeTrue(SparkProducerTest.shouldRunHive);
        DataFrameCallback callback = new DataFrameCallback<Long>() {
            @Override
            public Long onDataFrame(Dataset<Row> dataFrame, Object... payloads) {
                return dataFrame.count();
            }
        };
        long tablesCount = template.requestBodyAndHeader(sparkDataFrameUri, null, SparkConstants.SPARK_DATAFRAME_CALLBACK_HEADER, callback, Long.class);
        Truth.assertThat(tablesCount).isEqualTo(2);
    }

    @Test
    public void shouldExecuteConditionalFrameCount() {
        Assume.assumeTrue(SparkProducerTest.shouldRunHive);
        DataFrameCallback callback = new DataFrameCallback<Long>() {
            @Override
            public Long onDataFrame(Dataset<Row> dataFrame, Object... payloads) {
                String model = ((String) (payloads[0]));
                return dataFrame.where(dataFrame.col("model").eqNullSafe(model)).count();
            }
        };
        long tablesCount = template.requestBodyAndHeader(sparkDataFrameUri, "Micra", SparkConstants.SPARK_DATAFRAME_CALLBACK_HEADER, callback, Long.class);
        Truth.assertThat(tablesCount).isEqualTo(1);
    }
}

