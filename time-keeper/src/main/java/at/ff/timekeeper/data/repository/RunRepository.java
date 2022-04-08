package at.ff.timekeeper.data.repository;

import androidx.lifecycle.LiveData;

import java.util.List;

import at.ff.timekeeper.data.dao.RunDao;
import at.ff.timekeeper.data.entity.RunEntity;

public class RunRepository {

    private final RunDao dao;

    public final LiveData<List<RunEntity>> bronzeRuns;
    public final LiveData<List<RunEntity>> silverRuns;
    public final LiveData<List<RunEntity>> goldRuns;

    public RunRepository(RunDao dao) {
        this.dao = dao;
        bronzeRuns = dao.findAllBronze();
        silverRuns = dao.findAllSilver();
        goldRuns = dao.findAllGold();
    }

    public LiveData<List<RunEntity>> bronzeRuns() {
        return bronzeRuns;
    }

    public LiveData<List<RunEntity>> silverRuns() {
        return silverRuns;
    }

    public LiveData<List<RunEntity>> goldRuns() {
        return goldRuns;
    }

    public void insertAll(final RunEntity... entities) {
        new Thread(() -> dao.insertAll(entities)).start();
    }

}
