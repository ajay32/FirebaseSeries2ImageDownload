package com.hackingbuzz.firebaseseries2imagedownload;

/**
 * Created by Ajay Mehta on 4/2/2017.
 */


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;


// no user authenicated or..i can say its just a image uploading testing example no user ..saved in authenictation ..so inside rules tabs in storage autentication is null.


// Required Intent and Read External Storage Permission

public class MainActivity extends AppCompatActivity {

    private StorageReference mStorageRef;
    LinearLayout uploadFile;
    ProgressDialog dialog;
    public static int Request_Number = 1;
    Bitmap bitmap;
    private TextView tap;
    private ImageView profile;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setMessage("Uploading Image. Please wait...");

        initObjects();
        initView();


        uploadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //    Log.i("Clicked","Yes");


                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);  // content:// (url) - External_Content_Uri ..location location of images in sd card and Phone Internal Storage (MediaStore -- sd card and phone internal storage)
                startActivityForResult(intent, 1);



            }
        });

    }

    private void initView() {

        uploadFile = (LinearLayout) findViewById(R.id.ll_upload);
        tap = (TextView) findViewById(R.id.tv_tap);
        profile = (ImageView) findViewById(R.id.iv_profile);
    }

    private void initObjects() {

        mStorageRef = FirebaseStorage.getInstance().getReference();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  //
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {

            Uri uri = data.getData();

// Creating a folder named Photos on firebase  n setting our image path to server then with putFile method we putting image (in our path uri) to server
            StorageReference filePath = mStorageRef.child("Photos").child(uri.getLastPathSegment());

            dialog.show(); // showing dialoge at the time of putting image


            // putFIle method work asynchoronously ..its gonna process n put image in background.. n attaching listeners to it..
            filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {  // you can add more than one interface to each other ...where you put semicolon to close means method1(listener).methodw2(listener).method3(listener);
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {  // see if you dont know the name of your image you are uploading...so better get the image with the help of taskSnapshot...it get the details of image you uploading..u simpley get the path of that image..with the help of taskSnapshot..n downlaod it or put it on the image view with the help of picasso or glide..or universal loader..or any fish..
                    dialog.dismiss();
                    tap.setVisibility(View.GONE);

                   Uri downloadUri = taskSnapshot.getDownloadUrl();

                 //   Picasso.with(MainActivity.this).load(downloadUri).into(profile);

                    Picasso.with(MainActivity.this).load(downloadUri).fit().centerCrop().into(profile);  // set image into imageView with fit n center crop..

                  //  Toast.makeText(getApplicationContext(), "Image Uploaded Sucessfully", Toast.LENGTH_SHORT).show();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialog.dismiss();
                    Toast.makeText(MainActivity.this, "Image Uploading Failed", Toast.LENGTH_SHORT).show();

                }
            });


        }
    }
}