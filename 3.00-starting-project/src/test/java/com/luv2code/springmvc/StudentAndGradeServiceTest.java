package com.luv2code.springmvc;

import com.luv2code.springmvc.models.*;
import com.luv2code.springmvc.repository.HistoryGradesDao;
import com.luv2code.springmvc.repository.MathGradesDao;
import com.luv2code.springmvc.repository.ScienceGradesDao;
import com.luv2code.springmvc.repository.StudentDao;
import com.luv2code.springmvc.service.StudentAndGradeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource("/application.properties")
@SpringBootTest
public class StudentAndGradeServiceTest {


    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    StudentAndGradeService studentService;

    @Autowired
    StudentDao studentDao;

    @Autowired
    ScienceGradesDao scienceGradesDao;

    @Autowired
    MathGradesDao mathGradesDao;

    @Autowired
    HistoryGradesDao historyGradesDao;



    @BeforeEach
    public void setUpDatabase(){ //h2 sample data
        jdbc.execute("INSERT INTO student(id, firstname, lastname, email_address)" +
                "values(1, 'Eric', 'Roby', 'eric@gmail.com');");

        jdbc.execute("INSERT INTO math_grade(id, student_id,  grade) VALUES(1, 1, 100.0)");

        jdbc.execute("INSERT INTO science_grade(id, student_id, grade) VALUES(1, 1, 100.0)");

        jdbc.execute("INSERT INTO  history_grade(id, student_id, grade) VALUES(1, 1, 100.0)");

    }
    @Test
    public void createStudentService(){
        studentService.createStudent("Chad", "Darby", "chad@gmail.com");


        CollegeStudent student = studentDao.findByEmailAddress("chad@gmail.com");

        assertEquals("chad@gmail.com", student.getEmailAddress(), "find by email");

    }

    @Test
    public void isStudentNullCheck(){
        assertTrue(studentService.checkIfStudentIsNull(1));
        assertFalse(studentService.checkIfStudentIsNull(0));
    }

    @Test
    public void deleteStudentService(){
        Optional<CollegeStudent> deletedCollegeStudent = studentDao.findById(1);

        //student deletion should have collateral --> grade associated w/ student should be deleted
        Optional<MathGrade> deleteMathGrade = mathGradesDao.findById(1);
        Optional<HistoryGrade> deleteHistoryGrade = historyGradesDao.findById(1);
        Optional<ScienceGrade> deleteScienceGrade = scienceGradesDao.findById(1);

        assertTrue(deletedCollegeStudent.isPresent(), "Return True");
        assertTrue(deleteHistoryGrade.isPresent());
        assertTrue(deleteMathGrade.isPresent());
        assertTrue(deleteScienceGrade.isPresent());



        studentService.deleteStudent(1);//actual deletion shoul happen here--> collateral from student deletion



        deletedCollegeStudent = studentDao.findById(1);
        //check if collateral has been done
        deleteMathGrade = mathGradesDao.findById(1);
        deleteHistoryGrade = historyGradesDao.findById(1);
        deleteScienceGrade = scienceGradesDao.findById(1);

        assertFalse( deletedCollegeStudent.isPresent(), "Return False");
        assertFalse( deleteMathGrade.isPresent() );
        assertFalse( deleteHistoryGrade.isPresent());
        assertFalse( deleteScienceGrade.isPresent());



    }

    @Sql("/insertData.sql")
    @Test
    public void getGradebookService(){
        Iterable<CollegeStudent> iterableCollegeStudents = studentService.getGradebook();
        List<CollegeStudent> collegeStudents = new ArrayList<>();
        for( CollegeStudent collegeStudent : iterableCollegeStudents){
            collegeStudents.add(collegeStudent);
        }
        assertEquals(5, collegeStudents.size());
    }


    @Test
    public void createGradeService(){
        // Create the grade
        assertTrue(studentService.createGrade(80.5, 1, "math"));
        assertTrue(studentService.createGrade(80.5, 1, "science"));
        assertTrue(studentService.createGrade(80.5, 1, "history"));

        // Get all grades with studentId
        Iterable<MathGrade> mathGrades = mathGradesDao.findGradeByStudentId(1);
        Iterable<ScienceGrade> scienceGrades = scienceGradesDao.findGradeByStudentId(1);
        Iterable<HistoryGrade> historyGrades = historyGradesDao.findGradeByStudentId(1);

        // Verify there are grades
        assertEquals(2, ((Collection<MathGrade>) mathGrades).size(), "Student has math grades");
        assertEquals(2, ((Collection<ScienceGrade>)scienceGrades).size(), "Student has science grades");
        assertEquals(2, ((Collection<HistoryGrade>)historyGrades).size(), "Student has history grades");

    }


    //Invalid grade: --> Should fail if grade is not between 0 and 100. Should fail if student id does not exist
    //Should fail if invalid subject
    @Test
    public void createGradeServiceReturnFalse(){
        assertFalse(studentService.createGrade(105.0, 1, "math"));
        assertFalse(studentService.createGrade(-5.5, 1, "math"));

        //invalid student id
        assertFalse(studentService.createGrade(80.50, 2, "math"));
        //invalid subject
        assertFalse(studentService.createGrade(80.50, 1, "literature"));

    }


    @Test
    public void deleteGradeService(){
        //delete grade by grade id and grade type
        assertEquals(1, studentService.deleteGrade(1, "math"),
                "Returns student id after delete");

        assertEquals(1, studentService.deleteGrade(1, "science"),
                "Returns student id after delete");

        assertEquals(1, studentService.deleteGrade(1, "history"),
                "Returns student id after delete");

    }


    //check invalid cases grade deletion for invalid subject and grade id
    @Test
    public void deleteGradeServiceReturnStudentIdOfZero(){

        assertEquals(0, studentService.deleteGrade(0, "math"),
                "No student should  have 0 id");

        assertEquals(0, studentService.deleteGrade(0 , "science"),
                "No student should have 0 id");

        assertEquals(0, studentService.deleteGrade(0, "history"),
                "No student should have 0 id");

        //valid id invalid subject
        assertEquals(0, studentService.deleteGrade(1, "literature"),
                "No student should have a literature class");

    }


    @Test
    public void studentInformation(){
        //retrieve gradebook for college student by student id
        GradebookCollegeStudent gradebookCollegeStudent = studentService.studentInformation(1);


//        1, 'Eric', 'Roby', 'eric@gmail.com'
        assertEquals(1, gradebookCollegeStudent.getId());
        assertEquals("Eric", gradebookCollegeStudent.getFirstname());
        assertEquals("Roby", gradebookCollegeStudent.getLastname());
        assertEquals("eric@gmail.com", gradebookCollegeStudent.getEmailAddress());
        assertEquals(1, gradebookCollegeStudent.getStudentGrades().getMathGradeResults().size());
        assertEquals(1, gradebookCollegeStudent.getStudentGrades().getHistoryGradeResults().size());
        assertEquals(1, gradebookCollegeStudent.getStudentGrades().getScienceGradeResults().size());
    }

    @AfterEach
    public void setupAfterTransaction(){
        ArrayList<String> vals = new ArrayList<>(Arrays.asList("student", "history_grade", "science_grade", "math_grade"));
        for( String value : vals ){
            String query = "DELETE FROM ".concat(value).concat(";");
            jdbc.execute(query);
        }


    }
}
