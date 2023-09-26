package tables

import slick.ast.BaseTypedType
import slick.jdbc.JdbcType
import slick.jdbc.PostgresProfile.api._
import slick.lifted.ProvenShape
import java.sql
import java.util.Date
import scala.language.implicitConversions

case class History(id: Long, secid: String, tradeDate: Date, numTrades: Int, open: Double)

class HistoryTable(tag: Tag) extends Table[History](tag, Some("scala"), "history"){

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def secid = column[String]("secid")
  def tradeDate = column[Date]("trade_date")
  def numTrades = column[Int]("num_trades")
  def open = column[Double]("open")

  implicit val localDateColumnType: JdbcType[Date] with BaseTypedType[Date] =
    MappedColumnType.base[Date, java.sql.Date](sqlDateToUtilDate, utilDateToSqlDate)
  implicit def sqlDateToUtilDate(d: java.util.Date): sql.Date = new java.sql.Date(d.getTime)
  implicit def utilDateToSqlDate(d: java.sql.Date): Date = new Date(d.getTime)

  val link = foreignKey("security_fk", secid, Tables.securityTable)(_.secid)

  override def * : ProvenShape[History] = (id, secid, tradeDate, numTrades, open) <> (History.tupled, History.unapply)

}
