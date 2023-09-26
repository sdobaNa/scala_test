package service

import slick.jdbc.H2Profile.api._
import tables.{Security, Tables}

import scala.concurrent.Future

object SecurityService {

  private val connection = Connection.db
  private val table = Tables.securityTable

  def getOneById(secid: String): Future[Seq[Security]] = connection.run(table.filter(_.secid === secid).result)

  def getAll: Future[Seq[Security]] = connection.run(table.result)

  def createOne(security: Security): Future[Int] = connection.run(table += security)

  def updateOne(secid: String, security: Security): Future[Int] = connection.run(table.filter(_.secid === secid).update(security))

  def deleteOne(secid: String): Future[Int] = connection.run(table.filter(_.secid === secid).delete)

  def insertMany(list: Seq[Security]) = connection.run(table ++= list)
  //    connection.run(DBIO.sequence(list.map{ row => table.insertOrUpdate(row)})) //org.postgresql.util.PSQLException: ОШИБКА: ошибка синтаксиса (примерное положение: "(")
}
