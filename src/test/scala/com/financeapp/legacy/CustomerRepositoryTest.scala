package com.financeapp.legacy

import org.scalatest.FunSuite

class CustomerRepositoryTest extends FunSuite {

  test("should query sql database from spark") {
    val repository = new CustomerRepository()
//    repository.initialize()
    repository.add(Customer("george", "1 main street", "1"))
    repository.add(Customer("cindy", "1 main street", "2"))

    val customers = repository.query("select * from Customers where name='cindy'")
    assert(1 == customers.size)
    assert(customers(0).name == "cindy")
  }

}
