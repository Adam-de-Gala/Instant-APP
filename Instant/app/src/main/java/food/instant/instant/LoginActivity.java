package food.instant.instant;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewDebug;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.math.BigInteger;
import java.security.interfaces.RSAKey;

import static food.instant.instant.EncryptionHelper.EncryptMessage_Big_Integer;
import static food.instant.instant.EncryptionHelper.intToString;
import static food.instant.instant.EncryptionHelper.stringToInt;
import static food.instant.instant.HttpRequests.HttpGET;
import static food.instant.instant.HttpRequests.HttpPost;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private static Context c;

    private EditText etUsername;
    private EditText etPassword;
    private static String username;
    private String password;
    private LoginHandler handler;

    private Button bLogin;

    private ImageButton ibClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        c = this;

        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        bLogin = findViewById(R.id.ibLogin);
        ibClose = findViewById(R.id.ibClose);

        ibClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Login button was hit!");

                username = etUsername.getText() + "";
                password = etPassword.getText() + "";

                if(username.length() == 0 && password.length() != 0)
                {
                    Toast.makeText(c, "Please enter a username.", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(username.length() != 0 && password.length() == 0)
                {
                    Toast.makeText(c, "Please enter a password.", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(username.length() == 0 && password.length() == 0)
                {
                    Toast.makeText(c, "Please enter a username and password.", Toast.LENGTH_SHORT).show();
                    return;
                }
                else
                {
                    OrderDbHelper dbHelper = new OrderDbHelper(c);
                    Cursor c  = dbHelper.getRSAInfo(dbHelper.getReadableDatabase());
                    c.moveToFirst();
                    String RSA_KEY = c.getString(c.getColumnIndex(KeyContract.KeyEntry.SESSION_KEY_VALUE));
                    String Exponent = c.getString(c.getColumnIndex(KeyContract.KeyEntry.ENCRYPTION_EXPONENT));
                    int version = c.getInt(c.getColumnIndex(KeyContract.KeyEntry.VERSION));
                    System.out.println(password);
                    String integerRep = stringToInt(password);
                    System.out.println("Integer Rep Unencrypted: "+integerRep);
                    System.out.println("String Rep"+ intToString(integerRep));
                    BigInteger encrypted = EncryptMessage_Big_Integer(new BigInteger(integerRep),new BigInteger(RSA_KEY),new BigInteger(Exponent));
                    System.out.println("Integer Rep Encrypted: "+ encrypted);
                    handler = new LoginHandler(LoginActivity.this, password);
                    String path = "verifyLogin?VersionNumber="+version+"&User_Email="+username+"&User_Password_Encrypted="+encrypted;
                    //String path = "getPassword?User_Email=" + username;
                    System.out.println(path);
                    HttpGET(path, handler);
                }
                //VersionNumber, User_Email, User_Password_Encrypted
            }
        });

        TextView forgotPassword = findViewById(R.id.tvForgotPassword);
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(c, ForgotPassword.class);
                startActivity(intent);
            }
        });

        TextView signUp = findViewById(R.id.tvSignUp);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(c, SignUp.class);
                startActivity(intent);
            }
        });
    }

    private class LoginHandler extends Handler {
        /***************************************************************************************
         *    Title: Stack Overflow Answer to Question about static handlers
         *    Author: Tomasz Niedabylski
         *    Date: July 10, 2012
         *    Availability: https://stackoverflow.com/questions/11407943/this-handler-class-should-be-static-or-leaks-might-occur-incominghandler
         ***************************************************************************************/
        private final WeakReference<LoginActivity> loginActivity;
        private String password;
        public LoginHandler(LoginActivity loginActivity, String pass) {
            this.loginActivity = new WeakReference<LoginActivity>(loginActivity);
            this.password = pass;
        }
        /*** End Code***/
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == GlobalConstants.PASSWORD) {
                LoginActivity login = loginActivity.get();
                if (login != null) {
                    JSONObject response = null;
                    String gottenPass = "";
                    //version number wrong
                    //Get the stupid json and store the password in the String
                    try {
                        response = (JSONObject) msg.obj;
                        gottenPass = (String) response.get("Login_Success");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //check if login credentials were correct
                    if (gottenPass.equals("True")) {

                        //postAESKEY?VersionNumber=&EncryptedCode=&User_ID=
                        HttpGET("getAllUserInfo?User_Email=" + username, login.handler);
                    }
                    else if(gottenPass.equals("Wrong_Password")) {
                        Toast.makeText(c, "Invalid username/password", Toast.LENGTH_SHORT).show();
                        etUsername.setText("");
                        etPassword.setText("");
                    }
                    else{
                        System.out.println(gottenPass);
                    }
                    //Log.d(TAG, response.toString());
                }
            }
            else if(msg.what == GlobalConstants.USERINFO)
            {
                LoginActivity login = loginActivity.get();
                if (login != null) {
                    JSONArray response = null;
                    String username = "";
                    String firstname = "";
                    String lastname = "";
                    String birthday = "";
                    String address = "";
                    String id = "";
                    String type = "";

                    try {
                        response = ((JSONObject) msg.obj).getJSONArray("All_User_Info");
                        username = (String) ((JSONObject) response.get(0)).get("User_Email");
                        firstname = (String) ((JSONObject) response.get(0)).get("First_Name");
                        lastname = (String) ((JSONObject) response.get(0)).get("Last_Name");
                        birthday = (String) ((JSONObject) response.get(0)).get("User_Birthdate");
                        address = (String) ((JSONObject) response.get(0)).get("User_Address");
                        id = "" + ((JSONObject) response.get(0)).get("User_ID");
                        type = (String) ((JSONObject) response.get(0)).get("User_Type");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    OrderDbHelper helper = new OrderDbHelper(c);
                    Cursor cursor = helper.getAESInfo(helper.getReadableDatabase());
                    cursor.moveToFirst();
                    String AESKey = cursor.getString(cursor.getColumnIndex(KeyContract.KeyEntry.AES_KEY));
                    System.out.println("unencrypted"+AESKey.toString());
                    int version = cursor.getInt(cursor.getColumnIndex(KeyContract.KeyEntry.VERSION));
                    cursor = helper.getRSAInfo(helper.getReadableDatabase());
                    cursor.moveToFirst();
                    String RSAKEY = cursor.getString(cursor.getColumnIndex(KeyContract.KeyEntry.SESSION_KEY_VALUE));
                    String exponent = cursor.getString(cursor.getColumnIndex(KeyContract.KeyEntry.ENCRYPTION_EXPONENT));

                    BigInteger encryptedKey =  EncryptMessage_Big_Integer(new BigInteger(AESKey),new BigInteger(RSAKEY),new BigInteger(exponent));
                    System.out.println("encrypted "+encryptedKey.toString());
                    HttpPost("postAESKEY?VersionNumber="+version+"&EncryptedCode="+encryptedKey.toString()+"&User_ID="+id,login.handler);

                    //context, username, firstname, lastname, birthday, address, id, type
                    SaveSharedPreference.login(c, username, firstname, lastname, birthday, address, id, type);
                    Log.d(TAG, "////////   Logged in!   //////");
                    Toast.makeText(c, "Logged in!", Toast.LENGTH_SHORT).show();
                    LoginActivity.this.finish();
                    return;
                }
            }
        }
    }

}
