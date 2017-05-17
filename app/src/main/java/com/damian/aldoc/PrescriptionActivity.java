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
import android.support.v4.content.FileProvider;
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
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PrescriptionActivity extends AppCompatActivity
{
    //sciezka do pliku ze zrobionym zdjeciem recepty
    String m_photo_file_absolute_path;

    private ImageView image_view;
    private ListView list_view;
    private ArrayAdapter<PrescriptionEntry> adapter;
    private List<PrescriptionEntry> prescription_entries = new ArrayList<>();
    private String prescription_uid;
    private String[] med_list;

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

        //czytamy liste lekow z xml
        med_list = getResources().getStringArray(R.array.drugs_array);
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
                AlertDialog.Builder alert_delete = new AlertDialog.Builder(this);
                alert_delete.setTitle("Czy na pewno chcesz usunąć wpis?");

                alert_delete.setCancelable(true).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Database.DeletePrescriptionEntryFromDatabase(pe.getUid());
                    }
                });

                alert_delete.setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                Dialog delete_dialog = alert_delete.create();
                delete_dialog.show();

                break;
            case R.id.prescriptionEntryMenu_edit:
                View view = (LayoutInflater.from(this)).inflate(R.layout.prescription_entry_edit, null);

                AlertDialog.Builder alert_edit = new AlertDialog.Builder(this);
                alert_edit.setView(view);
                alert_edit.setTitle("Wpis z recepty");

                /*przypisujemy liste lekow do autocomplete*/
                final AutoCompleteTextView et_medicine_name = (AutoCompleteTextView)view.findViewById(R.id.prescriptionEntryEdit_medicineName);

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, med_list);
                et_medicine_name.setAdapter(adapter);

                final EditText et_dose_rate = (EditText)view.findViewById(R.id.prescriptionEntryEdit_doseRate);
                et_medicine_name.setText(pe.getMedicineName());
                et_dose_rate.setText(pe.getDoseRate());

                alert_edit.setCancelable(true).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        pe.setMedicineName(et_medicine_name.getText().toString());
                        pe.setDoseRate(et_dose_rate.getText().toString());
                        Database.UpdatePrescriptionEntryInDatabase(pe, pe.getUid());
                    }
                });

                alert_edit.setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                Dialog edit_dialog = alert_edit.create();
                edit_dialog.show();
                break;
        }

        return super.onContextItemSelected(item);
    }

    public void addEntryOnClick(View v)
    {
        View view = (LayoutInflater.from(this)).inflate(R.layout.prescription_entry_edit, null);

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setView(view);
        alertBuilder.setTitle("Wpis z recepty");

        /*przypisujemy liste lekow do autocomplete*/
        final AutoCompleteTextView et_medicine_name = (AutoCompleteTextView)view.findViewById(R.id.prescriptionEntryEdit_medicineName);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, med_list);
        et_medicine_name.setAdapter(adapter);

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

        alertBuilder.setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        Dialog dialog = alertBuilder.create();
        dialog.show();
    }

    /*Funkcja do tworzenia pliku zdjecia recepty*/
    private File createPerscriptionPictureFile() throws IOException
    {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = format.format(Calendar.getInstance().getTime());
        String file_name = "perscription_" + timestamp;

        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                file_name,          /* prefix */
                ".jpg",         /* suffix */
                storageDir          /* directory */
        );

        m_photo_file_absolute_path = image.getAbsolutePath();

        return image;
    }

    public void takePhotoOnClick(View view)
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(intent.resolveActivity(getPackageManager()) != null)
        {
            File photoFile = null;
            try{
                photoFile = createPerscriptionPictureFile();
            }catch (IOException e){}

            if(photoFile != null)
            {
                Uri photoUri = FileProvider.getUriForFile(this, "com.damian.aldoc.fileprovider", photoFile);

                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent, REQUEST_CAMERA);
            }
        }
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
            Bitmap prescription_bitmap = null;
            Uri picture_uri = null;

            if(requestCode == REQUEST_GALLERY)
            {
                picture_uri = data.getData();

                InputStream pictureStream;

                try{
                    pictureStream = getContentResolver().openInputStream(picture_uri);
                    prescription_bitmap = BitmapFactory.decodeStream(pictureStream);
                }catch (FileNotFoundException e)
                {
                    Toast.makeText(this, "Nie udało się załadować obrazu z galerii. Spróbuj ponownie.", Toast.LENGTH_LONG).show();
                }
            }
            else if (requestCode == REQUEST_CAMERA)
            {
                // Ustawiamy domyslne wymiary obrazu
                int targetW = findViewById(R.id.activity_prescription).getWidth();
                int targetH = image_view.getHeight();

                // Pobieramy wymiary obrazu
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                bmOptions.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(m_photo_file_absolute_path, bmOptions);
                int photoW = bmOptions.outWidth;
                int photoH = bmOptions.outHeight;

                // Obliczamy wspolczynnik do wyskalowania obrazu
                int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

                // dekodujemy obraz do bitmapy, odpowiednio wyskalowany
                bmOptions.inJustDecodeBounds = false;
                bmOptions.inSampleSize = scaleFactor;

                prescription_bitmap = BitmapFactory.decodeFile(m_photo_file_absolute_path, bmOptions);

                picture_uri = Uri.parse(m_photo_file_absolute_path);
            }

            /*Jezeli bitmapa zawiera obraz to wstawiamy go do image view
            i zapisujemy do bazy, w przeciwnym razie komunikat o bledzie*/
            if(prescription_bitmap != null)
            {
                image_view.setImageBitmap(prescription_bitmap);
                Database.UploadImageToDatabaseStorageUsingUriAndUpdatePrescription(picture_uri, prescription_uid);
            }
            else
                Toast.makeText(this, "Wystąpił błąd. Spróbuj ponownie.", Toast.LENGTH_LONG).show();
        }
    }

    public void imageOnClick(View v)
    {

    }
}
