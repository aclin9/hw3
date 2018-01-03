package bbigdata.hw3;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


class Data {
    String SiteName;
    String Temp;
    String Date;
}

public class FileActivity extends AppCompatActivity {
    //private TextView Res;
    private RequestQueue RQ;
    private ListView LV;
    private Context context;
    private ArrayList<Data> ODResult = new ArrayList<Data>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);
       // Res = findViewById(R.id.res);
        LV = (ListView)findViewById(R.id.listView);
        RQ = Volley.newRequestQueue(this);
        context = this;
        jsonparse();

    }

    private  void showItem()
    {
        Log.d("len",String.valueOf(ODResult.size()));
        MyAdapter myAdapter = new MyAdapter(ODResult,R.layout.list_item_view,context);
        LV.setAdapter(myAdapter);
    }

    private void jsonparse()
    {
        String url = "http://opendata.epa.gov.tw/ws/Data/ATM00698/?$format=json";
        JsonArrayRequest request =new JsonArrayRequest(url,new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    Data tmpD = new Data();
                    tmpD.SiteName = "SiteName";
                    tmpD.Temp = "Temperature";
                    tmpD.Date = "DataCreationDate";
                    ODResult.add(tmpD);
                    for(int i=0;i<response.length();i++)  {
                        tmpD = new Data();
                        JSONObject c = response.getJSONObject(i);
                        String SN = c.getString("SiteName");
                        String Temp = c.getString("Temperature");
                        String Date = c.getString("DataCreationDate");
                        tmpD.SiteName = SN;
                        tmpD.Temp = Temp;
                        tmpD.Date = Date;
                        ODResult.add(tmpD);
                    }
                    showItem();
                } catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RQ.add(request);
    }
}

