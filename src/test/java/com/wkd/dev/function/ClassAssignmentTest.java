package com.wkd.dev.function;

import com.wkd.dev.entity.Class;
import com.wkd.dev.entity.Student;
import com.wkd.dev.mapper.ClassMapper;
import com.wkd.dev.mapper.StudentMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("분반 테스트 - Student, Class")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest
public class ClassAssignmentTest {

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private ClassMapper classMapper;

    @DisplayName("학생 전체 조회 테스트")
    @Test
    void selectAllStudentsTest() {
        List<Student> allStudents = studentMapper.selectAllStudents();

        assertThat(allStudents).isNotEmpty().hasSize(239);
    }

    // 성적순 지그재그
    // 자연/인문 나누는데 한 반에 다 가득차지 않으면 자연/인문 절반씩 혼합하는 경우 고려 필요
    // 성적+계열 조합 반편성 고려 필요
    // 앞번호의 반은 문과/ 뒷번호의 반은 이과 / 문이과 계열의 학생수 비율이 다를 경우 문이과 합반이 발생하는 케이스 고려 필요

    @DisplayName("학급 편성 테스트 - 성적별 지그재그 편성")
    @Transactional
    @Test
    void assignClassByScoreTest() {
        List<Student> allStudents = studentMapper.selectAllStudents();
        Collections.sort(allStudents, Comparator.comparingInt(Student::getRank));
        Queue<Student> studentsQueue = new LinkedList<>(allStudents);

        int stdCount = studentsQueue.size();
        int clsCount = 10;

        List<Integer> result = 학급인원편성(stdCount, clsCount);

        // 일단 자료구조를 이용해 학급편성 로직 설계해보기
        List<Student[]> classes = result.stream()
                .map(Student[]::new)
                .collect(Collectors.toList());

        int idx = 0, direction = 1;
        while(!studentsQueue.isEmpty()) {
            Student[] cls = classes.get(idx);

            for(int i = 0; i < cls.length; i++) {
                if(Objects.isNull(cls[i])) {
                    cls[i] = studentsQueue.poll();
                    break;
                }
            }

            classes.set(idx, cls);

            idx += direction;

            if(idx == clsCount) {
                direction = -1;
                idx--;
            } else if(idx < 0) {
                direction = 1;
                idx++;
            }
        }

        for(int i = 0; i < classes.size(); i++) {
            Student[] students = classes.get(i);

            System.out.println("=== " + (i+1) + "반 (학급인원: "+ students.length +"명) ===");
            for(int j = 0; j < students.length; j++) {
                System.out.print("(" + students[j].getRank() + "등_" + students[j].getStudent_name() + "), ");
            }
            System.out.println();
        }

    }

    @DisplayName("학급 편성 테스트 - 성적별 + 성별별 편성)")
    @Transactional
    @Test
    void assignClassByGenderTest() {

        // TODO: 성별별은 남녀 두개로 쪼갠 후 두가지의 성적별 학급편성 로직 태우면 된다.

        List<Student> allStudents = studentMapper.selectAllStudents();
        Collections.sort(allStudents, Comparator.comparingInt(Student::getRank));

        List<Student> maleStudents = new ArrayList<>();
        List<Student> femaleStudents = new ArrayList<>();

        allStudents.forEach(std -> {
            if("M".equals(std.getGender())) maleStudents.add(std);
            else femaleStudents.add(std);
        });

        // 남녀비율 맞춰서 clsCnt 달리하는 로직 필요. 현재는 고정상수 할당.

        List<Student[]> maleClasses = 성적별학급편성(maleStudents, 5);
        List<Student[]> femaleClasses = 성적별학급편성(femaleStudents, 5);
    }

    @DisplayName("학급 편성 테스트 - 성적별 + 계열별 편성")
    @Transactional
    @Test
    void assignClassByDepartmentTest() {

        // TODO: 계열별은 문이과 두개로 쪼갠 후 두가지의 성적별 학급편성 로직 태우면 된다.

        List<Student> allStudents = studentMapper.selectAllStudents();
        Collections.sort(allStudents, Comparator.comparingInt(Student::getRank));

        List<Student> humanities = new ArrayList<>();
        List<Student> natureScis = new ArrayList<>();

        allStudents.forEach(std -> {
            if("human".equals(std.getDepartment())) humanities.add(std);
            else natureScis.add(std);
        });

        List<Student[]> humanClasses = 성적별학급편성(humanities, 5);
        List<Student[]> natureClasses = 성적별학급편성(natureScis, 5);
    }

    @DisplayName("학급 편성 테스트 - 성적별 + 성별별 + 계열별 편성")
    @Transactional
    @Test
    void assignClassByAllCriteriaTest() {
        List<Student> allStudents = studentMapper.selectAllStudents();

        // TODO: 1) 성별을 2개로 쪼개고, 2) 나뉘어진 2개 그룹에 대해 계열을 또 쪼개고, 3) 총 4가지 케이스의 성적별 학급편성 로직 태우면 된다.
    }

    @DisplayName("성별 기준 학급당 인원 편성 및 성적순 학급편성 테스트")
    @Test
    void 학급당인원편성테스트_성별기준() {
        List<Student> allStudents = studentMapper.selectAllStudents();
        Collections.sort(allStudents, Comparator.comparingInt(Student::getRank));   // 성적순 오름차순

        int clsCount = 8;

        List<Student> maleStudents = new ArrayList<>();
        List<Student> femaleStudents = new ArrayList<>();

        allStudents.forEach(std -> {
            if("M".equals(std.getGender())) maleStudents.add(std);
            else femaleStudents.add(std);
        });

        // 남자 0.66, 여자 0.33
        // 남자 5.35, 여자 2.64 -> (반올림시) 남자 5, 여자 3
        //System.out.println("남자학생/전체학생*설정학급수 = " + ((double) maleStudents.size()/allStudents.size()*clsCount));
        //System.out.println("여자학생/전체학생*설정학급수 = " + ((double) femaleStudents.size()/allStudents.size()*clsCount));

        int maleClsCount = (int) Math.round((double) maleStudents.size()/allStudents.size() * clsCount);
        int femaleClsCount = (int) Math.round((double) femaleStudents.size()/allStudents.size() * clsCount);

        System.out.println("-----------남자 반-------------");
        List<Student[]> maleClasses = 성적별학급편성(maleStudents, maleClsCount);
        System.out.println("\n-----------여자 반-------------");
        List<Student[]> femaleClasses = 성적별학급편성(femaleStudents, femaleClsCount);
    }

    @DisplayName("(성별 + 계열) 기준 학급당 인원 편성 및 성적순 학급편성 테스트")
    @Test
    void 학급당인원편성테스트_성별계열기준() {
        List<Student> allStudents = studentMapper.selectAllStudents();
        Collections.sort(allStudents, Comparator.comparingInt(Student::getRank));   // 성적순 오름차순

        int clsCount = 10;

        List<Student> male_humanities_students = new ArrayList<>();
        List<Student> male_natureScis_students = new ArrayList<>();
        List<Student> female_humanities_students = new ArrayList<>();
        List<Student> female_natureScis_students = new ArrayList<>();

        allStudents.forEach(std -> {
            if("M".equals(std.getGender())) {
                if("human".equals(std.getDepartment())) male_humanities_students.add(std);
                else male_natureScis_students.add(std);
            } else {
                if("human".equals(std.getDepartment())) female_humanities_students.add(std);
                else female_natureScis_students.add(std);
            }
        });

        int maleStdsCount = male_humanities_students.size() + male_natureScis_students.size();
        int femaleStdsCount = female_humanities_students.size() + female_natureScis_students.size();

        int maleClsCount = (int) Math.round((double) maleStdsCount/allStudents.size() * clsCount);
        int femaleClsCount = (int) Math.round((double) femaleStdsCount/allStudents.size() * clsCount);

        // 최종 학급 인원수 배정
        int maleHumanClsCount = (int) Math.round((double) male_humanities_students.size()/maleStdsCount * maleClsCount);
        int maleNatureClsCount = (int) Math.round((double) male_natureScis_students.size()/maleStdsCount * maleClsCount);

        int femaleHumanClsCount = (int) Math.round((double) female_humanities_students.size()/femaleStdsCount * femaleClsCount);
        int femaleNatureClsCount = (int) Math.round((double) female_natureScis_students.size()/femaleStdsCount * femaleClsCount);;

        List<Student[]> 남자문과 = 성적별학급편성(male_humanities_students, maleHumanClsCount);
        List<Student[]> 남자이과 = 성적별학급편성(male_natureScis_students, maleNatureClsCount);
        List<Student[]> 여자문과 = 성적별학급편성(female_humanities_students, femaleHumanClsCount);
        List<Student[]> 여자이과 = 성적별학급편성(female_natureScis_students, femaleNatureClsCount);

        int len = Math.max(Math.max(남자문과.size(), 남자이과.size()), Math.max(여자문과.size(), 여자이과.size()));

        System.out.println("\n");
        for(int i = 0; i < len; i++) {
            if(i < 남자문과.size()) {
                System.out.println("----남자문과반 시작----");
                printStudents(남자문과.get(i));
                System.out.println("----남자문과반 끝----\n");
            }
            if(i < 남자이과.size()) {
                System.out.println("----남자이과반 시작----");
                printStudents(남자이과.get(i));
                System.out.println("----남자이과반 끝----\n");
            }
            if(i < 여자문과.size()) {
                System.out.println("----여자문과반 시작----");
                printStudents(여자문과.get(i));
                System.out.println("----여자문과반 끝----\n");
            }
            if(i < 여자이과.size()) {
                System.out.println("----여자이과반 시작----");
                printStudents(여자이과.get(i));
                System.out.println("----여자이과반 끝----\n");
            }

            System.out.println("===============================================================\n");
        }

        System.out.println("남자문과반 수 = " + 남자문과.size());
        System.out.println("남자이과반 수 = " + 남자이과.size());
        System.out.println("여자문과반 수 = " + 여자문과.size());
        System.out.println("여자이과반 수 = " + 여자이과.size());
    }

    private void printStudents(Student[] stds) {
        System.out.println("(* 해당 반 인원은 = " + stds.length + "명)");
        for(int i = 0; i < stds.length; i++) {
            Student std = stds[i];
            System.out.print(std.getStudent_name() + ", ");
        }
        System.out.println("");
    }


    @DisplayName("학급 편성 테스트 - 학생수, 학급수에 따른 반 capacity 편성")
    @Test
    void 학급인원편성테스트() {
        List<Integer> result = 학급인원편성(413, 13);

        result.forEach(System.out::println);
    }

    private List<Student[]> 성적별학급편성(List<Student> all, int clsCount) {
        Queue<Student> studentsQueue = new LinkedList<>(all);

        int stdCount = studentsQueue.size();

        List<Integer> result = 학급인원편성(stdCount, clsCount);

        // 일단 자료구조를 이용해 학급편성 로직 설계해보기
        List<Student[]> classes = result.stream()
                .map(Student[]::new)
                .collect(Collectors.toList());

        int idx = 0, direction = 1;
        while(!studentsQueue.isEmpty()) {
            Student[] cls = classes.get(idx);

            for(int i = 0; i < cls.length; i++) {
                if(Objects.isNull(cls[i])) {
                    cls[i] = studentsQueue.poll();
                    break;
                }
            }

            classes.set(idx, cls);

            idx += direction;

            if(idx == clsCount) {
                direction = -1;
                idx--;
            } else if(idx < 0) {
                direction = 1;
                idx++;
            }
        }

        for(int i = 0; i < classes.size(); i++) {
            Student[] students = classes.get(i);

            System.out.println("=== " + (i+1) + "반 (학급인원: "+ students.length +"명) ===");
            for(int j = 0; j < students.length; j++) {
                System.out.print("(" + students[j].getRank() + "등_" + students[j].getStudent_name() + "), ");
            }
            System.out.println();
        }

        return classes;
    }

    /**
     * @param : 전체학생수, 편성할 학급수
     * */
    private List<Integer> 학급인원편성(int stdCount, int clsCount) {
        int share = stdCount / clsCount;
        int remainder = stdCount % clsCount;

        List<Integer> classSizes = new ArrayList<>();

        for(int i = 0; i < clsCount; i++) {
            if (remainder > 0) {
                classSizes.add(share + 1);
                remainder--;
            } else {
                classSizes.add(share);
            }
        }

        return classSizes;
    }
}
