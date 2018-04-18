package com.example.babajidemustapha.remedy;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;


/**
 * Created by Jide Mustapha on 4/8/2018.
 */
@Entity
public class Medication {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String name;
    public String description;
    public int interval;
    public String startDate;
    public String endDate;

    public Medication(String name, String description, String startDate, String endDate, int interval) {
        this.endDate = endDate;
        this.description = description;
        this.interval = interval;
        this.name = name;
        this.startDate = startDate;
    }

    @Ignore
    public Medication(int id, String name, String description, String startDate, String endDate, int interval) {
        this.id = id;
        this.endDate = endDate;
        this.description = description;
        this.interval = interval;
        this.name = name;
        this.startDate = startDate;
    }
}
