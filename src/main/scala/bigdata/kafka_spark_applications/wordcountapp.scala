package bigdata.kafka_spark_applications


import java.time.Duration
import java.util.Properties

import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.kstream._
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}
import org.apache.kafka.common.serialization._;

object wordcountapp extends App {

  import org.apache.kafka.streams.scala.Serdes._
  import org.apache.kafka.streams.scala.ImplicitConversions._

  val config: Properties = {
    val p = new Properties()
    p.put(StreamsConfig.APPLICATION_ID_CONFIG, "wordcount-scala-application")
    val bootstrapServers = if (args.length > 0) args(0) else "localhost:9092"
    p.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
    p.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG,Serdes.String().getClass)
    p.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG,Serdes.Long().getClass)

    p
  }

  val builder = new StreamsBuilder()
  val textLines: KStream[String, String] = builder.stream[String, String]("streams-plaintext-input")

  //val wordc =textLines.mapValues(t=>t.toLowerCase).to("streams-wordcount-output")

  val wordcd: KTable[String, Long] =textLines.selectKey((K, V)=>V).groupBy((K, V)=>K).count()

    wordcd.toStream.to("streams-wordcount-output")
  //val wordcetest: Grouped[String, String] => KTable[String, Long] =textLines.groupByKey(_).count();
  //val wordCounts: KTable[String, Long] = textLines
//    .flatMapValues(textLine => textLine.toLowerCase.split("\\W+"))
//    .groupBy((_, word) => word)
//    .count()
//  wordCounts.toStream.to("streams-wordcount-output")

  val streams: KafkaStreams = new KafkaStreams(builder.build(), config)


  streams.cleanUp()

  streams.start()

  // Add shutdown hook to respond to SIGTERM and gracefully close Kafka Streams
  sys.ShutdownHookThread {
    streams.close(Duration.ofSeconds(10))
  }

}
