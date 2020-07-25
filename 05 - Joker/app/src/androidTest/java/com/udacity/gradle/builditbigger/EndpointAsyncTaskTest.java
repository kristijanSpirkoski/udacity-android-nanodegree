package com.udacity.gradle.builditbigger;

import android.app.Application;
import android.test.AndroidTestCase;
import android.test.ApplicationTestCase;

import org.junit.Test;

import java.util.concurrent.ExecutionException;

public class EndpointAsyncTaskTest extends AndroidTestCase {


    public void testVerifyTask() {
        EndpointsAsyncTask task = new EndpointsAsyncTask(getContext());
        task.execute();
        String result = null;
        try {
            result = task.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        assertNotNull(result);
    }
}
