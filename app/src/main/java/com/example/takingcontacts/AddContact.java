package com.example.takingcontacts;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.InetAddresses;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.github.dhaval2404.imagepicker.ImagePicker;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddContact extends MainActivity {

    private EditText name,number,email,companyName;
    private Button saveData,addPhoto;
    private CircleImageView photo;
    private Uri urlOfImage,previousProfilePhoto;
    private String value ="";
    private String previousProfileName ="";
    private String previousProfilePhoneNo="",previousEmail="",previousCompany="";


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        name = findViewById(R.id.profile_name);
        number = findViewById(R.id.profile_contact);
        email = findViewById(R.id.profile_emailId);
        companyName = findViewById(R.id.profile_officeInfo);
        photo = findViewById(R.id.imageView);
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                        ImagePicker.with(AddContact.this)
                                .crop()                    //Crop image(Optional), Check Customization for more option
                                .compress(1024)            //Final image size will be less than 1 MB(Optional)
                                .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                                .start();


            }
        });

        Intent intent = getIntent();
        value = intent.getStringExtra("value");
        if (value.equalsIgnoreCase("1")) {
            Uri uri = Uri.parse("android.resource://your.package.here/drawable/image_name");
            previousProfilePhoto = uri;
        }
        else {
            previousProfilePhoto = Uri.parse(intent.getStringExtra("photo"));
        }
        previousProfileName = intent.getStringExtra("name");
        previousProfilePhoneNo = intent.getStringExtra("contact");
        previousEmail = intent.getStringExtra("email");
        previousCompany = intent.getStringExtra("company");
        if (value.equalsIgnoreCase("-1")){
            photo.setImageURI(previousProfilePhoto);
            name.setText(previousProfileName);
            number.setText(previousProfilePhoneNo);
            email.setText(previousEmail);
            companyName.setText(previousCompany);
        }
        saveData = findViewById(R.id.add_profile_btn);
        saveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if(value.equalsIgnoreCase("-1")) {
                        deleteContact(AddContact.this, previousProfilePhoneNo, previousProfileName);
                    }
                    addContact();
                    Toast.makeText(AddContact.this,"Successfully Saved",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AddContact.this,MainActivity.class);
                    startActivity(intent);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                } catch (OperationApplicationException e) {
                    throw new RuntimeException(e);
                }
            }

            @SuppressLint("Range")
            public boolean deleteContact(Context ctx, String phone, String name) {
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
        });

    }

    private void addContact() throws RemoteException, OperationApplicationException {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CONTACTS}, PackageManager.PERMISSION_GRANTED);
        }

        ArrayList<ContentProviderOperation> contentProviderOperations =
                new ArrayList<ContentProviderOperation>();

        contentProviderOperations.add(ContentProviderOperation.newInsert(
                ContactsContract.RawContacts.CONTENT_URI)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE,null)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_NAME,null)
                .build());

        contentProviderOperations.add(ContentProviderOperation.newInsert(
                ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,0)
                .withValue(ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                        name.getText().toString())
                .build());

        contentProviderOperations.add(ContentProviderOperation.newInsert(
                        ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,0)
                .withValue(ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER,
                        number.getText().toString())
                        .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                                ContactsContract.CommonDataKinds.Phone.TYPE_WORK)
                .build());

        contentProviderOperations.add(ContentProviderOperation.newInsert(
                        ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,0)
                .withValue(ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Email.DATA,
                        email.getText().toString())
                .build());

        contentProviderOperations.add(ContentProviderOperation.newInsert(
                        ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,0)
                .withValue(ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Organization.COMPANY,
                        companyName.getText().toString())
                .build());

        byte imageByte[] = imageUriToBytes();
        if(imageByte != null) {
            contentProviderOperations.add(ContentProviderOperation.newInsert(
                            ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Photo.PHOTO,
                            imageByte)
                    .build());
        }


        try {
            getContentResolver().applyBatch(ContactsContract.AUTHORITY,contentProviderOperations);

        } catch (RemoteException e) {
            throw new RuntimeException(e);
        } catch (OperationApplicationException e) {
            throw new RuntimeException(e);
        }

    }

    private byte[] imageUriToBytes() {
        Bitmap bitmap;
        ByteArrayOutputStream baos = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),urlOfImage);
            baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,50,baos);
            return baos.toByteArray();
        }
        catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        urlOfImage = data.getData();
        photo.setImageURI(urlOfImage);
    }

}

