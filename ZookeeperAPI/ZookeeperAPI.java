package com.study.ZookeeperAPI;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.Test;
import org.apache.zookeeper.CreateMode;

public class ZookeeperAPI{
	@Test
	public void createZnode() throws Exception {	//创建节点
		//1.定制一个重试策略		参数：重试的间隔时间（毫秒），重试的最大次数
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 1);
		
		//2.获取一个客户端对象	参数：服务器列表，会话超时时间，链接超时时间，重试策略
		String connStr = "localhost:2181";
		CuratorFramework client = CuratorFrameworkFactory.newClient(connStr, 8000, 8000, retryPolicy);
		
		//3.开启客户端
		client.start();
		
		//4.创建节点
		client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath("/hello", "world".getBytes());
		//在根目录下创建一个hello的永久节点，内容为world   修改withMode参数即可创建其他类型
		//Thread.sleep(5000);
		
		//5.关闭客户端，  关闭客户端会删除创建的临时节点
		client.close();
	}
	
	@Test
	public void setZnode() throws Exception {	//修改节点数据
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 1);
		
		CuratorFramework client = CuratorFrameworkFactory.newClient("localhost:2181", 8000, 8000, retryPolicy);
		
		client.start();
		
		//4.修改节点数据
		client.setData().forPath("/hello", "zookeeper".getBytes());
		
		client.close();
	}
	
	@Test
	public void deleteZnode() throws Exception {	//删除节点
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 1);
		
		CuratorFramework client = CuratorFrameworkFactory.newClient("localhost:2181", 8000, 8000, retryPolicy);
		
		client.start();
		
		//4.删除节点
		client.delete().forPath("/hello"， -1);	//需要指定版本号，如果为-1则不会检查版本，之家删除
		
		client.close();
	}	

	@Test
	public void getZnodeData() throws Exception {	//查看节点数据
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 1);
		
		CuratorFramework client = CuratorFrameworkFactory.newClient("localhost:2181", 8000, 8000, retryPolicy);
		
		client.start();
		
		//4.获取节点数据
		byte[] bytes = client.getData().forPath("/hello");
		
		System.out.println(new String(bytes));
		
		client.close();
	}
	
	@Test
	public void watchZnode() throws Exception {	//创建wacth机制
		//1.定制重试策略
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(3000, 1);
		
		//2.获取客户端
		CuratorFramework client = CuratorFrameworkFactory.newClient("localhost:2181", 8000, 8000, retryPolicy);
	
		//3.启动客户端
		client.start();
		
		//4.创建一个TreeCache对象，指定要监控的节点路径
		TreeCache treeCache = new TreeCache(client, "/hello2");	//让节点跟treecache建立映射关系
		
		//5.自定义一个监听器
		treeCache.getListenable().addListener(new TreeCacheListener() {
			public void childEvent(CuratorFramework client, TreeCacheEvent event) throws Exception {
				ChildData data = event.getData();	//获取数据 判断监听器是否触发
				if(data != null) {
					switch (event.getType()) {
						case NODE_ADDED:
							System.out.println("监测到有新增节点！");
							break;
						case NODE_UPDATED:
							System.out.println("监测到有节点被移除！");		//delete会提示更新节点，rmr path为移除节点
							break;
						case NODE_REMOVED:
							System.out.println("监测到有节点被更新！");
							break;
						
					}
				}
			}
		});
		
		//开始监听
		treeCache.start();
		
		Thread.sleep(10000000);	//休眠线程
		
		//关闭客户端
		client.close();
	}
}
