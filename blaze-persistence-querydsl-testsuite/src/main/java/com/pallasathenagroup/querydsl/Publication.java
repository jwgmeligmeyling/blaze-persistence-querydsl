package com.pallasathenagroup.querydsl;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.time.LocalDate;

@Entity
public class Publication extends BaseEntity {

    @ManyToOne
    Book book;

    @ManyToOne
    Publisher publisher;

    LocalDate publishedAt;

}
