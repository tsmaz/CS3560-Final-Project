package org.tonym.cs3560;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "loans")
public class Loan
{

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "loanNumber")
        private Long loanNumber;

        @Column(name = "borrowDate", nullable = false)
        private LocalDate borrowDate;

        @Column(name = "dueDate", nullable = false)
        private LocalDate dueDate;

        @Column(name = "returnDate")
        private LocalDate returnDate;

        @ManyToOne(fetch = FetchType.EAGER)
        @JoinColumn(name = "studentId", nullable = false)
        private Student student;

        @ManyToMany(fetch = FetchType.EAGER)
        @JoinTable(
	      name = "loan_book_copy",
	      joinColumns = @JoinColumn(name = "loan_number"),
	      inverseJoinColumns = @JoinColumn(name = "book_copy_barcode")
        )
        private List<BookCopy> loanedCopies = new ArrayList<>();

        public Loan()
        {
        }

        public Loan(Student student, LocalDate borrowDate, LocalDate dueDate)
        {
	      this.student = student;
	      this.borrowDate = borrowDate;
	      this.dueDate = dueDate;
        }

        public boolean isOverdue()
        {
                return returnDate == null && dueDate.isBefore(LocalDate.now());
        }

        // Getters and Setters
        public Long getLoanNumber()
        {
	      return loanNumber;
        }

        public void setLoanNumber(Long loanNumber)
        {
	      this.loanNumber = loanNumber;
        }

        public LocalDate getBorrowDate()
        {
	      return borrowDate;
        }

        public void setBorrowDate(LocalDate borrowDate)
        {
	      this.borrowDate = borrowDate;
        }

        public LocalDate getDueDate()
        {
	      return dueDate;
        }

        public void setDueDate(LocalDate dueDate)
        {
	      this.dueDate = dueDate;
        }

        public LocalDate getReturnDate()
        {
	      return returnDate;
        }

        public void setReturnDate(LocalDate returnDate)
        {
	      this.returnDate = returnDate;
        }

        public Student getStudent()
        {
	      return student;
        }

        public void setStudent(Student student)
        {
	      this.student = student;
        }

        public List<BookCopy> getLoanedCopies()
        {
	      return loanedCopies;
        }

        public void setLoanedCopies(List<BookCopy> loanedCopies)
        {
	      this.loanedCopies = loanedCopies;
        }

        @Override
        public String toString()
        {
                String status = (returnDate != null) ? "Returned" : (isOverdue() ? "Overdue" : "Active");
                return "Loan #" + loanNumber + " - " + loanedCopies.size() + " books (" + status + ")";
        }
}