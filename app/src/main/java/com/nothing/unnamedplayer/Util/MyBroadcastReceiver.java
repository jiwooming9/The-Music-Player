package com.nothing.unnamedplayer.Util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.nothing.unnamedplayer.PlayerService;
import com.nothing.unnamedplayer.Util.Actions;

public class MyBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        // Lấy ACTION của noti
        if (action.equals(Actions.ACTION_RESUME) ||
                action.equals(Actions.ACTION_END) ||
                action.equals(Actions.ACTION_NEXT) ||
                action.equals(Actions.ACTION_PLAY) ||
                action.equals(Actions.ACTION_PAUSE) ||
                action.equals(Actions.ACTION_PREV)) {
            Intent serviceIntent = new Intent(context, PlayerService.class);
            serviceIntent.setAction(action);
            context.startService(serviceIntent);
        }
        // dừng khi rút tai nghe, gọi.. đéo biết có đc ko
        else if (action.equals("android.bb")){

        }
    }
}
