package net.devcat.jmstest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.jms.*;
 
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.util.ByteBufferInputStream;
import org.apache.commons.io.IOUtils;

import net.devcat.jmschannel.JMSOutChannel;
import net.devcat.avro.model.User;

public class UserConsumer {
    private final SpecificDatumReader<User> avroUserReader = 
        new SpecificDatumReader<User>(User.SCHEMA$);
    private final DecoderFactory avroDecoderFactory = DecoderFactory.get();


    public UserConsumer() {

    }

    public void handleMsg(BytesMessage msg) {
        BinaryDecoder binaryDecoder = null;
        User user = null;
        try {
            if (msg.getBodyLength() > 0) {
                byte[] byteArray = new byte[(int)msg.getBodyLength()];
            	msg.readBytes(byteArray);
            	ByteArrayInputStream stream = 
                    new ByteArrayInputStream(byteArray);
            	binaryDecoder = avroDecoderFactory.binaryDecoder(stream, 
                    binaryDecoder);
            	user = avroUserReader.read(user, binaryDecoder);
            	System.out.println("firstName: " + user.getFirstName());
            	System.out.println("lastName: " + user.getLastName());
            	System.out.println("City: " + user.getCity());
            	System.out.println("State: " + user.getState());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error processing Avro message", e);
        }
    }
}
