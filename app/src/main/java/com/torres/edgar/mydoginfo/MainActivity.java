package com.torres.edgar.mydoginfo;

import com.sqlitelib.DataBaseHelper;
import com.sqlitelib.SQLite;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText txtDogname;
    Spinner spinBreed;
    Switch swVaccinated;
    TextView txtVaccinated;
    ListView lstViewdoginfo;
    Button btnAdddogInfo,btnDeletedoginfo;
    public String dogBreed[]={"Labrador Retriever","German Shepherd","BullDog","Boxer","Beagle","Siberian Huskey","Great Dane"};
    public Integer cntrBreed=0,cntrDog=0;
    ArrayAdapter adapterDoginfo;
    public String mdogname="brownie",mbreed="Rotweiller",mvaccinated="Vaccinated";
    public int valueId[] ;


    private DataBaseHelper dbhelper = new DataBaseHelper(MainActivity.this, "DogInfoDatabase", 2);



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtDogname=(EditText)findViewById(R.id.editTextDogName);
        spinBreed=(Spinner)findViewById(R.id.spinnerBreed);
        swVaccinated=(Switch)findViewById(R.id.switchVaccinated);
        txtVaccinated=(TextView)findViewById(R.id.textViewVaccinated);
        lstViewdoginfo =(ListView) findViewById(R.id.listViewDog);

        btnAdddogInfo=(Button)findViewById(R.id.buttonAdd);
        btnDeletedoginfo=(Button) findViewById(R.id.buttonDelete);
        txtVaccinated.setTextColor(0XFF27C408);

        spinBreedpopulate();
        swVaccinatedlistener();
        spinBreedListener();


        reloadDoginfo();
        btnAddListener();
        btnDeleteListener();
        lstViewdoginfoClickItemListener();
        lstViewdoginfoLongClickItemListener();


    }



    private void btnDeleteListener() {
         final SQLiteDatabase dbDelete = dbhelper.getWritableDatabase();

        btnDeletedoginfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);

                // Setting Dialog Title
                alertDialog.setTitle("Confirm Delete...");

                // Setting Dialog Message
                alertDialog.setMessage("Are you sure you want delete selected dog record ?");


                // Setting Icon to Dialog

                alertDialog.setIcon(R.drawable.list_divider);
                // Setting Positive "Yes" Button
                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {

                        // Write your code here to invoke YES event

                        String sqlStr = "DELETE from tbldoginfo where id = '" + cntrDog + "'";

                        dbDelete.execSQL(sqlStr);
                        reloadDoginfo();
                    }
                });

                // Setting Negative "NO" Button
                alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to invoke NO event
                        Toast.makeText(getApplicationContext(), "Deletion have been aborted", Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                });

                // Showing Alert Message
                alertDialog.show();


            }
        });

    }

    private void btnAddListener() {

        final SQLiteDatabase dbAdd = dbhelper.getWritableDatabase();
        btnAdddogInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String sqlStr = "INSERT INTO tbldoginfo (dogname, breed, vaccstatus) VALUES ('"
                        + txtDogname.getText() + "', '" + mbreed + "','"
                        + txtVaccinated.getText() + "')";



                dbAdd.execSQL(sqlStr);
                reloadDoginfo();


            }
        });



    }

    private void reloadDoginfo() {

         SQLiteDatabase dbDoginfo = dbhelper.getWritableDatabase();
        //get table from sqlite_master
        Cursor cDoginfo = dbDoginfo.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='tbldoginfo'", null);
        cDoginfo.moveToNext();


        if (cDoginfo.getCount() == 0) { //check if the database is exisitng
            SQLite.FITCreateTable("DogInfoDatabase", this, "tbldoginfo", "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "dogname VARCHAR(90),breed VARCHAR(90),vaccstatus VARCHAR(90)"); //create table


        } else {


            cDoginfo = dbDoginfo.rawQuery("SELECT id, dogname, breed,vaccstatus  FROM tbldoginfo order by id desc", null);

            String valueDoginfo[] = new String[cDoginfo.getCount()];
            int valueCurrentId[] = new int[cDoginfo.getCount()];


            int ctrl = 0;
            while (cDoginfo.moveToNext()) {
                String strFor = "";
//                Integer strId=0;

                strFor += "Dog Name : " + cDoginfo.getString(cDoginfo.getColumnIndex("dogname"));
                strFor += System.lineSeparator() + "Breed  : " + cDoginfo.getString(cDoginfo.getColumnIndex("breed"));
                strFor += System.lineSeparator() + "Status : " + cDoginfo.getString(cDoginfo.getColumnIndex("vaccstatus"));

                valueCurrentId[ctrl]= cDoginfo.getInt(cDoginfo.getColumnIndex("id"));
                valueDoginfo[ctrl] = strFor;
                ctrl++;
            }

            valueId = Arrays.copyOf(valueCurrentId, cDoginfo.getCount());//transfer content array to a public array


            adapterDoginfo = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, valueDoginfo);

            try {
                lstViewdoginfo.setAdapter(adapterDoginfo);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }



    }
    private void spinBreedpopulate() {


        List<String> listBreed = new ArrayList<String>();
        for (int x=0;x<dogBreed.length;x++) {
            listBreed.add(dogBreed[x]);
        }
        ArrayAdapter<String> dataAdapterStatus = new ArrayAdapter<String>(this,
                R.layout.spinner_item, listBreed);
        dataAdapterStatus.setDropDownViewResource(R.layout.spinner_item_dropdown);
        spinBreed.setAdapter(dataAdapterStatus);
        spinBreed.setPrompt("Select Breed");

    }

    private void spinBreedListener() {

        spinBreed.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mbreed=spinBreed.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

//


    }

    private void swVaccinatedlistener() {

        swVaccinated.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b==true) {
                    txtVaccinated.setTextColor(0XFF080EC4);
                    txtVaccinated.setText("Vaccinated");
                    swVaccinated.setText("Change to Not Vaccinated");

                }

                else {
                    txtVaccinated.setTextColor(0XFF27C408);
                    txtVaccinated.setText("Not Vaccinated");
                    swVaccinated.setText("Change to Vaccinated");
                }
            }
        });
    }

    private void lstViewdoginfoClickItemListener() {
        lstViewdoginfo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    cntrDog=valueId[i];
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String SDogInfo = cntrDog+" "+lstViewdoginfo.getItemAtPosition(i).toString();

                Toast.makeText(MainActivity.this,SDogInfo,Toast.LENGTH_LONG).show();
            }
        });
    }

    private void lstViewdoginfoLongClickItemListener() {
        lstViewdoginfo.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                cntrDog=valueId[i];
                final SQLiteDatabase dbDelete = dbhelper.getWritableDatabase();
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);

                // Setting Dialog Title
                alertDialog.setTitle("Confirm Delete...");

                // Setting Dialog Message
                alertDialog.setMessage("Are you sure you want delete selected dog record ?");


                // Setting Icon to Dialog

                alertDialog.setIcon(R.drawable.list_divider);
                // Setting Positive "Yes" Button
                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {

                        // Write your code here to invoke YES event

                        String sqlStr = "DELETE from tbldoginfo where id = '" + cntrDog + "'";

                        dbDelete.execSQL(sqlStr);
                        reloadDoginfo();
                    }
                });

                // Setting Negative "NO" Button
                alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to invoke NO event
                        Toast.makeText(getApplicationContext(), "Deletion have been aborted", Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                });

                // Showing Alert Message
                alertDialog.show();






                return false;
            }
        });

    }


}

