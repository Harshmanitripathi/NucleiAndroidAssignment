package com.example.takingcontacts;

//import static androidx.core.view.accessibility.AccessibilityWindowInfoCompat.Api21Impl.getId;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.content.Context;


import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements RecyclerViewInterface{
    private CircleImageView photo;
FloatingActionButton addContactButton;
ArrayList<String> profileName = new ArrayList<String>();
ArrayList<String> profileNumber = new ArrayList<String>();
ArrayList<String> profileEmail = new ArrayList<String>();
ArrayList<String> profileCompany = new ArrayList<String>();
ArrayList<Uri> profilePhoto = new ArrayList<>();
Uri profileImage;
RecyclerView recyclerView;
    String selectPhoto="";

    Bitmap bp ;Uri uri;





//    Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(address));

    // querying contact data store
//    Cursor phones = MainActivity.this.getContentResolver().query(contactUri, null, null, null, null);


    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // declaring listView using findViewById()
//        button = findViewById(R.id.button);
        addContactButton = findViewById(R.id.add_contact);
        photo = findViewById(R.id.person);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                getPhoneContacts();
//
//            }
//        });

        getPhoneContacts();
        recyclerView = findViewById(R.id.recyclerView);
        MyAdaptor myAdaptor = new MyAdaptor(profileNumber,profileName,profilePhoto,this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(myAdaptor);


        addContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,AddContact.class);
                intent.putExtra("name","Name");
                intent.putExtra("contact","Contact");
                intent.putExtra("value","1");
                startActivity(intent);
            }
        });
    }

    @SuppressLint("Range")
    private void getPhoneContacts(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_CONTACTS},0);
        }
        ContentResolver contentResolver = getContentResolver();
        uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        Cursor cursor = contentResolver.query(uri,null,null,null,null);
        Log.i("CONTACT_PROVIDER_DEMO","TOTAL # of Contact :::"+Integer.toString(cursor.getCount()));
        if (cursor.getCount() > 0){
            while (cursor.moveToNext()) {
                selectPhoto = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Photo.PHOTO_URI));
                
                @SuppressLint("Range") String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                @SuppressLint("Range") String contactNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                @SuppressLint("Range") String contactEmail = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
                @SuppressLint("Range") String contactCompany = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA1));

//                Uri person = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI,getContactId(contactNumber,MainActivity.this));
//                Uri u = Uri.withAppendedPath(person, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);


//                if (selectPhoto != null) {
//                    try {
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                            bp = MediaStore.Images.Media.getBitmap(MainActivity.this.getContentResolver(),Uri.parse(selectPhoto));
//                        }
//                    }
//                    catch (Exception e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//                }
                if (selectPhoto != null){
                profilePhoto.add(Uri.parse(selectPhoto));
                }
                else {
                    Uri uri1 = Uri.parse("android.resource://com.example.takingcontacts/drawable/person");
                    profilePhoto.add(uri1);
                }
                profileName.add(contactName);
                profileNumber.add(contactNumber);
                profileEmail.add(contactEmail);
                profileCompany.add(contactCompany);
                Log.i("CONTACT_PROVIDER_DEMO","Contact Name  ::  "+contactName+"    Phonen no :::  "+contactNumber);
            }
        }

    }

//    public static long getContactId(String name, Context context) {
//        long ret = -1;
//        String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME;
//        String[] projection = new String[] { ContactsContract.CommonDataKinds.Phone.CONTACT_ID };
//        Cursor c = context.getContentResolver().query(
//                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
//                projection, selection, null, null);
//        if (c.moveToFirst()) {
//            ret = c.getLong(0);
//        }
//        c.close();
//
//        return ret;
//    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(MainActivity.this,ProfileActivity.class);
        if (selectPhoto != null)
            intent.putExtra("photo", "");
        else
            intent.putExtra("photo", profilePhoto.get(position).toString());

        intent.putExtra("name",profileName.get(position));
        intent.putExtra("phone",profileNumber.get(position));
        intent.putExtra("email",profileEmail.get(position));
        intent.putExtra("company",profileCompany.get(position));
        startActivity(intent);
    }
}
