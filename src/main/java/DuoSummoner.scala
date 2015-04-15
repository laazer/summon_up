package main.java;

import dto.MatchHistory._
import scala.collection.JavaConversions._

/**
 * Created by jacob on 4/6/15.
 */
public class GameSumByPlayer(summonerId : Long, summonerName: String = "",
                          matchSummary: MatchSummary) {
    val game = matchSummary
    val partIdentity = getPartIdent
    val teamMates = getTeamates
    val playerInfo = getPlayerInfo
    lazy val champ = playerInfo.getChampionId
    lazy val kills = playerInfo.getStats.getKills
    lazy val deaths = playerInfo.getStats.getDeaths
    lazy val assists = playerInfo.getStats.getAssists
    lazy val won = playerInfo.getStats.isWinner

    def getTeamates() : List[Player] = {
        val sumPId = game.getParticipantIdentities.filter(id => id.getPlayer.getSummonerId == summonerId).get(0)
        val part = game.getParticipants.filter(p => p.getParticipantId == sumPId.getParticipantId).get(0)
        val teamMatesPartIds = game.getParticipants.filter(p => p.getTeamId == part.getTeamId).map(p => p.getParticipantId).toList
        val teamMatesPartIdent = game.getParticipantIdentities.filter(id => teamMatesPartIds.contains(id)).map(p => p.getPlayer).toList
        return teamMatesPartIdent
    }

    def getPartIdent(): ParticipantIdentity = {
        return game.getParticipantIdentities.filter(id => id.getPlayer.getSummonerId == summonerId).get(0)
    }

    def getPlayerInfo(): Participant = {
        return game.getParticipants.filter(id => id.getParticipantId == partIdentity.getParticipantId).get(0)
    }

}
