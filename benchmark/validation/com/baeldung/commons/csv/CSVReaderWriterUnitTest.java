package com.baeldung.commons.csv;


import CSVFormat.DEFAULT;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.junit.Assert;
import org.junit.Test;


public class CSVReaderWriterUnitTest {
    public static final Map<String, String> AUTHOR_BOOK_MAP = Collections.unmodifiableMap(new LinkedHashMap<String, String>() {
        {
            put("Dan Simmons", "Hyperion");
            put("Douglas Adams", "The Hitchhiker's Guide to the Galaxy");
        }
    });

    public static final String[] HEADERS = new String[]{ "author", "title" };

    public static final String EXPECTED_FILESTREAM = "author,title\r\n" + ("Dan Simmons,Hyperion\r\n" + "Douglas Adams,The Hitchhiker's Guide to the Galaxy");

    @Test
    public void givenCSVFile_whenRead_thenContentsAsExpected() throws IOException {
        Reader in = new FileReader("src/test/resources/book.csv");
        Iterable<CSVRecord> records = DEFAULT.withHeader(CSVReaderWriterUnitTest.HEADERS).withFirstRecordAsHeader().parse(in);
        for (CSVRecord record : records) {
            String author = record.get("author");
            String title = record.get("title");
            Assert.assertEquals(CSVReaderWriterUnitTest.AUTHOR_BOOK_MAP.get(author), title);
        }
    }

    @Test
    public void givenAuthorBookMap_whenWrittenToStream_thenOutputStreamAsExpected() throws IOException {
        StringWriter sw = new StringWriter();
        try (final CSVPrinter printer = new CSVPrinter(sw, DEFAULT.withHeader(CSVReaderWriterUnitTest.HEADERS))) {
            CSVReaderWriterUnitTest.AUTHOR_BOOK_MAP.forEach(( author, title) -> {
                try {
                    printer.printRecord(author, title);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
        Assert.assertEquals(CSVReaderWriterUnitTest.EXPECTED_FILESTREAM, sw.toString().trim());
    }
}

