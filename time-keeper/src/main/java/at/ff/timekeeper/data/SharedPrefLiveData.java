package at.ff.timekeeper.data;

import android.content.SharedPreferences;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Idea based on https://gist.github.com/rharter/1df1cd72ce4e9d1801bd2d49f2a96810
 * @param <T>
 */
public class SharedPrefLiveData<T> extends LiveData<T> {

    private static final Gson GSON = GsonHelper.customGson;

    private final SharedPreferences sharedPreferences;
    private final String key;
    private final Class<T> clazz;

    private final SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;

    private SharedPrefLiveData(SharedPreferences sharedPreferences, String key, Class<T> clazz) {
        this.sharedPreferences = sharedPreferences;
        this.key = key;
        this.clazz = clazz;

        super.postValue(getValue());

        this.preferenceChangeListener = (prefs, k) -> {
            if (k.equals(this.key)) {
                setValue(getValue());
            }
        };
    }

    @Nullable
    @Override
    public T getValue() {
        String json = sharedPreferences.getString(key, null);
        return json == null ? null : GSON.fromJson(json, clazz);
    }

    @Override
    public void postValue(T obj) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (obj == null) {
            editor.remove(key);
        } else {
            editor.putString(key, GSON.toJson(obj));
        }
        editor.apply();
        // super.postValue(obj); DO NOT call post value here. preference change listener takes care of the update.
    }

    @Override
    protected void onActive() {
        super.onActive();
        super.postValue(getValue());
        sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener);
    }

    @Override
    protected void onInactive() {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener);
        super.onInactive();
    }

    public static class Factory {

        private SharedPreferences sharedPreferences;

        public Factory(SharedPreferences sharedPreferences) {
            this.sharedPreferences = sharedPreferences;
        }

        public <C> SharedPrefLiveData<C> build(String key, Class<C> clazz) {
            return new SharedPrefLiveData<>(sharedPreferences, key, clazz);
        }

        /**
         * Dump all shared preferences to string.
         * @return human readable String of shared preferences.
         */
        @NotNull
        @Override
        public String toString() {

            StringBuilder builder = new StringBuilder();
            builder.append("SharedPreferences {\n");
            Map<String, ?> allEntries = sharedPreferences.getAll();
            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                builder.append("  ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
            builder.append("}");
            return builder.toString();

        }

    }

}
