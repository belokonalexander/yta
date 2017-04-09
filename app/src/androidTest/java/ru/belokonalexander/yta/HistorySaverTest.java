package ru.belokonalexander.yta;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import ru.belokonalexander.yta.Database.CompositeTranslateModel;
import ru.belokonalexander.yta.GlobalShell.HistorySaver;
import ru.belokonalexander.yta.Views.Helpers.OutputText;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


@RunWith(AndroidJUnit4.class)
public class HistorySaverTest {

    @Test
    public void is_AutoSavedCorrect(){
        HistorySaver historySaver = new HistorySaver();

        assertEquals(true, historySaver.delayedSavingWord(CompositeTranslateModel.getDummyInstance(), OutputText.Type.AUTOLOAD));
        assertEquals(historySaver.pushLast(),false);
        assertEquals(historySaver.delayedSavingWord(CompositeTranslateModel.getDummyInstance(), OutputText.Type.HANDWRITTEN), false);

    }

    @Test
    public void is_SaveIntentAfterCorrect() throws Exception {
        HistorySaver historySaver = new HistorySaver();

        CompositeTranslateModel model = CompositeTranslateModel.getDummyInstance();

        boolean result1 = historySaver.delayedSavingWord(model, OutputText.Type.HANDWRITTEN);

        boolean result2 = historySaver.setIntentSaver(model.getSource(),model.getLang());

        boolean result3 = historySaver.setIntentSaver(model.getSource(),model.getLang());

        assertTrue(result2);
        assertFalse(result3);
        assertFalse(result1);

    }

    @Test
    public void is_SaveIntentBeforeCorrect() throws Exception {
        HistorySaver historySaver = new HistorySaver();

        CompositeTranslateModel model = CompositeTranslateModel.getDummyInstance();

        boolean result1 = historySaver.setIntentSaver(model.getSource(),model.getLang());
        boolean result2 = historySaver.delayedSavingWord(model, OutputText.Type.HANDWRITTEN);
        boolean result3 = historySaver.setIntentSaver(model.getSource(),model.getLang());
        boolean result4 = historySaver.delayedSavingWord(model, OutputText.Type.HANDWRITTEN);

        assertTrue(result2);
        assertFalse(result1);
        assertFalse(result3);
        assertTrue(result4);
    }

    @Test
    public void is_WrongIntentCorrect() throws Exception {
        HistorySaver historySaver = new HistorySaver();

        CompositeTranslateModel model = CompositeTranslateModel.getDummyInstance();


        boolean result1 = historySaver.delayedSavingWord(model, OutputText.Type.HANDWRITTEN);
        boolean result2 = historySaver.setIntentSaver(model.getSource()+1,model.getLang());
        boolean result3 = historySaver.setIntentSaver(model.getSource(),model.getLang());


        assertFalse(result1);
        assertFalse(result2);
        assertTrue(result3);

    }

}
