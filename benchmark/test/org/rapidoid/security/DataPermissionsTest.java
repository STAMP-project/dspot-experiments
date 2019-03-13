package org.rapidoid.security;


import java.util.List;
import org.junit.jupiter.api.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.u.U;


@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class DataPermissionsTest extends SecurityTestCommons {
    private static final String[] USERS = new String[]{ null, "", "abc", "adm1", "adm2", "mng1", "mod1", "mod2" };

    @Test
    public void testCommentPermissions() {
        checkPermissions(null, Comment.class, "content", true, false);
        checkPermissions(null, Comment.class, "visible", false, false);
        checkPermissions(null, Comment.class, "createdBy", true, false);
        checkPermissions("", Comment.class, "content", true, false);
        checkPermissions("", Comment.class, "visible", false, false);
        checkPermissions("", Comment.class, "createdBy", true, false);
        checkPermissions("abc", Comment.class, "content", true, false);
        checkPermissions("abc", Comment.class, "visible", false, false);
        checkPermissions("abc", Comment.class, "createdBy", true, false);
    }

    @Test
    public void testIssuePermissions() {
        String[] fields = new String[]{ "title", "year", "author", "description", "comments", "createdBy", "sharedWith" };
        for (String field : fields) {
            for (String user : DataPermissionsTest.USERS) {
                checkPermissions(user, Issue.class, field, false, false);
            }
            checkPermissions("foo", Issue.class, field, false, false);
            checkPermissions("bar", Issue.class, field, false, false);
            checkPermissions("other", Issue.class, field, true, false);
        }
        Issue issue = new Issue();
        for (String field : fields) {
            for (String user : DataPermissionsTest.USERS) {
                checkPermissions(user, Issue.class, issue, field, false, false);
            }
            checkPermissions("foo", Issue.class, issue, field, false, false);
            checkPermissions("bar", Issue.class, issue, field, false, false);
            checkPermissions("other", Issue.class, issue, field, true, false);
        }
        issue.createdBy = "the-owner";
        issue.sharedWith = U.list(new User("bar"));
        for (String field : fields) {
            for (String user : DataPermissionsTest.USERS) {
                checkPermissions(user, Issue.class, issue, field, false, false);
            }
            checkPermissions("the-owner", Issue.class, issue, field, true, true);
            if (field.equals("comments")) {
                checkPermissions("bar", Issue.class, issue, field, true, true);
            } else {
                checkPermissions("bar", Issue.class, issue, field, true, false);
            }
            checkPermissions("other", Issue.class, issue, field, true, false);
        }
        for (String user : DataPermissionsTest.USERS) {
            checkPermissions(user, Issue.class, issue, "id", true, false);
            checkPermissions(user, Issue.class, issue, "notes", U.eq(user, "abc"), U.eq(user, "other"));
        }
    }

    @Test
    public void testCategoryPermissions() {
        for (String user : DataPermissionsTest.USERS) {
            checkPermissions(user, Category.class, "name", true, true);
            checkPermissions(user, Category.class, "desc", true, false);
        }
        checkPermissions("other", Category.class, "desc", true, true);
    }
}

