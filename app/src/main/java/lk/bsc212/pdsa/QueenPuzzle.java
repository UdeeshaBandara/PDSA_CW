package lk.bsc212.pdsa;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import lk.bsc212.pdsa.adapter.ChessAdapter;
import lk.bsc212.pdsa.model.room.QueenPlace;
import lk.bsc212.pdsa.model.room.QueenPlaceUser;
import lk.bsc212.pdsa.utils.AlertDialog;
import lk.bsc212.pdsa.utils.Queens;
import lk.bsc212.pdsa.utils.TinyDB;


public class QueenPuzzle extends AppCompatActivity {

    //Data structures
    List<QueenPlace> possiblePlaces = new ArrayList<>();
    ArrayList<String> selectedPlaces = new ArrayList<>(Collections.nCopies(64, "0"));

    //UI elements
    RecyclerView recyclerChessBoard;
    private KProgressHUD hud;
    Button btnFinish;

    ChessAdapter chessAdapter;
    TinyDB tinyDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queen);

        init();


        loadData();


        findViewById(R.id.btn_check).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Collections.frequency(selectedPlaces, "1") != 8)
//                    Toast.makeText(QueenPuzzle.this, "Please select 8 places", Toast.LENGTH_SHORT).show();
                    new AlertDialog().negativeAlert(QueenPuzzle.this, "Sorry!!!", " Cannot select more than 8 places", "Got it");

                else {
                    String selectedPlacesString = selectedPlaces.stream().map(Object::toString)
                            .collect(Collectors.joining(", "));

                    AsyncTask.execute(() -> {
                        boolean isTrueAnswer = false;
                        for (QueenPlace placesArray : possiblePlaces) {

                            if (placesArray.places.equals(selectedPlacesString)) {
                                isTrueAnswer = true;
                                List<QueenPlaceUser> queenPlaceUser = MainApplication.userDao.getAnsweredUser(placesArray.placeId);

                                if (queenPlaceUser.size() > 0) {
//                                    runOnUiThread(() -> Toast.makeText(QueenPuzzle.this, "Answer already provided by " + queenPlaceUser.get(0).user.name, Toast.LENGTH_SHORT).show());
                                    runOnUiThread(() -> new AlertDialog().negativeAlert(QueenPuzzle.this, " Sorry!!!", "Your choice have already submitted by " + queenPlaceUser.get(0).user.name, "OK"));

                                } else {
                                    MainApplication.placeDao.insertAll(new QueenPlace(placesArray.placeId, placesArray.places, tinyDB.getLong("userId", 1)));
//                                    runOnUiThread(() -> Toast.makeText(QueenPuzzle.this, "Correct answer", Toast.LENGTH_SHORT).show());
                                    runOnUiThread(() -> new AlertDialog().positiveAlert(QueenPuzzle.this, "Hurray!!!", "Your choice is a Correct answer….", "OK"));


                                    if (MainApplication.placeDao.checkOtherOptionExist() == 0) {
                                        runOnUiThread(() -> {
//                                            Toast.makeText(QueenPuzzle.this, "Congratulations! Game completed!!", Toast.LENGTH_SHORT).show();
                                            new AlertDialog().positiveAlert(QueenPuzzle.this, "Congratulations", "You have Successfully completed the Game", "Great");
                                            btnFinish.performClick();
                                        });
                                    }
                                }
                                break;
                            }
                        }
                        if (!isTrueAnswer)
//                            runOnUiThread(() -> Toast.makeText(QueenPuzzle.this, "Wrong answer. Try again", Toast.LENGTH_SHORT).show());
                            runOnUiThread(() -> new AlertDialog().negativeAlert(QueenPuzzle.this, "Oops!!!", "Wrong answer, Better try again for your winning choice", "OK"));
                    });
                }
            }
        });
        findViewById(R.id.btn_clear).setOnClickListener(view -> {

            selectedPlaces = new ArrayList<>(Collections.nCopies(64, "0"));
            chessAdapter.updatePlaces(selectedPlaces);

        });
        btnFinish.setOnClickListener(view -> {

            AsyncTask.execute(() -> MainApplication.placeDao.resetGame());
            selectedPlaces = new ArrayList<>(Collections.nCopies(64, "0"));
            chessAdapter.updatePlaces(selectedPlaces);

        });
        findViewById(R.id.btn_switch).setOnClickListener(view -> {

            tinyDB.putBoolean("isNameSelected", false);
            startActivity(new Intent(QueenPuzzle.this, NameActivity.class));
            finishAffinity();

        });

    }

    void init() {

        tinyDB = new TinyDB(QueenPuzzle.this);

        recyclerChessBoard = findViewById(R.id.recycler_chess_board);
        btnFinish = findViewById(R.id.btn_finish);
        hud = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);

        chessAdapter = new ChessAdapter(QueenPuzzle.this, selectedPlaces);
        recyclerChessBoard.setAdapter(chessAdapter);
        recyclerChessBoard.setLayoutManager(new GridLayoutManager(QueenPuzzle.this, 8, GridLayoutManager.VERTICAL, false));
    }


    void loadData() {
        showHUD();

        new Thread(() -> {
            if (MainApplication.placeDao.combinationCount() != 92)
                Queens.enumerate(8).forEach(place -> MainApplication.placeDao.insertAll(new QueenPlace(place)));


            possiblePlaces = MainApplication.placeDao.getQueenPlaces();

            runOnUiThread(() -> {

                hideHUD();

            });


        }).start();


    }


    private void showHUD() {
        if (hud.isShowing()) {
            hud.dismiss();
        }
        hud.show();
    }

    private void hideHUD() {
        if (hud.isShowing()) {
            hud.dismiss();
        }
    }
}