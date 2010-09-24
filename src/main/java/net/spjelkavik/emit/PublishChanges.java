package net.spjelkavik.emit;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
* User: hennings
* Date: 23.sep.2010
*/
class PublishChanges {
    BlockingQueue<SplitTime> clq = new LinkedBlockingQueue<SplitTime>();
    public void add(SplitTime t) {
        clq.offer(t);
    }

    public BlockingQueue<SplitTime> getQueue() {
        return clq;
    }
}
