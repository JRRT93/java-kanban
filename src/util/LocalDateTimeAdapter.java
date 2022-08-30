package util;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {

    @Override
    public void write(JsonWriter jsonWriter, LocalDateTime localDateTime) throws IOException {
        try {
            jsonWriter.value(localDateTime.format(DefaultFormatter.FORMATTER));
        } catch (Exception exception) {
            jsonWriter.value(String.valueOf(localDateTime));
        }
    }

    @Override
    public LocalDateTime read(JsonReader jsonReader) throws IOException {
        try {
            return LocalDateTime.parse(jsonReader.nextString(), DefaultFormatter.FORMATTER);
        } catch (Exception exception) {
            return null;
        }
    }
}
