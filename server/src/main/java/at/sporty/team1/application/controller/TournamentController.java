package at.sporty.team1.application.controller;

import at.sporty.team1.domain.Match;
import at.sporty.team1.domain.Tournament;
import at.sporty.team1.domain.interfaces.ITournament;
import at.sporty.team1.misc.InputSanitizer;
import at.sporty.team1.persistence.PersistenceFacade;
import at.sporty.team1.rmi.api.ITournamentController;
import at.sporty.team1.rmi.dtos.MatchDTO;
import at.sporty.team1.rmi.dtos.SessionDTO;
import at.sporty.team1.rmi.dtos.TournamentDTO;
import at.sporty.team1.rmi.enums.UserRole;
import at.sporty.team1.rmi.exceptions.DataType;
import at.sporty.team1.rmi.exceptions.NotAuthorisedException;
import at.sporty.team1.rmi.exceptions.UnknownEntityException;
import at.sporty.team1.rmi.exceptions.ValidationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;

import javax.persistence.PersistenceException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TournamentController represents the logic controller for a tournament
 */
public class TournamentController extends UnicastRemoteObject implements ITournamentController {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LogManager.getLogger();
    private static final Mapper MAPPER = new DozerBeanMapper();

    public TournamentController() throws RemoteException{
    	super();
    }

    @Override
    public List<TournamentDTO> searchAllTournaments(SessionDTO session)
    throws RemoteException, NotAuthorisedException {

        if (!LoginController.hasEnoughPermissions(session, UserRole.MEMBER)) throw new NotAuthorisedException();

        try {

            /* pulling a TournamentDAO and getting all Tournaments */
            List<Tournament> tournaments = PersistenceFacade.getNewGenericDAO(Tournament.class).findAll();

            //checking if there are an results
            if (tournaments.isEmpty()) return null;

            //Converting all Tournaments to TournamentDTO
            return tournaments.stream()
                    .map(tournament -> MAPPER.map(tournament, TournamentDTO.class))
                    .collect(Collectors.toList());

        } catch (PersistenceException e) {
            LOGGER.error("An error occurred while getting all Tournaments ", e);
            return null;
        }
    }

    @Override
    public List<TournamentDTO> searchTournamentsBySport(String sport, SessionDTO session)
    throws RemoteException, ValidationException, NotAuthorisedException {

        if (!LoginController.hasEnoughPermissions(session, UserRole.MEMBER)) throw new NotAuthorisedException();

        /* Validating Input */
        InputSanitizer inputSanitizer = new InputSanitizer();
        if (!inputSanitizer.isValid(sport, DataType.TEXT)) {
            throw inputSanitizer.getPreparedValidationException();
        }

        /* Is valid, moving forward */
        try {

            List<? extends ITournament> rawResults = PersistenceFacade.getNewTournamentDAO().findBySport(sport);

            //checking if there are an results
            if (rawResults == null || rawResults.isEmpty()) return null;

            //Converting results to TournamentDTO
            return rawResults.stream()
                    .map(tournament -> MAPPER.map(tournament, TournamentDTO.class))
                    .collect(Collectors.toList());

        } catch (PersistenceException e) {
            LOGGER.error("An error occurred while searching Tournaments by Sport \"{}\".", sport, e);
            return null;
        }
    }

    @Override
    public List<TournamentDTO> searchTournamentsByDate(String eventDate, SessionDTO session)
    throws RemoteException, ValidationException, NotAuthorisedException {

        if (!LoginController.hasEnoughPermissions(session, UserRole.MEMBER)) throw new NotAuthorisedException();

        /* Validating Input */
        InputSanitizer inputSanitizer = new InputSanitizer();
        if (!inputSanitizer.isValid(eventDate, DataType.SQL_DATE)) {
            throw inputSanitizer.getPreparedValidationException();
        }

        /* Is valid, moving forward */
        try {

            List<? extends ITournament> rawResults = PersistenceFacade.getNewTournamentDAO().findByEventDate(eventDate);

            //checking if there are an results
            if (rawResults == null || rawResults.isEmpty()) return null;

            //Converting results to TournamentDTO
            return rawResults.stream()
                    .map(tournament -> MAPPER.map(tournament, TournamentDTO.class))
                    .collect(Collectors.toList());

        } catch (PersistenceException e) {
            LOGGER.error("An error occurred while searching Tournaments by Event Date \"{}\".", eventDate, e);
            return null;
        }
    }

    @Override
    public List<TournamentDTO> searchTournamentsByLocation(String location, SessionDTO session)
    throws RemoteException, ValidationException, NotAuthorisedException {

        if (!LoginController.hasEnoughPermissions(session, UserRole.MEMBER)) throw new NotAuthorisedException();

        /* Validating Input */
        InputSanitizer inputSanitizer = new InputSanitizer();
        if (!inputSanitizer.isValid(location, DataType.TEXT)) {
            throw inputSanitizer.getPreparedValidationException();
        }

        /* Is valid, moving forward */
        try {

            List<? extends ITournament> rawResults = PersistenceFacade.getNewTournamentDAO().findByLocation(location);

            //checking if there are an results
            if (rawResults == null || rawResults.isEmpty()) return null;

            //Converting results to TournamentDTO
            return rawResults.stream()
                    .map(tournament -> MAPPER.map(tournament, TournamentDTO.class))
                    .collect(Collectors.toList());

        } catch (PersistenceException e) {
            LOGGER.error("An error occurred while searching Tournaments by Location \"{}\".", location, e);
            return null;
        }
    }

    @Override
    public List<String> searchAllTournamentTeams(Integer tournamentId, SessionDTO session)
    throws RemoteException, UnknownEntityException, NotAuthorisedException {

        if (!LoginController.hasEnoughPermissions(session, UserRole.MEMBER)) throw new NotAuthorisedException();

        if (tournamentId == null) throw new UnknownEntityException(ITournament.class);

        /* Is valid, moving forward */
        try {

            /* pulling a TournamentDAO and getting tournament by id */
            Tournament tournament = PersistenceFacade.getNewTournamentDAO().findById(tournamentId);
            if (tournament == null) throw new UnknownEntityException(ITournament.class);

            PersistenceFacade.forceLoadLazyProperty(tournament, Tournament::getTeams);
            return tournament.getTeams();

        } catch (PersistenceException e) {
            LOGGER.error(
                "An error occurred while getting all teams by Tournament #{}.",
                tournamentId,
                e
            );
            return null;
        }
    }

    @Override
    public List<MatchDTO> searchAllTournamentMatches(Integer tournamentId, SessionDTO session)
    throws RemoteException, UnknownEntityException, NotAuthorisedException {

        if (!LoginController.hasEnoughPermissions(session, UserRole.MEMBER)) throw new NotAuthorisedException();

        //TODO Match entity and MatchDTO

        if (tournamentId == null) throw new UnknownEntityException(ITournament.class);

        /* Is valid, moving forward */
        try {

            /* pulling a TournamentDAO and getting tournament by id */
            Tournament tournament = PersistenceFacade.getNewTournamentDAO().findById(tournamentId);
            if (tournament == null) throw new UnknownEntityException(ITournament.class);

            List<Match> rawResults = PersistenceFacade.getNewMatchDAO().findByTournament(tournament);

            //checking if there are an results
            if (rawResults == null || rawResults.isEmpty()) return null;

            //Converting results to TeamDTO
            return rawResults.stream()
                    .map(match -> MAPPER.map(match, MatchDTO.class))
                    .collect(Collectors.toList());

        } catch (PersistenceException e) {
            LOGGER.error(
                "An error occurred while getting all matches for Tournament #{}.",
                tournamentId,
                e
            );
            return null;
        }
    }

    @Override
    public void assignTeamToTournament(String teamName, Integer tournamentId, SessionDTO session)
    throws RemoteException, UnknownEntityException, ValidationException, NotAuthorisedException {

        if (!LoginController.hasEnoughPermissions(session, UserRole.TRAINER)) throw new NotAuthorisedException();

        /* Validating teamName */
        InputSanitizer inputSanitizer = new InputSanitizer();
        if (!inputSanitizer.isValid(teamName, DataType.TEXT)) {
            throw inputSanitizer.getPreparedValidationException();
        }

        if (tournamentId == null) throw new UnknownEntityException(ITournament.class);

        /* Is valid, moving forward */
        try {

            /* pulling a TournamentDAO and update Tournament */
            Tournament tournament = PersistenceFacade.getNewTournamentDAO().findById(tournamentId);
            if (tournament == null) throw new UnknownEntityException(ITournament.class);

            PersistenceFacade.forceLoadLazyProperty(tournament, Tournament::getTeams);
            tournament.addTeam(teamName);

            PersistenceFacade.getNewTournamentDAO().saveOrUpdate(tournament);
        } catch (PersistenceException e) {
            LOGGER.error(
                "An error occurred while adding a Team \"{}\" to a Tournament #{}",
                teamName,
                tournamentId,
                e
            );
        }
    }

	@Override
	public Integer createOrSaveTournament(TournamentDTO tournamentDTO, SessionDTO session)
    throws RemoteException, ValidationException, NotAuthorisedException {

        if (!LoginController.hasEnoughPermissions(session, UserRole.MANAGER)) throw new NotAuthorisedException();

        if (tournamentDTO == null) return null;

		 /* Validating Input */
        InputSanitizer inputSanitizer = new InputSanitizer();
        if (
            !inputSanitizer.isValid(tournamentDTO.getLocation(), DataType.TEXT) ||
            !inputSanitizer.isValid(tournamentDTO.getDate(), DataType.SQL_DATE)
        ) {
            throw inputSanitizer.getPreparedValidationException();
        }

        /* Is valid, moving forward */
        try {

            Tournament tournament = MAPPER.map(tournamentDTO, Tournament.class);

            /* pulling a TournamentDAO and save the Tournament */
            PersistenceFacade.getNewTournamentDAO().saveOrUpdate(tournament);

            LOGGER.info("Tournament for \"{}\" was successfully saved.", tournamentDTO.getDate());
            return tournament.getTournamentId();

        } catch (PersistenceException e) {
            LOGGER.error("Error occurred while communicating with DB.", e);
            return null;
        }
	}

    @Override
    public void createNewMatch(Integer tournamentId, MatchDTO matchDTO, SessionDTO session)
    throws RemoteException, ValidationException, UnknownEntityException, NotAuthorisedException {

        if (!LoginController.hasEnoughPermissions(session, UserRole.MANAGER)) throw new NotAuthorisedException();

        /* Validating Input */
        InputSanitizer inputSanitizer = new InputSanitizer();
        if (
            !inputSanitizer.isValid(matchDTO.getLocation(), DataType.TEXT) ||
            !inputSanitizer.isValid(matchDTO.getReferee(), DataType.TEXT) ||
            !inputSanitizer.isValid(matchDTO.getResult(), DataType.TEXT) ||
            !inputSanitizer.isValid(matchDTO.getTeam1(), DataType.TEXT) ||
            !inputSanitizer.isValid(matchDTO.getTeam2(), DataType.TEXT) ||
            !inputSanitizer.isValid(matchDTO.getDate(), DataType.TEXT)
        ) {
            throw inputSanitizer.getPreparedValidationException();
        }

        if (tournamentId == null) throw new UnknownEntityException(ITournament.class);

        /* Is valid, moving forward */
        try {

            /* pulling a TournamentDAO and save the Tournament */
            Tournament tournament = PersistenceFacade.getNewTournamentDAO().findById(tournamentId);
            if (tournament == null) throw new UnknownEntityException(ITournament.class);

//            IMatch match = MAPPER.map(matchDTO, Match.class);

            //TODO uncomment when Matches will be bind in Tournament
//            PersistenceFacade.forceLoadLazyProperty(tournament, Tournament::getMatches);
//            tournament.addMatch(match);

            PersistenceFacade.getNewTournamentDAO().saveOrUpdate(tournament);

        } catch (PersistenceException e) {
            LOGGER.error("An error occurred during adding a new Match to the Tournament: ", e);
        }
    }
}
