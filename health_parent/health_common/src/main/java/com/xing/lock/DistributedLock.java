package com.xing.lock;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/***
 * a distributed lock based on Zookeeper
 */
public class DistributedLock {
//    private final ZooKeeper zk;
    private final ZkClient zk;
    private final String lockDir;
    private final String lockName;
    private final String lockTmpNodesDir;
    private String lockPath;
    private IZkChildListener listener;

    public DistributedLock(ZkClient zk, String lockDir, String lockName) {
        this.zk = zk;
        this.lockDir = lockDir;
        this.lockName = lockName;
        this.lockTmpNodesDir = lockDir + "/" + lockName;
    }

    /***
     * lock the process , use ordered Znodes
     * lockPath is the returned value after ZooKeeper creating a tmp and sequential Znode with params -e -s
     */
    public void lock() throws IOException {
//      lockPath = zk.create(lockBasePath + "/" + lockName , null, ZooDefs.Ids.OPEN_ACL_UNSAFE , CreateMode.EPHEMERAL_SEQUENTIAL);

        if(!zk.exists(lockTmpNodesDir)) {
            zk.createPersistent(lockTmpNodesDir, true);
        }
        lockPath = zk.createEphemeralSequential(lockTmpNodesDir + "/node", null);

        // lock used to sync multiple ChildChanges events happened in lockBase
        // in that time, handler will be called several times at the same moment
        final Object lock = new Object();

        // subscribe events childChange
        listener = new IZkChildListener(){
            @Override
            public void handleChildChange(String parentPath, List<String> childrenList) throws Exception {
                synchronized (lock) {
                    lock.notifyAll();
                }
            }
        };
        zk.subscribeChildChanges(lockTmpNodesDir, listener);

        while(true) {
            synchronized (lock) {
                // if watched ZNode's children is changed , get the newest nodes list and make decision
                List<String> nodes = zk.getChildren(lockTmpNodesDir);
                Collections.sort(nodes);
                if(lockPath.endsWith(nodes.get(0))) {
                    // unsubscribe changes before exit the lock loop
                    zk.unsubscribeChildChanges(lockTmpNodesDir, listener);
                    return;
                } else {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    /***
     * unlock the process , delete the corresponding Znode in zookeeper , so the nodes after current process can be handled
     */
    public void unlock() throws IOException {
        zk.delete(lockPath);
    }
}
