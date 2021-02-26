package com.yes_u_du.zuyger.rules_and_policy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.yes_u_du.zuyger.BaseActivity;
import com.yes_u_du.zuyger.R;

public class InformationListActivity extends BaseActivity {
    private TextView rulesText;
    private TextView resourcesText;
    private TextView licenseText;
    private  String informationText;

    @Override
    public Fragment getFragment() {
        return null;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.information_activity);
        rulesText = findViewById(R.id.rule_and_policy);
        rulesText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                informationText = getResources().getString(R.string.text_rule_policy);
                Intent intent = InformationActivity.newIntent(getBaseContext(),informationText);
                startActivity(intent);
            }
        });
        resourcesText = findViewById(R.id.rules_of_resources);
        resourcesText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                informationText = getResources().getString(R.string.rules_resources_text);
                String resourceText = getResources().getString(R.string.resources_text_part);
                Intent intent = InformationActivity.newIntent(getBaseContext(),informationText+resourceText);
                startActivity(intent);
            }
        });
        licenseText = findViewById(R.id.license);
        licenseText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                informationText = getResources().getString(R.string.licence_text);
                Intent intent = InformationActivity.newIntent(getBaseContext(),informationText);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        status(getResources().getString(R.string.label_online));
    }

    @Override
    protected void onPause() {
        super.onPause();
        status(getResources().getString(R.string.label_offline));
    }
}
