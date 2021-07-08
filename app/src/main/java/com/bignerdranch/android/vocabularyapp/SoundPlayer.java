package com.bignerdranch.android.vocabularyapp;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

public class SoundPlayer {

    private AudioAttributes mAudioAttributes;
    final int SOUND_POOL_MAX = 2;
    private static SoundPool soundPool;
    private static int rightSound;
    private static int wrongSound;
    private static int countdownTime;

    public  SoundPlayer(Context context){

        //SoundPool is deprecated in API level 21 (.lolipop)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            mAudioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();

            soundPool = new SoundPool.Builder()
                    .setAudioAttributes(mAudioAttributes)
                    .setMaxStreams(SOUND_POOL_MAX)
                    .build();
        }else {
            //SoundPool (int maxStreams, int streamType, int srcQuality)
            soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC,0);
        }


        rightSound = soundPool.load(context, R.raw.right,1);
        wrongSound = soundPool.load(context,R.raw.wrong,1);

    }

    public void playRightSound(){

        //play(int soundID, float leftVolume, float rightVolume, int priority, int loop, float rate)
        soundPool.play(rightSound, 1.0f, 1.0f,1,0, 1.0f );
    }

    public void playWrongSound(){
        //play(int soundID, float leftVolume, float rightVolume, int priority, int loop, float rate)
        soundPool.play(wrongSound, 1.0f, 1.0f,1,0, 1.0f );
    }

    public void playCountdownTimeSound(){
        soundPool.play(countdownTime, 1.0f, 1.0f,1,0, 1.0f );
    }
}
