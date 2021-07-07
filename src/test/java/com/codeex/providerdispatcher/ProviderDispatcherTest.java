package com.codeex.providerdispatcher;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class ProviderDispatcherTest {

    @Test
    public void testProvider() throws InterruptedException {
        // Init providers and dispatcher
        Provider provider1 = new Provider("Provider1", true, range(1, 10), 5);
        for (Integer number : range(1, 10)) {
            assertTrue(provider1.canProcess(number));
        }
        assertFalse(provider1.canProcess(11));

        Provider provider2 = new Provider("Provider2", false, range(11, 20), 4);

        ProviderDispatcher providerDispatcher = new ProviderDispatcher();
        providerDispatcher.addProvider(provider1);
        providerDispatcher.addProvider(provider2);

        assertEquals(2, providerDispatcher.getProvidersCount());

        // Start dispatcher to accept request calls
        providerDispatcher.start();

        // Send random number as request to dispatcher
        // We select random number in range of provider 1
        providerDispatcher.dispatch(1);
        providerDispatcher.dispatch(2);
        providerDispatcher.dispatch(3);
        providerDispatcher.dispatch(4);
        assertFalse(provider1.isFull());
        providerDispatcher.dispatch(5);
        assertTrue(provider1.isHighPriority());
        assertTrue(provider1.isFull());
        assertEquals(0, provider1.getKillProcessCnt());
        // Dispatch a new number to provider 1 and check it kills one of its ongoing process
        providerDispatcher.dispatch(6);
        assertEquals(1, provider1.getKillProcessCnt());

        // Dispatch request to an non-priority provider
        providerDispatcher.dispatch(11);
        providerDispatcher.dispatch(12);
        providerDispatcher.dispatch(13);
        assertFalse(provider2.isFull());
        providerDispatcher.dispatch(14);
        assertTrue(provider2.isFull());
        assertEquals(0, provider2.getKillProcessCnt());
        // Dispatch a new number to provider 2 and check it rejects request
        providerDispatcher.dispatch(15);
        assertEquals(0, provider2.getKillProcessCnt());

        providerDispatcher.waitFinish();
    }

    @Test(expected = IllegalStateException.class)
    public void testNotStartedDispatcherState() throws InterruptedException {
        ProviderDispatcher providerDispatcher = new ProviderDispatcher();
        providerDispatcher.dispatch(1);
    }

    @Test(expected = IllegalStateException.class)
    public void testDispatcherEmptyProviderState() {
        ProviderDispatcher providerDispatcher = new ProviderDispatcher();
        providerDispatcher.start();
    }

    private List<Integer> range(int min, int max) {
        List<Integer> list = new ArrayList<>();
        for (int i = min; i <= max; i++) {
            list.add(i);
        }
        return list;
    }
}