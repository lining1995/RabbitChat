package com.biz.rabbitchat;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class Producer {
	public static void main(String[] args) throws IOException, TimeoutException {
		Producer producer = new Producer();
		Scanner scanner = new Scanner(System.in);
		String name = "";
		String message_send = "";
		boolean exit = true;
		Thread threadP;
		
		System.out.println("你是谁？");
		name = scanner.nextLine();
		// 发送名字
		threadP = new ThreadP(name);
		threadP.start();
		//初始化，监听交换器上的消息。
		producer.init();
		while (exit) {

			System.out.println("按1结束，输入其他继续聊天？");
			message_send = scanner.nextLine();
			if (message_send.equals("1")) {
				exit = false;
			} else {
				//发送消息
				threadP = new ThreadP(message_send);
				threadP.start();
			}
		}
	}

	public void init() throws IOException, TimeoutException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		factory.setPort(5672);
		factory.setUsername("rabbitmq_producer");
		factory.setPassword("123456");
		factory.setVirtualHost("test_host");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();
		// 参数1：队列名称
		// 参数2：为true时server重启队列不会消失
		// 参数3：队列是否是独占的，如果为true只能被一个connection使用，其他连接建立时会抛出异常
		// 参数4：队列不再使用时是否自动删除（没有连接，并且没有未处理的消息)
		// 参数5：建立队列时的其他参数
		channel.exchangeDeclare("fanoutExchange", "fanout",false, true, null);
		channel.queueDeclare("fanoutQueueP", true, false, false, null);
		//绑定交换机
		channel.queueBind("fanoutQueueP", "fanoutExchange", "");
		Consumer consumer = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
					byte[] body) throws IOException {
				String message = new String(body, "UTF-8");
				String real_message = "";
				//解析消息，判断是不是自己发的消息，如果是就不打印了。
				if (message.indexOf("11111") > (-1)) {

				} else {
					//打印消息
					real_message = message.substring(0, message.length() - 5);
					System.out.println(real_message);
				}

			}
		};
		// 参数1:队列名称
		// 参数2：是否发送ack包，不发送ack消息会持续在服务端保存，直到收到ack。 可以通过channel.basicAck手动回复ack
		// 参数3：消费者
		channel.basicConsume("fanoutQueueP", true, consumer);
	}
}

