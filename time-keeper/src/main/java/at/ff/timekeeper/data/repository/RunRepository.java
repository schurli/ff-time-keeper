package at.ff.timekeeper.data.repository;

import androidx.lifecycle.LiveData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.ff.timekeeper.data.dao.RunDao;
import at.ff.timekeeper.data.entity.RunEntity;

public class RunRepository {

    private final RunDao dao;

    public final Map<RunEntity.Mode, LiveData<List<RunEntity>>> topMap = new HashMap<>();
    public final Map<RunEntity.Mode, LiveData<List<RunEntity>>> latestMap = new HashMap<>();

    public RunRepository(RunDao dao) {
        this.dao = dao;
    }

    public LiveData<List<RunEntity>> topRuns(RunEntity.Mode mode) {
        return topMap.computeIfAbsent(mode, m -> dao.findTop(m.name));
    }

    public LiveData<List<RunEntity>> latestRuns(RunEntity.Mode mode) {
        return latestMap.computeIfAbsent(mode, m -> dao.findLatest(m.name));
    }

    public void insertAll(final RunEntity... entities) {
        new Thread(() -> dao.insertAll(entities)).start();
    }

    public void remove(final RunEntity entity) {
        new Thread(() -> dao.delete(entity)).start();
    }

}
