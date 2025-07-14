package org.tonym.cs3560;

import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

        public class LoanManager
        {
	      private static final DateTimeFormatter AMERICAN_DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");

	      private static final int MAX_LOAN_DAYS = 180;
	      private static final int MAX_BORROWED_BOOKS = 5;

	      public Loan createLoan(Student student, List<BookCopy> bookCopies, int loanDurationInDays)
	      {
		  Session session = HibernateHelper.getSessionFactory().openSession();
		  Transaction transaction = session.beginTransaction();
		  
		  LocalDate borrowDate = LocalDate.now();
		  LocalDate dueDate = borrowDate.plusDays(loanDurationInDays);
		  
		  Loan loan = new Loan(student, borrowDate, dueDate);
		  loan.setLoanedCopies(bookCopies);
		  
		  for (BookCopy copy : bookCopies) {
			  copy.setIsAvailable(false);
			  session.merge(copy);
		  }
		  
		  session.persist(loan);
		  student.getLoans().add(loan);
		  session.merge(student);
		  
		  transaction.commit();
		  session.close();

		  return loan;
	      }

	      public void returnLoan(Loan loan)
	      {
		  Session session = HibernateHelper.getSessionFactory().openSession();
		  Transaction transaction = session.beginTransaction();
		  
		  loan.setReturnDate(LocalDate.now());
		  
		  for (BookCopy copy : loan.getLoanedCopies()) {
			  copy.setIsAvailable(true);
			  session.merge(copy);
		  }
		  
		  session.merge(loan);
		  
		  transaction.commit();
		  session.close();
	      }

	      private boolean hasOverdueItems(Student student)
	      {
		  for (Loan loan : student.getLoans()) {
			  if (loan.isOverdue()) {
				  return true;
			  }
		  }
		  return false;
	      }

	      private boolean canBorrowMoreBooks(Student student, int newBooksCount)
	      {
		  int currentBooksCount = 0;
		  
		  for (Loan loan : student.getLoans()) {
			  if (loan.getReturnDate() == null) {
				  currentBooksCount += loan.getLoanedCopies().size();
			  }
		  }
		  
		  return (currentBooksCount + newBooksCount) <= MAX_BORROWED_BOOKS;
	      }

	      public Loan getActiveLoanByBookCopy(BookCopy bookCopy)
	      {
		  Session session = HibernateHelper.getSessionFactory().openSession();
		  
		  String hql = "SELECT l FROM Loan l JOIN l.loanedCopies c WHERE c = :bookCopy AND l.returnDate IS NULL";
		  List<Loan> activeLoans = session.createQuery(hql, Loan.class)
				  .setParameter("bookCopy", bookCopy)
				  .getResultList();
		  
		  session.close();
		  
		  return activeLoans.isEmpty() ? null : activeLoans.get(0);
	      }
	      
	      public List<Loan> getLoanHistoryForBookCopy(BookCopy bookCopy)
	      {
		  Session session = HibernateHelper.getSessionFactory().openSession();
		  
		  String hql = "SELECT l FROM Loan l JOIN l.loanedCopies c WHERE c = :bookCopy ORDER BY l.borrowDate DESC";
		  List<Loan> loanHistory = session.createQuery(hql, Loan.class)
				  .setParameter("bookCopy", bookCopy)
				  .getResultList();
		  
		  session.close();
		  
		  return loanHistory;
	      }
	      
	      public List<Loan> getActiveLoans()
	      {
		  Session session = HibernateHelper.getSessionFactory().openSession();
		  
		  String hql = "FROM Loan l WHERE l.returnDate IS NULL ORDER BY l.borrowDate DESC";
		  List<Loan> activeLoans = session.createQuery(hql, Loan.class).getResultList();
		  
		  session.close();
		  
		  return activeLoans;
	      }

	      public String generateLoanReceipt(Loan loan)
	      {
		  StringBuilder receipt = new StringBuilder();
		  
		  receipt.append("--- LOAN RECEIPT ---\n");
		  receipt.append("Loan Number: ").append(loan.getLoanNumber()).append("\n");
		  receipt.append("Student: ").append(loan.getStudent().getName()).append(" (ID: ").append(loan.getStudent().getBroncoId()).append(")\n");
		  receipt.append("Borrow Date: ").append(loan.getBorrowDate().format(AMERICAN_DATE_FORMATTER)).append("\n");
		  receipt.append("Due Date: ").append(loan.getDueDate().format(AMERICAN_DATE_FORMATTER)).append("\n");
		  if (loan.getReturnDate() != null) {
			  receipt.append("Return Date: ").append(loan.getReturnDate().format(AMERICAN_DATE_FORMATTER)).append("\n");
		  }
		  receipt.append("\nBorrowed Books:\n");
		  
		  for (BookCopy copy : loan.getLoanedCopies()) {
			  receipt.append("- ").append(copy.getBook().getTitle()).append(" (Barcode: ").append(copy.getBarCode()).append(")\n");
		  }
		  
		  receipt.append("===================");
		  
		  return receipt.toString();
	      }
        }
