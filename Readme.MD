# PubNative case study

## Introduction

The study have 3 tasks related advertising, impressions and the revenue obtained from the 
advertisers. The first is about load the json files with the data, the second needs to compute
some metrics by country and application and the third is about finding the advertiser with the
 highest revenue per impression.
 
 ##  Implementation
 
 The program was implemented in Scala following the TDD and Clean code guidelines. We used Sbt for building 
 and Scalatest for testing. Circe was used for json manipulation and Apache Spark was used in the 
 third task. All task are implemented in the processing module and called from the main program PubNativeMain. 
 Let explain in a litle more detail each task implementation.
 
 1. For loading json we read the file, transform its content to String and deserialize to 
 the respective case class sequence. This approach has the problem that the file need to fit in memory.
 In a big data case must be better a stream approach. A second problem is related the inconsistencies
 in impression file with missing, null or empty fields. To manage this problem we create a case
 case class specific for loading with all field is an Option. Then we filter out all objects with None
 or empty fields and transform to the natural case class.
  
 1. To compute the metrics the Click data was transformed to a Map "impression_id -> revenue" for efficient access. 
 Then the computation was done using a grouping by app and country over the sequence of impressions.
 
 1. To compute the top advertiser we create datasets from the sequences of impressions and clicks. Then join they by 
 impression id using a broadcast left join. The idea is that clicks dataset fit in memory so can be done a more efficient 
 join. Then the average revenue per advertiser is compute, the result is grouped by application and country, sorted by revenue and taked 
 the top five. In particular if a advertiser does not have revenue it is discarded.
 
 The main program get the paths for input and output files as line parameters in the order submissions,clicks, metrics
 and topAdvertisers. We use scallop library for process line parameter. A script for launch using spark-submit in 
 Example bellow.
 
 ## Preconditions
 
 * sbt
 * Apache Spark 2.4.5, with spark-submit in PATH
 * Clone repo and enter main dir
 
 ## Run tests
 
 sbt test
 
 ## Build project
 
 sbt assembly
 
 ## Run project example
 
 ./scripts/pubNative.sh data/impressions.json data/clicks.json data/metrics.json data/topAdvertiser.json
 
 