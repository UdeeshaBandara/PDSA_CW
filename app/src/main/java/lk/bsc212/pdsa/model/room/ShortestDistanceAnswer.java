package lk.bsc212.pdsa.model.room;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ShortestDistanceAnswer {
    @PrimaryKey(autoGenerate = true)
    public long answerId;

    public long userId;

    public int systemSelectedCityName;

    public ShortestDistanceAnswer(long userId, int systemSelectedCityName) {
        this.userId = userId;
        this.systemSelectedCityName = systemSelectedCityName;
    }
}
