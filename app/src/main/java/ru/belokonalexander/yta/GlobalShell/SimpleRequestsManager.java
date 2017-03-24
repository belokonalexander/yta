package ru.belokonalexander.yta.GlobalShell;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Alexander on 24.03.2017.
 */

public class SimpleRequestsManager implements ApiClientManager {

    private Set<IApiRequest> commands = new HashSet<>();

    @Override
    public void addRequest(IApiRequest request) {
        commands.add(request);
    }

    @Override
    public void clear() {
        for(IApiRequest request : commands){
            if(request!=null)
                request.cancel();
        }
    }
}
