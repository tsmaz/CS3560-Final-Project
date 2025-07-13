package org.tonym.cs3560;

import jakarta.persistence.*;

@Entity
@Table(name = "bookCopies")
public class BookCopy
{

        @Id
        @Column(name = "barcode", nullable = false, unique = true)
        private String barCode;

        @Column(name = "location")
        private String location;

        @Column(name = "isAvailable", nullable = false)
        private Boolean isAvailable;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "isbn", nullable = false)
        private Book book;

        public BookCopy()
        {
        }

        public BookCopy(String barCode, String location, Book book)
        {
	      this.barCode = barCode;
	      this.location = location;
	      this.book = book;
	      this.isAvailable = true;
        }

        // Getters and Setters
        public String getBarCode()
        {
	      return barCode;
        }

        public void setBarCode(String barCode)
        {
	      this.barCode = barCode;
        }

        public String getLocation()
        {
	      return location;
        }

        public void setLocation(String location)
        {
	      this.location = location;
        }

        public Boolean getIsAvailable()
        {
	      return isAvailable;
        }

        public void setIsAvailable(Boolean isAvailable)
        {
	      this.isAvailable = isAvailable;
        }

        public Book getBook()
        {
	      return book;
        }

        public void setBook(Book book)
        {
	      this.book = book;
        }
}
