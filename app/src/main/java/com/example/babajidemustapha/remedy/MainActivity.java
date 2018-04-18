package com.example.babajidemustapha.remedy;

import android.app.AlarmManager;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AddMedicationDialogFragment.MedicationDialogListener {

    MedicationViewModel medicationViewModel;
    RecyclerView recyclerView;
    MedsAdapter medsAdapter;
    Spinner spinner;
    SharedPreferences user_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        user_data = getSharedPreferences("user_data", MODE_PRIVATE);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        medsAdapter = new MedsAdapter(new ArrayList<Medication>());
        recyclerView.setAdapter(medsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        medicationViewModel = ViewModelProviders.of(this).get(MedicationViewModel.class);
        medicationViewModel.getAllMedications().observe(this, new Observer<List<Medication>>() {
            @Override
            public void onChanged(@Nullable List<Medication> medications) {
                medsAdapter.refresh(medications);
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialogFragment = new AddMedicationDialogFragment();
                Bundle bundle = new Bundle();
                bundle.putString("type", "add");
                dialogFragment.setArguments(bundle);
                dialogFragment.show(getFragmentManager(), "whoop");
            }
        });
    }

    @Override
    public void saveMedication(Medication medication) {
        int id = (int) medicationViewModel.addMed(medication);
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("med_id", id);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, id, intent, 0);
        triggerAlarmManager(medication.startDate, medication.interval, pendingIntent);
        medicationViewModel.getMedsInMonth(spinner.getSelectedItem().toString());
    }

    @Override
    public void updateMedication(Medication medication) {
        medicationViewModel.updateMed(medication);
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("med_id", medication.id);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, medication.id, intent, 0);
        triggerAlarmManager(medication.startDate, medication.interval, pendingIntent);
        medicationViewModel.getMedsInMonth(spinner.getSelectedItem().toString());
    }

    @Override
    public Medication getMedication(int id) {
        return medicationViewModel.getMed(id);
    }

    public void triggerAlarmManager(String startDate, int interval, PendingIntent pendingIntent) {
        // get a Calendar object with current time
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(new SimpleDateFormat("yyyy-HH-dd HH:ss").parse(startDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // add interval seconds to the calendar object
        //    cal.add(Calendar.SECOND, interval);

        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);//get instance of alarm manager
        manager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), interval * 60 * 60 * 1000, pendingIntent);//set alarm manager with entered timer by converting into milliseconds
        // Toast.makeText(this, "Alarm Set for " + interval + " seconds.", Toast.LENGTH_SHORT).show();
    }

    public void stopAlarmManager(Medication medication) {
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("med_id", medication.id);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, medication.id, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_profile:
                Intent intent = new Intent(this, ProfileActivity.class);
                startActivity(intent);
                break;
            case R.id.action_logout:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Are you sure you want to logout?")
                        .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                        .requestEmail()
                                        .build();
                                GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(MainActivity.this, gso);
                                googleSignInClient.signOut();
                                SharedPreferences.Editor editor = user_data.edit();
                                editor.clear().apply();
                                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                                editor = sharedPreferences.edit();
                                editor.clear().apply();
                                for (Medication medication : medicationViewModel.getMedicationsList()) {
                                    stopAlarmManager(medication);
                                }
                                medicationViewModel.deleteAll();
                                Intent intent2 = new Intent(MainActivity.this, LoginActivity.class);
                                intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent2);
                                finish();
                            }
                        })
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //do nothing
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.action_group_by_month);
        spinner = (Spinner) item.getActionView();
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.months, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter); // set the adapter to provide layout of rows and content
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String month = parent.getItemAtPosition(position).toString();
                medicationViewModel.getMedsInMonth(month);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        }); // set the listener, to perform actions based on item selection

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                medicationViewModel.searchMed(query, spinner.getSelectedItem().toString());
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                medicationViewModel.searchMed(newText, spinner.getSelectedItem().toString());
                return true;
            }
        });

        return true;
    }

    private class MedsAdapter extends RecyclerView.Adapter<MedsAdapter.ViewHolder> {
        List<Medication> source;

        private MedsAdapter(List<Medication> source) {
            this.source = source;
        }

        private void add(Medication medication) {
            source.add(medication);
            notifyDataSetChanged();
        }

        private void refresh(List<Medication> medications) {
            this.source = medications;
            notifyDataSetChanged();
        }

        @Override
        public MedsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MedsAdapter.ViewHolder(getLayoutInflater().inflate(R.layout.med_item_layout, parent, false));
        }

        @Override
        public void onBindViewHolder(MedsAdapter.ViewHolder holder, int position) {
            // Log.e("id",source.get(position).getResponse_id()+"");
            holder.name.setText(source.get(position).name);
            holder.desc.setText(source.get(position).description);
            holder.interval.setText("Every " + source.get(position).interval + "hr(s)");
            try {
                holder.date.setText(new SimpleDateFormat("yyyy-MM-dd").format(new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(source.get(position).startDate))
                        + " - " + source.get(position).endDate);
                //  holder.time.setText(new SimpleDateFormat("HH:mm").format(new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(source.get(position).getDate())));

            } catch (Exception e) {
                holder.date.setText(source.get(position).startDate + "-" + source.get(position).endDate);
                // holder.time.setText("00:00");
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return source.size();
        }

        protected class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView name;
            TextView date;
            TextView interval;
            TextView desc;

            private ViewHolder(View itemView) {
                super(itemView);
                name = (TextView) itemView.findViewById(R.id.name);
                date = (TextView) itemView.findViewById(R.id.date);
                interval = (TextView) itemView.findViewById(R.id.interval);
                desc = (TextView) itemView.findViewById(R.id.desc);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(MainActivity.this, view);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.med_action_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_delete:
                                stopAlarmManager(source.get(getAdapterPosition()));
                                medicationViewModel.deleteMed(source.get(getAdapterPosition()));
                                medicationViewModel.getMedsInMonth(spinner.getSelectedItem().toString());
                                return true;
//                            case R.id.action_stop:
//                                stopAlarmManager(source.get(getAdapterPosition()));
//                                return true;
                            case R.id.action_edit:
                                DialogFragment dialogFragment = new AddMedicationDialogFragment();
                                Bundle bundle = new Bundle();
                                bundle.putString("type", "update");
                                bundle.putInt("id", source.get(getAdapterPosition()).id);
                                dialogFragment.setArguments(bundle);
                                dialogFragment.show(getFragmentManager(), "whoop");
                                return true;
                        }
                        return false;
                    }
                });
                popup.show();
            }
//@Override
////public boolean onLongClick(View view) {
////
////    return true;
////}
        }
    }
}
