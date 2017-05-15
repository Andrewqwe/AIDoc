package com.damian.aldoc;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PrescriptionActivity extends AppCompatActivity
{
    private ImageView image_view;
    private ListView list_view;
    private ArrayAdapter<PrescriptionEntry> adapter;
    private List<PrescriptionEntry> prescription_entries = new ArrayList<>();
    private String prescription_uid;

    private final int REQUEST_GALLERY = 0;
    private final int REQUEST_CAMERA = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prescription);

        String[] prescription_data = getIntent().getStringArrayExtra("prescription");

        prescription_uid = prescription_data[1];

        TextView tv = (TextView)findViewById(R.id.prescription_Name);
        tv.setText(prescription_data[0]);

        image_view = (ImageView)findViewById(R.id.prescription_imageView);
        //Database.StorageDownloadAndDisplayInContextImage(this.getApplicationContext(),Database.StoragePhotoTestReference(),image_view);// - Radek - przykładowe zastosowanie metody do wyświetlana zdjęcia (psyduck)
        //tworzymy listenera, ktory dodaje do listview wszystkie wpisy z recepty
        Database.Initialize(true);
        DatabaseReference ref = Database.SetLocation("prescription_entries");
        Query q = ref.orderByChild("prescriptionUid").equalTo(prescription_uid);
        q.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                PrescriptionEntry pe = dataSnapshot.getValue(PrescriptionEntry.class);
                pe.setUid(dataSnapshot.getKey());

                prescription_entries.add(pe);
                adapter.notifyDataSetChanged();

                //ustawiamy rozmiar list_view aby dopasowac go do zawartosci zeby sie nie scrollowal
//                if(list_view.getChildAt(0) != null)
//                {
//                    ViewGroup.LayoutParams lp = list_view.getLayoutParams();
//                    lp.height = list_view.getCount() * list_view.getChildAt(0).getHeight();
//                    list_view.setLayoutParams(lp);
//                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s)
            {
                PrescriptionEntry pe = dataSnapshot.getValue(PrescriptionEntry.class);
                String uid = dataSnapshot.getKey();
                pe.setUid(uid);

                for(int p = 0; p < prescription_entries.size(); p++)
                {
                    if(prescription_entries.get(p).getUid().equals(uid))
                    {
                        prescription_entries.set(p, pe);
                        adapter.notifyDataSetChanged();
                        break;
                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot)
            {
                String uid = dataSnapshot.getKey();
                for(int p = 0; p < prescription_entries.size(); p++)
                {
                    if(prescription_entries.get(p).getUid().equals(uid))
                    {
                        prescription_entries.remove(p);
                        adapter.notifyDataSetChanged();
                        break;
                    }
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        //tworzymy adapter i przypisujemy go do listview zeby wyswietlac recepty
        adapter = new ArrayAdapter<PrescriptionEntry>(this, android.R.layout.simple_list_item_1, prescription_entries);

        list_view = (ListView)findViewById(R.id.prescription_listView);
        list_view.setAdapter(adapter);
        registerForContextMenu(list_view);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_prescription_entry, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final PrescriptionEntry pe = prescription_entries.get(info.position);

        switch (item.getItemId())
        {
            case R.id.prescriptionEntryMenu_delete:
                Database.DeletePrescriptionFromDatabase(pe.getUid());
                break;
            case R.id.prescriptionEntryMenu_edit:
                View view = (LayoutInflater.from(this)).inflate(R.layout.prescription_entry_edit, null);

                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                alertBuilder.setView(view);
                alertBuilder.setTitle("Prescription Entry");

                final EditText et_medicine_name = (EditText)view.findViewById(R.id.prescriptionEntryEdit_medicineName);
                final EditText et_dose_rate = (EditText)view.findViewById(R.id.prescriptionEntryEdit_doseRate);
                et_medicine_name.setText(pe.getMedicineName());
                et_dose_rate.setText(pe.getDoseRate());

                alertBuilder.setCancelable(true).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        pe.setMedicineName(et_medicine_name.getText().toString());
                        pe.setDoseRate(et_dose_rate.getText().toString());
                        Database.UpdatePrescriptionEntryInDatabase(pe, pe.getUid());
                    }
                });

                alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                Dialog dialog = alertBuilder.create();
                dialog.show();
                break;
        }

        return super.onContextItemSelected(item);
    }

    public void addEntryOnClick(View v)
    {
        View view = (LayoutInflater.from(this)).inflate(R.layout.prescription_entry_edit, null);

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setView(view);
        alertBuilder.setTitle("Prescription Entry");

        final EditText et_medicine_name = (EditText)view.findViewById(R.id.prescriptionEntryEdit_medicineName);
        final EditText et_dose_rate = (EditText)view.findViewById(R.id.prescriptionEntryEdit_doseRate);

        alertBuilder.setCancelable(true).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                String med_name = et_medicine_name.getText().toString();
                String dose_rate = et_dose_rate.getText().toString();

                PrescriptionEntry pe = new PrescriptionEntry(prescription_uid, med_name, dose_rate);
                Database.SendObjectPrescriptionEntryToDatabase(pe);
            }
        });

        alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        Dialog dialog = alertBuilder.create();
        dialog.show();
    }

    /*Funkcja do generowania nazwy pliku zdjec*/
    private String makePerscriptionPictureName()
    {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = format.format(Calendar.getInstance().getTime());
        return "perscription_" + timestamp + ".jpg";
    }

    public void takePhotoOnClick(View view)
    {
        //File pictureDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        //String pictureName = makePerscriptionPictureName();
        //File picureFile = new File(pictureDir, pictureName);
        //Uri pictureUri = Uri.fromFile(picureFile);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //intent.putExtra(MediaStore.EXTRA_OUTPUT, pictureUri);

        startActivityForResult(intent, REQUEST_CAMERA);
    }


    public void fromGalleryOnClick(View view)
    {
        File pictureDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String pictureDirPath = pictureDir.getPath();
        Uri data = Uri.parse(pictureDirPath);

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(data, "image/*");

        startActivityForResult(intent, REQUEST_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK)
        {
            if(requestCode == REQUEST_GALLERY)
            {
                Uri pictureUri = data.getData();

                InputStream pictureStream;

                try{
                    pictureStream = getContentResolver().openInputStream(pictureUri);

                    Bitmap galleryPicture = BitmapFactory.decodeStream(pictureStream);
                    image_view.setImageBitmap(galleryPicture);
                }catch (FileNotFoundException e)
                {
                    Toast.makeText(this, "Failed to load picture from gallery.", Toast.LENGTH_LONG);
                }
            }
            else if (requestCode == REQUEST_CAMERA)
            {
                Bitmap cameraPicture = (Bitmap)data.getExtras().get("data");
                image_view.setImageBitmap(cameraPicture);
            }
        }
    }
}
