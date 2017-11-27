/*
 * BORQS Software Solutions Pvt Ltd. CONFIDENTIAL
 * Copyright (c) 2017 All rights reserved.
 *
 * The source code contained or described herein and all documents
 * related to the source code ("Material") are owned by BORQS Software
 * Solutions Pvt Ltd. No part of the Material may be used,copied,
 * reproduced, modified, published, uploaded,posted, transmitted,
 * distributed, or disclosed in any way without BORQS Software
 * Solutions Pvt Ltd. prior written permission.
 *
 * No license under any patent, copyright, trade secret or other
 * intellectual property right is granted to or conferred upon you
 * by disclosure or delivery of the Materials, either expressly, by
 * implication, inducement, estoppel or otherwise. Any license
 * under such intellectual property rights must be express and
 * approved by BORQS Software Solutions Pvt Ltd. in writing.
 *
 */
package com.borqs.ime;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.inputmethodservice.InputMethodService;
import android.os.Handler;
import android.text.InputType;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.borqs.ime.adapters.HorizontalPager;
import com.borqs.ime.adapters.ShiftModeListAdapter;
import com.borqs.ime.adapters.SymbolAdapter;
import com.borqs.ime.inputmodes.IInputModeCallback;
import com.borqs.ime.inputmodes.NumericInputMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class IME extends InputMethodService implements IInputModeCallback {
    static final boolean DEBUG = false;
    static final String TAG = "IME";
    private InputMethodManager mInputMethodManager;

    private LatinKeyboardView mInputView;
    private boolean mPredictionOn, mInputTypePassword;
    private AlertDialog mOptionsDialog;
    private boolean mEmojiGridViewLoaded = false;
    View mEmojiView = null;
    public GridEmojiView mSymbolsGridView;
    public static final int SYMBOLS_SCREEN = 0;
    private String mLocaleCode;
    private static final int PEOPLE_EMOJI_SCREEN = 1;
    private static final int OBJECTS_EMOJI_SCREEN = 2;
    private static final int NATURE_EMOJI_SCREEN = 3;
    private static final int CARS_EMOJI_SCREEN = 4;
    private static final int SYMBOLS_EMOJI_SCREEN = 5;
    private static final int EMOTICONS_SCREEN = 6;
    private ImageButton mSymbolsTab , mEmojiPeopleTab,
            mEmojiObjectsTab, mEmojiNatureTab, mEmojiCarsTab,
            mEmojiPunctionsTab, mEmoticonsTab;

    private HorizontalPager mPager;
    //TODO: move this to enum
    public static final int ENGLISH_LANGUAGE_INDEX = 13;

    private String mLocale;
    private ArrayList<String> mLangList;
    private HashMap<String, String> mLocaleHashMap;
    private static final int BASE_HEX = 16;
    private ArrayList<String> mAllEmojis = new ArrayList<String>();
    private GridEmojiView mPeopleEmojisGridView;
    private GridEmojiView mObjectEmojisGridView;
    private GridEmojiView mNatureEmojisGridView;
    private GridEmojiView mPlacesEmojisGridView;
    private GridEmojiView mSymbolsEmojisGridView;
    private GridEmojiView mEmojiemoticonsGridView;
    private static final String COMMA_STRING = ",";
    private static final String VERTICAL_BAR_STRING = "\\|";
    private boolean mKeyLongPressHandled;
    private int mLanguageIndex = ENGLISH_LANGUAGE_INDEX;
    private final String ACTION_CUSTOM_WORD_UPDATE = "com.borqs.ime.action.custom_word_update";
    private final String CUSTOM_WORD_TAG = "customWord";
    private final String IS_CUSTOM_WORD_ADDED_TAG = "isWordAdded";


    /**
     * Main initialization of the input method component.  Be sure to call
     * to super class.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        mLocaleCode = getResources().getConfiguration().locale.toString();
        loadLibrary();
        setLangList();
        initEmojiGridView();
        // register to receive screen off messages
        registerReceiver(mReceiver, new IntentFilter(
                Intent.ACTION_SCREEN_OFF));

        // register to receive screen on messages
        registerReceiver(mReceiver, new IntentFilter(
                Intent.ACTION_SCREEN_ON));
        registerReceiver(mReceiver, new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));

        // register to receive custom word intent
        registerReceiver(mReceiver, new IntentFilter(ACTION_CUSTOM_WORD_UPDATE));
    }

    @Override
    public void onDestroy() {
        if (mInputView != null) {
            mInputView.destroyInstance();
            mInputView = null;
        }
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    /**
     * This is the point where we do all of our UI initialization.  It
     * is called after creation and any configuration change.
     */
    @Override
    public void onInitializeInterface() {
    }

    /**
     * Called by the framework when your view for creating input needs to
     * be generated.  This will be called the first time your input method
     * is displayed, and every time it needs to be re-created such as due to
     * a configuration change.
     */
    @Override
    public View onCreateInputView() {
        createInputView();
        return mInputView;
    }

    /**
     * Called by the framework when your view for showing candidates needs to
     * be generated, like {@link #onCreateInputView}.
     */
    @Override
    public View onCreateCandidatesView() {
        return null;
    }

    @Override
    public boolean isExtractViewShown() {
        return false;
    }

    @Override
    public void onStartCandidatesView(EditorInfo info, boolean restarting) {
        super.onStartCandidatesView(info, restarting);
    }

    @Override
    public boolean onKeyMultiple(int keyCode, int count, KeyEvent event) {
        return super.onKeyMultiple(keyCode, count, event);
    }

    private void createInputView() {
        if (mInputView == null) {
            mInputView = (LatinKeyboardView) getLayoutInflater().inflate(
                    R.layout.input, null);
            setInputLang();
            mInputView.setHalfWord(true);
            this.setInputView(mInputView);
            updateInputViewShown();
            if (!isInputViewShown()) {
                showWindow(false);
            }
        }
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(
                    android.content.Intent.ACTION_SHUTDOWN)) {
                onDestroy();
            } else if (intent.getAction().equals(
                    android.content.Intent.ACTION_SCREEN_OFF)) {
                updateStatusBarIcon(0);
                if (mInputView != null) {
                    mInputView.destroyInstance();
                }
            } else if (intent.getAction().equals(
                    android.content.Intent.ACTION_SCREEN_ON)) {
            } else if (intent.getAction().equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                if (mOptionsDialog != null && mOptionsDialog.isShowing()) {
                    mOptionsDialog.dismiss();
                    mOptionsDialog = null;
                }
            } else if(intent.getAction().toString().equalsIgnoreCase(ACTION_CUSTOM_WORD_UPDATE)) {
                String customWord = intent.getExtras().getString(CUSTOM_WORD_TAG);
                char[] temp = customWord.toCharArray();
                if (intent.getExtras().getBoolean(IS_CUSTOM_WORD_ADDED_TAG)) {
                    NativeAlphaInput.xt9input_addCustomWord(temp, temp.length);
                } else {
                    NativeAlphaInput.xt9input_deleteCustomWord(temp, temp.length);
                }

            }
        }
    };

    @Override
    public boolean onEvaluateFullscreenMode() {
        //FullScreen mode is never shown
        return false;
    }

    @Override
    public void onStartInput(EditorInfo attribute, boolean restarting) {

        super.onStartInput(attribute, restarting);
        mPredictionOn = false;
        mInputTypePassword = false;
        switch (attribute.inputType & InputType.TYPE_MASK_CLASS) {
            case InputType.TYPE_CLASS_NUMBER:
            case InputType.TYPE_CLASS_PHONE:

                createInputView();
                mInputView.setNumericInputMode(this, attribute.inputType);
                break;

            case InputType.TYPE_CLASS_TEXT:
                mPredictionOn = true;
                int variation = attribute.inputType & InputType.TYPE_MASK_VARIATION;
                if (variation == InputType.TYPE_TEXT_VARIATION_PASSWORD
                        || variation == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                        || variation == InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD) {
                    mPredictionOn = false;
                    mInputTypePassword = true;
                }
                if (variation == InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                        || variation == InputType.TYPE_TEXT_VARIATION_URI
                        || variation == InputType.TYPE_TEXT_VARIATION_FILTER
                        || variation == InputType.TYPE_TEXT_VARIATION_PERSON_NAME
                        || variation == InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS
                        || variation == InputType.TYPE_TEXT_VARIATION_PHONETIC) {
                    mPredictionOn = false;
                }
                if ((attribute.inputType & InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE) != 0) {
                    mPredictionOn = false;
                }
                if((attribute.inputType & InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS) != 0){
                    mPredictionOn = false;
                }

                createInputView();
                if (mPredictionOn) {
                    mInputView.setT9AbcInputMode(this);
                } else {
                    mInputView.setAbcInputMode(this);
                }
                break;
            default:
                // group editor input type = 176, browser findOnPage inputType = 524288
                if ((attribute.inputType == InputType.TYPE_TEXT_VARIATION_FILTER) ||
                        (attribute.inputType ==InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS)) {
                    createInputView();
                    mPredictionOn = false;
                    if (mPredictionOn) {
                        mInputView.setT9AbcInputMode(this);
                    } else {
                        mInputView.setAbcInputMode(this);
                    }
                }
                break;
        }
    }

    /**
     * This is called when the user is done editing a field.  We can use
     * this to reset our state.
     */
    @Override
    public void onFinishInput() {
        super.onFinishInput();

        if (mInputView != null) {
            mInputView.destroyInstance();
        }
        mInputView = null;
        this.showStatusIcon(0);
    }

    @Override
    public void onStartInputView(EditorInfo attribute, boolean restarting) {
        createInputView();
        super.onStartInputView(attribute, restarting);
    }

    @Override
    public void onCurrentInputMethodSubtypeChanged(InputMethodSubtype subtype) {
    }

    @Override
    public void onFinishCandidatesView(boolean finishingInput) {
        super.onFinishCandidatesView(finishingInput);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean isEventhandled = false;
        if(keyCode == KeyEvent.KEYCODE_BACK){
            hideWindow();
            return isEventhandled;
        }

        if (mInputView != null) {
            isEventhandled = mInputView.onKeyDown(keyCode, event);
        }
        if (!isEventhandled) {
            return super.onKeyDown(keyCode, event);
        } else {
            return isEventhandled;
        }
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        boolean isEventhandled = false;
        if (mInputView != null) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_POUND: {
                    if (event.getAction() == KeyEvent.ACTION_DOWN) {
                        showLanguageOptionDialog();
                        mKeyLongPressHandled = true;
                        isEventhandled = true;
                    }
                    break;
                }
                case KeyEvent.KEYCODE_1: {
                    if (event.getAction() == KeyEvent.ACTION_DOWN ) {
                        mKeyLongPressHandled = true;
                        showemojilayout();
                        isEventhandled = true;
                        if (mInputView.getCurrentInputMode() != null &&
                                mInputView.getCurrentInputMode() instanceof NumericInputMode) {
                            assignVisibilityForGridandTabs(View.GONE);
                        }
                        if (mInputTypePassword) {
                            assignVisibilityForGridandTabs(View.GONE);
                        }
                        break;
                    }
                }
                case KeyEvent.KEYCODE_2:
                case KeyEvent.KEYCODE_3:
                case KeyEvent.KEYCODE_4:
                case KeyEvent.KEYCODE_5:
                case KeyEvent.KEYCODE_6:
                case KeyEvent.KEYCODE_7:
                case KeyEvent.KEYCODE_8:
                case KeyEvent.KEYCODE_9:
                case KeyEvent.KEYCODE_0:
                    mKeyLongPressHandled = true;
                    getInputConnection().commitText(String.valueOf((char)
                            (keyCode + '0' - KeyEvent.KEYCODE_0)), 1);
                    isEventhandled = true;
            }
        }
        if (!isEventhandled) {
            return super.onKeyLongPress(keyCode, event);
        } else {
            return isEventhandled;
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        boolean isEventhandled = false;
        if (mInputView != null && !mKeyLongPressHandled) {
            isEventhandled = mInputView.onKeyUp(keyCode, event);
        }
        mKeyLongPressHandled = false;
        if (!isEventhandled) {
            return super.onKeyUp(keyCode, event);
        } else {
            return isEventhandled;
        }
    }

    //This method will populate the emojis view
    void initEmojiGridView() {
        if (mEmojiGridViewLoaded) {
            return;
        }
        mAllEmojis.clear();
        LayoutInflater inflater = (LayoutInflater) getBaseContext().
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mEmojiView = inflater.inflate(R.layout.emoji_window, null);
        //Setting Symbols View
        String[] mTempArray = getResources().getStringArray(R.array.symbols);
        mSymbolsGridView = (GridEmojiView) inflater.inflate(
                R.layout.grid_symbols_view, null);
        mSymbolsGridView.setAdapter(new SymbolAdapter(this, R.layout.simple_list, mTempArray));
        mSymbolsGridView.setTag(SYMBOLS_SCREEN);

        /// borqs: emoji added starts...
        //Setting up Emoji Faces
        mTempArray = getResources().getStringArray(R.array.emoji_people);
        String[] emojisPeople = new String[mTempArray.length];
        int i = 0;
        for (i=0; i<mTempArray.length;i++) {
            emojisPeople[i] = parseEmoji(mTempArray[i]);
            mAllEmojis.add(emojisPeople[i]);
        }
        mPeopleEmojisGridView = (GridEmojiView)inflater.inflate(
                R.layout.grid_emoji_view, null);
        mPeopleEmojisGridView.setArray(emojisPeople);
        mPeopleEmojisGridView.setTag(PEOPLE_EMOJI_SCREEN);


        //Setting up Emoji Objects
        mTempArray = getResources().getStringArray(R.array.emoji_objects);
        String[] emojisObjects = new String[mTempArray.length];
        for(i=0; i<mTempArray.length;i++) {
            emojisObjects[i] = parseEmoji(mTempArray[i]);
            mAllEmojis.add(emojisObjects[i]);
        }
        mObjectEmojisGridView = (GridEmojiView)inflater.inflate(
                R.layout.grid_emoji_view, null);
        mObjectEmojisGridView.setArray(emojisObjects);
        mObjectEmojisGridView.setTag(OBJECTS_EMOJI_SCREEN);


        //Setting up Emoji Nature
        mTempArray = getResources().getStringArray(R.array.emoji_nature);
        String[] emojisNature = new String[mTempArray.length];
        for(i=0; i<mTempArray.length;i++) {
            emojisNature[i] = parseEmoji(mTempArray[i]);
            mAllEmojis.add(emojisNature[i]);
        }
        mNatureEmojisGridView = (GridEmojiView)inflater.inflate(
                R.layout.grid_emoji_view, null);
        mNatureEmojisGridView.setArray(emojisNature);
        mNatureEmojisGridView.setTag(NATURE_EMOJI_SCREEN);

        //Setting up Emoji Places
        mTempArray = getResources().getStringArray(R.array.emoji_cars);
        String[] emojisPlaces = new String[mTempArray.length];
        for(i=0; i<mTempArray.length;i++) {
            emojisPlaces[i] = parseEmojiLabels(mTempArray[i]);
            mAllEmojis.add(emojisPlaces[i]);
        }
        mPlacesEmojisGridView = (GridEmojiView)inflater.inflate(
                R.layout.grid_emoji_view, null);
        mPlacesEmojisGridView.setArray(emojisPlaces);
        mPlacesEmojisGridView.setTag(CARS_EMOJI_SCREEN);

        //Setting up Emoji Symbols
        mTempArray = getResources().getStringArray(R.array.emoji_symbols);
        String[] emojiSymbols = new String[mTempArray.length];
        for(i=0; i<mTempArray.length;i++) {
            emojiSymbols[i] = parseEmojiLabels(mTempArray[i]);
            mAllEmojis.add(emojiSymbols[i]);
        }
        mSymbolsEmojisGridView = (GridEmojiView)inflater.inflate(
                R.layout.grid_emoji_view, null);
        mSymbolsEmojisGridView.setArray(emojiSymbols);
        mSymbolsEmojisGridView.setTag(SYMBOLS_EMOJI_SCREEN);

        String[] emojiemoticons = getResources().getStringArray(R.array.emoji_emoticons);
        mEmojiemoticonsGridView = (GridEmojiView)inflater.inflate(
                R.layout.grid_emojiemoticons_view, null);
        mEmojiemoticonsGridView.setArray(emojiemoticons);
        mEmojiemoticonsGridView.setTag(EMOTICONS_SCREEN);
        mEmojiGridViewLoaded = true;

        mPager = (HorizontalPager) mEmojiView.findViewById(R.id.horizontal_pager);
        mPager.removeAllViews();
        mPager.addView(mSymbolsGridView);

        mPager.addView(mPeopleEmojisGridView);
        mPager.addView(mObjectEmojisGridView);
        mPager.addView(mNatureEmojisGridView);
        mPager.addView(mPlacesEmojisGridView);
        mPager.addView(mSymbolsEmojisGridView);
        mPager.addView(mEmojiemoticonsGridView);


        ImageButton symbolsTab = (ImageButton) mEmojiView
                .findViewById(R.id.symbols_tab);
        setmSymbolsTab(symbolsTab);

        ImageButton emojiPeopleTab = (ImageButton) mEmojiView
                .findViewById(R.id.emojis_tab_1_people);
        setmEmojiPeopleTab(emojiPeopleTab);
        ImageButton emojiObjectsTab = (ImageButton) mEmojiView
                .findViewById(R.id.emojis_tab_3_objects);
        setmEmojiObjectsTab(emojiObjectsTab);
        ImageButton emojiNatureTab = (ImageButton) mEmojiView
                .findViewById(R.id.emojis_tab_2_nature);
        setmEmojiNatureTab(emojiNatureTab);
        ImageButton emojiCarsTab = (ImageButton) mEmojiView
                .findViewById(R.id.emojis_tab_4_cars);
        setmEmojiCarsTab(emojiCarsTab);
        ImageButton emojiPunctionsTab = (ImageButton) mEmojiView
                .findViewById(R.id.emojis_tab_5_punctuation);
        setmEmojiPunctionsTab(emojiPunctionsTab);
        ImageButton emoticonsTab = (ImageButton) mEmojiView
                .findViewById(R.id.emojis_emoticons);
        setmEmoticonsTab(emoticonsTab);
        mSymbolsTab.setVisibility(View.GONE);
        assignVisibilityForGridandTabs(View.GONE);


        final LinearLayout emojisTab = (LinearLayout) mEmojiView.
                findViewById(R.id.emojis_tab);
        mPager.setOnScreenSwitchListener(new HorizontalPager.OnScreenSwitchListener() {
            @Override
            public void onScreenSwitched(int screen) {
                for (int i = 0; i < emojisTab.getChildCount(); i++) {
                    emojisTab.getChildAt(i).setPressed(false);
                }
                emojisTab.getChildAt(screen).setPressed(true);
            }
        });

        View.OnFocusChangeListener focusChangeListenerfortab =
                new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mPager.setCurrentScreen((Integer) v.getTag(), true);
                    setFocusForcategory(R.drawable.ic_emoji_focused_bg2_2);
                }
            }
        };

        View.OnFocusChangeListener focusChangeListenerforgrid =
                new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(final View v, boolean hasFocus) {
                if (hasFocus) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            GridView gv = (GridView) mPager.getChildAt(
                                    mPager.getCurrentScreen());
                            //The below code is to handle the focus
                            //when Recent Emoji grid is empty.
                            int code = (Integer) gv.getTag();
                            if (gv != null && gv.getChildCount() == 0) {

                            } else {
                                gv.requestFocus();
                                gv.setSelection(0);
                                setFocusForcategory(R.drawable.ic_emoji_focused_bg2);
                            }
                        }
                    }, 10);
                }
            }
        };
        mSymbolsGridView.setOnFocusChangeListener(focusChangeListenerforgrid);

        mPeopleEmojisGridView.setOnFocusChangeListener(focusChangeListenerforgrid);
        mObjectEmojisGridView.setOnFocusChangeListener(focusChangeListenerforgrid);
        mNatureEmojisGridView.setOnFocusChangeListener(focusChangeListenerforgrid);
        mPlacesEmojisGridView.setOnFocusChangeListener(focusChangeListenerforgrid);
        mSymbolsEmojisGridView.setOnFocusChangeListener(focusChangeListenerforgrid);
        mEmojiemoticonsGridView.setOnFocusChangeListener(focusChangeListenerforgrid);

        mSymbolsTab.setOnFocusChangeListener(focusChangeListenerfortab);

        mEmojiPeopleTab.setOnFocusChangeListener(focusChangeListenerfortab);
        mEmojiObjectsTab.setOnFocusChangeListener(focusChangeListenerfortab);
        mEmojiNatureTab.setOnFocusChangeListener(focusChangeListenerfortab);
        mEmojiCarsTab.setOnFocusChangeListener(focusChangeListenerfortab);
        mEmojiPunctionsTab.setOnFocusChangeListener(focusChangeListenerfortab);
        mEmoticonsTab.setOnFocusChangeListener(focusChangeListenerfortab);

        mSymbolsTab.setOnClickListener(onClickListerner);

        mEmojiPeopleTab.setOnClickListener(onClickListerner);
        mEmojiObjectsTab.setOnClickListener(onClickListerner);
        mEmojiNatureTab.setOnClickListener(onClickListerner);
        mEmojiCarsTab.setOnClickListener(onClickListerner);
        mEmojiPunctionsTab.setOnClickListener(onClickListerner);
        mEmoticonsTab.setOnClickListener(onClickListerner);

    }

    private void setmSymbolsTab(ImageButton symbolsTab) {
        mSymbolsTab = symbolsTab;
        mSymbolsTab.setTag(SYMBOLS_SCREEN);
    }

    public void setFocusForcategory(int background) {
        //Setting the Focus for categoty icons by replacing the images
        GridView gv = (GridView) mPager.getChildAt(mPager.getCurrentScreen());
        int code = (Integer) gv.getTag();
        if (code == SYMBOLS_SCREEN) {
            assignImage(R.drawable.ic_emoji_symbol1_activated,
                    R.drawable.ic_emoji_people_normal,
                    R.drawable.ic_emoji_objects_normal,
                    R.drawable.ic_emoji_nature_normal,
                    R.drawable.ic_emoji_places_normal,
                    R.drawable.ic_emoji_symbols_normal,
                    R.drawable.ic_emoji_smily_normal,
                    background,
                    R.color.white, R.color.white, R.color.white, R.color.white,
                    R.color.white, R.color.white);
        }
        else if (code == PEOPLE_EMOJI_SCREEN) {
            assignImage(R.drawable.ic_emoji_symbol1_normal,
                    R.drawable.ic_emoji_people_activated,
                    R.drawable.ic_emoji_objects_normal,
                    R.drawable.ic_emoji_nature_normal,
                    R.drawable.ic_emoji_places_normal,
                    R.drawable.ic_emoji_symbols_normal,
                    R.drawable.ic_emoji_smily_normal, R.color.white,
                    background,
                    R.color.white, R.color.white, R.color.white, R.color.white,
                    R.color.white);
        } else if (code == OBJECTS_EMOJI_SCREEN) {
            assignImage(R.drawable.ic_emoji_symbol1_normal,
                    R.drawable.ic_emoji_people_normal,
                    R.drawable.ic_emoji_objects_activated,
                    R.drawable.ic_emoji_nature_normal,
                    R.drawable.ic_emoji_places_normal,
                    R.drawable.ic_emoji_symbols_normal,
                    R.drawable.ic_emoji_smily_normal, R.color.white,
                    R.color.white,
                    background, R.color.white,
                    R.color.white, R.color.white, R.color.white);
        } else if (code == NATURE_EMOJI_SCREEN) {
            assignImage(R.drawable.ic_emoji_symbol1_normal,
                    R.drawable.ic_emoji_people_normal,
                    R.drawable.ic_emoji_objects_normal,
                    R.drawable.ic_emoji_nature_activated,
                    R.drawable.ic_emoji_places_normal,
                    R.drawable.ic_emoji_symbols_normal,
                    R.drawable.ic_emoji_smily_normal, R.color.white,
                    R.color.white, R.color.white,
                    background, R.color.white,
                    R.color.white, R.color.white);
        } else if (code == CARS_EMOJI_SCREEN) {
            assignImage(R.drawable.ic_emoji_symbol1_normal,
                    R.drawable.ic_emoji_people_normal,
                    R.drawable.ic_emoji_objects_normal,
                    R.drawable.ic_emoji_nature_normal,
                    R.drawable.ic_emoji_places_activated,
                    R.drawable.ic_emoji_symbols_normal,
                    R.drawable.ic_emoji_smily_normal, R.color.white,
                    R.color.white, R.color.white, R.color.white,
                    background, R.color.white,
                    R.color.white);

        } else if (code == SYMBOLS_EMOJI_SCREEN) {
            assignImage(R.drawable.ic_emoji_symbol1_normal,
                    R.drawable.ic_emoji_people_normal,
                    R.drawable.ic_emoji_objects_normal,
                    R.drawable.ic_emoji_nature_normal,
                    R.drawable.ic_emoji_places_normal,
                    R.drawable.ic_emoji_symbols_activated,
                    R.drawable.ic_emoji_smily_normal, R.color.white,
                    R.color.white, R.color.white, R.color.white,
                    R.color.white, background,
                    R.color.white);

        } else if (code == EMOTICONS_SCREEN) {
            assignImage(R.drawable.ic_emoji_symbol1_normal,
                    R.drawable.ic_emoji_people_normal,
                    R.drawable.ic_emoji_objects_normal,
                    R.drawable.ic_emoji_nature_normal,
                    R.drawable.ic_emoji_places_normal,
                    R.drawable.ic_emoji_symbols_normal,
                    R.drawable.ic_emoji_smily_activated, R.color.white,
                    R.color.white, R.color.white, R.color.white,
                    R.color.white, R.color.white,
                    background);
        }
    }

    public void assignImage(int image0, int image2, int image3,
                            int image4, int image5, int image6, int image7, int background1,
                            int background3, int background4, int background5,
                            int background6, int background7, int background8
                ) {
        mSymbolsTab.setImageDrawable(getBaseContext().getResources()
                .getDrawable(image0));
        mSymbolsTab.setBackground(getBaseContext().getResources().getDrawable(
                background1));
        mEmojiPeopleTab.setImageDrawable(getBaseContext().getResources()
                .getDrawable(image2));
        mEmojiPeopleTab.setBackground(getBaseContext().getResources()
                .getDrawable(background3));
        mEmojiObjectsTab.setImageDrawable(getBaseContext().getResources()
                .getDrawable(image3));
        mEmojiObjectsTab.setBackground(getBaseContext().getResources()
                .getDrawable(background4));
        mEmojiNatureTab.setImageDrawable(getBaseContext().getResources()
                .getDrawable(image4));
        mEmojiNatureTab.setBackground(getBaseContext().getResources()
                .getDrawable(background5));
        mEmojiCarsTab.setImageDrawable(getBaseContext().getResources()
                .getDrawable(image5));
        mEmojiCarsTab.setBackground(getBaseContext().getResources()
                .getDrawable(background6));
        mEmojiPunctionsTab.setImageDrawable(getBaseContext().getResources()
                .getDrawable(image6));
        mEmojiPunctionsTab.setBackground(getBaseContext().getResources()
                .getDrawable(background7));
        mEmoticonsTab.setImageDrawable(getBaseContext().getResources()
                .getDrawable(image7));
        mEmoticonsTab.setBackground(getBaseContext().getResources()
                .getDrawable(background8));

    }

    private View.OnClickListener onClickListerner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            GridView gv = (GridView) mPager.getChildAt(mPager.getCurrentScreen());
            gv.requestFocus();
            gv.setSelection(0);
        }
    };

    // This method will show the emoji window
    void showemojilayout() {
        View v = (View) mEmojiView.getParent();
        if (v != null && v instanceof FrameLayout) {
            ((FrameLayout) v).removeView(mEmojiView);
        }

        assignVisibilityForGridandTabs(View.VISIBLE);

        mSymbolsGridView.setVisibility(View.VISIBLE);
        mSymbolsTab.setVisibility(View.VISIBLE);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) getBaseContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        builder.setView(mEmojiView);

        if (mOptionsDialog != null) {
            mOptionsDialog.cancel();
        }
        mOptionsDialog = builder.create();
        Window window = mOptionsDialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.token = mInputView.getWindowToken();
        lp.type = WindowManager.LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG;
        lp.gravity = Gravity.TOP;
        lp.y = getResources().getInteger(R.integer.window_alignment_height);
        window.setAttributes(lp);
        window.addFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        ImageButton symbolsTab = (ImageButton) mEmojiView.findViewById(R.id.symbols_tab);
        symbolsTab.callOnClick();
        mOptionsDialog.show();
    }

    @Override
    public void hideWindow() {

        if (mOptionsDialog != null && mOptionsDialog.isShowing()) {
            mOptionsDialog.dismiss();
            mOptionsDialog = null;
        }
        super.hideWindow();
    }

    public ImageButton getmSymbolsTab() {
        return mSymbolsTab;
    }


    private void loadLibrary() {
        System.loadLibrary("jni_basict9input");
    }

    /* This IME doesn't support Soft IME so sending false here*/
    @Override
    public boolean onEvaluateInputViewShown() {
        return false;
    }

    public void updateStatusBarIcon(int resId) {
        this.showStatusIcon(resId);
    }

    public InputConnection getInputConnection() {
        return this.getCurrentInputConnection();
    }

    public void toggleCapsMode(boolean switchOffCamelcase) {
        if (mInputView != null) {
            mInputView.toggleCapsMode( switchOffCamelcase);
        }
    }
    public int getLangugage() { return mLanguageIndex; }
    public boolean isPredictionOn(){ return mPredictionOn; };

    public void showLanguageOptionDialog() {

        if (mOptionsDialog != null && mOptionsDialog.isShowing()) {
            return;
        }
        final ShiftModeListAdapter shiftModeAdatper = new ShiftModeListAdapter(
                this, mLangList);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setCancelable(true);
        builder.setOnKeyListener(new DialogInterface.OnKeyListener() {

            public boolean onKey(DialogInterface dialog, int keyCode,
                                 KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_CALL
                        && event.getAction() == KeyEvent.ACTION_UP) {
                    dialog.dismiss();
                }
                return false;
            }
        });

        builder.setNegativeButton(android.R.string.cancel, null);
        builder.setAdapter(shiftModeAdatper,
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface di, int position) {
                        TextView textView = (TextView) shiftModeAdatper.getItem(position);
                        String langString = textView.getText().toString();
                        Toast.makeText(getApplicationContext(), "selected lang is: "
                                + langString, Toast.LENGTH_SHORT).show();
                        updateInputLang(position, langString);
                        di.dismiss();
                    }
                });

        builder.setTitle(getResources().getString(
                R.string.language_selection_dialog_title));

        mOptionsDialog = builder.create();
        Window window = mOptionsDialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();

        lp.token = mInputView.getWindowToken();
        lp.type = WindowManager.LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG;

        window.setAttributes(lp);
        window.addFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

        mOptionsDialog.show();

    }

    private void setInputLang() {
        // check if locale exists in engine lang list ?
        if (!setLocaleAsInputLang()) {
            mLanguageIndex = ENGLISH_LANGUAGE_INDEX;
            mInputView.setInputLanguage(ENGLISH_LANGUAGE_INDEX);
            updateInputLang(ENGLISH_LANGUAGE_INDEX, mLangList.get(ENGLISH_LANGUAGE_INDEX));
        }
    }

    private void updateInputLang(int langIndex, String langString) {
        mLanguageIndex = langIndex;
        // update mLocaleCode
        for (Entry<String, String> entry : mLocaleHashMap.entrySet()) {
            if (entry.getValue().equals(langString)) {
                mLocaleCode = entry.getKey().toString();
                break;
            }
        }
        if (mInputView == null) {
            createInputView();
        }
        mInputView.setInputLanguage(langIndex);
        saveInputLangToPref(langString);
    }

    public void saveInputLangToPref(String inputLang) {
        if (mInputView != null) {
            mInputView.saveInputLangToPref(this, inputLang);
        }
    }

    public String getCurrentInputLang() {
        if (mInputView != null) {
            return mInputView.getInputLangFromPref(this);
        }
        return null;

    }

    private void setLangList() {
        if (mLangList == null) {
            mLangList = new ArrayList<String>();
        }
        if (mLangList.size() <= 0) {
            String[] langListArray = getResources().
                    getStringArray(R.array.lang_array);
            for (int i=0; i<langListArray.length; i++) {
                mLangList.add(langListArray[i]);
            }
        }
        if (mLocaleHashMap == null) {
            mLocaleHashMap = new HashMap<String, String>();
        }
        if (mLocaleHashMap.size() <= 0) {
            String[] localeCodesArray = getResources().
                    getStringArray(R.array.localed_codes_array);
            for (int j=0; j<localeCodesArray.length; j++) {
                mLocaleHashMap.put(localeCodesArray[j], mLangList.get(j));
            }
        }
    }

    public void onConfigurationChanged(Configuration conf) {
        mLocaleCode = conf.locale.toString();
        if (mLocaleHashMap.get(mLocaleCode) != null) {
            // i.e. if selected device locale is supported by IME engine
            String langString = mLocaleHashMap.get(mLocaleCode);
            if (!langString.equalsIgnoreCase(getCurrentInputLang())) {
                setLocaleAsInputLang();
            }
        }
    }

    public boolean setLocaleAsInputLang() {
        boolean isLocaleSupported = false;
        if (mLocaleHashMap.get(mLocaleCode) != null) {
            String langString = mLocaleHashMap.get(mLocaleCode);
            int index = 0;
            for (index = 0; index < mLangList.size(); index++) {
                if (mLangList.get(index).equalsIgnoreCase(langString)) {
                    updateInputLang(index, langString);
                    isLocaleSupported = true;
                    break;
                }
            }
        }
        return isLocaleSupported;
    }


    // It takes unicode string and convert to Emoji image
    public String parseEmoji(String S) {
        final StringBuilder sb = new StringBuilder();
        final int codePoint = Integer.parseInt(S, BASE_HEX);
        sb.appendCodePoint(codePoint);
        return (sb.toString());
    }

    // This method can parse the unicode with diffrent formats
    public static String parseEmojiLabels(final String codesArraySpec) {
        final String labelSpec = getLabelSpec(codesArraySpec);
        final StringBuilder sb = new StringBuilder();
        for (final String codeInHex : labelSpec.split(COMMA_STRING)) {
            final int codePoint = Integer.parseInt(codeInHex, BASE_HEX);
            sb.appendCodePoint(codePoint);
        }
        return sb.toString();
    }

    private static String getLabelSpec(final String codesArraySpec) {
        final String[] strs = codesArraySpec.split(VERTICAL_BAR_STRING, -1);
        if (strs.length <= 1) {
            return codesArraySpec;
        }
        return strs[0];
    }

    public ImageButton getmEmojiPeopleTab() {
        return mEmojiPeopleTab;
    }

    private void setmEmojiPeopleTab(ImageButton emojiPeopleTab) {
        mEmojiPeopleTab = emojiPeopleTab;
        mEmojiPeopleTab.setTag(PEOPLE_EMOJI_SCREEN);
    }

    public ImageButton getmEmojiObjectsTab() {
        return mEmojiObjectsTab;
    }

    private void setmEmojiObjectsTab(ImageButton emojiObjectsTab) {
        mEmojiObjectsTab = emojiObjectsTab;
        mEmojiObjectsTab.setTag(OBJECTS_EMOJI_SCREEN);
    }
    public ImageButton getmEmojiNatureTab() {
        return mEmojiNatureTab;
    }

    private void setmEmojiNatureTab(ImageButton emojiNatureTab) {
        mEmojiNatureTab = emojiNatureTab;
        mEmojiNatureTab.setTag(NATURE_EMOJI_SCREEN);
    }

    public ImageButton getmEmojiCarsTab() {
        return mEmojiCarsTab;
    }

    private void setmEmojiCarsTab(ImageButton emojiCarsTab) {
        mEmojiCarsTab = emojiCarsTab;
        mEmojiCarsTab.setTag(CARS_EMOJI_SCREEN);
    }

    public ImageButton getmEmojiPunctionsTab() {
        return mEmojiPunctionsTab;
    }

    private void setmEmojiPunctionsTab(ImageButton emojiPunctionsTab) {
        mEmojiPunctionsTab = emojiPunctionsTab;
        mEmojiPunctionsTab.setTag(SYMBOLS_EMOJI_SCREEN);
    }

    public ImageButton getmEmoticonsTab() {
        return mEmoticonsTab;
    }

    public void setmEmoticonsTab(ImageButton emoticonsTab) {
        mEmoticonsTab = emoticonsTab;
        mEmoticonsTab.setTag(EMOTICONS_SCREEN);
    }

    //method to choose the visibility of the emoji or Symbols window
    void assignVisibilityForGridandTabs(int categoryTabVisibility){
        mPeopleEmojisGridView.setVisibility(categoryTabVisibility);
        mObjectEmojisGridView.setVisibility(categoryTabVisibility);
        mNatureEmojisGridView.setVisibility(categoryTabVisibility);
        mPlacesEmojisGridView.setVisibility(categoryTabVisibility);
        mSymbolsEmojisGridView.setVisibility(categoryTabVisibility);
        mEmojiemoticonsGridView.setVisibility(categoryTabVisibility);
        mEmojiPeopleTab.setVisibility(categoryTabVisibility);
        mEmojiObjectsTab.setVisibility(categoryTabVisibility);
        mEmojiNatureTab.setVisibility(categoryTabVisibility);
        mEmojiCarsTab.setVisibility(categoryTabVisibility);
        mEmojiPunctionsTab.setVisibility(categoryTabVisibility);
        mEmoticonsTab.setVisibility(categoryTabVisibility);
    }

    public void showSpellDialog(String spelledWord) {
        if (mOptionsDialog != null && mOptionsDialog.isShowing()) {
            return;
        }

        final ShiftModeListAdapter shiftModeAdatper = new ShiftModeListAdapter(
                this, mLangList);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setCancelable(true);
        builder.setOnKeyListener(new DialogInterface.OnKeyListener() {

            public boolean onKey(DialogInterface dialog, int keyCode,
                                 KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_CALL
                        && event.getAction() == KeyEvent.ACTION_UP) {
                    dialog.dismiss();
                }
                return false;
            }
        });

        final EditText spellEditText = new EditText(this);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
          // TODO: we have to define params based on gravity
          //spellEditText.setWidth(340);
          //spellEditText.setHeight(100);
          //spellEditText.setGravity(Gravity.CENTER);
          //spellEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        spellEditText.setLayoutParams(param);
        builder.setView(spellEditText);
        spellEditText.setText(spelledWord);
        builder.setNegativeButton(android.R.string.cancel, null);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String spellWord = spellEditText.getText().toString();
                Toast.makeText(IME.this, "Added word is: " +
                        spellWord, Toast.LENGTH_SHORT).show();
            }
        });
        builder.setTitle(getResources().getString(
                R.string.add_word_dialog_title));
        mOptionsDialog = builder.create();
        Window window = mOptionsDialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();

        lp.token = mInputView.getWindowToken();
        lp.type = WindowManager.LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG;

        window.setAttributes(lp);
        window.addFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        mOptionsDialog.show();
    }

    public void showSpellDialogActivity(){
        Intent intent = new Intent(this, SpellDialogActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}
