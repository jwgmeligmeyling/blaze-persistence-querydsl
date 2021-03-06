package com.pallasathenagroup.querydsl;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
public class Author extends BaseEntity {

    String name;

    @OneToMany(mappedBy = "author")
    List<Book> books;

}
