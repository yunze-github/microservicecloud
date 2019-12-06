package com.atguigu.myrule;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;

/*
 * 问题：依旧轮询策略，但是现在加上新需求，每个服务器要求被调用5次。也即以前是每个机器一次，现在是每台机器5次
 */
//随机算法
public class RandomRule_YZ extends AbstractLoadBalancerRule {

	/*
	 * 自定义轮询规则 total = 0 直到 total = 5以后，
	 * 我们的指针才能往下走 index = 0 对应提供服务的服务器地址
	 * total达到5以后，需要重新置为0，但是已经达到过一个5次，index向后移动变为1
	 * 
	 * 分析：有问题我们达到3次total=5以后，
	 * 因为我们只有3台提供服务的服务器，需要index重新重0开始
	 */
	private int total = 0;
	private int currentIndex = 0;

	/**
	 * Randomly choose from all living servers
	 */
	// 真正其作用的，选择哪一个提供者微服务
	public Server choose(ILoadBalancer lb, Object key) {
		if (lb == null) {
			return null;
		}
		Server server = null;

		while (server == null) {
			if (Thread.interrupted()) {
				return null;
			}
			// getReachableServers存活的可以对外提供服务的机器
			List<Server> upList = lb.getReachableServers();
			List<Server> allList = lb.getAllServers();

			int serverCount = allList.size();
			if (serverCount == 0) {
				/*
				 * No servers. End regardless of pass, because subsequent passes only get more
				 * restrictive.
				 */
				return null;
			}
			
			// 随机获取所有提供服务微服务的下标
			// 比如说是serverCount：3台机器 对应的index：0 1 2
			//int index = chooseRandomInt(serverCount);
			//server = upList.get(index);
			//private int total = 0;
			//private int currentIndex = 0;

			//自定定义的轮询规则，返回提供服务的服务器
			if(total < 5) {
				server = upList.get(currentIndex);
				total++;
			}else {
				total = 0;
				currentIndex++;
				if(currentIndex >= upList.size()) {
					currentIndex = 0;
				}
			}
			
			if (server == null) {
				/*
				 * The only time this should happen is if the server list were somehow trimmed.
				 * This is a transient condition. Retry after yielding.
				 */
				Thread.yield();
				continue;
			}

			if (server.isAlive()) {
				return (server);
			}

			// Shouldn't actually happen.. but must be transient or a bug.
			server = null;
			Thread.yield();
		}

		return server;

	}

	protected int chooseRandomInt(int serverCount) {
		return ThreadLocalRandom.current().nextInt(serverCount);
	}

	@Override
	public Server choose(Object key) {
		return choose(getLoadBalancer(), key);
	}

	@Override
	public void initWithNiwsConfig(IClientConfig arg0) {
		// TODO Auto-generated method stub

	}

}
