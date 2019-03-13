/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.document;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class DocumentFileTest {
    private static final String FILE_PATH = "psvm.java";

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private File temporaryFile;

    @Test
    public void insertAtStartOfTheFileShouldSucceed() throws IOException {
        writeContentToTemporaryFile("static void main(String[] args) {}");
        try (DocumentFile documentFile = new DocumentFile(temporaryFile, StandardCharsets.UTF_8)) {
            documentFile.insert(0, 0, "public ");
        }
        try (FileInputStream stream = new FileInputStream(temporaryFile)) {
            final String actualContent = new String(readAllBytes(stream));
            Assert.assertEquals("public static void main(String[] args) {}", actualContent);
        }
    }

    @Test
    public void insertVariousTokensIntoTheFileShouldSucceed() throws IOException {
        writeContentToTemporaryFile("static void main(String[] args) {}");
        try (DocumentFile documentFile = new DocumentFile(temporaryFile, StandardCharsets.UTF_8)) {
            documentFile.insert(0, 0, "public ");
            documentFile.insert(0, 17, "final ");
        }
        try (FileInputStream stream = new FileInputStream(temporaryFile)) {
            final String actualContent = new String(readAllBytes(stream));
            Assert.assertEquals("public static void main(final String[] args) {}", actualContent);
        }
    }

    @Test
    public void insertAtTheEndOfTheFileShouldSucceed() throws IOException {
        final String code = "public static void main(String[] args)";
        writeContentToTemporaryFile(code);
        try (DocumentFile documentFile = new DocumentFile(temporaryFile, StandardCharsets.UTF_8)) {
            documentFile.insert(0, code.length(), "{}");
        }
        try (FileInputStream stream = new FileInputStream(temporaryFile)) {
            final String actualContent = new String(readAllBytes(stream));
            Assert.assertEquals("public static void main(String[] args){}", actualContent);
        }
    }

    @Test
    public void removeTokenShouldSucceed() throws IOException {
        final String code = "public static void main(final String[] args) {}";
        writeContentToTemporaryFile(code);
        try (DocumentFile documentFile = new DocumentFile(temporaryFile, StandardCharsets.UTF_8)) {
            documentFile.delete(new RegionByLineImp(0, 0, 24, 30));
        }
        try (FileInputStream stream = new FileInputStream(temporaryFile)) {
            final String actualContent = new String(readAllBytes(stream));
            Assert.assertEquals("public static void main(String[] args) {}", actualContent);
        }
    }

    @Test
    public void insertAndRemoveTokensShouldSucceed() throws IOException {
        final String code = "static void main(final String[] args) {}";
        writeContentToTemporaryFile(code);
        try (DocumentFile documentFile = new DocumentFile(temporaryFile, StandardCharsets.UTF_8)) {
            documentFile.insert(0, 0, "public ");
            documentFile.delete(new RegionByLineImp(0, 0, 17, 23));
        }
        try (FileInputStream stream = new FileInputStream(temporaryFile)) {
            final String actualContent = new String(readAllBytes(stream));
            Assert.assertEquals("public static void main(String[] args) {}", actualContent);
        }
    }

    @Test
    public void insertAndDeleteVariousTokensShouldSucceed() throws IOException {
        final String code = "void main(String[] args) {}";
        writeContentToTemporaryFile(code);
        try (DocumentFile documentFile = new DocumentFile(temporaryFile, StandardCharsets.UTF_8)) {
            documentFile.insert(0, 0, "public ");
            documentFile.insert(0, 0, "static ");
            documentFile.delete(new RegionByLineImp(0, 0, 0, 4));
            documentFile.insert(0, 10, "final ");
            documentFile.delete(new RegionByLineImp(0, 0, 25, 28));
        }
        try (FileInputStream stream = new FileInputStream(temporaryFile)) {
            final String actualContent = new String(readAllBytes(stream));
            Assert.assertEquals("public static  main(final String[] args) ", actualContent);
        }
    }

    @Test
    public void replaceATokenShouldSucceed() throws IOException {
        final String code = "int main(String[] args) {}";
        writeContentToTemporaryFile(code);
        try (DocumentFile documentFile = new DocumentFile(temporaryFile, StandardCharsets.UTF_8)) {
            documentFile.replace(new RegionByLineImp(0, 0, 0, 3), "void");
        }
        try (FileInputStream stream = new FileInputStream(temporaryFile)) {
            final String actualContent = new String(readAllBytes(stream));
            Assert.assertEquals("void main(String[] args) {}", actualContent);
        }
    }

    @Test
    public void replaceVariousTokensShouldSucceed() throws IOException {
        final String code = "int main(String[] args) {}";
        writeContentToTemporaryFile(code);
        try (DocumentFile documentFile = new DocumentFile(temporaryFile, StandardCharsets.UTF_8)) {
            documentFile.replace(new RegionByLineImp(0, 0, 0, "int".length()), "void");
            documentFile.replace(new RegionByLineImp(0, 0, 4, (4 + ("main".length()))), "foo");
            documentFile.replace(new RegionByLineImp(0, 0, 9, (9 + ("String".length()))), "CharSequence");
        }
        try (FileInputStream stream = new FileInputStream(temporaryFile)) {
            final String actualContent = new String(readAllBytes(stream));
            Assert.assertEquals("void foo(CharSequence[] args) {}", actualContent);
        }
    }

    @Test
    public void insertDeleteAndReplaceVariousTokensShouldSucceed() throws IOException {
        final String code = "static int main(CharSequence[] args) {}";
        writeContentToTemporaryFile(code);
        try (DocumentFile documentFile = new DocumentFile(temporaryFile, StandardCharsets.UTF_8)) {
            documentFile.insert(0, 0, "public");
            documentFile.delete(new RegionByLineImp(0, 0, 0, 6));
            documentFile.replace(new RegionByLineImp(0, 0, 7, (7 + ("int".length()))), "void");
            documentFile.insert(0, 16, "final ");
            documentFile.replace(new RegionByLineImp(0, 0, 16, (16 + ("CharSequence".length()))), "String");
        }
        try (FileInputStream stream = new FileInputStream(temporaryFile)) {
            final String actualContent = new String(readAllBytes(stream));
            Assert.assertEquals("public void main(final String[] args) {}", actualContent);
        }
    }

    @Test
    public void lineToOffsetMappingWithLineFeedShouldSucceed() throws IOException {
        final String code = ((("public static int main(String[] args) {" + '\n') + "int var;") + '\n') + "}";
        writeContentToTemporaryFile(code);
        final List<Integer> expectedLineToOffset = new ArrayList<>();
        expectedLineToOffset.add(0);
        expectedLineToOffset.add(40);
        expectedLineToOffset.add(49);
        try (DocumentFile documentFile = new DocumentFile(temporaryFile, StandardCharsets.UTF_8)) {
            Assert.assertEquals(expectedLineToOffset, documentFile.getLineToOffset());
        }
    }

    @Test
    public void lineToOffsetMappingWithCarriageReturnFeedLineFeedShouldSucceed() throws IOException {
        final String code = "public static int main(String[] args) {" + ((("\r\n" + "int var;") + "\r\n") + "}");
        writeContentToTemporaryFile(code);
        final List<Integer> expectedLineToOffset = new ArrayList<>();
        expectedLineToOffset.add(0);
        expectedLineToOffset.add(41);
        expectedLineToOffset.add(51);
        try (DocumentFile documentFile = new DocumentFile(temporaryFile, StandardCharsets.UTF_8)) {
            Assert.assertEquals(expectedLineToOffset, documentFile.getLineToOffset());
        }
    }

    @Test
    public void lineToOffsetMappingWithMixedLineSeparatorsShouldSucceed() throws IOException {
        final String code = "public static int main(String[] args) {" + ((("\r\n" + "int var;") + "\n") + "}");
        writeContentToTemporaryFile(code);
        final List<Integer> expectedLineToOffset = new ArrayList<>();
        expectedLineToOffset.add(0);
        expectedLineToOffset.add(41);
        expectedLineToOffset.add(50);
        try (DocumentFile documentFile = new DocumentFile(temporaryFile, StandardCharsets.UTF_8)) {
            Assert.assertEquals(expectedLineToOffset, documentFile.getLineToOffset());
        }
    }
}

