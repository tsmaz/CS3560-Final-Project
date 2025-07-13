package org.tonym.cs3560;

import java.util.List;

public class LoanManager
{
        public class LoanController
        {

	      private static final int MAX_LOAN_DAYS = 180;
	      private static final int MAX_BORROWED_BOOKS = 5;

	      public Loan createLoan(Student student, List<BookCopy> bookCopies, int loanDurationInDays) throws IllegalStateException
	      {

	      }

	      public Loan returnLoan(Loan loan)
	      {

	      }

	      private boolean hasOverdueItems(Student student)
	      {

	      }

	      private boolean canBorrowMoreBooks(Student student, int newBooksCount)
	      {

	      }

	      public String generateLoanReceipt(Loan loan)
	      {

	      }
        }
}
