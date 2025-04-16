package tn.esprit.Controllers;



import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;
import tn.esprit.models.User;
import tn.esprit.services.UserService;

import java.net.URL;
import java.util.ResourceBundle;

public class UserListController implements Initializable {

    @FXML
    private TableView<User> userTable;

    @FXML
    private TableColumn<User, String> nameColumn;

    @FXML
    private TableColumn<User, String> lastnameColumn;

    @FXML
    private TableColumn<User, String> emailColumn;

    @FXML
    private TableColumn<User, String> numtelColumn;

    @FXML
    private TableColumn<User, String> adresseColumn;

    @FXML
    private TableColumn<User, Integer> ageColumn;

    @FXML
    private TableColumn<User, String> roleColumn;

    @FXML
    private TableColumn<User, String> specialtyColumn;

    @FXML
    private TableColumn<User, String> created_atColumn;

    @FXML
    private TableColumn<User, Boolean> is_bannedColumn;

    @FXML
    private TableColumn<User, Boolean> enabledColumn;

    @FXML
    private TableColumn<User, Void> actionsColumn;

    @FXML
    private TextField txtSearch;

    private UserService service;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Initialisation de UserListController");
        service = new UserService();

        // Configuration des colonnes
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        lastnameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        numtelColumn.setCellValueFactory(new PropertyValueFactory<>("numTel"));
        adresseColumn.setCellValueFactory(new PropertyValueFactory<>("adresse"));
        ageColumn.setCellValueFactory(new PropertyValueFactory<>("age"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        specialtyColumn.setCellValueFactory(new PropertyValueFactory<>("specialty"));
        created_atColumn.setCellValueFactory(new PropertyValueFactory<>("created_at"));
        is_bannedColumn.setCellValueFactory(new PropertyValueFactory<>("is_banned"));
        enabledColumn.setCellValueFactory(new PropertyValueFactory<>("enabled"));

        // Configuration de la colonne d'actions
        configurerColonneActions();

        // Chargement des données
        chargerDonnees();
    }

    private void configurerColonneActions() {
        Callback<TableColumn<User, Void>, TableCell<User, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<User, Void> call(final TableColumn<User, Void> param) {
                return new TableCell<>() {
                    private final Button btnModifier = new Button("Modifier");
                    private final Button btnSupprimer = new Button("Supprimer");
                    private final HBox hbox = new HBox(5, btnModifier, btnSupprimer);

                    {
                        btnModifier.getStyleClass().add("edit-button");
                        btnSupprimer.getStyleClass().add("delete-button");
                        hbox.setAlignment(javafx.geometry.Pos.CENTER);

                        btnModifier.setOnAction(event -> {
                            User user = getTableView().getItems().get(getIndex());
                            modifierUtilisateur(user);
                        });

                        btnSupprimer.setOnAction(event -> {
                            User user = getTableView().getItems().get(getIndex());
                            supprimerUtilisateur(user);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(hbox);
                        }
                    }
                };
            }
        };

        actionsColumn.setCellFactory(cellFactory);
    }

    public void chargerDonnees() {
        try {
            System.out.println("Chargement des données...");
            var users = service.rechercher();
            System.out.println("Nombre d'utilisateurs trouvés: " + users.size());
            userTable.setItems(FXCollections.observableArrayList(users));
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement des données: " + e.getMessage());
        }
    }

    @FXML
    private void handleSearch() {
        String query = txtSearch.getText().trim();
        if (!query.isEmpty()) {
            var users = service.rechercher(query);
            userTable.setItems(FXCollections.observableArrayList(users));
        } else {
            chargerDonnees(); // Si la recherche est vide, afficher tous les utilisateurs
        }
    }

    @FXML
    private void handleAddAssurance() {
        try {
            StackPane contentArea = (StackPane) userTable.getScene().lookup("#contentArea");
            Label pageTitleLabel = (Label) userTable.getScene().lookup("#pageTitleLabel");

            if (pageTitleLabel != null) {
                pageTitleLabel.setText("Nouvel Utilisateur");
            }

            if (contentArea != null) {
                URL url = getClass().getResource("/UserForm.fxml");
                System.out.println("URL du formulaire: " + url);

                if (url == null) {
                    System.err.println("Erreur: Ressource UserForm.fxml introuvable");
                    return;
                }

                Parent view = FXMLLoader.load(url);
                contentArea.getChildren().clear();
                contentArea.getChildren().add(view);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur de navigation: " + e.getMessage());
        }
    }

    private void modifierUtilisateur(User user) {
        try {
            StackPane contentArea = (StackPane) userTable.getScene().lookup("#contentArea");
            Label pageTitleLabel = (Label) userTable.getScene().lookup("#pageTitleLabel");

            if (pageTitleLabel != null) {
                pageTitleLabel.setText("Modifier Utilisateur");
            }

            if (contentArea != null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserForm.fxml"));
                Parent view = loader.load();

                // Récupérer le contrôleur et passer l'utilisateur
                UserFormController controller = loader.getController();
                controller.setUser(user);

                contentArea.getChildren().clear();
                contentArea.getChildren().add(view);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur de navigation: " + e.getMessage());
        }
    }

    private void supprimerUtilisateur(User user) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer l'utilisateur '" + user.getName() + "' ?");

        if (confirmation.showAndWait().get() == ButtonType.OK) {
            service.supprimer(user);
            chargerDonnees(); // Rafraîchir la liste
        }
    }

    @FXML
    private void handleRefresh() {
        txtSearch.clear();
        chargerDonnees();
    }
}