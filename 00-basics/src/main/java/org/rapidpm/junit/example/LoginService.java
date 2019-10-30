package org.rapidpm.junit.example;

public class LoginService {

  private DataSource dataSource;

  public LoginService(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  public boolean checkLogin(String login, String passwd){
    return dataSource.queryForLogin(login, passwd).findFirst().isPresent();
  }

}
