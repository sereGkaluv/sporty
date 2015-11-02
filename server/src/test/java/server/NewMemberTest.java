package server;

import java.rmi.RemoteException;

import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.Assert;
import org.junit.Rule;

import at.sporty.team1.rmi.dtos.MemberDTO;
import at.sporty.team1.application.controller.*;


public class NewMemberTest {
	
	@Rule 
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void newMemberTest_1() {		
		
		MemberDTO _activeMemberDTO = null;
		
		String fName = "";
        String lName = "";
        String bday = "";
        String email = "";
        String phone = "";
        String gender = null;
        String address = "";
        String sport = "";
        
        MemberController memberCon = null;
        
        boolean error = false;
        
		try {
			memberCon = new MemberController();
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
        try {
			error = memberCon.createNewMember(_activeMemberDTO);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        Assert.assertTrue(error);
   
	}

	/**
	 * all Parameters in the right format
	 */
	@Test
	public void newMemberTest_2() {
		
		
		MemberDTO _activeMemberDTO = new MemberDTO();
		boolean error = false;
		String fName = "Anne";
        String lName = "Tester";
        String bday = "2001-01-01";
        String email = "test@gmx.at";
        String phone = "0043 235923847";
        String gender = "F";
        String address = "Street 1";
        String sport = "Soccer";
        
        MemberController memberCon = null;
        
        _activeMemberDTO.setFirstName(fName);
        _activeMemberDTO.setLastName(lName);
        _activeMemberDTO.setDateOfBirth(bday);
        _activeMemberDTO.setEmail(email);
        //_activeMemberDTO.setPhone(phone);
        _activeMemberDTO.setGender(gender);
        _activeMemberDTO.setAddress(address);
        _activeMemberDTO.setDepartment(sport);
        
		try {
			memberCon = new MemberController();
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
        try {
			error = memberCon.createNewMember(_activeMemberDTO);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        Assert.assertFalse(error);
   
	}
	
	/**
	 * Bday in false format
	 * @throws Exception 
	 */
	@Test
	public void newMemberTest_3() {
		
		thrown.expect(java.lang.IllegalArgumentException.class);
	    
		MemberDTO _activeMemberDTO = new MemberDTO();
		boolean error = false;
		String fName = "Fred";
        String lName = "Tester";
        String bday = "01.01.1900";
        String email = "test@gmx.at";
        String phone = "0043 235923847";
        String gender = "F";
        String address = "Street 1";
        String sport = "Soccer";
        
        MemberController memberCon = null;
        
        _activeMemberDTO.setFirstName(fName);
        _activeMemberDTO.setLastName(lName);
        _activeMemberDTO.setDateOfBirth(bday);
        _activeMemberDTO.setEmail(email);
        //_activeMemberDTO.setPhone(phone);
        _activeMemberDTO.setGender(gender);
        _activeMemberDTO.setAddress(address);
        _activeMemberDTO.setDepartment(sport);
        
		try {
			memberCon = new MemberController();
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			error = true;
		}
		
        try {
			error = memberCon.createNewMember(_activeMemberDTO);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			error = true;
		}
	}
	
	/**
	 * Bday in false format
	 * @throws Exception 
	 */
	@Test
	public void newMemberTest_4() {
		
		thrown.expect(java.lang.IllegalArgumentException.class);
	    
		MemberDTO _activeMemberDTO = new MemberDTO();
		boolean error = false;
		String fName = "Fred";
        String lName = "Tester";
        String bday = "1900-12-12";
        String email = "test@gmx.at";
        String phone = "0043 235923847";
        String gender = "affe";
        String address = "Street 1";
        String sport = "Soccer";
        
        MemberController memberCon = null;
        
        _activeMemberDTO.setFirstName(fName);
        _activeMemberDTO.setLastName(lName);
        _activeMemberDTO.setDateOfBirth(bday);
        _activeMemberDTO.setEmail(email);
        //_activeMemberDTO.setPhone(phone);
        _activeMemberDTO.setGender(gender);
        _activeMemberDTO.setAddress(address);
        _activeMemberDTO.setDepartment(sport);
        
		try {
			memberCon = new MemberController();
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			error = true;
		}
		
        try {
			error = memberCon.createNewMember(_activeMemberDTO);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			error = true;
		}
	}

}