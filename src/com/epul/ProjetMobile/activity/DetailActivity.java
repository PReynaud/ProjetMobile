package com.epul.ProjetMobile.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import com.epul.ProjetMobile.R;

/**
 * Created by Pierre on 27/12/2015.
 */
public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(mToolbar);

        TextView tName = (TextView) findViewById(R.id.nameDetail);
        TextView tDistance = (TextView) findViewById(R.id.distanceDetail);
        TextView tAdresse = (TextView) findViewById(R.id.adresseDetail);
        TextView tDescription = (TextView) findViewById(R.id.descriptionDetail);


    }
}
