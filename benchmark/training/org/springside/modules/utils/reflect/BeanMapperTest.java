package org.springside.modules.utils.reflect;


import java.util.List;
import org.junit.Test;
import org.springside.modules.utils.collection.ListUtil;
import org.springside.modules.utils.mapper.BeanMapper;


public class BeanMapperTest {
    @Test
    public void copySingleObject() {
        BeanMapperTest.Student student = new BeanMapperTest.Student("zhang3", 20, new BeanMapperTest.Teacher("li4"), ListUtil.newArrayList("chinese", "english"));
        BeanMapperTest.StudentVO studentVo = BeanMapper.map(student, BeanMapperTest.StudentVO.class);
        assertThat(studentVo.name).isEqualTo("zhang3");
        assertThat(studentVo.getAge()).isEqualTo(20);
        assertThat(studentVo.getTeacher().getName()).isEqualTo("li4");
        assertThat(studentVo.getCourse()).containsExactly("chinese", "english");
    }

    @Test
    public void copyListObject() {
        BeanMapperTest.Student student1 = new BeanMapperTest.Student("zhang3", 20, new BeanMapperTest.Teacher("li4"), ListUtil.newArrayList("chinese", "english"));
        BeanMapperTest.Student student2 = new BeanMapperTest.Student("zhang4", 30, new BeanMapperTest.Teacher("li5"), ListUtil.newArrayList("chinese2", "english4"));
        BeanMapperTest.Student student3 = new BeanMapperTest.Student("zhang5", 40, new BeanMapperTest.Teacher("li6"), ListUtil.newArrayList("chinese3", "english5"));
        List<BeanMapperTest.Student> studentList = ListUtil.newArrayList(student1, student2, student3);
        List<BeanMapperTest.StudentVO> studentVoList = BeanMapper.mapList(studentList, BeanMapperTest.StudentVO.class);
        assertThat(studentVoList).hasSize(3);
        BeanMapperTest.StudentVO studentVo = studentVoList.get(0);
        assertThat(studentVo.name).isEqualTo("zhang3");
        assertThat(studentVo.getAge()).isEqualTo(20);
        assertThat(studentVo.getTeacher().getName()).isEqualTo("li4");
        assertThat(studentVo.getCourse()).containsExactly("chinese", "english");
    }

    @Test
    public void copyArrayObject() {
        BeanMapperTest.Student student1 = new BeanMapperTest.Student("zhang3", 20, new BeanMapperTest.Teacher("li4"), ListUtil.newArrayList("chinese", "english"));
        BeanMapperTest.Student student2 = new BeanMapperTest.Student("zhang4", 30, new BeanMapperTest.Teacher("li5"), ListUtil.newArrayList("chinese2", "english4"));
        BeanMapperTest.Student student3 = new BeanMapperTest.Student("zhang5", 40, new BeanMapperTest.Teacher("li6"), ListUtil.newArrayList("chinese3", "english5"));
        BeanMapperTest.Student[] studentList = new BeanMapperTest.Student[]{ student1, student2, student3 };
        BeanMapperTest.StudentVO[] studentVoList = BeanMapper.mapArray(studentList, BeanMapperTest.StudentVO.class);
        assertThat(studentVoList).hasSize(3);
        BeanMapperTest.StudentVO studentVo = studentVoList[0];
        assertThat(studentVo.name).isEqualTo("zhang3");
        assertThat(studentVo.getAge()).isEqualTo(20);
        assertThat(studentVo.getTeacher().getName()).isEqualTo("li4");
        assertThat(studentVo.getCourse()).containsExactly("chinese", "english");
    }

    public static class Student {
        public String name;

        private int age;

        private BeanMapperTest.Teacher teacher;

        private List<String> course = ListUtil.newArrayList();

        public Student() {
        }

        public Student(String name, int age, BeanMapperTest.Teacher teacher, List<String> course) {
            this.name = name;
            this.age = age;
            this.teacher = teacher;
            this.course = course;
        }

        public List<String> getCourse() {
            return course;
        }

        public void setCourse(List<String> course) {
            this.course = course;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public BeanMapperTest.Teacher getTeacher() {
            return teacher;
        }

        public void setTeacher(BeanMapperTest.Teacher teacher) {
            this.teacher = teacher;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class Teacher {
        private String name;

        public Teacher() {
        }

        public Teacher(String name) {
            super();
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class StudentVO {
        public String name;

        private int age;

        private BeanMapperTest.TeacherVO teacher;

        private List<String> course = ListUtil.newArrayList();

        public StudentVO() {
        }

        public StudentVO(String name, int age, BeanMapperTest.TeacherVO teacher, List<String> course) {
            this.name = name;
            this.age = age;
            this.teacher = teacher;
            this.course = course;
        }

        public List<String> getCourse() {
            return course;
        }

        public void setCourse(List<String> course) {
            this.course = course;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public BeanMapperTest.TeacherVO getTeacher() {
            return teacher;
        }

        public void setTeacher(BeanMapperTest.TeacherVO teacher) {
            this.teacher = teacher;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class TeacherVO {
        private String name;

        public TeacherVO() {
        }

        public TeacherVO(String name) {
            super();
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

