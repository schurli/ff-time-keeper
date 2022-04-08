package at.ff.timekeeper.data.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "runs")
public class RunEntity {

    public enum Mode {
        BRONZE(RunEntity.MODE_BRONZE),
        SILVER(RunEntity.MODE_SILVER),
        BRONZE_SUCTION(RunEntity.MODE_BRONZE_SUCTION),
        SILVER_SUCTION(RunEntity.MODE_SILVER_SUCTION);

        public final String name;

        Mode(String name) {
            this.name = name;
        }

        @NonNull
        @Override
        public String toString() {
            return name;
        }
    }

    public static final String MODE_BRONZE = "BRONZE";
    public static final String MODE_SILVER = "SILVER";
    public static final String MODE_BRONZE_SUCTION = "BRONZE_SUCTION";
    public static final String MODE_SILVER_SUCTION = "SILVER_SUCTION";

    @PrimaryKey (autoGenerate = true)
    public int id;

    @ColumnInfo(name = "mode")
    public String mode;

    @ColumnInfo(name = "start")
    public long start;

    @ColumnInfo(name = "end")
    public long end;

    @ColumnInfo(name = "duration")
    public long duration;

    public RunEntity(String mode, long start, long end, long duration) {
        this.mode = mode;
        this.start = start;
        this.end = end;
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "RunEntity{" +
                "id=" + id +
                ", mode='" + mode + '\'' +
                ", start=" + start +
                ", end=" + end +
                ", duration=" + duration +
                '}';
    }
}
