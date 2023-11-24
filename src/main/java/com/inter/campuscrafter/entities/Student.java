package com.inter.campuscrafter.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class Student extends UserProfile{
    List<String> courseIds;
    public Student() {
        super(UserRole.STUDENT);
        courseIds = new ArrayList<>();
    }
}
