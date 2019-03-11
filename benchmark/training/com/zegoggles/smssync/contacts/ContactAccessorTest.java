package com.zegoggles.smssync.contacts;


import ContactsContract.CommonDataKinds.GroupMembership;
import ContactsContract.Data.CONTENT_URI;
import ContactsContract.Groups;
import ContactsContract.Groups.CONTENT_SUMMARY_URI;
import RuntimeEnvironment.application;
import android.content.ContentResolver;
import android.database.MatrixCursor;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class ContactAccessorTest {
    ContactAccessor accessor;

    @Mock
    ContentResolver resolver;

    @Test
    public void shouldAccessContactsWithEverybody() throws Exception {
        Map<Integer, Group> groups = accessor.getGroups(resolver, application.getResources());
        assertThat(groups).hasSize(1);
        Group everybody = groups.get(ContactAccessor.EVERYBODY_ID);
        assertThat(everybody.title).isEqualTo("Everybody");
        assertThat(everybody._id).isEqualTo(ContactAccessor.EVERYBODY_ID);
        assertThat(everybody.count).isEqualTo(0);
        Mockito.verify(resolver).query(ArgumentMatchers.eq(CONTENT_SUMMARY_URI), ArgumentMatchers.any(String[].class), ArgumentMatchers.any(String.class), ArgumentMatchers.any(String[].class), ArgumentMatchers.eq(((Groups.TITLE) + " ASC")));
    }

    @Test
    public void shouldGetGroupsFromResolver() throws Exception {
        MatrixCursor cursor = new MatrixCursor(new String[]{ Groups._ID, Groups.TITLE, Groups.SUMMARY_COUNT });
        cursor.addRow(new Object[]{ 23, "Testing", 42 });
        Mockito.when(resolver.query(ArgumentMatchers.eq(CONTENT_SUMMARY_URI), ArgumentMatchers.any(String[].class), ArgumentMatchers.any(String.class), ArgumentMatchers.any(String[].class), ArgumentMatchers.eq(((Groups.TITLE) + " ASC")))).thenReturn(cursor);
        Map<Integer, Group> groups = accessor.getGroups(resolver, application.getResources());
        assertThat(groups).hasSize(2);
        Group everybody = groups.get(23);
        assertThat(everybody.title).isEqualTo("Testing");
        assertThat(everybody._id).isEqualTo(23);
        assertThat(everybody.count).isEqualTo(42);
    }

    @Test
    public void shouldGetGroupContactIdsEmpty() throws Exception {
        ContactGroupIds ids = accessor.getGroupContactIds(resolver, new ContactGroup(1));
        assertThat(ids.isEmpty()).isTrue();
        Mockito.verify(resolver).query(ArgumentMatchers.eq(CONTENT_URI), ArgumentMatchers.eq(new String[]{ GroupMembership.CONTACT_ID, GroupMembership.RAW_CONTACT_ID, GroupMembership.GROUP_ROW_ID }), ArgumentMatchers.eq(((((GroupMembership.GROUP_ROW_ID) + " = ? AND ") + (GroupMembership.MIMETYPE)) + " = ?")), ArgumentMatchers.eq(new String[]{ String.valueOf(1), GroupMembership.CONTENT_ITEM_TYPE }), ArgumentMatchers.any(String.class));
    }

    @Test
    public void shouldGetGroupContactIdsFromResolver() throws Exception {
        MatrixCursor cursor = new MatrixCursor(new String[]{ GroupMembership.CONTACT_ID, GroupMembership.RAW_CONTACT_ID, GroupMembership.GROUP_ROW_ID });
        cursor.addRow(new Object[]{ 123L, 256L, 789L });
        Mockito.when(resolver.query(ArgumentMatchers.eq(CONTENT_URI), ArgumentMatchers.any(String[].class), ArgumentMatchers.any(String.class), ArgumentMatchers.any(String[].class), ArgumentMatchers.any(String.class))).thenReturn(cursor);
        ContactGroupIds ids = accessor.getGroupContactIds(resolver, new ContactGroup(1));
        assertThat(ids.getIds().contains(123L));
        assertThat(ids.getRawIds().contains(256L));
    }
}

