package com.financeapp.legacy

import java.sql.{Connection, DriverManager, SQLException, Statement}

class ETLRepository {

  def initialize(): Unit = {
    try {
      val connection = getConnection
      val statement = connection.createStatement
      statement.execute("create table customer(name varchar(50), address varchar(50), accountId varchar(50))")
      statement.execute("create table eventstore(eventId varchar(50), eventType varchar(50), eventData  CLOB(1K))")
      connection.commit()
    } catch {
      case e: Exception ⇒
        throw new RuntimeException(e)
    }
  }

  private def getConnection = try
    DriverManager.getConnection("jdbc:hsqldb:mem:customer", "sa", "")
  catch {
    case e: SQLException ⇒
      throw new RuntimeException(e)
  }
}
