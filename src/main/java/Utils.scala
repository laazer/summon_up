package main.java


import dto.MatchHistory.{Participant, MatchSummary, Player}
import riotapi.{RiotApi}
import constant._
import scala.collection.JavaConversions._

/**
 * Created by jacob on 3/26/15.
 */
object Utils {

  val forge = new RiotApi("API-KEY")

}

class Summoner(name:String, forge: RiotApi){

  //static summmoner data from server
  val summonerStatic = forge.getSummonerByName(Region.NA, name).get(name)

  //dynamic summoner data from server
  //val summonerDyn = forge.getPlayerStatsSummary(Region.NA, summonerStatic.getId)

  //games summoner was in
  val games = forge.getMatchHistory(summonerStatic.getId).getMatches.toList
  //ranked games summoner was in
  val loRankedGames = games.filter(m => m.getQueueType.equals(QueueType.RANKED_SOLO_5x5))

  val gameSummeries = games.map(g => new GameSumByPlayer(summonerStatic.getId, summonerStatic.getName, g))

  val wonRankedGames = gameSummeries.filter(g => g.won)

  val listAllTeamMates = getAllTeamateIds(gameSummeries)

  def getAllTeamateIds(ggames : List[GameSumByPlayer]) : List[Player] = {
    return ggames.map(g => g.teamMates.filter(t => t.getSummonerId != summonerStatic.getId)).flatten
  }

  def getDuoCount(): List[(Player, Int)] = {
    val uniqueList = listAllTeamMates.toSet
    val playerCountPair = uniqueList.map(p => (p, listAllTeamMates.count(p2 => p.getSummonerId == p2.getSummonerId))).toList
    return playerCountPair
  }

  lazy val duoCount = getDuoCount()

  def getTopDuos(n : Int) : List[Player] = {
    var count = 1;
    var lDuoCount = getDuoCount()
    //ensures correct players are dropped if needed
    lDuoCount = lDuoCount.sortBy(p => p._2)
    var tmp = getDuoCount()
    while (lDuoCount.size > n) {
      tmp = lDuoCount
      lDuoCount.filter(p => p._2 < count)
    }

    if(lDuoCount.isEmpty)
      return tmp.map(p => p._1)
    else
      return lDuoCount.map(p => p._1).drop(lDuoCount.length - n)
  }

  def getMostPlayedChampGames(n : Int, gameSums : List[GameSumByPlayer]) : List[GameSumByPlayer] = {
      var ltopDuos = getTopDuos(n)
      ltopDuos.foreach( duoPlayer => {
        val duoGames = gameSums.filter(g => g.getTeamates().contains(duoPlayer)).map(g =>
          new GameSumByPlayer(duoPlayer.getSummonerId, duoPlayer.getSummonerName, g.game))
        val champs = duoGames.map(g => g.champ)
        champs
      })

  }



}
