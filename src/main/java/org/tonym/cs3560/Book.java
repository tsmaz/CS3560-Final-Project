package org.tonym.cs3560;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "books")
public class Book
{

        @Id
        @Column(name = "isbn", nullable = false, unique = true)
        private String isbn;

        @Column(name = "title", nullable = false)
        private String title;

        @Column(name = "description", length = 2000)
        private String description;

        @Column(name = "authors", nullable = false)
        private String authors;

        @Column(name = "numPages")
        private int numPages;

        @Column(name = "publisher")
        private String publisher;

        @Column(name = "publicationDate")
        private LocalDate publicationDate;

        @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
        private List<BookCopy> bookCopies = new ArrayList<>();

        public Book()
        {
        }

        public Book(String isbn, String title, String description, String authors, int numPages, String publisher, LocalDate publicationDate)
        {
	      this.isbn = isbn;
	      this.title = title;
	      this.description = description;
	      this.authors = authors;
	      this.numPages = numPages;
	      this.publisher = publisher;
	      this.publicationDate = publicationDate;
        }

        // Getters and Setters
        public String getIsbn()
        {
	      return isbn;
        }

        public void setIsbn(String isbn)
        {
	      this.isbn = isbn;
        }

        public String getTitle()
        {
	      return title;
        }

        public void setTitle(String title)
        {
	      this.title = title;
        }

        public String getDescription()
        {
	      return description;
        }

        public void setDescription(String description)
        {
	      this.description = description;
        }

        public String getAuthors()
        {
	      return authors;
        }

        public void setAuthors(String authors)
        {
	      this.authors = authors;
        }

        public int getNumPages()
        {
	      return numPages;
        }

        public void setNumPages(int numPages)
        {
	      this.numPages = numPages;
        }

        public String getPublisher()
        {
	      return publisher;
        }

        public void setPublisher(String publisher)
        {
	      this.publisher = publisher;
        }

        public LocalDate getPublicationDate()
        {
	      return publicationDate;
        }

        public void setPublicationDate(LocalDate publicationDate)
        {
	      this.publicationDate = publicationDate;
        }

        public List<BookCopy> getBookCopies()
        {
	      return bookCopies;
        }

        public void setBookCopies(List<BookCopy> bookCopies)
        {
	      this.bookCopies = bookCopies;
        }
}