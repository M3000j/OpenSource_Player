package com.example.cor.mjplayer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class PastlyricActivity extends AppCompatActivity {

    EditText editText;
    Button _Btn_Save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pastlyric);
        Initilise();
        _Btn_Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MakeLrcActivity.lyric = editText.getText().toString();
                onBackPressed();
            }
        });
    }

    public void Initilise() {
        _Btn_Save = (Button) findViewById(R.id._Btn_Save);
        editText = (EditText) findViewById(R.id._Txt_Lyric);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
