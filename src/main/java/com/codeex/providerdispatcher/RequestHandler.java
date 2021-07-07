package com.codeex.providerdispatcher;

import java.util.concurrent.BlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    private final String providerName;
    private final int number;
    private final BlockingQueue<RequestHandler> wq;

    public RequestHandler(String providerName, int number, BlockingQueue<RequestHandler> wq) {
        this.providerName = providerName;
        this.number = number;
        this.wq = wq;
    }

    @Override
    public void run() {
        try {
            processInternal(number);
            wq.remove(this);
        } catch (InterruptedException e) {
            logger.warn("Process for Number `{}` interrupted!", number);
        }
    }

    private void processInternal(int number) throws InterruptedException {
        logger.info("Provider `{}` start processing of Number `{}` ", providerName, number);
        Thread.sleep(2000);
        logger.info("Provider '{}' proceed Number `{}`", providerName, number);
    }

    @Override
    public String toString() {
        return "RequestHandler{" +
                "number=" + number +
                '}';
    }
}