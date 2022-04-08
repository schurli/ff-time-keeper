package at.ff.timekeeper.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import at.ff.timekeeper.data.dao.RunDao;
import at.ff.timekeeper.data.entity.RunEntity;

@Database(entities = {
        RunEntity.class
}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract RunDao runDao();

    private static AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "room_timekeeper.db")
                            .fallbackToDestructiveMigration() // deletes the database with each version upgrade
                            .build();

                }
            }
        }
        return INSTANCE;
    }

}