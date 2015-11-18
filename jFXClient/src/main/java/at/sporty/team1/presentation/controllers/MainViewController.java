package at.sporty.team1.presentation.controllers;

import at.sporty.team1.communication.CommunicationFacade;
import at.sporty.team1.presentation.ViewLoader;
import at.sporty.team1.presentation.controllers.core.IJfxController;
import at.sporty.team1.presentation.controllers.core.JfxController;
import at.sporty.team1.rmi.api.IDTO;
import at.sporty.team1.rmi.api.IMemberController;
import at.sporty.team1.rmi.dtos.MemberDTO;
import at.sporty.team1.rmi.enums.UserRole;
import at.sporty.team1.rmi.exceptions.ValidationException;
import at.sporty.team1.util.GUIHelper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.*;

public class MainViewController extends JfxController {

    private static final String NOT_VALID_SEARCH_INPUT = "Not valid search input";
    private static final String NO_RESULTS_TITLE = "No results";
    private static final String NO_RESULTS_CONTEXT = "No results were found.";
    private static final String TEAM_TAB_CAPTION = "TEAM";
    private static final String MEMBER_TAB_CAPTION = "MEMBER";
    private static final String COMPETITION_TAB_CAPTION = "NEW COMPETITION";

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<Tab, IJfxController> CONTROLLER_TO_TAB_MAP = new HashMap<>();
    private boolean _checkboxPaid = false;
    private boolean _checkboxNotPaid = false;

    @FXML private TextField _searchField;
    @FXML private ComboBox<SearchType> _searchType;
    @FXML private TabPane _tabPanel;
    @FXML private BorderPane _borderPanel;
    @FXML private CheckBox _feePaidCheckbox;
    @FXML private CheckBox _feeNotPaidCheckbox;

    private SearchResultViewController _searchResultViewController;
    
    private UserRole _userRole;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
        //loading left search results panel
        new Thread(() -> {

            ViewLoader<SearchResultViewController> viewLoader = ViewLoader.loadView(SearchResultViewController.class);
            Node node = viewLoader.loadNode();
            _searchResultViewController = viewLoader.getController();
            _searchResultViewController.setTargetConsumer(this::delegateToMemberTarget);

            Platform.runLater(() -> _borderPanel.setLeft(node));

        }).start();


        //Enter listener for search table
        _searchField.setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.ENTER){
                startSearch();
            }
        });

        //adding search types to ComboBox;
        _searchType.setItems(FXCollections.observableList(Arrays.asList(SearchType.values())));
        _searchType.getSelectionModel().select(SearchType.MEMBER_NAME);

        //Opening un-closable tabs
        openMemberView(false);
        openTeamView(false);
        openCompetitionView(false);
        
        _feeNotPaidCheckbox.setOnAction(e -> _checkboxNotPaid = _feeNotPaidCheckbox.isSelected());
        
        _feePaidCheckbox.setOnAction(e -> _checkboxPaid = _feePaidCheckbox.isSelected());
    }

    public void setUserRole(UserRole userRole){
        _userRole = userRole;
    }

    @FXML
    private void startSearch() {
    	
        String searchQuery = GUIHelper.readNullOrEmpty(_searchField.getText());

        if 	(searchQuery != null) {
        	
            _searchResultViewController.showProgressAnimation();

            new Thread(() -> {
                try {

                    IMemberController memberController = CommunicationFacade.lookupForMemberController();

                    //Performing search depending on selected search type
                    switch(_searchType.getValue()) {
                        case MEMBER_NAME: {
                            displaySearchResults(
                                memberController.searchMembersByNameString(
                                    searchQuery,
                                    _checkboxNotPaid,
                                    _checkboxPaid
                                )
                            );

                            break;
                        }

                        case DATE_OF_BIRTH: {
                            displaySearchResults(
                                memberController.searchMembersByDateOfBirth(
                                    searchQuery,
                                    _checkboxNotPaid,
                                    _checkboxPaid
                                )
                            );

                            break;
                        }

                        case COMMON_TEAM_NAME: {
                            displaySearchResults(
                                memberController.searchMembersByCommonTeamName(
                                    searchQuery,
                                    _checkboxNotPaid,
                                    _checkboxPaid
                                )
                            );

                            break;
                        }

                        case TOURNAMENT_TEAM_NAME: {
                            displaySearchResults(
                                memberController.searchMembersByTournamentTeamName(
                                    searchQuery,
                                    _checkboxNotPaid,
                                    _checkboxPaid
                                )
                            );

                            break;
                        }
                    }

                } catch (RemoteException | MalformedURLException | NotBoundException e) {
                    LOGGER.error("Error occurs while searching.", e);
                } catch (ValidationException e) {
                    LOGGER.error("Error occurs while searching.", e);

                    Platform.runLater(() -> {
                        GUIHelper.showValidationAlert(NOT_VALID_SEARCH_INPUT);
                        _searchResultViewController.displayResults(null);
                    });
                }

            }).start();
            
        } else {
        	_searchResultViewController.showProgressAnimation();
        	
        	new Thread(() -> {
                try {

                    IMemberController memberController = CommunicationFacade.lookupForMemberController();
                    displaySearchResults(memberController.searchAllMembers( _checkboxNotPaid, _checkboxPaid));

                } catch (RemoteException | MalformedURLException | NotBoundException e) {
                    LOGGER.error("Error occurs while searching.", e);
                }

            }).start();
        }
    }

    private void displaySearchResults(List<MemberDTO> rawSearchResults) {
        if(rawSearchResults != null && !rawSearchResults.isEmpty()){

            Platform.runLater(() -> _searchResultViewController.displayResults(rawSearchResults));

        }else{
            Platform.runLater(() -> {
                GUIHelper.showCustomAlert(
                    Alert.AlertType.INFORMATION,
                    NO_RESULTS_TITLE,
                    null,
                    NO_RESULTS_CONTEXT
                );

                _searchResultViewController.displayResults(null);
            });
        }
    }

	private void openMemberView(boolean closable) {
        openNewTab(MEMBER_TAB_CAPTION, closable, null, MemberViewController.class);
	}

    private void openTeamView(boolean closable) {
        openNewTab(TEAM_TAB_CAPTION, closable, null, TeamViewController.class);
    }
    
    private void openCompetitionView(boolean closable) {
        openNewTab(COMPETITION_TAB_CAPTION, closable, null, CompetitionViewController.class);
    }

    private void delegateToMemberTarget(MemberDTO dto) {
        Tab activeTab = _tabPanel.getSelectionModel().getSelectedItem();
        if (activeTab != null) {

            IJfxController controller = CONTROLLER_TO_TAB_MAP.get(activeTab);
            if (controller != null) {
                controller.displayDTO(dto);
            }

            //TODO reactivate when there will be more than 2 views;
            //else {
            //    //Default action if no tabs are selected or no tabs are available.
            //    openNewTab(MEMBER_TAB_CAPTION, false, dto, MemberViewController.class);
            //}
        }
    }

    private void openNewTab(
        String tabCaption,
        boolean closable,
        IDTO idto,
        Class<? extends IJfxController> controllerClass
    ) {

        new Thread(() -> {

            ViewLoader<? extends IJfxController> viewLoader = ViewLoader.loadView(controllerClass);
            Node node = viewLoader.loadNode();
            IJfxController controller = viewLoader.getController();

            //check if need to load a dto
            if (idto != null) controller.displayDTO(idto);

            Tab t = new Tab();
            t.setText(tabCaption);
            t.setContent(node);
            t.setClosable(closable);

            //registering bidirectional relation from tab to controller
            CONTROLLER_TO_TAB_MAP.put(t, controller);

            Platform.runLater(() -> {
                _tabPanel.getTabs().add(t);
                _tabPanel.getSelectionModel().select(t);
            });
        }).start();
    }

    private enum SearchType {
        MEMBER_NAME("member name"),
        DATE_OF_BIRTH("date of birth"),
        COMMON_TEAM_NAME("common team name"),
        TOURNAMENT_TEAM_NAME("tournament team name");

        private final String _stringValue;

        SearchType(String stringValue) {
            _stringValue = stringValue;
        }

        @Override
        public String toString() {
            return _stringValue;
        }
    }
}