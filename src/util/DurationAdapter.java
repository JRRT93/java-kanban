package util;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;

public class DurationAdapter extends TypeAdapter<Duration> {

    @Override
    public void write(JsonWriter jsonWriter, Duration duration) throws IOException {
        try {
            jsonWriter.value(duration.toMinutes());
        } catch (Exception exception) {
            jsonWriter.value(String.valueOf(duration));
        }
    }

    @Override
    public Duration read(JsonReader jsonReader) throws IOException {
        try {
            return Duration.ofMinutes(Integer.parseInt(jsonReader.nextString()));
        } catch (Exception exception) {
            return null;
        }
    }

}
