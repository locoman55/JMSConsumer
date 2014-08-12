package net.devcat.jmstest;

import javax.jms.*;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import net.devcat.jmschannel.ChannelType;
import net.devcat.jmschannel.JMSInChannel;
import net.devcat.jmschannel.MsgQueue;
import net.devcat.jmschannel.exceptions.JMSChannelException;

public class JMSConsumer {
    public JMSConsumer() {
    }

    private static Connection getConnection(String url) {
        ActiveMQConnectionFactory connectionFactory = 
            new ActiveMQConnectionFactory(url);
        Connection connection = null;
        try {
            connection = connectionFactory.createConnection();
            connection.start();
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
        return connection;
    }

    private static void doUsage() {
        java.lang.System.out.println(
            "USAGE: --type <TOPIC|QUEUE> --name <name>\n");
    }

    public static void main(String[] argv) {
        String name = null;
        int timeout = 0;
        ChannelType type = ChannelType.UNKNOWN;
        MsgQueue msgQueue = new MsgQueue();
        JMSInChannel inChannel = null;
        Connection connection = null;
        TextMessage textMsg = null;
        boolean done = false;

        if (argv.length >= 1) {
            for (int i = 0; i < argv.length; i++) {
                if (argv[i].equals("--help")) {
                    doUsage();
                    System.exit(0);
                } else if (argv[i].equals("--type")) {
                    i++;
                    try {
                        type = ChannelType.valueOf(argv[i].toUpperCase());
                    } catch (IllegalArgumentException e) {
                    	System.out.printf(
                            "Invalid Channel type (%s)\n", argv[i]);
                        System.exit(1);
                    }
                } else if (argv[i].equals("--name")) {
                    i++;
                    name = argv[i];
                } else {
                    System.out.printf(
                        "Invalid command argument (%s)\n", argv[i]);
                    System.exit(-1);
                }
            }
        }

        try {
            connection = getConnection(ActiveMQConnection.DEFAULT_BROKER_URL);
            inChannel = new JMSInChannel(type, name, connection, msgQueue);
        } catch (JMSChannelException e) {
            System.out.printf("%s\n", e.getMessage());
            System.exit(-1);
        }

        Thread t = new Thread(inChannel);
        t.start();
        
        while (!done) {
            Message message = msgQueue.getMsg(timeout);
            try {
                if (message instanceof TextMessage) {
                    textMsg = (TextMessage) message;
                        System.out.printf("MSG: %s\n", textMsg.getText());
                } else if (message instanceof BytesMessage) {
                    UserConsumer consumer = new UserConsumer();
                    consumer.handleMsg((BytesMessage)message);
                }
            } catch (JMSException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        System.exit(0);
    }
}
