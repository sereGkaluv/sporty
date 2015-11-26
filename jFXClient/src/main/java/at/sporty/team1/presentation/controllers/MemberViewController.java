package at.sporty.team1.presentation.controllers;

import at.sporty.team1.communication.CommunicationFacade;
import at.sporty.team1.presentation.controllers.core.JfxController;
import at.sporty.team1.rmi.api.IDTO;
import at.sporty.team1.rmi.api.IDepartmentController;
import at.sporty.team1.rmi.api.IMemberController;
import at.sporty.team1.rmi.api.ITeamController;
import at.sporty.team1.rmi.dtos.DepartmentDTO;
import at.sporty.team1.rmi.dtos.MemberDTO;
import at.sporty.team1.rmi.dtos.TeamDTO;
import at.sporty.team1.rmi.exceptions.NotAuthorisedException;
import at.sporty.team1.rmi.exceptions.UnknownEntityException;
import at.sporty.team1.rmi.exceptions.ValidationException;
import at.sporty.team1.util.GUIHelper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;


public class MemberViewController extends JfxController {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<String, DepartmentDTO> SPORT_MAP = new HashMap<>();
    private static final String SUCCESSFUL_MEMBER_SAVE = "Member was successfully saved.";
    private static final String SPORT_VOLLEYBALL = "Volleyball";
    private static final String SPORT_FOOTBALL = "Football";
    private static final String SPORT_BASEBALL = "Baseball";
    private static final String SPORT_SOCCER = "Soccer";
    private static final String FEMALE = "F";
    private static final String MALE = "M";

    @FXML private TextField fNameTextField;
    @FXML private TextField lNameTextField;
    @FXML private TextField birthTextField;
    @FXML private TextField emailTextField;
    @FXML private TextField phoneTextField;
    @FXML private TextField addressTextField;
    @FXML private RadioButton radioGenderFemale;
    @FXML private RadioButton radioGenderMale;
    @FXML private CheckBox memberSportCheckboxSoccer;
    @FXML private CheckBox memberSportCheckboxVolleyball;
    @FXML private CheckBox memberSportCheckboxBaseball;
    @FXML private CheckBox memberSportCheckboxFootball;
    @FXML private ComboBox<TeamDTO> memberTeamComboBoxSoccer;
    @FXML private ComboBox<TeamDTO> memberTeamComboBoxVolleyball;
    @FXML private ComboBox<TeamDTO> memberTeamComboBoxBaseball;
    @FXML private ComboBox<TeamDTO> memberTeamComboBoxFootball;
    @FXML private ComboBox<RoleType> roleComboBox;

    private static MemberDTO _activeMemberDTO;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ToggleGroup _group = new ToggleGroup();
        radioGenderFemale.setToggleGroup(_group);
        radioGenderMale.setToggleGroup(_group);
        Platform.runLater(fNameTextField::requestFocus);
        
        doComboBoxAndCheckBoxInitialization();
    }

    /**
     * Init the Checkboxes and the Comboboxes in MemberView
     */
    private void doComboBoxAndCheckBoxInitialization() {

        /**
         * Setting checkbox listeners for comboBoxes
         */

        memberSportCheckboxBaseball.selectedProperty().addListener(
            (observable, oldValue, newValue) -> memberTeamComboBoxBaseball.setDisable(oldValue)
        );

        memberSportCheckboxSoccer.selectedProperty().addListener(
            (observable, oldValue, newValue) -> memberTeamComboBoxSoccer.setDisable(oldValue)
        );

        memberSportCheckboxVolleyball.selectedProperty().addListener(
            (observable, oldValue, newValue) -> memberTeamComboBoxVolleyball.setDisable(oldValue)
        );

        memberSportCheckboxFootball.selectedProperty().addListener(
            (observable, oldValue, newValue) -> memberTeamComboBoxFootball.setDisable(oldValue)
        );

        /**
         * Converter from TeamDTO to Team name (String)
         */
        StringConverter<TeamDTO> teamDTOStringConverter = new StringConverter<TeamDTO>() {
            @Override
            public String toString(TeamDTO teamDTO) {
                if (teamDTO != null) {
                    return teamDTO.getTeamName();
                }
                return null;
            }

            @Override
            public TeamDTO fromString(String string) {
                return null;
            }
        };

        memberTeamComboBoxBaseball.setConverter(teamDTOStringConverter);
        memberTeamComboBoxFootball.setConverter(teamDTOStringConverter);
        memberTeamComboBoxSoccer.setConverter(teamDTOStringConverter);
        memberTeamComboBoxVolleyball.setConverter(teamDTOStringConverter);

        /**
         * Role Combobox
         */
        roleComboBox.setItems(FXCollections.observableList(Arrays.asList(RoleType.values())));
        roleComboBox.getSelectionModel().select(RoleType.MEMBER);

        /**
         * Sportdepartment and Teams loading
         */

        //starting as a new thread for faster user feedback
        new Thread(() -> {

            try {

                IDepartmentController departmentController = CommunicationFacade.lookupForDepartmentController();
                List<DepartmentDTO> departments = departmentController.searchAllDepartments(
                    CommunicationFacade.getActiveSession()
                );

                if (!departments.isEmpty()) {

                    IDepartmentController deptController = CommunicationFacade.lookupForDepartmentController();

                    for (DepartmentDTO actualDepartment : departments) {
                        Integer departmentId = actualDepartment.getDepartmentId();

                        switch (actualDepartment.getSport()) {
                            case SPORT_SOCCER: {
                                SPORT_MAP.put(SPORT_SOCCER, actualDepartment);

                                List<TeamDTO> resultList = departmentController.loadDepartmentTeams(
                                    departmentId,
                                    CommunicationFacade.getActiveSession()
                                );

                                if (resultList != null) {

                                    //loading values to comboBox
                                    Platform.runLater(() -> memberTeamComboBoxSoccer.setItems(
                                        FXCollections.observableList(resultList)
                                    ));
                                }
                                break;
                            }

                            case SPORT_VOLLEYBALL: {
                                SPORT_MAP.put(SPORT_VOLLEYBALL, actualDepartment);

                                List<TeamDTO> resultList = departmentController.loadDepartmentTeams(
                                    departmentId,
                                    CommunicationFacade.getActiveSession()
                                );

                                if (resultList != null) {

                                    //loading values to comboBox
                                    Platform.runLater(() -> memberTeamComboBoxVolleyball.setItems(
                                        FXCollections.observableList(resultList)
                                    ));
                                }
                                break;
                            }

                            case SPORT_BASEBALL: {
                                SPORT_MAP.put(SPORT_BASEBALL, actualDepartment);

                                List<TeamDTO> resultList = departmentController.loadDepartmentTeams(
                                    departmentId,
                                    CommunicationFacade.getActiveSession()
                                );

                                if (resultList != null) {

                                    //loading values to comboBox
                                    Platform.runLater(() -> memberTeamComboBoxBaseball.setItems(
                                        FXCollections.observableList(resultList)
                                    ));
                                }
                                break;
                            }

                            case SPORT_FOOTBALL: {
                                SPORT_MAP.put(SPORT_FOOTBALL, actualDepartment);

                                List<TeamDTO> resultList = departmentController.loadDepartmentTeams(
                                    departmentId,
                                    CommunicationFacade.getActiveSession()
                                );

                                if (resultList != null) {

                                    //loading values to comboBox
                                    Platform.runLater(() -> memberTeamComboBoxFootball.setItems(
                                        FXCollections.observableList(resultList)
                                    ));
                                }
                                break;
                            }
                        }
                    }
                }
            } catch (RemoteException | MalformedURLException | NotBoundException | UnknownEntityException e) {
                LOGGER.error("Error occurred while loading all Departments and their Teams.", e);
            } catch (NotAuthorisedException e) {
                //TODO
                e.printStackTrace();
            }
        }).start();
	}

	@Override
    public void displayDTO(IDTO idto){
        if (idto instanceof MemberDTO) {
            displayMemberDTO((MemberDTO) idto);
        }
    }

    /**
     * Pre-loads data into all view fields.
     * @param memberDTO MemberDTO that will be preloaded.
     */
    private void displayMemberDTO(MemberDTO memberDTO) {

        //clear form
        dispose();

        if (memberDTO != null) {
            _activeMemberDTO = memberDTO;

            fNameTextField.setText(_activeMemberDTO.getFirstName());
            lNameTextField.setText(_activeMemberDTO.getLastName());

            if (FEMALE.equals(_activeMemberDTO.getGender())) {
                radioGenderFemale.setSelected(true);
            } else if (MALE.equals(_activeMemberDTO.getGender())) {
                radioGenderMale.setSelected(true);
            }

            birthTextField.setText(_activeMemberDTO.getDateOfBirth());
            emailTextField.setText(_activeMemberDTO.getEmail());
            addressTextField.setText(_activeMemberDTO.getAddress());

            try {

                IMemberController memberController = CommunicationFacade.lookupForMemberController();

                List<DepartmentDTO> departments = memberController.loadMemberDepartments(
                    _activeMemberDTO.getMemberId(),
                    CommunicationFacade.getActiveSession()
                );

                if (departments != null) {

                    for (DepartmentDTO department : departments) {
                        switch (department.getSport()) {
                            case SPORT_VOLLEYBALL: {
                                Platform.runLater(() -> memberSportCheckboxVolleyball.setSelected(true));
                                break;
                            }

                            case SPORT_FOOTBALL: {
                                Platform.runLater(() -> memberSportCheckboxFootball.setSelected(true));
                                break;
                            }

                            case SPORT_BASEBALL: {
                                Platform.runLater(() -> memberSportCheckboxBaseball.setSelected(true));
                                break;
                            }

                            case SPORT_SOCCER: {
                                Platform.runLater(() -> memberSportCheckboxSoccer.setSelected(true));
                                break;
                            }
                        }
                    }
                }


                List<TeamDTO> teams = memberController.loadMemberTeams(
                    _activeMemberDTO.getMemberId(),
                    CommunicationFacade.getActiveSession()
                );

                if (teams != null) {

                    for (TeamDTO team : teams) {

                        DepartmentDTO department = CommunicationFacade.lookupForTeamController().loadTeamDepartment(
                            team.getTeamId(),
                            CommunicationFacade.getActiveSession()
                        );

                        switch (department.getSport()) {
                            case SPORT_VOLLEYBALL: {
                                Platform.runLater(() -> {
                                    memberSportCheckboxVolleyball.setSelected(true);
                                    memberTeamComboBoxSoccer.setDisable(false);
                                    memberTeamComboBoxVolleyball.getSelectionModel().select(team);
                                });

                                break;
                            }

                            case SPORT_FOOTBALL: {
                                Platform.runLater(() -> {
                                    memberSportCheckboxFootball.setSelected(true);
                                    memberTeamComboBoxSoccer.setDisable(false);
                                    memberTeamComboBoxFootball.getSelectionModel().select(team);
                                });

                                break;
                            }

                            case SPORT_BASEBALL: {
                                Platform.runLater(() -> {
                                    memberSportCheckboxBaseball.setSelected(true);
                                    memberTeamComboBoxSoccer.setDisable(false);
                                    memberTeamComboBoxBaseball.getSelectionModel().select(team);
                                });

                                break;
                            }

                            case SPORT_SOCCER: {
                                Platform.runLater(() -> {
                                    memberSportCheckboxSoccer.setSelected(true);
                                    memberTeamComboBoxSoccer.setDisable(false);
                                    memberTeamComboBoxSoccer.getSelectionModel().select(team);
                                });

                                break;
                            }
                        }
                    }
                }

            } catch (RemoteException | MalformedURLException | NotBoundException | UnknownEntityException e) {
                LOGGER.error("Error occurred while loading Member data (Teams).", e);
            } catch (NotAuthorisedException e) {
                //TODO
                e.printStackTrace();
            }


            String role = _activeMemberDTO.getRole();

            if (role != null) {

	            switch (role) {
                    case "Department Head": {
                        roleComboBox.getSelectionModel().select(RoleType.DEPARTMENT_HEAD);
                        break;
                    }

                    case "Member": {
                        roleComboBox.getSelectionModel().select(RoleType.MEMBER);
                        break;
                    }

                    case "Trainer": {
                        roleComboBox.getSelectionModel().select(RoleType.TRAINER);
                        break;
                    }

                    case "Managing Comittee": {
                        roleComboBox.getSelectionModel().select(RoleType.MANAGING_COMMITTEE);
                        break;
                    }
	            }
            }
        }
    }

    /**
     * Save Form-Method
     * saves the member data into the database
     */
    @FXML
    private void saveForm(ActionEvent actionEvent) {
        String fName = GUIHelper.readNullOrEmpty(fNameTextField.getText());
        String lName = GUIHelper.readNullOrEmpty(lNameTextField.getText());
        String bday = GUIHelper.readNullOrEmpty(birthTextField.getText());
        String email = GUIHelper.readNullOrEmpty(emailTextField.getText());
        String address = GUIHelper.readNullOrEmpty(addressTextField.getText());
        //TODO refactor phone to squad
        String phone = GUIHelper.readNullOrEmpty(phoneTextField.getText());
        String gender = null;
        
        if (radioGenderFemale.isSelected()) {
            gender = FEMALE;
        } else if (radioGenderMale.isSelected()) {
            gender = MALE;
        }

        //check if mandatory fields are filled with data,
        //validation was moved to a separate method for refactoring convenience (IDTO)
        if(isValidForm(fName, lName, bday, gender, address)) {
            try {

                //check if we are creating a new or editing an existing Member
                if (_activeMemberDTO == null) {
                    //this is a new Member
                    _activeMemberDTO = new MemberDTO();
                }

                //if it is an already existing member, changed member data will be simply updated.
                _activeMemberDTO.setFirstName(fName)
                        .setLastName(lName)
                        .setGender(gender)
                        .setDateOfBirth(bday)
                        .setEmail(email)
                        .setAddress(address)
                        .setIsFeePaid(false)
                        .setRole(roleComboBox.getSelectionModel().getSelectedItem().toString());
                        //TODO
                        //.setSquad(squad)

                IMemberController memberController = CommunicationFacade.lookupForMemberController();
                Integer memberId = memberController.createOrSaveMember(
                    _activeMemberDTO,
                    CommunicationFacade.getActiveSession()
                );

                saveOrUpdateMemberDepartments(memberId);
                saveOrUpdateMemberTeams(memberId);

                //FIXME we will get success message even if teams and departments will be not assigned
                GUIHelper.showSuccessAlert(SUCCESSFUL_MEMBER_SAVE);

                //Logging and cleaning the tab
                LOGGER.info("Member \"{} {}\" was successfully saved.", fName, lName);
                dispose();

            } catch (RemoteException | MalformedURLException | NotBoundException e) {
                LOGGER.error("Error occurred while saving the member.", e);
            } catch (ValidationException e) {
            	String context = String.format("Validation exception %s while saving member.", e.getCause());
            	
            	GUIHelper.showValidationAlert(context);
				LOGGER.error(context, e);
			} catch (NotAuthorisedException e) {
                //TODO
                e.printStackTrace();
            }
        }
    }

    private void saveOrUpdateMemberDepartments(Integer memberId) {
        try {
            IMemberController memberController = CommunicationFacade.lookupForMemberController();

            if (memberSportCheckboxBaseball.isSelected()) {
                //Saving department for member
                memberController.assignMemberToDepartment(
                    memberId,
                    SPORT_MAP.get(SPORT_BASEBALL).getDepartmentId(),
                    CommunicationFacade.getActiveSession()
                );
            }

            if (memberSportCheckboxFootball.isSelected()) {
                //Saving department for member
                memberController.assignMemberToDepartment(
                    memberId,
                    SPORT_MAP.get(SPORT_FOOTBALL).getDepartmentId(),
                    CommunicationFacade.getActiveSession()
                );
            }

            if (memberSportCheckboxSoccer.isSelected()) {
                //Saving department for member
                memberController.assignMemberToDepartment(
                    memberId,
                    SPORT_MAP.get(SPORT_SOCCER).getDepartmentId(),
                    CommunicationFacade.getActiveSession()
                );
            }

            if (memberSportCheckboxVolleyball.isSelected()) {
                //Saving department for member
                memberController.assignMemberToDepartment(
                    memberId,
                    SPORT_MAP.get(SPORT_VOLLEYBALL).getDepartmentId(),
                    CommunicationFacade.getActiveSession()
                );
            }

        } catch (RemoteException | MalformedURLException | NotBoundException e) {
            LOGGER.error("Error occurred while assigning Member to Department.", e);
        } catch (UnknownEntityException e) {
            LOGGER.error("DTO was not saved in Data Storage before assigning Member to Department.", e);
        } catch (NotAuthorisedException e) {
            //TODO
            e.printStackTrace();
        }
    }

    private void saveOrUpdateMemberTeams(Integer memberId) {
        try {

            IMemberController memberController = CommunicationFacade.lookupForMemberController();

            if (memberSportCheckboxBaseball.isSelected()) {
                //Baseball team holen
                TeamDTO teamDTO = memberTeamComboBoxBaseball.getSelectionModel().getSelectedItem();

                //Saving team if available;
                if (teamDTO != null) {
                    memberController.assignMemberToTeam(
                        memberId,
                        teamDTO.getTeamId(),
                        CommunicationFacade.getActiveSession()
                    );
                }
            }

            if (memberSportCheckboxFootball.isSelected()) {
                //Football team holen
                TeamDTO teamDTO = memberTeamComboBoxFootball.getSelectionModel().getSelectedItem();

                //Saving team if available;
                if (teamDTO != null) {
                    memberController.assignMemberToTeam(
                        memberId,
                        teamDTO.getTeamId(),
                        CommunicationFacade.getActiveSession()
                    );
                }
            }

            if (memberSportCheckboxSoccer.isSelected()) {
                //Soccer team holen
                TeamDTO teamDTO = memberTeamComboBoxSoccer.getSelectionModel().getSelectedItem();

                //Saving team if available;
                if (teamDTO != null) {
                    memberController.assignMemberToTeam(
                        memberId,
                        teamDTO.getTeamId(),
                        CommunicationFacade.getActiveSession()
                    );
                }
            }

            if (memberSportCheckboxVolleyball.isSelected()) {
                //Volleyball team holen
                TeamDTO teamDTO = memberTeamComboBoxVolleyball.getSelectionModel().getSelectedItem();

                //Saving team if available;
                if (teamDTO != null) {
                    memberController.assignMemberToTeam(
                        memberId,
                        teamDTO.getTeamId(),
                        CommunicationFacade.getActiveSession()
                    );
                }
            }

        } catch (RemoteException | MalformedURLException | NotBoundException e) {
            LOGGER.error("Error occurred while assigning Member to Team.", e);
        } catch (UnknownEntityException e) {
            LOGGER.error("DTO was not saved in Data Storage before assigning Member to Team.", e);
        } catch (NotAuthorisedException e) {
            //TODO
            e.printStackTrace();
        }
    }

	private boolean isValidForm(String fName, String lName, String bday, String gender, String address) {
        //Alert Box if a mandatory field is not filled
        if (fName == null) {
            GUIHelper.highlightNotValidTextField(fNameTextField);
            GUIHelper.showValidationAlert("Please fill in First Name field.");

            return false;
        }

        if (lName == null) {
            GUIHelper.highlightNotValidTextField(lNameTextField);
            GUIHelper.showValidationAlert("Please fill in Last Name field.");

            return false;
        }

        if (bday == null) {
            GUIHelper.highlightNotValidTextField(birthTextField);
            GUIHelper.showValidationAlert("Please fill in Date of Birth field.");

            return false;
        }

        if (gender == null) {
            GUIHelper.showValidationAlert("Please choose Gender.");

            return false;
        }
        
        if (address == null) {
            GUIHelper.highlightNotValidTextField(addressTextField);
            GUIHelper.showValidationAlert("Please fill in Address.");

            return false;
        }

        return true;
    }
	
	@FXML
    private void clearFields(ActionEvent actionEvent) {
        dispose();
	}

    @Override
    public void dispose() {
        _activeMemberDTO = null;

        fNameTextField.clear();
        lNameTextField.clear();
        birthTextField.clear();
        emailTextField.clear();
        phoneTextField.clear();
        addressTextField.clear();

        radioGenderFemale.setSelected(false);
        radioGenderMale.setSelected(false);

        memberSportCheckboxBaseball.setSelected(false);
        memberSportCheckboxFootball.setSelected(false);
        memberSportCheckboxSoccer.setSelected(false);
        memberSportCheckboxVolleyball.setSelected(false);

        memberTeamComboBoxBaseball.setDisable(true);
        memberTeamComboBoxFootball.setDisable(true);
        memberTeamComboBoxSoccer.setDisable(true);
        memberTeamComboBoxVolleyball.setDisable(true);

        memberTeamComboBoxSoccer.getSelectionModel().select(null);
        memberTeamComboBoxSoccer.setValue(null);

        memberTeamComboBoxVolleyball.getSelectionModel().select(null);
        memberTeamComboBoxSoccer.setValue(null);

        memberTeamComboBoxBaseball.getSelectionModel().select(null);
        memberTeamComboBoxSoccer.setValue(null);

        memberTeamComboBoxFootball.getSelectionModel().select(null);
        memberTeamComboBoxSoccer.setValue(null);

        roleComboBox.getSelectionModel().select(RoleType.MEMBER);
    }
    
    private enum RoleType {
        MEMBER("Member"),
    	TRAINER("Trainer"),
        DEPARTMENT_HEAD("Department Head"),
        MANAGING_COMMITTEE("Managing Committee");

        private final String _stringValue;

        RoleType(String stringValue) {
            _stringValue = stringValue;
        }

        @Override
        public String toString() {
            return _stringValue;
        }
    }
}
