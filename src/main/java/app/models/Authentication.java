package app.models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "authentication")
public class Authentication {

  @Id
  public Long id;

  public String role;

  public String password;

}