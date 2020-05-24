package com.example.puzzlegame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Size;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Date;
import java.util.Random;

public class PuzzleActivity extends AppCompatActivity {
    boolean wasWon;
    static int pieceId = 1; // Not 0
    static int rightPieces = 0;
    static int point = 0;
    int wImg;
    int hImg;
    static int roundIdx;
    static int totalRound;
    static Round round;
    static SavedState savedState;
    DatabaseHandler db;
    RelativeLayout layout;
    ImageView imgHelper;
    ImageButton ibPuzzleBoard;
    ImageButton ibHelp;
    ImageButton ibRefresh;
    Piece[][] pieces;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle);

        initRelativeLayout();
        initRound();
        initImageSize();
        initPieceArray();
        imgHelper = createHelpImageView(round.image);
        imgHelper.setVisibility(View.INVISIBLE);

        ibHelp = findViewById(R.id.ibHelp);
        ibPuzzleBoard = findViewById(R.id.ibPuzzleBoard);
        ibRefresh = findViewById(R.id.ibRefresh);

        // Start with board, so set active button is puzzle board button
        ibPuzzleBoard.setImageResource(R.drawable.puzzle_board_active);
        setClickEventForBoardButton();
        setClickEventForHelpButton();
        setClickEventForRefreshButton();

        setHeightOfAboveLayout();
        setTxtNumRound();
        saveCurState();
        totalRound = db.getTotalRounds();
    }
    
    private void initRound() {
        if (null == db) {
            db = new DatabaseHandler(this);
        }
        
        if (null == round) {
            round = db.getFirstRound();
        }
        else {
            round = db.getRound(round.numRound + 1);
        }
    }

    private void showCongratulationDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setTitle("Congratulation");
        dialog.setContentView(R.layout.congratulation);

        Button btnBack = dialog.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button btnNext = dialog.findViewById(R.id.btnNext);
        if (isLastRound())
            btnNext.setVisibility(View.INVISIBLE);
        else {
            btnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    wasWon = false;
                    setTxtNumRight();
                    imgHelper = createHelpImageView(round.image);
                    imgHelper.setVisibility(View.INVISIBLE);
                    initRound();
                    removeAllPieces();
                    setTxtNumRound();
                    initImageSize();
                    initPieceArray();
                    dialog.dismiss();
                }
            });
        }

        dialog.show();
    }

    private void setClickEventForHelpButton() {
        ibHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ibHelp.setImageResource(R.drawable.help_active);
                ibPuzzleBoard.setImageResource(R.drawable.puzzle_board);
                imgHelper.bringToFront();
                imgHelper = createHelpImageView(round.image);
                imgHelper.setVisibility(View.VISIBLE);
                if (!wasWon) {
                    disableAllPieces();
                }
            }
        });
    }

    private void setClickEventForRefreshButton() {
        ibRefresh.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                rightPieces = savedState.getNumRightPieces();
                point = savedState.getScore();
                setTxtNumRight();
                setTxtNumPoint();
                removeAllPieces();
                initPieceArray();
            }
        });
    }

    private void setClickEventForBoardButton() {
        ibPuzzleBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ibPuzzleBoard.setImageResource(R.drawable.puzzle_board_active);
                ibHelp.setImageResource(R.drawable.help);
                imgHelper.setVisibility(View.GONE);
                if (!wasWon) {
                    enableAllPieces();
                }
            }
        });
    }

    private void setTxtNumRound() {
        TextView txt = findViewById(R.id.txtNumRound);
        txt.setText(Integer.toString(roundIdx++));
    }

    private void setTxtNumRight() {
        TextView txt = findViewById(R.id.txtNumRight);
        txt.setText(Integer.toString(rightPieces));
    }

    private void setTxtNumPoint() {
        TextView txt = findViewById(R.id.txtNumPoint);
        txt.setText(Integer.toString(point));
    }

    private ImageView createHelpImageView(int id) {
        ImageView img = new ImageView(this);
        img.setImageResource(id);
        layout.addView(img);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)img.getLayoutParams();
        params.width = wImg * round.numPiecesInColumn;
        params.height = hImg * round.numPiecesInRow;
        img.setBackgroundColor(Color.BLUE);
        img.setScaleType(ImageView.ScaleType.FIT_XY);
        img.setLayoutParams(params);

        return img;
    }

    private void setHeightOfAboveLayout() {
        // Get size of screen
        Display display = getWindowManager().getDefaultDisplay();
        Point p = new Point();
        display.getSize(p);

        // Set the height of above relative layout is 70 % of the screen
        float percent = 0.7f;
        RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.relativeLayoutAbove);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)relativeLayout.getLayoutParams();
        params.height = (int) (percent * p.y);
        relativeLayout.setLayoutParams(params);
    }

    private void initRelativeLayout() {
        layout = (RelativeLayout)findViewById(R.id.relativeLayoutPuzzle);
    }

    private void initImageSize() {
        Display display = getWindowManager().getDefaultDisplay();
        Point p = new Point();
        display.getSize(p);
        wImg = p.x / round.numPiecesInColumn;
        hImg = p.y / round.numPiecesInRow / 2;
    }

    private void initPieceArray() {
        pieces = new Piece[round.numPiecesInRow][round.numPiecesInColumn];
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), round.image);
        PointF rightLoc = new PointF();
        int wBitmap = bitmap.getWidth() / round.numPiecesInColumn;
        int hBitmap = bitmap.getHeight() / round.numPiecesInRow;
        int x = 0;
        int y = 0;

        for (int i = 0; i < round.numPiecesInRow ; i++) {
            for (int j = 0; j < round.numPiecesInColumn; j++) {
                pieces[i][j] = new Piece(this, rightLoc);
                Bitmap bm = Bitmap.createBitmap(bitmap, x, y, wBitmap, hBitmap);
                initPiece(pieces[i][j], bm);
                rightLoc.x += wImg;
                x += wBitmap;
                setTouchEvent(pieces[i][j]);
            }

            x = 0;
            y += hBitmap;
            rightLoc.x = 0;
            rightLoc.y += hImg;
        }
    }

    private void setTouchEvent(Piece piece) {
        piece.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float touchX = event.getX();
                float touchY = event.getY();
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)v.getLayoutParams();
                Piece piece = (Piece)v;
                v.bringToFront();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //isDown = true;
                        piece.setPreviousLocation(touchX, touchY);

                        // Highlight
                        piece.setPadding(3, 3, 3, 3);
                        piece.setBackgroundColor(Color.GREEN);

                        break;

                    case MotionEvent.ACTION_UP:
                        //isDown = false;
                        putMatchLocation(params, piece);
                        piece.setLayoutParams(params);
                        piece.setCurrentLocation(params.leftMargin, params.topMargin);
                        piece.setPadding(0,0,0,0);
                        countRightPieces();
                        setTxtNumRight();
                        updatePoint(piece);
                        setTxtNumPoint();
                        if (isWin()) {
                            if (isLastRound()) {
                                handlingEndGame();
                            }
                            disableAllPieces();
                            rightPieces = 0;
                            saveCurState();
                            showCongratulationDialog();
                        }
                        break;

                    case MotionEvent.ACTION_MOVE:
                        float distanceX = touchX - piece.getPreviousLocation().x;
                        float distanceY = touchY - piece.getPreviousLocation().y;
                        params.leftMargin += distanceX;
                        params.topMargin += distanceY;
                        constraintView(params);
                        piece.setCurrentLocation(params.leftMargin, params.topMargin);
                        piece.setLayoutParams(params);
                        break;

                    default:
                        return true;
                }

                return true;
            }
        });
    }

    private void constraintView(RelativeLayout.LayoutParams params) {
        if (params.leftMargin < 0) {
            params.leftMargin = 0;
        }

        if (params.topMargin < 0) {
            params.topMargin = 0;
        }

        if (params.leftMargin + wImg > wImg * round.numPiecesInColumn) {
            params.leftMargin = wImg * round.numPiecesInColumn - wImg;
        }

        if (params.topMargin + hImg > hImg * round.numPiecesInColumn) {
            params.topMargin = hImg * round.numPiecesInColumn - hImg;
        }
    }

    private void initPiece(Piece outPiece, Bitmap bitmap) {
        layout.addView(outPiece);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)outPiece.getLayoutParams();
        PointF loc = generateRandomLocation();

        // Set attr for the piece
        outPiece.setId(pieceId++);
        params.width = wImg;
        params.height = hImg;
        params.leftMargin = (int) loc.x;
        params.topMargin = (int) loc.y;
        outPiece.setCurrentLocation(loc.x, loc.y);
        outPiece.setImageBitmap(bitmap);
        outPiece.setScaleType(ImageView.ScaleType.FIT_XY);
        outPiece.setLayoutParams(params);
    }

    private PointF generateRandomLocation() {
        PointF loc = new PointF();
        Random random = new Random();
        float wMin = 0;
        float wMax = wImg * (round.numPiecesInColumn - 1);
        float hMin = 0;
        float hMax = hImg * (round.numPiecesInRow - 1);
        loc.x = random.nextFloat() * (wMax - wMin) + wMin;
        loc.y = random.nextFloat() * (hMax - hMin) + hMin;

        return loc;
    }

    private void putMatchLocation(RelativeLayout.LayoutParams params ,Piece piece) {
        PointF loc = piece.getCurrentLocation();
        PointF centerPoint = new PointF();

        centerPoint.x = loc.x + wImg / 2;
        centerPoint.y = loc.y + hImg / 2;

        params.leftMargin = (int) (centerPoint.x / wImg) * wImg;
        params.topMargin = (int)((centerPoint.y) / hImg) * hImg;
    }

    private void updatePoint(Piece p) {
        if (p.isMatch()) {
            if (!p.getWasMatch()) {
                p.setWasMatch(true);
                point += round.additionPoint;
            }
        }
        else {
            point -= round.minusPoint;
        }
    }

    private boolean isWin() {
        return rightPieces == (round.numPiecesInColumn * round.numPiecesInRow);
    }

    private boolean isLastRound() {
        return roundIdx == totalRound;
    }

    private void handlingEndGame() {
        finish();
    }

    private void countRightPieces() {
        rightPieces = 0;
        for (Piece[] pieceArr : pieces) {
            for (Piece piece : pieceArr) {
                if (piece.isMatch()) {
                    rightPieces++;
                }
            }
        }
    }

    private void enableAllPieces() {
        for (Piece[] pieceArr : pieces) {
            for (Piece piece : pieceArr) {
                piece.setEnabled(true);
            }
        }
    }

    private void disableAllPieces() {
        for (Piece[] pieceArr : pieces) {
            for (Piece piece : pieceArr) {
                piece.setEnabled(false);
            }
        }
    }

    private void removeAllPieces() {
        for (Piece[] pieceArr : pieces) {
            for (Piece piece : pieceArr) {
                layout.removeView(piece);
            }
        }

    }

    private void saveCurState() {
        savedState = new SavedState(rightPieces, point);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean("wasWon", wasWon);
        outState.putInt("rightPieces", rightPieces);
        outState.putInt("point", point);
    }
}
