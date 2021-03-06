package com.example.sportsclub.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.sportsclub.Constants;
import com.example.sportsclub.R;

import org.json.JSONException;
import org.json.JSONObject;

public class SignInActivity extends AppCompatActivity {
    public static final String TAG = SignInActivity.class.getSimpleName();
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String EMAIL = "email";
    public static final String USERNAME = "username";
    public static final String PHONE = "phone";
    public static final String ADDRESS = "address";
    public static final String TOKEN = "token";
    private Button btnSignIn;
    private EditText edtEmail, edtPassword;
    private String email, password, token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        btnSignIn = findViewById(R.id.btn_sign_in);
        edtEmail = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edt_password);

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        token = sharedPreferences.getString(TOKEN, "");

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = edtEmail.getText().toString().trim();
                password = edtPassword.getText().toString().trim();

                boolean isEmpty = false;

                if (email.isEmpty()) {
                    isEmpty = true;
                    edtEmail.setError("Email harus diisi");
                }

                if (password.isEmpty()) {
                    isEmpty = true;
                    edtPassword.setError("Password harus diisi");
                }

                if (!isEmpty) {
                    AndroidNetworking.post(Constants.BASE_URL + "/api/login")
                            .addBodyParameter("email", email)
                            .addBodyParameter("password", password)
                            .addBodyParameter("token", token)
                            .setPriority(Priority.MEDIUM)
                            .build()
                            .getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    // do anything with response
                                    try {
                                        String status = response.getString("status");
                                        String message = response.getString("message");
                                        if (status.equals("success")) {
                                            JSONObject data = response.getJSONObject("data");
                                            Log.d("RBA", "resultData: " + data.toString());
                                            String name = data.getString("name");
                                            String id = data.getString("id");
                                            String email = data.getString("email");
                                            String username = data.getString("username");
                                            String phoneNumber = data.getString("phone");
                                            String address = data.getString("address");

                                            SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedPreferences.edit();

                                            editor.putString(ID, id);
                                            editor.putString(NAME, name);
                                            editor.putString(EMAIL, email);
                                            editor.putString(USERNAME, username);
                                            editor.putString(PHONE, phoneNumber);
                                            editor.putString(ADDRESS, address);
                                            editor.apply();

                                            Intent intent = new Intent(SignInActivity.this, MainDashboardActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);

                                            Toast.makeText(SignInActivity.this, "Selamat datang " + name, Toast.LENGTH_SHORT).show();
                                            finish();
                                        } else {
                                            Toast.makeText(SignInActivity.this, message, Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onError(ANError error) {
                                    // handle error
                                    Log.d("RBA", "onError: " + error.getErrorBody());
                                    Log.d("RBA", "onError: " + error.getLocalizedMessage());
                                    Log.d("RBA", "onError: " + error.getErrorDetail());
                                    Log.d("RBA", "onError: " + error.getResponse());
                                    Log.d("RBA", "onError: " + error.getErrorCode());
                                }
                            });
                }
            }
        });
    }
}
