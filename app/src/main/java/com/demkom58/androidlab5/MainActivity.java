package com.demkom58.androidlab5;

import android.content.res.Resources;
import android.gesture.*;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.demkom58.androidlab1.R;

import java.util.List;

public class MainActivity extends AppCompatActivity implements GestureOverlayView.OnGesturePerformedListener {
    private TextView infoText;
    private EditText inputField;
    private GestureOverlayView gestureOverlayView;

    private int guess;
    private boolean finished;

    private GestureLibrary gestureLibrary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        infoText = findViewById(R.id.infoView);
        inputField = findViewById(R.id.numberField);
        gestureOverlayView = findViewById(R.id.gestureOverlayView1);

        gestureLibrary = GestureLibraries.fromRawResource(this, R.raw.gesture);
        if (!gestureLibrary.load())
            finish();

        gestureOverlayView.addOnGesturePerformedListener(this);
        restartGame();
    }

    public void enter() {
        if (finished) {
            restartGame();
            return;
        }

        final Resources resources = getResources();
        final String numberString = inputField.getText().toString().trim();
        if (numberString.isEmpty()) {
            infoText.setText(resources.getString(R.string.error));
            return;
        }

        final int number;
        try {
            number = Integer.parseInt(numberString);
        } catch (NumberFormatException e) {
            infoText.setText(resources.getString(R.string.error));
            return;
        }

        if (number < 1 || number > 99) {
            infoText.setText(resources.getString(R.string.out_of_range));
            return;
        }

        if (number == guess) {
            infoText.setText(resources.getString(R.string.hit));
            finished = true;
            return;
        }

        infoText.setText(resources.getString(number > guess ? R.string.ahead : R.string.behind));
    }

    private void restartGame() {
        final Resources resources = getResources();
        infoText.setText(resources.getString(R.string.try_to_guess));
        guess = (int) (Math.random() * 100);
        finished = false;
    }

    @Override
    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
        final List<Prediction> predictions = gestureLibrary.recognize(gesture);
        if (predictions.isEmpty())
            return;

        final Prediction prediction = predictions.get(0);
        if (prediction.score > 1.0) {
            if (prediction.name.equals("S")) {
                enter();
                inputField.setText("");
                return;
            }

            inputField.setText(inputField.getText() + prediction.name);
        }
    }
}


