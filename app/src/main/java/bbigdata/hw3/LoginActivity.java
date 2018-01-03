package bbigdata.hw3;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener{

    private static final int RESULT_GOOGLE_SIGNIN = 1;
    private static final int RESULT_FACEBOOK_LOGIN = 2;
    private static final int RESULT_MEMBER_LOGIN = 3;
    private static final int RESULT_MEMBER_SIGNUP = 4;
    private static final int SIGNUP_SUCCESS = 5;

    private Activity mActivity = LoginActivity.this;

    private CallbackManager callbackManager;
    private LoginManager loginManager;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = this;
        findViewById(R.id.google_signin_button).setOnClickListener(this);
        findViewById(R.id.signup).setOnClickListener(this);
        findViewById(R.id.facebook_login_button).setOnClickListener(this);
        findViewById(R.id.login).setOnClickListener(this);

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        loginManager = LoginManager.getInstance();
        if(Profile.getCurrentProfile() != null)
            loginManager.logOut();

    }

    @Override
    public void onClick(View v){
        switch(v.getId()){

            case R.id.google_signin_button:
                googleSignin();
                break;
            case R.id.facebook_login_button:
                facebookLogin();
                break;
            case R.id.signup:
                signup();
                break;
            case R.id.login:
                memberLogin();
                break;
        }
    }

    private void memberLogin()
    {
        String url = "http://140.138.77.169/aclin/hw3/login.php";
        Map<String, String> params = new HashMap<String, String>();
        EditText account  = findViewById(R.id.account);
        EditText password = findViewById(R.id.password);
        String accS = account.getText().toString();
        String pwdS = password.getText().toString();

        params.put("account", accS);
        params.put("password", pwdS);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            String result = response.getString("msg").toString();
                            if(result.equals("登入成功")) {
                                Intent intent = new Intent(mActivity , MainActivity.class);
                                startActivity(intent);
                            }
                            else
                            {

                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                                alertDialogBuilder.setTitle("登入錯誤");
                                alertDialogBuilder.setMessage("帳號或密碼錯誤，請重新輸入");
                                alertDialogBuilder.setCancelable(true);
                                alertDialogBuilder.setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //Intent intent = new Intent(mActivity, MainActivity.class);
                                        //startActivity(intent);
                                    }
                                }).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) { Log.e("error", error.getMessage()); }
                });
        requestQueue.add(jsonObjectRequest);
    }

    private void googleSignin(){

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();

        // Build a GoogleSignInClient with the options specified by googleSignInOptions.
        GoogleApiClient mGoogleSignInClient = new GoogleApiClient.Builder(this.getApplicationContext()).enableAutoManage(LoginActivity.this, this) .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions).build();

        Intent googleSigninIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleSignInClient);
        startActivityForResult(googleSigninIntent, RESULT_GOOGLE_SIGNIN);

    }

    private void facebookLogin(){
        loginManager.setLoginBehavior(LoginBehavior.NATIVE_WITH_FALLBACK);
        List<String> permissions = new ArrayList<>();
        permissions.add("public_profile");
        permissions.add("email");

        loginManager.logInWithReadPermissions(this, permissions);
        loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>(){

            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken accessToken = loginResult.getAccessToken();
                Profile profile = Profile.getCurrentProfile();
                GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {

                        Intent intent = new Intent(mActivity, MainActivity.class);
                        startActivity(intent);

                    }
                });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender, birthday");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {}

            @Override
            public void onError(FacebookException error) {}
        });
        }

    private void signup(){

        Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
        startActivityForResult(intent, RESULT_MEMBER_SIGNUP);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){

        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RESULT_GOOGLE_SIGNIN){
            GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(googleSignInResult.isSuccess()){
                GoogleSignInAccount googleSignInAccount = googleSignInResult.getSignInAccount();

                Intent intent = new Intent(mActivity, MainActivity.class);
                startActivity(intent);

                //Toast.makeText(this, googleSignInAccount.getDisplayName(), Toast.LENGTH_SHORT);
            }
            else
                Log.e("fail", "google sign in fail");
        }
        else if(requestCode == RESULT_FACEBOOK_LOGIN){
        }
        else if(requestCode == RESULT_MEMBER_LOGIN){
            Intent intent = new Intent(mActivity, MainActivity.class);
            startActivity(intent);
        }
        else if(requestCode == RESULT_MEMBER_SIGNUP){

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("登入");
            alertDialogBuilder.setMessage("帳號建立成功。\n是否直接使用該帳號登入？");
            alertDialogBuilder.setCancelable(true);
            alertDialogBuilder.setPositiveButton("登入", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(mActivity, MainActivity.class);
                    startActivity(intent);
                }
            })
            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            })
            .show();

        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

}
