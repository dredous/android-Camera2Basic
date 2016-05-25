package com.example.android.camera2basic;

import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

/**
 * Created by Edwin Kurniawan on 5/23/2016.
 */
public class SocketCommunication {

    private Socket ioSocket;
    private String count = "";

    public SocketCommunication(String url) {
        try {
            ioSocket = IO.socket(url);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        ioSocket.connect();
        ioSocket.on("countNum", onNewMessage);
    }

    public void destroySocket() {
        if(ioSocket.connected()){
            ioSocket.disconnect();
            ioSocket.close();
        }
    }

    public void sendMessage(String messageTag) {
        if(messageTag == "countImg") {
            ioSocket.emit(messageTag);
        }
    }

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Integer data = (Integer) args[0];
            count = data.toString();
            Log.i("Socket IO", "Message Received: " + count + " inputs");
        }
    };

}
