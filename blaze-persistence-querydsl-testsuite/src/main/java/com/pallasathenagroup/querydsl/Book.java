package com.pallasathenagroup.querydsl;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
public class Book extends BaseEntity {

    String name;

    @ManyToOne
    Author author;

    @OneToMany(mappedBy = "book")
    List<Publication> publications;

}
