package org.tonym.cs3560;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Main extends Application
{
      private static final String APP_TITLE = "CPP Library Management System";
      private static final String FONT_STYLE_HEADER = "-fx-font-size: 16px; -fx-font-weight: bold;";
      private static final String FONT_STYLE_SUBHEADER = "-fx-font-size: 14px; -fx-font-weight: bold;";
      private static final Insets PADDING_TEN = new Insets(10);
      private static final int LEFT_PANEL_WIDTH = 300;
      private static final int DETAILS_PANEL_WIDTH = 600;
      private static final int MAIN_LIST_HEIGHT = 400;
      private static final int CHILD_LIST_HEIGHT = 200;

      private final StudentManager studentManager = new StudentManager();
      private final LoanManager loanManager = new LoanManager();
      private final BookManager bookManager = new BookManager();

      private final ObservableList<Student> studentsList = FXCollections.observableArrayList();
      private final ObservableList<Book> booksList = FXCollections.observableArrayList();
      private final ObservableList<Loan> studentLoansList = FXCollections.observableArrayList();
      private final ObservableList<BookCopy> bookCopiesList = FXCollections.observableArrayList();

      private ListView<Student> studentListView;
      private ListView<Book> bookListView;
      private ListView<Loan> studentLoansListView;
      private ListView<BookCopy> bookCopiesListView;

      private VBox studentDetailsPanel;
      private TextField studentIdField, studentNameField, studentAddressField, studentDegreeField;

      private VBox bookDetailsPanel;
      private TextField bookIsbnField, bookTitleField, bookDescriptionField, bookAuthorsField;
      private TextField bookPagesField, bookPublisherField, bookPublicationDateField;

      public static void main(String[] args)
      {
	  HibernateHelper.getSessionFactory();
	  launch(args);
      }

      @Override
      public void start(Stage primaryStage)
      {
	  primaryStage.setTitle(APP_TITLE);

	  TabPane tabPane = new TabPane(createStudentsTab(), createBooksTab());
	  Scene scene = new Scene(tabPane, 1200, 800);
	  primaryStage.setScene(scene);

	  primaryStage.show();

	  refreshStudentsList();
	  refreshBooksList();
      }

      @Override
      public void stop()
      {
	  HibernateHelper.shutdown();
      }

      private Tab createStudentsTab()
      {
	  studentListView = new ListView<>(studentsList);
	  studentListView.setPrefHeight(MAIN_LIST_HEIGHT);
	  studentListView.getSelectionModel().selectedItemProperty().addListener((obs, old, aNew) -> displayStudentDetails(aNew));

	  Button addStudentBtn = new Button("Add Student");
	  addStudentBtn.setOnAction(e -> showAddStudentDialog());
	  Button deleteStudentBtn = new Button("Delete Student");
	  deleteStudentBtn.setOnAction(e -> deleteSelectedStudent());
	  HBox studentButtons = new HBox(5, addStudentBtn, deleteStudentBtn);

	  VBox leftPanel = createLeftPanel("Students", studentListView, studentButtons);

	  studentDetailsPanel = createStudentDetailsPanel();
	  studentDetailsPanel.setVisible(false);

	  HBox mainContainer = new HBox(10, leftPanel, new Separator(), studentDetailsPanel);
	  mainContainer.setPadding(PADDING_TEN);

	  Tab studentsTab = new Tab("Students");
	  studentsTab.setClosable(false);
	  studentsTab.setContent(mainContainer);
	  return studentsTab;
      }

      private Tab createBooksTab()
      {
	  bookListView = new ListView<>(booksList);
	  bookListView.setPrefHeight(MAIN_LIST_HEIGHT - 50);
	  bookListView.getSelectionModel().selectedItemProperty().addListener((obs, old, aNew) -> displayBookDetails(aNew));

	  TextField searchField = new TextField();
	  searchField.setPromptText("Search books...");
	  Button searchBtn = new Button("Search");
	  searchBtn.setOnAction(e -> searchBooks(searchField.getText()));
	  HBox searchBox = new HBox(5, searchField, searchBtn);

	  Button addBookBtn = new Button("Add Book");
	  addBookBtn.setOnAction(e -> showAddBookDialog());
	  Button showAllBtn = new Button("Show All");
	  showAllBtn.setOnAction(e -> refreshBooksList());
	  HBox bookButtons = new HBox(5, addBookBtn, showAllBtn);

	  VBox leftPanel = createLeftPanel("Books", searchBox, bookListView, bookButtons);

	  bookDetailsPanel = createBookDetailsPanel();
	  bookDetailsPanel.setVisible(false);

	  HBox mainContainer = new HBox(10, leftPanel, new Separator(), bookDetailsPanel);
	  mainContainer.setPadding(PADDING_TEN);

	  Tab booksTab = new Tab("Books");
	  booksTab.setClosable(false);
	  booksTab.setContent(mainContainer);
	  return booksTab;
      }

      private VBox createLeftPanel(String label, Node... children)
      {
	  Label headerLabel = new Label(label);
	  headerLabel.setStyle(FONT_STYLE_HEADER);
	  VBox leftPanel = new VBox(10, headerLabel);
	  leftPanel.getChildren().addAll(children);
	  leftPanel.setPrefWidth(LEFT_PANEL_WIDTH);
	  return leftPanel;
      }

      private VBox createStudentDetailsPanel()
      {
	  GridPane studentForm = createStudentForm();

	  Label loansLabel = createSubHeaderLabel("Student Loans");
	  studentLoansListView = new ListView<>(studentLoansList);
	  studentLoansListView.setPrefHeight(CHILD_LIST_HEIGHT);

	  Button addLoanBtn = new Button("Create Loan");
	  addLoanBtn.setOnAction(e -> showCreateLoanDialog());
	  Button returnLoanBtn = new Button("Return Loan");
	  returnLoanBtn.setOnAction(e -> returnSelectedLoan());
	  Button viewReceiptBtn = new Button("View Receipt");
	  viewReceiptBtn.setOnAction(e -> showLoanReceipt());
	  HBox loanButtons = new HBox(5, addLoanBtn, returnLoanBtn, viewReceiptBtn);

	  VBox detailsPanel = new VBox(10,
		createHeaderLabel("Student Details"), studentForm, new Separator(),
		loansLabel, studentLoansListView, loanButtons
	  );
	  detailsPanel.setPrefWidth(DETAILS_PANEL_WIDTH);
	  detailsPanel.setPadding(PADDING_TEN);
	  return detailsPanel;
      }

      private GridPane createStudentForm()
      {
	  GridPane grid = createFormGrid();
	  studentIdField = createReadOnlyTextField();
	  studentNameField = new TextField();
	  studentAddressField = new TextField();
	  studentDegreeField = new TextField();

	  addFormField(grid, "Bronco ID:", studentIdField, 0);
	  addFormField(grid, "Name:", studentNameField, 1);
	  addFormField(grid, "Address:", studentAddressField, 2);
	  addFormField(grid, "Degree:", studentDegreeField, 3);

	  Button updateStudentBtn = new Button("Update Student");
	  updateStudentBtn.setOnAction(e -> updateSelectedStudent());
	  grid.add(updateStudentBtn, 1, 4);
	  return grid;
      }

      private VBox createBookDetailsPanel()
      {
	  GridPane bookForm = createBookForm();

	  Label copiesLabel = createSubHeaderLabel("Book Copies");
	  bookCopiesListView = new ListView<>(bookCopiesList);
	  bookCopiesListView.setPrefHeight(CHILD_LIST_HEIGHT);
	  bookCopiesListView.setCellFactory(listView -> new ListCell<BookCopy>()
	  {
	        @Override
	        protected void updateItem(BookCopy copy, boolean empty)
	        {
		    super.updateItem(copy, empty);
		    if (empty || copy == null)
		    {
			setText(null);
		    } else
		    {
			setText("Barcode: " + copy.getBarCode() + " - Location: " + copy.getLocation());
		    }
	        }
	  });

	  Button addCopyBtn = new Button("Add Copy");
	  addCopyBtn.setOnAction(e -> showAddBookCopyDialog());
	  Button deleteCopyBtn = new Button("Delete Copy");
	  deleteCopyBtn.setOnAction(e -> deleteSelectedBookCopy());
	  HBox copyButtons = new HBox(5, addCopyBtn, deleteCopyBtn);

	  VBox detailsPanel = new VBox(10,
		createHeaderLabel("Book Details"), bookForm, new Separator(),
		copiesLabel, bookCopiesListView, copyButtons
	  );
	  detailsPanel.setPrefWidth(DETAILS_PANEL_WIDTH);
	  detailsPanel.setPadding(PADDING_TEN);
	  return detailsPanel;
      }

      private GridPane createBookForm()
      {
	  GridPane grid = createFormGrid();
	  bookIsbnField = createReadOnlyTextField();
	  bookTitleField = new TextField();
	  bookDescriptionField = new TextField();
	  bookAuthorsField = new TextField();
	  bookPagesField = new TextField();
	  bookPublisherField = new TextField();
	  bookPublicationDateField = new TextField();

	  addFormField(grid, "ISBN:", bookIsbnField, 0);
	  addFormField(grid, "Title:", bookTitleField, 1);
	  addFormField(grid, "Description:", bookDescriptionField, 2);
	  addFormField(grid, "Authors:", bookAuthorsField, 3);
	  addFormField(grid, "Pages:", bookPagesField, 4);
	  addFormField(grid, "Publisher:", bookPublisherField, 5);
	  addFormField(grid, "Publication Date:", bookPublicationDateField, 6);

	  Button updateBookBtn = new Button("Update Book");
	  updateBookBtn.setOnAction(e -> updateSelectedBook());
	  grid.add(updateBookBtn, 1, 7);
	  return grid;
      }

      private GridPane createFormGrid()
      {
	  GridPane grid = new GridPane();
	  grid.setHgap(10);
	  grid.setVgap(10);
	  return grid;
      }

      private void addFormField(GridPane grid, String label, Node field, int rowIndex)
      {
	  grid.add(new Label(label), 0, rowIndex);
	  grid.add(field, 1, rowIndex);
      }

      private Label createHeaderLabel(String text)
      {
	  Label label = new Label(text);
	  label.setStyle(FONT_STYLE_HEADER);
	  return label;
      }

      private Label createSubHeaderLabel(String text)
      {
	  Label label = new Label(text);
	  label.setStyle(FONT_STYLE_SUBHEADER);
	  return label;
      }

      private TextField createReadOnlyTextField()
      {
	  TextField textField = new TextField();
	  textField.setEditable(false);
	  return textField;
      }

      private void refreshStudentsList()
      {
	  studentsList.clear();
	  studentsList.addAll(studentManager.getAllStudents());
      }

      private void refreshBooksList()
      {
	  booksList.clear();
	  booksList.addAll(bookManager.getAllBooks());
      }

      private void displayStudentDetails(Student student)
      {
	  if (student == null)
	  {
	        studentDetailsPanel.setVisible(false);
	        return;
	  }
	  studentIdField.setText(String.valueOf(student.getBroncoId()));
	  studentNameField.setText(student.getName());
	  studentAddressField.setText(student.getAddress());
	  studentDegreeField.setText(student.getDegree());

	  studentLoansList.clear();
	  studentLoansList.addAll(student.getLoans());
	  studentDetailsPanel.setVisible(true);
      }

      private void displayBookDetails(Book book)
      {
	  if (book == null)
	  {
	        bookDetailsPanel.setVisible(false);
	        return;
	  }
	  bookIsbnField.setText(book.getIsbn());
	  bookTitleField.setText(book.getTitle());
	  bookDescriptionField.setText(book.getDescription());
	  bookAuthorsField.setText(book.getAuthors());
	  bookPagesField.setText(String.valueOf(book.getNumPages()));
	  bookPublisherField.setText(book.getPublisher());
	  bookPublicationDateField.setText(book.getPublicationDate() != null ? book.getPublicationDate().toString() : "");

	  bookCopiesList.clear();
	  bookCopiesList.addAll(book.getBookCopies());
	  bookDetailsPanel.setVisible(true);
      }


      private void showAddStudentDialog()
      {
	  Dialog<Student> dialog = new Dialog<>();
	  dialog.setTitle("Add New Student");
	  dialog.setHeaderText("Enter student information");

	  ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
	  dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

	  GridPane grid = createFormGrid();
	  grid.setPadding(new Insets(20, 150, 10, 10));
	  TextField broncoId = new TextField();
	  broncoId.setPromptText("Bronco ID");
	  TextField name = new TextField();
	  name.setPromptText("Name");
	  TextField address = new TextField();
	  address.setPromptText("Address");
	  TextField degree = new TextField();
	  degree.setPromptText("Degree");

	  addFormField(grid, "Bronco ID:", broncoId, 0);
	  addFormField(grid, "Name:", name, 1);
	  addFormField(grid, "Address:", address, 2);
	  addFormField(grid, "Degree:", degree, 3);

	  dialog.getDialogPane().setContent(grid);

	  dialog.setResultConverter(dialogButton ->
	  {
	        if (dialogButton == addButtonType)
	        {
		    try
		    {
			int id = Integer.parseInt(broncoId.getText());
			return new Student(id, name.getText(), address.getText(), degree.getText());
		    } catch (NumberFormatException e)
		    {
			showAlert("Invalid Bronco ID", "Please enter a valid numeric Bronco ID.");
			return null;
		    }
	        }
	        return null;
	  });

	  dialog.showAndWait().ifPresent(newStudent ->
	  {
	        try
	        {
		    studentManager.createStudent(newStudent.getBroncoId(), newStudent.getName(),
			  newStudent.getAddress(), newStudent.getDegree());
		    refreshStudentsList();
		    showAlert("Success", "Student added successfully!");
	        } catch (Exception e)
	        {
		    showAlert("Error", "Failed to add student: " + e.getMessage());
	        }
	  });
      }

      private void showAddBookDialog()
      {
	  Dialog<Book> dialog = new Dialog<>();
	  dialog.setTitle("Add New Book");
	  dialog.setHeaderText("Enter book information");

	  ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
	  dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

	  GridPane grid = createFormGrid();
	  grid.setPadding(new Insets(20, 150, 10, 10));

	  TextField isbn = new TextField();
	  TextField title = new TextField();
	  TextField description = new TextField();
	  TextField authors = new TextField();
	  TextField numPages = new TextField();
	  TextField publisher = new TextField();
	  TextField publicationDate = new TextField();
	  publicationDate.setPromptText("YYYY-MM-DD");

	  addFormField(grid, "ISBN:", isbn, 0);
	  addFormField(grid, "Title:", title, 1);
	  addFormField(grid, "Description:", description, 2);
	  addFormField(grid, "Authors:", authors, 3);
	  addFormField(grid, "Pages:", numPages, 4);
	  addFormField(grid, "Publisher:", publisher, 5);
	  addFormField(grid, "Publication Date:", publicationDate, 6);

	  dialog.getDialogPane().setContent(grid);

	  dialog.setResultConverter(dialogButton ->
	  {
	        if (dialogButton == addButtonType)
	        {
		    try
		    {
			int pages = Integer.parseInt(numPages.getText());
			LocalDate pubDate = LocalDate.parse(publicationDate.getText());
			return new Book(isbn.getText(), title.getText(), description.getText(),
			        authors.getText(), pages, publisher.getText(), pubDate);
		    } catch (NumberFormatException e)
		    {
			showAlert("Invalid Pages", "Please enter a valid number for pages.");
			return null;
		    } catch (Exception e)
		    {
			showAlert("Invalid Date", "Please enter date in YYYY-MM-DD format.");
			return null;
		    }
	        }
	        return null;
	  });

	  dialog.showAndWait().ifPresent(newBook ->
	  {
	        try
	        {
		    Book createdBook = bookManager.createBook(newBook.getIsbn(), newBook.getTitle(),
			  newBook.getDescription(), newBook.getAuthors(), newBook.getNumPages(),
			  newBook.getPublisher(), newBook.getPublicationDate());
		    if (createdBook != null)
		    {
			refreshBooksList();
			showAlert("Success", "Book added successfully!");
		    } else
		    {
			showAlert("Duplicate ISBN", "A book with this ISBN already exists.");
		    }
	        } catch (Exception e)
	        {
		    showAlert("Error", "Failed to add book: " + e.getMessage());
	        }
	  });
      }

      private void showCreateLoanDialog()
      {
	  Student selectedStudent = studentListView.getSelectionModel().getSelectedItem();
	  if (selectedStudent == null)
	  {
	        showAlert("No Selection", "Please select a student first.");
	        return;
	  }

	  Dialog<List<BookCopy>> loanDialog = new Dialog<>();
	  loanDialog.setTitle("Create Loan");
	  loanDialog.setHeaderText("Select books to loan to " + selectedStudent.getName());
	  loanDialog.getDialogPane().setPrefWidth(600);
	  ButtonType createButtonType = new ButtonType("Create Loan", ButtonBar.ButtonData.OK_DONE);
	  loanDialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

	  TextField durationField = new TextField("30");
	  VBox content = createLoanDialogContent(durationField);
	  loanDialog.getDialogPane().setContent(content);

	  ListView<BookCopy> availableBooksListView = (ListView<BookCopy>) content.lookup("#availableBooksList");
	  ListView<BookCopy> selectedBooksListView = (ListView<BookCopy>) content.lookup("#selectedBooksList");
	  ObservableList<BookCopy> selectedBooksObservable = selectedBooksListView.getItems();

	  loadAvailableBooks(availableBooksListView, "");

	  loanDialog.setResultConverter(dialogButton -> (dialogButton == createButtonType) ? new ArrayList<>(selectedBooksObservable) : null);

	  loanDialog.showAndWait().ifPresent(selectedBookCopies ->
	  {
	        if (selectedBookCopies.isEmpty())
	        {
		    showAlert("No Books Selected", "Please select at least one book for the loan.");
		    return;
	        }
	        try
	        {
		    int duration = Integer.parseInt(durationField.getText());
		    if (duration <= 0)
		    {
			showAlert("Invalid Duration", "Loan duration must be a positive number.");
			return;
		    }
		    Loan loan = loanManager.createLoan(selectedStudent, selectedBookCopies, duration);
		    refreshStudentsList();
		    displayStudentDetails(selectedStudent);
		    showAlert("Success", "Loan created successfully! Loan #" + loan.getLoanNumber());
	        } catch (NumberFormatException e)
	        {
		    showAlert("Invalid Duration", "Please enter a valid number for loan duration.");
	        } catch (Exception e)
	        {
		    showAlert("Error", "Failed to create loan: " + e.getMessage());
	        }
	  });
      }

      private VBox createLoanDialogContent(TextField durationField)
      {
	  HBox durationBox = new HBox(10, new Label("Loan Duration (days):"), durationField);
	  durationBox.setAlignment(Pos.CENTER_LEFT);
	  durationField.setPrefWidth(100);

	  TextField bookSearchField = new TextField();
	  bookSearchField.setPromptText("Search books...");
	  Button searchBooksBtn = new Button("Search");

	  ListView<BookCopy> availableBooksListView = new ListView<>();
	  availableBooksListView.setId("availableBooksList");
	  availableBooksListView.setPrefHeight(200);
	  searchBooksBtn.setOnAction(e -> loadAvailableBooks(availableBooksListView, bookSearchField.getText()));

	  ListView<BookCopy> selectedBooksListView = new ListView<>(FXCollections.observableArrayList());
	  selectedBooksListView.setId("selectedBooksList");
	  selectedBooksListView.setPrefHeight(150);

	  Button addBookBtn = new Button("Add →");
	  addBookBtn.setOnAction(e ->
	  {
	        BookCopy selected = availableBooksListView.getSelectionModel().getSelectedItem();
	        if (selected != null)
	        {
		    if (selectedBooksListView.getItems().size() >= 5)
		    {
			showAlert("Loan Limit", "Cannot loan more than 5 books at once.");
			return;
		    }
		    availableBooksListView.getItems().remove(selected);
		    selectedBooksListView.getItems().add(selected);
	        }
	  });

	  Button removeBookBtn = new Button("← Remove");
	  removeBookBtn.setOnAction(e ->
	  {
	        BookCopy selected = selectedBooksListView.getSelectionModel().getSelectedItem();
	        if (selected != null)
	        {
		    selectedBooksListView.getItems().remove(selected);
		    availableBooksListView.getItems().add(selected);
	        }
	  });
	  HBox buttonBox = new HBox(10, addBookBtn, removeBookBtn);
	  buttonBox.setAlignment(Pos.CENTER);

	  VBox content = new VBox(10, durationBox, new Separator(), new HBox(10, bookSearchField, searchBooksBtn),
		new Label("Available Books:"), availableBooksListView, buttonBox,
		new Label("Selected Books:"), selectedBooksListView);
	  content.setPadding(new Insets(20));
	  return content;
      }

      private void searchBooks(String query)
      {
	  if (query == null || query.trim().isEmpty())
	  {
	        refreshBooksList();
	        return;
	  }
	  booksList.clear();
	  booksList.addAll(bookManager.searchBooks(query));
      }

      private void deleteSelectedStudent()
      {
	  Student selected = studentListView.getSelectionModel().getSelectedItem();
	  if (selected == null)
	  {
	        showAlert("No Selection", "Please select a student to delete.");
	        return;
	  }

	  if (studentManager.hasActiveLoans(selected))
	  {
	        showAlert("Cannot Delete", "Cannot delete student with active loans.");
	        return;
	  }

	  Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION,
		"Are you sure you want to delete " + selected.getName() + "?",
		ButtonType.OK, ButtonType.CANCEL);
	  confirmAlert.setTitle("Confirm Delete");
	  confirmAlert.setHeaderText("Delete Student");

	  confirmAlert.showAndWait().ifPresent(response ->
	  {
	        if (response == ButtonType.OK)
	        {
		    try
		    {
			studentManager.deleteStudent(selected);
			refreshStudentsList();
			studentDetailsPanel.setVisible(false);
			showAlert("Success", "Student deleted successfully!");
		    } catch (Exception e)
		    {
			showAlert("Error", "Failed to delete student: " + e.getMessage());
		    }
	        }
	  });
      }

      private void updateSelectedStudent()
      {
	  Student selected = studentListView.getSelectionModel().getSelectedItem();
	  if (selected == null) return;

	  try
	  {
	        selected.setName(studentNameField.getText());
	        selected.setAddress(studentAddressField.getText());
	        selected.setDegree(studentDegreeField.getText());
	        studentManager.updateStudent(selected);
	        refreshStudentsList();
	        showAlert("Success", "Student updated successfully!");
	  } catch (Exception e)
	  {
	        showAlert("Error", "Failed to update student: " + e.getMessage());
	  }
      }

      private void updateSelectedBook()
      {
	  Book selected = bookListView.getSelectionModel().getSelectedItem();
	  if (selected == null) return;

	  try
	  {
	        selected.setTitle(bookTitleField.getText());
	        selected.setDescription(bookDescriptionField.getText());
	        selected.setAuthors(bookAuthorsField.getText());
	        selected.setNumPages(Integer.parseInt(bookPagesField.getText()));
	        selected.setPublisher(bookPublisherField.getText());
	        selected.setPublicationDate(LocalDate.parse(bookPublicationDateField.getText()));
	        bookManager.updateBook(selected);
	        refreshBooksList();
	        showAlert("Success", "Book updated successfully!");
	  } catch (NumberFormatException e)
	  {
	        showAlert("Invalid Input", "Please enter a valid number for pages.");
	  } catch (Exception e)
	  {
	        showAlert("Error", "Failed to update book: " + e.getMessage());
	  }
      }

      private void loadAvailableBooks(ListView<BookCopy> listView, String searchQuery)
      {
	  listView.getItems().clear();
	  List<Book> books = (searchQuery == null || searchQuery.trim().isEmpty())
		? bookManager.getAllBooks()
		: bookManager.searchBooks(searchQuery);

	  for (Book book : books)
	  {
	        for (BookCopy copy : book.getBookCopies())
	        {
		    if (copy.getIsAvailable())
		    {
			listView.getItems().add(copy);
		    }
	        }
	  }
      }

      private void returnSelectedLoan()
      {
	  Loan selected = studentLoansListView.getSelectionModel().getSelectedItem();
	  if (selected == null)
	  {
	        showAlert("No Selection", "Please select a loan to return.");
	        return;
	  }

	  if (selected.getReturnDate() != null)
	  {
	        showAlert("Already Returned", "This loan has already been returned.");
	        return;
	  }

	  try
	  {
	        loanManager.returnLoan(selected);
	        refreshStudentsList();
	        displayStudentDetails(studentListView.getSelectionModel().getSelectedItem());
	        showAlert("Success", "Loan returned successfully!");
	  } catch (Exception e)
	  {
	        showAlert("Error", "Failed to return loan: " + e.getMessage());
	  }
      }

      private void showLoanReceipt()
      {
	  Loan selected = studentLoansListView.getSelectionModel().getSelectedItem();
	  if (selected == null)
	  {
	        showAlert("No Selection", "Please select a loan to view receipt.");
	        return;
	  }
	  String receipt = loanManager.generateLoanReceipt(selected);
	  Alert alert = new Alert(Alert.AlertType.INFORMATION);
	  alert.setTitle("Loan Receipt");
	  alert.setHeaderText(null);
	  alert.setContentText(receipt);
	  alert.getDialogPane().setPrefWidth(500);
	  alert.showAndWait();
      }

      private void showAddBookCopyDialog()
      {
	  Book selectedBook = bookListView.getSelectionModel().getSelectedItem();
	  if (selectedBook == null)
	  {
	        showAlert("No Selection", "Please select a book first.");
	        return;
	  }

	  var dialog = new Dialog<BookCopy>();
	  dialog.setTitle("Add Book Copy");
	  dialog.setHeaderText("Add a copy for: " + selectedBook.getTitle());

	  var addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
	  dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

	  GridPane grid = createFormGrid();
	  grid.setPadding(new Insets(20, 150, 10, 10));
	  TextField barcode = new TextField();
	  barcode.setPromptText("Barcode");
	  TextField location = new TextField();
	  location.setPromptText("Location");
	  addFormField(grid, "Barcode:", barcode, 0);
	  addFormField(grid, "Location:", location, 1);
	  dialog.getDialogPane().setContent(grid);

	  dialog.setResultConverter(db -> (db == addButtonType) ? new BookCopy(barcode.getText(), location.getText(), selectedBook) : null);

	  dialog.showAndWait().ifPresent(newCopy ->
	  {
	        try
	        {
		    BookCopy createdCopy = bookManager.createBookCopy(selectedBook, newCopy.getBarCode(), newCopy.getLocation());
		    if (createdCopy != null)
		    {
			displayBookDetails(selectedBook);
			showAlert("Success", "Book copy added successfully!");
		    } else
		    {
			showAlert("Duplicate Barcode", "A copy with this barcode already exists.");
		    }
	        } catch (Exception e)
	        {
		    showAlert("Error", "Failed to add book copy: " + e.getMessage());
	        }
	  });
      }

      private void deleteSelectedBookCopy()
      {
	  BookCopy selectedCopy = bookCopiesListView.getSelectionModel().getSelectedItem();
	  if (selectedCopy == null)
	  {
	        showAlert("No Selection", "Please select a book copy to delete.");
	        return;
	  }

	  Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION,
		"Are you sure you want to delete copy " + selectedCopy.getBarCode() + "?",
		ButtonType.OK, ButtonType.CANCEL);
	  confirmAlert.setTitle("Confirm Delete");
	  confirmAlert.setHeaderText("Delete Book Copy");

	  confirmAlert.showAndWait().ifPresent(response ->
	  {
	        if (response == ButtonType.OK)
	        {
		    try
		    {
			bookManager.deleteBookCopy(selectedCopy);
			displayBookDetails(bookListView.getSelectionModel().getSelectedItem());
			showAlert("Success", "Book copy deleted successfully!");
		    } catch (IllegalStateException e)
		    {
			showAlert("Cannot Delete", e.getMessage());
		    } catch (Exception e)
		    {
			showAlert("Error", "Failed to delete book copy: " + e.getMessage());
		    }
	        }
	  });
      }

      private void showAlert(String title, String message)
      {
	  Alert alert = new Alert(Alert.AlertType.INFORMATION);
	  alert.setTitle(title);
	  alert.setHeaderText(null);
	  alert.setContentText(message);
	  alert.showAndWait();
      }
}