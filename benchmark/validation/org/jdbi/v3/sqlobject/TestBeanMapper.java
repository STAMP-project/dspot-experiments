/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jdbi.v3.sqlobject;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.ValueType;
import org.jdbi.v3.core.mapper.ValueTypeMapper;
import org.jdbi.v3.core.result.LinkedHashMapRowReducer;
import org.jdbi.v3.core.result.RowView;
import org.jdbi.v3.core.rule.H2DatabaseRule;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.config.RegisterColumnMapper;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlBatch;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.UseRowReducer;
import org.junit.Rule;
import org.junit.Test;


public class TestBeanMapper {
    @Rule
    public H2DatabaseRule dbRule = new H2DatabaseRule().withPlugin(new SqlObjectPlugin());

    public static class TestBean {
        private ValueType valueType;

        public ValueType getValueType() {
            return valueType;
        }

        public void setValueType(ValueType valueType) {
            this.valueType = valueType;
        }
    }

    @RegisterColumnMapper(ValueTypeMapper.class)
    public interface TestDao {
        @SqlQuery("select * from testBean")
        @RegisterBeanMapper(TestBeanMapper.TestBean.class)
        List<TestBeanMapper.TestBean> listBeans();

        @SqlQuery("select valueType as bean_value_type from testBean")
        @RegisterBeanMapper(value = TestBeanMapper.TestBean.class, prefix = "bean_")
        List<TestBeanMapper.TestBean> listBeansPrefix();
    }

    Handle h;

    TestBeanMapper.TestDao dao;

    @Test
    public void testMapBean() {
        h.createUpdate("insert into testBean (valueType) values ('foo')").execute();
        List<TestBeanMapper.TestBean> beans = dao.listBeans();
        assertThat(beans).extracting(TestBeanMapper.TestBean::getValueType).containsExactly(ValueType.valueOf("foo"));
    }

    @Test
    public void testMapBeanPrefix() {
        h.createUpdate("insert into testBean (valueType) values ('foo')").execute();
        List<TestBeanMapper.TestBean> beans = dao.listBeansPrefix();
        assertThat(beans).extracting(TestBeanMapper.TestBean::getValueType).containsExactly(ValueType.valueOf("foo"));
    }

    public static class Document {
        private int id;

        private String name;

        private String contents;

        public Document() {
        }

        public Document(int id, String name, String contents) {
            this.id = id;
            this.name = name;
            this.contents = contents;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getContents() {
            return contents;
        }

        public void setContents(String contents) {
            this.contents = contents;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof TestBeanMapper.Document)) {
                return false;
            }
            TestBeanMapper.Document that = ((TestBeanMapper.Document) (obj));
            return (((this.id) == (that.id)) && (Objects.equals(this.name, that.name))) && (Objects.equals(this.contents, that.contents));
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, name, contents);
        }

        @Override
        public String toString() {
            return (((((((("Document{" + "id=") + (id)) + ", name='") + (name)) + '\'') + ", contents='") + (contents)) + '\'') + '}';
        }
    }

    public static class Folder {
        private int id;

        private String name;

        private List<TestBeanMapper.Document> documents = new ArrayList<>();

        public Folder() {
        }

        public Folder(int id, String name, TestBeanMapper.Document... documents) {
            this.id = id;
            this.name = name;
            this.documents = Arrays.asList(documents);
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<TestBeanMapper.Document> getDocuments() {
            return documents;
        }

        public void setDocuments(List<TestBeanMapper.Document> documents) {
            this.documents = documents;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof TestBeanMapper.Folder)) {
                return false;
            }
            TestBeanMapper.Folder that = ((TestBeanMapper.Folder) (obj));
            return (((this.id) == (that.id)) && (Objects.equals(this.name, that.name))) && (Objects.equals(this.documents, that.documents));
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, name, documents);
        }

        @Override
        public String toString() {
            return ((((((("Folder{" + "id=") + (id)) + ", name='") + (name)) + '\'') + ", documents=") + (documents)) + '}';
        }
    }

    private interface DocumentDao extends SqlObject {
        @SqlBatch("insert into folders (id, name) values (:id, :name)")
        void insertFolders(@BindBean
        TestBeanMapper.Folder... folders);

        @SqlBatch("insert into documents (id, folder_id, name, contents) values (:d.id, :f.id, :d.name, :d.contents)")
        void insertDocuments(@BindBean("f")
        TestBeanMapper.Folder folder, @BindBean("d")
        TestBeanMapper.Document... documents);

        @SqlQuery("select f.id f_id, f.name f_name, " + ((("d.id d_id, d.name d_name, d.contents d_contents " + "from folders f left join documents d ") + "on f.id = d.folder_id ") + "where f.id = :folderId"))
        @RegisterBeanMapper(value = TestBeanMapper.Folder.class, prefix = "f")
        @RegisterBeanMapper(value = TestBeanMapper.Document.class, prefix = "d")
        @UseRowReducer(TestBeanMapper.DocumentDao.FolderDocReducer.class)
        Optional<TestBeanMapper.Folder> getFolder(int folderId);

        @SqlQuery("select " + (((("f.id f_id, f.name f_name, " + "d.id d_id, d.name d_name, d.contents d_contents ") + "from folders f left join documents d ") + "on f.id = d.folder_id ") + "order by f.name, d.name"))
        @RegisterBeanMapper(value = TestBeanMapper.Folder.class, prefix = "f")
        @RegisterBeanMapper(value = TestBeanMapper.Document.class, prefix = "d")
        @UseRowReducer(TestBeanMapper.DocumentDao.FolderDocReducer.class)
        List<TestBeanMapper.Folder> listFolders();

        class FolderDocReducer implements LinkedHashMapRowReducer<Integer, TestBeanMapper.Folder> {
            @Override
            public void accumulate(Map<Integer, TestBeanMapper.Folder> map, RowView rv) {
                TestBeanMapper.Folder f = map.computeIfAbsent(rv.getColumn("f_id", Integer.class), ( id) -> rv.getRow(.class));
                if ((rv.getColumn("d_id", Integer.class)) != null) {
                    f.getDocuments().add(rv.getRow(TestBeanMapper.Document.class));
                }
            }
        }
    }

    @Test
    public void testFoldWithPrefixedMappers() {
        h.execute("create table folders (id identity primary key, name varchar(50))");
        h.execute("create table documents (id identity primary key, folder_id integer, name varchar(50), contents varchar(1000))");
        TestBeanMapper.Folder folder1 = new TestBeanMapper.Folder(1, "folder1");
        TestBeanMapper.Folder folder2 = new TestBeanMapper.Folder(2, "folder2");
        TestBeanMapper.Folder folder3 = new TestBeanMapper.Folder(3, "folder3");
        TestBeanMapper.Document doc1 = new TestBeanMapper.Document(4, "doc1.txt", "hello");
        TestBeanMapper.Document doc2 = new TestBeanMapper.Document(5, "doc2.txt", "foo");
        TestBeanMapper.Document doc3 = new TestBeanMapper.Document(6, "doc3.txt", "bar");
        TestBeanMapper.DocumentDao dao = h.attach(TestBeanMapper.DocumentDao.class);
        dao.insertFolders(folder1, folder2, folder3);
        dao.insertDocuments(folder2, doc1);
        dao.insertDocuments(folder3, doc2, doc3);
        assertThat(dao.getFolder(1)).contains(new TestBeanMapper.Folder(1, "folder1"));
        assertThat(dao.getFolder(2)).contains(new TestBeanMapper.Folder(2, "folder2", doc1));
        assertThat(dao.getFolder(3)).contains(new TestBeanMapper.Folder(3, "folder3", doc2, doc3));
        assertThat(dao.listFolders()).containsExactly(new TestBeanMapper.Folder(1, "folder1"), new TestBeanMapper.Folder(2, "folder2", doc1), new TestBeanMapper.Folder(3, "folder3", doc2, doc3));
    }
}

