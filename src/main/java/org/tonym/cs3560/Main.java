package org.tonym.cs3560;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application
{
        private final StudentManager studentManager = new StudentManager();

        public static void main(String[] args)
        {
	      HibernateHelper.getSessionFactory();
	      launch(args);
        }

        @Override
        public void start(Stage primaryStage)
        {

        }

        @Override
        public void stop()
        {
	      HibernateHelper.shutdown();
        }
}