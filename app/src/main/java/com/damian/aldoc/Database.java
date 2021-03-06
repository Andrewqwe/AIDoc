package com.damian.aldoc;

import com.bumptech.glide.Glide;
import com.damian.aldoc.visits.Prescription;
import com.damian.aldoc.visits.PrescriptionEntry;
import com.damian.aldoc.visits.Visit;
import com.damian.aldoc.visits.VisitsActivity;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.*;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.twitter.sdk.android.core.TwitterCore.TAG;


/**
 * Created by Radosław on 18-04-2017.
 * Update by Radosław on 24-04-2017.
 */

/**
 *
 * Klasa odpowiedzialna za obsługę bazy danych (w trakcie tworzenia)
 *
 */
public class Database {

    //Firebase instance variables
    static private FirebaseDatabase mDatabase;
    static private DatabaseReference mDatabaseReference;
    static private ChildEventListener mChildEventListener;

    static private String mCurrentUid;

    /*nazwy folderow z danymi w bazie*/
    static final private String visits_dir = "visits";
    static final private String prescriptions_dir = "prescriptions";
    static final private String prescriptionEntries_dir = "prescription_entries";
    static final private String notes_dir = "notes";
    static final private String diseases_dir = "diseases";
    static final private String users_dir = "users";
    static final private String groups_dir = "groups";

    /*funkcje do zwracania nazw katalogow w bazie danych*/
    public static String getVisitsDirName() { return visits_dir; }
    public static String getPrescriptionsDirName() { return prescriptions_dir; }
    public static String getPrescriptionEntriesDirName() { return prescriptionEntries_dir; }
    public static String getNotesDirName() { return notes_dir; }
    public static String getDiseasesDirName() { return diseases_dir; }
    public static String getUsersDirName() { return users_dir; }
    public static String getGroupsDirName() { return groups_dir; }

    /*funkcje do zwracania sciezek do katalogow uzytkownikow w bazie danych*/
    public static String getVisitsPath() { return users_dir + "/" + mCurrentUid + "/" + visits_dir; }
    public static String getPrescriptionsPath() { return users_dir + "/" + mCurrentUid + "/" + prescriptions_dir; }
    public static String getPrescriptionEntriesPath() { return users_dir + "/" + mCurrentUid + "/" + prescriptionEntries_dir; }
    public static String getNotesPath() { return users_dir + "/" + mCurrentUid + "/" + notes_dir; }
    public static String getDiseasesPath() { return users_dir + "/" + mCurrentUid + "/" + diseases_dir; }

    public static void setCurrentUid(String uid)
    {
        mCurrentUid = uid;
    }

    public static String getCurrentUid()
    {
        return mCurrentUid;
    }

    public static void createGroupInDatabase(String group_name, String group_owner_uid)
    {
        /*Tworzymy referencje do lokacji z grupami w bazie*/
        DatabaseReference groups_ref = SetLocation(groups_dir);

        /*Generujemy uid nowej grupy w bazie*/
        String gid = groups_ref.push().getKey();

        /*Tworzymy nowa grupe i ustawiamy jej nazwe w bazie*/
        groups_ref.child(gid).child("name").setValue(group_name);

        /*Dodajemy do grupy użytkownika który ją utworzył
        * z poziomem uprawnień 0 - superadmin*/
        groups_ref.child(gid).child("users").child(group_owner_uid).setValue(0);

        /*Do obiektu uzytkownika w bazie dodajemy wpis o przynależności do nowej grupy*/
        DatabaseReference user_groups_ref = SetLocation(users_dir).child(group_owner_uid).child("groups");
        user_groups_ref.push().setValue(gid);
    }

    public static void deleteGroupFromDatabase(String group_uid)
    {
        //TODO:Zaimplementować usuwanie grup z bazy
    }

    public static void addUserToGroup(final String user_id, final String group_id, final Long permission_level)
    {
        /*Czytamy z bazy listę grup danego użytkownika zeby sprawdzić czy już nie nalezy do tej grupy*/
        final DatabaseReference ref = SetLocation(users_dir).child(user_id).child("groups");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, String> group_ids = (HashMap<String,String>)dataSnapshot.getValue();

                if(group_ids != null) {
                    if (group_ids.containsValue(group_id)) {
                        return;
                    }
                }

                /*jeżeli nie należy to dodajemy do obiektu uzytkownika id grupy*/
                ref.push().setValue(group_id);
                /*oraz do obiektu grupy id użytkownika*/
                SetLocation(groups_dir).child(group_id).child("users").child(user_id).setValue(permission_level);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public static void removeUserFromGroup(final String user_id, final String group_id)
    {
        /*Czytamy z bazy listę grup danego użytkownika zeby sprawdzić czy nalezy do tej grupy
        * i jesli tak to pobrac z bazy id wpisu z danym id grupy*/
        final DatabaseReference ref = SetLocation(users_dir).child(user_id).child("groups");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, String> group_ids = (HashMap<String,String>)dataSnapshot.getValue();

                if(group_ids != null) {

                    for (final String key : group_ids.keySet()) {
                        if (group_ids.get(key).equals(group_id))
                        {
                            /*sprawdzamy czy użytkownik nie jest właścicielem grupy bo wtedy nie możemy go z niej usunąć*/
                            final DatabaseReference group_ref = SetLocation(groups_dir).child(group_id).child("users").child(user_id);
                            group_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Long permission_level = dataSnapshot.getValue(Long.class);

                                    if(permission_level != 0) {
                                        /*usuwamy wpis o grupie z obiektu użytkownika*/
                                            ref.child(key).removeValue();
                                        /*usuwamy z grupy wpis o użytkowniku*/
                                            group_ref.removeValue();
                                    }
                                    else
                                    {
                                        Toast.makeText(getApplicationContext(), "Nie można usunąć właściciela grupy!", Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {}
                            });

                            return;
                        }

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public static void Initialize(boolean persistence) {
        if (mDatabase == null){
            mDatabase = FirebaseDatabase.getInstance();
            mDatabase.setPersistenceEnabled(persistence);
        }
    }

    static public DatabaseReference SetLocation(String path) {
        mDatabaseReference = mDatabase.getReference().child(path);
        return mDatabaseReference;
    }

    //zawsze jest powód do użycia noża

    /**
     * Metoda publiczna do wysyłania dancyh do naszej bazy.
     *
     * @param path  - ścieżka do lokacji nadrzędnej do której chcemy wysłać dane
     * @param object - wysyłany obiekt (obecnie wysyłamy tylko obiekty!!!)
     */
    static public void SendObjectToDatabase(String path, Object object) {
        Initialize(true);
        SetLocation(path);
        mDatabaseReference.push().setValue(object);  //wysyła obiekt object do bazy jako dziecko lokacji path
    }

    /**
     * Metoda publiczna wysyłająca obiekt do lokacji visits w Bazie danych
     * @param object wysyłany obiekt klasy Visit
     */
    static public void SendObjectVisitToDatabase(Object object) {
        Initialize(true);
        SetLocation(getVisitsPath());
        mDatabaseReference.push().setValue(object);
    }

    /**
     * Metoda publiczna wysyłająca obiekt do lokacji prescriptions w Bazie danych
     * @param object wysyłany obiekt klasy Prescription
     */
    static public void SendObjectPrescriptionToDatabase(Object object) {
        Initialize(true);
        SetLocation(getPrescriptionsPath());
        mDatabaseReference.push().setValue(object);
    }

    /**
     * Metoda publiczna wysyłająca obiekt do lokacji prescription_entries w Bazie danych
     * @param object wysyłany obiekt klasy PrescriptionEntry
     */
    static public void SendObjectPrescriptionEntryToDatabase(Object object) {
        Initialize(true);
        SetLocation(getPrescriptionEntriesPath());
        mDatabaseReference.push().setValue(object);
    }

    /**
     * Metoda publiczna wysyłająca obiekt do lokacji notes w Bazie danych
     * @param object wysyłany obiekt klasy Note - której jeszcze nie ma ????
     */
    static public void SendObjectNotesToDatabase(Object object) {
        Initialize(true);
        SetLocation(getNotesPath());
        mDatabaseReference.push().setValue(object);
    }

    static public void SendObjectDiseasesToDatabase(Object object) {
        Initialize(true);
        SetLocation(getDiseasesPath());
        mDatabaseReference.push().setValue(object);
    }

    /**
     * Metoda prywatna pobierająca z bazy danych dane o zalogowanym użytkowniku
     * @return Zwraca tabele stringów gdzie kolejno jest nazwa użytkownika,e-mail,UID lub pustą tabelę gdy użytkownik nie jest zalogowany
     */
    static public String[]  GetUserInfo(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address
            String name = user.getDisplayName();
            String email = user.getEmail();
            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            String uid = user.getUid();
            String[] details = {name,email,uid};
            return details;
        }
            return null;
    }

    /**
     * Metoda sprawdzająca czy Email użytkownika jest zweryfikowany
     * @return Zwraca wartość true jeżeli jest e-mail zweryfikowany lub false w przeciwnym razie oraz null jeżeli użytkownik nie jest w ogóle zalogowany.
     */
    static public Boolean UserEmailVerified(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) return user.isEmailVerified();
        return null;
    }

    /**
     * Metoda pobierająca avatar(zdjęcie użytkownika) z ustawionego z pola logowania (np avatar google)
     * @return Zwraca Url do zdjęcia jeżeli użytkownik jest zalogowany
     */
    static public Uri  GetUserImage(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Uri photoUrl = user.getPhotoUrl();
            return photoUrl;
        }
        return null;
    }

    /**
     * Metoda publiczna pobierająca z bazy UID użytkownika
     * @return String UID lub null gdy użytkownik nie jest zalogowany
     */
    static public String GetUserUID() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            return uid;
        }
        return null;
    }

    /**
     * Metoda publiczna tworząca profil użytkownika bazujący na UID. Funkcja pobiera imie(nazwe uzytkownika) i adres e-mail z danych podanych przy rejestracji(logowaniu)
     * Funkcja nadpisuje profil użytkownika tz nie wypełnienie wszystkich danych będzie skutkować wyczyszczeniem niewypełnionych pól.
     * Funkcja tworzy obiekt klasy User w trakcie wykonania (dobrze żeby tak pozostało)

     */
    static public void SendUserInfoToDatabase() {
        Initialize(true);
       DatabaseReference users = SetLocation(users_dir);
        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.child(GetUserUID()).exists()) {
                    // run some code
                }else{
                    String[] details = GetUserInfo();
                    User user = new User(details[0],details[1]);
                    mDatabaseReference.child(GetUserUID()).setValue(user);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
       // mDatabaseReference.child(GetUserUID()).child("pesel").setValue(pesel); - podmienianie tylko gałęzi pesel (można warunek if zrobić i sprawdzać czy pesel != null i dopiero wtedy podmieniać) to już zależy od metody zrobienia panelu do wprowadzania danych
    }

    /**
     * Wysyła i uaktoalnia nr pesel użytkownika w bazie danych
     * @param pesel nr pesel użytkownika
     */

    static public void SendUserPeselToDatabase(String pesel){
        Initialize(true);
        SetLocation(users_dir);
        if(pesel != null)
        mDatabaseReference.child(GetUserUID()).child("Pesel").setValue(pesel);
    }

    /**
     * Wysyła i uaktualnia numer telefonu komórkowego użytkownika w bazie danych. Można wywołać podczas wprowadzania numer i późniejszej jego zmiany.
     * @param phone nr telefonu użytkownika
     */
    static public void SendUserPhoneNumberToDatabase(String phone){
        Initialize(true);
        SetLocation(users_dir);
        if(phone != null)
        mDatabaseReference.child(GetUserUID()).child("phone").setValue(phone);
    }
    /**
     * Obecnie Read From Visits nie dziala i działac nie będzie, kod jako przykład zastosowania budowy w klasie
     */

    static private void ReadFromVisits() {
        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Visit visits = dataSnapshot.getValue(Visit.class);  //czytanie z bazy i tworzenie przycisku
                VisitsActivity helper = new VisitsActivity();
                //helper.addVisit(visits);
            }
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            public void onChildRemoved(DataSnapshot dataSnapshot) {}
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        SetLocation(getVisitsPath()).addChildEventListener(mChildEventListener);
    }

    static private void ReadProfileDetails(){

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                User user = dataSnapshot.getValue(User.class);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Profile failed, log a message
                Log.w(TAG, "loadProfile:onCancelled", databaseError.toException());
            }
        };
        SetLocation("profile").addListenerForSingleValueEvent(postListener);
    }
    static private void pesel(String pesel){
        ReadProfileDetails();
    };

    /**
     * Metoda do kasowania całego obiektu o danym kluczu.
     * @param uid klucz do danego miejsca w wizytach np. -KivIPsb0iuUBuOns6Bv
     */
    static public void DeleteVisitFromDatabase(String uid){
        Initialize(true);
        DatabaseReference ref = SetLocation(getVisitsPath());
        ref.child(uid).removeValue();
    }

    /**
     * Metoda do kasowania całego obiektu o danym kluczu.
     * @param uid klucz do danego miejsca w prescriptions np. -KivIPsb0iuUBuOns6Bv
     */
    static public void DeletePrescriptionFromDatabase(String uid){
        Initialize(true);
        DatabaseReference ref = SetLocation(getPrescriptionsPath());
        ref.child(uid).removeValue();
    }


    static public void DeletePrescriptionEntryFromDatabase(String uid){
        Initialize(true);
        DatabaseReference ref = SetLocation(getPrescriptionEntriesPath());
        ref.child(uid).removeValue();
    }

    /**
     * Metoda do kasowania całego obiektu o danym kluczu.
     * @param uid klucz do danego miejsca w notes np. -KivIPsb0iuUBuOns6Bv
     */

    static public void DeleteNoteFromDatabase(String uid){
        Initialize(true);
        DatabaseReference ref = SetLocation(getNotesPath());
        ref.child(uid).removeValue();
    }

    static public void DeleteDiseaseFromDatabase(String uid){
        Initialize(true);
        DatabaseReference ref = SetLocation(getDiseasesPath());
        ref.child(uid).removeValue();
    }
    /**
     *Metoda która wyszukuje w bazie wizyty które na konkretnej pozycji - parametrName mają dokładną wartość - value
     * Metoda nic nie zwraca dlatego w OnChildAdded należy dodać wywołanie własnej funkcji która będzie coś robiła z tymi obiektami
     * DataSnapshot zawsze tworzy liste nawet 1 elementową (komputer jest głupi i nie wie czy szukana przez nas dana jest tylko w 1 miejscu)
     * @param parametrName nazwa zmiennej którą chcemy zmienić np location
     * @param value wartość na jaką chcemy podmienić np Breslav
     * TODO: To chyba do wyjebania :p
     */
    static public void GetVisitByValueFromDatabase(String parametrName, String value){
        Initialize(true);
        DatabaseReference ref = SetLocation(getVisitsPath());
        Query aa= ref.orderByChild(parametrName).equalTo(value);
        aa.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                System.out.println(dataSnapshot.getKey());  //tutaj podmienic na funkcjie która ma działać z otrzymanym kluczem.
                //wyżej dataSnapshot.getKey() otrzymuje uid znalezionych obiektów. ten print zostanie wywołany tyle razy ile obiektów będzie zgadzało się z naszym wyszukiwaniem!!!!!!!
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });


    }

    static public void UpdateObjectInDatabase(String path, Object object, String uid) {
        Initialize(true);
        SetLocation(path);
        mDatabaseReference.child(uid).setValue(object);
    }

    static public void UpdateVisitInDatabase(Visit visit, String uid) {
        Initialize(true);
        SetLocation(getVisitsPath());
        mDatabaseReference.child(uid).setValue(visit);
    }

    static public void UpdatePrescriptionInDatabase(Prescription prescription, String uid) {
        Initialize(true);
        SetLocation(getPrescriptionsPath());
        mDatabaseReference.child(uid).setValue(prescription);
    }

    static public void UpdatePrescriptionEntryInDatabase(PrescriptionEntry prescription_entry, String uid) {
        Initialize(true);
        SetLocation(getPrescriptionEntriesPath());
        mDatabaseReference.child(uid).setValue(prescription_entry);
    }

    static public void UpdateNoteInDatabase(Note note, String uid) {
        Initialize(true);
        SetLocation(getNotesPath());
        mDatabaseReference.child(uid).setValue(note);
    }
    /**
     *Metoda do podmiany wartości w sprecyzowanym miejscu w bazie. Tą metodą można też dodać nową zmienną jeżeli podamy parametrName który nie znajduje się w bazie.
     * @param uid klucz do danej lokacji w bazie (ten długi np.-KivIPsb0iuUBuOns6Bv)
     * @param parametrName nazwa zmiennej którą chcemy zmienić np location
     * @param value wartość na jaką chcemy podmienić np Breslav
     */
    static public void ModifyValueInDatabase(String uid,String parametrName, String value){
        Initialize(true);
        DatabaseReference ref = SetLocation("visits");
        Map<String, Object> nickname = new HashMap<String, Object>();
        nickname.put(parametrName, value);
        ref.child(uid).updateChildren(nickname);
    }

    static public void AddUriInPrescriptionInDatabase(String uid, Uri value) {
    System.out.println("--------------------------------------------------");
        System.out.println(uid);
        System.out.println(value);
        System.out.println("--------------------------------------------------");
        Initialize(true);
        SetLocation(getPrescriptionsPath());
        mDatabaseReference.child(uid).child("photo").setValue(value.toString());
    }

    static public StorageReference StorageInitialize(){
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://aidoc-14c21.appspot.com/");
        StorageReference storageRef = storage.getReference();
        return storageRef;
    }

    /**
     * Funkcja zmieniajaca uri na referencje do storage(używać jako paratetr wejścia do metody StorageDownloadAndDisplayInContextImage jako StorageReference)
     * @param uri Uri na którą chcemy utworzyc referencje w storage
     * @return Zwraca referencje na zdjecie wskazywane przez uri
     */
    static private StorageReference ReturnStorageReferenceToPassedUri(Uri uri){
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://aidoc-14c21.appspot.com/");
        return storage.getReferenceFromUrl(String.valueOf(uri));
    }

    /**
     * Funkcja wyświetlająca zdjęcie wskazywane przez StorageReference w polu imageView w zadanym context.
     * @param context Przekazanie contextu aktywności (najlepiej używać this.getApplicationContext())
     * @param uri Przekazywanie uri zdjęcia znajdującego się w bazie danych
     * @param imageView Przekazanie imageView(miejsce gdzie zostanie wyświetlone zdjęcie)
     */
    static public void StorageDownloadAndDisplayInContextImage(Context context,Uri uri, ImageView imageView){
        Glide.with(context)
                .using(new FirebaseImageLoader())
                .load(ReturnStorageReferenceToPassedUri(uri))
                .into(imageView);
    }

    /**
     * Metoda wrzucająca zdjęcie do storage(bazy)  coś jest nie tak z return, przy wylowaniu dostaniemy null badź uri ostatnio wysyłanego pliku (odpala wysyłanie które jest w tle i zanim wysle plik i otrzyma uri zwraca wartość uri w return)
     * @param path uri do pliku jaki chcemy wrzucić
     * @return Zwraca Uri zdjęcia przez nas wrzuconego (można to uri dopisać do notatki aby przypisac dane zdjęcie)
     */
    @SuppressWarnings("VisibleForTests")
    static public Uri UploadImageToDatabaseStorageUsingUri(Uri path){
        StorageReference ref = StorageInitialize();
        StorageReference riversRef = ref.child("images/"+path.getLastPathSegment());
        UploadTask uploadTask = riversRef.putFile(path);
        // Register observers to listen for when the download is done or if it fails
        // Listen for state changes, errors, and completion of the upload.
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                Toast.makeText(getApplicationContext(), "Upload is " + progress + "% done", Toast.LENGTH_SHORT).show();
                System.out.println("Upload is " + progress + "% done");
            }
        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                System.out.println("Upload is paused");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Handle successful uploads on complete
                //Uri downloadUrl = taskSnapshot.getMetadata().getDownloadUrl();
                //URLTEST(ReturnStorageReferenceToPassedUri); Tą metodą można utworzyć referencje na konkretne zdjęcie
                Toast.makeText(getApplicationContext(), "Your file was sent successfully.", Toast.LENGTH_SHORT).show();
                aaa = taskSnapshot.getMetadata().getDownloadUrl();
                // Toast.makeText(getApplicationContext(), String.valueOf(aaa), Toast.LENGTH_SHORT).show();
            }
        });
        return aaa;
    }

    /**
     *coś jest nie tak z return, przy wylowaniu dostaniemy null badź uri ostatnio wysyłanego pliku (odpala wysyłanie które jest w tle i zanim wysle plik i otrzyma uri zwraca wartość uri w return)
     * @param path Pobiera ścieżkę do pliku znajdującego się na telefonie w String
     * @return Zwraca Uri zdjęcia przez nas wrzuconego (można to uri dopisać do notatki aby przypisac dane zdjęcie)
     */
    @SuppressWarnings("VisibleForTests")
    static public Uri UploadImageToDatabaseStorageUsingPath(String path){
        StorageReference ref = StorageInitialize();
        Uri file = Uri.fromFile(new File(path));
        StorageReference riversRef = ref.child("images/"+file.getLastPathSegment());

        // Create the file metadata
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/jpeg")
                .build();


        UploadTask uploadTask = riversRef.putFile(file,metadata);
        // Register observers to listen for when the download is done or if it fails
        // Listen for state changes, errors, and completion of the upload.
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                Toast.makeText(getApplicationContext(), "Upload is " + progress + "% done", Toast.LENGTH_SHORT).show();
                System.out.println("Upload is " + progress + "% done");
            }
        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                System.out.println("Upload is paused");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Handle successful uploads on complete
                //Uri downloadUrl = taskSnapshot.getMetadata().getDownloadUrl();
                //URLTEST(ReturnStorageReferenceToPassedUri); Tą metodą można utworzyć referencje na konkretne zdjęcie
                Toast.makeText(getApplicationContext(), "Your file was sent successfully.", Toast.LENGTH_SHORT).show();
                aaa = taskSnapshot.getMetadata().getDownloadUrl();   // wrzucic ten url do notatki - czekac na dokladne instrukcjie od mateusza
               // Toast.makeText(getApplicationContext(), String.valueOf(aaa), Toast.LENGTH_SHORT).show();
            }
        });
        return aaa;  //do przebudowy - tak byc nie będzie
    }

static Uri aaa;


    /**
     * Metoda wrzucająca zdjęcie do storage(bazy)  coś jest nie tak z return, przy wylowaniu dostaniemy null badź uri ostatnio wysyłanego pliku (odpala wysyłanie które jest w tle i zanim wysle plik i otrzyma uri zwraca wartość uri w return)
     * @param path uri do pliku jaki chcemy wrzucić
     * @return Zwraca Uri zdjęcia przez nas wrzuconego (można to uri dopisać do notatki aby przypisac dane zdjęcie)
     */
    @SuppressWarnings("VisibleForTests")
    static public void UploadImageToDatabaseStorageUsingUriAndUpdatePrescription(Uri path,final String uid){
        StorageReference ref = StorageInitialize();
        StorageReference riversRef = ref.child("images/"+path.getLastPathSegment());
        UploadTask uploadTask = riversRef.putFile(path);
        // Register observers to listen for when the download is done or if it fails
        // Listen for state changes, errors, and completion of the upload.
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                Toast.makeText(getApplicationContext(), "Postęp przesyłania: " + progress + "%", Toast.LENGTH_SHORT).show();
                System.out.println("Postęp przesyłania:" + progress + "% ");
            }
        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                System.out.println("Wysyłanie zostało zatrzymane");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getApplicationContext(), "Zdjęcie zostało pomyślnie wysłane.", Toast.LENGTH_SHORT).show();
                Uri downloadUri = taskSnapshot.getMetadata().getDownloadUrl();
                AddUriInPrescriptionInDatabase(uid,downloadUri);
            }
        });
    }
}
// TODO  Metoda odczytująca z bazy danych. Metody wysyłające dane do większej ilości funkcjionalności. Podpięcie tworzenia grup i dodawania członków rodziny do nich.
//TODO:Zrobic statyczna tablice stringow z nazwami lekow i pobrac je z bazy tylko raz, zeby potem inne aktywnosci nie musialy za kazdym razem pobierac

