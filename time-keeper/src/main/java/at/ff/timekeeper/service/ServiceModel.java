package at.ff.timekeeper.service;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import javax.inject.Inject;

public class ServiceModel extends AndroidViewModel {
    @Inject
    public ServiceModel(@NonNull Application application) {
        super(application);
    }
}
