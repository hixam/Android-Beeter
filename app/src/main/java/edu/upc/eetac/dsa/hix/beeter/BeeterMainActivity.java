package edu.upc.eetac.dsa.hix.beeter;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.ArrayList;

import edu.upc.eetac.dsa.hix.beeter.api.AppException;
import edu.upc.eetac.dsa.hix.beeter.api.BeeterAPI;
import edu.upc.eetac.dsa.hix.beeter.api.Sting;
import edu.upc.eetac.dsa.hix.beeter.api.StingAdapter;
import edu.upc.eetac.dsa.hix.beeter.api.StingCollection;

public class BeeterMainActivity extends ListActivity {
    private final static String TAG = BeeterMainActivity.class.toString();
    private static final String[] items = { "lorem", "ipsum", "dolor", "sit",
            "amet", "consectetuer", "adipiscing", "elit", "morbi", "vel",
            "ligula", "vitae", "arcu", "aliquet", "mollis", "etiam", "vel",
            "erat", "placerat", "ante", "porttitor", "sodales", "pellentesque",
            "augue", "purus" };
    private StingAdapter adapter;

    private ArrayList<Sting> stingsList;

        /** Called when the activity is first created. */

        @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beeter_main);

        stingsList = new ArrayList<Sting>();
        adapter = new StingAdapter(this, stingsList);
        setListAdapter(adapter);

        SharedPreferences prefs = getSharedPreferences("beeter-profile",
                Context.MODE_PRIVATE);
        final String username = prefs.getString("username", null);
        final String password = prefs.getString("password", null);
        Authenticator.setDefault(new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password
                        .toCharArray());
            }
        });
        (new FetchStingsTask()).execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_beeter_main, menu);
        return true;
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Sting sting = stingsList.get(position);
        Log.d(TAG, sting.getLinks().get("self").getTarget());

        Intent intent = new Intent(this, StingDetailActivity.class);
        intent.putExtra("url", sting.getLinks().get("self").getTarget());
        startActivity(intent);
    }




    private void addStings(StingCollection stings){
        stingsList.addAll(stings.getStings());
        adapter.notifyDataSetChanged();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.miWrite:
                Intent intent = new Intent(this, WriteStingActivity.class);
                startActivityForResult(intent, WRITE_ACTIVITY);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private final static int WRITE_ACTIVITY = 0;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case WRITE_ACTIVITY:
                if (resultCode == RESULT_OK) {
                    Bundle res = data.getExtras();
                    String jsonSting = res.getString("json-sting");
                    Sting sting = new Gson().fromJson(jsonSting, Sting.class);
                    stingsList.add(0, sting);
                    adapter.notifyDataSetChanged();
                }
                break;
        }
    }



    private class FetchStingsTask extends
            AsyncTask<Void, Void, StingCollection> {
        private ProgressDialog pd;

        @Override
        protected StingCollection doInBackground(Void... params) {
            StingCollection stings = null;
            try {
                stings = BeeterAPI.getInstance(BeeterMainActivity.this)
                        .getStings();
            } catch (AppException e) {
                e.printStackTrace();
            }
            return stings;
        }

        @Override
        protected void onPostExecute(StingCollection result) {
            ArrayList<Sting> stings = new ArrayList<Sting>(result.getStings());
            for (Sting s : stings) {
                Log.d(TAG, s.getStingid() + "-" + s.getSubject());
            }
            if (pd != null) {
                pd.dismiss();
            }
        }

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(BeeterMainActivity.this);
            pd.setTitle("Searching...");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.show();
        }

    }

}