package com.example.permission;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import android.Manifest;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int READ_CONTACTS_REQUEST_CODE = 1001;
    private static final int WRITE_CONTACTS_REQUEST_CODE = 1002;
    private static final int CONTACT_LOADER = 1;
    private boolean isASC = true;
    private contactListAdapter contactListAdapter;
    List<ContactItem> contacts;
    ListView lvContacts;
    String newName, newPhone;

    ActivityResultLauncher<Intent> fromNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contacts = new ArrayList<ContactItem>();
        contactListAdapter = new contactListAdapter(this, R.layout.contact_infor, contacts );
        lvContacts = findViewById(R.id.lvPhoneNum);
        lvContacts.setAdapter(contactListAdapter);

        fromNew = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK){
                            Intent intent = result.getData();
                            newName = intent.getExtras().getString("name");
                            newPhone  = intent.getExtras().getString("phone");
                            saveContacts(newName, newPhone);
                            loadContacts();
                        }
                    }
                }
        );

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, READ_CONTACTS_REQUEST_CODE);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CONTACTS}, WRITE_CONTACTS_REQUEST_CODE);
        loadContacts();

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id)
        {
            case R.id.btnNewPhone:
                if (ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                    boolean cap  = false;// de ktra
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CONTACTS}, WRITE_CONTACTS_REQUEST_CODE);
                    cap = true;
                }
                fromNew.launch(new Intent(MainActivity.this, NewPhoneNumber.class));
                contactListAdapter.notifyDataSetChanged();
                break;
            case R.id.btnASC:
                isASC = true;
                loadContacts();
                break;
            case R.id.btnDESC:
                isASC = false;
                loadContacts();
                break;
        }
        return super.onOptionsItemSelected(item);

    }

    public  void onRequestPermissionResult (int requestCode, String permission[], int [] grantResults){
        super.onRequestPermissionsResult(requestCode, permission, grantResults);
        //ktra quyền đọc danh bạ được cấp chưa
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, READ_CONTACTS_REQUEST_CODE);
            //Kiem tr dduocj ghi k
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CONTACTS}, WRITE_CONTACTS_REQUEST_CODE);
            }
            else {
                boolean cap = true;
            }
        }
        else {
            // Quyền đọc đã được cấp, tải danh bạ
            loadContacts();
        }
        return;
    }

    private void loadContacts() {
        LoaderManager.getInstance(this).restartLoader(CONTACT_LOADER, null, this);
    }
    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        if (id == CONTACT_LOADER){
            // lấy các thuộc tính gì từ danh bạ
            String[] SELECTED_FIELDS = new String[]
                    {
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                            ContactsContract.CommonDataKinds.Phone.NUMBER,
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,

                    };
            return new CursorLoader(this, ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    SELECTED_FIELDS,
                    null,
                    null,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME +" "+  (isASC ? "ASC" : "DESC"));
            //(isASC ? "ASC" : "DESC")

        }
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == CONTACT_LOADER){
            List<ContactItem> contacts = new ArrayList<>();
            if (data != null){
                while (!data.isClosed() && data.moveToNext()){
                    String phone = data.getString(1);
                    String name = data.getString(2);
                    contacts.add( new ContactItem(name, phone));
                }
                contactListAdapter.clear();
                contactListAdapter.addAll(contacts);
                contactListAdapter.notifyDataSetChanged();
                data.close();
            }
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        loader = null;
    }

    public  void saveContacts (String name, String phone){


        ArrayList<ContentProviderOperation> cpo = new ArrayList<>();

        //them ten
        cpo.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.RawContacts.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
                .build());

        //them so
        cpo.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.RawContacts.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone)
                .build());

        try {
            ContentProviderResult[] results = getContentResolver().applyBatch(ContactsContract.AUTHORITY, cpo);

        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.d(TAG, "saveContact: " + e.getMessage());
        }
    }
//    public  void onRequestPermissionResult (int requestCode, String permission[], int [] grantResults){
//        super.onRequestPermissionsResult(requestCode, permission, grantResults);
//        //ktra chưa đc cấp quyền thì yêu cầu cấp quyêền
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CONTACTS}, WRITE_CONTACTS_REQUEST_CODE);
//
//        }
//        else {
//            // Quyền đã được cấp, tải danh bạ
//            saveContacts();
//
//        }
//        return;
//    }


}