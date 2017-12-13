package com.example.daniel.budgetbuddy;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;
import android.widget.EditText;
import android.widget.Button;

import java.util.HashMap;
import java.util.Map;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class SignupActivity extends AppCompatActivity {

    EditText txtFullName, txtPassword, txtEmail;
    Button btnSignup;

    // Creating Volley RequestQueue.
    RequestQueue requestQueue;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    // Create string variable to hold the EditText Value.
    String fname, lname, email, password;

    // Creating Progress dialog.
    ProgressDialog progressDialog;

    // Storing server url into String variable.
    String IP_ADDRESS = "192.168.0.38";
    String HttpUrl = "http://" + IP_ADDRESS + "/ADVANDB/insert_user.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // init Views
        txtFullName = (EditText) findViewById(R.id.txtFullName);
        txtEmail = (EditText) findViewById(R.id.txtEmail);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        btnSignup = (Button) findViewById(R.id.buttonSignup);

        // Creating Volley newRequestQueue .
        requestQueue = Volley.newRequestQueue(SignupActivity.this);

        progressDialog = new ProgressDialog(SignupActivity.this);

        // Adding click listener to button.
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Showing progress dialog at user registration time.
                progressDialog.setMessage("Registering Account...");
                progressDialog.show();

                // Calling method to get value from EditText.
                GetValueFromEditText();

                // Creating string request with post method.
                StringRequest stringRequest = new StringRequest(Request.Method.POST, HttpUrl,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String ServerResponse) {

                                // Hiding the progress dialog after all task complete.
                                progressDialog.dismiss();

                                // Showing response message coming from server.
                                //Toast.makeText(SignupActivity.this, ServerResponse, Toast.LENGTH_LONG).show();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {

                                // Hiding the progress dialog after all task complete.
                                progressDialog.dismiss();

                                // Showing error message if something goes wrong.
                                Toast.makeText(SignupActivity.this, volleyError.toString(), Toast.LENGTH_LONG).show();
                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() {

                        // Creating Map String Params.
                        Map<String, String> params = new HashMap<String, String>();

                        // Adding All values to Params.
                        params.put("first_name", fname);
                        params.put("last_name", lname);
                        params.put("email", email);
                        params.put("password", password);

                        return params;
                    }

                };

                // Creating RequestQueue.
                RequestQueue requestQueue = Volley.newRequestQueue(SignupActivity.this);

                // Adding the StringRequest object into requestQueue.
                requestQueue.add(stringRequest);


                //shared preferences

                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                editor = sharedPreferences.edit();

                editor.putString("user_fname", fname);
                editor.putString("user_lname", lname);
                editor.putString("user_email", email);
                editor.putString("user_password", password);
                editor.commit();

                Intent profileIntent = new Intent(SignupActivity.this, ProfileActivity.class);
                profileIntent.putExtra("current_user", fname);
                startActivity(profileIntent);
                finish();
            }
        });

    }

    // Creating method to get value from EditText.
    public void GetValueFromEditText() {

        String[] fullName = txtFullName.getText().toString().split(" ", 2);
        fname = fullName[0]; //first name
        lname = fullName[1]; //last name (can be more than one name, e.g. Dela Cruz)
        email = txtEmail.getText().toString();
        password = txtPassword.getText().toString();

    }
}