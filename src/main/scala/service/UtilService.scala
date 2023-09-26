package service

import slick.ast.BaseTypedType
import slick.jdbc.H2Profile.api._
import slick.jdbc.JdbcType
import tables.{History, Security, Tables}

import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.Executors
import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions
import scala.xml.{Node, NodeSeq}

object UtilService {

  implicit val ec = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(5))

  private val connection = Connection.db
  private val securityTable = Tables.securityTable
  private val historyTable = Tables.historyTable

  private final val SECURITY_KEY = "securities"
  private final val HISTORY_KEY = "history"

  def joinSecurityHistory = connection.run {
    (for {
      s <- securityTable
      h <- historyTable
    } yield (s.secid, s.regNumber, s.name, s.emitentTitle, h.tradeDate, h.numTrades, h.open)).to[List].result
  }

  def importData(targetName: String, node: NodeSeq) = {

    val data = node \ "data" filter (n => n.attributes.exists(_.value.text == targetName))

    val list = data \\ "row" map (n =>
      targetName match {
        case SECURITY_KEY =>
          Security(
            getValue("SECID", n),
            getValue("BOARDID", n),
            getValue("NAME", n),
            getValue("SHORTNAME", n))

        case HISTORY_KEY =>
          History(0,
            getValue("SECID", n),
            stringToDate(getValue("TRADEDATE", n)),
            getValue("NUMTRADES", n).toInt,
            if (getValue("OPEN", n).isBlank) 0.0 else getValue("OPEN", n).toDouble
          )
      }
      )

    targetName match {
      case SECURITY_KEY => SecurityService.insertMany(list.map(it => it.asInstanceOf[Security]))
      case HISTORY_KEY => Future(HistoryService.insertMany(list.map(it => it.asInstanceOf[History])))
    }
  }

  private def getValue(key: String, node: Node) = node.attribute(key).get.text

  private def stringToDate(str: String) = new SimpleDateFormat("yyyy-MM-dd").parse(str)

  implicit val localDateColumnType: JdbcType[Date] with BaseTypedType[Date] =
    MappedColumnType.base[Date, java.sql.Date](sqlDateToUtilDate, utilDateToSqlDate)

  implicit def sqlDateToUtilDate(d: java.util.Date): java.sql.Date = new java.sql.Date(d.getTime)

  implicit def utilDateToSqlDate(d: java.sql.Date): Date = new Date(d.getTime)
}
