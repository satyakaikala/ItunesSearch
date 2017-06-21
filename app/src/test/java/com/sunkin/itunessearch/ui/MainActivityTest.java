package com.sunkin.itunessearch.ui;

import android.content.Intent;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

/**
 * Created by kaika on 5/30/2017.
 */

@RunWith(MockitoJUnitRunner.class)
public class MainActivityTest {
    private MainActivity mainActivity;
    @Mock Intent mockIntent;

    @Before
    public void setUp() throws Exception {
        mainActivity = spy( MainActivity.class);

    }

    @Test
    public void testHandleIntent() {
        doReturn(Intent.ACTION_SEARCH).when(mockIntent).getAction();
       // doNothing().when(mainActivity).getSearchItems(any(String.class), any(String.class));
        mainActivity.handleIntent(mockIntent);
    }
}