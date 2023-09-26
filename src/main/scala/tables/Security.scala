package tables

import slick.jdbc.PostgresProfile.api._
import slick.lifted.ProvenShape

case class Security(secid: String, regNumber: String, name: String, emitentTitle: String)

class SecurityTable(tag: Tag) extends Table[Security](tag, Some("scala"), "security") {
  def secid = column[String]("secid", O.PrimaryKey, O.Unique)
  def regNumber = column[String]("reg_number")
  def name = column[String]("name")
  def emitentTitle = column[String]("emitent_title")

  def link = foreignKey("security_fk", secid, Tables.securityTable)(_.secid)

  override def * : ProvenShape[Security] = (secid, regNumber, name, emitentTitle) <> (Security.tupled, Security.unapply)
}
