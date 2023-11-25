package com.inter.campuscrafter.services;

import com.inter.campuscrafter.entities.Grade;
import com.inter.campuscrafter.repositories.interfaces.GradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GradeService {
    private final GradeRepository gradeRepository;

    public Grade createGrade(Grade grade, String assignmentId) {
        grade.setAssignmentId(assignmentId);
        grade.setSubmissionDate(LocalDateTime.now());
        return gradeRepository.save(grade);
    }

    public List<Grade> getGrades(String studentId) {
        return gradeRepository.findByStudentId(studentId);
    }

    public void deleteGradesByStudentId(String studentId) {
        gradeRepository.deleteAllByStudentId(studentId);
    }
}
