package com.example.babajidemustapha.remedy;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import java.util.List;

/**
 * Created by Jide Mustapha on 4/8/2018.
 */

public class MedicationViewModel extends AndroidViewModel {
    private AppDatabase appDatabase;
    private MutableLiveData<List<Medication>> medications;
    // private MutableLiveData<List<Medication>> mutableLiveData;
    // MediatorLiveData<List<Medication>> mediatorLiveData = new MediatorLiveData<>();


    public MedicationViewModel(Application application) {
        super(application);
        appDatabase = appDatabase == null ? AppDatabase.getInstance(application) : appDatabase;
        medications = new MutableLiveData<>();
//        mutableLiveData = mutableLiveData == null? new MutableLiveData<List<Medication>>():mutableLiveData;
//        mediatorLiveData = new MediatorLiveData<>();
    }

    public LiveData<List<Medication>> getAllMedications() {
        medications.setValue(appDatabase.medicationDao().getAllMeds());
        return medications;
    }

    public List<Medication> getMedicationsList() {
        return appDatabase.medicationDao().getAllMeds();
    }

    public void getMedsInMonth(String month) {
        switch (month) {
            case "ALL":
                medications.setValue(appDatabase.medicationDao().getAllMeds());
                break;
            default:
                Log.w("month num", MonthUtil.getMonthNumber(month) + "");
                medications.setValue(appDatabase.medicationDao().getMedsInMonth(MonthUtil.getMonthNumber(month)));
        }
    }

    public void searchMed(String query, String month) {
        medications.setValue(appDatabase.medicationDao().searchMed(query, MonthUtil.getMonthNumber(month)));
    }

    public void deleteMed(Medication medication) {
        appDatabase.medicationDao().deleteMed(medication);
        // medications.setValue(appDatabase.medicationDao().getAllMeds());
    }

    public void deleteAll() {
        appDatabase.medicationDao().deleteAll();
        // medications.setValue(appDatabase.medicationDao().getAllMeds());
    }

    public void updateMed(Medication medication) {
        appDatabase.medicationDao().updateMed(medication);
        //medications.setValue(appDatabase.medicationDao().getAllMeds());
    }

    public Medication getMed(int id) {
        return appDatabase.medicationDao().getMed(id);
    }

    public long addMed(Medication medication) {
        long num = appDatabase.medicationDao().insertMed(medication);
        //medications.setValue(appDatabase.medicationDao().getAllMeds());
        return num;
    }
}
