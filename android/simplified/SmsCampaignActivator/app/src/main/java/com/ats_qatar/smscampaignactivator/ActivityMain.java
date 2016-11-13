package com.ats_qatar.smscampaignactivator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ActivityMain extends AppCompatActivity {

    EditText editTextUserId,editTextExpirationDate,editTextSmsCredit,editTextActivationKey;
    Button buttonGenerate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextUserId = (EditText) findViewById(R.id.editTextUserId);
        editTextExpirationDate = (EditText) findViewById(R.id.editTextExpirationDate);
        editTextSmsCredit = (EditText) findViewById(R.id.editTextSmsCredit);
        editTextActivationKey = (EditText) findViewById(R.id.editTextActivationKey);

        buttonGenerate = (Button) findViewById(R.id.buttonGenerate);

        buttonGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Crypt crypt = Crypt.getInstance();
                    String key = editTextUserId.getText().toString() + "0";
                    String data  = editTextExpirationDate.getText().toString() + "," + editTextSmsCredit.getText().toString();
                    byte[]  bytes = crypt.encrypt(key,data.getBytes());

                    editTextActivationKey.setText(Crypt.toHex(bytes));

                } catch (Exception exception){
                    Toast.makeText(getApplicationContext(), exception.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });


    }
}
