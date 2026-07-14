package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private Button easyLevelButton;
    private Button mediumLevelButton;
    private Button hardLevelButton;
    private Button extremeLevelButton;
    private Button tenRangeButton;
    private Button twentyRangeButton;
    private Button fiftyRangeButton;
    private Button hundredRangeButton;
    private Button resetButton;
    private Button clearScreenButton;
    private Button guessButton;
    private TextView rangeTextView;
    private TextView randomNumberTextView;
    private TextView easyTextField;
    private TextView mediumTextField;
    private TextView hardTextField;
    private TextView extremeHardTextField;
    private TextView errorMessagesTextField;
    private TextView numberOfTriesTextField;
    private TextView feedbackTextField;
    private TextView gameStatusMessageTextField;
    private ImageView gameResultImageView;

    private EditText getGuess;
    private Button[] levelButtons;
    private Button[] rangeButtons;
    private TextView[] labels;
    private TextView difficultySelected;
    private int[] winnerImages = new int[]{R.drawable.trophy,R.drawable.winner2};
    private int[] loserImages = new int[]{R.drawable.loser,R.drawable.loser2};
    private int[] rangeLevels = new int[]{MAX_RANGE_TEN,MAX_RANGE_TWENTY,MAX_RANGE_FIFTY,MAX_RANGE_ONE_HUNDRED};
    private int[] difficultyLevels = new int[]{EASY_GAME_MODE,MEDIUM_GAME_MODE,HARD_GAME_MODE,EXTREME_HARD_GAME_MODE};
    private int[] buttonDifficultyLevels = new int[]{BUTTON_ONE,BUTTON_TWO,BUTTON_THREE,BUTTON_FOUR};

    private static final Random RAND_GENERATOR = new Random();
    private static final int MAX_RANGE_TEN = 10;
    private static final int MAX_RANGE_TWENTY = 20;
    private static final int MAX_RANGE_FIFTY = 50;
    private static final int MAX_RANGE_ONE_HUNDRED = 100;
    private static final int EASY_GAME_MODE = 15;
    private static final int MEDIUM_GAME_MODE = 10;
    private static final int HARD_GAME_MODE = 5;
    private static final int EXTREME_HARD_GAME_MODE = 3;
    private static final int BUTTON_ONE = 1;
    private static final int BUTTON_TWO = 2;
    private static final int BUTTON_THREE = 3;
    private static final int BUTTON_FOUR = 4;

    private int buttonId;
    private int guessCount = 0;
    private int level;
    private int turns;
    private int randomNumber;
    private int guessMin = 1;
    private int selectedRange = 0;
    private Runnable clearLastImage;
    private Runnable clearFeedBackTask;
    private Runnable clearRandomNumberTask;
    private Runnable clearErrorTask;
    private Runnable clearNumberOfTriesRunnable;
    private Runnable endGameMessage;
    private GameStatus gameStatus = GameStatus.GAME_IN_PROGRESS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        if(gameStatus == GameStatus.GAME_IN_PROGRESS){
            gameResultImageView.setVisibility(View.INVISIBLE);
            guessButtonActionListener();
            initializeDifficultyButtonListeners();
            initializeRangeButtonListeners();
            resetButtonActionListener();
            clearButtonActionListener();
            guessButton.setEnabled(false);
            clearScreenButton.setEnabled(false);
            resetButton.setEnabled(false);
        }
    }
    private void initializeDifficultyButtonListeners(){
        for(int i = 0; i < levelButtons.length; i++){
            difficultyGameActionListener(levelButtons[i]);
        }
    }
    private void initializeRangeButtonListeners(){
       for(int i = 0; i < rangeButtons.length; i++){
           rangeButtonActionListener(rangeButtons[i]);
       }
    }
    private void rangeButtonActionListener(Button button){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processRangeButtonClicked(v);
            }
        });
    }

    private void clearButtonActionListener(){
        clearScreenButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                clearButton();
            }
        });
    }

    private void resetButtonActionListener(){
        resetButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                resetButton();
            }
        });
    }

    private void guessButtonActionListener(){
        guessButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startGame(v);
            }
        });
    }

    private void clearButton(){
        getGuess.setText("");
    }

    private void resetButton(){
        easyLevelButton.setEnabled(true);
        mediumLevelButton.setEnabled(true);
        hardLevelButton.setEnabled(true);
        extremeLevelButton.setEnabled(true);
        tenRangeButton.setEnabled(true);
        twentyRangeButton.setEnabled(true);
        fiftyRangeButton.setEnabled(true);
        hundredRangeButton.setEnabled(true);
        turns = 0;
        guessCount = 0;
        guessButton.setEnabled(false);
        clearScreenButton.setEnabled(false);
        resetButton.setEnabled(false);
        difficultySelected.setText("");
        rangeTextView.setText("");
        getGuess.setText("");
    }
    private void startGame(View v){
        if(!getErrorChecks()){
            String guessText = getGuess.getText().toString();
            int guess = Integer.parseInt(guessText);
            checkGuess(guess);
            upDateUi(guess);
        }
    }
    private Integer tryParseGuess(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void checkGuess(int guess){
        Map<Integer,Boolean>checkGameStatus = new LinkedHashMap<>();
        checkGameStatus.put(R.string.msg_you_guessed_it,hasNumberBeenGuessed(guess));
        checkGameStatus.put(R.string.msg_guess_is_too_low,isNumberGuessedTooLow(guess));
        checkGameStatus.put(R.string.msg_guess_is_too_high,isNumberGuessedTooHigh(guess));
        for(Map.Entry<Integer,Boolean>entry:checkGameStatus.entrySet()){
            if(entry.getValue()){
                String message = getString(entry.getKey());
                setGameMessage(feedbackTextField,message);
                clearFeedBackTask = showTemporaryMessage(feedbackTextField,3000,clearFeedBackTask);
                break;
            }
        }
    }

    private boolean hasNumberBeenGuessed(int guess){
        return guess == randomNumber;
    }

    private boolean isNumberGuessedTooHigh(int guess){
        return guess > randomNumber;
    }

    private boolean isNumberGuessedTooLow(int guess){
        return guess < randomNumber;
    }

    private boolean getErrorChecks(){
        String guessText = getGuess.getText().toString();
        Map<Integer,Boolean> errorMessages = new LinkedHashMap<>();
        errorMessages.put(R.string.error_empty_input,isInputEmpty());
        errorMessages.put(R.string.error_guess_out_of_range,isGuessOutOfRange(guessText));
        for(Map.Entry<Integer,Boolean>entry:errorMessages.entrySet()){
            if(entry.getValue()){
                String message = getString(entry.getKey());
                setGameMessage(errorMessagesTextField,message);
                clearErrorTask = showTemporaryMessage(errorMessagesTextField,3000,clearErrorTask);
                return true;
            }
        }
        return false;
    }

    private Runnable showTemporaryMessage(TextView textView,int duration, Runnable runnable){
        textView.removeCallbacks(runnable);
        Runnable newTask = new Runnable() {
            @Override
            public void run() {
                textView.setText("");
            }
        };
        textView.postDelayed(newTask,duration);
        return newTask;
    }

    private void showTemporaryImage(ImageView imageView){
        imageView.removeCallbacks(clearLastImage);
        clearLastImage = new Runnable(){
            public void run(){
                imageView.setVisibility(View.INVISIBLE);
            }
        };
        imageView.postDelayed(clearLastImage,5000);
    }

    private boolean isInputEmpty(){
        String guessText = getGuess.getText().toString();
        return guessText.trim().isEmpty();
    }

    private boolean isGuessOutOfRange(String guessText){
        Integer guess = tryParseGuess(guessText);
        return guess == null || guess < guessMin || guess > selectedRange;
    }
    private void difficultyGameActionListener(Button button){
        button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                turns = setGameDifficulty(v);
                turns = level;
                gameStatus = GameStatus.GAME_IN_PROGRESS;
            }
        });
    }
    private void processRangeButtonClicked(View v){
        for(int i = 0; i < rangeButtons.length; i++){
            if(v.getId() == rangeButtons[i].getId()){
                setRange(rangeLevels[i]);
            }
        }
    }
    private void setRange(final int RANGE_VALUE){
        selectedRange = RANGE_VALUE;
        displayRange(rangeTextView,RANGE_VALUE);
        displayRandomNumber();
        lockInGuessingRange();
    }

    private int setGameDifficulty(View v){
        for(int i = 0; i < levelButtons.length; i++){
            if(v.getId() == levelButtons[i].getId()){
                buttonId = buttonDifficultyLevels[i];
            }
        }
        turns = processButtonClicked();
        lockInNumberOfGuesses();
        return buttonId;
    }

    private int processButtonClicked(){
        for(int i = 0; i < buttonDifficultyLevels.length; i++){
            if(buttonId == buttonDifficultyLevels[i]) {
                level = difficultyLevels[i];
                difficultySelected = labels[i];
                setNumberOfGuesses(difficultySelected,difficultyLevels[i]);
            }
        }
        return buttonId;
    }
    private void lockInNumberOfGuesses(){
        easyLevelButton.setEnabled(false);
        mediumLevelButton.setEnabled(false);
        hardLevelButton.setEnabled(false);
        extremeLevelButton.setEnabled(false);
    }

    private void lockInGuessingRange(){
        tenRangeButton.setEnabled(false);
        twentyRangeButton.setEnabled(false);
        fiftyRangeButton.setEnabled(false);
        hundredRangeButton.setEnabled(false);
        guessButton.setEnabled(true);
        clearScreenButton.setEnabled(true);
        resetButton.setEnabled(true);
    }

    private void setNumberOfGuesses(TextView guessingRange,int guessNumberRange){
        guessingRange.setText(" " + guessNumberRange);
    }

    private void displayRandomNumber(){
        randomNumber = getRandomNumber(selectedRange);
    }

    private int getRandomNumber(int selectedRange){
        return RAND_GENERATOR.nextInt(selectedRange)+1;
    }

    private void displayRange(TextView randomNumber, int selectedRange){
        randomNumber.setText(" " + selectedRange);
    }

    private TextView setGameMessage(TextView view,String message){
        view.setText(message);
        return view;
    }
    private int decideWhichImageToUse(int[]arr){
        if(arr.length == 0){
            return R.drawable.fallbackimage;
        }else{
            return getImage(arr);
        }
    }

    private int getImage(int[]arr){
        int random = RAND_GENERATOR.nextInt(arr.length);
        int selectedImage = arr[random];
        return selectedImage;
    }

    private void displayEndGameImage(int imageId){
        gameResultImageView.setImageResource(imageId);
        gameResultImageView.setVisibility(View.VISIBLE);
    }

    private void upDateUi(int guess){
        if(hasNumberBeenGuessed(guess)){
            endGameResults();
        }else{
            guessCount++;
            upDateGame();
        }
    }
    private void endGameResults(){
        gameStatus = GameStatus.GAME_OVER;
        revealRandomNumber();
        displayOutcomeVisuals();
        resetButton();
        displayRandomNumber();
    }

    private void revealRandomNumber(){
        randomNumberTextView.setVisibility(View.VISIBLE);
        randomNumberTextView.setText(String.valueOf(randomNumber));
        howManyGuesses();
        clearNumberOfTriesRunnable  = showTemporaryMessage(numberOfTriesTextField,4000,clearNumberOfTriesRunnable);
        clearRandomNumberTask = showTemporaryMessage(randomNumberTextView,4000,clearRandomNumberTask);
    }

    private void displayOutcomeVisuals(){
        String youWinMessage = getString(R.string.msg_you_win);
        printEndGameMessage(youWinMessage);
        int winnerImage = decideWhichImageToUse(winnerImages);
        displayEndGameImage(winnerImage);
        showTemporaryImage(gameResultImageView);
        endGameMessage = showTemporaryMessage(gameStatusMessageTextField,5000,endGameMessage);
    }

    private void howManyGuesses(){
        String message = getString(R.string.msg_number_of_guesses,guessCount);
        numberOfTriesTextField.setText(message);
    }

    private void upDateGame(){
        level--;
        difficultySelected.setText(String.valueOf(level));
        checkTurns();
    }

    private void printEndGameMessage(String message){
        gameStatusMessageTextField.setText(message);
    }

    private void checkTurns(){
        if(level == 0){
            gameStatus = GameStatus.GAME_OVER;
            String gameOverMessage = getString(R.string.msg_game_over);
            printEndGameMessage(gameOverMessage);
            int loserImage = decideWhichImageToUse(loserImages);
            displayEndGameImage(loserImage);
            showTemporaryImage(gameResultImageView);
            resetButton();
            randomNumber = getRandomNumber(selectedRange);
            endGameMessage = showTemporaryMessage(gameStatusMessageTextField,5000,endGameMessage);
        }
    }

    private void findViews(){
        easyLevelButton = findViewById(R.id.easy_button);
        mediumLevelButton = findViewById(R.id.medium_button);
        hardLevelButton = findViewById(R.id.hard_button);
        extremeLevelButton = findViewById(R.id.extreme_hard_button);

        tenRangeButton = findViewById(R.id.ten_range_button);
        twentyRangeButton = findViewById(R.id.twenty_range_button);
        fiftyRangeButton = findViewById(R.id.fifty_range_button);
        hundredRangeButton = findViewById(R.id.hundred_range_button);

        gameStatusMessageTextField = findViewById(R.id.game_message_status);

        easyTextField = findViewById(R.id.easy_text_field);
        mediumTextField = findViewById(R.id.medium_text_field);
        hardTextField = findViewById(R.id.hard_text_field);
        extremeHardTextField = findViewById(R.id.extreme_text_field);

        errorMessagesTextField = findViewById(R.id.error_messages);
        feedbackTextField = findViewById(R.id.feed_back);
        numberOfTriesTextField = findViewById(R.id.number_of_guesses);

        rangeTextView = findViewById(R.id.range_number);
        randomNumberTextView = findViewById(R.id.random_number_text);

        guessButton = findViewById(R.id.guess_button);
        resetButton = findViewById(R.id.reset_button);
        clearScreenButton = findViewById(R.id.clear_button);

        getGuess = findViewById(R.id.edit_text_guess);

        gameResultImageView = findViewById(R.id.image_game_result_view);

        levelButtons = initArray(easyLevelButton,mediumLevelButton,hardLevelButton,extremeLevelButton);
        labels = initArray(easyTextField,mediumTextField,hardTextField,extremeHardTextField);
        rangeButtons = initArray(tenRangeButton,twentyRangeButton,fiftyRangeButton,hundredRangeButton);

    }
    private Button[] initArray(Button...items){
        return items;
    }

    private TextView[] initArray(TextView...items){
        return items;
    }






























































































































































































































    // example code for reflection and generics


    // levelButtons = createArray(Button.class, easyLevelButton, mediumLevelButton, hardLevelButton, extremeLevelButton);

    //labels = createArray(TextView.class, easyTextField, mediumTextField, hardTextField, extremeHardTextField);

    //rangeButtons = createArray(Button.class, tenRangeButton, twentyRangeButton, fiftyRangeButton, hundredRangeButton);

        /* initButtonArray();
        initLabelArray();
        initRangeButtonArray();*/

   /* @SuppressWarnings("unchecked")
    private  <T> T[]createArray(Class<T>type, T... items){
        T[] array = (T[]) Array.newInstance(type, items.length);
        System.arraycopy(items, 0, array, 0, items.length);
        return array;
    }*/

    /*private void initButtonArray(){
        levelButtons = new Button[]{
                easyLevelButton,
                mediumLevelButton,
                hardLevelButton,
                extremeLevelButton};
    }

    private void initLabelArray(){
        labels = new TextView[]{
                easyTextField,
                mediumTextField,
                hardTextField,
                extremeHardTextField};
    }

    private void initRangeButtonArray(){
        rangeButtons = new Button[]{
                tenRangeButton,
                twentyRangeButton,
                fiftyRangeButton,
                hundredRangeButton};
    }*/


    /*private <T> List<T> createList(T...items){
        return Arrays.asList(items);
    }*/



}