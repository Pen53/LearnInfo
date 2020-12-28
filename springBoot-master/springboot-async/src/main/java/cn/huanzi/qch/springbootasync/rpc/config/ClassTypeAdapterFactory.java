package cn.huanzi.qch.springbootasync.rpc.config;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class ClassTypeAdapterFactory implements TypeAdapterFactory {
    public static final String ERROR_CODE = "ERROR_CODE ";
    public static final String ERROR_CODE_SUCCESS = "ERROR_CODE_SUCCESS";
    public static final String RESULT = "RESULT ";
    public static final String ERROR_MSG = "ERROR_MSG ";
    public static final String SEPARATOR = "&#-SEPARATOR-#&";
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        final TypeAdapter<T> delegateAdapter = gson.getDelegateAdapter(this, type);
        final TypeAdapter<JsonElement> jsonElementAdapter = gson.getAdapter(JsonElement.class);

        return new TypeAdapter<T>() {

            @Override
            public void write(JsonWriter out, T value) throws IOException {
                delegateAdapter.write(out, value);
            }

            @Override
            public T read(JsonReader in) throws IOException {
                if (in.peek() == JsonToken.NULL) {
                    in.nextNull();
                    return null;
                }
                JsonElement jsonElement = jsonElementAdapter.read(in);
                if (jsonElement.isJsonObject()) {
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    if (jsonObject.has(ERROR_CODE) && jsonObject.has(ERROR_MSG)) {
                        if (jsonObject.get(ERROR_CODE).getAsString().equals(ERROR_CODE_SUCCESS)) {
                            if (jsonObject.has(RESULT)) {
                                if (jsonObject.get(RESULT).isJsonNull()) {
                                    return delegateAdapter.fromJsonTree(new JsonObject());
                                }
                                return delegateAdapter.fromJsonTree(jsonObject.get(RESULT));
                            } else {
                                return delegateAdapter.fromJsonTree(new JsonObject());
                            }
                        } else {
                            throw new IOException(jsonObject.get(ERROR_CODE).getAsString() + SEPARATOR + jsonObject.get(ERROR_MSG).getAsString());
                        }
                    }
                }
                return delegateAdapter.fromJsonTree(jsonElement);
            }
        }.nullSafe();
    }
}
