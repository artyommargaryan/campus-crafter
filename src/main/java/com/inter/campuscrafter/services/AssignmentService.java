package com.inter.campuscrafter.services;

import com.inter.campuscrafter.entities.Assignment;
import com.inter.campuscrafter.repositories.interfaces.AssignmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AssignmentService {
    private final AssignmentRepository assignmentRepository;

    public List<Assignment> getAllAssignmentsByCourseId(String courseId) {
        return assignmentRepository.findAllByCourseId(courseId);
    }

    public Assignment createAssignmentsByCourseId(String courseId, Assignment assignment) {
        assignment.setCourseId(courseId);
        assignment.setPostedDate(LocalDateTime.now());
        return assignmentRepository.save(assignment);
    }

    public Assignment updateAssignmentById(String id, Assignment updatedAssignment) {
        Optional<Assignment> assignmentById = assignmentRepository.findById(id);

        return assignmentById.map(assignment -> {
            assignment.setTitle(updatedAssignment.getTitle());
            assignment.setContent(updatedAssignment.getContent());
            assignment.setDueDate(updatedAssignment.getDueDate());
            assignment.setMaxScore(updatedAssignment.getMaxScore());
            assignment.setSubmissionFormat(updatedAssignment.getSubmissionFormat());

            return assignmentRepository.save(assignment);
        }).orElse(null);
    }

    public void deleteAssignmentById(String id) {
        assignmentRepository.deleteById(id);
    }

    public void deleteAllByCourseId(String courseId) {
        assignmentRepository.deleteAllByCourseId(courseId);
    }
}
