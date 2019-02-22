package com.financeapp

import java.sql.{Connection, DriverManager, SQLException}

import com.financeapp.legacy.Customer

import scala.util.Random

object SeedApp extends App {
  val connection: Connection = getConnection
  initialize(connection)

  val firstNames = List("Adam", "Alex", "Aaron", "Ben", "Carl", "Dan", "David", "Edward", "Fred", "Frank", "George", "Hal", "Hank", "Ike", "John", "Jack", "Joe", "Larry", "Monte", "Matthew", "Mark", "Nathan", "Otto", "Paul", "Peter", "Roger", "Roger", "Steve", "Thomas", "Tim", "Ty", "Victor", "Walter")
  val lastNames = List("Anderson", "Ashwoon", "Aikin", "Bateman", "Bongard", "Bowers", "Boyd", "Cannon", "Cast", "Deitz", "Dewalt", "Ebner", "Frick", "Hancock", "Haworth", "Hesch", "Hoffman", "Kassing", "Knutson", "Lawless", "Lawicki", "Mccord", "McCormack", "Miller", "Myers", "Nugent", "Ortiz", "Orwig", "Ory", "Paiser", "Pak", "Pettigrew", "Quinn", "Quizoz", "Ramachandran", "Resnick", "Sagar", "Schickowski", "Schiebel", "Sellon", "Severson", "Shaffer", "Solberg", "Soloman", "Sonderling", "Soukup", "Soulis", "Stahl", "Sweeney", "Tandy", "Trebil", "Trusela", "Trussel", "Turco", "Uddin", "Uflan", "Ulrich", "Upson", "Vader", "Vail", "Valente", "Van Zandt", "Vanderpoel", "Ventotla", "Vogal", "Wagle", "Wagner", "Wakefield", "Weinstein", "Weiss", "Woo", "Yang", "Yates", "Yocum", "Zeaser", "Zeller", "Ziegler", "Bauer", "Baxster", "Casal", "Cataldi", "Caswell", "Celedon", "Chambers", "Chapman", "Christensen", "Darnell", "Davidson", "Davis", "DeLorenzo", "Dinkins", "Doran", "Dugelman", "Dugan", "Duffman", "Eastman", "Ferro", "Ferry", "Fletcher", "Fietzer", "Hylan", "Hydinger", "Illingsworth", "Ingram", "Irwin", "Jagtap", "Jenson", "Johnson", "Johnsen", "Jones", "Jurgenson", "Kalleg", "Kaskel", "Keller", "Leisinger", "LePage", "Lewis", "Linde", "Lulloff", "Maki", "Martin", "McGinnis", "Mills", "Moody", "Moore", "Napier", "Nelson", "Norquist", "Nuttle", "Olson", "Ostrander", "Reamer", "Reardon", "Reyes", "Rice", "Ripka", "Roberts", "Rogers", "Root", "Sandstrom", "Sawyer", "Schlicht", "Schmitt", "Schwager", "Schutz", "Schuster", "Tapia", "Thompson", "Tiernan", "Tisler")

  (1 to 1000).foreach(i ⇒ {
    val nameIndex = new Random().nextInt(firstNames.size)
    add(connection, Customer(firstNames(nameIndex), s"${i} main street", s"${i}"))
  })

  def initialize(connection: Connection): Unit = {
    try {
      val statement = connection.createStatement
      statement.execute("drop table customers")
      statement.execute("create table customers(name varchar(50), address varchar(50), accountId varchar(50))")
    } catch {
      case e: Exception ⇒
        throw new RuntimeException(e)
    }
  }

  def add(connection:Connection, customer:Customer) = {
    val ps = connection.prepareStatement("insert into customers values (?, ?, ?)")
    ps.setString(1, customer.name)
    ps.setString(2, customer.address)
    ps.setString(3, customer.accountId)
    ps.executeUpdate()
    ps.close()
  }

  private def getConnection = try
    //    DriverManager.getConnection("jdbc:hsqldb:mem:customer", "sa", "")
    DriverManager.getConnection("jdbc:postgresql://172.17.0.2:5432/?user=postgres", "postgres", "password")
  catch {
    case e: SQLException ⇒
      throw new RuntimeException(e)
  }

}
