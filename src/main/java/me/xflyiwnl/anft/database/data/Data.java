package me.xflyiwnl.anft.database.data;

import java.io.File;
import java.util.List;
import java.util.UUID;

public interface Data<T> {

    void load();

    T get(File file);
    T get(UUID uniqueId);

    List<T> all();
    void save(T t);
    void remove(T t);

    void create(T t);
    boolean exists(Object uniqueId);

}
