package bigdata.kafka_spark_applications
import java.util.Properties
import java.util.Date
import org.apache.kafka.common._;
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.KafkaProducer
import scala.util.Random
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.consumer._;
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.Callback
import org.apache.kafka.clients.producer.RecordMetadata
import java.text.SimpleDateFormat
import scala.collection.JavaConverters._;
import scala.concurrent.duration._;

object scalakafkaconsumer {
       def main(args:Array[String]){
val rnd = new Random()
val prop =new Properties();
val formatter= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
val g=formatter.format(new Date());
prop.put("bootstrap.servers", "127.0.0.1:9092")
prop.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringDeserializer")
prop.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringDeserializer")
prop.put(ConsumerConfig.CLIENT_ID_CONFIG,"Scalaproducer")
prop.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"latest")
prop.put(ConsumerConfig.GROUP_ID_CONFIG,"Seventh_Application")

val consumer= new KafkaConsumer[String,String](prop);
/* val events=List.range(1, 100)
 val callback=new Callback{
 def onCompletion(meta:RecordMetadata, e:Exception):Unit={
 if(e==null)
  println("Topic: "+meta.topic() +"\n"+ "Message:"+meta.timestamp()+"\n"+"Partition:"+meta.partition+"\n")
 else
 e.getStackTrace
   }
} */
val topic=List("first_topic","second_topic");

consumer.subscribe(Seq("first_topic").asJava)

//consumer.subscribe("first_topic")

while(true){
  
  val duration=Duration(1000,"millis")
  val records=consumer.poll(duration.toMillis)
  for(record<-records.asScala){
   println("Topic:"+record.topic+"\n"+
       "Key:"+record.key+"\n"+
       "value:"+record.value);
  }
    
}

       }
}