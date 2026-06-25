package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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

    private EditText getGuess;

    private String[] difficultyModes = new String[]{"Easy","Med","Hard","Ext"};
    private Button[] levelButtons;
    private Button[] rangeButtons;
    private TextView[] labels;

    private String[] numberRangeModes = new String[]{"1-10","1-20","1-50","100"};
    private static final Random RAND_GENERATOR = new Random();
    private static final int MAX_RANGE_TEN = 10;
    private static final int MAX_RANGE_TWENTY = 20;
    private static final int MAX_RANGE_FIFTY = 50;
    private static final int MAX_RANGE_ONE_HUNDRED = 100;

    private int[] rangeLevels = new int[]{MAX_RANGE_TEN,MAX_RANGE_TWENTY,MAX_RANGE_FIFTY,MAX_RANGE_ONE_HUNDRED};

    private static final int EASY_GAME_MODE = 15;
    private static final int MEDIUM_GAME_MODE = 10;
    private static final int HARD_GAME_MODE = 5;
    private static final int EXTREME_HARD_GAME_MODE = 3;

    private int[] difficultyLevels = new int[]{EASY_GAME_MODE,MEDIUM_GAME_MODE,HARD_GAME_MODE,EXTREME_HARD_GAME_MODE};

    private static final int BUTTON_ONE = 1;
    private static final int BUTTON_TWO = 2;
    private static final int BUTTON_THREE = 3;
    private static final int BUTTON_FOUR = 4;
    private int[] buttonDifficultyLevels = new int[]{BUTTON_ONE,BUTTON_TWO,BUTTON_THREE,BUTTON_FOUR};
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


    private GameStatus gameStatus = GameStatus.GAME_IN_PROGRESS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        //displayRandomNumber();
        if(gameStatus == GameStatus.GAME_IN_PROGRESS){
            initializeDifficultyButtonListeners();
            initializeRangeButtonListeners();
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

    private void guessButtonActionListener(){
        guessButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                startGame(v);
            }
        });
    }

    private void startGame(View v){
        boolean isValid = isDataValid();
        if(!isValid){
            getErrorChecks();
        }else{
            // do something here
            checkGuess();
        }
    }

    private void getErrorChecks(){
        Map<String,Boolean> errorMessages = new LinkedHashMap<>();
        errorMessages.put(getMessage("emptyInput"),isInputEmpty());
        errorMessages.put(getMessage("outOfRangeGuess"),isGuessOutOfRange());
        errorMessages.put(getMessage("invalidNumber"),isGuessValidNumber());
        for(Map.Entry<String,Boolean>entry:errorMessages.entrySet()){
            if(entry.getValue()){
                break;
            }
        }
    }

    private String getMessage(String message){
        return"";
    }

    private boolean isInputEmpty(){
        String guessText = getGuess.getText().toString();
        return guessText.trim().isEmpty();
    }

    private boolean isGuessOutOfRange(){
        String guessText = getGuess.getText().toString();
        int guess = Integer.parseInt(guessText);
        return guess < guessMin || guess > selectedRange;
    }

    private boolean isGuessValidNumber(){
        return true;
    }

    private void checkGuess(){

    }

    public boolean isDataValid(){
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
        randomNumber = getRandomNumber(selectedRange);
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
                setNumberOfGuesses(labels[i], difficultyLevels[i]);
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

    private void displayErrorMessages(TextView view,int somenumber){

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