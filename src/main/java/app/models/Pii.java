package app.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "pii")
public class Pii {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String 編碼日期;

  private String guid;

  private String mrn;

  private String 身份證字號;

  private String 姓氏;

  private String 名字;

  private String 出生月;

  private String 出生日;

  private String 出生年;

  private String 性別;

  private String 聯絡電話;

  private String 地址;

  private String 收案醫師;

  private String 收案醫院名稱;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String get編碼日期() {
    return 編碼日期;
  }

  public void set編碼日期(String 編碼日期) {
    this.編碼日期 = 編碼日期;
  }

  public String getGuid() {
    return guid;
  }

  public void setGuid(String guid) {
    this.guid = guid;
  }

  public String getMrn() {
    return mrn;
  }

  public void setMrn(String mrn) {
    this.mrn = mrn;
  }

  public String get身份證字號() {
    return 身份證字號;
  }

  public void set身份證字號(String 身份證字號) {
    this.身份證字號 = 身份證字號;
  }

  public String get姓氏() {
    return 姓氏;
  }

  public void set姓氏(String 姓氏) {
    this.姓氏 = 姓氏;
  }

  public String get名字() {
    return 名字;
  }

  public void set名字(String 名字) {
    this.名字 = 名字;
  }

  public String get出生月() {
    return 出生月;
  }

  public void set出生月(String 出生月) {
    this.出生月 = 出生月;
  }

  public String get出生日() {
    return 出生日;
  }

  public void set出生日(String 出生日) {
    this.出生日 = 出生日;
  }

  public String get出生年() {
    return 出生年;
  }

  public void set出生年(String 出生年) {
    this.出生年 = 出生年;
  }

  public String get性別() {
    return 性別;
  }

  public void set性別(String 性別) {
    this.性別 = 性別;
  }

  public String get聯絡電話() {
    return 聯絡電話;
  }

  public void set聯絡電話(String 聯絡電話) {
    this.聯絡電話 = 聯絡電話;
  }

  public String get地址() {
    return 地址;
  }

  public void set地址(String 地址) {
    this.地址 = 地址;
  }

  public String get收案醫師() {
    return 收案醫師;
  }

  public void set收案醫師(String 收案醫師) {
    this.收案醫師 = 收案醫師;
  }

  public String get收案醫院名稱() {
    return 收案醫院名稱;
  }

  public void set收案醫院名稱(String 收案醫院名稱) {
    this.收案醫院名稱 = 收案醫院名稱;
  }

}