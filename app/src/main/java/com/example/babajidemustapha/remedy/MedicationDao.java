package com.example.babajidemustapha.remedy;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * Created by Jide Mustapha on 4/8/2018.
 */
@Dao
public interface MedicationDao {
    @Insert
    long insertMed(Medication medication);

    @Query("SELECT * FROM MEDICATION")
    List<Medication> getAllMeds();

    @Query("SELECT * FROM MEDICATION WHERE ((strftime('%Y',endDate) - strftime('%Y',startDate) = 0) AND " +
            " (:month BETWEEN CAST(strftime('%m',startDate) AS INTEGER) AND CAST(strftime('%m',endDate) AS INTEGER))) " + //YEAR IS EQUAL
            "OR ((strftime('%Y',endDate) - strftime('%Y',startDate) = 1) AND (CAST(strftime('%m',endDate) AS INTEGER) > CAST(strftime('%m',startDate) AS INTEGER) " +
            "OR (:month BETWEEN CAST(strftime('%m',startDate) AS INTEGER) AND 12) OR (:month BETWEEN 1 AND CAST(strftime('%m',endDate) AS INTEGER))) " + //YEAR DIFFERENCE IS 1
            "OR ((strftime('%Y',endDate) - strftime('%Y',startDate) > 1) AND (:month BETWEEN 1 AND 12)))")
        // YEAR DIFFERENCE IS MORE THAN 1
    List<Medication> getMedsInMonth(int month);

    @Query("Select * FROM MEDICATION WHERE id = :id")
    Medication getMed(int id);

    @Delete
    void deleteMed(Medication medication);

    @Query("DELETE FROM MEDICATION")
    void deleteAll();

    @Update
    void updateMed(Medication medication);

    @Query("Select * FROM MEDICATION WHERE (name like '%'||:query||'%' OR description like  '%'||:query||'%') AND id IN " +
            "(SELECT id FROM MEDICATION WHERE ((strftime('%Y',endDate) - strftime('%Y',startDate) = 0) AND " +
            " (:month BETWEEN CAST(strftime('%m',startDate) AS INTEGER) AND CAST(strftime('%m',endDate) AS INTEGER))) " + //YEAR IS EQUAL
            "OR ((strftime('%Y',endDate) - strftime('%Y',startDate) = 1) AND (CAST(strftime('%m',endDate) AS INTEGER) > CAST(strftime('%m',startDate) AS INTEGER) " +
            "OR (:month BETWEEN CAST(strftime('%m',startDate) AS INTEGER) AND 12) OR (:month BETWEEN 1 AND CAST(strftime('%m',endDate) AS INTEGER))) " + //YEAR DIFFERENCE IS 1
            "OR ((strftime('%Y',endDate) - strftime('%Y',startDate) > 1) AND (:month BETWEEN 1 AND 12))))")
    List<Medication> searchMed(String query, int month);

}
