package com.misterc.encodedecode;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class DecryptActivity extends MenuActivity {

    private static final String TAG = "MyLogs";

    private TextView tv_output;
    private TextView tv_encode;
    private EditText et_password;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decrypt);

        tv_output = findViewById(R.id.tv_output);
        tv_encode = findViewById(R.id.tv_encode);
        et_password = findViewById(R.id.et_password);

        Button btn_decode = findViewById(R.id.btn_encode);
        btn_decode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String decMsq = "";
                String password = et_password.getText().toString();

                if (password.isEmpty()) {
                    Toast.makeText(DecryptActivity.this, R.string.decrypt_want_password, Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    decMsq = SimpleCrypto.decrypt(password, tv_output.getText().toString(), getSecretKeyItem());
                    tv_encode.setText(decMsq);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case READ_REQUEST_CODE:
                tv_output.setText(FileUtil.readFile(data));
                break;
            case SAVE_REQUEST_CODE:
                FileUtil.saveFile(data, tv_encode.getText().toString());
                break;
        }
    }
}
