package service

import slick.jdbc.H2Profile.api._
import tables.{History, Tables}

import java.util.concurrent.Executors
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

object HistoryService {

  implicit val ec = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(5))

  private val connection = Connection.db
  private val table = Tables.historyTable

  def getOneById(id: Long): Future[Seq[History]] = connection.run(table.filter(_.id === id).result)

  def getAll: Future[Seq[History]] = connection.run(table.result)

  def createOne(history: History): Future[Int] = connection.run(table += history)

  def updateOne(id: Long, history: History): Future[Int] = connection.run(table.filter(_.id === id).update(history))

  def deleteOne(id: Long): Future[Int] = connection.run(table.filter(_.id === id).delete)

  def insertMany(list: Seq[History]): Seq[Unit] = list.map { row =>
    connection.run(table += row).onComplete {
      case Success(value) => {}
      case Failure(ex) => //TODO
    }
  }

}
