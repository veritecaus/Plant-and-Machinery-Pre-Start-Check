package com.mb.prestartcheck;

import java.util.concurrent.ExecutorService;

public class AppExecutorService {

    private static AppExecutorService instance;

    private ExecutorService executorService;

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public  static AppExecutorService getInstance()
    {
        if (instance == null) instance = new AppExecutorService();

        return instance;
    }
}
