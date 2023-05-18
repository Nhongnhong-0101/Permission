package com.example.permission;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class NewPhoneNumber extends AppCompatActivity {

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
                String name = etName.getText().toString();
                String phone = etPhone.getText().toString();


                ContentValues values = new ContentValues();
                values.put(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, name);

                Uri phoneUri = ContactsContract.Data.CONTENT_URI;
                values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, phone);

                getContentResolver().insert(phoneUri, values);
                setResult(RESULT_OK);
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