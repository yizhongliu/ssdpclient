package pri.tool.ssdpclient;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.List;

import static pri.tool.ssdpclient.SSDPConstants.NEWLINE;

public class MainActivity extends Activity implements View.OnClickListener {

    private WifiManager.MulticastLock multicastLock;
    private List<String> listReceive = new ArrayList<String>();
    private static final String TAG = "MainActivity";
    private TextView tvReceive;//显示搜寻结果
    private boolean receiveThread = false;
    private SSDPSocket sock = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvReceive = (TextView) findViewById(R.id.tv_show_receive);
        Button btn = (Button) findViewById(R.id.btnSendSSDPSearch);
        btn.setOnClickListener(this);

        startMuReceive();
    }

    @Override
    protected void onStop() {
        super.onStop();
        receiveThread = false;
    }

    @Override
    public void onClick(View v) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SendMSearchMessage();
            }
        }).start();
    }

    /**
     * 获取组锁，使用后记得及时释放，否则会增加耗电。为了省电，Android设备默认关闭
     */

    private void acquireMultiLock() {
        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        multicastLock = wm.createMulticastLock("multicastLock");
        multicastLock.setReferenceCounted(true);
        multicastLock.acquire();//使用后，需要及时关闭
    }

    /**
     * 释放组锁
     */
    private void releaseMultiLock() {
        if (null != multicastLock) {
            multicastLock.release();
        }
    }


    private void SendMSearchMessage() {
        acquireMultiLock();
        SSDPSearchMsg searchMsg = new SSDPSearchMsg(SSDPConstants.IVIEW_DEVICE);
 //       SSDPSocket sock = null;
        try {
            //发送
 //           sock = new SSDPSocket();
            sock.send(searchMsg.toString());
            Log.i(TAG, "要发送的消息为：" + searchMsg.toString());
            //接收
            listReceive.clear();
//            while (true) {
//                DatagramPacket dp = sock.receive(); // Here, I only receive the same packets I initially sent above
//                String c = new String(dp.getData()).trim();
//                String ip = dp.getAddress().toString().trim();
//                Log.e(TAG, "接收到的消息为：\n" + c + "\n来源IP地址：" + ip);
//                //接收时候一遍后，直接跳出循环
//                if (listReceive.contains(c)) break;
//                else listReceive.add(c);
//
//
//            }
//            sock.close();
            releaseMultiLock();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    void startMuReceive() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                receiveThread = true;

                try {
                    //发送
                    sock = new SSDPSocket();
                    //接收
                    listReceive.clear();
                    while (receiveThread) {
                        DatagramPacket dp = sock.receive(); // Here, I only receive the same packets I initially sent above
                        String c = new String(dp.getData()).trim();
                        String ip = dp.getAddress().toString().trim();
                        Log.e(TAG, "接收到的消息为：\n" + c + "\n来源IP地址：" + ip);

                        if (listReceive.contains(c)) {

                        } else {
                            listReceive.add(c);
                        }

                        //显示接收结果
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                StringBuilder sb = new StringBuilder();
                                for (int i = 0; i < listReceive.size(); i++) {
                                    sb.append(i).append("\r\t").append(listReceive.get(i))
                                            .append(NEWLINE).append("-----------------------").append(NEWLINE);
                                }
                                String s = sb.toString();
                                tvReceive.setText(s);
                                Log.d(TAG, "result = " + s);
                            }
                        });
                    }
                    sock.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}