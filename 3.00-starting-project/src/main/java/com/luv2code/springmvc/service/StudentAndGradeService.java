package com.luv2code.springmvc.service;

import com.luv2code.springmvc.models.CollegeStudent;
import com.luv2code.springmvc.models.MathGrade;
import com.luv2code.springmvc.models.ScienceGrade;
import com.luv2code.springmvc.repository.MathGradesDao;
import com.luv2code.springmvc.repository.ScienceGradesDao;
import com.luv2code.springmvc.repository.StudentDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional
public class StudentAndGradeService {

    @Autowired
    private StudentDao studentDao;

    @Autowired
    @Qualifier("mathGrades")
    private MathGrade mathGrade;

    @Autowired
    @Qualifier("scienceGrades")
    private ScienceGrade scienceGrade;


    @Autowired
    private MathGradesDao mathGradeDao;

    @Autowired
    private ScienceGradesDao scienceGradesDao;


    public void createStudent(String firstname, String lastname, String emailAddress){
        CollegeStudent student = new CollegeStudent(firstname, lastname, emailAddress);
//        student.setId(0);
        studentDao.save(student);

    }

    public Boolean checkIfStudentIsNull(Integer id){
        Optional<CollegeStudent> student = studentDao.findById(id);
        if( student.isPresent()){
            return true;
        }
        return  false;
    }

    public void deleteStudent(Integer id){
        if( checkIfStudentIsNull(id)){
            studentDao.deleteById(id);
        }
    }

    public Iterable<CollegeStudent> getGradebook(){
        Iterable<CollegeStudent> collegeStudents = studentDao.findAll();
        return  collegeStudents;
    }

    public Boolean createGrade(Double grade, Integer studentId, String gradeType){
        if( !this.checkIfStudentIsNull(studentId)){
            return false;
        }

        if( grade >= 0 && grade <= 100){
            if( gradeType.equals("math")){
                mathGrade.setId(0);
                mathGrade.setGrade(grade);
                mathGrade.setStudentId(studentId);
                mathGradeDao.save(mathGrade);
                return true;
            }

            if( gradeType.equals("science")){
                scienceGrade.setId(0);
                scienceGrade.setGrade(grade);
                scienceGrade.setStudentId(studentId);
                scienceGradesDao.save(scienceGrade);
                return true;
            }
        }


        return false;
    }

}
