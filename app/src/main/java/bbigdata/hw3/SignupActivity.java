package bbigdata.hw3;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    EditText account;
    TextView password;
    private int SIGNUP_SUCCESS = 4;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mContext = this;

        account  = (EditText) findViewById(R.id.signup_acc);
        password = (EditText) findViewById(R.id.signup_pwd);
        findViewById(R.id.signup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup(account.getText().toString(), password.getText().toString());
            }
        });

    }

    private void signup(String account, String password){

        String url = "http://140.138.77.169/aclin/hw3/signup.php";
        Map<String, String> params = new HashMap<String, String>();
        params.put("account", account);
        params.put("password", password);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            String result = response.getString("msg").toString();
                            if(result.equals("註冊成功")) {
                                setResult(4);
                                finish();

                            }
                            else
                                Toast.makeText(mContext, result, Toast.LENGTH_SHORT);
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

}
