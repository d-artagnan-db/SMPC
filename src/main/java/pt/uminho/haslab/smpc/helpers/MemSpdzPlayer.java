package pt.uminho.haslab.smpc.helpers;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MemSpdzPlayer {

    private final BlockingQueue<byte[]>[] data;

    public MemSpdzPlayer(){

        data = new BlockingQueue[3];
        data[0] = new LinkedBlockingQueue<byte[]>();
        data[1] = new LinkedBlockingQueue<byte[]>();
        data[2] = new LinkedBlockingQueue<byte[]>();
    }

    public void storeValue(int playerID, byte[] share){
        try {
            this.data[playerID-1].put(share);
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }

    public byte[] receive(int playerID) {
        try {
            return this.data[playerID-1].take();
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }
}
