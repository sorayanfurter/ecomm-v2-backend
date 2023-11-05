package com.project.ecommv2backend.model.dto;

import com.project.ecommv2backend.model.Role;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocalUserDTO implements Serializable {


    private String username;

    private String email;

    private String firstName;

    private String lastName;

}
