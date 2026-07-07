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
    private ImageView gameResultImageView;

    private EditText getGuess;

    private Button[] levelButtons;
    private Button[] rangeButtons;
    private TextView[] labels;
    private TextView difficultySelected;

    private int[] gameImages = new int[]{R.drawable.trophy,R.drawable.loser};
    private int[] winnerImages = new int[]{R.drawable.trophy,R.drawable.winner2};
    private int[] loserImages = new int[]{R.drawable.loser,R.drawable.loser2};
    private String[] difficultyModes = new String[]{"Easy","Med","Hard","Ext"};
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
    private String selectedMode;
    private int randomNumber;
    private int guessRange;
    private int guessRangeCount;

    private int guessMin = 1;
    private int selectedRange = 0;
    private Runnable clearLastTask;
    //private GameStatus gameStatus;

    private GameStatus gameStatus = GameStatus.GAME_IN_PROGRESS;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        if(gameStatus == GameStatus.GAME_IN_PROGRESS){
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
                System.out.println("you clicked me ");
                startGame(v);
            }
        });
    }

    private void clearButton(){

    }

    private void resetButton(){
        easyLevelButton.setEnabled(true);
        mediumLevelButton.setEnabled(true);
        hardLevelButton.setEnabled(true);
        extremeLevelButton.setEnabled(true);
        turns = 0;
        guessCount = 0;
        guessButton.setEnabled(false);
        clearScreenButton.setEnabled(false);
        resetButton.setEnabled(false);

    }
    private void startGame(View v){
        boolean isValid = isDataValid();
        if(!isValid){
            getErrorChecks();
        }else{
            checkGuess();
        }
    }

    public boolean isDataValid(){
        String guessText = getGuess.getText().toString();
        if(guessText.trim().isEmpty()){
            return false;
        }

        if(isGuessOutOfRange(guessText)){
            return false;
        }

        if(tryParseGuess(guessText) == null){
            return false;
        }
        return true;
    }

    private Integer tryParseGuess(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void checkGuess(){
        String guessText = getGuess.getText().toString();
        int guess = Integer.parseInt(guessText);
        Map<String,Boolean>checkGameStatus = new LinkedHashMap<>();
        checkGameStatus.put(getMessage("youGuessedIt"),hasNumberBeenGuessed(guess));
        checkGameStatus.put(getMessage("guessTooLow"),isNumberGuessedTooLow(guess));
        checkGameStatus.put(getMessage("guessTooHigh"),isNumberGuessedTooHigh(guess));
        for(Map.Entry<String,Boolean>entry:checkGameStatus.entrySet()){
            if(entry.getValue()){
                setGameMessage(feedbackTextField,entry.getKey());
                showTemporaryMessage(feedbackTextField);
                upDateUi(guess);
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

    private void getErrorChecks(){
        String guessText = getGuess.getText().toString();
        Map<String,Boolean> errorMessages = new LinkedHashMap<>();
        errorMessages.put(getMessage("emptyInput"),isInputEmpty());
        errorMessages.put(getMessage("outOfRangeGuess"),isGuessOutOfRange(guessText));
        errorMessages.put(getMessage("invalidNumber"),isGuessValidNumber());
        for(Map.Entry<String,Boolean>entry:errorMessages.entrySet()){
            if(entry.getValue()){
                setGameMessage(errorMessagesTextField,entry.getKey());
                showTemporaryMessage(errorMessagesTextField);
                break;
            }
        }
    }

    private void showTemporaryMessage(TextView textView) {
        textView.removeCallbacks(clearLastTask);
        clearLastTask = new Runnable() {
            @Override
            public void run() {
                textView.setText("");
            }
        };
        textView.postDelayed(clearLastTask,3000);
    }


    /*private void showTemporaryMessage(TextView textView){
        textView.postDelayed(new Runnable(){
            @Override
            public void run() {
                textView.setText("");
            }
        },5000);
    }*/

    private String getMessage(String message){
        switch(message){
            case "emptyInput":
                return String.format("YOU MUST ENTER A GUESS!!");

            case "outOfRangeGuess":
                return String.format("GUESS IS OUT OF RANGE OF SELECTED RANGE SELECTED!");

            case "invalidNumber":
                return String.format("NUMBERS ONLY! NO LETTERS OR SYMBOLS!!!");

            case "youGuessIt":
                return String.format("YOU GOT IT!");

            case "guessTooLow":
                return String.format("GUESS IS TO LOW!");

            case "guessTooHigh":
                return String.format("GUESS IS TO HIGH! ");
        }
        return message;
    }

    private boolean isInputEmpty(){
        String guessText = getGuess.getText().toString();
        return guessText.trim().isEmpty();
    }

    private boolean isGuessOutOfRange(String guessText){
        Integer guess = tryParseGuess(guessText);
        return guess == null || guess < guessMin || guess > selectedRange;
    }

    private boolean isGuessValidNumber(){
        return true;
    }
    private void difficultyGameActionListener(Button button){
        button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                turns = setGameDifficulty(v);
                turns = level;
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
        //randomNumber = getRandomNumber(selectedRange);
        displayRandomNumber();
        System.out.println(selectedRange);
        System.out.println(randomNumber);
        lockInGuessingRange();
    }

    private int setGameDifficulty(View v){
        for(int i = 0; i < levelButtons.length; i++){
            if(v.getId() == levelButtons[i].getId()){
                buttonId = buttonDifficultyLevels[i];
                selectedMode = difficultyModes[i];

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
                //setNumberOfGuesses(labels[i], difficultyLevels[i]);
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

    /*private int getRandomImage(int[]arr){
        return RAND_GENERATOR.nextInt(arr.length);
    }*/

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
    }

    private void upDateUi(int guess){
        if(hasNumberBeenGuessed(guess)){
            randomNumberTextView.setVisibility(View.VISIBLE);
            randomNumberTextView.setText(String.valueOf(randomNumber));
            howManyGuesses();
            displayRandomNumber();
        }else{
            guessCount++;
            upDateGame();
        }
    }

    private void howManyGuesses(){
        numberOfTriesTextField.setText("It took you " + guessCount + " number of guesses! ");
    }

    private void upDateGame(){
        level--;
        difficultySelected.setText(String.valueOf(level));
        checkTurns();
    }

    private void checkTurns(){
        if(level == 0){
            gameStatus = GameStatus.GAME_OVER;
            resetButton();
            randomNumber = getRandomNumber(selectedRange);
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