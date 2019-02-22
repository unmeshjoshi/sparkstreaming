package com.financeapp.legacy

import java.sql.{DriverManager, SQLException}

case class Customer(name:String, address:String, accountId:String)

class CustomerRepository {


  def initialize(): Unit = {
    try {
      val connection = getConnection
      val statement = connection.createStatement
      statement.execute("create table customers(name varchar(50), address varchar(50), accountId varchar(50))")
      statement.execute("create table eventstore(eventId varchar(50), eventType varchar(50), eventData  CLOB(1K))")
      connection.commit()
    } catch {
      case e: Exception ⇒
        throw new RuntimeException(e)
    }
  }

  def add(customer:Customer): Int = {
    val ps = getConnection.prepareStatement("insert into customers values (?, ?, ?)")
    ps.setString(1, customer.name)
    ps.setString(2, customer.address)
    ps.setString(3, customer.accountId)
    ps.executeUpdate()
  }

  def query(sql: String): List[Customer] = {
    var customers = List[Customer]()
    val ps = getConnection.prepareStatement(sql)
    val resultSet = ps.executeQuery()
    while (resultSet.next()) {
      val name = resultSet.getString("name")
      val address = resultSet.getString("address")
      val accountId = resultSet.getString("accountId")
      customers = Customer(name, address, accountId) :: customers
    }
    customers
  }

  private def getConnection = try
//    DriverManager.getConnection("jdbc:hsqldb:mem:customer", "sa", "")
      DriverManager.getConnection("jdbc:postgresql://172.17.0.2:5432/?user=postgres", "postgres", "password")
  catch {
    case e: SQLException ⇒
      throw new RuntimeException(e)
  }
}
