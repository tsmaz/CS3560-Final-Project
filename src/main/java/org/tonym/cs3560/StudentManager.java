package org.tonym.cs3560;

import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class StudentManager
{


        public void createStudent(int broncoId, String name, String address, String degree)
        {
                Session session = HibernateHelper.getSessionFactory().openSession();
                Transaction transaction = session.beginTransaction();
                
                Student student = new Student(broncoId, name, address, degree);
                session.persist(student);
                
                transaction.commit();
                session.close();

        }

        public void updateStudent(Student student)
        {
                Session session = HibernateHelper.getSessionFactory().openSession();
                Transaction transaction = session.beginTransaction();
                
                session.merge(student);
                
                transaction.commit();
                session.close();
        }

        public Student findStudentById(int broncoId)
        {
                Session session = HibernateHelper.getSessionFactory().openSession();
                
                Student student = session.get(Student.class, broncoId);
                
                session.close();
                return student;
        }

        public List<Student> getAllStudents()
        {
                Session session = HibernateHelper.getSessionFactory().openSession();
                
                List<Student> students = session.createQuery("FROM Student", Student.class).getResultList();
                
                session.close();
                return students;
        }

        public boolean hasActiveLoans(Student student)
        {
                return !student.getLoans().isEmpty();
        }

        public void deleteStudent(Student student)
        {
                Session session = HibernateHelper.getSessionFactory().openSession();
                Transaction transaction = session.beginTransaction();
                
                session.remove(student);
                
                transaction.commit();
                session.close();
        }
}