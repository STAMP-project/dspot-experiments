/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.beam.sdk.io.xml;


import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.beam.sdk.io.BoundedSource;
import org.apache.beam.sdk.io.Source.Reader;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.testing.NeedsRunner;
import org.apache.beam.sdk.testing.PAssert;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link XmlSource} in particular - the rest of {@link XmlIO} is tested in {@link XmlIOTest}.
 */
@RunWith(JUnit4.class)
public class XmlSourceTest {
    @Rule
    public TestPipeline p = TestPipeline.create();

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private String tinyXML = "<trains><train><name>Thomas</name></train><train><name>Henry</name></train>" + "<train><name>James</name></train></trains>";

    private String xmlWithMultiByteElementName = "<?????????><???????><name>Thomas</name></???????><???????><name>Henry</name></???????>" + "<???????><name>James</name></???????></?????????>";

    private String xmlWithMultiByteChars = "<trains><train><name>Thomas?</name></train><train><name>Hen?ry</name></train>" + "<train><name>Jam?es</name></train></trains>";

    private String trainXML = "<trains>" + (((((("<train><name>Thomas</name><number>1</number><color>blue</color></train>" + "<train><name>Henry</name><number>3</number><color>green</color></train>") + "<train><name>Toby</name><number>7</number><color>brown</color></train>") + "<train><name>Gordon</name><number>4</number><color>blue</color></train>") + "<train><name>Emily</name><number>-1</number><color>red</color></train>") + "<train><name>Percy</name><number>6</number><color>green</color></train>") + "</trains>");

    private String trainXMLWithEmptyTags = "<trains>" + (((((((("<train/>" + "<train><name>Thomas</name><number>1</number><color>blue</color></train>") + "<train><name>Henry</name><number>3</number><color>green</color></train>") + "<train/>") + "<train><name>Toby</name><number>7</number><color>brown</color></train>") + "<train><name>Gordon</name><number>4</number><color>blue</color></train>") + "<train><name>Emily</name><number>-1</number><color>red</color></train>") + "<train><name>Percy</name><number>6</number><color>green</color></train>") + "</trains>");

    private String trainXMLWithAttributes = "<trains>" + (((((("<train size=\"small\"><name>Thomas</name><number>1</number><color>blue</color></train>" + "<train size=\"big\"><name>Henry</name><number>3</number><color>green</color></train>") + "<train size=\"small\"><name>Toby</name><number>7</number><color>brown</color></train>") + "<train size=\"big\"><name>Gordon</name><number>4</number><color>blue</color></train>") + "<train size=\"small\"><name>Emily</name><number>-1</number><color>red</color></train>") + "<train size=\"small\"><name>Percy</name><number>6</number><color>green</color></train>") + "</trains>");

    private String trainXMLWithSpaces = "<trains>" + (((((("<train><name>Thomas   </name>   <number>1</number><color>blue</color></train>" + "<train><name>Henry</name><number>3</number><color>green</color></train>\n") + "<train><name>Toby</name><number>7</number><color>  brown  </color></train>  ") + "<train><name>Gordon</name>   <number>4</number><color>blue</color>\n</train>\t") + "<train><name>Emily</name><number>-1</number>\t<color>red</color></train>") + "<train>\n<name>Percy</name>   <number>6  </number>   <color>green</color></train>") + "</trains>");

    private String trainXMLWithAllFeaturesMultiByte = "<?????????>" + ((((((((((("<???????/>" + "<\u0daf\u0dd4\u0db8\u0dca\u0dbb\u0dd2\u0dba size=\"small\"><name> Thomas\u00a5</name><number>1</number><color>blue</color>") + "</???????>") + "<\u0daf\u0dd4\u0db8\u0dca\u0dbb\u0dd2\u0dba size=\"big\"><name>He nry</name><number>3</number><color>green</color></\u0daf\u0dd4\u0db8\u0dca\u0dbb\u0dd2\u0dba>") + "<\u0daf\u0dd4\u0db8\u0dca\u0dbb\u0dd2\u0dba size=\"small\"><name>Toby  </name><number>7</number><color>br\u00b6own</color>") + "</???????>") + "<???????/>") + "<\u0daf\u0dd4\u0db8\u0dca\u0dbb\u0dd2\u0dba size=\"big\"><name>Gordon</name><number>4</number><color> blue</color></\u0daf\u0dd4\u0db8\u0dca\u0dbb\u0dd2\u0dba>") + "<\u0daf\u0dd4\u0db8\u0dca\u0dbb\u0dd2\u0dba size=\"small\"><name>Emily</name><number>-1</number><color>red</color></\u0daf\u0dd4\u0db8\u0dca\u0dbb\u0dd2\u0dba>") + "<\u0daf\u0dd4\u0db8\u0dca\u0dbb\u0dd2\u0dba size=\"small\"><name>Percy</name><number>6</number><color>green</color>") + "</???????>") + "</?????????>");

    private String trainXMLWithAllFeaturesSingleByte = "<trains>" + ((((((((((("<train/>" + "<train size=\"small\"><name> Thomas</name><number>1</number><color>blue</color>") + "</train>") + "<train size=\"big\"><name>He nry</name><number>3</number><color>green</color></train>") + "<train size=\"small\"><name>Toby  </name><number>7</number><color>brown</color>") + "</train>") + "<train/>") + "<train size=\"big\"><name>Gordon</name><number>4</number><color> blue</color></train>") + "<train size=\"small\"><name>Emily</name><number>-1</number><color>red</color></train>") + "<train size=\"small\"><name>Percy</name><number>6</number><color>green</color>") + "</train>") + "</trains>");

    private String trainXMLWithISO88591 = "<trains>" + ("<train size=\"small\"><name>C\u00e9dric</name><number>7</number><color>blue</color></train>" + "</trains>");

    @XmlRootElement
    static class TinyTrain {
        TinyTrain(String name) {
            this.name = name;
        }

        public TinyTrain() {
        }

        public String name = null;

        @Override
        public String toString() {
            String str = "Train[";
            if ((name) != null) {
                str = (str + "name=") + (name);
            }
            str = str + "]";
            return str;
        }
    }

    @XmlRootElement
    static class Train {
        public static final int TRAIN_NUMBER_UNDEFINED = -1;

        public String name = null;

        public String color = null;

        public int number = XmlSourceTest.Train.TRAIN_NUMBER_UNDEFINED;

        @XmlAttribute(name = "size")
        public String size = null;

        public Train() {
        }

        public Train(String name, int number, String color, String size) {
            this.name = name;
            this.number = number;
            this.color = color;
            this.size = size;
        }

        @Override
        public int hashCode() {
            int hashCode = 1;
            hashCode = (31 * hashCode) + ((name) == null ? 0 : name.hashCode());
            hashCode = (31 * hashCode) + (number);
            hashCode = (31 * hashCode) + ((color) == null ? 0 : name.hashCode());
            hashCode = (31 * hashCode) + ((size) == null ? 0 : name.hashCode());
            return hashCode;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof XmlSourceTest.Train)) {
                return false;
            }
            XmlSourceTest.Train other = ((XmlSourceTest.Train) (obj));
            return (((((name) == null) || (name.equals(other.name))) && ((number) == (other.number))) && (((color) == null) || (color.equals(other.color)))) && (((size) == null) || (size.equals(other.size)));
        }

        @Override
        public String toString() {
            String str = "Train[";
            boolean first = true;
            if ((name) != null) {
                str = (str + "name=") + (name);
                first = false;
            }
            if ((number) != (Integer.MIN_VALUE)) {
                if (!first) {
                    str = str + ",";
                }
                str = (str + "number=") + (number);
                first = false;
            }
            if ((color) != null) {
                if (!first) {
                    str = str + ",";
                }
                str = (str + "color=") + (color);
                first = false;
            }
            if ((size) != null) {
                if (!first) {
                    str = str + ",";
                }
                str = (str + "size=") + (size);
            }
            str = str + "]";
            return str;
        }
    }

    @Test
    public void testReadXMLTiny() throws IOException {
        File file = tempFolder.newFile("trainXMLTiny");
        Files.write(file.toPath(), tinyXML.getBytes(StandardCharsets.UTF_8));
        BoundedSource<XmlSourceTest.Train> source = XmlIO.<XmlSourceTest.Train>read().from(file.toPath().toString()).withRootElement("trains").withRecordElement("train").withRecordClass(XmlSourceTest.Train.class).withMinBundleSize(1024).createSource();
        List<XmlSourceTest.Train> expectedResults = ImmutableList.of(new XmlSourceTest.Train("Thomas", XmlSourceTest.Train.TRAIN_NUMBER_UNDEFINED, null, null), new XmlSourceTest.Train("Henry", XmlSourceTest.Train.TRAIN_NUMBER_UNDEFINED, null, null), new XmlSourceTest.Train("James", XmlSourceTest.Train.TRAIN_NUMBER_UNDEFINED, null, null));
        Assert.assertThat(trainsToStrings(expectedResults), Matchers.containsInAnyOrder(trainsToStrings(readEverythingFromReader(source.createReader(null))).toArray()));
    }

    @Test
    public void testReadXMLWithMultiByteChars() throws IOException {
        File file = tempFolder.newFile("trainXMLTiny");
        Files.write(file.toPath(), xmlWithMultiByteChars.getBytes(StandardCharsets.UTF_8));
        BoundedSource<XmlSourceTest.Train> source = XmlIO.<XmlSourceTest.Train>read().from(file.toPath().toString()).withRootElement("trains").withRecordElement("train").withRecordClass(XmlSourceTest.Train.class).withMinBundleSize(1024).createSource();
        List<XmlSourceTest.Train> expectedResults = ImmutableList.of(new XmlSourceTest.Train("Thomas?", XmlSourceTest.Train.TRAIN_NUMBER_UNDEFINED, null, null), new XmlSourceTest.Train("Hen?ry", XmlSourceTest.Train.TRAIN_NUMBER_UNDEFINED, null, null), new XmlSourceTest.Train("Jam?es", XmlSourceTest.Train.TRAIN_NUMBER_UNDEFINED, null, null));
        Assert.assertThat(trainsToStrings(expectedResults), Matchers.containsInAnyOrder(trainsToStrings(readEverythingFromReader(source.createReader(null))).toArray()));
    }

    @Test
    public void testSplitWithEmptyBundleAtEnd() throws Exception {
        File file = tempFolder.newFile("trainXMLTiny");
        Files.write(file.toPath(), tinyXML.getBytes(StandardCharsets.UTF_8));
        BoundedSource<XmlSourceTest.Train> source = XmlIO.<XmlSourceTest.Train>read().from(file.toPath().toString()).withRootElement("trains").withRecordElement("train").withRecordClass(XmlSourceTest.Train.class).withMinBundleSize(10).createSource();
        List<? extends BoundedSource<XmlSourceTest.Train>> splits = source.split(50, null);
        Assert.assertTrue(((splits.size()) > 2));
        List<XmlSourceTest.Train> results = new ArrayList<>();
        for (BoundedSource<XmlSourceTest.Train> split : splits) {
            results.addAll(readEverythingFromReader(split.createReader(null)));
        }
        List<XmlSourceTest.Train> expectedResults = ImmutableList.of(new XmlSourceTest.Train("Thomas", XmlSourceTest.Train.TRAIN_NUMBER_UNDEFINED, null, null), new XmlSourceTest.Train("Henry", XmlSourceTest.Train.TRAIN_NUMBER_UNDEFINED, null, null), new XmlSourceTest.Train("James", XmlSourceTest.Train.TRAIN_NUMBER_UNDEFINED, null, null));
        Assert.assertThat(trainsToStrings(expectedResults), Matchers.containsInAnyOrder(trainsToStrings(results).toArray()));
    }

    @Test
    public void testReadXMLSmall() throws IOException {
        File file = tempFolder.newFile("trainXMLSmall");
        Files.write(file.toPath(), trainXML.getBytes(StandardCharsets.UTF_8));
        BoundedSource<XmlSourceTest.Train> source = XmlIO.<XmlSourceTest.Train>read().from(file.toPath().toString()).withRootElement("trains").withRecordElement("train").withRecordClass(XmlSourceTest.Train.class).withMinBundleSize(1024).createSource();
        List<XmlSourceTest.Train> expectedResults = ImmutableList.of(new XmlSourceTest.Train("Thomas", 1, "blue", null), new XmlSourceTest.Train("Henry", 3, "green", null), new XmlSourceTest.Train("Toby", 7, "brown", null), new XmlSourceTest.Train("Gordon", 4, "blue", null), new XmlSourceTest.Train("Emily", (-1), "red", null), new XmlSourceTest.Train("Percy", 6, "green", null));
        Assert.assertThat(trainsToStrings(expectedResults), Matchers.containsInAnyOrder(trainsToStrings(readEverythingFromReader(source.createReader(null))).toArray()));
    }

    @Test
    public void testReadXMLIncorrectRootElement() throws IOException {
        File file = tempFolder.newFile("trainXMLSmall");
        Files.write(file.toPath(), trainXML.getBytes(StandardCharsets.UTF_8));
        BoundedSource<XmlSourceTest.Train> source = XmlIO.<XmlSourceTest.Train>read().from(file.toPath().toString()).withRootElement("something").withRecordElement("train").withRecordClass(XmlSourceTest.Train.class).createSource();
        exception.expectMessage("Unexpected close tag </trains>; expected </something>.");
        readEverythingFromReader(source.createReader(null));
    }

    @Test
    public void testReadXMLIncorrectRecordElement() throws IOException {
        File file = tempFolder.newFile("trainXMLSmall");
        Files.write(file.toPath(), trainXML.getBytes(StandardCharsets.UTF_8));
        BoundedSource<XmlSourceTest.Train> source = XmlIO.<XmlSourceTest.Train>read().from(file.toPath().toString()).withRootElement("trains").withRecordElement("something").withRecordClass(XmlSourceTest.Train.class).createSource();
        Assert.assertEquals(readEverythingFromReader(source.createReader(null)), new ArrayList<XmlSourceTest.Train>());
    }

    @XmlRootElement
    private static class WrongTrainType {
        @SuppressWarnings("unused")
        public String something;
    }

    @Test
    public void testReadXMLInvalidRecordClassWithCustomEventHandler() throws IOException {
        File file = tempFolder.newFile("trainXMLSmall");
        Files.write(file.toPath(), trainXML.getBytes(StandardCharsets.UTF_8));
        ValidationEventHandler validationEventHandler = ( event) -> {
            throw new RuntimeException("MyCustomValidationEventHandler failure mesage");
        };
        BoundedSource<XmlSourceTest.WrongTrainType> source = XmlIO.<XmlSourceTest.WrongTrainType>read().from(file.toPath().toString()).withRootElement("trains").withRecordElement("train").withRecordClass(XmlSourceTest.WrongTrainType.class).withValidationEventHandler(validationEventHandler).createSource();
        exception.expect(RuntimeException.class);
        // JAXB internationalizes the error message. So this is all we can match for.
        exception.expectMessage("MyCustomValidationEventHandler failure mesage");
        try (Reader<XmlSourceTest.WrongTrainType> reader = source.createReader(null)) {
            List<XmlSourceTest.WrongTrainType> results = new ArrayList<>();
            for (boolean available = reader.start(); available; available = reader.advance()) {
                XmlSourceTest.WrongTrainType train = reader.getCurrent();
                results.add(train);
            }
        }
    }

    @Test
    public void testReadXmlWithAdditionalFieldsShouldNotThrowException() throws IOException {
        File file = tempFolder.newFile("trainXMLSmall");
        Files.write(file.toPath(), trainXML.getBytes(StandardCharsets.UTF_8));
        BoundedSource<XmlSourceTest.TinyTrain> source = XmlIO.<XmlSourceTest.TinyTrain>read().from(file.toPath().toString()).withRootElement("trains").withRecordElement("train").withRecordClass(XmlSourceTest.TinyTrain.class).createSource();
        List<XmlSourceTest.TinyTrain> expectedResults = ImmutableList.of(new XmlSourceTest.TinyTrain("Thomas"), new XmlSourceTest.TinyTrain("Henry"), new XmlSourceTest.TinyTrain("Toby"), new XmlSourceTest.TinyTrain("Gordon"), new XmlSourceTest.TinyTrain("Emily"), new XmlSourceTest.TinyTrain("Percy"));
        Assert.assertThat(tinyTrainsToStrings(expectedResults), Matchers.containsInAnyOrder(tinyTrainsToStrings(readEverythingFromReader(source.createReader(null))).toArray()));
    }

    @Test
    public void testReadXMLNoBundleSize() throws IOException {
        File file = tempFolder.newFile("trainXMLSmall");
        Files.write(file.toPath(), trainXML.getBytes(StandardCharsets.UTF_8));
        BoundedSource<XmlSourceTest.Train> source = XmlIO.<XmlSourceTest.Train>read().from(file.toPath().toString()).withRootElement("trains").withRecordElement("train").withRecordClass(XmlSourceTest.Train.class).createSource();
        List<XmlSourceTest.Train> expectedResults = ImmutableList.of(new XmlSourceTest.Train("Thomas", 1, "blue", null), new XmlSourceTest.Train("Henry", 3, "green", null), new XmlSourceTest.Train("Toby", 7, "brown", null), new XmlSourceTest.Train("Gordon", 4, "blue", null), new XmlSourceTest.Train("Emily", (-1), "red", null), new XmlSourceTest.Train("Percy", 6, "green", null));
        Assert.assertThat(trainsToStrings(expectedResults), Matchers.containsInAnyOrder(trainsToStrings(readEverythingFromReader(source.createReader(null))).toArray()));
    }

    @Test
    public void testReadXMLWithEmptyTags() throws IOException {
        File file = tempFolder.newFile("trainXMLSmall");
        Files.write(file.toPath(), trainXMLWithEmptyTags.getBytes(StandardCharsets.UTF_8));
        BoundedSource<XmlSourceTest.Train> source = XmlIO.<XmlSourceTest.Train>read().from(file.toPath().toString()).withRootElement("trains").withRecordElement("train").withRecordClass(XmlSourceTest.Train.class).withMinBundleSize(1024).createSource();
        List<XmlSourceTest.Train> expectedResults = ImmutableList.of(new XmlSourceTest.Train("Thomas", 1, "blue", null), new XmlSourceTest.Train("Henry", 3, "green", null), new XmlSourceTest.Train("Toby", 7, "brown", null), new XmlSourceTest.Train("Gordon", 4, "blue", null), new XmlSourceTest.Train("Emily", (-1), "red", null), new XmlSourceTest.Train("Percy", 6, "green", null), new XmlSourceTest.Train(), new XmlSourceTest.Train());
        Assert.assertThat(trainsToStrings(expectedResults), Matchers.containsInAnyOrder(trainsToStrings(readEverythingFromReader(source.createReader(null))).toArray()));
    }

    @Test
    public void testReadXMLWithCharset() throws IOException {
        File file = tempFolder.newFile("trainXMLISO88591");
        Files.write(file.toPath(), trainXMLWithISO88591.getBytes(StandardCharsets.ISO_8859_1));
        PCollection<XmlSourceTest.Train> output = p.apply("ReadFileData", XmlIO.<XmlSourceTest.Train>read().from(file.toPath().toString()).withRootElement("trains").withRecordElement("train").withRecordClass(XmlSourceTest.Train.class).withMinBundleSize(1024).withCharset(StandardCharsets.ISO_8859_1));
        List<XmlSourceTest.Train> expectedResults = ImmutableList.of(new XmlSourceTest.Train("C?dric", 7, "blue", "small"));
        PAssert.that(output).containsInAnyOrder(expectedResults);
        p.run();
    }

    @Test
    @Category(NeedsRunner.class)
    public void testReadXMLSmallPipeline() throws IOException {
        File file = tempFolder.newFile("trainXMLSmall");
        Files.write(file.toPath(), trainXML.getBytes(StandardCharsets.UTF_8));
        PCollection<XmlSourceTest.Train> output = p.apply("ReadFileData", XmlIO.<XmlSourceTest.Train>read().from(file.toPath().toString()).withRootElement("trains").withRecordElement("train").withRecordClass(XmlSourceTest.Train.class).withMinBundleSize(1024));
        List<XmlSourceTest.Train> expectedResults = ImmutableList.of(new XmlSourceTest.Train("Thomas", 1, "blue", null), new XmlSourceTest.Train("Henry", 3, "green", null), new XmlSourceTest.Train("Toby", 7, "brown", null), new XmlSourceTest.Train("Gordon", 4, "blue", null), new XmlSourceTest.Train("Emily", (-1), "red", null), new XmlSourceTest.Train("Percy", 6, "green", null));
        PAssert.that(output).containsInAnyOrder(expectedResults);
        p.run();
    }

    @Test
    public void testReadXMLWithAttributes() throws IOException {
        File file = tempFolder.newFile("trainXMLSmall");
        Files.write(file.toPath(), trainXMLWithAttributes.getBytes(StandardCharsets.UTF_8));
        BoundedSource<XmlSourceTest.Train> source = XmlIO.<XmlSourceTest.Train>read().from(file.toPath().toString()).withRootElement("trains").withRecordElement("train").withRecordClass(XmlSourceTest.Train.class).withMinBundleSize(1024).createSource();
        List<XmlSourceTest.Train> expectedResults = ImmutableList.of(new XmlSourceTest.Train("Thomas", 1, "blue", "small"), new XmlSourceTest.Train("Henry", 3, "green", "big"), new XmlSourceTest.Train("Toby", 7, "brown", "small"), new XmlSourceTest.Train("Gordon", 4, "blue", "big"), new XmlSourceTest.Train("Emily", (-1), "red", "small"), new XmlSourceTest.Train("Percy", 6, "green", "small"));
        Assert.assertThat(trainsToStrings(expectedResults), Matchers.containsInAnyOrder(trainsToStrings(readEverythingFromReader(source.createReader(null))).toArray()));
    }

    @Test
    public void testReadXMLWithWhitespaces() throws IOException {
        File file = tempFolder.newFile("trainXMLSmall");
        Files.write(file.toPath(), trainXMLWithSpaces.getBytes(StandardCharsets.UTF_8));
        BoundedSource<XmlSourceTest.Train> source = XmlIO.<XmlSourceTest.Train>read().from(file.toPath().toString()).withRootElement("trains").withRecordElement("train").withRecordClass(XmlSourceTest.Train.class).withMinBundleSize(1024).createSource();
        List<XmlSourceTest.Train> expectedResults = ImmutableList.of(new XmlSourceTest.Train("Thomas   ", 1, "blue", null), new XmlSourceTest.Train("Henry", 3, "green", null), new XmlSourceTest.Train("Toby", 7, "  brown  ", null), new XmlSourceTest.Train("Gordon", 4, "blue", null), new XmlSourceTest.Train("Emily", (-1), "red", null), new XmlSourceTest.Train("Percy", 6, "green", null));
        Assert.assertThat(trainsToStrings(expectedResults), Matchers.containsInAnyOrder(trainsToStrings(readEverythingFromReader(source.createReader(null))).toArray()));
    }

    @Test
    public void testReadXMLLarge() throws IOException {
        String fileName = "temp.xml";
        List<XmlSourceTest.Train> trains = generateRandomTrainList(100);
        File file = createRandomTrainXML(fileName, trains);
        BoundedSource<XmlSourceTest.Train> source = XmlIO.<XmlSourceTest.Train>read().from(file.toPath().toString()).withRootElement("trains").withRecordElement("train").withRecordClass(XmlSourceTest.Train.class).withMinBundleSize(1024).createSource();
        Assert.assertThat(trainsToStrings(trains), Matchers.containsInAnyOrder(trainsToStrings(readEverythingFromReader(source.createReader(null))).toArray()));
    }

    @Test
    public void testSplitWithEmptyBundles() throws Exception {
        String fileName = "temp.xml";
        List<XmlSourceTest.Train> trains = generateRandomTrainList(10);
        File file = createRandomTrainXML(fileName, trains);
        BoundedSource<XmlSourceTest.Train> source = XmlIO.<XmlSourceTest.Train>read().from(file.toPath().toString()).withRootElement("trains").withRecordElement("train").withRecordClass(XmlSourceTest.Train.class).withMinBundleSize(10).createSource();
        List<? extends BoundedSource<XmlSourceTest.Train>> splits = source.split(100, null);
        Assert.assertTrue(((splits.size()) > 2));
        List<XmlSourceTest.Train> results = new ArrayList<>();
        for (BoundedSource<XmlSourceTest.Train> split : splits) {
            results.addAll(readEverythingFromReader(split.createReader(null)));
        }
        Assert.assertThat(trainsToStrings(trains), Matchers.containsInAnyOrder(trainsToStrings(results).toArray()));
    }

    @Test
    public void testXMLWithSplits() throws Exception {
        String fileName = "temp.xml";
        List<XmlSourceTest.Train> trains = generateRandomTrainList(100);
        File file = createRandomTrainXML(fileName, trains);
        BoundedSource<XmlSourceTest.Train> source = XmlIO.<XmlSourceTest.Train>read().from(file.toPath().toString()).withRootElement("trains").withRecordElement("train").withRecordClass(XmlSourceTest.Train.class).withMinBundleSize(10).createSource();
        List<? extends BoundedSource<XmlSourceTest.Train>> splits = source.split(256, null);
        // Not a trivial split
        Assert.assertTrue(((splits.size()) > 2));
        List<XmlSourceTest.Train> results = new ArrayList<>();
        for (BoundedSource<XmlSourceTest.Train> split : splits) {
            results.addAll(readEverythingFromReader(split.createReader(null)));
        }
        Assert.assertThat(trainsToStrings(trains), Matchers.containsInAnyOrder(trainsToStrings(results).toArray()));
    }

    @Test
    public void testSplitAtFraction() throws Exception {
        PipelineOptions options = PipelineOptionsFactory.create();
        String fileName = "temp.xml";
        List<XmlSourceTest.Train> trains = generateRandomTrainList(100);
        File file = createRandomTrainXML(fileName, trains);
        BoundedSource<XmlSourceTest.Train> fileSource = XmlIO.<XmlSourceTest.Train>read().from(file.toPath().toString()).withRootElement("trains").withRecordElement("train").withRecordClass(XmlSourceTest.Train.class).withMinBundleSize(10).createSource();
        List<? extends BoundedSource<XmlSourceTest.Train>> splits = fileSource.split(((file.length()) / 3), null);
        for (BoundedSource<XmlSourceTest.Train> splitSource : splits) {
            int numItems = readEverythingFromReader(splitSource.createReader(null)).size();
            // Should not split while unstarted.
            assertSplitAtFractionFails(splitSource, 0, 0.7, options);
            assertSplitAtFractionSucceedsAndConsistent(splitSource, 1, 0.7, options);
            assertSplitAtFractionSucceedsAndConsistent(splitSource, 15, 0.7, options);
            assertSplitAtFractionFails(splitSource, 0, 0.0, options);
            assertSplitAtFractionFails(splitSource, 20, 0.3, options);
            assertSplitAtFractionFails(splitSource, numItems, 1.0, options);
            // After reading 100 elements we will be approximately at position
            // 0.99 * (endOffset - startOffset) hence trying to split at fraction 0.9 will be
            // unsuccessful.
            assertSplitAtFractionFails(splitSource, numItems, 0.9, options);
            // Following passes since we can always find a fraction that is extremely close to 1 such that
            // the position suggested by the fraction will be larger than the position the reader is at
            // after reading "items - 1" elements.
            // This also passes for "numItemsToReadBeforeSplit = items" if the position at suggested
            // fraction is larger than the position the reader is at after reading all "items" elements
            // (i.e., the start position of the last element). This is true for most cases but will not
            // be true if reader position is only one less than the end position. (i.e., the last element
            // of the bundle start at the last byte that belongs to the bundle).
            assertSplitAtFractionSucceedsAndConsistent(splitSource, (numItems - 1), 0.999, options);
        }
    }

    @Test
    public void testSplitAtFractionExhaustiveSingleByte() throws Exception {
        PipelineOptions options = PipelineOptionsFactory.create();
        File file = tempFolder.newFile("trainXMLSmall");
        Files.write(file.toPath(), trainXMLWithAllFeaturesSingleByte.getBytes(StandardCharsets.UTF_8));
        BoundedSource<XmlSourceTest.Train> source = XmlIO.<XmlSourceTest.Train>read().from(file.toPath().toString()).withRootElement("trains").withRecordElement("train").withRecordClass(XmlSourceTest.Train.class).createSource();
        assertSplitAtFractionExhaustive(source, options);
    }
}

