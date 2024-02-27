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
        int clsCount = 9;

        List<Integer> result = 학급인원편성(stdCount, clsCount);

        // 일단 자료구조를 이용해 학급편성 로직 설계해보기
        List<Student[]> classes = result.stream()
                .map(size -> new Student[size])
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
        List<Student> allStudents = studentMapper.selectAllStudents();

        // TODO: 성별별은 남녀 두개로 쪼갠 후 두가지의 성적별 학급편성 로직 태우면 된다.

    }

    @DisplayName("학급 편성 테스트 - 성적별 + 계열별 편성")
    @Transactional
    @Test
    void assignClassByDepartmentTest() {
        List<Student> allStudents = studentMapper.selectAllStudents();

        // TODO: 계열별은 문이과 두개로 쪼갠 후 두가지의 성적별 학급편성 로직 태우면 된다.
    }

    @DisplayName("학급 편성 테스트 - 성적별 + 성별별 + 계열별 편성")
    @Transactional
    @Test
    void assignClassByAllCriteriaTest() {
        List<Student> allStudents = studentMapper.selectAllStudents();

        // TODO: 1) 성별을 2개로 쪼개고, 2) 나뉘어진 2개 그룹에 대해 계열을 또 쪼개고, 3) 총 4가지 케이스의 성적별 학급편성 로직 태우면 된다.
    }



    @DisplayName("학급 편성 테스트 - 학생수, 학급수에 따른 반 capacity 편성")
    @Test
    void 학급인원편성테스트() {
        List<Integer> result = 학급인원편성(413, 13);

        result.forEach(System.out::println);
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


