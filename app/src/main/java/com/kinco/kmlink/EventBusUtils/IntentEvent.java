package com.kinco.kmlink.EventBusUtils;

import android.content.Intent;

public class IntentEvent {
    Intent intent;

    public IntentEvent(Intent intent) {
        this.intent = intent;
    }

    public Intent getIntent() {
        return intent;
    }

    public void setIntent(Intent intent) {
        this.intent = intent;
    }

}
