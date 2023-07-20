package com.luv2code.springmvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.luv2code.springmvc.models.CollegeStudent;
import com.luv2code.springmvc.models.MathGrade;
import com.luv2code.springmvc.repository.HistoryGradesDao;
import com.luv2code.springmvc.repository.MathGradesDao;
import com.luv2code.springmvc.repository.ScienceGradesDao;
import com.luv2code.springmvc.repository.StudentDao;
import com.luv2code.springmvc.service.StudentAndGradeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;

@TestPropertySource("/application-test.properties")
@AutoConfigureMockMvc
@SpringBootTest
@Transactional
public class GradebookControllerTest {

    private static MockHttpServletRequest request;

    @PersistenceContext
    private EntityManager entityManager;

    @Mock
    StudentAndGradeService studentAndGradeServiceMock;

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private StudentDao studentDao;

    @Autowired
    private MathGradesDao mathGradesDao;

    @Autowired
    private ScienceGradesDao scienceGradesDao;

    @Autowired
    private HistoryGradesDao historyGradesDao;

    @Autowired
    private StudentAndGradeService studentAndGradeService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CollegeStudent collegeStudent;


    @Value("${sql.script.create.student}")
    private String sqlAddStudent;

    @Value("${sql.script.create.math.grade}")
    private String sqlAddMathGrade;

    @Value("${sql.script.create.science.grade}")
    private String sqlAddScienceGrade;

    @Value("${sql.script.create.history.grade}")
    private String sqlAddHistoryGrade;

    @Value("${sql.script.delete.student}")
    private String sqlDeleteStudent;

    @Value("${sql.script.delete.math.grade}")
    private String sqlDeleteMathGrade;

    @Value("${sql.script.delete.science.grade}")
    private String sqlDeleteScienceGrade;

    @Value("${sql.script.delete.history.grade}")
    private String sqlDeleteHistoryGrade;



    //Mediatype variable
    public static final MediaType APPLICATION_JSON_UTF8 = MediaType.APPLICATION_JSON;

    // Initialization work on MockServletRequest
    @BeforeAll
    static void setup(){
        request = new MockHttpServletRequest();
        request.setParameter("firstname", "Michael");
        request.setParameter("lastname", "Valente");

        request.setParameter("emailAddress", "michael@gmail.com");
    }


    @BeforeEach
    void setupDatabase(){
        jdbc.execute(sqlAddStudent);
        jdbc.execute(sqlAddMathGrade);
        jdbc.execute(sqlAddScienceGrade);
        jdbc.execute(sqlAddHistoryGrade);
    }





    @Test
    void getStudentsHttpRequest() throws Exception {

        //add a student directly using the entity manager.
        collegeStudent.setFirstname("Chad");
        collegeStudent.setLastname("Darby");
        collegeStudent.setEmailAddress("chad@gmail.com");
        entityManager.persist(collegeStudent);
        entityManager.flush();

        //jpa entity manager --> saves it to the db

        mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(2)));
    }


    @Test
    void createStudentHttpRequest() throws Exception{
        collegeStudent.setFirstname("Chad");
        collegeStudent.setLastname("Darby");
        collegeStudent.setEmailAddress("chad@gmail.com");

        mockMvc.perform(post("/")
                .contentType(APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(collegeStudent)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        //double verification --> search for student that was just created
        CollegeStudent verifyStudent = studentDao.findByEmailAddress("chad@gmail.com");
        assertNotNull(verifyStudent, "Student should be valid.");
    }

    @Test
    void deleteStudentHttpRequest() throws Exception{
        assertTrue(studentDao.findById(1).isPresent());

        mockMvc.perform(delete("/student/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(0)));

        assertFalse(studentDao.findById(1).isPresent());
    }



    @Test
    void deleteStudentHttpRequestErrorPage() throws Exception{
        assertFalse(studentDao.findById(0).isPresent());

        mockMvc.perform(delete("/student/{id}", 0))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", is("Student or Grade was not found")));

    }

    /**
     *
     *
     sql.script.create.student=insert into student(id,firstname,lastname,email_address) \
     values (1,'Eric', 'Roby', 'eric.roby@luv2code_school.com')
     sql.script.create.math.grade=insert into math_grade(id,student_id,grade) values (1,1,100.00)
     sql.script.create.science.grade=insert into science_grade(id,student_id,grade) values (1,1,100.00)
     sql.script.create.history.grade=insert into history_grade(id,student_id,grade) values (1,1,100.00)
     *
     */

    @Test
    void studentInformationHttpRequest() throws Exception{
        Optional<CollegeStudent> student = studentDao.findById(1);
        assertTrue(student.isPresent());

        mockMvc.perform(MockMvcRequestBuilders.get("/studentInformation/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstname", is("Eric")))
                .andExpect(jsonPath("$.lastname", is("Roby")))
                .andExpect(jsonPath("$.emailAddress", is("eric.roby@luv2code_school.com")));
    }

    @Test
    void studentInformationHttpRequestEmptyResponse() throws Exception {
        Optional<CollegeStudent> student = studentDao.findById(0);

        assertFalse(student.isPresent());


        mockMvc.perform(MockMvcRequestBuilders.get("/studentInformation/{id}", 0))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", is("Student or Grade was not found")));
    }

    @Test
    void createAValidGradeHttpRequest() throws Exception {

        this.mockMvc.perform(post("/grades")
                .contentType(APPLICATION_JSON_UTF8)
                .param("grade", "85.00")
                .param("gradeType", "math")
                .param("studentId", "1"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.firstname", is("Eric")))
                    .andExpect(jsonPath("$.lastname", is("Roby")))
                    .andExpect(jsonPath("$.emailAddress", is("eric.roby@luv2code_school.com")))
                    .andExpect(jsonPath("$.studentGrades.mathGradeResults", hasSize(2)));

    }

    @Test
    void createAValidGradeHttpRequestStudentDoesNotExistEmptyResponse() throws Exception {
        this.mockMvc.perform(post("/grades")
                .contentType(APPLICATION_JSON_UTF8)
                .param("grade", "85.00")
                .param("gradeType", "math")
                .param("studentId", "0"))
                    .andExpect(status().is4xxClientError())
                    .andExpect(jsonPath("$.status", is(404)))
                    .andExpect(jsonPath("$.message", is("Student or Grade was not found")));

    }

    @Test
    void createANonValidGradeHttPRequestGradeTypeDoesNotExistsEmptyResponse() throws Exception {
        this.mockMvc.perform(post("/grades")
                .contentType(APPLICATION_JSON_UTF8)
                .param("grade", "85.00")
                .param("gradeType", "literature") // invalid gradetype
                .param("studentId", "1"))
                    .andExpect(status().is4xxClientError())
                    .andExpect(jsonPath("$.status", is(404)))
                    .andExpect(jsonPath("$.message", is("Student or Grade was not found")));

    }


    @Test
    void deleteAValidGradeHttpRequest() throws Exception {

        Optional<MathGrade> mathGrade = mathGradesDao.findById(1);

        assertTrue(mathGrade.isPresent());

        mockMvc.perform(delete("/grades/{id}/{gradeType}", 1, "math"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstname", is("Eric")))
                .andExpect(jsonPath("$.lastname", is("Roby")))
                .andExpect(jsonPath("$.emailAddress", is("eric.roby@luv2code_school.com")))
                .andExpect(jsonPath("$.studentGrades.mathGradeResults", hasSize(0)));

    }

    @Test
    void deleteAValidGradeHttpRequestStudentIdDoesNotExistEmptyResponse() throws Exception{

        Optional<CollegeStudent> student = studentDao.findById(2);
        assertFalse(student.isPresent());

        mockMvc.perform(MockMvcRequestBuilders.delete("/grades/{id}/{gradeType}", 2, "history"))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", is("Student or Grade was not found")));
    }


    @Test
    void deleteANonValidGradeHttpRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/grades/{id}/{gradeType}", 1, "literature"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", is("Student or Grade was not found")));
    }
    @AfterEach
    void setupAfterTransaction(){
        jdbc.execute(sqlDeleteStudent);
        jdbc.execute(sqlDeleteMathGrade);
        jdbc.execute(sqlDeleteScienceGrade);
        jdbc.execute(sqlDeleteHistoryGrade);
    }


}
