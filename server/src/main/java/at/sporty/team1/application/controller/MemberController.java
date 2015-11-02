package at.sporty.team1.application.controller;


import at.sporty.team1.domain.Member;
import at.sporty.team1.domain.interfaces.IMember;
import at.sporty.team1.domain.readonly.IRMember;
import at.sporty.team1.persistence.PersistenceFacade;
import at.sporty.team1.persistence.daos.MemberDAO;
import at.sporty.team1.rmi.api.IMemberController;
import at.sporty.team1.rmi.dtos.MemberDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.PersistenceException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by f00 on 28.10.15.
 */
public class MemberController extends UnicastRemoteObject implements IMemberController {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LogManager.getLogger();

    public MemberController() throws RemoteException {
        super();
    }

    @Override
    public boolean createNewMember(MemberDTO memberDTO)
    throws RemoteException {

        if (memberDTO == null) return true;

        try {
            /* pulling a MemberDAO and save the Member */
            PersistenceFacade.getNewMemberDAO().saveOrUpdate(
                convertDTOToMember(memberDTO)
            );

            LOGGER.info("New member \"{} {}\" was created.", memberDTO.getFirstName(), memberDTO.getLastName());

        } catch (PersistenceException e) {
            LOGGER.error("Error occurs while communicating with DB.", e);
        }

        //TODO rewrite validation
//        /* Validating Input */
//        InputSanitizer inputSanitizer = new InputSanitizer();

//        if (inputSanitizer.check(fName, DataType.NAME) &&
//            inputSanitizer.check(lName, DataType.NAME) &&
//            inputSanitizer.check(bday, DataType.SQL_DATE) &&
//            inputSanitizer.check(email, DataType.EMAIL) &&
//            inputSanitizer.check(phone, DataType.PHONE_NUMBER) &&
//            inputSanitizer.check(address, DataType.ADDRESS) &&
//            inputSanitizer.check(sport, DataType.TEXT) &&
//            inputSanitizer.check(gender, DataType.GENDER))
//        {
        // all Input validated and OK
//        } else {
//            // There has been bad Input, throw the Exception
//            LOGGER.error("Wrong Input creating Member: {}", inputSanitizer.getLastFailedValidation());
//
//            //TODO  throws ValidationException;
//            //ValidationException validationException = new ValidationException();
//            //validationException.setReason(inputSanitizer.getLastFailedValidation());
//            //
//            //throw validationException;
//        }
        
        return false;
    }

    @Override
    public MemberDTO loadMemberById(int memberId)
    throws RemoteException {
        return convertMemberToDTO(PersistenceFacade.getNewMemberDAO().findById(memberId));
    }

    /**
     * Search for members by String (name, birthdate, department, teamname)
     *
     * @param searchQuery
     *
     * @return null or List<IMember>
     * @throws RemoteException
     */
    @Override
    public List<MemberDTO> searchForMembers(String searchQuery)
    throws RemoteException {

//        List<? extends IMember> rawSearchResultsList = PersistenceFacade.getNewMemberDAO().findAll();
//

        List<? extends IMember> rawResultsSearchList = null;

        try {
            rawResultsSearchList = PersistenceFacade.getNewMemberDAO().findByString(searchQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }


        //Converting results to MemberDTO
        return rawResultsSearchList.stream()
            .map(MemberController::convertMemberToDTO)
            .collect(Collectors.toList());
    }

    public void delete(String memberId) throws SQLException {
        //TODO test

        MemberDAO memberDAO = PersistenceFacade.getNewMemberDAO();
        Member member = memberDAO.findById(memberId);
        memberDAO.delete(member);
    }

    public void saveChanges(MemberDTO member) {
        //TODO Save member
    }

    /**
     * A helping method, converts all Member objects to MemberDTO.
     *
     * @param member Member to be converted to a MemberDTO
     * @return MemberDTO representation of the given Member.
     */
    private static MemberDTO convertMemberToDTO (IRMember member){
        if (member != null) {
            return new MemberDTO()
                .setMemberId(member.getMemberId())
                .setFirstName(member.getFirstName())
                .setLastName(member.getLastName())
                .setGender(member.getGender())
                .setDateOfBirth(convertDateToString(member.getDateOfBirth()))
                .setEmail(member.getEmail())
                .setAddress(member.getAddress())
                .setDepartment(member.getDepartment())
                .setTeam(member.getTeam())
                .setSquad(member.getSquad())
                .setRole(member.getRole())
                .setUsername(member.getUsername());
        }
        return null;
    }

    /**
     * A helping method, converts all MemberDTO to Member objects.
     *
     * @param memberDTO MemberDTO to be converted to a Member
     * @return Member representation of the given MemberDTO.
     */
    private static Member convertDTOToMember (MemberDTO memberDTO){
        if (memberDTO != null) {
            Member member = new Member();

            member.setMemberId(memberDTO.getMemberId());
            member.setFirstName(memberDTO.getFirstName());
            member.setLastName(memberDTO.getLastName());
            member.setGender(memberDTO.getGender());
            member.setDateOfBirth(parseDate(memberDTO.getDateOfBirth()));
            member.setEmail(memberDTO.getEmail());
            member.setAddress(memberDTO.getAddress());
            member.setDepartment(memberDTO.getDepartment());
            member.setTeam(memberDTO.getTeam());
            member.setSquad(memberDTO.getSquad());
            member.setRole(memberDTO.getRole());
            member.setUsername(memberDTO.getUsername());

            return member;
        }
        return null;
    }

    /**
     * A helping method.
     *
     * @param s String to be parsed as a date
     * @return parsed date
     */
    private static Date parseDate(String s) {
        return s != null ? Date.valueOf(s) : null;
    }

    /**
     * A helping method.
     *
     * @param d Date to be converted to String
     * @return converted date
     */
    private static String convertDateToString(Date d) {
        return d != null ? d.toString() : null;
    }
}
