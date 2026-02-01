package com.example.mobileapp.data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;


@Entity(tableName = "saved_scripts")
public class SavedScript {

    // Бірінші өріс: Бастапқы кілт (Primary Key)
    @PrimaryKey
    @NonNull
    private String id;

    // Скрипттің атауы
    private String name;

    // Скрипт кодының мазмұны
    private String code;

    // --------------------------------------------------------
    // Room деректерді жүктеу үшін қолданатын НЕГІЗГІ КОНСТРУКТОР
    // --------------------------------------------------------
    public SavedScript(@NonNull String id, String name, String code) {
        this.id = id;
        this.name = name;
        this.code = code;
    }

    // --------------------------------------------------------
    // ЕСКЕРТУДІ ЖОЮ ҮШІН ҚОСЫЛҒАН КОНСТРУКТОР
    // Егер басқа кітапханалар (мысалы, Gson) аргументсіз конструкторды талап етсе,
    // біз оны қосамыз, бірақ Room оны елемеуі үшін @Ignore аннотациясын қолданамыз.
    // --------------------------------------------------------
    @Ignore // <--- ОСЫНЫ ҚОСУ КЕРЕК
    public SavedScript() {
        this.id = ""; // ID-ні ішкі мақсаттар үшін инициализациялау
    }

    // --- Getters and Setters (міндетті) ---

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}