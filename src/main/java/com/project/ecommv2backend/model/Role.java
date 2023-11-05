package com.project.ecommv2backend.model;

import jakarta.persistence.*;
import lombok.Getter;


@Entity
@Table(name= "role")
@Getter
public class Role{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    @Enumerated(EnumType.STRING)
    private ERole name;
}