package com.example.takingcontacts;

import static java.nio.file.Files.delete;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.ref.PhantomReference;
import java.net.URI;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    Button delete,update;
    TextView name,contact,email,company;
    private CircleImageView photo;
    Uri profilePhoto;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        photo = findViewById(R.id.imageView);
        name = findViewById(R.id.profile_name);
        contact = findViewById(R.id.profile_contact);
        email = findViewById(R.id.profile_emailId);
        company = findViewById(R.id.profile_officeInfo);
        update = findViewById(R.id.btn_update);
        delete = findViewById(R.id.btn_delete);
        Intent intent = getIntent();
        if(intent.getStringExtra("photo") != null) {
            profilePhoto = Uri.parse(intent.getStringExtra("photo"));
        }
//        Bitmap profilePhoto = (Bitmap) BitmapFactory.decodeStream(intent.getParcelableExtra("photo"));
        String profileName = intent.getStringExtra("name");
        String profileContact = intent.getStringExtra("phone");
        String profileEmail = intent.getStringExtra("email");
        String profileCompanyName = intent.getStringExtra("company");
        if (intent.getStringExtra("photo") != null) {
            photo.setImageURI(profilePhoto);
        }
        name.setText(profileName);
        contact.setText(profileContact);
        email.setText(profileEmail);
        company.setText(profileCompanyName);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this,AddContact.class);
                intent.putExtra("photo",profilePhoto.toString());
                intent.putExtra("name",profileName);
                intent.putExtra("contact",profileContact);
                intent.putExtra("email",profileEmail);
                intent.putExtra("company",profileCompanyName);
                intent.putExtra("value","-1");
                startActivity(intent);
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteContact(ProfileActivity.this, contact.getText().toString(), name.getText().toString());
                Toast.makeText(ProfileActivity.this, "Successfully Deleted", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ProfileActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }

    @SuppressLint("Range")
    public static boolean deleteContact(Context ctx, String phone, String name) {
        Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phone));
        Cursor cur = ctx.getContentResolver().query(contactUri, null, null, null, null);
        try {
            if (cur.moveToFirst()) {
                do {
                    if (cur.getString(cur.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME)).equalsIgnoreCase(name)) {
                        String lookupKey = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
                        Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
                        ctx.getContentResolver().delete(uri, null, null);
                        return true;
                    }

                } while (cur.moveToNext());
            }

        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        } finally {
            cur.close();
        }
        return false;
    }
}
