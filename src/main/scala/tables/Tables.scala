package tables

import slick.lifted.TableQuery

object Tables {
  lazy val securityTable = TableQuery[SecurityTable]
  lazy val historyTable = TableQuery[HistoryTable]
}
