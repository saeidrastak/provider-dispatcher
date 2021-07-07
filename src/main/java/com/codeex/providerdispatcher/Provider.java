package com.codeex.providerdispatcher;

import com.google.common.util.concurrent.RateLimiter;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Data
public class Provider {

    private static final Logger logger = LoggerFactory.getLogger(Provider.class);

    private final String name;
    private final boolean highPriority;
    private final List<Integer> items;
    private final int processRate;
    private final BlockingQueue<RequestHandler> workQueue;
    private final RateLimiter rateLimiter;
    private int killProcessCnt = 0;

    public Provider(String name, boolean highPriority, List<Integer> items, int processRate) {
        this.name = name;
        this.highPriority = highPriority;
        this.items = items;
        this.processRate = processRate;
        this.rateLimiter = RateLimiter.create(processRate);
        workQueue = new ArrayBlockingQueue<>(processRate);
    }

    public void process(int number) throws InterruptedException {
        this.rateLimiter.acquire();
        RequestHandler requestHandler = new RequestHandler(name, number, workQueue);
        workQueue.put(requestHandler);
        requestHandler.start();
    }

    public void killOneOfOngoing() {
        for (RequestHandler requestHandler : workQueue) {
            if (requestHandler.isAlive()) {
                requestHandler.interrupt();
                workQueue.remove(requestHandler);
                killProcessCnt++;
                logger.info("Progress of provider `{}` for Number `{}` is canceled!", name, requestHandler);
                break;
            }
        }
    }

    public boolean canProcess(int number) {
        return items.contains(number);
    }

    public boolean isFull() {
        boolean full = workQueue.remainingCapacity() == 0;
        if (full) {
            logger.warn("Provider `{}` is full!", name);
        }
        return full;
    }

    public void finish() throws InterruptedException {
        for (Thread thread : workQueue) {
            if (thread.isAlive()) {
                thread.join();
            }
        }
    }

    public int getKillProcessCnt() {
        return killProcessCnt;
    }
}