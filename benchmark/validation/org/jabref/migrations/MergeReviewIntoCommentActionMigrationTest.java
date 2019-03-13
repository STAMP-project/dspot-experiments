package org.jabref.migrations;


import FieldName.COMMENT;
import FieldName.REVIEW;
import java.util.Collections;
import org.jabref.logic.importer.ParserResult;
import org.jabref.model.entry.BibEntry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


class MergeReviewIntoCommentActionMigrationTest {
    private MergeReviewIntoCommentMigration action;

    @Test
    public void noFields() {
        BibEntry entry = createMinimalBibEntry();
        ParserResult actualParserResult = new ParserResult(Collections.singletonList(entry));
        action.performMigration(actualParserResult);
        Assertions.assertEquals(entry, actualParserResult.getDatabase().getEntryByKey("Entry1").get());
    }

    @Test
    public void reviewField() {
        BibEntry actualEntry = createMinimalBibEntry();
        actualEntry.setField(REVIEW, "My Review");
        ParserResult actualParserResult = new ParserResult(Collections.singletonList(actualEntry));
        BibEntry expectedEntry = createMinimalBibEntry();
        expectedEntry.setField(COMMENT, "My Review");
        action.performMigration(actualParserResult);
        Assertions.assertEquals(expectedEntry, actualParserResult.getDatabase().getEntryByKey("Entry1").get());
    }

    @Test
    public void commentField() {
        BibEntry entry = createMinimalBibEntry();
        entry.setField(COMMENT, "My Comment");
        ParserResult actualParserResult = new ParserResult(Collections.singletonList(entry));
        action.performMigration(actualParserResult);
        Assertions.assertEquals(entry, actualParserResult.getDatabase().getEntryByKey("Entry1").get());
    }

    @Test
    public void multiLineReviewField() {
        String commentString = "My Review\n\nSecond Paragraph\n\nThird Paragraph";
        BibEntry actualEntry = createMinimalBibEntry();
        actualEntry.setField(REVIEW, commentString);
        ParserResult actualParserResult = new ParserResult(Collections.singletonList(actualEntry));
        BibEntry expectedEntry = createMinimalBibEntry();
        expectedEntry.setField(COMMENT, commentString);
        action.performMigration(actualParserResult);
        Assertions.assertEquals(expectedEntry, actualParserResult.getDatabase().getEntryByKey("Entry1").get());
    }
}

