package com.nothing.unnamedplayer.Util;

public class musicInfoConverter {

    public musicInfoConverter(){
    }

    public static String durationConvert(int duration){ //tính thời gian file nhạc
        int sec = duration / 1000;
        int min = sec / 60;
        int hr = min / 60;
        String res = "";
        sec = sec % 60;
        //tiếng
        if (hr > 0)
            res += hr + ":";
        //phút
        res += min;
        //giây
        if (sec < 10) {
            res += ":0" + sec;
        } else {
            res +=":" + sec;
        }
        return res;
    }
}
