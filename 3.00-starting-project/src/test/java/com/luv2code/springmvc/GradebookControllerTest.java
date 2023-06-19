package com.luv2code.springmvc;

import com.luv2code.springmvc.service.StudentAndGradeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@TestPropertySource("/application.properties")
@AutoConfigureMockMvc
@SpringBootTest
public class GradebookControllerTest {

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private StudentAndGradeService studentAndGradeService;

    @BeforeEach
    public void beforeEach(){
        jdbc.execute("INSERT INTO student(id, firstname, lastname, email_address)" +
                "values(1, 'Eric', 'Roby', ' eric@gmail.com');");
    }

    @AfterEach
    public void setupAfterTransaction(){
        jdbc.execute("DELETE FROM student;");
    }
}
