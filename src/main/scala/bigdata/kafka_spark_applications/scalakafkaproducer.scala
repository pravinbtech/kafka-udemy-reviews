package bigdata.kafka_spark_applications
import java.util.Properties
import java.util.Date
import org.apache.kafka.common._;
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.KafkaProducer
import scala.util.Random
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.Callback
import org.apache.kafka.clients.producer.RecordMetadata
import java.text.SimpleDateFormat
object scalakafkaproducer {
       def main(args:Array[String]){
val rnd = new Random()
val prop =new Properties();
val formatter= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
val g=formatter.format(new Date());
prop.put("bootstrap.servers", "127.0.0.1:9092")
prop.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringSerializer")
prop.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringSerializer")
prop.put(ProducerConfig.CLIENT_ID_CONFIG,"Scalaproducer")
prop.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION,1)
val producer= new KafkaProducer[String,String](prop);
val events=List.range(1, 100)
 val callback=new Callback{
 def onCompletion(meta:RecordMetadata, e:Exception):Unit={
 if(e==null)
  println("Topic: "+meta.topic() +"\n"+ "Message:"+meta.timestamp()+"\n"+"Partition:"+meta.partition+"\n")
 else
 e.getStackTrace
   }
}


for (nEvents <- Range(0, 10)) {
val t=System.currentTimeMillis()
val runtime= new Date().getDate
val ip = "192.168.2." + rnd.nextInt(255)
val msg = runtime + "," + nEvents + "," + ip
val data= new ProducerRecord [String,String] ("first_topic",nEvents.toString(),msg)
producer.send(data,callback)
//printf("Sen record"+nEvents.toString()+data.key()+"\n")
                                }

producer.flush()
producer.close()

                              }

}