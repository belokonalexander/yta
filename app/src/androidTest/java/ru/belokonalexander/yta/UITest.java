package ru.belokonalexander.yta;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.support.test.annotation.UiThreadTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Method;
import java.util.logging.Logger;

import ru.belokonalexander.yta.GlobalShell.Models.Language;
import ru.belokonalexander.yta.Views.WordList;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressBack;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.pressKey;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasFocus;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.anyOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@RunWith(AndroidJUnit4.class)
public class UITest {

    String typedString = "Тест";

    @Rule
    public ActivityTestRule mActivityRule = new ActivityTestRule<>(
            MainActivity.class);

    @Test
    public void writeTextInInput(){

        onView(withId(R.id.translate_search_input)).perform(click(),replaceText(typedString));

        onView(withId(R.id.translate_search_input)).check(matches(withText(typedString)));
        onView(withId(R.id.translate_search_input)).check(matches(hasFocus()));
    }

    /**
     * CustomEditText теряет фокус после PRESS_BACK, DONE
     */
    @Test
    public void writeTextInInputWithDone(){

        onView(withId(R.id.translate_search_input)).perform(click(),replaceText(typedString),pressBack());
        onView(withId(R.id.translate_search_input)).check(matches(not(hasFocus())));

        onView(withId(R.id.translate_search_input)).perform(click(),replaceText(typedString),pressImeActionButton());
        onView(withId(R.id.translate_search_input)).check(matches(not(hasFocus())));
    }

    @Test
    public void TextViewGetsResult() throws InterruptedException {

        WordList currentWordList = (WordList) mActivityRule.getActivity().findViewById(R.id.word_list);
        onView(withId(R.id.translate_search_input)).perform(click(),replaceText(typedString),pressBack());
        Thread.sleep(300);
        assertTrue(currentWordList.getTranslate()!=null);
    }

    @Test
    public void TextViewThrowsError() throws InterruptedException {

        WordList currentWordList = (WordList) mActivityRule.getActivity().findViewById(R.id.word_list);
        ((ActionFragment)((MainActivity)mActivityRule.getActivity()).fragments[0]).currentLanguage.setTo(new Language("qwerty"));
        onView(withId(R.id.translate_search_input)).perform(click(),replaceText(typedString),pressBack());
        Thread.sleep(1000);
        assertTrue(currentWordList.getLastError()!=null);
    }

    @Test
    public void TextViewThrowsErrorRetry() throws InterruptedException {

        onView(withId(R.id.translate_search_input)).perform(click(),replaceText(typedString));

        new Handler(mActivityRule.getActivity().getMainLooper()).post(() -> ((MainActivity)mActivityRule.getActivity()).mainViewPager.setCurrentItem(2));

        onView(withId(R.id.translate_search_input)).check(matches(not(hasFocus())));

    }



}
