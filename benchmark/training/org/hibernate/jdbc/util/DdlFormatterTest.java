/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jdbc.util;


import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
public class DdlFormatterTest extends BaseUnitTestCase {
    @Test
    public void testNoLoss() {
        assertNoLoss("drop table post if exists");
        assertNoLoss("drop table post_comment if exists");
        assertNoLoss("drop table post_details if exists");
        assertNoLoss("drop table post_tag if exists");
        assertNoLoss("drop table tag if exists");
        assertNoLoss("create table post (id bigint not null, title varchar(255), primary key (id))");
        assertNoLoss("create table post_comment (id bigint not null, review varchar(255), post_id bigint, primary key (id))");
        assertNoLoss("create table post_details (id bigint not null, created_by varchar(255), created_on timestamp, primary key (id))");
        assertNoLoss("create table post_tag (post_id bigint not null, tag_id bigint not null)");
        assertNoLoss("create table tag (id bigint not null, name varchar(255), primary key (id))");
        assertNoLoss("alter table post_comment add constraint FKna4y825fdc5hw8aow65ijexm0 foreign key (post_id) references post");
        assertNoLoss("alter table post_details add constraint FKkl5eik513p1xiudk2kxb0v92u foreign key (id) references post");
        assertNoLoss("alter table post_tag add constraint FKac1wdchd2pnur3fl225obmlg0 foreign key (tag_id) references tag");
        assertNoLoss("alter table post_tag add constraint FKc2auetuvsec0k566l0eyvr9cs foreign key (post_id) references post");
    }
}

