package com.example.favfood.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.example.favfood.MainActivity;
import com.example.favfood.Models.DatabaseUser;
import com.example.favfood.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class EditProfile extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{
    private static final String TAG = "Store";
    EditText Name, email, address, dob, phone;
    MaterialButton update;
    ImageView profileImage, imageChangeIcon, back;
    TextView name1, phone1;
    FirebaseUser user;
    DatabaseReference databaseReference;
    String uid, username, imageUrl = "";
    String emailId="", phoneId="",AddressId="",dobId="";
    List<String> userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        //EditText
        Name = findViewById(R.id.Name);
        email = findViewById(R.id.email4);
        phone = findViewById(R.id.phone2);
        address = findViewById(R.id.Address);
        dob = findViewById(R.id.Dob);
        TextInputLayout textInputLayout = findViewById(R.id.textInputLayout6);
        //Image
        profileImage = findViewById(R.id.profileImage);
        imageChangeIcon = findViewById(R.id.editProfileIcon);
        back = findViewById(R.id.imageView3);
        //Button
        update = findViewById(R.id.update);
        //TextView
        name1 = findViewById(R.id.userName);
        phone1 = findViewById(R.id.userEmail);
        //Dob picker
        textInputLayout.setEndIconOnClickListener(v -> new DatePicker().show(getSupportFragmentManager(),"Date Pick"));
        //Firebase
        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        databaseReference.keepSynced(true);
        uid = user.getUid();
        userData = new ArrayList<>();
        //Retrieve the data from firebase.
        RetrieveData();
        //Check if user is anonymous or not.
        if (user != null) {
            if (!user.isAnonymous()) Provider();
            else Anonymous();
        }
        back.setOnClickListener(v -> onBackPressed());
        imageChangeIcon.setOnClickListener(v -> openDialog());
        update.setOnClickListener(v -> UpdateDetails());
    }
    @Override
    public void onDateSet(android.widget.DatePicker  view, int year, int month, int dayOfMonth) {
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.set(Calendar.YEAR, year);
        mCalendar.set(Calendar.MONTH, month);
        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String selectedDate = DateFormat.getDateInstance(DateFormat.SHORT).format(mCalendar.getTime());
        dob.setText(selectedDate);
    }
    public void Anonymous() {
        SharedPreferences preferences = getSharedPreferences("Features",MODE_PRIVATE);
        String guestName = preferences.getString("Guest","No name");
        name1.setText(guestName);
        phone1.setText("");
    }

    public void Provider() {
        Name.setText(user.getDisplayName());
        SetText(name1, user.getDisplayName());
        username = user.getDisplayName();
        email.setText(user.getEmail());
        if (user.getEmail() != null) {
            phone1.setText(user.getEmail());
            emailId  = user.getEmail();
        } else {
            phone1.setText(user.getPhoneNumber());
            phoneId = user.getPhoneNumber();
        }
        phone.setText(user.getPhoneNumber());

    }

    public void SetText(TextView editText, String value) {
        if (value == null) {
            editText.setText(R.string.not_available);
        } else {
            editText.setText(value);
        }
    }

    public void UpdateDetails() {

        username = Name.getText().toString();
        emailId = email.getText().toString();
        phoneId = phone.getText().toString();
        AddressId = address.getText().toString();
        dobId = dob.getText().toString();

        if (username.isEmpty()) {
            Name.setError("UseName is empty");
            Name.requestFocus();
            return;
        }
        if (emailId.isEmpty()) {
            email.setError("Email is empty");
            email.requestFocus();
            return;
        }
        if (phoneId.isEmpty()) {
            phone.setError("Phone no is empty");
            phone.requestFocus();
            return;
        }
        if (AddressId.isEmpty()) {
            address.setError("Address is empty");
            address.requestFocus();
        }
        StoreDatabase(username, emailId, phoneId, AddressId, dobId);
    }

    public void StoreDatabase(String userName, String EmailId, String phoneNo, String Address, String dateOfBirth) {
        String imageId = imageUrl;
        DatabaseUser databaseUser = new DatabaseUser(userName, EmailId, phoneNo, imageId, Address, dateOfBirth);
        databaseReference.child(uid).setValue(databaseUser);
        Toast.makeText(this, "Details Updated", Toast.LENGTH_SHORT).show();
    }
    public void RetrieveData() {
        databaseReference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    userData.add(Objects.requireNonNull(postSnapshot.getValue()).toString());
//                    Log.w(TAG, "Value retrieved" + postSnapshot.getValue().toString());
                }
                if (userData.size() == 6) {
                    address.setText(userData.get(0));AddressId = userData.get(0);
                    dob.setText(userData.get(1));dobId = userData.get(1);
                    email.setText(userData.get(2));emailId = userData.get(2);
                    Name.setText(userData.get(4));username = userData.get(4);
                    phone.setText(userData.get(5));phoneId = userData.get(5);

                    if (!userData.get(3).equals("")) {
                        imageUrl = userData.get(3);
                        Picasso.get().load(userData.get(3)).networkPolicy(NetworkPolicy.OFFLINE).into(profileImage);
                    } else if (!Objects.requireNonNull(user.getPhotoUrl()).toString().equals("")) {
                        Picasso.get().load(user.getPhotoUrl().toString()).into(profileImage);
                    } else {
                        Picasso.get().load(R.drawable.dog).into(profileImage);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }
    private void openDialog() {
        try {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                final CharSequence[] options = {"Take Photo", "Choose From Gallery", "Cancel"};
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Select Option");
                builder.setItems(options, (dialog, item) -> {
                    if (options[item].equals("Take Photo")) {
                        dialog.cancel();
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        cameraIntentResultLauncher.launch(intent);
//                        startActivityForResult(intent, PICK_IMAGE_CAMERA);
                    } else if (options[item].equals("Choose From Gallery")) {
                        dialog.cancel();
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        galleryIntentResultLauncher.launch(pickPhoto);
//                        startActivityForResult(pickPhoto, PICK_IMAGE_GALLERY);
                    } else if (options[item].equals("Cancel")) {
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            } else {
                Toast.makeText(this, "Camera Permission error", Toast.LENGTH_SHORT).show();
                requestPermissions(new String[]{Manifest.permission.CAMERA}, 101);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Camera Permission error", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                openDialog();
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }
    ActivityResultLauncher<Intent> cameraIntentResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                try {
                    Bitmap bitmap = (Bitmap) Objects.requireNonNull(data).getExtras().get("data");
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes);
                    profileImage.setImageBitmap(bitmap);
                    byte[] data1 = bytes.toByteArray();
                    storeInFirebase(data1);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    });
    ActivityResultLauncher<Intent> galleryIntentResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {

            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                Uri mediaUri = data != null ? data.getData() : null;
                try {
                    InputStream inputStream = getBaseContext().getContentResolver().openInputStream(mediaUri);
                    Bitmap bm = BitmapFactory.decodeStream(inputStream);
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    bm.compress(Bitmap.CompressFormat.JPEG, 50, bytes);
                    profileImage.setImageBitmap(bm);
                    byte[] data1 = bytes.toByteArray();
                    storeInFirebase(data1);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    });
    public void storeInFirebase(byte[] data) {
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        String filename = "IMG" + System.currentTimeMillis() + ".jpeg";
        StorageReference imageRef  = mStorageRef.child("pictures/" + filename);
        UploadTask uploadTask = imageRef.putBytes(data);
        uploadTask.addOnFailureListener(exception -> Toast.makeText(this, "Error in uploading this file", Toast.LENGTH_SHORT).show())
                .addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    imageUrl = uri.toString();
                    Log.e("Link",imageUrl);
                    StoreDatabase(username,emailId,phoneId, AddressId,dobId);
                }));
        if(!new MainActivity().isConnected(getApplicationContext())){
            Toast.makeText(this, "Image will be uploaded when Internet is Available...", Toast.LENGTH_SHORT).show();
        }
    }
    public static class DatePicker extends DialogFragment {
        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            Calendar mCalendar = Calendar.getInstance();
            int year = mCalendar.get(Calendar.YEAR);
            int month = mCalendar.get(Calendar.MONTH);
            int dayOfMonth = mCalendar.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(), (DatePickerDialog.OnDateSetListener) getActivity(), year, month, dayOfMonth);
        }
    }
}