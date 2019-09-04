package kafka_favourite_colour

import java.lang
import java.util.Properties
import java.time.Duration

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.kstream.{KStream, KTable, ValueMapper}
import org.apache.kafka.streams.{KafkaStreams, KeyValue, StreamsBuilder, StreamsConfig}



object favcolor extends App {

  val config: Properties = {
    val p = new Properties()
    p.put(StreamsConfig.APPLICATION_ID_CONFIG, "favourite-color-input")
    val bootstrapServers = "localhost:9092"
    p.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
    p.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass)
    p.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass)
    p.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
    p
  }

  val builder: StreamsBuilder = new StreamsBuilder

  val textlines: KStream[String, String] = builder.stream[String, String]("fav-color-input")

  val userandcolor: KStream[String, String] = textlines.filter((K: String, V: String) => V.contains(","))
    .selectKey[String]((K: String, V: String) => V.split(",")(0).toLowerCase)
    .mapValues[String](new ValueMapper[String,String] {
      override def apply(V:String):String= {
        V.split(",")(1).toLowerCase()
      }
    })
   .filter((K:String,V:String)=>List("green","blue","red").contains(V))
    //.mapValues[String]((Values:String) => Values.split(",")(1).toLowerCase)
    //.filter((user: String, color: String) => List("green", "blue", "red").contains(color))

  val intertopic = "user_keys_colors_scala";
  userandcolor.to("user_keys_colors_scala")

  val userandcolortable: KTable[String,String] = builder.table("user_keys_colors_scala")
  val favcolors: KTable[String, lang.Long] = userandcolortable.groupBy((K:String, V:String)=>new KeyValue[String,String](V,V)).count()

  favcolors.toStream.to("fav-color-output")

  val streamscol: KafkaStreams =new KafkaStreams(builder.build(),config)


 streamscol.start()

 // Add shutdown hook to respond to SIGTERM and gracefully close Kafka Streams
Runtime.getRuntime.addShutdownHook(new Thread{

  override def run():Unit={
    streamscol.close()
  }
})

}
