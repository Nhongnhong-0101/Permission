package com.example.permission;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

public class NewPhoneNumber extends AppCompatActivity {
    private static final int WRITE_CONTACTS_REQUEST_CODE = 1002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_phone_number);

        EditText etName = (EditText) findViewById(R.id.etTen);
        EditText etPhone = (EditText) findViewById(R.id.etSDT);
        Button btnSave = (Button) findViewById(R.id.btnLuu);

        etPhone.setFilters( new InputFilter[] {filter}) ; //có thẻ set nhiều filter

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //hai biến chứa tên với sdt
                Intent intent = new Intent(NewPhoneNumber.this, MainActivity.class);
                Bundle bundle = new Bundle();

                String name = etName.getText().toString();
                String phone = etPhone.getText().toString();

                bundle.putString("name", name);
                bundle.putString("phone", phone);
                intent.putExtras(bundle);


                setResult(RESULT_OK, intent );
                finish(); // Kết thúc Activity và quay trở lại MainActivity

            }
        });
    }
    InputFilter filter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            // Lặp qua từng ký tự trong đầu vào
            for (int i = start; i < end; i++) {
                // Kiểm tra nếu ký tự không phải là số
                if (!Character.isDigit(source.charAt(i))) {
                    // Trả về rỗng để không chấp nhận ký tự không phải số
                    return "";
                }
            }
            // Trả về null để chấp nhận đầu vào là số
            return null;
        }
    };


}