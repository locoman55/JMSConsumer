JMSConsumer
===========

Simple consumer of JMS messages (Topics or Queues) that makes use of JMSChannel.

JMSConsumer has dependencies on the JMSChannel and AvroDemo projects. The JMSProducer project is a companion project and is needed to send messages for the JMSConsumer to consume. The messages can be text (the default) or they can be the User object that is serialized / deserialized using Avro and sent as a byte array (BytesMessage).
