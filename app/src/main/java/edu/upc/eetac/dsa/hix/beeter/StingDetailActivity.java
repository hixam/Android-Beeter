package edu.upc.eetac.dsa.hix.beeter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;

import edu.upc.eetac.dsa.hix.beeter.R;
import edu.upc.eetac.dsa.hix.beeter.api.AppException;
import edu.upc.eetac.dsa.hix.beeter.api.BeeterAPI;
import edu.upc.eetac.dsa.hix.beeter.api.Sting;

import android.util.Log;
import android.widget.TextView;

import java.text.SimpleDateFormat;

public class StingDetailActivity extends Activity {
    private final static String TAG = StingDetailActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sting_detail_layout);
        String urlSting = (String) getIntent().getExtras().get("url");
        (new FetchStingTask()).execute(urlSting);
    }

    private void loadSting(Sting sting) {
        TextView tvDetailSubject = (TextView) findViewById(R.id.tvDetailSubject);
        TextView tvDetailContent = (TextView) findViewById(R.id.tvDetailContent);
        TextView tvDetailUsername = (TextView) findViewById(R.id.tvDetailUsername);
        TextView tvDetailDate = (TextView) findViewById(R.id.tvDetailDate);

        tvDetailSubject.setText(sting.getSubject());
        tvDetailContent.setText(sting.getContent());
        tvDetailUsername.setText(sting.getUsername());
        tvDetailDate.setText(SimpleDateFormat.getInstance().format(
                sting.getLastModified()));
    }
    private class FetchStingTask extends AsyncTask<String, Void, Sting> {
        private ProgressDialog pd;

        @Override
        protected Sting doInBackground(String... params) {
            Sting sting = null;
            try {
                sting = BeeterAPI.getInstance(StingDetailActivity.this)
                        .getSting(params[0]);
            } catch (AppException e) {
                Log.d(TAG, e.getMessage(), e);
            }
            return sting;
        }

        @Override
        protected void onPostExecute(Sting result) {
            loadSting(result);
            if (pd != null) {
                pd.dismiss();
            }
        }

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(StingDetailActivity.this);
            pd.setTitle("Loading...");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.show();
        }

    }



}