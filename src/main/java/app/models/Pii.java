package app.models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "pii")
public class Pii {

  @Id
  public Long id;

  public String 編碼日期;

  public String guid;

  public String mrn;

  public String 身份證字號;

  public String 姓氏;

  public String 名字;

  public String 出生月;

  public String 出生日;

  public String 出生年;

  public String 性別;

  public String 聯絡電話;

  public String 地址;

  public String 收案醫師;

  public String 收案醫院名稱;

}