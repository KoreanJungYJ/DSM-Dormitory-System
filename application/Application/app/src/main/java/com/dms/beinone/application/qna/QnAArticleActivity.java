package com.dms.beinone.application.qna;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dms.beinone.application.R;
import com.dms.beinone.application.comment.CommentActivity;
import com.dms.beinone.application.dmsview.DMSButton;
import com.dms.beinone.application.utils.JSONParser;
import com.dms.boxfox.networking.HttpBox;
import com.dms.boxfox.networking.datamodel.Request;
import com.dms.boxfox.networking.datamodel.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by BeINone on 2017-01-23.
 */

public class QnAArticleActivity extends AppCompatActivity {

    private SharedPreferences mAccountPrefs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qna_article);

        // display back button on action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAccountPrefs = getSharedPreferences(getString(R.string.PREFS_ACCOUNT), MODE_PRIVATE);

        final int no = getIntent().getIntExtra(getString(R.string.EXTRA_NO), 0);

        DMSButton commentBtn = (DMSButton) findViewById(R.id.btn_qna_article_comment);
        commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewComment(no);
            }
        });

        new LoadQnATask().execute(no);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        String id = mAccountPrefs.getString(getString(R.string.PREFS_ACCOUNT_ID), "");
        String writer = getIntent().getStringExtra(getString(R.string.EXTRA_WRITER));

        if (id.equals(writer)) {
            // show delete menu if writer's id and the user's id are same
            getMenuInflater().inflate(R.menu.activity_article, menu);
            return true;
        } else {
            return super.onCreateOptionsMenu(menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return true;
    }

    /**
     * sets text of article
     * @param qna QnA object that contains information of article
     */
    private void bind(QnA qna) {
        TextView titleTV = (TextView) findViewById(R.id.tv_qna_article_title);
        TextView writerTV = (TextView) findViewById(R.id.tv_qna_article_writer);
        TextView questionDateTV = (TextView) findViewById(R.id.tv_qna_article_questiondate);
        TextView questionContentTV = (TextView) findViewById(R.id.tv_qna_article_questioncontent);

        titleTV.setText(qna.getTitle());
        writerTV.setText(qna.getWriter());
        questionDateTV.setText(qna.getQuestionDate());
        questionContentTV.setText(qna.getQuestionContent());
    }

    /**
     * start a new activity to display comments
     * @param no index of the article
     */
    private void viewComment(int no) {
        Intent intent = new Intent(this, CommentActivity.class);
        intent.putExtra(getString(R.string.EXTRA_NO), no);
        startActivity(intent);
    }

    private class LoadQnATask extends AsyncTask<Integer, Void, QnA> {

        @Override
        protected QnA doInBackground(Integer... params) {
            QnA qna = null;

            try {
                qna = loadQnA(params[0]);
            } catch (IOException ie) {
                return null;
            } catch (JSONException je) {
                return null;
            }

            return qna;
        }

        @Override
        protected void onPostExecute(QnA qna) {
            super.onPostExecute(qna);

            if (qna == null) {
                Toast.makeText(
                        QnAArticleActivity.this, R.string.qna_article_error, Toast.LENGTH_SHORT)
                        .show();
            } else {
                String id = mAccountPrefs.getString(QnAArticleActivity.this.getString(
                        R.string.PREFS_ACCOUNT_ID), "");

                if (qna.getWriter().equals(id)) {

                }
                bind(qna);
            }
        }

        private QnA loadQnA(int no) throws IOException, JSONException {
            Map<String, String> requestParams = new HashMap<>();
            requestParams.put("no", String.valueOf(no));

            Response response = HttpBox.post(QnAArticleActivity.this, "/post/qna", Request.TYPE_GET)
                    .putBodyData(requestParams)
                    .push();
            JSONObject responseJSONObject = response.getJsonObject();

            return JSONParser.parseQnAJSON(responseJSONObject, no);
        }
    }

}
