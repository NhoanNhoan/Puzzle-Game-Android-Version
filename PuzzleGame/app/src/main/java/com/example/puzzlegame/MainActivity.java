package com.example.puzzlegame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnNewGame = findViewById(R.id.btnNewGame);
        btnNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PuzzleActivity.class);
                startActivity(intent);
            }
        });

        Button btnExit = findViewById(R.id.btnExit);
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initDatabase();
    }

    private void initDatabase() {
        DatabaseHandler db = new DatabaseHandler(this);
        int numRound = 1;
        int numRow = 3;
        int numCol = 3;
        int additionPoint = 3;
        int minusPoint = 1;

        if (0 == db.getTotalRounds()) {
            Round round = new Round(numRound, numRow, numCol, additionPoint, minusPoint, R.drawable.meganfox);
            Round round2 = new Round(numRound, numRow + 1, numCol + 1, additionPoint + 1, minusPoint + 2, R.drawable.img2);
            Round round3 = new Round(numRound, numRow + 1, numCol + 1, additionPoint + 1, minusPoint + 2, R.drawable.img3);
            db.insertRound(round);
            db.insertRound(round2);
            db.insertRound(round3);
        }
    }
}
