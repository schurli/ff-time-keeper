package at.ff.timekeeper.service;

import javax.inject.Inject;

import at.ff.timekeeper.data.SharedPrefRepository;

public class ServiceModel  {

    private final SharedPrefRepository sharedPrefRepository;

    @Inject
    public ServiceModel(SharedPrefRepository sharedPrefRepository) {
        this.sharedPrefRepository = sharedPrefRepository;

    }
}
