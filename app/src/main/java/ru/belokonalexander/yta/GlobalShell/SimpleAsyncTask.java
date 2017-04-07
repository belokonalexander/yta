package ru.belokonalexander.yta.GlobalShell;

import android.os.AsyncTask;

/**
 * враппер для упрощенного испоьзования AsyncTask
 * @param <T>
 */
public class SimpleAsyncTask<T> {

    private AsyncTask<Void,Void,T> backgroundTaskWrapper;

    private InBackground<T> inBackgroundTask;
    private PostExecute<T> postExecute;



    private SimpleAsyncTask(InBackground<T> bt, PostExecute<T> pe) {
        this.inBackgroundTask = bt;
        this.postExecute = pe;

        backgroundTaskWrapper = new AsyncTask<Void, Void, T>() {
            @Override
            protected T doInBackground(Void... params) {


                if(inBackgroundTask!=null)
                    return inBackgroundTask.doInBackground();

                return null;
            }

            @Override
            protected void onPostExecute(T result) {
                super.onPostExecute(result);
                if(postExecute!=null)
                    postExecute.doPostExecute(result);
            }
        };
    }

    public boolean isExecuted() {
        return backgroundTaskWrapper.getStatus()!= AsyncTask.Status.PENDING;
    }

    public static<S> SimpleAsyncTask create(InBackground<S> inBackgroundTask, PostExecute<S> postExecute){
        return new SimpleAsyncTask<S>(inBackgroundTask, postExecute);
    }

    public static<S> SimpleAsyncTask create(InBackground<S> inBackgroundTask){
        return new SimpleAsyncTask<S>(inBackgroundTask, null);
    }

    public static<S> SimpleAsyncTask run(InBackground<S> inBackgroundTask, PostExecute<S> postExecute){
        SimpleAsyncTask s = new SimpleAsyncTask<S>(inBackgroundTask, postExecute);
        s.execute();
        return s;
    }

    public static<S> SimpleAsyncTask run(InBackground<S> inBackgroundTask){
        SimpleAsyncTask s = new SimpleAsyncTask<S>(inBackgroundTask, null);
        s.execute();
        return s;
    }




    public void execute(){
        backgroundTaskWrapper.execute();
    }

    public interface InBackground<T>{
        T doInBackground();
    }

    public interface PostExecute<T>{
        void doPostExecute(T result);
    }

    public void setPostExecute(PostExecute<T> postExecute) {
        this.postExecute = postExecute;
    }

    public PostExecute<T> getPostExecute() {
        return postExecute;
    }
}
