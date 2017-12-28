package com.caocao.shardingjdbc.console.common;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * @author liuke1@geely.com
 * @version 1.0
 * @since v1.0 2017/11/13 17:07
 */
@Component
@Slf4j
public class CuratorService {

    private CuratorFramework client;

    @Value("${zk.connectionTimeoutMs:5000}")
    private int connectionTimeoutMs;
    @Value("${zk.sessionTimeoutMs:60000}")
    private int sessionTimeoutMs;

    public Boolean init(String connectString) {
        client = CuratorFrameworkFactory.builder()
                .connectString(connectString)
                .connectionTimeoutMs(connectionTimeoutMs)
                .sessionTimeoutMs(sessionTimeoutMs)
                .canBeReadOnly(false)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();
        return initCuratorClient();
    }

    private Boolean initCuratorClient() {
        client.start();
        try {
            if (!client.blockUntilConnected(connectionTimeoutMs, TimeUnit.MILLISECONDS)) {
                client.close();
                return false;
            }
        } catch (InterruptedException e) {
            log.error("error initCurator client", e);
        }
        return true;
    }


    @PreDestroy
    public void destroy() {
        client.close();
    }

    public String create(String path) throws Exception {
        return createwithMode(path, "", CreateMode.PERSISTENT);
    }

    public String create(String path, String data) throws Exception {
        Assert.notNull(data, "data must be not null");
        return createwithMode(path, data, CreateMode.PERSISTENT);
    }

    public String create(String path, Object data) throws Exception {
        Assert.notNull(data, "data must be not null");
        return createwithMode(path, data, CreateMode.PERSISTENT);
    }

    public String createwithMode(String path, CreateMode createMode) throws Exception {
        return client.create().creatingParentsIfNeeded().withMode(createMode).forPath(path);
    }

    public String createwithMode(String path, String data, CreateMode createMode) throws Exception {
        Assert.notNull(data, "data must be not null");
        return client.create().creatingParentsIfNeeded().withMode(createMode)
                .forPath(path, data.getBytes());
    }

    public String createwithMode(String path, Object data, CreateMode createMode) throws Exception {
        Assert.notNull(data, "data must be not null");
        return client.create().creatingParentsIfNeeded().withMode(createMode)
                .forPath(path, JSON.toJSONBytes(data));
    }

    public Stat update(String path) throws Exception {
        return client.setData().forPath(path);
    }

    public Stat update(String path, String data) throws Exception {
        Assert.notNull(data, "data must be not null");
        return client.setData().forPath(path, data.getBytes());
    }

    public Stat update(String path, Object data) throws Exception {
        Assert.notNull(data, "data must be not null");
        return client.setData().forPath(path, JSON.toJSONBytes(data));
    }

    public void delete(String path) throws Exception {
        client.delete().deletingChildrenIfNeeded().inBackground().forPath(path);
    }

    public String getData(String path) throws Exception {
        return new String(client.getData().forPath(path));
    }

    public <T> T getData(String path, Class<T> valueType) throws Exception {
        return JSON.parseObject(client.getData().forPath(path), valueType);
    }

    public List<String> getChildren(String path) {
        try {
            return client.getChildren().forPath(path);
        } catch (Exception e) {
            //node not exist throw this exception
            return null;
        }
    }

    public boolean isExists(String path) throws Exception {
        return client.checkExists().forPath(path) != null;
    }

    /**
     * 用来监控一个ZNode的子节点. 当一个子节点增加， 更新，删除时， Path Cache会改变它的状态， 会包含最新的子节点， 子节点的数据和状态。
     */
    public PathChildrenCache addListener(String path,
                                         PathChildrenCacheListener pathChildrenCacheListener, Executor executor) throws Exception {
        //设置节点的cache
        PathChildrenCache pathChildrenCache = new PathChildrenCache(client, path, true);
        //设置监听器和处理过程
        pathChildrenCache.getListenable().addListener(pathChildrenCacheListener, executor);
        //开始监听
        pathChildrenCache.start();
        return pathChildrenCache;
    }


    public PathChildrenCache addListener(String path,
                                         PathChildrenCacheListener pathChildrenCacheListener, PathChildrenCache.StartMode startMode,
                                         Executor executor) throws Exception {
        //设置节点的cache
        PathChildrenCache pathChildrenCache = new PathChildrenCache(client, path, true);
        //设置监听器和处理过程
        pathChildrenCache.getListenable().addListener(pathChildrenCacheListener, executor);
        //开始监听
        pathChildrenCache.start(startMode);
        return pathChildrenCache;
    }

    /**
     * 当节点的数据修改或者删除时，Node Cache能更新它的状态包含最新的改变。操作和Path cache类似， 只是getCurrentData()返回的类型不同
     */
    public void addListener(String path, NodeCacheListener nodeCacheListener, Executor executor)
            throws Exception {
        //设置节点的cache
        NodeCache nodeCache = new NodeCache(client, path);
        //设置监听器和处理过程
        nodeCache.getListenable().addListener(nodeCacheListener, executor);
        //开始监听
        nodeCache.start();
    }
}
