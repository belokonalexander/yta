package ru.belokonalexander.yta.GlobalShell;


import org.greenrobot.eventbus.EventBus;

import java.util.Date;

import ru.belokonalexander.yta.Database.CompositeTranslateModel;
import ru.belokonalexander.yta.Events.WordSavedInHistoryEvent;
import ru.belokonalexander.yta.GlobalShell.Models.TranslateLanguage;
import ru.belokonalexander.yta.Views.Helpers.OutputText;

/**
 * объект, который сохраняет результат в историю перевода
 * зачем это нужно -> пользователь подтверждает, что хочет сохранить слово в переводе (например событие DONE на клавиатуре)
 * однако, мы не можем сразу же сохранить слово по этому событию, т.к не факт, что ответ от свервера успел прийти
 * Поэтому мы создаем намерение сохранить слово, и по этому намерению проверяется каждый ответ свервера
 */
public class HistorySaver {


    /**
     * намерение сохранить слово
     */
    private IntentSaver intentSaver;

    //экземпляр задачи для отложенного сохранения, например, когда овтет уже был получен, но намерения сохранить не было
    private SimpleAsyncTask delayedHistorySaveTask;

    //последний результат
    private CompositeTranslateModel lastResult;


    private class IntentSaver {
        final String text;
        final TranslateLanguage lang;

        IntentSaver(String text, TranslateLanguage lang) {
            this.text = text;
            this.lang = TranslateLanguage.cloneFabric(lang);
        }
    }



    /**
     * создаем намерение, что хотим сохранить следующей операцией данное слово
     * @param input
     * @param lang
     */
    public boolean setIntentSaver(String input, TranslateLanguage lang){
        intentSaver = new IntentSaver(input,lang);
        if(lastResult!=null && compareIntent(lastResult)) {
            return (saveHistoryWord(lastResult, OutputText.Type.AUTOLOAD));
        }
        return false;
    }



    public boolean delayedSavingWord(CompositeTranslateModel compositeTranslateModel, OutputText.Type type) {

        delayedHistorySaveTask = SimpleAsyncTask.create(new SimpleAsyncTask.InBackground<Void>() {
            @Override
            public Void doInBackground() {

                compositeTranslateModel.setHistory(true);
                compositeTranslateModel.setSaveHistoryDate(new Date());
                compositeTranslateModel.save();
                EventBus.getDefault().post(new WordSavedInHistoryEvent(compositeTranslateModel));

                //очищаем все данные
                clearState();

                return null;
            }
        });

        //пробуем сохранить слово
        return (saveHistoryWord(compositeTranslateModel, type));


    }

    /**
     * сохраняем результат
     * @return
     */
    private boolean saveHistoryWord(CompositeTranslateModel compositeTranslateModel, OutputText.Type type){

        if(type==OutputText.Type.AUTOLOAD || compareIntent(compositeTranslateModel)) {

            return (pushLast());

        } else {
            //сохраняем последний результат, возможно мы подтвердим сохранение позже
            lastResult = compositeTranslateModel;
        }
        return false;
    }



    /**
     * сравнивает, совпадает ли ответ свервера с намерением
     * @param model
     * @return
     */
    private boolean compareIntent(CompositeTranslateModel model){

        if(intentSaver==null){
            return false;
        }

        TranslateLanguage lang = model.getLang();
        String inputText = model.getSource();

        return inputText.equals(intentSaver.text) && lang.equals(intentSaver.lang);

    }

    private void clearState(){
        intentSaver = null;
        lastResult = null;
        delayedHistorySaveTask = null;
    }

    /**
     * сохранить последнее состояние
     */
    public boolean pushLast(){
        if (delayedHistorySaveTask != null && !delayedHistorySaveTask.isExecuted()) {
            delayedHistorySaveTask.execute();
            return true;
        }

        return false;
    }

}
