package com.spring.unictive.module.hobby.entity;

import com.spring.unictive.module.user.entity.User;
import com.spring.unictive.utils.entity.Auditing;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "hobbies")
@Data
public class Hobby extends Auditing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
