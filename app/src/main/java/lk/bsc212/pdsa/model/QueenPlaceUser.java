package lk.bsc212.pdsa.model;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Relation;

import java.util.List;

public class QueenPlaceUser {


    @Embedded
    public User user;
    @Relation(

            parentColumn = "userId",
            entityColumn = "answeredUserId"
    )
    public List<QueenPlace> queenPlace;


}
