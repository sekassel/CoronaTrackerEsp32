package de.uniks.postgres.db.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.Data;

import java.lang.reflect.Type;

@Data
public class InfectedUser {
    public static final String CLASS = "infectedUser";
    public static final String UUID = "uuid";
    public static final String TEK = "tek";
    public static final String RSIN = "rsin";

    private String uuid;
    private byte[] tek;
    private Integer rsin;

    public InfectedUser(String uuid, Integer rsin) {
        this.uuid = uuid;
        this.rsin = rsin;
    }

    public InfectedUser(String uuid, String tekAsString, Integer rsin) {
        this(uuid, rsin);
        // Deserialization because of getTekAsJSONArray()
        Type collectionType = new TypeToken<byte[]>() {
        }.getType();
        this.tek = new Gson().fromJson(tekAsString, collectionType);
    }

    public String getTekAsJSONArray() {
        return new Gson().toJson(getTek());
    }
}