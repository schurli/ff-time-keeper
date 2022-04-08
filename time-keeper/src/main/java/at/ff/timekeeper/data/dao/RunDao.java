package at.ff.timekeeper.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import at.ff.timekeeper.data.entity.RunEntity;

@Dao
public interface RunDao {

    @Query("SELECT * FROM runs WHERE mode = '" + RunEntity.MODE_BRONZE + "' ORDER BY duration ASC")
    LiveData<List<RunEntity>> findAllBronze();

    @Query("SELECT * FROM runs WHERE mode = '" + RunEntity.MODE_SILVER + "' ORDER BY duration ASC")
    LiveData<List<RunEntity>> findAllSilver();

    @Query("SELECT * FROM runs WHERE mode = '" + RunEntity.MODE_GOLD + "' ORDER BY duration ASC")
    LiveData<List<RunEntity>> findAllGold();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(RunEntity... runs);

    @Delete
    void delete(RunEntity run);

}
