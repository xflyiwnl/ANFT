package me.xflyiwnl.anft.object.serialize;

import java.io.File;
import java.util.Map;

public interface Serialize<T> {

    Map<String, Object> serialize();
    Map<String, Object> deserialize(File file);

}
