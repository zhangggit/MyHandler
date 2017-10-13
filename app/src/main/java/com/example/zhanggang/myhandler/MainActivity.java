package com.example.zhanggang.myhandler;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import javax.security.auth.login.LoginException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "TAG";
    @BindView(R.id.but1)
    Button but1;
    @BindView(R.id.but2)
    Button but2;
    @BindView(R.id.but3)
    Button but3;
    @BindView(R.id.but4)
    Button but4;
    private String[] title = {"66", "77", "88", "99"};
    Handler handler;  //

    Handler handler2 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;  //获取what值 进行比较
            switch (what) {
                case 1:
                    Log.e(TAG, "从子线程获取的消息：" + msg.obj);
                    break;
                case 2:
                    Log.e(TAG, "从主线程获取的消息：" + msg.obj);
                    break;
                case 4:
                    //获取到Bundle发送过来的数组
                    String[] lists = msg.getData().getStringArray("list");
                    for (String bean : lists) {  //遍历数组
                        Log.e(TAG, "获取Bundle传过来的值：" + bean);
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        but1.setOnClickListener(this);
        but2.setOnClickListener(this);
        but3.setOnClickListener(this);
        but4.setOnClickListener(this);
        new Thread(new MyThread()).start();

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.but1:  //子线程向主线程发送消息
                //开启一个线程 发送一条消息
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        Message message = handler2.obtainMessage();
                        message.what = 1;
                        message.obj = "张刚";
                        handler2.sendMessage(message);
                    }
                }.start();
                break;
            case R.id.but2:
                Message message = Message.obtain(handler2, 2, "张刚2");
                message.sendToTarget();
                break;
            case R.id.but3: //主线程向子线程发送消息
                Message message1 = Message.obtain();
                message1.obj = "张刚3";
                handler.sendMessage(message1);
                break;
            case R.id.but4:
                Message message2 = Message.obtain();
                message2.what = 4;
                Bundle bundle = new Bundle(); //创建一个Bundle 添加一个数组
                bundle.putStringArray("list", title);
                message2.setData(bundle);  //设置对象 并发送
                handler2.sendMessage(message2);
                break;
        }
    }

    public class MyThread implements Runnable {
        //子线程接收主线程的消息
        @Override
        public void run() {
            Looper.prepare();
            handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    Log.e(TAG, "子线程接收主线程的消息：" + msg.obj);
                }
            };
            Looper.loop();
        }
    }

}
