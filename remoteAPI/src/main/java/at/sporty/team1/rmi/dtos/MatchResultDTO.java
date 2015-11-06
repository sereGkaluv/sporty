package at.sporty.team1.rmi.dtos;

import at.sporty.team1.rmi.api.IDTO;

/**
 * Created by f00 on 03.11.15.
 */
public class MatchResultDTO implements IDTO {//TODO everything

    public MatchResultDTO() {
    }

    public TeamDTO winner;
    public TeamDTO teamA;
    public TeamDTO teamB;
    public String comment;
    public Integer pointsA;
    public Integer pointsB;


    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }


    public Integer getPointsA() {
        return pointsA;
    }


    public void setPointsA(Integer pointsA) {
        this.pointsA = pointsA;
    }


    public Integer getPointsB() {
        return pointsB;
    }

    public void setPointsB(Integer pointsB) {
        this.pointsB = pointsB;
    }


    public TeamDTO getTeamA() {
        return teamA;
    }


    public void setTeamA(TeamDTO teamA) {
        this.teamA = teamA;
    }

    public TeamDTO getWinner() {
        return winner;
    }


    public void setWinner(TeamDTO winner) {
        this.winner = winner;
    }


    public TeamDTO getTeamB() {
        return teamB;
    }


    public void setTeamB(TeamDTO teamB) {
        this.teamB = teamB;
    }
}
