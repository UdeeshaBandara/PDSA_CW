package lk.bsc212.pdsa;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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
    Button btnFinish, btnClear;

    ChessAdapter chessAdapter;
    TinyDB tinyDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queen);

        init();


        loadData();


        findViewById(R.id.btn_check).setOnClickListener(view -> {

            if (Collections.frequency(selectedPlaces, "1") != 8)
                new AlertDialog().negativeAlert(QueenPuzzle.this,
                        "Oops!!!", "Select 8 tiles as per your choice to check your luck of winning",
                        "Got it");

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
                                runOnUiThread(() -> {
                                    new AlertDialog().negativeAlert(QueenPuzzle.this, " Sorry!!!",
                                            "Your choice have already submitted by " + queenPlaceUser.get(0).user.name, "OK");
                                    btnClear.performClick();
                                });

                            } else {
                                MainApplication.queenPlaceDao.insertAll(new QueenPlace(placesArray.placeId, placesArray.places, tinyDB.getLong("userId", 1)));
                                runOnUiThread(() -> {
                                    new AlertDialog().positiveAlert(QueenPuzzle.this,
                                            "Hurray!!!", "Your choice is a Correct answer!!", "OK");
                                    btnClear.performClick();
                                });


                                if (MainApplication.queenPlaceDao.checkOtherOptionExist() == 0) {
                                    runOnUiThread(() -> {
                                        new AlertDialog().positiveAlert(QueenPuzzle.this,
                                                "Congratulations",
                                                "You have Successfully completed the Game", "Great");
                                        btnFinish.performClick();
                                    });
                                }
                            }
                            break;
                        }
                    }
                    if (!isTrueAnswer)
                        runOnUiThread(() -> new AlertDialog().negativeAlert(QueenPuzzle.this, "Oops!!!", "Wrong answer, Better try again for your winning choice", "OK"));
                });
            }
        });
        btnClear.setOnClickListener(view -> {

            selectedPlaces = new ArrayList<>(Collections.nCopies(64, "0"));
            chessAdapter.updatePlaces(selectedPlaces);

        });
        btnFinish.setOnClickListener(view -> {

            AsyncTask.execute(() -> MainApplication.queenPlaceDao.resetGame());
            selectedPlaces = new ArrayList<>(Collections.nCopies(64, "0"));
            chessAdapter.updatePlaces(selectedPlaces);
            new AlertDialog().positiveAlert(QueenPuzzle.this, "Information!!!", "All game records have been reset", "OK");

        });


    }

    void init() {

        tinyDB = new TinyDB(QueenPuzzle.this);

        recyclerChessBoard = findViewById(R.id.recycler_chess_board);
        btnClear = findViewById(R.id.btn_clear);
        btnFinish = findViewById(R.id.btn_finish);
        hud = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);

        chessAdapter = new ChessAdapter(QueenPuzzle.this, selectedPlaces);
        recyclerChessBoard.setAdapter(chessAdapter);
        recyclerChessBoard.setLayoutManager(new GridLayoutManager(QueenPuzzle.this, 8, GridLayoutManager.VERTICAL, false));

        new AlertDialog().positiveAlert(QueenPuzzle.this, "The Instructions!!!", "The object is to figure out how to place eight queens on a chess board such that none of them can attack each other. \n" +
                "In chess, a queen can attack another piece if it is on the same row, column, or diagonal. To solve this puzzle, therefore, the eight queens must be placed on the board so that no two of them are on the same row, column, or diagonal.\n" +
                "\n" +
                "Start your lucky choices by clicking on a square for the first queen. The queen will show up on the board.\n" +
                "\n" +
                " Continue placing queens until you win the game by perfectly placing eight choices of queens.\n" +
                "\n" +
                "Wish you all the best to win the game CHAMP !!!!!", "Got it");


    }


    void loadData() {
        showHUD();

        new Thread(() -> {
            if (MainApplication.queenPlaceDao.combinationCount() != 92)
                Queens.enumerate(8).forEach(place -> MainApplication.queenPlaceDao.insertAll(new QueenPlace(place)));


            possiblePlaces = MainApplication.queenPlaceDao.getQueenPlaces();

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