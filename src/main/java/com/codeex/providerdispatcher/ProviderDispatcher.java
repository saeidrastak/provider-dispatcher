package com.codeex.providerdispatcher;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProviderDispatcher {

    private static final Logger logger = LoggerFactory.getLogger(ProviderDispatcher.class);

    private boolean started = false;
    private final List<Provider> providers = new ArrayList<>();

    public void addProvider(Provider provider) {
        // Check provider number range overlapping
        for (Provider item : providers) {
            for (Integer providerItem : provider.getItems()) {
                if (item.canProcess(providerItem)) {
                    throw new IllegalArgumentException(
                            String.format("Provider item `%s` overlaps with existing provider `%s`", providerItem,
                                    provider));
                }
            }
        }
        providers.add(provider);
    }

    public void start() {
        if (providers.isEmpty()) {
            throw new IllegalStateException("There is no provider to handle request. Please first add provider!");
        }
        for (Provider provider : providers) {
            if (logger.isDebugEnabled()) {
                logger.debug(provider.toString());
            }
        }
        started = true;
    }

    public void dispatch(int number) throws InterruptedException {
        if (!started) {
            throw new IllegalStateException("Please start the dispatcher first!");
        }

        for (Provider provider : providers) {
            if (provider.canProcess(number)) {
                if (provider.isFull()) {
                    if (provider.isHighPriority()) {
                        provider.killOneOfOngoing();
                        provider.process(number);
                    } else {
                        logger.warn("Dispatching Number `{}` rejected by to Provider `{}` because provider is full!",
                                number, provider.getName());
                    }
                } else {
                    provider.process(number);
                }
            }
        }
    }

    public void waitFinish() throws InterruptedException {
        for (Provider provider : providers) {
            provider.finish();
        }
    }

    public int getProvidersCount() {
        return providers.size();
    }
}