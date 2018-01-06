package com.example.acer.iambored2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class prompt extends AppCompatActivity {

    public static String age, sex;
    private RadioGroup radioSexGroup;
    private RadioGroup radioAgeGroup;

    private RadioButton radioSexButton;
    private RadioButton radioAgeButton;

    private Button btnDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prompt);
        addListenerOnButton();

    }

    public void addListenerOnButton() {

        radioSexGroup = (RadioGroup) findViewById(R.id.radioSex);
        radioAgeGroup = (RadioGroup) findViewById(R.id.radioAge);
        btnDisplay = (Button) findViewById(R.id.btnDisplay);

        btnDisplay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // get selected radio button from radioGroup
                int selectedSexId = radioSexGroup.getCheckedRadioButtonId();
                int selectedAgeId = radioAgeGroup.getCheckedRadioButtonId();

                // find the radiobutton by returned id
                radioSexButton = (RadioButton) findViewById(selectedSexId);
                radioAgeButton = (RadioButton) findViewById(selectedAgeId);

                age=radioAgeButton.getText().toString();
                sex=radioSexButton.getText().toString();

                Button loadNewActivity = (Button) findViewById(R.id.btnDisplay);
                loadNewActivity.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }

        });

    }
}
