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
import com.rabbitmq.client.QueueingConsumer;

public class ConsumerA {
	public static void main(String[] args) throws IOException, TimeoutException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		factory.setPort(5672);
		factory.setUsername("rabbitmq_consumerA");
		factory.setPassword("123456");
		factory.setVirtualHost("test_host");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();
		//参数1：队列名称
        //参数2：为true时server重启队列不会消失
        //参数3：队列是否是独占的，如果为true只能被一个connection使用，其他连接建立时会抛出异常
        //参数4：队列不再使用时是否自动删除（没有连接，并且没有未处理的消息)
        //参数5：建立队列时的其他参数
		channel.queueDeclare("fanoutQueue1", true, false, false, null);
		//绑定交换机
		channel.queueBind("fanoutQueue1", "fanoutExchange", "");
		Consumer consumer = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
					byte[] body) throws IOException {
				String message = new String(body, "UTF-8");
				String real_message="";
				/*
				 * 解析消息，如果是producer就回复一下，如果是其他人就 打印出来，如果是自己的就不打印也不回复。
				 */
				if(message.indexOf("11111")>(-1)){
					real_message=message.substring(0,message.length()-5);
					System.out.println(" consumerA have received '" + real_message + "'");
					String sendmessage="大家好，我是consumerA，我刚刚收到了"+real_message;
					Thread thread=new ThreadA(sendmessage);
					thread.start();
				}else if(message.indexOf("33333")>(-1)){
					real_message=message.substring(0,message.length()-5);
					System.out.println(" consumerA have received '" + real_message + "'");
				}
				
			}
		};
		// 参数1:队列名称
		// 参数2：是否发送ack包，不发送ack消息会持续在服务端保存，直到收到ack。 可以通过channel.basicAck手动回复ack
		// 参数3：消费者
		channel.basicConsume("fanoutQueue1", true, consumer);
	}
}
