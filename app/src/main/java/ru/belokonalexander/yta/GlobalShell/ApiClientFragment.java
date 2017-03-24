package ru.belokonalexander.yta.GlobalShell;

import android.support.v4.app.Fragment;
import android.support.v4.util.ArraySet;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Alexander on 24.03.2017.
 */

public class ApiClientFragment extends Fragment{

    Set<IApiRequest> requests = new ArraySet<>();

    @Override
    public void onStop() {
        super.onStop();
        for(IApiRequest apiRequest : requests){
            if(apiRequest!=null)
                apiRequest.cancel();
        }
    }

    public void addRequest(IApiRequest request){
        requests.add(request);
    }

}
