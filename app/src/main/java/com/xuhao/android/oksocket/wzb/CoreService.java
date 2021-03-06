package com.xuhao.android.oksocket.wzb;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.xuhao.android.libsocket.sdk.ConnectionInfo;
import com.xuhao.android.libsocket.sdk.OkSocketOptions;
import com.xuhao.android.libsocket.sdk.SocketActionAdapter;
import com.xuhao.android.libsocket.sdk.bean.IPulseSendable;
import com.xuhao.android.libsocket.sdk.bean.ISendable;
import com.xuhao.android.libsocket.sdk.bean.OriginalData;
import com.xuhao.android.libsocket.sdk.connection.IConnectionManager;
import com.xuhao.android.libsocket.sdk.connection.NoneReconnect;

import java.nio.charset.Charset;

import static android.widget.Toast.LENGTH_SHORT;
import static com.xuhao.android.libsocket.sdk.OkSocket.open;

/**
 * @author wzb<wangzhibin_x@qq.com>
 * @date 2018-07-02	16:43
 */
public class CoreService extends Service{

    private ConnectionInfo mInfo;
    public static  IConnectionManager mManager;
    private OkSocketOptions mOkOptions;


    private SocketActionAdapter adapter = new SocketActionAdapter() {

        @Override
        public void onSocketConnectionSuccess(Context context, ConnectionInfo info, String action) {
            Log.e("wzb","onSocketConnectionSuccess 连接成功");
        }

        @Override
        public void onSocketDisconnection(Context context, ConnectionInfo info, String action, Exception e) {
            if (e != null) {
                Log.e("wzb","onSocketDisconnection 异常断开"+e.getMessage());
            } else {
                Log.e("wzb","onSocketDisconnection 正常断开");
            }
        }

        @Override
        public void onSocketConnectionFailed(Context context, ConnectionInfo info, String action, Exception e) {
            Toast.makeText(context, "连接失败" + e.getMessage(), LENGTH_SHORT).show();
            Log.e("wzb","onSocketConnectionFailed 连接失败");
        }

        @Override
        public void onSocketReadResponse(Context context, ConnectionInfo info, String action, OriginalData data) {
            super.onSocketReadResponse(context, info, action, data);
            String str = new String(data.getBodyBytes(), Charset.forName("utf-8"));
            //logRece(str);
        }

        @Override
        public void onSocketWriteResponse(Context context, ConnectionInfo info, String action, ISendable data) {
            super.onSocketWriteResponse(context, info, action, data);
            String str = new String(data.parse(), Charset.forName("utf-8"));
            //logSend(str);
        }

        @Override
        public void onPulseSend(Context context, ConnectionInfo info, IPulseSendable data) {
            super.onPulseSend(context, info, data);
            String str = new String(data.parse(), Charset.forName("utf-8"));
            //logSend(str);
        }
    };


    private void initSocket(){
        mInfo = new ConnectionInfo("192.168.16.101", 8282);
        mOkOptions = new OkSocketOptions.Builder()
                .setReconnectionManager(new NoneReconnect())
                .setWritePackageBytes(1024)
                .build();
        mManager = open(mInfo).option(mOkOptions);
        if(mManager !=null) mManager.registerReceiver(adapter);
    }

    private void releaseSocket(){
        if(mManager != null){
            mManager.disconnect();
            mManager.unRegisterReceiver(adapter);
        }
    }

    private void connect(){
        if(mManager == null) return;
        if(!mManager.isConnect()) mManager.connect();
    }

    private void disconnect(){
        if(mManager == null) return;
        if(mManager.isConnect())mManager.disconnect();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initSocket();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseSocket();
    }
}
