package com.example.daniel.budgetbuddy;

/**
 * Created by Daniel on 04/12/2017.
 */

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.net.URL;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    EditText txtEmail, txtPassword;
    Button btnLogin;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    // Creating Progress dialog.
    ProgressDialog progressDialog;

    // Storing server url into String variable.
    String IP_ADDRESS = "192.168.0.35";  //pocket wifi
    String HttpUrl = "http://" + IP_ADDRESS + "/Android/BudgetBuddy/get_users.php";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // init Views
        txtEmail = (EditText) findViewById(R.id.txtEmail);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        btnLogin = (Button) findViewById(R.id.buttonLogin);

        progressDialog = new ProgressDialog(LoginActivity.this);

        //getJSON(HttpUrl);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email, password;

                email = txtEmail.getText().toString().trim();
                password = txtPassword.getText().toString().trim();

                getJSON(HttpUrl, email, password);
            }
        });
    }

    //this method is actually fetching the json string
    private void getJSON(final String urlWebService, final String email, final String password) {
        /*
        * As fetching the json string is a network operation
        * And we cannot perform a network operation in main thread
        * so we need an AsyncTask
        * The constrains defined here are
        * Void -> We are not passing anything
        * Void -> Nothing at progress update as well
        * String -> After completion it should return a string and it will be the json string
        * */
        class GetJSON extends AsyncTask<Void, Void, String> {

            //this method will be called before execution
            //you can display a progress bar or something
            //so that user can understand that he should wait
            //as network operation may take some time
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog.show();
            }

            //this method will be called after execution
            //so here we are displaying a toast with the json string
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                // Hiding the progress dialog after all task complete.
                progressDialog.dismiss();


                if(isValidUser(s, email, password)) {
                    /* TO-DO: put this in a function */
                    String user_fname = "";
                    String user_lname = "";
                    try {
                        //creating a json array from the json string
                        JSONArray jsonArray = new JSONArray(s);

                        for(int i = 0; i < jsonArray.length(); i++) {
                            //getting json object from the json array
                            JSONObject obj = jsonArray.getJSONObject(i);

                            if(email.equals(obj.getString("email"))) {
                                user_fname = obj.getString("first_name");
                                user_lname = obj.getString("last_name");
                            }
                        }

                        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        editor = sharedPreferences.edit();

                        editor.putString("user_fname", user_fname);
                        editor.putString("user_lname", user_lname);
                        editor.putString("user_email", email);
                        editor.putString("user_password", password);
                        editor.commit();

                    } catch(JSONException e) {
                        e.printStackTrace();
                    }

                    Intent profileIntent = new Intent(LoginActivity.this, ProfileActivity.class);
                    profileIntent.putExtra("current_user", user_fname);
                    startActivity(profileIntent);
                    finish();

//                    String welcome = "Welcome, " + user_name;
//                    Toast.makeText(LoginActivity.this, welcome, Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(LoginActivity.this, "Wrong email or password", Toast.LENGTH_SHORT).show();
                }
                //Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
            }

            //in this method we are fetching the json string
            @Override
            protected String doInBackground(Void... voids) {

                try {
                    //creating a URL
                    URL url = new URL(urlWebService);

                    //Opening the URL using HttpURLConnection
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();

                    //StringBuilder object to read the string from the service
                    StringBuilder sb = new StringBuilder();

                    //We will use a buffered reader to read the string from service
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    //A simple string to read values from each line
                    String json;

                    //reading until we don't find null
                    while ((json = bufferedReader.readLine()) != null) {

                        //appending it to string builder
                        sb.append(json + "\n");
                    }

                    //finally returning the read string
                    return sb.toString().trim();
                } catch (Exception e) {
                    return null;
                }

            }
        }

        //creating asynctask object and executing it
        GetJSON getJSON = new GetJSON();
        getJSON.execute();
    }

    public boolean isValidUser(String json, String email, String password) {
        try {
            //creating a json array from the json string
            JSONArray jsonArray = new JSONArray(json);

            //looping through all the elements in json array
            for (int i = 0; i < jsonArray.length(); i++) {
                //getting json object from the json array
                JSONObject obj = jsonArray.getJSONObject(i);

                if(email.equals(obj.getString("email")) && password.equals(obj.getString("password")))
                    return true;
            }

        } catch(JSONException e) {
            e.printStackTrace();
            Toast.makeText(LoginActivity.this, "An error occurred logging you in", Toast.LENGTH_SHORT).show();
        }

        return false;
    }
}
