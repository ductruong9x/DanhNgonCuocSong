package com.truongtvd.danhngon;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ProgressBar;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Window;
import com.android.volley.VolleyError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.truongtvd.danhngon.adapter.DetailAdapter;
import com.truongtvd.danhngon.model.ItemNewFeed;
import com.truongtvd.danhngon.network.NetworkOperator;
import com.truongtvd.danhngon.util.JsonUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MainActivity extends SherlockActivity {

    private NetworkOperator operator;
    private ViewPager vpMain;
    private ArrayList<ItemNewFeed> listnew = new ArrayList<ItemNewFeed>();
    private DetailAdapter adapter;
    private Session session;
    private ProgressBar loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        operator = NetworkOperator.getInstance().init(this);
        vpMain = (ViewPager) findViewById(R.id.vpMain);
        session=Session.getActiveSession();
        loading=(ProgressBar)findViewById(R.id.loading);
        getIDUser();
    }

    private void getIDUser() {
        Request request = Request.newMeRequest(session,
                new Request.GraphUserCallback() {

                    @Override
                    public void onCompleted(GraphUser user, Response response) {
                        // TODO Auto-generated method stub
                        try {
                            getUserInfo( user.getId());
                        } catch (Exception e) {

                        }
                    }
                });
        Request.executeBatchAsync(request);
    }

    private void getUserInfo(String id) {
        String fqlQuery = "SELECT name,pic FROM user WHERE uid='" + id + "'";
        Bundle params = new Bundle();
        params.putString("q", fqlQuery);

        Request request = new Request(session, "/fql", params, HttpMethod.GET,
                new Request.Callback() {
                    public void onCompleted(Response response) {
                        JSONObject jso = JsonUtils.parseResponToJson(response);
                        try {
                            JSONArray data = jso.getJSONArray("data");
                            if (data.length() > 0) {
                                JSONObject info = data.getJSONObject(0);

                                MyApplication.setAvater(info.getString("pic"));
                                MyApplication.setName(info.getString("name"));
                                getNewFeed();
                            }

                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }


                    }
                });
        Request.executeBatchAsync(request);

    }


    private void getNewFeed() {
        operator.getNewFeed(200, getSuccess(), getError());
    }

    private com.android.volley.Response.Listener<JSONObject> getSuccess() {
        return new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response, String extraData) {
                loading.setVisibility(View.GONE);
                listnew = JsonUtils.getListItem(response, listnew);
                adapter = new DetailAdapter(MainActivity.this, listnew);
                vpMain.setAdapter(adapter);
            }
        };
    }


    private com.android.volley.Response.ErrorListener getError() {
        return new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error, String extraData) {
                error.printStackTrace();
                loading.setVisibility(View.GONE);
            }
        };
    }
}
