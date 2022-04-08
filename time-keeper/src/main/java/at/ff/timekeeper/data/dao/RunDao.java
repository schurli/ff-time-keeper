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

    @Query("SELECT * FROM runs WHERE mode = :mode ORDER BY duration ASC")
    LiveData<List<RunEntity>> findTop(String mode);

    @Query("SELECT * FROM runs WHERE mode = :mode ORDER BY start DESC")
    LiveData<List<RunEntity>> findLatest(String mode);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(RunEntity... runs);

    @Delete
    void delete(RunEntity run);

}
