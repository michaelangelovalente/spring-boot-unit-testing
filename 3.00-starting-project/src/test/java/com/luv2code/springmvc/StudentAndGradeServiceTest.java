package com.luv2code.springmvc;

import com.luv2code.springmvc.models.CollegeStudent;
import com.luv2code.springmvc.repository.StudentDao;
import com.luv2code.springmvc.service.StudentAndGradeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

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
    @BeforeEach
    public void setUpDatabase(){ //h2 sample data
        jdbc.execute("INSERT INTO student(id, firstname, lastname, email_address)" +
                "values(1, 'Eric', 'Roby', 'eric@gmail.com');");
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
        assertTrue(deletedCollegeStudent.isPresent(), "Return True");

        studentService.deleteStudent(1);


        deletedCollegeStudent = studentDao.findById(1);

        assertFalse( deletedCollegeStudent.isPresent(), "Return False");
    }

    @AfterEach
    public void setupAfterTransaction(){
        jdbc.execute("DELETE FROM student;");
    }
}
