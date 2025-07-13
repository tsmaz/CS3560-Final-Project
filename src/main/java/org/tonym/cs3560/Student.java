package org.tonym.cs3560;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "students")
public class Student
{

        @Id
        @Column(name = "bronco_id", nullable = false, unique = true)
        private String broncoId;

        @Column(name = "name", nullable = false)
        private String name;

        @Column(name = "address")
        private String address;

        @Column(name = "degree")
        private String degree;

        @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
        private List<Loan> loans = new ArrayList<>();


        public Student()
        {
        }

        public Student(String broncoId, String name, String address, String degree)
        {
	      this.broncoId = broncoId;
	      this.name = name;
	      this.address = address;
	      this.degree = degree;
        }

        // Getters and Setters
        public String getBroncoId()
        {
	      return broncoId;
        }

        public void setBroncoId(String broncoId)
        {
	      this.broncoId = broncoId;
        }

        public String getName()
        {
	      return name;
        }

        public void setName(String name)
        {
	      this.name = name;
        }

        public String getAddress()
        {
	      return address;
        }

        public void setAddress(String address)
        {
	      this.address = address;
        }

        public String getDegree()
        {
	      return degree;
        }

        public void setDegree(String degree)
        {
	      this.degree = degree;
        }

        public List<Loan> getLoans()
        {
	      return loans;
        }

        public void setLoans(List<Loan> loans)
        {
	      this.loans = loans;
        }
}
