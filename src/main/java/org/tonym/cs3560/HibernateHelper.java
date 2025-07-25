package org.tonym.cs3560;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateHelper
{

        private static final SessionFactory sessionFactory = buildSessionFactory();

        private static SessionFactory buildSessionFactory()
        {
	      try
	      {
		    return new Configuration().configure().buildSessionFactory();
	      }
	      catch (Throwable ex)
	      {
		    System.err.println("Failed to create SessionFactory." + ex);
		    throw new ExceptionInInitializerError(ex);
	      }
        }

        public static SessionFactory getSessionFactory()
        {
	      return sessionFactory;
        }

        public static void shutdown()
        {
	      getSessionFactory().close();
        }
}
