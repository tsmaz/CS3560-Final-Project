package org.tonym.cs3560;

import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.time.LocalDate;

public class BookManager
{

        public Book createBook(String isbn, String title, String description, String authors, int numPages, String publisher, LocalDate publicationDate)
        {
                Session session = HibernateHelper.getSessionFactory().openSession();
                Transaction transaction = session.beginTransaction();

                Book existingBook = session.get(Book.class, isbn);
                if (existingBook != null)
                {
                        session.close();
                        return null;
                }

                Book book = new Book(isbn, title, description, authors, numPages, publisher, publicationDate);
                session.persist(book);

                transaction.commit();
                session.close();

                return book;
        }

        public void updateBook(Book book)
        {
                Session session = HibernateHelper.getSessionFactory().openSession();
                Transaction transaction = session.beginTransaction();

                session.merge(book);

                transaction.commit();
                session.close();
        }

        public BookCopy createBookCopy(Book book, String barCode, String location)
        {
                Session session = HibernateHelper.getSessionFactory().openSession();
                Transaction transaction = session.beginTransaction();

                BookCopy existingCopy = session.get(BookCopy.class, barCode);
                if (existingCopy != null)
                {
                        session.close();
                        return null;
                }

                BookCopy bookCopy = new BookCopy(barCode, location, book);
                session.persist(bookCopy);

                transaction.commit();
                session.close();

                return bookCopy;
        }

        public List<Book> searchBooks(String query)
        {
                Session session = HibernateHelper.getSessionFactory().openSession();

                String hql = "FROM Book b WHERE LOWER(b.title) LIKE :query OR LOWER(b.authors) LIKE :query ORDER BY b.title";
                List<Book> books = session.createQuery(hql, Book.class)
                        .setParameter("query", "%" + query.toLowerCase() + "%")
                        .setMaxResults(5)
                        .getResultList();

                session.close();
                return books;
        }

        public void deleteBookCopy(BookCopy bookCopy) throws IllegalStateException
        {
                if (!bookCopy.getIsAvailable())
                {
                        throw new IllegalStateException("Cannot delete book copy that is currently loaned out");
                }

                Session session = HibernateHelper.getSessionFactory().openSession();
                Transaction transaction = session.beginTransaction();

                session.remove(bookCopy);

                transaction.commit();
                session.close();
        }

        public List<Book> getAllBooks()
        {
                Session session = HibernateHelper.getSessionFactory().openSession();

                List<Book> books = session.createQuery("FROM Book", Book.class).getResultList();

                session.close();
                return books;
        }
        
        public List<BookCopy> getAllBookCopies()
        {
                Session session = HibernateHelper.getSessionFactory().openSession();

                List<BookCopy> bookCopies = session.createQuery("FROM BookCopy ORDER BY barCode", BookCopy.class).getResultList();

                session.close();
                return bookCopies;
        }
        
        public BookCopy findBookCopyByBarcode(String barcode)
        {
                Session session = HibernateHelper.getSessionFactory().openSession();

                BookCopy bookCopy = session.get(BookCopy.class, barcode);

                session.close();
                return bookCopy;
        }
        
        public void updateBookCopy(BookCopy bookCopy)
        {
                Session session = HibernateHelper.getSessionFactory().openSession();
                Transaction transaction = session.beginTransaction();

                session.merge(bookCopy);

                transaction.commit();
                session.close();
        }

}