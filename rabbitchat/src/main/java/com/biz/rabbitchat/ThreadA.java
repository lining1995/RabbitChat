package com.biz.rabbitchat;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class ThreadA extends Thread{
	private String message;
	public ThreadA(String message){
		this.message=message;
	}
	public void run(){
		Connection connection = null;
		Channel channel = null;
		try {
			ConnectionFactory factory = new ConnectionFactory();
			factory.setHost("localhost");
			factory.setPort(5672);
			factory.setUsername("rabbitmq_consumerA");
			factory.setPassword("123456");
			factory.setVirtualHost("test_host");

			// 创建与RabbitMQ服务器的TCP连接
			connection = factory.newConnection();
			channel = connection.createChannel();
			channel.exchangeDeclare("fanoutExchange", "fanout",false, true, null);		
			System.out.println("consumerA Send Message is:'" + message + "'");
			//消息标记化
			message=message+"22222";
			channel.basicPublish("fanoutExchange", "", null, message.getBytes());
			
			
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (channel != null) {
				try {
					channel.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (TimeoutException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (connection != null) {
				try {
					connection.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
