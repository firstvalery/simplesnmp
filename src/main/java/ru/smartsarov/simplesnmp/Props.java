package ru.smartsarov.simplesnmp;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public final class Props {
  private Props() {
    //this.getClass().getResourceAsStream("db.properties");
  }

  public static Properties get() {
    Properties props = new Properties();

    try (FileInputStream f = new FileInputStream("c:/conf/simplesnmp/simplesnmp.conf")) {
      props.load(f);
    } catch (IOException ex) {
      ex.printStackTrace(); // handle an exception here
    }

    return props;
  }
}