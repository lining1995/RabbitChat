package com.biz.rabbitchat;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class ThreadP extends Thread{
	private String message;
	public ThreadP(String message){
		this.message=message;
	}
	public void run(){
		Connection connection = null;
		Channel channel = null;
		try {
			ConnectionFactory factory = new ConnectionFactory();
			factory.setHost("localhost");
			factory.setPort(5672);
			factory.setUsername("rabbitmq_producer");
			factory.setPassword("123456");
			factory.setVirtualHost("test_host");

			// 创建与RabbitMQ服务器的TCP连接
			connection = factory.newConnection();
			channel = connection.createChannel();
			//新建一个fanout交换机
			channel.exchangeDeclare("fanoutExchange", "fanout",false, true, null);		
			System.out.println("Send Message is:'" + message + "'");
			//把消息标记化，用于区分是谁发的
			message=message+"11111";
			//发送消息到该交换机
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
