# Reading and Saving Hive and Json data:

import org.apache.spark.sql.hive.HiveContext
val sqlContext = new HiveContext(sc) # creating hive context
val depts = sqlContext.sql("select * from departments") # creating RDD from departments table
depts.collect().foreach(println) # printing the data

# creating table from existing hive table and reading data from new table
sqlContext.sql("create table departmentssparkscala as select * from departments")
val depts = sqlContext.sql("select * from departmentssparkscala")
depts.collect().foreach(println)
# we can do any hive operation like above code snippet

# Reading and Saving Json data
# Lets assume we have json data in our linux file system
# Copying json file to HDFS
hadoop fs -put departments.json /user/cloudera/sparkscala/

import org.hadoop.spark.sql.SQLContext
val sqlContext = new SQLContext(sc)
val departmentsjson = sqlContext.jsonFile("/user/cloudera/sparkscala/departments.json")
departmentjson.registerTempTable("departmentTable")
val departmentsData = sqlContext("select * from departmentsTable")
departmentsData.collect().foreach(println)

# Writting data in json format
departmentsData.toJSON.saveAsTextFile('/user/coludera/scalaspark/departmentsJson')

# Validating the data
hadoop fs -cat /user/cloudera/scalaaprk/departmentsJson/part-*

########################################################################################################################
# Word count using scala
val data = sc.textFile("/user/cloudera/wordcount.txt")
val dataFlatMap = data.flatMap(x => x.split(" "))
val dataMap = dataFlatMap.map(x => (x, 1))
val dataReduceByKey = dataMap.reduceByKey((x, y) => x + y)
dataReduceByKey.saveAsTextFile("/user/cloudera/wordcountoutput")

########################################################################################################################

# Joining data sets
# Problem statement. Get the revenue and number of orders from order_items on daily basis

# Loading data sets into RDD
val ordersRDD = sc.textFile("/user/cloudera/sqoop_imports/orders")
val orderItemsRDD = sc.textFile("/usert/cloudera/sqoop_imports/order_items")

# Converting records into Key, Value pairs
val ordersParsedRDD = ordersRDD.map(rec => (rec.split(",")(0).toInt, rec))
val ordersItemsParsedRDD = orderItemsRDD.map( rec => (rec.split(",")(1).toInt, rec))

# Joinig datasets Uings keys
val ordersJoinOrderItems = ordersParsedRDD.join(orderItemsParsedRDD)

# Getting revenue per day
val revenPerOrderPerDay = ordersJoinOrderItems.map(t => (t._2._2.split(",")(1), t._2._1.split(",")(4).toFloat

# Getting Order count Per Day
val ordersPerDay = ordersJoinOrderItems.map(rec => rec._2._2.split(",")(1) + "," + rec._1).distinct()

val ordersPerDayParsedRDD = ordersPerDay.map(rec => (rec.split(",")(0), 1))
val totalOrdersPerDay = ordersPerDayParsedRDD.reduceByKey((x,y) => x + y)

# getting Revenue Per day from joined data sets

val totalRevenuePerDay = revenuePerOrderPerDay.reduceByKey((total1, total2) => total1 + total2)

# Joinig Total orders Per day and Total Revenue Per Day
val finalJoinRDD = totalOrdersPerDay.join(totalRevenuePerDay)

# Testing
finalJoinRDD.take(5).foreach(println)

// ########################################################################################################################

// Joining Data Sets using hive and sql context
// ============================================

import org.apache.spark.sql.hive.HiveContext
val sqlContext = new HiveContext(sc)

// Overriding default number of partitions, Default partitions is 200
sqlContext.sql("spark.sql.shuffle.partitions=10");

val joinAggData = sqlContext.sql(
  "
  SELECT  o.order_date,
          ROUND(SUM(oi.order_item_subtotal), 2),
          COUNT(DISTINCT o.order_id)
  FROM order o
  JOIN order_items oi
    ON o.order_id = oi.order_item_order_id
  "
)

// printing the results

joinAggData.collect().foreach(println)

// Using Spark native sql
import org.apache.spark.sql.SQLContext, org.apache.spark.sql.Row
val sqlContext = new SQLContext(sc)
sqlContext.sql("spark.sql.shuffle.partitions=10");

val ordersRDD = sc.textFile("/user/cloudera/sqoop_import/orders")
val ordersMap = ordersRDD.map(o => o.split(","))

case class Orders(
  order_id: Int,
  order_date: String,
  order_customer_id: Int,
  order_status: String
)
val orders = ordersMap.map(o => Orders(o(0).toInt, o(1), o(2).toInt, o(3)))

import sqlContext.creatSchemaRDD
orders.registerTempTable("orders")

// Registering order items table

case class OrderItems(
  order_item_id: Int,
  order_item_order_id: Int,
  order_item_product_id: Int,
  order_item_quantity: Int,
  order_item_subtotal: Float,
  order_item_product_price: Float
)

val orderItems = (sc.textFile("/user/cloudera/sqoop_import/order_items")
                .map(rec => rec.split(","))
                .map(oi => OrderItems(oi(0).toInt, oi(1).toInt, oi(2).toInt, oi(3).toInt, oi(4).toFloat, oi(5).toFloat))
              )
orderItems.registerTempTable("order_items")

val joinAggData = sqlContext.sql(
  "select o.order_date, sum(oi.order_item_subtotal), " +
  "count(distinct o.order_id) from orders o join order_items oi " +
  "on o.order_id = oi.order_item_order_id " +
  "group by o.order_date order by o.order_date"
)

joinAggData.collect().foreach(println)

// Aggrecations by key
val ordersRDD = sc.textFile("/user/cloudera/sqoop_import/orders")
val ordersMap = ordersRDD.map(rec => (rec.split(",")(3), 1))

// Count By Key
ordersMap.countByKey().foreach(println)

// groupByKey is not very efficient for aggregations. It does not use combiner
// groupByKey
val ordersByStatus = ordersMap.groupByKey().map(t => (t._1, t._2.sum))


// reduceByKey uses combiner - both reducer logic and combiner logic are same
val ordersByStatus = ordersMap((acc, value) => acc + value)

// combineByKey can be used when reduce logic and combine logic are different
// Both reduceByKey and combineByKey expects type of input data and output data are same

val ordersByStatus = ordersMap.combineByKey(value => 1, (acc:Int, value: Int) => acc + value, (acc: Int, value: Int) => acc + value)

// aggregateByKey can be used when reduce logic and combine logic is different
// Also type of input data and output data need not be same

val ordersMap = ordersRDD.map(rec => (rec.split(",")(3), rec))
val ordersByStatus = ordersMap.aggregateByKey(0, (acc, value) => acc + value, (acc, value) => acc + value)
ordersByStatus.collect().foreach(println)
