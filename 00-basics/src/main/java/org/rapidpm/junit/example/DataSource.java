package org.rapidpm.junit.example;

import org.rapidpm.frp.model.Quad;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toConcurrentMap;

public class DataSource {

  public static class User
      extends Quad<Long, String, String, String> {

    public User(Long id, String login, String passwd, String name) {
      super(id, login, passwd, name);
    }

    public Long id() {
      return getT1();
    }

    public String login() {
      return getT2();
    }

    public String passwd() {
      return getT3();
    }

    public String name() {
      return getT4();
    }

  }

  private AtomicLong idGenerator = new AtomicLong(0);

  private Map<Long, User> persistenceStore = Stream.of(new User(idGenerator.incrementAndGet(), "admin", "admin", "Mr Admin"),
                                                       new User(idGenerator.incrementAndGet(), "user", "user", "Mr User"))
                                                   .collect(toConcurrentMap(User::id, u -> u));

  public User load(Long id){
    return persistenceStore.get(id);
  }

  public void addUser(String login, String passwd, String name){
    final User u = new User(idGenerator.incrementAndGet(), login, passwd, name);
    persistenceStore.put(u.id(), u);
  }

  public void deleteUser(User u){
    persistenceStore.remove(u.id());
  }

  public Stream<User> queryForLogin(String login, String password){
    return persistenceStore.values()
                    .stream()
                    .filter(u -> u.login().equals(login))
                    .filter(u -> u.passwd().equals(password));
  }

}
