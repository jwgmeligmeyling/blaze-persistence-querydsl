package com.pallasathenagroup.querydsl;

import com.blazebit.persistence.CTE;

import javax.persistence.Entity;
import javax.persistence.Id;

@CTE
@Entity
public class IdHolderCte {

    @Id
    Long id;

    String name;

}
