package com.oakzmm.circlewaveprogress;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSeekBar;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.oakzmm.library.cwp.CircleWaveProgress;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.progress)
    AppCompatSeekBar mSeekBar;
    @Bind(R.id.border_with)
    AppCompatSeekBar borderWith;
    @Bind(R.id.border_color)
    RadioGroup borderColor;
    @Bind(R.id.front_wave_color)
    RadioGroup frontWaveColor;
    @Bind(R.id.behind_wave_color)
    RadioGroup behindWaveColor;
    @Bind(R.id.circleWaveProgress)
    CircleWaveProgress circleWaveProgress;
    @Bind(R.id.text_border_with)
    TextView textBorderWith;
    private BitHandler mBitHandler;
    private boolean flag = false;
    private int mProgress = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mBitHandler = new BitHandler();
        circleWaveProgress.setWaveRun(true);
        circleWaveProgress.setMax(100);
        mProgress = circleWaveProgress.getProgress();
        circleWaveProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new myThread().start();
            }
        });
        mSeekBar.setMax(100);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                flag=false;
                mProgress = progress;
                circleWaveProgress.setProgress(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        borderWith.setMax(10);
        borderWith.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                circleWaveProgress.setBorderWith(progress);
                textBorderWith.setText("BorderWith   " + progress);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        borderColor.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.group1_red:
                        circleWaveProgress.setBorderColor(Color.RED);
                        break;
                    case R.id.group1_green:
                        circleWaveProgress.setBorderColor(Color.GREEN);
                        break;
                    case R.id.group1_blue:
                        circleWaveProgress.setBorderColor(Color.BLUE);
                        break;
                    default:
                        break;
                }
            }
        });
        frontWaveColor.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.group2_red:
                        circleWaveProgress.setFrontWaveColor(Color.RED);
                        break;
                    case R.id.group2_green:
                        circleWaveProgress.setFrontWaveColor(Color.GREEN);
                        break;
                    case R.id.group2_blue:
                        circleWaveProgress.setFrontWaveColor(Color.BLUE);
                        break;
                    default:
                        break;
                }
            }
        });
        behindWaveColor.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.group3_red:
                        circleWaveProgress.setBehindWaveColor(Color.RED);
                        break;
                    case R.id.group3_green:
                        circleWaveProgress.setBehindWaveColor(Color.GREEN);
                        break;
                    case R.id.group3_blue:
                        circleWaveProgress.setBehindWaveColor(Color.BLUE);
                        break;
                    default:
                        break;
                }
            }
        });

    }

    class BitHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            circleWaveProgress.setProgress(mProgress);
            if (mProgress==100)
                flag = true;
        }
    }
    private class myThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (!flag) {
                // 重绘
                mProgress++;
                mBitHandler.sendEmptyMessage(0);
                try {
                    synchronized (this) {
                        Thread.sleep(100);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
