package com.pallasathenagroup.querydsl;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
public class Publisher extends BaseEntity {

    String name;

    @OneToMany(mappedBy = "publisher")
    List<Publication> publications;

}
