package com.skillbox.socialnetwork.main.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class Town {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    int id;

    @OneToOne
    City city;

    @OneToOne
    Country country;
}
