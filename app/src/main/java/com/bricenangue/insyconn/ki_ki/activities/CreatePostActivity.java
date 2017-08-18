package com.bricenangue.insyconn.ki_ki.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bricenangue.insyconn.ki_ki.Models.Post;
import com.bricenangue.insyconn.ki_ki.Models.UserPublic;
import com.bricenangue.insyconn.ki_ki.MyImagePicker;
import com.bricenangue.insyconn.ki_ki.R;
import com.bricenangue.insyconn.ki_ki.UserSharedPreference;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.mvc.imagepicker.ImagePicker;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class CreatePostActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imageViewPostPic;
    private ImageButton imageBtnPicker;
    private EditText editTextPostText;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference root;
    private UserSharedPreference userSharedPreference;
    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS=2301;
    Bitmap bitmap;
    private Uri selectedImage;
    private boolean isChoosen=false;
    private Uri pictureUri;
    String text ="";
    String creator_name= "";
    String creator_pic= "";
    private ProgressDialog progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        userSharedPreference=new UserSharedPreference(this);
        auth=FirebaseAuth.getInstance();
        if (auth!= null){
            user=auth.getCurrentUser();
        }
        root= FirebaseDatabase.getInstance().getReference().child("Posts");
        editTextPostText = (EditText) findViewById(R.id.editText_description_post);
        imageBtnPicker = (ImageButton) findViewById(R.id.create_post_imageBtnPickker);
        imageViewPostPic = (ImageView) findViewById(R.id.create_post_imageView);
        imageBtnPicker.setOnClickListener(this);

        DatabaseReference reference =  FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(user.getUid())
                .child("userPublic");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               creator_name = dataSnapshot.getValue(UserPublic.class).getName();
                creator_pic =dataSnapshot.getValue(UserPublic.class).getProfilePhotoUri();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public void ButtonCreatePostClicked(View view) {
        progressBar = new ProgressDialog(this);
        progressBar.setCancelable(false);
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.show();
        //upload content to firebase

        final String key= root.push().getKey();

        StorageReference referencePost = FirebaseStorage.getInstance().getReference()
                .child("Posts")
                .child(key).
                        child("post_picture");

        if (pictureUri!=null){
            referencePost.putFile(pictureUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String text = editTextPostText.getText().toString();
                    String creator_id = user.getUid();
                    long time= System.currentTimeMillis();

                    final Uri downloadUri=taskSnapshot.getDownloadUrl();

                   // imageViewPostPic.setImageURI(downloadUri);
                    Picasso.with(getApplicationContext()).load(downloadUri).networkPolicy(NetworkPolicy.OFFLINE)
                            .fit().centerInside()
                            .into(imageViewPostPic, new Callback() {
                                @Override
                                public void onSuccess() {
                                    if (progressBar!=null){
                                        progressBar.dismiss();
                                    }

                                }

                                @Override
                                public void onError() {
                                    Picasso.with(getApplicationContext()).load(downloadUri)
                                            .fit().centerInside().into(imageViewPostPic);
                                    if (progressBar!=null){
                                        progressBar.dismiss();
                                    }


                                }
                            });

                   String post_picture =downloadUri.toString();
                    Post post= new Post(text, post_picture,time,creator_id,creator_name,creator_pic,key);

                    DatabaseReference reference = root.child(key);
                    if (!text.isEmpty() || !post_picture.isEmpty() ){
                        reference.setValue(post).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                finish();
                                if (progressBar!=null){
                                    progressBar.dismiss();
                                }

                            }
                        });

                    }else {
                        editTextPostText.requestFocus();
                        if (progressBar!=null){
                            progressBar.dismiss();
                        }

                    }
                }
            });
        }else{
            String text = editTextPostText.getText().toString();
            String creator_id = user.getUid();
            long time= System.currentTimeMillis();


            Post post= new Post(text, null,time,creator_id,creator_name,creator_pic,key);

            DatabaseReference reference = root.child(key);
            if (!text.isEmpty()){
                reference.setValue(post).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        finish();
                    }
                });

            }else {
                editTextPostText.requestFocus();
            }
        }



    }

    @Override
    public void onClick(View view) {
        //load image Picker
        if(checkAndRequestPermissions()){

            if (bitmap!=null){
                bitmap.recycle();
                bitmap=null;
            }
            MyImagePicker.pickImage(this,getString(R.string.imagepicker_title));


        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode==RESULT_OK){

            if (requestCode == MyImagePicker.PICK_IMAGE_REQUEST_CODE){
                isChoosen=true;

                bitmap=MyImagePicker.getImageFromResult(getApplicationContext(),requestCode,resultCode,data);
                selectedImage =getImageUri(bitmap);


                imageViewPostPic.setImageURI(null);

                imageViewPostPic.setImageBitmap(null);
/*
                if (userPublic.getProfilePhotoUri()!=null){
                    Picasso.with(getApplicationContext()).invalidate(userPublic.getProfilePhotoUri());

                }
                */
                loadImage(selectedImage.toString());

                pictureUri=selectedImage;
                imageViewPostPic.setImageURI(pictureUri);

            }

        }


    }

    private void loadImage(final String uri){
        Picasso.with(getApplicationContext()).load(uri).networkPolicy(NetworkPolicy.OFFLINE)
                .fit().centerInside()
                .into(imageViewPostPic, new Callback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onError() {
                        Picasso.with(getApplicationContext()).load(uri)
                                .fit().centerInside().into(imageViewPostPic);

                    }
                });
    }
    public Uri getImageUri(Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        String path = MediaStore.Images.Media.insertImage(getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private  boolean checkAndRequestPermissions() {
        int camera = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);
        int storage = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int loc = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        List<String> listPermissionsNeeded = new ArrayList<>();

        if (camera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.CAMERA);
        }
        if (storage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (loc != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty())
        {
            ActivityCompat.requestPermissions(this,listPermissionsNeeded.toArray
                    (new String[listPermissionsNeeded.size()]),REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {

            case REQUEST_ID_MULTIPLE_PERMISSIONS :{
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    ImagePicker.pickImage(this,getString(R.string.imagepicker_title));

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(CreatePostActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;

            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

}
