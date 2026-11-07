package com.example.grocerylistapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;

public class    MainActivity extends AppCompatActivity {

    private Button btnAdd, btnDelete, btnUpdate, btnViewDB, btnAddImg;
    private EditText etName, etAmount;
    private TextView tvStatus, tvStats, tvLogOut;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ActivityResultLauncher<String[]> galleryLauncher;
    private ActivityResultLauncher<Uri> cameraLauncher;
    private  FirestoreDAO fsDAO = new FirestoreDAO();
    private ImageDAO imgDAO = new ImageDAO();
    private Uri cameraUri, selectedUri;
    private boolean sampleDrawable,selectedFromCamera;
    private ImageView ivPreview;
    private int originalWidth,originalHeight;
    private long originalBytes;
    private String USER_COLLECTION = "users", name = "";






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnAdd = findViewById(R.id.btnAdd);
        btnDelete = findViewById(R.id.btnDelete);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnViewDB = findViewById(R.id.btnViewDB);
        btnAddImg = findViewById(R.id.btnAddImg);
        etName = findViewById(R.id.etName);
        etAmount = findViewById(R.id.etAmount);
        tvStatus = findViewById(R.id.tvStatus);
        tvStats = findViewById(R.id.tvStats);
        ivPreview = findViewById(R.id.ivPreview);
        tvLogOut = findViewById(R.id.tvLogOut);

        checkRegister();


        tvLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut();
            }
        });
        db.collection(USER_COLLECTION).document(mAuth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()) {
                    name = documentSnapshot.getString("fName");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("MAIN_ACTIVITY", e.toString());
            }
        });

        galleryLauncher = registerForActivityResult(new ActivityResultContracts.OpenDocument(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri uri) {
                if(uri == null) return;

                try {
                    MainActivity.this.getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }catch (SecurityException ignored){

                }

                MainActivity.this.chooseUri(uri,false);
            }
        });
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                new ActivityResultCallback<Boolean>() {

                    @Override
                    public void onActivityResult(Boolean success) {

                        if (Boolean.TRUE.equals(success)
                                && cameraUri != null) {

                            MainActivity.this.chooseUri(
                                    cameraUri, true);

                        } else {

                            if (cameraUri != null) {
                                MainActivity.this
                                        .getContentResolver()
                                        .delete(cameraUri, null, null);
                            }

                            MainActivity.this.status("הצילום בוטל");
                        }

                        cameraUri = null;
                    }
                });


        btnViewDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ViewDatabaseActivity.class);
                startActivity(intent);
            }
        });

        btnAddImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSourceMenu();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Bitmap bitmap = loadForCompression();

                    if (bitmap == null) {
                        status("אין תמונה");
                        return;
                    }

                    String base64Img = ImageUtils.convertBitmapToBase64(bitmap);
                    if (base64Img == null) {
                        status("שגיאה בדחיסת התמונה");
                        return; // Valid here
                    }

                    if (!etAmount.getText().toString().trim().isEmpty() &&
                            !etName.getText().toString().trim().isEmpty() &&
                            (ivPreview.getDrawable() != null && !sampleDrawable)) {


                        fsDAO.createDocument(
                                etName.getText().toString(),
                                Integer.parseInt(etAmount.getText().toString()),
                                name,
                                base64Img
                        );

                        Toast.makeText(MainActivity.this, "המוצר נוסף בהצלחה", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "לפחות אחד מהשדות לא מלא או שלא נבחרה תמונה", Toast.LENGTH_SHORT).show();
                    }

                    etName.setText("");
                    etAmount.setText("");

                } catch (IOException e) {
                    status("שגיאה בטעינת התמונה");
                }
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!etName.getText().toString().trim().isEmpty())
                {
                    fsDAO.deleteDocument(etName.getText().toString());
                    Toast.makeText(MainActivity.this, "המוצר נמחק", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this, "אנא מלא שם", Toast.LENGTH_SHORT).show();
                }
                etName.setText("");
                etAmount.setText("");
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!etAmount.getText().toString().trim().isEmpty()&&!etName.getText().toString().trim().isEmpty()){
                    fsDAO.updateDocument(etName.getText().toString(), Integer.parseInt(etAmount.getText().toString()));
                    Toast.makeText(MainActivity.this, "כמות המוצר עודכנה", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this, "לפחות אחד מהשדות לא מלא", Toast.LENGTH_SHORT).show();
                }
                etName.setText("");
                etAmount.setText("");
            }

        });


    }

    private void logOut() {
        FirebaseAuth.getInstance().signOut();

        Intent intent = new Intent(this, AuthActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        Toast.makeText(this, "התנתקת בהצלחה",
                Toast.LENGTH_SHORT).show();

    }

    private void checkRegister() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            Intent intent = new Intent(MainActivity.this, AuthActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        db.collection(USER_COLLECTION).document(currentUser.getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        // If the document doesn't exist or 'fName' is missing, stage 2 is incomplete
                        if (documentSnapshot.exists() && documentSnapshot.getString("fName") != null) {
                            name = documentSnapshot.getString("fName");
                        } else {
                            // Redirect to Stage 2 registration
                            startActivity(new Intent(MainActivity.this, NextAuthStepActivity.class));
                            finish();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("MAIN_ACTIVITY", "Failed to fetch user profile", e);
                        Toast.makeText(MainActivity.this, "שגיאה בטעינת הנתונים", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private Bitmap loadForCompression() throws IOException {

        if (sampleDrawable) {
            Bitmap bitmap = BitmapFactory.decodeResource(
                    getResources(),android.R.drawable.ic_menu_camera);

            if (bitmap == null) {
                throw new IOException(
                        "לא ניתן לטעון את תמונת ה-Drawable");
            }

            return bitmap;
        }

        if (selectedUri == null)
            throw new IOException("יש לבחור תמונה תחילה");

        ImageDecoder.Source source =
                ImageDecoder.createSource(
                        getContentResolver(), selectedUri);

        return ImageDecoder.decodeBitmap(source,
                new ImageDecoder.OnHeaderDecodedListener() {
                    @Override
                    public void onHeaderDecoded(
                            @NonNull ImageDecoder decoder,
                            @NonNull ImageDecoder.ImageInfo info,
                            @NonNull ImageDecoder.Source src) {

                        int w = info.getSize().getWidth();
                        int h = info.getSize().getHeight();

                        float scale = Math.min(1f,
                                1280f / Math.max(w, h));

                        decoder.setTargetSize(
                                Math.max(1, Math.round(w * scale)),
                                Math.max(1, Math.round(h * scale)));

                        decoder.setAllocator(
                                ImageDecoder.ALLOCATOR_SOFTWARE);
                    }
                });
    }

    private void chooseUri(Uri uri, boolean fromCamera){
        selectedUri = uri;
        sampleDrawable = false;
        selectedFromCamera = fromCamera;

        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;

            try (InputStream input =
                         getContentResolver().openInputStream(uri)) {
                BitmapFactory.decodeStream(input, null, options);
            }

            originalWidth = options.outWidth;
            originalHeight = options.outHeight;

            if (originalWidth <= 0 || originalHeight <= 0)
                throw new IOException("קובץ התמונה אינו תקין");

            originalBytes = fileSize(uri);

            ivPreview.setImageURI(null);
            ivPreview.setImageURI(uri);

            tvStats.setText("מקור: "
                    + (fromCamera ? "מצלמה" : "גלריה")
                    + " | ממדים: " + originalWidth
                    + "×" + originalHeight
                    + " | קובץ מקורי: "
                    + formatBytes(originalBytes));

            status("התמונה נבחרה");

        } catch (Exception e) {
            selectedUri = null;
            status("לא ניתן לקרוא את התמונה: " + e.getMessage());
        }

    }

    private String formatBytes(long bytes) {
        if (bytes < 0) return "לא ידוע";
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        char pre = "KMGTPE".charAt(exp - 1);
        return String.format(Locale.US, "%.1f %cB", bytes / Math.pow(1024, exp), pre);
    }

    private long fileSize(Uri uri) {
        try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                if (sizeIndex != -1) {
                    return cursor.getLong(sizeIndex);
                }
            }
        } catch (Exception ignored) {
        }
        return -1;
    }

    private void showSourceMenu() {

        PopupMenu menu = new PopupMenu(
                this, findViewById(R.id.btnAddImg));

        menu.getMenu().add(Menu.NONE, 1, 1, "בחר מהגלריה");
        menu.getMenu().add(Menu.NONE, 2, 2, "צלם במצלמה");

        menu.setOnMenuItemClickListener(
                new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        if (item.getItemId() == 1) {

                            galleryLauncher.launch(
                                    new String[]{"image/*"});

                        } else if (item.getItemId() == 2) {

                            try {
                                cameraUri =
                                        MainActivity.this.createGalleryItem(
                                                "camera_"
                                                        + System.currentTimeMillis()
                                                        + ".jpg",
                                                "image/jpeg");

                                cameraLauncher.launch(cameraUri);

                            } catch (Exception e) {
                                MainActivity.this.status(
                                        "לא ניתן לפתוח מצלמה: "
                                                + e.getMessage());
                            }

                        }

                        return true;
                    }
                });

        menu.show();
    }

    private Uri createGalleryItem(String filename, String mime)
            throws IOException {

        ContentValues values = new ContentValues();

        values.put(MediaStore.Images.Media.DISPLAY_NAME, filename);

        values.put(MediaStore.Images.Media.MIME_TYPE, mime);

        values.put(MediaStore.Images.Media.RELATIVE_PATH,
                Environment.DIRECTORY_PICTURES + "/ImageLab");

        Uri uri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        if (uri == null) {
            throw new IOException("לא ניתן ליצור קובץ בגלריה");
        }

        return uri;
    }

    private void status(String msg) {
        if (tvStatus != null) {
            tvStatus.setText(msg);
        }
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        fsDAO.listenToAllDocumentChange();
    }

    @Override
    protected void onStop() {
        super.onStop();
        fsDAO.stopListening();
    }
}