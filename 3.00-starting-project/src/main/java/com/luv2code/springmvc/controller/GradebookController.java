package com.luv2code.springmvc.controller;

import com.luv2code.springmvc.models.*;
import com.luv2code.springmvc.service.StudentAndGradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class GradebookController {

    @Autowired
    private Gradebook gradebook;

    @Autowired
    private StudentAndGradeService studentAndGradeService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String getStudents(Model m) {
        Iterable<CollegeStudent> collegeStudents = studentAndGradeService.getGradebook();
        m.addAttribute("students", collegeStudents);
        return "index";
    }

    @PostMapping("/")
    public String createStudent(@ModelAttribute("student") CollegeStudent collegeStudent, Model model) {
        studentAndGradeService.createStudent(collegeStudent.getFirstname(), collegeStudent.getLastname(),
                collegeStudent.getEmailAddress());
        //after we create a student get a list of students and add as model attribute
        Iterable<CollegeStudent> collegeStudents = studentAndGradeService.getGradebook();
        model.addAttribute("students", collegeStudents);
        return "index";
    }

    @GetMapping("/delete/student/{id}")
    public String deleteStudent(@PathVariable Integer id, Model m) {
        if (!studentAndGradeService.checkIfStudentIsNull(id)) {
            return "error";
        }
        studentAndGradeService.deleteStudent(id);
        Iterable<CollegeStudent> collegeStudents = studentAndGradeService.getGradebook();
        m.addAttribute("students", collegeStudents);
        return "index";
    }


    @GetMapping("/studentInformation/{id}")
    public String studentInformation(@PathVariable int id, Model m) {
        if (Boolean.FALSE.equals(studentAndGradeService.checkIfStudentIsNull(id))) {
            return "error";
        }
        GradebookCollegeStudent studentEntity = studentAndGradeService.studentInformation(id);
        m.addAttribute("student", studentEntity);

        if (!studentEntity.getStudentGrades().getMathGradeResults().isEmpty()) {
            m.addAttribute("mathAverage", studentEntity.getStudentGrades().findGradePointAverage(
                    studentEntity.getStudentGrades().getMathGradeResults()
            ));
        } else {
            m.addAttribute("mathAverage", "N/A");
        }

        if (!studentEntity.getStudentGrades().getHistoryGradeResults().isEmpty()) {
            m.addAttribute("historyAverage", studentEntity.getStudentGrades().findGradePointAverage(
                    studentEntity.getStudentGrades().getHistoryGradeResults()
            ));

        } else {
            m.addAttribute("historyAverage", "N/A");
        }

        if (!studentEntity.getStudentGrades().getScienceGradeResults().isEmpty()) {
            m.addAttribute("scienceAverage", "N/A");
        } else {
            m.addAttribute("scienceAverage", "N/A");
        }


        return "studentInformation";
    }

    @PostMapping(value = "/grades")
    public String createGrade(@RequestParam("grade") double grade,
                              @RequestParam("gradeType") String gradeType,
                              @RequestParam("studentId") int studentId,
                              Model m) {

        if (Boolean.FALSE.equals(studentAndGradeService.checkIfStudentIsNull(studentId))) {
            return "error";
        }

        boolean success = studentAndGradeService.createGrade(grade, studentId, gradeType);

        if (!success) {
            return "error";
        }

        GradebookCollegeStudent studentEntity = studentAndGradeService.studentInformation(studentId);
        m.addAttribute("student", studentEntity);

        if (!studentEntity.getStudentGrades().getMathGradeResults().isEmpty()) {
            m.addAttribute("mathAverage", studentEntity.getStudentGrades().findGradePointAverage(
                    studentEntity.getStudentGrades().getMathGradeResults()
            ));
        } else {
            m.addAttribute("mathAverage", "N/A");
        }

        if (!studentEntity.getStudentGrades().getHistoryGradeResults().isEmpty()) {
            m.addAttribute("historyAverage", studentEntity.getStudentGrades().findGradePointAverage(
                    studentEntity.getStudentGrades().getHistoryGradeResults()
            ));

        } else {
            m.addAttribute("historyAverage", "N/A");
        }

        if (!studentEntity.getStudentGrades().getScienceGradeResults().isEmpty()) {
            m.addAttribute("scienceAverage", "N/A");
        } else {
            m.addAttribute("scienceAverage", "N/A");
        }

        return "studentInformation";
    }


}
