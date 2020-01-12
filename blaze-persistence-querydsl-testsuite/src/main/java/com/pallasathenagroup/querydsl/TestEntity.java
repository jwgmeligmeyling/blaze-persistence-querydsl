package com.pallasathenagroup.querydsl;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class TestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    String field;

}
